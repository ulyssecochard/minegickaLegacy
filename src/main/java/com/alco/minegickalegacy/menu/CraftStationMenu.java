package com.alco.minegickalegacy.menu;

import com.alco.minegickalegacy.blockentity.CraftStationBlockEntity;
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

public class CraftStationMenu extends AbstractContainerMenu {
    private static final int PLAYER_INVENTORY_ROWS = 3;
    private static final int PLAYER_INVENTORY_COLUMNS = 9;
    private static final int HOTBAR_SLOTS = 9;
    private static final int SLOT_SPACING = 18;
    private static final int PLAYER_INVENTORY_X = 16;
    private static final int PLAYER_INVENTORY_Y = 140;
    private static final int HOTBAR_Y = PLAYER_INVENTORY_Y + PLAYER_INVENTORY_ROWS * SLOT_SPACING + 4;
    private static final int OUTPUT_SLOT_X = 196;
    private static final int OUTPUT_SLOT_Y = 74;

    private final ContainerLevelAccess access;
    private final IItemHandler itemHandler;
    @Nullable
    private final CraftStationBlockEntity blockEntity;

    public static CraftStationMenu createClient(final int containerId, final Inventory inventory, final FriendlyByteBuf buffer) {
        final BlockPos pos = buffer.readBlockPos();
        final Level level = inventory.player.level();
        final CraftStationBlockEntity blockEntity = level != null && level.getBlockEntity(pos) instanceof CraftStationBlockEntity be ? be : null;
        return internalCreate(containerId, inventory, blockEntity, pos);
    }

    public static CraftStationMenu createFallback(final int containerId, final Inventory inventory, final BlockPos pos) {
        return internalCreate(containerId, inventory, null, pos);
    }

    public CraftStationMenu(final int containerId, final Inventory inventory, final CraftStationBlockEntity blockEntity) {
        this(MinegickaMenus.CRAFT_STATION.get(), containerId, inventory, blockEntity, blockEntity.getBlockPos());
    }

    private static CraftStationMenu internalCreate(final int containerId,
                                                   final Inventory inventory,
                                                   @Nullable final CraftStationBlockEntity blockEntity,
                                                   final BlockPos pos) {
        return new CraftStationMenu(MinegickaMenus.CRAFT_STATION.get(), containerId, inventory, blockEntity, pos);
    }

    private CraftStationMenu(final MenuType<?> menuType,
                             final int containerId,
                             final Inventory inventory,
                             @Nullable final CraftStationBlockEntity blockEntity,
                             final BlockPos pos) {
        super(menuType, containerId);
        this.access = ContainerLevelAccess.create(Objects.requireNonNull(inventory.player.level()), pos);
        this.itemHandler = blockEntity != null ? blockEntity.getItemHandler() : new ItemStackHandler(CraftStationBlockEntity.TOTAL_SLOTS);
        this.blockEntity = blockEntity;

        addOutputSlot();
        addPlayerInventory(inventory);
    }

    private void addOutputSlot() {
        addSlot(new ResultSlot(itemHandler, CraftStationBlockEntity.OUTPUT_SLOT_INDEX, OUTPUT_SLOT_X, OUTPUT_SLOT_Y));
    }

    private void addPlayerInventory(final Inventory inventory) {
        for (int row = 0; row < PLAYER_INVENTORY_ROWS; row++) {
            for (int col = 0; col < PLAYER_INVENTORY_COLUMNS; col++) {
                final int index = col + row * PLAYER_INVENTORY_COLUMNS + HOTBAR_SLOTS;
                final int x = PLAYER_INVENTORY_X + col * SLOT_SPACING;
                final int y = PLAYER_INVENTORY_Y + row * SLOT_SPACING;
                addSlot(new Slot(inventory, index, x, y));
            }
        }
        for (int col = 0; col < HOTBAR_SLOTS; col++) {
            final int x = PLAYER_INVENTORY_X + col * SLOT_SPACING;
            addSlot(new Slot(inventory, col, x, HOTBAR_Y));
        }
    }

    @Override
    public boolean stillValid(final Player player) {
        return stillValid(access, player, MinegickaBlocks.CRAFT_STATION.get());
    }

    @NotNull
    @Override
    public ItemStack quickMoveStack(final Player player, final int index) {
        ItemStack copied = ItemStack.EMPTY;
        final Slot slot = slots.get(index);
        if (slot != null && slot.hasItem()) {
            final ItemStack stack = slot.getItem();
            copied = stack.copy();

            final int tileSlots = CraftStationBlockEntity.TOTAL_SLOTS;
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
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (!stack.isEmpty() && stack.getCount() == copied.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }
        return copied;
    }

    @Nullable
    public CraftStationBlockEntity getBlockEntity() {
        return blockEntity;
    }

    private class ResultSlot extends SlotItemHandler {
        ResultSlot(final IItemHandler handler, final int index, final int x, final int y) {
            super(handler, index, x, y);
        }

        @Override
        public boolean mayPlace(@NotNull final ItemStack stack) {
            return false;
        }

        @Override
        public boolean mayPickup(final Player player) {
            return false;
        }
    }
}
