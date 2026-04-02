package me.roundaround.nicerportals.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.roundaround.nicerportals.config.NicerPortalsPerWorldConfig;
import me.roundaround.nicerportals.tags.BlockTags;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BaseFireBlock.class)
public abstract class BaseFireBlockMixin {
  @WrapOperation(
      method = "isPortal",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z")
  )
  private static boolean isPortalFrameBlock(BlockState instance, Object o, Operation<Boolean> original) {
    if (!NicerPortalsPerWorldConfig.getInstance().portalFrameTag.getValue()) {
      return original.call(instance, o);
    }
    return instance.is(BlockTags.PORTAL_FRAME);
  }
}
