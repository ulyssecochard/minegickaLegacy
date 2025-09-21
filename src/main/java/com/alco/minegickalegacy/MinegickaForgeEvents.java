package com.alco.minegickalegacy;

import com.alco.minegickalegacy.mechanics.clickcraft.ClickCraftManager;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class MinegickaForgeEvents {
    static final MinegickaForgeEvents INSTANCE = new MinegickaForgeEvents();

    private MinegickaForgeEvents() {
    }

    @SubscribeEvent
    public void onLevelLoad(final LevelEvent.Load event) {
        MinegickaMod.LOGGER.trace("Level loaded: {}", event.getLevel());
    }

    @SubscribeEvent
    public void onLevelUnload(final LevelEvent.Unload event) {
        MinegickaMod.LOGGER.trace("Level unloaded: {}", event.getLevel());
    }

    @SubscribeEvent
    public void onEntityJoinLevel(final EntityJoinLevelEvent event) {
        final Entity entity = event.getEntity();
        MinegickaMod.LOGGER.trace("Entity joined level: {}", entity.getDisplayName().getString());
    }

    @SubscribeEvent
    public void onLivingDrops(final LivingDropsEvent event) {
        // Placeholder hook for future loot injections.
    }

    @SubscribeEvent
    public void onEntityStruckByLightning(final EntityStruckByLightningEvent event) {
        // Placeholder hook for lightning resistance logic.
    }

    @SubscribeEvent
    public void onAddReloadListeners(final AddReloadListenerEvent event) {
        event.addListener(ClickCraftManager.INSTANCE);
    }
}
