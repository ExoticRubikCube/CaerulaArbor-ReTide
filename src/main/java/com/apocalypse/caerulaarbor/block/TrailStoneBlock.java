
package com.apocalypse.caerulaarbor.block;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.registries.ForgeRegistries;

public class TrailStoneBlock extends Block {
	public TrailStoneBlock() {
		super(BlockBehaviour.Properties.of()
				.sound(new ForgeSoundType(1.0f, 1.0f, () -> ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.stone.break")), () -> ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.stone.step")),
						() -> ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.stone.place")), () -> ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.stone.hit")),
						() -> ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.stone.fall"))))
				.strength(1.5f, 3f).requiresCorrectToolForDrops().speedFactor(0.9f));
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}
}
