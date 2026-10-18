package net.satisfy.foundation.neoforge.core.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.satisfy.foundation.rarity.RarityTooltipFrame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TooltipRenderUtil.class)
public abstract class TooltipRenderUtilMixin {
    @Inject(method = "renderTooltipBackground(Lnet/minecraft/client/gui/GuiGraphics;IIIIIIIII)V", at = @At("HEAD"), cancellable = true, require = 0)
    private static void foundation$rarityFrameColored(GuiGraphics graphics, int x, int y, int width, int height, int z, int backgroundTop, int backgroundBottom, int borderTop, int borderBottom, CallbackInfo ci) {
        if (RarityTooltipFrame.render(graphics, x, y, width, height, z)) {
            ci.cancel();
        }
    }
}
