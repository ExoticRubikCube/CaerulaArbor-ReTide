package com.apocalypse.caerulaarbor.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CaerulaArborModBlocks;
import com.apocalypse.caerulaarbor.utils.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
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
import net.minecraft.world.level.block.state.properties.*;
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
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
        double expand = 0;
        for (Direction directioniterator : Direction.Plane.HORIZONTAL) {
            if ((((LevelAccessor) world).getBlockState(BlockPos.containing((double) x + directioniterator.getStepX(), y, (double) z + directioniterator.getStepZ()))).is(BlockTags.create(new ResourceLocation(CaerulaArborMod.MODID, "trail")))) {
                expand = 1;
            }
        }
        {
            int _value = (int) ((blockstate.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty _getip8 ? blockstate.getValue(_getip8) : -1) + expand);
            BlockPos _pos = BlockPos.containing(x, y, z);
            BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
            if (_bs.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
                ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
        }
        if ((blockstate.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty _getip11 ? blockstate.getValue(_getip11) : -1) > 29) {
            ((LevelAccessor) world).setBlock(BlockPos.containing(x, y, z), ((new Object() {
                public BlockState with(BlockState _bs, String _property, int _newValue) {
                    Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty(_property);
                    return _prop instanceof IntegerProperty _ip && _prop.getPossibleValues().contains(_newValue) ? _bs.setValue(_ip, _newValue) : _bs;
                }
            }.with((new Object() {
                public BlockState with(BlockState _bs, Direction newValue) {
                    Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty("facing");
                    if (_prop instanceof DirectionProperty _dp && _dp.getPossibleValues().contains(newValue))
                        return _bs.setValue(_dp, newValue);
                    _prop = _bs.getBlock().getStateDefinition().getProperty("axis");
                    return _prop instanceof EnumProperty _ep && _ep.getPossibleValues().contains(newValue.getAxis()) ? _bs.setValue(_ep, newValue.getAxis()) : _bs;
                }
            }.with(CaerulaArborModBlocks.SEA_TRAIL_GROWN.get().defaultBlockState(), new Object() {
                public Direction getValue() {
                    Direction _dir = Direction.NORTH;
                    int _num = Mth.nextInt(RandomSource.create(), 1, 4);
                    if (_num == 1) {
                        _dir = Direction.EAST;
                    } else if (_num == 2) {
                        _dir = Direction.SOUTH;
                    } else if (_num == 3) {
                        _dir = Direction.WEST;
                    }
                    return _dir;
                }
            }.getValue())), "longevity", (int) ((blockstate.getBlock().getStateDefinition().getProperty("longevity") instanceof IntegerProperty _getip15 ? blockstate.getValue(_getip15) : -1) - 1))).getBlock().getStateDefinition()
                    .getProperty("waterlogged") instanceof BooleanProperty _withbp19 ? (new Object() {
                        public BlockState with(BlockState _bs, String _property, int _newValue) {
                            Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty(_property);
                            return _prop instanceof IntegerProperty _ip && _prop.getPossibleValues().contains(_newValue) ? _bs.setValue(_ip, _newValue) : _bs;
                        }
                    }.with((new Object() {
                        public BlockState with(BlockState _bs, Direction newValue) {
                            Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty("facing");
                            if (_prop instanceof DirectionProperty _dp && _dp.getPossibleValues().contains(newValue))
                                return _bs.setValue(_dp, newValue);
                            _prop = _bs.getBlock().getStateDefinition().getProperty("axis");
                            return _prop instanceof EnumProperty _ep && _ep.getPossibleValues().contains(newValue.getAxis()) ? _bs.setValue(_ep, newValue.getAxis()) : _bs;
                        }
                    }.with(CaerulaArborModBlocks.SEA_TRAIL_GROWN.get().defaultBlockState(), new Object() {
                        public Direction getValue() {
                            Direction _dir = Direction.NORTH;
                            int _num = Mth.nextInt(RandomSource.create(), 1, 4);
                            if (_num == 1) {
                                _dir = Direction.EAST;
                            } else if (_num == 2) {
                                _dir = Direction.SOUTH;
                            } else if (_num == 3) {
                                _dir = Direction.WEST;
                            }
                            return _dir;
                        }
                    }.getValue())), "longevity", (int) ((blockstate.getBlock().getStateDefinition().getProperty("longevity") instanceof IntegerProperty _getip15 ? blockstate.getValue(_getip15) : -1) - 1))).setValue(_withbp19,
                            (blockstate.getBlock().getStateDefinition().getProperty("waterlogged") instanceof BooleanProperty _getbp18 && blockstate.getValue(_getbp18))) : (new Object() {
                                public BlockState with(BlockState _bs, String _property, int _newValue) {
                                    Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty(_property);
                                    return _prop instanceof IntegerProperty _ip && _prop.getPossibleValues().contains(_newValue) ? _bs.setValue(_ip, _newValue) : _bs;
                                }
                            }.with((new Object() {
                                public BlockState with(BlockState _bs, Direction newValue) {
                                    Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty("facing");
                                    if (_prop instanceof DirectionProperty _dp && _dp.getPossibleValues().contains(newValue))
                                        return _bs.setValue(_dp, newValue);
                                    _prop = _bs.getBlock().getStateDefinition().getProperty("axis");
                                    return _prop instanceof EnumProperty _ep && _ep.getPossibleValues().contains(newValue.getAxis()) ? _bs.setValue(_ep, newValue.getAxis()) : _bs;
                                }
                            }.with(CaerulaArborModBlocks.SEA_TRAIL_GROWN.get().defaultBlockState(), new Object() {
                                public Direction getValue() {
                                    Direction _dir = Direction.NORTH;
                                    int _num = Mth.nextInt(RandomSource.create(), 1, 4);
                                    if (_num == 1) {
                                        _dir = Direction.EAST;
                                    } else if (_num == 2) {
                                        _dir = Direction.SOUTH;
                                    } else if (_num == 3) {
                                        _dir = Direction.WEST;
                                    }
                                    return _dir;
                                }
                            }.getValue())), "longevity", (int) ((blockstate.getBlock().getStateDefinition().getProperty("longevity") instanceof IntegerProperty _getip15 ? blockstate.getValue(_getip15) : -1) - 1)))),
                    3);
            if ((LevelAccessor) world instanceof Level _level) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.sculk_vein.step")), SoundSource.NEUTRAL, 1, 1);
            }
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
		WorldUtils.addGrowAge(world, pos, blockstate);
	}
}
