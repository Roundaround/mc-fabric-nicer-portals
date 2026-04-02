package me.roundaround.nicerportals.mixin;

import net.minecraft.world.level.block.state.BlockBehaviour.StatePredicate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PortalShape.class)
public interface PortalShapeAccessor {
  @Accessor("FRAME")
  static StatePredicate getIsValidFrameBlock() {
    throw new AssertionError();
  }

  @Invoker("isEmpty")
  static boolean isValidStateInsidePortal(BlockState state) {
    throw new AssertionError();
  }
}
