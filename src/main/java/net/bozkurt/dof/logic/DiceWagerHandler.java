package net.bozkurt.dof.logic;

import net.minecraft.util.math.random.Random;
import net.bozkurt.dof.effect.DiceEffect;
import net.bozkurt.dof.effect.DiceEffectRegistry;
import net.bozkurt.dof.effect.DiceType;
import net.bozkurt.dof.effect.EffectAlignment;
import net.bozkurt.dof.item.ModItems;
import net.bozkurt.dof.network.DiceOfFateNetworking;
import net.bozkurt.dof.wager.WagerType;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

public final class DiceWagerHandler {
    private static final int COOLDOWN_TICKS = 20 * 10;
    private static final int EFFECT_DELAY_TICKS = 48;

    private DiceWagerHandler() {
    }

    public static void handle(ServerPlayerEntity player, boolean isBlack, int handOrdinal, int wagerOrdinal) {
        Hand hand = Hand.values()[Math.max(0, Math.min(Hand.values().length - 1, handOrdinal))];
        WagerType wager = WagerType.fromOrdinal(wagerOrdinal);
        DiceType diceType = isBlack ? DiceType.BLACK : DiceType.WHITE;
        ItemStack stack = player.getStackInHand(hand);

        if (!isValidDice(stack, diceType)) {
            return;
        }

        if (isCoolingDown(player)) {
            return;
        }

        Random random = player.getRandom();
        if (!consumeWager(player, wager, random)) {
            return;
        }

        int roll = random.nextInt(6) + 1;
        boolean good = isGoodRoll(random, roll);
        DiceEffect effect = DiceEffectRegistry.getRandom(diceType, good ? EffectAlignment.GOOD : EffectAlignment.BAD, random);

        var buf = PacketByteBufs.create();
        buf.writeInt(roll);
        ServerPlayNetworking.send(player, DiceOfFateNetworking.DICE_ROLL_PACKET, buf);

        DiceEffectScheduler.schedule(player, effect, EFFECT_DELAY_TICKS);

        setSharedCooldown(player);
    }

    private static boolean isValidDice(ItemStack stack, DiceType diceType) {
        if (stack.isEmpty()) {
            return false;
        }
        if (diceType == DiceType.BLACK) {
            return stack.getItem() == ModItems.DICE_OF_FATE_BLACK;
        }
        return stack.getItem() == ModItems.DICE_OF_FATE;
    }

    private static boolean isCoolingDown(ServerPlayerEntity player) {
        return player.getItemCooldownManager().isCoolingDown(ModItems.DICE_OF_FATE)
            || player.getItemCooldownManager().isCoolingDown(ModItems.DICE_OF_FATE_BLACK);
    }

    private static void setSharedCooldown(ServerPlayerEntity player) {
        player.getItemCooldownManager().set(ModItems.DICE_OF_FATE, COOLDOWN_TICKS);
        player.getItemCooldownManager().set(ModItems.DICE_OF_FATE_BLACK, COOLDOWN_TICKS);
    }

    private static boolean consumeWager(ServerPlayerEntity player, WagerType wager, Random random) {
        if (wager == WagerType.IRON) {
            return removeOne(player, Items.IRON_INGOT, "You need an iron ingot to wager.");
        }
        if (wager == WagerType.DIAMOND) {
            return removeOne(player, Items.DIAMOND, "You need a diamond to wager.");
        }

        if (random.nextInt(200) == 0) {
            player.damage(player.getDamageSources().outOfWorld(), 10000.0f);
            return false;
        }

        return true;
    }

    private static boolean removeOne(ServerPlayerEntity player, Item item, String failMessage) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() == item && stack.getCount() > 0) {
                stack.decrement(1);
                if (stack.isEmpty()) {
                    player.getInventory().setStack(i, ItemStack.EMPTY);
                }
                return true;
            }
        }

        for (int i = 0; i < player.getInventory().offHand.size(); i++) {
            ItemStack stack = player.getInventory().offHand.get(i);
            if (stack.getItem() == item && stack.getCount() > 0) {
                stack.decrement(1);
                if (stack.isEmpty()) {
                    player.getInventory().offHand.set(i, ItemStack.EMPTY);
                }
                return true;
            }
        }

        player.sendMessage(Text.literal(failMessage), true);
        return false;
    }

    private static boolean isGoodRoll(Random random, int roll) {
        int goodWeight = roll >= 6 ? 5 : roll;
        return random.nextInt(6) < goodWeight;
    }
}
