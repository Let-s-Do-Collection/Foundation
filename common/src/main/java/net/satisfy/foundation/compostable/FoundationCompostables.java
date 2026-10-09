package net.satisfy.foundation.compostable;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class FoundationCompostables {
    private static final List<Entry> ENTRIES = new ArrayList<>();
    private static final Map<Item, Float> CHANCES = new IdentityHashMap<>();
    private static boolean applied;

    private FoundationCompostables() {
    }

    public static void register(float chance, Supplier<? extends ItemLike> item) {
        synchronized (ENTRIES) {
            Entry entry = new Entry(item, chance);
            ENTRIES.add(entry);
            if (applied) {
                put(entry);
            }
        }
    }

    @SafeVarargs
    public static void register(float chance, Supplier<? extends ItemLike>... items) {
        for (Supplier<? extends ItemLike> item : items) {
            register(chance, item);
        }
    }

    @SafeVarargs
    public static void low(Supplier<? extends ItemLike>... items) {
        register(0.3F, items);
    }

    @SafeVarargs
    public static void medium(Supplier<? extends ItemLike>... items) {
        register(0.5F, items);
    }

    @SafeVarargs
    public static void high(Supplier<? extends ItemLike>... items) {
        register(0.65F, items);
    }

    @SafeVarargs
    public static void veryHigh(Supplier<? extends ItemLike>... items) {
        register(0.85F, items);
    }

    @SafeVarargs
    public static void full(Supplier<? extends ItemLike>... items) {
        register(1.0F, items);
    }

    public static void apply() {
        synchronized (ENTRIES) {
            applied = true;
            for (Entry entry : ENTRIES) {
                put(entry);
            }
        }
    }

    private static void put(Entry entry) {
        Item item = entry.item().get().asItem();
        if (item == Items.AIR) return;
        CHANCES.put(item, entry.chance());
        ComposterBlock.COMPOSTABLES.put(item, entry.chance());
    }

    public static float chance(Item item) {
        Float chance = CHANCES.get(item);
        return chance == null ? -1.0F : chance;
    }

    private record Entry(Supplier<? extends ItemLike> item, float chance) {
    }
}
