package net.satisfy.foundation.neoforge.client.mimic;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.satisfy.foundation.client.mimic.MimicModels;
import net.satisfy.foundation.client.mimic.MimicQuads;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NeoForgeMimicModel extends BakedModelWrapper<BakedModel> {
    public static final ModelProperty<BlockState> MIMIC = new ModelProperty<>();

    public NeoForgeMimicModel(BakedModel original) {
        super(original);
    }

    public static void wrap(ModelEvent.ModifyBakingResult event) {
        Map<ModelResourceLocation, BakedModel> models = event.getModels();
        for (Map.Entry<ModelResourceLocation, BakedModel> entry : models.entrySet()) {
            ModelResourceLocation id = entry.getKey();
            if (entry.getValue() == null || ModelResourceLocation.INVENTORY_VARIANT.equals(id.variant())) continue;
            if (MimicModels.isMimicModel(BuiltInRegistries.BLOCK.get(id.id()))) {
                entry.setValue(new NeoForgeMimicModel(entry.getValue()));
            }
        }
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource random, @NotNull ModelData data, @Nullable RenderType renderType) {
        List<BakedQuad> quads = super.getQuads(state, side, random, data, renderType);
        if (state == null) return quads;
        BlockState mimic = data.get(MIMIC);
        MimicModels.QuadRule rule = MimicModels.retextureRule(state.getBlock());
        if (rule != null && MimicQuads.isApplied(state)) {
            TextureAtlasSprite sprite = MimicQuads.sprite(mimic);
            if (sprite != null) {
                quads = MimicQuads.retexture(quads, state, sprite, rule);
            }
        }
        if (MimicModels.isHeld(state.getBlock()) && MimicQuads.isUsable(mimic) && (renderType == null || heldModel(mimic).getRenderTypes(mimic, random, ModelData.EMPTY).contains(renderType))) {
            List<BakedQuad> held = MimicQuads.held(mimic, side, random);
            if (!held.isEmpty()) {
                List<BakedQuad> combined = new ArrayList<>(quads);
                combined.addAll(held);
                quads = combined;
            }
        }
        return quads;
    }

    @Override
    public @NotNull ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource random, @NotNull ModelData data) {
        ChunkRenderTypeSet types = super.getRenderTypes(state, random, data);
        BlockState mimic = data.get(MIMIC);
        if (MimicModels.isHeld(state.getBlock()) && MimicQuads.isUsable(mimic)) {
            return ChunkRenderTypeSet.union(types, heldModel(mimic).getRenderTypes(mimic, random, ModelData.EMPTY));
        }
        return types;
    }

    private static BakedModel heldModel(BlockState mimic) {
        return Minecraft.getInstance().getBlockRenderer().getBlockModel(mimic);
    }
}
