package net.satisfy.foundation.client.mimic;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.foundation.block.mimic.MimicBlock;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class MimicQuads {
    private static final int STRIDE = 8;
    private static final int UV = 4;

    private MimicQuads() {
    }

    public static boolean isApplied(BlockState state) {
        return !state.hasProperty(MimicBlock.APPLIED) || state.getValue(MimicBlock.APPLIED);
    }

    public static boolean isUsable(@Nullable BlockState mimic) {
        return mimic != null && !mimic.isAir() && mimic.getRenderShape() == RenderShape.MODEL && !MimicModels.isMimicModel(mimic.getBlock());
    }

    public static @Nullable TextureAtlasSprite sprite(@Nullable BlockState mimic) {
        if (!isUsable(mimic)) return null;
        TextureAtlasSprite sprite = Minecraft.getInstance().getBlockRenderer().getBlockModel(mimic).getParticleIcon();
        return sprite == null || "missingno".equals(sprite.contents().name().getPath()) ? null : sprite;
    }

    public static List<BakedQuad> retexture(List<BakedQuad> quads, BlockState state, TextureAtlasSprite sprite, MimicModels.QuadRule rule) {
        if (quads.isEmpty()) return quads;
        List<BakedQuad> result = new ArrayList<>(quads.size());
        for (BakedQuad quad : quads) {
            switch (rule.apply(quad, state)) {
                case KEEP -> result.add(quad);
                case REPLACE -> result.add(remap(quad, sprite));
                case UNDERLAY -> {
                    result.add(remap(quad, sprite));
                    result.add(quad);
                }
            }
        }
        return result;
    }

    public static List<BakedQuad> held(@Nullable BlockState held, @Nullable Direction side, RandomSource random) {
        if (!isUsable(held)) return List.of();
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(held);
        return model.getQuads(held, side, random);
    }

    public static BakedQuad remap(BakedQuad quad, TextureAtlasSprite target) {
        TextureAtlasSprite source = quad.getSprite();
        if (source == target) return quad;
        int[] vertices = quad.getVertices().clone();
        float widthScale = target.contents().width() / (float) source.contents().width();
        float heightScale = target.contents().height() / (float) source.contents().height();
        for (int i = 0; i < 4; i++) {
            int offset = i * STRIDE + UV;
            float u = Float.intBitsToFloat(vertices[offset]);
            float v = Float.intBitsToFloat(vertices[offset + 1]);
            vertices[offset] = Float.floatToRawIntBits((u - source.getU0()) * widthScale + target.getU0());
            vertices[offset + 1] = Float.floatToRawIntBits((v - source.getV0()) * heightScale + target.getV0());
        }
        return new BakedQuad(vertices, quad.getTintIndex(), quad.getDirection(), target, quad.isShade());
    }
}
