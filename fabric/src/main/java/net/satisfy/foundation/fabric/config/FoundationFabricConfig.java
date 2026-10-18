package net.satisfy.foundation.fabric.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "foundation")
@Config.Gui.Background("foundation:textures/block/tablecloth.png")
public class FoundationFabricConfig implements ConfigData {
    @ConfigEntry.Gui.CollapsibleObject
    public InfoTooltipSettings infoTooltips = new InfoTooltipSettings();

    public static class InfoTooltipSettings {
        @ConfigEntry.Gui.Tooltip
        public boolean enableImprovedTooltips = true;

        @ConfigEntry.Gui.Tooltip
        public boolean enableRarityColors = true;

        @ConfigEntry.Gui.Tooltip
        public boolean showItemInfo = true;

        @ConfigEntry.Gui.Tooltip
        public boolean showArmorSetInfo = true;

        @ConfigEntry.Gui.Tooltip
        public boolean enableAttributeIcons = true;

        @ConfigEntry.Gui.Tooltip
        public boolean showBlockNotices = true;
    }
}
