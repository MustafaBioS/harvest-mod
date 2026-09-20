package com.harvestmod.item;

import com.harvestmod.HarvestMod;
import com.harvestmod.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {

    public static final ItemGroup HARVESTING = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(HarvestMod.MOD_ID, "harvesting"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.TOMATO))
                    .displayName(Text.translatable("itemgroup.harvestmod.harvesting"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.TOMATO);
                        entries.add(ModItems.TOMATO_SEEDS);
                        entries.add(ModBlocks.HARVESTER_BLOCK);
                        entries.add(ModItems.NULLSKULLS_SPAWN_EGG);
                        entries.add(ModItems.MUSTAFA_SPAWN_EGG);
                    }).build());

    public static void registerItemGroups() {
        HarvestMod.LOGGER.info("Registering Item Groups For " + HarvestMod.MOD_ID);
    }
}
