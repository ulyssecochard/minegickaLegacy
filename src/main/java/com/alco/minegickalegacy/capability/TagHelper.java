package com.alco.minegickalegacy.capability;

import com.alco.minegickalegacy.mechanics.Element;
import net.minecraft.nbt.CompoundTag;

import java.util.Locale;

public final class TagHelper {
    private TagHelper() {}

    public static CompoundTag writeElement(final Element element) {
        final CompoundTag tag = new CompoundTag();
        tag.putString("Id", element.name().toLowerCase(Locale.ROOT));
        return tag;
    }

    public static Element readElement(final CompoundTag tag) {
        if (tag == null) {
            return null;
        }
        final String id = tag.getString("Id");
        try {
            return Element.valueOf(id.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
