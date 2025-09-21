package com.alco.minegickalegacy.spell;

import com.alco.minegickalegacy.registry.MagickRegistry.MagickDefinition;
import net.minecraft.server.level.ServerPlayer;

@FunctionalInterface
public interface MagickEffect {
    void perform(ServerPlayer player, MagickDefinition definition);
}
