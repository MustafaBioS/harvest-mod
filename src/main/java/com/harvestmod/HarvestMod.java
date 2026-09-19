package com.harvestmod;

import com.harvestmod.block.ModBlocks;
import com.harvestmod.entity.ModEntities;
import com.harvestmod.entity.custom.NullskullsEntity;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.item.ItemGroups;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.harvestmod.entity.ModEntities.NULLSKULLS;

public class HarvestMod implements ModInitializer {
	public static final String MOD_ID = "harvestmod";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.initialize();

		ModEntities.registerModEntities();

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> entries.add(ModBlocks.HARVESTER_BLOCK));

		FabricDefaultAttributeRegistry.register(NULLSKULLS, NullskullsEntity.createAttributes());

	}


	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
