package net.satisfy.foundation.tooltip;

import dev.architectury.event.events.client.ClientTooltipEvent;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.satisfy.foundation.Foundation;
import net.satisfy.foundation.platform.PlatformHelper;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class AttributeIcons {
    private static final Style ICON_STYLE = Style.EMPTY.withFont(Foundation.identifier("attribute_icons")).withColor(TextColor.fromRgb(0xFFFFFF)).withItalic(false).withBold(false);
    private static final Map<Holder<Attribute>, Character> ICONS = new LinkedHashMap<>();
    private static Map<String, Component> byKey;
    private static boolean initialized;

    static {
        ICONS.put(Attributes.ARMOR, '');
        ICONS.put(Attributes.ARMOR_TOUGHNESS, '');
        ICONS.put(Attributes.KNOCKBACK_RESISTANCE, '');
        ICONS.put(Attributes.ATTACK_DAMAGE, '');
        ICONS.put(Attributes.ATTACK_SPEED, '');
        ICONS.put(Attributes.MAX_HEALTH, '');
        ICONS.put(Attributes.MOVEMENT_SPEED, '');
    }

    private AttributeIcons() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        ClientTooltipEvent.ITEM.register((stack, lines, context, flag) -> {
            if (PlatformHelper.enableAttributeIcons()) {
                apply(lines);
            }
        });
    }

    public static void apply(List<Component> lines) {
        for (int i = 0; i < lines.size(); i++) {
            Component line = lines.get(i);
            Component icon = icon(line);
            if (icon != null) {
                lines.set(i, Component.empty().append(icon).append(CommonComponents.SPACE).append(trimLeadingSpace(line)));
            }
        }
    }

    @Nullable
    private static Component icon(Component line) {
        String key = attributeKey(line);
        return key == null ? null : icons().get(key);
    }

    private static Map<String, Component> icons() {
        if (byKey == null) {
            Map<String, Component> map = new LinkedHashMap<>();
            ICONS.forEach((attribute, glyph) -> map.put(attribute.value().getDescriptionId(), Component.literal(String.valueOf(glyph)).withStyle(ICON_STYLE)));
            byKey = map;
        }
        return byKey;
    }

    @Nullable
    private static String attributeKey(Component component) {
        if (component.getContents() instanceof TranslatableContents translatable && isModifierKey(translatable.getKey())) {
            for (Object arg : translatable.getArgs()) {
                if (arg instanceof Component argument && argument.getContents() instanceof TranslatableContents name && icons().containsKey(name.getKey())) {
                    return name.getKey();
                }
            }
        }
        for (Component sibling : component.getSiblings()) {
            String key = attributeKey(sibling);
            if (key != null) {
                return key;
            }
        }
        return null;
    }

    private static boolean isModifierKey(String key) {
        return key.startsWith("attribute.modifier.") || key.startsWith("neoforge.modifier.");
    }

    private static Component trimLeadingSpace(Component line) {
        if (line.getContents() instanceof PlainTextContents plain && plain.text().isBlank() && !line.getSiblings().isEmpty()) {
            MutableComponent trimmed = Component.empty().withStyle(line.getStyle());
            line.getSiblings().forEach(trimmed::append);
            return trimmed;
        }
        return line;
    }
}
