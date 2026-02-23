package net.bozkurt.dof.item.custom;

import net.bozkurt.dof.entity.ModEntities;
import net.bozkurt.dof.entity.ThrownDiceEntity;
import net.bozkurt.dof.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
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
            || user.getItemCooldownManager().isCoolingDown(ModItems.DICE_OF_FATE_BLACK)
            || user.getItemCooldownManager().isCoolingDown(ModItems.DICE_OF_FATE_RED)) {
            return TypedActionResult.fail(stack);
        }
        
        world.playSound(null, user.getX(), user.getY(), user.getZ(),
            SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.PLAYERS,
            0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
        
        if (!world.isClient) {
            ThrownDiceEntity diceEntity = new ThrownDiceEntity(ModEntities.THROWN_DICE, user, world, stack, hand);
            diceEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 0.8f, 1.0f);
            world.spawnEntity(diceEntity);
        }
        
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        return TypedActionResult.success(stack, world.isClient());
    }
}
