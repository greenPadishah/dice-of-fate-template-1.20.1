package net.bozkurt.dof.item;

import net.bozkurt.dof.DiceOfFate;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    
public static final ItemGroup DICE_OF_FATE_GROUP = Registry.register(Registries.ITEM_GROUP, 
            new Identifier(DiceOfFate.MOD_ID, "dice_of_fate"),
        FabricItemGroup.builder().displayName(Text.translatable("itemgroup.dice_of_fate"))
    .icon(() -> new ItemStack(ModItems.DICE_OF_FATE)).entries((displayContext, entries) -> {
        entries.add(ModItems.DICE_OF_FATE);
        entries.add(ModItems.DICE_OF_FATE_BLACK);


    }).build()); 


    public static void registerModItemGroups() {
        DiceOfFate.LOGGER.info("Registering Item Groups for " + DiceOfFate.MOD_ID);
    }
}