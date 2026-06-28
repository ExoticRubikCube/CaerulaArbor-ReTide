package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.utils.EntityPredicateUtils;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
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
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class HighmoreEntity extends SeaMonster implements RangedAttackMob {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(HighmoreEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(HighmoreEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(HighmoreEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_phase = SynchedEntityData.defineId(HighmoreEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_skillp1 = SynchedEntityData.defineId(HighmoreEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_skillp2 = SynchedEntityData.defineId(HighmoreEntity.class, EntityDataSerializers.INT);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";
	private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.YELLOW, ServerBossEvent.BossBarOverlay.NOTCHED_10);

	public HighmoreEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CaerulaArborModEntities.HIGHMORE.get(), world);
	}

	public HighmoreEntity(EntityType<HighmoreEntity> type, Level world) {
		super(type, world);
		xpReward = 64;
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
		this.entityData.define(TEXTURE, "highmore");
		this.entityData.define(DATA_phase, 0);
		this.entityData.define(DATA_skillp1, 200);
		this.entityData.define(DATA_skillp2, 100);
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
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this) {
			@Override
			public boolean canUse() {
				double x = HighmoreEntity.this.getX();
				double y = HighmoreEntity.this.getY();
				double z = HighmoreEntity.this.getZ();
				Entity entity = HighmoreEntity.this;
				Level world = HighmoreEntity.this.level();
                if (!super.canUse()) return false;
                return EntityPredicateUtils.isNotFakeDying(entity);
			}

			@Override
			public boolean canContinueToUse() {
				double x = HighmoreEntity.this.getX();
				double y = HighmoreEntity.this.getY();
				double z = HighmoreEntity.this.getZ();
				Entity entity = HighmoreEntity.this;
				Level world = HighmoreEntity.this.level();
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
				double x = HighmoreEntity.this.getX();
				double y = HighmoreEntity.this.getY();
				double z = HighmoreEntity.this.getZ();
				Entity entity = HighmoreEntity.this;
				Level world = HighmoreEntity.this.level();
				return super.canUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
			}

			@Override
			public boolean canContinueToUse() {
				double x = HighmoreEntity.this.getX();
				double y = HighmoreEntity.this.getY();
				double z = HighmoreEntity.this.getZ();
				Entity entity = HighmoreEntity.this;
				Level world = HighmoreEntity.this.level();
				return super.canContinueToUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
			}
		});
		this.targetSelector.addGoal(13, new NearestAttackableTargetGoal(this, Animal.class, true, false) {
			@Override
			public boolean canUse() {
				double x = HighmoreEntity.this.getX();
				double y = HighmoreEntity.this.getY();
				double z = HighmoreEntity.this.getZ();
				Entity entity = HighmoreEntity.this;
				Level world = HighmoreEntity.this.level();
				return super.canUse() && EntityUtils.canAttackAnimals();
			}

			@Override
			public boolean canContinueToUse() {
				double x = HighmoreEntity.this.getX();
				double y = HighmoreEntity.this.getY();
				double z = HighmoreEntity.this.getZ();
				Entity entity = HighmoreEntity.this;
				Level world = HighmoreEntity.this.level();
				return super.canContinueToUse() && EntityUtils.canAttackAnimals();
			}
		});
		this.goalSelector.addGoal(14, new RandomStrollGoal(this, 0.5, 20) {
			@Override
			protected Vec3 getPosition() {
				RandomSource random = HighmoreEntity.this.getRandom();
				double dir_x = HighmoreEntity.this.getX() + ((random.nextFloat() * 2 - 1) * 16);
				double dir_y = HighmoreEntity.this.getY() + ((random.nextFloat() * 2 - 1) * 16);
				double dir_z = HighmoreEntity.this.getZ() + ((random.nextFloat() * 2 - 1) * 16);
				return new Vec3(dir_x, dir_y, dir_z);
			}

			@Override
			public boolean canUse() {
				double x = HighmoreEntity.this.getX();
				double y = HighmoreEntity.this.getY();
				double z = HighmoreEntity.this.getZ();
				Entity entity = HighmoreEntity.this;
				Level world = HighmoreEntity.this.level();
                if (!super.canUse()) return false;
                return EntityPredicateUtils.isNotFakeDying(entity);
            }

			@Override
			public boolean canContinueToUse() {
				double x = HighmoreEntity.this.getX();
				double y = HighmoreEntity.this.getY();
				double z = HighmoreEntity.this.getZ();
				Entity entity = HighmoreEntity.this;
				Level world = HighmoreEntity.this.level();
                if (!super.canContinueToUse()) return false;
                return EntityPredicateUtils.isNotFakeDying(entity);
            }

		});
		this.goalSelector.addGoal(15, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(1, new HighmoreEntity.RangedAttackGoal(this, 1.25, 80, 19f) {
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
				return true;
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
			((HighmoreEntity) rangedAttackMob).entityData.set(SHOOT, false);
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
					((HighmoreEntity) rangedAttackMob).entityData.set(SHOOT, false);
					return;
				}
				((HighmoreEntity) rangedAttackMob).entityData.set(SHOOT, true);
				float f = (float) Math.sqrt(d0) / this.attackRadius;
				float f1 = Mth.clamp(f, 0.1F, 1.0F);
				this.rangedAttackMob.performRangedAttack(this.target, f1);
				this.attackTime = Mth.floor(f * (float) (this.attackIntervalMax - this.attackIntervalMin) + (float) this.attackIntervalMin);
			} else if (this.attackTime < 0) {
				this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(d0) / (double) this.attackRadius, (double) this.attackIntervalMin, (double) this.attackIntervalMax));
			} else
				((HighmoreEntity) rangedAttackMob).entityData.set(SHOOT, false);
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
	public SoundEvent getAmbientSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.axolotl.idle_water"));
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "highmore_hit"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "highmore_death"));
	}

	@Override
	public boolean causeFallDamage(float l, float d, DamageSource source) {
		return false;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
        LevelAccessor world = this.level();
        if (this != null) {
            for (Entity entityiterator : new ArrayList<>(world.players())) {
                if ((level().dimension()) == (entityiterator.level().dimension())) {
                    if (entityiterator instanceof ServerPlayer _player) {
                        Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "speechless_break"));
                        AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
                        if (!_ap.isDone()) {
                            for (String criteria : _ap.getRemainingCriteria())
                                _player.getAdvancements().award(_adv, criteria);
                        }
                    }
                }
            }
        }
        if (source.is(DamageTypes.DROWN))
			return false;
		return super.hurt(source, amount);
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
        if (this != null) {
            if (this instanceof HighmoreEntity) {
                ((HighmoreEntity) this).setAnimation("animation.highmore.start");
            }
        }
        return retval;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putInt("Dataphase", this.entityData.get(DATA_phase));
		compound.putInt("Dataskillp1", this.entityData.get(DATA_skillp1));
		compound.putInt("Dataskillp2", this.entityData.get(DATA_skillp2));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("Dataphase"))
			this.entityData.set(DATA_phase, compound.getInt("Dataphase"));
		if (compound.contains("Dataskillp1"))
			this.entityData.set(DATA_skillp1, compound.getInt("Dataskillp1"));
		if (compound.contains("Dataskillp2"))
			this.entityData.set(DATA_skillp2, compound.getInt("Dataskillp2"));
	}

	@Override
	public void baseTick() {
		super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        if (this != null) {
            double range = 0;
            double lvl = 0;
            double sklp1 = 0;
            double sklp2 = 0;
            if (((Entity) this instanceof HighmoreEntity _datEntI ? _datEntI.getEntityData().get(DATA_phase) : 0) == 0) {
                range = 7;
            } else if (((Entity) this instanceof HighmoreEntity _datEntI ? _datEntI.getEntityData().get(DATA_phase) : 0) == 1) {
                range = 11;
                lvl = 1;
            } else {
                range = 17;
                lvl = 2;
            }
            sklp1 = (Entity) this instanceof HighmoreEntity _datEntI ? _datEntI.getEntityData().get(DATA_skillp1) : 0;
            sklp2 = (Entity) this instanceof HighmoreEntity _datEntI ? _datEntI.getEntityData().get(DATA_skillp2) : 0;
            if (this.isAlive() && !((Entity) this instanceof LivingEntity _livEnt5 && _livEnt5.hasEffect(CaerulaArborModMobEffects.FAKE_DEATH.get()))) {
                assert Boolean.TRUE; //#dbg:HighmoreRim:marker1
                for (int index0 = 0; index0 < 120; index0++) {
                    if (world instanceof ServerLevel _level)
                        _level.sendParticles(ParticleTypes.DOLPHIN, (x + range * Math.sin(Math.toRadians(3 * index0))), y, (z + range * Math.cos(Math.toRadians(3 * index0))), 6, 0.15, 0.2, 0.15, 0.1);
                }
                {
                    final Vec3 _center = new Vec3(x, (y + 1.5), z);
                    List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate((range * 2) / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                    for (Entity entityiterator : _entfound) {
                        if (entityiterator == this) {
                            continue;
                        }
                        if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                            if (!(entityiterator == ((Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null))) {
                                continue;
                            }
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
                        if ((entityiterator != null ? distanceTo(entityiterator) : -1) <= range) {
                            if (!((Entity) this instanceof LivingEntity _livEnt13 && _livEnt13.hasEffect(CaerulaArborModMobEffects.FADINGSHADOW.get()))) {
                                if (entityiterator instanceof LivingEntity _entity && !this.level().isClientSide())
                                    this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.FADINGSHADOW.get(), 20, (int) lvl, false, false));
                                if (entityiterator instanceof LivingEntity _entity && !this.level().isClientSide())
                                    this.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 20, (int) lvl, false, true));
                            }
                        }
                    }
                }
                if (sklp1 <= 0 && !((Entity) this instanceof LivingEntity _livEnt17 && _livEnt17.hasEffect(CaerulaArborModMobEffects.COOLDOWN_SINAL.get()))) {
                    if (!(null == ((Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null))) {
                        if (this instanceof HighmoreEntity) {
                            ((HighmoreEntity) this).setAnimation("animation.highmore.skill");
                        }
                        if (world instanceof Level _level) {
                                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "highmore_preamble")), SoundSource.HOSTILE, 4, 1);
                        }
                        if (lvl == 0) {
                            CaerulaArborMod.queueServerWork(15, () -> {
                                if (!(null == ((Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null))) {
                                    this.multiShoot(world, x, y, z, (Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null, 3);
                                }
                            });
                            if ((Entity) this instanceof HighmoreEntity _datEntSetI)
                                _datEntSetI.getEntityData().set(DATA_skillp1, 200);
                        } else {
                            CaerulaArborMod.queueServerWork(15, () -> {
                                if (!(null == ((Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null))) {
                                    this.multiShoot(world, x, y, z, (Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null, 5);
                                }
                            });
                            if ((Entity) this instanceof HighmoreEntity _datEntSetI)
                                _datEntSetI.getEntityData().set(DATA_skillp1, 140);
                        }
                    }
                } else {
                    if ((Entity) this instanceof HighmoreEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_skillp1, (int) (sklp1 - 1));
                }
                if (sklp2 <= 0) {
                    if (!(null == ((Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null))) {
                        if (this instanceof HighmoreEntity) {
                            ((HighmoreEntity) this).setAnimation("animation.highmore.charge");
                        }
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.COOLDOWN_SINAL.get(), 80, 0, false, false));
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 80, 0, false, false));
                        CaerulaArborMod.queueServerWork(15, () -> {
                            push((Mth.nextDouble(RandomSource.create(), -0.3, 0.3)), (Mth.nextDouble(RandomSource.create(), 0, 0.3)), (Mth.nextDouble(RandomSource.create(), -0.3, 0.3)));
                            new Object() {
                                void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
									if (HighmoreEntity.this != null) {
										double angl = 0;
										double rng = 0;
										angl = Mth.nextDouble(RandomSource.create(), 0, 6.283);
										rng = Mth.nextDouble(RandomSource.create(), 5, 17);
										if (world instanceof ServerLevel _level)
											_level.sendParticles(ParticleTypes.END_ROD, x, (y + 1), z, 72, 2, 2, 2, 0.2);
										if (world instanceof ServerLevel projectileLevel) {
											Projectile _entityToSpawn = new Object() {
												public Projectile getArrow(Level level, Entity shooter, float damage, int knockback) {
													AbstractArrow entityToSpawn = new HighmoreShootEntity(CaerulaArborModEntities.HIGHMORE_SHOOT.get(), level);
													entityToSpawn.setOwner(shooter);
													entityToSpawn.setBaseDamage(damage);
													entityToSpawn.setKnockback(knockback);
													entityToSpawn.setSilent(true);
													return entityToSpawn;
												}
											}.getArrow(projectileLevel, (Entity) HighmoreEntity.this,
													(float) (((Entity) HighmoreEntity.this instanceof LivingEntity _livingEntity3 && _livingEntity3.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity3.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 1.5), 0);
											_entityToSpawn.setPos(x, (y + 9), z);
											_entityToSpawn.shoot((rng * Math.sin(angl)), (-9), (rng * Math.cos(angl)), (float) 1.5, 5);
											projectileLevel.addFreshEntity(_entityToSpawn);
										}
										for (int index0 = 0; index0 < 120; index0++) {
											if (world instanceof ServerLevel _level)
												_level.sendParticles(ParticleTypes.ENCHANTED_HIT, (x + 24 * Math.sin(Math.toRadians(3 * index0))), y, (z + 24 * Math.cos(Math.toRadians(3 * index0))), 3, 0.15, 0.15, 0.15, 0.1);
										}
									}
									final int tick2 = ticks;
                                    CaerulaArborMod.queueServerWork(tick2, () -> {
                                        if (timedlooptotal > timedloopiterator + 1) {
                                            timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                                        }
                                    });
                                }
                            }.timedLoop(0, 9, 3);
                        });
                        CaerulaArborMod.queueServerWork(65, () -> {
                            highmoreBlast(world, x, y, z, this);
                        });
                        if (lvl == 0) {
                            if ((Entity) this instanceof HighmoreEntity _datEntSetI)
                                _datEntSetI.getEntityData().set(DATA_skillp2, 680);
                        } else if (lvl == 1) {
                            if ((Entity) this instanceof HighmoreEntity _datEntSetI)
                                _datEntSetI.getEntityData().set(DATA_skillp2, 600);
                        } else {
                            if ((Entity) this instanceof HighmoreEntity _datEntSetI)
                                _datEntSetI.getEntityData().set(DATA_skillp2, 520);
                        }
                    }
                } else {
                    if ((Entity) this instanceof HighmoreEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_skillp2, (int) (sklp2 - 1));
                    if (((Entity) this instanceof HighmoreEntity _datEntI ? _datEntI.getEntityData().get(DATA_phase) : 0) >= 2) {
                        if ((Entity) this instanceof HighmoreEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_skillp2, (int) (sklp2 - 1));
                    }
                }
            }
        }
        this.refreshDimensions();
	}

	@Override
	public EntityDimensions getDimensions(Pose p_33597_) {
		return super.getDimensions(p_33597_).scale((float) 2);
	}

	@Override
	public void performRangedAttack(LivingEntity target, float flval) {
		HighmoreShootEntity.shoot(this, target, (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttributeValue(Attributes.ATTACK_DAMAGE) : 0) * (3.5 / 6.0));
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

	public static void init() {
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.6);
		builder = builder.add(Attributes.MAX_HEALTH, 210);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 6);
		builder = builder.add(Attributes.FOLLOW_RANGE, 48);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
		builder = builder.add(Attributes.FLYING_SPEED, 0.6);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) && this.onGround()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.highmore.move"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.highmore.die"));
			}
			if (!this.onGround()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.highmore.move"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.highmore.idle"));
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
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.highmore.attack"));
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
		if (this.deathTime == 30) {
			this.remove(HighmoreEntity.RemovalReason.KILLED);
			this.dropExperience();
            LevelAccessor world = this.level();
            if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                if (!world.isClientSide() && world.getServer() != null) {
                    for (ItemStack itemstackiterator : world.getServer().getLootData().getLootTable(new ResourceLocation(CaerulaArborMod.MODID, "gameplay/highmore_relics"))
                            .getRandomItems(new LootParams.Builder((ServerLevel) world).create(LootContextParamSets.EMPTY))) {
                        if (world instanceof ServerLevel _level) {
                            ItemEntity entityToSpawn = new ItemEntity(_level, this.getX(), this.getY(), this.getZ(), itemstackiterator);
                            entityToSpawn.setPickUpDelay(10);
                            entityToSpawn.setUnlimitedLifetime();
                            _level.addFreshEntity(entityToSpawn);
                        }
                    }
                }
            }
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
        if(this.hasEffect(CaerulaArborModMobEffects.INVULNERABLE.get()) && pHealth < this.getHealth()) return;
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
		data.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
		data.add(new AnimationController<>(this, "attacking", 4, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
	}

	public void multiShoot(LevelAccessor world, double x, double y, double z, Entity target, double t) {
		if (target == null)
			return;
		new Object() {
			void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
				if (world instanceof ServerLevel projectileLevel) {
					Projectile _entityToSpawn = new Object() {
						public Projectile getArrow(Level level, Entity shooter, float damage, int knockback) {
							AbstractArrow entityToSpawn = new HighmoreShootEntity(CaerulaArborModEntities.HIGHMORE_SHOOT.get(), level);
							entityToSpawn.setOwner(shooter);
							entityToSpawn.setBaseDamage(damage);
							entityToSpawn.setKnockback(knockback);
							entityToSpawn.setSilent(true);
							return entityToSpawn;
						}
					}.getArrow(projectileLevel, HighmoreEntity.this,
							(float) (HighmoreEntity.this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? HighmoreEntity.this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0), 0);
					_entityToSpawn.setPos(x, (y + 1.5), z);
					_entityToSpawn.shoot((target.getX() - x), ((target.getY() + target.getBbHeight() * 0.5) - (y + 2.5)), (target.getZ() - z), (float) 1.5, 5);
					projectileLevel.addFreshEntity(_entityToSpawn);
				}
				final int tick2 = ticks;
				CaerulaArborMod.queueServerWork(tick2, () -> {
					if (timedlooptotal > timedloopiterator + 1) {
						timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
					}
				});
			}
		}.timedLoop(0, (int) t, 3);
	}

	private void highmoreBlast(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity != null)
		{
			final Vec3 _center = new Vec3(x, y, z);
			List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(48 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
			for (Entity entityiterator : _entfound) {
				if (entityiterator == entity) {
					continue;
				}
				if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
					if (!(entityiterator == (entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null))) {
						continue;
					}
				}
				if (!(entityiterator instanceof Mob) && !(entityiterator instanceof Player)) {
					continue;
				}
				if (entity.distanceTo(entityiterator) <= 24) {
					new Object() {
						void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
							entityiterator.hurt(
									new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "highmore_attack"))), entity),
									(float) ((entity instanceof LivingEntity _livingEntity8 && _livingEntity8.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity8.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0)
											* (3.5 + (entity instanceof HighmoreEntity _datEntI ? _datEntI.getEntityData().get(HighmoreEntity.DATA_phase) : 0))));
							final int tick2 = ticks;
							CaerulaArborMod.queueServerWork(tick2, () -> {
								if (timedlooptotal > timedloopiterator + 1) {
									timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
								}
							});
						}
					}.timedLoop(0, (int) (2 + (entity instanceof HighmoreEntity _datEntI ? _datEntI.getEntityData().get(HighmoreEntity.DATA_phase) : 0)), 1);
					if (entity instanceof LivingEntity _entity && !this.level().isClientSide())
						this.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 40, 0));
				}
			}
		}
		for (int index0 = 0; index0 < 120; index0++) {
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.ENCHANTED_HIT, (x + 24 * Math.sin(Math.toRadians(3 * index0))), y, (z + 24 * Math.cos(Math.toRadians(3 * index0))), 6, 0.15, 0.15, 0.15, 0.1);
		}
		new Object() {
			void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
				for (int index1 = 0; index1 < 12; index1++) {
					if (world instanceof ServerLevel projectileLevel) {
						Projectile _entityToSpawn = new Object() {
							public Projectile getArrow(Level level, Entity shooter, float damage, int knockback) {
								AbstractArrow entityToSpawn = new HighmoreShootEntity(CaerulaArborModEntities.HIGHMORE_SHOOT.get(), level);
								entityToSpawn.setOwner(shooter);
								entityToSpawn.setBaseDamage(damage);
								entityToSpawn.setKnockback(knockback);
								entityToSpawn.setSilent(true);
								return entityToSpawn;
							}
						}.getArrow(projectileLevel, entity,
								(float) ((entity instanceof LivingEntity _livingEntity17 && _livingEntity17.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity17.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 3.3), 0);
						_entityToSpawn.setPos(x, (y + 0.75), z);
						_entityToSpawn.shoot(Math.sin(30 * index1), 0, Math.cos(30 * index1), 1, 0);
						projectileLevel.addFreshEntity(_entityToSpawn);
					}
				}
				final int tick2 = ticks;
				CaerulaArborMod.queueServerWork(tick2, () -> {
					if (timedlooptotal > timedloopiterator + 1) {
						timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
					}
				});
			}
		}.timedLoop(0, (int) (1 + (entity instanceof HighmoreEntity _datEntI ? _datEntI.getEntityData().get(HighmoreEntity.DATA_phase) : 0)), 3);
	}
}
