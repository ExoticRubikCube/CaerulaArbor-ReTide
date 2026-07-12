package com.apocalypse.caerulaarbor.datagen.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
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

public final class ConfiguredFeatureProvider {
    private ConfiguredFeatureProvider() {
    }

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        register(context, "branded_land_tree", new ConfiguredFeature<>(Feature.TREE, brandedLandTree()));
        register(context, "burnt_trails", new ConfiguredFeature<>(Feature.RANDOM_SELECTOR, burntTrails()));
        register(context, "iris_distribute", randomPatch(2, 2, 2, block("caerula_arbor:redstoneiris_seeding"), BlockPredicate.allOf(
                BlockPredicate.matchesBlocks(Blocks.AIR),
                BlockPredicate.matchesBlocks(new Vec3i(0, -1, 0), Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.REDSTONE_ORE)
        )));
        register(context, "nethersea_tree", new ConfiguredFeature<>(Feature.TREE, netherseaTree()));
        register(context, "redstoneiris_seeding", randomPatch(2, 7, 3, block("caerula_arbor:redstoneiris_seeding"), BlockPredicate.matchesBlocks(Blocks.AIR)));
        register(context, "slider_flower", randomPatch(3, 4, 3, block("caerula_arbor:viviparous_lily"), BlockPredicate.allOf(
                BlockPredicate.matchesBlocks(Blocks.AIR),
                BlockPredicate.matchesBlocks(new Vec3i(0, -1, 0), Blocks.GRASS_BLOCK)
        )));
        register(context, "trail_mushroom", randomPatch(64, 7, 3, block("caerula_arbor:trail_mushroom"), BlockPredicate.matchesBlocks(Blocks.AIR)));
    }

    private static TreeConfiguration brandedLandTree() {
        return new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.OAK_LOG),
                new StraightTrunkPlacer(4, 2, 0),
                BlockStateProvider.simple(Blocks.OAK_LEAVES),
                new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                new TwoLayersFeatureSize(1, 0, 1)
        ).dirt(BlockStateProvider.simple(Blocks.DIRT)).ignoreVines().build();
    }

    private static TreeConfiguration netherseaTree() {
        return new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(block("caerula_arbor:trail_log")),
                new CherryTrunkPlacer(
                        7,
                        3,
                        2,
                        UniformInt.of(1, 3),
                        UniformInt.of(2, 5),
                        UniformInt.of(-4, -3),
                        UniformInt.of(-1, 0)
                ),
                BlockStateProvider.simple(block("caerula_arbor:trail_leave")),
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

    private static RandomFeatureConfiguration burntTrails() {
        var disk = Holder.direct(new PlacedFeature(Holder.direct(new ConfiguredFeature<>(Feature.DISK, new DiskConfiguration(
                RuleBasedBlockStateProvider.simple(BlockStateProvider.simple(block("caerula_arbor:sea_trail_burnt_solid"))),
                BlockPredicate.matchesTag(BlockTags.DIRT),
                UniformInt.of(0, 3),
                1
        ))), List.of()));
        var simple = Holder.direct(new PlacedFeature(Holder.direct(new ConfiguredFeature<>(Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(block("caerula_arbor:sea_trail_burnt")))
        )), List.of()));
        var noop = Holder.direct(new PlacedFeature(Holder.direct(new ConfiguredFeature<>(Feature.NO_OP, NoneFeatureConfiguration.INSTANCE)), List.of()));
        return new RandomFeatureConfiguration(List.of(
                new WeightedPlacedFeature(disk, 0.5F),
                new WeightedPlacedFeature(simple, 0.5F)
        ), noop);
    }

    private static ConfiguredFeature<RandomPatchConfiguration, Feature<RandomPatchConfiguration>> randomPatch(int tries, int xzSpread, int ySpread, Block block, BlockPredicate predicate) {
        var placed = Holder.direct(new PlacedFeature(Holder.direct(new ConfiguredFeature<>(Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(block))
        )), List.of(BlockPredicateFilter.forPredicate(predicate))));
        return new ConfiguredFeature<>(Feature.RANDOM_PATCH, new RandomPatchConfiguration(tries, xzSpread, ySpread, placed));
    }

    private static void register(BootstapContext<ConfiguredFeature<?, ?>> context, String name, ConfiguredFeature<?, ?> feature) {
        context.register(WorldgenProvider.modKey(Registries.CONFIGURED_FEATURE, name), feature);
    }

    private static Block block(String id) {
        return BuiltInRegistries.BLOCK.get(WorldgenProvider.location(id));
    }
}
