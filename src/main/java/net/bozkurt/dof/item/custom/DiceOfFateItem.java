package net.bozkurt.dof.item.custom;

import net.bozkurt.dof.item.ModItems;
import net.bozkurt.dof.network.DiceOfFateNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class DiceOfFateItem extends Item{
    public DiceOfFateItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (user.getItemCooldownManager().isCoolingDown(ModItems.DICE_OF_FATE)
            || user.getItemCooldownManager().isCoolingDown(ModItems.DICE_OF_FATE_BLACK)) {
            return TypedActionResult.fail(stack);
        }
        if (!world.isClient && user instanceof ServerPlayerEntity serverPlayer) {
            boolean isBlack = stack.getItem() == ModItems.DICE_OF_FATE_BLACK;
            var buf = PacketByteBufs.create();
            buf.writeBoolean(isBlack);
            buf.writeInt(hand.ordinal());
            ServerPlayNetworking.send(serverPlayer, DiceOfFateNetworking.OPEN_WAGER_PACKET, buf);
        }

        return TypedActionResult.success(stack, world.isClient);
    }
    
}
