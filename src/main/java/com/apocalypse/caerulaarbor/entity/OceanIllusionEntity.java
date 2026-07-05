package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.entity.bullets.FishSplashEntity;
import com.apocalypse.caerulaarbor.init.CAAttributes;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumSet;

public class OceanIllusionEntity extends SeaMonster implements RangedAttackMob {
	public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(OceanIllusionEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(OceanIllusionEntity.class, EntityDataSerializers.STRING);
	public String animationprocedure = "empty";

	public OceanIllusionEntity(Level world) {
		this(CAEntities.OCEAN_ILLUSION.get(), world);
	}

	public OceanIllusionEntity(EntityType<OceanIllusionEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);
		setMaxUpStep(0.6f);
		setPersistenceRequired();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_SHOOT, false);
		this.entityData.define(DATA_ANIMATION, "undefined");
	}



    @Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, IronGolem.class, true, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, SnowGolem.class, true, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Villager.class, true, true));
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Illusioner.class, true, true));
		this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Pillager.class, true, true));
		this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, Vindicator.class, true, true));
		this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, Witch.class, true, true));
		this.targetSelector.addGoal(9, new NearestAttackableTargetGoal<>(this, Piglin.class, true, true));
		this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, PiglinBrute.class, true, true));
		this.targetSelector.addGoal(11, new NearestAttackableTargetGoal<>(this, ZombifiedPiglin.class, true, true));
		this.targetSelector.addGoal(12, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, true, target -> EntityUtils.isOceanizedPlayerNearby(this.level(), this.getX(), this.getY(), this.getZ())));
		this.goalSelector.addGoal(14, new RandomStrollGoal(this, 1));
		this.goalSelector.addGoal(15, new FloatGoal(this));
		this.goalSelector.addGoal(16, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25, 30, 2f) {
			@Override
			public boolean canContinueToUse() {
				return this.canUse();
			}
		});
	}

	public class RangedAttackGoal extends Goal {
		private final Mob mob;
		private final RangedAttackMob rangedAttackMob;
		@Nullable
		private LivingEntity target;
		private int attackTime = -1;
		private final double speedModifier;
		private int seeTime;
		private final int attackIntervalMin;
		private final int attackIntervalMax;
		private final float attackRadius;
		private final float attackRadiusSqr;

		public RangedAttackGoal(RangedAttackMob p_25768_, double p_25769_, int p_25770_, float p_25771_) {
			this(p_25768_, p_25769_, p_25770_, p_25770_, p_25771_);
		}

		public RangedAttackGoal(RangedAttackMob p_25773_, double p_25774_, int p_25775_, int p_25776_, float p_25777_) {
			if (!(p_25773_ instanceof LivingEntity)) {
				throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
			} else {
				this.rangedAttackMob = p_25773_;
				this.mob = (Mob) p_25773_;
				this.speedModifier = p_25774_;
				this.attackIntervalMin = p_25775_;
				this.attackIntervalMax = p_25776_;
				this.attackRadius = p_25777_;
				this.attackRadiusSqr = p_25777_ * p_25777_;
				this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
			}
		}

		public boolean canUse() {
			LivingEntity livingentity = this.mob.getTarget();
			if (livingentity != null && livingentity.isAlive()) {
				this.target = livingentity;
				return true;
			} else {
				return false;
			}
		}

		public boolean canContinueToUse() {
			return this.canUse() || this.target.isAlive() && !this.mob.getNavigation().isDone();
		}

		public void stop() {
			this.target = null;
			this.seeTime = 0;
			this.attackTime = -1;
			((OceanIllusionEntity) rangedAttackMob).entityData.set(DATA_SHOOT, false);
		}

		public boolean requiresUpdateEveryTick() {
			return true;
		}

		public void tick() {
			double d0 = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
			boolean flag = this.mob.getSensing().hasLineOfSight(this.target);
			if (flag) {
				++this.seeTime;
			} else {
				this.seeTime = 0;
			}
			if (!(d0 > (double) this.attackRadiusSqr) && this.seeTime >= 5) {
				this.mob.getNavigation().stop();
			} else {
				this.mob.getNavigation().moveTo(this.target, this.speedModifier);
			}
			this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
			if (--this.attackTime == 0) {
				if (!flag) {
					((OceanIllusionEntity) rangedAttackMob).entityData.set(DATA_SHOOT, false);
					return;
				}
				((OceanIllusionEntity) rangedAttackMob).entityData.set(DATA_SHOOT, true);
				float f = (float) Math.sqrt(d0) / this.attackRadius;
				float f1 = Mth.clamp(f, 0.1F, 1.0F);
				this.rangedAttackMob.performRangedAttack(this.target, f1);
				this.attackTime = Mth.floor(f * (float) (this.attackIntervalMax - this.attackIntervalMin) + (float) this.attackIntervalMin);
			} else if (this.attackTime < 0) {
				this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(d0) / (double) this.attackRadius, this.attackIntervalMin, this.attackIntervalMax));
			} else
				((OceanIllusionEntity) rangedAttackMob).entityData.set(DATA_SHOOT, false);
		}
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEFINED;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return SoundEvents.ILLUSIONER_HURT;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.ILLUSIONER_DEATH;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.DROWN))
			return false;
		return super.hurt(source, amount);
	}

	@Override
	public void actuallyHurt(DamageSource source, float amount) {
		super.actuallyHurt(source, Math.min(amount, 1));
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
		illusionSmoke(world, this.getX(), this.getY(), this.getZ());
		return retval;
	}

	@Override
	public void remove(RemovalReason pReason) {
		illusionSmoke(this.level(), this.getX(), this.getY(), this.getZ());
		super.remove(pReason);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
	}

	@Override
	public void baseTick() {
		super.baseTick();
		boolean finished = false;
		LevelAccessor world = this.level();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		Entity illusioner;
		Entity enemy;
		if (this.isAlive()) {
			if (tickCount % 40 == 20) {
                enemy = this.getTarget();
				if (!(enemy == null) && enemy.isAlive()) {
					finished = true;
				} else {
					illusioner = world.getEntitiesOfClass(OceanizedIllusionerEntity.class, AABB.ofSize(new Vec3(x, y, z), 48, 48, 48), e -> true).stream().min(new Object() {
                        Comparator<Entity> compareDistOf(double x, double y, double z) {
                            return Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(x, y, z));
                        }
                    }.compareDistOf(x, y, z)).orElse(null);
					if (illusioner == null) {
						finished = true;
					} else {
						Vec3 ownerPos = illusioner.position();
						Vec3 goal;
						Vec3 v = ownerPos.vectorTo(position());
						Vec3 v1 = new Vec3(v.x, 0, v.z);
						if (v1.lengthSqr() > 30.25)
							goal = ownerPos.add(v1.normalize().scale(5));
						else {
							RandomSource random1 = level().random;
							int yaw = Mth.nextInt(random1, 30, 90);
							double r = Mth.nextDouble(random1, 3, 5);
							goal = ownerPos.add(v1.normalize().scale(r).yRot((float) Math.toRadians(yaw)));
						}
						if (goal.distanceToSqr(position()) > 0.25) {
							this.getNavigation().moveTo(goal.x, goal.y, goal.z, 1);
                        }
					}
				}
			}
			if (!finished) {
				if (this.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE.get()))
					this.getAttribute(CAAttributes.GENERAL_DEFENSE.get()).setBaseValue(0);
			}
		}
		if (!this.level().isClientSide() && this.hasEffect(CAMobEffects.MUTE.get())) this.discard();
		this.refreshDimensions();
	}

	

	@Override
	public void performRangedAttack(LivingEntity target, float flval) {
		FishSplashEntity.shoot(this, target, (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttributeValue(Attributes.ATTACK_DAMAGE) : 0) * (2.5 / 9.0));
	}

	

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.18);
		builder = builder.add(Attributes.MAX_HEALTH, 3);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 9);
		builder = builder.add(Attributes.FOLLOW_RANGE, 24);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 1);
		return builder;
	}

	private PlayState movementPredicate(AnimationState<?> event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

			) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_illusioner.move"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_illusioner.die"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_illusioner.idle"));
		}
		return PlayState.STOP;
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
			this.remove(RemovalReason.KILLED);
			this.dropExperience();
			LevelAccessor world = this.level();
			if (Math.random() < 0.6) {
				OceanizedRavagerEntity.summonFellows(world, this.getX(), this.getY(), this.getZ(), 1);
			}
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
		data.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
		data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
	}

	private void illusionSmoke(LevelAccessor world, double x, double y, double z) {
		if (world instanceof ServerLevel level)
			level.sendParticles(ParticleTypes.LARGE_SMOKE, x, (y + 0.75), z, 16, 0.75, 0.75, 0.75, 0.1);
	}


	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}
}
