package com.susen36.caerulaarbor.block.grower;

import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class NetherseaSamplingTreeGrower extends AbstractTreeGrower {
	protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource randomSource, boolean hasFlower) {
		return FeatureUtils.createKey("caerula_arbor:nethersea_tree");
	}
}
