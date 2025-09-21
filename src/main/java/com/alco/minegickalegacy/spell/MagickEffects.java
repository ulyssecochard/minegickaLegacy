package com.alco.minegickalegacy.spell;

import com.alco.minegickalegacy.items.StaffItem;
import com.alco.minegickalegacy.mechanics.Element;
import com.alco.minegickalegacy.registry.MagickRegistry.MagickDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public final class MagickEffects {
    private static final double DEFAULT_RANGE = 8.0D;

    private MagickEffects() {
    }

    public static void collect(final ServerPlayer player, final MagickDefinition definition) {
        final Level level = player.level();
        final Vec3 center = player.position();
        final AABB box = player.getBoundingBox().inflate(DEFAULT_RANGE);

        for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, box)) {
            item.setNoPickUpDelay();
            final Vec3 direction = center.subtract(item.position());
            item.setDeltaMovement(direction.normalize().scale(0.5D));
        }

        for (ExperienceOrb orb : level.getEntitiesOfClass(ExperienceOrb.class, box)) {
            orb.playerTouch(player);
        }

        level.playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.6F, 1.4F);
    }

    public static void dePotion(final ServerPlayer player, final MagickDefinition definition) {
        player.getActiveEffects().stream()
                .map(MobEffectInstance::getEffect)
                .filter(effect -> effect.isBeneficial() ? false : true)
                .forEach(effect -> player.removeEffect(effect));
        player.level().playSound(null, player.blockPosition(), SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 0.7F, 0.7F);
    }

    public static void deSpawners(final ServerPlayer player, final MagickDefinition definition) {
        final ServerLevel level = player.serverLevel();
        final BlockPos origin = player.blockPosition();
        final int radius = 6;
        BlockPos.betweenClosed(origin.offset(-radius, -radius, -radius), origin.offset(radius, radius, radius)).forEach(pos -> {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof SpawnerBlockEntity) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                level.playSound(null, pos, SoundEvents.ANVIL_BREAK, SoundSource.BLOCKS, 0.7F, 0.5F);
            }
        });
    }

    public static void explosion(final ServerPlayer player, final MagickDefinition definition) {
        final double power = SpellAttributes.staffStats(player).power();
        final float strength = (float) (3.0F + power);
        player.level().explode(player, player.getX(), player.getY(), player.getZ(), strength, Level.ExplosionInteraction.NONE);
    }

    public static void extinguish(final ServerPlayer player, final MagickDefinition definition) {
        final ServerLevel level = player.serverLevel();
        player.clearFire();
        final BlockPos origin = player.blockPosition();
        final int radius = 5;
        BlockPos.betweenClosed(origin.offset(-radius, -1, -radius), origin.offset(radius, 3, radius)).forEach(pos -> {
            if (level.getBlockState(pos).is(Blocks.FIRE)) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            }
        });
        level.playSound(null, origin, SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.8F, 1.0F);
    }

    public static void featherFall(final ServerPlayer player, final MagickDefinition definition) {
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, tickDuration(player, 200), 0));
        player.level().playSound(null, player.blockPosition(), SoundEvents.ELYTRA_FLYING, SoundSource.PLAYERS, 0.5F, 1.2F);
    }

    public static void freezeMotion(final ServerPlayer player, final MagickDefinition definition) {
        final ServerLevel level = player.serverLevel();
        applyToNearbyMobs(player, 6.0D, mob -> mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, tickDuration(player, 120), 3)));
        final BlockPos origin = player.blockPosition();
        BlockPos.betweenClosed(origin.offset(-3, -1, -3), origin.offset(3, 1, 3)).forEach(pos -> {
            if (level.getFluidState(pos).is(FluidTags.WATER)) {
                level.setBlock(pos, Blocks.ICE.defaultBlockState(), Block.UPDATE_ALL);
            }
        });
        level.playSound(null, origin, SoundEvents.GLASS_HIT, SoundSource.BLOCKS, 0.8F, 0.3F);
    }

    public static void gravitational(final ServerPlayer player, final MagickDefinition definition) {
        applyToNearbyMobs(player, DEFAULT_RANGE, mob -> mob.addEffect(new MobEffectInstance(MobEffects.LEVITATION, tickDuration(player, 100), 1)));
    }

    public static void haste(final ServerPlayer player, final MagickDefinition definition) {
        final StaffItem.Stats stats = SpellAttributes.staffStats(player);
        final int duration = (int) (100 * stats.power());
        final int amplifier = Mth.clamp((int) Math.ceil(stats.attackSpeed()) - 1, 0, 4);
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, amplifier));
    }

    public static void homingLightning(final ServerPlayer player, final MagickDefinition definition) {
        final Optional<LivingEntity> target = findNearestHostile(player, 16.0D);
        if (target.isPresent()) {
            spawnLightning(player.serverLevel(), target.get().position());
        } else {
            spawnLightning(player.serverLevel(), player.position().add(player.getLookAngle().scale(6.0D)));
        }
    }

    public static void lightningBolt(final ServerPlayer player, final MagickDefinition definition) {
        spawnLightning(player.serverLevel(), player.position().add(player.getLookAngle().scale(4.0D)));
    }

    public static void nullify(final ServerPlayer player, final MagickDefinition definition) {
        for (MobEffectInstance effect : player.getActiveEffects()) {
            if (!effect.getEffect().isBeneficial()) {
                player.removeEffect(effect.getEffect());
            }
        }
        player.serverLevel().playSound(null, player.blockPosition(), SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS, 0.8F, 1.0F);
    }

    public static void snowStorm(final ServerPlayer player, final MagickDefinition definition) {
        final ServerLevel level = player.serverLevel();
        level.setWeatherParameters(0, 6000, true, false);
        final BlockPos origin = player.blockPosition();
        BlockPos.betweenClosed(origin.offset(-4, -1, -4), origin.offset(4, -1, 4)).forEach(pos -> {
            if (level.getBlockState(pos).isAir() && level.getBlockState(pos.below()).isSolid()) {
                level.setBlock(pos, Blocks.SNOW.defaultBlockState(), Block.UPDATE_ALL);
            }
        });
    }

    public static void teleport(final ServerPlayer player, final MagickDefinition definition) {
        final Vec3 look = player.getLookAngle();
        final Vec3 target = player.position().add(look.scale(8.0D));
        player.teleportTo(target.x, target.y, target.z);
        player.level().playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    public static void thaw(final ServerPlayer player, final MagickDefinition definition) {
        final ServerLevel level = player.serverLevel();
        level.setWeatherParameters(0, 6000, false, false);
        final BlockPos origin = player.blockPosition();
        BlockPos.betweenClosed(origin.offset(-3, -1, -3), origin.offset(3, 1, 3)).forEach(pos -> {
            if (level.getBlockState(pos).is(Blocks.SNOW)) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            }
            if (level.getBlockState(pos).is(Blocks.ICE)) {
                level.setBlock(pos, Blocks.WATER.defaultBlockState(), Block.UPDATE_ALL);
            }
        });
    }

    public static void vortex(final ServerPlayer player, final MagickDefinition definition) {
        final Vec3 center = player.position();
        applyToNearbyMobs(player, DEFAULT_RANGE, mob -> {
            final Vec3 pull = center.subtract(mob.position()).normalize().scale(0.5D);
            mob.setDeltaMovement(mob.getDeltaMovement().add(pull));
        });
        player.level().playSound(null, player.blockPosition(), SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 0.7F, 1.0F);
    }

    public static void waterShock(final ServerPlayer player, final MagickDefinition definition) {
        final Level level = player.level();
        final boolean inWater = player.isInWaterOrRain();
        if (inWater) {
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, tickDuration(player, 200), 0));
        }
        applyToNearbyMobs(player, DEFAULT_RANGE, mob -> {
            if (mob.isInWaterOrRain()) {
                mob.hurt(level.damageSources().magic(), 4.0F);
                mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, tickDuration(player, 100), 1));
            }
        });
    }

    private static void applyToNearbyMobs(final ServerPlayer player, final double range, final java.util.function.Consumer<Mob> action) {
        final Level level = player.level();
        final List<Mob> mobs = level.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(range));
        mobs.forEach(action);
    }

    private static Optional<LivingEntity> findNearestHostile(final ServerPlayer player, final double range) {
        return player.level().getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(range)).stream()
                .map(mob -> (LivingEntity) mob)
                .min(Comparator.comparingDouble(entity -> entity.distanceToSqr(player)));
    }
    private static void spawnLightning(final ServerLevel level, final Vec3 position) {
        final LightningBolt lightning = net.minecraft.world.entity.EntityType.LIGHTNING_BOLT.create(level);
        if (lightning == null) {
            return;
        }
        lightning.moveTo(position.x, position.y, position.z);
        level.addFreshEntity(lightning);
    }

    private static int tickDuration(final Player player, final int base) {
        return (int) (base * SpellAttributes.staffStats(player).power());
    }
}



