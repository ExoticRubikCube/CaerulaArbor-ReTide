package com.apocalypse.caerulaarbor.datagen.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * 生成生物群系标签数据，包含生成筛选和结构可生成群系列表
 */
public class BiomeTagsProvider extends TagsProvider.RegistryTagsProvider<Biome> {
    public BiomeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                             @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.BIOME, lookupProvider, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        addValues("common_spawn_biome", "caerula_arbor:branded_land", "birch_forest", "flower_forest", "forest", "old_growth_birch_forest", "plains", "snowy_plains", "sunflower_plains", "meadow", "stony_shore", "beach", "snowy_beach", "#caerula_arbor:underwater_spawn_biome", "#minecraft:is_beach", "#minecraft:is_forest");
        addValues("danger_spawn_biome", "caerula_arbor:branded_land", "beach", "snowy_beach", "stony_shore", "#minecraft:is_beach", "#caerula_arbor:marine_spawn_biome");
        addValues("deepmarine_spawn_biome", "deep_cold_ocean", "deep_frozen_ocean", "deep_lukewarm_ocean", "deep_ocean", "#minecraft:is_deep_ocean", "caerula_arbor:branded_land");
        addValues("marine_spawn_biome", "cold_ocean", "deep_cold_ocean", "deep_frozen_ocean", "deep_lukewarm_ocean", "deep_ocean", "frozen_ocean", "lukewarm_ocean", "ocean", "warm_ocean", "#minecraft:is_ocean", "caerula_arbor:branded_land");
        addValues("rare_spawn_biome", "caerula_arbor:branded_land", "snowy_plains", "sunflower_plains", "beach", "snowy_beach", "stony_shore", "#caerula_arbor:marine_spawn_biome", "#minecraft:is_forest");
        addValues("underwater_spawn_biome", "cold_ocean", "deep_cold_ocean", "deep_frozen_ocean", "deep_lukewarm_ocean", "deep_ocean", "frozen_ocean", "lukewarm_ocean", "ocean", "warm_ocean", "frozen_river", "river", "#minecraft:is_river", "#minecraft:is_ocean", "#minecraft:is_deep_ocean", "caerula_arbor:branded_land");

        addMinecraftValues("has_structure/buried_treasure", "caerula_arbor:branded_land");
        addMinecraftValues("has_structure/mineshaft", "caerula_arbor:branded_land");
        addMinecraftValues("has_structure/ocean_ruin_cold", "caerula_arbor:branded_land");
        addMinecraftValues("has_structure/pillager_outpost", "caerula_arbor:branded_land");
        addMinecraftValues("has_structure/shipwreck_beached", "caerula_arbor:branded_land");
        addMinecraftValues("has_structure/stronghold", "caerula_arbor:branded_land");
        addMinecraftValues("is_overworld");
    }
}
