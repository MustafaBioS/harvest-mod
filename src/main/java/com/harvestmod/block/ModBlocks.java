package com.harvestmod.block;

import com.harvestmod.HarvestMod;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;

public class ModBlocks {

    public static final Block HARVESTER_BLOCK = register(
            "harvester_block",
            new HarvesterBlock(AbstractBlock.Settings.create().strength(2.0f))
    );

    private static Block register(String name, Block block){
        Identifier id = HarvestMod.id(name);
        Registry.register(Registries.BLOCK, id, block);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()));
        return block;
    }

    public static void initialize(){

    }
}
