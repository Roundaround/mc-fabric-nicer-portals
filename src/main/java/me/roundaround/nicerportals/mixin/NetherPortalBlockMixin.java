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
      value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"
  )
  )
  private boolean canSpawnZombifiedPiglins(boolean gameRuleAllowsSpawning) {
    return gameRuleAllowsSpawning && !NicerPortalsPerWorldConfig.getInstance().preventPortalSpawns.getValue();
  }
}
