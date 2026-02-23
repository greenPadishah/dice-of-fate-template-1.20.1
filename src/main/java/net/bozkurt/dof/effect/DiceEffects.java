package net.bozkurt.dof.effect;

import java.util.List;
import java.util.Map;
import java.util.Set;
import net.bozkurt.dof.logic.DiceEffectScheduler;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
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

        DiceEffectRegistry.register(DiceType.WHITE, EffectAlignment.GOOD,
            new SimpleDiceEffect("white_lucky_loot", (player, random) -> {
                int count = 5 + random.nextInt(11);
                List<Item> lootItems = List.of(
                    Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT,
                    Items.COOKED_BEEF, Items.COOKED_MUTTON, Items.BREAD,
                    Items.ARROW, Items.ARROW, Items.ARROW,
                    Items.TORCH, Items.TORCH, Items.OAK_LOG,
                    Items.COAL, Items.STICK, Items.COBBLESTONE,
                    Items.STRING, Items.FEATHER, Items.ACACIA_LOG
                );
                for (int i = 0; i < count; i++) {
                    Item item = lootItems.get(random.nextInt(lootItems.size()));
                    int stackSize = Math.min(item.getMaxCount(), 1 + random.nextInt(4));
                    player.getInventory().insertStack(new ItemStack(item, stackSize));
                }
            })
        );

        DiceEffectRegistry.register(DiceType.WHITE, EffectAlignment.GOOD,
            new SimpleDiceEffect("white_strength_boost", (player, random) -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 45 * 20, 0));
            })
        );

        DiceEffectRegistry.register(DiceType.WHITE, EffectAlignment.GOOD,
            new SimpleDiceEffect("white_speed_boost", (player, random) -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 30 * 20, 1));
            })
        );

        DiceEffectRegistry.register(DiceType.WHITE, EffectAlignment.GOOD,
            new SimpleDiceEffect("white_golden_apple", (player, random) -> {
                player.getInventory().insertStack(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 1));
            })
        );

        DiceEffectRegistry.register(DiceType.WHITE, EffectAlignment.GOOD,
            new SimpleDiceEffect("white_mob_ally", (player, random) -> {
                ServerWorld world = player.getServerWorld();
                for (int i = 0; i < 2; i++) {
                    WolfEntity wolf = new WolfEntity(EntityType.WOLF, world);
                    wolf.setPos(player.getX() + (i * 2) - 1, player.getY(), player.getZ());
                    wolf.setOwner(player);
                    wolf.setTamed(true);
                    world.spawnEntity(wolf);
                }
            })
        );

        DiceEffectRegistry.register(DiceType.WHITE, EffectAlignment.GOOD,
            new SimpleDiceEffect("white_ore_burst", DiceEffects::oreBlast)
        );

        DiceEffectRegistry.register(DiceType.WHITE, EffectAlignment.GOOD,
            new SimpleDiceEffect("white_mini_totem", (player, random) -> {
                player.getInventory().insertStack(new ItemStack(Items.TOTEM_OF_UNDYING, 1));
            })
        );

        DiceEffectRegistry.register(DiceType.WHITE, EffectAlignment.GOOD,
            new SimpleDiceEffect("white_exp_shower", (player, random) -> {
                int levels = 5 + random.nextInt(16);
                player.addExperience(levels * 7);
            })
        );

        DiceEffectRegistry.register(DiceType.WHITE, EffectAlignment.GOOD,
            new SimpleDiceEffect("white_light_blessing", DiceEffects::placeTorches)
        );

        DiceEffectRegistry.register(DiceType.WHITE, EffectAlignment.GOOD,
            new SimpleDiceEffect("white_repair_pulse", (player, random) -> {
                repairInventoryItems(player);
            })
        );

        // WHITE DICE - BAD EFFECTS (10 new)
        DiceEffectRegistry.register(DiceType.WHITE, EffectAlignment.BAD,
            new SimpleDiceEffect("white_mob_ambush", (player, random) -> {
                ServerWorld world = player.getServerWorld();
                int mobCount = 3 + random.nextInt(3);
                EntityType<?>[] mobTypes = {EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER, EntityType.SPIDER};
                for (int i = 0; i < mobCount; i++) {
                    EntityType<?> type = mobTypes[random.nextInt(mobTypes.length)];
                    if (type.create(world) instanceof HostileEntity mob) {
                        mob.setPos(player.getX() + random.nextInt(10) - 5, player.getY(), player.getZ() + random.nextInt(10) - 5);
                        world.spawnEntity(mob);
                    }
                }
            })
        );

        DiceEffectRegistry.register(DiceType.WHITE, EffectAlignment.BAD,
            new SimpleDiceEffect("white_hunger_curse", (player, random) -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 45 * 20, 2));
            })
        );

        DiceEffectRegistry.register(DiceType.WHITE, EffectAlignment.BAD,
            new SimpleDiceEffect("white_lightning_strike", (player, random) -> {
                ServerWorld world = player.getServerWorld();
                double radius = 8.0;
                double offsetX = (random.nextDouble() - 0.5) * radius * 2;
                double offsetZ = (random.nextDouble() - 0.5) * radius * 2;
                BlockPos strikePos = new BlockPos((int)(player.getX() + offsetX), (int)player.getY(), (int)(player.getZ() + offsetZ));
                world.setBlockState(strikePos, Blocks.FIRE.getDefaultState());
            })
        );

        DiceEffectRegistry.register(DiceType.WHITE, EffectAlignment.BAD,
            new SimpleDiceEffect("white_armor_shuffle", (player, random) -> {
                DefaultedList<ItemStack> armor = player.getInventory().armor;
                for (int i = 0; i < armor.size() - 1; i++) {
                    int randomIdx = random.nextInt(armor.size());
                    ItemStack temp = armor.get(i);
                    armor.set(i, armor.get(randomIdx));
                    armor.set(randomIdx, temp);
                }
            })
        );

        DiceEffectRegistry.register(DiceType.WHITE, EffectAlignment.BAD,
            new SimpleDiceEffect("white_hotbar_scramble", (player, random) -> {
                DefaultedList<ItemStack> main = player.getInventory().main;
                for (int i = 0; i < 9; i++) {
                    int randomIdx = random.nextInt(9);
                    ItemStack temp = main.get(i);
                    main.set(i, main.get(randomIdx));
                    main.set(randomIdx, temp);
                }
            })
        );

        DiceEffectRegistry.register(DiceType.WHITE, EffectAlignment.BAD,
            new SimpleDiceEffect("white_blindness", (player, random) -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 20 * 20, 0));
            })
        );

        DiceEffectRegistry.register(DiceType.WHITE, EffectAlignment.BAD,
            new SimpleDiceEffect("white_slowness", (player, random) -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 30 * 20, 1));
            })
        );

        DiceEffectRegistry.register(DiceType.WHITE, EffectAlignment.BAD,
            new SimpleDiceEffect("white_creeper_surprise", (player, random) -> {
                ServerWorld world = player.getServerWorld();
                CreeperEntity creeper = new CreeperEntity(EntityType.CREEPER, world);
                Direction behind = player.getHorizontalFacing().getOpposite();
                BlockPos spawnPos = player.getBlockPos().offset(behind, 3);
                creeper.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
                world.spawnEntity(creeper);
            })
        );

        DiceEffectRegistry.register(DiceType.WHITE, EffectAlignment.BAD,
            new SimpleDiceEffect("white_water_trap", (player, random) -> {
                ServerWorld world = player.getServerWorld();
                BlockPos pos = player.getBlockPos();
                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 2; y++) {
                        for (int z = -1; z <= 1; z++) {
                            BlockPos waterPos = pos.add(x, y, z);
                            if (world.getBlockState(waterPos).isAir()) {
                                world.setBlockState(waterPos, Blocks.WATER.getDefaultState());
                            }
                        }
                    }
                }
            })
        );

        DiceEffectRegistry.register(DiceType.WHITE, EffectAlignment.BAD,
            new SimpleDiceEffect("white_xp_drain", (player, random) -> {
                player.addExperience(-5 * 7);
            })
        );

        // BLACK DICE - GOOD EFFECTS (10 new)
        DiceEffectRegistry.register(DiceType.BLACK, EffectAlignment.GOOD,
            new SimpleDiceEffect("black_treasure_vault", DiceEffects::treasureVault)
        );

        DiceEffectRegistry.register(DiceType.BLACK, EffectAlignment.GOOD,
            new SimpleDiceEffect("black_diamond_rain", DiceEffects::diamondRain)
        );

        DiceEffectRegistry.register(DiceType.BLACK, EffectAlignment.GOOD,
            new SimpleDiceEffect("black_heros_blessing", DiceEffects::heroBlessing)
        );

        DiceEffectRegistry.register(DiceType.BLACK, EffectAlignment.GOOD,
            new SimpleDiceEffect("black_mob_army", DiceEffects::mobArmy)
        );

        DiceEffectRegistry.register(DiceType.BLACK, EffectAlignment.GOOD,
            new SimpleDiceEffect("black_ore_transmutation", DiceEffects::oreTransmutation)
        );

        DiceEffectRegistry.register(DiceType.BLACK, EffectAlignment.GOOD,
            new SimpleDiceEffect("black_time_freeze", DiceEffects::timeFreeze)
        );

        DiceEffectRegistry.register(DiceType.BLACK, EffectAlignment.GOOD,
            new SimpleDiceEffect("black_totem_surge", (player, random) -> {
                player.getInventory().insertStack(new ItemStack(Items.TOTEM_OF_UNDYING, 3));
            })
        );

        DiceEffectRegistry.register(DiceType.BLACK, EffectAlignment.GOOD,
            new SimpleDiceEffect("black_sky_platform", DiceEffects::skyPlatform)
        );

        DiceEffectRegistry.register(DiceType.BLACK, EffectAlignment.GOOD,
            new SimpleDiceEffect("black_fortune_touch", (player, random) -> {
                ItemStack held = player.getMainHandStack();
                if (!held.isEmpty()) {
                    held.addEnchantment(Enchantments.FORTUNE, 5);
                }
            })
        );

        DiceEffectRegistry.register(DiceType.BLACK, EffectAlignment.GOOD,
            new SimpleDiceEffect("black_nether_jackpot", DiceEffects::netherJackpot)
        );

        // BLACK DICE - BAD EFFECTS (10 new)
        DiceEffectRegistry.register(DiceType.BLACK, EffectAlignment.BAD,
            new SimpleDiceEffect("black_wither_gamble", DiceEffects::witherGamble)
        );

        DiceEffectRegistry.register(DiceType.BLACK, EffectAlignment.BAD,
            new SimpleDiceEffect("black_random_stop", DiceEffects::randomStop)
        );

        DiceEffectRegistry.register(DiceType.BLACK, EffectAlignment.BAD,
            new SimpleDiceEffect("black_mini_raid", DiceEffects::miniRaid)
        );

        DiceEffectRegistry.register(DiceType.BLACK, EffectAlignment.BAD,
            new SimpleDiceEffect("black_meteor_strike", DiceEffects::meteorStrike)
        );

        DiceEffectRegistry.register(DiceType.BLACK, EffectAlignment.BAD,
            new SimpleDiceEffect("black_nether_swap", DiceEffects::netherSwap)
        );

        DiceEffectRegistry.register(DiceType.BLACK, EffectAlignment.BAD,
            new SimpleDiceEffect("black_mob_evolution", DiceEffects::mobEvolution)
        );

        DiceEffectRegistry.register(DiceType.BLACK, EffectAlignment.BAD,
            new SimpleDiceEffect("black_inventory_wipe", (player, random) -> {
                DefaultedList<ItemStack> main = player.getInventory().main;
                int itemsToRemove = Math.min(5, main.size());
                for (int i = 0; i < itemsToRemove; i++) {
                    int randomIdx = random.nextInt(main.size());
                    main.set(randomIdx, ItemStack.EMPTY);
                }
            })
        );

        DiceEffectRegistry.register(DiceType.BLACK, EffectAlignment.BAD,
            new SimpleDiceEffect("black_curse_binding", (player, random) -> {
                DefaultedList<ItemStack> armor = player.getInventory().armor;
                int randomArmorIdx = random.nextInt(armor.size());
                ItemStack armorPiece = armor.get(randomArmorIdx);
                if (!armorPiece.isEmpty()) {
                    NbtCompound tag = armorPiece.getOrCreateNbt();
                    tag.putBoolean("Curse", true);
                }
            })
        );

        DiceEffectRegistry.register(DiceType.BLACK, EffectAlignment.BAD,
            new SimpleDiceEffect("black_grim_reaper", DiceEffects::grimReaper)
        );

        DiceEffectRegistry.register(DiceType.BLACK, EffectAlignment.BAD,
            new SimpleDiceEffect("black_dragon_glare", DiceEffects::dragonGlare)

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
        spawnLootChestAt(world, chestPos, player.getHorizontalFacing().getOpposite(), random);
    }

    private static void spawnLootChestAt(ServerWorld world, BlockPos chestPos, Direction facing, Random random) {
        world.setBlockState(chestPos, Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, facing), 3);
        BlockEntity blockEntity = world.getBlockEntity(chestPos);
        if (!(blockEntity instanceof ChestBlockEntity chest)) {
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

    // Helper methods for new effects
    private static void oreBlast(ServerPlayerEntity player, Random random) {
        ServerWorld world = player.getServerWorld();
        BlockPos pos = player.getBlockPos();
        int radius = 10;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = pos.add(x, y, z);
                    if (world.getBlockState(checkPos).isOf(Blocks.STONE)) {
                        if (random.nextInt(15) == 0) {
                            world.setBlockState(checkPos, Blocks.IRON_ORE.getDefaultState());
                        }
                    }
                }
            }
        }
    }

    private static void placeTorches(ServerPlayerEntity player, Random random) {
        ServerWorld world = player.getServerWorld();
        BlockPos pos = player.getBlockPos();
        int radius = 10;
        int placed = 0;
        int attempts = 0;
        while (placed < 24 && attempts < 200) {
            int x = pos.getX() + random.nextInt(radius * 2 + 1) - radius;
            int z = pos.getZ() + random.nextInt(radius * 2 + 1) - radius;
            BlockPos columnPos = new BlockPos(x, 0, z);
            BlockPos topPos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, columnPos);
            BlockPos torchPos = topPos.up();
            if (world.getBlockState(torchPos).isAir() && world.getBlockState(topPos).isSolidBlock(world, topPos)) {
                world.setBlockState(torchPos, Blocks.TORCH.getDefaultState());
                placed++;
            }
            attempts++;
        }
    }

    private static void treasureVault(ServerPlayerEntity player, Random random) {
        spawnLootChest(player, random);
        ServerWorld world = player.getServerWorld();
        BlockPos pos = player.getBlockPos();
        int mobCount = 3 + random.nextInt(3);
        EntityType<?>[] mobTypes = {EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER};
        for (int i = 0; i < mobCount; i++) {
            EntityType<?> type = mobTypes[random.nextInt(mobTypes.length)];
            if (type.create(world) instanceof HostileEntity mob) {
                mob.setPos(pos.getX() + random.nextInt(30) - 15, pos.getY() + 1, pos.getZ() + random.nextInt(30) - 15);
                world.spawnEntity(mob);
            }
        }
    }

    private static void diamondRain(ServerPlayerEntity player, Random random) {
        ServerWorld world = player.getServerWorld();
        BlockPos pos = player.getBlockPos();
        int diamondCount = 5 + random.nextInt(16);
        for (int i = 0; i < diamondCount; i++) {
            ItemStack diamond = new ItemStack(Items.DIAMOND);
            double offsetX = (random.nextDouble() - 0.5) * 30;
            double offsetZ = (random.nextDouble() - 0.5) * 30;
            ItemScatterer.spawn(world, pos.getX() + offsetX, pos.getY() + 50, pos.getZ() + offsetZ, diamond);
        }
    }

    private static void heroBlessing(ServerPlayerEntity player, Random random) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 60 * 20, 1));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 60 * 20, 1));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 60 * 20, 0));
    }

    private static void mobArmy(ServerPlayerEntity player, Random random) {
        ServerWorld world = player.getServerWorld();
        BlockPos pos = player.getBlockPos();
        for (int i = 0; i < 5; i++) {
            var golem = EntityType.IRON_GOLEM.create(world);
            if (golem != null) {
                golem.setPos(pos.getX() + random.nextInt(10) - 5, pos.getY() + 1, pos.getZ() + random.nextInt(10) - 5);
                world.spawnEntity(golem);
            }
        }
    }

    private static void oreTransmutation(ServerPlayerEntity player, Random random) {
        ServerWorld world = player.getServerWorld();
        BlockPos pos = player.getBlockPos();
        int radius = 15;
        int[] ores = {0, 0, 0, 1, 1, 2, 3};
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = pos.add(x, y, z);
                    if (world.getBlockState(checkPos).isOf(Blocks.STONE)) {
                        if (random.nextInt(20) == 0) {
                            int oreType = ores[random.nextInt(ores.length)];
                            switch (oreType) {
                                case 0 -> world.setBlockState(checkPos, Blocks.IRON_ORE.getDefaultState());
                                case 1 -> world.setBlockState(checkPos, Blocks.GOLD_ORE.getDefaultState());
                                case 2 -> world.setBlockState(checkPos, Blocks.DIAMOND_ORE.getDefaultState());
                                case 3 -> world.setBlockState(checkPos, Blocks.ANCIENT_DEBRIS.getDefaultState());
                            }
                        }
                    }
                }
            }
        }
    }

    private static void timeFreeze(ServerPlayerEntity player, Random random) {
        ServerWorld world = player.getServerWorld();
        BlockPos pos = player.getBlockPos();
        double px = player.getX();
        double py = player.getY();
        double pz = player.getZ();
        // Find all mobs in a 20-block radius and apply slowness
        var box = net.minecraft.util.math.Box.of(pos.toCenterPos(), 20, 20, 20);
        for (var entity : world.getNonSpectatingEntities(net.minecraft.entity.Entity.class, box)) {
            if (entity instanceof net.minecraft.entity.mob.MobEntity mob && entity != player) {
                mob.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 15 * 20, 6));
            }
        }
    }

    private static void skyPlatform(ServerPlayerEntity player, Random random) {
        ServerWorld world = player.getServerWorld();
        BlockPos pos = player.getBlockPos();
        double distance = random.nextInt(30) + 20;
        double angle = random.nextDouble() * Math.PI * 2;
        double offsetX = Math.cos(angle) * distance;
        double offsetZ = Math.sin(angle) * distance;
        double newX = pos.getX() + offsetX;
        double newZ = pos.getZ() + offsetZ;
        BlockPos platformPos = new BlockPos((int) newX, 200, (int) newZ);
        
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                world.setBlockState(platformPos.add(x, 0, z), Blocks.OAK_PLANKS.getDefaultState());
            }
        }
        
        player.teleport(world, newX, 202, newZ, player.getYaw(), player.getPitch());

        BlockPos chestPos = platformPos.add(0, 1, 0);
        if (world.getBlockState(chestPos).isAir()) {
            spawnLootChestAt(world, chestPos, Direction.SOUTH, random);
        }
    }

    private static void netherJackpot(ServerPlayerEntity player, Random random) {
        if (!player.getWorld().getDimensionKey().getValue().getPath().equals("overworld")) {
            return;
        }
        List<Item> netherLoot = List.of(
            Items.BLAZE_ROD, Items.NETHER_BRICK, Items.NETHERITE_SCRAP,
            Items.NETHERITE_INGOT, Items.MAGMA_CREAM, Items.GHAST_TEAR
        );
        Item item = netherLoot.get(random.nextInt(netherLoot.size()));
        int count = 1 + random.nextInt(3);
        player.getInventory().insertStack(new ItemStack(item, count));
        
        ServerWorld world = player.getServerWorld();
        var blaze = EntityType.BLAZE.create(world);
        if (blaze != null) {
            blaze.setPos(player.getX() + 3, player.getY(), player.getZ());
            world.spawnEntity(blaze);
        }
    }

    private static void witherGamble(ServerPlayerEntity player, Random random) {
        ServerWorld world = player.getServerWorld();
        BlockPos pos = player.getBlockPos();
        var wither = EntityType.WITHER.create(world);
        if (wither != null) {
            wither.setPos(pos.getX() + 15, pos.getY(), pos.getZ());
            wither.setHealth(50);
            world.spawnEntity(wither);
        }
    }

    private static void randomStop(ServerPlayerEntity player, Random random) {
        ServerWorld world = player.getServerWorld();
        BlockPos safePos = findSafeSurfacePosition(world, player.getBlockPos(), 500, random);
        if (safePos != null) {
            player.teleport(world, safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5, player.getYaw(), player.getPitch());
        }
    }

    private static void miniRaid(ServerPlayerEntity player, Random random) {
        ServerWorld world = player.getServerWorld();
        BlockPos pos = player.getBlockPos();
        EntityType<?>[] raidMobs = {EntityType.PILLAGER, EntityType.VINDICATOR, EntityType.EVOKER, EntityType.RAVAGER};
        int mobCount = 5 + random.nextInt(4);
        for (int i = 0; i < mobCount; i++) {
            EntityType<?> type = raidMobs[random.nextInt(raidMobs.length)];
            if (type.create(world) instanceof HostileEntity mob) {
                mob.setPos(pos.getX() + random.nextInt(20) - 10, pos.getY() + 1, pos.getZ() + random.nextInt(20) - 10);
                world.spawnEntity(mob);
            }
        }
    }

    private static void meteorStrike(ServerPlayerEntity player, Random random) {
        ServerWorld world = player.getServerWorld();
        BlockPos pos = player.getBlockPos();
        int radius = 15;
        int tntCount = 3 + random.nextInt(3);
        for (int i = 0; i < tntCount; i++) {
            BlockPos tntPos = pos.add(random.nextInt(radius * 2) - radius, random.nextInt(10) + 20, random.nextInt(radius * 2) - radius);
            var tnt = new net.minecraft.entity.TntEntity(world, tntPos.getX() + 0.5, tntPos.getY(), tntPos.getZ() + 0.5, null);
            tnt.setFuse(40);
            tnt.setVelocity(0.0, -0.4, 0.0);
            world.spawnEntity(tnt);
        }
    }

    private static void netherSwap(ServerPlayerEntity player, Random random) {
        ServerWorld world = player.getServerWorld();
        BlockPos center = player.getBlockPos();
        buildWaterCage(world, center);
        player.teleport(world, center.getX() + 0.5, center.getY() + 1, center.getZ() + 0.5, player.getYaw(), player.getPitch());
    }

    private static BlockPos findSafeSurfacePosition(ServerWorld world, BlockPos origin, int radius, Random random) {
        for (int attempts = 0; attempts < 50; attempts++) {
            int x = origin.getX() + random.nextInt(radius * 2 + 1) - radius;
            int z = origin.getZ() + random.nextInt(radius * 2 + 1) - radius;
            BlockPos topPos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z));
            BlockPos safe = findSafeColumnLocation(world, topPos);
            if (safe != null) {
                return safe;
            }
        }
        return null;
    }

    private static BlockPos findSafeColumnLocation(ServerWorld world, BlockPos topPos) {
        for (int i = 0; i < 12; i++) {
            BlockPos ground = topPos.down(i);
            BlockPos stand = ground.up();
            BlockPos head = stand.up();
            if (world.getBlockState(ground).isSolidBlock(world, ground)
                && world.getBlockState(stand).isAir()
                && world.getBlockState(head).isAir()) {
                return stand;
            }
        }
        return null;
    }

    private static void repairInventoryItems(ServerPlayerEntity player) {
        repairList(player.getInventory().main);
        repairList(player.getInventory().armor);
        repairList(player.getInventory().offHand);
    }

    private static void repairList(DefaultedList<ItemStack> list) {
        for (int i = 0; i < list.size(); i++) {
            ItemStack stack = list.get(i);
            if (stack.isEmpty() || !stack.isDamageable()) {
                continue;
            }
            int maxDamage = stack.getMaxDamage();
            int currentDamage = stack.getDamage();
            if (currentDamage <= 0) {
                continue;
            }
            int repairAmount = Math.max(1, maxDamage / 4);
            stack.setDamage(Math.max(0, currentDamage - repairAmount));
        }
    }

    private static void buildWaterCage(ServerWorld world, BlockPos center) {
        int radius = 2;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = center.add(x, y, z);
                    boolean isEdge = Math.abs(x) == radius || Math.abs(y) == radius || Math.abs(z) == radius;
                    if (!isEdge) {
                        world.setBlockState(pos, Blocks.WATER.getDefaultState());
                        continue;
                    }

                    boolean glassFace = (x == 0 && y == 0 && Math.abs(z) == radius)
                        || (x == 0 && z == 0 && Math.abs(y) == radius)
                        || (y == 0 && z == 0 && Math.abs(x) == radius);
                    if (glassFace) {
                        world.setBlockState(pos, Blocks.GLASS.getDefaultState());
                    } else {
                        world.setBlockState(pos, Blocks.STONE.getDefaultState());
                    }
                }
            }
        }
    }

    private static void mobEvolution(ServerPlayerEntity player, Random random) {
        ServerWorld world = player.getServerWorld();
        BlockPos pos = player.getBlockPos();
        // Find all mobs in a 30-block radius and buff them
        var box = net.minecraft.util.math.Box.of(pos.toCenterPos(), 30, 30, 30);
        for (var entity : world.getNonSpectatingEntities(net.minecraft.entity.Entity.class, box)) {
            if (entity instanceof net.minecraft.entity.mob.MobEntity mob && entity != player) {
                mob.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 60 * 20, 0));
                mob.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 60 * 20, 0));
            }
        }
    }

    private static void grimReaper(ServerPlayerEntity player, Random random) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 10 * 20, 2));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 10 * 20, 3));
        for (int i = 0; i < 10; i++) {
            int delay = i * 20;
            DiceEffectScheduler.schedule(player,
                new SimpleDiceEffect("grim_reaper_bell", (target, rng) -> {
                    target.getServerWorld().playSound(
                        null,
                        target.getBlockPos(),
                        SoundEvents.BLOCK_NOTE_BLOCK_BELL.value(),
                        SoundCategory.PLAYERS,
                        2.0f,
                        1.0f
                    );
                }),
                delay
            );
        }
    }

    private static void dragonGlare(ServerPlayerEntity player, Random random) {
        ServerWorld world = player.getServerWorld();
        BlockPos pos = player.getBlockPos();
        int phantomCount = 5 + random.nextInt(6);
        for (int i = 0; i < phantomCount; i++) {
            double angle = (Math.PI * 2 * i) / phantomCount;
            double distance = 20;
            double offsetX = Math.cos(angle) * distance;
            double offsetZ = Math.sin(angle) * distance;
            
            var phantom = EntityType.PHANTOM.create(world);
            if (phantom instanceof net.minecraft.entity.mob.PhantomEntity) {
                net.minecraft.entity.mob.PhantomEntity phantomEntity = (net.minecraft.entity.mob.PhantomEntity) phantom;
                phantomEntity.setPos(pos.getX() + offsetX, pos.getY() + 5, pos.getZ() + offsetZ);
                phantomEntity.setTarget(player);
                world.spawnEntity(phantomEntity);
            }
        }
    }
}

