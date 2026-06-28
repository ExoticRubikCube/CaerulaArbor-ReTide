package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.utils.EntityPredicateUtils;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
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
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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

public class CompassionPrayerEntity extends SeaMonster implements RangedAttackMob {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(CompassionPrayerEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(CompassionPrayerEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(CompassionPrayerEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_PHASE = SynchedEntityData.defineId(CompassionPrayerEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_REVIVE_TICK = SynchedEntityData.defineId(CompassionPrayerEntity.class, EntityDataSerializers.INT);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public CompassionPrayerEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CaerulaArborModEntities.COMPASSION_PRAYER.get(), world);
	}

	public CompassionPrayerEntity(EntityType<CompassionPrayerEntity> type, Level world) {
		super(type, world);
		xpReward = 16;
		setNoAi(false);
		setMaxUpStep(0.8f);
		setPersistenceRequired();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "compassion_prayer");
		this.entityData.define(DATA_PHASE, 0);
		this.entityData.define(DATA_REVIVE_TICK, 0);
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
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this) {
			@Override
			public boolean canUse() {
				double x = CompassionPrayerEntity.this.getX();
				double y = CompassionPrayerEntity.this.getY();
				double z = CompassionPrayerEntity.this.getZ();
				Entity entity = CompassionPrayerEntity.this;
				Level world = CompassionPrayerEntity.this.level();
                if (!super.canUse()) return false;
                return EntityPredicateUtils.isNotFakeDying(entity);
			}

			@Override
			public boolean canContinueToUse() {
				double x = CompassionPrayerEntity.this.getX();
				double y = CompassionPrayerEntity.this.getY();
				double z = CompassionPrayerEntity.this.getZ();
				Entity entity = CompassionPrayerEntity.this;
				Level world = CompassionPrayerEntity.this.level();
                if (!super.canContinueToUse()) return false;
                return EntityPredicateUtils.isNotFakeDying(entity);
			}
		});
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, IronGolem.class, true, false));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, SnowGolem.class, true, false));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, Villager.class, true, false));
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Illusioner.class, true, false));
		this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, Pillager.class, true, false));
		this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, Vindicator.class, true, false));
		this.targetSelector.addGoal(8, new NearestAttackableTargetGoal(this, Witch.class, true, false));
		this.targetSelector.addGoal(9, new NearestAttackableTargetGoal(this, Piglin.class, true, false));
		this.targetSelector.addGoal(10, new NearestAttackableTargetGoal(this, PiglinBrute.class, true, false));
		this.targetSelector.addGoal(11, new NearestAttackableTargetGoal(this, ZombifiedPiglin.class, true, false));
		this.targetSelector.addGoal(12, new NearestAttackableTargetGoal(this, Player.class, true, false) {
			@Override
			public boolean canUse() {
				double x = CompassionPrayerEntity.this.getX();
				double y = CompassionPrayerEntity.this.getY();
				double z = CompassionPrayerEntity.this.getZ();
				Entity entity = CompassionPrayerEntity.this;
				Level world = CompassionPrayerEntity.this.level();
				return super.canUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
			}

			@Override
			public boolean canContinueToUse() {
				double x = CompassionPrayerEntity.this.getX();
				double y = CompassionPrayerEntity.this.getY();
				double z = CompassionPrayerEntity.this.getZ();
				Entity entity = CompassionPrayerEntity.this;
				Level world = CompassionPrayerEntity.this.level();
				return super.canContinueToUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
			}
		});
		this.goalSelector.addGoal(13, new RandomStrollGoal(this, 0.8) {
			@Override
			public boolean canUse() {
				double x = CompassionPrayerEntity.this.getX();
				double y = CompassionPrayerEntity.this.getY();
				double z = CompassionPrayerEntity.this.getZ();
				Entity entity = CompassionPrayerEntity.this;
				Level world = CompassionPrayerEntity.this.level();
                if (!super.canUse()) return false;
                return EntityPredicateUtils.isNotFakeDying(entity);
			}

			@Override
			public boolean canContinueToUse() {
				double x = CompassionPrayerEntity.this.getX();
				double y = CompassionPrayerEntity.this.getY();
				double z = CompassionPrayerEntity.this.getZ();
				Entity entity = CompassionPrayerEntity.this;
				Level world = CompassionPrayerEntity.this.level();
                if (!super.canContinueToUse()) return false;
                return EntityPredicateUtils.isNotFakeDying(entity);
            }
		});
		this.goalSelector.addGoal(14, new RandomLookAroundGoal(this) {
			@Override
			public boolean canUse() {
				double x = CompassionPrayerEntity.this.getX();
				double y = CompassionPrayerEntity.this.getY();
				double z = CompassionPrayerEntity.this.getZ();
				Entity entity = CompassionPrayerEntity.this;
				Level world = CompassionPrayerEntity.this.level();
                if (!super.canUse()) return false;
                return EntityPredicateUtils.isNotFakeDying(entity);
            }

			@Override
			public boolean canContinueToUse() {
				double x = CompassionPrayerEntity.this.getX();
				double y = CompassionPrayerEntity.this.getY();
				double z = CompassionPrayerEntity.this.getZ();
				Entity entity = CompassionPrayerEntity.this;
				Level world = CompassionPrayerEntity.this.level();
                if (!super.canContinueToUse()) return false;
                return EntityPredicateUtils.isNotFakeDying(entity);
            }
		});
		this.goalSelector.addGoal(15, new FloatGoal(this));
		this.goalSelector.addGoal(1, new CompassionPrayerEntity.RangedAttackGoal(this, 1.25, 80, 5f) {
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
				Entity entity = CompassionPrayerEntity.this;
                return EntityPredicateUtils.isNotFakeDying(entity);
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
			((CompassionPrayerEntity) rangedAttackMob).entityData.set(SHOOT, false);
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
					((CompassionPrayerEntity) rangedAttackMob).entityData.set(SHOOT, false);
					return;
				}
				((CompassionPrayerEntity) rangedAttackMob).entityData.set(SHOOT, true);
				float f = (float) Math.sqrt(d0) / this.attackRadius;
				float f1 = Mth.clamp(f, 0.1F, 1.0F);
				this.rangedAttackMob.performRangedAttack(this.target, f1);
				this.attackTime = Mth.floor(f * (float) (this.attackIntervalMax - this.attackIntervalMin) + (float) this.attackIntervalMin);
			} else if (this.attackTime < 0) {
				this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(d0) / (double) this.attackRadius, (double) this.attackIntervalMin, (double) this.attackIntervalMax));
			} else
				((CompassionPrayerEntity) rangedAttackMob).entityData.set(SHOOT, false);
		}
	}

	@Override
	public MobType getMobType() {
		return MobType.WATER;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "seaborn_generic_hit"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "seaborn_death"));
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.DROWN))
			return false;
		return super.hurt(source, amount);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putInt("DataPHASE", this.entityData.get(DATA_PHASE));
		compound.putInt("DataREVIVE_TICK", this.entityData.get(DATA_REVIVE_TICK));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("DataPHASE"))
			this.entityData.set(DATA_PHASE, compound.getInt("DataPHASE"));
		if (compound.contains("DataREVIVE_TICK"))
			this.entityData.set(DATA_REVIVE_TICK, compound.getInt("DataREVIVE_TICK"));
	}

	@Override
	public void baseTick() {
		super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity enemy = null;
        double dura = 0;
        double P = 0;
        double perc = 0;
        double d = 0;
        if (this.isAlive()) {
            dura = (Entity) this instanceof CompassionPrayerEntity _datEntI ? _datEntI.getEntityData().get(DATA_REVIVE_TICK) : 0;
            if (dura > 0) {
                if (dura <= 100) {
                    if ((Entity) this instanceof CompassionPrayerEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_PHASE, 1);
                }
                if ((Entity) this instanceof CompassionPrayerEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_REVIVE_TICK, (int) (dura - 1));
                setShiftKeyDown(true);
            } else {
                setShiftKeyDown(false);
            }
            P = (Entity) this instanceof CompassionPrayerEntity _datEntI ? _datEntI.getEntityData().get(DATA_PHASE) : 0;
            if (P == 0) {
                if ((Entity) this instanceof CompassionPrayerEntity animatable)
                    animatable.setTexture("compassion_prayer");
                if (tickCount % 10 == 0) {
                    {
                        final Vec3 _center = new Vec3(x, y, z);
                        List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(32 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                        for (Entity entityiterator : _entfound) {
                            if (!entityiterator.isAlive()) {
                                continue;
                            }
                            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "bossoffspring")))) {
                                continue;
                            }
                            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                                if (!(entityiterator instanceof LivingEntity _livEnt12 && _livEnt12.hasEffect(CaerulaArborModMobEffects.ADD_HEALTH_PERCLY.get()))) {
                                    perc = EntityUtils.getHealthPerc(entityiterator);
                                    if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
                                        _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ADD_HEALTH_PERCLY.get(), 32768, 1, false, false));
                                    if (entityiterator instanceof LivingEntity _entity)
                                        _entity.setHealth((float) ((entityiterator instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * perc));
                                }
                            }
                        }
                    }
                }
            } else {
                if ((Entity) this instanceof CompassionPrayerEntity animatable)
                    animatable.setTexture("compassion_prayer_a");
                Mob _mobEnt = (Mob) (Entity) this;
                enemy = (Entity) _mobEnt.getTarget();
                LivingEntity _livingEntity19 = this;
                d = _livingEntity19.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity19.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
                if (tickCount % 20 == 0) {
                    {
                        final Vec3 _center = new Vec3(x, y, z);
                        List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(10 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                        for (Entity entityiterator : _entfound) {
                            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                                if (!(entityiterator == enemy)) {
                                    continue;
                                }
                            }
                            if (!(entityiterator instanceof LivingEntity)) {
                                continue;
                            }
                            if (distanceTo(entityiterator) <= 5) {
                                entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "ocean_magic")))),
                                        (float) (d * 0.2));
                                EntityUtils.deductSanity(entityiterator, d * 30);
                            }
                        }
                    }
                }
				double angle;
				double d1;
				d1 = 5;
				for (int index0 = 0; index0 < 6; index0++) {
					angle = Mth.nextDouble(RandomSource.create(), 0, 6.283);
					if (world instanceof ServerLevel _level)
						_level.sendParticles(ParticleTypes.ELECTRIC_SPARK, (x + d1 * Math.sin(angle)), (y + 0.4), (z + d1 * Math.cos(angle)), 2, 0.1, 0.1, 0.1, 0.1);
				}
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
		PrayerSplashEntity.shoot(this, target, (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttributeValue(Attributes.ATTACK_DAMAGE) : 0) * (2.5 / 7.0));
	}

	public static void init() {
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.18);
		builder = builder.add(Attributes.MAX_HEALTH, 136);
		builder = builder.add(Attributes.ARMOR, 2);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 7);
		builder = builder.add(Attributes.FOLLOW_RANGE, 16);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.compassion_prayer.die"));
			}
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.1F && event.getLimbSwingAmount() < 0.1F))
) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.compassion_prayer.move"));
			}
			if (this.isShiftKeyDown()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.compassion_prayer.revive"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.compassion_prayer.idle"));
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
		if (this.swinging && this.lastSwing + 20L <= level().getGameTime()) {
			this.swinging = false;
		}
		if ((this.swinging || this.entityData.get(SHOOT)) && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.compassion_prayer.attack"));
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
		if (this.deathTime == 20) {
			this.remove(CompassionPrayerEntity.RemovalReason.KILLED);
			this.dropExperience();
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
		data.add(new AnimationController<>(this, "movement", 2, this::movementPredicate));
		data.add(new AnimationController<>(this, "attacking", 2, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 2, this::procedurePredicate));
	}
}
