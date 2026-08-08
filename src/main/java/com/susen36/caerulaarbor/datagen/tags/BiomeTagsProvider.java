package com.susen36.caerulaarbor.datagen.tags;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.init.CABiomes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * 生成生物群系标签数据，包含生成筛选和结构可生成群系列表
 */
public class BiomeTagsProvider extends TagsProvider.RegistryTagsProvider<Biome> {
    private static final TagKey<Biome> COMMON_SPAWN_BIOME = caBiomeTag("common_spawn_biome");
    private static final TagKey<Biome> DANGER_SPAWN_BIOME = caBiomeTag("danger_spawn_biome");
    private static final TagKey<Biome> DEEPMARINE_SPAWN_BIOME = caBiomeTag("deepmarine_spawn_biome");
    private static final TagKey<Biome> MARINE_SPAWN_BIOME = caBiomeTag("marine_spawn_biome");
    private static final TagKey<Biome> RARE_SPAWN_BIOME = caBiomeTag("rare_spawn_biome");
    private static final TagKey<Biome> UNDERWATER_SPAWN_BIOME = caBiomeTag("underwater_spawn_biome");

    public BiomeTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper
    ) {
        super(output, Registries.BIOME, lookupProvider, existingFileHelper);
    }

    /**
     * 创建 caerula_arbor 命名空间的 biome 标签 key
     *
     * @param path 标签路径
     * @return biome 标签 key
     */
    private static TagKey<Biome> caBiomeTag(String path) {
        return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, path));
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        addBiomesToTag(COMMON_SPAWN_BIOME,
                CABiomes.BRANDED_LAND,
                Biomes.BIRCH_FOREST,
                Biomes.FLOWER_FOREST,
                Biomes.FOREST,
                Biomes.OLD_GROWTH_BIRCH_FOREST,
                Biomes.PLAINS,
                Biomes.SNOWY_PLAINS,
                Biomes.SUNFLOWER_PLAINS,
                Biomes.MEADOW,
                Biomes.STONY_SHORE,
                Biomes.BEACH,
                Biomes.SNOWY_BEACH
        );
        addTagsToTag(COMMON_SPAWN_BIOME,
                UNDERWATER_SPAWN_BIOME,
                BiomeTags.IS_BEACH,
                BiomeTags.IS_FOREST
        );
        addBiomesToTag(DANGER_SPAWN_BIOME,
                CABiomes.BRANDED_LAND,
                Biomes.BEACH,
                Biomes.SNOWY_BEACH,
                Biomes.STONY_SHORE
        );
        addTagsToTag(DANGER_SPAWN_BIOME,
                BiomeTags.IS_BEACH,
                MARINE_SPAWN_BIOME
        );
        addBiomesToTag(DEEPMARINE_SPAWN_BIOME,
                Biomes.DEEP_COLD_OCEAN,
                Biomes.DEEP_FROZEN_OCEAN,
                Biomes.DEEP_LUKEWARM_OCEAN,
                Biomes.DEEP_OCEAN,
                CABiomes.BRANDED_LAND
        );
        addTagsToTag(DEEPMARINE_SPAWN_BIOME,
                BiomeTags.IS_DEEP_OCEAN
        );
        addBiomesToTag(MARINE_SPAWN_BIOME,
                Biomes.COLD_OCEAN,
                Biomes.DEEP_COLD_OCEAN,
                Biomes.DEEP_FROZEN_OCEAN,
                Biomes.DEEP_LUKEWARM_OCEAN,
                Biomes.DEEP_OCEAN,
                Biomes.FROZEN_OCEAN,
                Biomes.LUKEWARM_OCEAN,
                Biomes.OCEAN,
                Biomes.WARM_OCEAN,
                CABiomes.BRANDED_LAND
        );
        addTagsToTag(MARINE_SPAWN_BIOME,
                BiomeTags.IS_OCEAN
        );
        addBiomesToTag(RARE_SPAWN_BIOME,
                CABiomes.BRANDED_LAND,
                Biomes.SNOWY_PLAINS,
                Biomes.SUNFLOWER_PLAINS,
                Biomes.BEACH,
                Biomes.SNOWY_BEACH,
                Biomes.STONY_SHORE
        );
        addTagsToTag(RARE_SPAWN_BIOME,
                MARINE_SPAWN_BIOME,
                BiomeTags.IS_FOREST
        );
        addBiomesToTag(UNDERWATER_SPAWN_BIOME,
                Biomes.COLD_OCEAN,
                Biomes.DEEP_COLD_OCEAN,
                Biomes.DEEP_FROZEN_OCEAN,
                Biomes.DEEP_LUKEWARM_OCEAN,
                Biomes.DEEP_OCEAN,
                Biomes.FROZEN_OCEAN,
                Biomes.LUKEWARM_OCEAN,
                Biomes.OCEAN,
                Biomes.WARM_OCEAN,
                Biomes.FROZEN_RIVER,
                Biomes.RIVER,
                CABiomes.BRANDED_LAND
        );
        addTagsToTag(UNDERWATER_SPAWN_BIOME,
                BiomeTags.IS_RIVER,
                BiomeTags.IS_OCEAN,
                BiomeTags.IS_DEEP_OCEAN
        );

        addBiomesToTag(BiomeTags.HAS_BURIED_TREASURE, CABiomes.BRANDED_LAND);
        addBiomesToTag(BiomeTags.HAS_MINESHAFT, CABiomes.BRANDED_LAND);
        addBiomesToTag(BiomeTags.HAS_OCEAN_RUIN_COLD, CABiomes.BRANDED_LAND);
        addBiomesToTag(BiomeTags.HAS_PILLAGER_OUTPOST, CABiomes.BRANDED_LAND);
        addBiomesToTag(BiomeTags.HAS_SHIPWRECK_BEACHED, CABiomes.BRANDED_LAND);
        addBiomesToTag(BiomeTags.HAS_STRONGHOLD, CABiomes.BRANDED_LAND);
        tag(BiomeTags.IS_OVERWORLD);
    }

    /**
     * 向目标标签加入生物群系 key
     *
     * @param targetTag 目标标签
     * @param biomes    要加入的生物群系 key
     */
    @SafeVarargs
    private void addBiomesToTag(TagKey<Biome> targetTag, ResourceKey<Biome>... biomes) {
        var appender = tag(targetTag);
        for (var biome : biomes) {
            appender.add(biome);
        }
    }
}