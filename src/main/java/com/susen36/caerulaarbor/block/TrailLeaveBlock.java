
package com.susen36.caerulaarbor.block;

import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.init.CABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
public class TrailLeaveBlock extends LeavesBlock {
	public static final IntegerProperty GROW_AGE = IntegerProperty.create("grow_age", 0, 64);
	public static final IntegerProperty LONGEVITY = IntegerProperty.create("longevity", 0, 16);

	public TrailLeaveBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.MOSS).strength(0.5f, 1f).speedFactor(0.8f).jumpFactor(0.9f).noOcclusion());
		this.registerDefaultState(this.stateDefinition.any().setValue(GROW_AGE, 0).setValue(LONGEVITY, 16));
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 1;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(GROW_AGE, LONGEVITY);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(GROW_AGE, 0).setValue(LONGEVITY, 16);
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 20;
	}

	@Override
	public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
		super.onPlace(blockstate, world, pos, oldState, moving);
		world.scheduleTick(pos, this, 40);
	}

	@Override
	public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
		super.tick(blockstate, world, pos, random);
		int growAge = blockstate.getValue(GROW_AGE);
		int longevity = blockstate.getValue(LONGEVITY);
		if (growAge < 64) {
			world.setBlock(pos, blockstate.setValue(GROW_AGE, growAge + 1), 3);
		}
		if (growAge > 16 && growAge < 62) {
			if (longevity > 0) {
				double strategyGrow = MapVariables.get(world).strategy_grow;
				float effectiveSpreadRate = 0.2F * 0.9F * (1.0F + 0.10F * (float) strategyGrow);
				for (Direction direction : Direction.values()) {
					if (random.nextFloat() < effectiveSpreadRate) {
						int spreadLongevity = random.nextFloat() < 0.25F ? longevity - 1 : longevity;
						BlockPos targetPos = pos.relative(direction);
						BlockState targetBlock = world.getBlockState(targetPos);
						if (targetBlock.is(BlockTags.LEAVES) && targetBlock.getBlock() != CABlocks.TRAIL_LEAVE.get()) {
							world.setBlock(targetPos, CABlocks.TRAIL_LEAVE.get().withPropertiesOf(targetBlock).setValue(LONGEVITY, spreadLongevity), 3);
						}
					}
				}
			}
		} else if (growAge > 62 && random.nextFloat() < 0.0125F) {
			boolean drop = true;
			BlockPos.MutableBlockPos mutablePos = pos.mutable();
			BlockState seaTrailInitState = CABlocks.SEA_TRAIL_INIT.get().defaultBlockState();
			for (int index = 0; index < 64; index++) {
				mutablePos.set(pos.getX(), pos.getY() - index - 1, pos.getZ());
				BlockState targetBlock = world.getBlockState(mutablePos);
				if (targetBlock.isAir() || targetBlock.canBeReplaced()) {
					if (seaTrailInitState.canSurvive(world, mutablePos)) {
						break;
					}
					continue;
				}
				if (targetBlock.getBlock() == CABlocks.TRAIL_LOG.get() || targetBlock.getBlock() == CABlocks.STRIPPED_TRAIL_LOG.get()) {
					drop = false;
				}
				if (targetBlock.getBlock() == CABlocks.NETHERSEA_WOOD.get() || targetBlock.getBlock() == CABlocks.STRIPPED_NETHERSEA_WOOD.get()) {
					drop = false;
				}
				if (targetBlock.getBlock() == CABlocks.TRAIL_LEAVE.get() || index > 0) {
					drop = false;
				}
				break;
			}
			if (drop) {
				world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
				FallingBlockEntity.fall(world, pos, seaTrailInitState);
			}
		}
		world.scheduleTick(pos, this, 40);
	}
}