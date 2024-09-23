package me.roundaround.nicerportals.mixin;

import me.roundaround.nicerportals.config.NicerPortalsConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetherPortalBlock.class)
public abstract class NetherPortalBlockMixin {
  @Inject(method = "randomTick", at = @At(value = "HEAD"), cancellable = true)
  private void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo info) {
    if (NicerPortalsConfig.getInstance().modEnabled.getValue() &&
        NicerPortalsConfig.getInstance().preventPortalSpawns.getValue()) {
      info.cancel();
    }
  }
}
