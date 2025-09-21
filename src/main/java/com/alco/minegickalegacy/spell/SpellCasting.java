package com.alco.minegickalegacy.spell;

import com.alco.minegickalegacy.capability.PlayerManaCapability;
import com.alco.minegickalegacy.registry.MagickRegistry;
import com.alco.minegickalegacy.registry.MagickRegistry.MagickDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public final class SpellCasting {
    private SpellCasting() {
    }

    public static boolean cast(final ServerPlayer player, final ResourceLocation id) {
        return MagickRegistry.byId(id).map(def -> cast(player, def)).orElse(false);
    }

    public static boolean cast(final ServerPlayer player, final MagickDefinition definition) {
        return player.getCapability(PlayerManaCapability.CAPABILITY).map(data -> {
            if (!data.hasMagick(definition.id())) {
                return false;
            }
            if (!data.consumeMana(definition.baseManaCost())) {
                return false;
            }
            ServerSpellManager.startSpell(player, definition);
            return true;
        }).orElse(false);
    }
}
