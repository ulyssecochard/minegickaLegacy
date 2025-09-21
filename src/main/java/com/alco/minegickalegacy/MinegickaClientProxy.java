package com.alco.minegickalegacy;

import com.alco.minegickalegacy.client.ManaOverlayRenderer;
import com.alco.minegickalegacy.client.input.MinegickaKeyMappings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraft.client.gui.screens.MenuScreens;
import com.alco.minegickalegacy.client.gui.CraftStationScreen;
import com.alco.minegickalegacy.client.gui.EnchantStaffScreen;
import com.alco.minegickalegacy.menu.CraftStationMenu;
import com.alco.minegickalegacy.menu.EnchantStaffMenu;
import com.alco.minegickalegacy.registry.MinegickaMenus;
import com.alco.minegickalegacy.client.input.MinegickaKeyMappings;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class MinegickaClientProxy extends MinegickaCommonProxy {

    @Override
    public void registerModEvents(IEventBus modEventBus) {
        super.registerModEvents(modEventBus);
        modEventBus.addListener(this::onClientSetup);
        modEventBus.addListener(MinegickaKeyMappings::register);
    }

    @Override
    public void registerForgeEvents() {
        super.registerForgeEvents();
        MinecraftForge.EVENT_BUS.register(MinegickaTickEvents.Client.INSTANCE);
    }

    @Override
    public void onClientSetup(final FMLClientSetupEvent event) {
        ManaOverlayRenderer.register();
        event.enqueueWork(() -> { 
            MenuScreens.register(MinegickaMenus.CRAFT_STATION.get(), CraftStationScreen::new);
            MenuScreens.register(MinegickaMenus.ENCHANT_STAFF.get(), EnchantStaffScreen::new);
            ManaOverlayRenderer.register();
        });
    }
}
