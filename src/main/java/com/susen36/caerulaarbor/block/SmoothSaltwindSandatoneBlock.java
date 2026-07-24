
package com.susen36.caerulaarbor.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class SmoothSaltwindSandatoneBlock extends Block {
	public SmoothSaltwindSandatoneBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).sound(SoundType.STONE).strength(3f, 8f).requiresCorrectToolForDrops());
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}
}
