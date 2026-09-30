package net.satisfy.foundation.fabric;

import net.fabricmc.api.ModInitializer;
import net.satisfy.foundation.Foundation;

public class FoundationFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Foundation.init();
    }
}
