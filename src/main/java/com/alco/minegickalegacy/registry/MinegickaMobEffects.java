package com.alco.minegickalegacy.registry;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class MinegickaMobEffects {
    public static final RegistryObject<MobEffect> COLD_RESISTANCE = register("cold_resistance",
            () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 0xEEEEFF));
    public static final RegistryObject<MobEffect> LIFE_BOOST = register("life_boost",
            () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 0x00FF00));
    public static final RegistryObject<MobEffect> ARCANE_RESISTANCE = register("arcane_resistance",
            () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 0xEE0000));
    public static final RegistryObject<MobEffect> LIGHTNING_RESISTANCE = register("lightning_resistance",
            () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 0xFF22FF));

    private MinegickaMobEffects() {
    }

    private static RegistryObject<MobEffect> register(final String name, final Supplier<MobEffect> supplier) {
        return MinegickaDeferredRegisters.MOB_EFFECTS.register(name, supplier);
    }

    public static void bootstrap() {
        // Ensures static initialisation is triggered.
    }

    private static final class SimpleMobEffect extends MobEffect {
        private SimpleMobEffect(final MobEffectCategory category, final int color) {
            super(category, color);
        }
    }
}
