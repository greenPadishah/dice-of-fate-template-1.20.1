package net.bozkurt.dof.effect;

import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.collection.DefaultedList;

public final class DiceEffects {
    private static final Map<Item, Item> UPGRADE_MAP = Map.ofEntries(
        // Tools: wooden → stone → iron → gold → diamond → netherite
        Map.entry(Items.WOODEN_SWORD, Items.STONE_SWORD),
        Map.entry(Items.STONE_SWORD, Items.IRON_SWORD),
        Map.entry(Items.IRON_SWORD, Items.GOLDEN_SWORD),
        Map.entry(Items.GOLDEN_SWORD, Items.DIAMOND_SWORD),
        Map.entry(Items.DIAMOND_SWORD, Items.NETHERITE_SWORD),
        
        Map.entry(Items.WOODEN_PICKAXE, Items.STONE_PICKAXE),
        Map.entry(Items.STONE_PICKAXE, Items.IRON_PICKAXE),
        Map.entry(Items.IRON_PICKAXE, Items.GOLDEN_PICKAXE),
        Map.entry(Items.GOLDEN_PICKAXE, Items.DIAMOND_PICKAXE),
        Map.entry(Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE),
        
        Map.entry(Items.WOODEN_AXE, Items.STONE_AXE),
        Map.entry(Items.STONE_AXE, Items.IRON_AXE),
        Map.entry(Items.IRON_AXE, Items.GOLDEN_AXE),
        Map.entry(Items.GOLDEN_AXE, Items.DIAMOND_AXE),
        Map.entry(Items.DIAMOND_AXE, Items.NETHERITE_AXE),
        
        Map.entry(Items.WOODEN_SHOVEL, Items.STONE_SHOVEL),
        Map.entry(Items.STONE_SHOVEL, Items.IRON_SHOVEL),
        Map.entry(Items.IRON_SHOVEL, Items.GOLDEN_SHOVEL),
        Map.entry(Items.GOLDEN_SHOVEL, Items.DIAMOND_SHOVEL),
        Map.entry(Items.DIAMOND_SHOVEL, Items.NETHERITE_SHOVEL),
        
        Map.entry(Items.WOODEN_HOE, Items.STONE_HOE),
        Map.entry(Items.STONE_HOE, Items.IRON_HOE),
        Map.entry(Items.IRON_HOE, Items.GOLDEN_HOE),
        Map.entry(Items.GOLDEN_HOE, Items.DIAMOND_HOE),
        Map.entry(Items.DIAMOND_HOE, Items.NETHERITE_HOE),
        
        // Armor: leather → chainmail → iron → gold → diamond → netherite
        Map.entry(Items.LEATHER_HELMET, Items.CHAINMAIL_HELMET),
        Map.entry(Items.CHAINMAIL_HELMET, Items.IRON_HELMET),
        Map.entry(Items.IRON_HELMET, Items.GOLDEN_HELMET),
        Map.entry(Items.GOLDEN_HELMET, Items.DIAMOND_HELMET),
        Map.entry(Items.DIAMOND_HELMET, Items.NETHERITE_HELMET),
        
        Map.entry(Items.LEATHER_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE),
        Map.entry(Items.CHAINMAIL_CHESTPLATE, Items.IRON_CHESTPLATE),
        Map.entry(Items.IRON_CHESTPLATE, Items.GOLDEN_CHESTPLATE),
        Map.entry(Items.GOLDEN_CHESTPLATE, Items.DIAMOND_CHESTPLATE),
        Map.entry(Items.DIAMOND_CHESTPLATE, Items.NETHERITE_CHESTPLATE),
        
        Map.entry(Items.LEATHER_LEGGINGS, Items.CHAINMAIL_LEGGINGS),
        Map.entry(Items.CHAINMAIL_LEGGINGS, Items.IRON_LEGGINGS),
        Map.entry(Items.IRON_LEGGINGS, Items.GOLDEN_LEGGINGS),
        Map.entry(Items.GOLDEN_LEGGINGS, Items.DIAMOND_LEGGINGS),
        Map.entry(Items.DIAMOND_LEGGINGS, Items.NETHERITE_LEGGINGS),
        
        Map.entry(Items.LEATHER_BOOTS, Items.CHAINMAIL_BOOTS),
        Map.entry(Items.CHAINMAIL_BOOTS, Items.IRON_BOOTS),
        Map.entry(Items.IRON_BOOTS, Items.GOLDEN_BOOTS),
        Map.entry(Items.GOLDEN_BOOTS, Items.DIAMOND_BOOTS),
        Map.entry(Items.DIAMOND_BOOTS, Items.NETHERITE_BOOTS)
    );

