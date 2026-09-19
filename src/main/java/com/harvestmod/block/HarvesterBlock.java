package com.harvestmod.block;

import com.harvestmod.HarvestMod;
import com.harvestmod.block.entity.HarvesterBlockEntity;
import com.harvestmod.block.entity.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.mojang.serialization.MapCodec;

public class HarvesterBlock extends BlockWithEntity {

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ItemActionResult.SUCCESS;
        if (!(world.getBlockEntity(pos) instanceof HarvesterBlockEntity be)){
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (stack.isOf(Items.WHEAT_SEEDS)) {
            int leftOvers = be.addSeeds(stack.getCount());
            stack.setCount(leftOvers);
            HarvestMod.LOGGER.info("Added {} seeds to harvester at {}", stack.getCount(), pos);
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }


    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        if (!(world.getBlockEntity(pos) instanceof HarvesterBlockEntity be)) return ActionResult.PASS;

        int droppedCount = Math.min(64, be.getSeedsHeld());

        if (droppedCount <= 0) return ActionResult.PASS;

        be.consumeSeeds(droppedCount);
        player.getInventory().offerOrDrop(new ItemStack(Items.WHEAT_SEEDS, droppedCount));
        return ActionResult.SUCCESS;

    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())){
            if (world.getBlockEntity(pos) instanceof HarvesterBlockEntity be){
                int count = be.getSeedsHeld();
                while (count > 0) {
                    int toDrop = Math.min(count, Items.WHEAT_SEEDS.getMaxCount());
                    be.consumeSeeds(toDrop);
                    Block.dropStack(world, pos, new ItemStack(Items.WHEAT_SEEDS, toDrop));
                    count -= toDrop;
                }
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    public static final MapCodec<HarvesterBlock> CODEC = createCodec(HarvesterBlock::new);

    public HarvesterBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HarvesterBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) return null;
        return validateTicker(type, ModBlockEntities.HARVESTER, HarvesterBlockEntity::tick);
    }
}
