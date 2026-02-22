package net.bozkurt.dof;

import net.bozkurt.dof.item.ModItemGroups;
import net.bozkurt.dof.item.ModItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiceOfFate implements ModInitializer {
	public static final String MOD_ID = "dice-of-fate";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItemGroups.registerModItemGroups();
		ModItems.registerModItems();
	}
}