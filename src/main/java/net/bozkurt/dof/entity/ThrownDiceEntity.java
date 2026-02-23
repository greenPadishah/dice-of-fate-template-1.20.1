package net.bozkurt.dof.entity;

import net.bozkurt.dof.item.ModItems;
import net.bozkurt.dof.network.DiceOfFateNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class ThrownDiceEntity extends ThrownItemEntity {
    private static final TrackedData<Integer> DICE_TYPE = DataTracker.registerData(ThrownDiceEntity.class, TrackedDataHandlerRegistry.INTEGER);
    // 0 = white, 1 = black, 2 = red
    
    private Hand hand;
    private int ticksAlive = 0;

    public ThrownDiceEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public ThrownDiceEntity(EntityType<? extends ThrownItemEntity> entityType, LivingEntity owner, World world, ItemStack stack, Hand hand) {
        super(entityType, owner, world);
        int type = 0; // default white
        if (stack.getItem() == ModItems.DICE_OF_FATE_BLACK) {
            type = 1;
        } else if (stack.getItem() == ModItems.DICE_OF_FATE_RED) {
            type = 2;
        }
        this.dataTracker.set(DICE_TYPE, type);
        this.hand = hand;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(DICE_TYPE, 0);
    }

    public boolean isBlack() {
        return this.dataTracker.get(DICE_TYPE) == 1;
    }

    public boolean isRed() {
        return this.dataTracker.get(DICE_TYPE) == 2;
    }

    public int getDiceType() {
        return this.dataTracker.get(DICE_TYPE);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.SNOWBALL;
    }

    @Override
    public void tick() {
        super.tick();
        ticksAlive++;

        if (!this.getWorld().isClient && ticksAlive >= 20) {
            openWagerMenu();
            this.discard();
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            openWagerMenu();
            this.discard();
        }
    }

    private void openWagerMenu() {
        if (this.getOwner() instanceof ServerPlayerEntity player) {
            if (isRed()) {
                var buf = PacketByteBufs.create();
                buf.writeInt(hand.ordinal());
                ServerPlayNetworking.send(player, DiceOfFateNetworking.OPEN_RED_DICE_PACKET, buf);
            } else {
                var buf = PacketByteBufs.create();
                buf.writeBoolean(isBlack());
                buf.writeInt(hand.ordinal());
                ServerPlayNetworking.send(player, DiceOfFateNetworking.OPEN_WAGER_PACKET, buf);
            }
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("DiceType", this.dataTracker.get(DICE_TYPE));
        nbt.putInt("Hand", hand.ordinal());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(DICE_TYPE, nbt.getInt("DiceType"));
        int handOrdinal = nbt.getInt("Hand");
        hand = Hand.values()[Math.max(0, Math.min(Hand.values().length - 1, handOrdinal))];
    }
}
