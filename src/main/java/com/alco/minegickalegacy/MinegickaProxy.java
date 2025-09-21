package com.alco.minegickalegacy;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public interface MinegickaProxy {
    default void registerModEvents(IEventBus modEventBus) {
        modEventBus.addListener(this::onCommonSetup);
    }

    default void registerForgeEvents() {
        // Server- and client-side proxies can hook into the Forge event bus here.
    }

    default void onCommonSetup(final FMLCommonSetupEvent event) {
        // Placeholder for shared initialisation logic.
    }

    default void onClientSetup(final FMLClientSetupEvent event) {
        // Placeholder for client initialisation.
    }
}
