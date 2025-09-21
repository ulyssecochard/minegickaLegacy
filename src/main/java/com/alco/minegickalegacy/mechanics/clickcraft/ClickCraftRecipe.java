package com.alco.minegickalegacy.mechanics.clickcraft;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ClickCraftRecipe(net.minecraft.resources.ResourceLocation id,
                               String category,
                               ItemStack result,
                               List<IngredientStack> ingredients) {

    public record IngredientStack(Ingredient ingredient, int count) {
        public boolean matches(@NotNull final ItemStack stack) {
            return ingredient.test(stack);
        }

        public int countMatches(final Inventory inventory) {
            int total = 0;
            for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
                final ItemStack stack = inventory.getItem(slot);
                if (!stack.isEmpty() && matches(stack)) {
                    total += stack.getCount();
                }
            }
            return total;
        }
    }
}
