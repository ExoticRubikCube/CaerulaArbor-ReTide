package com.susen36.caerulaarbor.block;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.init.CABlocks;
import com.susen36.caerulaarbor.init.CAGameRules;
import com.susen36.caerulaarbor.manager.upgrade.SilenceUpgradeManager;
import com.susen36.caerulaarbor.util.CaerulaUtil;
import com.susen36.caerulaarbor.util.EntityUtils;
import com.susen36.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SeaTrailGrownBlock extends Block implements SimpleWaterloggedBlock, BonemealableBlock {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final IntegerProperty GROW_AGE = IntegerProperty.create("grow_age", 0, 64);
	public static final IntegerProperty LONGEVITY = IntegerProperty.create("longevity", 0, 16);
	private static final TagKey<Block> CANNOT_COVER = BlockTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "cannot_cover"));
	private static final TagKey<Block> ERRODABLE = BlockTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "errodable"));
	private static final TagKey<Block> FORGE_STONE = BlockTags.create(ResourceLocation.parse("forge:stone"));

	public SeaTrailGrownBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).sound(SoundType.SCULK_VEIN).strength(4f, 8f).lightLevel(s -> 4).requiresCorrectToolForDrops().friction(0.4f).speedFactor(0.7f).jumpFactor(0.875f).noOcclusion()
				.pushReaction(PushReaction.DESTROY).isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(GROW_AGE, 0).setValue(LONGEVITY, 16).setValue(WATERLOGGED, false));
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
		return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(GROW_AGE, 0).setValue(LONGEVITY, 16).setValue(WATERLOGGED, flag);
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
		return 8;
	}

	@Override
	public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
		super.onPlace(blockstate, world, pos, oldState, moving);
		world.scheduleTick(pos, this, 25);
	}

	@Override
	public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
		super.tick(blockstate, world, pos, random);
		int growAge = blockstate.getValue(GROW_AGE);
		int longevity = blockstate.getValue(LONGEVITY);
		boolean waterlogged = blockstate.getValue(WATERLOGGED);
		if (growAge < 62) {
			int expand = 1;
			if (world.getLevelData().isThundering()) {
				expand = 2;
			}
			if (world.getLevelData().isRaining() && random.nextFloat() < 0.5F) {
				expand = 2;
			}
			if (SilenceUpgradeManager.isSilence(world)) {
				expand = 3;
			}
			double strategyGrow = MapVariables.get(world).strategy_grow;
			int boostedExpand = Math.max(1, (int) Math.round(expand * (1.0 + 0.10 * strategyGrow)));
			boolean valid = !this.hasLargeLivingEntityNearby(world, pos);
			float effectiveSpreadRate = world.getGameRules().getInt(CAGameRules.SPREAD_RATE) * 0.9f * (1.0f + 0.10f * (float) strategyGrow);
			if (valid && growAge > 29 && longevity > 0 && random.nextFloat() * 100.0F < effectiveSpreadRate) {
				if (SilenceUpgradeManager.isSilence(world)) {
					expand = 1;
				}
				boostedExpand = Math.max(1, (int) Math.round(expand * (1.0 + 0.10 * strategyGrow)));
				if (random.nextFloat() < 0.2F) {
					Direction spreadDirection = this.getRandomHorizontalDirection(random);
					BlockPos targetPos = pos.relative(spreadDirection);
					BlockState targetState = world.getBlockState(targetPos);
					BlockState belowState = world.getBlockState(pos.below());
					int spreadLongevity = random.nextFloat() < 0.33F ? longevity - 1 : longevity;
					if (targetState.is(BlockTags.LOGS) && belowState.getBlock() != CABlocks.TRAIL_LOG.get() && belowState.getBlock() != CABlocks.STRIPPED_TRAIL_LOG.get()) {
						world.setBlock(targetPos, CABlocks.TRAIL_LOG.get().withPropertiesOf(targetState), 3);
					} else if (targetState.is(BlockTags.LEAVES) && targetState.getBlock() != CABlocks.TRAIL_LEAVE.get()) {
						world.setBlock(targetPos, CABlocks.TRAIL_LEAVE.get().defaultBlockState(), 3);
						world.levelEvent(2001, targetPos, getId(CABlocks.SEA_TRAIL_INIT.get().defaultBlockState()));
						world.playSound(null, pos, SoundEvents.SCULK_VEIN_PLACE, SoundSource.NEUTRAL, 1.0F, 1.0F);
					} else if (WorldUtils.isOrganic(targetState) && targetState.getBlock() != CABlocks.TRAIL_PULSE.get()) {
						world.setBlock(targetPos, CABlocks.TRAIL_PULSE.get().defaultBlockState(), 3);
						world.levelEvent(2001, targetPos, getId(CABlocks.TRAIL_PULSE.get().defaultBlockState()));
						world.playSound(null, pos, SoundEvents.SCULK_VEIN_PLACE, SoundSource.NEUTRAL, 1.0F, 1.0F);
					} else {
						BlockState blockToPlace = CABlocks.SEA_TRAIL_INIT.get().defaultBlockState().setValue(SeaTrailInitBlock.LONGEVITY, spreadLongevity);
						boolean watered = false;
						for (int index = 0; index < 3; index++) {
							BlockPos placePos = targetPos.below().above(index);
							if (!(world.getBlockFloorHeight(placePos) > 0) && !world.getBlockState(placePos).is(CANNOT_COVER) && CABlocks.SEA_TRAIL_INIT.get().defaultBlockState().canSurvive(world, placePos)) {
								Block fluidBlock = world.getFluidState(placePos).createLegacyBlock().getBlock();
								if (fluidBlock == Blocks.WATER || fluidBlock == Blocks.BUBBLE_COLUMN) {
									watered = true;
								}
								CaerulaUtil.replaceTrail(world, blockToPlace, watered, placePos.getX(), placePos.getY(), placePos.getZ());
							}
						}
					}
				}
			}
			int nextGrowAge = growAge + boostedExpand;
			if (nextGrowAge <= 64) {
				world.setBlock(pos, blockstate.setValue(GROW_AGE, nextGrowAge), 3);
			}
		} else {
			BlockPos belowPos = pos.below();
			BlockState targetState = world.getBlockState(belowPos);
			Direction targetDirection = this.getDirection(targetState);
			boolean change = false;
			if (random.nextFloat() < 0.2F && !targetState.is(CANNOT_COVER)) {
				if (targetState.is(BlockTags.LOGS)) {
					world.destroyBlock(pos, false);
					world.setBlock(belowPos, this.withFacing(CABlocks.TRAIL_LOG.get().defaultBlockState(), targetDirection), 3);
					change = true;
				} else if (targetState.getBlock() == Blocks.SOUL_SAND || targetState.getBlock() == Blocks.SOUL_SOIL) {
					world.destroyBlock(pos, false);
					world.setBlock(belowPos, CABlocks.NETHERSEA_SOUL_SAND.get().defaultBlockState(), 3);
					change = true;
				} else if (targetState.is(BlockTags.BASE_STONE_OVERWORLD) || targetState.is(FORGE_STONE)) {
					world.destroyBlock(pos, false);
					world.setBlock(belowPos, CABlocks.TRAIL_STONE.get().defaultBlockState(), 3);
					change = true;
				} else if (targetState.is(BlockTags.PLANKS)) {
					world.destroyBlock(pos, false);
					world.setBlock(belowPos, this.withFacing(CABlocks.TRAIL_PLANK.get().defaultBlockState(), targetDirection), 3);
					change = true;
				} else if (targetState.getBlock() == Blocks.CARVED_PUMPKIN || targetState.getBlock() == Blocks.JACK_O_LANTERN) {
					world.destroyBlock(pos, false);
					world.setBlock(belowPos, this.withFacing(CABlocks.TRAIL_PUMPKING.get().defaultBlockState(), targetDirection), 3);
					change = true;
				} else if (targetState.is(BlockTags.LEAVES)) {
					world.destroyBlock(pos, false);
					world.setBlock(belowPos, CABlocks.TRAIL_LEAVE.get().defaultBlockState(), 3);
					change = true;
				} else if (targetState.getBlock() == Blocks.ANCIENT_DEBRIS) {
					world.destroyBlock(pos, false);
					world.setBlock(belowPos, this.withFacing(CABlocks.TRAIL_DEBRIS.get().defaultBlockState(), targetDirection), 3);
					change = true;
				} else if (WorldUtils.isOrganic(targetState)) {
					world.destroyBlock(pos, false);
					world.setBlock(belowPos, this.withFacing(CABlocks.TRAIL_PULSE.get().defaultBlockState(), this.getRandomHorizontalDirection(random)), 3);
					change = true;
				} else if (targetState.is(ERRODABLE)) {
					world.destroyBlock(pos, false);
					world.setBlock(belowPos, this.withFacing(CABlocks.SEA_TRAIL_SOLID.get().defaultBlockState(), this.getRandomHorizontalDirection(random)), 3);
					change = true;
				}
				if (change) {
					world.playSound(null, pos, SoundEvents.SCULK_VEIN_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
				}
				float rand = random.nextFloat();
				BlockState blockToPlace;
				if (rand < 0.02F) {
					blockToPlace = random.nextFloat() < 0.12F ? CABlocks.RED_OVARY.get().defaultBlockState() : CABlocks.OCEAN_OVARY.get().defaultBlockState();
				} else if (rand < 0.1F) {
					blockToPlace = waterlogged ? CABlocks.DEEP_SEAGRASS.get().defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, true) : CABlocks.TRAIL_MUSHROOM.get().defaultBlockState();
				} else if (rand < 0.013F) {
					blockToPlace = CABlocks.VIVIPAROUS_LILY.get().defaultBlockState();
				} else {
					blockToPlace = CABlocks.SEA_TRAIL_STOP.get().defaultBlockState();
					if (blockToPlace.hasProperty(WATERLOGGED)) {
						blockToPlace = blockToPlace.setValue(WATERLOGGED, waterlogged);
					}
				}
				if (blockToPlace.getBlock() != Blocks.AIR && blockToPlace.canSurvive(world, pos)) {
					world.setBlock(pos, blockToPlace, 3);
					if (blockToPlace.getBlock() == CABlocks.DEEP_SEAGRASS.get() && random.nextFloat() < 0.5F && world.getFluidState(pos.above()).createLegacyBlock().getBlock() == Blocks.WATER) {
						world.setBlock(pos.above(), CABlocks.DEEP_SEAGRASS.get().defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, true), 3);
					}
				}
			}
		}
		world.scheduleTick(pos, this, 25);
	}

	private boolean hasLargeLivingEntityNearby(ServerLevel world, BlockPos pos) {
		Vec3 center = new Vec3(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
		for (Entity entity : world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(0.3D), candidate -> true)) {
			if (entity instanceof LivingEntity living && living.getMaxHealth() > 5.0F) {
				return true;
			}
		}
		return false;
	}

	private Direction getRandomHorizontalDirection(RandomSource random) {
		return switch (random.nextInt(4)) {
			case 0 -> Direction.EAST;
			case 1 -> Direction.SOUTH;
			case 2 -> Direction.WEST;
			default -> Direction.NORTH;
		};
	}

	private Direction getDirection(BlockState blockState) {
		if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
			return blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
		}
		if (blockState.hasProperty(BlockStateProperties.FACING)) {
			return blockState.getValue(BlockStateProperties.FACING);
		}
		if (blockState.hasProperty(BlockStateProperties.AXIS)) {
			return Direction.fromAxisAndDirection(blockState.getValue(BlockStateProperties.AXIS), Direction.AxisDirection.POSITIVE);
		}
		if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
			return Direction.fromAxisAndDirection(blockState.getValue(BlockStateProperties.HORIZONTAL_AXIS), Direction.AxisDirection.POSITIVE);
		}
		return Direction.NORTH;
	}

	private BlockState withFacing(BlockState blockState, Direction direction) {
		if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
			return blockState.setValue(BlockStateProperties.HORIZONTAL_FACING, direction);
		}
		if (blockState.hasProperty(BlockStateProperties.FACING)) {
			return blockState.setValue(BlockStateProperties.FACING, direction);
		}
		if (blockState.hasProperty(BlockStateProperties.AXIS)) {
			return blockState.setValue(BlockStateProperties.AXIS, direction.getAxis());
		}
		if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
			return blockState.setValue(BlockStateProperties.HORIZONTAL_AXIS, direction.getAxis());
		}
		return blockState;
	}

	@Override
	public boolean onDestroyedByPlayer(BlockState blockstate, Level world, BlockPos pos, Player entity, boolean willHarvest, FluidState fluid) {
		boolean retval = super.onDestroyedByPlayer(blockstate, world, pos, entity, willHarvest, fluid);
		CaerulaUtil.pokePlayer(world, pos.getX(), pos.getY(), pos.getZ(), entity);
		return retval;
	}

	@Override
	public void entityInside(BlockState blockstate, Level world, BlockPos pos, Entity entity) {
		super.entityInside(blockstate, world, pos, entity);
		EntityUtils.damagedByNethseabrand(world, entity);
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
		WorldUtils.addGrowAge(world, pos);
	}
}