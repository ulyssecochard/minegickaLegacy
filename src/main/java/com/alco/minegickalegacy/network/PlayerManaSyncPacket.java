package com.alco.minegickalegacy.network;

import com.alco.minegickalegacy.capability.PlayerManaCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record PlayerManaSyncPacket(CompoundTag data) {
    public static void encode(final PlayerManaSyncPacket packet, final FriendlyByteBuf buf) {
        buf.writeNbt(packet.data);
    }

    public static PlayerManaSyncPacket decode(final FriendlyByteBuf buf) {
        return new PlayerManaSyncPacket(buf.readNbt());
    }

    public static void handle(final PlayerManaSyncPacket packet, final Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context context = contextSupplier.get();
        final var unused = context.enqueueWork(() -> {
            final LocalPlayer player = Minecraft.getInstance().player;
            if (player == null || packet.data == null) {
                return;
            }
            player.getCapability(PlayerManaCapability.CAPABILITY).ifPresent(cap -> {
                cap.deserializeNBT(packet.data.copy());
                cap.clearDirty();
            });
        });
        context.setPacketHandled(true);
    }
}
