package com.alco.minegickalegacy.datagen;

import com.alco.minegickalegacy.MinegickaMod;
import com.alco.minegickalegacy.registry.MinegickaDeferredRegisters;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;

public final class MinegickaItemModelProvider extends ItemModelProvider {
    private static final ResourceLocation GENERATED = ResourceLocation.fromNamespaceAndPath("minecraft", "item/generated");
    private static final Map<String, ResourceLocation> ITEM_TEXTURE_OVERRIDES = Map.of();

    public MinegickaItemModelProvider(final PackOutput output, final ExistingFileHelper helper) {
        super(output, MinegickaMod.MODID, helper);
    }

    @Override
    protected void registerModels() {
        for (RegistryObject<Item> entry : MinegickaDeferredRegisters.ITEMS.getEntries()) {
            final Item item = entry.get();
            if (item instanceof BlockItem) {
                continue;
            }
            final ResourceLocation texture = ITEM_TEXTURE_OVERRIDES.getOrDefault(entry.getId().getPath(),
                    ResourceLocation.fromNamespaceAndPath(MinegickaMod.MODID, "item/" + entry.getId().getPath()));
            singleTexture(entry.getId().getPath(), GENERATED, "layer0", texture);
        }
    }
}
