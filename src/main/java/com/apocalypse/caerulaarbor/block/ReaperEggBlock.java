
package com.apocalypse.caerulaarbor.block;

import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import com.apocalypse.caerulaarbor.init.CaerulaArborModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;

public class ReaperEggBlock extends Block implements SimpleWaterloggedBlock {
	public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 2);
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final IntegerProperty HATCH = BlockStateProperties.HATCH;
	public static final IntegerProperty PROCESS = IntegerProperty.create("process", 0, 10);

	public ReaperEggBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.GILDED_BLACKSTONE).strength(2f, 3f).lightLevel(s -> (new Object() {
			public int getLightLevel() {
				if (s.getValue(BLOCKSTATE) == 1)
					return 0;
				if (s.getValue(BLOCKSTATE) == 2)
					return 0;
				return 0;
			}
		}.getLightLevel())).noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HATCH, 0).setValue(PROCESS, 0).setValue(WATERLOGGED, false));
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
		if (state.getValue(BLOCKSTATE) == 1) {
			return switch (state.getValue(FACING)) {
				default -> box(4, 0, 4, 12, 12, 12);
				case NORTH -> box(4, 0, 4, 12, 12, 12);
				case EAST -> box(4, 0, 4, 12, 12, 12);
				case WEST -> box(4, 0, 4, 12, 12, 12);
			};
		}
		if (state.getValue(BLOCKSTATE) == 2) {
			return switch (state.getValue(FACING)) {
				default -> box(4, 0, 4, 12, 12, 12);
				case NORTH -> box(4, 0, 4, 12, 12, 12);
				case EAST -> box(4, 0, 4, 12, 12, 12);
				case WEST -> box(4, 0, 4, 12, 12, 12);
			};
		}
		return switch (state.getValue(FACING)) {
			default -> box(4, 0, 4, 12, 12, 12);
			case NORTH -> box(4, 0, 4, 12, 12, 12);
			case EAST -> box(4, 0, 4, 12, 12, 12);
			case WEST -> box(4, 0, 4, 12, 12, 12);
		};
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, HATCH, PROCESS, WATERLOGGED, BLOCKSTATE);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
		return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(HATCH, 0).setValue(PROCESS, 0).setValue(WATERLOGGED, flag);
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
        if (blockstate.getBlock().getStateDefinition().getProperty("waterlogged") instanceof BooleanProperty _getbp1 && blockstate.getValue(_getbp1)) {
            if (Math.random() < 0.2) {
                {
                    int _value = (int) ((blockstate.getBlock().getStateDefinition().getProperty("process") instanceof IntegerProperty _getip3 ? blockstate.getValue(_getip3) : -1) + 1);
                    BlockPos _pos = BlockPos.containing(x, y, z);
                    BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                    if (_bs.getBlock().getStateDefinition().getProperty("process") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
                        ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
                }
            }
        }
        if ((blockstate.getBlock().getStateDefinition().getProperty("process") instanceof IntegerProperty _getip6 ? blockstate.getValue(_getip6) : -1) >= 10) {
            {
                int _value = (int) ((blockstate.getBlock().getStateDefinition().getProperty("hatch") instanceof IntegerProperty _getip8 ? blockstate.getValue(_getip8) : -1) + 1);
                BlockPos _pos = BlockPos.containing(x, y, z);
                BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                if (_bs.getBlock().getStateDefinition().getProperty("hatch") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
                    ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
            }
            {
                int _value = 0;
                BlockPos _pos = BlockPos.containing(x, y, z);
                BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                if (_bs.getBlock().getStateDefinition().getProperty("process") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
                    ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
            }
            if ((LevelAccessor) world instanceof Level _level) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.turtle.egg_crack")), SoundSource.BLOCKS, 1, 1);
            }
        }
        if ((blockstate.getBlock().getStateDefinition().getProperty("hatch") instanceof IntegerProperty _getip13 ? blockstate.getValue(_getip13) : -1) >= 2
                && (blockstate.getBlock().getStateDefinition().getProperty("process") instanceof IntegerProperty _getip15 ? blockstate.getValue(_getip15) : -1) >= 10) {
            world.destroyBlock(BlockPos.containing(x, y, z), false);
            if ((LevelAccessor) world instanceof ServerLevel _level) {
                Entity entityToSpawn = CaerulaArborModEntities.REAPER_PET.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.setYRot(((LevelAccessor) world).getRandom().nextFloat() * 360F);
                }
            }
            if ((LevelAccessor) world instanceof Level _level) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.turtle.egg_hatch")), SoundSource.BLOCKS, 1, 1);
            }
        }
        {
            int _value = blockstate.getBlock().getStateDefinition().getProperty("hatch") instanceof IntegerProperty _getip20 ? blockstate.getValue(_getip20) : -1;
            BlockPos _pos = BlockPos.containing(x, y, z);
            BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
            if (_bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
                ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
        }
        world.scheduleTick(pos, this, 20);
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
            if (((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CaerulaArborModItems.CELL_CLUSTER.get()) {
                if (new Object() {
                    public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    {
                        int _value = (int) ((blockstate.getBlock().getStateDefinition().getProperty("hatch") instanceof IntegerProperty _getip4 ? blockstate.getValue(_getip4) : -1) + 1);
                        BlockPos _pos = BlockPos.containing(x, y, z);
                        BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                        if (_bs.getBlock().getStateDefinition().getProperty("hatch") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
                            ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
                    }
                    ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                } else {
                    {
                        int _value = (int) ((blockstate.getBlock().getStateDefinition().getProperty("process") instanceof IntegerProperty _getip9 ? blockstate.getValue(_getip9) : -1) + 1);
                        BlockPos _pos = BlockPos.containing(x, y, z);
                        BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                        if (_bs.getBlock().getStateDefinition().getProperty("process") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
                            ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
                    }
                    ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                }
                result = InteractionResult.SUCCESS;
            }
        }
        return result;
	}
}
