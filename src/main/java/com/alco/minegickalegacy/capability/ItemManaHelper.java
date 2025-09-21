package com.alco.minegickalegacy.capability;

import com.alco.minegickalegacy.items.StaffItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class ItemManaHelper {
    private ItemManaHelper() {
    }

    public static double getRecoveryMultiplier(final Player player) {
        if (player == null) {
            return 1.0D;
        }
        final ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() instanceof StaffItem) {
            return StaffItem.getEffectiveStats(mainHand).recover();
        }
        final ItemStack offHand = player.getOffhandItem();
        if (offHand.getItem() instanceof StaffItem) {
            return StaffItem.getEffectiveStats(offHand).recover();
        }
        return 1.0D;
    }
}
