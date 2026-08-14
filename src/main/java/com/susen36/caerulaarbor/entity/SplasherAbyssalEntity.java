package com.susen36.caerulaarbor.entity;


import com.susen36.babel.init.BabelAttributes;
import com.susen36.caerulaarbor.entity.ai.MountVehicleGoal;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.entity.bullets.FishShootEntity;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.init.CASounds;
import com.susen36.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import software.bernie.geckolib.animation.*;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class SplasherAbyssalEntity extends SeaMonster implements RangedAttackMob {
	public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(SplasherAbyssalEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(SplasherAbyssalEntity.class, EntityDataSerializers.STRING);
	private boolean swinging;
	private long lastSwing;
	public String animationprocedure = "empty";

	public SplasherAbyssalEntity(Level world) {
		this(CAEntities.SPLASHER_ABYSSAL.get(), world);
	}

	public SplasherAbyssalEntity(EntityType<SplasherAbyssalEntity> type, Level world) {
		super(type, world);
		xpReward = 4;
		setNoAi(false);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_SHOOT, false);
		builder.define(DATA_ANIMATION, "undefined");
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(13, new MountVehicleGoal(this, OceanizedPolarBearEntity.class, OceanizedHorseEntity.class));
		this.goalSelector.addGoal(15, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25, 40, 6f) {
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
			((SplasherAbyssalEntity) rangedAttackMob).entityData.set(DATA_SHOOT, false);
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
					((SplasherAbyssalEntity) rangedAttackMob).entityData.set(DATA_SHOOT, false);
					return;
				}
				((SplasherAbyssalEntity) rangedAttackMob).entityData.set(DATA_SHOOT, true);
				float f = (float) Math.sqrt(d0) / this.attackRadius;
				float f1 = Mth.clamp(f, 0.1F, 1.0F);
				this.rangedAttackMob.performRangedAttack(this.target, f1);
				this.attackTime = Mth.floor(f * (float) (this.attackIntervalMax - this.attackIntervalMin) + (float) this.attackIntervalMin);
			} else if (this.attackTime < 0) {
				this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(d0) / (double) this.attackRadius, this.attackIntervalMin, this.attackIntervalMax));
			} else
				((SplasherAbyssalEntity) rangedAttackMob).entityData.set(DATA_SHOOT, false);
		}
	}

	@Override
	public SoundEvent getAmbientSound() {
		return SoundEvents.PUFFER_FISH_AMBIENT;
	}

	@Override
	public void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(SoundEvents.SILVERFISH_STEP, 0.15f, 1);
	}

	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return CASounds.SEABORN_GENERIC_HIT.get();
	}

	@Override
	public SoundEvent getDeathSound() {
		return CASounds.SEABORN_DEATH.get();
	}

	@Override
	public void baseTick() {
		super.baseTick();
		if (!this.hasEffect(CAMobEffects.SPLASHER_ATTACK)) {
			if (!this.level().isClientSide())
				this.addEffect(new MobEffectInstance(CAMobEffects.SPLASHER_ATTACK, -1, 0, false, false));
		}
		this.refreshDimensions();
	}

	@Override
	public void performRangedAttack(LivingEntity target, float flval) {
		FishShootEntity.shoot(this, target, (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttributeValue(Attributes.ATTACK_DAMAGE) : 0) * 0.2);
	}

	public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
		event.register(CAEntities.SPLASHER_ABYSSAL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (entityType, world, reason, pos, random) -> {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			return WorldUtils.canCommonSeabornSpawn(world, x, y, z);
		}, RegisterSpawnPlacementsEvent.Operation.REPLACE);
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.18);
		builder = builder.add(Attributes.MAX_HEALTH, 15);
		builder = builder.add(Attributes.ARMOR, 5);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 10);
		builder = builder.add(Attributes.FOLLOW_RANGE, 16);
		builder = builder.add(BabelAttributes.MAGIC_RESISTANCE, 27);
		builder = builder.add(Attributes.STEP_HEIGHT, 0.85f);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.splasher.move"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.splasher.idle"));
		}
		return PlayState.STOP;
	}

	private PlayState attackingPredicate(AnimationState event) {
		if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
			this.swinging = true;
			this.lastSwing = level().getGameTime();
		}
		if (this.swinging && this.lastSwing + 10L <= level().getGameTime()) {
			this.swinging = false;
		}
		if ((this.swinging || this.entityData.get(DATA_SHOOT)) && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.splasher.attack"));
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
