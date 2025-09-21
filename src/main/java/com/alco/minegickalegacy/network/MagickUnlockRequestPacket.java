package com.alco.minegickalegacy.network;

import com.alco.minegickalegacy.capability.PlayerManaCapability;
import com.alco.minegickalegacy.capability.PlayerManaData;
import com.alco.minegickalegacy.registry.MagickRegistry;
import com.alco.minegickalegacy.registry.MagickRegistry.MagickDefinition;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public record MagickUnlockRequestPacket(Optional<ResourceLocation> magickId) {
    public static void encode(final MagickUnlockRequestPacket packet, final FriendlyByteBuf buffer) {
        buffer.writeBoolean(packet.magickId.isPresent());
        packet.magickId.ifPresent(buffer::writeResourceLocation);
    }

    public static MagickUnlockRequestPacket decode(final FriendlyByteBuf buffer) {
        final boolean present = buffer.readBoolean();
        Optional<ResourceLocation> id = Optional.empty();
        if (present) {
            id = Optional.of(buffer.readResourceLocation());
        }
        return new MagickUnlockRequestPacket(id);
    }

    public static void handle(final MagickUnlockRequestPacket packet, final Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context context = contextSupplier.get();
        final var unused = context.enqueueWork(() -> {
            final ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }
            player.getCapability(PlayerManaCapability.CAPABILITY).ifPresent(data -> {
                boolean changed = false;
                if (packet.magickId.isPresent()) {
                    final ResourceLocation id = packet.magickId.orElseThrow();
                    final Optional<MagickDefinition> definition = MagickRegistry.byId(id);
                    if (definition.isPresent()) {
                        if (!data.hasMagick(id)) {
                            data.unlockMagick(id);
                            changed = true;
                        }
                    }
                } else {
                    data.unlockAll();
                    changed = true;
                }
                if (changed) {
                    MinegickaNetwork.sendTo(player, data);
                    data.clearDirty();
                }
            });
        });
        context.setPacketHandled(true);
    }
}
