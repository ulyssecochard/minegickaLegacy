package com.alco.minegickalegacy.client;

import com.alco.minegickalegacy.MinegickaMod;
import net.minecraft.resources.ResourceLocation;

public final class MinegickaTextures {
    private MinegickaTextures() {
    }

    private static ResourceLocation gui(final String name) {
        return ResourceLocation.fromNamespaceAndPath(MinegickaMod.MODID, "textures/gui/" + name + ".png");
    }

    private static ResourceLocation entity(final String name) {
        return ResourceLocation.fromNamespaceAndPath(MinegickaMod.MODID, "textures/entities/" + name + ".png");
    }

    public static final ResourceLocation HUD_ELEMENTS = gui("elements");
    public static final ResourceLocation GUI_CRAFT_STATION = gui("guiCraftStation");
    public static final ResourceLocation GUI_ENCHANT_STAFF = gui("guiEnchantStaff");
    public static final ResourceLocation GUI_MAGICK_PEDIA = gui("guiMagickPedia");

    public static final ResourceLocation ENTITY_888 = entity("888");
    public static final ResourceLocation ENTITY_888_EYES = entity("888eyes");
}
