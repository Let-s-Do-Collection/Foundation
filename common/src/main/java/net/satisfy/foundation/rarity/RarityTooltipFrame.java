package net.satisfy.foundation.rarity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public final class RarityTooltipFrame {
    private static final long TIMEOUT = 100L;
    private static final int PADDING = 12;
    @Nullable
    private static FoundationRarity rarity;
    private static long markedAt;

    private RarityTooltipFrame() {
    }

    public static void mark(@Nullable FoundationRarity next) {
        rarity = next;
        markedAt = System.currentTimeMillis();
    }

    public static boolean render(GuiGraphics graphics, int x, int y, int width, int height, int z) {
        FoundationRarity current = rarity;
        rarity = null;
        if (current == null || System.currentTimeMillis() - markedAt >= TIMEOUT) {
            return false;
        }
        ResourceLocation location = current.tooltipSprite();
        TextureAtlasSprite sprite = Minecraft.getInstance().getGuiSprites().getSprite(location);
        if (sprite.contents().name().equals(MissingTextureAtlasSprite.getLocation())) {
            return false;
        }
        graphics.pose().pushPose();
        graphics.pose().translate(0.0F, 0.0F, z);
        RenderSystem.enableBlend();
        graphics.blitSprite(location, x - PADDING, y - PADDING, width + PADDING * 2, height + PADDING * 2);
        RenderSystem.disableBlend();
        graphics.pose().popPose();
        return true;
    }
}
