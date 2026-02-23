package net.bozkurt.dof.effect;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.Random;

public interface DiceEffect {
    String id();

    void apply(ServerPlayerEntity player, Random random);
}
