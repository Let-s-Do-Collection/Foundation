package net.satisfy.foundation.client.fog;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.FogType;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shared terrain fog for all mods. Every mod registers a {@link Source}, Foundation picks the densest active fog
 * and blends it in and out smoothly, so several mods never overwrite each other.
 */
public final class BiomeFog {
    private static final Map<ResourceLocation, Source> SOURCES = new LinkedHashMap<>();
    private static final float FADE_STEP = 1.0F / 60.0F;
    private static final float FOLLOW_SPEED = 0.1F;

    private static boolean initialized;
    private static float factor;
    private static float previousFactor;
    private static float start;
    private static float end;
    private static float previousStart;
    private static float previousEnd;

    private BiomeFog() {
    }

    /** Fog distances in blocks. */
    public record Fog(float start, float end) {
    }

    @FunctionalInterface
    public interface Source {
        /** @return the fog this source wants right now, or {@code null} for none */
        @Nullable
        Fog get(ClientLevel level, BlockPos cameraPos, float renderDistance);
    }

    /** Adds or replaces a fog source. */
    public static void register(ResourceLocation id, Source source) {
        SOURCES.put(id, source);
        if (!initialized) {
            initialized = true;
            ClientTickEvent.CLIENT_POST.register(BiomeFog::tick);
        }
    }

    private static void tick(Minecraft minecraft) {
        previousFactor = factor;
        previousStart = start;
        previousEnd = end;
        ClientLevel level = minecraft.level;
        if (level == null || minecraft.player == null) {
            factor = 0.0F;
            return;
        }
        float renderDistance = minecraft.gameRenderer.getRenderDistance();
        BlockPos cameraPos = minecraft.gameRenderer.getMainCamera().getBlockPosition();
        Fog target = null;
        for (Source source : SOURCES.values()) {
            Fog fog = source.get(level, cameraPos, renderDistance);
            if (fog != null && (target == null || fog.end() < target.end())) {
                target = fog;
            }
        }
        if (target == null) {
            factor = Math.max(0.0F, factor - FADE_STEP);
            return;
        }
        if (factor <= 0.0F) {
            start = previousStart = target.start();
            end = previousEnd = target.end();
        } else {
            start = Mth.lerp(FOLLOW_SPEED, start, target.start());
            end = Mth.lerp(FOLLOW_SPEED, end, target.end());
        }
        factor = Math.min(1.0F, factor + FADE_STEP);
    }

    /** Called at the end of {@link FogRenderer#setupFog}. */
    public static void apply(Camera camera, FogRenderer.FogMode mode, float partialTick) {
        if (mode != FogRenderer.FogMode.FOG_TERRAIN || camera.getFluidInCamera() != FogType.NONE) {
            return;
        }
        float blend = Mth.lerp(partialTick, previousFactor, factor);
        if (blend <= 0.0F) {
            return;
        }
        blend = blend * blend * (3.0F - 2.0F * blend);
        float fogStart = Mth.lerp(partialTick, previousStart, start);
        float fogEnd = Mth.lerp(partialTick, previousEnd, end);
        RenderSystem.setShaderFogStart(Mth.lerp(blend, RenderSystem.getShaderFogStart(), Math.min(fogStart, RenderSystem.getShaderFogStart())));
        RenderSystem.setShaderFogEnd(Mth.lerp(blend, RenderSystem.getShaderFogEnd(), Math.min(fogEnd, RenderSystem.getShaderFogEnd())));
    }
}
