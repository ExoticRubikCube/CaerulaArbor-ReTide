package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.api.event.SanityEvent;
import com.apocalypse.caerulaarbor.capability.sanity.SIHelper;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CAAttributes;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.init.CAParticleTypes;
import com.apocalypse.caerulaarbor.util.EntityPredicateUtils;
import com.apocalypse.caerulaarbor.util.WorldUtils;
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
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
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

public class ThirsterEntity extends SeaMonster {

	private boolean isThirsterDurative() {
		return EntityPredicateUtils.isThirsterDurative(this);
	}
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(ThirsterEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(ThirsterEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(ThirsterEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(ThirsterEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_SKILL_P = SynchedEntityData.defineId(ThirsterEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_INTEGRATION = SynchedEntityData.defineId(ThirsterEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_DIZZY_NUM = SynchedEntityData.defineId(ThirsterEntity.class, EntityDataSerializers.INT);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";
	private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.BLUE, ServerBossEvent.BossBarOverlay.PROGRESS);

	public ThirsterEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CAEntities.THIRSTER.get(), world);
	}

	public ThirsterEntity(EntityType<ThirsterEntity> type, Level world) {
		super(type, world);
		xpReward = 24;
		setNoAi(false);
		setMaxUpStep(1.25f);
		setPersistenceRequired();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "thirster");
		this.entityData.define(DATA_DURATION, 0);
		this.entityData.define(DATA_SKILL_P, 0);
		this.entityData.define(DATA_INTEGRATION, 0);
		this.entityData.define(DATA_DIZZY_NUM, 2);
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
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 4;
			}
		});
		this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1) {
			@Override
			public boolean canUse() {
				return super.canUse() && isThirsterDurative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && isThirsterDurative();
			}
		});
		this.goalSelector.addGoal(4, new RandomLookAroundGoal(this) {
			@Override
			public boolean canUse() {
				return super.canUse() && isThirsterDurative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && isThirsterDurative();
			}
		});
		this.goalSelector.addGoal(5, new FloatGoal(this));
	}

    @Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	public SoundEvent HURT = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "seaborn_generic_hit"));
	public SoundEvent DIE = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "seaborn_death"));
	
	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return HURT;
	}

	@Override
	public SoundEvent getDeathSound() {
		return DIE;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.DROWN))
			return false;
		double healthBeforeDamage = this.getHealth();
		boolean damaged = super.hurt(source, amount);
		if (damaged) {
			LevelAccessor world = this.level();
			double x = this.getX();
			double y = this.getY();
			double z = this.getZ();
			double duration = this.getEntityData().get(DATA_DURATION);
			double integration = this.getEntityData().get(DATA_INTEGRATION) + Math.max(1, amount);
			double maxHealth = this.getMaxHealth();
			this.getEntityData().set(DATA_INTEGRATION, (int) integration);
			if (integration >= maxHealth * 0.15 && duration <= 0) {
				double dizzyTargetCount = this.getEntityData().get(DATA_DIZZY_NUM);
				Entity currentTarget = this.getTarget();
				new Object() {
					void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
						double d = timedloopiterator * 4;
						for (int index0 = 0; index0 < 120; index0++) {
							double angle = index0 * 3;
							if (world instanceof ServerLevel _level)
								_level.sendParticles(ParticleTypes.CLOUD, (x + d * Math.sin(angle)), (y + 0.5), (z + d * Math.cos(angle)), 2, 0.1, 0.1, 0.1, 0.1);
						}
						final int tick2 = ticks;
						CaerulaArborMod.queueServerWork(tick2, () -> {
							if (timedlooptotal > timedloopiterator + 1) {
								timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
							}
						});
					}
				}.timedLoop(0, 5, 1);
				if (world instanceof Level _level) {
						_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "bishopfish_attack")), SoundSource.HOSTILE,
								(float) 2.5, 1);
				}
				final Vec3 _center = new Vec3(x, y, z);
				List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(40 / 2d), e -> true).stream()
						.sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
				for (Entity entityiterator : _entfound) {
					if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
						if (!(entityiterator == currentTarget)) {
							continue;
						}
					}
					if (!(entityiterator instanceof LivingEntity)) {
						continue;
					}
					if (entityiterator instanceof Player player && (player.isCreative() || player.isSpectator())) {
						continue;
					}
					if (this.distanceTo(entityiterator) < 20) {
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(CAMobEffects.DIZZY.get(), 160, 0, false, false));
						dizzyTargetCount = dizzyTargetCount - 1;
						if (dizzyTargetCount <= 1) {
							break;
						}
					}
				}
				this.getEntityData().set(DATA_INTEGRATION, 0);
				this.getEntityData().set(DATA_DURATION, 400);
				if (this.getAttributes().hasAttribute(CAAttributes.LIVING_BARRIER.get()))
					this.getAttribute(CAAttributes.LIVING_BARRIER.get()).setBaseValue((maxHealth - healthBeforeDamage));
			}
		}
		return damaged;
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
        if (this.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE.get()))
            this.getAttribute(CAAttributes.GENERAL_DEFENSE.get()).setBaseValue(10);
        if (this.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get()))
            this.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).setBaseValue(95);
        if (this.getAttributes().hasAttribute(CAAttributes.SANITY_RATE.get()))
            this.getAttribute(CAAttributes.SANITY_RATE.get()).setBaseValue(50);
        return retval;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putInt("DataDURATION", this.entityData.get(DATA_DURATION));
		compound.putInt("DataSKILL_P", this.entityData.get(DATA_SKILL_P));
		compound.putInt("DataINTEGRATION", this.entityData.get(DATA_INTEGRATION));
		compound.putInt("DataDIZZY_NUM", this.entityData.get(DATA_DIZZY_NUM));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("DataDURATION"))
			this.entityData.set(DATA_DURATION, compound.getInt("DataDURATION"));
		if (compound.contains("DataSKILL_P"))
			this.entityData.set(DATA_SKILL_P, compound.getInt("DataSKILL_P"));
		if (compound.contains("DataINTEGRATION"))
			this.entityData.set(DATA_INTEGRATION, compound.getInt("DataINTEGRATION"));
		if (compound.contains("DataDIZZY_NUM"))
			this.entityData.set(DATA_DIZZY_NUM, compound.getInt("DataDIZZY_NUM"));
	}

	@Override
	public void baseTick() {
		super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity enemy;
        double barr;
        double perc;
        double sklp1;
        double d;
        double maxH;
        double angle;
        double interg = 0;
        double dura;
        if (!world.isClientSide()) {
            if (this.isAlive()) {
                sklp1 = (Entity) this instanceof ThirsterEntity _datEntI ? _datEntI.getEntityData().get(DATA_SKILL_P) : 0;
                dura = (Entity) this instanceof ThirsterEntity _datEntI ? _datEntI.getEntityData().get(DATA_DURATION) : 0;
                if ((Entity) this instanceof ThirsterEntity _datEntI) {
                    _datEntI.getEntityData().get(DATA_INTEGRATION);
                }
                enemy = (Entity) this instanceof Mob _mobEnt ? _mobEnt.getTarget() : null;
                barr = this.getAttributes().hasAttribute(CAAttributes.LIVING_BARRIER.get())
                        ? this.getAttribute(CAAttributes.LIVING_BARRIER.get()).getBaseValue()
                        : 0;
                if (dura > 0) {
                    if ((Entity) this instanceof ThirsterEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_DURATION, (int) (dura - 1));
                    if (barr <= 0) {
                        if ((Entity) this instanceof ThirsterEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_DURATION, 0);
                        if ((Entity) this instanceof ThirsterEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_DIZZY_NUM, 2);
                    }
                } else {
                    if (barr > 0) {
                        if (this.getAttributes().hasAttribute(CAAttributes.LIVING_BARRIER.get()))
                            this.getAttribute(CAAttributes.LIVING_BARRIER.get()).setBaseValue(0);
                        this.performSanityAttack();
                    }
                }
                if (sklp1 > 0) {
                    if ((Entity) this instanceof ThirsterEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_SKILL_P, (int) (sklp1 - 1));
                } else {
                    if (!(enemy == null) && enemy.isAlive()) {
                        if ((Entity) this instanceof ThirsterEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_SKILL_P, 600);
                        if (this instanceof ThirsterEntity) {
                            this.setAnimation("animation.thirster.skill");
                        }
                        CaerulaArborMod.queueServerWork(5, () -> {
                            Entity enemy1;
                            double num;
                            double tX = 0;
                            double tZ = 0;
                            double tY;
                            num = 2;
                            enemy1 = this.getTarget();
                            {
                                final Vec3 _center = new Vec3(x, y, z);
                                List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(40 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                                for (Entity entityiterator : _entfound) {
                                    if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                                        if (!(entityiterator == enemy1)) {
                                            continue;
                                        }
                                    }
                                    if (!(entityiterator instanceof LivingEntity)) {
                                        continue;
                                    }
                                    if (entityiterator instanceof Player) {
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
                                    }
                                    if (distanceTo(entityiterator) < 20) {
                                        num = num - 1;
                                        tX = tX + entityiterator.getX();
                                        tZ = tZ + entityiterator.getZ();
                                        if (world instanceof ServerLevel _level)
                                            _level.sendParticles(CAParticleTypes.MOIST_BOOM.get(), (entityiterator.getX()), (entityiterator.getY() + 0.75), (entityiterator.getZ()), 8, 0.75, 0.75, 0.75, 0.1);
                                        CaerulaArborMod.queueServerWork(15, () -> {
                                            entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "ocean_magic"))), this),
                                                    (float) (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0));
                                        });
                                        if (num <= 0) {
                                            break;
                                        }
                                    }
                                }
                            }
                            if (num < 2) {
                                tX = tX / (2 - num);
                                tZ = tZ / (2 - num);
                                tY = WorldUtils.findYzforTear(world, tX, y, tZ);
                                if (tY < 1111) {
                                    if (world instanceof ServerLevel _level) {
                                        Entity entityToSpawn = CAEntities.ABSORBER_LIMB.get().spawn(_level, BlockPos.containing(tX, tY, tZ), MobSpawnType.MOB_SUMMONED);
                                        if (entityToSpawn != null) {
                                            entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
                maxH = this.getMaxHealth();
                if (maxH > 0) {
                    perc = barr / maxH;
                    if (perc > 0) {
                        for (int index0 = 0; index0 < 5; index0++) {
                            if (Math.random() < perc) {
                                angle = Mth.nextDouble(RandomSource.create(), 0, 6.283);
                                d = Mth.nextDouble(RandomSource.create(), 2, 2.5);
                                if (world instanceof ServerLevel _level)
                                    _level.sendParticles(ParticleTypes.ENCHANTED_HIT, (x + d * Math.sin(angle)), (y + 1), (z + d * Math.cos(angle)), 2, 0.1, 0.1, 0.1, 0.1);
                            }
                        }
                    }
                }
                if (tickCount % 20 == 10) {
                    double num;
                    Entity enemy1;
                    double result;
                    final Vec3 _center1 = new Vec3(x, y, z);
                    List<AbsorberLimbEntity> _entfound1 = world.getEntitiesOfClass(AbsorberLimbEntity.class,
                            new AABB(_center1, _center1).inflate(48 / 2d), AbsorberLimbEntity::isAlive);
                    result = _entfound1.size();
                    num = result;
                    enemy1 = this.getTarget();
                    if (num > 0) {
                        {
                            final Vec3 _center = new Vec3(x, y, z);
                            List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(40 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                            for (Entity entityiterator : _entfound) {
                                if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                                    if (!(entityiterator == enemy1)) {
                                        continue;
                                    }
                                }
                                if (!(entityiterator instanceof LivingEntity)) {
                                    continue;
                                }
                                if (entityiterator instanceof Player) {
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
                                }
                                if (distanceTo(entityiterator) < 20) {
                                    entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "trail_damage")))),
                                            (float) num);
                                    if (entityiterator instanceof LivingEntity target) {
                                        SIHelper.causeSanityInjury(target, this, num * 25, SanityEvent.Hurt.Type.ENTITY);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        this.refreshDimensions();
	}

	@Override
	public void tick(){
		super.tick();
		float p = (float) this.entityData.get(DATA_DURATION) / 400f;
		if (p > 0 && !this.isDeadOrDying()) {
			this.bossInfo.setColor(ServerBossEvent.BossBarColor.WHITE);
			this.bossInfo.setProgress(p);
		} else {
			this.bossInfo.setColor(ServerBossEvent.BossBarColor.BLUE);
			float m = this.getMaxHealth();
			if(m > 0) this.bossInfo.setProgress(this.getHealth() / m);
		}
	}

	@Override
	public EntityDimensions getDimensions(Pose p_33597_) {
		return super.getDimensions(p_33597_).scale((float) 1.2);
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

	private void performSanityAttack() {
		LevelAccessor world = this.level();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();

		new Object() {
			void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
				double d = timedloopiterator * 4;
				for (int index0 = 0; index0 < 120; index0++) {
					double angle = index0 * 3;
					if (world instanceof ServerLevel _level)
						_level.sendParticles(ParticleTypes.ELECTRIC_SPARK, (x + d * Math.sin(angle)), (y + 0.5), (z + d * Math.cos(angle)), 2, 0.1, 0.1, 0.1, 0.1);
				}
				final int tick2 = ticks;
				CaerulaArborMod.queueServerWork(tick2, () -> {
					if (timedlooptotal > timedloopiterator + 1) {
						timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
					}
				});
			}
		}.timedLoop(0, 5, 1);

		if (world instanceof Level _level) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "creeper_fish_explode")), SoundSource.HOSTILE, 3, 1);
		}

		this.getEntityData().set(DATA_DIZZY_NUM, this.getEntityData().get(DATA_DIZZY_NUM) + 1);

		final Vec3 center = new Vec3(x, y, z);
		List<Entity> nearbyEntities = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(20), e -> true).stream()
				.sorted(Comparator.comparingDouble(ent -> ent.distanceToSqr(center)))
				.toList();

		for (Entity entityiterator : nearbyEntities) {
			if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
				continue;
			}
			if (!(entityiterator instanceof LivingEntity)) {
				continue;
			}
			if (this.distanceTo(entityiterator) < 20) {
				if (entityiterator instanceof LivingEntity target) {
					SIHelper.causeSanityInjury(target, this, 1000, SanityEvent.Hurt.Type.ENTITY);
				}
			}
		}
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.2);
		builder = builder.add(Attributes.MAX_HEALTH, 340);
		builder = builder.add(Attributes.ARMOR, 15);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 5);
		builder = builder.add(Attributes.FOLLOW_RANGE, 36);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 1);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

			) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.thirster.move"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.thirster.die"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.thirster.idle"));
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
		if (this.swinging && this.lastSwing + 13L <= level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.thirster.attack"));
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
			this.remove(ThirsterEntity.RemovalReason.KILLED);
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
		data.add(new AnimationController<>(this, "movement", 1, this::movementPredicate));
		data.add(new AnimationController<>(this, "attacking", 1, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 1, this::procedurePredicate));
	}
}
