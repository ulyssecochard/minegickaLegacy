package com.alco.minegickalegacy.capability;

import com.alco.minegickalegacy.MinegickaMod;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public final class PlayerManaCapability {
    public static final Capability<PlayerManaData> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    private static final ResourceLocation CAP_ID = ResourceLocation.fromNamespaceAndPath(MinegickaMod.MODID, "player_mana");

    private PlayerManaCapability() {
    }

    public static void register(final IEventBus modEventBus) {
        modEventBus.addListener(PlayerManaCapability::onRegisterCapabilities);
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, PlayerManaCapability::attachCapabilities);
        MinecraftForge.EVENT_BUS.addListener(PlayerManaCapability::onPlayerClone);
        PlayerManaEvents.register();
    }

    private static void onRegisterCapabilities(final RegisterCapabilitiesEvent event) {
        event.register(PlayerManaData.class);
    }

    private static void attachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof Player player)) {
            return;
        }

        event.addCapability(CAP_ID, new Provider(player));
    }

    private static void onPlayerClone(final PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            return;
        }

        event.getOriginal().reviveCaps();
        final PlayerManaData original = event.getOriginal().getCapability(CAPABILITY).orElse(null);
        final PlayerManaData target = event.getEntity().getCapability(CAPABILITY).orElse(null);
        if (original != null && target != null) {
            target.copyFrom(original);
        }
        event.getOriginal().invalidateCaps();
    }

    public static final class Provider extends CapabilityProvider<Provider> implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final PlayerManaData backend;
        private final LazyOptional<PlayerManaData> optional;

        public Provider(final Player owner) {
            super(Provider.class);
            this.backend = new PlayerManaData(owner);
            this.optional = LazyOptional.of(() -> backend);
        }

        @Override
        public CompoundTag serializeNBT() {
            return backend.serializeNBT();
        }

        @Override
        public void deserializeNBT(final CompoundTag nbt) {
            backend.deserializeNBT(nbt);
        }

        @Override
        public <T> LazyOptional<T> getCapability(final Capability<T> cap, final Direction side) {
            return CAPABILITY.orEmpty(cap, optional);
        }
    }
}
