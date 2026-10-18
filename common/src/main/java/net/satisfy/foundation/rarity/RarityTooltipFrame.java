package net.satisfy.foundation.rarity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.satisfy.foundation.mixin.ClientTextTooltipAccessor;
import net.satisfy.foundation.platform.PlatformHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public final class RarityTooltipFrame {
    private static final long TIMEOUT = 100L;
    private static final int PADDING = 12;
    @Nullable
    private static FoundationRarity rarity;
    private static ItemStack stack = ItemStack.EMPTY;
    private static long markedAt;

    private RarityTooltipFrame() {
    }

    public static void mark(@Nullable FoundationRarity next, ItemStack hovered) {
        rarity = next;
        stack = hovered;
        markedAt = System.currentTimeMillis();
    }

    public static List<ClientTooltipComponent> decorate(List<ClientTooltipComponent> components) {
        if (rarity == null || components.isEmpty() || stack.isEmpty() || !(components.getFirst() instanceof ClientTextTooltip) || sprite() == null) {
            return components;
        }
        Font font = Minecraft.getInstance().font;
        String footer = text(components.getLast());
        List<ClientTooltipComponent> lines = new ArrayList<>();
        lines.add(new RarityTitleTooltip(stack, components.getFirst()));
        for (int i = 1; i < components.size(); i++) {
            ClientTooltipComponent component = components.get(i);
            if (i < components.size() - 1 && !footer.isEmpty() && footer.equals(text(component))) {
                continue;
            }
            if (component instanceof ClientTextTooltip && component.getWidth(font) == 0) {
                if (lines.size() > 1 && !(lines.getLast() instanceof RaritySpacerTooltip)) {
                    lines.add(new RaritySpacerTooltip());
                }
                continue;
            }
            lines.add(component);
        }
        if (lines.getLast() instanceof RaritySpacerTooltip) {
            lines.removeLast();
        }
        return lines;
    }

    private static String text(ClientTooltipComponent component) {
        if (!(component instanceof ClientTextTooltipAccessor accessor)) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        accessor.foundation$getText().accept((index, style, codePoint) -> {
            builder.appendCodePoint(codePoint);
            return true;
        });
        return builder.toString().strip();
    }

    public static void keepOnScreen(int screenWidth, int screenHeight, Vector2i pos, int width, int height) {
        if (sprite() == null) {
            return;
        }
        if (pos.x + width + PADDING > screenWidth) {
            pos.x = Math.max(screenWidth - width - PADDING, PADDING);
        }
        if (pos.y + height + PADDING > screenHeight) {
            pos.y = screenHeight - height - PADDING;
        }
        pos.x = Math.max(pos.x, PADDING);
        pos.y = Math.max(pos.y, PADDING);
    }

    public static boolean render(GuiGraphics graphics, int x, int y, int width, int height, int z) {
        ResourceLocation location = sprite();
        rarity = null;
        if (location == null) {
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

    @Nullable
    private static ResourceLocation sprite() {
        FoundationRarity current = rarity;
        if (current == null || System.currentTimeMillis() - markedAt >= TIMEOUT || !PlatformHelper.enableImprovedTooltips()) {
            return null;
        }
        ResourceLocation location = current.tooltipSprite();
        if (Minecraft.getInstance().getGuiSprites().getSprite(location).contents().name().equals(MissingTextureAtlasSprite.getLocation())) {
            return null;
        }
        return location;
    }
}
