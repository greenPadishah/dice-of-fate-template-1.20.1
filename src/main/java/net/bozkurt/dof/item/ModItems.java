package net.bozkurt.dof.item;

import net.bozkurt.dof.DiceOfFate;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    
    public static final Item DICE_OF_FATE = registerItem("dice_of_fate", new Item(new FabricItemSettings()));
    public static final Item DICE_OF_FATE_BLACK = registerItem("dice_of_fate_black", new Item(new FabricItemSettings()));
    
    
    private static Item registerItem (String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(DiceOfFate.MOD_ID, name), item);
    }
    
    public static void registerModItems() {
        DiceOfFate.LOGGER.info("Registering Mod Items for " + DiceOfFate.MOD_ID);

    }
}