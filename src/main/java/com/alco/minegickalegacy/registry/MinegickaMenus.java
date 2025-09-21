package com.alco.minegickalegacy.registry;

import com.alco.minegickalegacy.MinegickaMod;
import com.alco.minegickalegacy.menu.CraftStationMenu;
import com.alco.minegickalegacy.menu.EnchantStaffMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class MinegickaMenus {
    private MinegickaMenus() {
    }

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MinegickaMod.MODID);

    public static final RegistryObject<MenuType<CraftStationMenu>> CRAFT_STATION = MENUS.register("craft_station",
            () -> IForgeMenuType.create(CraftStationMenu::createClient));

    public static final RegistryObject<MenuType<EnchantStaffMenu>> ENCHANT_STAFF = MENUS.register("enchant_staff",
            () -> IForgeMenuType.create(EnchantStaffMenu::createClient));
}
