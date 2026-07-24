
package com.susen36.caerulaarbor.block;

import com.susen36.caerulaarbor.util.CaerulaUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class CrackedTrailBrickBlock extends Block {
	public CrackedTrailBrickBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.SCULK_CATALYST).strength(1.5f, 12f).requiresCorrectToolForDrops().friction(0.7f).speedFactor(0.9f));
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}

	@Override
	public boolean onDestroyedByPlayer(BlockState blockstate, Level world, BlockPos pos, Player entity, boolean willHarvest, FluidState fluid) {
		boolean retval = super.onDestroyedByPlayer(blockstate, world, pos, entity, willHarvest, fluid);
		CaerulaUtil.pokeSlightly(world, pos.getX(), pos.getY(), pos.getZ(), entity);
		return retval;
	}
}
