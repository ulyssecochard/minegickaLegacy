package com.alco.minegickalegacy.registry;

import com.alco.minegickalegacy.MinegickaMod;
import com.alco.minegickalegacy.mechanics.Element;
import com.alco.minegickalegacy.spell.MagickEffect;
import com.alco.minegickalegacy.spell.MagickEffects;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class MagickRegistry {
    public static final class MagickDefinition {
        private final ResourceLocation id;
        private final String displayName;
        private final List<Element> combination;
        private final double baseManaCost;
        private final MagickEffect effect;

        private MagickDefinition(final ResourceLocation id, final String displayName, final List<Element> combination, final double baseManaCost, final MagickEffect effect) {
            this.id = id;
            this.displayName = displayName;
            this.combination = List.copyOf(combination);
            this.baseManaCost = baseManaCost;
            this.effect = effect;
        }

        public ResourceLocation id() {
            return id;
        }

        public String displayName() {
            return displayName;
        }

        public List<Element> combination() {
            return combination;
        }

        public double baseManaCost() {
            return baseManaCost;
        }

        public MagickEffect effect() {
            return effect;
        }
    }

    private static final Map<ResourceLocation, MagickDefinition> DEFINITIONS = new LinkedHashMap<>();
    private static final Map<Character, Element> ELEMENT_LOOKUP = Map.of(
            'A', Element.ARCANE,
            'C', Element.COLD,
            'D', Element.SHIELD,
            'E', Element.EARTH,
            'F', Element.FIRE,
            'H', Element.LIGHTNING,
            'I', Element.ICE,
            'L', Element.LIFE,
            'S', Element.STEAM,
            'W', Element.WATER
    );

    static {
        registerDefaults();
    }

    private MagickRegistry() {
    }

    private static void registerDefaults() {
        if (!DEFINITIONS.isEmpty()) {
            return;
        }

        add("Collect", "SEES", 100, MagickEffects::collect);
        add("De-potion", "LD", 100, MagickEffects::dePotion);
        add("De-spawners", "AADEE", 100, MagickEffects::deSpawners);
        add("Explosion", "HFAF", 250, MagickEffects::explosion);
        add("Extinguish", "WD", 100, MagickEffects::extinguish);
        add("Feather Fall", "SD", 100, MagickEffects::featherFall);
        add("Freeze Motion", "ID", 150, MagickEffects::freezeMotion);
        add("Gravitational", "ED", 100, MagickEffects::gravitational);
        add("Haste", "HAF", 100, MagickEffects::haste);
        add("Homing Lightning", "SSHAH", 600, MagickEffects::homingLightning);
        add("Lightning Bolt", "SHAH", 400, MagickEffects::lightningBolt);
        add("Nullify", "AD", 150, MagickEffects::nullify);
        add("Snow", "CD", 100, MagickEffects::snowStorm);
        add("Teleport", "HAH", 400, MagickEffects::teleport);
        add("Thaw", "FD", 100, MagickEffects::thaw);
        add("Vortex", "IAIDI", 666, MagickEffects::vortex);
        add("Water Shock", "HD", 100, MagickEffects::waterShock);
    }

    private static void add(final String displayName, final String comboString, final double baseManaCost, final MagickEffect effect) {
        final ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MinegickaMod.MODID, toIdentifier(displayName));
        final List<Element> combination = comboStringToElements(comboString);
        DEFINITIONS.put(id, new MagickDefinition(id, displayName, combination, baseManaCost, effect));
    }

    private static String toIdentifier(final String displayName) {
        return displayName.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "_").replaceAll("_+", "_").replaceAll("^_|_$", "");
    }

    private static List<Element> comboStringToElements(final String raw) {
        final List<Element> list = new ArrayList<>();
        for (char c : raw.toUpperCase(Locale.ROOT).toCharArray()) {
            final Element element = ELEMENT_LOOKUP.get(c);
            if (element != null) {
                list.add(element);
            }
        }
        return list;
    }

    public static Optional<MagickDefinition> byId(final ResourceLocation id) {
        return Optional.ofNullable(DEFINITIONS.get(id));
    }

    public static Optional<MagickDefinition> first() {
        return DEFINITIONS.values().stream().findFirst();
    }

    public static Set<ResourceLocation> keys() {
        return Collections.unmodifiableSet(DEFINITIONS.keySet());
    }

    public static List<MagickDefinition> all() {
        return List.copyOf(DEFINITIONS.values());
    }

    public static Optional<MagickDefinition> findByCombination(final List<Element> combination) {
        return DEFINITIONS.values().stream()
                .filter(def -> def.combination().equals(combination))
                .findFirst();
    }

    public static String formatCombination(final List<Element> combination) {
        return combination.stream().map(Element::name).map(name -> name.substring(0, 1)).collect(java.util.stream.Collectors.joining());
    }
}