    private static final Map<Item, Item> DOWNGRADE_MAP = Map.ofEntries(
        // Tools: netherite → diamond → gold → iron → stone → wooden
        Map.entry(Items.NETHERITE_SWORD, Items.DIAMOND_SWORD),
        Map.entry(Items.DIAMOND_SWORD, Items.GOLDEN_SWORD),
        Map.entry(Items.GOLDEN_SWORD, Items.IRON_SWORD),
        Map.entry(Items.IRON_SWORD, Items.STONE_SWORD),
        Map.entry(Items.STONE_SWORD, Items.WOODEN_SWORD),
        
        Map.entry(Items.NETHERITE_PICKAXE, Items.DIAMOND_PICKAXE),
        Map.entry(Items.DIAMOND_PICKAXE, Items.GOLDEN_PICKAXE),
        Map.entry(Items.GOLDEN_PICKAXE, Items.IRON_PICKAXE),
        Map.entry(Items.IRON_PICKAXE, Items.STONE_PICKAXE),
        Map.entry(Items.STONE_PICKAXE, Items.WOODEN_PICKAXE),
        
        Map.entry(Items.NETHERITE_AXE, Items.DIAMOND_AXE),
        Map.entry(Items.DIAMOND_AXE, Items.GOLDEN_AXE),
        Map.entry(Items.GOLDEN_AXE, Items.IRON_AXE),
        Map.entry(Items.IRON_AXE, Items.STONE_AXE),
        Map.entry(Items.STONE_AXE, Items.WOODEN_AXE),
        
        Map.entry(Items.NETHERITE_SHOVEL, Items.DIAMOND_SHOVEL),
        Map.entry(Items.DIAMOND_SHOVEL, Items.GOLDEN_SHOVEL),
        Map.entry(Items.GOLDEN_SHOVEL, Items.IRON_SHOVEL),
        Map.entry(Items.IRON_SHOVEL, Items.STONE_SHOVEL),
        Map.entry(Items.STONE_SHOVEL, Items.WOODEN_SHOVEL),
        
        Map.entry(Items.NETHERITE_HOE, Items.DIAMOND_HOE),
        Map.entry(Items.DIAMOND_HOE, Items.GOLDEN_HOE),
        Map.entry(Items.GOLDEN_HOE, Items.IRON_HOE),
        Map.entry(Items.IRON_HOE, Items.STONE_HOE),
        Map.entry(Items.STONE_HOE, Items.WOODEN_HOE),
        
        // Armor: netherite → diamond → gold → iron → chainmail → leather
        Map.entry(Items.NETHERITE_HELMET, Items.DIAMOND_HELMET),
        Map.entry(Items.DIAMOND_HELMET, Items.GOLDEN_HELMET),
        Map.entry(Items.GOLDEN_HELMET, Items.IRON_HELMET),
        Map.entry(Items.IRON_HELMET, Items.CHAINMAIL_HELMET),
        Map.entry(Items.CHAINMAIL_HELMET, Items.LEATHER_HELMET),
        
        Map.entry(Items.NETHERITE_CHESTPLATE, Items.DIAMOND_CHESTPLATE),
        Map.entry(Items.DIAMOND_CHESTPLATE, Items.GOLDEN_CHESTPLATE),
        Map.entry(Items.GOLDEN_CHESTPLATE, Items.IRON_CHESTPLATE),
        Map.entry(Items.IRON_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE),
        Map.entry(Items.CHAINMAIL_CHESTPLATE, Items.LEATHER_CHESTPLATE),
        
        Map.entry(Items.NETHERITE_LEGGINGS, Items.DIAMOND_LEGGINGS),
        Map.entry(Items.DIAMOND_LEGGINGS, Items.GOLDEN_LEGGINGS),
        Map.entry(Items.GOLDEN_LEGGINGS, Items.IRON_LEGGINGS),
        Map.entry(Items.IRON_LEGGINGS, Items.CHAINMAIL_LEGGINGS),
        Map.entry(Items.CHAINMAIL_LEGGINGS, Items.LEATHER_LEGGINGS),
        
        Map.entry(Items.NETHERITE_BOOTS, Items.DIAMOND_BOOTS),
        Map.entry(Items.DIAMOND_BOOTS, Items.GOLDEN_BOOTS),
        Map.entry(Items.GOLDEN_BOOTS, Items.IRON_BOOTS),
        Map.entry(Items.IRON_BOOTS, Items.CHAINMAIL_BOOTS),
        Map.entry(Items.CHAINMAIL_BOOTS, Items.LEATHER_BOOTS)
    );

