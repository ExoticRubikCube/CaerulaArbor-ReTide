package com.apocalypse.caerulaarbor.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.capability.player.PlayerVariable;
import com.apocalypse.caerulaarbor.init.CAItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class GoldenChaliseBlock extends Block implements SimpleWaterloggedBlock {
	public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 2);
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public GoldenChaliseBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(8f, 1200f).lightLevel(s -> (new Object() {
			public int getLightLevel() {
				if (s.getValue(BLOCKSTATE) == 1)
					return 2;
				if (s.getValue(BLOCKSTATE) == 2)
					return 4;
				return 0;
			}
		}.getLightLevel())).noOcclusion().isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, BlockGetter level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("block.caerula_arbor.golden_chalise.description_0"));
		list.add(Component.translatable("block.caerula_arbor.golden_chalise.description_1"));
		list.add(Component.translatable("block.caerula_arbor.golden_chalise.description_2"));
		list.add(Component.translatable("block.caerula_arbor.golden_chalise.description_3"));
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
                case NORTH -> box(4.5, 0, 4.5, 11.5, 10.5, 11.5);
				case EAST -> box(4.5, 0, 4.5, 11.5, 10.5, 11.5);
				case WEST -> box(4.5, 0, 4.5, 11.5, 10.5, 11.5);
                default -> box(4.5, 0, 4.5, 11.5, 10.5, 11.5);
            };
		}
		if (state.getValue(BLOCKSTATE) == 2) {
			return switch (state.getValue(FACING)) {
                case NORTH -> box(4.5, 0, 4.5, 11.5, 11, 11.5);
				case EAST -> box(4.5, 0, 4.5, 11.5, 11, 11.5);
				case WEST -> box(4.5, 0, 4.5, 11.5, 11, 11.5);
                default -> box(4.5, 0, 4.5, 11.5, 11, 11.5);
            };
		}
		return switch (state.getValue(FACING)) {
            case NORTH -> box(4.5, 0, 4.5, 11.5, 10, 11.5);
			case EAST -> box(4.5, 0, 4.5, 11.5, 10, 11.5);
			case WEST -> box(4.5, 0, 4.5, 11.5, 10, 11.5);
            default -> box(4.5, 0, 4.5, 11.5, 10, 11.5);
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
        InteractionResult result;
        if (direction == null || entity == null) {
            result = InteractionResult.PASS;
        } else {
            InteractionResult res = InteractionResult.PASS;
            String output;
            double balance;
            double amount;
            balance = (((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).plauyer_balance;
            if (balance >= 131072) {
                if ((Entity) entity instanceof Player _player && !_player.level().isClientSide())
                    _player.displayClientMessage(Component.literal((Component.translatable("block.golden_chalise.inquiry").getString())), true);
                res = InteractionResult.PASS;
            } else {
                if (((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CAItems.REDSTONE_INGOT.get()) {
                    if (direction == Direction.UP) {
                        amount = ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getCount();
                        balance = Math.min(balance + amount, 131072);
                        {
                            double _setval = balance;
                            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                capability.plauyer_balance = _setval;
                                capability.syncPlayerVariables(entity);
                            });
                        }
                        ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).setCount(0);
                        if ((LevelAccessor) world instanceof Level _level) {
                                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "money_in")), SoundSource.BLOCKS, 1, 1);
                        }
                        res = InteractionResult.SUCCESS;
                    } else {
                        balance = balance + 1;
                        {
                            double _setval = balance;
                            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                capability.plauyer_balance = _setval;
                                capability.syncPlayerVariables(entity);
                            });
                        }
                        if ((Entity) entity instanceof Player _player) {
                            ItemStack _stktoremove = new ItemStack(CAItems.REDSTONE_INGOT.get());
                            _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                        }
                        if ((LevelAccessor) world instanceof Level _level) {
                                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "money_in")), SoundSource.BLOCKS, 1, 1);
                        }
                        res = InteractionResult.SUCCESS;
                    }
                } else if (((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CAItems.REDSTONIUM.get()) {
                    if (direction == Direction.UP) {
                        amount = ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getCount();
                        balance = Math.min(balance + amount * 9, 131072);
                        {
                            double _setval = balance;
                            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                capability.plauyer_balance = _setval;
                                capability.syncPlayerVariables(entity);
                            });
                        }
                        ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).setCount(0);
                        if ((LevelAccessor) world instanceof Level _level) {
                                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "money_in")), SoundSource.BLOCKS, 1, 1);
                        }
                        res = InteractionResult.SUCCESS;
                    } else {
                        balance = Math.min(balance + 9, 131072);
                        {
                            double _setval = balance;
                            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                capability.plauyer_balance = _setval;
                                capability.syncPlayerVariables(entity);
                            });
                        }
                        if ((Entity) entity instanceof Player _player) {
                            ItemStack _stktoremove = new ItemStack(CAItems.REDSTONIUM.get());
                            _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                        }
                        if ((LevelAccessor) world instanceof Level _level) {
                                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "money_in")), SoundSource.BLOCKS, 1, 1);
                        }
                        res = InteractionResult.SUCCESS;
                    }
                } else if (((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == ItemStack.EMPTY.getItem()) {
                    if (entity.isShiftKeyDown()) {
                        output = Component.translatable("block.golden_chalise.inquiry").getString() + Math.round(balance) + " /131072";
                        if ((Entity) entity instanceof Player _player && !_player.level().isClientSide())
                            _player.displayClientMessage(Component.literal(output), true);
                    } else {
                        if (direction == Direction.UP) {
                            if (balance >= 9) {
                                balance = balance - 9;
                                {
                                    double _setval = balance;
                                    ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                        capability.plauyer_balance = _setval;
                                        capability.syncPlayerVariables(entity);
                                    });
                                }
                                if ((LevelAccessor) world instanceof ServerLevel _level) {
                                    ItemEntity entityToSpawn = new ItemEntity(_level, ((double) x + 0.5), ((double) y + 0.75), ((double) z + 0.5), new ItemStack(CAItems.REDSTONIUM.get()));
                                    entityToSpawn.setPickUpDelay(10);
                                    _level.addFreshEntity(entityToSpawn);
                                }
                                if ((LevelAccessor) world instanceof Level _level) {
                                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "money_out")), SoundSource.BLOCKS, 1, 1);
                                }
                                res = InteractionResult.SUCCESS;
                            } else if (balance >= 1) {
                                for (int index0 = 0; index0 < (int) balance; index0++) {
                                    if ((LevelAccessor) world instanceof ServerLevel _level) {
                                        ItemEntity entityToSpawn = new ItemEntity(_level, ((double) x + 0.5), ((double) y + 0.75), ((double) z + 0.5), new ItemStack(CAItems.REDSTONE_INGOT.get()));
                                        entityToSpawn.setPickUpDelay(10);
                                        _level.addFreshEntity(entityToSpawn);
                                    }
                                }
                                balance = 0;
                                {
                                    double _setval = balance;
                                    ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                        capability.plauyer_balance = _setval;
                                        capability.syncPlayerVariables(entity);
                                    });
                                }
                                if ((LevelAccessor) world instanceof Level _level) {
                                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "money_out")), SoundSource.BLOCKS, 1, 1);
                                }
                                res = InteractionResult.SUCCESS;
                            }
                        } else if (!(direction == Direction.DOWN) && balance >= 1) {
                            balance = balance - 1;
                            {
                                double _setval = balance;
                                ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                    capability.plauyer_balance = _setval;
                                    capability.syncPlayerVariables(entity);
                                });
                            }
                            if ((LevelAccessor) world instanceof ServerLevel _level) {
                                ItemEntity entityToSpawn = new ItemEntity(_level, ((double) x + 0.5), ((double) y + 0.75), ((double) z + 0.5), new ItemStack(CAItems.REDSTONE_INGOT.get()));
                                entityToSpawn.setPickUpDelay(10);
                                _level.addFreshEntity(entityToSpawn);
                            }
                            if ((LevelAccessor) world instanceof Level _level) {
                                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "money_out")), SoundSource.BLOCKS, 1, 1);
                            }
                            res = InteractionResult.SUCCESS;
                        }
                    }
                }
                if (balance > 128) {
                    {
                        int _value = 2;
                        BlockPos _pos = BlockPos.containing(x, y, z);
                        BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                        if (_bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
                            ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
                    }
                } else if (balance > 64) {
                    {
                        int _value = 1;
                        BlockPos _pos = BlockPos.containing(x, y, z);
                        BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                        if (_bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
                            ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
                    }
                } else {
                    {
                        int _value = 0;
                        BlockPos _pos = BlockPos.containing(x, y, z);
                        BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                        if (_bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
                            ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
                    }
                }
            }
            result = res;
        }
        return result;
	}
}
