package com.alco.minegickalegacy.client.gui;

import com.alco.minegickalegacy.blockentity.EnchantStaffBlockEntity;
import com.alco.minegickalegacy.client.MinegickaTextures;
import com.alco.minegickalegacy.items.StaffItem;
import com.alco.minegickalegacy.menu.EnchantStaffMenu;
import com.alco.minegickalegacy.network.MinegickaNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EnchantStaffScreen extends AbstractContainerScreen<EnchantStaffMenu> {
    private static final ResourceLocation TEXTURE = MinegickaTextures.GUI_ENCHANT_STAFF;
    private static final int PANEL_BG = 0xCC2B2B2B;
    private static final int PANEL_BORDER = 0xFF505050;

    private Button enchantButton;

    public EnchantStaffScreen(final EnchantStaffMenu menu, final Inventory inventory, final Component title) {
        super(menu, inventory, title);
        this.imageWidth = 248;
        this.imageHeight = 240;
        this.titleLabelX = 20;
        this.titleLabelY = 12;
        this.inventoryLabelX = 16;
        this.inventoryLabelY = 144;
    }

    @Override
    protected void init() {
        super.init();
        final int buttonWidth = 60;
        final int buttonHeight = 20;
        final int buttonX = leftPos + imageWidth - buttonWidth - 12;
        final int buttonY = topPos + 36;
        enchantButton = addRenderableWidget(Button.builder(Component.translatable("block.minegicka.enchant_staff.button"), this::onEnchantClicked)
                .bounds(buttonX, buttonY, buttonWidth, buttonHeight)
                .build());
    }

    @Override
    protected void renderBg(final GuiGraphics guiGraphics, final float partialTick, final int mouseX, final int mouseY) {
        final int left = leftPos;
        final int top = topPos;
        guiGraphics.blit(TEXTURE, left, top, 0, 0, imageWidth, imageHeight, 858, 540);

        drawPanel(guiGraphics, left + 24, top + 34, 48, 136);
        drawPanel(guiGraphics, left + 92, top + 34, 120, 136);
        drawPanel(guiGraphics, left + 16, top + 142, 216, 86);
    }

    private void drawPanel(final GuiGraphics guiGraphics, final int x, final int y, final int width, final int height) {
        guiGraphics.fill(x - 1, y - 1, x + width + 1, y + height + 1, PANEL_BORDER);
        guiGraphics.fill(x, y, x + width, y + height, PANEL_BG);
    }

    @Override
    public void render(final GuiGraphics guiGraphics, final int mouseX, final int mouseY, final float partialTick) {
        updateButtonState();
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void onEnchantClicked(final Button button) {
        MinegickaNetwork.requestEnchant(EnchantStaffBlockEntity.STAFF_SLOT, gatherIngredients());
    }

    private List<ItemStack> gatherIngredients() {
        final List<ItemStack> stacks = new ArrayList<>();
        for (int slot = EnchantStaffBlockEntity.INGREDIENT_SLOT_START; slot < EnchantStaffBlockEntity.TOTAL_SLOTS; slot++) {
            final ItemStack stack = menu.getSlot(slot).getItem();
            if (!stack.isEmpty()) {
                stacks.add(stack.copy());
            }
        }
        return stacks;
    }

    private void updateButtonState() {
        if (enchantButton == null) {
            return;
        }
        final ItemStack staff = menu.getSlot(EnchantStaffBlockEntity.STAFF_SLOT).getItem();
        boolean hasStaff = !staff.isEmpty() && staff.getItem() instanceof StaffItem;
        boolean hasIngredients = false;
        for (int slot = EnchantStaffBlockEntity.INGREDIENT_SLOT_START; slot < EnchantStaffBlockEntity.TOTAL_SLOTS; slot++) {
            if (!menu.getSlot(slot).getItem().isEmpty()) {
                hasIngredients = true;
                break;
            }
        }
        enchantButton.active = hasStaff && hasIngredients;
    }
}