    private static final Set<Item> DIAMOND_ITEMS = Set.of(
        Items.DIAMOND,
        Items.DIAMOND_SWORD,
        Items.DIAMOND_PICKAXE,
        Items.DIAMOND_AXE,
        Items.DIAMOND_SHOVEL,
        Items.DIAMOND_HOE,
        Items.DIAMOND_HELMET,
        Items.DIAMOND_CHESTPLATE,
        Items.DIAMOND_LEGGINGS,
        Items.DIAMOND_BOOTS,
        Items.DIAMOND_HORSE_ARMOR,
        Items.NETHERITE_INGOT,
        Items.NETHERITE_SCRAP,
        Items.NETHERITE_SWORD,
        Items.NETHERITE_PICKAXE,
        Items.NETHERITE_AXE,
        Items.NETHERITE_SHOVEL,
        Items.NETHERITE_HOE,
        Items.NETHERITE_HELMET,
        Items.NETHERITE_CHESTPLATE,
        Items.NETHERITE_LEGGINGS,
        Items.NETHERITE_BOOTS
    );

    private static final List<Item> GOOD_LOOT = List.of(
        Items.DIAMOND,
        Items.EMERALD,
        Items.GOLDEN_APPLE,
        Items.ENCHANTED_GOLDEN_APPLE,
        Items.NETHERITE_INGOT,
        Items.TOTEM_OF_UNDYING,
        Items.EXPERIENCE_BOTTLE,
        Items.ENDER_PEARL,
        Items.BLAZE_ROD
    );

    private DiceEffects() {
    }

    public static void registerDefaults() {
        DiceEffectRegistry.register(DiceType.WHITE, EffectAlignment.GOOD,
            new SimpleDiceEffect("white_full_health", (player, random) -> {
                player.setHealth(player.getMaxHealth());
            })
        );

        DiceEffectRegistry.register(DiceType.WHITE, EffectAlignment.GOOD,
            new SimpleDiceEffect("white_full_hunger", (player, random) -> {
                player.getHungerManager().setFoodLevel(20);
                player.getHungerManager().setSaturationLevel(20.0f);
            })
        );

        DiceEffectRegistry.register(DiceType.WHITE, EffectAlignment.GOOD,
            new SimpleDiceEffect("white_upgrade_gear", (player, random) -> {
                replaceItems(player, UPGRADE_MAP);
            })
        );

        DiceEffectRegistry.register(DiceType.WHITE, EffectAlignment.BAD,
            new SimpleDiceEffect("white_half_heart", (player, random) -> {
                player.setHealth(Math.min(1.0f, player.getMaxHealth()));
            })
        );

        DiceEffectRegistry.register(DiceType.WHITE, EffectAlignment.BAD,
            new SimpleDiceEffect("white_empty_hunger", (player, random) -> {
                player.getHungerManager().setFoodLevel(0);
                player.getHungerManager().setSaturationLevel(0.0f);
            })
        );

        DiceEffectRegistry.register(DiceType.WHITE, EffectAlignment.BAD,
            new SimpleDiceEffect("white_downgrade_gear", (player, random) -> {
                replaceItems(player, DOWNGRADE_MAP);
            })
        );

        DiceEffectRegistry.register(DiceType.BLACK, EffectAlignment.BAD,
            new SimpleDiceEffect("black_remove_diamonds", (player, random) -> {
                removeItems(player, DIAMOND_ITEMS);
            })
        );

        DiceEffectRegistry.register(DiceType.BLACK, EffectAlignment.BAD,
            new SimpleDiceEffect("black_stash_and_launch", DiceEffects::stashAndLaunch)
        );

        DiceEffectRegistry.register(DiceType.BLACK, EffectAlignment.GOOD,
            new SimpleDiceEffect("black_fill_stacks", (player, random) -> {
                fillStacks(player);
            })
        );

        DiceEffectRegistry.register(DiceType.BLACK, EffectAlignment.GOOD,
            new SimpleDiceEffect("black_good_loot_chest", DiceEffects::spawnLootChest)
        );
    }

