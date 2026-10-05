package net.satisfy.foundation.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.foundation.Foundation;
import net.satisfy.foundation.block.SinkBlock;
import net.satisfy.foundation.block.SinkBlockEntity;

import java.util.Set;

public final class FoundationBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Foundation.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<SinkBlockEntity>> SINK = BLOCK_ENTITY_TYPES.register("sink", () -> new BlockEntityType<>(SinkBlockEntity::new, Set.of(), null) {
        @Override
        public boolean isValid(BlockState state) {
            return state.getBlock() instanceof SinkBlock;
        }
    });

    private FoundationBlockEntities() {
    }

    public static void init() {
        BLOCK_ENTITY_TYPES.register();
    }
}
