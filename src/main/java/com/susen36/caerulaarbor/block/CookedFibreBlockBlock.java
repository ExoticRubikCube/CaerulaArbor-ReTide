
package com.susen36.caerulaarbor.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class CookedFibreBlockBlock extends Block {
	public CookedFibreBlockBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.SAND).sound(SoundType.SHROOMLIGHT).strength(0.15f, 0.1f));
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}
}
