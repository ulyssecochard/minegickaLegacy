package com.alco.minegickalegacy.datagen;

import com.alco.minegickalegacy.registry.MinegickaBlocks;
import com.alco.minegickalegacy.registry.MinegickaDeferredRegisters;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class MinegickaLootTableProvider extends LootTableProvider {
    public MinegickaLootTableProvider(final PackOutput output) {
        super(output, Set.of(), List.of(new SubProviderEntry(MinegickaBlockLoot::new, LootContextParamSets.BLOCK)));
    }

    private static final class MinegickaBlockLoot extends BlockLootSubProvider {
        protected MinegickaBlockLoot() {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags());
        }

        @Override
        protected void generate() {
            dropSelf(MinegickaBlocks.SHIELD_BLOCK.get());
            dropSelf(MinegickaBlocks.WALL_BLOCK.get());
            dropSelf(MinegickaBlocks.CRAFT_STATION.get());
            dropSelf(MinegickaBlocks.ENCHANT_STAFF.get());
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return MinegickaDeferredRegisters.BLOCKS.getEntries().stream()
                    .map(RegistryObject::get)
                    .collect(Collectors.toList());
        }
    }
}
