package net.satisfy.foundation.food;

import net.satisfy.foundation.util.LibUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Food item with potion-like effect tooltip, optionally gives the bowl back.
 *
 * Note: {@code duration} is unused, same as in {@link EffectDrinkItem}.
 */
@SuppressWarnings("unused")
public class EffectFoodItem extends Item {
    private final boolean returnBowl;

    public EffectFoodItem(Properties properties, int duration, boolean returnBowl) {
        super(properties);
        this.returnBowl = returnBowl;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
        EffectTooltips.append(itemStack, tooltipContext, tooltip);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        ItemStack eaten = livingEntity.eat(level, itemStack);
        if (this.returnBowl) {
            return LibUtil.convertStackAfterFinishUsing(livingEntity, eaten, Items.BOWL, this);
        }
        return eaten;
    }
}
