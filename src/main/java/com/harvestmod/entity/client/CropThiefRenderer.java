package com.harvestmod.entity.client;

import com.google.common.collect.Maps;
import com.harvestmod.HarvestMod;
import com.harvestmod.entity.custom.CropThiefVariant;
import com.harvestmod.entity.custom.CropThiefEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ZombieEntityRenderer;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.Map;

public class CropThiefRenderer extends ZombieEntityRenderer {

    private static Map<CropThiefVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(CropThiefVariant.class), map -> {
                map.put(CropThiefVariant.NULLSKULLS, Identifier.of(HarvestMod.MOD_ID, "textures/entity/nullskulls/nullskulls.png"));
                map.put(CropThiefVariant.MUSTAFA, Identifier.of(HarvestMod.MOD_ID, "textures/entity/mustafa/mustafa.png"));
            });

    public CropThiefRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(ZombieEntity entity) {
        return LOCATION_BY_VARIANT.get(((CropThiefEntity) entity).getVariant());
    }

}
