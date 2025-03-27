package me.roundaround.nicerportals.client;

import me.roundaround.gradle.api.annotation.Entrypoint;
import me.roundaround.nicerportals.config.NicerPortalsConfig;
import net.fabricmc.api.ClientModInitializer;

@Entrypoint(Entrypoint.CLIENT)
public class NicerPortalsClientMod implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    NicerPortalsConfig.getInstance().init();
  }
}
