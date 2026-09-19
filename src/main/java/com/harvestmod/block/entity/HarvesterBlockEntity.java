package com.harvestmod.block.entity;

import com.harvestmod.HarvestMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HarvesterBlockEntity extends BlockEntity {
    private int tick_count = 0;

    private static final int LOOKUP_RANGE = 24;
    private static final int LOOKUP_Y_DEVIATION = 10;

    public HarvesterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HARVESTER, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, HarvesterBlockEntity be) {
        be.tick_count++;
        if (be.tick_count >= 100){
            be.tick_count = 0;
            HarvestMod.LOGGER.info("Harvester ticking at {} (is_client: {})", pos, world.isClient);
        }

        Iterable<BlockPos> possibleCrops = BlockPos.iterate(
                pos.add(-LOOKUP_RANGE, 0, -LOOKUP_RANGE),
                pos.add(LOOKUP_RANGE, 0, LOOKUP_RANGE)
        );

        for (BlockPos cropPos : possibleCrops) {
            if (!world.isChunkLoaded(cropPos)) continue;

            BlockState cropState = world.getBlockState(cropPos);

            if (cropState.getBlock() instanceof CropBlock crop && crop.isMature(cropState)) {
                Block.dropStacks(cropState, world, cropPos);
                world.setBlockState(cropPos, crop.withAge(0), Block.NOTIFY_LISTENERS);
                HarvestMod.LOGGER.info("Harvested crop at {}", cropPos);
            }
        }
    }

}
