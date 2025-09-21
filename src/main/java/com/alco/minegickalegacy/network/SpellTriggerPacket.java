package com.alco.minegickalegacy.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SpellTriggerPacket(ResourceLocation spellId, boolean start) {
    public static void encode(final SpellTriggerPacket packet, final FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(packet.spellId);
        buffer.writeBoolean(packet.start);
    }

    public static SpellTriggerPacket decode(final FriendlyByteBuf buffer) {
        final ResourceLocation id = buffer.readResourceLocation();
        final boolean start = buffer.readBoolean();
        return new SpellTriggerPacket(id, start);
    }

    public static void handle(final SpellTriggerPacket packet, final Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context context = contextSupplier.get();
        final var unused = context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.handle(packet)));
        context.setPacketHandled(true);
    }

    private static final class ClientHandler {
        private static void handle(final SpellTriggerPacket packet) {
            com.alco.minegickalegacy.spell.ClientSpellManager.handleSpellTrigger(packet.spellId, packet.start);
        }
    }
}
