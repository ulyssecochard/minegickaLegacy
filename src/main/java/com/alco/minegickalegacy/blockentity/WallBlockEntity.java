package com.alco.minegickalegacy.blockentity;

import com.alco.minegickalegacy.registry.MinegickaBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Simplified wall block entity used as a hook for the legacy spell-driven wall behaviour.
 */
public class WallBlockEntity extends BlockEntity {
    private double life = 40.0D;

    public WallBlockEntity(final BlockPos pos, final BlockState state) {
        super(MinegickaBlockEntities.WALL.get(), pos, state);
    }

    public double getLife() {
        return life;
    }

    public void setLife(final double value) {
        life = value;
    }

    @Override
    protected void saveAdditional(final CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putDouble("Life", life);
    }

    @Override
    public void load(final CompoundTag tag) {
        super.load(tag);
        life = tag.getDouble("Life");
    }

    public static void serverTick(final Level level, final BlockPos pos, final BlockState state, final WallBlockEntity blockEntity) {
        if (level.isClientSide) {
            return;
        }
        if (blockEntity.life <= 0.0D) {
            level.destroyBlock(pos, false);
            return;
        }
        blockEntity.life--;
        if (blockEntity.life <= 0.0D) {
            level.destroyBlock(pos, false);
        }
    }
}
