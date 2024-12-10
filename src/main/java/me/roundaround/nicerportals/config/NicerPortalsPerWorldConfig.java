package me.roundaround.nicerportals.config;

import me.roundaround.nicerportals.NicerPortalsMod;
import me.roundaround.roundalib.config.ConfigPath;
import me.roundaround.roundalib.config.manage.ModConfigImpl;
import me.roundaround.roundalib.config.manage.store.WorldScopedFileStore;
import me.roundaround.roundalib.config.option.BooleanConfigOption;
import me.roundaround.roundalib.config.option.IntConfigOption;

public class NicerPortalsPerWorldConfig extends ModConfigImpl implements WorldScopedFileStore {
  public static NicerPortalsPerWorldConfig instance = null;

  public static NicerPortalsPerWorldConfig getInstance() {
    if (instance == null) {
      instance = new NicerPortalsPerWorldConfig();
    }
    return instance;
  }

  public BooleanConfigOption preventPortalSpawns;
  public BooleanConfigOption cryingObsidian;
  public BooleanConfigOption anyShape;
  public IntConfigOption maxSize;
  public BooleanConfigOption enforceMinimum;

  private NicerPortalsPerWorldConfig() {
    super(NicerPortalsMod.MOD_ID, "world");
  }

  @Override
  protected void registerOptions() {
    this.preventPortalSpawns = this.buildRegistration(
        BooleanConfigOption.yesNoBuilder(ConfigPath.of("single", "preventPortalSpawns"))
            .setDefaultValue(true)
            .setComment("Whether to prevent portals from spawning Zombified", "Piglins in the overworld.",
                "Server-side & single player only."
            )
            .build()).serverOnly().commit();

    this.cryingObsidian = this.buildRegistration(
        BooleanConfigOption.yesNoBuilder(ConfigPath.of("single", "cryingObsidian"))
            .setDefaultValue(true)
            .setComment("Whether to allow using crying obsidian for portals.", "Server-side & single player only.")
            .build()).serverOnly().commit();

    this.anyShape = this.buildRegistration(BooleanConfigOption.yesNoBuilder(ConfigPath.of("single", "anyShape"))
        .setDefaultValue(true)
        .setComment("Whether to allow portals in any shape and size.", "Server-side & single player only.")
        .build()).serverOnly().commit();

    this.maxSize = this.buildRegistration(IntConfigOption.builder(ConfigPath.of("single", "maxSize"))
        .setDefaultValue(2304)
        .setComment("The maximum allowed portal size to allow. Be warned",
            "that setting values that are too large here could lag",
            "or even crash the game. 2304 is the default and seems to", "cause only a small hiccup in come cases.",
            "Note this value only has any effect if anyShape is true.", "Server-side & single player only."
        )
        .build()).serverOnly().commit();

    this.enforceMinimum = this.buildRegistration(
        BooleanConfigOption.yesNoBuilder(ConfigPath.of("single", "enforceMinimum"))
            .setDefaultValue(true)
            .setComment("Require that portals are at least a 1x2 shape (can",
                "walk through them). Set to false to allow 1x1 portals.",
                "Note this value only has any effect if anyShape is true.", "Server-side & single player only."
            )
            .build()).serverOnly().commit();
  }
}
