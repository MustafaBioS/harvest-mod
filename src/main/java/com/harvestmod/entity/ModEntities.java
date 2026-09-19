package com.harvestmod.entity;

import com.harvestmod.HarvestMod;
import com.harvestmod.entity.custom.NullskullsEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<NullskullsEntity> NULLSKULLS = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(HarvestMod.MOD_ID, "nullskulls"),
            EntityType.Builder.create(NullskullsEntity::new, SpawnGroup.MONSTER)
                    .dimensions(1f, 2.5f).build());


    public static void registerModEntities() {
        HarvestMod.LOGGER.info("Registering Mod Entities for " + HarvestMod.MOD_ID);
    }
}
