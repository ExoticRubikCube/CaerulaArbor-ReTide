package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.RangedSanityAttacker;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CaerulaArborModAttributes;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import com.apocalypse.caerulaarbor.init.CaerulaArborModItems;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.util.EntityPredicateUtils;
import com.apocalypse.caerulaarbor.util.EntityUtils;
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
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
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
import java.util.List;

public class TideChimeraEntity extends SeaMonster implements RangedSanityAttacker {

	private boolean isChimeraDurative() {
		return EntityPredicateUtils.isChimeraDurative(this);
	}
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(TideChimeraEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(TideChimeraEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(TideChimeraEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_duration = SynchedEntityData.defineId(TideChimeraEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_summonP = SynchedEntityData.defineId(TideChimeraEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_skillP = SynchedEntityData.defineId(TideChimeraEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_deal = SynchedEntityData.defineId(TideChimeraEntity.class, EntityDataSerializers.INT);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";
	private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.BLUE, ServerBossEvent.BossBarOverlay.NOTCHED_12);

	public TideChimeraEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CaerulaArborModEntities.TIDE_CHIMERA.get(), world);
	}

	public TideChimeraEntity(EntityType<TideChimeraEntity> type, Level world) {
		super(type, world);
		xpReward = 99;
		setNoAi(false);
		setMaxUpStep(1.5f);
		setPersistenceRequired();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "general_seaborns");
		this.entityData.define(DATA_duration, 0);
		this.entityData.define(DATA_summonP, 3);
		this.entityData.define(DATA_skillP, 200);
		this.entityData.define(DATA_deal, 0);
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
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 16;
			}

			@Override
			public boolean canUse() {
				double x = TideChimeraEntity.this.getX();
				double y = TideChimeraEntity.this.getY();
				double z = TideChimeraEntity.this.getZ();
				Entity entity = TideChimeraEntity.this;
				Level world = TideChimeraEntity.this.level();
				return super.canUse() && isChimeraDurative();
			}

			@Override
			public boolean canContinueToUse() {
				double x = TideChimeraEntity.this.getX();
				double y = TideChimeraEntity.this.getY();
				double z = TideChimeraEntity.this.getZ();
				Entity entity = TideChimeraEntity.this;
				Level world = TideChimeraEntity.this.level();
				return super.canContinueToUse() && isChimeraDurative();
			}

		});
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, SnowGolem.class, true, false));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolem.class, true, false));
		this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1) {
			@Override
			public boolean canUse() {
				double x = TideChimeraEntity.this.getX();
				double y = TideChimeraEntity.this.getY();
				double z = TideChimeraEntity.this.getZ();
				Entity entity = TideChimeraEntity.this;
				Level world = TideChimeraEntity.this.level();
				return super.canUse() && isChimeraDurative();
			}

			@Override
			public boolean canContinueToUse() {
				double x = TideChimeraEntity.this.getX();
				double y = TideChimeraEntity.this.getY();
				double z = TideChimeraEntity.this.getZ();
				Entity entity = TideChimeraEntity.this;
				Level world = TideChimeraEntity.this.level();
				return super.canContinueToUse() && isChimeraDurative();
			}
		});
		this.goalSelector.addGoal(6, new RandomLookAroundGoal(this) {
			@Override
			public boolean canUse() {
				double x = TideChimeraEntity.this.getX();
				double y = TideChimeraEntity.this.getY();
				double z = TideChimeraEntity.this.getZ();
				Entity entity = TideChimeraEntity.this;
				Level world = TideChimeraEntity.this.level();
				return super.canUse() && isChimeraDurative();
			}

			@Override
			public boolean canContinueToUse() {
				double x = TideChimeraEntity.this.getX();
				double y = TideChimeraEntity.this.getY();
				double z = TideChimeraEntity.this.getZ();
				Entity entity = TideChimeraEntity.this;
				Level world = TideChimeraEntity.this.level();
				return super.canContinueToUse() && isChimeraDurative();
			}
		});
		this.goalSelector.addGoal(7, new FloatGoal(this));
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
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "apocata_die"));
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		double targetX = target.getX();
		double targetY = target.getY();
		double targetZ = target.getZ();
		if (!this.level().isClientSide()) {
			CaerulaArborMod.queueServerWork(8, () -> {
				if (this.isAlive() && target.isAlive() && this.distanceTo(target) <= 4) {
					EntityUtils.giveLessArmor(target, 11);
					this.level().playSound(null, BlockPos.containing(targetX, targetY, targetZ),
							ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "puncturefish_attack")), SoundSource.HOSTILE, 3,
							(float) Mth.nextDouble(RandomSource.create(), 0.9, 1.1));
					target.hurt(
							new DamageSource(
									this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
											.getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "general_seaborn_attack"))),
									this),
							(float) (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0));
				}
			});
		}
		return true;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
        LevelAccessor world = this.level();
        Entity sourceentity = source.getEntity();
        if (this != null && sourceentity != null) {
            if (!(sourceentity instanceof Player) && !(sourceentity instanceof ApocataEntity)) {
                if (!sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                    {
                        final Vec3 _center = new Vec3(this.getX(), this.getY(), this.getZ());
                        List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(32 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                        for (Entity entityiterator : _entfound) {
                            if (entityiterator == this) {
                                continue;
                            }
                            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                                if (entityiterator instanceof Mob _entity && sourceentity instanceof LivingEntity _ent)
                                    _entity.setTarget(_ent);
                            }
                        }
                    }
                }
            }
        }
        if (source.is(DamageTypes.FALL))
			return false;
		if (source.is(DamageTypes.DROWN))
			return false;
		float healthBeforeDamage = this.getHealth();
		boolean damaged = super.hurt(source, amount);
		if (damaged && amount <= healthBeforeDamage) {
			double accumulatedDamage = this.getEntityData().get(DATA_deal) + amount;
			this.getEntityData().set(DATA_deal, (int) accumulatedDamage);
			if (accumulatedDamage >= this.getMaxHealth() * 0.25) {
				this.performRangedSanityAttack();
				this.getEntityData().set(DATA_deal, 0);
			}
		}
		return damaged;
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        if (this != null) {
            if (!this.level().isClientSide())
                this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 100, 9, false, false));
            if (this.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_RATE.get()))
                this.getAttribute(CaerulaArborModAttributes.SANITY_RATE.get()).setBaseValue(4);
            if (this.getAttributes().hasAttribute(CaerulaArborModAttributes.MISSRATE.get()))
                this.getAttribute(CaerulaArborModAttributes.MISSRATE.get()).setBaseValue(50);
            if (this.getAttributes().hasAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()))
                this.getAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()).setBaseValue(45);
            if (this instanceof TideChimeraEntity) {
                this.setAnimation("animation.super_apocata.start");
            }
            CaerulaArborMod.queueServerWork(36, () -> {
                if (this.isAlive()) {
                    if ((LevelAccessor) world instanceof Level _level) {
                        if (!_level.isClientSide()) {
                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.anvil.land")), SoundSource.HOSTILE, (float) 2.5, 1);
                        } else {
                            _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.anvil.land")), SoundSource.HOSTILE, (float) 2.5, 1, false);
                        }
                    }
                }
            });
            CaerulaArborMod.queueServerWork(60, () -> {
                if (this.isAlive()) {
                    if ((LevelAccessor) world instanceof Level _level) {
                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.slime.jump")), SoundSource.HOSTILE, (float) 2.5, 1);
                    }
                }
            });
            CaerulaArborMod.queueServerWork(66, () -> {
                if (this.isAlive()) {
                    if ((LevelAccessor) world instanceof Level _level) {
                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.slime.jump")), SoundSource.HOSTILE, (float) 2.5, 1);
                    }
                }
            });
            CaerulaArborMod.queueServerWork(83, () -> {
                if (this.isAlive()) {
                    if ((LevelAccessor) world instanceof Level _level) {
                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.slime_block.place")), SoundSource.HOSTILE, (float) 2.5, 1);
                    }
                }
            });
        }
        return retval;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putInt("Dataduration", this.entityData.get(DATA_duration));
		compound.putInt("DatasummonP", this.entityData.get(DATA_summonP));
		compound.putInt("DataskillP", this.entityData.get(DATA_skillP));
		compound.putInt("Datadeal", this.entityData.get(DATA_deal));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("Dataduration"))
			this.entityData.set(DATA_duration, compound.getInt("Dataduration"));
		if (compound.contains("DatasummonP"))
			this.entityData.set(DATA_summonP, compound.getInt("DatasummonP"));
		if (compound.contains("DataskillP"))
			this.entityData.set(DATA_skillP, compound.getInt("DataskillP"));
		if (compound.contains("Datadeal"))
			this.entityData.set(DATA_deal, compound.getInt("Datadeal"));
	}

	@Override
	public void baseTick() {
		super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        if (this != null) {
            Entity enemy = null;
            double sklp1 = 0;
            double dura = 0;
            double tap = 0;
            double perc = 0;
            boolean maySummon = false;
            if (((Entity) this instanceof LivingEntity _livEnt ? _livEnt.deathTime : 0) == 10) {
                if (world instanceof Level _level) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.dragon_fireball.explode")), SoundSource.HOSTILE, 2, 1);
                }
                Entity entityToSpawn = null;
                BlockPos pos = BlockPos.containing(x, y, z);
                if (world instanceof ServerLevel _level) {
                    entityToSpawn = CaerulaArborModEntities.CRACKER_ABYSSAL.get().spawn(_level, pos, MobSpawnType.MOB_SUMMONED);
                    if (entityToSpawn != null) {
                        entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                    }
                    entityToSpawn = CaerulaArborModEntities.GUIDE_ABYSSAL.get().spawn(_level, pos, MobSpawnType.MOB_SUMMONED);
                    if (entityToSpawn != null) {
                        entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                    }
                    entityToSpawn = CaerulaArborModEntities.UMBRELLA_ABYSSAL.get().spawn(_level, pos, MobSpawnType.MOB_SUMMONED);
                    if (entityToSpawn != null) {
                        entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                    }
                    entityToSpawn = CaerulaArborModEntities.CREEPER_FISH.get().spawn(_level, pos, MobSpawnType.MOB_SUMMONED);
                    if (entityToSpawn != null) {
                        entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                    }
                    entityToSpawn = CaerulaArborModEntities.PREGNANT_FISH.get().spawn(_level, pos, MobSpawnType.MOB_SUMMONED);
                    if (entityToSpawn != null) {
                        entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                    }
                    entityToSpawn = CaerulaArborModEntities.BASELAYER_ABYSSAL.get().spawn(_level, pos, MobSpawnType.MOB_SUMMONED);
                    if (entityToSpawn != null) {
                        entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                    }
                    entityToSpawn = CaerulaArborModEntities.PREDATOR_ABYSSAL.get().spawn(_level, pos, MobSpawnType.MOB_SUMMONED);
                    if (entityToSpawn != null) {
                        entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                    }
                    entityToSpawn = CaerulaArborModEntities.SPLASHER_ABYSSAL.get().spawn(_level, pos, MobSpawnType.MOB_SUMMONED);
                    if (entityToSpawn != null) {
                        entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                    }
                    entityToSpawn = CaerulaArborModEntities.APOSTLE_PROKARYOTE.get().spawn(_level, pos, MobSpawnType.MOB_SUMMONED);
                    if (entityToSpawn != null) {
                        entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                    }
                    entityToSpawn = CaerulaArborModEntities.DIVICELLULAR_GO.get().spawn(_level, pos, MobSpawnType.MOB_SUMMONED);
                    if (entityToSpawn != null) {
                        entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                    }
                    entityToSpawn = CaerulaArborModEntities.PUNCTURE_FISH.get().spawn(_level, pos, MobSpawnType.MOB_SUMMONED);
                    if (entityToSpawn != null) {
                        entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                    }
                    entityToSpawn = CaerulaArborModEntities.NUCLEIC_MALEFICENT.get().spawn(_level, pos, MobSpawnType.MOB_SUMMONED);
                    if (entityToSpawn != null) {
                        entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                    }
                }
            }
            if (this.isAlive()) {
                sklp1 = (Entity) this instanceof TideChimeraEntity _datEntI ? _datEntI.getEntityData().get(DATA_skillP) : 0;
                dura = (Entity) this instanceof TideChimeraEntity _datEntI ? _datEntI.getEntityData().get(DATA_duration) : 0;
                tap = (Entity) this instanceof TideChimeraEntity _datEntI ? _datEntI.getEntityData().get(DATA_summonP) : 0;
                perc = EntityUtils.getHealthPerc(this);
                enemy = (Entity) this instanceof Mob _mobEnt ? _mobEnt.getTarget() : null;
                if (dura > 0) {
                    if ((Entity) this instanceof TideChimeraEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_duration, (int) (dura - 1));
                }
                if (dura <= 0) {
                    if (perc <= 0.25 && tap >= 1) {
                        maySummon = true;
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.IMMORTAL.get(), 600, 0, false, false));
                        CaerulaArborMod.queueServerWork(10, () -> {
                            summonRandomChimera(world, x, y, z);
                        });
                    } else if (perc <= 0.5 && tap >= 2) {
                        maySummon = true;
                        CaerulaArborMod.queueServerWork(10, () -> {
                            summonRandomChimera(world, x, y, z);
                        });
                    } else if (perc <= 0.75 && tap >= 3) {
                        maySummon = true;
                        CaerulaArborMod.queueServerWork(10, () -> {
                            summonRandomChimera(world, x, y, z);
                        });
                    }
                    if (maySummon) {
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 20, 0, false, false));
                        if ((Entity) this instanceof TideChimeraEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_summonP, (int) (tap - 1));
                        if ((Entity) this instanceof TideChimeraEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_duration, 18);
                        dura = 18;
                        if (this instanceof TideChimeraEntity) {
                            this.setAnimation("animation.super_apocata.throw");
                        }
                    }
                }
                if (sklp1 > 0) {
                    if ((Entity) this instanceof TideChimeraEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_skillP, (int) (sklp1 - 1));
                } else if (dura <= 0) {
                    if (!(enemy == null) && enemy.isAlive()) {
                        if ((enemy != null ? distanceTo(enemy) : -1) <= 8) {
                            if (this instanceof TideChimeraEntity) {
                                this.setAnimation("animation.super_apocata.ranged");
                            }
                            if ((Entity) this instanceof TideChimeraEntity _datEntSetI)
                                _datEntSetI.getEntityData().set(DATA_skillP, 300);
                            if ((Entity) this instanceof TideChimeraEntity _datEntSetI)
                                _datEntSetI.getEntityData().set(DATA_duration, 28);
                            CaerulaArborMod.queueServerWork(12, () -> {
                                if (this.isAlive()) {
                                    if (world instanceof ServerLevel _level)
                                        _level.sendParticles(ParticleTypes.EXPLOSION, x, (y + 4), z, 3, 0, 0, 0, 0.1);
                                    if (world instanceof Level _level) {
                                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "firstteller_attack")), SoundSource.HOSTILE, (float) 1.5, 1);
                                    }
                                    this.distributeBullets(world, x, y, z);
                                }
                            });
                            CaerulaArborMod.queueServerWork(13, () -> {
                                if (this.isAlive()) {
                                    if (world instanceof ServerLevel _level)
                                        _level.sendParticles(ParticleTypes.EXPLOSION, x, (y + 4), z, 3, 0, 0, 0, 0.1);
                                    if (world instanceof Level _level) {
                                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "firstteller_attack")), SoundSource.HOSTILE, (float) 1.5, 1);
                                    }
                                    this.distributeBullets(world, x, y, z);
                                }
                            });
                            CaerulaArborMod.queueServerWork(14, () -> {
                                if (this.isAlive()) {
                                    if (world instanceof ServerLevel _level)
                                        _level.sendParticles(ParticleTypes.EXPLOSION, x, (y + 4), z, 3, 0, 0, 0, 0.1);
                                    if (world instanceof Level _level) {
                                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "firstteller_attack")), SoundSource.HOSTILE, (float) 1.5, 1);
                                    }
                                    this.distributeBullets(world, x, y, z);
                                }
                            });
                        }
                    }
                }
                EntityUtils.giveGuideLay(this);
                if (this != null) {
                    double angle = 0;
                    double d = 0;
                    double daam = 0;
                    if ((Entity) this instanceof LivingEntity _entity)
                        _entity.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
                    for (int index0 = 0; index0 < 8; index0++) {
                        angle = Mth.nextDouble(RandomSource.create(), 0, 6.283);
                        d = Mth.nextDouble(RandomSource.create(), 6.5, 6.75);
                        if (world instanceof ServerLevel _level)
                            _level.sendParticles(ParticleTypes.BUBBLE_COLUMN_UP, (x + d * Math.sin(angle)), (y + 0.4), (z + d * Math.cos(angle)), 2, 0.1, 0.1, 0.1, 0.1);
                        angle = Mth.nextDouble(RandomSource.create(), 0, 6.283);
                        d = Mth.nextDouble(RandomSource.create(), 6.5, 6.75);
                        if (world instanceof ServerLevel _level)
                            _level.sendParticles(ParticleTypes.ELECTRIC_SPARK, (x + d * Math.sin(angle)), (y + 0.4), (z + d * Math.cos(angle)), 2, 0.1, 0.1, 0.1, 0.1);
                    }
                    if (tickCount % 20 == 0) {
                        angle = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
                        for (Entity entityiterator : world.getEntities(this, new AABB((x - 6.5), (y - 2), (z - 6.5), (x + 6.5), (y + 5), (z + 6.5)))) {
                            if ((entityiterator != null ? distanceTo(entityiterator) : -1) <= 6.5) {
                                if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                                    if (!(entityiterator == ((Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null))) {
                                        continue;
                                    }
                                }
                                if (!(entityiterator instanceof LivingEntity)) {
                                    continue;
                                }
                                EntityUtils.deductSanity(entityiterator, daam * 4);
                                entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "ocean_magic")))),
                                        (float) (daam * 0.5));
                            }
                        }
                    }
                }
            }
        }
        this.refreshDimensions();
	}

	private void summonRandomChimera(LevelAccessor world, double x, double y, double z) {
		double randomValue = Math.random();
		if (world instanceof Level level) {
				level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.lingering_potion.throw")), SoundSource.HOSTILE, 3, 1);
		}
		Entity entityToSpawn = null;
		BlockPos pos = BlockPos.containing(x, y + 3, z);
		if (world instanceof ServerLevel serverLevel) {
			if (randomValue < 0.01) {
				entityToSpawn = CaerulaArborModEntities.IZUMIK.get().spawn(serverLevel, pos, MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn instanceof IzumikEntity izumik) {
					izumik.getEntityData().set(IzumikEntity.DATA_growth_p, 20);
				}
			} else if (randomValue < 0.02) {
				entityToSpawn = CaerulaArborModEntities.TIDE_CHIMERA.get().spawn(serverLevel, pos, MobSpawnType.MOB_SUMMONED);
			} else if (randomValue < 0.21) {
				entityToSpawn = CaerulaArborModEntities.TIDE_DEATHREPELLER.get().spawn(serverLevel, pos, MobSpawnType.MOB_SUMMONED);
			} else if (randomValue < 0.4) {
				entityToSpawn = CaerulaArborModEntities.LINGERING_PATHSHAPER.get().spawn(serverLevel, pos, MobSpawnType.MOB_SUMMONED);
			} else if (randomValue < 0.6) {
				entityToSpawn = CaerulaArborModEntities.ENDSPEAKER_3.get().spawn(serverLevel, pos, MobSpawnType.MOB_SUMMONED);
			} else if (randomValue < 0.8) {
				entityToSpawn = CaerulaArborModEntities.FIRST_TO_TALK.get().spawn(serverLevel, pos, MobSpawnType.MOB_SUMMONED);
			} else {
				entityToSpawn = CaerulaArborModEntities.MEGA_CHEST.get().spawn(serverLevel, pos, MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn instanceof MegaChestEntity megaChest) {
					megaChest.getEntityData().set(MegaChestEntity.DATA_released, true);
				}
			}
		}
		if (entityToSpawn != null) {
			entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
			entityToSpawn.push(this.getLookAngle().x, 0.25, this.getLookAngle().z);
		}
	}

	@Override
	public EntityDimensions getDimensions(Pose p_33597_) {
		return super.getDimensions(p_33597_).scale((float) 1);
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	protected void doPush(Entity entityIn) {
	}

	@Override
	protected void pushEntities() {
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

	public static void init() {
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.18);
		builder = builder.add(Attributes.MAX_HEALTH, 300);
		builder = builder.add(Attributes.ARMOR, 8);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 19);
		builder = builder.add(Attributes.FOLLOW_RANGE, 48);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

			) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.super_apocata.move"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.super_apocata.die"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.super_apocata.idle"));
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
		if (this.swinging && this.lastSwing + 19L <= level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.super_apocata.attack"));
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
			this.remove(TideChimeraEntity.RemovalReason.KILLED);
			this.dropExperience();
            LevelAccessor world = this.level();
            if (world instanceof ServerLevel _level) {
                ItemEntity entityToSpawn = new ItemEntity(_level, this.getX(), this.getY(), this.getZ(), new ItemStack(CaerulaArborModItems.APOCALYPSE.get()));
                entityToSpawn.setPickUpDelay(10);
                entityToSpawn.setUnlimitedLifetime();
                _level.addFreshEntity(entityToSpawn);
            }
        }
	}

	@Override
    public void setHealth(float pHealth){
    	float hlth = this.getHealth();
    	float mhlth = this.getMaxHealth();
        if(this.hasEffect(CaerulaArborModMobEffects.INVULNERABLE.get()) && pHealth < this.getHealth()) return;
        float reduction = hlth - pHealth;
        float finalV = reduction >= mhlth * 0.26f ? hlth - mhlth * 0.26f : hlth - reduction;
        super.setHealth(finalV);
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

	public void distributeBullets(LevelAccessor world, double x, double y, double z) {
		double r = 0;
		double d = 0;
		double tx = 0;
		double tz = 0;
		double ty = 0;
		double dama = 0;
		dama = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
		for (int index0 = 0; index0 < 3; index0++) {
			r = Mth.nextDouble(RandomSource.create(), 0, 6.283);
			d = Mth.nextDouble(RandomSource.create(), 2, 9);
			tx = x + d * Math.cos(r);
			ty = y + Mth.nextDouble(RandomSource.create(), 8, 11);
			tz = z + d * Math.sin(r);
			if (world instanceof ServerLevel projectileLevel) {
				Projectile _entityToSpawn = new Object() {
					public Projectile getArrow(Level level, Entity shooter, float damage, int knockback) {
						AbstractArrow entityToSpawn = new FishShootEntity(CaerulaArborModEntities.FISH_SHOOT.get(), level);
						entityToSpawn.setOwner(shooter);
						entityToSpawn.setBaseDamage(damage);
						entityToSpawn.setKnockback(knockback);
						entityToSpawn.setSilent(true);
						entityToSpawn.setCritArrow(true);
						return entityToSpawn;
					}
				}.getArrow(projectileLevel, this, (float) dama, 0);
				_entityToSpawn.setPos(tx, ty, tz);
				_entityToSpawn.shoot(0, (-1), 0, 1, (float) 0.1);
				projectileLevel.addFreshEntity(_entityToSpawn);
			}
		}
		for (int index1 = 0; index1 < 3; index1++) {
			r = Mth.nextDouble(RandomSource.create(), 0, 6.283);
			d = Mth.nextDouble(RandomSource.create(), 2, 9);
			tx = x + d * Math.cos(r);
			ty = y + Mth.nextDouble(RandomSource.create(), 8, 11);
			tz = z + d * Math.sin(r);
			if (world instanceof ServerLevel projectileLevel) {
				Projectile _entityToSpawn = new Object() {
					public Projectile getArrow(Level level, Entity shooter, float damage, int knockback) {
						AbstractArrow entityToSpawn = new FishSplashEntity(CaerulaArborModEntities.FISH_SPLASH.get(), level);
						entityToSpawn.setOwner(shooter);
						entityToSpawn.setBaseDamage(damage);
						entityToSpawn.setKnockback(knockback);
						entityToSpawn.setSilent(true);
						entityToSpawn.setCritArrow(true);
						return entityToSpawn;
					}
				}.getArrow(projectileLevel, this, (float) dama, 0);
				_entityToSpawn.setPos(tx, ty, tz);
				_entityToSpawn.shoot(0, (-1), 0, 1, (float) 0.1);
				projectileLevel.addFreshEntity(_entityToSpawn);
			}
		}
		for (int index2 = 0; index2 < 2; index2++) {
			r = Mth.nextDouble(RandomSource.create(), 0, 6.283);
			d = Mth.nextDouble(RandomSource.create(), 2, 9);
			tx = x + d * Math.cos(r);
			ty = y + Mth.nextDouble(RandomSource.create(), 8, 11);
			tz = z + d * Math.sin(r);
			if (world instanceof ServerLevel projectileLevel) {
				Projectile _entityToSpawn = new Object() {
					public Projectile getArrow(Level level, Entity shooter, float damage, int knockback) {
						AbstractArrow entityToSpawn = new FleefishBulletEntity(CaerulaArborModEntities.FLEEFISH_BULLET.get(), level);
						entityToSpawn.setOwner(shooter);
						entityToSpawn.setBaseDamage(damage);
						entityToSpawn.setKnockback(knockback);
						entityToSpawn.setSilent(true);
						entityToSpawn.setCritArrow(true);
						return entityToSpawn;
					}
				}.getArrow(projectileLevel, this, (float) dama, 0);
				_entityToSpawn.setPos(tx, ty, tz);
				_entityToSpawn.shoot(0, (-1), 0, 1, (float) 0.1);
				projectileLevel.addFreshEntity(_entityToSpawn);
			}
		}
		for (int index3 = 0; index3 < 2; index3++) {
			r = Mth.nextDouble(RandomSource.create(), 0, 6.283);
			d = Mth.nextDouble(RandomSource.create(), 2, 9);
			tx = x + d * Math.cos(r);
			ty = y + Mth.nextDouble(RandomSource.create(), 8, 11);
			tz = z + d * Math.sin(r);
			if (world instanceof ServerLevel projectileLevel) {
				Projectile _entityToSpawn = new Object() {
					public Projectile getArrow(Level level, Entity shooter, float damage, int knockback) {
						AbstractArrow entityToSpawn = new TellerShotEntity(CaerulaArborModEntities.TELLER_SHOT.get(), level);
						entityToSpawn.setOwner(shooter);
						entityToSpawn.setBaseDamage(damage);
						entityToSpawn.setKnockback(knockback);
						entityToSpawn.setSilent(true);
						entityToSpawn.setCritArrow(true);
						return entityToSpawn;
					}
				}.getArrow(projectileLevel, this, (float) dama, 0);
				_entityToSpawn.setPos(tx, ty, tz);
				_entityToSpawn.shoot(0, (-1), 0, 1, (float) 0.1);
				projectileLevel.addFreshEntity(_entityToSpawn);
			}
		}
		for (int index4 = 0; index4 < 2; index4++) {
			r = Mth.nextDouble(RandomSource.create(), 0, 6.283);
			d = Mth.nextDouble(RandomSource.create(), 2, 9);
			tx = x + d * Math.cos(r);
			ty = y + Mth.nextDouble(RandomSource.create(), 8, 11);
			tz = z + d * Math.sin(r);
			if (world instanceof ServerLevel projectileLevel) {
				Projectile _entityToSpawn = new Object() {
					public Projectile getArrow(Level level, Entity shooter, float damage, int knockback) {
						AbstractArrow entityToSpawn = new AbandonedShootEntity(CaerulaArborModEntities.ABANDONED_SHOOT.get(), level);
						entityToSpawn.setOwner(shooter);
						entityToSpawn.setBaseDamage(damage);
						entityToSpawn.setKnockback(knockback);
						entityToSpawn.setSilent(true);
						entityToSpawn.setCritArrow(true);
						return entityToSpawn;
					}
				}.getArrow(projectileLevel, this, (float) dama, 0);
				_entityToSpawn.setPos(tx, ty, tz);
				_entityToSpawn.shoot(0, (-1), 0, 1, (float) 0.1);
				projectileLevel.addFreshEntity(_entityToSpawn);
			}
		}
		for (int index5 = 0; index5 < 3; index5++) {
			r = Mth.nextDouble(RandomSource.create(), 0, 6.283);
			d = Mth.nextDouble(RandomSource.create(), 2, 9);
			tx = x + d * Math.cos(r);
			ty = y + Mth.nextDouble(RandomSource.create(), 8, 11);
			tz = z + d * Math.sin(r);
			if (world instanceof ServerLevel projectileLevel) {
				Projectile _entityToSpawn = new Object() {
					public Projectile getArrow(Level level, Entity shooter, float damage, int knockback) {
						AbstractArrow entityToSpawn = new FakerggShootEntity(CaerulaArborModEntities.FAKERGG_SHOOT.get(), level);
						entityToSpawn.setOwner(shooter);
						entityToSpawn.setBaseDamage(damage);
						entityToSpawn.setKnockback(knockback);
						entityToSpawn.setSilent(true);
						entityToSpawn.setCritArrow(true);
						return entityToSpawn;
					}
				}.getArrow(projectileLevel, this, (float) dama, 0);
				_entityToSpawn.setPos(tx, ty, tz);
				_entityToSpawn.shoot(0, (-1), 0, 1, (float) 0.1);
				projectileLevel.addFreshEntity(_entityToSpawn);
			}
		}
	}
}
