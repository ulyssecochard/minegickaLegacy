package com.alco.minegickalegacy.network;

import com.alco.minegickalegacy.mechanics.StaffAbilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ActiveStaffPacket(InteractionHand hand) {
    public static void encode(final ActiveStaffPacket packet, final FriendlyByteBuf buffer) {
        buffer.writeEnum(packet.hand);
    }

    public static ActiveStaffPacket decode(final FriendlyByteBuf buffer) {
        return new ActiveStaffPacket(buffer.readEnum(InteractionHand.class));
    }

    public static void handle(final ActiveStaffPacket packet, final Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context context = contextSupplier.get();
        final var unused = context.enqueueWork(() -> {
            if (context.getSender() != null) {
                StaffAbilities.activate(context.getSender(), packet.hand);
            }
        });
        context.setPacketHandled(true);
    }
}
