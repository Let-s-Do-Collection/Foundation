package net.satisfy.foundation.mixin;

import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.satisfy.foundation.rarity.RarityTooltipFrame;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DefaultTooltipPositioner.class)
public abstract class DefaultTooltipPositionerMixin {
    @Inject(method = "positionTooltip(IILorg/joml/Vector2i;II)V", at = @At("TAIL"), require = 0)
    private void foundation$keepRarityFrameOnScreen(int screenWidth, int screenHeight, Vector2i pos, int width, int height, CallbackInfo ci) {
        RarityTooltipFrame.keepOnScreen(screenWidth, screenHeight, pos, width, height);
    }
}
