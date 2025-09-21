package com.alco.minegickalegacy.network;

import com.alco.minegickalegacy.mechanics.StaffEnchantingService;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record EnchantStaffPacket(int slotIndex, List<ItemStack> ingredients) {
    public static void encode(final EnchantStaffPacket packet, final FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.slotIndex);
        buffer.writeCollection(packet.ingredients, FriendlyByteBuf::writeItem);
    }

    public static EnchantStaffPacket decode(final FriendlyByteBuf buffer) {
        final int slot = buffer.readVarInt();
        final List<ItemStack> items = buffer.readCollection(ArrayList::new, FriendlyByteBuf::readItem);
        return new EnchantStaffPacket(slot, List.copyOf(items));
    }

    public static void handle(final EnchantStaffPacket packet, final Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context context = contextSupplier.get();
        final var unused = context.enqueueWork(() -> {
            final ServerPlayer player = context.getSender();
            if (player != null) {
                StaffEnchantingService.handleRequest(player, packet.slotIndex, packet.ingredients);
            }
        });
        context.setPacketHandled(true);
    }
}
