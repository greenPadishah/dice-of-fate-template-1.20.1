package net.bozkurt.dof.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerDiceData extends PersistentState {
    private static final String DATA_NAME = "dice_of_fate_player_data";
    private final Set<UUID> playersWhoReceivedDice = new HashSet<>();

    public PlayerDiceData() {
    }

    public static PlayerDiceData getServerState(MinecraftServer server) {
        PersistentStateManager manager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
        PlayerDiceData state = manager.getOrCreate(
            PlayerDiceData::createFromNbt,
            PlayerDiceData::new,
            DATA_NAME
        );
        return state;
    }

    public static PlayerDiceData createFromNbt(NbtCompound nbt) {
        PlayerDiceData state = new PlayerDiceData();
        
        int size = nbt.getInt("PlayerCount");
        for (int i = 0; i < size; i++) {
            UUID uuid = nbt.getUuid("Player" + i);
            state.playersWhoReceivedDice.add(uuid);
        }
        
        return state;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("PlayerCount", playersWhoReceivedDice.size());
        int index = 0;
        for (UUID uuid : playersWhoReceivedDice) {
            nbt.putUuid("Player" + index, uuid);
            index++;
        }
        return nbt;
    }

    public boolean hasReceivedDice(UUID playerId) {
        return playersWhoReceivedDice.contains(playerId);
    }

    public void markAsReceived(UUID playerId) {
        playersWhoReceivedDice.add(playerId);
        markDirty();
    }
}
