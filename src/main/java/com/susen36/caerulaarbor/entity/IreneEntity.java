package com.susen36.caerulaarbor.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.base.SyncedAnimationEntity;
import com.susen36.caerulaarbor.init.*;
import com.susen36.caerulaarbor.util.EntityUtils;
import com.susen36.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Comparator;
import java.util.List;

public class IreneEntity extends Animal implements GeoEntity, SyncedAnimationEntity {

	public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(IreneEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(IreneEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_SKILLP_1 = SynchedEntityData.defineId(IreneEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_SKILLP_2 = SynchedEntityData.defineId(IreneEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(IreneEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_TAP_TICK = SynchedEntityData.defineId(IreneEntity.class, EntityDataSerializers.INT);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private long lastSwing;
	public String animationprocedure = "empty";

	public IreneEntity(Level world) {
		this(CAEntities.IRENE.get(), world);
	}

	public IreneEntity(EntityType<IreneEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);
		this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1f);
		setPersistenceRequired();
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_SHOOT, false);
		builder.define(DATA_ANIMATION, "undefined");
		builder.define(DATA_SKILLP_1, 0);
		builder.define(DATA_SKILLP_2, 12);
		builder.define(DATA_DURATION, 0);
		builder.define(DATA_TAP_TICK, 0);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this,  1.25, false) {

			@Override
			public boolean canUse() {
				return super.canUse() && isIreneDurative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && isIreneDurative();
			}

		});
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Monster.class, true, false));
		this.goalSelector.addGoal(4, new OpenDoorGoal(this, false));
		this.goalSelector.addGoal(5, new OpenDoorGoal(this, true));
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, (float) 6) {
			@Override
			public boolean canUse() {
				return super.canUse() && isIreneDurative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && isIreneDurative();
			}
		});
		this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1) {
			@Override
			public boolean canUse() {
				return super.canUse() && isIreneDurative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && isIreneDurative();
			}
		});
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this) {
			@Override
			public boolean canUse() {
				return super.canUse() && isIreneDurative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && isIreneDurative();
			}
		});
		this.goalSelector.addGoal(9, new FloatGoal(this));
	}

	@Override
	protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
		super.dropCustomDeathLoot(level, damageSource, recentlyHit);
		this.spawnAtLocation(new ItemStack(CAItems.TRAIL_POWDER.get()));
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return CASounds.IRENE_HIT.get();
	}

	@Override
	public SoundEvent getDeathSound() {
		return CASounds.IRENE_DIE.get();
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		double targetX = target.getX();
		double targetY = target.getY();
		double targetZ = target.getZ();
		float attackDamage = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
		if (!this.level().isClientSide()) {
			this.getEntityData().set(DATA_SKILLP_1, this.getEntityData().get(DATA_SKILLP_1) + 1);
			this.getEntityData().set(DATA_SKILLP_2, this.getEntityData().get(DATA_SKILLP_2) + 1);
			CaerulaArbor.queueServerWork(6, () -> {
				if (this.isAlive() && target.isAlive() && this.distanceTo(target) <= 3) {
					this.level().playSound(null, BlockPos.containing(targetX, targetY, targetZ),
							CASounds.IRENE_ATTACK.get(), SoundSource.NEUTRAL, 2.5F,
							(float) Mth.nextDouble(RandomSource.create(), 0.9, 1.1));
					if (target instanceof LivingEntity livingTarget) {
						livingTarget.addEffect(new MobEffectInstance(CAMobEffects.MUTE, 60, 0, false, false));
					}
					target.hurt(
							CADamageTypes.source(this.level(), CADamageTypes.GENERIC_WARRIOR_ATTACK, this), this.applyLaunchPunishBonus(target, attackDamage));
				}
			});
			CaerulaArbor.queueServerWork(11, () -> {
				if (this.isAlive() && target.isAlive() && this.distanceTo(target) <= 3) {
					this.level().playSound(null, BlockPos.containing(targetX, targetY, targetZ),
							CASounds.IRENE_ATTACK.get(), SoundSource.NEUTRAL, 2.5F,
							(float) Mth.nextDouble(RandomSource.create(), 0.9, 1.1));
					target.hurt(
							CADamageTypes.source(this.level(), CADamageTypes.GENERIC_WARRIOR_ATTACK, this), this.applyLaunchPunishBonus(target, attackDamage));
				}
			});
		}
		return true;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		LevelAccessor world = this.level();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		Entity sourceentity = source.getEntity();
		if (sourceentity instanceof LivingEntity livingSource && (livingSource.hasEffect(MobEffects.SLOW_FALLING) || livingSource.hasEffect(CAMobEffects.MUTE))) {
			amount *= 0.65F;
		}
		if (sourceentity != null) {
			Entity specter;
			if (!(sourceentity instanceof Player) && !(sourceentity instanceof SpecterEntity)) {
				specter = world.getEntitiesOfClass(SpecterEntity.class, AABB.ofSize(new Vec3(x, y, z), 32, 32, 32), e -> true).stream().min(new Object() {
                    Comparator<Entity> compareDistOf(double x, double y, double z) {
                        return Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(x, y, z));
                    }
                }.compareDistOf(x, y, z)).orElse(null);
				if (specter instanceof Mob entity) {
					entity.getNavigation().moveTo(x, y, z, 1);
					if (sourceentity instanceof LivingEntity ent)
						entity.setTarget(ent);
				}
			}
		}
		return super.hurt(source, amount);
	}

	private float applyLaunchPunishBonus(Entity target, float baseDamage) {
		float damage = baseDamage;
		if (target instanceof LivingEntity livingTarget && livingTarget.hasEffect(MobEffects.SLOW_FALLING)) {
			damage *= 1.2F;
			if (target.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "sea_born")))) {
				damage *= 1.2F;
				if (!livingTarget.level().isClientSide()) {
					livingTarget.addEffect(new MobEffectInstance(CAMobEffects.ROCK_BREAK, 80, 1));
				}
			} else if (!livingTarget.level().isClientSide()) {
				livingTarget.addEffect(new MobEffectInstance(CAMobEffects.ROCK_BREAK, 60, 0));
			}
		}
		return damage;
	}

	private boolean isValidEnemy(Entity enemy) {
		if (enemy != null && enemy != this && enemy.isAlive() && enemy instanceof LivingEntity) {
			Entity currentTarget = this.getTarget();
			boolean isPlayer = enemy instanceof Player;
			boolean isTamed = enemy instanceof TamableAnimal tamable && tamable.isTame();
			boolean isHumanSide = enemy.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "is_humanside")));
			return !(isPlayer || isTamed || isHumanSide) || enemy == currentTarget;
		}
		return false;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("Skillp1", this.entityData.get(DATA_SKILLP_1));
		compound.putInt("Skillp2", this.entityData.get(DATA_SKILLP_2));
		compound.putInt("Duration", this.entityData.get(DATA_DURATION));
		compound.putInt("TapTick", this.entityData.get(DATA_TAP_TICK));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Skillp1")) {
		    this.entityData.set(DATA_SKILLP_1, compound.getInt("Skillp1"));
		}
		if (compound.contains("Skillp2")) {
		    this.entityData.set(DATA_SKILLP_2, compound.getInt("Skillp2"));
		}
		if (compound.contains("Duration")) {
		    this.entityData.set(DATA_DURATION, compound.getInt("Duration"));
		}
		if (compound.contains("TapTick")) {
		    this.entityData.set(DATA_TAP_TICK, compound.getInt("TapTick"));
		}
	}

	@Override
	public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
		super.mobInteract(sourceentity, hand);
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		Entity entity = this;
		Level world = this.level();
		double tap;
                Entity target;
		if (!entity.isAlive()) {
			return InteractionResult.PASS;
		}
        if (sourceentity.isHolding(CAItems.PERSONNEL_TRANSPORTER.get())) {
			return InteractionResult.PASS;
		}
		tap = entity instanceof IreneEntity datEntI ? datEntI.getEntityData().get(DATA_TAP_TICK) : 0;
		if (tap <= 0) {
                        target = entity instanceof Mob mobEnt ? mobEnt.getTarget() : null;
                        if (!(target == null) && target.isAlive()) {
				return InteractionResult.PASS;
			}
			if (!world.isClientSide()) {
				if ((LevelAccessor) world instanceof Level level) {
					level.playSound(null, BlockPos.containing(x, y, z), CASounds.IRENE_INTERACT.get(), SoundSource.NEUTRAL, 3, 1);
				}
			}
			if (entity instanceof IreneEntity) {
				((IreneEntity) entity).setAnimation("animation.irene.interact");
			}
			if (entity instanceof IreneEntity datEntSetI)
				datEntSetI.getEntityData().set(DATA_TAP_TICK, 35);
			if (entity instanceof IreneEntity datEntSetI)
				datEntSetI.getEntityData().set(DATA_DURATION, 35);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public void baseTick() {
		super.baseTick();
		LevelAccessor world = this.level();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		Entity target;
		double sklp1;
		double dura;
		double skillp2;
		double tap;
        if (this.isAlive()) {
			if (tickCount % 40 == 15) {
				burnBrandAround(world, x, y, z);
			}
			sklp1 = (Entity) this instanceof IreneEntity datEntI ? datEntI.getEntityData().get(DATA_SKILLP_1) : 0;
			skillp2 = (Entity) this instanceof IreneEntity datEntI ? datEntI.getEntityData().get(DATA_SKILLP_2) : 0;
			dura = (Entity) this instanceof IreneEntity datEntI ? datEntI.getEntityData().get(DATA_DURATION) : 0;
			tap = (Entity) this instanceof IreneEntity datEntI ? datEntI.getEntityData().get(DATA_TAP_TICK) : 0;
            target = this.getTarget();
			if (dura > 0) {
				if ((Entity) this instanceof IreneEntity datEntSetI)
					datEntSetI.getEntityData().set(DATA_DURATION, (int) (dura - 1));
			}
			if (tap > 0) {
				if ((Entity) this instanceof IreneEntity datEntSetI)
					datEntSetI.getEntityData().set(DATA_TAP_TICK, (int) (tap - 1));
			}
			if (sklp1 >= 4 && dura <= 0) {
				if (!(target == null) && target.isAlive()) {
					if (distanceTo(target) <= 4) {
						if (this instanceof IreneEntity) {
							this.setAnimation("animation.irene.skill_1");
						}
						if ((Entity) this instanceof IreneEntity datEntSetI)
							datEntSetI.getEntityData().set(DATA_SKILLP_1, 0);
						if ((Entity) this instanceof IreneEntity datEntSetI)
							datEntSetI.getEntityData().set(DATA_DURATION, 27);
						CaerulaArbor.queueServerWork(10, () -> {
							if (this.isAlive()) {
								if (world instanceof Level level) {
									level.playSound(null, BlockPos.containing(x, y, z), CASounds.IRENE_FLY.get(), SoundSource.NEUTRAL, 3, 1);
								}
                                Entity enemy1 = this.getTarget();
								if (enemy1 == null)
									return;
								enemy1.push(0, 0.4, 0);
								if (world instanceof ServerLevel level)
									level.sendParticles(ParticleTypes.FIREWORK, (enemy1.getX()), (enemy1.getY() + 1), (enemy1.getZ()), 48, 0.15, 1, 0.15, 0.15);
								if (enemy1 instanceof LivingEntity entity && !entity.level().isClientSide())
									entity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, (int) (double) 30, 0));
								enemy1.hurt(CADamageTypes.source(world, CADamageTypes.HUNTER_ATTACK, this), this.applyLaunchPunishBonus(enemy1, (float) (((Entity) this instanceof LivingEntity livingEntity6 && livingEntity6.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity6.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * (double) 3)));
								CaerulaArbor.queueServerWork(6, () -> {
									if (world instanceof Level level) {
										level.playSound(null, BlockPos.containing(x, y, z), CASounds.IRENE_GUN.get(), SoundSource.NEUTRAL, 3, 1);
									}
									if (world instanceof ServerLevel level)
										level.sendParticles(ParticleTypes.END_ROD, (enemy1.getX()), (enemy1.getY() + 0.75), (enemy1.getZ()), 32, 0.75, 0.75, 0.75, 0.15);
									enemy1.hurt(CADamageTypes.source(world, CADamageTypes.HUNTER_ATTACK, this), this.applyLaunchPunishBonus(enemy1, (float) (((Entity) this instanceof LivingEntity livingEntity14 && livingEntity14.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity14.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 2)));
								});
							}
						});
					}
				}
			} else if (skillp2 >= 16 && dura <= 0 && target != null && target.isAlive() && distanceTo(target) <= 6) {
				if (this instanceof IreneEntity) {
					this.setAnimation("animation.irene.skill_2");
				}
				if (!this.level().isClientSide())
					this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 60, 9, false, false));
				if (world instanceof Level level) {
					level.playSound(null, BlockPos.containing(x, y, z), CASounds.IRENE_SKILL.get(), SoundSource.NEUTRAL, 3, 1);
				}
				if ((Entity) this instanceof IreneEntity datEntSetI)
					datEntSetI.getEntityData().set(DATA_SKILLP_2, 0);
				if ((Entity) this instanceof IreneEntity datEntSetI)
					datEntSetI.getEntityData().set(DATA_DURATION, 70);
				CaerulaArbor.queueServerWork(9, () -> {
					if (this.isAlive()) {
						double damage;
                        damage = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
						if (world instanceof Level level) {
							level.playSound(null, BlockPos.containing(x, y, z), CASounds.IRENE_SKILL_FLY.get(), SoundSource.NEUTRAL, 3, 1);
						}
						final Vec3 center = new Vec3(x, y, z);
						List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(14 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
						for (Entity entityiterator : entfound) {
							if (this.isValidEnemy(entityiterator) && distanceTo(entityiterator) <= 7) {
								entityiterator.push(0, 0.5, 0);
								if (world instanceof ServerLevel level)
									level.sendParticles(ParticleTypes.FIREWORK, (entityiterator.getX()), (entityiterator.getY() + 0.75), (entityiterator.getZ()), 48, 0.15, 1, 0.15, 0.15);
								if (entityiterator instanceof LivingEntity entity && !entity.level().isClientSide())
									entity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 80, 0));
								entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.HUNTER_ATTACK, this), this.applyLaunchPunishBonus(entityiterator, (float) (damage * 3)));
							}
						}
					}
				});
				CaerulaArbor.queueServerWork(16, () -> {
					if (this.isAlive()) {
						if (world instanceof Level level) {
							level.playSound(null, BlockPos.containing(x, y, z), CASounds.IRENE_SKILL_LOOP.get(), SoundSource.NEUTRAL, 2, 1);
						}
					}
				});
				for (int index0 = 0; index0 < 10; index0++) {
					CaerulaArbor.queueServerWork(Math.toIntExact(Math.round(20 + index0 * 3.778)), () -> {
						if (this.isAlive()) {
							Entity selected;
							double damage;
							double tx;
							double ty;
							double tz;
                            damage = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
							Entity result = null;
							Vec3 pos = position();
							AABB area = new AABB(pos.add(-7, -7, -7), pos.add(7, 7, 7));
							List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, area, e1 -> {
								return this.isValidEnemy(e1)
										&& e1.position().vectorTo(pos).horizontalDistanceSqr() <= 49;
							});
							if (!entities.isEmpty()) {
								int index = Mth.nextInt(world.getRandom(), 0, entities.size() - 1);
								result = entities.get(index);
							}
							selected = result;
							if (selected == null) {
								return;
							}
							tx = selected.getX();
							ty = selected.getY();
							tz = selected.getZ();
							final Vec3 center = new Vec3(tx, ty, tz);
							List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(6 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
							for (Entity entityiterator : entfound) {
								if (this.isValidEnemy(entityiterator) && selected.distanceTo(entityiterator) <= 3) {
									entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.HUNTER_ATTACK, this), this.applyLaunchPunishBonus(entityiterator, (float) (damage * 2.5)));
									if (world instanceof Level level) {
										level.playSound(null, BlockPos.containing(x, y, z), CASounds.IRENE_SKILL_GUN.get(), SoundSource.NEUTRAL, 3, 1);
									}
									if (world instanceof ServerLevel level)
										level.sendParticles(ParticleTypes.END_ROD, (entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ()), 72, 2.5, 2.5, 2.5, 0.1);
								}
							}
						}
					});
				}
				CaerulaArbor.queueServerWork(59, () -> {
					if (this.isAlive()) {
						if (world instanceof Level level) {
							level.playSound(null, BlockPos.containing(x, y, z), CASounds.IRENE_RELOAD.get(), SoundSource.NEUTRAL, 3, 1);
						}
					}
				});
			}
			EntityUtils.vanguardBuff(world, x, y, z, this);
		}
		this.refreshDimensions();
	}

	// TODO：高优先级。后续应为 IreneEntity 与 SaintCarmenEntity 抽取共同基类承载该逻辑，而不是继续通过静态方法复用。
	public static void burnBrandAround(LevelAccessor world, double x, double y, double z) {
		BlockState toBeBurn;
		double px;
		double py;
		double pz;
		for (int index0 = 0; index0 < 3; index0++) {
			for (int index1 = 0; index1 < 3; index1++) {
				for (int index2 = 0; index2 < 3; index2++) {
					px = x + index0 - 1;
					py = y + index1 - 1;
					pz = z + index2 - 1;
					toBeBurn = world.getBlockState(BlockPos.containing(px, py, pz));
					if (toBeBurn.getBlock() == CABlocks.SEA_TRAIL_INIT.get() || toBeBurn.getBlock() == CABlocks.SEA_TRAIL_GROWING.get() || toBeBurn.getBlock() == CABlocks.SEA_TRAIL_GROWN.get()
							|| toBeBurn.getBlock() == CABlocks.SEA_TRAIL_STOP.get() || toBeBurn.getBlock() == CABlocks.SEA_TRAIL_SOLID.get() || toBeBurn.getBlock() == CABlocks.TRAIL_PULSE.get()) {
						WorldUtils.burndownTrail(world, toBeBurn, px, py, pz);
						if (world instanceof ServerLevel level) {
							level.sendParticles(CAParticles.PURPLE_FLAME.get(), x + 0.5, y + 1, z + 0.5, 16, 0.75, 0.75, 0.75, 0.15);
						}
					}
				}
			}
		}
	}

	@Override
	public void aiStep() {
		super.aiStep();
		this.updateSwingTime();
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.19);
		builder = builder.add(Attributes.MAX_HEALTH, 85);
		builder = builder.add(Attributes.ARMOR, 6);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 9);
		builder = builder.add(Attributes.FOLLOW_RANGE, 32);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.25);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) && !this.isAggressive()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.irene.move"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.irene.die"));
			}
			if (this.isAggressive() && event.isMoving()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.irene.run"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.irene.idle"));
		}
		return PlayState.STOP;
	}

	private PlayState attackingPredicate(AnimationState event) {
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
			this.swinging = true;
			this.lastSwing = level().getGameTime();
		}
		if (this.swinging && this.lastSwing + 19L <= level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.irene.attack"));
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
			this.remove(RemovalReason.KILLED);
			this.dropExperience(this.getKillCredit());
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
		data.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
		data.add(new AnimationController<>(this, "attacking", 0, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	private boolean isIreneDurative() {
		return this.isAlive() && this.getEntityData().get(DATA_DURATION) <= 0;
	}

	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return false;
	}

	@javax.annotation.Nullable
	@Override
	public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
		return null;
	}
}