package net.satisfy.foundation.overlay;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import dev.architectury.event.events.client.ClientCommandRegistrationEvent;
import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.satisfy.foundation.Foundation;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

public final class TutorialLoader implements ResourceManagerReloadListener {
    private static final String DIRECTORY = "tutorials";
    private static boolean registered;

    private TutorialLoader() {
    }

    public static void init() {
        if (!registered) {
            registered = true;
            ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, new TutorialLoader(), Foundation.identifier("tutorials"));
            ClientCommandRegistrationEvent.EVENT.register((dispatcher, context) -> dispatcher.register(ClientCommandRegistrationEvent.literal("foundation_tutorials")
                    .then(ClientCommandRegistrationEvent.literal("reset").executes(command -> {
                        Tutorials.reset();
                        command.getSource().arch$sendSuccess(() -> Component.translatable("hud.foundation.tutorial.reset"), false);
                        return 1;
                    }))));
        }
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        List<Tutorials.Step> steps = new ArrayList<>();
        for (Map.Entry<ResourceLocation, Resource> entry : manager.listResources(DIRECTORY, path -> path.getPath().endsWith(".json")).entrySet()) {
            ResourceLocation file = entry.getKey();
            String path = file.getPath().substring(DIRECTORY.length() + 1, file.getPath().length() - ".json".length());
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(file.getNamespace(), path);
            try (Reader reader = entry.getValue().openAsReader()) {
                steps.add(parse(id, JsonParser.parseReader(reader).getAsJsonObject()));
            } catch (Exception exception) {
                Foundation.LOGGER.warn("Could not load tutorial {}: {}", id, exception.getMessage());
            }
        }
        Tutorials.replaceJsonSteps(steps);
    }

    private static Tutorials.Step parse(ResourceLocation id, JsonObject json) {
        Component title = text(json.get("title"));
        List<Component> hints = new ArrayList<>();
        if (json.has("hints")) {
            json.getAsJsonArray("hints").forEach(element -> hints.add(text(element)));
        }
        ResourceLocation after = json.has("after") ? ResourceLocation.parse(json.get("after").getAsString()) : null;
        Predicate<Minecraft> start = json.has("start") ? startCondition(json.getAsJsonObject("start")) : TutorialConditions.always();
        int goal = 1;
        ToIntFunction<Minecraft> tracker = null;
        if (json.has("goal")) {
            JsonObject goalJson = json.getAsJsonObject("goal");
            goal = Math.max(1, goalJson.has("count") ? goalJson.get("count").getAsInt() : 1);
            tracker = goalTracker(goalJson, goal);
        }
        Component doneTitle = json.has("done_title") ? text(json.get("done_title")) : null;
        return new Tutorials.Step(id, title, List.copyOf(hints), goal, after, start, tracker, doneTitle, true);
    }

    private static Component text(@Nullable JsonElement element) {
        if (element == null) {
            return Component.empty();
        }
        return ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, element).getOrThrow();
    }

    private static String value(JsonObject json, String key) {
        if (!json.has(key)) {
            throw new IllegalArgumentException("missing \"" + key + "\"");
        }
        return json.get(key).getAsString();
    }

    private static Predicate<Minecraft> startCondition(JsonObject json) {
        String type = value(json, "type");
        return switch (type) {
            case "always" -> TutorialConditions.always();
            case "looking_at" -> TutorialConditions.lookingAt(TutorialConditions.blockTest(value(json, "block")));
            case "holding" -> TutorialConditions.holding(TutorialConditions.itemTest(value(json, "item")));
            case "in_biome" -> TutorialConditions.biomeTest(value(json, "biome"));
            case "has_item" -> {
                ToIntFunction<Minecraft> count = TutorialConditions.itemCount(TutorialConditions.itemTest(value(json, "item")));
                int needed = json.has("count") ? json.get("count").getAsInt() : 1;
                yield minecraft -> count.applyAsInt(minecraft) >= needed;
            }
            default -> throw new IllegalArgumentException("unknown start type \"" + type + "\"");
        };
    }

    @Nullable
    private static ToIntFunction<Minecraft> goalTracker(JsonObject json, int goal) {
        String type = value(json, "type");
        return switch (type) {
            case "manual" -> null;
            case "has_item" -> TutorialConditions.itemCount(TutorialConditions.itemTest(value(json, "item")));
            case "looking_at", "holding", "in_biome" -> {
                Predicate<Minecraft> test = startCondition(json);
                yield minecraft -> test.test(minecraft) ? goal : 0;
            }
            default -> throw new IllegalArgumentException("unknown goal type \"" + type + "\"");
        };
    }
}