    private static void replaceItems(ServerPlayerEntity player, Map<Item, Item> replacements) {
        replaceStacks(player.getInventory().main, replacements);
        replaceStacks(player.getInventory().armor, replacements);
        replaceStacks(player.getInventory().offHand, replacements);
    }

    private static void replaceStacks(DefaultedList<ItemStack> list, Map<Item, Item> replacements) {
        for (int i = 0; i < list.size(); i++) {
            ItemStack stack = list.get(i);
            if (stack.isEmpty()) {
                continue;
            }

            Item replacement = replacements.get(stack.getItem());
            if (replacement != null) {
                list.set(i, copyWithItem(stack, replacement));
            }
        }
    }

    private static ItemStack copyWithItem(ItemStack original, Item replacement) {
        ItemStack newStack = new ItemStack(replacement, original.getCount());
        if (original.getNbt() != null) {
            newStack.setNbt(original.getNbt().copy());
        }
        return newStack;
    }

    private static void removeItems(ServerPlayerEntity player, Set<Item> items) {
        clearItems(player.getInventory().main, items);
        clearItems(player.getInventory().armor, items);
        clearItems(player.getInventory().offHand, items);
    }

    private static void clearItems(DefaultedList<ItemStack> list, Set<Item> items) {
        for (int i = 0; i < list.size(); i++) {
            ItemStack stack = list.get(i);
            if (!stack.isEmpty() && items.contains(stack.getItem())) {
                list.set(i, ItemStack.EMPTY);
            }
        }
    }

    private static void fillStacks(ServerPlayerEntity player) {
        fillStacks(player.getInventory().main);
        fillStacks(player.getInventory().offHand);
    }

    private static void fillStacks(DefaultedList<ItemStack> list) {
        for (int i = 0; i < list.size(); i++) {
            ItemStack stack = list.get(i);
            if (stack.isEmpty() || !stack.isStackable()) {
                continue;
            }
            int max = stack.getMaxCount();
            if (stack.getCount() < max) {
                stack.setCount(max);
            }
        }
    }

    private static void stashAndLaunch(ServerPlayerEntity player, Random random) {
        ServerWorld world = player.getServerWorld();
        BlockPos chestPos = findChestPos(world, player.getBlockPos(), player.getHorizontalFacing());
        if (chestPos == null) {
            player.sendMessage(Text.literal("No space for the stash chest."), true);
            return;
        }

        world.setBlockState(chestPos, Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, player.getHorizontalFacing().getOpposite()), 3);
        BlockEntity blockEntity = world.getBlockEntity(chestPos);
        if (!(blockEntity instanceof ChestBlockEntity chest)) {
            player.sendMessage(Text.literal("The stash chest failed to appear."), true);
            return;
        }

