package com.susen36.caerulaarbor.block.grower;

import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public final class NetherseaSamplingTreeGrower {
    public static final TreeGrower INSTANCE = new TreeGrower(
        "nethersea_sampling",
        Optional.empty(),
        Optional.of(FeatureUtils.createKey("caerula_arbor:nethersea_tree")),
        Optional.empty()
    );

    private NetherseaSamplingTreeGrower() {
    }
}
