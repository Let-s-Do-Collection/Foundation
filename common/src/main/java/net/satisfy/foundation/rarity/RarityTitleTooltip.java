package net.satisfy.foundation.rarity;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

public final class RarityTitleTooltip implements ClientTooltipComponent {
    private static final int HEIGHT = 20;
    private static final int ICON_SIZE = 16;
    private static final int GAP = 4;
    private static final int ICON_Y = 1;
    private static final int TEXT_Y = 5;
    private final ItemStack stack;
    private final ClientTooltipComponent title;

    public RarityTitleTooltip(ItemStack stack, ClientTooltipComponent title) {
        this.stack = stack;
        this.title = title;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public int getWidth(Font font) {
        return ICON_SIZE + GAP + this.title.getWidth(font);
    }

    @Override
    public void renderText(Font font, int x, int y, Matrix4f matrix, MultiBufferSource.BufferSource bufferSource) {
        this.title.renderText(font, x + ICON_SIZE + GAP, y + TEXT_Y, matrix, bufferSource);
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        graphics.renderItem(this.stack, x, y + ICON_Y);
    }
}
