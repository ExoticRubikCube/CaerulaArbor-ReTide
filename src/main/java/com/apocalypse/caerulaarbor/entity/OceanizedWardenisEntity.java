package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.entity.base.SeaMonster;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import net.minecraft.client.Minecraft;
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
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
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
import net.minecraft.world.level.GameType;
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
import java.util.List;

public class OceanizedWardenisEntity extends SeaMonster {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(OceanizedWardenisEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(OceanizedWardenisEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(OceanizedWardenisEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_skillp1 = SynchedEntityData.defineId(OceanizedWardenisEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_skillp2 = SynchedEntityData.defineId(OceanizedWardenisEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_duration = SynchedEntityData.defineId(OceanizedWardenisEntity.class, EntityDataSerializers.INT);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";
	private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.BLUE, ServerBossEvent.BossBarOverlay.NOTCHED_6);

	public OceanizedWardenisEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CaerulaArborModEntities.OCEANIZED_WARDENIS.get(), world);
	}

	public OceanizedWardenisEntity(EntityType<OceanizedWardenisEntity> type, Level world) {
		super(type, world);
		xpReward = 1024;
		setNoAi(false);
		setMaxUpStep(0.6f);
		setPersistenceRequired();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "oceanized_wardenis");
		this.entityData.define(DATA_skillp1, 100);
		this.entityData.define(DATA_skillp2, 120);
		this.entityData.define(DATA_duration, 0);
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
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 2, true) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 5.0625;
			}

			@Override
			public boolean canUse() {
				double x = OceanizedWardenisEntity.this.getX();
				double y = OceanizedWardenisEntity.this.getY();
				double z = OceanizedWardenisEntity.this.getZ();
				Entity entity = OceanizedWardenisEntity.this;
				Level world = OceanizedWardenisEntity.this.level();
				return super.canUse() && OceanizedWardenisEntity.this.isDurative();
			}

