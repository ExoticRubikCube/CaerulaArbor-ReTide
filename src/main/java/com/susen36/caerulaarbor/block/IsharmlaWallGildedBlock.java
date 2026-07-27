
package com.susen36.caerulaarbor.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class IsharmlaWallGildedBlock extends WallBlock {
	public IsharmlaWallGildedBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(16f, 5000f).lightLevel(s -> 8).requiresCorrectToolForDrops().speedFactor(1.1f).noOcclusion().isRedstoneConductor((bs, br, bp) -> false).dynamicShape().forceSolidOn());
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 0;
	}
}