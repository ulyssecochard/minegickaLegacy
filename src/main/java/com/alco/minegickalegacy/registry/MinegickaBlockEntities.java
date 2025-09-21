package com.alco.minegickalegacy.registry;

import com.alco.minegickalegacy.MinegickaMod;
import com.alco.minegickalegacy.blockentity.CraftStationBlockEntity;
import com.alco.minegickalegacy.blockentity.EnchantStaffBlockEntity;
import com.alco.minegickalegacy.blockentity.ShieldBlockEntity;
import com.alco.minegickalegacy.blockentity.WallBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class MinegickaBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MinegickaMod.MODID);

    public static final RegistryObject<BlockEntityType<CraftStationBlockEntity>> CRAFT_STATION = register("craft_station", CraftStationBlockEntity::new, MinegickaBlocks.CRAFT_STATION);
    public static final RegistryObject<BlockEntityType<EnchantStaffBlockEntity>> ENCHANT_STAFF = register("enchant_staff", EnchantStaffBlockEntity::new, MinegickaBlocks.ENCHANT_STAFF);
    public static final RegistryObject<BlockEntityType<ShieldBlockEntity>> SHIELD = register("shield_block", ShieldBlockEntity::new, MinegickaBlocks.SHIELD_BLOCK);
    public static final RegistryObject<BlockEntityType<WallBlockEntity>> WALL = register("wall_block", WallBlockEntity::new, MinegickaBlocks.WALL_BLOCK);

    private MinegickaBlockEntities() {
    }

    public static void register(final IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(final String name,
                                                                                       final BlockEntityType.BlockEntitySupplier<T> factory,
                                                                                       final Supplier<? extends Block> blockSupplier) {
        return BLOCK_ENTITIES.register(name, () -> BlockEntityType.Builder.of(factory, blockSupplier.get()).build(null));
    }
}
