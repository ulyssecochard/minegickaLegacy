package com.alco.minegickalegacy.mechanics;

import com.alco.minegickalegacy.MinegickaMod;
import com.alco.minegickalegacy.blockentity.EnchantStaffBlockEntity;
import com.alco.minegickalegacy.items.StaffItem;
import com.alco.minegickalegacy.menu.EnchantStaffMenu;
import com.alco.minegickalegacy.registry.MinegickaItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Applies lightweight legacy-inspired stat adjustments to staffs based on the ingredients placed in the enchanting station.
 */
public final class StaffEnchantingService {
    private static final Map<Item, StatDelta> MODIFIERS = new HashMap<>();

    static {
        register(MinegickaItems.THINGY.get(), 0.001D, 0.001D, 0.001D, 0.001D);
        register(MinegickaItems.THINGY_GOOD.get(), 0.016D, 0.016D, 0.016D, 0.016D);
        register(MinegickaItems.THINGY_SUPER.get(), 0.256D, 0.256D, 0.256D, 0.256D);

        register(Items.IRON_INGOT, 0.001D, 0.001D, 0.0D, 0.0D);
        register(Items.GOLD_INGOT, 0.0D, 0.0D, 0.001D, 0.001D);
        register(Items.IRON_BLOCK, 0.01D, 0.01D, 0.0D, 0.0D);
        register(Items.GOLD_BLOCK, 0.0D, 0.0D, 0.01D, 0.01D);
        register(Items.DIAMOND, 0.02D, -0.02D, 0.0D, 0.0D);
        register(Items.EMERALD, -0.02D, 0.02D, 0.0D, 0.0D);
        register(Items.SUGAR, 0.0D, 0.01D, -0.01D, 0.0D);
        register(Items.NETHER_STAR, 1.0D, 1.0D, 1.0D, 1.0D);
        register(Items.REDSTONE, 0.0D, -0.005D, 0.01D, 0.01D);
        register(Items.GUNPOWDER, 0.0005D, -0.0005D, -0.0005D, -0.0005D);
    }

    private StaffEnchantingService() {
    }

    public static void handleRequest(final ServerPlayer player, final int slotIndex, final List<ItemStack> ignoredIngredients) {
        if (!(player.containerMenu instanceof EnchantStaffMenu menu)) {
            MinegickaMod.LOGGER.debug("Ignoring enchant request from {} - menu mismatch", player.getGameProfile().getName());
            return;
        }
        final EnchantStaffBlockEntity blockEntity = menu.getBlockEntity();
        if (blockEntity == null) {
            MinegickaMod.LOGGER.debug("Ignoring enchant request from {} - no block entity", player.getGameProfile().getName());
            return;
        }
        if (slotIndex != EnchantStaffBlockEntity.STAFF_SLOT) {
            MinegickaMod.LOGGER.debug("Ignoring enchant request from {} - invalid slot {}", player.getGameProfile().getName(), slotIndex);
            return;
        }
        performEnchant(blockEntity, player);
    }

    public static void performEnchant(final EnchantStaffBlockEntity blockEntity, final Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        final IItemHandler handler = blockEntity.getItemHandler();
        final ItemStack staffStack = handler.getStackInSlot(EnchantStaffBlockEntity.STAFF_SLOT);
        if (!(staffStack.getItem() instanceof StaffItem)) {
            serverPlayer.displayClientMessage(Component.translatable("block.minegicka.enchant_staff.no_staff"), true);
            return;
        }

        final List<ItemStack> ingredients = collectIngredients(handler);
        if (ingredients.isEmpty()) {
            serverPlayer.displayClientMessage(Component.translatable("block.minegicka.enchant_staff.no_ingredients"), true);
            return;
        }

        final StatDelta delta = computeDelta(ingredients);
        if (delta.isZero()) {
            serverPlayer.displayClientMessage(Component.translatable("block.minegicka.enchant_staff.no_effect"), true);
            return;
        }

        applyStats(staffStack, delta);
        clearIngredients(handler);
        blockEntity.setChanged();
        serverPlayer.level().levelEvent(1031, blockEntity.getBlockPos(), 0); // enchant sound
        serverPlayer.displayClientMessage(Component.translatable("block.minegicka.enchant_staff.success"), true);
    }

    private static List<ItemStack> collectIngredients(final IItemHandler handler) {
        final List<ItemStack> items = new ArrayList<>();
        for (int slot = EnchantStaffBlockEntity.INGREDIENT_SLOT_START; slot < EnchantStaffBlockEntity.TOTAL_SLOTS; slot++) {
            final ItemStack stack = handler.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                items.add(stack.copy());
            }
        }
        return items;
    }

    private static void clearIngredients(final IItemHandler handler) {
        for (int slot = EnchantStaffBlockEntity.INGREDIENT_SLOT_START; slot < EnchantStaffBlockEntity.TOTAL_SLOTS; slot++) {
            handler.extractItem(slot, handler.getStackInSlot(slot).getCount(), false);
        }
    }

    private static StatDelta computeDelta(final List<ItemStack> ingredients) {
        StatDelta total = StatDelta.ZERO;
        for (final ItemStack stack : ingredients) {
            final StatDelta modifier = MODIFIERS.get(stack.getItem());
            if (modifier != null) {
                total = total.add(modifier.scale(stack.getCount()));
            }
        }
        return total;
    }

    private static void applyStats(final ItemStack staffStack, final StatDelta delta) {
        final StaffItem.Stats current = StaffItem.getEffectiveStats(staffStack);
        final StaffItem.Stats updated = new StaffItem.Stats(
                clamp(current.power() + delta.power()),
                clamp(current.attackSpeed() + delta.attackSpeed()),
                clamp(current.consume() + delta.consume()),
                clamp(current.recover() + delta.recover())
        );
        StaffItem.applyEnchantment(staffStack, updated);
    }

    private static double clamp(final double value) {
        return Math.max(0.001D, value);
    }

    private static void register(final Item item, final double power, final double attackSpeed, final double consume, final double recover) {
        MODIFIERS.put(item, new StatDelta(power, attackSpeed, consume, recover));
    }

    private record StatDelta(double power, double attackSpeed, double consume, double recover) {
        static final StatDelta ZERO = new StatDelta(0.0D, 0.0D, 0.0D, 0.0D);

        boolean isZero() {
            return Math.abs(power) < 1.0E-9D
                    && Math.abs(attackSpeed) < 1.0E-9D
                    && Math.abs(consume) < 1.0E-9D
                    && Math.abs(recover) < 1.0E-9D;
        }

        StatDelta add(final StatDelta other) {
            return new StatDelta(power + other.power, attackSpeed + other.attackSpeed, consume + other.consume, recover + other.recover);
        }

        StatDelta scale(final int count) {
            if (count <= 1) {
                return this;
            }
            return new StatDelta(power * count, attackSpeed * count, consume * count, recover * count);
        }
    }
}
