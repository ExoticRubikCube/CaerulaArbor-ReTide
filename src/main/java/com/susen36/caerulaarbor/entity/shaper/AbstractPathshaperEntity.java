package com.susen36.caerulaarbor.entity.shaper;

import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.entity.base.SeaMonsterBoss;
import com.susen36.caerulaarbor.init.CAGameRules;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.util.EntityUtils;
import com.susen36.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractPathshaperEntity extends SeaMonsterBoss {
	protected static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(AbstractPathshaperEntity.class, EntityDataSerializers.STRING);
	protected static final EntityDataAccessor<Integer> DATA_ATTACK_SKILLP = SynchedEntityData.defineId(AbstractPathshaperEntity.class, EntityDataSerializers.INT);
	protected static final EntityDataAccessor<Integer> DATA_HURT_SKILLP = SynchedEntityData.defineId(AbstractPathshaperEntity.class, EntityDataSerializers.INT);
	protected static final EntityDataAccessor<Integer> DATA_PHASE = SynchedEntityData.defineId(AbstractPathshaperEntity.class, EntityDataSerializers.INT);
	public String animationprocedure = "empty";
	protected String prevAnim = "empty";
	protected boolean swinging;
	protected long lastSwing;

	protected AbstractPathshaperEntity(EntityType<? extends AbstractPathshaperEntity> entityType, Level level) {
		super(entityType, level);
		this.bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.BLUE, ServerBossEvent.BossBarOverlay.NOTCHED_6);
		setNoAi(false);
		this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.5f);
		setPersistenceRequired();
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_ANIMATION, "undefined");
		builder.define(DATA_ATTACK_SKILLP, 0);
		builder.define(DATA_HURT_SKILLP, 0);
		builder.define(DATA_PHASE, 0);
	}

	protected abstract int getHurtSummonThreshold();

	protected abstract EntityType<?> getSummonedFractalType();

	protected int getHurtSkillp() {
		return this.entityData.get(DATA_HURT_SKILLP);
	}

	protected void setHurtSkillp(int hurtSkillp) {
		this.entityData.set(DATA_HURT_SKILLP, hurtSkillp);
	}

	protected int getAttackSkillp() {
		return this.entityData.get(DATA_ATTACK_SKILLP);
	}

	protected void setAttackSkillp(int attackSkillp) {
		this.entityData.set(DATA_ATTACK_SKILLP, attackSkillp);
	}

	protected int getPhase() {
		return this.entityData.get(DATA_PHASE);
	}

	protected void setPhase(int phase) {
		this.entityData.set(DATA_PHASE, phase);
	}

	@Override
	public void die(DamageSource source) {
		if (MapVariables.get(this.level()).strategy_subsisting >= 4 && this.getPhase() == 0) {
			this.setPhase(1);
			if (!this.level().isClientSide()) {
				this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 200, 1, false, false));
				this.addEffect(new MobEffectInstance(CAMobEffects.FAKE_DEATH, 200, 1, false, false));
			}
			return;
		}
		super.die(source);
	}

	protected void summonFractal() {
		if (EntityUtils.getSeabornNum(this.level(), this.getX(), this.getY(), this.getZ()) > this.level().getLevelData().getGameRules().getInt(CAGameRules.CLONE_NUMBER_LIMIT)) {
			return;
		}
		double nearbyCount = 0;
		Vec3 center = new Vec3(this.getX(), this.getY(), this.getZ());
		List<Mob> nearbyEntities = this.level().getEntitiesOfClass(Mob.class, new AABB(center, center).inflate(32), entity -> true);
		for (Mob nearbyEntity : nearbyEntities) {
			if (nearbyEntity instanceof RouteFractalEntity || nearbyEntity instanceof LineringPathshaperEntity) {
				nearbyCount = nearbyCount + 1;
			}
		}
		if (nearbyCount >= 18) {
			return;
		}
		double offsetX = Mth.nextDouble(RandomSource.create(), -0.5, 0.5);
		double offsetZ = Mth.nextDouble(RandomSource.create(), -0.5, 0.5);
		if (this.level() instanceof ServerLevel serverLevel) {
			Entity entityToSpawn = this.getSummonedFractalType().spawn(serverLevel, BlockPos.containing(this.getX() + offsetX, this.getY(), this.getZ() + offsetZ), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
				entityToSpawn.setYRot(this.level().getRandom().nextFloat() * 360F);
				if (entityToSpawn instanceof AbstractFractalEntity fractal) {
					fractal.setOwner(this);
				}
			}
			serverLevel.sendParticles(ParticleTypes.CLOUD, this.getX() + offsetX, this.getY(), this.getZ() + offsetZ, 48, 0.5, 1, 0.5, 0.1);
		}
	}

	protected PlayState procedurePredicate(AnimationState event) {
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

	/**
	 * 处理塑路者本体系实体的移动、待机与死亡动画。
	 *
	 * @param event GeckoLib 动画状态
	 * @return 对应控制器的播放状态
	 */
	protected PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) && !this.isVehicle() && !this.isAggressive() && !this.isSprinting()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.routeshaper.move"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.routeshaper.die"));
			}
			if (this.isSprinting()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.routeshaper.move"));
			}
			if (this.isVehicle() && event.isMoving()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.routeshaper.move"));
			}
			if (this.isAggressive() && event.isMoving() && !this.isVehicle()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.routeshaper.move"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.routeshaper.idle"));
		}
		return PlayState.STOP;
	}

	/**
	 * 处理塑路者本体系实体的普通挥击动画。
	 *
	 * @param event GeckoLib 动画状态
	 * @return 对应控制器的播放状态
	 */
	protected PlayState attackingPredicate(AnimationState event) {
		if (this.getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
			this.swinging = true;
			this.lastSwing = this.level().getGameTime();
		}
		if (this.swinging && this.lastSwing + 19L <= this.level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.routeshaper.attack"));
		}
		return PlayState.CONTINUE;
	}

	public String getSyncedAnimation() {
		return this.entityData.get(DATA_ANIMATION);
	}

	public void setAnimation(String animation) {
		this.entityData.set(DATA_ANIMATION, animation);
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	public SoundEvent getAmbientSound() {
		return SoundEvents.ZOMBIE_VILLAGER_AMBIENT;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.RAVAGER_HURT;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.RAVAGER_DEATH;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(15, new RandomStrollGoal(this, 1));
		this.goalSelector.addGoal(16, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(17, new FloatGoal(this));
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.FALL))
			return false;
		if (source.is(DamageTypes.DROWN))
			return false;
		boolean flag = super.hurt(source, amount);
		if (flag) {
			Entity sourceEntity = source.getEntity();
			int nextHurtSkillp = this.getHurtSkillp() + 1;
			if (nextHurtSkillp >= this.getHurtSummonThreshold()) {
				this.summonFractal();
				if (Math.random() < 0.33) {
					this.summonFractal();
				}
				this.setHurtSkillp(0);
			} else {
				this.setHurtSkillp(nextHurtSkillp);
			}

			if (sourceEntity instanceof LivingEntity attacker) {
				Vec3 center = new Vec3(this.getX(), this.getY(), this.getZ());
				List<AbstractFractalEntity> nearbyEntities = this.level().getEntitiesOfClass(AbstractFractalEntity.class, new AABB(center, center).inflate(64 / 2d), entity -> true).stream()
					.sorted(Comparator.comparingDouble(entity -> entity.distanceToSqr(center)))
					.toList();
				for (AbstractFractalEntity nearbyEntity : nearbyEntities) {
					if (nearbyEntity.hasOwner(this.getUUID())) {
						nearbyEntity.setTarget(attacker);
					}
				}
			}
		}
		return flag;
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		boolean flag = super.doHurtTarget(target);
		if (flag) {
			if (this.getAttackSkillp() >= 2) {
				this.summonFractal();
				this.setAttackSkillp(0);
			} else {
				this.setAttackSkillp(this.getAttackSkillp() + 1);
			}
		}
		return flag;
	}

	@Override
	public void baseTick() {
		super.baseTick();
		if (this.tickCount % 20 == 7) {
			if (this instanceof LineringPathshaperEntity || this instanceof RouteShaperEntity routeShaper && routeShaper.getPhase() == 1) {
				Vec3 center = new Vec3(this.getX(), this.getY(), this.getZ());
				List<Mob> nearbyEntities = this.level().getEntitiesOfClass(Mob.class, new AABB(center, center).inflate(32), entity -> true);
				for (Mob nearbyEntity : nearbyEntities) {
					if (nearbyEntity instanceof RouteFractalEntity routeFractal && !routeFractal.hasEffect(CAMobEffects.SEEK_OF_FRACTAL)) {
						if (!routeFractal.level().isClientSide()) {
							routeFractal.addEffect(new MobEffectInstance(CAMobEffects.SEEK_OF_FRACTAL, -1, 0));
						}
					}
				}
			}
		}
		this.refreshDimensions();
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
        if (world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(this.getX(), this.getY(), this.getZ()), 16, 16, 16), e -> true).isEmpty()) {
            if (!this.level().isClientSide())
                this.addEffect(new MobEffectInstance(MobEffects.GLOWING, 1800, 0, false, false));
        }
        return super.finalizeSpawn(world, difficulty, reason, livingdata);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		data.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
		data.add(new AnimationController<>(this, "attacking", 0, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("AttackCount", this.getAttackSkillp());
		compound.putInt("HurtCount", this.getHurtSkillp());
		compound.putInt("Phase", this.getPhase());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("AttackCount"))
			this.setAttackSkillp(compound.getInt("AttackCount"));
		if (compound.contains("HurtCount"))
			this.setHurtSkillp(compound.getInt("HurtCount"));
		if (compound.contains("Phase"))
			this.setPhase(compound.getInt("Phase"));
	}

	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}

	@Override
	public EntityDimensions getDefaultDimensions(Pose pose) {
		return super.getDefaultDimensions(pose).scale(1F);
	}

	@Override
	public boolean canUsePortal(boolean allowVehicles) {
		return false;
	}

	@Override
	protected void tickDeath() {
		++this.deathTime;
		if (this.deathTime == 20) {
			this.remove(RemovalReason.KILLED);
			this.dropExperience(this.getKillCredit());
			WorldUtils.dropRelicRoute(this.level(), this.getX(), this.getY(), this.getZ());
		}
	}
}
