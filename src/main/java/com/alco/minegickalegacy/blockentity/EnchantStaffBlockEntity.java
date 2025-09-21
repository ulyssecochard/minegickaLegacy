package com.alco.minegickalegacy.blockentity;

import com.alco.minegickalegacy.items.StaffItem;
import com.alco.minegickalegacy.menu.EnchantStaffMenu;
import com.alco.minegickalegacy.registry.MinegickaBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Clearable;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.SimpleContainer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Stores the staff enchanting inventory (dedicated staff slot + ingredient matrix).
 */
public class EnchantStaffBlockEntity extends BlockEntity implements MenuProvider, Clearable {
    public static final int STAFF_SLOT = 0;
    public static final int INGREDIENT_SLOT_START = 1;
    public static final int INGREDIENT_SLOT_COUNT = 9;
    public static final int TOTAL_SLOTS = INGREDIENT_SLOT_START + INGREDIENT_SLOT_COUNT;

    private final ItemStackHandler items = new ItemStackHandler(TOTAL_SLOTS) {
        @Override
        protected void onContentsChanged(final int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(final int slot, @NotNull final ItemStack stack) {
            if (slot == STAFF_SLOT) {
                return stack.getItem() instanceof StaffItem;
            }
            return super.isItemValid(slot, stack);
        }

        @Override
        public int getSlotLimit(final int slot) {
            return slot == STAFF_SLOT ? 1 : super.getSlotLimit(slot);
        }
    };

    private final LazyOptional<IItemHandler> itemCapability = LazyOptional.of(() -> items);

    public EnchantStaffBlockEntity(final BlockPos pos, final BlockState state) {
        super(MinegickaBlockEntities.ENCHANT_STAFF.get(), pos, state);
    }

    public ItemStackHandler getItemHandler() {
        return items;
    }

    @Override
    public void load(final CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Inventory")) {
            items.deserializeNBT(tag.getCompound("Inventory"));
        }
    }

    @Override
    protected void saveAdditional(final CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", items.serializeNBT());
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        itemCapability.invalidate();
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull final Capability<T> cap, @Nullable final Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemCapability.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < items.getSlots(); i++) {
            items.setStackInSlot(i, ItemStack.EMPTY);
        }
        setChanged();
    }

    public void dropContents(final Level level, final BlockPos pos) {
        if (level.isClientSide) {
            return;
        }
        final SimpleContainer container = new SimpleContainer(items.getSlots());
        for (int i = 0; i < items.getSlots(); i++) {
            final ItemStack extracted = items.extractItem(i, items.getStackInSlot(i).getCount(), false);
            container.setItem(i, extracted);
        }
        Containers.dropContents(level, pos, container);
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.minegicka.enchant_staff");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int containerId, @NotNull final Inventory playerInventory, @NotNull final Player player) {
        return new EnchantStaffMenu(containerId, playerInventory, this);
    }
}
