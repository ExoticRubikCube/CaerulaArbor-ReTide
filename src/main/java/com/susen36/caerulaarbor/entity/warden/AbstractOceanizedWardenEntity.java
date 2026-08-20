package com.susen36.caerulaarbor.entity.warden;

import com.mojang.serialization.Dynamic;
import com.susen36.babel.init.BabelAttributes;
import com.susen36.babel.util.EPUtils;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.entity.base.SeaMonsterBoss;
import com.susen36.caerulaarbor.init.CADamageTypes;
import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public abstract class AbstractOceanizedWardenEntity extends SeaMonsterBoss implements VibrationSystem {
	protected static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(AbstractOceanizedWardenEntity.class, EntityDataSerializers.BOOLEAN);
	protected static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(AbstractOceanizedWardenEntity.class, EntityDataSerializers.STRING);
	protected static final EntityDataAccessor<Integer> DATA_SKILL_1 = SynchedEntityData.defineId(AbstractOceanizedWardenEntity.class, EntityDataSerializers.INT);
	protected static final EntityDataAccessor<Integer> DATA_SKILL_2 = SynchedEntityData.defineId(AbstractOceanizedWardenEntity.class, EntityDataSerializers.INT);
	protected static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(AbstractOceanizedWardenEntity.class, EntityDataSerializers.INT);
	private static final int VIBRATION_COOLDOWN_TICKS = 40;
	private static final int PROJECTILE_ANGER_DISTANCE = 30;
	public String animationprocedure = "empty";
	protected String prevAnim = "empty";
	protected boolean swinging;
	protected long lastSwing;
	private final DynamicGameEventListener<VibrationSystem.Listener> dynamicGameEventListener;
	private final VibrationSystem.User vibrationUser;
	private VibrationSystem.Data vibrationData;
	private int vibrationCooldown;
	@Nullable
	private LivingEntity pendingVibrationTarget;

	protected AbstractOceanizedWardenEntity(EntityType<? extends AbstractOceanizedWardenEntity> type, Level world) {
		super(type, world);
		this.vibrationUser = new VibrationUser();
		this.vibrationData = new VibrationSystem.Data();
		this.dynamicGameEventListener = new DynamicGameEventListener<>(new VibrationSystem.Listener(this));
		this.bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.BLUE, ServerBossEvent.BossBarOverlay.NOTCHED_6);
		this.xpReward = 1024;
		this.setNoAi(false);
		this.setPersistenceRequired();
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_SHOOT, false);
		builder.define(DATA_ANIMATION, "undefined");
		builder.define(DATA_SKILL_1, 100);
		builder.define(DATA_SKILL_2, 120);
		builder.define(DATA_DURATION, 0);
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
	public void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(SoundEvents.WARDEN_STEP, 0.15F, 1);
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		double targetX = target.getX();
		double targetY = target.getY();
		double targetZ = target.getZ();
		if (!this.level().isClientSide()) {
			CaerulaArbor.queueServerWork(10, () -> {
				if (target.isAlive() && this.distanceTo(target) <= 3) {
					this.level().playSound(null, BlockPos.containing(targetX, targetY, targetZ),
							SoundEvents.WARDEN_ATTACK_IMPACT, SoundSource.HOSTILE,
							(float) Mth.nextDouble(RandomSource.create(), 0.9, 1.1), 1);
					this.performRangedAttack(false, 1, targetX, targetY, targetZ, null, null);
				}
			});
		}
		return true;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(5, new VibrationTargetGoal(SeaMonster.class));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 2, true) {

			@Override
			public boolean canUse() {
				return super.canUse() && AbstractOceanizedWardenEntity.this.isDurative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && AbstractOceanizedWardenEntity.this.isDurative();
			}
		});
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

	protected void performRangedAttack(boolean isSonic, double rate, double x, double y, double z, @Nullable Set<Integer> knockedEntities, @Nullable Vec3 knockbackDir) {
		LivingEntity target = this.getTarget();
		double radius = 2.5;
		double radiusSq = radius * radius;
		Vec3 center = new Vec3(x, y, z);
		boolean melee = !isSonic;
		List<LivingEntity> nearbyEntities = this.level().getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(radius), entity -> {
			if (!(entity instanceof Mob) && !(entity instanceof Player)) {
				return false;
			}
			if (entity == this) {
				return false;
			}
			if (center.distanceToSqr(new Vec3(entity.getX(), entity.getY(), entity.getZ())) > radiusSq) {
				return false;
			}
			if (melee) {
				double dx = entity.getX() - this.getX();
				double dz = entity.getZ() - this.getZ();
				double horLen = Math.sqrt(dx * dx + dz * dz);
				if (horLen > 1.0E-4D) {
					float yaw = this.getYRot() * ((float) Math.PI / 180F);
					float facingX = -Mth.sin(yaw);
					float facingZ = Mth.cos(yaw);
					double tx = dx / horLen;
					double tz = dz / horLen;
					double cosAngle = facingX * tx + facingZ * tz;
					if (cosAngle < Math.cos(Math.PI / 4.0D)) {
						return false;
					}
				}
			}
			if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "seaborn"))) && entity != target) {
				return false;
			}
			return true;
		});
		for (LivingEntity nearbyEntity : nearbyEntities) {
			double damage = (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * rate;
			if (isSonic) {
				if (nearbyEntity.hurt(this.level().damageSources().sonicBoom(this), (float) damage)) {
					EPUtils.causeSanityInjury(nearbyEntity, damage * 0.5);
					if (knockedEntities != null && knockbackDir != null && knockedEntities.add(nearbyEntity.getId())) {
						double kbRes = nearbyEntity.getAttributes().hasAttribute(Attributes.KNOCKBACK_RESISTANCE) ? 1.0 - nearbyEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE) : 1.0;
						double horizontal = 2.5 * kbRes;
						double vertical = 0.5 * kbRes;
						nearbyEntity.push(knockbackDir.x * horizontal, knockbackDir.y * vertical, knockbackDir.z * horizontal);
					}
				}
			} else {
				nearbyEntity.hurt(CADamageTypes.wardenAttack(this.level(), this), (float) damage);
			}
		}
	}

	protected void performSonicBoom(Entity target, double rate, int particleCount) {
		if (target == null) {
			return;
		}
		double chestY = this.getY() + this.getBbHeight() * 0.6;
		Vec3 targetEye = target.getEyePosition();
		double vx = targetEye.x - this.getX();
		double vy = targetEye.y - chestY;
		double vz = targetEye.z - this.getZ();
		double length = Math.sqrt(vx * vx + vy * vy + vz * vz);
		if (length > 0) {
			vx /= length;
			vy /= length;
			vz /= length;
		} else {
			Vec3 look = this.getLookAngle();
			vx = look.x;
			vy = look.y;
			vz = look.z;
		}
		int segmentCount = length > 0 ? Mth.floor(length) + 7 : 22;
		Set<Integer> knockedEntities = new HashSet<>();
		Vec3 knockbackDir = new Vec3(vx, vy, vz);
		for (int index = 0; index < segmentCount; index++) {
			double tx = this.getX() + vx * (index + 1);
			double ty = chestY + vy * (index + 1);
			double tz = this.getZ() + vz * (index + 1);
			this.performRangedAttack(true, rate, tx, ty, tz, knockedEntities, knockbackDir);
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
		if (this.hasEffect(CAMobEffects.INVULNERABLE)) {
			return false;
		}
		float cappedAmount = Math.min(amount, this.getMaxHealth() * 0.3F);
		return super.hurt(source, cappedAmount);
	}

	@Override
	public void setTarget(@Nullable LivingEntity target) {
		LivingEntity previousTarget = this.getTarget();
		super.setTarget(target);
		if (target != null && target != previousTarget) {
			this.entityData.set(DATA_SKILL_1, Math.max(this.entityData.get(DATA_SKILL_1), 100));
		}
	}

	@Override
	public void die(DamageSource source) {
		super.die(source);
		Level world = this.level();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();

		CaerulaArbor.queueServerWork(10, () -> {
			world.playSound(null, BlockPos.containing(x, y, z), SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 0.1F, 1);
			for (int index = 0; index < 3; index++) {
				double t = Mth.nextDouble(RandomSource.create(), 0, 6.283);
				double p = Mth.nextDouble(RandomSource.create(), 0, 6.283);
				double distance = 4;
				if (world instanceof ServerLevel level) {
					level.sendParticles(ParticleTypes.SONIC_BOOM, x + distance * Math.cos(t) * Math.cos(p), y + distance * Math.sin(p), z + distance * Math.sin(t) * Math.cos(p), 2, 0, 0, 0, 0.1);
				}
			}
		});

		CaerulaArbor.queueServerWork(20, () -> {
			world.playSound(null, BlockPos.containing(x, y, z), SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 0.2F, 1);
			for (int index = 0; index < 5; index++) {
				double t = Mth.nextDouble(RandomSource.create(), 0, 6.283);
				double p = Mth.nextDouble(RandomSource.create(), 0, 6.283);
				double distance = 4;
				if (world instanceof ServerLevel level) {
					level.sendParticles(ParticleTypes.SONIC_BOOM, x + distance * Math.cos(t) * Math.cos(p), y + distance * Math.sin(p), z + distance * Math.sin(t) * Math.cos(p), 2, 0, 0, 0, 0.1);
				}
			}
		});

		CaerulaArbor.queueServerWork(32, () -> {
			world.playSound(null, BlockPos.containing(x, y, z), SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 0.3F, 1);
			for (int index = 0; index < 9; index++) {
				double t = Mth.nextDouble(RandomSource.create(), 0, 6.283);
				double p = Mth.nextDouble(RandomSource.create(), 0, 6.283);
				double distance = 4;
				if (world instanceof ServerLevel level) {
					level.sendParticles(ParticleTypes.SONIC_BOOM, x + distance * Math.cos(t) * Math.cos(p), y + distance * Math.sin(p), z + distance * Math.sin(t) * Math.cos(p), 2, 0, 0, 0, 0.1);
				}
			}
		});

		CaerulaArbor.queueServerWork(35, () -> {
			world.playSound(null, BlockPos.containing(x, y, z), SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 0.4F, 1);
			for (int index = 0; index < 9; index++) {
				double t = Mth.nextDouble(RandomSource.create(), 0, 6.283);
				double p = Mth.nextDouble(RandomSource.create(), 0, 6.283);
				double distance = 4;
				if (world instanceof ServerLevel level) {
					level.sendParticles(ParticleTypes.SONIC_BOOM, x + distance * Math.cos(t) * Math.cos(p), y + distance * Math.sin(p), z + distance * Math.sin(t) * Math.cos(p), 2, 0, 0, 0, 0.1);
				}
			}
		});

		CaerulaArbor.queueServerWork(38, () -> {
			world.playSound(null, BlockPos.containing(x, y, z), SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 0.5F, 1);
			for (int index = 0; index < 9; index++) {
				double t = Mth.nextDouble(RandomSource.create(), 0, 6.283);
				double p = Mth.nextDouble(RandomSource.create(), 0, 6.283);
				double distance = 4;
				if (world instanceof ServerLevel level) {
					level.sendParticles(ParticleTypes.SONIC_BOOM, x + distance * Math.cos(t) * Math.cos(p), y + distance * Math.sin(p), z + distance * Math.sin(t) * Math.cos(p), 2, 0, 0, 0, 0.1);
				}
			}
		});

		CaerulaArbor.queueServerWork(40, () -> {
			world.playSound(null, BlockPos.containing(x, y, z), SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 2, 1);
		});

		CaerulaArbor.queueServerWork(47, () -> {
			boolean hasSound = false;
			Vec3 center = new Vec3(x, y, z);
			List<LivingEntity> nearbyEntities = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(21), entity -> true);
			for (LivingEntity nearbyEntity : nearbyEntities) {
				if (!(nearbyEntity instanceof Mob) && !(nearbyEntity instanceof Player)) {
					continue;
				}
				if (nearbyEntity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "seaborn")))) {
					continue;
				}
				if (nearbyEntity == this) {
					continue;
				}
				if (nearbyEntity instanceof Player player && player.isCreative()) {
					continue;
				}
				double dx = x - nearbyEntity.getX();
				double dz = z - nearbyEntity.getZ();
				double dy = y - nearbyEntity.getY();
				double hDistSqr = Mth.square(dx) + Mth.square(dz);
				double vDist = Math.abs(dy);
				if (hDistSqr <= Mth.square(21.0) && vDist <= 21.0) {
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
		VibrationSystem.Data.CODEC.encodeStart(NbtOps.INSTANCE, this.vibrationData)
			.resultOrPartial(CaerulaArbor.LOGGER::error)
			.ifPresent(nbt -> compound.put("listener", nbt));
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
		if (compound.contains("listener", 10)) {
			VibrationSystem.Data.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, compound.getCompound("listener")))
				.resultOrPartial(CaerulaArbor.LOGGER::error)
				.ifPresent(data -> this.vibrationData = data);
		}
	}

	@Override
	public void tick() {
		if (this.level() instanceof ServerLevel serverLevel) {
			VibrationSystem.Ticker.tick(serverLevel, this.vibrationData, this.vibrationUser);
			if (this.vibrationCooldown > 0) {
				--this.vibrationCooldown;
			}
		}
		super.tick();
	}

	@Override
	public void customServerAiStep() {
		super.customServerAiStep();
		if (this.tickCount + this.getId() % 120 == 0) {
			MobEffectInstance mobeffectinstance = new MobEffectInstance(MobEffects.DARKNESS, 260, 0, false, false);
			MobEffectUtil.addEffectToPlayersAround((ServerLevel) this.level(), this, this.position(), 20, mobeffectinstance, 200);
		}
	}

	protected abstract String getAnimationPrefix();

	protected abstract int getAttackAnimationLength();

	@Override
	public void baseTick() {
		super.baseTick();
		Level world = this.level();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		if (this.isAlive()) {
			int skillp1 = this.entityData.get(DATA_SKILL_1);
			int skillp2 = this.entityData.get(DATA_SKILL_2);
			int duration = this.entityData.get(DATA_DURATION);
			LivingEntity target = this.getTarget();
			if (duration > 0) {
				this.entityData.set(DATA_DURATION, duration - 1);
				if (target != null && target.isAlive()) {
					this.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
				}
			}

			if (skillp1 > 0) {
				this.entityData.set(DATA_SKILL_1, skillp1 - 1);
			} else if (target != null && target.isAlive()) {
				double hDistSqr = Mth.square(target.getX() - this.getX()) + Mth.square(target.getZ() - this.getZ());
				double vDist = Math.abs(target.getY() - this.getY());
				if (!(hDistSqr > Mth.square(21.0) || vDist > 21.0 || duration > 0 || !this.canTargetEntity(target))) {
					final LivingEntity lockedTarget = target;
					this.entityData.set(DATA_DURATION, 45);
					this.setAnimation(this.getAnimationPrefix() + ".sonic");
					if (!this.level().isClientSide()) {
						this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 45, 0, false, false));
						this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 45, 9, false, false));
					}
					if (world instanceof Level level) {
						level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 2, 1);
					}
					CaerulaArbor.queueServerWork(30, () -> {
						boolean valid = this.isAlive() && lockedTarget.isAlive();
						if (valid) {
							double dx = this.getX() - lockedTarget.getX();
							double dz = this.getZ() - lockedTarget.getZ();
							double dy = this.getY() - lockedTarget.getY();
							double cbHDistSqr = Mth.square(dx) + Mth.square(dz);
							double cbVDist = Math.abs(dy);
							valid = cbHDistSqr <= Mth.square(21.0) && cbVDist <= 21.0;
						}
						if (!valid) {
							this.entityData.set(DATA_DURATION, 0);
							this.setAnimation("undefined");
							this.removeEffect(CAMobEffects.INVULNERABLE);
							this.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
							return;
						}
						if (world instanceof Level level) {
							level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 2, 1);
						}
						this.performSonicBoom(lockedTarget, 0.25, 3);
					});
					this.entityData.set(DATA_SKILL_1, 200);
				}
			}

			if (skillp2 > 0) {
				this.entityData.set(DATA_SKILL_2, skillp2 - 1);
			} else if (target != null && target.isAlive()) {
				if (!(this.distanceTo(target) > 4 || duration > 0)) {
					final LivingEntity lockedTarget = target;
					this.entityData.set(DATA_DURATION, 45);
					this.setAnimation(this.getAnimationPrefix() + ".combo");
					CaerulaArbor.queueServerWork(12, () -> {
						if (this.isAlive() && lockedTarget.isAlive() && this.distanceTo(lockedTarget) <= 4) {
							lockedTarget.hurt(CADamageTypes.wardenAttack(world, this),
									(float) ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 1.5));
							double dx = lockedTarget.getX() - this.getX();
							double dz = lockedTarget.getZ() - this.getZ();
							lockedTarget.knockback(2.5, dx, dz);
							Vec3 velocity = lockedTarget.getDeltaMovement();
							lockedTarget.setDeltaMovement(velocity.x, 1.25, velocity.z);
						}
					});
					CaerulaArbor.queueServerWork(20, () -> {
						if (world instanceof Level level) {
							level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 2, 1);
						}
						if (!this.level().isClientSide()) {
							this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 25, 9, false, false));
						}
					});
					CaerulaArbor.queueServerWork(27, () -> {
						if (world instanceof Level level) {
							level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 2, 1);
						}
						if (lockedTarget.isAlive()) {
							this.performSonicBoom(lockedTarget, 0.15, 1);
						}
					});
					this.entityData.set(DATA_SKILL_2, 300);
				}
			}

			int gap = 40;
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
			if (gap > 0 && target != null && target.isAlive() && this.tickCount % gap == 0) {
				if (world instanceof Level level) {
					level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.WARDEN_HEARTBEAT, SoundSource.HOSTILE, 2,
							Mth.nextInt(RandomSource.create(),  0,  1));
				}
			}
		}
		this.refreshDimensions();
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
	public boolean canDisableShield() {
		return true;
	}

	@Override
	public void handleEntityEvent(byte id) {
		if (id == 4) {
			this.swinging = true;
		} else if (id == 62) {
			this.setAnimation(this.getAnimationPrefix() + ".sonic");
		} else {
			super.handleEntityEvent(id);
		}
	}

	class VibrationTargetGoal extends TargetGoal {
		private final Class<?>[] toIgnoreDamage;
		@Nullable
		protected LivingEntity target;
		protected TargetingConditions targetConditions;

		public VibrationTargetGoal(Class<?>... toIgnoreDamage) {
			super(AbstractOceanizedWardenEntity.this, false, false);
			this.toIgnoreDamage = toIgnoreDamage;
			this.setFlags(EnumSet.of(Flag.TARGET));
			this.targetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).ignoreLineOfSight().ignoreInvisibilityTesting().selector(living -> {
				if (!AbstractOceanizedWardenEntity.this.canTargetEntity(living)) {
					return false;
				}
				if (AbstractOceanizedWardenEntity.this.isInWater() ^ living.isInWater()) {
					return false;
				}
				for (Class<?> oclass : this.toIgnoreDamage) {
					if (oclass.isAssignableFrom(living.getClass())) {
						return false;
					}
				}
				return true;
			});
		}

		@Override
		public boolean canUse() {
			if (AbstractOceanizedWardenEntity.this.getTarget() != null) {
				return false;
			}
			this.findTarget();
			return this.target != null;
		}

		protected void findTarget() {
			LivingEntity candidate = AbstractOceanizedWardenEntity.this.pendingVibrationTarget;
			if (candidate != null && candidate.isAlive() && this.canAttack(candidate, this.targetConditions)) {
				this.target = candidate;
			} else {
				this.target = null;
				AbstractOceanizedWardenEntity.this.pendingVibrationTarget = null;
			}
		}

		@Override
		public void start() {
			AbstractOceanizedWardenEntity.this.setTarget(this.target);
			this.targetMob = this.target;
			this.unseenMemoryTicks = 300;
			AbstractOceanizedWardenEntity.this.pendingVibrationTarget = null;
			super.start();
		}

		public void setTarget(@Nullable LivingEntity target) {
			this.target = target;
		}
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.185);
		builder = builder.add(Attributes.MAX_HEALTH, 650);
		builder = builder.add(Attributes.ARMOR, 8);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 38);
		builder = builder.add(Attributes.FOLLOW_RANGE, 36);
		builder = builder.add(Attributes.ATTACK_KNOCKBACK, 2);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 3);
		builder = builder.add(BabelAttributes.MAGIC_RESISTANCE, 75);
		builder = builder.add(BabelAttributes.ELEMENTAL_MODIFIER, 0.75);
		builder = builder.add(Attributes.STEP_HEIGHT, 0.6F);
		return builder;
	}

	protected PlayState movementPredicate(AnimationState event) {
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

	protected PlayState attackingPredicate(AnimationState event) {
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

	protected PlayState procedurePredicate(AnimationState event) {
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
			this.dropExperience(this.getKillCredit());
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
			this.hurt(CADamageTypes.source(this.level(), CADamageTypes.OCEANKILLER_DAMAGE), 20);
			return;
		}
		super.remove(reason);
	}

	@Override
	public void setHealth(float health) {
		super.setHealth(health);
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

	public boolean canTargetEntity(@Nullable Entity entity) {
		if (entity instanceof LivingEntity living) {
			if (this.level() == entity.level() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity) && !this.isAlliedTo(entity) && !living.isInvulnerable() && !living.isDeadOrDying() && this.level().getWorldBorder().isWithinBounds(living.getBoundingBox())) {
				return living.getType() != EntityType.ARMOR_STAND && living.getType() != this.getType() && living.getType().getCategory() != MobCategory.WATER_CREATURE && !living.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "seaborn")));
			}
		}
		return false;
	}

	@Override
	public VibrationSystem.Data getVibrationData() {
		return this.vibrationData;
	}

	@Override
	public VibrationSystem.User getVibrationUser() {
		return this.vibrationUser;
	}

	@Override
	public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> consumer) {
		if (this.level() instanceof ServerLevel serverLevel) {
			consumer.accept(this.dynamicGameEventListener, serverLevel);
		}
	}

	class VibrationUser implements VibrationSystem.User {
		private final PositionSource positionSource = new EntityPositionSource(AbstractOceanizedWardenEntity.this, AbstractOceanizedWardenEntity.this.getEyeHeight());

		@Override
		public int getListenerRadius() {
			return 16;
		}

		@Override
		public PositionSource getPositionSource() {
			return this.positionSource;
		}

		@Override
		public TagKey<GameEvent> getListenableEvents() {
			return GameEventTags.WARDEN_CAN_LISTEN;
		}

		@Override
		public boolean canTriggerAvoidVibration() {
			return true;
		}

		@Override
		public boolean canReceiveVibration(ServerLevel level, BlockPos pos, Holder<GameEvent> event, GameEvent.Context context) {
			if (AbstractOceanizedWardenEntity.this.getTarget() != null || AbstractOceanizedWardenEntity.this.isNoAi() || AbstractOceanizedWardenEntity.this.isDeadOrDying() || AbstractOceanizedWardenEntity.this.vibrationCooldown > 0 || !level.getWorldBorder().isWithinBounds(pos)) {
				return false;
			}
			Entity sourceEntity = context.sourceEntity();
			return !(sourceEntity instanceof LivingEntity livingEntity) || AbstractOceanizedWardenEntity.this.canTargetEntity(livingEntity);
		}

		@Override
		public void onReceiveVibration(ServerLevel level, BlockPos pos, Holder<GameEvent> event, @Nullable Entity sourceEntity, @Nullable Entity projectileOwner, float distance) {
			if (!AbstractOceanizedWardenEntity.this.isDeadOrDying()) {
				AbstractOceanizedWardenEntity.this.vibrationCooldown = VIBRATION_COOLDOWN_TICKS;
				AbstractOceanizedWardenEntity.this.playSound(SoundEvents.WARDEN_TENDRIL_CLICKS, 5.0F, AbstractOceanizedWardenEntity.this.getVoicePitch());

				LivingEntity targetEntity = null;
				if (projectileOwner != null) {
					if (AbstractOceanizedWardenEntity.this.closerThan(projectileOwner, PROJECTILE_ANGER_DISTANCE) && projectileOwner instanceof LivingEntity living && AbstractOceanizedWardenEntity.this.canTargetEntity(living) && !(AbstractOceanizedWardenEntity.this.isInWater() ^ living.isInWater())) {
						targetEntity = living;
					}
				} else if (sourceEntity instanceof LivingEntity living && AbstractOceanizedWardenEntity.this.canTargetEntity(living) && !(AbstractOceanizedWardenEntity.this.isInWater() ^ living.isInWater())) {
					targetEntity = living;
				}
				if (targetEntity != null) {
					AbstractOceanizedWardenEntity.this.pendingVibrationTarget = targetEntity;
				}
			}
		}
	}
}