package net.satisfy.foundation.fabric;

import dev.architectury.event.events.common.LifecycleEvent;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.satisfy.foundation.Foundation;
import net.satisfy.foundation.compostable.FoundationCompostables;
import net.satisfy.foundation.fabric.config.FoundationFabricConfig;
import net.satisfy.foundation.flammable.FoundationFlammables;

public class FoundationFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        AutoConfig.register(FoundationFabricConfig.class, GsonConfigSerializer::new);
        Foundation.init();
        LifecycleEvent.SETUP.register(FoundationFlammables::apply);
        LifecycleEvent.SETUP.register(FoundationCompostables::apply);
    }
}
