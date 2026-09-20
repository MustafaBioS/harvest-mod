package com.harvestmod.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.BlockItem;
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

public class PlanterBlockEntity extends BlockEntity {

    private int plantsBeforeRepair = 0;
    private int currentMaxCooldown = 100;
    private int currentCooldown = currentMaxCooldown;
    private int seedsHeld = 0;
    private int currentMaxSeeds = 32;
    private Item seedItem = null;


    private static final int ABSOLUTE_MAX_SEEDS = 256;
    private static final int MIN_COOLDOWN = 3;
    private static final int PLANTER_RANGE = 12;

    private final static Map<Item, Integer> COOLDOWN_REDUCERS = Map.of(
            Items.GOLD_INGOT, 5,
            Items.GOLD_NUGGET, 1,
            Items.GOLD_BLOCK, 15,
            Items.IRON_BLOCK, 10,
            Items.IRON_INGOT, 3,
            Items.IRON_NUGGET, 0,
            Items.DIAMOND, 15
    );

    private final static Map<Item, Integer> PLANTER_REPAIRERS = Map.of(
            Items.WOODEN_HOE, 200,
            Items.STONE_HOE, 800,
            Items.GOLDEN_HOE, 1600,
            Items.IRON_HOE, 3200,
            Items.DIAMOND_HOE, 6000,
            Items.NETHERITE_HOE, 9000
    );

    public boolean canInteract (ItemStack stack) {
        Item stackItem = stack.getItem();
        if (stackItem == Items.EMERALD) return true;
        if (getCropBlock(stack.getItem()) != null) return true;
        if (PLANTER_REPAIRERS.containsKey(stackItem)) return true;
        if (COOLDOWN_REDUCERS.containsKey(stackItem)) return true;

        return false;
    }

    public PlanterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PLANTER, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putInt("plantsBeforeRepair", plantsBeforeRepair);
        nbt.putInt("seedsHeld", seedsHeld);
        nbt.putInt("currentMaxSeeds", currentMaxSeeds);
        nbt.putInt("currentMaxCooldown", currentMaxCooldown);
        nbt.putString("seedItem", seedItem == null ? "" : Registries.ITEM.getId(seedItem).toString());
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        plantsBeforeRepair = nbt.getInt("plantsBeforeRepair");
        seedsHeld = nbt.getInt("seedsHeld");
        currentMaxSeeds = nbt.getInt("currentMaxSeeds");
        currentMaxCooldown = nbt.getInt("currentMaxCooldown");

        String temp_id = nbt.getString("seedItem");
        Identifier seedId = temp_id.isEmpty() ? null : Identifier.tryParse(temp_id);
        seedItem = seedId == null ? null : Registries.ITEM.getOrEmpty(seedId).orElse(null);

