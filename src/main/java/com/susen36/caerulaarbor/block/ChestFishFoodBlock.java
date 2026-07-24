package com.susen36.caerulaarbor.block;

import com.susen36.caerulaarbor.init.CABlocks;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
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

public class ChestFishFoodBlock extends Block implements SimpleWaterloggedBlock {
	public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 2);
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public ChestFishFoodBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(2.0f, 10.0f).noOcclusion().isRedstoneConductor((bs, br, bp) -> false));
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
		return switch (state.getValue(FACING)) {
			case NORTH -> box(1, 0, 1, 15, 11, 15);
			case EAST -> box(1, 0, 1, 15, 11, 15);
			case WEST -> box(1, 0, 1, 15, 11, 15);
			default -> box(1, 0, 1, 15, 11, 15);
		};
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, WATERLOGGED, BLOCKSTATE);
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
			world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay((LevelReader) world));
		}
		return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public boolean onDestroyedByPlayer(BlockState blockstate, Level world, BlockPos pos, Player entity, boolean willHarvest, FluidState fluid) {
		double x = pos.getX();
		double y = pos.getY();
		double z = pos.getZ();
		if (entity != null) {
			int bs = -1;
			IntegerProperty blockstateProp = (IntegerProperty) blockstate.getBlock().getStateDefinition().getProperty("blockstate");
			if (blockstateProp != null) {
				bs = blockstate.getValue(blockstateProp);
			}
			if (!((Entity) entity instanceof Player player) || !player.getAbilities().instabuild) {
				if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
					if (bs == 0) {
						if ((LevelAccessor) world instanceof ServerLevel level) {
							ItemEntity entityToSpawn = new ItemEntity(level, x, y, z, new ItemStack(CABlocks.CHEST_FISH_FOOD.get()));
							entityToSpawn.setPickUpDelay(10);
							level.addFreshEntity(entityToSpawn);
						}
					} else if (bs == 1) {
						for (int i = 0; i < Mth.nextInt(RandomSource.create(), 3, 4); ++i) {
							if (!((LevelAccessor) world instanceof ServerLevel level)) {
								continue;
							}
							ItemEntity entityToSpawn = new ItemEntity(level, x + 0.5, y + 0.75, z + 0.5, new ItemStack(CAItems.OCEAN_FIBRE.get()));
							entityToSpawn.setPickUpDelay(5);
							level.addFreshEntity(entityToSpawn);
						}
						for (int i = 0; i < Mth.nextInt(RandomSource.create(), 2, 4); ++i) {
							if (!((LevelAccessor) world instanceof ServerLevel level)) {
								continue;
							}
							ItemEntity entityToSpawn = new ItemEntity(level, x + 0.5, y + 0.75, z + 0.5, new ItemStack(CAItems.CHITIN_COOKIE_RAW.get()));
							entityToSpawn.setPickUpDelay(5);
							level.addFreshEntity(entityToSpawn);
						}
					} else if (bs == 2) {
						for (int i = 0; i < Mth.nextInt(RandomSource.create(), 2, 4); ++i) {
							if (!((LevelAccessor) world instanceof ServerLevel level)) {
								continue;
							}
							ItemEntity entityToSpawn = new ItemEntity(level, x + 0.5, y + 0.75, z + 0.5, new ItemStack(CAItems.CHITIN_COOKIE_RAW.get()));
							entityToSpawn.setPickUpDelay(5);
							level.addFreshEntity(entityToSpawn);
						}
					}
				}
			}
		}
		return super.onDestroyedByPlayer(blockstate, world, pos, entity, willHarvest, fluid);
	}

	@Override
	public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
		super.use(blockstate, world, pos, entity, hand, hit);
		double x = pos.getX();
		double y = pos.getY();
		double z = pos.getZ();
		if (entity == null) {
			return InteractionResult.PASS;
		}
		int bs = -1;
		IntegerProperty blockstateProp = (IntegerProperty) blockstate.getBlock().getStateDefinition().getProperty("blockstate");
		if (blockstateProp != null) {
			bs = blockstate.getValue(blockstateProp);
		}
		ItemStack mainHand = (Entity) entity instanceof LivingEntity living ? living.getMainHandItem() : ItemStack.EMPTY;
		if (mainHand.getItem() == Blocks.AIR.asItem()) {
			ItemStack offHand = (Entity) entity instanceof LivingEntity living ? living.getOffhandItem() : ItemStack.EMPTY;
			if (offHand.getItem() == Blocks.AIR.asItem()) {
				if (bs == 0) {
					for (int i = 0; i < Mth.nextInt(RandomSource.create(), 3, 4); ++i) {
						if (!((LevelAccessor) world instanceof ServerLevel level)) {
							continue;
						}
						ItemEntity entityToSpawn = new ItemEntity(level, x + 0.5, y + 0.75, z + 0.5, new ItemStack(CAItems.OCEAN_PEDUNCLE.get()));
						entityToSpawn.setPickUpDelay(5);
						level.addFreshEntity(entityToSpawn);
					}
					BlockPos pos1 = BlockPos.containing(x, y, z);
					BlockState currentState = world.getBlockState(pos1);
					IntegerProperty prop = (IntegerProperty) currentState.getBlock().getStateDefinition().getProperty("blockstate");
					if (prop != null && prop.getPossibleValues().contains(1)) {
						world.setBlock(pos1, currentState.setValue(prop, 1), 3);
					}
				} else if (bs == 1) {
					for (int i = 0; i < Mth.nextInt(RandomSource.create(), 3, 4); ++i) {
						if (!((LevelAccessor) world instanceof ServerLevel level)) {
							continue;
						}
						ItemEntity entityToSpawn = new ItemEntity(level, x + 0.5, y + 0.75, z + 0.5, new ItemStack(CAItems.OCEAN_FIBRE.get()));
						entityToSpawn.setPickUpDelay(5);
						level.addFreshEntity(entityToSpawn);
					}
					BlockPos pos1 = BlockPos.containing(x, y, z);
					BlockState currentState = world.getBlockState(pos1);
					IntegerProperty prop = (IntegerProperty) currentState.getBlock().getStateDefinition().getProperty("blockstate");
					if (prop != null && prop.getPossibleValues().contains(2)) {
						world.setBlock(pos1, currentState.setValue(prop, 2), 3);
					}
				} else if (bs == 2) {
					for (int i = 0; i < Mth.nextInt(RandomSource.create(), 2, 4); ++i) {
						if (!((LevelAccessor) world instanceof ServerLevel level)) {
							continue;
						}
						ItemEntity entityToSpawn = new ItemEntity(level, x + 0.5, y + 0.75, z + 0.5, new ItemStack(CAItems.CHITIN_COOKIE_RAW.get()));
						entityToSpawn.setPickUpDelay(5);
						level.addFreshEntity(entityToSpawn);
					}
					world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
				}
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}
}