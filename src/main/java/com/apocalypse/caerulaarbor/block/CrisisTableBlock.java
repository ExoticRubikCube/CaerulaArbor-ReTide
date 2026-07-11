
package com.apocalypse.caerulaarbor.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CABlockEntities;
import com.apocalypse.caerulaarbor.init.CABlocks;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CASounds;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CrisisTableBlock extends BaseEntityBlock implements EntityBlock {
	public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 1);
	public static final IntegerProperty DATA_ANIMATION = IntegerProperty.create("animation", 0, 4);
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	public CrisisTableBlock() {
		super(BlockBehaviour.Properties.of()

				.sound(SoundType.METAL).strength(256f, 32f).lightLevel(s -> (new Object() {
					public int getLightLevel() {
						if (s.getValue(BLOCKSTATE) == 1)
							return 0;
						return 5;
					}
				}.getLightLevel())).requiresCorrectToolForDrops().noOcclusion().pushReaction(PushReaction.BLOCK).hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true).isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return CABlockEntities.CRISIS_TABLE.get().create(blockPos, blockState);
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
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		if (state.getValue(BLOCKSTATE) == 1) {

			return switch (state.getValue(FACING)) {
                case NORTH -> box(0, 0, 0, 16, 16, 16);
				case EAST -> box(0, 0, 0, 16, 16, 16);
				case WEST -> box(0, 0, 0, 16, 16, 16);
                default -> box(0, 0, 0, 16, 16, 16);
            };
		}

		return switch (state.getValue(FACING)) {
            case NORTH -> box(0, 0, 0, 16, 16, 16);
			case EAST -> box(0, 0, 0, 16, 16, 16);
			case WEST -> box(0, 0, 0, 16, 16, 16);
            default -> box(0, 0, 0, 16, 16, 16);
        };
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(DATA_ANIMATION, FACING, BLOCKSTATE);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	@Override
	public float getEnchantPowerBonus(BlockState state, LevelReader world, BlockPos pos) {
		return 3f;
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		List<ItemStack> dropsOriginal = super.getDrops(state, builder);
		if (!dropsOriginal.isEmpty())
			return dropsOriginal;
		return Collections.singletonList(new ItemStack(this, 1));
	}

	@Override
	public void neighborChanged(BlockState blockstate, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
		super.neighborChanged(blockstate, world, pos, neighborBlock, fromPos, moving);
		if (world.getBestNeighborSignal(pos) > 0) {
            double x = pos.getX();
            double y = pos.getY();
            double z = pos.getZ();
            if ((blockstate.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty getip1 ? blockstate.getValue(getip1) : -1) == 1) {
                if ((LevelAccessor) world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), CASounds.START.get(), SoundSource.BLOCKS, 1, 1);
                }
                {
                    int value = 2;
                    BlockPos blockPos = BlockPos.containing(x, y, z);
                    BlockState bs = ((LevelAccessor) world).getBlockState(pos);
                    if (bs.getBlock().getStateDefinition().getProperty("animation") instanceof IntegerProperty integerProp && integerProp.getPossibleValues().contains(value))
                        ((LevelAccessor) world).setBlock(pos, bs.setValue(integerProp, value), 3);
                }
            }
        } else {
            double x = pos.getX();
            double y = pos.getY();
            double z = pos.getZ();
            if ((blockstate.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty getip1 ? blockstate.getValue(getip1) : -1) == 1) {
                if ((LevelAccessor) world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), CASounds.QUIT.get(), SoundSource.BLOCKS, 1, 1);
                }
                {
                    int value = 3;
                    BlockPos blockPos = BlockPos.containing(x, y, z);
                    BlockState bs = ((LevelAccessor) world).getBlockState(pos);
                    if (bs.getBlock().getStateDefinition().getProperty("animation") instanceof IntegerProperty integerProp && integerProp.getPossibleValues().contains(value))
                        ((LevelAccessor) world).setBlock(pos, bs.setValue(integerProp, value), 3);
                }
            }
        }
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
            if ((blockstate.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty getip1 ? blockstate.getValue(getip1) : -1) == 0) {
                {
                    int value = 1;
                    BlockPos blockPos = BlockPos.containing(x, y, z);
                    BlockState bs = ((LevelAccessor) world).getBlockState(pos);
                    if (bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty integerProp && integerProp.getPossibleValues().contains(value))
                        ((LevelAccessor) world).setBlock(pos, bs.setValue(integerProp, value), 3);
                }
                {
                    int value = 1;
                    BlockPos blockPos = BlockPos.containing(x, y, z);
                    BlockState bs = ((LevelAccessor) world).getBlockState(pos);
                    if (bs.getBlock().getStateDefinition().getProperty("animation") instanceof IntegerProperty integerProp && integerProp.getPossibleValues().contains(value))
                        ((LevelAccessor) world).setBlock(pos, bs.setValue(integerProp, value), 3);
                }
                if ((LevelAccessor) world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), CASounds.START.get(), SoundSource.BLOCKS, 1, 1);
                }
                CaerulaArborMod.queueServerWork(25, () -> {
                    if ((((LevelAccessor) world).getBlockState(BlockPos.containing(x, y, z))).getBlock() == CABlocks.CRISIS_TABLE.get()
                            && ((((LevelAccessor) world).getBlockState(BlockPos.containing(x, y, z))).getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty getip8
                            ? (((LevelAccessor) world).getBlockState(BlockPos.containing(x, y, z))).getValue(getip8)
                            : -1) == 1) {
                        if ((Entity) entity instanceof Player player && !player.level().isClientSide())
                            player.displayClientMessage(Component.literal((Component.translatable("crisis_table.log_0").getString())), false);
                        if ((LevelAccessor) world instanceof Level level) {
                                level.playSound(null, BlockPos.containing(x, y, z), CASounds.NOTICE.get(), SoundSource.BLOCKS, 1, 1);
                        }
                    }
                });
                CaerulaArborMod.queueServerWork(45, () -> {
                    if ((((LevelAccessor) world).getBlockState(BlockPos.containing(x, y, z))).getBlock() == CABlocks.CRISIS_TABLE.get()
                            && ((((LevelAccessor) world).getBlockState(BlockPos.containing(x, y, z))).getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty getip16
                            ? (((LevelAccessor) world).getBlockState(BlockPos.containing(x, y, z))).getValue(getip16)
                            : -1) == 1) {
                        if ((Entity) entity instanceof Player player && !player.level().isClientSide())
                            player.displayClientMessage(Component.literal((Component.translatable("crisis_table.log_1").getString())), false);
                        if ((LevelAccessor) world instanceof Level level) {
                                level.playSound(null, BlockPos.containing(x, y, z), CASounds.NOTICE.get(), SoundSource.BLOCKS, 1, 1);
                        }
                    }
                });
                CaerulaArborMod.queueServerWork(55, () -> {
                    if ((((LevelAccessor) world).getBlockState(BlockPos.containing(x, y, z))).getBlock() == CABlocks.CRISIS_TABLE.get()
                            && ((((LevelAccessor) world).getBlockState(BlockPos.containing(x, y, z))).getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty getip24
                            ? (((LevelAccessor) world).getBlockState(BlockPos.containing(x, y, z))).getValue(getip24)
                            : -1) == 1) {
                        if ((Entity) entity instanceof Player player && !player.level().isClientSide())
                            player.displayClientMessage(Component.literal((Component.translatable("crisis_table.log_2").getString())), false);
                        if ((LevelAccessor) world instanceof Level level) {
                                level.playSound(null, BlockPos.containing(x, y, z), CASounds.NOTICE.get(), SoundSource.BLOCKS, 1, 1);
                        }
                    }
                });
                CaerulaArborMod.queueServerWork(65, () -> {
                    if ((((LevelAccessor) world).getBlockState(BlockPos.containing(x, y, z))).getBlock() == CABlocks.CRISIS_TABLE.get()
                            && ((((LevelAccessor) world).getBlockState(BlockPos.containing(x, y, z))).getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty getip32
                            ? (((LevelAccessor) world).getBlockState(BlockPos.containing(x, y, z))).getValue(getip32)
                            : -1) == 1) {
                        double creeper;
                        double gap;
                        new Object() {
                            void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                                summonShooter(world, x, y, z, blockstate);
                                final int tick2 = ticks;
                                CaerulaArborMod.queueServerWork(tick2, () -> {
                                    if (timedlooptotal > timedloopiterator + 1) {
                                        timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                                    }
                                });
                            }
                        }.timedLoop(0, 8, 3);
                        new Object() {
                            void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                                summonShooter(world, x, y, z, blockstate);
                                final int tick2 = ticks;
                                CaerulaArborMod.queueServerWork(tick2, () -> {
                                    if (timedlooptotal > timedloopiterator + 1) {
                                        timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                                    }
                                });
                            }
                        }.timedLoop(0, 3, 10);
                        new Object() {
                            void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                                summonFirst(world, x, y, z, blockstate);
                                final int tick2 = ticks;
                                CaerulaArborMod.queueServerWork(tick2, () -> {
                                    if (timedlooptotal > timedloopiterator + 1) {
                                        timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                                    }
                                });
                            }
                        }.timedLoop(0, 3, 10);
                        new Object() {
                            void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                                summonFirst(world, x, y, z, blockstate);
                                final int tick2 = ticks;
                                CaerulaArborMod.queueServerWork(tick2, () -> {
                                    if (timedlooptotal > timedloopiterator + 1) {
                                        timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                                    }
                                });
                            }
                        }.timedLoop(0, 2, 15);
                        creeper = Mth.nextInt(RandomSource.create(), 2, 6);
                        gap = Math.round(30 / creeper);
                        new Object() {
                            void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                                summonCreeper(world, x, y, z, blockstate);
                                final int tick2 = ticks;
                                CaerulaArborMod.queueServerWork(tick2, () -> {
                                    if (timedlooptotal > timedloopiterator + 1) {
                                        timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                                    }
                                });
                            }
                        }.timedLoop(0, (int) creeper, (int) gap);
                        if ((Entity) entity instanceof Player player && !player.level().isClientSide())
                            player.displayClientMessage(Component.literal((Component.translatable("crisis_table.log_3").getString())), false);
                        if ((LevelAccessor) world instanceof Level level) {
                                level.playSound(null, BlockPos.containing(x, y, z), CASounds.ALERT.get(), SoundSource.BLOCKS, 3, 1);
                        }
                    }
                });
                CaerulaArborMod.queueServerWork(95, () -> {
                    if ((((LevelAccessor) world).getBlockState(BlockPos.containing(x, y, z))).getBlock() == CABlocks.CRISIS_TABLE.get()
                            && ((((LevelAccessor) world).getBlockState(BlockPos.containing(x, y, z))).getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty getip40
                            ? (((LevelAccessor) world).getBlockState(BlockPos.containing(x, y, z))).getValue(getip40)
                            : -1) == 1) {
                        if ((Entity) entity instanceof Player player && !player.level().isClientSide())
                            player.displayClientMessage(Component.literal((Component.translatable("crisis_table.log_4").getString())), false);
                        if ((Entity) entity instanceof ServerPlayer player) {
                            Advancement adv = player.server.getAdvancements().getAdvancement(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "operation_deepness"));
                            AdvancementProgress ap = player.getAdvancements().getOrStartProgress(adv);
                            if (!ap.isDone()) {
                                for (String criteria : ap.getRemainingCriteria())
                                    player.getAdvancements().award(adv, criteria);
                            }
                        }
                    }
                    {
                        final Vec3 center = new Vec3(x, y, z);
                        List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(12 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                        for (Entity entityiterator : entfound) {
                            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring")))
                                    && !entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanpet")))) {
                                if (entityiterator instanceof Mob mob)
                                    mob.setTarget(mob);
                            }
                        }
                    }
                });
                CaerulaArborMod.queueServerWork(105, () -> {
                    if ((((LevelAccessor) world).getBlockState(BlockPos.containing(x, y, z))).getBlock() == CABlocks.CRISIS_TABLE.get()
                            && ((((LevelAccessor) world).getBlockState(BlockPos.containing(x, y, z))).getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty getip52
                            ? (((LevelAccessor) world).getBlockState(BlockPos.containing(x, y, z))).getValue(getip52)
                            : -1) == 1) {
                        if ((LevelAccessor) world instanceof Level level) {
                                level.playSound(null, BlockPos.containing(x, y, z), CASounds.QUIT.get(), SoundSource.BLOCKS, 1, 1);
                        }
                    }
                });
                result = InteractionResult.SUCCESS;
            }
        }
        return result;
	}

	private void summonCreeper(LevelAccessor world, double x, double y, double z, BlockState blockstate) {
		Direction dire;
		Direction dire1;
		double offset0;
		double offset1;
		dire = new Object() {
			public Direction getDirection(BlockState bs) {
				Property<?> prop = bs.getBlock().getStateDefinition().getProperty("facing");
				if (prop instanceof DirectionProperty dp)
					return bs.getValue(dp);
				prop = bs.getBlock().getStateDefinition().getProperty("axis");
				return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) bs.getValue(ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
			}
		}.getDirection(blockstate);
		dire1 = dire.getCounterClockWise(Direction.Axis.Y);
		offset0 = Mth.nextDouble(RandomSource.create(), -3, 4);
		offset1 = Mth.nextDouble(RandomSource.create(), -5, 6);
		if (world instanceof ServerLevel level) {
			Entity entityToSpawn = CAEntities.CREEPER_FISH.get().spawn(level, BlockPos.containing(x + offset0 * dire.getStepX() + offset1 * dire1.getStepX(), y + 1, z + offset0 * dire.getStepZ() + offset1 * dire1.getStepZ()),
					MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
				entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
			}
		}
	}

	private void summonReaper(LevelAccessor world, double x, double y, double z, BlockState blockstate) {
		Direction dire;
		Direction dire1;
		double offset0;
		double offset1;
		dire = new Object() {
			public Direction getDirection(BlockState bs) {
				Property<?> prop = bs.getBlock().getStateDefinition().getProperty("facing");
				if (prop instanceof DirectionProperty dp)
					return bs.getValue(dp);
				prop = bs.getBlock().getStateDefinition().getProperty("axis");
				return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) bs.getValue(ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
			}
		}.getDirection(blockstate);
		dire1 = dire.getCounterClockWise(Direction.Axis.Y);
		offset0 = Mth.nextDouble(RandomSource.create(), -3, 4);
		offset1 = Mth.nextDouble(RandomSource.create(), -5, 6);
		if (world instanceof ServerLevel level) {
			Entity entityToSpawn = CAEntities.REAPER_FISH.get().spawn(level, BlockPos.containing(x + offset0 * dire.getStepX() + offset1 * dire1.getStepX(), y + 1, z + offset0 * dire.getStepZ() + offset1 * dire1.getStepZ()),
					MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
				entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
			}
		}
	}

	private void summonShooter(LevelAccessor world, double x, double y, double z, BlockState blockstate) {
		Direction dire;
		Direction dire1;
		double offset0;
		double offset1;
		dire = new Object() {
			public Direction getDirection(BlockState bs) {
				Property<?> prop = bs.getBlock().getStateDefinition().getProperty("facing");
				if (prop instanceof DirectionProperty dp)
					return bs.getValue(dp);
				prop = bs.getBlock().getStateDefinition().getProperty("axis");
				return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) bs.getValue(ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
			}
		}.getDirection(blockstate);
		dire1 = dire.getCounterClockWise(Direction.Axis.Y);
		offset0 = Mth.nextDouble(RandomSource.create(), -3, 4);
		offset1 = Mth.nextDouble(RandomSource.create(), -5, 6);
		if (world instanceof ServerLevel level) {
			Entity entityToSpawn = CAEntities.SHOOTER_FISH.get().spawn(level, BlockPos.containing(x + offset0 * dire.getStepX() + offset1 * dire1.getStepX(), y + 1, z + offset0 * dire.getStepZ() + offset1 * dire1.getStepZ()),
					MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
				entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
			}
		}
	}

	private void summonFirst(LevelAccessor world, double x, double y, double z, BlockState blockstate) {
		Direction dire;
		Direction dire1;
		double offset0;
		double offset1;
		dire = new Object() {
			public Direction getDirection(BlockState bs) {
				Property<?> prop = bs.getBlock().getStateDefinition().getProperty("facing");
				if (prop instanceof DirectionProperty dp)
					return bs.getValue(dp);
				prop = bs.getBlock().getStateDefinition().getProperty("axis");
				return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) bs.getValue(ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
			}
		}.getDirection(blockstate);
		dire1 = dire.getCounterClockWise(Direction.Axis.Y);
		offset0 = Mth.nextDouble(RandomSource.create(), -3, 4);
		offset1 = Mth.nextDouble(RandomSource.create(), -5, 6);
		if (world instanceof ServerLevel level) {
			Entity entityToSpawn = CAEntities.FIRST_TO_TALK.get().spawn(level, BlockPos.containing(x + offset0 * dire.getStepX() + offset1 * dire1.getStepX(), y + 1, z + offset0 * dire.getStepZ() + offset1 * dire1.getStepZ()),
					MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
				entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
			}
		}
	}
}
