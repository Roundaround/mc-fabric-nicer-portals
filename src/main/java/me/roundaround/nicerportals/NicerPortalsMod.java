package me.roundaround.nicerportals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.roundaround.nicerportals.config.NicerPortalsConfig;
import net.fabricmc.api.ModInitializer;

public final class NicerPortalsMod implements ModInitializer {
  public static final String MOD_ID = "nicerportals";
  public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
  public static final NicerPortalsConfig CONFIG = new NicerPortalsConfig();

  @Override
  public void onInitialize() {
    CONFIG.init();
  }
}
