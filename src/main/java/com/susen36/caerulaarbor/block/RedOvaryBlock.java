package com.susen36.caerulaarbor.block;

import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.init.CAConfigs;
import com.susen36.caerulaarbor.init.CAGameRules;
import com.susen36.caerulaarbor.manager.spwan.SeabornSpawnManager;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.PushReaction;

public class RedOvaryBlock extends AbstractOvaryBlock {

	public RedOvaryBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.SCULK_SENSOR).strength(0.5f, 0.5f).lightLevel(s -> 4).requiresCorrectToolForDrops().speedFactor(0.9f).jumpFactor(0.9f).noOcclusion().pushReaction(PushReaction.BLOCK)
				.hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true).isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OUTPUT, 0).setValue(WATERLOGGED, false));
	}

	@Override
	protected int getTickDelay() {
		return 60;
	}

	@Override
	protected String getDescriptionKey() {
		return "block.caerula_arbor.red_ovary.description_0";
	}

	@Override
	protected double getDestroySpawnRate() {
		return 0.5;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, OUTPUT, WATERLOGGED);
	}

	@Override
	public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
		super.tick(blockstate, world, pos, random);
		if (world.getDifficulty() != Difficulty.PEACEFUL && !(world.getBlockFloorHeight(pos.above()) > 0) && !(world.getBlockFloorHeight(pos.above(2)) > 0)) {
			double strategyBreed = MapVariables.get(world).strategy_breed;
			double rate = 0.5D * (1.0D + 0.075D * strategyBreed);
			int output = blockstate.getValue(OUTPUT);
			if (random.nextFloat() < output * 0.005F) {
				double cloneLimit = Math.min(world.getGameRules().getInt(CAGameRules.CLONE_NUMBER_LIMIT), CAConfigs.CLONE_NUM.get());
				if (EntityUtils.getSeabornNum(world, pos.getX(), pos.getY(), pos.getZ()) < cloneLimit) {
					SeabornSpawnManager.summonRandomSeaborn(world, rate, pos.getX() + 0.5D, pos.getY() + 1.5D, pos.getZ() + 0.5D);
					world.setBlock(pos, blockstate.setValue(OUTPUT, 0), 3);
				}
			} else if (output < 200) {
				world.setBlock(pos, blockstate.setValue(OUTPUT, output + 1), 3);
			}
		}
		world.scheduleTick(pos, this, 60);
	}
}