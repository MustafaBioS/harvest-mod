package com.harvestmod;

import com.harvestmod.block.ModBlocks;
import com.harvestmod.block.entity.ModBlockEntities;
import com.harvestmod.entity.ModEntities;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HarvestMod implements ModInitializer {
	public static final String MOD_ID = "harvestmod";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.initialize();
		ModBlockEntities.initialize();

		ModEntities.registerModEntities();

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> entries.add(ModBlocks.HARVESTER_BLOCK));
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
