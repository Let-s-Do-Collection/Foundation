package net.satisfy.foundation.overlay;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.platform.Platform;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.satisfy.foundation.platform.PlatformHelper;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

public final class Tutorials {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "foundation_tutorials.json";
    private static final int FINISH_TICKS = 50;
    private static final int FADE_TICKS = 10;
    private static final int PAUSE_TICKS = 20;
    private static final int SAVE_INTERVAL = 100;

    private static final Map<ResourceLocation, Step> STEPS = new LinkedHashMap<>();
    private static final Set<ResourceLocation> DONE = new LinkedHashSet<>();
    private static final Map<ResourceLocation, Integer> PROGRESS = new HashMap<>();

    private static boolean initialized;
    private static boolean loaded;
    private static boolean dirty;
    private static int saveTimer;
    @Nullable
    private static Step active;
    @Nullable
    private static Step finished;
    private static int finishTicks;
    private static int pauseTicks;
    private static int fadeTicks;

    private Tutorials() {
    }

    public record Step(ResourceLocation id, Component title, List<Component> hints, int goal, @Nullable ResourceLocation after,
                       Predicate<Minecraft> startWhen, @Nullable ToIntFunction<Minecraft> tracker, @Nullable Component doneTitle, boolean fromJson) {
    }

    public static Builder step(ResourceLocation id) {
        return new Builder(id);
    }

    public static void register(Step step) {
        STEPS.put(step.id(), step);
        init();
    }

    static void replaceJsonSteps(List<Step> steps) {
        STEPS.values().removeIf(Step::fromJson);
        if (active != null && active.fromJson()) {
            active = null;
        }
        for (Step step : steps) {
            STEPS.putIfAbsent(step.id(), step);
        }
        init();
    }

    public static void advance(ResourceLocation id) {
        advance(id, 1);
    }

    public static void advance(ResourceLocation id, int amount) {
        if (DONE.contains(id) || amount <= 0) {
            return;
        }
        PROGRESS.merge(id, amount, Integer::sum);
        dirty = true;
    }

    public static void complete(ResourceLocation id) {
        Step step = STEPS.get(id);
        PROGRESS.put(id, step == null ? 1 : step.goal());
        dirty = true;
    }

    public static boolean isDone(ResourceLocation id) {
        load();
        return DONE.contains(id);
    }

    public static void reset() {
        load();
        DONE.clear();
        PROGRESS.clear();
        active = null;
        finished = null;
        save();
    }

    private static void init() {
        if (!initialized) {
            initialized = true;
            ClientTickEvent.CLIENT_POST.register(Tutorials::tick);
        }
    }

    private static void tick(Minecraft minecraft) {
        if (!PlatformHelper.showTutorials() || minecraft.level == null || minecraft.player == null) {
            BlockInfoOverlay.setTutorialPanel(List.of(), 0.0F);
            return;
        }
        load();
        if (dirty && ++saveTimer >= SAVE_INTERVAL) {
            save();
        }
        if (finished != null) {
            finishTicks--;
            BlockInfoOverlay.setTutorialPanel(finishedSections(finished), Math.min(1.0F, finishTicks / (float) FADE_TICKS));
            if (finishTicks <= 0) {
                finished = null;
                pauseTicks = PAUSE_TICKS;
            }
            return;
        }
        if (pauseTicks > 0) {
            pauseTicks--;
            BlockInfoOverlay.setTutorialPanel(List.of(), 0.0F);
            return;
        }
        if (active == null) {
            active = findNext(minecraft);
            fadeTicks = 0;
        }
        if (active == null) {
            BlockInfoOverlay.setTutorialPanel(List.of(), 0.0F);
            return;
        }
        Step step = active;
        if (step.tracker() != null) {
            int tracked = step.tracker().applyAsInt(minecraft);
            if (tracked > PROGRESS.getOrDefault(step.id(), 0)) {
                PROGRESS.put(step.id(), tracked);
                dirty = true;
            }
        }
        int current = PROGRESS.getOrDefault(step.id(), 0);
        if (current >= step.goal()) {
            DONE.add(step.id());
            PROGRESS.remove(step.id());
            save();
            active = null;
            finished = step;
            finishTicks = FINISH_TICKS;
            BlockInfoOverlay.setTutorialPanel(finishedSections(step), 1.0F);
            return;
        }
        fadeTicks = Math.min(FADE_TICKS, fadeTicks + 1);
        BlockInfoOverlay.setTutorialPanel(activeSections(step, current), fadeTicks / (float) FADE_TICKS);
    }

