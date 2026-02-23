package net.bozkurt.dof.event;

import net.bozkurt.dof.data.PlayerDiceData;
import net.bozkurt.dof.item.ModItems;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public final class PlayerJoinHandler {
    private PlayerJoinHandler() {
    }

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            UUID playerId = player.getUuid();
            
            PlayerDiceData data = PlayerDiceData.getServerState(server);
            if (!data.hasReceivedDice(playerId)) {
                ItemStack whiteDice = new ItemStack(ModItems.DICE_OF_FATE, 1);
                player.getInventory().insertStack(whiteDice);
                
                data.markAsReceived(playerId);
            }
        });
    }
}
