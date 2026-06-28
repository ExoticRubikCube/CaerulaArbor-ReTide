
package com.apocalypse.caerulaarbor.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CaerulaArborModBlockEntities;
import com.apocalypse.caerulaarbor.init.CaerulaArborModBlocks;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import com.apocalypse.caerulaarbor.utils.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ViviparousLilyBlock extends BaseEntityBlock implements SimpleWaterloggedBlock, EntityBlock {
	public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 1);
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public ViviparousLilyBlock() {
		super(BlockBehaviour.Properties.of()

				.sound(SoundType.FUNGUS).instabreak().lightLevel(s -> 1).noCollission().noOcclusion().randomTicks().pushReaction(PushReaction.DESTROY).isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return CaerulaArborModBlockEntities.VIVIPAROUS_LILY.get().create(blockPos, blockState);
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

		return switch (state.getValue(FACING)) {
            case NORTH -> box(6, 0, 6, 10, 5, 10);
			case EAST -> box(6, 0, 6, 10, 5, 10);
			case WEST -> box(6, 0, 6, 10, 5, 10);
            default -> box(6, 0, 6, 10, 5, 10);
        };
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(ANIMATION, FACING, WATERLOGGED);
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
	public boolean canSurvive(BlockState blockstate, LevelReader worldIn, BlockPos pos) {
		if (worldIn instanceof LevelAccessor world) {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			return WorldUtils.canLilyExist(world, x, y, z);
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
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		List<ItemStack> dropsOriginal = super.getDrops(state, builder);
		if (!dropsOriginal.isEmpty())
			return dropsOriginal;
		return Collections.singletonList(new ItemStack(this, 1));
	}

	@Override
	public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
		super.onPlace(blockstate, world, pos, oldState, moving);
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        {
            Direction _dir = new Object() {
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
            }.getValue();
            BlockPos _pos = BlockPos.containing(x, y, z);
            BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
            Property<?> _property = _bs.getBlock().getStateDefinition().getProperty("facing");
            if (_property instanceof DirectionProperty _dp && _dp.getPossibleValues().contains(_dir)) {
                ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_dp, _dir), 3);
            } else {
                _property = _bs.getBlock().getStateDefinition().getProperty("axis");
                if (_property instanceof EnumProperty _ap && _ap.getPossibleValues().contains(_dir.getAxis()))
                    ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_ap, _dir.getAxis()), 3);
            }
        }
    }

	@Override
	public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
		super.tick(blockstate, world, pos, random);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

        boolean huge = false;
        huge = true;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (!((((LevelAccessor) world).getBlockState(BlockPos.containing((double) x + dx, y, (double) z + dz))).getBlock() == CaerulaArborModBlocks.VIVIPAROUS_LILY.get())) {
                    huge = false;
                    break;
                }
            }
            if (!huge) {
                break;
            }
        }
        if (huge) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (!(dx == 0 && dz == 0)) {
                        ((LevelAccessor) world).setBlock(BlockPos.containing((double) x + dx, y, (double) z + dz), Blocks.AIR.defaultBlockState(), 3);
                        if ((LevelAccessor) world instanceof Level _level) {
                                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.fungus.break")), SoundSource.BLOCKS, 1, 1);
                        }
                    }
                }
            }
            if ((LevelAccessor) world instanceof ServerLevel _level)
                _level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, ((double) x + 0.5), y, ((double) z + 0.5), 2, 0.1, 0.1, 0.1, 0.1);
            {
                int _value = 1;
                BlockPos _pos = BlockPos.containing(x, y, z);
                BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                if (_bs.getBlock().getStateDefinition().getProperty("animation") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
                    ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
            }
            CaerulaArborMod.queueServerWork(20, () -> {
                if ((((LevelAccessor) world).getBlockState(BlockPos.containing(x, y, z))).getBlock() == CaerulaArborModBlocks.VIVIPAROUS_LILY.get()) {
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
                        }.with(CaerulaArborModBlocks.HUGE_LILY.get().defaultBlockState(), (new Object() {
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
            });
        } else {
            if ((((LevelAccessor) world).getBlockState(BlockPos.containing(x, (double) y - 1, z))).is(BlockTags.create(new ResourceLocation(CaerulaArborMod.MODID, "trail")))) {
                if (Math.random() < 0.33) {
                    if ((LevelAccessor) world instanceof ServerLevel _level) {
                        Entity entityToSpawn = CaerulaArborModEntities.SLIDER_FISH.get().spawn(_level, BlockPos.containing((double) x + 0.5, y, (double) z + 0.5), MobSpawnType.MOB_SUMMONED);
                        if (entityToSpawn != null) {
                            entityToSpawn.setYRot(((LevelAccessor) world).getRandom().nextFloat() * 360F);
                        }
                    }
                    if ((LevelAccessor) world instanceof Level _level) {
                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.puffer_fish.blow_out")), SoundSource.BLOCKS, 1, 1);
                    }
                    ((LevelAccessor) world).setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
    }
}
