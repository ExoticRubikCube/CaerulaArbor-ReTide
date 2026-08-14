package com.susen36.caerulaarbor.entity;

import com.susen36.babel.init.BabelAttributes;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CASounds;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import javax.annotation.Nullable;
import java.util.Comparator;

public class AbsorberLimbEntity extends SeaMonster {
	public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(AbsorberLimbEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(AbsorberLimbEntity.class, EntityDataSerializers.STRING);
	private boolean swinging;
	private long lastSwing;
	public String animationprocedure = "empty";

	public AbsorberLimbEntity(Level world) {
		this(CAEntities.ABSORBER_LIMB.get(), world);
	}

	public AbsorberLimbEntity(EntityType<AbsorberLimbEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);
		setPersistenceRequired();
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
		this.goalSelector.addGoal(1, new RandomLookAroundGoal(this));
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
		super.dropCustomDeathLoot(level, damageSource, recentlyHit);
		this.spawnAtLocation(new ItemStack(CAItems.NERVOUS_REGENERATION.get()));
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
	public void die(DamageSource source) {
		super.die(source);
		LevelAccessor world = this.level();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		Entity thirster;
		thirster = world.getEntitiesOfClass(ThirsterEntity.class, AABB.ofSize(new Vec3(x, y, z), 48, 48, 48), e -> true).stream().min(new Object() {
			Comparator<Entity> compareDistOf(double x, double y, double z) {
				return Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(x, y, z));
			}
		}.compareDistOf(x, y, z)).orElse(null);
		if (!(thirster == null)) {
			if (thirster instanceof LivingEntity livingEntity3 && livingEntity3.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE))
				livingEntity3.getAttribute(CAAttributes.GENERAL_DEFENSE)
						.setBaseValue(Math.max((thirster instanceof LivingEntity livingEntity2 && livingEntity2.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE)
								? livingEntity2.getAttribute(CAAttributes.GENERAL_DEFENSE).getBaseValue()
								: 0) - 1, 0));
			if (thirster instanceof LivingEntity livingEntity5 && livingEntity5.getAttributes().hasAttribute(BabelAttributes.MAGIC_RESISTANCE)) {
				livingEntity5.getAttribute(BabelAttributes.MAGIC_RESISTANCE)
						.setBaseValue(Math.max((livingEntity5.getAttributes().hasAttribute(BabelAttributes.MAGIC_RESISTANCE)
								? livingEntity5.getAttribute(BabelAttributes.MAGIC_RESISTANCE).getBaseValue()
								: 0) - 5, 0));
			}
		}
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
		this.setHealth(this.getMaxHealth() * 2 / 3);
		this.setAnimation("animation.absorber_limb.start");
		return super.finalizeSpawn(world, difficulty, reason, livingdata);
	}

	@Override
	public void baseTick() {
		super.baseTick();
		setDeltaMovement(new Vec3(0, 0, 0));
		if (tickCount >= 16 && this.getHealth() >= this.getMaxHealth()) {
			this.kill();
		}
		this.refreshDimensions();
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	protected void doPush(Entity entityIn) {
	}

	@Override
	protected void pushEntities() {
	}


	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
		builder = builder.add(Attributes.MAX_HEALTH, 81);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 1);
		builder = builder.add(Attributes.FOLLOW_RANGE, 24);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 1);
		builder = builder.add(Attributes.STEP_HEIGHT, 0.6f);
		return builder;
	}

	@Override
	public boolean canBeAffected(MobEffectInstance pEffectInstance) {
		if (pEffectInstance.getEffect() == MobEffects.REGENERATION) return false;
		return super.canBeAffected(pEffectInstance);
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.absorber_limb.die"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.absorber_limb.idle"));
		}
		return PlayState.STOP;
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
		data.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
		data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
	}

	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}
}