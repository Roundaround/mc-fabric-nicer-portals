package me.roundaround.nicerportals.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.roundaround.nicerportals.client.PortalBreakTracker;
import me.roundaround.nicerportals.config.NicerPortalsConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.world.WorldEventHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(WorldEventHandler.class)
public abstract class WorldEventHandlerMixin {
  @Shadow
  @Final
  private ClientWorld world;

  @WrapWithCondition(
      method = "processWorldEvent", slice = @Slice(
      from = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/block/BlockState;getSoundGroup()Lnet/minecraft/sound/BlockSoundGroup;"
      ), to = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/client/world/ClientWorld;addBlockBreakParticles(Lnet/minecraft/util/math/BlockPos;" +
               "Lnet/minecraft/block/BlockState;)V"
  )
  ), at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/client/world/ClientWorld;playSoundAtBlockCenterClient" +
               "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;" +
               "Lnet/minecraft/sound/SoundCategory;FFZ)V"
  )
  )
  private boolean onlyPlayFirstSound(
      ClientWorld instance,
      BlockPos pos,
      SoundEvent sound,
      SoundCategory category,
      float volume,
      float pitch,
      boolean useDistance
  ) {
    if (!NicerPortalsConfig.getInstance().dedupeBreakSound.getValue()) {
      return true;
    }

    BlockState blockState = this.world.getBlockState(pos);
    if (!blockState.getBlock().equals(Blocks.NETHER_PORTAL)) {
      return true;
    }

    long tick = this.world.getTime();
    PortalBreakTracker tracker = PortalBreakTracker.getInstance();
    boolean deny = tracker.isAlreadyTracked(tick);
    tracker.add(tick);
    return !deny;
  }
}
