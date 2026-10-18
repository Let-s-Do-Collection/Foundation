package net.satisfy.foundation.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class PlatformHelper {
    @ExpectPlatform
    public static boolean enableImprovedTooltips() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean enableRarityColors() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean showItemInfo() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean showArmorSetInfo() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean enableAttributeIcons() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean showBlockNotices() {
        throw new AssertionError();
    }
}
