package net.satisfy.foundation.flammable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class FoundationFlammables {
    private static final List<Entry> ENTRIES = new ArrayList<>();

    private FoundationFlammables() {
    }

    public static void register(int igniteOdds, int burnOdds, Supplier<? extends Block> block) {
        synchronized (ENTRIES) {
            ENTRIES.add(new Entry(block, igniteOdds, burnOdds));
        }
    }

    @SafeVarargs
    public static void register(int igniteOdds, int burnOdds, Supplier<? extends Block>... blocks) {
        for (Supplier<? extends Block> block : blocks) {
            register(igniteOdds, burnOdds, block);
        }
    }

    @SafeVarargs
    public static void wood(Supplier<? extends Block>... blocks) {
        register(5, 20, blocks);
    }

    @SafeVarargs
    public static void wool(Supplier<? extends Block>... blocks) {
        register(30, 60, blocks);
    }

    @SafeVarargs
    public static void hay(Supplier<? extends Block>... blocks) {
        register(60, 20, blocks);
    }

    @SafeVarargs
    public static void plant(Supplier<? extends Block>... blocks) {
        register(60, 100, blocks);
    }

    public static void apply() {
        FireBlock fire = (FireBlock) Blocks.FIRE;
        synchronized (ENTRIES) {
            for (Entry entry : ENTRIES) {
                fire.setFlammable(entry.block().get(), entry.igniteOdds(), entry.burnOdds());
            }
        }
    }

    private record Entry(Supplier<? extends Block> block, int igniteOdds, int burnOdds) {
    }
}
