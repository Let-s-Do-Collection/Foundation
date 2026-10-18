package net.satisfy.foundation.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.satisfy.foundation.Foundation;
import net.satisfy.foundation.compostable.FoundationCompostables;
import net.satisfy.foundation.flammable.FoundationFlammables;
import net.satisfy.foundation.neoforge.config.FoundationNeoForgeConfig;

@Mod(Foundation.MOD_ID)
public class FoundationNeoForge {
    public FoundationNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, FoundationNeoForgeConfig.COMMON_CONFIG);
        modEventBus.addListener(FoundationNeoForgeConfig::onLoad);
        modEventBus.addListener(FoundationNeoForgeConfig::onReload);
        Foundation.init();
        modEventBus.addListener(FMLCommonSetupEvent.class, event -> event.enqueueWork(FoundationFlammables::apply));
        modEventBus.addListener(FMLCommonSetupEvent.class, event -> event.enqueueWork(FoundationCompostables::apply));
    }
}
