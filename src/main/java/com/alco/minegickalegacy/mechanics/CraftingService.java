package com.alco.minegickalegacy.mechanics;

import com.alco.minegickalegacy.MinegickaMod;
import com.alco.minegickalegacy.blockentity.CraftStationBlockEntity;
import com.alco.minegickalegacy.mechanics.clickcraft.ClickCraftManager;
import com.alco.minegickalegacy.mechanics.clickcraft.ClickCraftRecipe;
import com.alco.minegickalegacy.menu.CraftStationMenu;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Optional;

public final class CraftingService {
    private CraftingService() {
    }

    public static void handleRequest(final ServerPlayer player, final ResourceLocation recipeId, final int repeat) {
        if (!(player.containerMenu instanceof CraftStationMenu menu)) {
            MinegickaMod.LOGGER.debug("Ignoring craft request {} from {} - menu mismatch", recipeId, player.getGameProfile().getName());
            return;
        }
        final CraftStationBlockEntity blockEntity = menu.getBlockEntity();
        if (blockEntity == null) {
            MinegickaMod.LOGGER.debug("Ignoring craft request {} from {} - no block entity", recipeId, player.getGameProfile().getName());
            return;
        }
        final Optional<ClickCraftRecipe> recipeOpt = ClickCraftManager.INSTANCE.getRecipe(recipeId);
        if (recipeOpt.isEmpty()) {
            MinegickaMod.LOGGER.debug("Ignoring craft request {} - recipe not found", recipeId);
            return;
        }
        blockEntity.setCurrentRecipe(recipeId);
        final ClickCraftRecipe recipe = recipeOpt.get();
        final int attempts = Math.max(1, repeat);
        for (int i = 0; i < attempts; i++) {
            if (!craft(recipe, player)) {
                break;
            }
        }
    }

    private static boolean craft(final ClickCraftRecipe recipe, final ServerPlayer player) {
        if (!canCraft(recipe, player)) {
            return false;
        }
        consumeIngredients(recipe, player);
        final ItemStack result = recipe.result().copy();
        ItemHandlerHelper.giveItemToPlayer(player, result);
        return true;
    }

    public static boolean canCraft(final ClickCraftRecipe recipe, final Player player) {
        final Inventory inventory = player.getInventory();
        for (final ClickCraftRecipe.IngredientStack ingredient : recipe.ingredients()) {
            if (countMatchingItems(inventory, ingredient) < ingredient.count()) {
                return false;
            }
        }
        return true;
    }

    private static void consumeIngredients(final ClickCraftRecipe recipe, final Player player) {
        final Inventory inventory = player.getInventory();
        for (final ClickCraftRecipe.IngredientStack ingredient : recipe.ingredients()) {
            int remaining = ingredient.count();
            for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
                final ItemStack stack = inventory.getItem(slot);
                if (stack.isEmpty()) {
                    continue;
                }
                if (ingredient.matches(stack)) {
                    final int toRemove = Math.min(stack.getCount(), remaining);
                    stack.shrink(toRemove);
                    if (stack.isEmpty()) {
                        inventory.setItem(slot, ItemStack.EMPTY);
                    }
                    remaining -= toRemove;
                    if (remaining <= 0) {
                        break;
                    }
                }
            }
        }
        inventory.setChanged();
    }

    private static int countMatchingItems(final Inventory inventory, final ClickCraftRecipe.IngredientStack ingredient) {
        int total = 0;
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            final ItemStack stack = inventory.getItem(slot);
            if (!stack.isEmpty() && ingredient.matches(stack)) {
                total += stack.getCount();
            }
        }
        return total;
    }
}
