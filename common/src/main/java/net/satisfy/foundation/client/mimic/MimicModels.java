package net.satisfy.foundation.client.mimic;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class MimicModels {
    public static final QuadRule ALL = (quad, state) -> Mode.REPLACE;

    private static final List<Entry> RETEXTURED = new ArrayList<>();
    private static final List<Supplier<? extends Block>> HELD = new ArrayList<>();
    private static @Nullable Map<Block, QuadRule> retexturedCache;
    private static @Nullable Map<Block, Boolean> heldCache;

    private MimicModels() {
    }

    public enum Mode {
        KEEP,
        REPLACE,
        UNDERLAY
    }

    @FunctionalInterface
    public interface QuadRule {
        Mode apply(BakedQuad quad, BlockState state);
    }

    public static QuadRule tintIndex(int tintIndex) {
        return (quad, state) -> quad.getTintIndex() == tintIndex ? Mode.REPLACE : Mode.KEEP;
    }

    public static QuadRule placeholder(Mode others) {
        return (quad, state) -> quad.getSprite().contents().name().getPath().contains("placeholder") ? Mode.REPLACE : others;
    }

    @SafeVarargs
    public static synchronized void retexture(QuadRule rule, Supplier<? extends Block>... blocks) {
        for (Supplier<? extends Block> block : blocks) {
            RETEXTURED.add(new Entry(block, rule));
        }
        retexturedCache = null;
    }

    @SafeVarargs
    public static synchronized void held(Supplier<? extends Block>... blocks) {
        HELD.addAll(List.of(blocks));
        heldCache = null;
    }

    public static synchronized @Nullable QuadRule retextureRule(Block block) {
        if (retexturedCache == null) {
            Map<Block, QuadRule> map = new IdentityHashMap<>();
            for (Entry entry : RETEXTURED) {
                map.put(entry.block().get(), entry.rule());
            }
            retexturedCache = map;
        }
        return retexturedCache.get(block);
    }

    public static synchronized boolean isHeld(Block block) {
        if (heldCache == null) {
            Map<Block, Boolean> map = new IdentityHashMap<>();
            for (Supplier<? extends Block> supplier : HELD) {
                map.put(supplier.get(), true);
            }
            heldCache = map;
        }
        return heldCache.containsKey(block);
    }

    public static boolean isMimicModel(Block block) {
        return retextureRule(block) != null || isHeld(block);
    }

    private record Entry(Supplier<? extends Block> block, QuadRule rule) {
    }
}
