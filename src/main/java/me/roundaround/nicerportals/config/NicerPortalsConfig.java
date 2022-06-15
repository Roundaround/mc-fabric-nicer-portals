package me.roundaround.nicerportals.config;

import me.roundaround.nicerportals.NicerPortalsMod;
import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.option.BooleanConfigOption;
import me.roundaround.roundalib.config.option.IntConfigOption;

public class NicerPortalsConfig extends ModConfig {
  public final BooleanConfigOption MOD_ENABLED;
  public final BooleanConfigOption DEDUPE_BREAK_SOUND;
  public final BooleanConfigOption PREVENT_PORTAL_SPAWNS;
  public final BooleanConfigOption CRYING_OBSIDIAN;
  public final BooleanConfigOption ANY_SHAPE;
  public final IntConfigOption MAX_SIZE;
  public final BooleanConfigOption ENFORCE_MINUMUM;

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

    MAX_SIZE = registerConfigOption(
        "single", // The config screen group is only relevant for single player
        IntConfigOption
            .builder("maxSize", "nicerportals.max_size.label")
            .setDefaultValue(2304)
            .setComment("The maximum allowed portal size to allow. Be warned\n"
                + "that setting values that are too large here could lag\n"
                + "or even crash the game. 2304 is the default and seems to\n"
                + "cause only a small hiccup in come cases.\n"
                + "Note this value only has any effect if anyShape is true.\n"
                + "Server-side & single player only.")
            .build());

    ENFORCE_MINUMUM = registerConfigOption(
        "single", // The config screen group is only relevant for single player
        BooleanConfigOption
            .yesNoBuilder("enforceMinimum", "nicerportals.enforce_minimum.label")
            .setComment("Require that portals are at least a 1x2 shape (can\n"
                + "walk through them). Set to false to allow 1x1 portals.\n"
                + "Note this value only has any effect if anyShape is true.\n"
                + "Server-side & single player only.")
            .build());
  }
}
