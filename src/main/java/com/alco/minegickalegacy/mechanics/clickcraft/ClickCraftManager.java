package com.alco.minegickalegacy.mechanics.clickcraft;

import com.alco.minegickalegacy.MinegickaMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ClickCraftManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setLenient().create();
    public static final ClickCraftManager INSTANCE = new ClickCraftManager();

    private volatile Map<ResourceLocation, ClickCraftRecipe> recipes = Collections.emptyMap();
    private volatile Map<String, List<ClickCraftRecipe>> recipesByCategory = Collections.emptyMap();
    private volatile List<String> categories = Collections.emptyList();

    private ClickCraftManager() {
        super(GSON, "clickcraft");
    }

    @Override
    protected void apply(final Map<ResourceLocation, JsonElement> object,
                          final ResourceManager resourceManager,
                          final ProfilerFiller profiler) {
        final Map<ResourceLocation, ClickCraftRecipe> recipeMap = new LinkedHashMap<>();
        final Map<String, List<ClickCraftRecipe>> byCategory = new LinkedHashMap<>();
        final LinkedHashSet<String> categoryOrder = new LinkedHashSet<>();

        object.forEach((id, json) -> {
            try {
                if (!json.isJsonObject()) {
                    throw new JsonSyntaxException("Expected object for clickcraft recipe");
                }
                final JsonObject root = json.getAsJsonObject();
                final String category = GsonHelper.getAsString(root, "category");
                final ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(root, "result"));
                final JsonArray ingredientsArray = GsonHelper.getAsJsonArray(root, "ingredients");
                final List<ClickCraftRecipe.IngredientStack> ingredients = new ArrayList<>(ingredientsArray.size());
                for (JsonElement element : ingredientsArray) {
                    final JsonObject ingredientObj = GsonHelper.convertToJsonObject(element, "ingredient");
                    final Ingredient ingredient = Ingredient.fromJson(ingredientObj);
                    final int count = GsonHelper.getAsInt(ingredientObj, "count", 1);
                    ingredients.add(new ClickCraftRecipe.IngredientStack(ingredient, Math.max(1, count)));
                }
                final ClickCraftRecipe recipe = new ClickCraftRecipe(id, category, result, List.copyOf(ingredients));
                recipeMap.put(id, recipe);
                categoryOrder.add(category);
                byCategory.computeIfAbsent(category, key -> new ArrayList<>()).add(recipe);
            } catch (Exception ex) {
                MinegickaMod.LOGGER.error("Failed to parse clickcraft recipe {}: {}", id, ex.getMessage());
            }
        });

        this.recipes = Collections.unmodifiableMap(recipeMap);
        final Map<String, List<ClickCraftRecipe>> immutableByCategory = new LinkedHashMap<>();
        byCategory.forEach((key, list) -> immutableByCategory.put(key, List.copyOf(list)));
        this.recipesByCategory = Collections.unmodifiableMap(immutableByCategory);
        this.categories = List.copyOf(categoryOrder);
        MinegickaMod.LOGGER.debug("Loaded {} clickcraft recipes across {} categories", recipeMap.size(), categoryOrder.size());
    }

    public List<String> getCategories() {
        return categories;
    }

    public List<ClickCraftRecipe> getRecipes(final String category) {
        return recipesByCategory.getOrDefault(category, List.of());
    }

    public Optional<ClickCraftRecipe> getRecipe(final ResourceLocation id) {
        return Optional.ofNullable(recipes.get(id));
    }

    @Nullable
    public ClickCraftRecipe getFirstRecipe(final String category) {
        final List<ClickCraftRecipe> list = recipesByCategory.get(category);
        return (list == null || list.isEmpty()) ? null : list.get(0);
    }
}
