package com.apocalypse.caerulaarbor.datagen.worldgen;

import com.apocalypse.caerulaarbor.init.CABlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.CherryFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedBlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.CherryTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;

/**
 * 生成 configured_feature 注册表数据
 *
 * <p>新增 configured feature 时，在 {@link #bootstrap(BootstapContext)} 中创建 {@link ConfiguredFeature}，
 * 选择 {@link Feature} 与匹配的配置对象，再通过 {@link #register(BootstapContext, ResourceKey, ConfiguredFeature)} 写入注册表
 * 方块状态直接引用 {@link CABlocks} 或 {@link Blocks} 字段，不通过字符串 ID 查询注册表
 * <p>示例：
 * <pre>{@code
 * public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
 *     // 注册一个随机斑块 configured feature，适合花草、蘑菇等简单方块散布
 *     register(context, WorldgenKeys.ConfiguredFeatures.IRIS_DISTRIBUTE, randomPatch(
 *             // 每次放置尝试次数
 *             16,
 *             // 水平方向扩散范围
 *             7,
 *             // 垂直方向扩散范围
 *             3,
 *             // 直接引用已注册的模组方块
 *             CABlocks.REDSTONEIRIS_SEEDING.get(),
 *             // 放置位置需要满足的条件
 *             BlockPredicate.matchesBlocks(Blocks.AIR)
 *     ));
 *
 *     // 注册一个树 configured feature，Feature 与配置对象需要匹配
 *     register(context, WorldgenKeys.ConfiguredFeatures.BRANDED_LAND_TREE, new ConfiguredFeature<>(
 *             // 选择原版树 feature
 *             Feature.TREE,
 *             // 提供树干、树叶、树冠尺寸等配置
 *             exampleTree()
 *     ));
 * }
 *
 * private static TreeConfiguration exampleTree() {
 *     // 使用 TreeConfigurationBuilder 组装树的方块和形态参数
 *     return new TreeConfiguration.TreeConfigurationBuilder(
 *             // 树干方块
 *             BlockStateProvider.simple(Blocks.OAK_LOG),
 *             // 树干高度和随机高度
 *             new StraightTrunkPlacer(4, 2, 0),
 *             // 树叶方块
 *             BlockStateProvider.simple(Blocks.OAK_LEAVES),
 *             // 树冠半径和高度
 *             new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
 *             // 树形尺寸层级
 *             new TwoLayersFeatureSize(1, 0, 1)
 *     // 设置泥土替换规则并构建配置
 *     ).dirt(BlockStateProvider.simple(Blocks.DIRT)).ignoreVines().build();
 * }</pre>
 */
public final class ConfiguredFeatureProvider {
    /**
     * 工具类不实例化
     */
    private ConfiguredFeatureProvider() {
    }

