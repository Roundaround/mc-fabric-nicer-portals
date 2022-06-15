package me.roundaround.nicerportals.mixin;

import java.util.HashSet;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.roundaround.nicerportals.NicerPortalsMod;
import me.roundaround.nicerportals.util.HashSetQueue;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.AreaHelper;

@Mixin(AreaHelper.class)
public abstract class AreaHelperMixin {
  private boolean valid = false;

  @Shadow
  Direction negativeDir;

  @Inject(method = "method_30487", at = @At(value = "HEAD"), cancellable = true)
  private static void isValidFrameBlock(
      BlockState state,
      BlockView world,
      BlockPos pos,
      CallbackInfoReturnable<Boolean> info) {
    if (!NicerPortalsMod.CONFIG.MOD_ENABLED.getValue()
        || !NicerPortalsMod.CONFIG.CRYING_OBSIDIAN.getValue()) {
      return;
    }

    if (state.isOf(Blocks.CRYING_OBSIDIAN)) {
      info.setReturnValue(true);
    }
  }

  @Inject(method = "<init>", at = @At(value = "TAIL"))
  private void constructor(WorldAccess world, BlockPos startPos, Direction.Axis axis, CallbackInfo info) {
    if (!NicerPortalsMod.CONFIG.MOD_ENABLED.getValue()
        || !NicerPortalsMod.CONFIG.ANY_SHAPE.getValue()) {
      return;
    }

    long startTime = Util.getMeasuringTimeMs();

    valid = isAreaValidForCreation(world, startPos, axis);
    NicerPortalsMod.LOGGER.info("Portal area is " + (valid ? "" : "not ") + "valid.");

    long duration = Util.getMeasuringTimeMs() - startTime;
    NicerPortalsMod.LOGGER.info(String.format("Portal validity check took %dms", duration));
  }

  private boolean isAreaValidForCreation(WorldAccess world, BlockPos startPos, Direction.Axis axis) {
    HashSet<BlockPos> positionsChecked = new HashSet<>();
    HashSetQueue<BlockPos> positionsToCheck = new HashSetQueue<>();
    boolean minSizeFound = false;
    int portalBlocksCounted = 0;

    List<Direction> directions = List.of(
        Direction.DOWN,
        Direction.UP,
        negativeDir,
        negativeDir.getOpposite());

    positionsToCheck.push(startPos);

    while (!positionsToCheck.isEmpty()) {
      BlockPos pos = positionsToCheck.pop();
      if (positionsChecked.contains(pos)) {
        continue;
      }

      boolean isOrCanBePortal = isValidPosForPortalBlock(world, pos);
      boolean isFrameBlock = isValidFrameBlock(world, pos);

      if (!isOrCanBePortal && !isFrameBlock) {
        return false;
      }

      if (isOrCanBePortal) {
        if (portalBlocksCounted++ > 2048) {
          return false;
        }

        if (!minSizeFound && List.of(pos.up(), pos.down()).stream().anyMatch(
            (neighborPos) -> positionsChecked.contains(neighborPos) && isValidPosForPortalBlock(world, neighborPos))) {
          minSizeFound = true;
        }

        directions.forEach((direction) -> {
          BlockPos neighborPos = pos.offset(direction);
          if (!positionsChecked.contains(neighborPos)) {
            positionsToCheck.push(neighborPos);
          }
        });
      }
    }

    return minSizeFound;
  }

  private static boolean isValidPosForPortalBlock(BlockView world, BlockPos pos) {
    return AreaHelperAccessor.isValidStateInsidePortal(world.getBlockState(pos))
        && !world.isOutOfHeightLimit(pos);
  }

  private static boolean isValidFrameBlock(BlockView world, BlockPos pos) {
    return AreaHelperAccessor.getIsValidFrameBlock().test(world.getBlockState(pos), world, pos)
        && !world.isOutOfHeightLimit(pos);
  }

  @Inject(method = "createPortal", at = @At(value = "HEAD"), cancellable = true)
  private void createPortal(CallbackInfo info) {
    if (!NicerPortalsMod.CONFIG.MOD_ENABLED.getValue()
        || !NicerPortalsMod.CONFIG.ANY_SHAPE.getValue()) {
      return;
    }
  }

  @Inject(method = "isValid", at = @At(value = "HEAD"), cancellable = true)
  private void isValid(CallbackInfoReturnable<Boolean> info) {
    if (!NicerPortalsMod.CONFIG.MOD_ENABLED.getValue()
        || !NicerPortalsMod.CONFIG.ANY_SHAPE.getValue()) {
      return;
    }
  }

  @Inject(method = "wasAlreadyValid", at = @At(value = "HEAD"), cancellable = true)
  private void wasAlreadyValid(CallbackInfoReturnable<Boolean> info) {
    if (!NicerPortalsMod.CONFIG.MOD_ENABLED.getValue()
        || !NicerPortalsMod.CONFIG.ANY_SHAPE.getValue()) {
      return;
    }
  }
}
