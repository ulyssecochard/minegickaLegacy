package com.alco.minegickalegacy.blockentity;

import com.alco.minegickalegacy.menu.CraftStationMenu;
import com.alco.minegickalegacy.mechanics.clickcraft.ClickCraftManager;
import com.alco.minegickalegacy.mechanics.clickcraft.ClickCraftRecipe;
import com.alco.minegickalegacy.registry.MinegickaBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Clearable;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.SimpleContainer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * Stores the crafting station output preview and exposes it to menus.
 */
public class CraftStationBlockEntity extends BlockEntity implements MenuProvider, Clearable {
    public static final int INPUT_SLOT_COUNT = 0;
    public static final int OUTPUT_SLOT_INDEX = 0;
    public static final int TOTAL_SLOTS = 1;

    private final ItemStackHandler items = new ItemStackHandler(TOTAL_SLOTS) {
        @Override
        protected void onContentsChanged(final int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(final int slot, @NotNull final ItemStack stack) {
            return false;
        }
    };

    private final LazyOptional<IItemHandler> itemCapability = LazyOptional.of(() -> items);

    @Nullable
    private ResourceLocation currentRecipeId;

    public CraftStationBlockEntity(final BlockPos pos, final BlockState state) {
        super(MinegickaBlockEntities.CRAFT_STATION.get(), pos, state);
    }

    public ItemStackHandler getItemHandler() {
        return items;
    }

    @Nullable
    public ResourceLocation getCurrentRecipe() {
        return currentRecipeId;
    }

    public void setCurrentRecipe(@Nullable final ResourceLocation id) {
        if (!Objects.equals(this.currentRecipeId, id)) {
            this.currentRecipeId = id;
            updatePreview();
            setChanged();
            if (level != null) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            }
        }
    }

    private void updatePreview() {
        ItemStack preview = ItemStack.EMPTY;
        if (currentRecipeId != null) {
            final Optional<ClickCraftRecipe> recipe = ClickCraftManager.INSTANCE.getRecipe(currentRecipeId);
            if (recipe.isPresent()) {
                preview = recipe.get().result().copy();
            }
        }
        items.setStackInSlot(OUTPUT_SLOT_INDEX, preview);
    }

    @Override
    public void load(final CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Inventory")) {
            items.deserializeNBT(tag.getCompound("Inventory"));
        }
        if (tag.contains("SelectedRecipe")) {
            currentRecipeId = ResourceLocation.tryParse(tag.getString("SelectedRecipe"));
        }
        updatePreview();
    }

    @Override
    protected void saveAdditional(final CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", items.serializeNBT());
        if (currentRecipeId != null) {
            tag.putString("SelectedRecipe", currentRecipeId.toString());
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        updatePreview();
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
        return Component.translatable("block.minegicka.craft_station");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int containerId, @NotNull final Inventory playerInventory, @NotNull final Player player) {
        return new CraftStationMenu(containerId, playerInventory, this);
    }
}
