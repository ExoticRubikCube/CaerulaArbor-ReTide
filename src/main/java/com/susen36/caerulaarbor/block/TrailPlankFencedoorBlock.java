
package com.susen36.caerulaarbor.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class TrailPlankFencedoorBlock extends FenceGateBlock {
	public TrailPlankFencedoorBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(3f, 5f).noOcclusion().isRedstoneConductor((bs, br, bp) -> false).dynamicShape().forceSolidOn(), WoodType.OAK);
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 0;
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 8;
	}
}
