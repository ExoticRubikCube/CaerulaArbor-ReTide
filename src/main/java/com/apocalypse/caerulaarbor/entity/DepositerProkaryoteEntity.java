package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CaerulaArborModBlocks;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import com.apocalypse.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class DepositerProkaryoteEntity extends SeaMonster {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(DepositerProkaryoteEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(DepositerProkaryoteEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(DepositerProkaryoteEntity.class, EntityDataSerializers.STRING);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public DepositerProkaryoteEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CaerulaArborModEntities.DEPOSITER_PROKARYOTE.get(), world);
	}

	public DepositerProkaryoteEntity(EntityType<DepositerProkaryoteEntity> type, Level world) {
		super(type, world);
		xpReward = 4;
		setNoAi(false);
		setMaxUpStep(0.6f);
		this.setPathfindingMalus(BlockPathTypes.WATER, 0);
		this.moveControl = new MoveControl(this) {
			@Override
			public void tick() {
				if (DepositerProkaryoteEntity.this.isInWater())
					DepositerProkaryoteEntity.this.setDeltaMovement(DepositerProkaryoteEntity.this.getDeltaMovement().add(0, 0.005, 0));
				if (this.operation == MoveControl.Operation.MOVE_TO && !DepositerProkaryoteEntity.this.getNavigation().isDone()) {
					double dx = this.wantedX - DepositerProkaryoteEntity.this.getX();
					double dy = this.wantedY - DepositerProkaryoteEntity.this.getY();
					double dz = this.wantedZ - DepositerProkaryoteEntity.this.getZ();
					float f = (float) (Mth.atan2(dz, dx) * (180 / Math.PI)) - 90;
					float f1 = (float) (this.speedModifier * DepositerProkaryoteEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
					DepositerProkaryoteEntity.this.setYRot(this.rotlerp(DepositerProkaryoteEntity.this.getYRot(), f, 10));
					DepositerProkaryoteEntity.this.yBodyRot = DepositerProkaryoteEntity.this.getYRot();
					DepositerProkaryoteEntity.this.yHeadRot = DepositerProkaryoteEntity.this.getYRot();
					if (DepositerProkaryoteEntity.this.isInWater()) {
						DepositerProkaryoteEntity.this.setSpeed((float) DepositerProkaryoteEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
						float f2 = -(float) (Mth.atan2(dy, (float) Math.sqrt(dx * dx + dz * dz)) * (180 / Math.PI));
						f2 = Mth.clamp(Mth.wrapDegrees(f2), -85, 85);
						DepositerProkaryoteEntity.this.setXRot(this.rotlerp(DepositerProkaryoteEntity.this.getXRot(), f2, 5));
						float f3 = Mth.cos(DepositerProkaryoteEntity.this.getXRot() * (float) (Math.PI / 180.0));
						DepositerProkaryoteEntity.this.setZza(f3 * f1);
						DepositerProkaryoteEntity.this.setYya((float) (f1 * dy));
					} else {
						DepositerProkaryoteEntity.this.setSpeed(f1 * 0.05F);
					}
				} else {
					DepositerProkaryoteEntity.this.setSpeed(0);
					DepositerProkaryoteEntity.this.setYya(0);
					DepositerProkaryoteEntity.this.setZza(0);
				}
			}
		};
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "depositer");
	}

	public void setTexture(String texture) {
		this.entityData.set(TEXTURE, texture);
	}

	public String getTexture() {
		return this.entityData.get(TEXTURE);
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected PathNavigation createNavigation(Level world) {
		return new WaterBoundPathNavigation(this, world);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.25, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 2.25;
			}
		});
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Pufferfish.class, true, false) {
			@Override
			public boolean canUse() {
				double x = DepositerProkaryoteEntity.this.getX();
				double y = DepositerProkaryoteEntity.this.getY();
				double z = DepositerProkaryoteEntity.this.getZ();
				Entity entity = DepositerProkaryoteEntity.this;
				Level world = DepositerProkaryoteEntity.this.level();
				return super.canUse() && EntityUtils.canAttackAnimals();
			}

			@Override
			public boolean canContinueToUse() {
				double x = DepositerProkaryoteEntity.this.getX();
				double y = DepositerProkaryoteEntity.this.getY();
				double z = DepositerProkaryoteEntity.this.getZ();
				Entity entity = DepositerProkaryoteEntity.this;
				Level world = DepositerProkaryoteEntity.this.level();
				return super.canContinueToUse() && EntityUtils.canAttackAnimals();
			}
		});
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, GlowSquid.class, true, false) {
			@Override
			public boolean canUse() {
				double x = DepositerProkaryoteEntity.this.getX();
				double y = DepositerProkaryoteEntity.this.getY();
				double z = DepositerProkaryoteEntity.this.getZ();
				Entity entity = DepositerProkaryoteEntity.this;
				Level world = DepositerProkaryoteEntity.this.level();
				return super.canUse() && EntityUtils.canAttackAnimals();
			}

			@Override
			public boolean canContinueToUse() {
				double x = DepositerProkaryoteEntity.this.getX();
				double y = DepositerProkaryoteEntity.this.getY();
				double z = DepositerProkaryoteEntity.this.getZ();
				Entity entity = DepositerProkaryoteEntity.this;
				Level world = DepositerProkaryoteEntity.this.level();
				return super.canContinueToUse() && EntityUtils.canAttackAnimals();
			}
		});
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Squid.class, true, false) {
			@Override
			public boolean canUse() {
				double x = DepositerProkaryoteEntity.this.getX();
				double y = DepositerProkaryoteEntity.this.getY();
				double z = DepositerProkaryoteEntity.this.getZ();
				Entity entity = DepositerProkaryoteEntity.this;
				Level world = DepositerProkaryoteEntity.this.level();
				return super.canUse() && EntityUtils.canAttackAnimals();
			}

			@Override
			public boolean canContinueToUse() {
				double x = DepositerProkaryoteEntity.this.getX();
				double y = DepositerProkaryoteEntity.this.getY();
				double z = DepositerProkaryoteEntity.this.getZ();
				Entity entity = DepositerProkaryoteEntity.this;
				Level world = DepositerProkaryoteEntity.this.level();
				return super.canContinueToUse() && EntityUtils.canAttackAnimals();
			}
		});
		this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, TropicalFish.class, true, false) {
			@Override
			public boolean canUse() {
				double x = DepositerProkaryoteEntity.this.getX();
				double y = DepositerProkaryoteEntity.this.getY();
				double z = DepositerProkaryoteEntity.this.getZ();
				Entity entity = DepositerProkaryoteEntity.this;
				Level world = DepositerProkaryoteEntity.this.level();
				return super.canUse() && EntityUtils.canAttackAnimals();
			}

			@Override
			public boolean canContinueToUse() {
				double x = DepositerProkaryoteEntity.this.getX();
				double y = DepositerProkaryoteEntity.this.getY();
				double z = DepositerProkaryoteEntity.this.getZ();
				Entity entity = DepositerProkaryoteEntity.this;
				Level world = DepositerProkaryoteEntity.this.level();
				return super.canContinueToUse() && EntityUtils.canAttackAnimals();
			}
		});
		this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, Salmon.class, true, false) {
			@Override
			public boolean canUse() {
				double x = DepositerProkaryoteEntity.this.getX();
				double y = DepositerProkaryoteEntity.this.getY();
				double z = DepositerProkaryoteEntity.this.getZ();
				Entity entity = DepositerProkaryoteEntity.this;
				Level world = DepositerProkaryoteEntity.this.level();
				return super.canUse() && EntityUtils.canAttackAnimals();
			}

			@Override
			public boolean canContinueToUse() {
				double x = DepositerProkaryoteEntity.this.getX();
				double y = DepositerProkaryoteEntity.this.getY();
				double z = DepositerProkaryoteEntity.this.getZ();
				Entity entity = DepositerProkaryoteEntity.this;
				Level world = DepositerProkaryoteEntity.this.level();
				return super.canContinueToUse() && EntityUtils.canAttackAnimals();
			}
		});
		this.targetSelector.addGoal(8, new NearestAttackableTargetGoal(this, Player.class, true, false) {
			@Override
			public boolean canUse() {
				double x = DepositerProkaryoteEntity.this.getX();
				double y = DepositerProkaryoteEntity.this.getY();
				double z = DepositerProkaryoteEntity.this.getZ();
				Entity entity = DepositerProkaryoteEntity.this;
				Level world = DepositerProkaryoteEntity.this.level();
				return super.canUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
			}

			@Override
			public boolean canContinueToUse() {
				double x = DepositerProkaryoteEntity.this.getX();
				double y = DepositerProkaryoteEntity.this.getY();
				double z = DepositerProkaryoteEntity.this.getZ();
				Entity entity = DepositerProkaryoteEntity.this;
				Level world = DepositerProkaryoteEntity.this.level();
				return super.canContinueToUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
			}
		});
		this.goalSelector.addGoal(9, new RandomSwimmingGoal(this, 1, 40));
		this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
	}

	@Override
	public MobType getMobType() {
		return MobType.WATER;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.puffer_fish.hurt"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.puffer_fish.death"));
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.DROWN))
			return false;
		return super.hurt(source, amount);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
	}

	@Override
	public void baseTick() {
		super.baseTick();
		this.refreshDimensions();
	}

	@Override
	public EntityDimensions getDimensions(Pose p_33597_) {
		return super.getDimensions(p_33597_).scale((float) 1);
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	public boolean checkSpawnObstruction(LevelReader world) {
		return world.isUnobstructed(this);
	}

	@Override
	public boolean isPushedByFluid() {
		return false;
	}

	public static void init() {
		SpawnPlacements.register(CaerulaArborModEntities.DEPOSITER_PROKARYOTE.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (entityType, world, reason, pos, random) -> {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			return WorldUtils.canSpawnMarineSeaborn(world, x, y, z);
		});
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 1.5);
		builder = builder.add(Attributes.MAX_HEALTH, 18);
		builder = builder.add(Attributes.ARMOR, 3);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 4);
		builder = builder.add(Attributes.FOLLOW_RANGE, 22);
		builder = builder.add(ForgeMod.SWIM_SPEED.get(), 1.5);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

			) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.depsoiter.move"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.depsoiter.die"));
			}
			if (this.isInWaterOrBubble()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.depsoiter.move"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.depsoiter.idle"));
		}
		return PlayState.STOP;
	}

	private PlayState attackingPredicate(AnimationState event) {
		double d1 = this.getX() - this.xOld;
		double d0 = this.getZ() - this.zOld;
		float velocity = (float) Math.sqrt(d1 * d1 + d0 * d0);
		if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
			this.swinging = true;
			this.lastSwing = level().getGameTime();
		}
		if (this.swinging && this.lastSwing + 20L <= level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.depsoiter.attack"));
		}
		return PlayState.CONTINUE;
	}

	String prevAnim = "empty";

	private PlayState procedurePredicate(AnimationState event) {
		if (!animationprocedure.equals("empty") && event.getController().getAnimationState() == AnimationController.State.STOPPED || (!this.animationprocedure.equals(prevAnim) && !this.animationprocedure.equals("empty"))) {
			if (!this.animationprocedure.equals(prevAnim))
				event.getController().forceAnimationReset();
			event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
			if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
				this.animationprocedure = "empty";
				event.getController().forceAnimationReset();
			}
		} else if (animationprocedure.equals("empty")) {
			prevAnim = "empty";
			return PlayState.STOP;
		}
		prevAnim = this.animationprocedure;
		return PlayState.CONTINUE;
	}

	@Override
	protected void tickDeath() {
		++this.deathTime;
		if (this.deathTime == 20) {
			this.remove(DepositerProkaryoteEntity.RemovalReason.KILLED);
			this.dropExperience();
            LevelAccessor world = this.level();
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();
            if ((world.getBlockState(BlockPos.containing(x, y, z))).canBeReplaced()) {
                {
                    BlockPos _bp = BlockPos.containing(x, y, z);
                    BlockState _bs = CaerulaArborModBlocks.WHITE_CHITIN_BLOCK.get().withPropertiesOf(world.getBlockState(_bp));
                    if (_bs.getBlock().getStateDefinition().getProperty("waterlogged") instanceof BooleanProperty _waterlogged)
                        _bs = _bs.setValue(_waterlogged, (world.getFluidState(BlockPos.containing(x, y, z)).createLegacyBlock()).getBlock() == Blocks.WATER);
                    world.setBlock(_bp, _bs, 3);
                }
                world.levelEvent(2001, BlockPos.containing(x, y, z), Block.getId(CaerulaArborModBlocks.WHITE_CHITIN_BLOCK.get().defaultBlockState()));
            }
            for (Direction directioniterator : Direction.values()) {
                if (Math.random() < 0.5) {
                    if ((world.getBlockState(BlockPos.containing(x + directioniterator.getStepX(), y + directioniterator.getStepY(), z + directioniterator.getStepZ()))).canBeReplaced()) {
                        {
                            BlockPos _bp = BlockPos.containing(x + directioniterator.getStepX(), y + directioniterator.getStepY(), z + directioniterator.getStepZ());
                            BlockState _bs = CaerulaArborModBlocks.WHITE_CHITIN_BLOCK.get().withPropertiesOf(world.getBlockState(_bp));
                            if (_bs.getBlock().getStateDefinition().getProperty("waterlogged") instanceof BooleanProperty _waterlogged)
                                _bs = _bs.setValue(_waterlogged,
                                        (world.getFluidState(BlockPos.containing(x + directioniterator.getStepX(), y + directioniterator.getStepY(), z + directioniterator.getStepZ())).createLegacyBlock()).getBlock() == Blocks.WATER);
                            world.setBlock(_bp, _bs, 3);
                        }
                        world.levelEvent(2001, BlockPos.containing(x + directioniterator.getStepX(), y + directioniterator.getStepY(), z + directioniterator.getStepZ()), Block.getId(CaerulaArborModBlocks.WHITE_CHITIN_BLOCK.get().defaultBlockState()));
                    }
                }
            }
        }
	}

	public String getSyncedAnimation() {
		return this.entityData.get(ANIMATION);
	}

	public void setAnimation(String animation) {
		this.entityData.set(ANIMATION, animation);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		data.add(new AnimationController<>(this, "movement", 2, this::movementPredicate));
		data.add(new AnimationController<>(this, "attacking", 2, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 2, this::procedurePredicate));
	}
}
