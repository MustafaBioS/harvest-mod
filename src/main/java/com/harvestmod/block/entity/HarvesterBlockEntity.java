package com.harvestmod.block.entity;

import com.harvestmod.HarvestMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HarvesterBlockEntity extends BlockEntity {
    private int tickCount = 0;
    private int seedsHeld = 0;

    private static final int LOOKUP_RANGE = 12;
    private static final int MAX_SEEDS_HELD = 256;

    public HarvesterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HARVESTER, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putInt("SeedsHeld", seedsHeld);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        seedsHeld = nbt.getInt("SeedsHeld");
    }

    public int addSeeds(int seedCount){
        int deltaSeeds = Math.clamp(seedsHeld+seedCount, 0, MAX_SEEDS_HELD) - seedsHeld;
        seedsHeld += deltaSeeds;
        markDirty();
        return seedCount-deltaSeeds;

    }

    public int getSeedsHeld(){
        return seedsHeld;
    }

    public void tick(World world, BlockPos pos, BlockState state, HarvesterBlockEntity be) {
        be.tickCount++;
        if (be.tickCount >= 100){
            be.tickCount = 0;
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
                world.removeBlock(cropPos, false);
                Block.dropStacks(cropState, world, cropPos);
                if (seedsHeld > 0) {
                    world.setBlockState(cropPos, crop.withAge(0), Block.NOTIFY_LISTENERS);
                    seedsHeld--;
                }
                HarvestMod.LOGGER.info("Harvested crop at {}", cropPos);
            }
        }
    }

}
