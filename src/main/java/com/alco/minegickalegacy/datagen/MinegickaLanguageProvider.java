package com.alco.minegickalegacy.datagen;

import com.alco.minegickalegacy.MinegickaMod;
import com.alco.minegickalegacy.registry.MinegickaDeferredRegisters;
import com.alco.minegickalegacy.registry.MagickRegistry;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public final class MinegickaLanguageProvider extends LanguageProvider {
    public MinegickaLanguageProvider(final PackOutput output, final String locale) {
        super(output, MinegickaMod.MODID, locale);
    }

    private final Set<String> usedTranslationKeys = new HashSet<>();

    private void addSafe(final String key, final String value) {
        if (usedTranslationKeys.add(key)) {
            add(key, value);
        }
    }

    private void addSafe(final net.minecraft.world.item.Item item, final String value) {
        addSafe(item.getDescriptionId(), value);
    }

    private void addSafe(final net.minecraft.world.level.block.Block block, final String value) {
        addSafe(block.getDescriptionId(), value);
    }


    @Override
    protected void addTranslations() {
        addSafe("itemGroup.minegicka.main", "Minegicka Legacy");
        addSafe("hud.minegicka.mana", "Mana: %s / %s");
        addSafe("hud.minegicka.combo", "Combo: %s");
        addSafe("hud.minegicka.spell", "Spell: %s");
        addSafe("key.categories.minegicka", "Minegicka");
        addSafe("key.minegicka.arcane", "Arcane Element");
        addSafe("key.minegicka.cold", "Cold Element");
        addSafe("key.minegicka.earth", "Earth Element");
        addSafe("key.minegicka.fire", "Fire Element");
        addSafe("key.minegicka.ice", "Ice Element");
        addSafe("key.minegicka.life", "Life Element");
        addSafe("key.minegicka.lightning", "Lightning Element");
        addSafe("key.minegicka.shield", "Shield Element");
        addSafe("key.minegicka.steam", "Steam Element");
        addSafe("key.minegicka.water", "Water Element");
        addSafe("key.minegicka.cast", "Cast / clear magick queue");
        addSafe("item.minegicka.magick_tablet.unlock_all", "All magicks unlocked!");
        addSafe("item.minegicka.magick_tablet.unlock", "Unlocked magick: %s");
        addSafe("item.minegicka.magick_tablet.cast", "Cast magick: %s");
        addSafe("item.minegicka.magick_tablet.no_mana", "Not enough mana");
        addSafe("item.minegicka.staff.use", "Staff stats P:%s A:%s C:%s R:%s");
        addSafe("item.minegicka.staff.power", "Power x%s");
        addSafe("item.minegicka.staff.attack_speed", "Attack Speed x%s");
        addSafe("item.minegicka.staff.consume", "Consume x%s");
        addSafe("item.minegicka.staff.recover", "Recover x%s");
        addSafe("spell.minegicka.started", "Spell started: %s");
        addSafe("spell.minegicka.stopped", "Spell stopped: %s");
        addSafe("spell.minegicka.unknown", "Unknown magick combination");

        MinegickaDeferredRegisters.ITEMS.getEntries().forEach(entry -> addSafe(entry.get(), titleCase(entry.getId().getPath())));
        MinegickaDeferredRegisters.BLOCKS.getEntries().forEach(entry -> addSafe(entry.get(), titleCase(entry.getId().getPath())));

        MagickRegistry.all().forEach(def -> addSafe("magick." + def.id().getNamespace() + "." + def.id().getPath(), def.displayName()));
    }

    private static String titleCase(final String input) {
        final List<String> parts = splitIdentifier(input);
        final StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(part.substring(0, 1).toUpperCase(Locale.ROOT));
            if (part.length() > 1) {
                builder.append(part.substring(1));
            }
        }
        return builder.toString();
    }

    private static List<String> splitIdentifier(final String input) {
        final List<String> parts = new ArrayList<>();
        int start = 0;
        for (int i = 0; i <= input.length(); i++) {
            if (i == input.length() || input.charAt(i) == '_') {
                if (i > start) {
                    parts.add(input.substring(start, i));
                }
                start = i + 1;
            }
        }
        return parts;
    }
}
