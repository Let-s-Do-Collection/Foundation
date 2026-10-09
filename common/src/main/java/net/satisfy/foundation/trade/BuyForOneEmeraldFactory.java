package net.satisfy.foundation.trade;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;

public class BuyForOneEmeraldFactory implements VillagerTrades.ItemListing {
    private static final float DEFAULT_MULTIPLIER = 0.05F;

    private final Item buy;
    private final int price;
    private final int maxUses;
    private final int experience;
    private final float multiplier;

    public BuyForOneEmeraldFactory(ItemLike item, int price, int maxUses, int experience) {
        this(item, price, maxUses, experience, DEFAULT_MULTIPLIER);
    }

    public BuyForOneEmeraldFactory(ItemLike item, int price, int maxUses, int experience, float multiplier) {
        this.buy = item.asItem();
        this.price = price;
        this.maxUses = maxUses;
        this.experience = experience;
        this.multiplier = multiplier;
    }

    @Override
    public MerchantOffer getOffer(Entity entity, RandomSource random) {
        return new MerchantOffer(new ItemCost(buy, price), new ItemStack(Items.EMERALD), maxUses, experience, multiplier);
    }
}
