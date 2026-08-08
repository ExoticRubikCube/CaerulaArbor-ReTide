
package com.susen36.caerulaarbor.block;

import com.susen36.caerulaarbor.util.PlayerStateUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class TrailWallBlock extends WallBlock {
	public TrailWallBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.SCULK_CATALYST).strength(5f, 12f).requiresCorrectToolForDrops().friction(0.7f).speedFactor(0.9f).dynamicShape().forceSolidOn());
	}

	@Override
	public boolean onDestroyedByPlayer(BlockState blockstate, Level world, BlockPos pos, Player entity, boolean willHarvest, FluidState fluid) {
        PlayerStateUtils.pokeSlightly(world, pos.getX(), pos.getY(), pos.getZ(), entity);
		return super.onDestroyedByPlayer(blockstate, world, pos, entity, willHarvest, fluid);
	}
}