package me.roundaround.nicerportals.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.roundaround.nicerportals.NicerPortalsMod;
import me.roundaround.roundalib.config.gui.ConfigScreen;

public class ModMenuImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (screen) -> new ConfigScreen(screen, NicerPortalsMod.CONFIG);
    }
}
