package net.satisfy.foundation.rarity;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public final class RaritySpacerTooltip implements ClientTooltipComponent {
    private static final int HEIGHT = 6;

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public int getWidth(Font font) {
        return 0;
    }
}
