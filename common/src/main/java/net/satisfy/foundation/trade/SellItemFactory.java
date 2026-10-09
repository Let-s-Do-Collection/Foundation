package net.satisfy.foundation.trade;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;

public class SellItemFactory implements VillagerTrades.ItemListing {
    private static final int DEFAULT_MAX_USES = 12;
    private static final float DEFAULT_MULTIPLIER = 0.05F;

    private final ItemStack sell;
    private final int price;
    private final int count;
    private final int maxUses;
    private final int experience;
    private final float multiplier;

    public SellItemFactory(ItemLike item, int price, int count, int experience) {
        this(new ItemStack(item), price, count, DEFAULT_MAX_USES, experience);
    }

    public SellItemFactory(ItemLike item, int price, int count, int maxUses, int experience) {
        this(new ItemStack(item), price, count, maxUses, experience);
    }

    public SellItemFactory(ItemStack stack, int price, int count, int maxUses, int experience) {
        this(stack, price, count, maxUses, experience, DEFAULT_MULTIPLIER);
    }

    public SellItemFactory(ItemStack stack, int price, int count, int maxUses, int experience, float multiplier) {
        this.sell = stack;
        this.price = price;
        this.count = count;
        this.maxUses = maxUses;
        this.experience = experience;
        this.multiplier = multiplier;
    }

    @Override
    public MerchantOffer getOffer(Entity entity, RandomSource random) {
        return new MerchantOffer(new ItemCost(Items.EMERALD, price), sell.copyWithCount(count), maxUses, experience, multiplier);
    }
}
