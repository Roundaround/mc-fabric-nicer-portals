package me.roundaround.nicerportals.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.roundaround.gradle.api.annotation.Entrypoint;
import me.roundaround.nicerportals.config.NicerPortalsConfig;
import me.roundaround.nicerportals.config.NicerPortalsPerWorldConfig;
import me.roundaround.nicerportals.generated.Constants;
import me.roundaround.nicerportals.roundalib.client.gui.screen.ConfigScreen;

@Entrypoint(Entrypoint.MOD_MENU)
public class ModMenuImpl implements ModMenuApi {
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return (screen) -> new ConfigScreen(
        screen,
        Constants.MOD_ID,
        NicerPortalsConfig.getInstance(),
        NicerPortalsPerWorldConfig.getInstance()
    );
  }
}
