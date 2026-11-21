package net.satisfy.foundation.client;

import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.satisfy.foundation.block.DyeableSofaBlock;

import java.util.ArrayList;
import java.util.List;

public final class FurnitureColors {
    private FurnitureColors() {
    }

    public static void init() {
        List<Block> sofas = new ArrayList<>();
        for (Block block : BuiltInRegistries.BLOCK) {
            if (block instanceof DyeableSofaBlock) sofas.add(block);
        }
        if (!sofas.isEmpty()) {
            ColorHandlerRegistry.registerBlockColors((state, level, pos, tintIndex) -> state.getValue(DyeableSofaBlock.COLOR).tint(), sofas.toArray(new Block[0]));
        }
    }
}
