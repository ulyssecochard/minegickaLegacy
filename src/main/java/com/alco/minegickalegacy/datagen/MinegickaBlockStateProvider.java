package com.alco.minegickalegacy.datagen;

import com.alco.minegickalegacy.MinegickaMod;
import com.alco.minegickalegacy.registry.MinegickaDeferredRegisters;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;

public final class MinegickaBlockStateProvider extends BlockStateProvider {
    private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "block/iron_block");
    private static final Map<String, ResourceLocation> TEXTURE_OVERRIDES = Map.of(
            "wall_block", ResourceLocation.fromNamespaceAndPath("minecraft", "block/stone_bricks"),
            "craft_station", ResourceLocation.fromNamespaceAndPath("minecraft", "block/smithing_table_side"),
            "enchant_staff", ResourceLocation.fromNamespaceAndPath("minecraft", "block/enchanting_table_top")
    );

    public MinegickaBlockStateProvider(final PackOutput output, final ExistingFileHelper helper) {
        super(output, MinegickaMod.MODID, helper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (RegistryObject<Block> entry : MinegickaDeferredRegisters.BLOCKS.getEntries()) {
            final Block block = entry.get();
            final String name = entry.getId().getPath();
            final ResourceLocation texture = TEXTURE_OVERRIDES.getOrDefault(name, DEFAULT_TEXTURE);
            final BlockModelBuilder model = models().cubeAll(name, texture);
            simpleBlock(block, ConfiguredModel.builder().modelFile(model).build());
            simpleBlockItem(block, model);
        }
    }
}
