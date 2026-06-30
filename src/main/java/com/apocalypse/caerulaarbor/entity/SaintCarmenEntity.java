package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.init.CAParticleTypes;
import com.apocalypse.caerulaarbor.util.EntityPredicateUtils;
import com.apocalypse.caerulaarbor.util.WorldUtils;
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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class SaintCarmenEntity extends Animal implements GeoEntity, SyncedAnimationEntity {

	private boolean isCarmenDurative() {
		return EntityPredicateUtils.isCarmenDurative(this);
	}
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(SaintCarmenEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(SaintCarmenEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(SaintCarmenEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_skillP1 = SynchedEntityData.defineId(SaintCarmenEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_skillP2 = SynchedEntityData.defineId(SaintCarmenEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_shootP = SynchedEntityData.defineId(SaintCarmenEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_bullet = SynchedEntityData.defineId(SaintCarmenEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_duration = SynchedEntityData.defineId(SaintCarmenEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_reloadP = SynchedEntityData.defineId(SaintCarmenEntity.class, EntityDataSerializers.INT);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public SaintCarmenEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CAEntities.SAINT_CARMEN.get(), world);
	}

	public SaintCarmenEntity(EntityType<SaintCarmenEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);
		setMaxUpStep(1f);
		setPersistenceRequired();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "saint_carmen");
		this.entityData.define(DATA_skillP1, 200);
		this.entityData.define(DATA_skillP2, 100);
		this.entityData.define(DATA_shootP, 80);
		this.entityData.define(DATA_bullet, 3);
		this.entityData.define(DATA_duration, 0);
		this.entityData.define(DATA_reloadP, 500);
	}

	public void setTexture(String texture) {
		this.entityData.set(TEXTURE, texture);
	}

	public String getTexture() {
		return this.entityData.get(TEXTURE);
	}

	@Override
	protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
		return 1.7F;
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.3, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 6.25;
			}

			@Override
			public boolean canUse() {
				return super.canUse() && isCarmenDurative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && isCarmenDurative();
			}

		});
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Monster.class, true, false));
		this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1) {
			@Override
			public boolean canUse() {
				return super.canUse() && isCarmenDurative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && isCarmenDurative();
			}
		});
		this.goalSelector.addGoal(5, new RandomLookAroundGoal(this) {
			@Override
			public boolean canUse() {
				return super.canUse() && isCarmenDurative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && isCarmenDurative();
			}
		});
		this.goalSelector.addGoal(6, new FloatGoal(this));
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
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.hurt"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.death"));
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		double targetX = target.getX();
		double targetY = target.getY();
		double targetZ = target.getZ();
		if (!this.level().isClientSide()) {
			CaerulaArborMod.queueServerWork(9, () -> {
				if (this.isAlive() && target.isAlive() && this.distanceTo(target) <= 3) {
					this.level().playSound(null, BlockPos.containing(targetX, targetY, targetZ),
							ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "carmen_melee")), SoundSource.NEUTRAL, 2.33F,
							(float) Mth.nextDouble(RandomSource.create(), 0.9, 1.1));
					this.applyMuteOnHit(target);
					target.hurt(
							new DamageSource(
									this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
											.getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "generic_warrior_attack"))),
									this),
							(float) (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0));
				}
			});
		}
		return true;
	}

	private void applyMuteOnHit(Entity target) {
		if (target instanceof LivingEntity livingTarget && !livingTarget.level().isClientSide()) {
			livingTarget.addEffect(new MobEffectInstance(CAMobEffects.MUTE.get(), 100, 0));
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putInt("DataskillP1", this.entityData.get(DATA_skillP1));
		compound.putInt("DataskillP2", this.entityData.get(DATA_skillP2));
		compound.putInt("DatashootP", this.entityData.get(DATA_shootP));
		compound.putInt("Databullet", this.entityData.get(DATA_bullet));
		compound.putInt("Dataduration", this.entityData.get(DATA_duration));
		compound.putInt("DatareloadP", this.entityData.get(DATA_reloadP));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("DataskillP1"))
			this.entityData.set(DATA_skillP1, compound.getInt("DataskillP1"));
		if (compound.contains("DataskillP2"))
			this.entityData.set(DATA_skillP2, compound.getInt("DataskillP2"));
		if (compound.contains("DatashootP"))
			this.entityData.set(DATA_shootP, compound.getInt("DatashootP"));
		if (compound.contains("Databullet"))
			this.entityData.set(DATA_bullet, compound.getInt("Databullet"));
		if (compound.contains("Dataduration"))
			this.entityData.set(DATA_duration, compound.getInt("Dataduration"));
		if (compound.contains("DatareloadP"))
			this.entityData.set(DATA_reloadP, compound.getInt("DatareloadP"));
	}

	@Override
	public void baseTick() {
		super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity enemy;
        double sklp1;
        double dura;
        double sklp2;
        double shootCooldown;
        double bullet;
        double reloadP;
        boolean canShoot;
        if (this.isAlive()) {
            if (tickCount % 40 == 20) {
                WorldUtils.ireneBurnBrandAround(world, x, y, z);
            }
            sklp1 = (Entity) this instanceof SaintCarmenEntity _datEntI ? _datEntI.getEntityData().get(DATA_skillP1) : 0;
            sklp2 = (Entity) this instanceof SaintCarmenEntity _datEntI ? _datEntI.getEntityData().get(DATA_skillP2) : 0;
            shootCooldown = (Entity) this instanceof SaintCarmenEntity _datEntI ? _datEntI.getEntityData().get(DATA_shootP) : 0;
            bullet = (Entity) this instanceof SaintCarmenEntity _datEntI ? _datEntI.getEntityData().get(DATA_bullet) : 0;
            reloadP = (Entity) this instanceof SaintCarmenEntity _datEntI ? _datEntI.getEntityData().get(DATA_reloadP) : 0;
            dura = (Entity) this instanceof SaintCarmenEntity _datEntI ? _datEntI.getEntityData().get(DATA_duration) : 0;
            enemy = (Entity) this instanceof Mob _mobEnt ? _mobEnt.getTarget() : null;
            if (dura > 0) {
                if ((Entity) this instanceof SaintCarmenEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_duration, (int) (dura - 1));
            }
            if (shootCooldown > 0) {
                if ((Entity) this instanceof SaintCarmenEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_shootP, (int) (shootCooldown - 1));
            }
            if (sklp1 > 0) {
                if ((Entity) this instanceof SaintCarmenEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_skillP1, (int) (sklp1 - 1));
            } else if (dura <= 0) {
                if (!(enemy == null) && enemy.isAlive()) {
                    if ((enemy != null ? distanceTo(enemy) : -1) <= 24) {
                        if (this instanceof SaintCarmenEntity) {
                            this.setAnimation("animation.saint_carmen.melee_skill");
                        }
                        if ((Entity) this instanceof SaintCarmenEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_skillP1, 240);
                        if ((Entity) this instanceof SaintCarmenEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_duration, 33);
                        dura = 33;
                        CaerulaArborMod.queueServerWork(10, () -> {
                            if (this.isAlive()) {
                                carmenTeleport(world, x, y, z);
                            }
                        });
                        CaerulaArborMod.queueServerWork(21, () -> {
                            if (this.isAlive()) {
                                shoot(world, x, y, z, 2);
                            }
                        });
                    }
                }
            }
            canShoot = bullet > 0 && shootCooldown <= 0;
            if (sklp2 > 0) {
                if ((Entity) this instanceof SaintCarmenEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_skillP2, (int) (sklp2 - 1));
            } else if (dura <= 0 && canShoot) {
                if (!(enemy == null) && enemy.isAlive()) {
                    if ((enemy != null ? distanceTo(enemy) : -1) <= 24) {
                        if (this instanceof SaintCarmenEntity) {
                            this.setAnimation("animation.saint_carmen.gun_skill");
                        }
                        if ((Entity) this instanceof SaintCarmenEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_skillP2, 480);
                        if ((Entity) this instanceof SaintCarmenEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_duration, 50);
                        if ((Entity) this instanceof SaintCarmenEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_shootP, 50);
                        dura = 50;
                        push((getLookAngle().x * (-1.5)), 0, (getLookAngle().z * (-1.5)));
                        ((Entity) this).lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((enemy.getX()), (getY() + 1.8), (enemy.getZ())));
                        CaerulaArborMod.queueServerWork(12, () -> {
                            if (this.isAlive()) {
                                shootAbundant(world, x, y, z, 1);
                            }
                        });
                        CaerulaArborMod.queueServerWork(23, () -> {
                            if (this.isAlive()) {
                                shootAbundant(world, x, y, z, 2);
                            }
                        });
                        CaerulaArborMod.queueServerWork(35, () -> {
                            if (this.isAlive()) {
                                shootAbundant(world, x, y, z, 3);
                            }
                        });
                    }
                }
            }
            if (canShoot) {
                if (dura <= 0) {
                    if (!(enemy == null) && enemy.isAlive()) {
                        if ((enemy != null ? distanceTo(enemy) : -1) <= 6) {
                            if (this instanceof SaintCarmenEntity) {
                                this.setAnimation("animation.saint_carmen.gun");
                            }
                            if ((Entity) this instanceof SaintCarmenEntity _datEntSetI)
                                _datEntSetI.getEntityData().set(DATA_duration, 20);
                            if ((Entity) this instanceof SaintCarmenEntity _datEntSetI)
                                _datEntSetI.getEntityData().set(DATA_shootP, 80);
                            if ((Entity) this instanceof SaintCarmenEntity _datEntSetI)
                                _datEntSetI.getEntityData().set(DATA_bullet, (int) (bullet - 1));
                            ((Entity) this).lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((enemy.getX()), (getY() + 1.8), (enemy.getZ())));
                            CaerulaArborMod.queueServerWork(9, () -> {
                                if (this.isAlive()) {
                                    shoot(world, x, y, z, 2);
                                }
                            });
                        }
                    }
                }
            } else {
                if (reloadP > 0) {
                    if ((Entity) this instanceof SaintCarmenEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_reloadP, (int) (reloadP - 1));
                } else if (dura <= 0) {
                    if (this instanceof SaintCarmenEntity) {
                        this.setAnimation("animation.saint_carmen.reload");
                    }
                    if ((Entity) this instanceof SaintCarmenEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_bullet, 3);
                    if ((Entity) this instanceof SaintCarmenEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_duration, 20);
                    if ((Entity) this instanceof SaintCarmenEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_reloadP, 600);
                    if ((Entity) this instanceof SaintCarmenEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_shootP, 20);
                    if (world instanceof Level _level) {
                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "irene_reload")), SoundSource.NEUTRAL, 2, 1);
                    }
                }
            }
            showBullets(bullet);
        }
        this.refreshDimensions();
	}

	@Override
	public EntityDimensions getDimensions(Pose p_33597_) {
		return super.getDimensions(p_33597_).scale((float) 1);
	}

	@Override
	public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageable) {
		SaintCarmenEntity retval = CAEntities.SAINT_CARMEN.get().create(serverWorld);
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

	public static void init() {
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.16);
		builder = builder.add(Attributes.MAX_HEALTH, 280);
		builder = builder.add(Attributes.ARMOR, 8);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 22);
		builder = builder.add(Attributes.FOLLOW_RANGE, 32);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 1);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

					&& !this.isAggressive()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.saint_carmen.move"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.saint_carmen.die"));
			}
			if (this.isAggressive() && event.isMoving()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.saint_carmen.sprint"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.saint_carmen.idle"));
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
		if (this.swinging && this.lastSwing + 16L <= level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.saint_carmen.melee"));
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
			this.remove(SaintCarmenEntity.RemovalReason.KILLED);
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
		data.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
		data.add(new AnimationController<>(this, "attacking", 0, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	private void shootAbundant(LevelAccessor world, double x, double y, double z, double t) {
		double dama;
		double xx;
		double yy;
		double zz;
		Entity target;
		if (world instanceof Level _level) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "carmen_big_shoot")), SoundSource.NEUTRAL, 3, 1);
		}
		dama = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
		target = this.getTarget();
		if (!(target == null) && target.isAlive()) {
			xx = target.getX();
			yy = target.getY();
			zz = target.getZ();
			this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(xx, (yy + 1.8), zz));
		}
		for (int index0 = 0; index0 < (int) t; index0++) {
			for (int index1 = 0; index1 < 3; index1++) {
				{
					Entity _shootFrom = this;
					Level projectileLevel = _shootFrom.level();
					if (!projectileLevel.isClientSide()) {
						Projectile _entityToSpawn = new Object() {
							public Projectile getArrow(Level level, Entity shooter, float damage, int knockback, byte piercing) {
								AbstractArrow entityToSpawn = new CarmenBulletEntity(CAEntities.CARMEN_BULLET.get(), level);
								entityToSpawn.setOwner(shooter);
								entityToSpawn.setBaseDamage(damage);
								entityToSpawn.setKnockback(knockback);
								entityToSpawn.setSilent(true);
								entityToSpawn.setPierceLevel(piercing);
								return entityToSpawn;
							}
						}.getArrow(projectileLevel, this, (float) dama, 0, (byte) 1);
						_entityToSpawn.setPos(_shootFrom.getX(), _shootFrom.getEyeY() - 0.1, _shootFrom.getZ());
						_entityToSpawn.shoot(_shootFrom.getLookAngle().x, _shootFrom.getLookAngle().y, _shootFrom.getLookAngle().z, (float) 1.75, 15);
						projectileLevel.addFreshEntity(_entityToSpawn);
					}
				}
			}
		}
	}

	private void shoot(LevelAccessor world, double x, double y, double z, double rate) {
		Entity enemy;
		double xx;
		double yy;
		double zz;
		double dama;
		enemy = this.getTarget();
		if (enemy == null) {
			return;
		}
		if (!enemy.isAlive()) {
			return;
		}
		if ((enemy != null ? this.distanceTo(enemy) : -1) > 8) {
			return;
		}
		xx = enemy.getX();
		yy = enemy.getY();
		zz = enemy.getZ();
		this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(xx, (yy + 1.8), zz));
		dama = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
		this.applyMuteOnHit(enemy);
		enemy.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "generic_warrior_attack"))), this),
				(float) (dama * rate));
		if (world instanceof Level _level) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "carmen_shoot")), SoundSource.NEUTRAL, 3, 1);
		}
		if (world instanceof ServerLevel _level)
			_level.sendParticles(ParticleTypes.END_ROD, xx, (yy + 0.75), zz, 32, 0.75, 0.75, 0.75, 0.1);
	}

	private void showBullets(double bulletCount) {
		if (this.tickCount % 2 == 0) {
			for (int index = 0; index < (int) bulletCount; index++) {
				this.level().addParticle(CAParticleTypes.BULLETS.get(), (this.getX() + 1), (this.getY() + 1.5 + index * 0.25), (this.getZ() + 1), 0, 0, 0);
			}
		}
	}

	private void carmenTeleport(LevelAccessor world, double x, double y, double z) {
		Entity enemy;
		double xx;
		double yy;
		double zz;
		double dama;
		enemy = this.getTarget();
		if (enemy == null) {
			return;
		}
		if (!enemy.isAlive()) {
			return;
		}
		if ((enemy != null ? this.distanceTo(enemy) : -1) > 24) {
			return;
		}
		xx = enemy.getX();
		yy = enemy.getY();
		zz = enemy.getZ();
		dama = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
		this.teleportTo((xx + Mth.nextDouble(RandomSource.create(), -0.5, 0.5)), yy, (zz + Mth.nextDouble(RandomSource.create(), -0.5, 0.5)));
		if (world instanceof Level _level) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "carmen_melee")), SoundSource.NEUTRAL, 3, 1);
		}
		this.applyMuteOnHit(enemy);
		enemy.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "generic_warrior_attack"))), this),
				(float) (dama * 2));
	}


	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}
}
