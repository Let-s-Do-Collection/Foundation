package net.satisfy.foundation.food;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.ArrayList;
import java.util.List;

public final class EffectTooltips {
    private EffectTooltips() {
    }

    public static List<MobEffectInstance> effects(ItemStack stack) {
        List<MobEffectInstance> effects = new ArrayList<>();
        stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).forEachEffect(effects::add);
        FoodProperties food = stack.get(DataComponents.FOOD);
        if (food != null) {
            for (FoodProperties.PossibleEffect possibleEffect : food.effects()) {
                effects.add(possibleEffect.effect());
            }
        }
        return effects;
    }

    public static void append(ItemStack stack, Item.TooltipContext context, List<Component> tooltip) {
        PotionContents.addPotionTooltip(effects(stack), tooltip::add, 1.0F, context.tickRate());
    }
}
