package com.apocalypse.caerulaarbor.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CaerulaArborModBlocks;
import com.apocalypse.caerulaarbor.procedures.PokeSlightlyProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

public class TrailLogBlock extends Block {
	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
	public static final IntegerProperty GROW_AGE = IntegerProperty.create("grow_age", 0, 64);
	public static final IntegerProperty LONGEVITY = IntegerProperty.create("longevity", 0, 16);

	public TrailLogBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).sound(SoundType.WOOD).strength(3f, 5f).speedFactor(0.9f));
		this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.Y).setValue(GROW_AGE, 0).setValue(LONGEVITY, 16));
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(AXIS, GROW_AGE, LONGEVITY);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(AXIS, context.getClickedFace().getAxis()).setValue(GROW_AGE, 0).setValue(LONGEVITY, 16);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		if (rot == Rotation.CLOCKWISE_90 || rot == Rotation.COUNTERCLOCKWISE_90) {
			if (state.getValue(AXIS) == Direction.Axis.X) {
				return state.setValue(AXIS, Direction.Axis.Z);
			} else if (state.getValue(AXIS) == Direction.Axis.Z) {
				return state.setValue(AXIS, Direction.Axis.X);
			}
		}
		return state;
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
        BlockState targetBlock = Blocks.AIR.defaultBlockState();
        Direction dire = Direction.NORTH;
        double expand = 0;
        double longev = 0;
        if ((blockstate.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty _getip1 ? blockstate.getValue(_getip1) : -1) < 64) {
            {
                int _value = (blockstate.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty _getip3 ? blockstate.getValue(_getip3) : -1) + 1;
                BlockPos _pos = BlockPos.containing(x, y, z);
                BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                if (_bs.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
                    ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
            }
        }
        if ((blockstate.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty _getip6 ? blockstate.getValue(_getip6) : -1) > 30
                && (blockstate.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty _getip8 ? blockstate.getValue(_getip8) : -1) < 64) {
            for (Direction directioniterator : Direction.values()) {
                if (Math.random() < 0.2) {
                    longev = blockstate.getBlock().getStateDefinition().getProperty("longevity") instanceof IntegerProperty _getip10 ? blockstate.getValue(_getip10) : -1;
                    if (longev > 0) {
                        if (Math.random() < 0.5) {
                            longev = longev - 1;
                        }
                        dire = directioniterator;
                        targetBlock = (((LevelAccessor) world).getBlockState(BlockPos.containing((double) x + dire.getStepX(), (double) y + dire.getStepY(), (double) z + dire.getStepZ())));
                        if (targetBlock.is(BlockTags.create(new ResourceLocation("minecraft:logs"))) && !targetBlock.is(BlockTags.create(new ResourceLocation(CaerulaArborMod.MODID, "cannot_cover")))) {
                            {
                                BlockPos _bp = BlockPos.containing((double) x + dire.getStepX(), (double) y + dire.getStepY(), (double) z + dire.getStepZ());
                                BlockState _bs = (new Object() {
                                    public BlockState with(BlockState _bs, Direction newValue) {
                                        Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty("facing");
                                        if (_prop instanceof DirectionProperty _dp && _dp.getPossibleValues().contains(newValue))
                                            return _bs.setValue(_dp, newValue);
                                        _prop = _bs.getBlock().getStateDefinition().getProperty("axis");
                                        return _prop instanceof EnumProperty _ep && _ep.getPossibleValues().contains(newValue.getAxis()) ? _bs.setValue(_ep, newValue.getAxis()) : _bs;
                                    }
                                }.with((new Object() {
                                    public BlockState with(BlockState _bs, String _property, int _newValue) {
                                        Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty(_property);
                                        return _prop instanceof IntegerProperty _ip && _prop.getPossibleValues().contains(_newValue) ? _bs.setValue(_ip, _newValue) : _bs;
                                    }
                                }.with(CaerulaArborModBlocks.TRAIL_LOG.get().defaultBlockState(), "longevity", (int) longev)), dire));
                                BlockState _bso = ((LevelAccessor) world).getBlockState(_bp);
                                for (Map.Entry<Property<?>, Comparable<?>> entry : _bso.getValues().entrySet()) {
                                    Property<?> _property = _bs.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
                                    if (_property != null && _bs.getValue(_property) != null)
                                        try {
                                            _bs = _bs.setValue(_property, (Comparable) entry.getValue());
                                        } catch (Exception e) {
                                        }
                                }
                                ((LevelAccessor) world).setBlock(_bp, _bs, 3);
                            }
                        } else if (targetBlock.is(BlockTags.create(new ResourceLocation("minecraft:leaves"))) && !(targetBlock.getBlock() == CaerulaArborModBlocks.TRAIL_LEAVE.get())) {
                            {
                                BlockPos _bp = BlockPos.containing((double) x + dire.getStepX(), (double) y + dire.getStepY(), (double) z + dire.getStepZ());
                                BlockState _bs = CaerulaArborModBlocks.TRAIL_LEAVE.get().defaultBlockState();
                                BlockState _bso = ((LevelAccessor) world).getBlockState(_bp);
                                for (Map.Entry<Property<?>, Comparable<?>> entry : _bso.getValues().entrySet()) {
                                    Property<?> _property = _bs.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
                                    if (_property != null && _bs.getValue(_property) != null)
                                        try {
                                            _bs = _bs.setValue(_property, (Comparable) entry.getValue());
                                        } catch (Exception e) {
                                        }
                                }
                                ((LevelAccessor) world).setBlock(_bp, _bs, 3);
                            }
                        } else if (targetBlock.getBlock() == Blocks.VINE) {
                            world.destroyBlock(BlockPos.containing((double) x + dire.getStepX(), (double) y + dire.getStepY(), (double) z + dire.getStepZ()), false);
                        }
                    }
                }
            }
        }
        world.scheduleTick(pos, this, 20);
	}

	@Override
	public boolean onDestroyedByPlayer(BlockState blockstate, Level world, BlockPos pos, Player entity, boolean willHarvest, FluidState fluid) {
		boolean retval = super.onDestroyedByPlayer(blockstate, world, pos, entity, willHarvest, fluid);
		PokeSlightlyProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ(), entity);
		return retval;
	}

	@Override
	public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
		super.use(blockstate, world, pos, entity, hand, hit);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		double hitX = hit.getLocation().x;
		double hitY = hit.getLocation().y;
		double hitZ = hit.getLocation().z;
		Direction direction = hit.getDirection();
        InteractionResult result = InteractionResult.PASS;
        if (entity != null) {
            if (((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).is(ItemTags.create(new ResourceLocation("minecraft:axes")))
                    || ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).is(ItemTags.create(new ResourceLocation("minecraft:axes")))) {
                world.levelEvent(2001, BlockPos.containing(x, y, z), getId(CaerulaArborModBlocks.TRAIL_LOG.get().defaultBlockState()));
                if ((LevelAccessor) world instanceof Level _level) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.axe.strip")), SoundSource.BLOCKS, 1, 1);
                }
                {
                    BlockPos _bp = BlockPos.containing(x, y, z);
                    BlockState _bs = (new Object() {
                        public BlockState with(BlockState _bs, Direction newValue) {
                            Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty("facing");
                            if (_prop instanceof DirectionProperty _dp && _dp.getPossibleValues().contains(newValue))
                                return _bs.setValue(_dp, newValue);
                            _prop = _bs.getBlock().getStateDefinition().getProperty("axis");
                            return _prop instanceof EnumProperty _ep && _ep.getPossibleValues().contains(newValue.getAxis()) ? _bs.setValue(_ep, newValue.getAxis()) : _bs;
                        }
                    }.with(CaerulaArborModBlocks.STRIPPED_TRAIL_LOG.get().defaultBlockState(), (new Object() {
                        public Direction getDirection(BlockState _bs) {
                            Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty("facing");
                            if (_prop instanceof DirectionProperty _dp)
                                return _bs.getValue(_dp);
                            _prop = _bs.getBlock().getStateDefinition().getProperty("axis");
                            return _prop instanceof EnumProperty _ep && _ep.getPossibleValues().toArray()[0] instanceof Direction.Axis
                                    ? Direction.fromAxisAndDirection((Direction.Axis) _bs.getValue(_ep), Direction.AxisDirection.POSITIVE)
                                    : Direction.NORTH;
                        }
                    }.getDirection(blockstate))));
                    BlockState _bso = ((LevelAccessor) world).getBlockState(_bp);
                    for (Map.Entry<Property<?>, Comparable<?>> entry : _bso.getValues().entrySet()) {
                        Property<?> _property = _bs.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
                        if (_property != null && _bs.getValue(_property) != null)
                            try {
                                _bs = _bs.setValue(_property, (Comparable) entry.getValue());
                            } catch (Exception e) {
                            }
                    }
                    ((LevelAccessor) world).setBlock(_bp, _bs, 3);
                }
                result = InteractionResult.SUCCESS;
            }
        }
        return result;
	}
}
