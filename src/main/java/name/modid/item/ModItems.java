package name.modid.item;

import name.modid.DiceOfFate;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public class ModItems {
    public static final Item FATE_OF_DICE = registerItem("fate_of_dice", new Item(new Item.Properties()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(DiceOfFate.MOD_ID, name), item);
    }

    public static void registerModItems() {
        DiceOfFate.LOGGER.info("Registering Mod Items for " + DiceOfFate.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(entries -> {
            entries.add(new ItemStack(FATE_OF_DICE));
        });
    }
}
