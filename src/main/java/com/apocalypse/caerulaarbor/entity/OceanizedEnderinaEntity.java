package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.*;
import com.apocalypse.caerulaarbor.util.EntityPredicateUtils;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import com.apocalypse.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class OceanizedEnderinaEntity extends SeaMonster implements RangedAttackMob {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(OceanizedEnderinaEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(OceanizedEnderinaEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(OceanizedEnderinaEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_REVIVE_TICK = SynchedEntityData.defineId(OceanizedEnderinaEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_PHASE = SynchedEntityData.defineId(OceanizedEnderinaEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_SKILL_P = SynchedEntityData.defineId(OceanizedEnderinaEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(OceanizedEnderinaEntity.class, EntityDataSerializers.INT);
	public static SoundEvent PRE = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "caster_pre"));
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public static void spawnLinkParticles(LevelAccessor world, double fromX, double fromY, double fromZ, double toX, double toY, double toZ) {
		double vx = toX - fromX;
		double vy = toY - fromY;
		double vz = toZ - fromZ;
		double size = Math.max(Math.min(Math.round(Math.sqrt(vx * vx + vy * vy + vz * vz)), 32), 1);
		for (int index0 = 0; index0 < (int) size; index0++) {
			if (world instanceof ServerLevel serverLevel) {
				serverLevel.sendParticles(CAParticleTypes.EDERMAN_PTC.get(), fromX + (vx / size) * index0, fromY + (vy / size) * index0 + 1, fromZ + (vz / size) * index0, 1, 0, 0, 0, 0.01);
			}
		}
	}

	private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.PINK, ServerBossEvent.BossBarOverlay.NOTCHED_10);

	public OceanizedEnderinaEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CAEntities.OCEANIZED_ENDERINA.get(), world);
	}

	public OceanizedEnderinaEntity(EntityType<OceanizedEnderinaEntity> type, Level world) {
		super(type, world);
		xpReward = 128;
		setNoAi(false);
		setMaxUpStep(0.6f);
		setPersistenceRequired();
		this.moveControl = new FlyingMoveControl(this, 10, true);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "oceanized_enderina");
		this.entityData.define(DATA_REVIVE_TICK, 0);
		this.entityData.define(DATA_PHASE, 0);
		this.entityData.define(DATA_SKILL_P, 0);
		this.entityData.define(DATA_DURATION, 50);
	}

	public void setTexture(String texture) {
		this.entityData.set(TEXTURE, texture);
	}

	public String getTexture() {
		return this.entityData.get(TEXTURE);
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected PathNavigation createNavigation(Level world) {
		return new FlyingPathNavigation(this, world);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1, 20) {
			@Override
			protected Vec3 getPosition() {
				RandomSource random = OceanizedEnderinaEntity.this.getRandom();
				double dir_x = OceanizedEnderinaEntity.this.getX() + ((random.nextFloat() * 2 - 1) * 16);
				double dir_y = OceanizedEnderinaEntity.this.getY() + ((random.nextFloat() * 2 - 1) * 16);
				double dir_z = OceanizedEnderinaEntity.this.getZ() + ((random.nextFloat() * 2 - 1) * 16);
				return new Vec3(dir_x, dir_y, dir_z);
			}

			@Override
			public boolean canUse() {
				Entity entity = OceanizedEnderinaEntity.this;
				return super.canUse() && EntityPredicateUtils.isEnderinaDurative(entity);
			}

			@Override
			public boolean canContinueToUse() {
				Entity entity = OceanizedEnderinaEntity.this;
				return super.canContinueToUse() && EntityPredicateUtils.isEnderinaDurative(entity);
			}

		});
		this.goalSelector.addGoal(3, new RandomLookAroundGoal(this) {
			@Override
			public boolean canUse() {
				Entity entity = OceanizedEnderinaEntity.this;
				return super.canUse() && EntityPredicateUtils.isEnderinaDurative(entity);
			}

			@Override
			public boolean canContinueToUse() {
				Entity entity = OceanizedEnderinaEntity.this;
				return super.canContinueToUse() && EntityPredicateUtils.isEnderinaDurative(entity);
			}
		});
		this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25, 60, 5f) {
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
				this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
			}
		}

		public boolean canUse() {
			LivingEntity livingentity = this.mob.getTarget();
			if (livingentity != null && livingentity.isAlive()) {
				this.target = livingentity;
				Entity entity = OceanizedEnderinaEntity.this;
				return EntityPredicateUtils.isEnderinaDurative(entity);
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
			((OceanizedEnderinaEntity) rangedAttackMob).entityData.set(SHOOT, false);
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
					((OceanizedEnderinaEntity) rangedAttackMob).entityData.set(SHOOT, false);
					return;
				}
				((OceanizedEnderinaEntity) rangedAttackMob).entityData.set(SHOOT, true);
				float f = (float) Math.sqrt(d0) / this.attackRadius;
				float f1 = Mth.clamp(f, 0.1F, 1.0F);
				this.rangedAttackMob.performRangedAttack(this.target, f1);
				this.attackTime = Mth.floor(f * (float) (this.attackIntervalMax - this.attackIntervalMin) + (float) this.attackIntervalMin);
			} else if (this.attackTime < 0) {
				this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(d0) / (double) this.attackRadius, this.attackIntervalMin, this.attackIntervalMax));
			} else
				((OceanizedEnderinaEntity) rangedAttackMob).entityData.set(SHOOT, false);
		}
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEFINED;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	public static SoundEvent HURT_SOUND = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "caster_hurt"));
	public static SoundEvent DIE_SOUND = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "caster_die"));

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return HURT_SOUND;
	}

	@Override
	public SoundEvent getDeathSound() {
		return DIE_SOUND;
	}

	@Override
	public boolean causeFallDamage(float l, float d, DamageSource source) {
		return false;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.DRAGON_BREATH)) return false;
		Entity sourceEntity = source.getEntity();
		if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
				|| source.is(DamageTypeTags.BYPASSES_EFFECTS))
			return super.hurt(source, amount);
		if (sourceEntity == null && amount < this.getMaxHealth())
			return super.hurt(source, 0);
		float rate = 1;
		if (isReviving()) rate = 0.25f;
		return super.hurt(source, 100 * rate * (float) Math.log(0.01f * amount + 1));
	}

	@Override
	public void die(DamageSource source) {
		if (!isReviving()) {
			this.setHealth(this.getMaxHealth() * 0.5f);
			this.reviveing();
			return;
		}
		super.die(source);
	}

	@Override
	public void setHealth(float pHealth) {
		float hlth = this.getHealth();
		float mhlth = this.getMaxHealth();
		if (pHealth <= 0 && !isReviving()) {
			super.setHealth(mhlth * 0.5f);
			this.reviveing();
			return;
		}
		float reduction = hlth - pHealth;
		super.setHealth(reduction >= mhlth * 0.33f ? hlth - mhlth * 0.33f : hlth - reduction);
	}

	@Override
	public void remove(RemovalReason pReason) {
		if (pReason == RemovalReason.DISCARDED && this.level().getDifficulty() != Difficulty.PEACEFUL) return;
		else if (pReason == RemovalReason.KILLED && !this.isDeadOrDying()) return;
		super.remove(pReason);
	}


	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
		if (this.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get()))
			this.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).setBaseValue(85);
		if (this.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE.get()))
			this.getAttribute(CAAttributes.GENERAL_DEFENSE.get()).setBaseValue(4);
		if (this.getAttributes().hasAttribute(CAAttributes.SANITY_MODIFIER.get()))
			this.getAttribute(CAAttributes.SANITY_MODIFIER.get()).setBaseValue(0.0125);
		if (this.getAttributes().hasAttribute(CAAttributes.SANITY_RESISTANCE.get()))
			this.getAttribute(CAAttributes.SANITY_RESISTANCE.get()).setBaseValue(75);
		if (this instanceof OceanizedEnderinaEntity) {
			this.setAnimation("animation.oceanized_enderina.start");
		}
		if (!this.level().isClientSide())
			this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 50, 9, false, false));
		return retval;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putInt("DataREVIVE_TICK", this.entityData.get(DATA_REVIVE_TICK));
		compound.putInt("DataPHASE", this.entityData.get(DATA_PHASE));
		compound.putInt("DataSKILL_P", this.entityData.get(DATA_SKILL_P));
		compound.putInt("DataDURATION", this.entityData.get(DATA_DURATION));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("DataREVIVE_TICK"))
			this.entityData.set(DATA_REVIVE_TICK, compound.getInt("DataREVIVE_TICK"));
		if (compound.contains("DataPHASE"))
			this.entityData.set(DATA_PHASE, compound.getInt("DataPHASE"));
		if (compound.contains("DataSKILL_P"))
			this.entityData.set(DATA_SKILL_P, compound.getInt("DataSKILL_P"));
		if (compound.contains("DataDURATION"))
			this.entityData.set(DATA_DURATION, compound.getInt("DataDURATION"));
	}

	@Override
	public void baseTick() {
		super.baseTick();
		LevelAccessor world = this.level();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		Entity enemy;
		double dura;
		double P;
		double sklp1;
		double rev;
		double deadTime;
		deadTime = this.deathTime;
		if (deadTime >= 30) {
			if ((Entity) this instanceof OceanizedEnderinaEntity animatable)
				animatable.setTexture("oceanized_enderina_3");
		} else if (deadTime >= 20) {
			if ((Entity) this instanceof OceanizedEnderinaEntity animatable)
				animatable.setTexture("oceanized_enderina_2");
		} else if (deadTime >= 10) {
			if ((Entity) this instanceof OceanizedEnderinaEntity animatable)
				animatable.setTexture("oceanized_enderina_1");
		}
		if (this.isAlive()) {
			sklp1 = (Entity) this instanceof OceanizedEnderinaEntity _datEntI ? _datEntI.getEntityData().get(DATA_SKILL_P) : 0;
			dura = (Entity) this instanceof OceanizedEnderinaEntity _datEntI ? _datEntI.getEntityData().get(DATA_DURATION) : 0;
			rev = (Entity) this instanceof OceanizedEnderinaEntity _datEntI ? _datEntI.getEntityData().get(DATA_REVIVE_TICK) : 0;
			if (tickCount % 100 == 0) {
				if (WorldUtils.isDistFromGround(world, x, y, z)) {
					push(0, (-0.35), 0);
				}
			}
			P = (Entity) this instanceof OceanizedEnderinaEntity _datEntI ? _datEntI.getEntityData().get(DATA_PHASE) : 0;
			if (rev > 0) {
				if ((Entity) this instanceof OceanizedEnderinaEntity _datEntSetI)
					_datEntSetI.getEntityData().set(DATA_REVIVE_TICK, (int) (dura - 1));
				setShiftKeyDown(true);
				setDeltaMovement(new Vec3(0, 0, 0));
				if (tickCount % 10 == 0) {
					this.swallowNearbyCrystals();
				}
				if (((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) >= ((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1)) {
					if ((Entity) this instanceof OceanizedEnderinaEntity _datEntSetI)
						_datEntSetI.getEntityData().set(DATA_REVIVE_TICK, 0);
				}
				if (Math.random() < 0.033) {
					MoistDragonBreathEntity.dragonBreathRain(world, x, y, z, this);
				}
				if (P > 0.5) {
					if (Math.random() < 0.033) {
						MoistDragonBreathEntity.dragonBreathRain(world, x, y, z, this);
					}
					if (((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) >= ((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1)) {
						if ((Entity) this instanceof OceanizedEnderinaEntity _datEntSetI)
							_datEntSetI.getEntityData().set(DATA_REVIVE_TICK, 0);
						if ((Entity) this instanceof OceanizedEnderinaEntity animatable)
							animatable.setTexture("oceanized_enderina_noise");
					}
					if (rev < 100) {
						if ((Entity) this instanceof OceanizedEnderinaEntity animatable)
							animatable.setTexture("oceanized_enderina_noise");
					}
				} else {
					if (((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) >= ((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1)) {
						if ((Entity) this instanceof OceanizedEnderinaEntity _datEntSetI)
							_datEntSetI.getEntityData().set(DATA_REVIVE_TICK, 0);
					}
				}
			} else {
				setShiftKeyDown(false);
			}
			enemy = (Entity) this instanceof Mob _mobEnt ? _mobEnt.getTarget() : null;
			if (dura > 0) {
				if ((Entity) this instanceof OceanizedEnderinaEntity _datEntSetI)
					_datEntSetI.getEntityData().set(DATA_DURATION, (int) (dura - 1));
			}
			if (sklp1 > 0) {
				if ((Entity) this instanceof OceanizedEnderinaEntity _datEntSetI)
					_datEntSetI.getEntityData().set(DATA_SKILL_P, (int) (sklp1 - 1));
			} else if (dura <= 0) {
				if (!(enemy == null) && enemy.isAlive()) {
					if (this instanceof OceanizedEnderinaEntity) {
						this.setAnimation("animation.oceanized_enderina.chant");
					}
					if ((Entity) this instanceof OceanizedEnderinaEntity _datEntSetI)
						_datEntSetI.getEntityData().set(DATA_SKILL_P, 370);
					if ((Entity) this instanceof OceanizedEnderinaEntity _datEntSetI)
						_datEntSetI.getEntityData().set(DATA_DURATION, 70);
					if (!this.level().isClientSide())
						this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 50, 0, false, false));
					if (world instanceof Level _level) {
						_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "caster_skill")), SoundSource.HOSTILE, (float) 2.5, 1);
					}
					for (int index0 = 0; index0 < 8; index0++) {
						CaerulaArborMod.queueServerWork(12 + index0 * 5, () -> {
							if (this.isAlive()) {
								MoistDragonBreathEntity.spawn(world, getX(), getY() + 3, getZ(), this, (Entity) this instanceof Mob _mobEnt ? _mobEnt.getTarget() : null, Mth.nextInt(RandomSource.create(), 0, 1));
							}
						});
						if (P > 0.5) {
							CaerulaArborMod.queueServerWork(14 + index0 * 5, () -> {
								if (this.isAlive()) {
									MoistDragonBreathEntity.spawn(world, getX(), getY() + 3, getZ(), this, (Entity) this instanceof Mob _mobEnt ? _mobEnt.getTarget() : null, Mth.nextInt(RandomSource.create(), 0, 1));
								}
							});
							CaerulaArborMod.queueServerWork(15 + index0 * 5, () -> {
								if (this.isAlive()) {
									MoistDragonBreathEntity.spawn(world, getX(), getY() + 3, getZ(), this, (Entity) this instanceof Mob _mobEnt ? _mobEnt.getTarget() : null, Mth.nextInt(RandomSource.create(), 0, 1));
								}
							});
						}
					}
				}
			}
			if (tickCount % 20 == 10) {
				if (!(enemy == null) && enemy.isAlive()) {
					WorldUtils.witheriaDestroyBlocks(world, x, y, z);
				}
			}
			if (tickCount % 400 == 100) {
				for (int index1 = 0; index1 < 2; index1++) {
					distributeCrystal(world, x, y, z);
				}
			}
			if (tickCount % 20 == 0) {
				final Vec3 _center = new Vec3(x, y, z);
				List<MoistEnderCrystalEntity> _entfound = world.getEntitiesOfClass(MoistEnderCrystalEntity.class,
						new AABB(_center, _center).inflate(16), e -> !e.IS_STATIC);
				for (Entity entityiterator : _entfound) {
					if (entityiterator == null || this == null)
						continue;
					Entity illusioner;
					Entity enemy1 = null;
					illusioner = this;
					if (illusioner == null) {
						continue;
					}
					if ((illusioner instanceof OceanizedEnderinaEntity _datEntI ? _datEntI.getEntityData().get(DATA_REVIVE_TICK) : 0) <= 0) {
						crytsalToEnderina(entityiterator, illusioner);
						EntityUtils.heal(this, this.getMaxHealth() * 0.01);
					} else {
						if (entityiterator instanceof Mob _entity)
							_entity.getNavigation().moveTo((illusioner.getX()), (illusioner.getY()), (illusioner.getZ()), 0.5);
					}
					if (illusioner == null)
						continue;
					OceanizedEnderinaEntity.spawnLinkParticles(world, entityiterator.getX(), entityiterator.getY() + 0.5, entityiterator.getZ(), illusioner.getX(), illusioner.getY(), illusioner.getZ());
				}
			}
			if (rev > 0 && tickCount % 70 == 50) {
				distributeCrystal(world, x, y, z);
			}
			if (EntityUtils.getSpeed(this) > 0.64) {
				setDeltaMovement(new Vec3(0, 0, 0));
			}
		}
		this.refreshDimensions();
	}

	@Override
	public EntityDimensions getDimensions(Pose p_33597_) {
		return super.getDimensions(p_33597_).scale((float) 1);
	}

	@Override
	public void performRangedAttack(LivingEntity target, float flval) {
		Vec3 p = this.getLookAngle().scale(0.45).reverse();
		this.push(p.x, 0, p.z);
		Level level = this.level();
		if (!level.isClientSide()) {
			level.playSound(this, this.blockPosition(), PRE, SoundSource.HOSTILE, 2, 1);
		} else {
			level.playLocalSound(this.getX(), this.getY(), this.getZ(), PRE, SoundSource.HOSTILE, 2, 1, false);
		}
		if (getPhase() == 0) {
			normalAttack(target);
		} else {
			superAttack(target);
		}
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

	@Override
	protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
	}

	@Override
	public void setNoGravity(boolean ignored) {
		super.setNoGravity(true);
	}

	public void aiStep() {
		super.aiStep();
		this.setNoGravity(true);
	}

	

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.45);
		builder = builder.add(Attributes.MAX_HEALTH, 385);
		builder = builder.add(Attributes.ARMOR, 15);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 14);
		builder = builder.add(Attributes.FOLLOW_RANGE, 36);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
		builder = builder.add(Attributes.FLYING_SPEED, 0.55);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_enderina.die"));
			}
			if (this.isShiftKeyDown()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_enderina.revive"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_enderina.idle"));
		}
		return PlayState.STOP;
	}

	private PlayState attackingPredicate(AnimationState event) {
		double d1 = this.getX() - this.xOld;
		double d0 = this.getZ() - this.zOld;
		float velocity = (float) Math.sqrt(d1 * d1 + d0 * d0);
		if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
			this.swinging = true;
			this.lastSwing = level().getGameTime();
		}
		if (this.swinging && this.lastSwing + 25L <= level().getGameTime()) {
			this.swinging = false;
		}
		if ((this.swinging || this.entityData.get(SHOOT)) && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_enderina.attack"));
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
		if (this.deathTime == 40) {
			this.remove(RemovalReason.KILLED);
			this.dropExperience();
			LevelAccessor world = this.level();
			double x = this.getX();
			double y = this.getY();
			double z = this.getZ();
			if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
				if (world instanceof ServerLevel _level) {
					ItemEntity entityToSpawn = new ItemEntity(_level, x, (y + 1), z, new ItemStack(CAItems.MOIST_DRAGON_HEART.get()));
					entityToSpawn.setPickUpDelay(5);
					_level.addFreshEntity(entityToSpawn);
				}
				for (int index0 = 0; index0 < 64; index0++) {
					if (world instanceof ServerLevel _level)
						_level.addFreshEntity(new ExperienceOrb(_level, (x + Mth.nextDouble(RandomSource.create(), -1, 1)), y, (z + Mth.nextDouble(RandomSource.create(), -1, 1)), Mth.nextInt(RandomSource.create(), 48, 96)));
				}
			}
		}
	}

	public String getSyncedAnimation() {
		return this.entityData.get(ANIMATION);
	}

	public void setAnimation(String animation) {
		this.entityData.set(ANIMATION, animation);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		data.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
		data.add(new AnimationController<>(this, "attacking", 0, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
	}

	private int getPhase() {
		return this.entityData.get(DATA_PHASE);
	}

	private boolean isReviving() {
		return this.entityData.get(DATA_REVIVE_TICK) > 0;
	}

	private void normalAttack(LivingEntity target) {
		CaerulaArborMod.queueServerWork(11, () -> {
					MoistDragonBreathEntity.spawn(this.level(), this.getX(), this.getY() + 2.5, this.getZ(), this, target, 0);
					MoistDragonBreathEntity.spawn(this.level(), this.getX(), this.getY() + 2.5, this.getZ(), this, target, 0);
					MoistDragonBreathEntity.spawn(this.level(), this.getX(), this.getY() + 2.5, this.getZ(), this, target, 1);
				}
		);
	}

	private void superAttack(LivingEntity target) {
		CaerulaArborMod.queueServerWork(11, () -> {
					MoistDragonBreathEntity.spawn(this.level(), this.getX(), this.getY() + 2.5, this.getZ(), this, target, 0);
					MoistDragonBreathEntity.spawn(this.level(), this.getX(), this.getY() + 2.5, this.getZ(), this, target, 0);
					MoistDragonBreathEntity.spawn(this.level(), this.getX(), this.getY() + 2.5, this.getZ(), this, target, 0);
					MoistDragonBreathEntity.spawn(this.level(), this.getX(), this.getY() + 2.5, this.getZ(), this, target, 1);
					MoistDragonBreathEntity.spawn(this.level(), this.getX(), this.getY() + 2.5, this.getZ(), this, target, 1);
				}
		);
	}

	private void distributeCrystal(LevelAccessor world, double x, double y, double z) {
		double r;
		double d;
		double tx;
		double tz;
		double result;
		final Vec3 _center = new Vec3(x, y, z);
		List<MoistEnderCrystalEntity> _entfound = world.getEntitiesOfClass(MoistEnderCrystalEntity.class,
				new AABB(_center, _center).inflate(32 / 2d), MoistEnderCrystalEntity::isAlive);
		result = _entfound.size();
		if (result < 12) {
			r = Mth.nextDouble(RandomSource.create(), 0, 6.283);
			d = Mth.nextDouble(RandomSource.create(), 7, 12);
			tx = x + d * Math.cos(r);
			tz = z + d * Math.sin(r);
			if (world instanceof ServerLevel _level) {
				Entity entityToSpawn = CAEntities.MOIST_ENDER_CRYSTAL.get().spawn(_level, BlockPos.containing(tx, y, tz), MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn != null) {
					entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
				}
			}
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.EXPLOSION, tx, (y + 1), tz, 1, 0, 0, 0, 0.1);
		}
	}

	private void swallowNearbyCrystals() {
		Vec3 center = this.position();
		List<Entity> nearbyEntities = this.level().getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(5 / 2d), entity -> true).stream()
				.sorted(Comparator.comparingDouble(entity -> entity.distanceToSqr(center)))
				.toList();
		for (Entity nearbyEntity : nearbyEntities) {
			if (nearbyEntity instanceof MoistEnderCrystalEntity && this.distanceTo(nearbyEntity) < 2.5) {
				if (!nearbyEntity.level().isClientSide()) {
					nearbyEntity.discard();
				}
				EntityUtils.heal(this, this.getMaxHealth() * 0.05F);
				if (this.level() instanceof ServerLevel level) {
					level.sendParticles(ParticleTypes.DRAGON_BREATH, nearbyEntity.getX(), nearbyEntity.getY() + 0.5, nearbyEntity.getZ(), 16, 0.5, 0.5, 0.5, 0.1);
				}
			}
		}
	}

	public void reviveing() {
		this.getEntityData().set(OceanizedEnderinaEntity.DATA_DURATION, 600);
		this.getEntityData().set(OceanizedEnderinaEntity.DATA_REVIVE_TICK, 600);
	}

	private void crytsalToEnderina(Entity me, Entity owner) {
		Vec3 ownerPos = owner.position();
		Vec3 goal;
		Vec3 v1 = ownerPos.vectorTo(me.position());
		Vec3 v = new Vec3(v1.x, 0, v1.z);
		if (v.lengthSqr() > 144)
			goal = ownerPos.add(v.normalize().scale(10));
		else {
			RandomSource random = me.level().random;
			int yaw = Mth.nextInt(random, 30, 90);
			double r = Mth.nextDouble(random, 7, 12);
			goal = ownerPos.add(v.normalize().scale(r).yRot((float) Math.toRadians(yaw)));
		}
		if (goal.distanceToSqr(me.position()) > 0.25) {
			if (me instanceof Mob mob) {
				mob.getNavigation().moveTo(goal.x, goal.y, goal.z, 1);
			}
		}
	}


	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}
}
