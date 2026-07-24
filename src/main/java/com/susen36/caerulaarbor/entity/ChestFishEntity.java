package com.susen36.caerulaarbor.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;

public class ChestFishEntity extends SeaMonster {
	public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(ChestFishEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(ChestFishEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Boolean> DATA_RELEASE = SynchedEntityData.defineId(ChestFishEntity.class, EntityDataSerializers.BOOLEAN);
	private boolean swinging;
	private long lastSwing;
	public String animationprocedure = "empty";

	public ChestFishEntity(Level world) {
		this(CAEntities.CHEST_FISH.get(), world);
	}

	public ChestFishEntity(EntityType<ChestFishEntity> type, Level world) {
		super(type, world);
		xpReward = 16;
		setNoAi(false);
		setMaxUpStep(0.6f);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_SHOOT, false);
		this.entityData.define(DATA_ANIMATION, "undefined");
		this.entityData.define(DATA_RELEASE, false);
	}

	@Override
	public boolean canCollideWith(Entity entity) {
		return true;
	}

	@Override
	public boolean canBeCollidedWith() {
		Entity entity = this;
		return EntityUtils.isAlive(entity);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 3.24;
			}

			@Override
			public boolean canUse() {
				if (!super.canUse()) return false;
				return !isShiftKeyDown();
			}

			@Override
			public boolean canContinueToUse() {
				if (!super.canContinueToUse()) return false;
				return !isShiftKeyDown();
			}

		});
		this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1) {
			@Override
			public boolean canUse() {
				if (!super.canUse()) return false;
				return !isShiftKeyDown();
			}

			@Override
			public boolean canContinueToUse() {
				if (!super.canContinueToUse()) return false;
				return !isShiftKeyDown();
			}
		});
		this.goalSelector.addGoal(4, new RandomLookAroundGoal(this) {
			@Override
			public boolean canUse() {
				if (!super.canUse()) return false;
				return !isShiftKeyDown();
			}

			@Override
			public boolean canContinueToUse() {
				if (!super.canContinueToUse()) return false;
				return !isShiftKeyDown();
			}
		});
	}

    @Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return SoundEvents.ARMOR_STAND_HIT;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.ARMOR_STAND_BREAK;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.FALL))
			return false;
		if (source.is(DamageTypes.DROWN))
			return false;
		boolean flag = super.hurt(source, amount);
		if (flag) this.startChest(this.level(), this.getX(), this.getY(), this.getZ(), source.getEntity());
		return flag;
	}

	@Override
	public void die(DamageSource source) {
		super.die(source);
		if (source.getEntity() instanceof ServerPlayer player) {
			Advancement advancement = player.server.getAdvancements().getAdvancement(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "treasures"));
			AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
			if (!progress.isDone()) {
				for (String criteria : progress.getRemainingCriteria())
					player.getAdvancements().award(advancement, criteria);
			}
		}
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
		this.setYRot((float) (90 * Mth.nextInt(RandomSource.create(), 0, 3)));
		this.setXRot(0);
		this.setYBodyRot(this.getYRot());
		this.setYHeadRot(this.getYRot());
		this.yRotO = this.getYRot();
		this.xRotO = this.getXRot();
		this.yBodyRotO = this.getYRot();
		this.yHeadRotO = this.getYRot();
		return retval;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("Release", this.entityData.get(DATA_RELEASE));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Release")) {
		    this.entityData.set(DATA_RELEASE, compound.getBoolean("Release"));
		}
	}

	@Override
	public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
		super.mobInteract(sourceentity, hand);
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
        Level world = this.level();
		return this.startChest(world, x, y, z, sourceentity);
	}

	@Override
	public void baseTick() {
		super.baseTick();
        if (!((Entity) this instanceof ChestFishEntity datEntL0 && datEntL0.getEntityData().get(DATA_RELEASE))) {
            setShiftKeyDown(true);
            if (!this.level().isClientSide())
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 8, false, false));
        } else {
            setShiftKeyDown(false);
        }
        this.refreshDimensions();
	}


	public static void registerSpawnPlacements() {
		SpawnPlacements.register(CAEntities.CHEST_FISH.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				(entityType, world, reason, pos, random) -> (world.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(world, pos, random) && Mob.checkMobSpawnRules(entityType, world, reason, pos, random)));
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.2);
		builder = builder.add(CAAttributes.MAGIC_RESISTANCE.get(), 18);
		builder = builder.add(Attributes.MAX_HEALTH, 120);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 12);
		builder = builder.add(Attributes.FOLLOW_RANGE, 19);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.85);
		return builder;
	}

	private PlayState movementPredicate(AnimationState<?> event) {
		if (this.isDeadOrDying()) {
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.chest_fish.die"));
		}
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

			&& this.entityData.get(DATA_RELEASE)) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.chest_fish.move"));
			}
			if (this.isShiftKeyDown()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.chest_fish.chest"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.chest_fish.idle"));
		}
		return PlayState.STOP;
	}

	private PlayState attackingPredicate(AnimationState<?> event) {
		if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
			this.swinging = true;
			this.lastSwing = level().getGameTime();
		}
		if (this.swinging && this.lastSwing + 20L <= level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.chest_fish.attack"));
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
			this.remove(ChestFishEntity.RemovalReason.KILLED);
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
		data.add(new AnimationController<>(this, "movement", 3, this::movementPredicate));
		data.add(new AnimationController<>(this, "attacking", 3, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 3, this::procedurePredicate));
	}

	public InteractionResult startChest(Level world, double x, double y, double z, Entity sourceentity) {
		if (this.isShiftKeyDown()) {
			this.setAnimation("animation.chest_fish.start");
			if (!world.isClientSide()) {
				world.playSound(null, BlockPos.containing(x, y, z), SoundEvents.CHEST_OPEN, SoundSource.HOSTILE, 1, 1);
			} else {
				world.playLocalSound(x, y, z, SoundEvents.CHEST_OPEN, SoundSource.HOSTILE, 1, 1, false);
			}
			this.setShiftKeyDown(false);
			this.getEntityData().set(DATA_RELEASE, true);
			this.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
			if (sourceentity instanceof LivingEntity ent)
				this.setTarget(ent);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}


	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}
}
