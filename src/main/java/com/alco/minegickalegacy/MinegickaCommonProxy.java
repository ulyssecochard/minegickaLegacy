package com.alco.minegickalegacy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class MinegickaCommonProxy implements MinegickaProxy {

    @Override
    public void registerModEvents(IEventBus modEventBus) {
        MinegickaProxy.super.registerModEvents(modEventBus);
    }

    @Override
    public void registerForgeEvents() {
        MinecraftForge.EVENT_BUS.register(MinegickaForgeEvents.INSTANCE);
        MinecraftForge.EVENT_BUS.register(MinegickaTickEvents.Server.INSTANCE);
    }

    @Override
    public void onCommonSetup(final FMLCommonSetupEvent event) {
        MinegickaMod.LOGGER.debug("Minegicka common setup initialised");
    }
}
