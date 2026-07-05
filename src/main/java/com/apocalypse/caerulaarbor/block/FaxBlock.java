
package com.apocalypse.caerulaarbor.block;

import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAItems;
import com.apocalypse.caerulaarbor.init.CAParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

public class FaxBlock extends Block {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	public FaxBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(1f, 12f).requiresCorrectToolForDrops());
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
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
        if (direction != null && entity != null) {
            ItemStack treaty;
            boolean isCreative;
            treaty = ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).copy();
            isCreative = new Object() {
                public boolean checkGamemode(Entity ent) {
                    if (ent instanceof ServerPlayer serverPlayer) {
                        return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                    } else if (ent.level().isClientSide() && ent instanceof Player player) {
                        return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                    }
                    return false;
                }
            }.checkGamemode((Entity) entity);
            if (treaty.getItem() == CAItems.TREATY_COPPER.get()) {
                if (((Entity) entity instanceof Player plr ? plr.experienceLevel : 0) >= 5 || isCreative) {
                    if (!isCreative) {
                        if ((Entity) entity instanceof Player player)
                            player.giveExperienceLevels(-(5));
                    }
                    ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                    if ((LevelAccessor) world instanceof ServerLevel level) {
                        Entity entityToSpawn = CAEntities.JUNIOR_WARRIOR_PRIEST.get().spawn(level, BlockPos.containing((double) x + direction.getStepX() + 0.5, (double) y + direction.getStepY(), (double) z + direction.getStepZ() + 0.5), MobSpawnType.MOB_SUMMONED);
                        if (entityToSpawn != null) {
                            entityToSpawn.setYRot(((LevelAccessor) world).getRandom().nextFloat() * 360F);
                        }
                    }
                    result = InteractionResult.SUCCESS;
                }
            } else if (treaty.getItem() == CAItems.TREATY_IRON.get()) {
                if (((Entity) entity instanceof Player plr ? plr.experienceLevel : 0) >= 7 || isCreative) {
                    if (!isCreative) {
                        if ((Entity) entity instanceof Player player)
                            player.giveExperienceLevels(-(7));
                    }
                    ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                    if ((LevelAccessor) world instanceof ServerLevel level) {
                        Entity entityToSpawn = CAEntities.WARRIOR_PRIEST.get().spawn(level, BlockPos.containing((double) x + direction.getStepX() + 0.5, (double) y + direction.getStepY(), (double) z + direction.getStepZ() + 0.5), MobSpawnType.MOB_SUMMONED);
                        if (entityToSpawn != null) {
                            entityToSpawn.setYRot(((LevelAccessor) world).getRandom().nextFloat() * 360F);
                        }
                    }
                    result = InteractionResult.SUCCESS;
                }
            } else if (treaty.getItem() == CAItems.TREATY_GOLD.get()) {
                if (((Entity) entity instanceof Player plr ? plr.experienceLevel : 0) >= 9 || isCreative) {
                    if (!isCreative) {
                        if ((Entity) entity instanceof Player player)
                            player.giveExperienceLevels(-(9));
                    }
                    ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                    if (Math.random() < 0.5) {
                        if ((LevelAccessor) world instanceof ServerLevel level) {
                            Entity entityToSpawn = CAEntities.CORRECTIONAL_PHALAX_VANGUARD.get().spawn(level, BlockPos.containing((double) x + direction.getStepX() + 0.5, (double) y + direction.getStepY(), (double) z + direction.getStepZ() + 0.5),
                                    MobSpawnType.MOB_SUMMONED);
                            if (entityToSpawn != null) {
                                entityToSpawn.setYRot(((LevelAccessor) world).getRandom().nextFloat() * 360F);
                            }
                        }
                    } else {
                        if ((LevelAccessor) world instanceof ServerLevel level) {
                            Entity entityToSpawn = CAEntities.CORRECTIONAL_PHALANXY_INFANTRY.get().spawn(level, BlockPos.containing((double) x + direction.getStepX() + 0.5, (double) y + direction.getStepY(), (double) z + direction.getStepZ() + 0.5),
                                    MobSpawnType.MOB_SUMMONED);
                            if (entityToSpawn != null) {
                                entityToSpawn.setYRot(((LevelAccessor) world).getRandom().nextFloat() * 360F);
                            }
                        }
                    }
                    result = InteractionResult.SUCCESS;
                }
            } else if (treaty.getItem() == CAItems.EMERALD_TREATY.get()) {
                if (((Entity) entity instanceof Player plr ? plr.experienceLevel : 0) >= 9 || isCreative) {
                    if (!isCreative) {
                        if ((Entity) entity instanceof Player player)
                            player.giveExperienceLevels(-(9));
                    }
                    ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                    if (Math.random() < 0.5) {
                        if ((LevelAccessor) world instanceof ServerLevel level) {
                            Entity entityToSpawn = CAEntities.TRIBUNAL_HEALER.get().spawn(level, BlockPos.containing((double) x + direction.getStepX() + 0.5, (double) y + direction.getStepY(), (double) z + direction.getStepZ() + 0.5), MobSpawnType.MOB_SUMMONED);
                            if (entityToSpawn != null) {
                                entityToSpawn.setYRot(((LevelAccessor) world).getRandom().nextFloat() * 360F);
                            }
                        }
                    } else {
                        if ((LevelAccessor) world instanceof ServerLevel level) {
                            Entity entityToSpawn = CAEntities.TRIBUNAL_HEALER.get().spawn(level, BlockPos.containing((double) x + direction.getStepX() + 0.5, (double) y + direction.getStepY(), (double) z + direction.getStepZ() + 0.5), MobSpawnType.MOB_SUMMONED);
                            if (entityToSpawn != null) {
                                entityToSpawn.setYRot(((LevelAccessor) world).getRandom().nextFloat() * 360F);
                            }
                        }
                    }
                    result = InteractionResult.SUCCESS;
                }
            } else if (treaty.getItem() == CAItems.TREATY_DIAMOND.get()) {
                if (((Entity) entity instanceof Player plr ? plr.experienceLevel : 0) >= 11 || isCreative) {
                    if (!isCreative) {
                        if ((Entity) entity instanceof Player player)
                            player.giveExperienceLevels(-(11));
                    }
                    ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                    if ((LevelAccessor) world instanceof ServerLevel level)
                        level.sendParticles(CAParticles.PURPLE_FLAME.get(), ((double) x + 0.5), ((double) y + 1.5), ((double) z + 0.5), 32, 0.75, 0.75, 0.75, 0.1);
                    if ((LevelAccessor) world instanceof ServerLevel level) {
                        Entity entityToSpawn = CAEntities.IRENE.get().spawn(level, BlockPos.containing((double) x + direction.getStepX() + 0.5, (double) y + direction.getStepY(), (double) z + direction.getStepZ() + 0.5), MobSpawnType.MOB_SUMMONED);
                        if (entityToSpawn != null) {
                            entityToSpawn.setYRot(((LevelAccessor) world).getRandom().nextFloat() * 360F);
                        }
                    }
                    result = InteractionResult.SUCCESS;
                }
            }
        }
        return result;
	}
}
