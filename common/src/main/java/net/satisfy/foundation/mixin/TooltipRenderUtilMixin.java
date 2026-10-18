package net.satisfy.foundation.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.satisfy.foundation.rarity.RarityTooltipFrame;
import net.satisfy.foundation.tooltip.TooltipBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Swaps the tooltip border colors when {@link TooltipBorder} was marked just before. */
@Mixin(TooltipRenderUtil.class)
public abstract class TooltipRenderUtilMixin {
    @Inject(method = "renderTooltipBackground(Lnet/minecraft/client/gui/GuiGraphics;IIIII)V", at = @At("HEAD"), cancellable = true, require = 0)
    private static void foundation$rarityFrame(GuiGraphics graphics, int x, int y, int width, int height, int z, CallbackInfo ci) {
        if (RarityTooltipFrame.render(graphics, x, y, width, height, z)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderTooltipBackground(Lnet/minecraft/client/gui/GuiGraphics;IIIIIIIII)V", at = @At("HEAD"), cancellable = true, require = 0)
    private static void foundation$rarityFrameColored(GuiGraphics graphics, int x, int y, int width, int height, int z, int backgroundTop, int backgroundBottom, int borderTop, int borderBottom, CallbackInfo ci) {
        if (RarityTooltipFrame.render(graphics, x, y, width, height, z)) {
            ci.cancel();
        }
    }

    @ModifyVariable(method = "renderFrameGradient", at = @At("HEAD"), argsOnly = true, ordinal = 5, require = 0)
    private static int foundation$borderTop(int color) {
        return TooltipBorder.top(color);
    }

    @ModifyVariable(method = "renderFrameGradient", at = @At("HEAD"), argsOnly = true, ordinal = 6, require = 0)
    private static int foundation$borderBottom(int color) {
        return TooltipBorder.bottom(color);
    }
}
