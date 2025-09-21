package com.alco.minegickalegacy.block;

import com.alco.minegickalegacy.blockentity.WallBlockEntity;
import com.alco.minegickalegacy.registry.MinegickaBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WallBlock extends Block implements EntityBlock {
    public WallBlock(final Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new WallBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(final Level level,
                                                                  final BlockState state,
                                                                  final BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        if (type == MinegickaBlockEntities.WALL.get()) {
            return (lvl, blockPos, blockState, blockEntity) -> {
                if (blockEntity instanceof WallBlockEntity wall) {
                    WallBlockEntity.serverTick(lvl, blockPos, blockState, wall);
                }
            };
        }
        return null;
    }

    @Override
    public boolean triggerEvent(final BlockState state, final Level level, final BlockPos pos, final int id, final int param) {
        final BlockEntity blockEntity = level.getBlockEntity(pos);
        return (blockEntity != null && blockEntity.triggerEvent(id, param)) || super.triggerEvent(state, level, pos, id, param);
    }
}
