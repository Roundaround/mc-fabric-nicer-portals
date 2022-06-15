package me.roundaround.nicerportals.config;

import me.roundaround.nicerportals.NicerPortalsMod;
import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.option.BooleanConfigOption;

public class NicerPortalsConfig extends ModConfig {
  public final BooleanConfigOption MOD_ENABLED;
  public final BooleanConfigOption DEDUPE_BREAK_SOUND;
  public final BooleanConfigOption PREVENT_PORTAL_SPAWNS;
  public final BooleanConfigOption CRYING_OBSIDIAN;
  public final BooleanConfigOption ANY_SHAPE;

  public NicerPortalsConfig() {
    super(NicerPortalsMod.MOD_ID);

    MOD_ENABLED = registerConfigOption(
        BooleanConfigOption
            .builder("modEnabled", "nicerportals.mod_enabled.label")
            .setComment("Simple toggle for the mod! Set to false to disable everything.")
            .build());

    DEDUPE_BREAK_SOUND = registerConfigOption(
        BooleanConfigOption
            .yesNoBuilder("dedupeBreakSound", "nicerportals.dedupe_break_sound.label")
            .setComment("Whether to makes portals emit only one sound when they \n"
                + "break.\n"
                + "Client-side only.")
            .build());

    PREVENT_PORTAL_SPAWNS = registerConfigOption(
        "single", // The config screen group is only relevant for single player
        BooleanConfigOption
            .yesNoBuilder("preventPortalSpawns", "nicerportals.prevent_portal_spawns.label")
            .setComment("Whether to prevent portals from spawning Zombified\n"
                + "Piglins in the overworld.\n"
                + "Server-side & single player only.")
            .build());

    CRYING_OBSIDIAN = registerConfigOption(
        "single", // The config screen group is only relevant for single player
        BooleanConfigOption
            .yesNoBuilder("cryingObsidian", "nicerportals.crying_obsidian.label")
            .setComment("Whether to allow using crying obsidian for portals.\n"
                + "Server-side & single player only.")
            .build());

    ANY_SHAPE = registerConfigOption(
        "single", // The config screen group is only relevant for single player
        BooleanConfigOption
            .yesNoBuilder("anyShape", "nicerportals.any_shape.label")
            .setComment("Whether to allow portals in any shape and size.\n"
                + "Server-side & single player only.")
            .build());
  }
}
