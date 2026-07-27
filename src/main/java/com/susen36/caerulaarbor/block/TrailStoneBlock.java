
package com.susen36.caerulaarbor.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.ForgeSoundType;

public class TrailStoneBlock extends Block {
	public TrailStoneBlock() {
		super(BlockBehaviour.Properties.of()
				.sound(new ForgeSoundType(1.0f, 1.0f, () -> SoundEvents.STONE_BREAK, () -> SoundEvents.STONE_STEP,
						() -> SoundEvents.STONE_PLACE, () -> SoundEvents.STONE_HIT,
						() -> SoundEvents.STONE_FALL))
				.strength(1.5f, 3f).requiresCorrectToolForDrops().speedFactor(0.9f));
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}
}