package com.alco.minegickalegacy.spell;

import com.alco.minegickalegacy.network.MinegickaNetwork;
import com.alco.minegickalegacy.registry.MagickRegistry.MagickDefinition;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ServerSpellManager {
    private static final Map<UUID, SpellInstance> ACTIVE_SPELLS = new ConcurrentHashMap<>();
    private static final Map<ResourceKey<Level>, Set<UUID>> LEVEL_CASTERS = new ConcurrentHashMap<>();

    private ServerSpellManager() {
    }

    public static void startSpell(final ServerPlayer player, final MagickDefinition definition) {
        stopSpell(player, SpellStopReason.INTERRUPTED);

        final SpellForm form = SpellExecutors.determineForm(definition.combination());
        final SpellExecutor executor = SpellExecutors.createExecutor(form, definition);
        final SpellInstance instance = SpellInstance.create(player, definition, executor, form);

        MinegickaNetwork.sendSpellTrigger(player, instance.definitionId(), true);
        if (!instance.start(player)) {
            MinegickaNetwork.sendSpellTrigger(player, instance.definitionId(), false);
            return;
        }

        ACTIVE_SPELLS.put(player.getUUID(), instance);
        LEVEL_CASTERS.computeIfAbsent(instance.levelKey(), key -> ConcurrentHashMap.newKeySet()).add(player.getUUID());
    }

    public static void stopSpell(final ServerPlayer player) {
        stopSpell(player, SpellStopReason.INTERRUPTED);
    }

    public static void stopSpell(final ServerPlayer player, final SpellStopReason reason) {
        final SpellInstance instance = ACTIVE_SPELLS.remove(player.getUUID());
        if (instance == null) {
            return;
        }
        removeFromLevel(instance);
        final ServerLevel level = resolveLevel(player, instance.levelKey());
        if (level != null) {
            instance.stop(level, reason);
        }
        MinegickaNetwork.sendSpellTrigger(player, instance.definitionId(), false);
    }

    public static void stopSpell(final ServerLevel level, final UUID casterId, final SpellStopReason reason) {
        final SpellInstance instance = ACTIVE_SPELLS.remove(casterId);
        if (instance == null) {
            return;
        }
        removeFromLevel(instance);
        instance.stop(level, reason);
        final ServerPlayer player = level.getServer().getPlayerList().getPlayer(casterId);
        if (player != null) {
            MinegickaNetwork.sendSpellTrigger(player, instance.definitionId(), false);
        }
    }

    public static void clearForLevel(final ServerLevel level) {
        final Set<UUID> casters = LEVEL_CASTERS.get(level.dimension());
        if (casters == null || casters.isEmpty()) {
            return;
        }
        final List<UUID> ids = new ArrayList<>(casters);
        ids.forEach(id -> stopSpell(level, id, SpellStopReason.LEVEL_UNLOADED));
        LEVEL_CASTERS.remove(level.dimension());
    }

    public static void tickLevel(final ServerLevel level) {
        final Set<UUID> casters = LEVEL_CASTERS.get(level.dimension());
        if (casters == null || casters.isEmpty()) {
            return;
        }
        final List<UUID> snapshot = new ArrayList<>(casters);
        for (UUID casterId : snapshot) {
            final SpellInstance instance = ACTIVE_SPELLS.get(casterId);
            if (instance == null) {
                casters.remove(casterId);
                continue;
            }
            if (instance.isStopped()) {
                ACTIVE_SPELLS.remove(casterId);
                casters.remove(casterId);
                continue;
            }
            if (!instance.matchesLevel(level.dimension())) {
                ACTIVE_SPELLS.remove(casterId);
                removeFromLevel(instance);
                instance.stop(level, SpellStopReason.INTERRUPTED);
                final ServerPlayer player = level.getServer().getPlayerList().getPlayer(casterId);
                if (player != null) {
                    MinegickaNetwork.sendSpellTrigger(player, instance.definitionId(), false);
                }
                continue;
            }
            final boolean keepRunning = instance.tick(level);
            if (!keepRunning) {
                ACTIVE_SPELLS.remove(casterId);
                removeFromLevel(instance);
                final ServerPlayer player = level.getServer().getPlayerList().getPlayer(casterId);
                if (player != null) {
                    MinegickaNetwork.sendSpellTrigger(player, instance.definitionId(), false);
                }
            }
        }
        if (casters.isEmpty()) {
            LEVEL_CASTERS.remove(level.dimension());
        }
    }

    public static void tickPlayer(final ServerPlayer player) {
        final SpellInstance instance = ACTIVE_SPELLS.get(player.getUUID());
        if (instance == null || instance.isStopped()) {
            return;
        }
        if (!instance.matchesLevel(player.level().dimension())) {
            stopSpell(player, SpellStopReason.INTERRUPTED);
        }
    }

    private static void removeFromLevel(final SpellInstance instance) {
        final Set<UUID> casters = LEVEL_CASTERS.get(instance.levelKey());
        if (casters != null) {
            casters.remove(instance.casterId());
            if (casters.isEmpty()) {
                LEVEL_CASTERS.remove(instance.levelKey());
            }
        }
    }

    private static ServerLevel resolveLevel(final ServerPlayer player, final ResourceKey<Level> key) {
        final ServerLevel specific = player.server.getLevel(key);
        return specific != null ? specific : player.serverLevel();
    }
}
