package com.alco.minegickalegacy.spell;

import com.alco.minegickalegacy.registry.MagickRegistry;
import com.alco.minegickalegacy.spell.ElementSelectionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public final class ClientSpellManager {
    private static ResourceLocation currentSpellId;
    private static Component currentSpellDisplay;

    private ClientSpellManager() {
    }

    public static void handleSpellTrigger(final ResourceLocation id, final boolean start) {
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }
        MagickRegistry.byId(id).ifPresent(def -> {
            if (start) {
                currentSpellId = id;
                currentSpellDisplay = Component.literal(def.displayName());
                ElementSelectionManager.setCombination(def.combination());
                minecraft.player.displayClientMessage(Component.translatable("spell.minegicka.started", def.displayName()), true);
            } else if (id.equals(currentSpellId)) {
                minecraft.player.displayClientMessage(Component.translatable("spell.minegicka.stopped", def.displayName()), true);
                clear();
            }
        });
    }

    public static Optional<Component> currentSpellDisplay() {
        return Optional.ofNullable(currentSpellDisplay);
    }

    public static void clear() {
        currentSpellId = null;
        ElementSelectionManager.clear();
        currentSpellDisplay = null;
    }
}
