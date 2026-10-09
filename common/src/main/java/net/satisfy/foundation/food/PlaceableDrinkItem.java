package net.satisfy.foundation.food;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.foundation.storage.StorageBlockEntity;
import net.satisfy.foundation.util.LibUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class PlaceableDrinkItem extends BlockItem {
    private static final int DRINK_DURATION = 32;

    private boolean crouchToPlace;
    private boolean drinkAnytime;
    private @Nullable Supplier<? extends ItemLike> container;

    public PlaceableDrinkItem(Block block, Properties properties) {
        super(block, properties);
    }

    public PlaceableDrinkItem crouchToPlace() {
        crouchToPlace = true;
        return this;
    }

    public PlaceableDrinkItem drinkAnytime() {
        drinkAnytime = true;
        return this;
    }

    public PlaceableDrinkItem container(Supplier<? extends ItemLike> container) {
        this.container = container;
        return this;
    }

    protected @Nullable Item containerFor(ItemStack stack) {
        return container == null ? null : container.get().asItem();
    }

    protected void applyEffects(ItemStack stack, Level level, LivingEntity entity) {
        stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).forEachEffect(effect -> entity.addEffect(new MobEffectInstance(effect)));
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return stack.has(DataComponents.FOOD) ? super.getUseDuration(stack, entity) : DRINK_DURATION;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (drinkAnytime || !player.getItemInHand(hand).has(DataComponents.FOOD)) {
            return ItemUtils.startUsingInstantly(level, player, hand);
        }
        return super.use(level, player, hand);
    }

    @Override
    protected @Nullable BlockState getPlacementState(BlockPlaceContext context) {
        if (crouchToPlace && (context.getPlayer() == null || !context.getPlayer().isCrouching())) {
            return null;
        }
        return super.getPlacementState(context);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack stack, BlockState state) {
        if (level.getBlockEntity(pos) instanceof StorageBlockEntity storage) {
            storage.setStack(0, stack.copyWithCount(1));
        }
        return super.updateCustomBlockEntityTag(pos, level, player, stack, state);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        Item returned = containerFor(stack);
        if (!level.isClientSide) {
            applyEffects(stack, level, entity);
        }
        ItemStack result;
        if (stack.has(DataComponents.FOOD)) {
            result = super.finishUsingItem(stack, level, entity);
        } else {
            stack.consume(1, entity);
            result = stack;
        }
        return returned == null ? result : LibUtil.convertStackAfterFinishUsing(entity, result, returned, this);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        EffectTooltips.append(stack, context, tooltip);
    }
}
