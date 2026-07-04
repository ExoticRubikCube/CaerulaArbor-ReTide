package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.*;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import com.apocalypse.caerulaarbor.util.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SkadiCorruptedEntity extends SeaMonster {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(SkadiCorruptedEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(SkadiCorruptedEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_convertP = SynchedEntityData.defineId(SkadiCorruptedEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Boolean> DATA_mayCorrupt = SynchedEntityData.defineId(SkadiCorruptedEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Integer> DATA_duration = SynchedEntityData.defineId(SkadiCorruptedEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_convertTick = SynchedEntityData.defineId(SkadiCorruptedEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_deal = SynchedEntityData.defineId(SkadiCorruptedEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_phase = SynchedEntityData.defineId(SkadiCorruptedEntity.class, EntityDataSerializers.INT);
	private boolean swinging;
	private long lastSwing;
	public String animationprocedure = "empty";
	private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.RED, ServerBossEvent.BossBarOverlay.NOTCHED_6);

	public SkadiCorruptedEntity(Level world) {
		this(CAEntities.SKADI_CORRUPTED.get(), world);
	}

	public SkadiCorruptedEntity(EntityType<SkadiCorruptedEntity> type, Level world) {
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
		this.entityData.define(DATA_convertP, 900);
		this.entityData.define(DATA_mayCorrupt, true);
		this.entityData.define(DATA_duration, 0);
		this.entityData.define(DATA_convertTick, 1000);
		this.entityData.define(DATA_deal, 0);
		this.entityData.define(DATA_phase, 0);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 4;
			}

			@Override
			public boolean canUse() {
				return super.canUse() && isCorruptedDurative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && isCorruptedDurative();
			}

		});
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true, false));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, SnowGolem.class, true, false));
		this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1) {
			@Override
			public boolean canUse() {
				return super.canUse() && isCorruptedDurative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && isCorruptedDurative();
			}
		});
		this.goalSelector.addGoal(6, new OpenDoorGoal(this, true));
		this.goalSelector.addGoal(7, new OpenDoorGoal(this, false));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this) {
			@Override
			public boolean canUse() {
				return super.canUse() && isCorruptedDurative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && isCorruptedDurative();
			}
		});
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
		return SoundEvents.GUARDIAN_HURT;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.GUARDIAN_DEATH;
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		if (!this.level().isClientSide()) {
			CaerulaArborMod.queueServerWork(24, () -> {
				if (this.isAlive() && isCorruptedDurative() && target.isAlive() && this.distanceTo(target) <= 2.25) {
					double damage = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
					final Vec3 center = new Vec3(this.getX(), this.getY(), this.getZ());
					List<Entity> foundEntities = this.level().getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(6 / 2d), entity -> true).stream()
							.sorted(Comparator.comparingDouble(candidate -> candidate.distanceToSqr(center))).toList();
					for (Entity entityIterator : foundEntities) {
						if (!(entityIterator instanceof LivingEntity)) {
							continue;
						}
						if (entityIterator == this) {
							continue;
						}
						if (entityIterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
							continue;
						}
						if (this.distanceTo(entityIterator) <= 3) {
							entityIterator.hurt(
									new DamageSource(
											this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
													.getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "general_seaborn_attack"))),
											this),
									(float) damage);
							Vec3 pushVec = this.position().vectorTo(entityIterator.position());
							if (pushVec.lengthSqr() < 0.0001) {
								pushVec = new Vec3(0, 0, 1);
							} else {
								pushVec = pushVec.normalize();
							}
							pushVec = pushVec.scale(1.25);
							entityIterator.push(pushVec.x, pushVec.y, pushVec.z);
						}
					}
					this.getEntityData().set(DATA_duration, 40);
				}
			});
		}
		return true;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.IN_FIRE))
			return false;
		if (source.is(DamageTypes.DROWN))
			return false;
		float newAmount = amount;
		if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			int p = getPhase();
			if (p < 0.5) newAmount = amount * 0.15f;
			else if (p < 1.5) newAmount = amount * 0.5f;
		}
		boolean damaged = super.hurt(source, newAmount);
		if (damaged && this.isCorruptedSource(source)) {
			if (this.getEntityData().get(DATA_phase) > 1.5) {
				return damaged;
			}
			double accumulatedDamage = this.getEntityData().get(DATA_deal) + newAmount;
			this.getEntityData().set(DATA_deal, (int) accumulatedDamage);
			if (accumulatedDamage >= this.getMaxHealth() * 0.7) {
				this.getEntityData().set(DATA_mayCorrupt, false);
				if (this.getEntityData().get(DATA_phase) > 0.5) {
					return damaged;
				}
				this.setAnimation("animation.skadi_corrupted.convert_in_1");
				if (!this.level().isClientSide())
					this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 9999, 9, false, false));
				this.getEntityData().set(DATA_duration, 10000);
				this.getEntityData().set(DATA_convertTick, 30);
				this.getEntityData().set(DATA_convertP, 10000);
			} else {
				this.getEntityData().set(DATA_mayCorrupt, true);
			}
		}
		return damaged;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("DataconvertP", this.entityData.get(DATA_convertP));
		compound.putBoolean("DatamayCorrupt", this.entityData.get(DATA_mayCorrupt));
		compound.putInt("Dataduration", this.entityData.get(DATA_duration));
		compound.putInt("DataconvertTick", this.entityData.get(DATA_convertTick));
		compound.putInt("Datadeal", this.entityData.get(DATA_deal));
		compound.putInt("Dataphase", this.entityData.get(DATA_phase));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("DataconvertP"))
			this.entityData.set(DATA_convertP, compound.getInt("DataconvertP"));
		if (compound.contains("DatamayCorrupt"))
			this.entityData.set(DATA_mayCorrupt, compound.getBoolean("DatamayCorrupt"));
		if (compound.contains("Dataduration"))
			this.entityData.set(DATA_duration, compound.getInt("Dataduration"));
		if (compound.contains("DataconvertTick"))
			this.entityData.set(DATA_convertTick, compound.getInt("DataconvertTick"));
		if (compound.contains("Datadeal"))
			this.entityData.set(DATA_deal, compound.getInt("Datadeal"));
		if (compound.contains("Dataphase"))
			this.entityData.set(DATA_phase, compound.getInt("Dataphase"));
	}

	@Override
	public void baseTick() {
		super.baseTick();
		LevelAccessor world = this.level();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
        double dura;
		double conv;
		double deal;
		double phase = 0;
		double converT;
		double gap;
		double nn;
		converT = (Entity) this instanceof SkadiCorruptedEntity _datEntI ? _datEntI.getEntityData().get(DATA_convertTick) : 0;
		if (converT < 999) {
			if (converT > 0) {
				if ((Entity) this instanceof SkadiCorruptedEntity _datEntSetL)
					_datEntSetL.getEntityData().set(DATA_mayCorrupt, false);
				if ((Entity) this instanceof SkadiCorruptedEntity _datEntSetI)
					_datEntSetI.getEntityData().set(DATA_convertTick, (int) (converT - 1));
			} else if (phase < 0.5) {
				for (Entity entityiterator : new ArrayList<>(world.players())) {
					if ((entityiterator != null ? distanceTo(entityiterator) : -1) < 32) {
						if (entityiterator instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(Component.literal((Component.translatable("entity.caerula_arbor.skadi_corrupted.convert").getString())), false);
					}
				}
				if (world instanceof ServerLevel _level) {
					ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(CAItems.INCANDESCENT_ANIMA.get()));
					entityToSpawn.setPickUpDelay(10);
					entityToSpawn.setUnlimitedLifetime();
					_level.addFreshEntity(entityToSpawn);
				}
				if (world instanceof ServerLevel _level) {
					ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(CAItems.RECORD_UNDERTIDES.get()));
					entityToSpawn.setPickUpDelay(10);
					entityToSpawn.setUnlimitedLifetime();
					_level.addFreshEntity(entityToSpawn);
				}
				if (world instanceof Level _level) {
					_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "corrupted_convert")), SoundSource.HOSTILE, 2, 1);
				}
				if ((Entity) this instanceof SkadiCorruptedEntity _datEntSetL)
					_datEntSetL.getEntityData().set(DATA_mayCorrupt, false);
				if (!level().isClientSide())
					discard();
				this.spawnHurtSkadi(world, x, y, z);
			}
		}
		if (this.isAlive()) {
			if (!this.level().isClientSide())
				this.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20, 0, false, false));
			conv = (Entity) this instanceof SkadiCorruptedEntity _datEntI ? _datEntI.getEntityData().get(DATA_convertP) : 0;
			dura = (Entity) this instanceof SkadiCorruptedEntity _datEntI ? _datEntI.getEntityData().get(DATA_duration) : 0;
			deal = (Entity) this instanceof SkadiCorruptedEntity _datEntI ? _datEntI.getEntityData().get(DATA_deal) : 0;
			phase = (Entity) this instanceof SkadiCorruptedEntity _datEntI ? _datEntI.getEntityData().get(DATA_phase) : 0;
			if (dura > 0) {
				if ((Entity) this instanceof SkadiCorruptedEntity _datEntSetI)
					_datEntSetI.getEntityData().set(DATA_duration, (int) (dura - 1));
			}
			if (conv > 0) {
				if (phase < 1.9) {
					if ((Entity) this instanceof SkadiCorruptedEntity _datEntSetI)
						_datEntSetI.getEntityData().set(DATA_convertP, (int) (conv - 1));
				}
			} else if (dura <= 0) {
				if (phase < 0.5) {
					if (this instanceof SkadiCorruptedEntity) {
						this.setAnimation("animation.skadi_corrupted.to_phase_2");
					}
					if ((Entity) this instanceof SkadiCorruptedEntity _datEntSetI)
						_datEntSetI.getEntityData().set(DATA_convertP, 1120);
					if ((Entity) this instanceof LivingEntity _entity)
						_entity.setHealth((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1);
					if ((Entity) this instanceof SkadiCorruptedEntity _datEntSetI)
						_datEntSetI.getEntityData().set(DATA_phase, 1);
					if ((Entity) this instanceof SkadiCorruptedEntity _datEntSetI)
						_datEntSetI.getEntityData().set(DATA_deal, 0);
					if ((Entity) this instanceof SkadiCorruptedEntity _datEntSetI)
						_datEntSetI.getEntityData().set(DATA_duration, 60);
					if (this.getAttributes().hasAttribute(Attributes.ARMOR))
						this.getAttribute(Attributes.ARMOR)
								.setBaseValue(((this.getAttributes().hasAttribute(Attributes.ARMOR) ? this.getAttribute(Attributes.ARMOR).getBaseValue() : 0) * 1.5));
					if ((Entity) this instanceof SkadiCorruptedEntity _datEntSetL)
						_datEntSetL.getEntityData().set(DATA_mayCorrupt, true);
					if (!this.level().isClientSide())
						this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 60, 9, false, false));
					if (!world.isClientSide()) {
						if (world instanceof Level _level) {
							_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "silence3")), SoundSource.HOSTILE, 2, 1);
						}
					}
				} else if (phase < 1.5) {
					if (this instanceof SkadiCorruptedEntity) {
						this.setAnimation("animation.skadi_corrupted.to_phase_3");
					}
					if ((Entity) this instanceof SkadiCorruptedEntity _datEntSetI)
						_datEntSetI.getEntityData().set(DATA_convertP, 99999);
					if ((Entity) this instanceof LivingEntity _entity)
						_entity.setHealth((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1);
					if ((Entity) this instanceof SkadiCorruptedEntity _datEntSetI)
						_datEntSetI.getEntityData().set(DATA_phase, 2);
					if ((Entity) this instanceof SkadiCorruptedEntity _datEntSetI)
						_datEntSetI.getEntityData().set(DATA_deal, 0);
					if ((Entity) this instanceof SkadiCorruptedEntity _datEntSetI)
						_datEntSetI.getEntityData().set(DATA_duration, 80);
					if (this.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get()))
						this.getAttribute(CAAttributes.MAGIC_RESISTANCE.get())
								.setBaseValue(((this.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get())
										? this.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).getBaseValue()
										: 0) + 50));
					if (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
						this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(
								((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : 0) * 1.25));
					if ((Entity) this instanceof SkadiCorruptedEntity _datEntSetL)
						_datEntSetL.getEntityData().set(DATA_mayCorrupt, true);
					if (!this.level().isClientSide())
						this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 80, 9, false, false));
					if (!world.isClientSide()) {
						if (world instanceof Level _level) {
							_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "silence4")), SoundSource.HOSTILE, 2, 1);
						}
					}
				}
			}
			if (phase < 0.5) {
				gap = 300;
				nn = 3;
			} else if (phase < 1.5) {
				gap = 360;
				nn = 4;
			} else {
				gap = 300;
				nn = 5;
				if (tickCount % 20 == 5) {
					Entity enemy1;
					double ddd;
					double dama;
					ddd = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
                    enemy1 = this.getTarget();
					{
						final Vec3 _center = new Vec3(x, y, z);
						List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(24 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
						for (Entity entityiterator : _entfound) {
							if (!(entityiterator instanceof LivingEntity)) {
								continue;
							}
							if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
								if (!(entityiterator == enemy1)) {
									continue;
								}
							}
							if (entityiterator == this) {
								continue;
							}
							if (new Object() {
								public boolean checkGamemode(Entity _ent) {
									if (_ent instanceof ServerPlayer _serverPlayer) {
										return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
									} else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
										return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
												&& Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
									}
									return false;
								}
							}.checkGamemode(entityiterator)) {
								continue;
							}
							if (new Object() {
								public boolean checkGamemode(Entity _ent) {
									if (_ent instanceof ServerPlayer _serverPlayer) {
										return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
									} else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
										return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
												&& Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.SPECTATOR;
									}
									return false;
								}
							}.checkGamemode(entityiterator)) {
								continue;
							}
							if (distanceTo(entityiterator) <= 12) {
								entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "sanity_break")))),
										(float) (ddd * 1.1));
							}
						}
					}
					dama = ((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.01;
					if (((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) > dama) {
						if ((Entity) this instanceof LivingEntity _entity)
							_entity.setHealth((float) (((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) - dama));
					} else {
						((Entity) this).hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceankiller_damage")))), 99999);
					}
				}
                double ang;
				double r;
				double t;
				t = tickCount % 90;
				for (int index0 = 0; index0 < 20; index0++) {
					ang = Math.toRadians(index0 * 6 + t * 4);
					r = 11.5 + Math.sin(index0 * 12);
					if (world instanceof ServerLevel _level)
						_level.sendParticles(CAParticles.CORRUPTED_FISH.get(), (x + r * Math.sin(ang)), (y + 0.15), (z + r * Math.cos(ang)), 1, 0, 0.25, 0, 0.2);
				}
			}
			if (tickCount % 20 == 10) {
				double ddd;
				double healPerc;
				boolean mayBonus;
				boolean isSeaborn;
				Entity enemy1;
				ddd = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
				healPerc = 0.1;
                enemy1 = this.getTarget();
				if (phase > 0.5) {
					healPerc = 0.2;
				}
				if (phase <= 1) {
					EntityUtils.heal(this, ddd * healPerc * 3);
					if (phase > 0.5) {
						if ((Entity) this instanceof SkadiCorruptedEntity _datEntSetI)
							_datEntSetI.getEntityData().set(DATA_deal, (int) (((Entity) this instanceof SkadiCorruptedEntity _datEntI ? _datEntI.getEntityData().get(DATA_deal) : 0) - ddd * healPerc * 3));
					}
				}
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(32 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						mayBonus = false;
						isSeaborn = false;
						if ((entityiterator != null ? distanceTo(entityiterator) : -1) <= 16) {
							if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
								mayBonus = true;
								isSeaborn = true;
							}
							if (phase <= 1 && entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "is_humanside")))) {
								mayBonus = true;
								isSeaborn = false;
							}
							if (entityiterator == this) {
								continue;
							}
							if (mayBonus && entityiterator instanceof LivingEntity livingEntity) {
								EntityUtils.heal(livingEntity, ddd * healPerc);
								if (phase == 1 && !entityiterator.getPersistentData().getBoolean("corruptedBonus1")) {
									if (entityiterator instanceof LivingEntity _livingEntity10 && _livingEntity10.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
										_livingEntity10.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(
												((entityiterator instanceof LivingEntity _livingEntity9 && _livingEntity9.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity9.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : 0)
														+ ddd * 0.4));
									if (entityiterator instanceof LivingEntity _livingEntity12 && _livingEntity12.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE.get()))
										_livingEntity12.getAttribute(CAAttributes.GENERAL_DEFENSE.get())
												.setBaseValue(((entityiterator instanceof LivingEntity _livingEntity11 && _livingEntity11.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE.get())
														? _livingEntity11.getAttribute(CAAttributes.GENERAL_DEFENSE.get()).getBaseValue()
														: 0) + ddd * 0.4));
									entityiterator.getPersistentData().putBoolean("corruptedBonus1", true);
								}
								if (phase == 2 && !entityiterator.getPersistentData().getBoolean("corruptedBonus2")) {
									if (entityiterator instanceof LivingEntity _livingEntity16 && _livingEntity16.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
										_livingEntity16.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(
												((entityiterator instanceof LivingEntity _livingEntity15 && _livingEntity15.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity15.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : 0)
														+ ddd));
									if (entityiterator instanceof LivingEntity _livingEntity18 && _livingEntity18.getAttributes().hasAttribute(Attributes.MAX_HEALTH))
										_livingEntity18.getAttribute(Attributes.MAX_HEALTH).setBaseValue(
												((entityiterator instanceof LivingEntity _livingEntity17 && _livingEntity17.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? _livingEntity17.getAttribute(Attributes.MAX_HEALTH).getBaseValue() : 0) + ddd));
									entityiterator.getPersistentData().putBoolean("corruptedBonus2", true);
								}
								if (phase > 0.5 && isSeaborn) {
									if (!entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "bossoffspring")))
											&& !entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanpet")))) {
										if (entityiterator instanceof Mob _entity && enemy1 instanceof LivingEntity _ent)
											_entity.setTarget(_ent);
									}
								}
							}
						}
					}
				}
			}
			if (tickCount % gap == 99) {
				assert Boolean.TRUE; //#dbg:SkadiCorruptedSkills:corruptedSpawnCheck
				WorldUtils.corruptedSpawnMobs(world, x, y, z, nn);
			}
			double phase1;
			double ang;
			double r;
			ang = Mth.nextDouble(RandomSource.create(), 0, 6.283);
			phase1 = (Entity) this instanceof SkadiCorruptedEntity _datEntI ? _datEntI.getEntityData().get(DATA_phase) : 0;
			for (int index0 = 0; index0 < (int) (phase1 + 1); index0++) {
				r = Mth.nextDouble(RandomSource.create(), 2, 3.5);
				if (world instanceof ServerLevel _level)
					_level.sendParticles(CAParticles.CORRUPTED_FISH.get(), (x + r * Math.sin(ang)), (y + 0.25), (z + r * Math.cos(ang)), 1, 0, 0, 0, 0.2);
			}
			if (!(phase > 1.5)) {
				LivingEntity _livEnt = this;
				if (deal >= _livEnt.getMaxHealth() * 0.75) {
					if ((Entity) this instanceof SkadiCorruptedEntity _datEntSetL)
						_datEntSetL.getEntityData().set(DATA_mayCorrupt, false);
				} else {
					if ((Entity) this instanceof SkadiCorruptedEntity _datEntSetL)
						_datEntSetL.getEntityData().set(DATA_mayCorrupt, true);
				}
			}
		}
		if (entityData.get(DATA_mayCorrupt) || getPhase() >= 2) bossInfo.setColor(ServerBossEvent.BossBarColor.RED);
		else bossInfo.setColor(ServerBossEvent.BossBarColor.BLUE);
		this.refreshDimensions();
	}

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		if (player.getMainHandItem().getItem() == CAItems.CORRUPTED_HEART_SPAWNER.get()) {
			entityData.set(DATA_convertP, 1);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
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
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
		double x = this.getX(), y = this.getY(), z = this.getZ();
		if (world instanceof Level level && !level.isClientSide()) {
			level.playSound(
					null, BlockPos.containing(x, y, z),
					ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "silence2")),
					SoundSource.NEUTRAL, 1, 1);
		}
		return retval;
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.12);
		builder = builder.add(Attributes.MAX_HEALTH, 405);
		builder = builder.add(Attributes.ARMOR, 9);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 15);
		builder = builder.add(Attributes.FOLLOW_RANGE, 36);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
		builder = builder.add(CAAttributes.SANITY_MODIFIER.get(), 0.02);
		builder = builder.add(CAAttributes.MAGIC_RESISTANCE.get(), 30);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.1F && event.getLimbSwingAmount() < 0.1F))

			) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.skadi_corrupted.move"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.skadi_corrupted.convert_in_23"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.skadi_corrupted.idle"));
		}
		return PlayState.STOP;
	}

	private PlayState attackingPredicate(AnimationState event) {
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
			this.swinging = true;
			this.lastSwing = level().getGameTime();
		}
		if (this.swinging && this.lastSwing + 40L <= level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.skadi_corrupted.attack"));
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

	public boolean isCorruptedDurative() {
		return this.isAlive() && this.getEntityData().get(DATA_duration) <= 0;
	}

	private void spawnHurtSkadi(LevelAccessor world, double x, double y, double z) {
		if (world instanceof ServerLevel level) {
			LivingEntity entityToSpawn = CAEntities.SKADI.get().spawn(level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
				entityToSpawn.setHealth(entityToSpawn.getMaxHealth() * 0.4F);
				entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
			}
		}
	}

	private boolean isCorruptedSource(DamageSource source) {
		Entity sourceEntity = source.getEntity();
		if (sourceEntity == null) {
			return true;
		}
		if (sourceEntity.getType().is(EntityUtils.HUMAN)) {
			return true;
		}
		return sourceEntity instanceof Player;
	}

	@Override
	protected void tickDeath() {
		++this.deathTime;
		if (this.deathTime == 30) {
			this.remove(RemovalReason.KILLED);
			this.dropExperience();
			LevelAccessor world = this.level();
			double x = this.getX();
			double y = this.getY();
			double z = this.getZ();
			if ((Entity) this instanceof SkadiCorruptedEntity _datEntL0 && _datEntL0.getEntityData().get(DATA_mayCorrupt)) {
				if (!world.isClientSide()) {
					if (world instanceof Level _level) {
						_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "corrupted_corrupt")), SoundSource.HOSTILE, 2, 1);
					}
				}
				for (Entity entityiterator : new ArrayList<>(world.players())) {
					if ((entityiterator != null ? distanceTo(entityiterator) : -1) < 32) {
						if (entityiterator instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(Component.literal((Component.translatable("entity.caerula_arbor.skadi_corrupted.corrupted").getString())), false);
					}
				}
				if (world instanceof ServerLevel _level) {
					Entity entityToSpawn = CAEntities.ISHARMLA.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
					}
				}
			} else {
				if (!world.isClientSide()) {
					if (world instanceof Level _level) {
						_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "corrupted_convert")), SoundSource.HOSTILE, 2, 1);
					}
				}
				for (Entity entityiterator : new ArrayList<>(world.players())) {
					if ((entityiterator != null ? distanceTo(entityiterator) : -1) < 32) {
						if (entityiterator instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(Component.literal((Component.translatable("entity.caerula_arbor.skadi_corrupted.convert").getString())), false);
					}
				}
				if (world instanceof ServerLevel _level) {
					ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(CAItems.INCANDESCENT_ANIMA.get()));
					entityToSpawn.setPickUpDelay(10);
					entityToSpawn.setUnlimitedLifetime();
					_level.addFreshEntity(entityToSpawn);
				}
				this.spawnHurtSkadi(world, x, y, z);
			}
			if (world instanceof ServerLevel _level) {
				ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(CAItems.RECORD_UNDERTIDES.get()));
				entityToSpawn.setPickUpDelay(10);
				entityToSpawn.setUnlimitedLifetime();
				_level.addFreshEntity(entityToSpawn);
			}
		}
	}

	@Override
	public void setHealth(float pHealth) {
		if (getPhase() < 2 && pHealth <= 0 && entityData.get(DATA_mayCorrupt)) {
			entityData.set(DATA_convertP, 1);
			super.setHealth(this.getMaxHealth());
			return;
		}
		super.setHealth(pHealth);
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

	public int getPhase() {
		return entityData.get(DATA_phase);
	}


	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}
}
