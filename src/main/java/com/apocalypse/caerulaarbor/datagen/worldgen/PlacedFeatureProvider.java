package com.apocalypse.caerulaarbor.datagen.worldgen;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
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
 *
 * <p>新增 placed feature 时，先通过 {@code context.lookup} 获取 configured feature 查询器，
 * 再用 {@link PlacedFeature} 绑定 configured feature 和 {@link PlacementModifier} 列表
 * <p>示例：
 * <pre>{@code
 * public static void bootstrap(BootstapContext<PlacedFeature> context) {
 *     // 查询 configured feature 注册表，用于通过 key 取得要放置的 configured feature
 *     HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
 *
 *     // 调用 register(...) 注册 placed feature，并传入两个字段表 key 和 placement modifier 列表
 *     register(context, configuredFeatures, WorldgenKeys.PlacedFeatures.IRIS_DISTRIBUTE, WorldgenKeys.ConfiguredFeatures.IRIS_DISTRIBUTE, List.of(
 *             // 控制平均多少区块尝试生成一次
 *             RarityFilter.onAverageOnceEvery(16),
 *             // 将放置位置散布到区块内随机 xz 坐标
 *             InSquarePlacement.spread(),
 *             // 将 y 坐标吸附到指定高度图
 *             HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
 *             // 限制只在当前 biome 允许该 placed feature 时生成
 *             BiomeFilter.biome()
 *     ));
 * }
 *
 * private static void register(
 *         BootstapContext<PlacedFeature> context,
 *         HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures,
 *         ResourceKey<PlacedFeature> key,
 *         ResourceKey<ConfiguredFeature<?, ?>> configuredFeatureKey,
 *         List<PlacementModifier> modifiers
 * ) {
 *     // 直接使用 placed feature 注册 key 写入注册表
 *     context.register(key, new PlacedFeature(
 *             // 直接使用 configured feature key 查找 holder
 *             configuredFeatures.getOrThrow(configuredFeatureKey),
 *             // 传入 Count、Rarity、Heightmap、BiomeFilter 等放置修饰器
 *             modifiers
 *     ));
 * }
 * }</pre>
 */
public class PlacedFeatureProvider {
    /**
     * 工具类不实例化
     */
    private PlacedFeatureProvider() {
    }

    /**
     * 注册 placed feature
     *
     * @param context Mojang 提供的注册表 bootstrap 上下文
     */
    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        register(context, configuredFeatures, WorldgenKeys.PlacedFeatures.BRANDED_LAND_TREE, WorldgenKeys.ConfiguredFeatures.BRANDED_LAND_TREE, List.of(
                CountPlacement.of(2),
                InSquarePlacement.spread(),
                SurfaceWaterDepthFilter.forMaxDepth(0),
                HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR),
                BiomeFilter.biome(),
                BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(Blocks.OAK_SAPLING.defaultBlockState(), Vec3i.ZERO))
        ));
        register(context, configuredFeatures, WorldgenKeys.PlacedFeatures.BURNT_TRAILS, WorldgenKeys.ConfiguredFeatures.BURNT_TRAILS, List.of(
                CountPlacement.of(2),
                RarityFilter.onAverageOnceEvery(3),
                InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR)
        ));
        register(context, configuredFeatures, WorldgenKeys.PlacedFeatures.IRIS_DISTRIBUTE, WorldgenKeys.ConfiguredFeatures.IRIS_DISTRIBUTE, List.of(
                RarityFilter.onAverageOnceEvery(160),
                InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING)
        ));
        register(context, configuredFeatures, WorldgenKeys.PlacedFeatures.REDSTONEIRIS_SEEDING, WorldgenKeys.ConfiguredFeatures.REDSTONEIRIS_SEEDING, List.of(
                RarityFilter.onAverageOnceEvery(32),
                InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                BiomeFilter.biome()
        ));
        register(context, configuredFeatures, WorldgenKeys.PlacedFeatures.SLIDER_FLOWER, WorldgenKeys.ConfiguredFeatures.SLIDER_FLOWER, List.of(
                RarityFilter.onAverageOnceEvery(4),
                InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES)
        ));
        register(context, configuredFeatures, WorldgenKeys.PlacedFeatures.TRAIL_MUSHROOM, WorldgenKeys.ConfiguredFeatures.TRAIL_MUSHROOM, List.of(
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
     * @param key                placed feature 注册 key
     * @param configuredFeatureKey configured feature 注册 key
     * @param modifiers          放置修饰器列表
     */
    private static void register(BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures, ResourceKey<PlacedFeature> key, ResourceKey<ConfiguredFeature<?, ?>> configuredFeatureKey, List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(
                configuredFeatures.getOrThrow(configuredFeatureKey),
                modifiers
        ));
    }
}
