package com.alco.minegickalegacy.client.input;

import com.alco.minegickalegacy.mechanics.Element;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public final class MinegickaKeyMappings {
    private static final String CATEGORY = "key.categories.minegicka";
    private static final Map<Element, KeyMapping> ELEMENT_KEYS = new EnumMap<>(Element.class);
    private static final KeyMapping CAST_OR_CLEAR = new KeyMapping("key.minegicka.cast", GLFW.GLFW_KEY_R, CATEGORY);

    private MinegickaKeyMappings() {
    }

    public static void register(final RegisterKeyMappingsEvent event) {
        registerElementKey(event, Element.ARCANE, "key.minegicka.arcane", GLFW.GLFW_KEY_J);
        registerElementKey(event, Element.COLD, "key.minegicka.cold", GLFW.GLFW_KEY_O);
        registerElementKey(event, Element.EARTH, "key.minegicka.earth", GLFW.GLFW_KEY_K);
        registerElementKey(event, Element.FIRE, "key.minegicka.fire", GLFW.GLFW_KEY_L);
        registerElementKey(event, Element.ICE, "key.minegicka.ice", GLFW.GLFW_KEY_P);
        registerElementKey(event, Element.LIFE, "key.minegicka.life", GLFW.GLFW_KEY_U);
        registerElementKey(event, Element.LIGHTNING, "key.minegicka.lightning", GLFW.GLFW_KEY_H);
        registerElementKey(event, Element.SHIELD, "key.minegicka.shield", GLFW.GLFW_KEY_I);
        registerElementKey(event, Element.STEAM, "key.minegicka.steam", GLFW.GLFW_KEY_T);
        registerElementKey(event, Element.WATER, "key.minegicka.water", GLFW.GLFW_KEY_Y);
        event.register(CAST_OR_CLEAR);
    }

    private static void registerElementKey(final RegisterKeyMappingsEvent event, final Element element, final String translationKey, final int defaultKey) {
        final KeyMapping mapping = new KeyMapping(translationKey, defaultKey, CATEGORY);
        ELEMENT_KEYS.put(element, mapping);
        event.register(mapping);
    }

    public static Map<Element, KeyMapping> elementKeys() {
        return Collections.unmodifiableMap(ELEMENT_KEYS);
    }

    public static KeyMapping castOrClearKey() {
        return CAST_OR_CLEAR;
    }

    public static boolean isInputAvailable() {
        return Minecraft.getInstance().screen == null;
    }
}
