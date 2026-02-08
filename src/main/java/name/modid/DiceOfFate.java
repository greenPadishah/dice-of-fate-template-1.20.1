package name.modid;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import name.modid.item.ModItems;

public class DiceOfFate implements ModInitializer {
	public static final String MOD_ID = "dice-of-fate";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
	}
}