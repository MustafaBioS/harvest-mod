package com.harvestmod.block;

import com.harvestmod.block.entity.ModBlockEntities;
import com.harvestmod.block.entity.PlanterBlockEntity;
import com.mojang.serialization.MapCodec;
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

public class PlanterBlock extends BlockWithEntity {

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ItemActionResult.SUCCESS;
        if (!(world.getBlockEntity(pos) instanceof PlanterBlockEntity be)) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!be.canInteract(stack)) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        be.consumeEmeraldOre(stack);
        be.consumeOre(stack);
        be.addSeeds(stack);
        be.repairPlanter(stack);
        return ItemActionResult.SUCCESS;

    }


    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {

        if (world.isClient) return ActionResult.SUCCESS;
        if (!(world.getBlockEntity(pos) instanceof PlanterBlockEntity be)) return ActionResult.PASS;
        if (!player.getStackInHand(Hand.MAIN_HAND).isEmpty()) return ActionResult.PASS;

        int seedsHeld = be.getSeedsHeld();

        if (seedsHeld == 0) return ActionResult.PASS;

        while (seedsHeld > 0) {
            int seedsToDrop = Math.min(seedsHeld, 64);
            seedsHeld -= seedsToDrop;
            player.getInventory().offerOrDrop(new ItemStack(Items.WHEAT_SEEDS, seedsToDrop));
        }
        be.voidSeeds();

        return ActionResult.SUCCESS;

    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            if (world.getBlockEntity(pos) instanceof PlanterBlockEntity be && be.hasSeeds()) {
                int remainingSeeds = be.getSeedsHeld();
                int droppedSeeds = 0;
                while (remainingSeeds > 0) {
                    droppedSeeds = Math.min(remainingSeeds, 64);
                    remainingSeeds -= droppedSeeds;
                    Block.dropStack(world, pos, new ItemStack(Items.WHEAT_SEEDS, droppedSeeds));
                }
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    public static final MapCodec<PlanterBlock> CODEC = createCodec(PlanterBlock::new);

    public PlanterBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PlanterBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) return null;
        return validateTicker(type, ModBlockEntities.PLANTER, PlanterBlockEntity::tick);
    }
}
