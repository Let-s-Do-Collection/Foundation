package net.satisfy.foundation.platform.neoforge;

import net.satisfy.foundation.neoforge.config.FoundationNeoForgeConfig;
import net.satisfy.foundation.platform.PlatformHelper;

public class PlatformHelperImpl extends PlatformHelper {
    public static boolean enableImprovedTooltips() {
        return FoundationNeoForgeConfig.enableImprovedTooltips;
    }

    public static boolean enableRarityColors() {
        return FoundationNeoForgeConfig.enableRarityColors;
    }

    public static boolean showItemInfo() {
        return FoundationNeoForgeConfig.showItemInfo;
    }

    public static boolean showArmorSetInfo() {
        return FoundationNeoForgeConfig.showArmorSetInfo;
    }

    public static boolean enableAttributeIcons() {
        return FoundationNeoForgeConfig.enableAttributeIcons;
    }

    public static boolean showBlockNotices() {
        return FoundationNeoForgeConfig.showBlockNotices;
    }
}
