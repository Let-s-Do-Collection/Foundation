package net.satisfy.foundation.client;

import net.satisfy.foundation.client.armor.ArmorSetTooltips;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import net.satisfy.foundation.client.render.SinkRenderer;
import net.satisfy.foundation.client.render.StackRenderer;
import net.satisfy.foundation.registry.FoundationBlockEntities;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import net.satisfy.foundation.banner.CompletionistBannerRenderer;
import net.satisfy.foundation.overlay.BlockInfoOverlay;
import net.satisfy.foundation.particle.DyeSplashParticle;
import net.satisfy.foundation.particle.FeatherParticle;
import net.satisfy.foundation.particle.FireflyParticle;
import net.satisfy.foundation.particle.SoupBubbleParticle;
import net.satisfy.foundation.particle.SoupCookingBubbleParticle;
import net.satisfy.foundation.particle.SoupSteamParticle;
import net.satisfy.foundation.particle.WaterDripParticle;
import net.satisfy.foundation.particle.WaterSplashParticle;
import net.satisfy.foundation.registry.FoundationEntities;
import net.satisfy.foundation.registry.FoundationParticles;
import net.satisfy.foundation.seat.ChairRenderer;
import net.satisfy.foundation.tooltip.InfoTooltip;

/** Client side setup of the lib, called from the loader specific client entrypoints. */
public class FoundationClient {
    /** Model layers and entity renderers, needs to happen early. */
    public static void preInitClient() {
        EntityModelLayerRegistry.register(CompletionistBannerRenderer.LAYER_LOCATION, CompletionistBannerRenderer::createBodyLayer);
        EntityRendererRegistry.register(FoundationEntities.CHAIR, ChairRenderer::new);
    }

    public static void registerBlockEntityRenderers() {
        BlockEntityRendererRegistry.register(FoundationBlockEntities.SINK.get(), SinkRenderer::new);
        BlockEntityRendererRegistry.register(FoundationBlockEntities.STACK.get(), StackRenderer::new);
    }

    /** Overlay + tooltip hooks. */
    public static void onInitializeClient() {
        BlockInfoOverlay.init();
        ArmorSetTooltips.init();
        FurnitureColors.init();
        InfoTooltip.init();
    }

    /** Hooks up all particle providers. */
    public static void registerParticles() {
        ParticleProviderRegistry.register(FoundationParticles.SOUP_BUBBLE.get(), SoupBubbleParticle.Provider::new);
        ParticleProviderRegistry.register(FoundationParticles.SOUP_STEAM.get(), SoupSteamParticle.Provider::new);
        ParticleProviderRegistry.register(FoundationParticles.COLORED_STEAM.get(), SoupSteamParticle.ColoredProvider::new);
        ParticleProviderRegistry.register(FoundationParticles.SOUP_COOKING_BUBBLE.get(), SoupCookingBubbleParticle.Provider::new);
        ParticleProviderRegistry.register(FoundationParticles.COLORED_SOUP_BUBBLE.get(), SoupBubbleParticle.ColoredProvider::new);
        ParticleProviderRegistry.register(FoundationParticles.COLORED_SOUP_COOKING_BUBBLE.get(), SoupCookingBubbleParticle.ColoredProvider::new);
        ParticleProviderRegistry.register(FoundationParticles.DYE_SPLASH.get(), DyeSplashParticle.Provider::new);
        ParticleProviderRegistry.register(FoundationParticles.FEATHER.get(), FeatherParticle.Provider::new);
        ParticleProviderRegistry.register(FoundationParticles.WATER_DRIP.get(), WaterDripParticle.Provider::new);
        ParticleProviderRegistry.register(FoundationParticles.WATER_SPLASH.get(), WaterSplashParticle.Provider::new);
        ParticleProviderRegistry.register(FoundationParticles.FIREFLY.get(), FireflyParticle.Provider::new);
    }
}
