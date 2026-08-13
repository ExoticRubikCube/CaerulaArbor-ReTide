
package com.susen36.caerulaarbor.block;

import com.susen36.caerulaarbor.init.CABlocks;
import com.susen36.caerulaarbor.manager.upgrade.SilenceUpgradeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SeaTrailInitBlock extends Block implements NetherseaBrandBlock, SimpleWaterloggedBlock, BonemealableBlock {
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final IntegerProperty LONGEVITY = IntegerProperty.create("longevity", 0, 16);
	public static final IntegerProperty GROW_AGE = IntegerProperty.create("grow_age", 0, 48);

	public SeaTrailInitBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.SCULK_VEIN).strength(0.5f, 0f).noCollission().friction(0.5f).speedFactor(0.8f).noOcclusion().pushReaction(PushReaction.DESTROY).isRedstoneConductor((bs, br, bp) -> false).dynamicShape()
				.offsetType(Block.OffsetType.XZ));
		this.registerDefaultState(this.stateDefinition.any().setValue(LONGEVITY, 16).setValue(GROW_AGE, 0).setValue(WATERLOGGED, false));
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
		return box(2, 0, 2, 14, 0.625, 14);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(LONGEVITY, GROW_AGE, WATERLOGGED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
		return super.getStateForPlacement(context).setValue(LONGEVITY, 16).setValue(GROW_AGE, 0).setValue(WATERLOGGED, flag);
	}

	@Override
	public boolean canSurvive(BlockState blockstate, LevelReader worldIn, BlockPos pos) {
		if (worldIn instanceof LevelAccessor world) {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			return NetherseaBrandBlock.canPutTrail(world, x, y, z);
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
		return 3;
	}

	@Override
	public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
		super.onPlace(blockstate, world, pos, oldState, moving);
		world.scheduleTick(pos, this, 20);
	}

	@Override
	public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
		super.tick(blockstate, world, pos, random);
		int expand = 1;
		if (world.getLevelData().isThundering()) {
			expand = 2;
		}
		if (SilenceUpgradeManager.isSilence(world)) {
			expand = 3;
		}
		int growAge = blockstate.getValue(GROW_AGE);
		int nextGrowAge = growAge + expand;
		if (nextGrowAge <= 48) {
			world.setBlock(pos, blockstate.setValue(GROW_AGE, nextGrowAge), 3);
		}
		if (growAge > 29) {
			BlockState nextState = CABlocks.SEA_TRAIL_GROWING.get().defaultBlockState()
				.setValue(SeaTrailGrowingBlock.LONGEVITY, blockstate.getValue(LONGEVITY));
			if (nextState.hasProperty(SeaTrailGrowingBlock.WATERLOGGED)) {
				nextState = nextState.setValue(SeaTrailGrowingBlock.WATERLOGGED, blockstate.getValue(WATERLOGGED));
			}
			world.setBlock(pos, nextState, 3);
			world.playSound(null, pos, SoundEvents.SCULK_VEIN_STEP, SoundSource.NEUTRAL, 1.0F, 1.0F);
		}
		world.scheduleTick(pos, this, 20);
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader worldIn, BlockPos pos, BlockState blockstate) {
		return true;
	}

	@Override
	public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState blockstate) {
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState blockstate) {
		this.addGrowAge(world, pos);
	}
}