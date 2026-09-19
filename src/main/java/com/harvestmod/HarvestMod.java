package com.harvestmod;

import com.harvestmod.entity.ModEntities;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HarvestMod implements ModInitializer {
	public static final String MOD_ID = "harvestmod";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ModEntities.registerModEntities();

	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
