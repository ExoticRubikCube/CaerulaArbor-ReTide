package com.apocalypse.caerulaarbor.datagen.worldgen;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.placement.SurfaceWaterDepthFilter;

import java.util.List;

/**
 * 生成 placed_feature 注册表数据
 */
public class PlacedFeatureProvider {
    private PlacedFeatureProvider() {
    }

    /**
     * 注册 placed feature
     *
     * @param context Mojang 提供的注册表 bootstrap 上下文
     */
    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        register(context, configuredFeatures, "branded_land_tree", "caerula_arbor:branded_land_tree", List.of(
                CountPlacement.of(2),
                InSquarePlacement.spread(),
                SurfaceWaterDepthFilter.forMaxDepth(0),
                HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR),
                BiomeFilter.biome(),
                BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(Blocks.OAK_SAPLING.defaultBlockState(), Vec3i.ZERO))
        ));
        register(context, configuredFeatures, "burnt_trails", "caerula_arbor:burnt_trails", List.of(
                CountPlacement.of(2),
                RarityFilter.onAverageOnceEvery(3),
                InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR)
        ));
        register(context, configuredFeatures, "iris_distribute", "caerula_arbor:iris_distribute", List.of(
                RarityFilter.onAverageOnceEvery(160),
                InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING)
        ));
        register(context, configuredFeatures, "redstoneiris_seeding", "caerula_arbor:redstoneiris_seeding", List.of(
                RarityFilter.onAverageOnceEvery(32),
                InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                BiomeFilter.biome()
        ));
        register(context, configuredFeatures, "slider_flower", "caerula_arbor:slider_flower", List.of(
                RarityFilter.onAverageOnceEvery(4),
                InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES)
        ));
        register(context, configuredFeatures, "trail_mushroom", "caerula_arbor:trail_mushroom", List.of(
                CountPlacement.of(2),
                RarityFilter.onAverageOnceEvery(32),
                InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                BiomeFilter.biome()
        ));
    }

    /**
     * 注册 placed feature 并绑定 configured feature 与 placement modifier
     *
     * @param context            注册表 bootstrap 上下文
     * @param configuredFeatures configured feature 查询器
     * @param name               placed feature 注册路径
     * @param configuredFeature  configured feature ID
     * @param modifiers          放置修饰器列表
     */
    private static void register(BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures, String name, String configuredFeature, List<PlacementModifier> modifiers) {
        context.register(WorldgenProvider.modKey(Registries.PLACED_FEATURE, name), new PlacedFeature(
                configuredFeatures.getOrThrow(WorldgenProvider.key(Registries.CONFIGURED_FEATURE, configuredFeature)),
                modifiers
        ));
    }
}
