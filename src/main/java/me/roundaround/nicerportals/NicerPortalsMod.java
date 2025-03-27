package me.roundaround.nicerportals;

import me.roundaround.gradle.api.annotation.Entrypoint;
import me.roundaround.nicerportals.config.NicerPortalsPerWorldConfig;
import net.fabricmc.api.ModInitializer;

@Entrypoint(Entrypoint.MAIN)
public final class NicerPortalsMod implements ModInitializer {
  @Override
  public void onInitialize() {
    NicerPortalsPerWorldConfig.getInstance().init();
  }
}
