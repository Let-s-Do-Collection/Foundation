package net.satisfy.foundation.overlay;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

import java.util.List;

public interface EntityInfoProvider {
    List<InfoSection> describe(Level level, Entity entity, EntityHitResult hit);

    default void beforeBackground(Level level, Entity entity) {
    }
}
