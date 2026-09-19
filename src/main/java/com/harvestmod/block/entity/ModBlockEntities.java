package com.harvestmod.block.entity;

import com.harvestmod.HarvestMod;
import com.harvestmod.block.ModBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;


public class ModBlockEntities {
    public static final BlockEntityType<HarvesterBlockEntity> HARVESTER =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    HarvestMod.id("harvester"),
                    BlockEntityType.Builder.create(
                            HarvesterBlockEntity::new,
                            ModBlocks.HARVESTER_BLOCK
                    ).build()
            );

    public static void initialize(){

    }
}
