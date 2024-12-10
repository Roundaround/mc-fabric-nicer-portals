package me.roundaround.nicerportals.client;

import me.roundaround.nicerportals.config.NicerPortalsConfig;
import net.fabricmc.api.ClientModInitializer;

public class NicerPortalsClientMod implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    NicerPortalsConfig.getInstance().init();
  }
}
