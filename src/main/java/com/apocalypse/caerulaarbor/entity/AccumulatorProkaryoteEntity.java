package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CAAttributes;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.init.CASounds;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import com.apocalypse.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.ForgeMod;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class AccumulatorProkaryoteEntity extends SeaMonster {
	public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(AccumulatorProkaryoteEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(AccumulatorProkaryoteEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Boolean> DATA_SPLIT = SynchedEntityData.defineId(AccumulatorProkaryoteEntity.class, EntityDataSerializers.BOOLEAN);
	private boolean swinging;
	private long lastSwing;
	public String animationprocedure = "empty";

	public AccumulatorProkaryoteEntity(Level world) {
		this(CAEntities.ACCUMULATOR_PROKARYOTE.get(), world);
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
		this.entityData.define(DATA_SHOOT, false);
		this.entityData.define(DATA_ANIMATION, "undefined");
		this.entityData.define(DATA_SPLIT, true);
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
		this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, target -> EntityUtils.isOceanizedPlayerNearby(this.level(), this.getX(), this.getY(), this.getZ())));
		this.goalSelector.addGoal(9, new RandomSwimmingGoal(this, 1, 40));
		this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
	}

    @Override
	public void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(SoundEvents.PUFFER_FISH_FLOP, 0.15f, 1);
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return CASounds.SEABORN_GENERIC_HIT.get();
	}

	@Override
	public SoundEvent getDeathSound() {
		return CASounds.SEABORN_DEATH.get();
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
		compound.putBoolean("Split", this.entityData.get(DATA_SPLIT));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Split")) {
		    this.entityData.set(DATA_SPLIT, compound.getBoolean("Split"));
		}
	}

	@Override
	public void baseTick() {
		super.baseTick();
        LevelAccessor world = this.level();
        if (this.isAlive() && tickCount % 10 == 0) {
            if (((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) <= ((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.5 && (Entity) this instanceof AccumulatorProkaryoteEntity _datEntL4
                    && _datEntL4.getEntityData().get(DATA_SPLIT)) {
                if (this instanceof AccumulatorProkaryoteEntity) {
                    this.setAnimation("animation.accumulator.split");
                }
                if ((Entity) this instanceof LivingEntity _entity)
                    _entity.setHealth((float) (((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.5));
                if ((Entity) this instanceof AccumulatorProkaryoteEntity _datEntSetL)
                    _datEntSetL.getEntityData().set(DATA_SPLIT, false);
                if (!((Entity) this instanceof LivingEntity _livEnt9 && _livEnt9.hasEffect(CAMobEffects.MUTE.get()))) {
                    CaerulaArborMod.queueServerWork(10, () -> {
                        if (isInWater()) {
                            if (world instanceof ServerLevel _level) {
                                Entity entityToSpawn = CAEntities.ACCUMULATOR_CLONE.get().spawn(_level,
                                        BlockPos.containing(getX() + Mth.nextDouble(RandomSource.create(), -1, 1), getY() + 0.5, getZ() + Mth.nextDouble(RandomSource.create(), -1, 1)), MobSpawnType.MOB_SUMMONED);
                                if (entityToSpawn != null) {
                                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                                }
                            }
                        } else {
                            if (world instanceof ServerLevel _level) {
                                Entity entityToSpawn = CAEntities.DIVICELLULAR_GO.get().spawn(_level,
                                        BlockPos.containing(getX() + Mth.nextDouble(RandomSource.create(), -1, 1), getY() + 0.5, getZ() + Mth.nextDouble(RandomSource.create(), -1, 1)), MobSpawnType.MOB_SUMMONED);
                                if (entityToSpawn != null) {
                                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                                }
                            }
                        }
                        if (world instanceof Level _level) {
                            _level.playSound(null, BlockPos.containing(getX(), getY(), getZ()), SoundEvents.PUFFER_FISH_BLOW_OUT, SoundSource.HOSTILE, 2, 1);
                        }
                    });
                }
            }
        }
        this.refreshDimensions();
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

	public static void registerSpawnPlacements() {
		SpawnPlacements.register(CAEntities.ACCUMULATOR_PROKARYOTE.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (entityType, world, reason, pos, random) -> {
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
		builder = builder.add(CAAttributes.MAGIC_RESISTANCE.get(), 18);
		return builder;
	}

	private PlayState movementPredicate(AnimationState<?> event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

			) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.accumulator.move"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.accumulator.idle"));
		}
		return PlayState.STOP;
	}

	private PlayState attackingPredicate(AnimationState<?> event) {
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

	private PlayState procedurePredicate(AnimationState<?> event) {
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
		return this.entityData.get(DATA_ANIMATION);
	}

	public void setAnimation(String animation) {
		this.entityData.set(DATA_ANIMATION, animation);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		data.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
		data.add(new AnimationController<>(this, "attacking", 4, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
	}


	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}
}

