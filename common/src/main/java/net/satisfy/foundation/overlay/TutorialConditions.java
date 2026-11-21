package net.satisfy.foundation.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.function.Predicate;
import java.util.function.ToIntFunction;

public final class TutorialConditions {
    private TutorialConditions() {
    }

    public static Predicate<Minecraft> always() {
        return minecraft -> true;
    }

    public static Predicate<Minecraft> lookingAt(Block block) {
        return lookingAt(state -> state.is(block));
    }

    public static Predicate<Minecraft> lookingAt(TagKey<Block> tag) {
        return lookingAt(state -> state.is(tag));
    }

    public static Predicate<Minecraft> lookingAt(Predicate<BlockState> test) {
        return minecraft -> minecraft.level != null && minecraft.hitResult instanceof BlockHitResult hit && hit.getType() == HitResult.Type.BLOCK
                && test.test(minecraft.level.getBlockState(hit.getBlockPos()));
    }

    public static Predicate<Minecraft> holding(Item item) {
        return holding(stack -> stack.is(item));
    }

    public static Predicate<Minecraft> holding(TagKey<Item> tag) {
        return holding(stack -> stack.is(tag));
    }

    public static Predicate<Minecraft> holding(Predicate<ItemStack> test) {
        return minecraft -> minecraft.player != null && (test.test(minecraft.player.getMainHandItem()) || test.test(minecraft.player.getOffhandItem()));
    }

    public static Predicate<Minecraft> inBiome(ResourceKey<Biome> biome) {
        return inBiome(holder -> holder.is(biome));
    }

    public static Predicate<Minecraft> inBiome(TagKey<Biome> tag) {
        return inBiome(holder -> holder.is(tag));
    }

    private static Predicate<Minecraft> inBiome(Predicate<Holder<Biome>> test) {
        return minecraft -> minecraft.level != null && minecraft.player != null && test.test(minecraft.level.getBiome(minecraft.player.blockPosition()));
    }

    public static ToIntFunction<Minecraft> itemCount(Item item) {
        return itemCount(stack -> stack.is(item));
    }

    public static ToIntFunction<Minecraft> itemCount(TagKey<Item> tag) {
        return itemCount(stack -> stack.is(tag));
    }

    public static ToIntFunction<Minecraft> itemCount(Predicate<ItemStack> test) {
        return minecraft -> {
            if (minecraft.player == null) {
                return 0;
            }
            Inventory inventory = minecraft.player.getInventory();
            int count = 0;
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stack = inventory.getItem(i);
                if (!stack.isEmpty() && test.test(stack)) {
                    count += stack.getCount();
                }
            }
            return count;
        };
    }

    static Predicate<ItemStack> itemTest(String value) {
        if (value.startsWith("#")) {
            TagKey<Item> tag = TagKey.create(Registries.ITEM, ResourceLocation.parse(value.substring(1)));
            return stack -> stack.is(tag);
        }
        ResourceLocation id = ResourceLocation.parse(value);
        if (!BuiltInRegistries.ITEM.containsKey(id)) {
            throw new IllegalArgumentException("unknown item \"" + value + "\"");
        }
        Item item = BuiltInRegistries.ITEM.get(id);
        return stack -> stack.is(item);
    }

    static Predicate<BlockState> blockTest(String value) {
        if (value.startsWith("#")) {
            TagKey<Block> tag = TagKey.create(Registries.BLOCK, ResourceLocation.parse(value.substring(1)));
            return state -> state.is(tag);
        }
        ResourceLocation id = ResourceLocation.parse(value);
        if (!BuiltInRegistries.BLOCK.containsKey(id)) {
            throw new IllegalArgumentException("unknown block \"" + value + "\"");
        }
        Block block = BuiltInRegistries.BLOCK.get(id);
        return state -> state.is(block);
    }

    static Predicate<Minecraft> biomeTest(String value) {
        if (value.startsWith("#")) {
            return inBiome(TagKey.create(Registries.BIOME, ResourceLocation.parse(value.substring(1))));
        }
        return inBiome(ResourceKey.create(Registries.BIOME, ResourceLocation.parse(value)));
    }
}
