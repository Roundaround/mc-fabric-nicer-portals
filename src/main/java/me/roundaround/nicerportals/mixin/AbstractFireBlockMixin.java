package me.roundaround.nicerportals.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.roundaround.nicerportals.config.NicerPortalsPerWorldConfig;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractFireBlock.class)
public abstract class AbstractFireBlockMixin {
  @WrapOperation(
      method = "shouldLightPortalAt",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z")
  )
  private static boolean isPortalFrameBlock(BlockState blockState, Block block, Operation<Boolean> original) {
    if (!NicerPortalsPerWorldConfig.getInstance().cryingObsidian.getValue()) {
      return original.call(blockState, block);
    }
    return blockState.isOf(Blocks.OBSIDIAN) || blockState.isOf(Blocks.CRYING_OBSIDIAN);
  }
}
