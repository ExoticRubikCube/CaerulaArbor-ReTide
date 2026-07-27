
package com.susen36.caerulaarbor.block;

import com.mojang.serialization.MapCodec;
import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.init.CABlockEntities;
import com.susen36.caerulaarbor.init.CAEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class ChestmegaSpawnerBlock extends BaseEntityBlock implements SimpleWaterloggedBlock, EntityBlock {
	public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 1);
	public static final IntegerProperty DATA_ANIMATION = IntegerProperty.create("animation", 0, 1);
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public ChestmegaSpawnerBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(-1, 3600000).lightLevel(s -> (new Object() {
					public int getLightLevel() {
						if (s.getValue(BLOCKSTATE) == 1)
							return 0;
						return 4;
					}
				}.getLightLevel())).noOcclusion().isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return CABlockEntities.CHESTMEGA_SPAWNER.get().create(blockPos, blockState);
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
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		if (state.getValue(BLOCKSTATE) == 1) {

			return switch (state.getValue(FACING)) {
                case NORTH -> box(0, 0, 0, 16, 11, 16);
				case EAST -> box(0, 0, 0, 16, 11, 16);
				case WEST -> box(0, 0, 0, 16, 11, 16);
                default -> box(0, 0, 0, 16, 11, 16);
            };
		}

		return switch (state.getValue(FACING)) {
            case NORTH -> box(0, 0, 0, 16, 11, 16);
			case EAST -> box(0, 0, 0, 16, 11, 16);
			case WEST -> box(0, 0, 0, 16, 11, 16);
            default -> box(0, 0, 0, 16, 11, 16);
        };
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(DATA_ANIMATION, FACING, WATERLOGGED, BLOCKSTATE);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, flag);
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
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		List<ItemStack> dropsOriginal = super.getDrops(state, builder);
		if (!dropsOriginal.isEmpty())
			return dropsOriginal;
		return Collections.singletonList(new ItemStack(this, 1));
	}

	@Override
	public void neighborChanged(BlockState blockstate, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
		super.neighborChanged(blockstate, world, pos, neighborBlock, fromPos, moving);
		if (world.getBestNeighborSignal(pos) > 0) {
			this.summonMegachest(world, pos, blockstate);
		}
	}

	@Override
	public void attack(BlockState blockstate, Level world, BlockPos pos, Player entity) {
		super.attack(blockstate, world, pos, entity);
		this.summonMegachest(world, pos, blockstate);
	}

	@Override
	public InteractionResult useWithoutItem(BlockState blockstate, Level world, BlockPos pos, Player entity, BlockHitResult hit) {
		super.useWithoutItem(blockstate, world, pos, entity, hit);
		return this.summonMegachest(world, pos, blockstate);
	}

	private InteractionResult summonMegachest(LevelAccessor world, BlockPos pos, BlockState blockstate) {
		if (blockstate.getValue(BLOCKSTATE) != 0) {
			return InteractionResult.PASS;
		}

		world.setBlock(pos, world.getBlockState(pos).setValue(BLOCKSTATE, 1), 3);
		world.setBlock(pos, world.getBlockState(pos).setValue(DATA_ANIMATION, 1), 3);

		Direction facing = blockstate.getValue(FACING);
		CaerulaArborMod.queueServerWork(15, () -> {
			world.destroyBlock(pos, false);
			if (world instanceof Level level) {
				level.playSound(null, pos, SoundEvents.ENDER_CHEST_CLOSE, SoundSource.BLOCKS, 1, 1);
			}
			if (world instanceof ServerLevel level) {
				Entity entityToSpawn = CAEntities.MEGA_CHEST.get().spawn(level, BlockPos.containing(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5), MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn != null) {
					switch (facing) {
						case NORTH -> {
							entityToSpawn.setYRot(-180);
							entityToSpawn.setYBodyRot(-180);
							entityToSpawn.setYHeadRot(-180);
						}
						case WEST -> {
							entityToSpawn.setYRot(90);
							entityToSpawn.setYBodyRot(90);
							entityToSpawn.setYHeadRot(90);
						}
						case EAST -> {
							entityToSpawn.setYRot(-90);
							entityToSpawn.setYBodyRot(-90);
							entityToSpawn.setYHeadRot(-90);
						}
						default -> {
						}
					}
				}
			}
		});

		return InteractionResult.SUCCESS;
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return MapCodec.unit(this);
	}
}