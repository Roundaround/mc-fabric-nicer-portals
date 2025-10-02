package me.roundaround.nicerportals.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.roundaround.nicerportals.config.NicerPortalsPerWorldConfig;
import net.minecraft.block.NetherPortalBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NetherPortalBlock.class)
public abstract class NetherPortalBlockMixin {
  @ModifyExpressionValue(
      method = "randomTick", at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/block/BlockState;allowsSpawning(Lnet/minecraft/world/BlockView;" +
               "Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/EntityType;)Z"
  )
  )
  private boolean canSpawnZombifiedPiglins(boolean originalValue) {
    return originalValue && !NicerPortalsPerWorldConfig.getInstance().preventPortalSpawns.getValue();
  }
}
