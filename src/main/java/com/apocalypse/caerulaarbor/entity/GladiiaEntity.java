package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.SyncedAnimationEntity;
import com.apocalypse.caerulaarbor.init.CAAttributes;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.init.CASounds;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Comparator;
import java.util.List;

public class GladiiaEntity extends Animal implements GeoEntity, SyncedAnimationEntity {

	public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(GladiiaEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_SKILL_P = SynchedEntityData.defineId(GladiiaEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(GladiiaEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_SKILL_P2 = SynchedEntityData.defineId(GladiiaEntity.class, EntityDataSerializers.INT);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private long lastSwing;
	public String animationprocedure = "empty";

	public GladiiaEntity(Level world) {
		this(CAEntities.GLADIIA.get(), world);
	}

	public GladiiaEntity(EntityType<GladiiaEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);
		setMaxUpStep(1.2f);
		setPersistenceRequired();
	}

	public static GladiiaEntity getGladiiaAround(LevelAccessor world, double x, double y, double z) {
		GladiiaEntity gladiia = world.getEntitiesOfClass(GladiiaEntity.class, AABB.ofSize(new Vec3(x, y, z), 64, 64, 64), e -> true).stream().min(new Object() {
			Comparator<GladiiaEntity> compareDistOf(double queryX, double queryY, double queryZ) {
				return Comparator.comparingDouble(candidate -> candidate.distanceToSqr(queryX, queryY, queryZ));
			}
		}.compareDistOf(x, y, z)).orElse(null);
		if (gladiia != null && gladiia.isAlive()) {
			return gladiia;
		}
		return null;
	}

	public static void healFromGladiia(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null) {
			return;
		}
		if (entity.tickCount % 5 == 0) {
			if (getGladiiaAround(world, x, y, z) != null) {
				if (entity instanceof LivingEntity livingEntity) {
					livingEntity.setHealth((float) (livingEntity.getHealth() + livingEntity.getMaxHealth() * 0.008));
				}
			}
		}
	}

	public static void spawnGladiiaLinkParticles(LevelAccessor world, Entity entity, Entity target) {
		if (entity == null || target == null) {
			return;
		}
		double fromX = entity.getX();
		double fromY = entity.getY();
		double fromZ = entity.getZ();
		double vx = target.getX() - fromX;
		double vy = target.getY() - fromY;
		double vz = target.getZ() - fromZ;
		double size = Math.max(Math.min(Math.round(Math.sqrt(vx * vx + vy * vy + vz * vz)), 32), 1);
		for (int index0 = 0; index0 < (int) size; index0++) {
			if (world instanceof ServerLevel level) {
				level.sendParticles(ParticleTypes.DRIPPING_WATER, fromX + (vx / size) * index0, fromY + (vy / size) * index0 + 0.5, fromZ + (vz / size) * index0, 8, 0.32, 0.5, 0.32, 0.05);
			}
		}
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_ANIMATION, "undefined");
		this.entityData.define(DATA_SKILL_P, 100);
		this.entityData.define(DATA_DURATION, 0);
		this.entityData.define(DATA_SKILL_P2, 200);
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 2, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 20.25;
			}

