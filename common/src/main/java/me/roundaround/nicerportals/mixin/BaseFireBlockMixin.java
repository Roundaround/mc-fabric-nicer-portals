package me.roundaround.nicerportals.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.function.Predicate;
import me.roundaround.nicerportals.config.NicerPortalsPerWorldConfig;
import me.roundaround.nicerportals.tags.BlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// isPortal gates the expensive PortalShape scan on a cheap adjacent-frame check. 26.3 routes that
// through PortalShape.FRAME.test(state) on Fabric and NeoForge; Forge binpatches it onto the
// three-argument PortalShape.FRAME_TEST. Each loader matches exactly one wrap, hence require = 0 —
// MidAirIgnitionPortalClientTest is what catches both missing.
//
// Bails to vanilla on the logical client. isPortal is reached from FlintAndSteelItem /
// FireChargeItem#useOn, which the client runs as prediction, and the per-world config it would read
// only exists where a world directory is attached — never on a client connected to a server. See GH-16.
@Mixin(BaseFireBlock.class)
public abstract class BaseFireBlockMixin {
  @WrapOperation(
      method = "isPortal",
      at = @At(value = "INVOKE", target = "Ljava/util/function/Predicate;test(Ljava/lang/Object;)Z"),
      require = 0
  )
  private static boolean nicerportals$frameViaTag(
      Predicate<BlockState> frame,
      Object state,
      Operation<Boolean> original,
      @Local(argsOnly = true) Level level
  ) {
    if (level.isClientSide() || !NicerPortalsPerWorldConfig.getInstance().portalFrameTag.getValue()) {
      return original.call(frame, state);
    }
    return ((BlockState) state).is(BlockTags.PORTAL_FRAME);
  }

  @WrapOperation(
      method = "isPortal",
      at = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$StatePredicate;test(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z"
      ),
      require = 0
  )
  private static boolean nicerportals$frameViaTagForge(
      BlockBehaviour.StatePredicate frame,
      BlockState state,
      BlockGetter getter,
      BlockPos pos,
      Operation<Boolean> original,
      @Local(argsOnly = true) Level level
  ) {
    if (level.isClientSide() || !NicerPortalsPerWorldConfig.getInstance().portalFrameTag.getValue()) {
      return original.call(frame, state, getter, pos);
    }
    return state.is(BlockTags.PORTAL_FRAME);
  }
}
