package com.apocalypse.caerulaarbor.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.init.CAItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;

public class OperationTableBlock extends Block {
	public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 1);
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	public OperationTableBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(75f, 1024f).lightLevel(s -> (new Object() {
			public int getLightLevel() {
				if (s.getValue(BLOCKSTATE) == 1)
					return 0;
				return 0;
			}
		}.getLightLevel())).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return true;
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
                case NORTH -> box(0, 0, 0, 16, 16, 32);
				case EAST -> box(-16, 0, 0, 16, 16, 16);
				case WEST -> box(0, 0, 0, 32, 16, 16);
                default -> box(0, 0, -16, 16, 16, 16);
            };
		}
		return switch (state.getValue(FACING)) {
            case NORTH -> box(0, 0, 0, 16, 16, 32);
			case EAST -> box(-16, 0, 0, 16, 16, 16);
			case WEST -> box(0, 0, 0, 32, 16, 16);
            default -> box(0, 0, -16, 16, 16, 16);
        };
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, BLOCKSTATE);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	@Override
	public BlockPathTypes getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, Mob entity) {
		return BlockPathTypes.BLOCKED;
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
            double stats;
            String res = "";
            ItemStack item;
            ItemStack output;
            stats = blockstate.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _getip1 ? blockstate.getValue(_getip1) : -1;
            item = ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).copy();
            if (!(Math.abs((double) x - hitX) > 1)) {
                if (!(Math.abs((double) y - hitY) > 1)) {
                    if (!(Math.abs((double) z - hitZ) > 1)) {
                        if (stats == 0) {
                            if (item.getItem() == CAItems.PERSONNEL_TRANSPORTER.get()) {
                                if ((item.getOrCreateTag().getString("name")).equals("caerula_arbor:the_abandoned")) {
                                    {
                                        int _value = 1;
                                        BlockPos _pos = BlockPos.containing(x, y, z);
                                        BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                                        if (_bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
                                            ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
                                    }
                                    ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                                    if ((LevelAccessor) world instanceof Level _level) {
                                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.armor.equip_leather")), SoundSource.BLOCKS, 1, 1);
                                    }
                                    result = InteractionResult.SUCCESS;
                                }
                            } else {
                                if ((Entity) entity instanceof Player _player && !_player.level().isClientSide())
                                    _player.displayClientMessage(Component.literal((Component.translatable("block.caerula_arbor.operation_table.misuse").getString())), true);
                            }
                        } else if (stats == 1) {
                            if (item.getItem() == CAItems.OPERATION_KIT_SKADI.get()) {
                                res = "caerula_arbor:skadi";
                            } else if (item.getItem() == CAItems.OPERATION_KIT_ULPIANS.get()) {
                                res = "caerula_arbor:ulpians";
                            } else if (item.getItem() == CAItems.OPERATION_KIT_GLADIIA.get()) {
                                res = "caerula_arbor:gladiia";
                            } else if (item.getItem() == CAItems.OPERATION_KIT_SPECTER.get()) {
                                res = "caerula_arbor:specter";
                            }
                            if (!(res).isEmpty()) {
                                if ((Entity) entity instanceof ServerPlayer _player) {
                                    Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "to_slain_the_sea"));
                                    AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
                                    if (!_ap.isDone()) {
                                        for (String criteria : _ap.getRemainingCriteria())
                                            _player.getAdvancements().award(_adv, criteria);
                                    }
                                }
                                if ((LevelAccessor) world instanceof Level _level) {
                                    if (!_level.isClientSide()) {
                                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.zombie.converted_to_drowned")), SoundSource.BLOCKS, 1, 1);
                                    } else {
                                        _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.zombie.converted_to_drowned")), SoundSource.BLOCKS, 1, 1, false);
                                    }
                                }
                                ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                                output = new ItemStack(CAItems.PERSONNEL_TRANSPORTER.get()).copy();
                                output.getOrCreateTag().putString("name", res);
                                output.getOrCreateTag().putDouble("perc", 0.5);
                                {
                                    int _value = 0;
                                    BlockPos _pos = BlockPos.containing(x, y, z);
                                    BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                                    if (_bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
                                        ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
                                }
                                if ((LevelAccessor) world instanceof ServerLevel _level) {
                                    ItemEntity entityToSpawn = new ItemEntity(_level, ((double) x + 0.5), ((double) y + 1), ((double) z + 0.5), output);
                                    entityToSpawn.setPickUpDelay(10);
                                    entityToSpawn.setUnlimitedLifetime();
                                    _level.addFreshEntity(entityToSpawn);
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
	}
}
