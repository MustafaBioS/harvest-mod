package com.harvestmod.item;

import com.harvestmod.HarvestMod;
import com.harvestmod.block.ModBlocks;
import com.harvestmod.entity.ModEntities;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class ModItems {

    public static final Item TOMATO_SEEDS = registerItem("tomato_seeds",
            new AliasedBlockItem(ModBlocks.TOMATO_CROP, new Item.Settings().component(
                    DataComponentTypes.LORE, new LoreComponent(List.of(
                            Text.translatable("item.harvestmod.tomato_seeds.tooltip")
                    ))
            )));

    public static final Item TOMATO = registerItem("tomato", new Item(new Item.Settings()
            .component(DataComponentTypes.LORE, new LoreComponent(List.of(
                    Text.translatable("item.harvestmod.tomato.tooltip")
            )))
            .food(
                new FoodComponent.Builder()
                    .nutrition(2)
                    .saturationModifier(0.6f)
                    .build()
            )
    ));

    public static final Item TOMATO_SOUP = registerItem("tomato_soup", new Item(new Item.Settings()
            .component(DataComponentTypes.LORE, new LoreComponent(List.of(
                    Text.translatable("item.harvestmod.tomato_soup.tooltip")
            )))
            .food(
                new FoodComponent.Builder()
                    .nutrition(4)
                    .saturationModifier(0.8f)
                    .build()
            )
    ));

    public static final Item GARLIC = registerItem("garlic", new Item(new Item.Settings()
            .component(DataComponentTypes.LORE, new LoreComponent(List.of(
                    Text.translatable("item.harvestmod.garlic.tooltip")
            )))
            .food(
                    new FoodComponent.Builder()
                        .nutrition(-1)
                        .saturationModifier(0.1f)
                        .build()
            )
    ));

    public static final Item NULLSKULLS_SPAWN_EGG = registerItem("nullskulls_spawn_egg",
            new SpawnEggItem(ModEntities.CROP_THIEF, 0x36454F, 0xFFFFFF,
                    new Item.Settings().component(
                            DataComponentTypes.LORE,
                            new LoreComponent(List.of(
                                    Text.translatable("item.harvestmod.nullskulls_spawn_egg.tooltip")
                            ))
                    )));

    public static final Item MUSTAFA_SPAWN_EGG = registerItem("mustafa_spawn_egg",
            new SpawnEggItem(ModEntities.CROP_THIEF, 0x6AAABF, 0xFFFFFF,
                    new Item.Settings()
                            .component(DataComponentTypes.ENTITY_DATA, NbtComponent.of(variantData(1)))
                            .component(DataComponentTypes.LORE, new LoreComponent(List.of(
                                    Text.translatable("item.harvestmod.mustafa_spawn_egg.tooltip")
                            )))
                    ));

    private static NbtCompound variantData(int variantId) {
        NbtCompound data = new NbtCompound();
        data.putInt("Variant", variantId);
        return data;
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(HarvestMod.MOD_ID, name), item);
    }

    public static void registerModItems() {

        HarvestMod.LOGGER.info("Registering ModItems for " + HarvestMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(entries -> {
            entries.add(TOMATO_SEEDS);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(TOMATO);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> {
            entries.add(NULLSKULLS_SPAWN_EGG);
            entries.add(MUSTAFA_SPAWN_EGG);
        });
    }


}
