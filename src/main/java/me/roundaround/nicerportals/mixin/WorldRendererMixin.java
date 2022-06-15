package me.roundaround.nicerportals.mixin;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.roundaround.nicerportals.NicerPortalsMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
  private Set<Long> portalBreakTicks = new HashSet<>();

  @Shadow
  private ClientWorld world;

  @Inject(method = "processWorldEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getSoundGroup()Lnet/minecraft/sound/BlockSoundGroup;"), cancellable = true)
  private void processWorldEvent(int eventId, BlockPos pos, int data, CallbackInfo info) {
    if (!NicerPortalsMod.CONFIG.MOD_ENABLED.getValue()
        || !NicerPortalsMod.CONFIG.DEDUPE_BREAK_SOUND.getValue()) {
      return;
    }

    BlockState blockState = Block.getStateFromRawId(data);
    if (!blockState.getBlock().equals(Blocks.NETHER_PORTAL)) {
      return;
    }

    long tick = world.getTime();
    if (portalBreakTicks.contains(tick) || portalBreakTicks.contains(tick - 1)) {
      // TODO: Investigate how to only skip the playSound call instead of
      // cancelling the entire processWorldEvent method
      world.addBlockBreakParticles(pos, blockState);
      info.cancel();
    }

    portalBreakTicks.add(tick);
  }

  @Inject(method = "tick", at = @At(value = "TAIL"))
  private void tick(CallbackInfo info) {
    long tick = world.getTime();
    portalBreakTicks.stream()
        .filter((portalBreakTick) -> tick >= portalBreakTick + 2)
        .collect(Collectors.toList())
        .forEach(portalBreakTicks::remove);
  }
}
