package net.satisfy.foundation;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Foundation {
    public static final String MOD_ID = "foundation";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
    }

    public static ResourceLocation identifier(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
