
package com.susen36.caerulaarbor.block.doll;

import com.mojang.serialization.MapCodec;
import com.susen36.caerulaarbor.CaerulaArbor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;

import java.util.Collections;
import java.util.List;

public abstract class SeaBornDollBlock<T extends Mob> extends BaseEntityBlock implements SimpleWaterloggedBlock, net.minecraft.world.level.block.EntityBlock {
	protected static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	protected static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final BooleanProperty POWERED = BooleanProperty.create("powered");

	protected SeaBornDollBlock(BlockBehaviour.Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
			.setValue(FACING, Direction.NORTH)
			.setValue(WATERLOGGED, false)
			.setValue(POWERED, false));
	}

	protected abstract float getSpawnScale();

	protected abstract T createEntity(ServerLevel level);

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
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
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED, POWERED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
		return this.defaultBlockState()
			.setValue(FACING, context.getHorizontalDirection().getOpposite())
			.setValue(WATERLOGGED, flag)
			.setValue(POWERED, false);
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		if (!oldState.is(state.getBlock())) {
			this.checkPoweredState(level, pos, state.setValue(POWERED, false));
		}
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		this.checkPoweredState(level, pos, state);
	}

	protected final void checkPoweredState(Level level, BlockPos pos, BlockState state) {
		boolean hasSignal = level.hasNeighborSignal(pos);
		if (!state.getValue(POWERED) && hasSignal) {
			level.setBlock(pos, state.setValue(POWERED, true), 3);
			final Direction facing = state.getValue(FACING);
			CaerulaArbor.queueServerWork(5, () -> {
				level.destroyBlock(pos, false);
				if (level instanceof ServerLevel serverLevel) {
					T entity = this.createEntity(serverLevel);
					double sx = pos.getX() + 0.5;
					double sy = pos.getY();
					double sz = pos.getZ() + 0.5;
					entity.setPos(sx, sy, sz);
					entity.getAttribute(Attributes.SCALE).setBaseValue(this.getSpawnScale());
					float yRot = switch (facing) {
						case NORTH -> -90.0F;
						case SOUTH -> 90.0F;
						case WEST -> 0.0F;
						case EAST -> -180.0F;
						default -> 0.0F;
					};
					entity.setYRot(yRot);
					entity.setYBodyRot(yRot);
					entity.setYHeadRot(yRot);
					entity.yBodyRotO = yRot;
					entity.yRotO = yRot;
					entity.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
					serverLevel.addFreshEntity(entity);
				}
			});
		} else if (state.getValue(POWERED) != hasSignal) {
			level.setBlock(pos, state.setValue(POWERED, hasSignal), 3);
		}
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
			world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}
		return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		List<ItemStack> dropsOriginal = super.getDrops(state, builder);
		if (!dropsOriginal.isEmpty()) {
			return dropsOriginal;
		}
		return Collections.singletonList(new ItemStack(this, 1));
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return MapCodec.unit(this);
	}
}