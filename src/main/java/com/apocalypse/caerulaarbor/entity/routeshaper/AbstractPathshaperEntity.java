package com.apocalypse.caerulaarbor.entity.routeshaper;

import com.apocalypse.caerulaarbor.capability.map.MapVariables;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CAAttributes;
import com.apocalypse.caerulaarbor.init.CAGameRules;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import com.apocalypse.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractPathshaperEntity extends SeaMonster {
	protected static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(AbstractPathshaperEntity.class, EntityDataSerializers.STRING);
	protected static final EntityDataAccessor<Integer> DATA_ATTACK_SKILLP = SynchedEntityData.defineId(AbstractPathshaperEntity.class, EntityDataSerializers.INT);
	protected static final EntityDataAccessor<Integer> DATA_HURT_SKILLP = SynchedEntityData.defineId(AbstractPathshaperEntity.class, EntityDataSerializers.INT);
	protected static final EntityDataAccessor<Integer> DATA_PHASE = SynchedEntityData.defineId(AbstractPathshaperEntity.class, EntityDataSerializers.INT);
	protected final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.BLUE, ServerBossEvent.BossBarOverlay.NOTCHED_6);
	public String animationprocedure = "empty";
	protected String prevAnim = "empty";
	protected boolean swinging;
	protected long lastSwing;

	protected AbstractPathshaperEntity(EntityType<? extends AbstractPathshaperEntity> entityType, Level level) {
		super(entityType, level);
		setNoAi(false);
		setMaxUpStep(1.5f);
		setPersistenceRequired();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(DATA_ATTACK_SKILLP, 0);
		this.entityData.define(DATA_HURT_SKILLP, 0);
		this.entityData.define(DATA_PHASE, 0);
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
				this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 200, 1, false, false));
				this.addEffect(new MobEffectInstance(CAMobEffects.FAKE_DEATH.get(), 200, 1, false, false));
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
		List<Entity> nearbyEntities = this.level().getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(64 / 2d), entity -> true).stream()
			.sorted(Comparator.comparingDouble(entity -> entity.distanceToSqr(center)))
			.toList();
		for (Entity nearbyEntity : nearbyEntities) {
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
			}
			serverLevel.sendParticles(ParticleTypes.CLOUD, this.getX() + offsetX, this.getY(), this.getZ() + offsetZ, 48, 0.5, 1, 0.5, 0.1);
		}
	}

	protected PlayState procedurePredicate(AnimationState<?> event) {
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
	protected PlayState movementPredicate(AnimationState<?> event) {
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
	 * 处理塑路者本体系实体的普攻挥击动画。
	 *
	 * @param event GeckoLib 动画状态
	 * @return 对应控制器的播放状态
	 */
	protected PlayState attackingPredicate(AnimationState<?> event) {
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
		return this.entityData.get(ANIMATION);
	}

	public void setAnimation(String animation) {
		this.entityData.set(ANIMATION, animation);
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	public SoundEvent getAmbientSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.zombie_villager.ambient"));
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.ravager.hurt"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.ravager.death"));
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 16;
			}

			@Override
			public boolean canUse() {
				return super.canUse() && !hasEffect(CAMobEffects.FAKE_DEATH.get());
			}

			@Override
			public boolean canContinueToUse() {
				return super.canUse() && !hasEffect(CAMobEffects.FAKE_DEATH.get());
			}
		});
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true, false));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, SnowGolem.class, true, false));
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Villager.class, true, false));
		this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Illusioner.class, true, false));
		this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, Pillager.class, true, false));
		this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, Vindicator.class, true, false));
		this.targetSelector.addGoal(9, new NearestAttackableTargetGoal<>(this, Witch.class, true, false));
		this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, Piglin.class, true, false));
		this.targetSelector.addGoal(11, new NearestAttackableTargetGoal<>(this, PiglinBrute.class, true, false));
		this.targetSelector.addGoal(12, new NearestAttackableTargetGoal<>(this, ZombifiedPiglin.class, true, false));
		this.targetSelector.addGoal(13, new NearestAttackableTargetGoal<>(this, Player.class, true, false) {
			@Override
			public boolean canUse() {
				double x = AbstractPathshaperEntity.this.getX();
				double y = AbstractPathshaperEntity.this.getY();
				double z = AbstractPathshaperEntity.this.getZ();
				Level world = AbstractPathshaperEntity.this.level();
				return super.canUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
			}

			@Override
			public boolean canContinueToUse() {
				double x = AbstractPathshaperEntity.this.getX();
				double y = AbstractPathshaperEntity.this.getY();
				double z = AbstractPathshaperEntity.this.getZ();
				Level world = AbstractPathshaperEntity.this.level();
				return super.canContinueToUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
			}
		});
		this.targetSelector.addGoal(14, new NearestAttackableTargetGoal<>(this, Animal.class, true, false) {
			@Override
			public boolean canUse() {
				return super.canUse() && EntityUtils.canAttackAnimals();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && EntityUtils.canAttackAnimals();
			}
		});
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
					nearbyEntity.setTarget(attacker);
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
				List<Entity> nearbyEntities = this.level().getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(64 / 2d), entity -> true).stream()
					.sorted(Comparator.comparingDouble(entity -> entity.distanceToSqr(center)))
					.toList();
				for (Entity nearbyEntity : nearbyEntities) {
					if (nearbyEntity instanceof RouteFractalEntity routeFractal && !routeFractal.hasEffect(CAMobEffects.SEEK_OF_FRACTAL.get())) {
						if (!routeFractal.level().isClientSide()) {
							routeFractal.addEffect(new MobEffectInstance(CAMobEffects.SEEK_OF_FRACTAL.get(), 999, 0));
						}
					}
				}
			}
		}
		this.refreshDimensions();
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
        if (this.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get()))
            this.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).setBaseValue(24);
        if (world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(this.getX(), this.getY(), this.getZ()), 16, 16, 16), e -> true).isEmpty()) {
            if (!this.level().isClientSide())
                this.addEffect(new MobEffectInstance(MobEffects.GLOWING, 1800, 0, false, false));
        }
        return retval;
	}

	@Override
	public void startSeenByPlayer(ServerPlayer player) {
		super.startSeenByPlayer(player);
		this.bossInfo.addPlayer(player);
	}

	@Override
	public void stopSeenByPlayer(ServerPlayer player) {
		super.stopSeenByPlayer(player);
		this.bossInfo.removePlayer(player);
	}

	@Override
	public void customServerAiStep() {
		super.customServerAiStep();
		this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
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
		// TODO: Other entities that still use MCreator-style flattened NBT keys should be migrated to semantic split keys too.
		compound.putInt("AttackCount", this.getAttackSkillp());
		compound.putInt("HurtCount", this.getHurtSkillp());
		compound.putInt("Dataphase", this.getPhase());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("AttackCount"))
			this.setAttackSkillp(compound.getInt("AttackCount"));
		if (compound.contains("HurtCount"))
			this.setHurtSkillp(compound.getInt("HurtCount"));
		if (compound.contains("Dataphase"))
			this.setPhase(compound.getInt("Dataphase"));
	}



	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}

	@Override
	public EntityDimensions getDimensions(Pose pose) {
		return super.getDimensions(pose).scale(1F);
	}

	@Override
	public boolean canChangeDimensions() {
		return false;
	}

	@Override
	protected void tickDeath() {
		++this.deathTime;
		if (this.deathTime == 20) {
			this.remove(RemovalReason.KILLED);
			this.dropExperience();
			WorldUtils.dropRelicRoute(this.level(), this.getX(), this.getY(), this.getZ());
		}
	}
}
