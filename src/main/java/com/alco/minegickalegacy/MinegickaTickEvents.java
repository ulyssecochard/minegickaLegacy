package com.alco.minegickalegacy;

import com.alco.minegickalegacy.client.input.MinegickaKeyHandler;
import com.alco.minegickalegacy.spell.ServerSpellManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class MinegickaTickEvents {
    private MinegickaTickEvents() {
    }

    public static final class Client {
        static final Client INSTANCE = new Client();

        private Client() {
        }

        @SubscribeEvent
        public void onClientTick(final TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                MinegickaKeyHandler.handleClientTick();
            }
        }

        @SubscribeEvent
        public void onRenderTick(final TickEvent.RenderTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                // TODO: port legacy render tick logic.
            }
        }
    }

    public static final class Server {
        static final Server INSTANCE = new Server();

        private Server() {
        }

        @SubscribeEvent
        public void onServerTick(final TickEvent.ServerTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                // Reserved for future global spell housekeeping.
            }
        }

        @SubscribeEvent
        public void onLevelTick(final TickEvent.LevelTickEvent event) {
            if (!(event.level instanceof ServerLevel serverLevel)) {
                return;
            }
            if (event.phase == TickEvent.Phase.END) {
                ServerSpellManager.tickLevel(serverLevel);
            }
        }

        @SubscribeEvent
        public void onPlayerTick(final TickEvent.PlayerTickEvent event) {
            if (!(event.player instanceof ServerPlayer serverPlayer)) {
                return;
            }
            if (event.phase == TickEvent.Phase.END) {
                ServerSpellManager.tickPlayer(serverPlayer);
            }
        }
    }
}
