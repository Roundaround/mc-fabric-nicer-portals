package me.roundaround.nicerportals.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.roundaround.nicerportals.client.PortalBreakTracker;
import me.roundaround.nicerportals.config.NicerPortalsConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(LevelEventHandler.class)
public abstract class LevelEventHandlerMixin {
  @Shadow
  @Final
  private ClientLevel level;

  @WrapWithCondition(
      method = "levelEvent", slice = @Slice(
      from = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/world/level/block/state/BlockState;getSoundType()" +
                   "Lnet/minecraft/world/level/block/SoundType;"
      ), to = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/client/multiplayer/ClientLevel;addDestroyBlockEffect(Lnet/minecraft/core/BlockPos;" +
               "Lnet/minecraft/world/level/block/state/BlockState;)V",
      ordinal = 0
  )
  ), at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/client/multiplayer/ClientLevel;playLocalSound(Lnet/minecraft/core/BlockPos;" +
               "Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"
  )
  )
  private boolean onlyPlayFirstSound(
      ClientLevel instance,
      BlockPos pos,
      SoundEvent sound,
      SoundSource category,
      float volume,
      float pitch,
      boolean useDistance
  ) {
    if (!NicerPortalsConfig.getInstance().dedupeBreakSound.getValue()) {
      return true;
    }

    BlockState blockState = this.level.getBlockState(pos);
    if (!blockState.getBlock().equals(Blocks.NETHER_PORTAL)) {
      return true;
    }

    long tick = this.level.getGameTime();
    PortalBreakTracker tracker = PortalBreakTracker.getInstance();
    boolean deny = tracker.isAlreadyTracked(tick);
    tracker.add(tick);
    return !deny;
  }
}
