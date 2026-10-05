package net.satisfy.foundation.fabric.datagen;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.stream.IntStream;

public record ModelGenHelper(String namespace) {
    public ResourceLocation blockId(String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, "block/" + path);
    }

    public ResourceLocation itemId(String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, "item/" + path);
    }

    public List<ResourceLocation> variantIds(String pattern, int amount) {
        return IntStream.range(0, amount).mapToObj(i -> blockId(String.format(pattern, i))).toList();
    }

    public static List<Variant> variants(List<ResourceLocation> models) {
        return models.stream().map(id -> Variant.variant().with(VariantProperties.MODEL, id)).toList();
    }

    public static Variant[] variantArray(List<ResourceLocation> models) {
        return variants(models).toArray(Variant[]::new);
    }

    public static void slabFromTexture(BlockModelGenerators generators, Block slab, ResourceLocation texture) {
        TextureMapping mapping = TextureMapping.cube(texture);
        ResourceLocation bottom = ModelTemplates.SLAB_BOTTOM.create(ModelLocationUtils.getModelLocation(slab, "_bottom"), mapping, generators.modelOutput);
        ResourceLocation top = ModelTemplates.SLAB_TOP.create(ModelLocationUtils.getModelLocation(slab, "_top"), mapping, generators.modelOutput);
        generators.blockStateOutput.accept(BlockModelGenerators.createSlab(slab, bottom, top, texture));
        generators.delegateItemModel(slab.asItem(), bottom);
    }
}
