package net.satisfy.foundation.entity.equipment;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public final class ZombieEquipment {
    private static final List<Outfit> OUTFITS = new ArrayList<>();

    private ZombieEquipment() {
    }

    public static Outfit add(DoubleSupplier chance) {
        Outfit outfit = new Outfit(chance);
        synchronized (OUTFITS) {
            OUTFITS.add(outfit);
        }
        return outfit;
    }

    public static Outfit add(double chance) {
        return add(() -> chance);
    }

    public static void equip(Zombie zombie, RandomSource random) {
        synchronized (OUTFITS) {
            for (Outfit outfit : OUTFITS) {
                outfit.tryEquip(zombie, random);
            }
        }
    }

    public static final class Outfit {
        private final DoubleSupplier chance;
        private final Map<EquipmentSlot, Supplier<? extends ItemLike>> items = new EnumMap<>(EquipmentSlot.class);
        private boolean adultsOnly;

        private Outfit(DoubleSupplier chance) {
            this.chance = chance;
        }

        public Outfit slot(EquipmentSlot slot, Supplier<? extends ItemLike> item) {
            items.put(slot, item);
            return this;
        }

        public Outfit adultsOnly() {
            adultsOnly = true;
            return this;
        }

        private void tryEquip(Zombie zombie, RandomSource random) {
            if (adultsOnly && zombie.isBaby()) return;
            if (random.nextDouble() >= chance.getAsDouble()) return;
            items.forEach((slot, item) -> zombie.setItemSlot(slot, new ItemStack(item.get())));
        }
    }
}
