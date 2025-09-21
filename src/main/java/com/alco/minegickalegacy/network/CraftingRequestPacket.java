package com.alco.minegickalegacy.network;

import com.alco.minegickalegacy.mechanics.CraftingService;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record CraftingRequestPacket(ResourceLocation recipeId, int repeat) {
    public static void encode(final CraftingRequestPacket packet, final FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(packet.recipeId);
        buffer.writeVarInt(packet.repeat);
    }

    public static CraftingRequestPacket decode(final FriendlyByteBuf buffer) {
        final ResourceLocation id = buffer.readResourceLocation();
        final int repeat = buffer.readVarInt();
        return new CraftingRequestPacket(id, repeat);
    }

    public static void handle(final CraftingRequestPacket packet, final Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context context = contextSupplier.get();
        final var unused = context.enqueueWork(() -> {
            if (context.getSender() != null) {
                CraftingService.handleRequest(context.getSender(), packet.recipeId, Math.max(1, packet.repeat));
            }
        });
        context.setPacketHandled(true);
    }
}
