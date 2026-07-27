package com.susen36.caerulaarbor.block;

import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ChestFishCookedBlock extends Block implements SimpleWaterloggedBlock {
	public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 2);
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public ChestFishCookedBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(2.0F, 10.0F).lightLevel(s -> 0).noOcclusion().isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false).setValue(BLOCKSTATE, 0));
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
		return box(1.0D, 0.0D, 1.0D, 15.0D, 11.0D, 15.0D);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED, BLOCKSTATE);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, flag);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED)) {
			world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay((LevelReader) world));
		}
		return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public boolean onDestroyedByPlayer(BlockState blockstate, Level world, BlockPos pos, Player entity, boolean willHarvest, FluidState fluid) {
		boolean retval = super.onDestroyedByPlayer(blockstate, world, pos, entity, willHarvest, fluid);
		if (entity != null && !entity.isCreative() && world.getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_DOBLOCKDROPS)) {
			int bs = blockstate.getValue(BLOCKSTATE);
			if (bs == 0) {
				if (world instanceof ServerLevel serverLevel) {
					ItemEntity entityToSpawn = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(this));
					entityToSpawn.setPickUpDelay(10);
					serverLevel.addFreshEntity(entityToSpawn);
				}
			} else if (bs == 1) {
				for (int index0 = 0; index0 < Mth.nextInt(RandomSource.create(), 3, 4); ++index0) {
					if (!(world instanceof ServerLevel serverLevel)) continue;
					ItemEntity entityToSpawn = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.75, pos.getZ() + 0.5, new ItemStack(CAItems.COOKED_PEDUNCLE.get()));
					entityToSpawn.setPickUpDelay(5);
					serverLevel.addFreshEntity(entityToSpawn);
				}
				for (int index1 = 0; index1 < Mth.nextInt(RandomSource.create(), 2, 4); ++index1) {
					if (!(world instanceof ServerLevel serverLevel)) continue;
					ItemEntity entityToSpawn = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.75, pos.getZ() + 0.5, new ItemStack(CAItems.CHITIN_COOKIE_DONE.get()));
					entityToSpawn.setPickUpDelay(5);
					serverLevel.addFreshEntity(entityToSpawn);
				}
			} else if (bs == 2) {
				for (int index2 = 0; index2 < Mth.nextInt(RandomSource.create(), 2, 4); ++index2) {
					if (!(world instanceof ServerLevel serverLevel)) continue;
					ItemEntity entityToSpawn = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.75, pos.getZ() + 0.5, new ItemStack(CAItems.CHITIN_COOKIE_DONE.get()));
					entityToSpawn.setPickUpDelay(5);
					serverLevel.addFreshEntity(entityToSpawn);
				}
			}
		}
		return retval;
	}

	@Override
	public InteractionResult useWithoutItem(BlockState blockstate, Level world, BlockPos pos, Player entity, BlockHitResult hit) {
		super.useWithoutItem(blockstate, world, pos, entity, hit);
		if (entity == null) {
			return InteractionResult.PASS;
		}
		int bs = blockstate.getValue(BLOCKSTATE);
		if (bs == 0) {
			for (int index0 = 0; index0 < Mth.nextInt(RandomSource.create(), 3, 4); ++index0) {
				if (!(world instanceof ServerLevel serverLevel)) continue;
				ItemEntity entityToSpawn = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.75, pos.getZ() + 0.5, new ItemStack(CAItems.COOKED_PEDUNCLE.get()));
				entityToSpawn.setPickUpDelay(5);
				serverLevel.addFreshEntity(entityToSpawn);
			}
			world.setBlock(pos, blockstate.setValue(BLOCKSTATE, 1), 3);
		} else if (bs == 1) {
			for (int index1 = 0; index1 < Mth.nextInt(RandomSource.create(), 3, 4); ++index1) {
				if (!(world instanceof ServerLevel serverLevel)) continue;
				ItemEntity entityToSpawn = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.75, pos.getZ() + 0.5, new ItemStack(CAItems.COOKED_FIBRE.get()));
				entityToSpawn.setPickUpDelay(5);
				serverLevel.addFreshEntity(entityToSpawn);
			}
			world.setBlock(pos, blockstate.setValue(BLOCKSTATE, 2), 3);
		} else if (bs == 2) {
			for (int index2 = 0; index2 < Mth.nextInt(RandomSource.create(), 2, 4); ++index2) {
				if (!(world instanceof ServerLevel serverLevel)) continue;
				ItemEntity entityToSpawn = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.75, pos.getZ() + 0.5, new ItemStack(CAItems.CHITIN_COOKIE_DONE.get()));
				entityToSpawn.setPickUpDelay(5);
				serverLevel.addFreshEntity(entityToSpawn);
			}
			world.setBlock(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
		}
		return InteractionResult.SUCCESS;
	}
}