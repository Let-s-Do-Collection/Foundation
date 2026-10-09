package net.satisfy.foundation.food;

import net.satisfy.foundation.util.LibUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Drink item that shows its food effects in the tooltip like a potion does.
 *
 * Note: {@code duration} in the constructor is not used anymore, kept for compat.
 */
@SuppressWarnings("unused")
public class EffectDrinkItem extends Item {
    private final boolean returnBottle;

    public EffectDrinkItem(Properties properties, int duration, boolean returnBottle) {
        super(properties);
        this.returnBottle = returnBottle;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        ItemStack eaten = livingEntity.eat(level, itemStack);
        if (this.returnBottle) {
            return LibUtil.convertStackAfterFinishUsing(livingEntity, eaten, Items.GLASS_BOTTLE, this);
        }
        return eaten;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, tooltipContext, tooltip, tooltipFlag);
        EffectTooltips.append(itemStack, tooltipContext, tooltip);
    }
}
