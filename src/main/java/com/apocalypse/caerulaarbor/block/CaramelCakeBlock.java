
package com.apocalypse.caerulaarbor.block;

import com.apocalypse.caerulaarbor.init.CAItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class CaramelCakeBlock extends Block implements SimpleWaterloggedBlock {
	public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 3);
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public CaramelCakeBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.SCULK).strength(0.5f).lightLevel(s -> (new Object() {
			public int getLightLevel() {
				if (s.getValue(BLOCKSTATE) == 1)
					return 0;
				if (s.getValue(BLOCKSTATE) == 2)
					return 0;
				if (s.getValue(BLOCKSTATE) == 3)
					return 0;
				return 0;
			}
		}.getLightLevel())).friction(0.5f).speedFactor(0.8f).jumpFactor(0.9f).noOcclusion().isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
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
                case NORTH -> Shapes.or(box(8, 0, 8, 15, 8, 15), box(1, 0, 1, 8, 8, 8), box(1, 0, 8, 8, 8, 15));
				case EAST -> Shapes.or(box(1, 0, 8, 8, 8, 15), box(8, 0, 1, 15, 8, 8), box(1, 0, 1, 8, 8, 8));
				case WEST -> Shapes.or(box(8, 0, 1, 15, 8, 8), box(1, 0, 8, 8, 8, 15), box(8, 0, 8, 15, 8, 15));
                default -> Shapes.or(box(1, 0, 1, 8, 8, 8), box(8, 0, 8, 15, 8, 15), box(8, 0, 1, 15, 8, 8));
            };
		}
		if (state.getValue(BLOCKSTATE) == 2) {
			return switch (state.getValue(FACING)) {
                case NORTH -> Shapes.or(box(8, 0, 8, 15, 8, 15), box(1, 0, 8, 8, 8, 15));
				case EAST -> Shapes.or(box(1, 0, 8, 8, 8, 15), box(1, 0, 1, 8, 8, 8));
				case WEST -> Shapes.or(box(8, 0, 1, 15, 8, 8), box(8, 0, 8, 15, 8, 15));
                default -> Shapes.or(box(1, 0, 1, 8, 8, 8), box(8, 0, 1, 15, 8, 8));
            };
		}
		if (state.getValue(BLOCKSTATE) == 3) {
			return switch (state.getValue(FACING)) {
                case NORTH -> box(8, 0, 8, 15, 8, 15);
				case EAST -> box(1, 0, 8, 8, 8, 15);
				case WEST -> box(8, 0, 1, 15, 8, 8);
                default -> box(1, 0, 1, 8, 8, 8);
            };
		}
		return switch (state.getValue(FACING)) {
            case NORTH -> Shapes.or(box(8, 0, 8, 15, 8, 15), box(1, 0, 1, 8, 8, 8), box(1, 0, 8, 8, 8, 15), box(8, 0, 1, 15, 8, 8));
			case EAST -> Shapes.or(box(1, 0, 8, 8, 8, 15), box(8, 0, 1, 15, 8, 8), box(1, 0, 1, 8, 8, 8), box(8, 0, 8, 15, 8, 15));
			case WEST -> Shapes.or(box(8, 0, 1, 15, 8, 8), box(1, 0, 8, 8, 8, 15), box(8, 0, 8, 15, 8, 15), box(1, 0, 1, 8, 8, 8));
            default -> Shapes.or(box(1, 0, 1, 8, 8, 8), box(8, 0, 8, 15, 8, 15), box(8, 0, 1, 15, 8, 8), box(1, 0, 8, 8, 8, 15));
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
			world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}
		return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
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
        InteractionResult result = InteractionResult.SUCCESS;
        if (entity == null) {
            result = InteractionResult.PASS;
        } else if (((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Blocks.AIR.asItem()
                && ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == Blocks.AIR.asItem()) {
            if ((blockstate.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _getip2 ? blockstate.getValue(_getip2) : -1) < 3) {
                {
                    int _value = (blockstate.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _getip4 ? blockstate.getValue(_getip4) : -1) + 1;
                    BlockPos _pos = BlockPos.containing(x, y, z);
                    BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                    if (_bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
                        ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
                }
            } else {
                ((LevelAccessor) world).setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((Entity) entity instanceof Player _player) {
                ItemStack _setstack = new ItemStack(CAItems.CARAMEL_CAKE_PIECE.get()).copy();
                _setstack.setCount(1);
                ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
            }
        } else if ((((Entity) entity instanceof LivingEntity _entity) ? _entity.getMainHandItem() : ItemStack.EMPTY).getItem() instanceof SwordItem
                || ((Entity) entity instanceof LivingEntity _entity ? _entity.getOffhandItem() : ItemStack.EMPTY).getItem() instanceof SwordItem
                || (((Entity) entity instanceof LivingEntity _entity) ? _entity.getMainHandItem() : ItemStack.EMPTY).getItem() instanceof AxeItem
                || ((Entity) entity instanceof LivingEntity _entity ? _entity.getOffhandItem() : ItemStack.EMPTY).getItem() instanceof AxeItem
                || ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).is(ItemTags.create(new ResourceLocation("forge:tools/knives")))
                || ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).is(ItemTags.create(new ResourceLocation("forge:tools/knives")))) {
            for (int index0 = 0; index0 < (4 - (blockstate.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _getip13 ? blockstate.getValue(_getip13) : -1)); index0++) {
                if ((LevelAccessor) world instanceof ServerLevel _level) {
                    ItemEntity entityToSpawn = new ItemEntity(_level, ((double) x + 0.5), ((double) y + 0.5), ((double) z + 0.5), new ItemStack(CAItems.CARAMEL_CAKE_PIECE.get()));
                    entityToSpawn.setPickUpDelay(10);
                    _level.addFreshEntity(entityToSpawn);
                }
            }
            world.destroyBlock(BlockPos.containing(x, y, z), false);
            if ((LevelAccessor) world instanceof Level _level) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.sheep.shear")), SoundSource.NEUTRAL, 1, 1);
            }
        } else {
            result = InteractionResult.PASS;
        }
        return result;
	}
}
