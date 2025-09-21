package com.alco.minegickalegacy.registry;

import com.alco.minegickalegacy.block.CraftStationBlock;
import com.alco.minegickalegacy.block.EnchantStaffBlock;
import com.alco.minegickalegacy.block.ShieldBlock;
import com.alco.minegickalegacy.block.WallBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class MinegickaBlocks {
    public static final RegistryObject<Block> SHIELD_BLOCK = registerBlock("shield_block",
            () -> new ShieldBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).strength(5.0F, 1200.0F).sound(SoundType.GLASS).noOcclusion()));

    public static final RegistryObject<Block> WALL_BLOCK = registerBlock("wall_block",
            () -> new WallBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 1200.0F).sound(SoundType.GLASS).noOcclusion()));

    public static final RegistryObject<Block> CRAFT_STATION = registerBlock("craft_station",
            () -> new CraftStationBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(3.5F).sound(SoundType.STONE)));

    public static final RegistryObject<Block> ENCHANT_STAFF = registerBlock("enchant_staff",
            () -> new EnchantStaffBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(3.5F).sound(SoundType.STONE)));

    private MinegickaBlocks() {
    }

    private static RegistryObject<Block> registerBlock(final String name, final Supplier<Block> blockSupplier) {
        final RegistryObject<Block> block = MinegickaDeferredRegisters.BLOCKS.register(name, blockSupplier);
        MinegickaItems.registerBlockItem(name, block);
        return block;
    }

    public static void bootstrap() {
        // Ensures static initialisation is triggered.
    }
}
