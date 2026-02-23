package net.bozkurt.dof.logic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.bozkurt.dof.effect.DiceEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public final class DiceEffectScheduler {
    private static final List<PendingEffect> PENDING = new ArrayList<>();

    private DiceEffectScheduler() {
    }

    public static void schedule(ServerPlayerEntity player, DiceEffect effect, int delayTicks) {
        if (effect == null) {
            return;
        }
        if (delayTicks <= 0) {
            effect.apply(player, player.getRandom());
            return;
        }
        PENDING.add(new PendingEffect(player.getUuid(), effect, delayTicks));
    }

    public static void tick(MinecraftServer server) {
        if (PENDING.isEmpty()) {
            return;
        }

        Iterator<PendingEffect> iterator = PENDING.iterator();
        while (iterator.hasNext()) {
            PendingEffect entry = iterator.next();
            entry.ticksRemaining--;
            if (entry.ticksRemaining > 0) {
                continue;
            }

            ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.playerId);
            if (player != null) {
                entry.effect.apply(player, player.getRandom());
                if (player.isCreative()) {
                    player.sendMessage(Text.literal("Dice effect: " + entry.effect.id()), false);
                }
            }
            iterator.remove();
        }
    }

    private static final class PendingEffect {
        private final UUID playerId;
        private final DiceEffect effect;
        private int ticksRemaining;

        private PendingEffect(UUID playerId, DiceEffect effect, int ticksRemaining) {
            this.playerId = playerId;
            this.effect = effect;
            this.ticksRemaining = ticksRemaining;
        }
    }
}
