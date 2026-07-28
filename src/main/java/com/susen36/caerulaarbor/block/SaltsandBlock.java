
package com.susen36.caerulaarbor.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class SaltsandBlock extends FallingBlock {
	public static final MapCodec<SaltsandBlock> CODEC = simpleCodec(SaltsandBlock::new);

	

	public SaltsandBlock() {
		this(BlockBehaviour.Properties.of());
	}

	public SaltsandBlock(BlockBehaviour.Properties properties) {
		super(properties.mapColor(MapColor.SAND).sound(SoundType.SAND).strength(0.75f, 1f));
	}

@Override
	protected MapCodec<? extends FallingBlock> codec() {
		return CODEC;
	}
	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}
}