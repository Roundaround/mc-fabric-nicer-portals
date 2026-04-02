package me.roundaround.nicerportals.tags;

import me.roundaround.nicerportals.generated.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class BlockTags {
  public static final TagKey<Block> PORTAL_FRAME = TagKey.create(
      Registries.BLOCK,
      Identifier.fromNamespaceAndPath(Constants.MOD_ID, "portal_frame")
  );
}
