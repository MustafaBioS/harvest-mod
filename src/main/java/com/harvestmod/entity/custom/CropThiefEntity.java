package com.harvestmod.entity.custom;

import com.harvestmod.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class CropThiefEntity extends ZombieEntity {

    private static final TrackedData<Integer> DATA_ID_TYPE_VARIANT =
            DataTracker.registerData(CropThiefEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final int CROP_SEARCH_RANGE = 8;

    private final SimpleInventory stolenCrops = new SimpleInventory(32);
    private static final int GARLIC_RANGE = 10;

    private boolean isRepelled() {
        return findNearestGarlic() != null;
    }

    private ItemEntity findNearestGarlic() {
        ItemEntity nearest = null;
        double best = Double.MAX_VALUE;
        for (ItemEntity g: this.getWorld().getEntitiesByClass(
                ItemEntity.class,
                this.getBoundingBox().expand(GARLIC_RANGE),
                item  -> item.getStack().isOf(ModItems.GARLIC))) {
            double d = this.squaredDistanceTo(g);
            if (d < best) { best = d; nearest = g; }
        }
        return nearest;
    }

    private class AvoidGarlicGoal extends Goal {
        private Vec3d fleeTarget;

        AvoidGarlicGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            ItemEntity garlic = findNearestGarlic();
            if (garlic == null) return false;

            fleeTarget = FuzzyTargeting.findFrom(CropThiefEntity.this, 16, 7, garlic.getPos());
            return fleeTarget != null;
        }

        @Override
        public void start() {
            CropThiefEntity.this.getNavigation().startMovingTo(fleeTarget.x, fleeTarget.y, fleeTarget.z, 1.4);
        }

        @Override
        public boolean shouldContinue() {
            return CropThiefEntity.this.getNavigation().isIdle();
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_CAT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_CAT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_CAT_DEATH;
    }

    @Override
    protected void initGoals() {
        super.initGoals();

        this.goalSelector.add(3, new AvoidGarlicGoal());
        this.goalSelector.add(4, new Goal() {

            private BlockPos cropPos;

            @Override
            public boolean canStart() {
                if (CropThiefEntity.this.isInventoryFull()) {
                    return false;
                }

                if (CropThiefEntity.this.getRandom().nextInt(10) != 0) {
                    return false;
                }

                if (CropThiefEntity.this.isRepelled()) return false;

                this.cropPos = findNearbyCrop();

                return this.cropPos != null;
            }

            @Override
            public void start() {
                if (this.cropPos != null) {
                    CropThiefEntity.this.getNavigation().startMovingTo(
                            this.cropPos.getX() + 0.5,
                            this.cropPos.getY(),
                            this.cropPos.getZ() + 0.5,
                            1.25
                    );
                }
            }

            @Override
            public boolean shouldContinue() {
                return this.cropPos != null
                        && !CropThiefEntity.this.getNavigation().isIdle();
            }

            @Override
            public void tick() {
                if (this.cropPos == null) {
                    return;
                }

                if (CropThiefEntity.this.squaredDistanceTo(
                        this.cropPos.getX() + 0.5,
                        this.cropPos.getY(),
                        this.cropPos.getZ()
                ) < 2.0) {
                    if (CropThiefEntity.this.getWorld() instanceof ServerWorld serverWorld) {
                        BlockState state = serverWorld.getBlockState(this.cropPos);

                        for (ItemStack drop : Block.getDroppedStacks(
                                state,
                                serverWorld,
                                this.cropPos,
                                null
                        )) {
                            CropThiefEntity.this.stolenCrops.addStack(drop);
                        }

                        CropThiefEntity.this.getWorld().breakBlock(this.cropPos, false);
                        this.cropPos = null;
                    }
                }
            }

            private BlockPos findNearbyCrop() {
                BlockPos origin = CropThiefEntity.this.getBlockPos();
                for (int x = -CROP_SEARCH_RANGE; x <= CROP_SEARCH_RANGE; x++) {
                    for (int y = -2; y <= 2; y++) {
                        for (int z = -CROP_SEARCH_RANGE; z <= CROP_SEARCH_RANGE; z++) {
                            BlockPos pos = origin.add(x, y, z);
                            if (origin.getSquaredDistance(pos) > CROP_SEARCH_RANGE * CROP_SEARCH_RANGE) {
                                continue;
                            }
                            BlockState state = CropThiefEntity.this.getWorld().getBlockState(pos);

                            if (state.getBlock() instanceof CropBlock crop
                                    && crop.isMature(state)) {
                                return pos;
                            }
                        }
                    }
                }
                return null;
            }
        });
    }

    private boolean isInventoryFull() {
        for (int i = 0; i < stolenCrops.size(); i++) {
            if (stolenCrops.getStack(i).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return ZombieEntity.createZombieAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 25);
    }

    @Override
    protected boolean burnsInDaylight() { return false; }

    @Override
    protected void dropLoot(DamageSource source, boolean causedByPlayer) {
        super.dropLoot(source, causedByPlayer);

        for (int i = 0; i < stolenCrops.size(); i++) {
            ItemStack stack = stolenCrops.getStack(i);

            if (!stack.isEmpty()) {
                this.dropStack(stack.copy());
                stolenCrops.setStack(i, ItemStack.EMPTY);
            }
        }
    }

    public CropThiefEntity(EntityType<? extends CropThiefEntity> entityType, World world) {
        super(entityType, world);
    }


    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(DATA_ID_TYPE_VARIANT, 0);
    }

    public CropThiefVariant getVariant() {
        return CropThiefVariant.byId(this.getTypeVariant() & 255);
    }

    @Override
    public Text getName() {
        if (this.hasCustomName()) {
            return super.getName();
        }

        return Text.translatable("entity.harvestmod.crop_thief." + this.getVariant().name().toLowerCase());
    }

    private int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }

    private void setVariant(CropThiefVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.getTypeVariant());
        nbt.put("StolenCrops", stolenCrops.toNbtList(this.getWorld().getRegistryManager()));
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, nbt.getInt("Variant"));
        stolenCrops.readNbtList(
                nbt.getList("StolenCrops", NbtElement.COMPOUND_TYPE),
                this.getWorld().getRegistryManager()
        );
    }

    @Override
    public @Nullable EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {

        CropThiefVariant variant = Util.getRandom(CropThiefVariant.values(), this.random);
        setVariant(variant);

        return super.initialize(world, difficulty, spawnReason, entityData);
    }
}
