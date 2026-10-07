package net.satisfy.foundation.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import net.satisfy.foundation.client.fog.BiomeFog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Hook for {@link BiomeFog}. */
@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
    @Inject(method = "setupFog", at = @At("TAIL"), require = 0)
    private static void foundation$biomeFog(Camera camera, FogRenderer.FogMode mode, float renderDistance, boolean thickFog, float partialTick, CallbackInfo callback) {
        BiomeFog.apply(camera, mode, partialTick);
    }
}