    @Nullable
    private static Step findNext(Minecraft minecraft) {
        for (Step step : STEPS.values()) {
            if (DONE.contains(step.id()) || (step.after() != null && !DONE.contains(step.after()))) {
                continue;
            }
            if (PROGRESS.containsKey(step.id()) || step.startWhen().test(minecraft)) {
                return step;
            }
        }
        return null;
    }

    private static List<InfoSection> activeSections(Step step, int current) {
        List<InfoSection> sections = new ArrayList<>();
        sections.add(InfoSection.title(step.title()));
        for (Component hint : step.hints()) {
            sections.add(InfoSection.title(hint.copy().withStyle(ChatFormatting.GRAY)));
        }
        if (step.goal() > 1) {
            sections.add(InfoSection.progress(Component.empty(), current, step.goal()));
        }
        return sections;
    }

    private static List<InfoSection> finishedSections(Step step) {
        Component title = step.doneTitle() != null ? step.doneTitle() : step.title();
        List<InfoSection> sections = new ArrayList<>();
        sections.add(InfoSection.title(title.copy().append(" ✔").withStyle(ChatFormatting.GREEN)));
        if (step.goal() > 1) {
            sections.add(InfoSection.progress(Component.empty(), step.goal(), step.goal()));
        }
        return sections;
    }

    private static Path file() {
        return Platform.getConfigFolder().resolve(FILE_NAME);
    }

    private static void load() {
        if (loaded) {
            return;
        }
        loaded = true;
        Path path = file();
        if (!Files.exists(path)) {
            return;
        }
        try {
            JsonObject json = JsonParser.parseString(Files.readString(path)).getAsJsonObject();
            if (json.has("done")) {
                json.getAsJsonArray("done").forEach(element -> {
                    ResourceLocation id = ResourceLocation.tryParse(element.getAsString());
                    if (id != null) {
                        DONE.add(id);
                    }
                });
            }
            if (json.has("progress")) {
                json.getAsJsonObject("progress").entrySet().forEach(entry -> {
                    ResourceLocation id = ResourceLocation.tryParse(entry.getKey());
                    if (id != null) {
                        PROGRESS.put(id, entry.getValue().getAsInt());
                    }
                });
            }
        } catch (IOException | RuntimeException ignored) {
        }
    }

    private static void save() {
        dirty = false;
        saveTimer = 0;
        JsonObject json = new JsonObject();
        JsonArray done = new JsonArray();
        DONE.forEach(id -> done.add(id.toString()));
        json.add("done", done);
        JsonObject progress = new JsonObject();
        PROGRESS.forEach((id, value) -> progress.addProperty(id.toString(), value));
        json.add("progress", progress);
        try {
            Files.writeString(file(), GSON.toJson(json));
        } catch (IOException ignored) {
        }
    }

    public static final class Builder {
        private final ResourceLocation id;
        private Component title = Component.empty();
        private final List<Component> hints = new ArrayList<>();
        private int goal = 1;
        @Nullable
        private ResourceLocation after;
        private Predicate<Minecraft> startWhen = TutorialConditions.always();
        @Nullable
        private ToIntFunction<Minecraft> tracker;
        @Nullable
        private Component doneTitle;

        private Builder(ResourceLocation id) {
            this.id = id;
        }

        public Builder title(Component title) {
            this.title = title;
            return this;
        }

        public Builder hint(Component hint) {
            this.hints.add(hint);
            return this;
        }

        public Builder goal(int goal) {
            this.goal = Math.max(1, goal);
            return this;
        }

        public Builder after(ResourceLocation after) {
            this.after = after;
            return this;
        }

        public Builder startWhen(Predicate<Minecraft> startWhen) {
            this.startWhen = startWhen;
            return this;
        }

        public Builder track(ToIntFunction<Minecraft> tracker) {
            this.tracker = tracker;
            return this;
        }

        public Builder doneWhen(Predicate<Minecraft> doneWhen) {
            this.tracker = minecraft -> doneWhen.test(minecraft) ? this.goal : 0;
            return this;
        }

        public Builder doneTitle(Component doneTitle) {
            this.doneTitle = doneTitle;
            return this;
        }

        public Step build() {
            return new Step(this.id, this.title, List.copyOf(this.hints), this.goal, this.after, this.startWhen, this.tracker, this.doneTitle, false);
        }

        public Step register() {
            Step step = this.build();
            Tutorials.register(step);
            return step;
        }
    }
}
