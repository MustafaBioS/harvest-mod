package com.harvestmod.entity.client;

import com.harvestmod.HarvestMod;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ZombieEntityRenderer;
import net.minecraft.util.Identifier;

public class NullskullsRenderer extends ZombieEntityRenderer {
    public NullskullsRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(net.minecraft.entity.mob.ZombieEntity entity) {
        return Identifier.of(HarvestMod.MOD_ID, "textures/entity/nullskulls/nullskulls.png");
    }
}
