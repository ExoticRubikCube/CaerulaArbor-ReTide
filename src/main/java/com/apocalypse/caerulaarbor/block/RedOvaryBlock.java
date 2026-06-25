package com.apocalypse.caerulaarbor.block;

import com.apocalypse.caerulaarbor.configuration.CaerulaConfigsConfiguration;
import com.apocalypse.caerulaarbor.init.CaerulaArborModGameRules;
import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import com.apocalypse.caerulaarbor.procedures.SummonRandomSeabornProcedure;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;

public class RedOvaryBlock extends AbstractOvaryBlock {

	public RedOvaryBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.SCULK_SENSOR).strength(6f, 18f).lightLevel(s -> 4).requiresCorrectToolForDrops().speedFactor(0.9f).jumpFactor(0.9f).noOcclusion().pushReaction(PushReaction.BLOCK)
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
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		double chance = 0;
		double rate = 0;
		if (!(world.getDifficulty() == Difficulty.PEACEFUL)) {
			if (!(world.getBlockFloorHeight(BlockPos.containing(x, (double) y + 1, z)) > 0) && !(world.getBlockFloorHeight(BlockPos.containing(x, (double) y + 2, z)) > 0)) {
				rate = 0.5;
				if (CaerulaArborModVariables.MapVariables.get(world).strategy_breed >= 2) {
					rate = 0.65;
				}
				if (CaerulaArborModVariables.MapVariables.get(world).strategy_breed >= 4) {
					rate = 0.7;
				}
				chance = blockstate.getValue(OUTPUT);
				if (Math.random() < chance * 0.005) {
					if (EntityUtils.getSeabornNum(world, x, y, z) < Math.min((((LevelAccessor) world).getLevelData().getGameRules().getInt(CaerulaArborModGameRules.CLONE_NUMBER_LIMIT)), (double) CaerulaConfigsConfiguration.CLONE_NUM.get())) {
						com.apocalypse.caerulaarbor.utils.WorldUtils.summonRandomSeaborn(world, rate, (double) x + 0.5, (double) y + 1.5, (double) z + 0.5);
						{
							int _value = 0;
							BlockPos _pos = BlockPos.containing(x, y, z);
							BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
							if (_bs.getBlock().getStateDefinition().getProperty("output") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
								((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
						}
					}
				} else {
					{
						int _value = (int) (chance + 1);
						BlockPos _pos = BlockPos.containing(x, y, z);
						BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
						if (_bs.getBlock().getStateDefinition().getProperty("output") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
							((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
					}
				}
			}
		}
		world.scheduleTick(pos, this, 60);
	}
}