        moveInventoryToChest(player, chest, world, chestPos);
        clearInventory(player);
        giveWaterBucket(player);
        teleportHigh(player, world);
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 20 * 15, 0));
    }

    private static void spawnLootChest(ServerPlayerEntity player, Random random) {
        ServerWorld world = player.getServerWorld();
        BlockPos chestPos = findChestPos(world, player.getBlockPos(), player.getHorizontalFacing());
        if (chestPos == null) {
            player.sendMessage(Text.literal("No space for the loot chest."), true);
            return;
        }

        world.setBlockState(chestPos, Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, player.getHorizontalFacing().getOpposite()), 3);
        BlockEntity blockEntity = world.getBlockEntity(chestPos);
        if (!(blockEntity instanceof ChestBlockEntity chest)) {
            player.sendMessage(Text.literal("The loot chest failed to appear."), true);
            return;
        }

        int rolls = 6 + random.nextInt(5);
        for (int i = 0; i < rolls; i++) {
            Item item = GOOD_LOOT.get(random.nextInt(GOOD_LOOT.size()));
            int max = Math.min(item.getMaxCount(), 16);
            int count = 1 + random.nextInt(Math.max(1, max));
            addToInventoryOrDrop(chest, new ItemStack(item, count), world, chestPos);
        }
    }

    private static BlockPos findChestPos(ServerWorld world, BlockPos origin, Direction facing) {
        Direction[] directions = new Direction[] {
            facing,
            facing.rotateYClockwise(),
            facing.rotateYCounterclockwise(),
            facing.getOpposite()
        };

        for (Direction direction : directions) {
            BlockPos pos = origin.offset(direction);
            if (world.getBlockState(pos).isAir() && world.getBlockState(pos.down()).isSolidBlock(world, pos.down())) {
                return pos;
            }
        }

        BlockPos above = origin.up();
        if (world.getBlockState(above).isAir()) {
            return above;
        }

        return null;
    }

    private static void moveInventoryToChest(ServerPlayerEntity player, Inventory chest, ServerWorld world, BlockPos chestPos) {
        moveListToChest(player.getInventory().main, chest, world, chestPos);
        moveListToChest(player.getInventory().armor, chest, world, chestPos);
        moveListToChest(player.getInventory().offHand, chest, world, chestPos);
    }

    private static void moveListToChest(DefaultedList<ItemStack> list, Inventory chest, ServerWorld world, BlockPos chestPos) {
        for (int i = 0; i < list.size(); i++) {
            ItemStack stack = list.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            list.set(i, ItemStack.EMPTY);
            addToInventoryOrDrop(chest, stack, world, chestPos);
        }
    }

    private static void addToInventoryOrDrop(Inventory chest, ItemStack stack, ServerWorld world, BlockPos chestPos) {
        for (int slot = 0; slot < chest.size(); slot++) {
            ItemStack existing = chest.getStack(slot);
            if (existing.isEmpty()) {
                chest.setStack(slot, stack);
                return;
            }
            if (ItemStack.canCombine(existing, stack) && existing.getCount() < existing.getMaxCount()) {
                int move = Math.min(stack.getCount(), existing.getMaxCount() - existing.getCount());
                existing.increment(move);
                stack.decrement(move);
                if (stack.isEmpty()) {
                    return;
                }
            }
        }

        ItemScatterer.spawn(world, chestPos.getX(), chestPos.getY(), chestPos.getZ(), stack);
    }

    private static void clearInventory(ServerPlayerEntity player) {
        clearList(player.getInventory().main);
        clearList(player.getInventory().armor);
        clearList(player.getInventory().offHand);
    }

    private static void clearList(DefaultedList<ItemStack> list) {
        for (int i = 0; i < list.size(); i++) {
            list.set(i, ItemStack.EMPTY);
        }
    }

    private static void giveWaterBucket(ServerPlayerEntity player) {
        int slot = player.getInventory().selectedSlot;
        player.getInventory().setStack(slot, new ItemStack(Items.WATER_BUCKET));
    }

    private static void teleportHigh(ServerPlayerEntity player, ServerWorld world) {
        double targetY = Math.min(world.getTopY() - 1, player.getY() + 120.0);
        player.teleport(world, player.getX(), targetY, player.getZ(), player.getYaw(), player.getPitch());
    }
}
