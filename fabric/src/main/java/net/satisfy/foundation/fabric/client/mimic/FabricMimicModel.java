package net.satisfy.foundation.fabric.client.mimic;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.foundation.block.mimic.MimicBlockEntity;
import net.satisfy.foundation.client.mimic.MimicModels;
import net.satisfy.foundation.client.mimic.MimicQuads;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class FabricMimicModel extends ForwardingBakedModel {
    private static final Direction[] SIDES = {Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, null};

    public FabricMimicModel(BakedModel wrapped) {
        this.wrapped = wrapped;
    }

    public static void register() {
        ModelLoadingPlugin.register(context -> context.modifyModelAfterBake().register((model, modelContext) -> {
            ModelResourceLocation id = modelContext.topLevelId();
            if (model == null || id == null || ModelResourceLocation.INVENTORY_VARIANT.equals(id.variant())) {
                return model;
            }
            return MimicModels.isMimicModel(BuiltInRegistries.BLOCK.get(id.id())) ? new FabricMimicModel(model) : model;
        }));
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter level, BlockState state, BlockPos pos, Supplier<RandomSource> random, RenderContext context) {
        BlockState mimic = level.getBlockEntity(pos) instanceof MimicBlockEntity entity ? entity.getMimicState() : null;
        MimicModels.QuadRule rule = MimicModels.retextureRule(state.getBlock());
        TextureAtlasSprite sprite = rule != null && MimicQuads.isApplied(state) ? MimicQuads.sprite(mimic) : null;
        boolean held = MimicModels.isHeld(state.getBlock());
        RenderMaterial material = context.getEmitter().material();
        for (Direction side : SIDES) {
            List<BakedQuad> quads = wrapped.getQuads(state, side, random.get());
            emit(context, material, side, sprite != null ? MimicQuads.retexture(quads, state, sprite, rule) : quads);
            if (held) {
                emit(context, material, side, MimicQuads.held(mimic, side, random.get()));
            }
        }
    }

    private static void emit(RenderContext context, RenderMaterial material, @Nullable Direction side, List<BakedQuad> quads) {
        for (BakedQuad quad : quads) {
            QuadEmitter emitter = context.getEmitter();
            emitter.fromVanilla(quad, material, side);
            emitter.emit();
        }
    }
}
