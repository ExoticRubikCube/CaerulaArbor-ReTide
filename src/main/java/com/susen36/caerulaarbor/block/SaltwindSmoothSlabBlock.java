
package com.susen36.caerulaarbor.block;

import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class SaltwindSmoothSlabBlock extends SlabBlock {
	public SaltwindSmoothSlabBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).sound(SoundType.STONE).strength(1f, 3f).requiresCorrectToolForDrops().dynamicShape());
	}
}