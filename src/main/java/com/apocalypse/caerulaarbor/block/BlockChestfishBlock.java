
package com.apocalypse.caerulaarbor.block;

import com.apocalypse.caerulaarbor.init.CAEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockChestfishBlock extends Block implements SimpleWaterloggedBlock {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public BlockChestfishBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(16384f, 5000f).noOcclusion().isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
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
            case NORTH -> box(1, 0, 1, 15, 14, 15);
			case EAST -> box(1, 0, 1, 15, 14, 15);
			case WEST -> box(1, 0, 1, 15, 14, 15);
            default -> box(1, 0, 1, 15, 14, 15);
        };
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, WATERLOGGED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
		return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, flag);
	}

	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

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
	public void neighborChanged(BlockState blockstate, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
		super.neighborChanged(blockstate, world, pos, neighborBlock, fromPos, moving);
		if (world.getBestNeighborSignal(pos) > 0) {
			summonChestFish(world, pos, blockstate);
		}
	}

	@Override
	public void attack(BlockState blockstate, Level world, BlockPos pos, Player entity) {
		super.attack(blockstate, world, pos, entity);
		summonChestFish(world, pos, blockstate);
	}

	@Override
	public void onProjectileHit(Level world, BlockState blockstate, BlockHitResult hit, Projectile entity) {
		summonChestFish(world, hit.getBlockPos(), blockstate);
	}

	@Override
	public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
		super.use(blockstate, world, pos, entity, hand, hit);
		return summonChestFish(world, pos, blockstate);
	}

	private InteractionResult summonChestFish(Level world, BlockPos pos, BlockState blockstate) {
		float angl = switch (blockstate.getValue(FACING)) {
			case NORTH -> -180;
			case SOUTH -> 0;
			case WEST -> 90;
			default -> -90;
		};
		world.destroyBlock(pos, false);
		if (!world.isClientSide()) {
			world.playSound(null, pos, SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1, 1);
		} else {
			world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1, 1, false);
		}
		if (Math.random() < 0.7) {
			if (world instanceof ServerLevel _level) {
				Entity entityToSpawn = CAEntities.CHEST_FISH.get().spawn(_level, BlockPos.containing(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5), MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn != null) {
					entityToSpawn.setYRot(angl);
					entityToSpawn.setYBodyRot(angl);
					entityToSpawn.setYHeadRot(angl);
				}
			}
		} else if (Math.random() < 0.85) {
			if (world instanceof ServerLevel _level) {
				Entity entityToSpawn = CAEntities.SCREAM_CHEST_FISH.get().spawn(_level, BlockPos.containing(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5), MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn != null) {
					entityToSpawn.setYRot(angl);
					entityToSpawn.setYBodyRot(angl);
					entityToSpawn.setYHeadRot(angl);
				}
			}
		} else {
			if (world instanceof ServerLevel _level) {
				Entity entityToSpawn = CAEntities.SPIKE_CHEST.get().spawn(_level, BlockPos.containing(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5), MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn != null) {
					entityToSpawn.setYRot(angl);
					entityToSpawn.setYBodyRot(angl);
					entityToSpawn.setYHeadRot(angl);
				}
			}
		}
		return InteractionResult.PASS;
	}
}