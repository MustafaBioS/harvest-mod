package com.harvestmod.block.entity;

import com.harvestmod.HarvestMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;

public class HarvesterBlockEntity extends BlockEntity {
    private int tickCount = 0;
    private Item heldHoe = null;
    private int swingsLeft = 0;
    private int currentCooldown = 0;


    private static final int LOOKUP_RANGE = 12;
    private static final Map<Item, Integer> HOE_COOLDOWNS = Map.of(
            Items.WOODEN_HOE, 20,
            Items.STONE_HOE, 15,
            Items.IRON_HOE, 10,
            Items.DIAMOND_HOE, 5,
            Items.NETHERITE_HOE, 2,
            Items.GOLDEN_HOE, 2
    );



    public HarvesterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HARVESTER, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putInt("swingsLeft", swingsLeft);
        nbt.putString("heldHoe", heldHoe == null ? "" : Registries.ITEM.getId(heldHoe).toString());
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        swingsLeft = nbt.getInt("swingsLeft");
        String temp_id = nbt.getString("heldHoe");
        heldHoe = temp_id.isEmpty() ? null : Registries.ITEM.get(Identifier.of(temp_id));
    }

    public int getSwingsLeft(){
        return swingsLeft;
    }

    public void consumeSwings(int swingsToConsume) {
        swingsLeft = Math.max(0, swingsLeft - swingsToConsume);
        markDirty();
    }

    public void setSwing(int swingsToSet) {
        swingsLeft = Math.max(0, swingsToSet);
        markDirty();
    }

    public void setHoe(ItemStack stack) {
        heldHoe = stack.getItem();
        setSwing((stack.getMaxDamage()-stack.getDamage())*3);
        markDirty();
    }

    public void voidHoe() {
        heldHoe = null;
        setSwing(0);
        markDirty();
    }

    public ItemStack getHoe() {
        if (heldHoe == null) return ItemStack.EMPTY;
        ItemStack stack = new ItemStack(heldHoe);
        int remaining = getSwingsLeft()/3;
        stack.setDamage(stack.getMaxDamage()-remaining);
        return stack;
    }

    public boolean canHarvest() {
        return getSwingsLeft() > 0 && heldHoe != null;
    }

    public boolean hasHoe() {
        return heldHoe != null;
    }

    public static boolean isValidHoe(ItemStack stack) {
        return HOE_COOLDOWNS.containsKey(stack.getItem());
    }

    public static void tick(World world, BlockPos pos, BlockState state, HarvesterBlockEntity be) {
        be.tickCount++;
        if (be.tickCount >= 100){
            be.tickCount = 0;
            HarvestMod.LOGGER.info("Harvester ticking at {} (is_client: {})", pos, world.isClient);
        }

        if (be.currentCooldown > 0) {
            be.currentCooldown--;
            return;
        }

        if (!be.canHarvest()) return;

        Iterable<BlockPos> possibleCrops = BlockPos.iterate(
                pos.add(-LOOKUP_RANGE, 0, -LOOKUP_RANGE),
                pos.add(LOOKUP_RANGE, 0, LOOKUP_RANGE)
        );

        for (BlockPos cropPos : possibleCrops) {
            if (!world.isChunkLoaded(cropPos)) continue;

            BlockState cropState = world.getBlockState(cropPos);

            if (cropState.getBlock() instanceof CropBlock crop && crop.isMature(cropState)) {

                Block.dropStacks(cropState, world, cropPos);
                world.removeBlock(cropPos, false);
                if (world instanceof ServerWorld serverWorld)
                    serverWorld.spawnParticles(
                            ParticleTypes.HAPPY_VILLAGER,
                            cropPos.getX() + 0.5, cropPos.getY() + 0.5, cropPos.getZ() + 0.5,
                            6,
                            0.3, 0.3, 0.3,
                            0.0
                    );

                be.consumeSwings(1);
                HarvestMod.LOGGER.info("Harvested crop at {}", cropPos);
                be.currentCooldown = HOE_COOLDOWNS.getOrDefault(be.heldHoe, 20);
                break;
            }
        }
    }

}
