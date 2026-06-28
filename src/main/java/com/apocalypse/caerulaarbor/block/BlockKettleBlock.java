
package com.apocalypse.caerulaarbor.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.config.CaerulaConfigsConfiguration;
import com.apocalypse.caerulaarbor.init.CaerulaArborModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
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
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockKettleBlock extends Block implements SimpleWaterloggedBlock {
	public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 1);
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final BooleanProperty WATERED = BooleanProperty.create("watered");
	public static final BooleanProperty BOILING = BooleanProperty.create("boiling");
	public static final BooleanProperty NOODLED = BooleanProperty.create("noodled");

	public BlockKettleBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(0.5f, 2f).lightLevel(s -> (new Object() {
			public int getLightLevel() {
				if (s.getValue(BLOCKSTATE) == 1)
					return 0;
				return 0;
			}
		}.getLightLevel())).noOcclusion().isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERED, false).setValue(BOILING, false).setValue(NOODLED, false).setValue(WATERLOGGED, false));
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
                case NORTH -> Shapes.or(box(2.5, 3, 2.5, 13.5, 6, 13.5), box(3.5, 6, 3.5, 12.5, 9, 12.5), box(3.5, 2, 3.5, 12.5, 3, 12.5), box(4.5, 10, 4.5, 11.5, 12, 11.5), box(5.5, 12, 5.5, 10.5, 13, 10.5), box(7.5, 13, 7.5, 8.5, 14, 8.5),
						box(4.5, 1, 4.5, 11.5, 2, 11.5), box(3.5, 0, 3.5, 12.5, 1, 12.5), box(4.5, 9, 4.5, 11.5, 10, 11.5));
				case EAST -> Shapes.or(box(2.5, 3, 2.5, 13.5, 6, 13.5), box(3.5, 6, 3.5, 12.5, 9, 12.5), box(3.5, 2, 3.5, 12.5, 3, 12.5), box(4.5, 10, 4.5, 11.5, 12, 11.5), box(5.5, 12, 5.5, 10.5, 13, 10.5), box(7.5, 13, 7.5, 8.5, 14, 8.5),
						box(4.5, 1, 4.5, 11.5, 2, 11.5), box(3.5, 0, 3.5, 12.5, 1, 12.5), box(4.5, 9, 4.5, 11.5, 10, 11.5));
				case WEST -> Shapes.or(box(2.5, 3, 2.5, 13.5, 6, 13.5), box(3.5, 6, 3.5, 12.5, 9, 12.5), box(3.5, 2, 3.5, 12.5, 3, 12.5), box(4.5, 10, 4.5, 11.5, 12, 11.5), box(5.5, 12, 5.5, 10.5, 13, 10.5), box(7.5, 13, 7.5, 8.5, 14, 8.5),
						box(4.5, 1, 4.5, 11.5, 2, 11.5), box(3.5, 0, 3.5, 12.5, 1, 12.5), box(4.5, 9, 4.5, 11.5, 10, 11.5));
                default -> Shapes.or(box(2.5, 3, 2.5, 13.5, 6, 13.5), box(3.5, 6, 3.5, 12.5, 9, 12.5), box(3.5, 2, 3.5, 12.5, 3, 12.5), box(4.5, 10, 4.5, 11.5, 12, 11.5), box(5.5, 12, 5.5, 10.5, 13, 10.5), box(7.5, 13, 7.5, 8.5, 14, 8.5),
                        box(4.5, 1, 4.5, 11.5, 2, 11.5), box(3.5, 0, 3.5, 12.5, 1, 12.5), box(4.5, 9, 4.5, 11.5, 10, 11.5));
            };
		}
		return switch (state.getValue(FACING)) {
            case NORTH -> Shapes.or(box(2.5, 3, 2.5, 13.5, 6, 13.5), box(3.5, 6, 3.5, 12.5, 9, 12.5), box(3.5, 2, 3.5, 12.5, 3, 12.5), box(4.5, 10, 4.5, 11.5, 12, 11.5), box(5.5, 12, 5.5, 10.5, 13, 10.5), box(7.5, 13, 7.5, 8.5, 14, 8.5),
					box(4.5, 1, 4.5, 11.5, 2, 11.5), box(3.5, 0, 3.5, 12.5, 1, 12.5), box(4.5, 9, 4.5, 11.5, 10, 11.5));
			case EAST -> Shapes.or(box(2.5, 3, 2.5, 13.5, 6, 13.5), box(3.5, 6, 3.5, 12.5, 9, 12.5), box(3.5, 2, 3.5, 12.5, 3, 12.5), box(4.5, 10, 4.5, 11.5, 12, 11.5), box(5.5, 12, 5.5, 10.5, 13, 10.5), box(7.5, 13, 7.5, 8.5, 14, 8.5),
					box(4.5, 1, 4.5, 11.5, 2, 11.5), box(3.5, 0, 3.5, 12.5, 1, 12.5), box(4.5, 9, 4.5, 11.5, 10, 11.5));
			case WEST -> Shapes.or(box(2.5, 3, 2.5, 13.5, 6, 13.5), box(3.5, 6, 3.5, 12.5, 9, 12.5), box(3.5, 2, 3.5, 12.5, 3, 12.5), box(4.5, 10, 4.5, 11.5, 12, 11.5), box(5.5, 12, 5.5, 10.5, 13, 10.5), box(7.5, 13, 7.5, 8.5, 14, 8.5),
					box(4.5, 1, 4.5, 11.5, 2, 11.5), box(3.5, 0, 3.5, 12.5, 1, 12.5), box(4.5, 9, 4.5, 11.5, 10, 11.5));
            default -> Shapes.or(box(2.5, 3, 2.5, 13.5, 6, 13.5), box(3.5, 6, 3.5, 12.5, 9, 12.5), box(3.5, 2, 3.5, 12.5, 3, 12.5), box(4.5, 10, 4.5, 11.5, 12, 11.5), box(5.5, 12, 5.5, 10.5, 13, 10.5), box(7.5, 13, 7.5, 8.5, 14, 8.5),
                    box(4.5, 1, 4.5, 11.5, 2, 11.5), box(3.5, 0, 3.5, 12.5, 1, 12.5), box(4.5, 9, 4.5, 11.5, 10, 11.5));
        };
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, WATERED, BOILING, NOODLED, WATERLOGGED, BLOCKSTATE);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
		return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(WATERED, false).setValue(BOILING, false).setValue(NOODLED, false).setValue(WATERLOGGED, flag);
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
		world.scheduleTick(pos, this, 40);
	}

	@Override
	public void neighborChanged(BlockState blockstate, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
		super.neighborChanged(blockstate, world, pos, neighborBlock, fromPos, moving);
		setBoiling(world, pos);
	}

	@Override
	public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
		super.tick(blockstate, world, pos, random);
		setBoiling(world, pos);
		world.scheduleTick(pos, this, 40);
	}

	private void setBoiling(LevelAccessor world, BlockPos pos) {
		boolean valid = false;
		BlockState lower = world.getBlockState(pos.below());
		if (lower.is(BlockTags.create(new ResourceLocation(CaerulaArborMod.MODID, "heat")))) {
			if (lower.getBlock() == Blocks.CAMPFIRE || lower.getBlock() == Blocks.SOUL_CAMPFIRE) {
				valid = lower.getBlock().getStateDefinition().getProperty("lit") instanceof BooleanProperty litProperty && lower.getValue(litProperty);
			} else if (lower.getBlock() == Blocks.SMOKER) {
				valid = lower.getBlock().getStateDefinition().getProperty("lit") instanceof BooleanProperty litProperty && lower.getValue(litProperty);
			} else {
				valid = true;
			}
		} else {
			for (String blockId : CaerulaConfigsConfiguration.BOIL_WATER.get()) {
				if (ForgeRegistries.BLOCKS.getKey(lower.getBlock()).toString().equals(blockId)) {
					if (ForgeRegistries.BLOCKS.getKey(lower.getBlock()).toString().equals("create:blaze_burner")) {
						if (!(lower.getBlock().getStateDefinition().getProperty("blaze") instanceof EnumProperty<?> blazeProperty
								&& lower.getValue(blazeProperty).toString().equals("smouldering"))) {
							valid = true;
						}
					} else {
						valid = true;
					}
					break;
				}
			}
		}
		BlockState state = world.getBlockState(pos);
		int blockStateValue = valid ? 1 : 0;
		if (state.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty integerProperty && integerProperty.getPossibleValues().contains(blockStateValue)) {
			state = state.setValue(integerProperty, blockStateValue);
		}
		if (state.getBlock().getStateDefinition().getProperty("boiling") instanceof BooleanProperty booleanProperty) {
			state = state.setValue(booleanProperty, valid);
		}
		world.setBlock(pos, state, 3);
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
        boolean finished = false;
        if (entity != null) {
            if (!(blockstate.getBlock().getStateDefinition().getProperty("watered") instanceof BooleanProperty _getbp1 && blockstate.getValue(_getbp1))) {
                if (((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CaerulaArborModItems.CANNED_WATER.get()) {
                    {
                        BlockPos _pos = BlockPos.containing(x, y, z);
                        BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                        if (_bs.getBlock().getStateDefinition().getProperty("watered") instanceof BooleanProperty _booleanProp)
                            ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_booleanProp, true), 3);
                    }
                    if ((Entity) entity instanceof LivingEntity _entity) {
                        ItemStack _setstack = new ItemStack(CaerulaArborModItems.EMPTY_CAN.get()).copy();
                        _setstack.setCount(((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getCount());
                        _entity.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
                        if (_entity instanceof Player _player)
                            _player.getInventory().setChanged();
                    }
                    if ((LevelAccessor) world instanceof Level _level) {
                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.bucket.fill")), SoundSource.NEUTRAL, 1, 1);
                    }
                    result = InteractionResult.SUCCESS;
                    finished = true;
                } else if (((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CaerulaArborModItems.A_CUP_OF_WATER.get()) {
                    {
                        BlockPos _pos = BlockPos.containing(x, y, z);
                        BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                        if (_bs.getBlock().getStateDefinition().getProperty("watered") instanceof BooleanProperty _booleanProp)
                            ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_booleanProp, true), 3);
                    }
                    ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _setstack = new ItemStack(CaerulaArborModItems.OCEANGLASS_CUP.get()).copy();
                        _setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                    }
                    if ((LevelAccessor) world instanceof Level _level) {
                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.bucket.fill")), SoundSource.NEUTRAL, 1, 1);
                    }
                    result = InteractionResult.SUCCESS;
                    finished = true;
                }
            } else if (((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CaerulaArborModItems.CANNED_WATER.get()
                    || ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CaerulaArborModItems.A_CUP_OF_WATER.get()) {
                if ((Entity) entity instanceof Player _player && !_player.level().isClientSide())
                    _player.displayClientMessage(Component.literal((Component.translatable("block.caerula_arbor.kettle.filled").getString())), true);
            }
            if (!finished) {
                if (blockstate.getBlock().getStateDefinition().getProperty("watered") instanceof BooleanProperty _getbp25 && blockstate.getValue(_getbp25)
                        && blockstate.getBlock().getStateDefinition().getProperty("boiling") instanceof BooleanProperty _getbp27 && blockstate.getValue(_getbp27)) {
                    if (((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CaerulaArborModItems.EMPTY_CAN.get()
                            && blockstate.getBlock().getStateDefinition().getProperty("noodled") instanceof BooleanProperty _getbp31 && blockstate.getValue(_getbp31)) {
                        {
                            BlockPos _pos = BlockPos.containing(x, y, z);
                            BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                            if (_bs.getBlock().getStateDefinition().getProperty("watered") instanceof BooleanProperty _booleanProp)
                                ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_booleanProp, false), 3);
                        }
                        {
                            BlockPos _pos = BlockPos.containing(x, y, z);
                            BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                            if (_bs.getBlock().getStateDefinition().getProperty("noodled") instanceof BooleanProperty _booleanProp)
                                ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_booleanProp, false), 3);
                        }
                        ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                        if ((Entity) entity instanceof Player _player) {
                            ItemStack _setstack = new ItemStack(CaerulaArborModItems.CANNED_NOODLE.get()).copy();
                            _setstack.setCount(1);
                            ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                        }
                        result = InteractionResult.SUCCESS;
                        finished = true;
                    } else if (((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CaerulaArborModItems.EMPTY_CAN.get()
                            && !(blockstate.getBlock().getStateDefinition().getProperty("noodled") instanceof BooleanProperty _getbp41 && blockstate.getValue(_getbp41))) {
                        {
                            BlockPos _pos = BlockPos.containing(x, y, z);
                            BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                            if (_bs.getBlock().getStateDefinition().getProperty("watered") instanceof BooleanProperty _booleanProp)
                                ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_booleanProp, false), 3);
                        }
                        ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                        if ((Entity) entity instanceof Player _player) {
                            ItemStack _setstack = new ItemStack(CaerulaArborModItems.CANNED_BOILED_WATER.get()).copy();
                            _setstack.setCount(1);
                            ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                        }
                        result = InteractionResult.SUCCESS;
                        finished = true;
                    } else if (((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CaerulaArborModItems.INSTANT_NOODLE.get()
                            && !(blockstate.getBlock().getStateDefinition().getProperty("noodled") instanceof BooleanProperty _getbp50 && blockstate.getValue(_getbp50))) {
                        {
                            BlockPos _pos = BlockPos.containing(x, y, z);
                            BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                            if (_bs.getBlock().getStateDefinition().getProperty("noodled") instanceof BooleanProperty _booleanProp)
                                ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_booleanProp, true), 3);
                        }
                        ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                        if ((LevelAccessor) world instanceof Level _level) {
                            if (!_level.isClientSide()) {
                                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.redstone_torch.burnout")), SoundSource.NEUTRAL, 2, 1);
                            } else {
                                _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.redstone_torch.burnout")), SoundSource.NEUTRAL, 2, 1, false);
                            }
                        }
                        result = InteractionResult.SUCCESS;
                        finished = true;
                    } else if (((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CaerulaArborModItems.INSTANT_NOODLE.get()) {
                        if ((Entity) entity instanceof Player _player && !_player.level().isClientSide())
                            _player.displayClientMessage(Component.literal((Component.translatable("block.caerula_arbor.kettle.noodled").getString())), true);
                    }
                }
                if (!finished) {
                    if (blockstate.getBlock().getStateDefinition().getProperty("watered") instanceof BooleanProperty _getbp61 && blockstate.getValue(_getbp61)
                            && !(blockstate.getBlock().getStateDefinition().getProperty("boiling") instanceof BooleanProperty _getbp63 && blockstate.getValue(_getbp63))) {
                        if (((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CaerulaArborModItems.EMPTY_CAN.get()) {
                            ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                            if ((Entity) entity instanceof Player _player) {
                                ItemStack _setstack = new ItemStack(CaerulaArborModItems.CANNED_WATER.get()).copy();
                                _setstack.setCount(1);
                                ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                            }
                            {
                                BlockPos _pos = BlockPos.containing(x, y, z);
                                BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                                if (_bs.getBlock().getStateDefinition().getProperty("watered") instanceof BooleanProperty _booleanProp)
                                    ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_booleanProp, false), 3);
                            }
                            if ((LevelAccessor) world instanceof Level _level) {
                                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.bottle.fill")), SoundSource.NEUTRAL, 1, 1);
                            }
                            result = InteractionResult.SUCCESS;
                            finished = true;
                        } else if (((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CaerulaArborModItems.OCEANGLASS_CUP.get()) {
                            ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                            if ((Entity) entity instanceof Player _player) {
                                ItemStack _setstack = new ItemStack(CaerulaArborModItems.A_CUP_OF_WATER.get()).copy();
                                _setstack.setCount(1);
                                ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                            }
                            {
                                BlockPos _pos = BlockPos.containing(x, y, z);
                                BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                                if (_bs.getBlock().getStateDefinition().getProperty("watered") instanceof BooleanProperty _booleanProp)
                                    ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_booleanProp, false), 3);
                            }
                            if ((LevelAccessor) world instanceof Level _level) {
                                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.bottle.fill")), SoundSource.NEUTRAL, 1, 1);
                            }
                            result = InteractionResult.SUCCESS;
                            finished = true;
                        }
                    }
                    if (!finished) {
                        if (blockstate.getBlock().getStateDefinition().getProperty("watered") instanceof BooleanProperty _getbp81 && blockstate.getValue(_getbp81)
                                && !(blockstate.getBlock().getStateDefinition().getProperty("noodled") instanceof BooleanProperty _getbp83 && blockstate.getValue(_getbp83))) {
                            if (((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Blocks.SPONGE.asItem()) {
                                ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                                {
                                    BlockPos _pos = BlockPos.containing(x, y, z);
                                    BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                                    if (_bs.getBlock().getStateDefinition().getProperty("watered") instanceof BooleanProperty _booleanProp)
                                        ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_booleanProp, false), 3);
                                }
                                if ((Entity) entity instanceof Player _player) {
                                    ItemStack _setstack = new ItemStack(Blocks.WET_SPONGE).copy();
                                    _setstack.setCount(1);
                                    ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                                }
                                if ((LevelAccessor) world instanceof Level _level) {
                                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("intentionally_empty")), SoundSource.NEUTRAL, 1, 1);
                                }
                                result = InteractionResult.SUCCESS;
                            } else if (((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CaerulaArborModItems.CANNED_LAVA.get()) {
                                ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                                {
                                    BlockPos _pos = BlockPos.containing(x, y, z);
                                    BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                                    if (_bs.getBlock().getStateDefinition().getProperty("watered") instanceof BooleanProperty _booleanProp)
                                        ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_booleanProp, false), 3);
                                }
                                if ((Entity) entity instanceof Player _player) {
                                    ItemStack _setstack = new ItemStack(CaerulaArborModItems.OBISIDIAN_BALL.get()).copy();
                                    _setstack.setCount(1);
                                    ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                                }
                                if ((Entity) entity instanceof Player _player) {
                                    ItemStack _setstack = new ItemStack(CaerulaArborModItems.EMPTY_CAN.get()).copy();
                                    _setstack.setCount(1);
                                    ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                                }
                                if ((LevelAccessor) world instanceof Level _level) {
                                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.lava.extinguish")), SoundSource.NEUTRAL, 1, 1);
                                }
                                result = InteractionResult.SUCCESS;
                            } else if (((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CaerulaArborModItems.REAL_EGG.get()
                                    && blockstate.getBlock().getStateDefinition().getProperty("boiling") instanceof BooleanProperty _getbp104 && blockstate.getValue(_getbp104)) {
                                {
                                    BlockPos _pos = BlockPos.containing(x, y, z);
                                    BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                                    if (_bs.getBlock().getStateDefinition().getProperty("watered") instanceof BooleanProperty _booleanProp)
                                        ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_booleanProp, false), 3);
                                }
                                ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                                if ((Entity) entity instanceof Player _player) {
                                    ItemStack _setstack = new ItemStack(CaerulaArborModItems.BOILED_EGG.get()).copy();
                                    _setstack.setCount(1);
                                    ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                                }
                                if ((LevelAccessor) world instanceof Level _level) {
                                    if (!_level.isClientSide()) {
                                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.brewing_stand.brew")), SoundSource.NEUTRAL, 2, 1);
                                    } else {
                                        _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.brewing_stand.brew")), SoundSource.NEUTRAL, 2, 1, false);
                                    }
                                }
                                result = InteractionResult.SUCCESS;
                            }
                        }
                    }
                }
            }
        }
        return result;
	}
}
