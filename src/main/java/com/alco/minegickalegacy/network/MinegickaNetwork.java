package com.alco.minegickalegacy.network;

import com.alco.minegickalegacy.MinegickaMod;
import com.alco.minegickalegacy.capability.PlayerManaData;
import com.alco.minegickalegacy.mechanics.Element;
import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.List;
import java.util.Optional;

public final class MinegickaNetwork {
    private static final String PROTOCOL_VERSION = "2";
    private static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(ResourceLocation.fromNamespaceAndPath(MinegickaMod.MODID, "main"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();

    private static int nextMessageId = 0;

    private MinegickaNetwork() {
    }

    public static void register() {
        CHANNEL.registerMessage(nextMessageId++, NetworkHandshakePacket.class,
                NetworkHandshakePacket::encode,
                NetworkHandshakePacket::decode,
                NetworkHandshakePacket::handle);
        CHANNEL.registerMessage(nextMessageId++, ElementCombinationSyncPacket.class,
                ElementCombinationSyncPacket::encode,
                ElementCombinationSyncPacket::decode,
                ElementCombinationSyncPacket::handle);
        CHANNEL.registerMessage(nextMessageId++, SpellTriggerPacket.class,
                SpellTriggerPacket::encode,
                SpellTriggerPacket::decode,
                SpellTriggerPacket::handle);
        CHANNEL.registerMessage(nextMessageId++, PlayerManaSyncPacket.class,
                PlayerManaSyncPacket::encode,
                PlayerManaSyncPacket::decode,
                PlayerManaSyncPacket::handle);
        CHANNEL.registerMessage(nextMessageId++, SpellCastRequestPacket.class,
                SpellCastRequestPacket::encode,
                SpellCastRequestPacket::decode,
                SpellCastRequestPacket::handle);
        CHANNEL.registerMessage(nextMessageId++, MagickUnlockRequestPacket.class,
                MagickUnlockRequestPacket::encode,
                MagickUnlockRequestPacket::decode,
                MagickUnlockRequestPacket::handle);
        CHANNEL.registerMessage(nextMessageId++, CraftingRequestPacket.class,
                CraftingRequestPacket::encode,
                CraftingRequestPacket::decode,
                CraftingRequestPacket::handle);
        CHANNEL.registerMessage(nextMessageId++, SelectCraftingRecipePacket.class,
                SelectCraftingRecipePacket::encode,
                SelectCraftingRecipePacket::decode,
                SelectCraftingRecipePacket::handle);
        CHANNEL.registerMessage(nextMessageId++, EnchantStaffPacket.class,
                EnchantStaffPacket::encode,
                EnchantStaffPacket::decode,
                EnchantStaffPacket::handle);
        CHANNEL.registerMessage(nextMessageId++, ActiveStaffPacket.class,
                ActiveStaffPacket::encode,
                ActiveStaffPacket::decode,
                ActiveStaffPacket::handle);
    }

    public static String protocolVersion() {
        return PROTOCOL_VERSION;
    }

    public static void sendHandshake(final ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new NetworkHandshakePacket(PROTOCOL_VERSION));
    }

    public static void sendCombination(final ServerPlayer player, final List<Element> combination) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ElementCombinationSyncPacket(List.copyOf(combination)));
    }

    public static void sendTo(final ServerPlayer player, final PlayerManaData data) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new PlayerManaSyncPacket(data.serializeNBT()));
    }

    public static void sendSpellTrigger(final ServerPlayer player, final ResourceLocation spellId, final boolean start) {
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SpellTriggerPacket(spellId, start));
    }

    public static void sendSpellCastRequest(final List<Element> combination) {
        CHANNEL.sendToServer(new SpellCastRequestPacket(List.copyOf(combination)));
    }

    public static void requestMagickUnlock(final ResourceLocation id) {
        CHANNEL.sendToServer(new MagickUnlockRequestPacket(Optional.of(id)));
    }

    public static void requestUnlockAllMagicks() {
        CHANNEL.sendToServer(new MagickUnlockRequestPacket(Optional.empty()));
    }

    public static void requestCraft(final ResourceLocation recipeId, final int repeat) {
        CHANNEL.sendToServer(new CraftingRequestPacket(recipeId, repeat));
    }

    public static void requestSelectRecipe(final net.minecraft.core.BlockPos pos, @Nullable final ResourceLocation recipeId) {
        CHANNEL.sendToServer(new SelectCraftingRecipePacket(pos, recipeId));
    }

    public static void requestEnchant(final int slotIndex, final List<ItemStack> ingredients) {
        CHANNEL.sendToServer(new EnchantStaffPacket(slotIndex, List.copyOf(ingredients)));
    }

    public static void requestStaffAbility(final InteractionHand hand) {
        CHANNEL.sendToServer(new ActiveStaffPacket(hand));
    }
}