			@Override
			public boolean canUse() {
				return super.canUse() && isGladiiaDurative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && isGladiiaDurative();
			}

		});
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Monster.class, true, false));
		this.goalSelector.addGoal(4, new OpenDoorGoal(this, false));
		this.goalSelector.addGoal(5, new OpenDoorGoal(this, true));
		this.goalSelector.addGoal(6, new RandomStrollGoal(this, 1) {
			@Override
			public boolean canUse() {
				return super.canUse() && isGladiiaDurative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && isGladiiaDurative();
			}
		});
		this.goalSelector.addGoal(7, new RandomLookAroundGoal(this) {
			@Override
			public boolean canUse() {
				return super.canUse() && isGladiiaDurative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && isGladiiaDurative();
			}
		});
		this.goalSelector.addGoal(8, new FloatGoal(this));
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEFINED;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return CASounds.GLADIIA_HURT.get();
	}

	@Override
	public SoundEvent getDeathSound() {
		return CASounds.GLADIIA_DIE.get();
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		double targetX = target.getX();
		double targetY = target.getY();
		double targetZ = target.getZ();
		if (!this.level().isClientSide()) {
			this.level().playSound(null, BlockPos.containing(targetX, targetY, targetZ),
					CASounds.GLADIIA_ATTACK_PRE.get(), SoundSource.NEUTRAL, 2.2F, 1);
			CaerulaArborMod.queueServerWork(9, () -> {
				if (this.isAlive() && target.isAlive() && this.distanceTo(target) <= 5) {
					this.level().playSound(null, BlockPos.containing(targetX, targetY, targetZ),
							CASounds.GLADIIA_ATTACK_HIT.get(), SoundSource.NEUTRAL, 2.75F, 1);
					this.hurtWithHunterAttack(target, (float) (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0));
				}
			});
		}
		return true;
	}

	private boolean hurtWithHunterAttack(Entity target, float amount) {
		if (EntityUtils.getSize(target) < EntityUtils.getSize(this) * 2) {
			amount *= 1.3F;
		}
		if (EntityUtils.getHealthPerc(target) < EntityUtils.getHealthPerc(this)) {
			amount *= 1.5F;
		}
		return target.hurt(
				new DamageSource(
						this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
								.getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hunter_attack"))),
						this),
				amount);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.DROWN))
			return false;
		Entity sourceEntity = source.getEntity();
		if (sourceEntity != null && EntityUtils.getSize(sourceEntity) >= EntityUtils.getSize(this) * 2) {
			amount *= 0.75F;
		}
		return super.hurt(source, amount);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("SkillP", this.entityData.get(DATA_SKILL_P));
		compound.putInt("Duration", this.entityData.get(DATA_DURATION));
		compound.putInt("SkillP2", this.entityData.get(DATA_SKILL_P2));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("SkillP")) {
		    this.entityData.set(DATA_SKILL_P, compound.getInt("SkillP"));
		}
		if (compound.contains("Duration")) {
		    this.entityData.set(DATA_DURATION, compound.getInt("Duration"));
		}
		if (compound.contains("SkillP2")) {
		    this.entityData.set(DATA_SKILL_P2, compound.getInt("SkillP2"));
		}
	}

	@Override
	public void baseTick() {
		super.baseTick();
		LevelAccessor world = this.level();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		Entity enemy;
		double gap = 0;
		double sklp1;
		double dura;
		double skillp2;
		if (this.isAlive()) {
			sklp1 = (Entity) this instanceof GladiiaEntity _datEntI ? _datEntI.getEntityData().get(DATA_SKILL_P) : 0;
			skillp2 = (Entity) this instanceof GladiiaEntity _datEntI ? _datEntI.getEntityData().get(DATA_SKILL_P2) : 0;
			dura = (Entity) this instanceof GladiiaEntity _datEntI ? _datEntI.getEntityData().get(DATA_DURATION) : 0;
			enemy = (Entity) this instanceof Mob _mobEnt ? _mobEnt.getTarget() : null;
			if (dura > 0) {
				if ((Entity) this instanceof GladiiaEntity _datEntSetI)
					_datEntSetI.getEntityData().set(DATA_DURATION, (int) (dura - 1));
			}
			if (sklp1 > 0) {
				if ((Entity) this instanceof GladiiaEntity _datEntSetI)
					_datEntSetI.getEntityData().set(DATA_SKILL_P, (int) (sklp1 - 1));
			} else {
				if (!(enemy == null) && enemy.isAlive()) {
					if (distanceTo(enemy) <= 7.5 && dura <= 0) {
						if (this instanceof GladiiaEntity) {
							this.setAnimation("animation.gladiia.pull");
						}
						if ((Entity) this instanceof GladiiaEntity _datEntSetI)
							_datEntSetI.getEntityData().set(DATA_SKILL_P, 160);
						if ((Entity) this instanceof GladiiaEntity _datEntSetI)
							_datEntSetI.getEntityData().set(DATA_DURATION, 30);
						if (world instanceof Level _level) {
							_level.playSound(null, BlockPos.containing(x, y, z), CASounds.GLADIIA_PULL_PRE.get(), SoundSource.NEUTRAL, (float) 2.5, 1);
						}
						((Entity) this).lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((enemy.getX()), (enemy.getY() + 1.6), (enemy.getZ())));
						CaerulaArborMod.queueServerWork(10, () -> {
							if (this.isAlive()) {
								Entity ene = this.getTarget();
								if (ene == null)
									return;
								Entity side;
								double damage;
								if (world instanceof Level _level) {
									_level.playSound(null, BlockPos.containing(ene.getX(), ene.getY(), ene.getZ()), CASounds.GLADIIA_PULL_PULL.get(), SoundSource.NEUTRAL, 3, 1);
								}
								EntityUtils.pullToward(ene, this);
								GladiiaEntity.spawnGladiiaLinkParticles(world, this, ene);
								damage = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
								this.hurtWithHunterAttack(ene, (float) (damage * 3));
								side = EntityUtils.catchNearestEnemy(world, ene.getX(), ene.getY(), ene.getZ(), ene);
								if (!(side == null)) {
									EntityUtils.pullToward(side, this);
									GladiiaEntity.spawnGladiiaLinkParticles(world, this, side);
									this.hurtWithHunterAttack(side, (float) (damage * 3));
								}
								CaerulaArborMod.queueServerWork(10, () -> {
									if (world instanceof Level _level) {
										_level.playSound(null, BlockPos.containing(ene.getX(), ene.getY(), ene.getZ()), CASounds.GLADIIA_ATTACK_PRE.get(), SoundSource.NEUTRAL, 3, 1);
									}
									if (ene instanceof LivingEntity _entity && !this.level().isClientSide())
										this.addEffect(new MobEffectInstance(CAMobEffects.DIZZY.get(), 40, 0, false, false));
									if (EntityUtils.catchNearestEnemy(world, ene.getX(), ene.getY(), ene.getZ(), ene) instanceof LivingEntity _entity && !this.level().isClientSide())
										this.addEffect(new MobEffectInstance(CAMobEffects.DIZZY.get(), 40, 0, false, false));
								});
							}
						});
					}
				}
			}
			if (skillp2 > 0) {
				if ((Entity) this instanceof GladiiaEntity _datEntSetI)
					_datEntSetI.getEntityData().set(DATA_SKILL_P2, (int) (skillp2 - 1));
			} else {
				if (!(enemy == null) && enemy.isAlive()) {
					if (distanceTo(enemy) <= 21 && dura <= 0) {
						if (this instanceof GladiiaEntity) {
							this.setAnimation("animation.gladiia.float");
						}
						if (SpecterEntity.isSpecterAround(world, x, y, z)) {
							if ((Entity) this instanceof GladiiaEntity _datEntSetI)
								_datEntSetI.getEntityData().set(DATA_SKILL_P2, 400);
						} else {
							if ((Entity) this instanceof GladiiaEntity _datEntSetI)
								_datEntSetI.getEntityData().set(DATA_SKILL_P2, 500);
						}
						if ((Entity) this instanceof GladiiaEntity _datEntSetI)
							_datEntSetI.getEntityData().set(DATA_DURATION, 120);
						if (world instanceof Level _level) {
							_level.playSound(null, BlockPos.containing(x, y, z), CASounds.GLADIIA_SKILL_RELEASE.get(), SoundSource.NEUTRAL, 3, 1);
						}
						if (world instanceof Level _level) {
							_level.playSound(null, BlockPos.containing(x, y, z), CASounds.GLADIIA_SKILL.get(), SoundSource.NEUTRAL, 2, 1);
						}
						((Entity) this).lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((enemy.getX()), (enemy.getY() + 1.6), (enemy.getZ())));
						if (!this.level().isClientSide())
							this.addEffect(new MobEffectInstance(CAMobEffects.ADD_ATTACK_PERCLY.get(), 120, 4, false, false));
						if (!this.level().isClientSide())
							this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 120, 9, false, false));
						if (enemy instanceof LivingEntity _entity && !this.level().isClientSide())
							this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 3, false, false));
						if (world instanceof ServerLevel _level) {
							Entity entityToSpawn = CAEntities.GLADIIA_WHIRL.get().spawn(_level, BlockPos.containing(enemy.getX(), enemy.getY(), enemy.getZ()), MobSpawnType.MOB_SUMMONED);
							if (entityToSpawn != null) {
								entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
							}
						}
						CaerulaArborMod.queueServerWork(111, () -> {
							if (this.isAlive()) {
								if (world instanceof Level _level) {
									_level.playSound(null, BlockPos.containing(x, y, z), CASounds.GLADIIA_PULL_PRE.get(), SoundSource.NEUTRAL, (float) 2.5, 1);
								}
							}
						});
						CaerulaArborMod.queueServerWork(114, () -> {
							if (this.isAlive()) {
								Entity ene = this.getTarget();
								if (ene == null)
									return;
								Entity side = null;
								double damage;
								double d;
								if (world instanceof Level _level) {
									_level.playSound(null, BlockPos.containing(ene.getX(), ene.getY(), ene.getZ()), CASounds.GLADIIA_PULL_PULL.get(), SoundSource.NEUTRAL, 3, 1);
								}
								damage = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
								{
									final Vec3 _center = new Vec3((ene.getX()), (ene.getY()), (ene.getZ()));
									List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(8 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
									for (Entity entityiterator : _entfound) {
										if (!(entityiterator instanceof LivingEntity)) {
											continue;
										}
										if (entityiterator instanceof Monster || (entityiterator instanceof Mob _mobEnt1 ? (Entity) _mobEnt1.getTarget() : null) == this) {
											d = ene.distanceTo(entityiterator);
											if (d <= 4) {
												EntityUtils.pullToward(entityiterator, this);
												GladiiaEntity.spawnGladiiaLinkParticles(world, this, entityiterator);
												this.hurtWithHunterAttack(entityiterator, (float) (damage * 1.8F));
											}
										}
									}
								}
							}
						});
					}
				}
			}
			GladiiaEntity.healFromGladiia(world, x, y, z, this);
		}
		this.refreshDimensions();
	}

	

	@Override
	public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageable) {
		GladiiaEntity retval = CAEntities.GLADIIA.get().create(serverWorld);
		retval.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(retval.blockPosition()), MobSpawnType.BREEDING, null, null);
		return retval;
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return List.of().contains(stack.getItem());
	}

	@Override
	public void aiStep() {
		super.aiStep();
		this.updateSwingTime();
	}

	

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.18);
		builder = builder.add(ForgeMod.SWIM_SPEED.get(), 8);
		builder = builder.add(CAAttributes.SANITY_MODIFIER.get(), 0.33);
		builder = builder.add(Attributes.MAX_HEALTH, 216);
		builder = builder.add(Attributes.ARMOR, 9);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 27);
		builder = builder.add(Attributes.FOLLOW_RANGE, 32);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 8);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

					&& !this.isAggressive()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.gladiia.move"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.gladiia.die"));
			}
			if (this.isAggressive() && event.isMoving()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.gladiia.run"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.gladiia.idle"));
		}
		return PlayState.STOP;
	}

	private PlayState attackingPredicate(AnimationState event) {
		double d1 = this.getX() - this.xOld;
		double d0 = this.getZ() - this.zOld;
		if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
			this.swinging = true;
			this.lastSwing = level().getGameTime();
		}
		if (this.swinging && this.lastSwing + 18L <= level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.gladiia.attack"));
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

	@Override
	protected void tickDeath() {
		++this.deathTime;
		if (this.deathTime == 25) {
			this.remove(RemovalReason.KILLED);
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
		data.add(new AnimationController<>(this, "movement", 1, this::movementPredicate));
		data.add(new AnimationController<>(this, "attacking", 1, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 1, this::procedurePredicate));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	private boolean isGladiiaDurative() {
		return this.isAlive() && this.getEntityData().get(DATA_DURATION) <= 0;
	}

	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}
}