        if (seedItem == null) seedsHeld = 0;
    }

    private void consumeDurability(int amount) {
        plantsBeforeRepair = Math.max(0, plantsBeforeRepair - amount);
        markDirty();
    }

    public boolean hasSeeds() {
        return seedsHeld > 0;
    }

    public int getSeedsHeld() {
        return seedsHeld;
    }

    private void consumeSeeds(int amount) {
        seedsHeld = Math.max(0, seedsHeld - amount);
        if (seedsHeld == 0) voidSeedItem();
        markDirty();
    }

    public void voidSeeds() {
        seedsHeld = 0;
        seedItem = null;
        markDirty();
    }

    private void resetCooldown() {
        currentCooldown = currentMaxCooldown;
    }

    private boolean canPlant() {
        return seedsHeld > 0 && plantsBeforeRepair > 0;
    }

    private void setPlantsBeforeRepair(int amount) {
        plantsBeforeRepair = Math.max(0, amount);
        markDirty();
    }

    public boolean needsRepair() {
        return plantsBeforeRepair <= 0;
    }

    public ItemStack repairPlanter(ItemStack stack) {
        if (stack.isEmpty()) return stack;
        if (!PLANTER_REPAIRERS.containsKey(stack.getItem())) return stack;
        if (!needsRepair()) return stack;
        Item item = stack.getItem();
        setPlantsBeforeRepair(PLANTER_REPAIRERS.getOrDefault(item, 0));
        stack.decrement(1);
        return stack;
    }

    private boolean canConsumeOre(ItemStack stack) {
        return COOLDOWN_REDUCERS.containsKey(stack.getItem()) && currentMaxCooldown > MIN_COOLDOWN;
    }

    private void setCooldown(int amount) {
        currentMaxCooldown = Math.max(MIN_COOLDOWN, amount);
        markDirty();
    }

    public ItemStack consumeOre(ItemStack stack) {
        if (stack.isEmpty()) return stack;
        if (!canConsumeOre(stack)) return stack;
        int amount = COOLDOWN_REDUCERS.getOrDefault(stack.getItem(), 0);
        setCooldown(currentMaxCooldown - amount);
        stack.decrement(1);
        return stack;
    }

    private boolean isEmeraldOre (ItemStack stack) {
        return stack.getItem() == Items.EMERALD;
    }

    private boolean canConsumeEmeraldOre(ItemStack stack) {
        return isEmeraldOre(stack) && currentMaxSeeds < ABSOLUTE_MAX_SEEDS;
    }

    private void increaseMaxSeeds(int amount) {
        currentMaxSeeds = Math.min(ABSOLUTE_MAX_SEEDS, currentMaxSeeds + amount);
        markDirty();
    }

    public ItemStack consumeEmeraldOre(ItemStack stack) {
        if (stack.isEmpty()) return stack;
        if (!canConsumeEmeraldOre(stack)) return stack;
        increaseMaxSeeds(20);
        stack.decrement(1);
        return stack;
    }

    private int incrementSeedsHeld(int amount) {
        int deltaSeeds = Math.min(currentMaxSeeds - seedsHeld, amount);
        if (deltaSeeds <= 0) return amount;
        seedsHeld += deltaSeeds;
        markDirty();
        return amount - deltaSeeds;
    }

    private static CropBlock getCropBlock(Item seed) {
        if (seed instanceof BlockItem blockItem && blockItem.getBlock() instanceof CropBlock crop){
            return crop;
        }
        return null;
    }


    private void voidSeedItem() {
        seedItem = null;
        markDirty();
    }

    private void setSeedItem(Item item) {
        seedItem = item;
        markDirty();
    }

    public Item getSeedItem() {
        return seedItem;
    }

    public ItemStack addSeeds(ItemStack stack) {
        if (stack.isEmpty()) return stack;
        if (getCropBlock(stack.getItem()) == null) return stack;
        if (stack.getItem() != seedItem && seedItem != null) return stack;

        setSeedItem(stack.getItem());

        int newSeedCount = incrementSeedsHeld(stack.getCount());
        stack.setCount(newSeedCount);
        return stack;
    }


    public static void tick(World world, BlockPos pos, BlockState state, PlanterBlockEntity be) {

        if (be.currentCooldown > 0) {
            be.currentCooldown--;
            return;
        }
        if (world.isClient) return;
        if (!be.canPlant()) return;

        Iterable<BlockPos> possiblePlantPos = BlockPos.iterate(
                pos.add(-PLANTER_RANGE, 1, -PLANTER_RANGE),
                pos.add(PLANTER_RANGE, 1, PLANTER_RANGE)
        );

        be.resetCooldown();
        CropBlock crop = getCropBlock(be.seedItem);
        if (crop == null) return;
        BlockState newCropState = crop.getDefaultState();


        for (BlockPos plantPos : possiblePlantPos) {
            if (!world.isChunkLoaded(plantPos)) continue;
            if (!world.getBlockState(plantPos).isAir()) continue;

            if(!newCropState.canPlaceAt(world, plantPos)) continue;

            world.setBlockState(plantPos, newCropState, Block.NOTIFY_LISTENERS);
            be.consumeDurability(1);
            be.consumeSeeds(1);
            if (world instanceof ServerWorld serverWorld)
                serverWorld.spawnParticles(
                        ParticleTypes.HAPPY_VILLAGER,
                        plantPos.getX() + 0.5, plantPos.getY() + 0.5, plantPos.getZ() + 0.5,
                        6,
                        0.3, 0.3, 0.3,
                        0.0
                );


            if (be.needsRepair() && world instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(
                        ParticleTypes.ITEM_SLIME,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        20,
                        0.3, 0.3, 0.3,
                        0.05
                );
            }
            break;
        }
    }
}
