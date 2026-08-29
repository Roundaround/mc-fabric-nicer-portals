package me.roundaround.nicerportals.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.roundaround.nicerportals.config.NicerPortalsPerWorldConfig;
import me.roundaround.nicerportals.tags.BlockTags;
import me.roundaround.nicerportals.util.NetherPortalExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;
import me.roundaround.nicerportals.util.HashSetQueue;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.List;

@Mixin(PortalShape.class)
public abstract class PortalShapeMixin implements NetherPortalExtensions {
  @Unique
  private boolean valid = false;
  @Unique
  HashSet<BlockPos> validPortalPositions = new HashSet<>();
  @Unique
  int portalBlockCount = 0;

  // True only once the flood fill below has actually run, which takes both a logical server and
  // anyShape. Every hook that has no level to test keys off this instead of re-reading the config,
  // so on the logical client they all fall through to vanilla and the per-world config is never
  // touched there at all. The client has no world directory to load one from, so its values would
  // be a guess at the server's; vanilla's own rules are the honest fallback. See GH-16.
  @Unique
  private boolean scanned = false;

  @Final
  @Shadow
  private Direction rightDir;

  @Final
  @Shadow
  private BlockPos bottomLeft;

  // PortalShape is handed a bare BlockGetter, so the logical side has to come off the level it was
  // given. Nothing that isn't a LevelReader reaches these paths in vanilla; treat that case as the
  // server so an unexpected caller keeps the mod's rules rather than silently losing them.
  @Unique
  private static boolean nicerportals$isLogicalClient(BlockGetter world) {
    return world instanceof LevelReader reader && reader.isClientSide();
  }

  @Inject(
      method = "lambda$static$0", at = @At(value = "HEAD"), cancellable = true
  )
  private static void isValidFrameBlock(
      BlockState state,
      BlockGetter world,
      BlockPos pos,
      CallbackInfoReturnable<Boolean> info
  ) {
    if (nicerportals$isLogicalClient(world) ||
        !NicerPortalsPerWorldConfig.getInstance().portalFrameTag.getValue()) {
      return;
    }
    info.setReturnValue(state.is(BlockTags.PORTAL_FRAME));
  }

  @ModifyReturnValue(
      method = "findAnyShape", at = @At("RETURN")
  )
  private static PortalShape checkPortalValidity(PortalShape portal, @Local(argsOnly = true) BlockGetter world) {
    portal.nicerportals$checkAreaForPortalValidity(world);
    return portal;
  }

  @Inject(method = "isValid", at = @At(value = "HEAD"), cancellable = true)
  private void isValid(CallbackInfoReturnable<Boolean> info) {
    if (!this.scanned) {
      return;
    }
    info.setReturnValue(this.valid);
  }

  @Inject(
      method = "createPortalBlocks", at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/core/BlockPos;betweenClosed(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Ljava/lang/Iterable;"
  ), cancellable = true
  )
  private void createPortal(LevelAccessor world, CallbackInfo ci, @Local BlockState portalState) {
    if (!this.scanned) {
      return;
    }

    this.getValidPortalPositions().forEach((pos) -> world.setBlock(
        pos,
        portalState,
        Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE
    ));

    ci.cancel();
  }

  @Inject(method = "isComplete", at = @At(value = "HEAD"), cancellable = true)
  private void wasAlreadyValid(CallbackInfoReturnable<Boolean> info) {
    if (!this.scanned) {
      return;
    }

    info.setReturnValue(this.valid && this.getValidPortalPositions().size() == this.portalBlockCount);
  }

  @Override
  public void nicerportals$checkAreaForPortalValidity(BlockGetter world) {
    this.scanned = false;

    if (nicerportals$isLogicalClient(world) || !NicerPortalsPerWorldConfig.getInstance().anyShape.getValue()) {
      return;
    }

    this.scanned = true;
    this.getValidPortalPositions().clear();

    HashSet<BlockPos> validFrameBlocks = new HashSet<>();
    HashSetQueue<BlockPos> positionsToCheck = new HashSetQueue<>();
    boolean minSizeFound = false;

    List<Direction> directions = List.of(
        Direction.DOWN,
        Direction.UP,
        this.rightDir,
        this.rightDir.getOpposite()
    );

    positionsToCheck.push(this.bottomLeft);

    while (!positionsToCheck.isEmpty()) {
      BlockPos pos = positionsToCheck.pop();
      if (this.getValidPortalPositions().contains(pos) || validFrameBlocks.contains(pos)) {
        continue;
      }

      boolean isOrCanBePortal = this.isValidPosForPortalBlock(world, pos);
      boolean isFrameBlock = this.isValidFrameBlock(world, pos);

      if (!isOrCanBePortal && !isFrameBlock) {
        this.valid = false;
        return;
      }

      if (isOrCanBePortal) {
        this.getValidPortalPositions().add(pos);
        if (this.getValidPortalPositions().size() > NicerPortalsPerWorldConfig.getInstance().maxSize.getValue()) {
          this.valid = false;
          return;
        }

        if (world.getBlockState(pos).is(Blocks.NETHER_PORTAL)) {
          this.portalBlockCount++;
        }

        if (!minSizeFound &&
            (this.getValidPortalPositions().contains(pos.above()) || this.getValidPortalPositions().contains(pos.below()))) {
          minSizeFound = true;
        }

        directions.forEach((direction) -> {
          BlockPos neighborPos = pos.relative(direction);
          if (!this.getValidPortalPositions().contains(neighborPos) && !validFrameBlocks.contains(neighborPos)) {
            positionsToCheck.push(neighborPos);
          }
        });
      } else {
        validFrameBlocks.add(pos);
      }
    }

    this.valid = minSizeFound || !NicerPortalsPerWorldConfig.getInstance().enforceMinimum.getValue();
  }

  @Unique
  private HashSet<BlockPos> getValidPortalPositions() {
    if (this.validPortalPositions == null) {
      this.validPortalPositions = new HashSet<>();
    }
    return this.validPortalPositions;
  }

  @Unique
  private boolean isValidPosForPortalBlock(BlockGetter world, BlockPos pos) {
    return PortalShapeAccessor.isValidStateInsidePortal(world.getBlockState(pos)) && !world.isOutsideBuildHeight(pos);
  }

  @Unique
  private boolean isValidFrameBlock(BlockGetter world, BlockPos pos) {
    return PortalShapeAccessor.getIsValidFrameBlock().test(world.getBlockState(pos), world, pos) &&
           !world.isOutsideBuildHeight(pos);
  }
}
