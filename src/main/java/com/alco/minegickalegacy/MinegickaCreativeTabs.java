package com.alco.minegickalegacy;

import com.alco.minegickalegacy.registry.MinegickaItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class MinegickaCreativeTabs {
    private MinegickaCreativeTabs() {
    }

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MinegickaMod.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(Items.BOOK))
                    .title(Component.translatable("itemGroup.minegicka.main"))
                    .displayItems((parameters, output) -> MinegickaItems.addToCreativeTab(output))
                    .build());

    public static void bootstrap() {
        // Ensures the class is loaded so the tab registration is initialised.
    }
}
