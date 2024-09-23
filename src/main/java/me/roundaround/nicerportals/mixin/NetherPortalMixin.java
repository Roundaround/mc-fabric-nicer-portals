package me.roundaround.nicerportals.mixin;

import me.roundaround.nicerportals.config.NicerPortalsConfig;
import me.roundaround.nicerportals.util.HashSetQueue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
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
public abstract class NetherPortalMixin {
  @Unique
  private boolean valid = false;
  @Unique
  HashSet<BlockPos> validPortalPositions = new HashSet<>();
  @Unique
  int portalBlockCount = 0;

  @Final
  @Shadow
  private WorldAccess world;

  @Final
  @Shadow
  private Direction negativeDir;

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
    if (!NicerPortalsConfig.getInstance().modEnabled.getValue() ||
        !NicerPortalsConfig.getInstance().cryingObsidian.getValue()) {
      return;
    }

    if (state.isOf(Blocks.CRYING_OBSIDIAN)) {
      info.setReturnValue(true);
    }
  }

  @Inject(method = "<init>", at = @At(value = "TAIL"))
  private void constructor(WorldAccess world, BlockPos startPos, Direction.Axis axis, CallbackInfo info) {
    if (!NicerPortalsConfig.getInstance().modEnabled.getValue() ||
        !NicerPortalsConfig.getInstance().anyShape.getValue()) {
      return;
    }

    // TODO: Look into caching the AreaHelper reference in the block somehow
    valid = checkAreaForPortalValidity(startPos, axis);
  }

  @Unique
  private boolean checkAreaForPortalValidity(BlockPos startPos, Direction.Axis axis) {
    validPortalPositions.clear();

    HashSet<BlockPos> validFrameBlocks = new HashSet<>();
    HashSetQueue<BlockPos> positionsToCheck = new HashSetQueue<>();
    boolean minSizeFound = false;

    List<Direction> directions = List.of(Direction.DOWN, Direction.UP, negativeDir, negativeDir.getOpposite());

    positionsToCheck.push(startPos);

    while (!positionsToCheck.isEmpty()) {
      BlockPos pos = positionsToCheck.pop();
      if (validPortalPositions.contains(pos) || validFrameBlocks.contains(pos)) {
        continue;
      }

      boolean isOrCanBePortal = isValidPosForPortalBlock(pos);
      boolean isFrameBlock = isValidFrameBlock(pos);

      if (!isOrCanBePortal && !isFrameBlock) {
        return false;
      }

      if (isOrCanBePortal) {
        validPortalPositions.add(pos);
        if (validPortalPositions.size() > NicerPortalsConfig.getInstance().maxSize.getValue()) {
          return false;
        }

        if (world.getBlockState(pos).isOf(Blocks.NETHER_PORTAL)) {
          portalBlockCount++;
        }

        if (!minSizeFound && (validPortalPositions.contains(pos.up()) || validPortalPositions.contains(pos.down()))) {
          minSizeFound = true;
        }

        directions.forEach((direction) -> {
          BlockPos neighborPos = pos.offset(direction);
          if (!validPortalPositions.contains(neighborPos) && !validFrameBlocks.contains(neighborPos)) {
            positionsToCheck.push(neighborPos);
          }
        });
      } else {
        validFrameBlocks.add(pos);
      }
    }

    return minSizeFound || !NicerPortalsConfig.getInstance().enforceMinimum.getValue();
  }

  @Unique
  private boolean isValidPosForPortalBlock(BlockPos pos) {
    return NetherPortalAccessor.isValidStateInsidePortal(world.getBlockState(pos)) && !world.isOutOfHeightLimit(pos);
  }

  @Unique
  private boolean isValidFrameBlock(BlockPos pos) {
    return NetherPortalAccessor.getIsValidFrameBlock().test(world.getBlockState(pos), world, pos) &&
        !world.isOutOfHeightLimit(pos);
  }

  @Inject(method = "createPortal", at = @At(value = "HEAD"), cancellable = true)
  private void createPortal(CallbackInfo info) {
    if (!NicerPortalsConfig.getInstance().modEnabled.getValue() ||
        !NicerPortalsConfig.getInstance().anyShape.getValue()) {
      return;
    }

    BlockState blockState = Blocks.NETHER_PORTAL.getDefaultState().with(NetherPortalBlock.AXIS, axis);
    validPortalPositions.forEach(
        (pos) -> world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS | Block.FORCE_STATE));

    info.cancel();
  }

  @Inject(method = "isValid", at = @At(value = "HEAD"), cancellable = true)
  private void isValid(CallbackInfoReturnable<Boolean> info) {
    if (!NicerPortalsConfig.getInstance().modEnabled.getValue() ||
        !NicerPortalsConfig.getInstance().anyShape.getValue()) {
      return;
    }

    info.setReturnValue(valid);
  }

  @Inject(method = "wasAlreadyValid", at = @At(value = "HEAD"), cancellable = true)
  private void wasAlreadyValid(CallbackInfoReturnable<Boolean> info) {
    if (!NicerPortalsConfig.getInstance().modEnabled.getValue() ||
        !NicerPortalsConfig.getInstance().anyShape.getValue()) {
      return;
    }

    info.setReturnValue(valid && validPortalPositions.size() == portalBlockCount);
  }
}
