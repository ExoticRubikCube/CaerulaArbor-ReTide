package com.susen36.caerulaarbor.block;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.SkadiCorruptedEntity;
import com.susen36.caerulaarbor.entity.SkadiEntity;
import com.susen36.caerulaarbor.entity.isharmla.IsharmlaEntity;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.init.CASounds;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Comparator;

public class IsharmlaRemainBlock extends Block {
	public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 1);
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final EnumProperty<AttachFace> FACE = FaceAttachedHorizontalDirectionalBlock.FACE;

	public IsharmlaRemainBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).sound(SoundType.METAL).strength(-1, 3600000).lightLevel(s -> (new Object() {
			public int getLightLevel() {
				if (s.getValue(BLOCKSTATE) == 1)
					return 12;
				return 8;
			}
		}.getLightLevel())).noOcclusion().pushReaction(PushReaction.BLOCK).hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true).isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FACE, AttachFace.WALL));
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
                case NORTH -> switch (state.getValue(FACE)) {
					case FLOOR -> box(0, 0, 0, 16, 16, 16);
					case WALL -> box(0, 0, 0, 16, 16, 16);
					case CEILING -> box(0, 0, 0, 16, 16, 16);
				};
				case EAST -> switch (state.getValue(FACE)) {
					case FLOOR -> box(0, 0, 0, 16, 16, 16);
					case WALL -> box(0, 0, 0, 16, 16, 16);
					case CEILING -> box(0, 0, 0, 16, 16, 16);
				};
				case WEST -> switch (state.getValue(FACE)) {
					case FLOOR -> box(0, 0, 0, 16, 16, 16);
					case WALL -> box(0, 0, 0, 16, 16, 16);
					case CEILING -> box(0, 0, 0, 16, 16, 16);
				};
                default -> switch (state.getValue(FACE)) {
                    case FLOOR -> box(0, 0, 0, 16, 16, 16);
                    case WALL -> box(0, 0, 0, 16, 16, 16);
                    case CEILING -> box(0, 0, 0, 16, 16, 16);
                };
            };
		}
		return switch (state.getValue(FACING)) {
            case NORTH -> switch (state.getValue(FACE)) {
				case FLOOR -> box(0, 0, 0, 16, 16, 16);
				case WALL -> box(0, 0, 0, 16, 16, 16);
				case CEILING -> box(0, 0, 0, 16, 16, 16);
			};
			case EAST -> switch (state.getValue(FACE)) {
				case FLOOR -> box(0, 0, 0, 16, 16, 16);
				case WALL -> box(0, 0, 0, 16, 16, 16);
				case CEILING -> box(0, 0, 0, 16, 16, 16);
			};
			case WEST -> switch (state.getValue(FACE)) {
				case FLOOR -> box(0, 0, 0, 16, 16, 16);
				case WALL -> box(0, 0, 0, 16, 16, 16);
				case CEILING -> box(0, 0, 0, 16, 16, 16);
			};
            default -> switch (state.getValue(FACE)) {
                case FLOOR -> box(0, 0, 0, 16, 16, 16);
                case WALL -> box(0, 0, 0, 16, 16, 16);
                case CEILING -> box(0, 0, 0, 16, 16, 16);
            };
        };
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, FACE, BLOCKSTATE);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		if (context.getClickedFace().getAxis() == Direction.Axis.Y)
			return super.getStateForPlacement(context).setValue(FACE, context.getClickedFace().getOpposite() == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR).setValue(FACING, context.getHorizontalDirection());
		return super.getStateForPlacement(context).setValue(FACE, AttachFace.WALL).setValue(FACING, context.getClickedFace());
	}

	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	@Override
	public float getEnchantPowerBonus(BlockState state, LevelReader world, BlockPos pos) {
		return 4f;
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack itemstack, BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
		super.useItemOn(itemstack, blockstate, world, pos, entity, hand, hit);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
        ItemInteractionResult result = ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        double stat;
        Entity skadi;
        if (world.getEntitiesOfClass(SkadiCorruptedEntity.class, AABB.ofSize(new Vec3(x, y, z), 64, 64, 64), e -> true).isEmpty()) {
            stat = blockstate.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty getip3 ? blockstate.getValue(getip3) : -1;
            if (stat == 0) {
                if (((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CAItems.WHIRL_EYE.get()
                        && ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == CAItems.CAERULA_HEART.get()) {
                    skadi = world.getEntitiesOfClass(SkadiEntity.class, AABB.ofSize(new Vec3(x, y, z), 64, 64, 64), e -> true).stream().sorted(new Object() {
                        Comparator<Entity> compareDistOf(double x, double y, double z) {
                            return Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(x, y, z));
                        }
                    }.compareDistOf((double) x, (double) y, (double) z)).findFirst().orElse(null);
                    if (skadi == null) {
                        if ((Entity) entity instanceof Player player && !player.level().isClientSide())
                            player.displayClientMessage(Component.literal((Component.translatable("block.caerula_arbor.isharmla_remain.fail").getString())), true);
                        result = ItemInteractionResult.FAIL;
                    } else {
                        if (skadi instanceof LivingEntity livingEntity && !entity.level().isClientSide())
                            entity.addEffect(new MobEffectInstance(CAMobEffects.ISHARMLA_CURSE, 99999, 0));
                        IsharmlaEntity.sendLinkParticlesToEntity(world, x, y, z, skadi);
                        if ((LevelAccessor) world instanceof Level level) {
                                level.playSound(null, BlockPos.containing(x, y, z), CASounds.ISHARMLA_TEAR_PLACE.get(), SoundSource.BLOCKS, 3, 1);
                        }
                        ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                        ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getOffhandItem() : ItemStack.EMPTY).shrink(1);
                        if ((Entity) entity instanceof ServerPlayer player) {
                            AdvancementHolder adv = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "unlock_calamity"));
                            AdvancementProgress ap = player.getAdvancements().getOrStartProgress(adv);
                            if (!ap.isDone()) {
                                for (String criteria : ap.getRemainingCriteria())
                                    player.getAdvancements().award(adv, criteria);
                            } 
                        }
                        for (int index0 = 0; index0 < 3; index0++) {
                            for (int index1 = 0; index1 < 3; index1++) {
                                for (int index2 = 0; index2 < 3; index2++) {
                                    {
                                        int value = 1;
                                        BlockPos blockPos = BlockPos.containing((double) x + index0 - 1, (double) y + index1 - 1, (double) z + index2 - 1);
                                        BlockState bs = world.getBlockState(pos);
                                        if (bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty integerProp && integerProp.getPossibleValues().contains(value))
                                            world.setBlock(pos, bs.setValue(integerProp, value), 3);
                                    }
                                }
                            }
                        }
                        result = ItemInteractionResult.SUCCESS;
                    }
                } else if (((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Blocks.AIR.asItem()
                        && ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == Blocks.AIR.asItem()) {
                    if ((Entity) entity instanceof Player player && !player.level().isClientSide())
                        player.displayClientMessage(Component.literal((Component.translatable("block.caerula_arbor.isharmla_remain.notice").getString())), true);
                }
            } else if (((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CAItems.TEAR_ISHARMLA.get()) {
                if ((LevelAccessor) world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.TOTEM_USE, SoundSource.BLOCKS, 3, 1);
                }
                ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                if ((LevelAccessor) world instanceof ServerLevel level)
                    level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, ((double) x + 0.5), ((double) y + 0.5), ((double) z + 0.5), 24, 1, 1, 1, 0.1);
                for (int index3 = 0; index3 < 3; index3++) {
                    for (int index4 = 0; index4 < 3; index4++) {
                        for (int index5 = 0; index5 < 3; index5++) {
                            {
                                int value = 0;
                                BlockPos blockPos = BlockPos.containing((double) x + index3 - 1, (double) y + index4 - 1, (double) z + index5 - 1);
                                BlockState bs = world.getBlockState(pos);
                                if (bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty integerProp && integerProp.getPossibleValues().contains(value))
                                    world.setBlock(pos, bs.setValue(integerProp, value), 3);
                            }
                        }
                    }
                }
                result = ItemInteractionResult.SUCCESS;
            } else if (((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Blocks.AIR.asItem()
                    && ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == Blocks.AIR.asItem()) {
                if ((Entity) entity instanceof Player player && !player.level().isClientSide())
                    player.displayClientMessage(Component.literal((Component.translatable("block.caerula_arbor.isharmla_remain.reset").getString())), true);
            }
        }
        return result;
	}
}