package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CaerulaArborModAttributes;
import com.apocalypse.caerulaarbor.init.CaerulaArborModBlocks;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.utils.EntityPredicateUtils;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
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
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.levelgen.Heightmap;
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

public class BishopFishEntity extends SeaMonster {

	private boolean isBishopStarted() {
		return EntityPredicateUtils.isBishopStarted(this);
	}
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(BishopFishEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(BishopFishEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(BishopFishEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_sklp = SynchedEntityData.defineId(BishopFishEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_endp = SynchedEntityData.defineId(BishopFishEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_locx = SynchedEntityData.defineId(BishopFishEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_locy = SynchedEntityData.defineId(BishopFishEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_locz = SynchedEntityData.defineId(BishopFishEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_summonp = SynchedEntityData.defineId(BishopFishEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_duration = SynchedEntityData.defineId(BishopFishEntity.class, EntityDataSerializers.INT);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";
	private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.BLUE, ServerBossEvent.BossBarOverlay.NOTCHED_10);

	public BishopFishEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CaerulaArborModEntities.BISHOP_FISH.get(), world);
	}

	public BishopFishEntity(EntityType<BishopFishEntity> type, Level world) {
		super(type, world);
		xpReward = 64;
		setNoAi(false);
		setMaxUpStep(2f);
		setPersistenceRequired();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "bishop");
		this.entityData.define(DATA_sklp, 200);
		this.entityData.define(DATA_endp, 1200);
		this.entityData.define(DATA_locx, 0);
		this.entityData.define(DATA_locy, 0);
		this.entityData.define(DATA_locz, 0);
		this.entityData.define(DATA_summonp, 280);
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
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 0, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 144;
			}

			@Override
			public boolean canUse() {
				double x = BishopFishEntity.this.getX();
				double y = BishopFishEntity.this.getY();
				double z = BishopFishEntity.this.getZ();
				Entity entity = BishopFishEntity.this;
				Level world = BishopFishEntity.this.level();
				return super.canUse() && isBishopStarted();
			}

			@Override
			public boolean canContinueToUse() {
				double x = BishopFishEntity.this.getX();
				double y = BishopFishEntity.this.getY();
				double z = BishopFishEntity.this.getZ();
				Entity entity = BishopFishEntity.this;
				Level world = BishopFishEntity.this.level();
				return super.canContinueToUse() && isBishopStarted();
			}

		});
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, true, false));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, SnowGolem.class, true, false));
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Villager.class, true, false));
		this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, Illusioner.class, true, false));
		this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, Pillager.class, true, false));
		this.targetSelector.addGoal(8, new NearestAttackableTargetGoal(this, Vindicator.class, true, false));
		this.targetSelector.addGoal(9, new NearestAttackableTargetGoal(this, Witch.class, true, false));
		this.targetSelector.addGoal(10, new NearestAttackableTargetGoal(this, Piglin.class, true, false));
		this.targetSelector.addGoal(11, new NearestAttackableTargetGoal(this, PiglinBrute.class, true, false));
		this.targetSelector.addGoal(12, new NearestAttackableTargetGoal(this, ZombifiedPiglin.class, true, false));
		this.targetSelector.addGoal(13, new NearestAttackableTargetGoal(this, Player.class, true, false) {
			@Override
			public boolean canUse() {
				double x = BishopFishEntity.this.getX();
				double y = BishopFishEntity.this.getY();
				double z = BishopFishEntity.this.getZ();
				Entity entity = BishopFishEntity.this;
				Level world = BishopFishEntity.this.level();
				return super.canUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
			}

			@Override
			public boolean canContinueToUse() {
				double x = BishopFishEntity.this.getX();
				double y = BishopFishEntity.this.getY();
				double z = BishopFishEntity.this.getZ();
				Entity entity = BishopFishEntity.this;
				Level world = BishopFishEntity.this.level();
				return super.canContinueToUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
			}
		});
		this.goalSelector.addGoal(14, new RandomLookAroundGoal(this));
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
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.elder_guardian.ambient"));
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.elder_guardian.hurt"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.elder_guardian.death"));
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        if (this != null) {
            double rate = 0;
            double sklp = 0;
            double dx = 0;
            double dz = 0;
            double yfnl = 0;
            if (this.isAlive()) {
                if (((Entity) this instanceof BishopFishEntity _datEntI ? _datEntI.getEntityData().get(DATA_sklp) : 0) <= 0) {
                    if (this instanceof BishopFishEntity) {
                        ((BishopFishEntity) this).setAnimation("animation.bishop.skill");
                    }
                    if ((Entity) this instanceof BishopFishEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_duration, (int) (((Entity) this instanceof BishopFishEntity _datEntI ? _datEntI.getEntityData().get(DATA_duration) : 0) + 20));
                    if (world instanceof Level _level) {
                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "bishopfish_flap")), SoundSource.HOSTILE, 3, 1);
                    }
                    new Object() {
                        void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                            for (int index0 = 0; index0 < 180; index0++) {
                                if (world instanceof ServerLevel _level)
                                    _level.sendParticles(ParticleTypes.ELECTRIC_SPARK, (x + timedloopiterator * 2 * Math.sin(Math.toRadians(2 * index0))), (y + 0.5), (z + timedloopiterator * 2 * Math.cos(Math.toRadians(2 * index0))), 16, 0.15, 0.5, 0.15,
                                            0.1);
                            }
                            final int tick2 = ticks;
                            CaerulaArborMod.queueServerWork(tick2, () -> {
                                if (timedlooptotal > timedloopiterator + 1) {
                                    timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                                }
                            });
                        }
                    }.timedLoop(0, 10, 2);
                    for (Entity entityiterator : world.getEntities(this, new AABB((x + 20), (y - 4), (z + 20), (x - 20), (y + 8), (z - 20)))) {
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
                        if (!(entityiterator instanceof Mob) && !(entityiterator instanceof Player)) {
                            continue;
                        }
                        if ((entityiterator != null ? distanceTo(entityiterator) : -1) <= 20) {
                            dx = entityiterator.getX() - getX();
                            if ((dx) > (0) && (dx) < (1)) {
                                dx = 1;
                            } else if ((dx) > ((-1)) && (dx) < (0)) {
                                dx = -1;
                            } else if (dx == 0) {
                                dx = 1;
                            }
                            dz = entityiterator.getZ() - getZ();
                            if ((dz) > (0) && (dz) < (1)) {
                                dz = 1;
                            } else if ((dz) > ((-1)) && (dz) < (0)) {
                                dz = -1;
                            } else if (dz == 0) {
                                dz = 1;
                            }
                            entityiterator.push((1.5 / dx), 0.25, (1.5 / dz));
                            entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "ocean_magic")))),
                                    (float) ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 1.5));
                            EntityUtils.deductSanity(entityiterator, (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0)
                                            * (this.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_RATE.get())
                                            ? this.getAttribute(CaerulaArborModAttributes.SANITY_RATE.get()).getValue()
                                            : 0)
                                            * 1.5);
                            if (entityiterator instanceof LivingEntity _entity && !this.level().isClientSide())
                                this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.DIZZY.get(), 60, 0, false, false));
                        }
                    }
                    if ((Entity) this instanceof LivingEntity _livEnt33 && _livEnt33.hasEffect(CaerulaArborModMobEffects.ANGER_OF_BISHOP.get())) {
                        if (((Entity) this instanceof LivingEntity _livEnt && _livEnt.hasEffect(CaerulaArborModMobEffects.ANGER_OF_BISHOP.get()) ? _livEnt.getEffect(CaerulaArborModMobEffects.ANGER_OF_BISHOP.get()).getAmplifier() : 0) >= 1) {
                            if ((Entity) this instanceof BishopFishEntity _datEntSetI)
                                _datEntSetI.getEntityData().set(DATA_sklp, 100);
                        } else {
                            if ((Entity) this instanceof BishopFishEntity _datEntSetI)
                                _datEntSetI.getEntityData().set(DATA_sklp, 300);
                        }
                    } else {
                        if ((Entity) this instanceof BishopFishEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_sklp, 500);
                    }
                }
                if (((Entity) this instanceof BishopFishEntity _datEntI ? _datEntI.getEntityData().get(DATA_summonp) : 0) <= 0) {
                    for (Entity entityiterator : world.getEntities(this, new AABB((x - 32), (y - 16), (z - 32), (x + 32), (y + 16), (z + 32)))) {
                        if (entityiterator instanceof SonsEntity) {
                            if ((entityiterator != null ? distanceTo(entityiterator) : -1) <= 6) {
                                entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.FELL_OUT_OF_WORLD)), 99999);
                            } else {
                                rate = rate + 1;
                            }
                        }
                    }
                    if (rate < 32) {
                        for (int index1 = 0; index1 < 8; index1++) {
                            dx = Mth.nextDouble(RandomSource.create(), -22, 22);
                            dz = Mth.nextDouble(RandomSource.create(), -22, 22);
                            yfnl = world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) (x + dx), (int) (z + dz));
                            if (yfnl > y + 3) {
                                yfnl = y + 3;
                            }
                            if (world instanceof ServerLevel _level) {
                                Entity entityToSpawn = CaerulaArborModEntities.SONS.get().spawn(_level, BlockPos.containing(x + dx, yfnl, z + dz), MobSpawnType.MOB_SUMMONED);
                                if (entityToSpawn != null) {
                                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                                }
                            }
                            if (world instanceof ServerLevel _level)
                                _level.sendParticles(ParticleTypes.SMOKE, (x + dx), (yfnl + 0.5), (z + dz), 16, 0.5, 0.5, 0.5, 0.2);
                            if (world instanceof Level _level) {
                                    _level.playSound(null, BlockPos.containing(x + dx, yfnl, z + dz), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.guardian.flop")), SoundSource.HOSTILE, 1, 1);
                            }
                        }
                        if ((Entity) this instanceof LivingEntity _livEnt50 && _livEnt50.hasEffect(CaerulaArborModMobEffects.ANGER_OF_BISHOP.get())) {
                            if ((Entity) this instanceof BishopFishEntity _datEntSetI)
                                _datEntSetI.getEntityData().set(DATA_summonp, 360);
                        } else {
                            if ((Entity) this instanceof BishopFishEntity _datEntSetI)
                                _datEntSetI.getEntityData().set(DATA_summonp, 600);
                        }
                    }
                }
            }
        }
        if (source.is(DamageTypes.FALL))
			return false;
		if (source.is(DamageTypes.DROWN))
			return false;
		if (source.is(DamageTypes.LIGHTNING_BOLT))
			return false;
		if (source.is(DamageTypes.FALLING_ANVIL))
			return false;
		return super.hurt(source, amount);
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        if (this != null) {
            if ((Entity) this instanceof BishopFishEntity _datEntSetI)
                _datEntSetI.getEntityData().set(DATA_locx, (int) Math.round(x));
            if ((Entity) this instanceof BishopFishEntity _datEntSetI)
                _datEntSetI.getEntityData().set(DATA_locy, (int) Math.round(y));
            if ((Entity) this instanceof BishopFishEntity _datEntSetI)
                _datEntSetI.getEntityData().set(DATA_locz, (int) Math.round(z));
            if (this.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_RATE.get()))
                this.getAttribute(CaerulaArborModAttributes.SANITY_RATE.get()).setBaseValue(10);
            if (this.getAttributes().hasAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()))
                this.getAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()).setBaseValue(24);
            setNoGravity(true);
            if (!this.level().isClientSide())
                this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 80, 1, false, false));
            if ((LevelAccessor) world instanceof Level _level) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.warden.emerge")), SoundSource.HOSTILE, 3, 1);
            }
            if (this instanceof BishopFishEntity) {
                ((BishopFishEntity) this).setAnimation("animation.bishop.start1");
            }
        }
        return retval;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putInt("Datasklp", this.entityData.get(DATA_sklp));
		compound.putInt("Dataendp", this.entityData.get(DATA_endp));
		compound.putInt("Datalocx", this.entityData.get(DATA_locx));
		compound.putInt("Datalocy", this.entityData.get(DATA_locy));
		compound.putInt("Datalocz", this.entityData.get(DATA_locz));
		compound.putInt("Datasummonp", this.entityData.get(DATA_summonp));
		compound.putInt("Dataduration", this.entityData.get(DATA_duration));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("Datasklp"))
			this.entityData.set(DATA_sklp, compound.getInt("Datasklp"));
		if (compound.contains("Dataendp"))
			this.entityData.set(DATA_endp, compound.getInt("Dataendp"));
		if (compound.contains("Datalocx"))
			this.entityData.set(DATA_locx, compound.getInt("Datalocx"));
		if (compound.contains("Datalocy"))
			this.entityData.set(DATA_locy, compound.getInt("Datalocy"));
		if (compound.contains("Datalocz"))
			this.entityData.set(DATA_locz, compound.getInt("Datalocz"));
		if (compound.contains("Datasummonp"))
			this.entityData.set(DATA_summonp, compound.getInt("Datasummonp"));
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
            double skl = 0;
            double end = 0;
            double smm = 0;
            double dx = 0;
            double rate = 0;
            double dz = 0;
            double d = 0;
            if (((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) <= ((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.67) {
                if (!((Entity) this instanceof LivingEntity _livEnt2 && _livEnt2.hasEffect(CaerulaArborModMobEffects.ANGER_OF_BISHOP.get()))) {
                    if (((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) <= ((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.33) {
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ANGER_OF_BISHOP.get(), 20, 1));
                    } else {
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ANGER_OF_BISHOP.get(), 20, 0));
                    }
                }
            }
            skl = (Entity) this instanceof BishopFishEntity _datEntI ? _datEntI.getEntityData().get(DATA_sklp) : 0;
            smm = (Entity) this instanceof BishopFishEntity _datEntI ? _datEntI.getEntityData().get(DATA_summonp) : 0;
            end = (Entity) this instanceof BishopFishEntity _datEntI ? _datEntI.getEntityData().get(DATA_endp) : 0;
            d = (Entity) this instanceof BishopFishEntity _datEntI ? _datEntI.getEntityData().get(DATA_duration) : 0;
            if (d > 0) {
                if ((Entity) this instanceof BishopFishEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_duration, (int) (d - 1));
            }
            if (skl > 0) {
                if ((Entity) this instanceof BishopFishEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_sklp, (int) (skl - 1));
            }
            if (smm > 0) {
                if ((Entity) this instanceof BishopFishEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_summonp, (int) (smm - 1));
            }
            if (end <= 0) {
                if (EntityUtils.getSeabornAround(world, x, y, z, this) < 32) {
                    if (this instanceof BishopFishEntity) {
                        ((BishopFishEntity) this).setAnimation("animation.bishop.blast");
                    }
                    if ((Entity) this instanceof BishopFishEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_duration, (int) (d + 40));
                    if (world instanceof Level _level) {
                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "bishopfish_blast")), SoundSource.HOSTILE, 4, 1);
                    }
                    if (!this.level().isClientSide())
                        this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 40, 0));
                    if ((Entity) this instanceof BishopFishEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_endp, 2400);
                    new Object() {
                        void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                            double dx1 = 0;
                            double dz1 = 0;
                            double yfnl = 0;
                            dx1 = Mth.nextInt(RandomSource.create(), -18, 18);
                            dz1 = Mth.nextInt(RandomSource.create(), -18, 18);
                            yfnl = world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) (x + dx1), (int) (z + dz1));
                            if (yfnl < y - 6) {
                                yfnl = y;
                            }
                            if (yfnl > y + 3) {
                                yfnl = y + 3;
                            }
                            com.apocalypse.caerulaarbor.utils.WorldUtils.summonRandomSeaborn(world, 0.75, x + dx1, yfnl, z + dz1);
                            if (world instanceof ServerLevel _level)
                                FallingBlockEntity.fall(_level, BlockPos.containing(x + dx1, yfnl + 6, z + dz1), CaerulaArborModBlocks.SEA_TRAIL_GROWN.get().defaultBlockState());
                            if (world instanceof ServerLevel _level)
                                _level.sendParticles(ParticleTypes.CLOUD, (x + dx1), (yfnl + 1), (z + dz1), 64, 1, 1, 1, 0.1);
                            if (world instanceof Level _level) {
                                    _level.playSound(null, BlockPos.containing(x + dx1, yfnl + 1, z + dz1), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("ambient.underwater.enter")), SoundSource.NEUTRAL, (float) 1.5, 1);
                            }
                            for (Entity entityiterator : world.getEntities(BishopFishEntity.this, new AABB((x + 18), y, (z + 18), (x - 18), (y + 12), (z - 18)))) {
                                if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                                    if (!(entityiterator == ((Entity) BishopFishEntity.this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null))) {
                                        continue;
                                    }
                                }
                                if (!(entityiterator instanceof Mob) && !(entityiterator instanceof Player)) {
                                    continue;
                                }
                                if ((entityiterator != null ? distanceTo(entityiterator) : -1) <= 20) {
                                    entityiterator.hurt(
                                            new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "ocean_magic"))), BishopFishEntity.this),
                                            (float) (((Entity) BishopFishEntity.this instanceof LivingEntity _livingEntity25 && _livingEntity25.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity25.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 3));
                                }
                            }
                            final int tick2 = ticks;
                            CaerulaArborMod.queueServerWork(tick2, () -> {
                                if (timedlooptotal > timedloopiterator + 1) {
                                    timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                                }
                            });
                        }
                    }.timedLoop(0, 16, 5);
                }
            } else {
                if ((Entity) this instanceof BishopFishEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_endp, (int) (end - 1));
            }
            if (tickCount % 10 == 0 && new Vec3(x, y, z).distanceTo(new Vec3(((Entity) this instanceof BishopFishEntity _datEntI ? _datEntI.getEntityData().get(DATA_locx) : 0),
                    ((Entity) this instanceof BishopFishEntity _datEntI ? _datEntI.getEntityData().get(DATA_locy) : 0), ((Entity) this instanceof BishopFishEntity _datEntI ? _datEntI.getEntityData().get(DATA_locz) : 0))) >= 3) {
                setDeltaMovement(new Vec3(0, 0, 0));
                {
                    Entity _ent = this;
                    _ent.teleportTo(((Entity) this instanceof BishopFishEntity _datEntI ? _datEntI.getEntityData().get(DATA_locx) : 0),
                            ((Entity) this instanceof BishopFishEntity _datEntI ? _datEntI.getEntityData().get(DATA_locy) : 0), ((Entity) this instanceof BishopFishEntity _datEntI ? _datEntI.getEntityData().get(DATA_locz) : 0));
                    if (_ent instanceof ServerPlayer _serverPlayer)
                        _serverPlayer.connection.teleport(((Entity) this instanceof BishopFishEntity _datEntI ? _datEntI.getEntityData().get(DATA_locx) : 0),
                                ((Entity) this instanceof BishopFishEntity _datEntI ? _datEntI.getEntityData().get(DATA_locy) : 0), ((Entity) this instanceof BishopFishEntity _datEntI ? _datEntI.getEntityData().get(DATA_locz) : 0),
                                _ent.getYRot(), _ent.getXRot());
                }
            }
            for (Entity entityiterator : world.getEntities(this, new AABB((x - 6), (y - 6), (z - 6), (x + 6), (y + 6), (z + 6)))) {
                if (entityiterator instanceof SonsEntity) {
                    if ((entityiterator != null ? distanceTo(entityiterator) : -1) <= 6) {
                        entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.FELL_OUT_OF_WORLD)), 99999);
                    }
                }
            }
        }
        this.refreshDimensions();
	}

	@Override
	public EntityDimensions getDimensions(Pose p_33597_) {
		return super.getDimensions(p_33597_).scale((float) 5);
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
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0);
		builder = builder.add(Attributes.MAX_HEALTH, 560);
		builder = builder.add(Attributes.ARMOR, 8);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 7);
		builder = builder.add(Attributes.FOLLOW_RANGE, 64);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.isDeadOrDying()) {
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.bishop.die"));
		}
		if (this.animationprocedure.equals("empty")) {
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.bishop.idle"));
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
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.bishop.attack"));
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
			this.remove(BishopFishEntity.RemovalReason.KILLED);
			this.dropExperience();
            LevelAccessor world = this.level();
            if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                if (!world.isClientSide() && world.getServer() != null) {
                    for (ItemStack itemstackiterator : world.getServer().getLootData().getLootTable(new ResourceLocation(CaerulaArborMod.MODID, "gameplay/relic_bishop"))
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
}
