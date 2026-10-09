package net.satisfy.foundation.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.satisfy.foundation.Foundation;
import net.satisfy.foundation.compostable.FoundationCompostables;
import net.satisfy.foundation.flammable.FoundationFlammables;

@Mod(Foundation.MOD_ID)
public class FoundationNeoForge {
    public FoundationNeoForge(IEventBus modEventBus) {
        Foundation.init();
        modEventBus.addListener(FMLCommonSetupEvent.class, event -> event.enqueueWork(FoundationFlammables::apply));
        modEventBus.addListener(FMLCommonSetupEvent.class, event -> event.enqueueWork(FoundationCompostables::apply));
    }
}
