package com.alco.minegickalegacy.network;

import com.alco.minegickalegacy.mechanics.Element;
import com.alco.minegickalegacy.spell.ElementSelectionManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record ElementCombinationSyncPacket(List<Element> combination) {
    public static void encode(final ElementCombinationSyncPacket packet, final FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.combination.size());
        for (Element element : packet.combination) {
            buffer.writeEnum(element);
        }
    }

    public static ElementCombinationSyncPacket decode(final FriendlyByteBuf buffer) {
        final int size = buffer.readVarInt();
        final List<Element> elements = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            elements.add(buffer.readEnum(Element.class));
        }
        return new ElementCombinationSyncPacket(List.copyOf(elements));
    }

    public static void handle(final ElementCombinationSyncPacket packet, final Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context context = contextSupplier.get();
        final var unused = context.enqueueWork(() -> ElementSelectionManager.setCombination(packet.combination));
        context.setPacketHandled(true);
    }
}
