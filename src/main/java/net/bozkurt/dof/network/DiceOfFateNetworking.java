package net.bozkurt.dof.network;

import net.bozkurt.dof.DiceOfFate;
import net.bozkurt.dof.logic.DiceWagerHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public final class DiceOfFateNetworking {
    public static final Identifier DICE_ROLL_PACKET = new Identifier(DiceOfFate.MOD_ID, "dice_roll");
    public static final Identifier OPEN_WAGER_PACKET = new Identifier(DiceOfFate.MOD_ID, "open_wager");
    public static final Identifier WAGER_PACKET = new Identifier(DiceOfFate.MOD_ID, "wager_selection");

    private DiceOfFateNetworking() {
    }

    public static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(WAGER_PACKET, (server, player, handler, buf, responseSender) -> {
            boolean isBlack = buf.readBoolean();
            int handOrdinal = buf.readInt();
            int wagerOrdinal = buf.readInt();

            server.execute(() -> DiceWagerHandler.handle(player, isBlack, handOrdinal, wagerOrdinal));
        });
    }
}
