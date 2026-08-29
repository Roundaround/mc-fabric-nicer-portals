package me.roundaround.nicerportals.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.roundaround.nicerportals.config.NicerPortalsPerWorldConfig;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// shouldLightPortalAt is reached from FlintAndSteelItem / FireChargeItem#useOnBlock, which the
// client runs as prediction. The per-world config it would read only exists where a world
// directory is attached — never on a client connected to a server — so bail to vanilla there.
// See GH-16.
@Mixin(AbstractFireBlock.class)
public abstract class AbstractFireBlockMixin {
  @WrapOperation(
      method = "shouldLightPortalAt",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z")
  )
  private static boolean isPortalFrameBlock(
      BlockState blockState, Block block, Operation<Boolean> original, @Local(argsOnly = true) World world) {
    if (world.isClient() || !NicerPortalsPerWorldConfig.getInstance().cryingObsidian.getValue()) {
      return original.call(blockState, block);
    }
    return blockState.isOf(Blocks.OBSIDIAN) || blockState.isOf(Blocks.CRYING_OBSIDIAN);
  }
}
