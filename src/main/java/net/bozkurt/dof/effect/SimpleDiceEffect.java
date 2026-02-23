package net.bozkurt.dof.effect;

import java.util.function.BiConsumer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.Random;

public class SimpleDiceEffect implements DiceEffect {
    private final String id;
    private final BiConsumer<ServerPlayerEntity, Random> effect;

    public SimpleDiceEffect(String id, BiConsumer<ServerPlayerEntity, Random> effect) {
        this.id = id;
        this.effect = effect;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public void apply(ServerPlayerEntity player, Random random) {
        effect.accept(player, random);
    }
}
