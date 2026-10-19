package net.satisfy.foundation.client.creative;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.satisfy.foundation.rarity.FoundationRarity;
import net.satisfy.foundation.rarity.RarityTooltipFrame;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class CreativeTabTooltips {
    private static final Map<ResourceKey<CreativeModeTab>, FoundationRarity> RARITIES = new HashMap<>();

    private CreativeTabTooltips() {
    }

    public static void register(ResourceKey<CreativeModeTab> tab, FoundationRarity rarity) {
        RARITIES.put(tab, rarity);
    }

    public static boolean renderTab(GuiGraphics graphics, Font font, CreativeModeTab tab, int mouseX, int mouseY) {
        Optional<ResourceKey<CreativeModeTab>> key = BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(tab);
        FoundationRarity rarity = key.map(RARITIES::get).orElse(null);
        if (rarity == null) {
            return false;
        }
        Component modId = Component.literal("@" + key.get().location().getNamespace()).withStyle(style -> style.withColor(TextColor.fromRgb(rarity.baseColor())).withItalic(true));
        render(graphics, font, rarity, tab.getIconItem(), List.of(tab.getDisplayName(), modId), mouseX, mouseY);
        return true;
    }

    public static boolean renderSideTab(GuiGraphics graphics, Font font, CreativeModeTab tab, CreativeSideTabs.SideTab sideTab, int mouseX, int mouseY) {
        FoundationRarity rarity = rarity(tab);
        if (rarity == null) {
            return false;
        }
        render(graphics, font, rarity, sideTab.icon(), List.of(sideTab.title()), mouseX, mouseY);
        return true;
    }

    @Nullable
    private static FoundationRarity rarity(CreativeModeTab tab) {
        return BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(tab).map(RARITIES::get).orElse(null);
    }

    private static void render(GuiGraphics graphics, Font font, FoundationRarity rarity, ItemStack icon, List<Component> lines, int mouseX, int mouseY) {
        RarityTooltipFrame.mark(rarity, icon);
        graphics.renderTooltip(font, lines, Optional.empty(), mouseX, mouseY);
    }
}
