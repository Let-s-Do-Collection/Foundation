package net.satisfy.foundation.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.satisfy.foundation.rarity.RarityTooltipFrame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {
    @ModifyVariable(method = "renderTooltipInternal", at = @At("HEAD"), argsOnly = true, require = 0)
    private List<ClientTooltipComponent> foundation$rarityTooltipLayout(List<ClientTooltipComponent> components) {
        return RarityTooltipFrame.decorate(components);
    }
}
