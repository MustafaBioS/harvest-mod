package com.harvestmod.block;

import com.harvestmod.HarvestMod;
import com.harvestmod.block.entity.HarvesterBlockEntity;
import com.harvestmod.block.entity.ModBlockEntities;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
        HarvestMod.LOGGER.info("Right click at {} with {} (is_client: {})", pos, stack, world.isClient);
        return ItemActionResult.SUCCESS;
        //        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
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
