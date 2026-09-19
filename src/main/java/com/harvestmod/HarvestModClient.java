package com.harvestmod;

import com.harvestmod.entity.ModEntities;
import com.harvestmod.entity.client.NullskullsRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class HarvestModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.NULLSKULLS, NullskullsRenderer::new);
    }
}
