package com.alco.minegickalegacy.spell;

import com.alco.minegickalegacy.items.StaffItem;
import net.minecraft.world.entity.player.Player;

public final class SpellAttributes {
    private static final StaffItem.Stats DEFAULT_STATS = new StaffItem.Stats(1.0D, 1.0D, 1.0D, 1.0D);

    private SpellAttributes() {
    }

    public static StaffItem.Stats staffStats(final Player player) {
        if (player == null) {
            return DEFAULT_STATS;
        }
        if (player.getMainHandItem().getItem() instanceof StaffItem) {
            return StaffItem.getEffectiveStats(player.getMainHandItem());
        }
        if (player.getOffhandItem().getItem() instanceof StaffItem) {
            return StaffItem.getEffectiveStats(player.getOffhandItem());
        }
        return DEFAULT_STATS;
    }

    public static double sustainCost(final SpellContext context, final double perElementMultiplier) {
        final StaffItem.Stats stats = staffStats(context.caster());
        final double consume = Math.max(0.1D, stats.consume());
        final int elements = Math.max(1, context.elementCount());
        return elements * perElementMultiplier * consume;
    }
}
