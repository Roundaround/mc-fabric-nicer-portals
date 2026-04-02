package me.roundaround.nicerportals.mixin;

import me.roundaround.nicerportals.client.PortalBreakTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
  @Shadow
  private ClientLevel level;

  @Inject(method = "tick", at = @At(value = "TAIL"))
  private void tick(CallbackInfo ci) {
    PortalBreakTracker.getInstance().cleanup(this.level.getGameTime());
  }
}
