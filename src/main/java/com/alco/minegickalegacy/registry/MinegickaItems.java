package com.alco.minegickalegacy.registry;

import com.alco.minegickalegacy.items.MagickTabletItem;
import com.alco.minegickalegacy.items.StaffItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class MinegickaItems {
    private static final List<Supplier<? extends Item>> CREATIVE_ORDER = new ArrayList<>();

    private MinegickaItems() {
    }

    public static final RegistryObject<Item> THINGY = registerSimpleItem("thingy");
    public static final RegistryObject<Item> THINGY_GOOD = registerSimpleItem("thingy_good");
    public static final RegistryObject<Item> THINGY_SUPER = registerSimpleItem("thingy_super");

    public static final RegistryObject<Item> STICK = registerSimpleItem("the_stick");
    public static final RegistryObject<Item> STICK_GOOD = registerSimpleItem("the_stick_good");
    public static final RegistryObject<Item> STICK_SUPER = registerSimpleItem("the_stick_super");

    public static final RegistryObject<Item> MAGIC_APPLE = registerSimpleItem("magic_apple");
    public static final RegistryObject<Item> MAGIC_GOOD_APPLE = registerSimpleItem("magic_good_apple");
    public static final RegistryObject<Item> MAGIC_SUPER_APPLE = registerSimpleItem("magic_super_apple");

    public static final RegistryObject<Item> MAGIC_COOKIE = registerSimpleItem("magic_cookie");
    public static final RegistryObject<Item> MAGIC_GOOD_COOKIE = registerSimpleItem("magic_good_cookie");
    public static final RegistryObject<Item> MAGIC_SUPER_COOKIE = registerSimpleItem("magic_super_cookie");

    public static final RegistryObject<Item> ESSENCE_ARCANE = registerSimpleItem("arcane_essence");
    public static final RegistryObject<Item> ESSENCE_COLD = registerSimpleItem("cold_essence");
    public static final RegistryObject<Item> ESSENCE_EARTH = registerSimpleItem("earth_essence");
    public static final RegistryObject<Item> ESSENCE_FIRE = registerSimpleItem("fire_essence");
    public static final RegistryObject<Item> ESSENCE_ICE = registerSimpleItem("ice_essence");
    public static final RegistryObject<Item> ESSENCE_LIFE = registerSimpleItem("life_essence");
    public static final RegistryObject<Item> ESSENCE_LIGHTNING = registerSimpleItem("lightning_essence");
    public static final RegistryObject<Item> ESSENCE_SHIELD = registerSimpleItem("shield_essence");
    public static final RegistryObject<Item> ESSENCE_STEAM = registerSimpleItem("steam_essence");
    public static final RegistryObject<Item> ESSENCE_WATER = registerSimpleItem("water_essence");

    public static final RegistryObject<Item> MAT_RESISTANCE = registerSimpleItem("mat_resistance");

    public static final RegistryObject<Item> MAGICK_PEDIA = registerSingleStackItem("magick_pedia");
    public static final RegistryObject<Item> MAGICK_TABLET = registerItem("magick_tablet", () -> new MagickTabletItem(singleStackProperties()));

    public static final RegistryObject<Item> STAFF = registerStaff("staff", new StaffItem.Stats(1.0D, 1.0D, 1.0D, 1.0D));
    public static final RegistryObject<Item> STAFF_GRAND = registerStaff("staff_grand", new StaffItem.Stats(2.0D, 2.0D, 0.5D, 1.5D));
    public static final RegistryObject<Item> STAFF_SUPER = registerStaff("staff_super", new StaffItem.Stats(4.0D, 4.0D, 0.25D, 2.0D));
    public static final RegistryObject<Item> HEMMY_STAFF = registerStaff("hemmy_staff", new StaffItem.Stats(2.5D, 20.0D, 0.1D, 10.0D));
    public static final RegistryObject<Item> STAFF_BLESSING = registerStaff("staff_blessing", new StaffItem.Stats(0.75D, 1.5D, 1.25D, 1.25D));
    public static final RegistryObject<Item> STAFF_DESTRUCTION = registerStaff("staff_destruction", new StaffItem.Stats(1.5D, 0.75D, 0.8D, 0.8D));
    public static final RegistryObject<Item> STAFF_TELEKINESIS = registerStaff("staff_telekinesis", new StaffItem.Stats(0.5D, 2.0D, 0.5D, 2.0D));
    public static final RegistryObject<Item> STAFF_MANIPULATION = registerStaff("staff_manipulation", new StaffItem.Stats(1.0D, 1.0D, 0.5D, 2.0D));

    public static final RegistryObject<Item> HAT = registerSingleStackItem("hat");
    public static final RegistryObject<Item> HAT_IMMUNITY = registerSingleStackItem("hat_of_immunity");
    public static final RegistryObject<Item> HAT_RISK = registerSingleStackItem("hat_of_risk");
    public static final RegistryObject<Item> HAT_RESISTANCE = registerSingleStackItem("hat_of_resistance");

    public static final RegistryObject<Item> TEST_ITEM = registerSimpleItem("test");

    private static RegistryObject<Item> registerSimpleItem(final String name) {
        return registerItem(name, () -> new Item(defaultProperties()));
    }

    private static RegistryObject<Item> registerSingleStackItem(final String name) {
        return registerItem(name, () -> new Item(singleStackProperties()));
    }

    private static RegistryObject<Item> registerStaff(final String name, final StaffItem.Stats stats) {
        return registerItem(name, () -> new StaffItem(stats, defaultProperties()));
    }

    private static RegistryObject<Item> registerItem(final String name, final Supplier<Item> supplier) {
        final RegistryObject<Item> registryObject = MinegickaDeferredRegisters.ITEMS.register(name, supplier);
        CREATIVE_ORDER.add(registryObject);
        return registryObject;
    }

    public static RegistryObject<Item> registerBlockItem(final String name, final Supplier<? extends Block> blockSupplier) {
        final RegistryObject<Item> registryObject = MinegickaDeferredRegisters.ITEMS.register(name, () -> new BlockItem(blockSupplier.get(), defaultProperties()));
        CREATIVE_ORDER.add(registryObject);
        return registryObject;
    }

    private static Item.Properties defaultProperties() {
        return new Item.Properties();
    }

    private static Item.Properties singleStackProperties() {
        return new Item.Properties().stacksTo(1);
    }

    public static void addToCreativeTab(final CreativeModeTab.Output output) {
        CREATIVE_ORDER.stream().map(Supplier::get).forEach(output::accept);
    }

    public static void bootstrap() {
        // Ensures the class is loaded and the static initialisers run.
    }
}
