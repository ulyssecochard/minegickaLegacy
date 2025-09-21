package com.alco.minegickalegacy.capability;

import com.alco.minegickalegacy.data.PlayerManaSavedData;
import com.alco.minegickalegacy.network.MinegickaNetwork;
import com.alco.minegickalegacy.spell.ServerSpellManager;
import com.alco.minegickalegacy.spell.SpellStopReason;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.List;

public final class PlayerManaEvents {
    private PlayerManaEvents() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(PlayerManaEvents::onPlayerLoggedIn);
        MinecraftForge.EVENT_BUS.addListener(PlayerManaEvents::onPlayerLoggedOut);
        MinecraftForge.EVENT_BUS.addListener(PlayerManaEvents::onPlayerChangedDimension);
        MinecraftForge.EVENT_BUS.addListener(PlayerManaEvents::onPlayerRespawn);
        MinecraftForge.EVENT_BUS.addListener(PlayerManaEvents::onPlayerDeath);
        MinecraftForge.EVENT_BUS.addListener(PlayerManaEvents::onPlayerTick);
    }

    private static void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            MinegickaNetwork.sendHandshake(serverPlayer);
            MinegickaNetwork.sendCombination(serverPlayer, List.of());
            serverPlayer.getCapability(PlayerManaCapability.CAPABILITY).ifPresent(data -> {
                PlayerManaSavedData.get(serverPlayer.serverLevel()).apply(serverPlayer, data);
                MinegickaNetwork.sendTo(serverPlayer, data);
                data.clearDirty();
            });
        }
    }

    private static void onPlayerLoggedOut(final PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(PlayerManaCapability.CAPABILITY).ifPresent(data -> {
                PlayerManaSavedData.get(serverPlayer.serverLevel()).store(serverPlayer.getUUID(), data);
                data.clearDirty();
            });
            ServerSpellManager.stopSpell(serverPlayer, SpellStopReason.CASTER_INVALID);
        }
    }

    private static void onPlayerChangedDimension(final PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            MinegickaNetwork.sendCombination(serverPlayer, List.of());
            serverPlayer.getCapability(PlayerManaCapability.CAPABILITY).ifPresent(data -> {
                final ServerLevel from = serverPlayer.server.getLevel(event.getFrom());
                if (from != null) {
                    PlayerManaSavedData.get(from).store(serverPlayer.getUUID(), data);
                    ServerSpellManager.stopSpell(from, serverPlayer.getUUID(), SpellStopReason.INTERRUPTED);
                } else {
                    ServerSpellManager.stopSpell(serverPlayer, SpellStopReason.INTERRUPTED);
                }
                PlayerManaSavedData.get(serverPlayer.serverLevel()).apply(serverPlayer, data);
                MinegickaNetwork.sendTo(serverPlayer, data);
                data.clearDirty();
            });
        }
    }

    private static void onPlayerRespawn(final PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            ServerSpellManager.stopSpell(serverPlayer, SpellStopReason.CASTER_INVALID);
        }
    }

    private static void onPlayerDeath(final LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            ServerSpellManager.stopSpell(serverPlayer, SpellStopReason.CASTER_INVALID);
        }
    }

    private static void onPlayerTick(final TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        final Player player = event.player;
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        serverPlayer.getCapability(PlayerManaCapability.CAPABILITY).ifPresent(data -> {
            data.recoverMana(1.0D / 6.0D);
            if (data.isDirty()) {
                PlayerManaSavedData.get(serverPlayer.serverLevel()).store(serverPlayer.getUUID(), data);
                MinegickaNetwork.sendTo(serverPlayer, data);
                data.clearDirty();
            }
        });
    }
}
