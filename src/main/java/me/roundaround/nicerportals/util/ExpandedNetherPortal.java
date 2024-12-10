package me.roundaround.nicerportals.util;

import net.minecraft.world.BlockView;

public interface ExpandedNetherPortal {
  default void checkAreaForPortalValidity(BlockView world) {
  }
}
