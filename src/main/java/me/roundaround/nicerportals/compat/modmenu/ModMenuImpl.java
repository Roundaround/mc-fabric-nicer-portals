package me.roundaround.nicerportals.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.roundaround.nicerportals.NicerPortalsMod;
import me.roundaround.nicerportals.config.NicerPortalsConfig;
import me.roundaround.nicerportals.config.NicerPortalsPerWorldConfig;
import me.roundaround.roundalib.client.gui.screen.ConfigScreen;

public class ModMenuImpl implements ModMenuApi {
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return (screen) -> new ConfigScreen(
        screen, NicerPortalsMod.MOD_ID, NicerPortalsConfig.getInstance(), NicerPortalsPerWorldConfig.getInstance());
  }
}
