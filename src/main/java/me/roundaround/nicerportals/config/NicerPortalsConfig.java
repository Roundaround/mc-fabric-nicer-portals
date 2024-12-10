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
    super(NicerPortalsMod.MOD_ID, "game", 3);
  }

  @Override
  protected void registerOptions() {
    this.dedupeBreakSound = this.buildRegistration(BooleanConfigOption.yesNoBuilder(ConfigPath.of("dedupeBreakSound"))
        .setDefaultValue(true)
        .setComment("Whether to makes portals emit only one sound when they ", "break.", "Client-side only.")
        .build()).clientOnly().commit();
  }

  @Override
  public boolean performConfigUpdate(int versionSnapshot, Config inMemoryConfigSnapshot) {
    // Version 1 actually is the same as version 3. Go figure!

    if (versionSnapshot == 2) {
      inMemoryConfigSnapshot.set("dedupeBreakSound", inMemoryConfigSnapshot.get("client.dedupeBreakSound"));
      inMemoryConfigSnapshot.remove("client.dedupeBreakSound");

      return true;
    }

    return false;
  }
}
