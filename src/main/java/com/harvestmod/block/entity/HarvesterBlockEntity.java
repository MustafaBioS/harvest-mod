package com.harvestmod.block.entity;

import com.harvestmod.HarvestMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HarvesterBlockEntity extends BlockEntity {
    private int tick_count = 0;

    public HarvesterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HARVESTER, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, HarvesterBlockEntity be) {
        be.tick_count++;
        if (be.tick_count >= 100){
            be.tick_count = 0;
            HarvestMod.LOGGER.info("Harvester ticking at {} (is_client: {})", pos, world.isClient);
        }
    }

}
