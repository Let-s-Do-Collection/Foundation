package net.satisfy.foundation.client.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.satisfy.foundation.Foundation;
import net.satisfy.foundation.block.SinkBlock;
import net.satisfy.foundation.block.SinkBlockEntity;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class SinkRenderer implements BlockEntityRenderer<SinkBlockEntity> {
    private static final ResourceLocation DEFAULT_FAUCET = Foundation.identifier("models/block/sink_faucet.json");
    private static final ResourceLocation WATER_TEXTURE = ResourceLocation.withDefaultNamespace("block/water_still");
    private static final float PIXEL = 1.0F / 16.0F;
    private static final float HANDLE_TURN = 135.0F;
    private static final float KNOB_TURN = 90.0F;
    private static final int WATER_GRID = 5;
    private static final float SLOSH_AMPLITUDE = 0.006F;
    private static final float RIPPLE_AMPLITUDE = 0.006F;
    private static final Map<Block, List<Element>> FAUCETS = new HashMap<>();

    public SinkRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(SinkBlockEntity sink, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        Level level = sink.getLevel();
        if (level == null || !(sink.getBlockState().getBlock() instanceof SinkBlock block)) {
            return;
        }
        Direction facing = sink.getBlockState().getValue(SinkBlock.FACING);
        float yaw = switch (facing) {
            case EAST -> 90.0F;
            case SOUTH -> 180.0F;
            case WEST -> 270.0F;
            default -> 0.0F;
        };
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
        poseStack.translate(-0.5F, 0.0F, -0.5F);

        int upperLight = LevelRenderer.getLightColor(level, sink.getBlockPos().above());
        drawWater(poseStack, buffers, level, sink, block.layout(), partialTick, upperLight);

        float turn = Mth.clamp((level.getGameTime() + partialTick - sink.getToggleTime()) / SinkBlockEntity.TURN_TICKS, 0.0F, 1.0F);
        float eased = 1.0F - (1.0F - turn) * (1.0F - turn);
        float progress = sink.isOpen() ? eased : 1.0F - eased;
        poseStack.translate(0.0F, 1.0F, 0.0F);
        drawFaucet(poseStack, buffers, faucet(block), progress, upperLight);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(SinkBlockEntity sink) {
        return true;
    }

    private static void drawWater(PoseStack poseStack, MultiBufferSource buffers, Level level, SinkBlockEntity sink, SinkBlock.Layout layout, float partialTick, int light) {
        float waterLevel = sink.getDisplayLevel(partialTick);
        if (waterLevel <= 0.01F) {
            return;
        }
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(WATER_TEXTURE);
        int color = BiomeColors.getAverageWaterColor(level, sink.getBlockPos());
        float r = (color >> 16 & 255) / 255.0F, g = (color >> 8 & 255) / 255.0F, b = (color & 255) / 255.0F;
        float y = layout.surfaceHeight(waterLevel);
        float x0 = layout.basinX0() * PIXEL, x1 = layout.basinX1() * PIXEL, z0 = layout.basinZ0() * PIXEL, z1 = layout.basinZ1() * PIXEL;
        float spoutX = layout.spoutX() * PIXEL, spoutZ = layout.spoutZ() * PIXEL;
        float time = level.getGameTime() + partialTick;
        float slosh = sink.getSlosh(partialTick);
        boolean ripple = sink.isOpen() && sink.getWaterLevel() < SinkBlockEntity.MAX_LEVEL;
        VertexConsumer consumer = buffers.getBuffer(RenderType.translucent());
        PoseStack.Pose pose = poseStack.last();
        for (int iz = 0; iz < WATER_GRID; iz++) {
            float za = Mth.lerp(iz / (float) WATER_GRID, z0, z1);
            float zb = Mth.lerp((iz + 1) / (float) WATER_GRID, z0, z1);
            for (int ix = 0; ix < WATER_GRID; ix++) {
                float xa = Mth.lerp(ix / (float) WATER_GRID, x0, x1);
                float xb = Mth.lerp((ix + 1) / (float) WATER_GRID, x0, x1);
                float[][] corners = {{xa, za}, {xa, zb}, {xb, zb}, {xb, za}};
                for (float[] corner : corners) {
                    float height = y + wave(corner[0], corner[1], time, slosh, ripple, spoutX, spoutZ);
                    consumer.addVertex(pose, corner[0], height, corner[1])
                            .setColor(r, g, b, 0.85F)
                            .setUv(sprite.getU(corner[0]), sprite.getV(corner[1]))
                            .setLight(light)
                            .setNormal(pose, 0.0F, 1.0F, 0.0F);
                }
            }
        }
    }

    private static float wave(float x, float z, float time, float slosh, boolean ripple, float spoutX, float spoutZ) {
        float height = 0.0F;
        if (slosh > 0.001F) {
            height += SLOSH_AMPLITUDE * slosh * (0.6F * Mth.sin(time * 0.45F + z * 14.0F + x * 4.0F) + 0.4F * Mth.sin(time * 0.6F - x * 15.0F + z * 5.0F));
        }
        if (ripple) {
            float dx = x - spoutX, dz = z - spoutZ;
            float dist = Mth.sqrt(dx * dx + dz * dz);
            height += RIPPLE_AMPLITUDE * Mth.sin(dist * 40.0F - time) * (float) Math.exp(-dist * 6.0F);
        }
        return height;
    }

    private static void drawFaucet(PoseStack poseStack, MultiBufferSource buffers, List<Element> faucet, float progress, int light) {
        Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
        VertexConsumer consumer = buffers.getBuffer(RenderType.cutout());
        for (Element element : faucet) {
            poseStack.pushPose();
            float cx = (element.from[0] + element.to[0]) / 2.0F * PIXEL;
            float cy = (element.from[1] + element.to[1]) / 2.0F * PIXEL;
            float cz = (element.from[2] + element.to[2]) / 2.0F * PIXEL;
            switch (element.motion) {
                case HANDLE -> {
                    poseStack.translate(0.0F, cy, cz);
                    poseStack.mulPose(Axis.XP.rotationDegrees(progress * HANDLE_TURN));
                    poseStack.translate(0.0F, -cy, -cz);
                }
                case KNOB, KNOB_REVERSE -> {
                    float angle = progress * KNOB_TURN * (element.motion == Motion.KNOB ? 1.0F : -1.0F);
                    poseStack.translate(cx, 0.0F, cz);
                    poseStack.mulPose(Axis.YP.rotationDegrees(angle));
                    poseStack.translate(-cx, 0.0F, -cz);
                }
                default -> {
                }
            }
            element.draw(poseStack.last(), consumer, atlas, light);
            poseStack.popPose();
        }
    }

    private static List<Element> faucet(Block block) {
        return FAUCETS.computeIfAbsent(block, SinkRenderer::loadFaucet);
    }

    private static List<Element> loadFaucet(Block block) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        ResourceLocation own = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "models/block/" + id.getPath() + "_faucet.json");
        List<Element> elements = read(own);
        return elements.isEmpty() ? read(DEFAULT_FAUCET) : elements;
    }

    private static List<Element> read(ResourceLocation location) {
        List<Element> elements = new ArrayList<>();
        Optional<net.minecraft.server.packs.resources.Resource> resource = Minecraft.getInstance().getResourceManager().getResource(location);
        if (resource.isEmpty()) {
            return elements;
        }
        try (Reader reader = resource.get().openAsReader()) {
            JsonObject model = JsonParser.parseReader(reader).getAsJsonObject();
            Map<String, String> textures = new HashMap<>();
            if (model.has("textures")) {
                for (Map.Entry<String, JsonElement> entry : model.getAsJsonObject("textures").entrySet()) {
                    textures.put(entry.getKey(), entry.getValue().getAsString());
                }
            }
            for (JsonElement entry : model.getAsJsonArray("elements")) {
                elements.add(Element.parse(entry.getAsJsonObject(), textures));
            }
        } catch (Exception exception) {
            Foundation.LOGGER.warn("Could not read sink faucet model {}", location, exception);
        }
        return elements;
    }

    private static ResourceLocation resolve(String reference, Map<String, String> textures) {
        String value = reference;
        for (int depth = 0; depth < 8 && value.startsWith("#"); depth++) {
            value = textures.getOrDefault(value.substring(1), "minecraft:missingno");
        }
        return ResourceLocation.parse(value);
    }

    private enum Motion {
        NONE, HANDLE, KNOB, KNOB_REVERSE;

        static Motion of(JsonObject json) {
            if (!json.has("name")) {
                return NONE;
            }
            return switch (json.get("name").getAsString()) {
                case "handle" -> HANDLE;
                case "knob" -> KNOB;
                case "knob_reverse" -> KNOB_REVERSE;
                default -> NONE;
            };
        }
    }

    private record Face(Direction direction, float[] uv, int rotation, ResourceLocation texture) {
    }

    private record Element(float[] from, float[] to, Motion motion, List<Face> faces) {
        static Element parse(JsonObject json, Map<String, String> textures) {
            List<Face> faces = new ArrayList<>();
            String fallback = textures.containsKey("faucet") ? "#faucet" : textures.keySet().stream().filter(key -> !key.equals("particle")).findFirst().map(key -> "#" + key).orElse("#particle");
            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("faces").entrySet()) {
                JsonObject face = entry.getValue().getAsJsonObject();
                Direction direction = Direction.byName(entry.getKey());
                if (direction != null) {
                    String reference = face.has("texture") ? face.get("texture").getAsString() : fallback;
                    faces.add(new Face(direction, floats(face.getAsJsonArray("uv")), face.has("rotation") ? face.get("rotation").getAsInt() : 0, resolve(reference, textures)));
                }
            }
            return new Element(floats(json.getAsJsonArray("from")), floats(json.getAsJsonArray("to")), Motion.of(json), faces);
        }

        private static float[] floats(JsonArray array) {
            float[] values = new float[array.size()];
            for (int index = 0; index < values.length; index++) {
                values[index] = array.get(index).getAsFloat();
            }
            return values;
        }

        void draw(PoseStack.Pose pose, VertexConsumer consumer, Function<ResourceLocation, TextureAtlasSprite> atlas, int light) {
            float x0 = from[0] * PIXEL, y0 = from[1] * PIXEL, z0 = from[2] * PIXEL;
            float x1 = to[0] * PIXEL, y1 = to[1] * PIXEL, z1 = to[2] * PIXEL;
            for (Face face : faces) {
                TextureAtlasSprite sprite = atlas.apply(face.texture);
                float[][] points = switch (face.direction) {
                    case UP -> new float[][]{{x0, y1, z0}, {x0, y1, z1}, {x1, y1, z1}, {x1, y1, z0}};
                    case DOWN -> new float[][]{{x0, y0, z1}, {x0, y0, z0}, {x1, y0, z0}, {x1, y0, z1}};
                    case NORTH -> new float[][]{{x1, y1, z0}, {x1, y0, z0}, {x0, y0, z0}, {x0, y1, z0}};
                    case SOUTH -> new float[][]{{x0, y1, z1}, {x0, y0, z1}, {x1, y0, z1}, {x1, y1, z1}};
                    case WEST -> new float[][]{{x0, y1, z0}, {x0, y0, z0}, {x0, y0, z1}, {x0, y1, z1}};
                    case EAST -> new float[][]{{x1, y1, z1}, {x1, y0, z1}, {x1, y0, z0}, {x1, y1, z0}};
                };
                float u0 = face.uv[0], v0 = face.uv[1], u1 = face.uv[2], v1 = face.uv[3];
                float[][] uvs = {{u0, v0}, {u0, v1}, {u1, v1}, {u1, v0}};
                int shift = (face.rotation / 90) % 4;
                float shade = switch (face.direction) {
                    case UP -> 1.0F;
                    case DOWN -> 0.5F;
                    case NORTH, SOUTH -> 0.8F;
                    default -> 0.6F;
                };
                for (int corner = 0; corner < 4; corner++) {
                    float[] uv = uvs[(corner + shift) % 4];
                    consumer.addVertex(pose, points[corner][0], points[corner][1], points[corner][2])
                            .setColor(shade, shade, shade, 1.0F)
                            .setUv(sprite.getU(uv[0] / 16.0F), sprite.getV(uv[1] / 16.0F))
                            .setLight(light)
                            .setNormal(pose, face.direction.getStepX(), face.direction.getStepY(), face.direction.getStepZ());
                }
            }
        }
    }
}
