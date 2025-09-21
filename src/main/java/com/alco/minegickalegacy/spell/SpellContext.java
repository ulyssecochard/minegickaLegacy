package com.alco.minegickalegacy.spell;

import com.alco.minegickalegacy.capability.PlayerManaCapability;
import com.alco.minegickalegacy.capability.PlayerManaData;
import com.alco.minegickalegacy.items.StaffItem;
import com.alco.minegickalegacy.registry.MagickRegistry.MagickDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public final class SpellContext {
    private static final Component NO_MANA_MESSAGE = Component.translatable("item.minegicka.magick_tablet.no_mana");

    private final ServerLevel level;
    private final ServerPlayer caster;
    private final SpellInstance spell;

    public SpellContext(final ServerLevel level, final ServerPlayer caster, final SpellInstance spell) {
        this.level = level;
        this.caster = caster;
        this.spell = spell;
    }

    public ServerLevel level() {
        return level;
    }

    public ServerPlayer caster() {
        return caster;
    }

    public SpellInstance spell() {
        return spell;
    }

    public MagickDefinition definition() {
        return spell.definition();
    }

    public int ticks() {
        return spell.ticksLived();
    }

    public int elementCount() {
        return Math.max(1, spell.combination().size());
    }

    public Optional<PlayerManaData> manaData() {
        return caster.getCapability(PlayerManaCapability.CAPABILITY).resolve();
    }

    public boolean consumeMana(final double amount) {
        return consumeMana(amount, false);
    }

    public boolean consumeMana(final double amount, final boolean notifyOnFailure) {
        if (amount <= 0.0D) {
            return true;
        }
        final Optional<PlayerManaData> data = manaData();
        final boolean success = data.isPresent() && data.get().consumeMana(amount);
        if (!success && notifyOnFailure) {
            caster.displayClientMessage(NO_MANA_MESSAGE, true);
        }
        return success;
    }

    public StaffItem.Stats staffStats() {
        return SpellAttributes.staffStats(caster);
    }

    public void performEffect() {
        spell.definition().effect().perform(caster, spell.definition());
    }
}