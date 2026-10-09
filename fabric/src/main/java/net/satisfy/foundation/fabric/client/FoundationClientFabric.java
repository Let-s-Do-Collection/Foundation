package net.satisfy.foundation.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.satisfy.foundation.client.FoundationClient;
import net.satisfy.foundation.fabric.client.mimic.FabricMimicModel;
import net.satisfy.foundation.particle.DriftingParticle;
import net.satisfy.foundation.particle.ColoredDripParticle;
import net.satisfy.foundation.particle.DyeSplashParticle;
import net.satisfy.foundation.particle.FeatherParticle;
import net.satisfy.foundation.particle.FireflyParticle;
import net.satisfy.foundation.particle.SoupBubbleParticle;
import net.satisfy.foundation.particle.SoupCookingBubbleParticle;
import net.satisfy.foundation.particle.SoupSteamParticle;
import net.satisfy.foundation.particle.WaterDripParticle;
import net.satisfy.foundation.particle.WaterSplashParticle;
import net.satisfy.foundation.registry.FoundationParticles;

public class FoundationClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FoundationClient.preInitClient();
        registerParticles();
        FabricMimicModel.register();
        FoundationClient.registerBlockEntityRenderers();
        FoundationClient.onInitializeClient();
    }

    /** Goes straight through Fabric API, architectury's ParticleProviderRegistry is a no-op on fabric. */
    private static void registerParticles() {
        ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();
        registry.register(FoundationParticles.SOUP_BUBBLE.get(), SoupBubbleParticle.Provider::new);
        registry.register(FoundationParticles.SOUP_STEAM.get(), SoupSteamParticle.Provider::new);
        registry.register(FoundationParticles.COLORED_STEAM.get(), SoupSteamParticle.ColoredProvider::new);
        registry.register(FoundationParticles.SOUP_COOKING_BUBBLE.get(), SoupCookingBubbleParticle.Provider::new);
        registry.register(FoundationParticles.COLORED_SOUP_BUBBLE.get(), SoupBubbleParticle.ColoredProvider::new);
        registry.register(FoundationParticles.COLORED_SOUP_COOKING_BUBBLE.get(), SoupCookingBubbleParticle.ColoredProvider::new);
        registry.register(FoundationParticles.DYE_SPLASH.get(), DyeSplashParticle.Provider::new);
        registry.register(FoundationParticles.FEATHER.get(), FeatherParticle.Provider::new);
        registry.register(FoundationParticles.COLORED_DRIP.get(), ColoredDripParticle.Provider::new);
        registry.register(FoundationParticles.WATER_DRIP.get(), WaterDripParticle.Provider::new);
        registry.register(FoundationParticles.WATER_SPLASH.get(), WaterSplashParticle.Provider::new);
        registry.register(FoundationParticles.FIREFLY.get(), FireflyParticle.Provider::new);
        registry.register(FoundationParticles.LEAF.get(), sprites -> new DriftingParticle.Provider(sprites, DriftingParticle.Style.LEAF, DriftingParticle.MotionProfile.LEAF));
    }
}
