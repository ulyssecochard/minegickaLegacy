package com.alco.minegickalegacy.data;

import com.alco.minegickalegacy.capability.PlayerManaData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManaSavedData extends SavedData {
    private static final String DATA_NAME = "minegicka_player_mana";

    private final Map<UUID, CompoundTag> storedData = new HashMap<>();

    public PlayerManaSavedData() {
    }

    public static PlayerManaSavedData get(final ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(PlayerManaSavedData::load, PlayerManaSavedData::new, DATA_NAME);
    }

    public void store(final UUID uuid, final PlayerManaData data) {
        if (uuid == null || data == null) {
            return;
        }
        storedData.put(uuid, data.serializeNBT());
        setDirty();
    }

    public void apply(final Player player, final PlayerManaData capability) {
        final CompoundTag tag = storedData.get(player.getUUID());
        if (tag != null) {
            capability.deserializeNBT(tag.copy());
            capability.clearDirty();
        }
    }

    public static PlayerManaSavedData load(final CompoundTag tag) {
        final PlayerManaSavedData data = new PlayerManaSavedData();
        final CompoundTag entries = tag.getCompound("Entries");
        for (final String key : entries.getAllKeys()) {
            data.storedData.put(UUID.fromString(key), entries.getCompound(key));
        }
        return data;
    }

    @Override
    public CompoundTag save(final CompoundTag tag) {
        final CompoundTag entries = new CompoundTag();
        storedData.forEach((uuid, nbt) -> entries.put(uuid.toString(), nbt));
        tag.put("Entries", entries);
        return tag;
    }
}
