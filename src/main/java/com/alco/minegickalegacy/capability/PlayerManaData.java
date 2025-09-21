package com.alco.minegickalegacy.capability;

import com.alco.minegickalegacy.mechanics.Element;
import com.alco.minegickalegacy.registry.MagickRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.Set;

public final class PlayerManaData {
    private static final String TAG_MANA = "Mana";
    private static final String TAG_MAX_MANA = "MaxMana";
    private static final String TAG_UNLOCKED_ELEMENTS = "UnlockedElements";
    private static final String TAG_UNLOCKED_MAGICKS = "UnlockedMagicks";

    private final Player owner;
    private double mana = 1000.0D;
    private double maxMana = 1000.0D;
    private final Set<Element> unlockedElements = new HashSet<>();
    private final Set<ResourceLocation> unlockedMagicks = new HashSet<>();
    private boolean dirty;

    public PlayerManaData(final Player owner) {
        this.owner = owner;
        unlockedElements.add(Element.ICE);
        unlockedElements.add(Element.STEAM);
        dirty = true;
    }

    public double getMana() {
        return mana;
    }

    public double getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(final double value) {
        final double clamped = Math.max(0, value);
        if (Double.compare(maxMana, clamped) != 0) {
            maxMana = clamped;
            if (mana > maxMana) {
                mana = maxMana;
            }
            markDirty();
        }
    }

    public void setMana(final double value) {
        final double clamped = Math.max(0, Math.min(maxMana, value));
        if (Double.compare(mana, clamped) != 0) {
            mana = clamped;
            markDirty();
        }
    }

    public double recoverMana(final double baseRate) {
        final double recover = Math.max(0.0D, baseRate) * ItemManaHelper.getRecoveryMultiplier(owner);
        setMana(mana + recover);
        return mana;
    }

    public boolean consumeMana(final double amount) {
        if (mana < amount) {
            return false;
        }
        setMana(mana - amount);
        return true;
    }

    public void unlockElement(final Element element) {
        if (element != null && unlockedElements.add(element)) {
            markDirty();
        }
    }

    public boolean hasElement(final Element element) {
        return (owner != null && owner.isCreative()) || unlockedElements.contains(element);
    }

    public void unlockMagick(final ResourceLocation id) {
        if (id != null && unlockedMagicks.add(id)) {
            markDirty();
        }
    }

    public boolean hasMagick(final ResourceLocation id) {
        return (owner != null && owner.isCreative()) || unlockedMagicks.contains(id);
    }

    public void unlockAll() {
        boolean changed = unlockedElements.addAll(Set.of(Element.values()));
        changed |= unlockedMagicks.addAll(MagickRegistry.keys());
        if (changed) {
            markDirty();
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public void clearDirty() {
        dirty = false;
    }

    private void markDirty() {
        dirty = true;
    }

    public CompoundTag serializeNBT() {
        final CompoundTag tag = new CompoundTag();
        tag.putDouble(TAG_MANA, mana);
        tag.putDouble(TAG_MAX_MANA, maxMana);

        final ListTag elements = new ListTag();
        for (final Element element : unlockedElements) {
            elements.add(TagHelper.writeElement(element));
        }
        tag.put(TAG_UNLOCKED_ELEMENTS, elements);

        final ListTag magicks = new ListTag();
        for (final ResourceLocation id : unlockedMagicks) {
            final CompoundTag entry = new CompoundTag();
            entry.putString("Id", id.toString());
            magicks.add(entry);
        }
        tag.put(TAG_UNLOCKED_MAGICKS, magicks);
        return tag;
    }

    public void deserializeNBT(final CompoundTag tag) {
        mana = tag.getDouble(TAG_MANA);
        maxMana = tag.getDouble(TAG_MAX_MANA);

        unlockedElements.clear();
        final ListTag elements = tag.getList(TAG_UNLOCKED_ELEMENTS, Tag.TAG_COMPOUND);
        for (final Tag elementTag : elements) {
            final Element element = TagHelper.readElement((CompoundTag) elementTag);
            if (element != null) {
                unlockedElements.add(element);
            }
        }

        unlockedMagicks.clear();
        final ListTag magicks = tag.getList(TAG_UNLOCKED_MAGICKS, Tag.TAG_COMPOUND);
        for (final Tag magickTag : magicks) {
            final CompoundTag entry = (CompoundTag) magickTag;
            final String id = entry.getString("Id");
            if (!id.isEmpty()) {
                final ResourceLocation rl = ResourceLocation.tryParse(id);
                if (rl != null) {
                    unlockedMagicks.add(rl);
                }
            }
        }
        dirty = false;
    }

    public void copyFrom(final PlayerManaData other) {
        mana = other.mana;
        maxMana = other.maxMana;
        unlockedElements.clear();
        unlockedElements.addAll(other.unlockedElements);
        unlockedMagicks.clear();
        unlockedMagicks.addAll(other.unlockedMagicks);
        markDirty();
    }
}
