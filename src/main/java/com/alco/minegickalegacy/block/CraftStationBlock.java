package com.alco.minegickalegacy.block;

import com.alco.minegickalegacy.blockentity.CraftStationBlockEntity;
import com.alco.minegickalegacy.menu.CraftStationMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class CraftStationBlock extends Block implements EntityBlock {
    public CraftStationBlock(final Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new CraftStationBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(final Level level,
                                                                  final BlockState state,
                                                                  final BlockEntityType<T> type) {
        return null;
    }

    @Override
    public InteractionResult use(final BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            final BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof CraftStationBlockEntity craftStation) {
                NetworkHooks.openScreen(serverPlayer, craftStation, pos);
            } else {
                NetworkHooks.openScreen(serverPlayer,
                        new SimpleMenuProvider((id, inventory, p) -> CraftStationMenu.createFallback(id, inventory, pos),
                                Component.translatable("block.minegicka.craft_station")), pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onRemove(final BlockState state, final Level level, final BlockPos pos, final BlockState newState, final boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            final BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof CraftStationBlockEntity craftStation) {
                craftStation.dropContents(level, pos);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public boolean triggerEvent(final BlockState state, final Level level, final BlockPos pos, final int id, final int param) {
        final BlockEntity blockEntity = level.getBlockEntity(pos);
        return (blockEntity != null && blockEntity.triggerEvent(id, param)) || super.triggerEvent(state, level, pos, id, param);
    }
}
