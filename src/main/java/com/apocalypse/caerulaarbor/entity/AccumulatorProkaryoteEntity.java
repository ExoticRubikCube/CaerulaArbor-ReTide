package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import com.apocalypse.caerulaarbor.utils.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
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
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
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

import javax.annotation.Nullable;

public class AccumulatorProkaryoteEntity extends SeaMonster {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(AccumulatorProkaryoteEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(AccumulatorProkaryoteEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(AccumulatorProkaryoteEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Boolean> DATA_split = SynchedEntityData.defineId(AccumulatorProkaryoteEntity.class, EntityDataSerializers.BOOLEAN);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public AccumulatorProkaryoteEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CaerulaArborModEntities.ACCUMULATOR_PROKARYOTE.get(), world);
	}

	public AccumulatorProkaryoteEntity(EntityType<AccumulatorProkaryoteEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);
		setMaxUpStep(0.6f);
		this.setPathfindingMalus(BlockPathTypes.WATER, 0);
		this.moveControl = new MoveControl(this) {
			@Override
			public void tick() {
				if (AccumulatorProkaryoteEntity.this.isInWater())
					AccumulatorProkaryoteEntity.this.setDeltaMovement(AccumulatorProkaryoteEntity.this.getDeltaMovement().add(0, 0.005, 0));
				if (this.operation == MoveControl.Operation.MOVE_TO && !AccumulatorProkaryoteEntity.this.getNavigation().isDone()) {
					double dx = this.wantedX - AccumulatorProkaryoteEntity.this.getX();
					double dy = this.wantedY - AccumulatorProkaryoteEntity.this.getY();
					double dz = this.wantedZ - AccumulatorProkaryoteEntity.this.getZ();
					float f = (float) (Mth.atan2(dz, dx) * (180 / Math.PI)) - 90;
					float f1 = (float) (this.speedModifier * AccumulatorProkaryoteEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
					AccumulatorProkaryoteEntity.this.setYRot(this.rotlerp(AccumulatorProkaryoteEntity.this.getYRot(), f, 10));
					AccumulatorProkaryoteEntity.this.yBodyRot = AccumulatorProkaryoteEntity.this.getYRot();
					AccumulatorProkaryoteEntity.this.yHeadRot = AccumulatorProkaryoteEntity.this.getYRot();
					if (AccumulatorProkaryoteEntity.this.isInWater()) {
						AccumulatorProkaryoteEntity.this.setSpeed((float) AccumulatorProkaryoteEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
						float f2 = -(float) (Mth.atan2(dy, (float) Math.sqrt(dx * dx + dz * dz)) * (180 / Math.PI));
						f2 = Mth.clamp(Mth.wrapDegrees(f2), -85, 85);
						AccumulatorProkaryoteEntity.this.setXRot(this.rotlerp(AccumulatorProkaryoteEntity.this.getXRot(), f2, 5));
						float f3 = Mth.cos(AccumulatorProkaryoteEntity.this.getXRot() * (float) (Math.PI / 180.0));
						AccumulatorProkaryoteEntity.this.setZza(f3 * f1);
						AccumulatorProkaryoteEntity.this.setYya((float) (f1 * dy));
					} else {
						AccumulatorProkaryoteEntity.this.setSpeed(f1 * 0.05F);
					}
				} else {
					AccumulatorProkaryoteEntity.this.setSpeed(0);
					AccumulatorProkaryoteEntity.this.setYya(0);
					AccumulatorProkaryoteEntity.this.setZza(0);
				}
			}
		};
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "accumulator");
		this.entityData.define(DATA_split, true);
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
				return 9;
			}
		});
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Pufferfish.class, true, false) {
			@Override
			public boolean canUse() {
				double x = AccumulatorProkaryoteEntity.this.getX();
				double y = AccumulatorProkaryoteEntity.this.getY();
				double z = AccumulatorProkaryoteEntity.this.getZ();
				Entity entity = AccumulatorProkaryoteEntity.this;
				Level world = AccumulatorProkaryoteEntity.this.level();
				return super.canUse() && EntityUtils.canAttackAnimals();
			}

			@Override
			public boolean canContinueToUse() {
				double x = AccumulatorProkaryoteEntity.this.getX();
				double y = AccumulatorProkaryoteEntity.this.getY();
				double z = AccumulatorProkaryoteEntity.this.getZ();
				Entity entity = AccumulatorProkaryoteEntity.this;
				Level world = AccumulatorProkaryoteEntity.this.level();
				return super.canContinueToUse() && EntityUtils.canAttackAnimals();
			}
		});
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, GlowSquid.class, true, false) {
			@Override
			public boolean canUse() {
				double x = AccumulatorProkaryoteEntity.this.getX();
				double y = AccumulatorProkaryoteEntity.this.getY();
				double z = AccumulatorProkaryoteEntity.this.getZ();
				Entity entity = AccumulatorProkaryoteEntity.this;
				Level world = AccumulatorProkaryoteEntity.this.level();
				return super.canUse() && EntityUtils.canAttackAnimals();
			}

			@Override
			public boolean canContinueToUse() {
				double x = AccumulatorProkaryoteEntity.this.getX();
				double y = AccumulatorProkaryoteEntity.this.getY();
				double z = AccumulatorProkaryoteEntity.this.getZ();
				Entity entity = AccumulatorProkaryoteEntity.this;
				Level world = AccumulatorProkaryoteEntity.this.level();
				return super.canContinueToUse() && EntityUtils.canAttackAnimals();
			}
		});
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Squid.class, true, false) {
			@Override
			public boolean canUse() {
				double x = AccumulatorProkaryoteEntity.this.getX();
				double y = AccumulatorProkaryoteEntity.this.getY();
				double z = AccumulatorProkaryoteEntity.this.getZ();
				Entity entity = AccumulatorProkaryoteEntity.this;
				Level world = AccumulatorProkaryoteEntity.this.level();
				return super.canUse() && EntityUtils.canAttackAnimals();
			}

			@Override
			public boolean canContinueToUse() {
				double x = AccumulatorProkaryoteEntity.this.getX();
				double y = AccumulatorProkaryoteEntity.this.getY();
				double z = AccumulatorProkaryoteEntity.this.getZ();
				Entity entity = AccumulatorProkaryoteEntity.this;
				Level world = AccumulatorProkaryoteEntity.this.level();
				return super.canContinueToUse() && EntityUtils.canAttackAnimals();
			}
		});
		this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, TropicalFish.class, true, false) {
			@Override
			public boolean canUse() {
				double x = AccumulatorProkaryoteEntity.this.getX();
				double y = AccumulatorProkaryoteEntity.this.getY();
				double z = AccumulatorProkaryoteEntity.this.getZ();
				Entity entity = AccumulatorProkaryoteEntity.this;
				Level world = AccumulatorProkaryoteEntity.this.level();
				return super.canUse() && EntityUtils.canAttackAnimals();
			}

			@Override
			public boolean canContinueToUse() {
				double x = AccumulatorProkaryoteEntity.this.getX();
				double y = AccumulatorProkaryoteEntity.this.getY();
				double z = AccumulatorProkaryoteEntity.this.getZ();
				Entity entity = AccumulatorProkaryoteEntity.this;
				Level world = AccumulatorProkaryoteEntity.this.level();
				return super.canContinueToUse() && EntityUtils.canAttackAnimals();
			}
		});
		this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, Salmon.class, true, false) {
			@Override
			public boolean canUse() {
				double x = AccumulatorProkaryoteEntity.this.getX();
				double y = AccumulatorProkaryoteEntity.this.getY();
				double z = AccumulatorProkaryoteEntity.this.getZ();
				Entity entity = AccumulatorProkaryoteEntity.this;
				Level world = AccumulatorProkaryoteEntity.this.level();
				return super.canUse() && EntityUtils.canAttackAnimals();
			}

			@Override
			public boolean canContinueToUse() {
				double x = AccumulatorProkaryoteEntity.this.getX();
				double y = AccumulatorProkaryoteEntity.this.getY();
				double z = AccumulatorProkaryoteEntity.this.getZ();
				Entity entity = AccumulatorProkaryoteEntity.this;
				Level world = AccumulatorProkaryoteEntity.this.level();
				return super.canContinueToUse() && EntityUtils.canAttackAnimals();
			}
		});
		this.targetSelector.addGoal(8, new NearestAttackableTargetGoal(this, Player.class, true, false) {
			@Override
			public boolean canUse() {
				double x = AccumulatorProkaryoteEntity.this.getX();
				double y = AccumulatorProkaryoteEntity.this.getY();
				double z = AccumulatorProkaryoteEntity.this.getZ();
				Entity entity = AccumulatorProkaryoteEntity.this;
				Level world = AccumulatorProkaryoteEntity.this.level();
				return super.canUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
			}

			@Override
			public boolean canContinueToUse() {
				double x = AccumulatorProkaryoteEntity.this.getX();
				double y = AccumulatorProkaryoteEntity.this.getY();
				double z = AccumulatorProkaryoteEntity.this.getZ();
				Entity entity = AccumulatorProkaryoteEntity.this;
				Level world = AccumulatorProkaryoteEntity.this.level();
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
	public void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.puffer_fish.flop")), 0.15f, 1);
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "seaborn_generic_hit"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "seaborn_death"));
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.DROWN))
			return false;
		return super.hurt(source, amount);
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
		EntityUtils.initBplusMagic(this);
		return retval;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putBoolean("Datasplit", this.entityData.get(DATA_split));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("Datasplit"))
			this.entityData.set(DATA_split, compound.getBoolean("Datasplit"));
	}

	@Override
	public void baseTick() {
		super.baseTick();
        LevelAccessor world = this.level();
        if (this != null) {
            if (this.isAlive() && tickCount % 10 == 0) {
                if (((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) <= ((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.5 && (Entity) this instanceof AccumulatorProkaryoteEntity _datEntL4
                        && _datEntL4.getEntityData().get(DATA_split)) {
                    if (this instanceof AccumulatorProkaryoteEntity) {
                        this.setAnimation("animation.accumulator.split");
                    }
                    if ((Entity) this instanceof LivingEntity _entity)
                        _entity.setHealth((float) (((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.5));
                    if ((Entity) this instanceof AccumulatorProkaryoteEntity _datEntSetL)
                        _datEntSetL.getEntityData().set(DATA_split, false);
                    if (!((Entity) this instanceof LivingEntity _livEnt9 && _livEnt9.hasEffect(CaerulaArborModMobEffects.MUTE.get()))) {
                        CaerulaArborMod.queueServerWork(10, () -> {
                            if (isInWater()) {
                                if (world instanceof ServerLevel _level) {
                                    Entity entityToSpawn = CaerulaArborModEntities.ACCUMULATOR_CLONE.get().spawn(_level,
                                            BlockPos.containing(getX() + Mth.nextDouble(RandomSource.create(), -1, 1), getY() + 0.5, getZ() + Mth.nextDouble(RandomSource.create(), -1, 1)), MobSpawnType.MOB_SUMMONED);
                                    if (entityToSpawn != null) {
                                        entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                                    }
                                }
                            } else {
                                if (world instanceof ServerLevel _level) {
                                    Entity entityToSpawn = CaerulaArborModEntities.DIVICELLULAR_GO.get().spawn(_level,
                                            BlockPos.containing(getX() + Mth.nextDouble(RandomSource.create(), -1, 1), getY() + 0.5, getZ() + Mth.nextDouble(RandomSource.create(), -1, 1)), MobSpawnType.MOB_SUMMONED);
                                    if (entityToSpawn != null) {
                                        entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                                    }
                                }
                            }
                            if (world instanceof Level _level) {
                                    _level.playSound(null, BlockPos.containing(getX(), getY(), getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.puffer_fish.blow_out")), SoundSource.HOSTILE, 2, 1);
                            }
                        });
                    }
                }
            }
        }
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
		SpawnPlacements.register(CaerulaArborModEntities.ACCUMULATOR_PROKARYOTE.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (entityType, world, reason, pos, random) -> {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			return WorldUtils.canSpawnUnderwaterSeaborn(world, x, y, z);
		});
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 1.75);
		builder = builder.add(Attributes.MAX_HEALTH, 32);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
		builder = builder.add(Attributes.FOLLOW_RANGE, 16);
		builder = builder.add(ForgeMod.SWIM_SPEED.get(), 1.75);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

			) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.accumulator.move"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.accumulator.idle"));
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
		if (this.swinging && this.lastSwing + 10L <= level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.accumulator.attack"));
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
			this.remove(AccumulatorProkaryoteEntity.RemovalReason.KILLED);
			this.dropExperience();
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
		data.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
		data.add(new AnimationController<>(this, "attacking", 4, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
	}
}
