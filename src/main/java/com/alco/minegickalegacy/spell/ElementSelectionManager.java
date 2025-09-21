package com.alco.minegickalegacy.spell;

import com.alco.minegickalegacy.mechanics.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ElementSelectionManager {
    private static final List<Element> CURRENT_COMBINATION = new ArrayList<>();
    private static final int MAX_ELEMENTS = 5;

    private ElementSelectionManager() {
    }

    public static void setCombination(final List<Element> combination) {
        CURRENT_COMBINATION.clear();
        CURRENT_COMBINATION.addAll(combination);
    }

    public static void addElement(final Element element) {
        if (CURRENT_COMBINATION.size() >= MAX_ELEMENTS) {
            CURRENT_COMBINATION.remove(0);
        }
        CURRENT_COMBINATION.add(element);
    }

    public static void clear() {
        CURRENT_COMBINATION.clear();
    }

    public static List<Element> getCombination() {
        return Collections.unmodifiableList(CURRENT_COMBINATION);
    }

    public static String formatCombination() {
        if (CURRENT_COMBINATION.isEmpty()) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        for (Element element : CURRENT_COMBINATION) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(element.name().substring(0, 1));
        }
        return builder.toString();
    }
}
