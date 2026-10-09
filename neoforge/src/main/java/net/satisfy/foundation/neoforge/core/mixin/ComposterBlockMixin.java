package net.satisfy.foundation.neoforge.core.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ComposterBlock;
import net.satisfy.foundation.compostable.FoundationCompostables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ComposterBlock.class)
public abstract class ComposterBlockMixin {
    @Inject(method = "getValue", at = @At("RETURN"), cancellable = true, remap = false)
    private static void foundation$fallbackChance(ItemStack stack, CallbackInfoReturnable<Float> cir) {
        if (cir.getReturnValueF() <= 0.0F) {
            float chance = FoundationCompostables.chance(stack.getItem());
            if (chance > 0.0F) {
                cir.setReturnValue(chance);
            }
        }
    }
}
