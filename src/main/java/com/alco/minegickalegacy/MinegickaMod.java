package com.alco.minegickalegacy;

import com.alco.minegickalegacy.capability.PlayerManaCapability;
import com.alco.minegickalegacy.datagen.MinegickaDataGenerators;
import com.alco.minegickalegacy.network.MinegickaNetwork;
import com.alco.minegickalegacy.registry.MinegickaBlocks;
import com.alco.minegickalegacy.registry.MinegickaDeferredRegisters;
import com.alco.minegickalegacy.registry.MinegickaEntityTypes;
import com.alco.minegickalegacy.registry.MinegickaItems;
import com.alco.minegickalegacy.registry.MinegickaMobEffects;
import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(MinegickaMod.MODID)
public class MinegickaMod {
    public static final String MODID = "minegicka";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final MinegickaProxy PROXY = DistExecutor.safeRunForDist(
            () -> MinegickaClientProxy::new,
            () -> MinegickaCommonProxy::new
    );

    public MinegickaMod() {
        LOGGER.info("Bootstrapping Minegicka Legacy skeleton");

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(MinegickaDataGenerators::gatherData);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MinegickaConfig.SPEC);

        MinegickaItems.bootstrap();
        MinegickaBlocks.bootstrap();
        MinegickaMobEffects.bootstrap();
        MinegickaEntityTypes.bootstrap();

        MinegickaNetwork.register();


        MinegickaDeferredRegisters.register(modEventBus);
        PlayerManaCapability.register(modEventBus);
        MinegickaCreativeTabs.bootstrap();

        PROXY.registerModEvents(modEventBus);
        PROXY.registerForgeEvents();
    }
}
