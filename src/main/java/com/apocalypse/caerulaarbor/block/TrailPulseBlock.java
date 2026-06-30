
package com.apocalypse.caerulaarbor.block;

import com.apocalypse.caerulaarbor.init.CABlocks;
import com.apocalypse.caerulaarbor.util.CaerulaUtil;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import com.apocalypse.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.registries.ForgeRegistries;

public class TrailPulseBlock extends Block {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final IntegerProperty NURTR = IntegerProperty.create("nurtr", 0, 16);
	public static final IntegerProperty GROW_AGE = IntegerProperty.create("grow_age", 0, 24);

	public TrailPulseBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BLUE).sound(SoundType.SCULK).strength(3f, 4f).lightLevel(s -> 2).friction(0.7f).speedFactor(0.6f).jumpFactor(0.875f));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(NURTR, 16).setValue(GROW_AGE, 24));
	}

	@Override
	public float[] getBeaconColorMultiplier(BlockState state, LevelReader world, BlockPos pos, BlockPos beaconPos) {
		return new float[]{0.2470588235f, 0.3960784314f, 0.5764705882f};
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 6;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, NURTR, GROW_AGE);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(NURTR, 16).setValue(GROW_AGE, 24);
	}

	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public int getSignal(BlockState blockstate, BlockGetter blockAccess, BlockPos pos, Direction direction) {
		return 15;
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 12;
	}

	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction direction, IPlantable plantable) {
		return true;
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
		return true;
	}

	@Override
	public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
		super.onPlace(blockstate, world, pos, oldState, moving);
		world.scheduleTick(pos, this, 80);
	}

	@Override
	public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
		super.tick(blockstate, world, pos, random);
		BlockState seaTrailInitState = CABlocks.SEA_TRAIL_INIT.get().defaultBlockState();
		boolean put = false;
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			for (int dy = 0; dy <= 2; dy++) {
				for (int dist = 1; dist <= 2; dist++) {
					if (random.nextFloat() < 0.25F) {
						BlockPos targetPos = pos.offset(direction.getStepX() * dist, dy, direction.getStepZ() * dist);
						if (this.tryPlaceSeaTrailInit(world, targetPos, seaTrailInitState)) {
							put = true;
							break;
						}
					}
				}
				if (put) {
					break;
				}
			}
			if (put) {
				break;
			}
			for (int dist = 1; dist <= 2 && !put; dist++) {
				BlockPos dropPos = pos.offset(direction.getStepX() * dist, -1, direction.getStepZ() * dist);
				if (random.nextFloat() < 0.25F && this.canDropTrail(world, dropPos, seaTrailInitState)) {
					FallingBlockEntity.fall(world, dropPos, seaTrailInitState);
					put = true;
				}
			}
			if (put) {
				break;
			}
		}
		if (!put) {
			put = this.tryDropDiagonalTrail(world, pos.offset(1, -1, 1), random, seaTrailInitState);
		}
		if (!put) {
			put = this.tryDropDiagonalTrail(world, pos.offset(1, -1, -1), random, seaTrailInitState);
		}
		if (!put) {
			put = this.tryDropDiagonalTrail(world, pos.offset(-1, -1, 1), random, seaTrailInitState);
		}
		if (!put) {
			put = this.tryDropDiagonalTrail(world, pos.offset(-1, -1, -1), random, seaTrailInitState);
		}
		if (!put) {
			for (Direction direction : Direction.values()) {
				if (random.nextFloat() < 0.25F) {
					BlockPos targetPos = pos.relative(direction);
					BlockState targetState = world.getBlockState(targetPos);
					if (WorldUtils.isOrganic(targetState)) {
						world.setBlock(targetPos, CABlocks.TRAIL_PULSE.get().withPropertiesOf(targetState), 3);
						put = true;
						break;
					}
				}
			}
		}
		int nurture = blockstate.getValue(NURTR);
		if (nurture <= 0) {
			world.destroyBlock(pos, false);
			world.playSound(null, pos, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.sculk_vein.break")), SoundSource.BLOCKS, 0.33F, 1.0F);
			if (random.nextFloat() < 0.025F) {
				world.setBlock(pos, random.nextFloat() < 0.012F ? CABlocks.RED_OVARY.get().defaultBlockState() : CABlocks.OCEAN_OVARY.get().defaultBlockState(), 3);
			}
		}
		int growAge = blockstate.getValue(GROW_AGE);
		if (growAge <= 0 && random.nextFloat() < 0.33F) {
			world.destroyBlock(pos, false);
			world.playSound(null, pos, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.sculk_vein.break")), SoundSource.BLOCKS, 0.33F, 1.0F);
			if (random.nextFloat() < 0.025F) {
				world.setBlock(pos, random.nextFloat() < 0.012F ? CABlocks.RED_OVARY.get().defaultBlockState() : CABlocks.OCEAN_OVARY.get().defaultBlockState(), 3);
			}
		}
		if (put) {
			world.playSound(null, pos, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.sculk_vein.place")), SoundSource.BLOCKS, 0.33F, 1.0F);
			if (nurture > 0) {
				BlockState currentState = world.getBlockState(pos);
				if (currentState.hasProperty(NURTR)) {
					world.setBlock(pos, currentState.setValue(NURTR, nurture - 1), 3);
				}
			}
		}
		if (growAge > 0) {
			BlockState currentState = world.getBlockState(pos);
			if (currentState.hasProperty(GROW_AGE)) {
				world.setBlock(pos, currentState.setValue(GROW_AGE, growAge - 1), 3);
			}
		}
		world.scheduleTick(pos, this, 80);
	}

	private boolean tryPlaceSeaTrailInit(ServerLevel world, BlockPos pos, BlockState seaTrailInitState) {
		BlockState replaceState = world.getBlockState(pos);
		if (!replaceState.canBeReplaced() || !seaTrailInitState.canSurvive(world, pos)) {
			return false;
		}
		BlockState placedState = CABlocks.SEA_TRAIL_INIT.get().withPropertiesOf(replaceState);
		if (placedState.hasProperty(BlockStateProperties.WATERLOGGED)) {
			placedState = placedState.setValue(BlockStateProperties.WATERLOGGED, replaceState.getBlock() == Blocks.WATER);
		}
		world.setBlock(pos, placedState, 3);
		return true;
	}

	private boolean tryDropDiagonalTrail(ServerLevel world, BlockPos pos, RandomSource random, BlockState seaTrailInitState) {
		if (random.nextFloat() < 0.25F && this.canDropTrail(world, pos, seaTrailInitState)) {
			FallingBlockEntity.fall(world, pos, seaTrailInitState);
			return true;
		}
		return false;
	}

	private boolean canDropTrail(LevelAccessor world, BlockPos pos, BlockState seaTrailInitState) {
		if (world.getBlockFloorHeight(pos) > 0) {
			return false;
		}
		BlockPos.MutableBlockPos mutablePos = pos.mutable();
		for (int index = 0; index < 64; index++) {
			mutablePos.set(pos.getX(), pos.getY() - index - 1, pos.getZ());
			BlockState targetBlock = world.getBlockState(mutablePos);
			if (world.isEmptyBlock(mutablePos) || targetBlock.canBeReplaced()) {
				if (seaTrailInitState.canSurvive(world, mutablePos)) {
					return true;
				}
			} else {
				if (index > 0) {
					return false;
				}
				break;
			}
		}
		return false;
	}

	@Override
	public boolean onDestroyedByPlayer(BlockState blockstate, Level world, BlockPos pos, Player entity, boolean willHarvest, FluidState fluid) {
		boolean retval = super.onDestroyedByPlayer(blockstate, world, pos, entity, willHarvest, fluid);
		CaerulaUtil.pokePlayer(world, pos.getX(), pos.getY(), pos.getZ(), entity);
		return retval;
	}

	@Override
	public void stepOn(Level world, BlockPos pos, BlockState blockstate, Entity entity) {
		super.stepOn(world, pos, blockstate, entity);
		EntityUtils.damagedByNethseabrand(world, entity);
	}
}
