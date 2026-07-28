package com.susen36.caerulaarbor.block;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
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
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

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
	public PathType getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, Mob entity) {
		return PathType.BLOCKED;
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack itemstack, BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
		super.useItemOn(itemstack, blockstate, world, pos, entity, hand, hit);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		double hitX = hit.getLocation().x;
		double hitY = hit.getLocation().y;
		double hitZ = hit.getLocation().z;
		Direction direction = hit.getDirection();
        ItemInteractionResult result = ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (entity != null) {
            double stats;
            String res = "";
            ItemStack item;
            ItemStack output;
            stats = blockstate.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty getip1 ? blockstate.getValue(getip1) : -1;
            item = ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).copy();
            if (!(Math.abs((double) x - hitX) > 1)) {
                if (!(Math.abs((double) y - hitY) > 1)) {
                    if (!(Math.abs((double) z - hitZ) > 1)) {
                        if (stats == 0) {
                            if (item.getItem() == CAItems.PERSONNEL_TRANSPORTER.get()) {
                                if ((item.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getString("name")).equals("caerula_arbor:the_abandoned")) {
                                    {
                                        int value = 1;
                                        BlockPos blockPos = BlockPos.containing(x, y, z);
                                        BlockState bs = world.getBlockState(pos);
                                        if (bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty integerProp && integerProp.getPossibleValues().contains(value))
                                            world.setBlock(pos, bs.setValue(integerProp, value), 3);
                                    }
                                    ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                                    if ((LevelAccessor) world instanceof Level level) {
                                            level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.BLOCKS, 1, 1);
                                    }
                                    result = ItemInteractionResult.SUCCESS;
                                }
                            } else {
                                if ((Entity) entity instanceof Player player && !player.level().isClientSide())
                                    player.displayClientMessage(Component.literal((Component.translatable("block.caerula_arbor.operation_table.misuse").getString())), true);
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
                                if ((Entity) entity instanceof ServerPlayer player) {
                                    AdvancementHolder adv = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "to_slain_the_sea"));
                                    AdvancementProgress ap = player.getAdvancements().getOrStartProgress(adv);
                                    if (!ap.isDone()) {
                                        for (String criteria : ap.getRemainingCriteria())
                                            player.getAdvancements().award(adv, criteria);
                                    }
                                }
                                if ((LevelAccessor) world instanceof Level level) {
                                    if (!level.isClientSide()) {
                                        level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.ZOMBIE_CONVERTED_TO_DROWNED, SoundSource.BLOCKS, 1, 1);
                                    } else {
                                        level.playLocalSound(x, y, z, SoundEvents.ZOMBIE_CONVERTED_TO_DROWNED, SoundSource.BLOCKS, 1, 1, false);
                                    }
                                }
                                (entity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                                output = new ItemStack(CAItems.PERSONNEL_TRANSPORTER.get()).copy();
                                String resultName = res;
                                CustomData.update(DataComponents.CUSTOM_DATA, output, tag -> tag.putString("name", resultName));
                                CustomData.update(DataComponents.CUSTOM_DATA, output, tag -> tag.putDouble("perc", 0.5));
                                {
                                    int value = 0;
                                    BlockState bs = world.getBlockState(pos);
                                    if (bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty integerProp && integerProp.getPossibleValues().contains(value))
                                        world.setBlock(pos, bs.setValue(integerProp, value), 3);
                                }
                                if ((LevelAccessor) world instanceof ServerLevel level) {
                                    ItemEntity entityToSpawn = new ItemEntity(level, ((double) x + 0.5), ((double) y + 1), ((double) z + 0.5), output);
                                    entityToSpawn.setPickUpDelay(10);
                                    entityToSpawn.setUnlimitedLifetime();
                                    level.addFreshEntity(entityToSpawn);
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