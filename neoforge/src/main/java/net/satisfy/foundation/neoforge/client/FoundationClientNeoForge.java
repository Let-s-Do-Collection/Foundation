package net.satisfy.foundation.neoforge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.satisfy.foundation.Foundation;
import net.satisfy.foundation.client.FoundationClient;

@EventBusSubscriber(modid = Foundation.MOD_ID, value = Dist.CLIENT)
public class FoundationClientNeoForge {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(FoundationClient::onInitializeClient);
    }
}
