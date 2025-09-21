package com.alco.minegickalegacy.network;

import com.alco.minegickalegacy.blockentity.CraftStationBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public record SelectCraftingRecipePacket(BlockPos pos, @Nullable ResourceLocation recipeId) {
    public static void encode(final SelectCraftingRecipePacket packet, final FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos);
        buffer.writeBoolean(packet.recipeId != null);
        if (packet.recipeId != null) {
            buffer.writeResourceLocation(packet.recipeId);
        }
    }

    public static SelectCraftingRecipePacket decode(final FriendlyByteBuf buffer) {
        final BlockPos pos = buffer.readBlockPos();
        final ResourceLocation recipeId = buffer.readBoolean() ? buffer.readResourceLocation() : null;
        return new SelectCraftingRecipePacket(pos, recipeId);
    }

    public static void handle(final SelectCraftingRecipePacket packet, final Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context context = contextSupplier.get();
        final var unused = context.enqueueWork(() -> {
            final ServerLevel level = context.getSender() != null ? context.getSender().serverLevel() : null;
            if (level == null) {
                return;
            }
            if (level.getBlockEntity(packet.pos) instanceof CraftStationBlockEntity craftStation) {
                craftStation.setCurrentRecipe(packet.recipeId);
            }
        });
        context.setPacketHandled(true);
    }
}
