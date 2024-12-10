package me.roundaround.nicerportals.mixin;

import me.roundaround.nicerportals.config.NicerPortalsPerWorldConfig;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Arrays;

@Mixin(AbstractFireBlock.class)
public abstract class AbstractFireBlockMixin {
  @ModifyVariable(
      method = "shouldLightPortalAt", at = @At(
      value = "INVOKE", target = "Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"
  )
  )
  private static boolean hasFoundPortalBlock(
      boolean alreadyFoundPortalBlock, World world, BlockPos pos, Direction direction
  ) {
    if (!NicerPortalsPerWorldConfig.getInstance().cryingObsidian.getValue()) {
      return alreadyFoundPortalBlock;
    }

    BlockPos.Mutable mutable = pos.mutableCopy();
    return alreadyFoundPortalBlock || Arrays.stream(Direction.values())
        .anyMatch((searchDirection) -> world.getBlockState(mutable.set(pos).move(searchDirection))
            .isOf(Blocks.CRYING_OBSIDIAN));
  }
}
