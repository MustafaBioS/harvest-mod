package com.harvestmod.entity.client;

import com.google.common.collect.Maps;
import com.harvestmod.HarvestMod;
import com.harvestmod.entity.custom.MustafaEntity;
import com.harvestmod.entity.custom.NullskullsEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ZombieEntityRenderer;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.Map;

public class NullskullsRenderer extends ZombieEntityRenderer {

    private static Map<MustafaEntity, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(MustafaEntity.class), map -> {
                map.put(MustafaEntity.DEFAULT, Identifier.of(HarvestMod.MOD_ID, "textures/entity/nullskulls/nullskulls.png"));
                map.put(MustafaEntity.MUSTAFA, Identifier.of(HarvestMod.MOD_ID, "textures/entity/mustafa/mustafa.png"));
            });

    public NullskullsRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(ZombieEntity entity) {
        return LOCATION_BY_VARIANT.get(((NullskullsEntity) entity).getVariant());
    }

}
