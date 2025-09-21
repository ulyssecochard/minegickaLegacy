package com.alco.minegickalegacy.client.input;

import com.alco.minegickalegacy.mechanics.Element;
import com.alco.minegickalegacy.network.MinegickaNetwork;
import com.alco.minegickalegacy.spell.ElementSelectionManager;
import net.minecraft.client.Minecraft;

public final class MinegickaKeyHandler {
    private MinegickaKeyHandler() {
    }

    public static void handleClientTick() {
        if (!MinegickaKeyMappings.isInputAvailable()) {
            return;
        }
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }
        MinegickaKeyMappings.elementKeys().forEach((element, mapping) -> {
            while (mapping.consumeClick()) {
                ElementSelectionManager.addElement(element);
            }
        });
        while (MinegickaKeyMappings.castOrClearKey().consumeClick()) {
            if (ElementSelectionManager.getCombination().isEmpty()) {
                MinegickaNetwork.sendSpellCastRequest(java.util.Collections.emptyList());
                ElementSelectionManager.clear();
            } else {
                MinegickaNetwork.sendSpellCastRequest(ElementSelectionManager.getCombination());
                ElementSelectionManager.clear();
            }
        }
    }
}
