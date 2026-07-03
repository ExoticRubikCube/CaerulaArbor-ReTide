package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.SyncedAnimationEntity;
import com.apocalypse.caerulaarbor.init.*;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import com.apocalypse.caerulaarbor.util.WorldUtils;
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

import java.util.Comparator;
import java.util.List;

public class IreneEntity extends Animal implements GeoEntity, SyncedAnimationEntity {

	private boolean isIreneDurative() {
		return this.isAlive() && this.getEntityData().get(DATA_duration) <= 0;
	}

	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(IreneEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(IreneEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_skillp1 = SynchedEntityData.defineId(IreneEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_skillp2 = SynchedEntityData.defineId(IreneEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_duration = SynchedEntityData.defineId(IreneEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_tapTick = SynchedEntityData.defineId(IreneEntity.class, EntityDataSerializers.INT);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public IreneEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CAEntities.IRENE.get(), world);
	}

	public IreneEntity(EntityType<IreneEntity> type, Level world) {
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
		this.entityData.define(DATA_skillp1, 0);
		this.entityData.define(DATA_skillp2, 12);
		this.entityData.define(DATA_duration, 0);
		this.entityData.define(DATA_tapTick, 0);
	}



	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.25, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 7.5625;
			}

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
	public MobType getMobType() {
		return MobType.UNDEFINED;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHitIn) {
		super.dropCustomDeathLoot(source, looting, recentlyHitIn);
		this.spawnAtLocation(new ItemStack(CAItems.TRAIL_POWDER.get()));
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "irene_hit"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "irene_die"));
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		double targetX = target.getX();
		double targetY = target.getY();
		double targetZ = target.getZ();
		float attackDamage = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
		if (!this.level().isClientSide()) {
			this.getEntityData().set(DATA_skillp1, this.getEntityData().get(DATA_skillp1) + 1);
			this.getEntityData().set(DATA_skillp2, this.getEntityData().get(DATA_skillp2) + 1);
			CaerulaArborMod.queueServerWork(6, () -> {
				if (this.isAlive() && target.isAlive() && this.distanceTo(target) <= 3) {
					this.level().playSound(null, BlockPos.containing(targetX, targetY, targetZ),
							ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "irene_attack")), SoundSource.NEUTRAL, 2.5F,
							(float) Mth.nextDouble(RandomSource.create(), 0.9, 1.1));
					if (target instanceof LivingEntity livingTarget) {
						livingTarget.addEffect(new MobEffectInstance(CAMobEffects.MUTE.get(), 60, 0, false, false));
					}
					target.hurt(
							new DamageSource(
									this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
											.getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "generic_warrior_attack"))),
									this),
							this.applyLaunchPunishBonus(target, attackDamage));
				}
			});
			CaerulaArborMod.queueServerWork(11, () -> {
				if (this.isAlive() && target.isAlive() && this.distanceTo(target) <= 3) {
					this.level().playSound(null, BlockPos.containing(targetX, targetY, targetZ),
							ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "irene_attack")), SoundSource.NEUTRAL, 2.5F,
							(float) Mth.nextDouble(RandomSource.create(), 0.9, 1.1));
					target.hurt(
							new DamageSource(
									this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
											.getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "generic_warrior_attack"))),
									this),
							this.applyLaunchPunishBonus(target, attackDamage));
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
		if (sourceentity instanceof LivingEntity livingSource && (livingSource.hasEffect(MobEffects.SLOW_FALLING) || livingSource.hasEffect(CAMobEffects.MUTE.get()))) {
			amount *= 0.65F;
		}
		if (sourceentity != null) {
			Entity specter;
			if (!(sourceentity instanceof Player) && !(sourceentity instanceof SpecterEntity)) {
				specter = world.getEntitiesOfClass(SpecterEntity.class, AABB.ofSize(new Vec3(x, y, z), 32, 32, 32), e -> true).stream().min(new Object() {
                    Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                        return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
                    }
                }.compareDistOf(x, y, z)).orElse(null);
				if (specter instanceof Mob _entity) {
					_entity.getNavigation().moveTo(x, y, z, 1);
					if (sourceentity instanceof LivingEntity _ent)
						_entity.setTarget(_ent);
				}
			}
		}
		return super.hurt(source, amount);
	}

	private float applyLaunchPunishBonus(Entity target, float baseDamage) {
		float damage = baseDamage;
		if (target instanceof LivingEntity livingTarget && livingTarget.hasEffect(MobEffects.SLOW_FALLING)) {
			damage *= 1.2F;
			if (target.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
				damage *= 1.2F;
				if (!livingTarget.level().isClientSide()) {
					livingTarget.addEffect(new MobEffectInstance(CAMobEffects.ROCK_BREAK.get(), 80, 1));
				}
			} else if (!livingTarget.level().isClientSide()) {
				livingTarget.addEffect(new MobEffectInstance(CAMobEffects.ROCK_BREAK.get(), 60, 0));
			}
		}
		return damage;
	}

	private boolean isValidEnemy(Entity enemy) {
		if (enemy != null && enemy != this && enemy.isAlive() && enemy instanceof LivingEntity) {
			Entity currentTarget = this.getTarget();
			boolean isPlayer = enemy instanceof Player;
			boolean isTamed = enemy instanceof TamableAnimal tamable && tamable.isTame();
			boolean isHumanSide = enemy.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "is_humanside")));
			return !(isPlayer || isTamed || isHumanSide) || enemy == currentTarget;
		}
		return false;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("Dataskillp1", this.entityData.get(DATA_skillp1));
		compound.putInt("Dataskillp2", this.entityData.get(DATA_skillp2));
		compound.putInt("Dataduration", this.entityData.get(DATA_duration));
		compound.putInt("DatatapTick", this.entityData.get(DATA_tapTick));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Dataskillp1"))
			this.entityData.set(DATA_skillp1, compound.getInt("Dataskillp1"));
		if (compound.contains("Dataskillp2"))
			this.entityData.set(DATA_skillp2, compound.getInt("Dataskillp2"));
		if (compound.contains("Dataduration"))
			this.entityData.set(DATA_duration, compound.getInt("Dataduration"));
		if (compound.contains("DatatapTick"))
			this.entityData.set(DATA_tapTick, compound.getInt("DatatapTick"));
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
		Entity enemy;
		if (!entity.isAlive()) {
			return InteractionResult.PASS;
		}
		if ((Entity) sourceentity instanceof LivingEntity _entity && _entity.isHolding(CAItems.PERSONNEL_TRANSPORTER.get())) {
			return InteractionResult.PASS;
		}
		tap = entity instanceof IreneEntity _datEntI ? _datEntI.getEntityData().get(DATA_tapTick) : 0;
		if (tap <= 0) {
			enemy = entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null;
			if (!(enemy == null) && enemy.isAlive()) {
				return InteractionResult.PASS;
			}
			if (!((LevelAccessor) world).isClientSide()) {
				if ((LevelAccessor) world instanceof Level _level) {
					_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "irene_interact")), SoundSource.NEUTRAL, 3, 1);
				}
			}
			if (entity instanceof IreneEntity) {
				((IreneEntity) entity).setAnimation("animation.irene.interact");
			}
			if (entity instanceof IreneEntity _datEntSetI)
				_datEntSetI.getEntityData().set(DATA_tapTick, 35);
			if (entity instanceof IreneEntity _datEntSetI)
				_datEntSetI.getEntityData().set(DATA_duration, 35);
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
		Entity enemy;
		double sklp1;
		double dura;
		double skillp2;
		double tap;
        if (this.isAlive()) {
			if (tickCount % 40 == 15) {
				burnBrandAround(world, x, y, z);
			}
			sklp1 = (Entity) this instanceof IreneEntity _datEntI ? _datEntI.getEntityData().get(DATA_skillp1) : 0;
			skillp2 = (Entity) this instanceof IreneEntity _datEntI ? _datEntI.getEntityData().get(DATA_skillp2) : 0;
			dura = (Entity) this instanceof IreneEntity _datEntI ? _datEntI.getEntityData().get(DATA_duration) : 0;
			tap = (Entity) this instanceof IreneEntity _datEntI ? _datEntI.getEntityData().get(DATA_tapTick) : 0;
			enemy = (Entity) this instanceof Mob _mobEnt ? _mobEnt.getTarget() : null;
			if (dura > 0) {
				if ((Entity) this instanceof IreneEntity _datEntSetI)
					_datEntSetI.getEntityData().set(DATA_duration, (int) (dura - 1));
			}
			if (tap > 0) {
				if ((Entity) this instanceof IreneEntity _datEntSetI)
					_datEntSetI.getEntityData().set(DATA_tapTick, (int) (tap - 1));
			}
			if (sklp1 >= 4 && dura <= 0) {
				if (!(enemy == null) && enemy.isAlive()) {
					if (distanceTo(enemy) <= 4) {
						if (this instanceof IreneEntity) {
							this.setAnimation("animation.irene.skill_1");
						}
						if ((Entity) this instanceof IreneEntity _datEntSetI)
							_datEntSetI.getEntityData().set(DATA_skillp1, 0);
						if ((Entity) this instanceof IreneEntity _datEntSetI)
							_datEntSetI.getEntityData().set(DATA_duration, 27);
						CaerulaArborMod.queueServerWork(10, () -> {
							if (this.isAlive()) {
								if (world instanceof Level _level) {
									_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "irene_fly")), SoundSource.NEUTRAL, 3, 1);
								}
								Entity enemy1 = (Entity) this instanceof Mob _mobEnt ? _mobEnt.getTarget() : null;
								if (enemy1 == null)
									return;
								enemy1.push(0, 0.4, 0);
								if (world instanceof ServerLevel _level)
									_level.sendParticles(ParticleTypes.FIREWORK, (enemy1.getX()), (enemy1.getY() + 1), (enemy1.getZ()), 48, 0.15, 1, 0.15, 0.15);
								if (enemy1 instanceof LivingEntity _entity && !_entity.level().isClientSide())
									_entity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, (int) (double) 30, 0));
								enemy1.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hunter_attack"))), this),
										this.applyLaunchPunishBonus(enemy1, (float) (((Entity) this instanceof LivingEntity _livingEntity6 && _livingEntity6.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity6.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * (double) 3)));
								CaerulaArborMod.queueServerWork(6, () -> {
									if (world instanceof Level _level) {
										_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "irene_gun")), SoundSource.NEUTRAL, 3, 1);
									}
									if (world instanceof ServerLevel _level)
										_level.sendParticles(ParticleTypes.END_ROD, (enemy1.getX()), (enemy1.getY() + 0.75), (enemy1.getZ()), 32, 0.75, 0.75, 0.75, 0.15);
									enemy1.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hunter_attack"))), this),
											this.applyLaunchPunishBonus(enemy1, (float) (((Entity) this instanceof LivingEntity _livingEntity14 && _livingEntity14.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity14.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 2)));
								});
							}
						});
					}
				}
			} else if (skillp2 >= 16 && dura <= 0 && enemy != null && enemy.isAlive() && distanceTo(enemy) <= 6) {
				if (this instanceof IreneEntity) {
					this.setAnimation("animation.irene.skill_2");
				}
				if (!this.level().isClientSide())
					this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 60, 9, false, false));
				if (world instanceof Level _level) {
					_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "irene_skill")), SoundSource.NEUTRAL, 3, 1);
				}
				if ((Entity) this instanceof IreneEntity _datEntSetI)
					_datEntSetI.getEntityData().set(DATA_skillp2, 0);
				if ((Entity) this instanceof IreneEntity _datEntSetI)
					_datEntSetI.getEntityData().set(DATA_duration, 70);
				CaerulaArborMod.queueServerWork(9, () -> {
					if (this.isAlive()) {
						double damage;
                        damage = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
						if (world instanceof Level _level) {
							_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "irene_skill_fly")), SoundSource.NEUTRAL, 3, 1);
						}
						final Vec3 _center = new Vec3(x, y, z);
						List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(14 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
						for (Entity entityiterator : _entfound) {
							if (this.isValidEnemy(entityiterator) && distanceTo(entityiterator) <= 7) {
								entityiterator.push(0, 0.5, 0);
								if (world instanceof ServerLevel _level)
									_level.sendParticles(ParticleTypes.FIREWORK, (entityiterator.getX()), (entityiterator.getY() + 0.75), (entityiterator.getZ()), 48, 0.15, 1, 0.15, 0.15);
								if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
									_entity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 80, 0));
								entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hunter_attack"))), this),
										this.applyLaunchPunishBonus(entityiterator, (float) (damage * 3)));
							}
						}
					}
				});
				CaerulaArborMod.queueServerWork(16, () -> {
					if (this.isAlive()) {
						if (world instanceof Level _level) {
							_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "irene_skill_loop")), SoundSource.NEUTRAL, 2, 1);
						}
					}
				});
				for (int index0 = 0; index0 < 10; index0++) {
					CaerulaArborMod.queueServerWork(Math.toIntExact(Math.round(20 + index0 * 3.778)), () -> {
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
							final Vec3 _center = new Vec3(tx, ty, tz);
							List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(6 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
							for (Entity entityiterator : _entfound) {
								if (this.isValidEnemy(entityiterator) && selected.distanceTo(entityiterator) <= 3) {
									entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hunter_attack"))), this),
											this.applyLaunchPunishBonus(entityiterator, (float) (damage * 2.5)));
									if (world instanceof Level _level) {
										_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "irene_skill_gun")), SoundSource.NEUTRAL, 3, 1);
									}
									if (world instanceof ServerLevel _level)
										_level.sendParticles(ParticleTypes.END_ROD, (entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ()), 72, 2.5, 2.5, 2.5, 0.1);
								}
							}
						}
					});
				}
				CaerulaArborMod.queueServerWork(59, () -> {
					if (this.isAlive()) {
						if (world instanceof Level _level) {
							_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "irene_reload")), SoundSource.NEUTRAL, 3, 1);
						}
					}
				});
			}
			EntityUtils.vanguardBuff(world, x, y, z, this);
		}
		this.refreshDimensions();
	}

	// TODO: HIGH: IreneEntity 与 SaintCarmenEntity 之后应抽取共同基类承载该逻辑，而不是继续通过静态方法复用。
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
	public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageable) {
		IreneEntity retval = CAEntities.IRENE.get().create(serverWorld);
		retval.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(retval.blockPosition()), MobSpawnType.BREEDING, null, null);
		return retval;
	}

	@Override
	public boolean isFood(ItemStack stack) {
        return false;
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


	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}
}
