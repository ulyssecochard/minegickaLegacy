package com.alco.minegickalegacy.items;

import com.alco.minegickalegacy.capability.PlayerManaCapability;
import com.alco.minegickalegacy.capability.PlayerManaData;
import com.alco.minegickalegacy.registry.MagickRegistry;
import com.alco.minegickalegacy.network.MinegickaNetwork;
import com.alco.minegickalegacy.registry.MagickRegistry.MagickDefinition;
import com.alco.minegickalegacy.spell.SpellCasting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MagickTabletItem extends Item {
    public MagickTabletItem(final Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(PlayerManaCapability.CAPABILITY)
                    .ifPresent(data -> handleUse(serverLevel, serverPlayer, data));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
    private void handleUse(final ServerLevel level, final ServerPlayer player, final PlayerManaData data) {
        if (player.isShiftKeyDown()) {
            data.unlockAll();
            MinegickaNetwork.sendTo(player, data);
            data.clearDirty();
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.PLAYERS, 0.8F, 1.2F);
            player.displayClientMessage(Component.translatable("item.minegicka.magick_tablet.unlock_all"), true);
            return;
        }

        final var definition = MagickRegistry.first();
        if (definition.isEmpty()) {
            return;
        }
        final MagickDefinition def = definition.get();
        final boolean unlocked = data.hasMagick(def.id());
        if (!unlocked) {
            data.unlockMagick(def.id());
            MinegickaNetwork.sendTo(player, data);
            data.clearDirty();
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.PLAYERS, 0.8F, 1.2F);
            player.displayClientMessage(Component.translatable("item.minegicka.magick_tablet.unlock", def.displayName()), true);
            return;
        }

        if (SpellCasting.cast(player, def)) {
            player.displayClientMessage(Component.translatable("item.minegicka.magick_tablet.cast", def.displayName()), true);
        } else {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.VILLAGER_NO, SoundSource.PLAYERS, 0.6F, 0.9F);
            player.displayClientMessage(Component.translatable("item.minegicka.magick_tablet.no_mana"), true);
        }
    }
}





