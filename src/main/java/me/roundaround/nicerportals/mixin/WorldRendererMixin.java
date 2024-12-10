package me.roundaround.nicerportals.mixin;

import me.roundaround.nicerportals.client.PortalBreakTracker;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
  @Shadow
  private ClientWorld world;

  @Inject(method = "tick", at = @At(value = "TAIL"))
  private void tick(CallbackInfo ci) {
    PortalBreakTracker.getInstance().cleanup(this.world.getTime());
  }
}
