
package com.apocalypse.caerulaarbor.block;

import com.apocalypse.caerulaarbor.init.CaerulaArborModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Map;

public class TrailLeaveBlock extends LeavesBlock {
	public static final IntegerProperty GROW_AGE = IntegerProperty.create("grow_age", 0, 64);
	public static final IntegerProperty LONGEVITY = IntegerProperty.create("longevity", 0, 16);

	public TrailLeaveBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.MOSS).strength(0.5f, 1f).speedFactor(0.8f).jumpFactor(0.9f).noOcclusion());
		this.registerDefaultState(this.stateDefinition.any().setValue(GROW_AGE, 0).setValue(LONGEVITY, 16));
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 1;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(GROW_AGE, LONGEVITY);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(GROW_AGE, 0).setValue(LONGEVITY, 16);
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 20;
	}

	@Override
	public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
		super.onPlace(blockstate, world, pos, oldState, moving);
		world.scheduleTick(pos, this, 40);
	}

	@Override
	public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
		super.tick(blockstate, world, pos, random);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
        BlockState targetBlock = Blocks.AIR.defaultBlockState();
        Direction dire = Direction.NORTH;
        double expand = 0;
        double longev = 0;
        boolean drop = false;
        if ((blockstate.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty _getip1 ? blockstate.getValue(_getip1) : -1) < 64) {
            {
                int _value = (int) ((blockstate.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty _getip3 ? blockstate.getValue(_getip3) : -1) + 1);
                BlockPos _pos = BlockPos.containing(x, y, z);
                BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                if (_bs.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
                    ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
            }
        }
        if ((blockstate.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty _getip6 ? blockstate.getValue(_getip6) : -1) > 16
                && (blockstate.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty _getip8 ? blockstate.getValue(_getip8) : -1) < 62) {
            for (Direction directioniterator : Direction.values()) {
                if (Math.random() < 0.2) {
                    longev = blockstate.getBlock().getStateDefinition().getProperty("longevity") instanceof IntegerProperty _getip10 ? blockstate.getValue(_getip10) : -1;
                    if (longev > 0) {
                        if (Math.random() < 0.25) {
                            longev = longev - 1;
                        }
                        dire = directioniterator;
                        targetBlock = (((LevelAccessor) world).getBlockState(BlockPos.containing((double) x + dire.getStepX(), (double) y + dire.getStepY(), (double) z + dire.getStepZ())));
                        if (targetBlock.is(BlockTags.create(new ResourceLocation("minecraft:leaves"))) && !(targetBlock.getBlock() == CaerulaArborModBlocks.TRAIL_LEAVE.get())) {
                            {
                                BlockPos _bp = BlockPos.containing((double) x + dire.getStepX(), (double) y + dire.getStepY(), (double) z + dire.getStepZ());
                                BlockState _bs = (new Object() {
                                    public BlockState with(BlockState _bs, String _property, int _newValue) {
                                        Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty(_property);
                                        return _prop instanceof IntegerProperty _ip && _prop.getPossibleValues().contains(_newValue) ? _bs.setValue(_ip, _newValue) : _bs;
                                    }
                                }.with(CaerulaArborModBlocks.TRAIL_LEAVE.get().defaultBlockState(), "longevity", (int) longev));
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
                        }
                    }
                }
            }
        } else if ((blockstate.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty _getip25 ? blockstate.getValue(_getip25) : -1) > 62) {
            if (Math.random() < 0.0125) {
                drop = true;
                for (int index0 = 0; index0 < 64; index0++) {
                    targetBlock = (((LevelAccessor) world).getBlockState(BlockPos.containing(x, (double) y - index0 - 1, z)));
                    if (world.isEmptyBlock(BlockPos.containing(x, (double) y - index0 - 1, z)) || targetBlock.canBeReplaced()) {
                        if (CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState().canSurvive(world, BlockPos.containing(x, (double) y - index0 - 1, z))) {
                            drop = true;
                            break;
                        }
                        continue;
                    } else {
                        if (targetBlock.getBlock() == CaerulaArborModBlocks.TRAIL_LOG.get() || targetBlock.getBlock() == CaerulaArborModBlocks.STRIPPED_TRAIL_LOG.get()) {
                            drop = false;
                        }
                        if (targetBlock.getBlock() == CaerulaArborModBlocks.NETHERSEA_WOOD.get() || targetBlock.getBlock() == CaerulaArborModBlocks.STRIPPED_NETHERSEA_WOOD.get()) {
                            drop = false;
                        }
                        if (targetBlock.getBlock() == CaerulaArborModBlocks.TRAIL_LEAVE.get()) {
                            drop = false;
                        }
                        if (index0 > 0) {
                            drop = false;
                        }
                        break;
                    }
                }
                if (drop) {
                    ((LevelAccessor) world).setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
                    if ((LevelAccessor) world instanceof ServerLevel _level)
                        FallingBlockEntity.fall(_level, BlockPos.containing(x, y, z), CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState());
                }
            }
        }
        world.scheduleTick(pos, this, 40);
	}
}
