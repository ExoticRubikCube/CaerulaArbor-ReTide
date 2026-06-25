
package com.apocalypse.caerulaarbor.block;

import com.apocalypse.caerulaarbor.init.CaerulaArborModBlocks;
import com.apocalypse.caerulaarbor.procedures.PokePlayerProcedure;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import com.apocalypse.caerulaarbor.utils.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
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
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

public class TrailPulseBlock extends Block {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final IntegerProperty NURTR = IntegerProperty.create("nurtr", 0, 16);
	public static final IntegerProperty GROW_AGE = IntegerProperty.create("grow_age", 0, 24);

	public TrailPulseBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BLUE).sound(SoundType.SCULK).strength(3f, 4f).lightLevel(s -> 2).friction(0.7f).speedFactor(0.6f).jumpFactor(0.875f));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(NURTR, 16).setValue(GROW_AGE, 24));
	}

	@Override
	public float[] getBeaconColorMultiplier(BlockState state, LevelReader world, BlockPos pos, BlockPos beaconPos) {
		return new float[]{0.2470588235f, 0.3960784314f, 0.5764705882f};
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 6;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, NURTR, GROW_AGE);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(NURTR, 16).setValue(GROW_AGE, 24);
	}

	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public int getSignal(BlockState blockstate, BlockGetter blockAccess, BlockPos pos, Direction direction) {
		return 15;
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 12;
	}

	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction direction, IPlantable plantable) {
		return true;
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
		return true;
	}

	@Override
	public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
		super.onPlace(blockstate, world, pos, oldState, moving);
		world.scheduleTick(pos, this, 80);
	}

	@Override
	public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
		super.tick(blockstate, world, pos, random);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
        BlockState toPlace = Blocks.AIR.defaultBlockState();
        BlockState toReplace = Blocks.AIR.defaultBlockState();
        boolean put = false;
        double dltx = 0;
        double dltz = 0;
        for (Direction directioniterator : Direction.Plane.HORIZONTAL) {
            for (int dy = (int) 0; dy <= (int) 2; dy++) {
                for (int dist = (int) 1; dist <= (int) 2; dist++) {
                    if (Math.random() < 0.25) {
                        toReplace = (((LevelAccessor) world).getBlockState(BlockPos.containing((double) x + directioniterator.getStepX() * dist, (double) y + dy, (double) z + directioniterator.getStepZ() * dist)));
                        if (toReplace.canBeReplaced()
                                && CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState().canSurvive(world, BlockPos.containing((double) x + directioniterator.getStepX() * dist, (double) y + dy, (double) z + directioniterator.getStepZ() * dist))) {
                            if (toReplace.getBlock() == Blocks.WATER) {
                                toPlace = (CaerulaArborModBlocks.SEA_TRAIL_INIT.get().getStateDefinition().getProperty("waterlogged") instanceof BooleanProperty _withbp12
                                        ? CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState().setValue(_withbp12, true)
                                        : CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState());
                            } else {
                                toPlace = CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState();
                            }
                            {
                                BlockPos _bp = BlockPos.containing((double) x + directioniterator.getStepX() * dist, (double) y + dy, (double) z + directioniterator.getStepZ() * dist);
                                BlockState _bs = (new Object() {
                                    public BlockState with(BlockState _bs, Direction newValue) {
                                        Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty("facing");
                                        if (_prop instanceof DirectionProperty _dp && _dp.getPossibleValues().contains(newValue))
                                            return _bs.setValue(_dp, newValue);
                                        _prop = _bs.getBlock().getStateDefinition().getProperty("axis");
                                        return _prop instanceof EnumProperty _ep && _ep.getPossibleValues().contains(newValue.getAxis()) ? _bs.setValue(_ep, newValue.getAxis()) : _bs;
                                    }
                                }.with(toPlace, new Object() {
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
                                }.getValue()));
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
                            put = true;
                            break;
                        }
                    }
                }
                if (put) {
                    break;
                }
            }
            if (put) {
                break;
            }
            if (!put) {
                for (int dist = (int) 1; dist <= (int) 2; dist++) {
                    if (Math.random() < 0.25 && canDropTrail(world, (double) x + directioniterator.getStepX() * dist, (double) y - 1, (double) z + directioniterator.getStepZ() * dist)) {
                        if ((LevelAccessor) world instanceof ServerLevel _level)
                            FallingBlockEntity.fall(_level, BlockPos.containing((double) x + directioniterator.getStepX() * dist, (double) y - 1, (double) z + directioniterator.getStepZ() * dist), CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState());
                        put = true;
                        break;
                    }
                }
            }
        }
        if (!put) {
            dltx = 1;
            dltz = 1;
            for (int dy = (int) 0; dy >= (int) 2; dy--) {
                if (Math.random() < 0.25) {
                    toReplace = (((LevelAccessor) world).getBlockState(BlockPos.containing((double) x + dltx, (double) y + dy, (double) z + dltz)));
                    if (toReplace.canBeReplaced() && CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState().canSurvive(world, BlockPos.containing((double) x + dltx, (double) y + dy, (double) z + dltz))) {
                        if (toReplace.getBlock() == Blocks.WATER) {
                            toPlace = (CaerulaArborModBlocks.SEA_TRAIL_INIT.get().getStateDefinition().getProperty("waterlogged") instanceof BooleanProperty _withbp37
                                    ? CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState().setValue(_withbp37, true)
                                    : CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState());
                        } else {
                            toPlace = CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState();
                        }
                        {
                            BlockPos _bp = BlockPos.containing((double) x + dltx, (double) y + dy, (double) z + dltz);
                            BlockState _bs = (new Object() {
                                public BlockState with(BlockState _bs, Direction newValue) {
                                    Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty("facing");
                                    if (_prop instanceof DirectionProperty _dp && _dp.getPossibleValues().contains(newValue))
                                        return _bs.setValue(_dp, newValue);
                                    _prop = _bs.getBlock().getStateDefinition().getProperty("axis");
                                    return _prop instanceof EnumProperty _ep && _ep.getPossibleValues().contains(newValue.getAxis()) ? _bs.setValue(_ep, newValue.getAxis()) : _bs;
                                }
                            }.with(toPlace, new Object() {
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
                            }.getValue()));
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
                        put = true;
                        break;
                    }
                }
            }
            if (Math.random() < 0.25 && canDropTrail(world, (double) x + dltx, (double) y - 1, (double) z + dltz)) {
                if ((LevelAccessor) world instanceof ServerLevel _level)
                    FallingBlockEntity.fall(_level, BlockPos.containing((double) x + dltx, (double) y - 1, (double) z + dltz), CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState());
                put = true;
            }
        }
        if (!put) {
            dltx = 1;
            dltz = -1;
            for (int dy = (int) 0; dy >= (int) 2; dy--) {
                if (Math.random() < 0.25) {
                    toReplace = (((LevelAccessor) world).getBlockState(BlockPos.containing((double) x + dltx, (double) y + dy, (double) z + dltz)));
                    if (toReplace.canBeReplaced() && CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState().canSurvive(world, BlockPos.containing((double) x + dltx, (double) y + dy, (double) z + dltz))) {
                        if (toReplace.getBlock() == Blocks.WATER) {
                            toPlace = (CaerulaArborModBlocks.SEA_TRAIL_INIT.get().getStateDefinition().getProperty("waterlogged") instanceof BooleanProperty _withbp48
                                    ? CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState().setValue(_withbp48, true)
                                    : CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState());
                        } else {
                            toPlace = CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState();
                        }
                        {
                            BlockPos _bp = BlockPos.containing((double) x + dltx, (double) y + dy, (double) z + dltz);
                            BlockState _bs = (new Object() {
                                public BlockState with(BlockState _bs, Direction newValue) {
                                    Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty("facing");
                                    if (_prop instanceof DirectionProperty _dp && _dp.getPossibleValues().contains(newValue))
                                        return _bs.setValue(_dp, newValue);
                                    _prop = _bs.getBlock().getStateDefinition().getProperty("axis");
                                    return _prop instanceof EnumProperty _ep && _ep.getPossibleValues().contains(newValue.getAxis()) ? _bs.setValue(_ep, newValue.getAxis()) : _bs;
                                }
                            }.with(toPlace, new Object() {
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
                            }.getValue()));
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
                        put = true;
                        break;
                    }
                }
            }
            if (Math.random() < 0.25 && canDropTrail(world, (double) x + dltx, (double) y - 1, (double) z + dltz)) {
                if ((LevelAccessor) world instanceof ServerLevel _level)
                    FallingBlockEntity.fall(_level, BlockPos.containing((double) x + dltx, (double) y - 1, (double) z + dltz), CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState());
                put = true;
            }
        }
        if (!put) {
            dltx = -1;
            dltz = 1;
            for (int dy = (int) 0; dy >= (int) 2; dy--) {
                if (Math.random() < 0.25) {
                    toReplace = (((LevelAccessor) world).getBlockState(BlockPos.containing((double) x + dltx, (double) y + dy, (double) z + dltz)));
                    if (toReplace.canBeReplaced() && CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState().canSurvive(world, BlockPos.containing((double) x + dltx, (double) y + dy, (double) z + dltz))) {
                        if (toReplace.getBlock() == Blocks.WATER) {
                            toPlace = (CaerulaArborModBlocks.SEA_TRAIL_INIT.get().getStateDefinition().getProperty("waterlogged") instanceof BooleanProperty _withbp59
                                    ? CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState().setValue(_withbp59, true)
                                    : CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState());
                        } else {
                            toPlace = CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState();
                        }
                        {
                            BlockPos _bp = BlockPos.containing((double) x + dltx, (double) y + dy, (double) z + dltz);
                            BlockState _bs = (new Object() {
                                public BlockState with(BlockState _bs, Direction newValue) {
                                    Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty("facing");
                                    if (_prop instanceof DirectionProperty _dp && _dp.getPossibleValues().contains(newValue))
                                        return _bs.setValue(_dp, newValue);
                                    _prop = _bs.getBlock().getStateDefinition().getProperty("axis");
                                    return _prop instanceof EnumProperty _ep && _ep.getPossibleValues().contains(newValue.getAxis()) ? _bs.setValue(_ep, newValue.getAxis()) : _bs;
                                }
                            }.with(toPlace, new Object() {
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
                            }.getValue()));
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
                        put = true;
                        break;
                    }
                }
            }
            if (Math.random() < 0.25 && canDropTrail(world, (double) x + dltx, (double) y - 1, (double) z + dltz)) {
                if ((LevelAccessor) world instanceof ServerLevel _level)
                    FallingBlockEntity.fall(_level, BlockPos.containing((double) x + dltx, (double) y - 1, (double) z + dltz), CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState());
                put = true;
            }
        }
        if (!put) {
            dltx = -1;
            dltz = -1;
            for (int dy = (int) 0; dy >= (int) 2; dy--) {
                if (Math.random() < 0.25) {
                    toReplace = (((LevelAccessor) world).getBlockState(BlockPos.containing((double) x + dltx, (double) y + dy, (double) z + dltz)));
                    if (toReplace.canBeReplaced() && CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState().canSurvive(world, BlockPos.containing((double) x + dltx, (double) y + dy, (double) z + dltz))) {
                        if (toReplace.getBlock() == Blocks.WATER) {
                            toPlace = (CaerulaArborModBlocks.SEA_TRAIL_INIT.get().getStateDefinition().getProperty("waterlogged") instanceof BooleanProperty _withbp70
                                    ? CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState().setValue(_withbp70, true)
                                    : CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState());
                        } else {
                            toPlace = CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState();
                        }
                        {
                            BlockPos _bp = BlockPos.containing((double) x + dltx, (double) y + dy, (double) z + dltz);
                            BlockState _bs = (new Object() {
                                public BlockState with(BlockState _bs, Direction newValue) {
                                    Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty("facing");
                                    if (_prop instanceof DirectionProperty _dp && _dp.getPossibleValues().contains(newValue))
                                        return _bs.setValue(_dp, newValue);
                                    _prop = _bs.getBlock().getStateDefinition().getProperty("axis");
                                    return _prop instanceof EnumProperty _ep && _ep.getPossibleValues().contains(newValue.getAxis()) ? _bs.setValue(_ep, newValue.getAxis()) : _bs;
                                }
                            }.with(toPlace, new Object() {
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
                            }.getValue()));
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
                        put = true;
                        break;
                    }
                }
            }
            if (Math.random() < 0.25 && canDropTrail(world, (double) x + dltx, (double) y - 1, (double) z + dltz)) {
                if ((LevelAccessor) world instanceof ServerLevel _level)
                    FallingBlockEntity.fall(_level, BlockPos.containing((double) x + dltx, (double) y - 1, (double) z + dltz), CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState());
                put = true;
            }
        }
        if (!put) {
            for (Direction directioniterator : Direction.values()) {
                if (Math.random() < 0.25) {
                    if (WorldUtils.isOrganic(((LevelAccessor) world).getBlockState(BlockPos.containing((double) x + directioniterator.getStepX(), (double) y + directioniterator.getStepY(), (double) z + directioniterator.getStepZ())))) {
                        {
                            BlockPos _bp = BlockPos.containing((double) x + directioniterator.getStepX(), (double) y + directioniterator.getStepY(), (double) z + directioniterator.getStepZ());
                            BlockState _bs = CaerulaArborModBlocks.TRAIL_PULSE.get().defaultBlockState();
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
                        put = true;
                        break;
                    }
                }
            }
        }
        if ((blockstate.getBlock().getStateDefinition().getProperty("nurtr") instanceof IntegerProperty _getip93 ? blockstate.getValue(_getip93) : -1) <= 0) {
            world.destroyBlock(BlockPos.containing(x, y, z), false);
            if ((LevelAccessor) world instanceof Level _level) {
                if (!_level.isClientSide()) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.sculk_vein.break")), SoundSource.BLOCKS, (float) 0.33, 1);
                } else {
                    _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.sculk_vein.break")), SoundSource.BLOCKS, (float) 0.33, 1, false);
                }
            }
            if (Math.random() < 0.025) {
                if (Math.random() < 0.012) {
                    ((LevelAccessor) world).setBlock(BlockPos.containing(x, y, z), CaerulaArborModBlocks.RED_OVARY.get().defaultBlockState(), 3);
                } else {
                    ((LevelAccessor) world).setBlock(BlockPos.containing(x, y, z), CaerulaArborModBlocks.OCEAN_OVARY.get().defaultBlockState(), 3);
                }
            }
        }
        if ((blockstate.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty _getip99 ? blockstate.getValue(_getip99) : -1) <= 0 && Math.random() < 0.33) {
            world.destroyBlock(BlockPos.containing(x, y, z), false);
            if ((LevelAccessor) world instanceof Level _level) {
                if (!_level.isClientSide()) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.sculk_vein.break")), SoundSource.BLOCKS, (float) 0.33, 1);
                } else {
                    _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.sculk_vein.break")), SoundSource.BLOCKS, (float) 0.33, 1, false);
                }
            }
            if (Math.random() < 0.025) {
                if (Math.random() < 0.012) {
                    ((LevelAccessor) world).setBlock(BlockPos.containing(x, y, z), CaerulaArborModBlocks.RED_OVARY.get().defaultBlockState(), 3);
                } else {
                    ((LevelAccessor) world).setBlock(BlockPos.containing(x, y, z), CaerulaArborModBlocks.OCEAN_OVARY.get().defaultBlockState(), 3);
                }
            }
        }
        if (put) {
            if ((LevelAccessor) world instanceof Level _level) {
                if (!_level.isClientSide()) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.sculk_vein.place")), SoundSource.BLOCKS, (float) 0.33, 1);
                } else {
                    _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.sculk_vein.place")), SoundSource.BLOCKS, (float) 0.33, 1, false);
                }
            }
            {
                int _value = (int) ((blockstate.getBlock().getStateDefinition().getProperty("nurtr") instanceof IntegerProperty _getip106 ? blockstate.getValue(_getip106) : -1) - 1);
                BlockPos _pos = BlockPos.containing(x, y, z);
                BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                if (_bs.getBlock().getStateDefinition().getProperty("nurtr") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
                    ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
            }
        }
        {
            int _value = (int) ((blockstate.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty _getip109 ? blockstate.getValue(_getip109) : -1) - 1);
            BlockPos _pos = BlockPos.containing(x, y, z);
            BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
            if (_bs.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
                ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
        }
        world.scheduleTick(pos, this, 80);
	}

	private boolean canDropTrail(LevelAccessor world, double xx, double yy, double zz) {
		BlockState targetBlock = Blocks.AIR.defaultBlockState();
		boolean drop = false;
		drop = true;
		if (world.getBlockFloorHeight(BlockPos.containing(xx, yy, zz)) > 0) {
			return false;
		}
		for (int index0 = 0; index0 < 64; index0++) {
			targetBlock = (world.getBlockState(BlockPos.containing(xx, yy - index0 - 1, zz)));
			if (world.isEmptyBlock(BlockPos.containing(xx, yy - index0 - 1, zz)) || targetBlock.canBeReplaced()) {
				if (CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState().canSurvive(world, BlockPos.containing(xx, yy - index0 - 1, zz))) {
					return true;
				}
			} else {
				if (index0 > 0) {
					return false;
				}
				break;
			}
		}
		return false;
	}

	@Override
	public boolean onDestroyedByPlayer(BlockState blockstate, Level world, BlockPos pos, Player entity, boolean willHarvest, FluidState fluid) {
		boolean retval = super.onDestroyedByPlayer(blockstate, world, pos, entity, willHarvest, fluid);
		PokePlayerProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ(), entity);
		return retval;
	}

	@Override
	public void stepOn(Level world, BlockPos pos, BlockState blockstate, Entity entity) {
		super.stepOn(world, pos, blockstate, entity);
		EntityUtils.damagedByNethseabrand(world, entity);
	}
}
