package com.susen36.caerulaarbor.block.grower;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public final class NetherseaSamplingTreeGrower {
    public static final TreeGrower INSTANCE = new TreeGrower(
        "nethersea_sampling",
        Optional.empty(),
        Optional.of(ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath("caerula_arbor", "nethersea_tree"))),
        Optional.empty()
    );

    private NetherseaSamplingTreeGrower() {
    }
}
