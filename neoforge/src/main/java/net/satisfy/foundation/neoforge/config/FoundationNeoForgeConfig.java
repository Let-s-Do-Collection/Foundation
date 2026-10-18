package net.satisfy.foundation.neoforge.config;

import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class FoundationNeoForgeConfig {
    public static final ModConfigSpec COMMON_CONFIG;
    public static final ModConfigSpec.BooleanValue ENABLE_IMPROVED_TOOLTIPS;
    public static final ModConfigSpec.BooleanValue ENABLE_RARITY_COLORS;
    public static final ModConfigSpec.BooleanValue SHOW_ITEM_INFO;
    public static final ModConfigSpec.BooleanValue SHOW_ARMOR_SET_INFO;
    public static final ModConfigSpec.BooleanValue SHOW_BLOCK_NOTICES;

    public static boolean enableImprovedTooltips = true;
    public static boolean enableRarityColors = true;
    public static boolean showItemInfo = true;
    public static boolean showArmorSetInfo = true;
    public static boolean showBlockNotices = true;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Item tooltips and notices. Only affects your own screen.").push("Info Tooltips");
        ENABLE_IMPROVED_TOOLTIPS = builder.comment("Items with a rarity get a framed tooltip with their icon next to the name.").define("enableImprovedTooltips", true);
        ENABLE_RARITY_COLORS = builder.comment("Item names are colored by their rarity, in tooltips and above the hotbar.").define("enableRarityColors", true);
        SHOW_ITEM_INFO = builder.comment("Shows extra tooltip lines like Can be placed and the details while holding Shift.").define("showItemInfo", true);
        SHOW_ARMOR_SET_INFO = builder.comment("Shows which pieces of an armor set you are wearing and its set bonus.").define("showArmorSetInfo", true);
        SHOW_BLOCK_NOTICES = builder.comment("Shows a short confirmation next to a block after you use it.").define("showBlockNotices", true);
        builder.pop();

        COMMON_CONFIG = builder.build();
    }

    public static void onLoad(ModConfigEvent.Loading event) {
        sync();
    }

    public static void onReload(ModConfigEvent.Reloading event) {
        sync();
    }

    public static void sync() {
        enableImprovedTooltips = ENABLE_IMPROVED_TOOLTIPS.get();
        enableRarityColors = ENABLE_RARITY_COLORS.get();
        showItemInfo = SHOW_ITEM_INFO.get();
        showArmorSetInfo = SHOW_ARMOR_SET_INFO.get();
        showBlockNotices = SHOW_BLOCK_NOTICES.get();
    }
}
