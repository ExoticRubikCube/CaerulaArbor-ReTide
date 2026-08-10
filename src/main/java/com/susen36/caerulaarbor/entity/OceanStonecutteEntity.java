package com.susen36.caerulaarbor.entity;


import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.entity.bullets.FishShootEntity;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CASounds;
import com.susen36.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class OceanStonecutteEntity extends SeaMonster implements RangedAttackMob, Bucketable {
	public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(OceanStonecutteEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(OceanStonecutteEntity.class, EntityDataSerializers.STRING);
	private static final ResourceLocation SLOW_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "ocean_stonecutte_slow");
	private static final ResourceLocation RESISTANCE_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "ocean_stonecutte_resistance");
	private static final AttributeModifier SLOW_MODIFIER = new AttributeModifier(SLOW_MODIFIER_ID, -0.45, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
	private static final AttributeModifier RESISTANCE_MODIFIER = new AttributeModifier(RESISTANCE_MODIFIER_ID, 10.0, AttributeModifier.Operation.ADD_VALUE);
	private boolean swinging;
	private long lastSwing;
	private boolean fromBucket;
	public String animationprocedure = "empty";
	private int defenseDuration;

	public OceanStonecutteEntity(Level world) {
		this(CAEntities.OCEAN_STONECUTTE.get(), world);
	}

	public OceanStonecutteEntity(EntityType<OceanStonecutteEntity> type, Level world) {
		super(type, world);
		xpReward = 4;
		setNoAi(false);
	}

	public OceanStonecutteEntity(EntityType<OceanStonecutteEntity> type, Level world, boolean fromDoll) {
		super(type, world);
		xpReward = 4;
		setNoAi(false);
		if (fromDoll) {
			this.getAttribute(Attributes.SCALE).setBaseValue(0.8F);
		}
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_SHOOT, false);
		builder.define(DATA_ANIMATION, "undefined");
	}

	private void applyDefenseBoost() {
		if (!this.level().isClientSide() && this.defenseDuration <= 0) {
			this.defenseDuration = 400;
			this.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(SLOW_MODIFIER);
			this.getAttribute(Attributes.ARMOR).addTransientModifier(RESISTANCE_MODIFIER);
		}
	}

	private void tickDefenseTimers() {
		if (!this.level().isClientSide()) {
			if (this.defenseDuration > 0) {
				this.defenseDuration--;
				if (this.defenseDuration <= 0) {
					this.getAttribute(Attributes.ARMOR).removeModifier(RESISTANCE_MODIFIER_ID);
					this.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SLOW_MODIFIER_ID);
				}
			}
		}
	}

	private void reapplyModifiersOnLoad() {
		if (!this.level().isClientSide() && this.defenseDuration > 0) {
			this.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SLOW_MODIFIER_ID);
			this.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(SLOW_MODIFIER);
			this.getAttribute(Attributes.ARMOR).removeModifier(RESISTANCE_MODIFIER_ID);
			this.getAttribute(Attributes.ARMOR).addTransientModifier(RESISTANCE_MODIFIER);
		}
	}

	@Override
	public void setTarget(@Nullable LivingEntity target) {
		super.setTarget(target);
		if (target != null) {
			this.applyDefenseBoost();
		}
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(14, new RandomStrollGoal(this, 0.8));
		this.goalSelector.addGoal(15, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(16, new FloatGoal(this));

		this.goalSelector.addGoal(1, new OceanStonecutteEntity.RangedAttackGoal(this, 1.25, 50, 18f) {
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
				this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
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
			 ((OceanStonecutteEntity)rangedAttackMob).entityData.set(DATA_SHOOT, false);
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
					((OceanStonecutteEntity) rangedAttackMob).entityData.set(DATA_SHOOT, false);
					return;
				}
				((OceanStonecutteEntity) rangedAttackMob).entityData.set(DATA_SHOOT, true);
				float f = (float) Math.sqrt(d0) / this.attackRadius;
				float f1 = Mth.clamp(f, 0.1F, 1.0F);
				this.rangedAttackMob.performRangedAttack(this.target, f1);
				this.attackTime = Mth.floor(f * (float) (this.attackIntervalMax - this.attackIntervalMin) + (float) this.attackIntervalMin);
			} else if (this.attackTime < 0) {
				this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(d0) / (double) this.attackRadius, this.attackIntervalMin, this.attackIntervalMax));
			} else
				((OceanStonecutteEntity) rangedAttackMob).entityData.set(DATA_SHOOT, false);
		}
	}

    @Override
	public SoundEvent getAmbientSound() {
		return SoundEvents.TURTLE_AMBIENT_LAND;
	}

	@Override
	public void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(SoundEvents.SPIDER_STEP, 0.15f, 1);
	}

	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return CASounds.SEABORN_GENERIC_HIT.get();
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.TURTLE_DEATH;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.DROWN))
			return false;
		if (this.defenseDuration > 0) {
			amount = amount * 0.6F;
		}
		return super.hurt(source, amount);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("FromBucket", this.fromBucket());
		compound.putInt("DefenseDuration", this.defenseDuration);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.setFromBucket(compound.getBoolean("FromBucket"));
		this.defenseDuration = compound.getInt("DefenseDuration");
		this.reapplyModifiersOnLoad();
	}

	@Override
	public boolean fromBucket() {
		return this.fromBucket;
	}

	@Override
	public void setFromBucket(boolean fromBucket) {
		this.fromBucket = fromBucket;
	}

	@Override
	public void saveToBucketTag(ItemStack bucketStack) {
		Bucketable.saveDefaultDataToBucketTag(this, bucketStack);
	}

	@Override
	public void loadFromBucketTag(CompoundTag bucketTag) {
		Bucketable.loadDefaultDataFromBucketTag(this, bucketTag);
		this.setFromBucket(true);
	}

	@Override
	public ItemStack getBucketItemStack() {
		return new ItemStack(CAItems.BUCKET_OCEAN_STONECUTTE.get());
	}

	@Override
	public SoundEvent getPickupSound() {
		return SoundEvents.BUCKET_FILL_FISH;
	}

	@Override
	public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
		return Bucketable.bucketMobPickup(sourceentity, hand, this).orElse(super.mobInteract(sourceentity, hand));
	}

	@Override
	public boolean requiresCustomPersistence() {
		return super.requiresCustomPersistence() || this.fromBucket();
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return !this.fromBucket() && !this.hasCustomName();
	}

	@Override
	public void baseTick() {
		super.baseTick();
		this.tickDefenseTimers();
		if (this.isAggressive() && this.getTarget() != null) {
			this.applyDefenseBoost();
		}
        this.refreshDimensions();
	}

	@Override
	public EntityDimensions getDefaultDimensions(Pose p_33597_) {
		return super.getDefaultDimensions(p_33597_).scale((float) 1.2);
	}

	@Override
	public void performRangedAttack(LivingEntity target, float flval) {
		FishShootEntity.shoot(this, target, (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttributeValue(Attributes.ATTACK_DAMAGE) : 0) * (2.0 / 5.0));
	}

	public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
		event.register(CAEntities.OCEAN_STONECUTTE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (entityType, world, reason, pos, random) -> {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			return WorldUtils.canCommonSeabornSpawn(world, x, y, z);
		}, RegisterSpawnPlacementsEvent.Operation.REPLACE);
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.2);
		builder = builder.add(Attributes.MAX_HEALTH, 14);
		builder = builder.add(Attributes.ARMOR, 5);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 5);
		builder = builder.add(Attributes.FOLLOW_RANGE, 12);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.33);
		builder = builder.add(Attributes.STEP_HEIGHT, 0.6f);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.ocean_stonecutte.move"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.ocean_stonecutte.die"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.ocean_stonecutte.idle"));
		}
		return PlayState.STOP;
	}

	private PlayState attackingPredicate(AnimationState event) {
		if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
			this.swinging = true;
			this.lastSwing = level().getGameTime();
		}
		if (this.swinging && this.lastSwing + 7L <= level().getGameTime()) {
			this.swinging = false;
		}
		if ((this.swinging || this.entityData.get(DATA_SHOOT)) && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.ocean_stonecutte.attack"));
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
		data.add(new AnimationController<>(this, "movement", 2, this::movementPredicate));
		data.add(new AnimationController<>(this, "attacking", 2, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 2, this::procedurePredicate));
	}


	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}
}
