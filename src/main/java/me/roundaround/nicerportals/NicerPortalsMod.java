package me.roundaround.nicerportals;

import me.roundaround.nicerportals.config.NicerPortalsPerWorldConfig;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class NicerPortalsMod implements ModInitializer {
  public static final String MOD_ID = "nicerportals";
  public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

  @Override
  public void onInitialize() {
    NicerPortalsPerWorldConfig.getInstance().init();
  }
}
