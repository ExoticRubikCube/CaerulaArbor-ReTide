
package com.susen36.caerulaarbor.block;

import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;

public class ChitinBlockBlock extends Block {
	public ChitinBlockBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.RAW_IRON).sound(SoundType.BONE_BLOCK).strength(18f, 75f).requiresCorrectToolForDrops());
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}
}
