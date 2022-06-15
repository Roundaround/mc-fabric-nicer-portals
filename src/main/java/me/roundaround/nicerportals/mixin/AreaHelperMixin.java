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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.AreaHelper;

@Mixin(AreaHelper.class)
public abstract class AreaHelperMixin {
  private boolean valid = false;
  HashSet<BlockPos> validPortalPositions = new HashSet<>();

  @Shadow
  WorldAccess world;

  @Shadow
  Direction negativeDir;

  @Shadow
  Direction.Axis axis;

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

    NicerPortalsMod.LOGGER.info("Constructor called");
    valid = checkAreaForPortalValidity(startPos, axis);
  }

  private boolean checkAreaForPortalValidity(BlockPos startPos, Direction.Axis axis) {
    validPortalPositions.clear();

    HashSet<BlockPos> validFrameBlocks = new HashSet<>();
    HashSetQueue<BlockPos> positionsToCheck = new HashSetQueue<>();
    boolean minSizeFound = false;

    List<Direction> directions = List.of(
        Direction.DOWN,
        Direction.UP,
        negativeDir,
        negativeDir.getOpposite());

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
        if (validPortalPositions.size() > 2048) {
          return false;
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

    return minSizeFound;
  }

  private boolean isValidPosForPortalBlock(BlockPos pos) {
    return AreaHelperAccessor.isValidStateInsidePortal(world.getBlockState(pos))
        && !world.isOutOfHeightLimit(pos);
  }

  private boolean isValidFrameBlock(BlockPos pos) {
    return AreaHelperAccessor.getIsValidFrameBlock().test(world.getBlockState(pos), world, pos)
        && !world.isOutOfHeightLimit(pos);
  }

  @Inject(method = "createPortal", at = @At(value = "HEAD"), cancellable = true)
  private void createPortal(CallbackInfo info) {
    if (!NicerPortalsMod.CONFIG.MOD_ENABLED.getValue()
        || !NicerPortalsMod.CONFIG.ANY_SHAPE.getValue()) {
      return;
    }
    
    BlockState blockState = (BlockState)Blocks.NETHER_PORTAL.getDefaultState().with(NetherPortalBlock.AXIS, axis);
    validPortalPositions.stream().forEach((pos) -> {
      world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
    });

    info.cancel();
  }

  @Inject(method = "isValid", at = @At(value = "HEAD"), cancellable = true)
  private void isValid(CallbackInfoReturnable<Boolean> info) {
    if (!NicerPortalsMod.CONFIG.MOD_ENABLED.getValue()
        || !NicerPortalsMod.CONFIG.ANY_SHAPE.getValue()) {
      return;
    }

    info.setReturnValue(valid);
  }

  @Inject(method = "wasAlreadyValid", at = @At(value = "HEAD"), cancellable = true)
  private void wasAlreadyValid(CallbackInfoReturnable<Boolean> info) {
    if (!NicerPortalsMod.CONFIG.MOD_ENABLED.getValue()
        || !NicerPortalsMod.CONFIG.ANY_SHAPE.getValue()) {
      return;
    }

    info.setReturnValue(valid);
  }
}
