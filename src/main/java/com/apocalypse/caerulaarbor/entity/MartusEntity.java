package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.entity.base.SeaMonster;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CaerulaArborModAttributes;
import com.apocalypse.caerulaarbor.init.CaerulaArborModBlocks;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import com.apocalypse.caerulaarbor.procedures.MartusParticleRimProcedure;
import com.apocalypse.caerulaarbor.procedures.ParticleLinkProcedure;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import com.apocalypse.caerulaarbor.utils.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
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

public class MartusEntity extends SeaMonster {
	private int releaseTime = 0;

	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(MartusEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(MartusEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(MartusEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_phase = SynchedEntityData.defineId(MartusEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_skillp1 = SynchedEntityData.defineId(MartusEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_skillp2 = SynchedEntityData.defineId(MartusEntity.class, EntityDataSerializers.INT);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";
	private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.BLUE, ServerBossEvent.BossBarOverlay.NOTCHED_6);

	public MartusEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CaerulaArborModEntities.MARTUS.get(), world);
	}

	public MartusEntity(EntityType<MartusEntity> type, Level world) {
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
		this.entityData.define(TEXTURE, "martus");
		this.entityData.define(DATA_phase, 0);
		this.entityData.define(DATA_skillp1, 200);
		this.entityData.define(DATA_skillp2, 200);
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
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 4;
			}
		});
		this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.8, 20) {
			@Override
			protected Vec3 getPosition() {
				RandomSource random = MartusEntity.this.getRandom();
				double dir_x = MartusEntity.this.getX() + ((random.nextFloat() * 2 - 1) * 16);
				double dir_y = MartusEntity.this.getY() + ((random.nextFloat() * 2 - 1) * 16);
				double dir_z = MartusEntity.this.getZ() + ((random.nextFloat() * 2 - 1) * 16);
				return new Vec3(dir_x, dir_y, dir_z);
			}
		});
		this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(4, new FloatGoal(this));
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
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.hurt"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.death"));
	}

	@Override
	public boolean causeFallDamage(float l, float d, DamageSource source) {
		return false;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.IN_FIRE))
			return false;
		if (source.getDirectEntity() instanceof ThrownPotion || source.getDirectEntity() instanceof AreaEffectCloud)
			return false;
		if (source.is(DamageTypes.FALL))
			return false;
		if (source.is(DamageTypes.CACTUS))
			return false;
		if (source.is(DamageTypes.DROWN))
			return false;
		if (source.is(DamageTypes.LIGHTNING_BOLT))
			return false;
		if (source.is(DamageTypes.EXPLOSION))
			return false;
		if (source.is(DamageTypes.FALLING_ANVIL))
			return false;
		if (source.is(DamageTypes.DRAGON_BREATH))
			return false;
		if (source.is(DamageTypes.WITHER))
			return false;
		if (source.is(DamageTypes.WITHER_SKULL))
			return false;
		boolean isKiller = source.is(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "inv_killer")));
		if (isKiller){
			this.releaseTime = 10;
		}
		return super.hurt(source, amount);
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
        LevelAccessor world1 = this.level();
        if (this != null) {
            if (this.getAttributes().hasAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get()))
                this.getAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get()).setBaseValue(16384);
            if (this.getAttributes().hasAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()))
                this.getAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()).setBaseValue(100);
            new Object() {
                void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                    double r = 0;
                    double d = 0;
                    double tx = 0;
                    double tz = 0;
                    double ty = 0;
                    for (int index0 = 0; index0 < 8; index0++) {
                        r = Mth.nextDouble(RandomSource.create(), 0, 6.283);
                        d = Mth.nextDouble(RandomSource.create(), 6, 12);
                        tx = MartusEntity.this.getX() + d * Math.cos(r);
                        ty = MartusEntity.this.getY() + Mth.nextDouble(RandomSource.create(), 4, 9);
                        tz = MartusEntity.this.getZ() + d * Math.sin(r);
                        if ((world1.getBlockState(BlockPos.containing(tx, ty, tz))).canBeReplaced()) {
                            if (world1 instanceof ServerLevel _level)
                                FallingBlockEntity.fall(_level, BlockPos.containing(tx, ty, tz), (new Object() {
                                    public BlockState with(BlockState _bs, Direction newValue) {
                                        Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty("facing");
                                        if (_prop instanceof DirectionProperty _dp && _dp.getPossibleValues().contains(newValue))
                                            return _bs.setValue(_dp, newValue);
                                        _prop = _bs.getBlock().getStateDefinition().getProperty("axis");
                                        return _prop instanceof EnumProperty _ep && _ep.getPossibleValues().contains(newValue.getAxis()) ? _bs.setValue(_ep, newValue.getAxis()) : _bs;
                                    }
                                }.with(CaerulaArborModBlocks.ABANDONED_SULPTURE.get().defaultBlockState(), new Object() {
                                    public Direction getValue() {
                                        Direction _dir = Direction.NORTH;
                                        int _num = Mth.nextInt(RandomSource.create(), 1, 4);
                                        if (_num == 1) {
                                            _dir = Direction.EAST;
                                        } else if (_num == 2) {
                                            _dir = Direction.SOUTH;
                                        } else if (_num == 3) {
                                            _dir = Direction.WEST;
                                        }
                                        return _dir;
                                    }
                                }.getValue())));
                            break;
                        }
                    }
                    final int tick2 = ticks;
                    CaerulaArborMod.queueServerWork(tick2, () -> {
                        if (timedlooptotal > timedloopiterator + 1) {
                            timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                        }
                    });
                }
            }.timedLoop(0, 8, 1);
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
            Entity tgt = null;
            double sklp1 = 0;
            double sklp2 = 0;
            double phase = 0;
            double perc = 0;
            if (this.isAlive()) {
                sklp1 = (Entity) this instanceof MartusEntity _datEntI ? _datEntI.getEntityData().get(DATA_skillp1) : 0;
                sklp2 = (Entity) this instanceof MartusEntity _datEntI ? _datEntI.getEntityData().get(DATA_skillp2) : 0;
                phase = (Entity) this instanceof MartusEntity _datEntI ? _datEntI.getEntityData().get(DATA_phase) : 0;
                if (tickCount % 12 == 0) {
                    if (this != null) {
                        double num = 0;
                        double limit = 0;
                        limit = 2;
                        if (CaerulaArborModVariables.MapVariables.get(world).strategy_subsisting > 3) {
                            limit = 3;
                        }
                        {
                            final Vec3 _center = new Vec3(x, y, z);
                            List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(96 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                            for (Entity entityiterator : _entfound) {
                                if (!(entityiterator instanceof Mob)) {
                                    continue;
                                }
                                if (entityiterator instanceof LivingEntity _livEnt1 && _livEnt1.hasEffect(CaerulaArborModMobEffects.GUIDED_EVO.get())) {
                                    num = num + 1;
                                    ParticleLinkProcedure.execute(world, this, entityiterator);
                                    CaerulaArborMod.queueServerWork(3, () -> {
                                        ParticleLinkProcedure.execute(world, this, entityiterator);
                                    });
                                    CaerulaArborMod.queueServerWork(6, () -> {
                                        ParticleLinkProcedure.execute(world, this, entityiterator);
                                    });
                                    CaerulaArborMod.queueServerWork(9, () -> {
                                        ParticleLinkProcedure.execute(world, this, entityiterator);
                                    });
                                }
                                if (num >= limit) {
                                    break;
                                }
                            }
                        }
                    }
                }
                if (tickCount % 100 == 0) {
                    if (WorldUtils.isDistFromGround(world, x, y, z)) {
                        push(0, (-0.64), 0);
                    }
                }
                if (phase < 0.33) {
                    if (tickCount % 2 == 0) {
                        MartusParticleRimProcedure.execute(world, x, y, z);
                    }
                    if (!((Entity) this instanceof LivingEntity _livEnt8 && _livEnt8.hasEffect(CaerulaArborModMobEffects.INVULNERABLE.get()))) {
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 10000, 9, false, false));
                    }
                    if (sklp1 > 0) {
                        if ((Entity) this instanceof MartusEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_skillp1, (int) (sklp1 - 1));
                        if (CaerulaArborModVariables.MapVariables.get(world).strategy_subsisting > 3) {
                            if ((Entity) this instanceof MartusEntity _datEntSetI)
                                _datEntSetI.getEntityData().set(DATA_skillp1, (int) (sklp1 - 1));
                        }
                    } else {
                        if (tickCount % 10 == 0) {
                            Entity result = null;
                            if (this != null) {
                                Entity tgt_ent = null;
                                double num = 0;
                                double max_h = 0;
                                double limit = 0;
                                limit = 2;
                                if (CaerulaArborModVariables.MapVariables.get(world).strategy_subsisting > 3) {
                                    limit = 3;
                                }
                                for (Entity entityiterator : world.getEntities(this, new AABB((x + 32), (y + 32), (z + 32), (x - 32), (y - 32), (z - 32)))) {
                                    if (!(entityiterator instanceof Mob)) {
                                        continue;
                                    }
                                    if (!entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                                        continue;
                                    }
                                    if (entityiterator instanceof MartusEntity) {
                                        continue;
                                    }
                                    if (entityiterator.getPersistentData().getBoolean("blessed")) {
                                        num = num + 1;
                                        continue;
                                    }
                                    if (num >= limit) {
                                        break;
                                    }
                                    if ((entityiterator instanceof LivingEntity _livEnt1 ? _livEnt1.getMaxHealth() : -1) > max_h) {
                                        max_h = entityiterator instanceof LivingEntity _livEnt1 ? _livEnt1.getMaxHealth() : -1;
                                        tgt_ent = entityiterator;
                                    }
                                }
                                result = tgt_ent;
                            }
                            tgt = result;
                            if (!(tgt == null) && tgt.isAlive() && !tgt.getPersistentData().getBoolean("blessed")) {
                                if ((Entity) this instanceof MartusEntity _datEntSetI)
                                    _datEntSetI.getEntityData().set(DATA_skillp1, 400);
                                if (this instanceof MartusEntity) {
                                    ((MartusEntity) this).setAnimation("animation.martus.buff");
                                }
                                tgt.getPersistentData().putBoolean("blessed", true);
                                perc = (tgt instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) / (tgt instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1);
                                if (tgt instanceof LivingEntity _livingEntity22 && _livingEntity22.getAttributes().hasAttribute(Attributes.MAX_HEALTH))
                                    _livingEntity22.getAttribute(Attributes.MAX_HEALTH)
                                            .setBaseValue(((tgt instanceof LivingEntity _livingEntity21 && _livingEntity21.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? _livingEntity21.getAttribute(Attributes.MAX_HEALTH).getBaseValue() : 0) * 2.5));
                                if (tgt instanceof LivingEntity _livingEntity24 && _livingEntity24.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
                                    _livingEntity24.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(
                                            ((tgt instanceof LivingEntity _livingEntity23 && _livingEntity23.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity23.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : 0) * 2.5));
                                if (tgt instanceof LivingEntity _livingEntity26 && _livingEntity26.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_RATE.get()))
                                    _livingEntity26.getAttribute(CaerulaArborModAttributes.SANITY_RATE.get())
                                            .setBaseValue(((tgt instanceof LivingEntity _livingEntity25 && _livingEntity25.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_RATE.get())
                                                    ? _livingEntity25.getAttribute(CaerulaArborModAttributes.SANITY_RATE.get()).getBaseValue()
                                                    : 0) + 25));
                                if (tgt instanceof LivingEntity _entity)
                                    _entity.setHealth((float) ((tgt instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * perc));
                                if (tgt instanceof LivingEntity _entity && !this.level().isClientSide())
                                    this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.GUIDED_EVO.get(), -1, 0));
                            }
                        }
                    }
                    if (tickCount % 300 == 0) {
                        martusTimedSpawn(world, x, y, z);
                    }
                } else {
                    if (((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) > ((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.33) {
                        if (!((Entity) this instanceof LivingEntity _livEnt33 && _livEnt33.hasEffect(CaerulaArborModMobEffects.INVULNERABLE.get()))) {
                            if (!this.level().isClientSide())
                                this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 20, 9, false, false));
                        }
                        if (tickCount % 2 == 0) {
                            MartusParticleRimProcedure.execute(world, x, y, z);
                        }
                    }
                    if (sklp1 > 0) {
                        if ((Entity) this instanceof MartusEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_skillp1, (int) (sklp1 - 1));
                    } else {
                        if (tickCount % 10 == 0 && EntityUtils.getSeabornAround(world, x, y, z, this) > 0) {
                            if ((Entity) this instanceof MartusEntity _datEntSetI)
                                _datEntSetI.getEntityData().set(DATA_skillp1, 1000);
                            if (this instanceof MartusEntity) {
                                ((MartusEntity) this).setAnimation("animation.martus.cure");
                            }
                            new Object() {
                                void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                                    if (((Entity) MartusEntity.this instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) > ((Entity) MartusEntity.this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.02) {
                                        EntityUtils.hurtMartus(world, MartusEntity.this, null, 0, 0.02);
                                    }
                                    final int tick2 = ticks;
                                    CaerulaArborMod.queueServerWork(tick2, () -> {
                                        if (timedlooptotal > timedloopiterator + 1) {
                                            timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                                        }
                                    });
                                }
                            }.timedLoop(0, 10, 20);
                            CaerulaArborMod.queueServerWork(10, () -> {
                                {
                                    final Vec3 _center = new Vec3(x, y, z);
                                    List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(64 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                                    for (Entity entityiterator : _entfound) {
                                        if (!(entityiterator instanceof Mob)) {
                                            continue;
                                        }
                                        if (!entityiterator.isAlive()) {
                                            continue;
                                        }
                                        if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))
                                                && !entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "bossoffspring")))) {
                                            if (entityiterator instanceof LivingEntity _entity && !this.level().isClientSide())
                                                this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.FAKE_DEATH.get(), 200, 1));
                                            if (entityiterator instanceof LivingEntity _entity && !this.level().isClientSide())
                                                this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 200, 0));
                                        }
                                        if (world instanceof ServerLevel _level)
                                            _level.sendParticles(ParticleTypes.DOLPHIN, (entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ()), 32, 0.85, 0.85, 0.85, 0.1);
                                    }
                                }
                            });
                        }
                    }
                    if (sklp2 > 0) {
                        if ((Entity) this instanceof MartusEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_skillp2, (int) (sklp2 - 1));
                    } else {
                        if ((Entity) this instanceof MartusEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_skillp2, 600);
                        if (this instanceof MartusEntity) {
                            ((MartusEntity) this).setAnimation("animation.martus.reject");
                        }
                        CaerulaArborMod.queueServerWork(15, () -> {
                            if (this == null)
                                return;
                            Entity tgt_ent = null;
                            double max_h = 0;
                            tgt_ent = (Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null;
                            if (tgt_ent == null || tgt_ent instanceof LivingEntity _livEnt2 && _livEnt2.hasEffect(CaerulaArborModMobEffects.SUB_HAEMO.get())) {
                                tgt_ent = ((Entity) this instanceof LivingEntity _entity) ? _entity.getLastHurtByMob() : null;
                            }
                            if (tgt_ent == null || tgt_ent instanceof LivingEntity _livEnt5 && _livEnt5.hasEffect(CaerulaArborModMobEffects.SUB_HAEMO.get())) {
                                for (Entity entityiterator : world.getEntities(this, new AABB((x + 32), (y + 32), (z + 32), (x - 32), (y - 32), (z - 32)))) {
                                    if (!(entityiterator instanceof Mob)) {
                                        continue;
                                    }
                                    if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                                        continue;
                                    }
                                    if (entityiterator instanceof LivingEntity _livEnt8 && _livEnt8.hasEffect(CaerulaArborModMobEffects.SUB_HAEMO.get())) {
                                        continue;
                                    }
                                    if ((entityiterator instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) > max_h) {
                                        max_h = entityiterator instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1;
                                        tgt_ent = entityiterator;
                                    }
                                }
                            }
                            if (!(tgt_ent == null) && !(tgt_ent instanceof LivingEntity _livEnt13 && _livEnt13.hasEffect(CaerulaArborModMobEffects.SUB_HAEMO.get()))) {
                                if (tgt_ent instanceof LivingEntity _entity && !this.level().isClientSide())
                                    this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.SUB_HAEMO.get(), 800, 0));
                            }
                        });
                    }
                    if (tickCount % 200 == 0) {
                        martusTimedSpawn(world, x, y, z);
                    }
                }
            }
        }
        this.refreshDimensions();
		if(this.releaseTime > 0) this.releaseTime --;
	}

	@Override
	public EntityDimensions getDimensions(Pose p_33597_) {
		return super.getDimensions(p_33597_).scale((float) 1);
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	public boolean checkSpawnObstruction(LevelReader world) {
		return world.isUnobstructed(this);
	}

	@Override
	public boolean isPushedByFluid() {
		return false;
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

	private void martusTimedSpawn(LevelAccessor world, double x, double y, double z) {
		if (EntityUtils.getSeabornAround(world, x, y, z, this) < (world.getLevelData().getGameRules().getInt(com.apocalypse.caerulaarbor.init.CaerulaArborModGameRules.CLONE_NUMBER_LIMIT))) {
			for (int index0 = 0; index0 < 2; index0++) {
				com.apocalypse.caerulaarbor.utils.WorldUtils.summonRandomSeaborn(world, 0.33, x, y, z);
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.CLOUD, x, y, z, 18, 0.6, 0.6, 0.6, 0.16);
			}
		}
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
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.5);
		builder = builder.add(Attributes.MAX_HEALTH, 500);
		builder = builder.add(Attributes.ARMOR, 30);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 2);
		builder = builder.add(Attributes.FOLLOW_RANGE, 24);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
		builder = builder.add(Attributes.FLYING_SPEED, 0.5);
		builder = builder.add(ForgeMod.SWIM_SPEED.get(), 0.5);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.martus.die"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.martus.idle"));
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
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.martus.attack"));
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
		if (this.deathTime == 25) {
			this.remove(MartusEntity.RemovalReason.KILLED);
			this.dropExperience();
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
    public void heal(float amount){
        super.heal(0);
    }

	@Override
    public void setHealth(float pHealth){
    	if(this.releaseTime > 0) super.setHealth(pHealth);
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
}
