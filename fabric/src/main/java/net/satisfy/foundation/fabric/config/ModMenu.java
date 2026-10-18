package net.satisfy.foundation.fabric.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.satisfy.foundation.Foundation;
import net.satisfy.foundation.client.gui.LetsDoConfigScreen;

@Environment(EnvType.CLIENT)
public class ModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new LetsDoConfigScreen(parent, Foundation.MOD_ID, screen -> AutoConfig.getConfigScreen(FoundationFabricConfig.class, screen).get());
    }
}
