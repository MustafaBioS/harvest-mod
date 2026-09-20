package com.harvestmod.block;

import com.harvestmod.HarvestMod;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;

import java.util.List;

public class ModBlocks {

    public static final Block HARVESTER_BLOCK = register(
            "harvester_block",
            new HarvesterBlock(AbstractBlock.Settings.create().strength(2.0f)),
            new Item.Settings().component(
                    DataComponentTypes.LORE,
                    new LoreComponent(List.of(
                            Text.translatable("item.harvestmod.harvester_block.tooltip")
                    ))
            )
    );

    public static final Block PLANTER_BLOCK = register(
            "planter_block",
            new PlanterBlock(AbstractBlock.Settings.create().strength(2.0f)),
            new Item.Settings().component(
                    DataComponentTypes.LORE,
                    new LoreComponent(List.of(
                            Text.translatable("item.harvestmod.planter_block.tooltip")
                    ))
            )
    );

    public static final Block TOMATO_CROP = registerBlockWithoutItem("tomato_crop",
            new TomatoCropBlock(AbstractBlock.Settings.create().noCollision()
                    .ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP).pistonBehavior(PistonBehavior.DESTROY).mapColor(MapColor.GREEN)));

    public static final Block GARLIC_CROP = registerBlockWithoutItem("garlic_crop",
            new TomatoCropBlock(AbstractBlock.Settings.create().noCollision()
                    .ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP).pistonBehavior(PistonBehavior.DESTROY).mapColor(MapColor.GREEN)));

    public static Block registerBlockWithoutItem(String name, Block block) {
        return Registry.register(Registries.BLOCK, Identifier.of(HarvestMod.MOD_ID, name), block);
    }

    private static Block register(String name, Block block, Item.Settings itemSettings){
        Identifier id = HarvestMod.id(name);
        Registry.register(Registries.BLOCK, id, block);
        Registry.register(Registries.ITEM, id, new BlockItem(block, itemSettings));
        return block;
    }

    public static void initialize(){

    }
}