			@Override
			public boolean canContinueToUse() {
				double x = OceanizedWardenisEntity.this.getX();
				double y = OceanizedWardenisEntity.this.getY();
				double z = OceanizedWardenisEntity.this.getZ();
				Entity entity = OceanizedWardenisEntity.this;
				Level world = OceanizedWardenisEntity.this.level();
				return super.canContinueToUse() && OceanizedWardenisEntity.this.isDurative();
			}

		});
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, true, false));
		this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1) {
			@Override
			public boolean canUse() {
				double x = OceanizedWardenisEntity.this.getX();
				double y = OceanizedWardenisEntity.this.getY();
				double z = OceanizedWardenisEntity.this.getZ();
				Entity entity = OceanizedWardenisEntity.this;
				Level world = OceanizedWardenisEntity.this.level();
				return super.canUse() && OceanizedWardenisEntity.this.isDurative();
			}

			@Override
			public boolean canContinueToUse() {
				double x = OceanizedWardenisEntity.this.getX();
				double y = OceanizedWardenisEntity.this.getY();
				double z = OceanizedWardenisEntity.this.getZ();
				Entity entity = OceanizedWardenisEntity.this;
				Level world = OceanizedWardenisEntity.this.level();
				return super.canContinueToUse() && OceanizedWardenisEntity.this.isDurative();
			}
		});
		this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, (float) 9));
		this.goalSelector.addGoal(6, new RandomLookAroundGoal(this) {
			@Override
			public boolean canUse() {
				double x = OceanizedWardenisEntity.this.getX();
				double y = OceanizedWardenisEntity.this.getY();
				double z = OceanizedWardenisEntity.this.getZ();
				Entity entity = OceanizedWardenisEntity.this;
				Level world = OceanizedWardenisEntity.this.level();
				return super.canUse() && OceanizedWardenisEntity.this.isDurative();
			}

			@Override
			public boolean canContinueToUse() {
				double x = OceanizedWardenisEntity.this.getX();
				double y = OceanizedWardenisEntity.this.getY();
				double z = OceanizedWardenisEntity.this.getZ();
				Entity entity = OceanizedWardenisEntity.this;
				Level world = OceanizedWardenisEntity.this.level();
				return super.canContinueToUse() && OceanizedWardenisEntity.this.isDurative();
			}
		});
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
	public SoundEvent getAmbientSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "wardenis_idle"));
	}

	@Override
	public void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.warden.step")), 0.15f, 1);
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "wardenis_hurt"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "wardenis_die"));
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
							ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.warden.attack_impact")), SoundSource.HOSTILE,
							(float) Mth.nextDouble(RandomSource.create(), 0.9, 1.1), 1);
					EntityUtils.wardenRangedAttack(this.level(), this, false, 1, targetX, targetY, targetZ);
				}
			});
		}
		return true;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.IN_FIRE))
			return false;
		if (source.is(DamageTypes.FALL))
			return false;
		if (source.is(DamageTypes.DROWN))
			return false;
		return super.hurt(source, amount);
	}

	@Override
	public void die(DamageSource source) {
		super.die(source);
		this.triggerWardenDeathEffect();
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
		EntityUtils.initWardenAttributes(this);
		this.setAnimation("animation.oceanized_wardenis.start");
		this.getEntityData().set(DATA_duration, 80);
		if (!this.level().isClientSide())
			this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 80, 5, false, false));
		return retval;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putInt("Dataskillp1", this.entityData.get(DATA_skillp1));
		compound.putInt("Dataskillp2", this.entityData.get(DATA_skillp2));
		compound.putInt("Dataduration", this.entityData.get(DATA_duration));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("Dataskillp1"))
			this.entityData.set(DATA_skillp1, compound.getInt("Dataskillp1"));
		if (compound.contains("Dataskillp2"))
			this.entityData.set(DATA_skillp2, compound.getInt("Dataskillp2"));
		if (compound.contains("Dataduration"))
			this.entityData.set(DATA_duration, compound.getInt("Dataduration"));
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
            double sklp2 = 0;
            double perc = 0;
            double dura = 0;
            double gap = 0;
            if (this.isAlive()) {
                if (tickCount % 100 == 0) {
                    {
                        final Vec3 _center = new Vec3(x, y, z);
                        List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(24 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                        for (Entity entityiterator : _entfound) {
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
                            }.checkGamemode(entityiterator) || new Object() {
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
                            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                                continue;
                            }
                            if (!(entityiterator instanceof LivingEntity _livEnt5 && _livEnt5.hasEffect(MobEffects.DARKNESS))) {
                                if (entityiterator instanceof LivingEntity _entity && !this.level().isClientSide())
                                    this.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 1200, 0, false, false));
                            }
                        }
                    }
                }
                sklp1 = (Entity) this instanceof OceanizedWardenisEntity _datEntI ? _datEntI.getEntityData().get(DATA_skillp1) : 0;
                sklp2 = (Entity) this instanceof OceanizedWardenisEntity _datEntI ? _datEntI.getEntityData().get(DATA_skillp2) : 0;
                dura = (Entity) this instanceof OceanizedWardenisEntity _datEntI ? _datEntI.getEntityData().get(DATA_duration) : 0;
                enemy = (Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null;
                if (dura > 0) {
                    if ((Entity) this instanceof OceanizedWardenisEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_duration, (int) (dura - 1));
                }
                if (sklp1 > 0) {
                    if ((Entity) this instanceof OceanizedWardenisEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_skillp1, (int) (sklp1 - 1));
                } else {
                    if (!(enemy == null) && enemy.isAlive()) {
                        if (!((enemy != null ? distanceTo(enemy) : -1) > 32 || dura > 0)) {
                            if ((Entity) this instanceof OceanizedWardenisEntity _datEntSetI)
                                _datEntSetI.getEntityData().set(DATA_duration, 45);
                            if (this instanceof OceanizedWardenisEntity) {
                                ((OceanizedWardenisEntity) this).setAnimation("animation.oceanized_wardenis.sonic");
                            }
                            if (!this.level().isClientSide())
                                this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 45, 0, false, false));
                            if (!this.level().isClientSide())
                                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 45, 9, false, false));
                            ((Entity) this).lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((enemy.getX()), (enemy.getY()), (enemy.getZ())));
                            if (world instanceof Level _level) {
                                if (!_level.isClientSide()) {
                                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.warden.sonic_charge")), SoundSource.HOSTILE, 2, 1);
                                } else {
                                    _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.warden.sonic_charge")), SoundSource.HOSTILE, 2, 1, false);
                                }
                            }
                            CaerulaArborMod.queueServerWork(30, () -> {
                                if (this.isAlive()) {
                                    if (world instanceof Level _level) {
                                        if (!_level.isClientSide()) {
                                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.warden.sonic_boom")), SoundSource.HOSTILE, 2, 1);
                                        } else {
                                            _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.warden.sonic_boom")), SoundSource.HOSTILE, 2, 1, false);
                                        }
                                    }
                                    EntityUtils.wardenSonicBoom(world, this, (Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null);
                                }
                            });
                            if ((Entity) this instanceof OceanizedWardenisEntity _datEntSetI)
                                _datEntSetI.getEntityData().set(DATA_skillp1, 200);
                        }
                    }
                }
                if (sklp2 > 0) {
                    if ((Entity) this instanceof OceanizedWardenisEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_skillp2, (int) (sklp2 - 1));
                } else {
                    if (!(enemy == null) && enemy.isAlive()) {
                        if (!((enemy != null ? distanceTo(enemy) : -1) > 4 || dura > 0)) {
                            if ((Entity) this instanceof OceanizedWardenisEntity _datEntSetI)
                                _datEntSetI.getEntityData().set(DATA_duration, 45);
                            if (this instanceof OceanizedWardenisEntity) {
                                ((OceanizedWardenisEntity) this).setAnimation("animation.oceanized_wardenis.combo");
                            }
                            ((Entity) this).lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((enemy.getX()), (enemy.getY()), (enemy.getZ())));
                            CaerulaArborMod.queueServerWork(12, () -> {
                                if (this.isAlive() && !(((Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) == null)) {
                                    if ((((Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) != null ? distanceTo(((Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null)) : -1) <= 4) {
                                        ((Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null).hurt(
                                                new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "warden_attack"))), this),
                                                (float) ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0)
                                                        * 1.5));
                                    }
                                    ((Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null).push(0, 1.25, 0);
                                }
                            });
                            CaerulaArborMod.queueServerWork(20, () -> {
                                if (world instanceof Level _level) {
                                    if (!_level.isClientSide()) {
                                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.warden.sonic_charge")), SoundSource.HOSTILE, 2, 1);
                                    } else {
                                        _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.warden.sonic_charge")), SoundSource.HOSTILE, 2, 1, false);
                                    }
                                }
                                if (!this.level().isClientSide())
                                    this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 25, 9, false, false));
                            });
                            CaerulaArborMod.queueServerWork(27, () -> {
                                if (world instanceof Level _level) {
                                    if (!_level.isClientSide()) {
                                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.warden.sonic_boom")), SoundSource.HOSTILE, 2, 1);
                                    } else {
                                        _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.warden.sonic_boom")), SoundSource.HOSTILE, 2, 1, false);
                                    }
                                }
                                EntityUtils.wardenLightBoom(world, this, (Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null);
                            });
                            if ((Entity) this instanceof OceanizedWardenisEntity _datEntSetI)
                                _datEntSetI.getEntityData().set(DATA_skillp2, 300);
                        }
                    }
                }
                if (sklp1 < 60) {
                    gap = 10;
                } else if (sklp1 < 100) {
                    gap = 20;
                }
                if (sklp2 < 60) {
                    gap = 10;
                } else if (sklp2 < 100) {
                    gap = 20;
                }
                if (gap > 0 && !(enemy == null) && enemy.isAlive()) {
                    if (tickCount % gap == 0) {
                        if (world instanceof Level _level) {
                            if (!_level.isClientSide()) {
                                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.warden.heartbeat")), SoundSource.HOSTILE, 2, Mth.nextInt(RandomSource.create(), (int) 0.9, (int) 1.05));
                            } else {
                                _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.warden.heartbeat")), SoundSource.HOSTILE, 2, Mth.nextInt(RandomSource.create(), (int) 0.9, (int) 1.05), false);
                            }
                        }
                    }
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
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.15);
		builder = builder.add(Attributes.MAX_HEALTH, 825);
		builder = builder.add(Attributes.ARMOR, 10);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 50);
		builder = builder.add(Attributes.FOLLOW_RANGE, 48);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

					&& !this.isAggressive()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_wardenis.move"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_wardenis.die"));
			}
			if (this.isAggressive() && event.isMoving()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_wardenis.sprint"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_wardenis.idle"));
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
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_wardenis.attack"));
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
		if (this.deathTime == 50) {
			this.remove(OceanizedWardenisEntity.RemovalReason.KILLED);
			this.dropExperience();
			EntityUtils.dropWardenExp(this.level(), this.getX(), this.getY(), this.getZ());
		}
	}

	@Override
	public void remove(RemovalReason pReason){
		if(this.level().getDifficulty() != Difficulty.PEACEFUL && pReason == RemovalReason.DISCARDED){
			this.hurt(
				new DamageSource(
					this.level().registryAccess().
					registryOrThrow(Registries.DAMAGE_TYPE).
					getHolderOrThrow(
						ResourceKey.create(
							Registries.DAMAGE_TYPE, 
							new ResourceLocation(CaerulaArborMod.MODID, "oceankiller_damage")
						)
					)
				),
				20
			);
			return;
		}
		super.remove(pReason);
	}

	@Override
    public void setHealth(float pHealth){
        float hlth = this.getHealth();
        float mhlth = this.getMaxHealth();
        if(this.hasEffect(CaerulaArborModMobEffects.INVULNERABLE.get()) && pHealth < hlth) return;
        float reduction = hlth - pHealth;
        super.setHealth(reduction >= mhlth * 0.3f ? hlth - mhlth * 0.3f : hlth - reduction);
    }

	public String getSyncedAnimation() {
		return this.entityData.get(ANIMATION);
	}

	public void setAnimation(String animation) {
		this.entityData.set(ANIMATION, animation);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		data.add(new AnimationController<>(this, "movement", 1, this::movementPredicate));
		data.add(new AnimationController<>(this, "attacking", 1, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 1, this::procedurePredicate));
	}

	private boolean isDurative() {
		return getEntityData().get(DATA_duration) <= 0;
	}

	private void triggerWardenDeathEffect() {
		LevelAccessor world = this.level();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();

		CaerulaArborMod.queueServerWork(10, () -> {
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.warden.sonic_charge")), SoundSource.HOSTILE, (float) 0.1, 1);
				} else {
					_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.warden.sonic_charge")), SoundSource.HOSTILE, (float) 0.1, 1, false);
				}
			}
			for (int index0 = 0; index0 < 3; index0++) {
				double t = Mth.nextDouble(RandomSource.create(), 0, 6.283);
				double p = Mth.nextDouble(RandomSource.create(), 0, 6.283);
				double d = 4;
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.SONIC_BOOM, (x + d * Math.cos(t) * Math.cos(p)), (y + d * Math.sin(p)), (z + d * Math.sin(t) * Math.cos(p)), 2, 0, 0, 0, 0.1);
			}
		});

		CaerulaArborMod.queueServerWork(20, () -> {
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.warden.sonic_charge")), SoundSource.HOSTILE, (float) 0.2, 1);
				} else {
					_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.warden.sonic_charge")), SoundSource.HOSTILE, (float) 0.2, 1, false);
				}
			}
			for (int index1 = 0; index1 < 5; index1++) {
				double t = Mth.nextDouble(RandomSource.create(), 0, 6.283);
				double p = Mth.nextDouble(RandomSource.create(), 0, 6.283);
				double d = 4;
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.SONIC_BOOM, (x + d * Math.cos(t) * Math.cos(p)), (y + d * Math.sin(p)), (z + d * Math.sin(t) * Math.cos(p)), 2, 0, 0, 0, 0.1);
			}
		});

		CaerulaArborMod.queueServerWork(32, () -> {
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.warden.sonic_charge")), SoundSource.HOSTILE, (float) 0.3, 1);
				} else {
					_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.warden.sonic_charge")), SoundSource.HOSTILE, (float) 0.3, 1, false);
				}
			}
			for (int index2 = 0; index2 < 9; index2++) {
				double t = Mth.nextDouble(RandomSource.create(), 0, 6.283);
				double p = Mth.nextDouble(RandomSource.create(), 0, 6.283);
				double d = 4;
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.SONIC_BOOM, (x + d * Math.cos(t) * Math.cos(p)), (y + d * Math.sin(p)), (z + d * Math.sin(t) * Math.cos(p)), 2, 0, 0, 0, 0.1);
			}
		});

		CaerulaArborMod.queueServerWork(35, () -> {
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.warden.sonic_charge")), SoundSource.HOSTILE, (float) 0.4, 1);
				} else {
					_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.warden.sonic_charge")), SoundSource.HOSTILE, (float) 0.4, 1, false);
				}
			}
			for (int index3 = 0; index3 < 9; index3++) {
				double t = Mth.nextDouble(RandomSource.create(), 0, 6.283);
				double p = Mth.nextDouble(RandomSource.create(), 0, 6.283);
				double d = 4;
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.SONIC_BOOM, (x + d * Math.cos(t) * Math.cos(p)), (y + d * Math.sin(p)), (z + d * Math.sin(t) * Math.cos(p)), 2, 0, 0, 0, 0.1);
			}
		});

		CaerulaArborMod.queueServerWork(38, () -> {
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.warden.sonic_charge")), SoundSource.HOSTILE, (float) 0.5, 1);
				} else {
					_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.warden.sonic_charge")), SoundSource.HOSTILE, (float) 0.5, 1, false);
				}
			}
			for (int index4 = 0; index4 < 9; index4++) {
				double t = Mth.nextDouble(RandomSource.create(), 0, 6.283);
				double p = Mth.nextDouble(RandomSource.create(), 0, 6.283);
				double d = 4;
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.SONIC_BOOM, (x + d * Math.cos(t) * Math.cos(p)), (y + d * Math.sin(p)), (z + d * Math.sin(t) * Math.cos(p)), 2, 0, 0, 0, 0.1);
			}
		});

		CaerulaArborMod.queueServerWork(40, () -> {
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.warden.sonic_charge")), SoundSource.HOSTILE, 2, 1);
				} else {
					_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.warden.sonic_charge")), SoundSource.HOSTILE, 2, 1, false);
				}
			}
		});

		CaerulaArborMod.queueServerWork(47, () -> {
			boolean hasSound = false;
			final Vec3 center = new Vec3(x, y, z);
			List<Entity> nearbyEntities = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(32), e -> true).stream()
					.sorted(Comparator.comparingDouble(ent -> ent.distanceToSqr(center)))
					.toList();

			for (Entity entityiterator : nearbyEntities) {
				if (!(entityiterator instanceof Mob) && !(entityiterator instanceof Player)) {
					continue;
				}
				if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
					continue;
				}
				if (entityiterator == OceanizedWardenisEntity.this) {
					continue;
				}
				if (isCreativePlayer(entityiterator)) {
					continue;
				}
				if (this.distanceTo(entityiterator) <= 32) {
					EntityUtils.wardenSonicBoom(world, this, entityiterator);
					hasSound = true;
				}
			}

			if (hasSound) {
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.warden.sonic_boom")), SoundSource.HOSTILE, 22, 1);
					} else {
						_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.warden.sonic_boom")), SoundSource.HOSTILE, 22, 1, false);
					}
				}
			}
		});
	}

	private boolean isCreativePlayer(Entity entity) {
		if (entity instanceof ServerPlayer serverPlayer) {
			return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
		}
		if (entity.level().isClientSide() && entity instanceof Player player) {
			var connection = Minecraft.getInstance().getConnection();
			var playerInfo = connection == null ? null : connection.getPlayerInfo(player.getGameProfile().getId());
			return playerInfo != null && playerInfo.getGameMode() == GameType.CREATIVE;
		}
		return false;
	}
}
