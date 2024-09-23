package me.roundaround.nicerportals.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.roundaround.nicerportals.config.NicerPortalsConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
  @Unique
  private final Set<Long> portalBreakTicks = new HashSet<>();

  @Shadow
  private ClientWorld world;

  @WrapWithCondition(
      method = "processWorldEvent", at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/client/world/ClientWorld;playSoundAtBlockCenter(Lnet/minecraft/util/math/BlockPos;" +
          "Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V"
  )
  )
  private boolean onlyPlayFirstSound(
      ClientWorld world,
      BlockPos blockPos,
      SoundEvent soundEvent,
      SoundCategory soundCategory,
      float volume,
      float pitch,
      boolean useDistance
  ) {
    if (!NicerPortalsConfig.getInstance().modEnabled.getValue() ||
        !NicerPortalsConfig.getInstance().dedupeBreakSound.getValue()) {
      return true;
    }

    BlockState blockState = world.getBlockState(blockPos);
    if (!blockState.getBlock().equals(Blocks.NETHER_PORTAL)) {
      return true;
    }

    long tick = world.getTime();
    boolean deny = this.portalBreakTicks.contains(tick) || this.portalBreakTicks.contains(tick - 1);

    this.portalBreakTicks.add(tick);
    return !deny;
  }

  @Inject(method = "tick", at = @At(value = "TAIL"))
  private void tick(CallbackInfo info) {
    long tick = this.world.getTime();
    this.portalBreakTicks.stream()
        .filter((portalBreakTick) -> tick >= portalBreakTick + 2)
        .toList()
        .forEach(this.portalBreakTicks::remove);
  }
}
