package me.roundaround.nicerportals.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.roundaround.nicerportals.NicerPortalsMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.dimension.AreaHelper;

@Mixin(AreaHelper.class)
public abstract class AreaHelperMixin {
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
}
