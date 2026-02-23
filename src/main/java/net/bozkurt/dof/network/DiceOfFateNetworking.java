package net.bozkurt.dof.network;

import net.bozkurt.dof.DiceOfFate;
import net.bozkurt.dof.effect.DiceEffect;
import net.bozkurt.dof.effect.DiceEffectRegistry;
import net.bozkurt.dof.logic.DiceWagerHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public final class DiceOfFateNetworking {
    public static final Identifier DICE_ROLL_PACKET = new Identifier(DiceOfFate.MOD_ID, "dice_roll");
    public static final Identifier OPEN_WAGER_PACKET = new Identifier(DiceOfFate.MOD_ID, "open_wager");
    public static final Identifier WAGER_PACKET = new Identifier(DiceOfFate.MOD_ID, "wager_selection");
    public static final Identifier RED_DICE_EFFECT_PACKET = new Identifier(DiceOfFate.MOD_ID, "red_dice_effect");
    public static final Identifier OPEN_RED_DICE_PACKET = new Identifier(DiceOfFate.MOD_ID, "open_red_dice");

    private DiceOfFateNetworking() {
    }

    public static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(WAGER_PACKET, (server, player, handler, buf, responseSender) -> {
            boolean isBlack = buf.readBoolean();
            int handOrdinal = buf.readInt();
            int wagerOrdinal = buf.readInt();

            server.execute(() -> DiceWagerHandler.handle(player, isBlack, handOrdinal, wagerOrdinal));
        });

        ServerPlayNetworking.registerGlobalReceiver(RED_DICE_EFFECT_PACKET, (server, player, handler, buf, responseSender) -> {
            buf.readInt(); // hand ordinal (not needed on server)
            int effectIndex = buf.readInt();

            server.execute(() -> {
                List<DiceEffect> allEffects = DiceEffectRegistry.getAllEffects();
                if (effectIndex >= 0 && effectIndex < allEffects.size()) {
                    DiceEffect effect = allEffects.get(effectIndex);
                    effect.apply(player, player.getRandom());
                    
                    if (player.isCreative()) {
                        String translationKey = "effect.dice-of-fate." + effect.id();
                        Text translatedName = Text.translatable(translationKey);
                        player.sendMessage(Text.literal("Dice effect: ").append(translatedName), false);
                    }
                }
            });
        });
    }
}
