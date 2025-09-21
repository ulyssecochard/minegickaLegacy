package com.alco.minegickalegacy.mechanics;

import com.alco.minegickalegacy.MinegickaMod;
import com.alco.minegickalegacy.items.StaffItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public final class StaffAbilities {
    private StaffAbilities() {
    }

    public static void activate(final ServerPlayer player, final InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof StaffItem staff)) {
            MinegickaMod.LOGGER.debug("Ignoring staff ability request: {} is not holding a staff", player.getGameProfile().getName());
            return;
        }
        staff.activateAbility(player.serverLevel(), player, stack);
    }
}
