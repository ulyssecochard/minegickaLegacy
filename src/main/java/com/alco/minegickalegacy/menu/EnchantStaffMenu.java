package com.alco.minegickalegacy.menu;

import com.alco.minegickalegacy.blockentity.EnchantStaffBlockEntity;
import com.alco.minegickalegacy.items.StaffItem;
import com.alco.minegickalegacy.registry.MinegickaBlocks;
import com.alco.minegickalegacy.registry.MinegickaMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class EnchantStaffMenu extends AbstractContainerMenu {
    private static final int PLAYER_INVENTORY_ROWS = 3;
    private static final int PLAYER_INVENTORY_COLUMNS = 9;
    private static final int HOTBAR_SLOTS = 9;
    private static final int SLOT_SPACING = 18;
    private static final int STAFF_SLOT_X = 40;
    private static final int STAFF_SLOT_Y = 64;
    private static final int INGREDIENT_GRID_START_X = 104;
    private static final int INGREDIENT_GRID_START_Y = 36;
    private static final int PLAYER_INVENTORY_X = 16;
    private static final int PLAYER_INVENTORY_Y = 150;
    private static final int HOTBAR_Y = PLAYER_INVENTORY_Y + PLAYER_INVENTORY_ROWS * SLOT_SPACING + 4;

    private final ContainerLevelAccess access;
    private final IItemHandler itemHandler;
    @Nullable
    private final EnchantStaffBlockEntity blockEntity;

    public static EnchantStaffMenu createClient(final int containerId, final Inventory inventory, final FriendlyByteBuf buffer) {
        final BlockPos pos = buffer.readBlockPos();
        final Level level = inventory.player.level();
        final EnchantStaffBlockEntity blockEntity = level != null && level.getBlockEntity(pos) instanceof EnchantStaffBlockEntity be ? be : null;
        return internalCreate(containerId, inventory, blockEntity, pos);
    }

    public static EnchantStaffMenu createFallback(final int containerId, final Inventory inventory, final BlockPos pos) {
        return internalCreate(containerId, inventory, null, pos);
    }

    public EnchantStaffMenu(final int containerId, final Inventory inventory, final EnchantStaffBlockEntity blockEntity) {
        this(MinegickaMenus.ENCHANT_STAFF.get(), containerId, inventory, blockEntity, blockEntity.getBlockPos());
    }

    private static EnchantStaffMenu internalCreate(final int containerId,
                                                   final Inventory inventory,
                                                   @Nullable final EnchantStaffBlockEntity blockEntity,
                                                   final BlockPos pos) {
        return new EnchantStaffMenu(MinegickaMenus.ENCHANT_STAFF.get(), containerId, inventory, blockEntity, pos);
    }

    private EnchantStaffMenu(final MenuType<?> menuType,
                             final int containerId,
                             final Inventory inventory,
                             @Nullable final EnchantStaffBlockEntity blockEntity,
                             final BlockPos pos) {
        super(menuType, containerId);
        this.access = ContainerLevelAccess.create(Objects.requireNonNull(inventory.player.level()), pos);
        this.itemHandler = blockEntity != null ? blockEntity.getItemHandler() : new ItemStackHandler(EnchantStaffBlockEntity.TOTAL_SLOTS);
        this.blockEntity = blockEntity;

        addStaffSlot();
        addIngredientSlots();
        addPlayerInventory(inventory);
    }

    private void addStaffSlot() {
        addSlot(new StaffSlot(itemHandler, EnchantStaffBlockEntity.STAFF_SLOT, STAFF_SLOT_X, STAFF_SLOT_Y));
    }

    private void addIngredientSlots() {
        int index = EnchantStaffBlockEntity.INGREDIENT_SLOT_START;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                final int x = INGREDIENT_GRID_START_X + col * SLOT_SPACING;
                final int y = INGREDIENT_GRID_START_Y + row * SLOT_SPACING;
                addSlot(new SlotItemHandler(itemHandler, index++, x, y));
            }
        }
    }

    private void addPlayerInventory(final Inventory inventory) {
        for (int row = 0; row < PLAYER_INVENTORY_ROWS; row++) {
            for (int col = 0; col < PLAYER_INVENTORY_COLUMNS; col++) {
                final int slotIndex = col + row * PLAYER_INVENTORY_COLUMNS + HOTBAR_SLOTS;
                final int x = PLAYER_INVENTORY_X + col * SLOT_SPACING;
                final int y = PLAYER_INVENTORY_Y + row * SLOT_SPACING;
                addSlot(new Slot(inventory, slotIndex, x, y));
            }
        }
        for (int col = 0; col < HOTBAR_SLOTS; col++) {
            final int x = PLAYER_INVENTORY_X + col * SLOT_SPACING;
            addSlot(new Slot(inventory, col, x, HOTBAR_Y));
        }
    }

    @Override
    public boolean stillValid(final Player player) {
        return stillValid(access, player, MinegickaBlocks.ENCHANT_STAFF.get());
    }

    @NotNull
    @Override
    public ItemStack quickMoveStack(final Player player, final int index) {
        ItemStack copied = ItemStack.EMPTY;
        final Slot slot = slots.get(index);
        if (slot != null && slot.hasItem()) {
            final ItemStack stack = slot.getItem();
            copied = stack.copy();

            final int tileSlots = EnchantStaffBlockEntity.TOTAL_SLOTS;
            final int playerInventoryStart = tileSlots;
            final int playerInventoryEnd = playerInventoryStart + PLAYER_INVENTORY_ROWS * PLAYER_INVENTORY_COLUMNS;
            final int hotbarStart = playerInventoryEnd;
            final int totalSlots = hotbarStart + HOTBAR_SLOTS;

            if (index < tileSlots) {
                if (!moveItemStackTo(stack, playerInventoryStart, totalSlots, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, copied);
            } else {
                if (isStaff(stack)) {
                    if (!moveItemStackTo(stack, EnchantStaffBlockEntity.STAFF_SLOT, EnchantStaffBlockEntity.STAFF_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!moveItemStackTo(stack, EnchantStaffBlockEntity.INGREDIENT_SLOT_START, tileSlots, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == copied.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }
        return copied;
    }

    @Nullable
    public EnchantStaffBlockEntity getBlockEntity() {
        return blockEntity;
    }

    private static boolean isStaff(final ItemStack stack) {
        return stack.getItem() instanceof StaffItem;
    }

    private static class StaffSlot extends SlotItemHandler {
        StaffSlot(final IItemHandler handler, final int index, final int x, final int y) {
            super(handler, index, x, y);
        }

        @Override
        public boolean mayPlace(@NotNull final ItemStack stack) {
            return stack.getItem() instanceof StaffItem;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }
}
