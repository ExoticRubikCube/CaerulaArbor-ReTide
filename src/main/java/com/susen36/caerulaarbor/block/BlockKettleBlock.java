
package com.susen36.caerulaarbor.block;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.init.CAConfigs;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
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
import net.neoforged.neoforge.items.ItemHandlerHelper;

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
		if (lower.is(BlockTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "heat")))) {
			if (lower.getBlock() == Blocks.CAMPFIRE || lower.getBlock() == Blocks.SOUL_CAMPFIRE) {
				valid = lower.getBlock().getStateDefinition().getProperty("lit") instanceof BooleanProperty litProperty && lower.getValue(litProperty);
			} else if (lower.getBlock() == Blocks.SMOKER) {
				valid = lower.getBlock().getStateDefinition().getProperty("lit") instanceof BooleanProperty litProperty && lower.getValue(litProperty);
			} else {
				valid = true;
			}
		} else {
			for (String blockId : CAConfigs.BOIL_WATER.get()) {
				if (BuiltInRegistries.BLOCK.getKey(lower.getBlock()).toString().equals(blockId)) {
					if (BuiltInRegistries.BLOCK.getKey(lower.getBlock()).toString().equals("create:blaze_burner")) {
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
	public ItemInteractionResult useItemOn(ItemStack itemstack, BlockState blockstate, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		super.useItemOn(itemstack, blockstate, world, pos, player, hand, hit);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
        ItemInteractionResult result = ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        boolean finished = false;
        if (!(blockstate.getBlock().getStateDefinition().getProperty("watered") instanceof BooleanProperty getbp1 && blockstate.getValue(getbp1))) {
            if (((player instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CAItems.CANNED_WATER.get())) {
                {
                    BlockState bs = world.getBlockState(pos);
                    if (bs.getBlock().getStateDefinition().getProperty("watered") instanceof BooleanProperty booleanProp)
                        world.setBlock(pos, bs.setValue(booleanProp, true), 3);
                }
                if (player instanceof LivingEntity livingEntity) {
                    ItemStack setstack = new ItemStack(CAItems.EMPTY_CAN.get()).copy();
                    setstack.setCount(((Entity) player instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getCount());
                    player.setItemInHand(InteractionHand.MAIN_HAND, setstack);
                    player.getInventory().setChanged();
                }
                if ((LevelAccessor) world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.BUCKET_FILL, SoundSource.NEUTRAL, 1, 1);
                }
                result = ItemInteractionResult.SUCCESS;
                finished = true;
            } else if (((Entity) player instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CAItems.A_CUP_OF_WATER.get()) {
                {
                    BlockPos blockPos = BlockPos.containing(x, y, z);
                    BlockState bs = ((LevelAccessor) world).getBlockState(pos);
                    if (bs.getBlock().getStateDefinition().getProperty("watered") instanceof BooleanProperty booleanProp)
                        ((LevelAccessor) world).setBlock(pos, bs.setValue(booleanProp, true), 3);
                }
                ((Entity) player instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                ItemStack setstack = new ItemStack(CAItems.OCEANGLASS_CUP.get()).copy();
                    setstack.setCount(1);
                    ItemHandlerHelper.giveItemToPlayer(player, setstack);
                if ((LevelAccessor) world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.BUCKET_FILL, SoundSource.NEUTRAL, 1, 1);
                }
                result = ItemInteractionResult.SUCCESS;
                finished = true;
            }
        } else if (((Entity) player instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CAItems.CANNED_WATER.get()
                || ((Entity) player instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CAItems.A_CUP_OF_WATER.get()) {
            if (!player.level().isClientSide())
                player.displayClientMessage(Component.literal((Component.translatable("block.caerula_arbor.kettle.filled").getString())), true);
        }
        if (!finished) {
            if (blockstate.getBlock().getStateDefinition().getProperty("watered") instanceof BooleanProperty getbp25 && blockstate.getValue(getbp25)
                    && blockstate.getBlock().getStateDefinition().getProperty("boiling") instanceof BooleanProperty getbp27 && blockstate.getValue(getbp27)) {
                if (((Entity) player instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CAItems.EMPTY_CAN.get()
                        && blockstate.getBlock().getStateDefinition().getProperty("noodled") instanceof BooleanProperty getbp31 && blockstate.getValue(getbp31)) {
                    {
                        BlockPos blockPos = BlockPos.containing(x, y, z);
                        BlockState bs = ((LevelAccessor) world).getBlockState(pos);
                        if (bs.getBlock().getStateDefinition().getProperty("watered") instanceof BooleanProperty booleanProp)
                            ((LevelAccessor) world).setBlock(pos, bs.setValue(booleanProp, false), 3);
                    }
                    {
                        BlockPos blockPos = BlockPos.containing(x, y, z);
                        BlockState bs = ((LevelAccessor) world).getBlockState(pos);
                        if (bs.getBlock().getStateDefinition().getProperty("noodled") instanceof BooleanProperty booleanProp)
                            ((LevelAccessor) world).setBlock(pos, bs.setValue(booleanProp, false), 3);
                    }
                    ((Entity) player instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                    ItemStack setstack = new ItemStack(CAItems.CANNED_NOODLE.get()).copy();
                        setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(player, setstack);
                    result = ItemInteractionResult.SUCCESS;
                    finished = true;
                } else if (((Entity) player instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CAItems.EMPTY_CAN.get()
                        && !(blockstate.getBlock().getStateDefinition().getProperty("noodled") instanceof BooleanProperty getbp41 && blockstate.getValue(getbp41))) {
                    {
                        BlockPos blockPos = BlockPos.containing(x, y, z);
                        BlockState bs = ((LevelAccessor) world).getBlockState(pos);
                        if (bs.getBlock().getStateDefinition().getProperty("watered") instanceof BooleanProperty booleanProp)
                            ((LevelAccessor) world).setBlock(pos, bs.setValue(booleanProp, false), 3);
                    }
                    ((Entity) player instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                    ItemStack setstack = new ItemStack(CAItems.CANNED_BOILED_WATER.get()).copy();
                        setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(player, setstack);
                    result = ItemInteractionResult.SUCCESS;
                    finished = true;
                } else if (((Entity) player instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CAItems.INSTANT_NOODLE.get()
                        && !(blockstate.getBlock().getStateDefinition().getProperty("noodled") instanceof BooleanProperty getbp50 && blockstate.getValue(getbp50))) {
                    {
                        BlockPos blockPos = BlockPos.containing(x, y, z);
                        BlockState bs = ((LevelAccessor) world).getBlockState(pos);
                        if (bs.getBlock().getStateDefinition().getProperty("noodled") instanceof BooleanProperty booleanProp)
                            ((LevelAccessor) world).setBlock(pos, bs.setValue(booleanProp, true), 3);
                    }
                    ((Entity) player instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                    if ((LevelAccessor) world instanceof Level level) {
                        if (!level.isClientSide()) {
                            level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.NEUTRAL, 2, 1);
                        } else {
                            level.playLocalSound(x, y, z, SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.NEUTRAL, 2, 1, false);
                        }
                    }
                    result = ItemInteractionResult.SUCCESS;
                    finished = true;
                } else if (((Entity) player instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CAItems.INSTANT_NOODLE.get()) {
                    if (!player.level().isClientSide())
                        player.displayClientMessage(Component.literal((Component.translatable("block.caerula_arbor.kettle.noodled").getString())), true);
                }
            }
            if (!finished) {
                if (blockstate.getBlock().getStateDefinition().getProperty("watered") instanceof BooleanProperty getbp61 && blockstate.getValue(getbp61)
                        && !(blockstate.getBlock().getStateDefinition().getProperty("boiling") instanceof BooleanProperty getbp63 && blockstate.getValue(getbp63))) {
                    if (((Entity) player instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CAItems.EMPTY_CAN.get()) {
                        ((Entity) player instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                        ItemStack setstack = new ItemStack(CAItems.CANNED_WATER.get()).copy();
                            setstack.setCount(1);
                            ItemHandlerHelper.giveItemToPlayer(player, setstack);
                            BlockPos blockPos = BlockPos.containing(x, y, z);
                            BlockState bs = ((LevelAccessor) world).getBlockState(pos);
                            if (bs.getBlock().getStateDefinition().getProperty("watered") instanceof BooleanProperty booleanProp)
                                ((LevelAccessor) world).setBlock(pos, bs.setValue(booleanProp, false), 3);
                        if ((LevelAccessor) world instanceof Level level) {
                                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1, 1);
                        }
                        result = ItemInteractionResult.SUCCESS;
                        finished = true;
                    } else if (((Entity) player instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CAItems.OCEANGLASS_CUP.get()) {
                        ((Entity) player instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                        ItemStack setstack = new ItemStack(CAItems.A_CUP_OF_WATER.get()).copy();
                            setstack.setCount(1);
                            ItemHandlerHelper.giveItemToPlayer(player, setstack);
                        BlockPos blockPos = BlockPos.containing(x, y, z);
                        BlockState bs = ((LevelAccessor) world).getBlockState(pos);
                        if (bs.getBlock().getStateDefinition().getProperty("watered") instanceof BooleanProperty booleanProp)
                            ((LevelAccessor) world).setBlock(pos, bs.setValue(booleanProp, false), 3);

                        if ((LevelAccessor) world instanceof Level level) {
                                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1, 1);
                        }
                        result = ItemInteractionResult.SUCCESS;
                        finished = true;
                    }
                }
                if (!finished) {
                    if (blockstate.getBlock().getStateDefinition().getProperty("watered") instanceof BooleanProperty getbp81 && blockstate.getValue(getbp81)
                            && !(blockstate.getBlock().getStateDefinition().getProperty("noodled") instanceof BooleanProperty getbp83 && blockstate.getValue(getbp83))) {
                        if (((Entity) player instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Blocks.SPONGE.asItem()) {
                            ((Entity) player instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                            {
                                BlockPos blockPos = BlockPos.containing(x, y, z);
                                BlockState bs = ((LevelAccessor) world).getBlockState(pos);
                                if (bs.getBlock().getStateDefinition().getProperty("watered") instanceof BooleanProperty booleanProp)
                                    ((LevelAccessor) world).setBlock(pos, bs.setValue(booleanProp, false), 3);
                            }
                            ItemStack setstack = new ItemStack(Blocks.WET_SPONGE).copy();
                                setstack.setCount(1);
                                ItemHandlerHelper.giveItemToPlayer(player, setstack);
                            if ((LevelAccessor) world instanceof Level level) {
                                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.EMPTY, SoundSource.NEUTRAL, 1, 1);
                            }
                            result = ItemInteractionResult.SUCCESS;
                        } else if (((Entity) player instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CAItems.CANNED_LAVA.get()) {
                            ((Entity) player instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                            {
                                BlockPos blockPos = BlockPos.containing(x, y, z);
                                BlockState bs = ((LevelAccessor) world).getBlockState(pos);
                                if (bs.getBlock().getStateDefinition().getProperty("watered") instanceof BooleanProperty booleanProp)
                                    ((LevelAccessor) world).setBlock(pos, bs.setValue(booleanProp, false), 3);
                            }
                            ItemStack setstack = new ItemStack(CAItems.OBISIDIAN_BALL.get()).copy();
                                setstack.setCount(1);
                                ItemHandlerHelper.giveItemToPlayer(player, setstack);
                            ItemStack setstack2 = new ItemStack(CAItems.EMPTY_CAN.get()).copy();
                                setstack2.setCount(1);
                                ItemHandlerHelper.giveItemToPlayer(player, setstack2);
                            if ((LevelAccessor) world instanceof Level level) {
                                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.LAVA_EXTINGUISH, SoundSource.NEUTRAL, 1, 1);
                            }
                            result = ItemInteractionResult.SUCCESS;
                        } else if (((Entity) player instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CAItems.REAL_EGG.get()
                                && blockstate.getBlock().getStateDefinition().getProperty("boiling") instanceof BooleanProperty getbp104 && blockstate.getValue(getbp104)) {
                            {
                                BlockPos blockPos = BlockPos.containing(x, y, z);
                                BlockState bs = ((LevelAccessor) world).getBlockState(pos);
                                if (bs.getBlock().getStateDefinition().getProperty("watered") instanceof BooleanProperty booleanProp)
                                    ((LevelAccessor) world).setBlock(pos, bs.setValue(booleanProp, false), 3);
                            }
                            ((Entity) player instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                            ItemStack setstack = new ItemStack(CAItems.BOILED_EGG.get()).copy();
                            setstack.setCount(1);
                            ItemHandlerHelper.giveItemToPlayer(player, setstack);
                            if ((LevelAccessor) world instanceof Level level) {
                                if (!level.isClientSide()) {
                                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.BREWING_STAND_BREW, SoundSource.NEUTRAL, 2, 1);
                                } else {
                                    level.playLocalSound(x, y, z, SoundEvents.BREWING_STAND_BREW, SoundSource.NEUTRAL, 2, 1, false);
                                }
                            }
                            result = ItemInteractionResult.SUCCESS;
                        }
                    }
                }
            }
        }
        return result;
	}
}