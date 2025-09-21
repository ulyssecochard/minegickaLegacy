package com.alco.minegickalegacy.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;

public final class MinegickaDataGenerators {
    private MinegickaDataGenerators() {
    }

    public static void gatherData(final GatherDataEvent event) {
        final DataGenerator generator = event.getGenerator();
        final ExistingFileHelper fileHelper = event.getExistingFileHelper();

        if (event.includeClient()) {
            generator.addProvider(true, new MinegickaItemModelProvider(generator.getPackOutput(), fileHelper));
            generator.addProvider(true, new MinegickaLanguageProvider(generator.getPackOutput(), "en_us"));
            generator.addProvider(true, new MinegickaBlockStateProvider(generator.getPackOutput(), fileHelper));
        }

        if (event.includeServer()) {
            generator.addProvider(true, new MinegickaLootTableProvider(generator.getPackOutput()));
        }
    }
}
