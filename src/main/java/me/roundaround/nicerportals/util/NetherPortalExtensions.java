package me.roundaround.nicerportals.util;

import me.roundaround.gradle.api.annotation.InjectedInterface;
import net.minecraft.world.level.BlockGetter;

@InjectedInterface
public interface NetherPortalExtensions {
  default void nicerportals$checkAreaForPortalValidity(BlockGetter world) {
    throw new UnsupportedOperationException("Unable to call directly from injected interface. Implemented in mixin.");
  }
}
