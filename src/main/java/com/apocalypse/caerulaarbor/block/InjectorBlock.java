package com.apocalypse.caerulaarbor.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.init.CaerulaArborModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;

public class InjectorBlock extends Block implements SimpleWaterloggedBlock {
	public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 1);
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public InjectorBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(75f, 1024f).lightLevel(s -> (new Object() {
			public int getLightLevel() {
				if (s.getValue(BLOCKSTATE) == 1)
					return 0;
				return 0;
			}
		}.getLightLevel())).requiresCorrectToolForDrops().noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((bs, br, bp) -> false));
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
				default -> box(1, 0, 1, 15, 15.75, 15);
				case NORTH -> box(1, 0, 1, 15, 15.75, 15);
				case EAST -> box(1, 0, 1, 15, 15.75, 15);
				case WEST -> box(1, 0, 1, 15, 15.75, 15);
			};
		}
		return switch (state.getValue(FACING)) {
			default -> box(1, 0, 1, 15, 15.75, 15);
			case NORTH -> box(1, 0, 1, 15, 15.75, 15);
			case EAST -> box(1, 0, 1, 15, 15.75, 15);
			case WEST -> box(1, 0, 1, 15, 15.75, 15);
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
        InteractionResult result = InteractionResult.PASS;
        if (entity != null) {
            ItemStack res = ItemStack.EMPTY;
            ItemStack input = ItemStack.EMPTY;
            double stats = 0;
            input = ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).copy();
            stats = blockstate.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _getip2 ? blockstate.getValue(_getip2) : -1;
            if (stats == 0) {
                if (input.getItem() == CaerulaArborModItems.TARGETED_BASE.get()) {
                    ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                    if ((LevelAccessor) world instanceof Level _level) {
                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.glow_item_frame.add_item")), SoundSource.BLOCKS, 1, 1);
                    }
                    {
                        int _value = 1;
                        BlockPos _pos = BlockPos.containing(x, y, z);
                        BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                        if (_bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
                            ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
                    }
                    result = InteractionResult.SUCCESS;
                } else if (input.is(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "gene")))) {
                    if ((Entity) entity instanceof Player _player && !_player.level().isClientSide())
                        _player.displayClientMessage(Component.literal((Component.translatable("block.caerula_arbor.injector.note").getString())), true);
                }
            } else if (stats == 1) {
                if (input.getItem() == CaerulaArborModItems.HUNTER_GENE_SKADI.get()) {
                    res = new ItemStack(CaerulaArborModItems.TARGETED_TRANSMITTER_SKADI.get()).copy();
                } else if (input.getItem() == CaerulaArborModItems.HUNTER_GENE_ULPIANS.get()) {
                    res = new ItemStack(CaerulaArborModItems.TARGETED_TRANSMITTER_ULPIANS.get()).copy();
                } else if (input.getItem() == CaerulaArborModItems.HUNTER_GENE_GLADIIA.get()) {
                    res = new ItemStack(CaerulaArborModItems.TARGETED_TRANSMITTER_GLADIIA.get()).copy();
                } else if (input.getItem() == CaerulaArborModItems.TARGETED_BASE.get()) {
                    if ((Entity) entity instanceof Player _player && !_player.level().isClientSide())
                        _player.displayClientMessage(Component.literal((Component.translatable("block.caerula_arbor.injector.have").getString())), true);
                } else if (input.getItem() == CaerulaArborModItems.HUNTER_GENE_SPECTER.get()) {
                    res = new ItemStack(CaerulaArborModItems.TARGETED_TRANSMITTER_SPECTER.get()).copy();
                }
                if (!(res.getItem() == ItemStack.EMPTY.getItem())) {
                    ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                    {
                        int _value = 0;
                        BlockPos _pos = BlockPos.containing(x, y, z);
                        BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                        if (_bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
                            ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
                    }
                    if ((LevelAccessor) world instanceof Level _level) {
                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "notice")), SoundSource.BLOCKS, 2, 1);
                    }
                    if ((LevelAccessor) world instanceof ServerLevel _level) {
                        ItemEntity entityToSpawn = new ItemEntity(_level, ((double) x + 0.5), ((double) y + 1), ((double) z + 0.5), res);
                        entityToSpawn.setPickUpDelay(10);
                        entityToSpawn.setUnlimitedLifetime();
                        _level.addFreshEntity(entityToSpawn);
                    }
                    result = InteractionResult.SUCCESS;
                }
            }
        }
        return result;
	}
}
