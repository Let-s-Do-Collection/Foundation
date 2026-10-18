package net.satisfy.foundation.platform.fabric;

import me.shedaniel.autoconfig.AutoConfig;
import net.satisfy.foundation.fabric.config.FoundationFabricConfig;
import net.satisfy.foundation.platform.PlatformHelper;

public class PlatformHelperImpl extends PlatformHelper {
    private static FoundationFabricConfig config() {
        return AutoConfig.getConfigHolder(FoundationFabricConfig.class).getConfig();
    }

    public static boolean enableImprovedTooltips() {
        return config().infoTooltips.enableImprovedTooltips;
    }

    public static boolean enableRarityColors() {
        return config().infoTooltips.enableRarityColors;
    }

    public static boolean showItemInfo() {
        return config().infoTooltips.showItemInfo;
    }

    public static boolean showArmorSetInfo() {
        return config().infoTooltips.showArmorSetInfo;
    }

    public static boolean showBlockNotices() {
        return config().infoTooltips.showBlockNotices;
    }
}
