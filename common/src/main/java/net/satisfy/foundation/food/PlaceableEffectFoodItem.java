package net.satisfy.foundation.food;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Effect food that can also be placed as block when sneaking. */
@SuppressWarnings("unused")
public class PlaceableEffectFoodItem extends BlockItem {
    public PlaceableEffectFoodItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
        EffectTooltips.append(itemStack, tooltipContext, tooltip);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        return user.eat(level, stack);
    }

    @Override
    public @NotNull InteractionResult place(BlockPlaceContext context) {
        Player player = context.getPlayer();
        if (player != null && player.isShiftKeyDown()) {
            return super.place(context);
        }
        return InteractionResult.PASS;
    }
}
