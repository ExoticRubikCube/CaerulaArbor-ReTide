package com.susen36.caerulaarbor.entity.slime;

import com.susen36.caerulaarbor.entity.base.SeaMonster;
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
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import javax.annotation.Nullable;

public abstract class AbstractSeaSlimeEntity extends SeaMonster {
	protected static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(AbstractSeaSlimeEntity.class, EntityDataSerializers.BOOLEAN);
	protected static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(AbstractSeaSlimeEntity.class, EntityDataSerializers.STRING);
	protected static final EntityDataAccessor<Integer> DATA_SIZE = SynchedEntityData.defineId(AbstractSeaSlimeEntity.class, EntityDataSerializers.INT);
	public String animationprocedure = "empty";
	String prevAnim = "empty";

	protected AbstractSeaSlimeEntity(EntityType<? extends AbstractSeaSlimeEntity> entityType, Level level) {
		super(entityType, level);
		this.xpReward = 2;
		this.setNoAi(false);
	}

	protected void refreshAttributesBySize(int size) {
		int currentSize = this.entityData.get(DATA_SIZE);
		double currentSizeSq = Mth.square((double) Math.max(currentSize, 1));
		double targetSizeSq = Mth.square((double) size);
		if (this.getAttributes().hasAttribute(Attributes.MAX_HEALTH)) {
			double baseHealth = this.getAttribute(Attributes.MAX_HEALTH).getBaseValue() / currentSizeSq;
			this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(baseHealth * targetSizeSq);
		}
		if (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) {
			double baseAttack = this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() / (double) Math.max(currentSize, 1);
			this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(baseAttack * (double) size);
		}
		if (this.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
			double currentSpeed = this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue();
			double speedDelta = 0.005D * ((double) size - (double) Math.max(currentSize, 1));
			this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(currentSpeed + speedDelta);
		}
		if (this.getAttributes().hasAttribute(Attributes.KNOCKBACK_RESISTANCE)) {
			double baseKB = this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).getBaseValue() / (double) Math.max(currentSize, 1);
			this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(baseKB * (double) size);
		}
	}

	protected abstract EntityType<? extends AbstractSeaSlimeEntity> getSplitEntityType();

	protected void onPushEntity(Entity entity) {
	}

	protected boolean isMovingLimbSwing(AnimationState<?> event) {
		return event.isMoving() || !(event.getLimbSwingAmount() > -0.05F && event.getLimbSwingAmount() < 0.05F);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_SHOOT, false);
		builder.define(DATA_ANIMATION, "undefined");
		builder.define(DATA_SIZE, 1);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("Size", this.entityData.get(DATA_SIZE));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Size")) {
			int i = compound.getInt("Size");
			this.refreshAttributesBySize(i);
			this.entityData.set(DATA_SIZE, i);
			this.setHealth(this.getMaxHealth());
		}
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata);
		RandomSource random = world.getRandom();
		int i = random.nextInt(3);
		if (i < 2 && random.nextFloat() < 0.5F * difficulty.getSpecialMultiplier()) {
			i++;
		}
		int size = 1 << i;
		this.refreshAttributesBySize(size);
		this.entityData.set(DATA_SIZE, size);
		this.setHealth(this.getMaxHealth());
		return retval;
	}

	@Override
	public void baseTick() {
		super.baseTick();
		this.refreshDimensions();
	}

	public double getSlimeSize() {
		return this.entityData.get(DATA_SIZE) * 0.5;
	}

	public int getSize() {
		return this.entityData.get(DATA_SIZE);
	}

	public void setSize(int size) {
		this.refreshAttributesBySize(size);
		this.entityData.set(DATA_SIZE, size);
		this.setHealth(this.getMaxHealth());
	}

	@Override
	public EntityDimensions getDefaultDimensions(Pose pose) {
		return super.getDefaultDimensions(pose).scale((float) this.getSlimeSize());
	}

	@Override
	public void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(SoundEvents.SLIME_JUMP_SMALL, 0.15f, 0.5F);
	}

	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.SLIME_HURT;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.SLIME_DEATH;
	}

	@Override
	public boolean causeFallDamage(float l, float d, DamageSource source) {
		this.level().playSound(null, BlockPos.containing(this.getX(), this.getY(), this.getZ()), SoundEvents.SLIME_SQUISH, SoundSource.HOSTILE, 1, 1);
		return super.causeFallDamage(l, d, source);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.FALL)) {
			return false;
		}
		return super.hurt(source, amount);
	}

	@Override
	public void playerTouch(Player player) {
		if (this.isEffectiveAi()) {
			this.dealDamage(player);
		}
	}

	@Override
	public void push(Entity pEntity) {
		super.push(pEntity);
		this.onPushEntity(pEntity);
	}

	protected void dealDamage(LivingEntity target) {
		if (this.isAlive() && this.isWithinMeleeAttackRange(target) && this.hasLineOfSight(target)) {
			DamageSource damagesource = this.damageSources().mobAttack(this);
			if (target.hurt(damagesource, (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE))) {
				this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
				if (this.level() instanceof ServerLevel serverlevel) {
					EnchantmentHelper.doPostAttackEffects(serverlevel, target, damagesource);
				}
			}
		}
	}

	@Override
	protected void tickDeath() {
		++this.deathTime;
		if (this.deathTime >= 14) {
			this.remove(RemovalReason.KILLED);
			this.dropExperience(this.getKillCredit());
			Level world = this.level();
			double x = this.getX();
			double y = this.getY();
			double z = this.getZ();
			int size = this.entityData.get(DATA_SIZE);
			if (size > this.getSplitSizeThreshold()) {
				Vec3 pos = new Vec3(x, y, z);
				if (world instanceof ServerLevel level) {
					RandomSource levelRandom = level.getRandom();
					int t = Mth.nextInt(levelRandom, this.getSplitMinCount(), this.getSplitMaxCount());
					for (int index0 = 0; index0 < t; index0++) {
						Vec3 offset = new Vec3(Mth.nextDouble(levelRandom, -1, 1), 0, Mth.nextDouble(levelRandom, -1, 1));
						Entity entityToSpawn = this.getSplitEntityType().create(level);
						if (entityToSpawn instanceof AbstractSeaSlimeEntity slime) {
							slime.setPos(pos.add(offset));
							int childSize = this.computeSplitChildSize(size, index0);
							slime.refreshAttributesBySize(childSize);
							slime.entityData.set(DATA_SIZE, childSize);
							slime.setHealth(slime.getMaxHealth());
							slime.setYRot(world.getRandom().nextFloat() * 360F);
							level.addFreshEntity(slime);
						}
					}
				}
			} else if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)&&this.shouldDropLoot()) {
				Vec3 pos = new Vec3(x, y, z);
				this.dropLoot(world,pos);
			}
		}
	}

	protected abstract String getAnimationPrefix();

	protected abstract int getSplitSizeThreshold();

	protected abstract int getSplitMinCount();

	protected abstract int getSplitMaxCount();

	protected abstract int computeSplitChildSize(int parentSize, int spawnIndex);

	protected abstract void dropLoot(Level level,Vec3 pos);

	public String getSyncedAnimation() {
		return this.entityData.get(DATA_ANIMATION);
	}

	public void setAnimation(String animation) {
		this.entityData.set(DATA_ANIMATION, animation);
	}

	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		data.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
		data.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
	}

	protected PlayState movementPredicate(AnimationState<?> event) {
		if (this.animationprocedure.equals("empty")) {
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay(this.getAnimationPrefix() + ".die"));
			}
			if (this.isMovingLimbSwing(event)) {
				return event.setAndContinue(RawAnimation.begin().thenLoop(this.getAnimationPrefix() + ".move"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop(this.getAnimationPrefix() + ".idle"));
		}
		return PlayState.STOP;
	}

	protected PlayState procedurePredicate(AnimationState<?> event) {
		if ((!this.animationprocedure.equals("empty") && event.getController().getAnimationState() == AnimationController.State.STOPPED)
			|| (!this.animationprocedure.equals(this.prevAnim) && !this.animationprocedure.equals("empty"))) {
			if (!this.animationprocedure.equals(this.prevAnim)) {
				event.getController().forceAnimationReset();
			}
			event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
			if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
				this.animationprocedure = "empty";
				event.getController().forceAnimationReset();
			}
		} else if (this.animationprocedure.equals("empty")) {
			this.prevAnim = "empty";
			return PlayState.STOP;
		}
		this.prevAnim = this.animationprocedure;
		return PlayState.CONTINUE;
	}
}