package com.alco.minegickalegacy.spell;

import com.alco.minegickalegacy.mechanics.Element;
import com.alco.minegickalegacy.registry.MagickRegistry.MagickDefinition;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

public final class SpellInstance {
    private final UUID casterId;
    private final ResourceLocation definitionId;
    private final MagickDefinition definition;
    private final List<Element> combination;
    private final SpellExecutor executor;
    private final SpellForm form;
    private final ResourceKey<Level> levelKey;

    private int ticksLived;
    private boolean started;
    private boolean stopped;

    private SpellInstance(final UUID casterId,
                          final ResourceLocation definitionId,
                          final MagickDefinition definition,
                          final List<Element> combination,
                          final SpellExecutor executor,
                          final SpellForm form,
                          final ResourceKey<Level> levelKey) {
        this.casterId = casterId;
        this.definitionId = definitionId;
        this.definition = definition;
        this.combination = combination;
        this.executor = executor;
        this.form = form;
        this.levelKey = levelKey;
        this.ticksLived = 0;
        this.started = false;
        this.stopped = false;
    }

    public static SpellInstance create(final ServerPlayer player,
                                       final MagickDefinition definition,
                                       final SpellExecutor executor,
                                       final SpellForm form) {
        return new SpellInstance(
                player.getUUID(),
                definition.id(),
                definition,
                List.copyOf(definition.combination()),
                executor,
                form,
                player.level().dimension()
        );
    }

    public UUID casterId() {
        return casterId;
    }

    public ResourceLocation definitionId() {
        return definitionId;
    }

    public MagickDefinition definition() {
        return definition;
    }

    public List<Element> combination() {
        return combination;
    }

    public SpellForm form() {
        return form;
    }

    public int ticksLived() {
        return ticksLived;
    }

    public ResourceKey<Level> levelKey() {
        return levelKey;
    }

    public boolean matchesLevel(final ResourceKey<Level> key) {
        return levelKey.equals(key);
    }

    public boolean isStopped() {
        return stopped;
    }

    public boolean start(final ServerPlayer player) {
        if (stopped) {
            return false;
        }
        if (started) {
            return !stopped;
        }
        started = true;
        final ServerLevel level = player.serverLevel();
        final SpellContext context = new SpellContext(level, player, this);
        final boolean keepRunning = executor.onStart(context);
        if (!keepRunning) {
            stopped = true;
            executor.onStop(context, SpellStopReason.FINISHED);
        }
        return keepRunning;
    }

    public boolean tick(final ServerLevel level) {
        if (stopped) {
            return false;
        }
        final ServerPlayer player = level.getServer().getPlayerList().getPlayer(casterId);
        if (player == null || player.isRemoved() || player.isDeadOrDying()) {
            stop(level, SpellStopReason.CASTER_INVALID);
            return false;
        }
        if (!player.level().dimension().equals(levelKey)) {
            stop(level, SpellStopReason.CASTER_INVALID);
            return false;
        }
        ticksLived++;
        final SpellContext context = new SpellContext(level, player, this);
        final boolean keepRunning = executor.tick(context);
        if (!keepRunning) {
            internalStop(context, SpellStopReason.FINISHED);
        }
        return keepRunning;
    }

    public void stop(final ServerLevel level, final SpellStopReason reason) {
        if (stopped) {
            return;
        }
        final ServerPlayer player = level.getServer().getPlayerList().getPlayer(casterId);
        if (player != null) {
            internalStop(new SpellContext(level, player, this), reason);
        } else {
            stopped = true;
        }
    }

    private void internalStop(final SpellContext context, final SpellStopReason reason) {
        if (stopped) {
            return;
        }
        stopped = true;
        if (context != null) {
            executor.onStop(context, reason);
        }
    }
}
