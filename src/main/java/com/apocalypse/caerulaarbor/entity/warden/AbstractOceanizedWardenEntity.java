package com.apocalypse.caerulaarbor.entity.warden;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.capability.sanity.SIHelper;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CAAttributes;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Comparator;
import java.util.List;

public abstract class AbstractOceanizedWardenEntity extends SeaMonster {
	protected static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(AbstractOceanizedWardenEntity.class, EntityDataSerializers.BOOLEAN);
	protected static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(AbstractOceanizedWardenEntity.class, EntityDataSerializers.STRING);
	protected static final EntityDataAccessor<Integer> DATA_SKILL_1 = SynchedEntityData.defineId(AbstractOceanizedWardenEntity.class, EntityDataSerializers.INT);
	protected static final EntityDataAccessor<Integer> DATA_SKILL_2 = SynchedEntityData.defineId(AbstractOceanizedWardenEntity.class, EntityDataSerializers.INT);
	protected static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(AbstractOceanizedWardenEntity.class, EntityDataSerializers.INT);
	protected final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.BLUE, ServerBossEvent.BossBarOverlay.NOTCHED_6);
	public String animationprocedure = "empty";
	protected String prevAnim = "empty";
	protected boolean swinging;
	protected long lastSwing;

	protected AbstractOceanizedWardenEntity(EntityType<? extends AbstractOceanizedWardenEntity> type, Level world) {
		super(type, world);
		this.xpReward = 1024;
		this.setNoAi(false);
		this.setMaxUpStep(0.6F);
		this.setPersistenceRequired();
	}

        protected abstract SoundEvent getAmbientSoundEvent();

        protected abstract SoundEvent getHurtSoundEvent();

        protected abstract SoundEvent getDeathSoundEvent();

	protected abstract String getAnimationPrefix();

	protected abstract int getAttackAnimationLength();

	protected abstract int getInitialHeartbeatGap();

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_SHOOT, false);
		this.entityData.define(DATA_ANIMATION, "undefined");
		this.entityData.define(DATA_SKILL_1, 100);
		this.entityData.define(DATA_SKILL_2, 120);
		this.entityData.define(DATA_DURATION, 0);
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
                return this.getAmbientSoundEvent();
	}

	@Override
	public void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(SoundEvents.WARDEN_STEP, 0.15F, 1);
	}

	@Override
	public SoundEvent getHurtSound(DamageSource source) {
                return this.getHurtSoundEvent();
	}

	@Override
	public SoundEvent getDeathSound() {
                return this.getDeathSoundEvent();
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		double targetX = target.getX();
		double targetY = target.getY();
		double targetZ = target.getZ();
		if (!this.level().isClientSide()) {
			CaerulaArborMod.queueServerWork(10, () -> {
				if (target.isAlive() && this.distanceTo(target) <= 6) {
					this.level().playSound(null, BlockPos.containing(targetX, targetY, targetZ),
							SoundEvents.WARDEN_ATTACK_IMPACT, SoundSource.HOSTILE,
							(float) Mth.nextDouble(RandomSource.create(), 0.9, 1.1), 1);
					this.performRangedAttack(false, 1, targetX, targetY, targetZ);
				}
			});
		}
		return true;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(AbstractOceanizedWardenEntity.this, 2, true) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 5.0625;
			}

			@Override
			public boolean canUse() {
				return super.canUse() && AbstractOceanizedWardenEntity.this.isDurative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && AbstractOceanizedWardenEntity.this.isDurative();
			}
		});
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true, false));
		this.goalSelector.addGoal(4, new RandomStrollGoal(AbstractOceanizedWardenEntity.this, 1) {
			@Override
			public boolean canUse() {
				return super.canUse() && AbstractOceanizedWardenEntity.this.isDurative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && AbstractOceanizedWardenEntity.this.isDurative();
			}
		});
		this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 9F));
		this.goalSelector.addGoal(6, new RandomLookAroundGoal(AbstractOceanizedWardenEntity.this) {
			@Override
			public boolean canUse() {
				return super.canUse() && AbstractOceanizedWardenEntity.this.isDurative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && AbstractOceanizedWardenEntity.this.isDurative();
			}
		});
	}

	protected void performRangedAttack(boolean isSonic, double rate, double x, double y, double z) {
		Entity enemy = this.getTarget();
		double radius = isSonic ? 4.5 : 3;
		Vec3 center = new Vec3(x, y, z);
		List<Entity> nearbyEntities = this.level().getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(radius), entity -> true).stream()
				.sorted(Comparator.comparingDouble(candidate -> candidate.distanceToSqr(center)))
				.toList();
		for (Entity nearbyEntity : nearbyEntities) {
			if (!(nearbyEntity instanceof Mob) && !(nearbyEntity instanceof Player)) {
				continue;
			}
			if (nearbyEntity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring"))) && nearbyEntity != enemy) {
				continue;
			}
			if (nearbyEntity == this) {
				continue;
			}
			if (center.distanceTo(new Vec3(nearbyEntity.getX(), nearbyEntity.getY(), nearbyEntity.getZ())) <= radius) {
				double damage = (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * rate;
				if (isSonic) {
					nearbyEntity.hurt(new DamageSource(this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "warden_sonic"))), this), (float) damage);
					if (nearbyEntity instanceof LivingEntity livingEntity) {
						SIHelper.causeSanityInjury(livingEntity, damage * 1.5);
					}
				} else {
					nearbyEntity.hurt(
							new DamageSource(this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
									.getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "warden_attack"))), this),
							(float) damage);
				}
			}
		}
	}

	protected void performSonicBoom(Entity target, double rate, int particleCount) {
		if (target == null) {
			return;
		}
		double vx = target.getX() - this.getX();
		double vy = target.getY() - this.getY();
		double vz = target.getZ() - this.getZ();
		double length = Math.sqrt(vx * vx + vy * vy + vz * vz);
		if (length > 0) {
			vx /= length;
			vy /= length;
			vz /= length;
		} else {
			vx = this.getLookAngle().y;
			vy = this.getLookAngle().x;
			vz = this.getLookAngle().z;
		}
		for (int index = 0; index < 32; index++) {
			double tx = this.getX() + vx * (index + 1);
			double ty = this.getY() + 1.5 + vy * (index + 1);
			double tz = this.getZ() + vz * (index + 1);
			this.performRangedAttack(true, rate, tx, ty, tz);
			if (this.level() instanceof ServerLevel serverLevel) {
				serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, tx, ty, tz, particleCount, 0.1, 0.1, 0.1, 0.1);
			}
		}
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.IN_FIRE)) {
			return false;
		}
		if (source.is(DamageTypes.FALL)) {
			return false;
		}
		if (source.is(DamageTypes.DROWN)) {
			return false;
		}
		return super.hurt(source, amount);
	}

	@Override
	public void die(DamageSource source) {
		super.die(source);
		LevelAccessor world = this.level();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();

		CaerulaArborMod.queueServerWork(10, () -> {
			if (world instanceof Level level) {
				level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 0.1F, 1);
			}
			for (int index = 0; index < 3; index++) {
				double t = Mth.nextDouble(RandomSource.create(), 0, 6.283);
				double p = Mth.nextDouble(RandomSource.create(), 0, 6.283);
				double distance = 4;
				if (world instanceof ServerLevel level) {
					level.sendParticles(ParticleTypes.SONIC_BOOM, x + distance * Math.cos(t) * Math.cos(p), y + distance * Math.sin(p), z + distance * Math.sin(t) * Math.cos(p), 2, 0, 0, 0, 0.1);
				}
			}
		});

		CaerulaArborMod.queueServerWork(20, () -> {
			if (world instanceof Level level) {
				level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 0.2F, 1);
			}
			for (int index = 0; index < 5; index++) {
				double t = Mth.nextDouble(RandomSource.create(), 0, 6.283);
				double p = Mth.nextDouble(RandomSource.create(), 0, 6.283);
				double distance = 4;
				if (world instanceof ServerLevel level) {
					level.sendParticles(ParticleTypes.SONIC_BOOM, x + distance * Math.cos(t) * Math.cos(p), y + distance * Math.sin(p), z + distance * Math.sin(t) * Math.cos(p), 2, 0, 0, 0, 0.1);
				}
			}
		});

		CaerulaArborMod.queueServerWork(32, () -> {
			if (world instanceof Level level) {
				level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 0.3F, 1);
			}
			for (int index = 0; index < 9; index++) {
				double t = Mth.nextDouble(RandomSource.create(), 0, 6.283);
				double p = Mth.nextDouble(RandomSource.create(), 0, 6.283);
				double distance = 4;
				if (world instanceof ServerLevel level) {
					level.sendParticles(ParticleTypes.SONIC_BOOM, x + distance * Math.cos(t) * Math.cos(p), y + distance * Math.sin(p), z + distance * Math.sin(t) * Math.cos(p), 2, 0, 0, 0, 0.1);
				}
			}
		});

		CaerulaArborMod.queueServerWork(35, () -> {
			if (world instanceof Level level) {
				level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 0.4F, 1);
			}
			for (int index = 0; index < 9; index++) {
				double t = Mth.nextDouble(RandomSource.create(), 0, 6.283);
				double p = Mth.nextDouble(RandomSource.create(), 0, 6.283);
				double distance = 4;
				if (world instanceof ServerLevel level) {
					level.sendParticles(ParticleTypes.SONIC_BOOM, x + distance * Math.cos(t) * Math.cos(p), y + distance * Math.sin(p), z + distance * Math.sin(t) * Math.cos(p), 2, 0, 0, 0, 0.1);
				}
			}
		});

		CaerulaArborMod.queueServerWork(38, () -> {
			if (world instanceof Level level) {
				level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 0.5F, 1);
			}
			for (int index = 0; index < 9; index++) {
				double t = Mth.nextDouble(RandomSource.create(), 0, 6.283);
				double p = Mth.nextDouble(RandomSource.create(), 0, 6.283);
				double distance = 4;
				if (world instanceof ServerLevel level) {
					level.sendParticles(ParticleTypes.SONIC_BOOM, x + distance * Math.cos(t) * Math.cos(p), y + distance * Math.sin(p), z + distance * Math.sin(t) * Math.cos(p), 2, 0, 0, 0, 0.1);
				}
			}
		});

		CaerulaArborMod.queueServerWork(40, () -> {
			if (world instanceof Level level) {
				level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 2, 1);
			}
		});

		CaerulaArborMod.queueServerWork(47, () -> {
			boolean hasSound = false;
			Vec3 center = new Vec3(x, y, z);
			List<Entity> nearbyEntities = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(32), entity -> true).stream()
					.sorted(Comparator.comparingDouble(entity -> entity.distanceToSqr(center)))
					.toList();
			for (Entity nearbyEntity : nearbyEntities) {
				if (!(nearbyEntity instanceof Mob) && !(nearbyEntity instanceof Player)) {
					continue;
				}
				if (nearbyEntity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
					continue;
				}
				if (nearbyEntity == this) {
					continue;
				}
				if (nearbyEntity instanceof Player player && player.isCreative()) {
					continue;
				}
				if (this.distanceTo(nearbyEntity) <= 32) {
					this.performSonicBoom(nearbyEntity, 0.25, 3);
					hasSound = true;
				}
			}
			if (hasSound && world instanceof Level level) {
				level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 22, 1);
			}
		});
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("Skill1", this.entityData.get(DATA_SKILL_1));
		compound.putInt("Skill2", this.entityData.get(DATA_SKILL_2));
		compound.putInt("Duration", this.entityData.get(DATA_DURATION));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Skill1")) {
		    this.entityData.set(DATA_SKILL_1, compound.getInt("Skill1"));
		}
		if (compound.contains("Skill2")) {
		    this.entityData.set(DATA_SKILL_2, compound.getInt("Skill2"));
		}
		if (compound.contains("Duration")) {
		    this.entityData.set(DATA_DURATION, compound.getInt("Duration"));
		}
	}

	@Override
	public void baseTick() {
		super.baseTick();
		LevelAccessor world = this.level();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		if (this.isAlive()) {
			if (this.tickCount % 100 == 0) {
				Vec3 center = new Vec3(x, y, z);
				List<Entity> nearbyEntities = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(24 / 2D), entity -> true).stream()
						.sorted(Comparator.comparingDouble(entity -> entity.distanceToSqr(center)))
						.toList();
				for (Entity nearbyEntity : nearbyEntities) {
					if (nearbyEntity instanceof Player player && player.isCreative() || nearbyEntity instanceof Player player1 && player1.isSpectator()) {
						continue;
					}
					if (nearbyEntity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
						continue;
					}
					if (!(nearbyEntity instanceof LivingEntity livingEntity && livingEntity.hasEffect(MobEffects.DARKNESS))) {
						if (nearbyEntity instanceof LivingEntity && !this.level().isClientSide()) {
							this.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 1200, 0, false, false));
						}
					}
				}
			}

			int skillp1 = this.entityData.get(DATA_SKILL_1);
			int skillp2 = this.entityData.get(DATA_SKILL_2);
			int duration = this.entityData.get(DATA_DURATION);
			Entity enemy = this.getTarget();
			if (duration > 0) {
				this.entityData.set(DATA_DURATION, duration - 1);
			}

			if (skillp1 > 0) {
				this.entityData.set(DATA_SKILL_1, skillp1 - 1);
			} else if (enemy != null && enemy.isAlive()) {
				if (!(this.distanceTo(enemy) > 32 || duration > 0)) {
					this.entityData.set(DATA_DURATION, 45);
					this.setAnimation(this.getAnimationPrefix() + ".sonic");
					if (!this.level().isClientSide()) {
						this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 45, 0, false, false));
						this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 45, 9, false, false));
					}
					this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(enemy.getX(), enemy.getY(), enemy.getZ()));
					if (world instanceof Level level) {
						level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 2, 1);
					}
					CaerulaArborMod.queueServerWork(30, () -> {
						if (this.isAlive()) {
							if (world instanceof Level level) {
								level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 2, 1);
							}
							this.performSonicBoom(this.getTarget(), 0.25, 3);
						}
					});
					this.entityData.set(DATA_SKILL_1, 200);
				}
			}

			if (skillp2 > 0) {
				this.entityData.set(DATA_SKILL_2, skillp2 - 1);
			} else if (enemy != null && enemy.isAlive()) {
				if (!(this.distanceTo(enemy) > 4 || duration > 0)) {
					this.entityData.set(DATA_DURATION, 45);
					this.setAnimation(this.getAnimationPrefix() + ".combo");
					this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(enemy.getX(), enemy.getY(), enemy.getZ()));
					CaerulaArborMod.queueServerWork(12, () -> {
						Entity target = this.getTarget();
						if (this.isAlive() && target != null) {
							if (this.distanceTo(target) <= 4) {
								target.hurt(
										new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
												.getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "warden_attack"))), this),
										(float) ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 1.5));
							}
							target.push(0, 1.25, 0);
						}
					});
					CaerulaArborMod.queueServerWork(20, () -> {
						if (world instanceof Level level) {
							level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 2, 1);
						}
						if (!this.level().isClientSide()) {
							this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 25, 9, false, false));
						}
					});
					CaerulaArborMod.queueServerWork(27, () -> {
						if (world instanceof Level level) {
							level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 2, 1);
						}
						Entity target = this.getTarget();
						if (target != null) {
							this.performSonicBoom(target, 0.15, 1);
						}
					});
					this.entityData.set(DATA_SKILL_2, 300);
				}
			}

			int gap = this.getInitialHeartbeatGap();
			if (skillp1 < 60) {
				gap = 10;
			} else if (skillp1 < 100) {
				gap = 20;
			}
			if (skillp2 < 60) {
				gap = 10;
			} else if (skillp2 < 100) {
				gap = 20;
			}
			if (gap > 0 && enemy != null && enemy.isAlive() && this.tickCount % gap == 0) {
				if (world instanceof Level level) {
					level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.WARDEN_HEARTBEAT, SoundSource.HOSTILE, 2,
							Mth.nextInt(RandomSource.create(), (int) 0.9, (int) 1.05));
				}
			}
		}
		this.refreshDimensions();
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

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.15);
		builder = builder.add(Attributes.MAX_HEALTH, 825);
		builder = builder.add(Attributes.ARMOR, 10);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 50);
		builder = builder.add(Attributes.FOLLOW_RANGE, 48);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
		builder = builder.add(CAAttributes.MAGIC_RESISTANCE.get(), 75);
		builder = builder.add(CAAttributes.SANITY_MODIFIER.get(), 0.01);
		return builder;
	}

	protected PlayState movementPredicate(AnimationState<?> event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) && !this.isAggressive()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop(this.getAnimationPrefix() + ".move"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay(this.getAnimationPrefix() + ".die"));
			}
			if (this.isAggressive() && event.isMoving()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop(this.getAnimationPrefix() + ".sprint"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop(this.getAnimationPrefix() + ".idle"));
		}
		return PlayState.STOP;
	}

	protected PlayState attackingPredicate(AnimationState<?> event) {
		if (this.getAttackAnim(event.getPartialTick()) > 0F && !this.swinging) {
			this.swinging = true;
			this.lastSwing = this.level().getGameTime();
		}
		if (this.swinging && this.lastSwing + this.getAttackAnimationLength() <= this.level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay(this.getAnimationPrefix() + ".attack"));
		}
		return PlayState.CONTINUE;
	}

	protected PlayState procedurePredicate(AnimationState<?> event) {
		if (!this.animationprocedure.equals("empty") && event.getController().getAnimationState() == AnimationController.State.STOPPED || (!this.animationprocedure.equals(this.prevAnim) && !this.animationprocedure.equals("empty"))) {
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

	@Override
	protected void tickDeath() {
		++this.deathTime;
		if (this.deathTime == 50) {
			this.remove(RemovalReason.KILLED);
			this.dropExperience();
			LevelAccessor world = this.level();
			if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
				for (int index = 0; index < 64; index++) {
					if (world instanceof ServerLevel level) {
						level.addFreshEntity(new ExperienceOrb(level, this.getX() + Mth.nextDouble(RandomSource.create(), -1, 1), this.getY(),
								this.getZ() + Mth.nextDouble(RandomSource.create(), -1, 1), Mth.nextInt(RandomSource.create(), 32, 64)));
					}
				}
			}
		}
	}

	@Override
	public void remove(RemovalReason reason) {
		if (this.level().getDifficulty() != Difficulty.PEACEFUL && reason == RemovalReason.DISCARDED) {
			this.hurt(
					new DamageSource(this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
							.getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceankiller_damage")))),
					20);
			return;
		}
		super.remove(reason);
	}

	@Override
	public void setHealth(float health) {
		float currentHealth = this.getHealth();
		float maxHealth = this.getMaxHealth();
		if (this.hasEffect(CAMobEffects.INVULNERABLE.get()) && health < currentHealth) {
			return;
		}
		float reduction = currentHealth - health;
		super.setHealth(reduction >= maxHealth * 0.3F ? currentHealth - maxHealth * 0.3F : currentHealth - reduction);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		data.add(new AnimationController<>(this, "movement", 1, this::movementPredicate));
		data.add(new AnimationController<>(this, "attacking", 1, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 1, this::procedurePredicate));
	}

	protected boolean isDurative() {
		return this.getEntityData().get(DATA_DURATION) <= 0;
	}

	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}
}
