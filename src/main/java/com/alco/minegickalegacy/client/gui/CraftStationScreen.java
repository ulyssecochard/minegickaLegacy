package com.alco.minegickalegacy.client.gui;

import com.alco.minegickalegacy.blockentity.CraftStationBlockEntity;
import com.alco.minegickalegacy.client.MinegickaTextures;
import com.alco.minegickalegacy.mechanics.clickcraft.ClickCraftManager;
import com.alco.minegickalegacy.mechanics.clickcraft.ClickCraftRecipe;
import com.alco.minegickalegacy.menu.CraftStationMenu;
import com.alco.minegickalegacy.network.MinegickaNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CraftStationScreen extends AbstractContainerScreen<CraftStationMenu> {
    private static final ResourceLocation TEXTURE = MinegickaTextures.GUI_CRAFT_STATION;
    private static final int PANEL_BG = 0xCC2B2B2B;
    private static final int PANEL_BORDER = 0xFF505050;

    @Nullable
    private CraftStationBlockEntity blockEntity;
    private RecipeList recipeList;
    private CycleButton<String> categoryButton;
    private Button craftOneButton;
    private Button craftBatchButton;
    private Button craftMaxButton;

    private List<String> categories = List.of();
    @Nullable
    private String selectedCategory;
    @Nullable
    private ResourceLocation selectedRecipeId;

    public CraftStationScreen(final CraftStationMenu menu, final Inventory inventory, final Component title) {
        super(menu, inventory, title);
        this.imageWidth = 248;
        this.imageHeight = 240;
        this.titleLabelX = 20;
        this.titleLabelY = 12;
        this.inventoryLabelX = 16;
        this.inventoryLabelY = 134;
    }

    @Override
    protected void init() {
        super.init();
        this.blockEntity = menu.getBlockEntity();
        categories = ClickCraftManager.INSTANCE.getCategories();
        if (categories.isEmpty()) {
            selectedCategory = null;
        } else if (selectedCategory == null || !categories.contains(selectedCategory)) {
            selectedCategory = categories.get(0);
        }
        selectedRecipeId = blockEntity != null ? blockEntity.getCurrentRecipe() : selectedRecipeId;

        final int categoryWidth = 120;
        final int categoryHeight = 20;
        final int categoryX = leftPos + 18;
        final int categoryY = topPos + 30;
        final String[] cycleValues = categories.isEmpty() ? new String[]{"none"} : categories.toArray(new String[0]);
        categoryButton = addRenderableWidget(CycleButton.builder(this::categoryLabel)
                .displayOnlyValue()
                .withValues(cycleValues)
                .create(categoryX, categoryY, categoryWidth, categoryHeight, Component.translatable("gui.minegicka.craft.category"),
                        (button, value) -> setCategory(value)));
        categoryButton.active = !categories.isEmpty();
        if (selectedCategory != null) {
            categoryButton.setValue(selectedCategory);
        }

        final int listLeft = leftPos + 18;
        final int listTop = topPos + 54;
        final int listWidth = 206;
        final int listHeight = 110;
        recipeList = addRenderableWidget(new RecipeList(minecraft, listWidth, listHeight, listTop, listTop + listHeight, 22));
        recipeList.setLeftPos(listLeft);

        craftOneButton = addRenderableWidget(Button.builder(Component.translatable("gui.minegicka.craft.one"), button -> craft(1))
                .bounds(leftPos + 18, topPos + 172, 60, 20)
                .build());
        craftBatchButton = addRenderableWidget(Button.builder(Component.translatable("gui.minegicka.craft.five"), button -> craft(5))
                .bounds(leftPos + 82, topPos + 172, 60, 20)
                .build());
        craftMaxButton = addRenderableWidget(Button.builder(Component.translatable("gui.minegicka.craft.max"), button -> craft(Integer.MAX_VALUE))
                .bounds(leftPos + 146, topPos + 172, 78, 20)
                .build());

        refreshRecipeList();
        updateButtonState();
    }

    private Component categoryLabel(final String category) {
        if (category == null || "none".equals(category)) {
            return Component.translatable("gui.minegicka.craft.category.none");
        }
        return Component.translatable("gui.minegicka.craft.category." + category);
    }

    private void setCategory(final String category) {
        if (recipeList == null) {
            this.selectedCategory = (category == null || "none".equals(category)) ? null : category;
            return;
        }
        if (category == null || "none".equals(category)) {
            this.selectedCategory = null;
            recipeList.setRecipes(List.of());
            setSelectedRecipe(null);
            return;
        }
        this.selectedCategory = category;
        refreshRecipeList();
    }

    private void refreshRecipeList() {
        final List<CraftRecipeEntry> entries = new ArrayList<>();
        if (selectedCategory != null) {
            for (ClickCraftRecipe recipe : ClickCraftManager.INSTANCE.getRecipes(selectedCategory)) {
                entries.add(new CraftRecipeEntry(recipe));
            }
        }
        recipeList.setRecipes(entries);
        if (entries.isEmpty()) {
            setSelectedRecipe(null);
        } else {
            final ResourceLocation current = selectedRecipeId;
            CraftRecipeEntry toSelect = entries.stream()
                    .filter(entry -> entry.recipe.id().equals(current))
                    .findFirst()
                    .orElse(entries.get(0));
            recipeList.setSelected(toSelect);
            setSelectedRecipe(toSelect.recipe.id());
        }
        updateButtonState();
    }

    private void setSelectedRecipe(@Nullable final ResourceLocation recipeId) {
        if (Objects.equals(this.selectedRecipeId, recipeId)) {
            return;
        }
        this.selectedRecipeId = recipeId;
        updateButtonState();
        if (blockEntity != null) {
            MinegickaNetwork.requestSelectRecipe(blockEntity.getBlockPos(), recipeId);
        }
    }

    private void craft(final int repeat) {
        if (selectedRecipeId == null) {
            return;
        }
        final Optional<ClickCraftRecipe> recipeOpt = currentRecipe();
        if (recipeOpt.isEmpty()) {
            return;
        }
        int attempts = repeat;
        if (repeat == Integer.MAX_VALUE) {
            attempts = computeMaxCraftable(recipeOpt.get());
        }
        if (attempts <= 0) {
            return;
        }
        MinegickaNetwork.requestCraft(selectedRecipeId, attempts);
    }

    private void updateButtonState() {
        final boolean hasSelection = selectedRecipeId != null;
        int maxCraftable = 0;
        if (hasSelection) {
            maxCraftable = currentRecipe().map(this::computeMaxCraftable).orElse(0);
        }
        final boolean canCraft = hasSelection && maxCraftable > 0;
        craftOneButton.active = canCraft;
        craftBatchButton.active = canCraft;
        craftMaxButton.active = canCraft;
    }

    @Override
    protected void renderBg(final GuiGraphics guiGraphics, final float partialTick, final int mouseX, final int mouseY) {
        final int left = leftPos;
        final int top = topPos;
        guiGraphics.blit(TEXTURE, left, top, 0, 0, imageWidth, imageHeight, 858, 540);

        drawPanel(guiGraphics, left + 16, top + 28, 216, 142);
        drawPanel(guiGraphics, left + 16, top + 170, 216, 46);
    }

    private void drawPanel(final GuiGraphics guiGraphics, final int x, final int y, final int width, final int height) {
        guiGraphics.fill(x - 1, y - 1, x + width + 1, y + height + 1, PANEL_BORDER);
        guiGraphics.fill(x, y, x + width, y + height, PANEL_BG);
    }

    @Override
    public void render(final GuiGraphics guiGraphics, final int mouseX, final int mouseY, final float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderSelectionInfo(guiGraphics);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void renderSelectionInfo(final GuiGraphics guiGraphics) {
        final int textX = leftPos + 20;
        final int textY = topPos + 180;
        if (selectedRecipeId == null) {
            guiGraphics.drawString(font, Component.translatable("gui.minegicka.craft.no_selection"), textX, textY, 0xFFFFFF);
            return;
        }
        final Optional<ClickCraftRecipe> recipeOpt = currentRecipe();
        if (recipeOpt.isEmpty()) {
            guiGraphics.drawString(font, Component.translatable("gui.minegicka.craft.invalid_selection"), textX, textY, 0xFF5555);
            return;
        }
        final ClickCraftRecipe recipe = recipeOpt.get();
        int lineY = textY;
        guiGraphics.drawString(font, recipe.result().getHoverName(), textX, lineY, 0xFFFFFF);
        lineY += 10;
        final int maxCraftable = computeMaxCraftable(recipe);
        guiGraphics.drawString(font, Component.translatable("gui.minegicka.craft.available", maxCraftable), textX, lineY, 0xAAAAAA);
        lineY += 12;
        guiGraphics.drawString(font, Component.translatable("gui.minegicka.craft.ingredients"), textX, lineY, 0xFFFFFF);
        lineY += 10;
        for (ClickCraftRecipe.IngredientStack ingredient : recipe.ingredients()) {
            guiGraphics.drawString(font, ingredientDescription(ingredient), textX + 4, lineY, 0xDDDDDD);
            lineY += 10;
        }
    }

    private int computeMaxCraftable(final ClickCraftRecipe recipe) {
        if (minecraft == null || minecraft.player == null) {
            return 0;
        }
        return recipe.ingredients().stream()
                .mapToInt(ingredient -> ingredient.countMatches(minecraft.player.getInventory()) / ingredient.count())
                .filter(count -> count >= 0)
                .min()
                .orElse(0);
    }

    private Optional<ClickCraftRecipe> currentRecipe() {
        return selectedRecipeId == null ? Optional.empty() : ClickCraftManager.INSTANCE.getRecipe(selectedRecipeId);
    }

    private Component ingredientDescription(final ClickCraftRecipe.IngredientStack ingredient) {
        final ItemStack[] stacks = ingredient.ingredient().getItems();
        final Component name = stacks.length > 0 ? stacks[0].getHoverName() : Component.translatable("gui.minegicka.craft.unknown");
        return Component.literal(" - ").append(name).append(Component.literal(" x" + ingredient.count()));
    }

    private void select(ClickCraftRecipe recipe) {
        recipeList.setSelected(recipeList.children().stream()
                .filter(entry -> entry.recipe.equals(recipe))
                .findFirst()
                .orElse(null));
        setSelectedRecipe(recipe.id());
    }

    private class RecipeList extends ObjectSelectionList<CraftRecipeEntry> {
        RecipeList(final Minecraft minecraft, final int width, final int height, final int top, final int bottom, final int itemHeight) {
            super(minecraft, width, height, top, bottom, itemHeight);
        }

        @Override
        protected void renderBackground(final GuiGraphics guiGraphics) {
            // background handled by parent
        }

        void setRecipes(final List<CraftRecipeEntry> entries) {
            replaceEntries(entries);
        }
    }

    private class CraftRecipeEntry extends ObjectSelectionList.Entry<CraftRecipeEntry> {
        private final ClickCraftRecipe recipe;

        CraftRecipeEntry(final ClickCraftRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void render(final GuiGraphics guiGraphics, final int index, final int y, final int x, final int entryWidth, final int entryHeight, final int mouseX, final int mouseY, final boolean hovered, final float partialTick) {
            final ItemStack result = recipe.result();
            guiGraphics.renderItem(result, x + 2, y + 2);
            guiGraphics.renderItemDecorations(font, result, x + 2, y + 2);
            final int textColour = hovered || (selectedRecipeId != null && selectedRecipeId.equals(recipe.id())) ? 0xFFFFAA : 0xFFFFFF;
            guiGraphics.drawString(font, result.getHoverName(), x + 24, y + 6, textColour);
        }

        @Override
        public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
            if (button == 0) {
                select(recipe);
                return true;
            }
            return false;
        }

        @Override
        public Component getNarration() {
            return recipe.result().getHoverName();
        }
    }
}
