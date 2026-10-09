package net.satisfy.foundation.fabric;

import dev.architectury.event.events.common.LifecycleEvent;
import net.fabricmc.api.ModInitializer;
import net.satisfy.foundation.Foundation;
import net.satisfy.foundation.compostable.FoundationCompostables;
import net.satisfy.foundation.flammable.FoundationFlammables;

public class FoundationFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Foundation.init();
        LifecycleEvent.SETUP.register(FoundationFlammables::apply);
        LifecycleEvent.SETUP.register(FoundationCompostables::apply);
    }
}
