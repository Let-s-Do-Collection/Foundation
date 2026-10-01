package net.satisfy.foundation.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.satisfy.foundation.client.FoundationClient;

public class FoundationClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FoundationClient.preInitClient();
        FoundationClient.registerParticles();
        FoundationClient.onInitializeClient();
    }
}
