package me.roundaround.nicerportals.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.roundaround.nicerportals.config.NicerPortalsPerWorldConfig;
import me.roundaround.nicerportals.util.ExpandedNetherPortal;
import me.roundaround.nicerportals.util.HashSetQueue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.NetherPortal;
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

@Mixin(NetherPortal.class)
public abstract class NetherPortalMixin implements ExpandedNetherPortal {
  @Unique
  private boolean valid = false;
  @Unique
  HashSet<BlockPos> validPortalPositions = new HashSet<>();
  @Unique
  int portalBlockCount = 0;

  @Final
  @Shadow
  private Direction negativeDir;

  @Final
  @Shadow
  private BlockPos lowerCorner;

  @Final
  @Shadow
  private Direction.Axis axis;

  @Inject(
      method = "method_30487(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;" +
               "Lnet/minecraft/util/math/BlockPos;)Z", at = @At(value = "HEAD"), cancellable = true
  )
  private static void isValidFrameBlock(
      BlockState state, BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> info
  ) {
    if (!NicerPortalsPerWorldConfig.getInstance().cryingObsidian.getValue()) {
      return;
    }

    if (state.isOf(Blocks.CRYING_OBSIDIAN)) {
      info.setReturnValue(true);
    }
  }

  @ModifyReturnValue(
      method = "getOnAxis", at = @At("RETURN")
  )
  private static NetherPortal checkPortalValidity(NetherPortal original, @Local(argsOnly = true) BlockView world) {
    original.checkAreaForPortalValidity(world);
    return original;
  }

  @Inject(method = "isValid", at = @At(value = "HEAD"), cancellable = true)
  private void isValid(CallbackInfoReturnable<Boolean> info) {
    if (!NicerPortalsPerWorldConfig.getInstance().anyShape.getValue()) {
      return;
    }

    info.setReturnValue(this.valid);
  }

  @Inject(
      method = "createPortal", at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/util/math/BlockPos;iterate(Lnet/minecraft/util/math/BlockPos;" +
               "Lnet/minecraft/util/math/BlockPos;)Ljava/lang/Iterable;"
  ), cancellable = true
  )
  private void createPortal(WorldAccess world, CallbackInfo ci, @Local BlockState blockState) {
    if (!NicerPortalsPerWorldConfig.getInstance().anyShape.getValue()) {
      return;
    }

    this.validPortalPositions.forEach(
        (pos) -> world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS | Block.FORCE_STATE));

    ci.cancel();
  }

  @Inject(method = "wasAlreadyValid", at = @At(value = "HEAD"), cancellable = true)
  private void wasAlreadyValid(CallbackInfoReturnable<Boolean> info) {
    if (!NicerPortalsPerWorldConfig.getInstance().anyShape.getValue()) {
      return;
    }

    info.setReturnValue(this.valid && this.validPortalPositions.size() == this.portalBlockCount);
  }

  @Override
  public void checkAreaForPortalValidity(BlockView world) {
    if (!NicerPortalsPerWorldConfig.getInstance().anyShape.getValue()) {
      return;
    }

    this.validPortalPositions.clear();

    HashSet<BlockPos> validFrameBlocks = new HashSet<>();
    HashSetQueue<BlockPos> positionsToCheck = new HashSetQueue<>();
    boolean minSizeFound = false;

    List<Direction> directions = List.of(
        Direction.DOWN, Direction.UP, this.negativeDir, this.negativeDir.getOpposite());

    positionsToCheck.push(this.lowerCorner);

    while (!positionsToCheck.isEmpty()) {
      BlockPos pos = positionsToCheck.pop();
      if (this.validPortalPositions.contains(pos) || validFrameBlocks.contains(pos)) {
        continue;
      }

      boolean isOrCanBePortal = this.isValidPosForPortalBlock(world, pos);
      boolean isFrameBlock = this.isValidFrameBlock(world, pos);

      if (!isOrCanBePortal && !isFrameBlock) {
        this.valid = false;
        return;
      }

      if (isOrCanBePortal) {
        this.validPortalPositions.add(pos);
        if (this.validPortalPositions.size() > NicerPortalsPerWorldConfig.getInstance().maxSize.getValue()) {
          this.valid = false;
          return;
        }

        if (world.getBlockState(pos).isOf(Blocks.NETHER_PORTAL)) {
          this.portalBlockCount++;
        }

        if (!minSizeFound &&
            (this.validPortalPositions.contains(pos.up()) || this.validPortalPositions.contains(pos.down()))) {
          minSizeFound = true;
        }

        directions.forEach((direction) -> {
          BlockPos neighborPos = pos.offset(direction);
          if (!this.validPortalPositions.contains(neighborPos) && !validFrameBlocks.contains(neighborPos)) {
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
  private boolean isValidPosForPortalBlock(BlockView world, BlockPos pos) {
    return NetherPortalAccessor.isValidStateInsidePortal(world.getBlockState(pos)) && !world.isOutOfHeightLimit(pos);
  }

  @Unique
  private boolean isValidFrameBlock(BlockView world, BlockPos pos) {
    return NetherPortalAccessor.getIsValidFrameBlock().test(world.getBlockState(pos), world, pos) &&
           !world.isOutOfHeightLimit(pos);
  }
}
