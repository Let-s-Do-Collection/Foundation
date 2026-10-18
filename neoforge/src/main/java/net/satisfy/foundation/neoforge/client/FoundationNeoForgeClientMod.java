package net.satisfy.foundation.neoforge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.satisfy.foundation.Foundation;
import net.satisfy.foundation.client.gui.LetsDoConfigScreen;

@Mod(value = Foundation.MOD_ID, dist = Dist.CLIENT)
public class FoundationNeoForgeClientMod {
    public FoundationNeoForgeClientMod(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, parent) -> new LetsDoConfigScreen(parent, Foundation.MOD_ID, screen -> new ConfigurationScreen(modContainer, screen)));
    }
}
