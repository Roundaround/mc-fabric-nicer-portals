package me.roundaround.nicerportals.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.roundaround.nicerportals.config.NicerPortalsPerWorldConfig;
import net.minecraft.world.level.block.NetherPortalBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NetherPortalBlock.class)
public abstract class NetherPortalBlockMixin {
  @ModifyExpressionValue(
      method = "randomTick", at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/level/block/state/BlockState;isValidSpawn" +
               "(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;" +
               "Lnet/minecraft/world/entity/EntityType;)Z"
  )
  )
  private boolean canSpawnZombifiedPiglins(boolean originalValue) {
    return originalValue && !NicerPortalsPerWorldConfig.getInstance().preventPortalSpawns.getValue();
  }
}
