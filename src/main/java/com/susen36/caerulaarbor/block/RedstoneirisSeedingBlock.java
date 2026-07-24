
package com.susen36.caerulaarbor.block;

import com.susen36.caerulaarbor.init.CABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.Map;

public class RedstoneirisSeedingBlock extends FlowerBlock implements BonemealableBlock {
	public RedstoneirisSeedingBlock() {
		super(() -> MobEffects.REGENERATION, 80, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).sound(SoundType.GRASS).instabreak().noCollission().offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
	}

	@Override
	public int getEffectDuration() {
		return 80;
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 100;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 60;
	}

	@Override
	public boolean mayPlaceOn(BlockState groundState, BlockGetter worldIn, BlockPos pos) {
		return groundState.is(Blocks.MOSS_BLOCK) || groundState.is(Blocks.GRASS_BLOCK) || groundState.is(Blocks.DIRT) || groundState.is(Blocks.COARSE_DIRT) || groundState.is(Blocks.PODZOL) || groundState.is(Blocks.ROOTED_DIRT)
				|| groundState.is(Blocks.REDSTONE_ORE) || groundState.is(Blocks.DEEPSLATE_REDSTONE_ORE) || groundState.is(Blocks.REDSTONE_ORE);
	}

	@Override
	public boolean canSurvive(BlockState blockstate, LevelReader worldIn, BlockPos pos) {
		BlockPos blockpos = pos.below();
		BlockState groundState = worldIn.getBlockState(blockpos);
		return this.mayPlaceOn(groundState, worldIn, blockpos);
	}

	@SuppressWarnings("unchecked")
	private static <T extends Comparable<T>> BlockState copyProperty(BlockState source, BlockState target, Property<T> property) {
		if (source.hasProperty(property)) {
			return target.setValue(property, source.getValue(property));
		}
		return target;
	}

	@Override
	public void randomTick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
		if (Math.random() < 0.05) {
			BlockState bs = CABlocks.REDSTONE_IRIS.get().defaultBlockState();
			BlockState bso = world.getBlockState(pos);
			for (Map.Entry<Property<?>, Comparable<?>> entry : bso.getValues().entrySet()) {
				Property<?> property = bs.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
				if (property != null) {
					bs = copyProperty(bso, bs, property);
				}
			}
			world.setBlock(pos, bs, 3);
		}
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader worldIn, BlockPos pos, BlockState blockstate, boolean clientSide) {
		if (worldIn instanceof LevelAccessor world) {
            double x = pos.getX();
            double y = pos.getY();
            double z = pos.getZ();
            return (world.getBlockState(BlockPos.containing(x, y - 1, z))).getBlock() == Blocks.REDSTONE_ORE || (world.getBlockState(BlockPos.containing(x, y - 1, z))).getBlock() == Blocks.REDSTONE_ORE
                    || (world.getBlockState(BlockPos.containing(x, y - 1, z))).getBlock() == Blocks.DEEPSLATE_REDSTONE_ORE;
        }
		return false;
	}

	@Override
	public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState blockstate) {
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState blockstate) {
		if (Math.random() < 0.05) {
			BlockState bs = CABlocks.REDSTONE_IRIS.get().defaultBlockState();
			BlockState bso = world.getBlockState(pos);
			for (Map.Entry<Property<?>, Comparable<?>> entry : bso.getValues().entrySet()) {
				Property<?> property = bs.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
				if (property != null) {
					bs = copyProperty(bso, bs, property);
				}
			}
			world.setBlock(pos, bs, 3);
		}
	}
}
