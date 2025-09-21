package com.alco.minegickalegacy.network;

import com.alco.minegickalegacy.mechanics.Element;
import com.alco.minegickalegacy.registry.MagickRegistry;
import com.alco.minegickalegacy.spell.ServerSpellManager;
import com.alco.minegickalegacy.spell.SpellCasting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record SpellCastRequestPacket(List<Element> combination) {
    public static void encode(final SpellCastRequestPacket packet, final FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.combination.size());
        for (final Element element : packet.combination) {
            buffer.writeEnum(element);
        }
    }

    public static SpellCastRequestPacket decode(final FriendlyByteBuf buffer) {
        final int size = buffer.readVarInt();
        final List<Element> elements = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            elements.add(buffer.readEnum(Element.class));
        }
        return new SpellCastRequestPacket(elements);
    }

    public static void handle(final SpellCastRequestPacket packet, final Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context context = contextSupplier.get();
        final var unused = context.enqueueWork(() -> {
            final Player sender = context.getSender();
            if (!(sender instanceof ServerPlayer serverPlayer)) {
                return;
            }
            if (packet.combination.isEmpty()) {
                ServerSpellManager.stopSpell(serverPlayer);
                return;
            }
            MagickRegistry.findByCombination(packet.combination).ifPresentOrElse(
                    definition -> SpellCasting.cast(serverPlayer, definition),
                    () -> serverPlayer.displayClientMessage(Component.translatable("spell.minegicka.unknown"), true)
            );
        });
        context.setPacketHandled(true);
    }
}


