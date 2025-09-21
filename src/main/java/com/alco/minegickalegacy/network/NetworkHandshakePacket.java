package com.alco.minegickalegacy.network;

import com.alco.minegickalegacy.MinegickaMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record NetworkHandshakePacket(String protocolVersion) {
    public static void encode(final NetworkHandshakePacket packet, final FriendlyByteBuf buffer) {
        buffer.writeUtf(packet.protocolVersion);
    }

    public static NetworkHandshakePacket decode(final FriendlyByteBuf buffer) {
        return new NetworkHandshakePacket(buffer.readUtf(16));
    }

    public static void handle(final NetworkHandshakePacket packet, final Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context context = contextSupplier.get();
        final var unused = context.enqueueWork(() -> {
            final String expected = MinegickaNetwork.protocolVersion();
            if (!expected.equals(packet.protocolVersion)) {
                MinegickaMod.LOGGER.error("Minegicka network protocol mismatch: expected {} but received {}", expected, packet.protocolVersion);
            }
        });
        context.setPacketHandled(true);
    }
}
