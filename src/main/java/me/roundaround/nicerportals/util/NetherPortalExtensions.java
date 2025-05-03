package me.roundaround.nicerportals.util;

import net.minecraft.world.BlockView;

public interface NetherPortalExtensions {
  default void nicerportals$checkAreaForPortalValidity(BlockView world) {
    throw new UnsupportedOperationException("Unable to call directly from injected interface. Implemented in mixin.");
  }
}
