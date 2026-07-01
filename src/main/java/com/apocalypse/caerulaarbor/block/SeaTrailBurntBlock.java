package com.apocalypse.caerulaarbor.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CABlocks;
import com.apocalypse.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;

public class SeaTrailBurntBlock extends Block implements SimpleWaterloggedBlock, BonemealableBlock {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final IntegerProperty GROW_AGE = IntegerProperty.create("grow_age", 0, 64);
	public static final IntegerProperty LONGEVITY = IntegerProperty.create("longevity", 0, 16);
	private static final TagKey<Block> TRAIL_TAG = BlockTags.create(new ResourceLocation(CaerulaArborMod.MODID, "trail"));

	public SeaTrailBurntBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.SNOW).strength(1f).speedFactor(0.9f).jumpFactor(0.9f).noOcclusion().pushReaction(PushReaction.DESTROY).isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(GROW_AGE, 0).setValue(LONGEVITY, 0).setValue(WATERLOGGED, false));
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return state.getFluidState().isEmpty();
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 0;
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(FACING)) {
            case NORTH -> box(0, 0, 0, 16, 0.625, 16);
			case EAST -> box(0, 0, 0, 16, 0.625, 16);
			case WEST -> box(0, 0, 0, 16, 0.625, 16);
            default -> box(0, 0, 0, 16, 0.625, 16);
        };
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, GROW_AGE, LONGEVITY, WATERLOGGED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
		return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(GROW_AGE, 0).setValue(LONGEVITY, 0).setValue(WATERLOGGED, flag);
	}

	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	@Override
	public boolean canSurvive(BlockState blockstate, LevelReader worldIn, BlockPos pos) {
		if (worldIn instanceof LevelAccessor world) {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			return WorldUtils.canPutTrail(world, x, y, z);
		}
		return super.canSurvive(blockstate, worldIn, pos);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED)) {
			world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}
		return !state.canSurvive(world, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 24;
	}

	@Override
	public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
		super.onPlace(blockstate, world, pos, oldState, moving);
		world.scheduleTick(pos, this, 20);
	}

	@Override
	public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
		super.tick(blockstate, world, pos, random);
		int expand = 0;
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			if (world.getBlockState(pos.relative(direction)).is(TRAIL_TAG)) {
				expand = 1;
				break;
			}
		}
		int growAge = blockstate.getValue(GROW_AGE);
		int nextGrowAge = growAge + expand;
		if (nextGrowAge <= 64) {
			world.setBlock(pos, blockstate.setValue(GROW_AGE, nextGrowAge), 3);
		}
		if (growAge > 29) {
			Direction facing = switch (random.nextInt(4)) {
				case 0 -> Direction.EAST;
				case 1 -> Direction.SOUTH;
				case 2 -> Direction.WEST;
				default -> Direction.NORTH;
			};
			BlockState nextState = CABlocks.SEA_TRAIL_GROWN.get().defaultBlockState()
				.setValue(SeaTrailGrownBlock.FACING, facing)
				.setValue(SeaTrailGrownBlock.LONGEVITY, blockstate.getValue(LONGEVITY) - 1);
			if (nextState.hasProperty(SeaTrailGrownBlock.WATERLOGGED)) {
				nextState = nextState.setValue(SeaTrailGrownBlock.WATERLOGGED, blockstate.getValue(WATERLOGGED));
			}
			world.setBlock(pos, nextState, 3);
			world.playSound(null, pos, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.sculk_vein.step")), SoundSource.NEUTRAL, 1.0F, 1.0F);
		}
		world.scheduleTick(pos, this, 20);
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader worldIn, BlockPos pos, BlockState blockstate, boolean clientSide) {
		return true;
	}

	@Override
	public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState blockstate) {
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState blockstate) {
		WorldUtils.addGrowAge(world, pos);
	}
}
