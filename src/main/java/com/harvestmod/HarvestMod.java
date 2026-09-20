package com.harvestmod;

import com.harvestmod.block.ModBlocks;
import com.harvestmod.block.entity.ModBlockEntities;
import com.harvestmod.entity.ModEntities;
import com.harvestmod.entity.custom.CropThiefEntity;
import com.harvestmod.item.ModItemGroups;
import com.harvestmod.item.ModItems;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.minecraft.item.ItemGroups;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.harvestmod.entity.ModEntities.CROP_THIEF;

public class HarvestMod implements ModInitializer {
	public static final String MOD_ID = "harvestmod";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ModItemGroups.registerItemGroups();

		ModBlocks.initialize();
		ModBlockEntities.initialize();

		ModItems.registerModItems();

		ModEntities.registerModEntities();

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(
				entries -> {
					entries.add(ModBlocks.HARVESTER_BLOCK);
					entries.add(ModBlocks.PLANTER_BLOCK);
				}
		);

		FabricDefaultAttributeRegistry.register(CROP_THIEF, CropThiefEntity.createAttributes());

		CompostingChanceRegistry.INSTANCE.add(ModItems.TOMATO, 0.5f);
		CompostingChanceRegistry.INSTANCE.add(ModItems.TOMATO_SEEDS, 0.25f);

		CompostingChanceRegistry.INSTANCE.add(ModItems.GARLIC, 0.5f);
		CompostingChanceRegistry.INSTANCE.add(ModItems.GARLIC_SPROUT, 0.25f);
	}


	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