    /**
     * 注册 configured feature
     *
     * @param context Mojang 提供的注册表 bootstrap 上下文
     */
    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        register(context, WorldgenKeys.ConfiguredFeatures.BRANDED_LAND_TREE, new ConfiguredFeature<>(Feature.TREE, brandedLandTree()));
        register(context, WorldgenKeys.ConfiguredFeatures.BURNT_TRAILS, new ConfiguredFeature<>(Feature.RANDOM_SELECTOR, burntTrails()));
        register(context, WorldgenKeys.ConfiguredFeatures.IRIS_DISTRIBUTE, randomPatch(2, 2, 2, CABlocks.REDSTONEIRIS_SEEDING.get(), BlockPredicate.allOf(
                BlockPredicate.matchesBlocks(Blocks.AIR),
                BlockPredicate.matchesBlocks(new Vec3i(0, -1, 0), Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.REDSTONE_ORE)
        )));
        register(context, WorldgenKeys.ConfiguredFeatures.NETHERSEA_TREE, new ConfiguredFeature<>(Feature.TREE, netherseaTree()));
        register(context, WorldgenKeys.ConfiguredFeatures.REDSTONEIRIS_SEEDING, randomPatch(2, 7, 3, CABlocks.REDSTONEIRIS_SEEDING.get(), BlockPredicate.matchesBlocks(Blocks.AIR)));
        register(context, WorldgenKeys.ConfiguredFeatures.SLIDER_FLOWER, randomPatch(3, 4, 3, CABlocks.VIVIPAROUS_LILY.get(), BlockPredicate.allOf(
                BlockPredicate.matchesBlocks(Blocks.AIR),
                BlockPredicate.matchesBlocks(new Vec3i(0, -1, 0), Blocks.GRASS_BLOCK)
        )));
        register(context, WorldgenKeys.ConfiguredFeatures.TRAIL_MUSHROOM, randomPatch(64, 7, 3, CABlocks.TRAIL_MUSHROOM.get(), BlockPredicate.matchesBlocks(Blocks.AIR)));
    }

    /**
     * 构建 branded_land_tree 的树配置
     *
     * @return 树配置
     */
    private static TreeConfiguration brandedLandTree() {
        return new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.OAK_LOG),
                new StraightTrunkPlacer(4, 2, 0),
                BlockStateProvider.simple(Blocks.OAK_LEAVES),
                new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                new TwoLayersFeatureSize(1, 0, 1)
        ).dirt(BlockStateProvider.simple(Blocks.DIRT)).ignoreVines().build();
    }

    /**
     * 构建 nethersea_tree 的树配置
     *
     * @return 树配置
     */
    private static TreeConfiguration netherseaTree() {
        return new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(CABlocks.TRAIL_LOG.get()),
                new CherryTrunkPlacer(
                        7,
                        3,
                        2,
                        UniformInt.of(1, 3),
                        UniformInt.of(2, 5),
                        UniformInt.of(-4, -3),
                        UniformInt.of(-1, 0)
                ),
                BlockStateProvider.simple(CABlocks.TRAIL_LEAVE.get()),
                new CherryFoliagePlacer(
                        ConstantInt.of(4),
                        ConstantInt.of(0),
                        ConstantInt.of(6),
                        0.25F,
                        0.25F,
                        0.166F,
                        0.4F
                ),
                new TwoLayersFeatureSize(1, 0, 2)
        ).dirt(BlockStateProvider.simple(Blocks.DIRT)).ignoreVines().build();
    }

    /**
     * 构建 burnt_trails 的随机选择配置
     *
     * @return 随机 feature 配置
     */
    private static RandomFeatureConfiguration burntTrails() {
        var disk = Holder.direct(new PlacedFeature(Holder.direct(new ConfiguredFeature<>(Feature.DISK, new DiskConfiguration(
                RuleBasedBlockStateProvider.simple(BlockStateProvider.simple(CABlocks.SEA_TRAIL_BURNT_SOLID.get())),
                BlockPredicate.matchesTag(BlockTags.DIRT),
                UniformInt.of(0, 3),
                1
        ))), List.of()));
        var simple = Holder.direct(new PlacedFeature(Holder.direct(new ConfiguredFeature<>(Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(CABlocks.SEA_TRAIL_BURNT.get()))
        )), List.of()));
        var noop = Holder.direct(new PlacedFeature(Holder.direct(new ConfiguredFeature<>(Feature.NO_OP, NoneFeatureConfiguration.INSTANCE)), List.of()));
        return new RandomFeatureConfiguration(List.of(
                new WeightedPlacedFeature(disk, 0.5F),
                new WeightedPlacedFeature(simple, 0.5F)
        ), noop);
    }

    /**
     * 构建随机斑块 feature
     *
     * @param tries     尝试次数
     * @param xzSpread  水平扩散范围
     * @param ySpread   垂直扩散范围
     * @param block     放置方块
     * @param predicate 放置条件
     * @return configured feature
     */
    private static ConfiguredFeature<RandomPatchConfiguration, Feature<RandomPatchConfiguration>> randomPatch(int tries, int xzSpread, int ySpread, Block block, BlockPredicate predicate) {
        var placed = Holder.direct(new PlacedFeature(Holder.direct(new ConfiguredFeature<>(Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(block))
        )), List.of(BlockPredicateFilter.forPredicate(predicate))));
        return new ConfiguredFeature<>(Feature.RANDOM_PATCH, new RandomPatchConfiguration(tries, xzSpread, ySpread, placed));
    }

    /**
     * 注册 configured feature
     *
     * @param context 注册表 bootstrap 上下文
     * @param key     configured feature 注册 key
     * @param feature configured feature
     */
    private static void register(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, ConfiguredFeature<?, ?> feature) {
        context.register(key, feature);
    }

}
