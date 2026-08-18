package com.susen36.caerulaarbor.entity;


import com.susen36.babel.api.entity.ElementalAttacker;
import com.susen36.babel.elemental.base.AbstractEPCapability;
import com.susen36.babel.init.BabelAttributes;
import com.susen36.caerulaarbor.block.NetherseaBrandBlock;
import com.susen36.caerulaarbor.entity.ai.MountVehicleGoal;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.entity.bullets.FishShootEntity;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAEntityTypeTags;
import com.susen36.caerulaarbor.init.CASounds;
import com.susen36.caerulaarbor.util.EntityUtils;
import com.susen36.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
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

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class SplasherAbyssalEntity extends SeaMonster implements RangedAttackMob, ElementalAttacker {
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
	public AbstractEPCapability.EPType getElementalType() {
		return AbstractEPCapability.EPType.NERVOUS;
	}

	@Override
	public double getElementalRate() {
		return 0.15D;
	}

	@Override
	public double getElementalInjuryDamage() {
		return 0.0D;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(9, new MountVehicleGoal(this, OceanizedPolarBearEntity.class, OceanizedHorseEntity.class));
		this.goalSelector.addGoal(13, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(1, new MultiTargetRangedAttackGoal(this, 1.25));
	}

	public static class MultiTargetRangedAttackGoal extends Goal {
		private static final int ATTACK_INTERVAL = 40;
		private static final double NORMAL_ATTACK_RADIUS = 18.0;
		private static final int MAX_TARGETS = 3;

		private final SplasherAbyssalEntity mob;
		private final double speedModifier;
		private int attackTime = -1;

		public MultiTargetRangedAttackGoal(SplasherAbyssalEntity mob, double speedModifier) {
			this.mob = mob;
			this.speedModifier = speedModifier;
			this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
		}

		@Override
		public boolean canUse() {
			return !this.collectTargets().isEmpty();
		}

		@Override
		public boolean canContinueToUse() {
			return this.canUse();
		}

		@Override
		public void stop() {
			this.mob.entityData.set(DATA_SHOOT, false);
			this.attackTime = -1;
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}

		// 站在活性溟痕上的目标无视攻击距离，仅受寻敌半径(FOLLOW_RANGE)约束，其余目标限制在18格内；取前3个
		private List<LivingEntity> collectTargets() {
			double followRange = this.mob.getAttributeValue(Attributes.FOLLOW_RANGE);
			List<LivingEntity> candidates = this.mob.level().getEntitiesOfClass(LivingEntity.class,
					this.mob.getBoundingBox().inflate(followRange),
					e -> e != this.mob && e.isAlive()
							&& !e.getType().is(CAEntityTypeTags.SEABORN)
							&& !EntityUtils.isOceanizedPlayer(e));
			return candidates.stream()
					.filter(e -> NetherseaBrandBlock.isOnActiveTrail(e)
							|| this.mob.distanceToSqr(e) <= NORMAL_ATTACK_RADIUS * NORMAL_ATTACK_RADIUS)
					.sorted(Comparator
							.comparingDouble((LivingEntity e) -> NetherseaBrandBlock.isOnActiveTrail(e) ? 0 : 1)
							.thenComparingDouble(this.mob::distanceToSqr))
					.limit(MAX_TARGETS)
					.toList();
		}

		@Override
		public void tick() {
			List<LivingEntity> targets = this.collectTargets();
			if (!targets.isEmpty()) {
				LivingEntity primary = targets.getFirst();
				// 保留追击移动行为，无视视线遮挡
				this.mob.getNavigation().moveTo(primary, this.speedModifier);
				this.mob.getLookControl().setLookAt(primary, 30.0F, 30.0F);
				if (--this.attackTime == 0) {
					this.mob.entityData.set(DATA_SHOOT, true);
					for (LivingEntity target : targets) {
						this.mob.performRangedAttack(target, 1.0F);
					}
					this.attackTime = ATTACK_INTERVAL;
				} else if (this.attackTime < 0) {
					// 首次激活时先初始化冷却，避免激活即开火
					this.attackTime = ATTACK_INTERVAL;
				} else {
					this.mob.entityData.set(DATA_SHOOT, false);
				}
			} else {
				this.mob.entityData.set(DATA_SHOOT, false);
			}
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
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.175);
		builder = builder.add(Attributes.MAX_HEALTH, 15);
		builder = builder.add(Attributes.ARMOR, 5);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 10);
		builder = builder.add(Attributes.FOLLOW_RANGE, 18);
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