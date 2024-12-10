package me.roundaround.nicerportals.config;

import me.roundaround.nicerportals.NicerPortalsMod;
import me.roundaround.roundalib.config.ConfigPath;
import me.roundaround.roundalib.config.manage.ModConfigImpl;
import me.roundaround.roundalib.config.manage.store.GameScopedFileStore;
import me.roundaround.roundalib.config.option.BooleanConfigOption;
import me.roundaround.roundalib.nightconfig.core.Config;

public class NicerPortalsConfig extends ModConfigImpl implements GameScopedFileStore {
  public static NicerPortalsConfig instance = null;

  public static NicerPortalsConfig getInstance() {
    if (instance == null) {
      instance = new NicerPortalsConfig();
    }
    return instance;
  }

  public BooleanConfigOption dedupeBreakSound;

  private NicerPortalsConfig() {
    super(NicerPortalsMod.MOD_ID, "game", 2);
  }

  @Override
  protected void registerOptions() {
    this.dedupeBreakSound = this.buildRegistration(
        BooleanConfigOption.yesNoBuilder(ConfigPath.of("client", "dedupeBreakSound"))
            .setDefaultValue(true)
            .setComment("Whether to makes portals emit only one sound when they ", "break.", "Client-side only.")
            .build()).clientOnly().commit();
  }

  @Override
  public boolean performConfigUpdate(int versionSnapshot, Config inMemoryConfigSnapshot) {
    if (versionSnapshot == 1) {
      inMemoryConfigSnapshot.set("client.dedupeBreakSound", inMemoryConfigSnapshot.get("dedupeBreakSound"));
      inMemoryConfigSnapshot.remove("dedupeBreakSound");

      return true;
    }

    return false;
  }
}
