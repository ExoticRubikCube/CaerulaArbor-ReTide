package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.capability.map.MapVariables;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.*;
import com.apocalypse.caerulaarbor.manager.SeabornSpawnManager;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import com.apocalypse.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
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

    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(MartusEntity.class, EntityDataSerializers.STRING);

    public static final EntityDataAccessor<Integer> DATA_PHASE = SynchedEntityData.defineId(MartusEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_SKILLP_1 = SynchedEntityData.defineId(MartusEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_SKILLP_2 = SynchedEntityData.defineId(MartusEntity.class, EntityDataSerializers.INT);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";
    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.BLUE, ServerBossEvent.BossBarOverlay.NOTCHED_6);

    public MartusEntity(Level world) {
        this(CAEntities.MARTUS.get(), world);
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
        this.entityData.define(DATA_ANIMATION, "undefined");
        this.entityData.define(DATA_PHASE, 0);
        this.entityData.define(DATA_SKILLP_1, 200);
        this.entityData.define(DATA_SKILLP_2, 200);
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
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return SoundEvents.GENERIC_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
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
        boolean isKiller = source.is(CADamageTypes.INV_KILLER);
        if (isKiller) {
            this.releaseTime = 10;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource source) {
        if (this.getEntityData().get(DATA_PHASE) == 0) {
            this.removeEffect(CAMobEffects.INVULNERABLE.get());
            if (!this.level().isClientSide()) {
                this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 200, 1, false, false));
                this.addEffect(new MobEffectInstance(CAMobEffects.FAKE_DEATH.get(), 200, 1, false, false));
            }
            this.getEntityData().set(DATA_PHASE, 1);
            this.getEntityData().set(DATA_SKILLP_1, 600);
            this.getEntityData().set(DATA_SKILLP_2, 200);
            return;
        }
        super.die(source);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
        LevelAccessor world1 = this.level();
        new Object() {
            void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                double r;
                double d;
                double tx;
                double tz;
                double ty;
                for (int index0 = 0; index0 < 8; index0++) {
                    r = Mth.nextDouble(RandomSource.create(), 0, 6.283);
                    d = Mth.nextDouble(RandomSource.create(), 6, 12);
                    tx = MartusEntity.this.getX() + d * Math.cos(r);
                    ty = MartusEntity.this.getY() + Mth.nextDouble(RandomSource.create(), 4, 9);
                    tz = MartusEntity.this.getZ() + d * Math.sin(r);
                    if ((world1.getBlockState(BlockPos.containing(tx, ty, tz))).canBeReplaced()) {
                        if (world1 instanceof ServerLevel level)
                            FallingBlockEntity.fall(level, BlockPos.containing(tx, ty, tz), (new Object() {
                                public BlockState with(BlockState bs, Direction newValue) {
                                    Property<?> prop = bs.getBlock().getStateDefinition().getProperty("facing");
                                    if (prop instanceof DirectionProperty dp && dp.getPossibleValues().contains(newValue))
                                        return bs.setValue(dp, newValue);
                                    prop = bs.getBlock().getStateDefinition().getProperty("axis");
                                    return prop instanceof EnumProperty ep && ep.getPossibleValues().contains(newValue.getAxis()) ? bs.setValue(ep, newValue.getAxis()) : bs;
                                }
                            }.with(CABlocks.ABANDONED_SULPTURE.get().defaultBlockState(), new Object() {
                                public Direction getValue() {
                                    Direction dir = Direction.NORTH;
                                    int num = Mth.nextInt(RandomSource.create(), 1, 4);
                                    if (num == 1) {
                                        dir = Direction.EAST;
                                    } else if (num == 2) {
                                        dir = Direction.SOUTH;
                                    } else if (num == 3) {
                                        dir = Direction.WEST;
                                    }
                                    return dir;
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
        return super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Phase", this.entityData.get(DATA_PHASE));
        compound.putInt("Skillp1", this.entityData.get(DATA_SKILLP_1));
        compound.putInt("Skillp2", this.entityData.get(DATA_SKILLP_2));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Phase")) {
            this.entityData.set(DATA_PHASE, compound.getInt("Phase"));
        }
        if (compound.contains("Skillp1")) {
            this.entityData.set(DATA_SKILLP_1, compound.getInt("Skillp1"));
        }
        if (compound.contains("Skillp2")) {
            this.entityData.set(DATA_SKILLP_2, compound.getInt("Skillp2"));
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity tgt;
        double sklp1;
        double sklp2;
        double phase;
        double perc;
        if (this.isAlive()) {
            sklp1 = (Entity) this instanceof MartusEntity datEntI ? datEntI.getEntityData().get(DATA_SKILLP_1) : 0;
            sklp2 = (Entity) this instanceof MartusEntity datEntI ? datEntI.getEntityData().get(DATA_SKILLP_2) : 0;
            phase = (Entity) this instanceof MartusEntity datEntI ? datEntI.getEntityData().get(DATA_PHASE) : 0;
            if (tickCount % 12 == 0) {
                double num = 0;
                double limit;
                limit = 2;
                if (MapVariables.get(world).strategy_subsisting > 3) {
                    limit = 3;
                }
                {
                    final Vec3 center = new Vec3(x, y, z);
                    List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(96 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                    for (Entity entityiterator : entfound) {
                        if (!(entityiterator instanceof Mob livEnt1)) {
                            continue;
                        }
                        if (livEnt1.hasEffect(CAMobEffects.GUIDED_EVO.get())) {
                            num = num + 1;
                            this.spawnParticleLink(entityiterator);
                            CaerulaArborMod.queueServerWork(3, () -> {
                                this.spawnParticleLink(entityiterator);
                            });
                            CaerulaArborMod.queueServerWork(6, () -> {
                                this.spawnParticleLink(entityiterator);
                            });
                            CaerulaArborMod.queueServerWork(9, () -> {
                                this.spawnParticleLink(entityiterator);
                            });
                        }
                        if (num >= limit) {
                            break;
                        }
                    }
                }
            }
            if (tickCount % 100 == 0) {
                if (WorldUtils.hasNoSolidGroundWithin20Below(world, x, y, z)) {
                    push(0, (-0.64), 0);
                }
            }
            if (phase < 0.33) {
                if (tickCount % 2 == 0) {
                    this.spawnMartusParticleRim();
                }
                if (!this.hasEffect(CAMobEffects.INVULNERABLE.get())) {
                    if (!this.level().isClientSide())
                        this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 10000, 9, false, false));
                }
                if (sklp1 > 0) {
                    if ((Entity) this instanceof MartusEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_SKILLP_1, (int) (sklp1 - 1));
                    if (MapVariables.get(world).strategy_subsisting > 3) {
                        if ((Entity) this instanceof MartusEntity datEntSetI)
                            datEntSetI.getEntityData().set(DATA_SKILLP_1, (int) (sklp1 - 1));
                    }
                } else {
                    if (tickCount % 10 == 0) {
                        Entity result;
                        Entity tgt_ent = null;
                        double num = 0;
                        double max_h = 0;
                        double limit;
                        limit = 2;
                        if (MapVariables.get(world).strategy_subsisting > 3) {
                            limit = 3;
                        }
                        for (Entity entityiterator : world.getEntities(this, new AABB((x + 32), (y + 32), (z + 32), (x - 32), (y - 32), (z - 32)))) {
                            if (!(entityiterator instanceof Mob livEnt1)) {
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
                            if (livEnt1.getMaxHealth() > max_h) {
                                max_h = entityiterator instanceof LivingEntity _livEnt1 ? _livEnt1.getMaxHealth() : -1;
                                tgt_ent = entityiterator;
                            }
                        }
                        result = tgt_ent;
                        tgt = result;
                        if (!(tgt == null) && tgt.isAlive() && !tgt.getPersistentData().getBoolean("blessed")) {
                            if ((Entity) this instanceof MartusEntity datEntSetI)
                                datEntSetI.getEntityData().set(DATA_SKILLP_1, 400);
                            if (this instanceof MartusEntity) {
                                this.setAnimation("animation.martus.buff");
                            }
                            tgt.getPersistentData().putBoolean("blessed", true);
                            perc = (tgt instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) / (tgt instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1);
                            if (tgt instanceof LivingEntity livingEntity22 && livingEntity22.getAttributes().hasAttribute(Attributes.MAX_HEALTH)) {
                                livingEntity22.getAttribute(Attributes.MAX_HEALTH)
                                        .setBaseValue((livingEntity22.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? livingEntity22.getAttribute(Attributes.MAX_HEALTH).getBaseValue() : 0) * 2.5);
                            }
                            if (tgt instanceof LivingEntity livingEntity24 && livingEntity24.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
                                livingEntity24.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(
                                        ((tgt instanceof LivingEntity livingEntity23 && livingEntity23.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity23.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : 0) * 2.5));
                            if (tgt instanceof LivingEntity livingEntity26 && livingEntity26.getAttributes().hasAttribute(CAAttributes.SANITY_RATE.get()))
                                livingEntity26.getAttribute(CAAttributes.SANITY_RATE.get())
                                        .setBaseValue(((tgt instanceof LivingEntity livingEntity25 && livingEntity25.getAttributes().hasAttribute(CAAttributes.SANITY_RATE.get())
                                                ? livingEntity25.getAttribute(CAAttributes.SANITY_RATE.get()).getBaseValue()
                                                : 0) + 25));
                            if (tgt instanceof LivingEntity entity)
                                entity.setHealth((float) ((tgt instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * perc));
                            if (tgt instanceof LivingEntity && !this.level().isClientSide())
                                this.addEffect(new MobEffectInstance(CAMobEffects.GUIDED_EVO.get(), -1, 0));
                        }
                    }
                }
                if (tickCount % 300 == 0) {
                    martusTimedSpawn(world, x, y, z);
                }
            } else {
                if (((Entity) this instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) > ((Entity) this instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * 0.33) {
                    if (!((Entity) this instanceof LivingEntity livEnt33 && livEnt33.hasEffect(CAMobEffects.INVULNERABLE.get()))) {
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 20, 9, false, false));
                    }
                    if (tickCount % 2 == 0) {
                        this.spawnMartusParticleRim();
                    }
                }
                if (sklp1 > 0) {
                    if ((Entity) this instanceof MartusEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_SKILLP_1, (int) (sklp1 - 1));
                } else {
                    if (tickCount % 10 == 0 && EntityUtils.getSeabornAround(world, x, y, z, this) > 0) {
                        if ((Entity) this instanceof MartusEntity datEntSetI)
                            datEntSetI.getEntityData().set(DATA_SKILLP_1, 1000);
                        if (this instanceof MartusEntity) {
                            this.setAnimation("animation.martus.cure");
                        }
                        new Object() {
                            void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                                if (((Entity) MartusEntity.this instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) > ((Entity) MartusEntity.this instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * 0.02) {
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
                                final Vec3 center = new Vec3(x, y, z);
                                List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(64 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                                for (Entity entityiterator : entfound) {
                                    if (!(entityiterator instanceof Mob)) {
                                        continue;
                                    }
                                    if (!entityiterator.isAlive()) {
                                        continue;
                                    }
                                    if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))
                                            && !entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "bossoffspring")))) {
                                        if (entityiterator instanceof LivingEntity && !this.level().isClientSide())
                                            this.addEffect(new MobEffectInstance(CAMobEffects.FAKE_DEATH.get(), 200, 1));
                                        if (entityiterator instanceof LivingEntity entity && !this.level().isClientSide())
                                            this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 200, 0));
                                    }
                                    if (world instanceof ServerLevel level)
                                        level.sendParticles(ParticleTypes.DOLPHIN, (entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ()), 32, 0.85, 0.85, 0.85, 0.1);
                                }
                            }
                        });
                    }
                }
                if (sklp2 > 0) {
                    if ((Entity) this instanceof MartusEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_SKILLP_2, (int) (sklp2 - 1));
                } else {
                    if ((Entity) this instanceof MartusEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_SKILLP_2, 600);
                    if (this instanceof MartusEntity) {
                        this.setAnimation("animation.martus.reject");
                    }
                    CaerulaArborMod.queueServerWork(15, () -> {
                        Entity tgt_ent;
                        double max_h = 0;
                        tgt_ent = this.getTarget();
                        if (tgt_ent == null || tgt_ent instanceof LivingEntity livEnt2 && livEnt2.hasEffect(CAMobEffects.SUB_HAEMO.get())) {
                            tgt_ent = ((Entity) this instanceof LivingEntity entity) ? entity.getLastHurtByMob() : null;
                        }
                        if (tgt_ent == null || tgt_ent instanceof LivingEntity livEnt5 && livEnt5.hasEffect(CAMobEffects.SUB_HAEMO.get())) {
                            for (Entity entityiterator : world.getEntities(this, new AABB((x + 32), (y + 32), (z + 32), (x - 32), (y - 32), (z - 32)))) {
                                if (!(entityiterator instanceof Mob)) {
                                    continue;
                                }
                                if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                                    continue;
                                }
                                if (entityiterator instanceof LivingEntity livEnt8 && livEnt8.hasEffect(CAMobEffects.SUB_HAEMO.get())) {
                                    continue;
                                }
                                if ((entityiterator instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) > max_h) {
                                    max_h = entityiterator instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1;
                                    tgt_ent = entityiterator;
                                }
                            }
                        }
                        if (!(tgt_ent == null) && !(tgt_ent instanceof LivingEntity livEnt13 && livEnt13.hasEffect(CAMobEffects.SUB_HAEMO.get()))) {
                            if (tgt_ent instanceof LivingEntity && !this.level().isClientSide())
                                this.addEffect(new MobEffectInstance(CAMobEffects.SUB_HAEMO.get(), 800, 0));
                        }
                    });
                }
                if (tickCount % 200 == 0) {
                    martusTimedSpawn(world, x, y, z);
                }
            }
        }
        this.refreshDimensions();
        if (this.releaseTime > 0) this.releaseTime--;
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
        if (EntityUtils.getSeabornAround(world, x, y, z, this) < (world.getLevelData().getGameRules().getInt(CAGameRules.CLONE_NUMBER_LIMIT))) {
            for (int index0 = 0; index0 < 2; index0++) {
                SeabornSpawnManager.summonRandomSeaborn(world, 0.33, x, y, z);
                if (world instanceof ServerLevel level)
                    level.sendParticles(ParticleTypes.CLOUD, x, y, z, 18, 0.6, 0.6, 0.6, 0.16);
            }
        }
    }

    private void spawnMartusParticleRim() {
        double angleOffset = Mth.nextInt(RandomSource.create(), 0, 59);
        for (int index0 = 0; index0 < 60; index0++) {
            double angle = angleOffset + index0 * 6;
            double radius = 2.5 + 0.5 * Math.sin(Math.toRadians(index0 * 24));
            double particleX = this.getX() + radius * Math.sin(Math.toRadians(angle));
            double particleZ = this.getZ() + radius * Math.cos(Math.toRadians(angle));
            this.level().addParticle(CAParticles.MARTUS_CHARS.get(), particleX, this.getY() + 1, particleZ, 0, 0.15, 0);
            this.level().addParticle(CAParticles.MARTUS_CHARS.get(), particleX, this.getY() + 0.8, particleZ, 0, -0.08, 0);
        }
    }

    private void spawnParticleLink(Entity target) {
        if (!this.isAlive() || target == null || !(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        double vx = target.getX() - this.getX();
        double vy = target.getY() + target.getBbHeight() * 0.5 - (this.getY() + this.getBbHeight() * 0.5);
        double vz = target.getZ() - this.getZ();
        double size = Math.max(Math.min(Math.round(Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2) + Math.pow(vz, 2))), 32), 1) * 3;
        for (int index0 = 0; index0 < (int) size; index0++) {
            double particleX = this.getX() + vx / size * index0;
            double particleY = this.getY() + vy / size * index0 + this.getBbHeight() * 0.5;
            double particleZ = this.getZ() + vz / size * index0;
            if (Math.random() > 0.5) {
                serverLevel.sendParticles(ParticleTypes.END_ROD, particleX, particleY, particleZ, 1, 0.08, 0.08, 0.08, 0);
            } else {
                serverLevel.sendParticles(ParticleTypes.FIREWORK, particleX, particleY, particleZ, 1, 0.08, 0.08, 0.08, 0);
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
        builder = builder.add(CAAttributes.GENERAL_DEFENSE.get(), 16384);
        builder = builder.add(CAAttributes.MAGIC_RESISTANCE.get(), 100);
        return builder;
    }

    private PlayState movementPredicate(AnimationState<?> event) {
        if (this.animationprocedure.equals("empty")) {
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.martus.die"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.martus.idle"));
        }
        return PlayState.STOP;
    }

    private PlayState attackingPredicate(AnimationState<?> event) {
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

    private PlayState procedurePredicate(AnimationState<?> event) {
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
            this.remove(RemovalReason.KILLED);
            this.dropExperience();
        }
    }

    @Override
    public void remove(RemovalReason pReason) {
        if (this.level().getDifficulty() != Difficulty.PEACEFUL && pReason == RemovalReason.DISCARDED) {
            this.hurt(
                    CADamageTypes.source(this.level(), CADamageTypes.OCEANKILLER_DAMAGE),
                    20
            );
            return;
        }
        super.remove(pReason);
    }

    @Override
    public void heal(float amount) {
        super.heal(0);
    }

    @Override
    public void setHealth(float pHealth) {
        if (this.releaseTime > 0) super.setHealth(pHealth);
        if (this.hasEffect(CAMobEffects.INVULNERABLE.get()) && pHealth < this.getHealth()) return;
        super.setHealth(pHealth);
    }

    public String getSyncedAnimation() {
        return this.entityData.get(DATA_ANIMATION);
    }

    public void setAnimation(String animation) {
        this.entityData.set(DATA_ANIMATION, animation);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
        data.add(new AnimationController<>(this, "attacking", 4, this::attackingPredicate));
        data.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
    }


    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}
