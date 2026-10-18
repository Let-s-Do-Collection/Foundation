package net.satisfy.foundation.neoforge.client;

import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.client.event.GatherSkippedAttributeTooltipsEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.satisfy.foundation.platform.PlatformHelper;

public final class QuarkAttributeOverride {
    private static final String QUARK_COMPONENT = "org.violetmoon.quark.content.client.tooltip.AttributeTooltips$AttributeComponent";

    private QuarkAttributeOverride() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, QuarkAttributeOverride::keepAttributeLines);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, QuarkAttributeOverride::removeQuarkComponent);
    }

    private static void keepAttributeLines(GatherSkippedAttributeTooltipsEvent event) {
        if (PlatformHelper.enableAttributeIcons() && event.isSkippingAll()) {
            event.setSkipAll(false);
        }
    }

    private static void removeQuarkComponent(RenderTooltipEvent.GatherComponents event) {
        if (PlatformHelper.enableAttributeIcons()) {
            event.getTooltipElements().removeIf(element -> element.right().map(component -> component.getClass().getName().equals(QUARK_COMPONENT)).orElse(false));
        }
    }
}
