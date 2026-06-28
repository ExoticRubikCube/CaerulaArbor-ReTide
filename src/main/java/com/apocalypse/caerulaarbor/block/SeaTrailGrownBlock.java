package com.apocalypse.caerulaarbor.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.init.CaerulaArborModBlocks;
import com.apocalypse.caerulaarbor.init.CaerulaArborModGameRules;
import com.apocalypse.caerulaarbor.procedures.PokePlayerProcedure;
import com.apocalypse.caerulaarbor.procedures.TrailReplaceProcedure;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import com.apocalypse.caerulaarbor.utils.StrategyUtils;
import com.apocalypse.caerulaarbor.utils.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
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
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class SeaTrailGrownBlock extends Block implements SimpleWaterloggedBlock, BonemealableBlock {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final IntegerProperty GROW_AGE = IntegerProperty.create("grow_age", 0, 64);
	public static final IntegerProperty LONGEVITY = IntegerProperty.create("longevity", 0, 16);

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
			default -> box(0, 0, 0, 16, 0.625, 16);
			case NORTH -> box(0, 0, 0, 16, 0.625, 16);
			case EAST -> box(0, 0, 0, 16, 0.625, 16);
			case WEST -> box(0, 0, 0, 16, 0.625, 16);
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
		WorldUtils.saveWaterloggedState(world, pos.getX(), pos.getY(), pos.getZ(), oldState);
	}

	@Override
	public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
		super.tick(blockstate, world, pos, random);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
        boolean valid = false;
        boolean change = false;
        boolean watered = false;
        double direc = 0;
        double dx = 0;
        double dz = 0;
        double longev = 0;
        double expand = 0;
        double rand = 0;
        BlockState blocktoplace = Blocks.AIR.defaultBlockState();
        BlockState targetB = Blocks.AIR.defaultBlockState();
        Direction dire = Direction.NORTH;
        if ((blockstate.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty _getip1 ? blockstate.getValue(_getip1) : -1) < 62) {
            expand = 1;
            if (((LevelAccessor) world).getLevelData().isThundering()) {
                expand = 2;
            }
            if (((LevelAccessor) world).getLevelData().isRaining() && Math.random() < 0.5) {
                expand = 2;
            }
            if (StrategyUtils.isSilence(world)) {
                expand = 3;
            }
            valid = true;
            {
                final Vec3 _center = new Vec3(((double) x + 0.5), ((double) y + 0.5), ((double) z + 0.5));
                List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(0.6 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                for (Entity entityiterator : _entfound) {
                    if ((entityiterator instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) > 5) {
                        valid = false;
                        break;
                    }
                }
            }
            if (valid && (blockstate.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty _getip7 ? blockstate.getValue(_getip7) : -1) > 29
                    && (blockstate.getBlock().getStateDefinition().getProperty("longevity") instanceof IntegerProperty _getip9 ? blockstate.getValue(_getip9) : -1) > 0) {
                if (Math.random() * 100 < (((LevelAccessor) world).getLevelData().getGameRules().getInt(CaerulaArborModGameRules.SPREAD_RATE))) {
                    if (StrategyUtils.isSilence(world)) {
                        expand = 1;
                    }
                    if (Math.random() < 0.2) {
                        dx = 0;
                        dx = 1;
                        direc = Mth.nextInt(RandomSource.create(), 0, 3);
                        if (direc == 0) {
                            dx = 0;
                            dz = 1;
                        } else if (direc == 1) {
                            dx = 0;
                            dz = -1;
                        } else if (direc == 2) {
                            dx = 1;
                            dz = 0;
                        } else {
                            dx = -1;
                            dz = 0;
                        }
                        longev = blockstate.getBlock().getStateDefinition().getProperty("longevity") instanceof IntegerProperty _getip13 ? blockstate.getValue(_getip13) : -1;
                        if (Math.random() < 0.33) {
                            longev = longev - 1;
                        }
                        if ((((LevelAccessor) world).getBlockState(BlockPos.containing((double) x + dx, y, (double) z + dz))).is(BlockTags.create(new ResourceLocation("minecraft:logs")))
                                && !((((LevelAccessor) world).getBlockState(BlockPos.containing(x, (double) y - 1, z))).getBlock() == CaerulaArborModBlocks.TRAIL_LOG.get()
                                        || (((LevelAccessor) world).getBlockState(BlockPos.containing(x, (double) y - 1, z))).getBlock() == CaerulaArborModBlocks.STRIPPED_TRAIL_LOG.get())) {
                            {
                                BlockPos _bp = BlockPos.containing((double) x + dx, y, (double) z + dz);
                                BlockState _bs = (new Object() {
                                    public BlockState with(BlockState _bs, Direction newValue) {
                                        Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty("facing");
                                        if (_prop instanceof DirectionProperty _dp && _dp.getPossibleValues().contains(newValue))
                                            return _bs.setValue(_dp, newValue);
                                        _prop = _bs.getBlock().getStateDefinition().getProperty("axis");
                                        return _prop instanceof EnumProperty _ep && _ep.getPossibleValues().contains(newValue.getAxis()) ? _bs.setValue(_ep, newValue.getAxis()) : _bs;
                                    }
                                }.with(CaerulaArborModBlocks.TRAIL_LOG.get().defaultBlockState(), (new Object() {
                                    public Direction getDirection(BlockState _bs) {
                                        Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty("facing");
                                        if (_prop instanceof DirectionProperty _dp)
                                            return _bs.getValue(_dp);
                                        _prop = _bs.getBlock().getStateDefinition().getProperty("axis");
                                        return _prop instanceof EnumProperty _ep && _ep.getPossibleValues().toArray()[0] instanceof Direction.Axis
                                                ? Direction.fromAxisAndDirection((Direction.Axis) _bs.getValue(_ep), Direction.AxisDirection.POSITIVE)
                                                : Direction.NORTH;
                                    }
                                }.getDirection((((LevelAccessor) world).getBlockState(BlockPos.containing((double) x + dx, y, (double) z + dz)))))));
                                BlockState _bso = ((LevelAccessor) world).getBlockState(_bp);
                                for (Map.Entry<Property<?>, Comparable<?>> entry : _bso.getValues().entrySet()) {
                                    Property _property = _bs.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
                                    if (_property != null && _bs.getValue(_property) != null)
                                        try {
                                            _bs = _bs.setValue(_property, (Comparable) entry.getValue());
                                        } catch (Exception e) {
                                        }
                                }
                                ((LevelAccessor) world).setBlock(_bp, _bs, 3);
                            }
                        } else if ((((LevelAccessor) world).getBlockState(BlockPos.containing((double) x + dx, y, (double) z + dz))).is(BlockTags.create(new ResourceLocation("minecraft:leaves")))
                                && !((((LevelAccessor) world).getBlockState(BlockPos.containing((double) x + dx, y, (double) z + dz))).getBlock() == CaerulaArborModBlocks.TRAIL_LEAVE.get())) {
                            ((LevelAccessor) world).setBlock(BlockPos.containing((double) x + dx, y, (double) z + dz),
                                    (CaerulaArborModBlocks.TRAIL_LEAVE.get().getStateDefinition().getProperty("waterlogged") instanceof BooleanProperty _withbp28
                                            ? CaerulaArborModBlocks.TRAIL_LEAVE.get().defaultBlockState().setValue(_withbp28, false)
                                            : CaerulaArborModBlocks.TRAIL_LEAVE.get().defaultBlockState()),
                                    3);
                            world.levelEvent(2001, BlockPos.containing((double) x + dx, y, (double) z + dz), getId(CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState()));
                            if ((LevelAccessor) world instanceof Level _level) {
                                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.sculk_vein.place")), SoundSource.NEUTRAL, 1, 1);
                            }
                        } else if (WorldUtils.isOrganic(((LevelAccessor) world).getBlockState(BlockPos.containing((double) x + dx, y, (double) z + dz))) && !((((LevelAccessor) world).getBlockState(BlockPos.containing((double) x + dx, y, (double) z + dz))).getBlock() == CaerulaArborModBlocks.TRAIL_PULSE.get())) {
                            ((LevelAccessor) world).setBlock(BlockPos.containing((double) x + dx, y, (double) z + dz), CaerulaArborModBlocks.TRAIL_PULSE.get().defaultBlockState(), 3);
                            world.levelEvent(2001, BlockPos.containing((double) x + dx, y, (double) z + dz), getId(CaerulaArborModBlocks.TRAIL_PULSE.get().defaultBlockState()));
                            if ((LevelAccessor) world instanceof Level _level) {
                                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.sculk_vein.place")), SoundSource.NEUTRAL, 1, 1);
                            }
                        } else {
                            blocktoplace = (new Object() {
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
                            }.with(CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState(), new Object() {
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
                            }.getValue())), "longevity", (int) longev));
                            for (int index0 = 0; index0 < 3; index0++) {
                                if (!(world.getBlockFloorHeight(BlockPos.containing((double) x + dx, (double) y - 1 + index0, (double) z + dz)) > 0)
                                        && !(((LevelAccessor) world).getBlockState(BlockPos.containing((double) x + dx, (double) y - 1 + index0, (double) z + dz))).is(BlockTags.create(new ResourceLocation(CaerulaArborMod.MODID, "cannot_cover")))
                                        && CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState().canSurvive(world, BlockPos.containing((double) x + dx, (double) y - 1 + index0, (double) z + dz))) {
                                    if ((((LevelAccessor) world).getFluidState(BlockPos.containing((double) x + dx, (double) y - 1 + index0, (double) z + dz)).createLegacyBlock()).getBlock() == Blocks.WATER
                                            || (((LevelAccessor) world).getFluidState(BlockPos.containing((double) x + dx, (double) y - 1 + index0, (double) z + dz)).createLegacyBlock()).getBlock() == Blocks.BUBBLE_COLUMN) {
                                        watered = true;
                                    }
                                    TrailReplaceProcedure.execute(world, blocktoplace, watered, (double) x + dx, (double) y - 1 + index0, (double) z + dz);
                                }
                            }
                        }
                    }
                }
            }
            {
                int _value = (int) ((blockstate.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty _getip50 ? blockstate.getValue(_getip50) : -1) + expand);
                BlockPos _pos = BlockPos.containing(x, y, z);
                BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                if (_bs.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
                    ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
            }
        } else {
            targetB = (((LevelAccessor) world).getBlockState(BlockPos.containing(x, (double) y - 1, z)));
            dire = new Object() {
                public Direction getDirection(BlockPos pos1) {
                    BlockState _bs = ((LevelAccessor) world).getBlockState(pos1);
                    Property<?> property = _bs.getBlock().getStateDefinition().getProperty("facing");
                    if (property != null && _bs.getValue(property) instanceof Direction _dir)
                        return _dir;
                    else if (_bs.hasProperty(BlockStateProperties.AXIS))
                        return Direction.fromAxisAndDirection(_bs.getValue(BlockStateProperties.AXIS), Direction.AxisDirection.POSITIVE);
                    else if (_bs.hasProperty(BlockStateProperties.HORIZONTAL_AXIS))
                        return Direction.fromAxisAndDirection(_bs.getValue(BlockStateProperties.HORIZONTAL_AXIS), Direction.AxisDirection.POSITIVE);
                    return Direction.NORTH;
                }
            }.getDirection(BlockPos.containing(x, (double) y - 1, z));
            if (Math.random() < 0.2 && !targetB.is(BlockTags.create(new ResourceLocation(CaerulaArborMod.MODID, "cannot_cover")))) {
                if (targetB.is(BlockTags.create(new ResourceLocation("minecraft:logs")))) {
                    world.destroyBlock(BlockPos.containing(x, y, z), false);
                    ((LevelAccessor) world).setBlock(BlockPos.containing(x, (double) y - 1, z), (new Object() {
                        public BlockState with(BlockState _bs, Direction newValue) {
                            Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty("facing");
                            if (_prop instanceof DirectionProperty _dp && _dp.getPossibleValues().contains(newValue))
                                return _bs.setValue(_dp, newValue);
                            _prop = _bs.getBlock().getStateDefinition().getProperty("axis");
                            return _prop instanceof EnumProperty _ep && _ep.getPossibleValues().contains(newValue.getAxis()) ? _bs.setValue(_ep, newValue.getAxis()) : _bs;
                        }
                    }.with(CaerulaArborModBlocks.TRAIL_LOG.get().defaultBlockState(), dire)), 3);
                    change = true;
                } else if (targetB.getBlock() == Blocks.SOUL_SAND || targetB.getBlock() == Blocks.SOUL_SOIL) {
                    world.destroyBlock(BlockPos.containing(x, y, z), false);
                    ((LevelAccessor) world).setBlock(BlockPos.containing(x, (double) y - 1, z), CaerulaArborModBlocks.NETHERSEA_SOUL_SAND.get().defaultBlockState(), 3);
                    change = true;
                } else if (targetB.is(BlockTags.create(new ResourceLocation("minecraft:base_stone_overworld"))) || targetB.is(BlockTags.create(new ResourceLocation("forge:stone")))) {
                    world.destroyBlock(BlockPos.containing(x, y, z), false);
                    ((LevelAccessor) world).setBlock(BlockPos.containing(x, (double) y - 1, z), CaerulaArborModBlocks.TRAIL_STONE.get().defaultBlockState(), 3);
                    change = true;
                } else if (targetB.is(BlockTags.create(new ResourceLocation("minecraft:planks")))) {
                    world.destroyBlock(BlockPos.containing(x, y, z), false);
                    ((LevelAccessor) world).setBlock(BlockPos.containing(x, (double) y - 1, z), (new Object() {
                        public BlockState with(BlockState _bs, Direction newValue) {
                            Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty("facing");
                            if (_prop instanceof DirectionProperty _dp && _dp.getPossibleValues().contains(newValue))
                                return _bs.setValue(_dp, newValue);
                            _prop = _bs.getBlock().getStateDefinition().getProperty("axis");
                            return _prop instanceof EnumProperty _ep && _ep.getPossibleValues().contains(newValue.getAxis()) ? _bs.setValue(_ep, newValue.getAxis()) : _bs;
                        }
                    }.with(CaerulaArborModBlocks.TRAIL_PLANK.get().defaultBlockState(), dire)), 3);
                    change = true;
                } else if (targetB.getBlock() == Blocks.CARVED_PUMPKIN || targetB.getBlock() == Blocks.JACK_O_LANTERN) {
                    world.destroyBlock(BlockPos.containing(x, y, z), false);
                    ((LevelAccessor) world).setBlock(BlockPos.containing(x, (double) y - 1, z), (new Object() {
                        public BlockState with(BlockState _bs, Direction newValue) {
                            Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty("facing");
                            if (_prop instanceof DirectionProperty _dp && _dp.getPossibleValues().contains(newValue))
                                return _bs.setValue(_dp, newValue);
                            _prop = _bs.getBlock().getStateDefinition().getProperty("axis");
                            return _prop instanceof EnumProperty _ep && _ep.getPossibleValues().contains(newValue.getAxis()) ? _bs.setValue(_ep, newValue.getAxis()) : _bs;
                        }
                    }.with(CaerulaArborModBlocks.TRAIL_PUMPKING.get().defaultBlockState(), dire)), 3);
                    change = true;
                } else if (targetB.is(BlockTags.create(new ResourceLocation("minecraft:leaves")))) {
                    world.destroyBlock(BlockPos.containing(x, y, z), false);
                    ((LevelAccessor) world).setBlock(BlockPos.containing(x, (double) y - 1, z), (new Object() {
                        public BlockState with(BlockState _bs, Direction newValue) {
                            Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty("facing");
                            if (_prop instanceof DirectionProperty _dp && _dp.getPossibleValues().contains(newValue))
                                return _bs.setValue(_dp, newValue);
                            _prop = _bs.getBlock().getStateDefinition().getProperty("axis");
                            return _prop instanceof EnumProperty _ep && _ep.getPossibleValues().contains(newValue.getAxis()) ? _bs.setValue(_ep, newValue.getAxis()) : _bs;
                        }
                    }.with(CaerulaArborModBlocks.TRAIL_LEAVE.get().defaultBlockState(), dire)), 3);
                    change = true;
                } else if (targetB.getBlock() == Blocks.ANCIENT_DEBRIS) {
                    world.destroyBlock(BlockPos.containing(x, y, z), false);
                    ((LevelAccessor) world).setBlock(BlockPos.containing(x, (double) y - 1, z), (new Object() {
                        public BlockState with(BlockState _bs, Direction newValue) {
                            Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty("facing");
                            if (_prop instanceof DirectionProperty _dp && _dp.getPossibleValues().contains(newValue))
                                return _bs.setValue(_dp, newValue);
                            _prop = _bs.getBlock().getStateDefinition().getProperty("axis");
                            return _prop instanceof EnumProperty _ep && _ep.getPossibleValues().contains(newValue.getAxis()) ? _bs.setValue(_ep, newValue.getAxis()) : _bs;
                        }
                    }.with(CaerulaArborModBlocks.TRAIL_DEBRIS.get().defaultBlockState(), dire)), 3);
                    change = true;
                } else if (WorldUtils.isOrganic(targetB)) {
                    world.destroyBlock(BlockPos.containing(x, y, z), false);
                    ((LevelAccessor) world).setBlock(BlockPos.containing(x, (double) y - 1, z), (new Object() {
                        public BlockState with(BlockState _bs, Direction newValue) {
                            Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty("facing");
                            if (_prop instanceof DirectionProperty _dp && _dp.getPossibleValues().contains(newValue))
                                return _bs.setValue(_dp, newValue);
                            _prop = _bs.getBlock().getStateDefinition().getProperty("axis");
                            return _prop instanceof EnumProperty _ep && _ep.getPossibleValues().contains(newValue.getAxis()) ? _bs.setValue(_ep, newValue.getAxis()) : _bs;
                        }
                    }.with(CaerulaArborModBlocks.TRAIL_PULSE.get().defaultBlockState(), new Object() {
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
                    }.getValue())), 3);
                    change = true;
                } else if (targetB.is(BlockTags.create(new ResourceLocation(CaerulaArborMod.MODID, "errodable")))) {
                    world.destroyBlock(BlockPos.containing(x, y, z), false);
                    ((LevelAccessor) world).setBlock(BlockPos.containing(x, (double) y - 1, z), (new Object() {
                        public BlockState with(BlockState _bs, Direction newValue) {
                            Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty("facing");
                            if (_prop instanceof DirectionProperty _dp && _dp.getPossibleValues().contains(newValue))
                                return _bs.setValue(_dp, newValue);
                            _prop = _bs.getBlock().getStateDefinition().getProperty("axis");
                            return _prop instanceof EnumProperty _ep && _ep.getPossibleValues().contains(newValue.getAxis()) ? _bs.setValue(_ep, newValue.getAxis()) : _bs;
                        }
                    }.with(CaerulaArborModBlocks.SEA_TRAIL_SOLID.get().defaultBlockState(), new Object() {
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
                    }.getValue())), 3);
                    change = true;
                }
                if (change) {
                    if ((LevelAccessor) world instanceof Level _level) {
                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.sculk_vein.break")), SoundSource.BLOCKS, 1, 1);
                    }
                }
                blocktoplace = Blocks.AIR.defaultBlockState();
                rand = Math.random();
                if (rand < 0.02) {
                    if (Math.random() < 0.12) {
                        blocktoplace = CaerulaArborModBlocks.RED_OVARY.get().defaultBlockState();
                    } else {
                        blocktoplace = CaerulaArborModBlocks.OCEAN_OVARY.get().defaultBlockState();
                    }
                } else if (rand < 0.1) {
                    if (blockstate.getBlock().getStateDefinition().getProperty("waterlogged") instanceof BooleanProperty _getbp95 && blockstate.getValue(_getbp95)) {
                        blocktoplace = (CaerulaArborModBlocks.DEEP_SEAGRASS.get().getStateDefinition().getProperty("waterlogged") instanceof BooleanProperty _withbp96
                                ? CaerulaArborModBlocks.DEEP_SEAGRASS.get().defaultBlockState().setValue(_withbp96, true)
                                : CaerulaArborModBlocks.DEEP_SEAGRASS.get().defaultBlockState());
                    } else {
                        blocktoplace = CaerulaArborModBlocks.TRAIL_MUSHROOM.get().defaultBlockState();
                    }
                } else if (rand < 0.013) {
                    blocktoplace = CaerulaArborModBlocks.VIVIPAROUS_LILY.get().defaultBlockState();
                } else {
                    blocktoplace = CaerulaArborModBlocks.SEA_TRAIL_STOP.get().defaultBlockState();
                }
                if (!(blocktoplace.getBlock() == Blocks.AIR) && blocktoplace.canSurvive(world, BlockPos.containing(x, y, z))) {
                    ((LevelAccessor) world).setBlock(BlockPos.containing(x, y, z), blocktoplace, 3);
                    if (blocktoplace.getBlock() == CaerulaArborModBlocks.DEEP_SEAGRASS.get()) {
                        if (Math.random() < 0.5 && (((LevelAccessor) world).getFluidState(BlockPos.containing(x, (double) y + 1, z)).createLegacyBlock()).getBlock() == Blocks.WATER) {
                            ((LevelAccessor) world).setBlock(BlockPos.containing(x, (double) y + 1, z),
                                    (CaerulaArborModBlocks.DEEP_SEAGRASS.get().getStateDefinition().getProperty("waterlogged") instanceof BooleanProperty _withbp103
                                            ? CaerulaArborModBlocks.DEEP_SEAGRASS.get().defaultBlockState().setValue(_withbp103, true)
                                            : CaerulaArborModBlocks.DEEP_SEAGRASS.get().defaultBlockState()),
                                    3);
                        }
                    }
                }
            }
        }
        world.scheduleTick(pos, this, 25);
	}

	@Override
	public boolean onDestroyedByPlayer(BlockState blockstate, Level world, BlockPos pos, Player entity, boolean willHarvest, FluidState fluid) {
		boolean retval = super.onDestroyedByPlayer(blockstate, world, pos, entity, willHarvest, fluid);
		PokePlayerProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ(), entity);
		return retval;
	}

	@Override
	public void entityInside(BlockState blockstate, Level world, BlockPos pos, Entity entity) {
		super.entityInside(blockstate, world, pos, entity);
		EntityUtils.damagedByNethseabrand(world, entity);
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
