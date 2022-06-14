package me.roundaround.nicerportals.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.block.BlockState;
import net.minecraft.block.AbstractBlock.ContextPredicate;
import net.minecraft.world.dimension.AreaHelper;

@Mixin(AreaHelper.class)
public interface AreaHelperAccessor {
  @Accessor("IS_VALID_FRAME_BLOCK")
  static ContextPredicate getIsValidFrameBlock() {
    throw new AssertionError();
  }

  @Invoker("validStateInsidePortal")
  static boolean isValidStateInsidePortal(BlockState state) {
    throw new AssertionError();
  }
}
