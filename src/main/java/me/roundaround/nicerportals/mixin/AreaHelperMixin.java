package me.roundaround.nicerportals.mixin;

import java.util.HashSet;
import java.util.Stack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.roundaround.nicerportals.NicerPortalsMod;
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
    NicerPortalsMod.LOGGER
        .info(String.format("Portal validity check took %dms", Util.getMeasuringTimeMs() - startTime));
  }

  private boolean isAreaValidForCreation(WorldAccess world, BlockPos startPos, Direction.Axis axis) {
    HashSet<BlockPos> positionsChecked = new HashSet<>();
    HashSet<BlockPos> positionsFound = new HashSet<>();
    Stack<BlockPos> positionsToCheck = new Stack<>();

    positionsFound.add(startPos);
    positionsToCheck.push(startPos);

    while (!positionsToCheck.isEmpty()) {
      BlockPos pos = positionsToCheck.pop();
      if (positionsChecked.contains(pos)) {
        continue;
      }

      BlockState state = world.getBlockState(pos);

      positionsChecked.add(pos);

      if (AreaHelperAccessor.getIsValidFrameBlock().test(state, world, pos)) {
        continue;
      }

      if (!AreaHelperAccessor.isValidStateInsidePortal(state)) {
        return false;
      }

      if (positionsChecked.size() > 512) {
        return false;
      }

      BlockPos up = pos.up();
      if (!positionsFound.contains(up)) {
        positionsToCheck.push(up);
        positionsFound.add(up);
      }

      BlockPos down = pos.down();
      if (!positionsFound.contains(down)) {
        positionsToCheck.push(down);
        positionsFound.add(down);
      }

      BlockPos side1 = pos.offset(negativeDir);
      if (!positionsFound.contains(side1)) {
        positionsToCheck.push(side1);
        positionsFound.add(side1);
      }

      BlockPos side2 = pos.offset(negativeDir.getOpposite());
      if (!positionsFound.contains(side2)) {
        positionsToCheck.push(side2);
        positionsFound.add(side2);
      }
    }

    // TODO: Enforce minimum size, must be at least 1 wide, 2 tall
    return true;
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
