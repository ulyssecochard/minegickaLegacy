package com.alco.minegickalegacy.client;

import com.alco.minegickalegacy.capability.PlayerManaCapability;
import com.alco.minegickalegacy.capability.PlayerManaData;
import com.alco.minegickalegacy.spell.ClientSpellManager;
import com.alco.minegickalegacy.spell.ElementSelectionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;

public final class ManaOverlayRenderer {
    private ManaOverlayRenderer() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(ManaOverlayRenderer::renderOverlay);
    }

    private static void renderOverlay(final RenderGuiOverlayEvent.Post event) {
        if (!event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) {
            return;
        }
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        minecraft.player.getCapability(PlayerManaCapability.CAPABILITY).ifPresent(data -> drawHud(event, data));
    }

    private static void drawHud(final RenderGuiOverlayEvent.Post event, final PlayerManaData data) {
        final var guiGraphics = event.getGuiGraphics();
        final Component manaText = Component.translatable("hud.minegicka.mana", Math.round(data.getMana()), Math.round(data.getMaxMana()));
        final int x = 10;
        int y = 10;
        guiGraphics.drawString(Minecraft.getInstance().font, manaText, x, y, 0x88BBFF, true);
        y += 10;
        final String combo = ElementSelectionManager.formatCombination();
        if (!combo.isEmpty()) {
            final Component comboText = Component.translatable("hud.minegicka.combo", combo);
            guiGraphics.drawString(Minecraft.getInstance().font, comboText, x, y, 0xFFD37F, true);
            y += 10;
        }
        final Component spell = ClientSpellManager.currentSpellDisplay().orElse(null);
        if (spell != null) {
            final Component spellText = Component.translatable("hud.minegicka.spell", spell);
            guiGraphics.drawString(Minecraft.getInstance().font, spellText, x, y, 0xFFD37F, true);
        }
    }
}
