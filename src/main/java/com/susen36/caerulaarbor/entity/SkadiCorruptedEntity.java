package com.susen36.caerulaarbor.entity;

import com.susen36.babel.init.BabelAttributes;
import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.init.*;
import com.susen36.caerulaarbor.manager.SeabornSpawnManager;
import com.susen36.caerulaarbor.util.EntityUtils;
import com.susen36.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SkadiCorruptedEntity extends SeaMonster {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(SkadiCorruptedEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(SkadiCorruptedEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_CONVERT_P = SynchedEntityData.defineId(SkadiCorruptedEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> DATA_MAY_CORRUPT = SynchedEntityData.defineId(SkadiCorruptedEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(SkadiCorruptedEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_CONVERT_TICK = SynchedEntityData.defineId(SkadiCorruptedEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_DEAL = SynchedEntityData.defineId(SkadiCorruptedEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_PHASE = SynchedEntityData.defineId(SkadiCorruptedEntity.class, EntityDataSerializers.INT);
    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.RED, ServerBossEvent.BossBarOverlay.NOTCHED_6);
    public String animationprocedure = "empty";
    String prevAnim = "empty";
    private boolean swinging;
    private long lastSwing;

    public SkadiCorruptedEntity(Level world) {
        this(CAEntities.SKADI_CORRUPTED.get(), world);
    }

    public SkadiCorruptedEntity(EntityType<SkadiCorruptedEntity> type, Level world) {
        super(type, world);
        xpReward = 0;
        setNoAi(false);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1f);
        setPersistenceRequired();
    }

    public static void corruptedSpawnMobs(LevelAccessor world, double x, double y, double z, double count) {
        double spawnX;
        double spawnY;
        double spawnZ;
        double radius;
        double angle;
        if (EntityUtils.getSeabornNum(world, x, y, z) >= world.getLevelData().getGameRules().getInt(CAGameRules.CLONE_NUMBER_LIMIT)) {
            return;
        }
        for (int spawnIndex = 0; spawnIndex < (int) count; spawnIndex++) {
            for (int attempt = 0; attempt < 8; attempt++) {
                radius = Mth.nextInt(RandomSource.create(), 4, 16);
                angle = Mth.nextDouble(RandomSource.create(), 0, 6.283);
                spawnX = x + radius * Math.sin(angle);
                spawnZ = z + radius * Math.cos(angle);
                spawnY = WorldUtils.findValidSpawnY(world, x, y, z, spawnX, y, spawnZ);
                if (!Double.isNaN(spawnY)) {
                    SeabornSpawnManager.summonRandomSeaborn(world, 0.33, spawnX, spawnY, spawnZ);
                    if (world instanceof ServerLevel level) {
                        level.sendParticles(ParticleTypes.CLOUD, spawnX, spawnY + 0.75, spawnZ, 64, 0.75, 0.75, 0.75, 0.1);
                    }
                    break;
                }
            }
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.12);
        builder = builder.add(Attributes.MAX_HEALTH, 405);
        builder = builder.add(Attributes.ARMOR, 9);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 15);
        builder = builder.add(Attributes.FOLLOW_RANGE, 36);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
        builder = builder.add(BabelAttributes.ELEMENTAL_MODIFIER, 0.02);
        builder = builder.add(CAAttributes.MAGIC_RESISTANCE, 30);
        builder = builder.add(BabelAttributes.MAX_ELEMENTAL_VALUE, 2000);
        return builder;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHOOT, false);
        builder.define(DATA_ANIMATION, "undefined");
        builder.define(DATA_CONVERT_P, 900);
        builder.define(DATA_MAY_CORRUPT, true);
        builder.define(DATA_DURATION, 0);
        builder.define(DATA_CONVERT_TICK, 1000);
        builder.define(DATA_DEAL, 0);
        builder.define(DATA_PHASE, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this,  1, false) {

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
                    List<LivingEntity> foundEntities = this.level().getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(6), entity -> true);
                    for (LivingEntity entityIterator : foundEntities) {
                        if (entityIterator == this) {
                            continue;
                        }
                        if (entityIterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring")))) {
                            continue;
                        }
                        if (this.distanceToSqr(entityIterator) <= 9) {
                            entityIterator.hurt(
                                    CADamageTypes.source(this.level(), CADamageTypes.GENERIC_SEABORN_ATTACK, this), (float) damage);
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
                    this.getEntityData().set(DATA_DURATION, 40);
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
            if (this.getEntityData().get(DATA_PHASE) > 1.5) {
                return damaged;
            }
            double accumulatedDamage = this.getEntityData().get(DATA_DEAL) + newAmount;
            this.getEntityData().set(DATA_DEAL, (int) accumulatedDamage);
            if (accumulatedDamage >= this.getMaxHealth() * 0.7) {
                this.getEntityData().set(DATA_MAY_CORRUPT, false);
                if (this.getEntityData().get(DATA_PHASE) > 0.5) {
                    return damaged;
                }
                this.setAnimation("animation.skadi_corrupted.convert_in_1");
                if (!this.level().isClientSide())
                    this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 9999, 9, false, false));
                this.getEntityData().set(DATA_DURATION, 10000);
                this.getEntityData().set(DATA_CONVERT_TICK, 30);
                this.getEntityData().set(DATA_CONVERT_P, 10000);
            } else {
                this.getEntityData().set(DATA_MAY_CORRUPT, true);
            }
        }
        return damaged;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("ConvertP", this.entityData.get(DATA_CONVERT_P));
        compound.putBoolean("MayCorrupt", this.entityData.get(DATA_MAY_CORRUPT));
        compound.putInt("Duration", this.entityData.get(DATA_DURATION));
        compound.putInt("ConvertTick", this.entityData.get(DATA_CONVERT_TICK));
        compound.putInt("Deal", this.entityData.get(DATA_DEAL));
        compound.putInt("Phase", this.entityData.get(DATA_PHASE));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("ConvertP")) {
            this.entityData.set(DATA_CONVERT_P, compound.getInt("ConvertP"));
        }
        if (compound.contains("MayCorrupt")) {
            this.entityData.set(DATA_MAY_CORRUPT, compound.getBoolean("MayCorrupt"));
        }
        if (compound.contains("Duration")) {
            this.entityData.set(DATA_DURATION, compound.getInt("Duration"));
        }
        if (compound.contains("ConvertTick")) {
            this.entityData.set(DATA_CONVERT_TICK, compound.getInt("ConvertTick"));
        }
        if (compound.contains("Deal")) {
            this.entityData.set(DATA_DEAL, compound.getInt("Deal"));
        }
        if (compound.contains("Phase")) {
            this.entityData.set(DATA_PHASE, compound.getInt("Phase"));
        }
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
        converT = (Entity) this instanceof SkadiCorruptedEntity datEntI ? datEntI.getEntityData().get(DATA_CONVERT_TICK) : 0;
        if (converT < 999) {
            if (converT > 0) {
                if ((Entity) this instanceof SkadiCorruptedEntity datEntSetL)
                    datEntSetL.getEntityData().set(DATA_MAY_CORRUPT, false);
                if ((Entity) this instanceof SkadiCorruptedEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_CONVERT_TICK, (int) (converT - 1));
            } else if (phase < 0.5) {
                for (Entity entityiterator : new ArrayList<>(world.players())) {
                    if ((entityiterator != null ? distanceTo(entityiterator) : -1) < 32) {
                        if (entityiterator instanceof Player player && !player.level().isClientSide())
                            player.displayClientMessage(Component.literal((Component.translatable("entity.caerula_arbor.skadi_corrupted.convert").getString())), false);
                    }
                }
                if (world instanceof ServerLevel level) {
                    ItemEntity entityToSpawn = new ItemEntity(level, x, y, z, new ItemStack(CAItems.INCANDESCENT_ANIMA.get()));
                    entityToSpawn.setPickUpDelay(10);
                    entityToSpawn.setUnlimitedLifetime();
                    level.addFreshEntity(entityToSpawn);
                }
                if (world instanceof ServerLevel level) {
                    ItemEntity entityToSpawn = new ItemEntity(level, x, y, z, new ItemStack(CAItems.RECORD_UNDERTIDES.get()));
                    entityToSpawn.setPickUpDelay(10);
                    entityToSpawn.setUnlimitedLifetime();
                    level.addFreshEntity(entityToSpawn);
                }
                if (world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), CASounds.CORRUPTED_CONVERT.get(), SoundSource.HOSTILE, 2, 1);
                }
                if ((Entity) this instanceof SkadiCorruptedEntity datEntSetL)
                    datEntSetL.getEntityData().set(DATA_MAY_CORRUPT, false);
                if (!level().isClientSide())
                    discard();
                this.spawnHurtSkadi(world, x, y, z);
            }
        }
        if (this.isAlive()) {
            if (!this.level().isClientSide())
                this.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20, 0, false, false));
            conv = (Entity) this instanceof SkadiCorruptedEntity datEntI ? datEntI.getEntityData().get(DATA_CONVERT_P) : 0;
            dura = (Entity) this instanceof SkadiCorruptedEntity datEntI ? datEntI.getEntityData().get(DATA_DURATION) : 0;
            deal = (Entity) this instanceof SkadiCorruptedEntity datEntI ? datEntI.getEntityData().get(DATA_DEAL) : 0;
            phase = (Entity) this instanceof SkadiCorruptedEntity datEntI ? datEntI.getEntityData().get(DATA_PHASE) : 0;
            if (dura > 0) {
                if ((Entity) this instanceof SkadiCorruptedEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_DURATION, (int) (dura - 1));
            }
            if (conv > 0) {
                if (phase < 1.9) {
                    if ((Entity) this instanceof SkadiCorruptedEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_CONVERT_P, (int) (conv - 1));
                }
            } else if (dura <= 0) {
                if (phase < 0.5) {
                    if (this instanceof SkadiCorruptedEntity) {
                        this.setAnimation("animation.skadi_corrupted.to_phase_2");
                    }
                    if ((Entity) this instanceof SkadiCorruptedEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_CONVERT_P, 1120);
                    if (this instanceof LivingEntity entity)
                        entity.setHealth(this instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1);
                    if ((Entity) this instanceof SkadiCorruptedEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_PHASE, 1);
                    if ((Entity) this instanceof SkadiCorruptedEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_DEAL, 0);
                    if ((Entity) this instanceof SkadiCorruptedEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_DURATION, 60);
                    if (this.getAttributes().hasAttribute(Attributes.ARMOR))
                        this.getAttribute(Attributes.ARMOR)
                                .setBaseValue(((this.getAttributes().hasAttribute(Attributes.ARMOR) ? this.getAttribute(Attributes.ARMOR).getBaseValue() : 0) * 1.5));
                    if ((Entity) this instanceof SkadiCorruptedEntity datEntSetL)
                        datEntSetL.getEntityData().set(DATA_MAY_CORRUPT, true);
                    if (!this.level().isClientSide())
                        this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 60, 9, false, false));
                    if (!world.isClientSide()) {
                        if (world instanceof Level level) {
                            level.playSound(null, BlockPos.containing(x, y, z), CASounds.SILENCE3.get(), SoundSource.HOSTILE, 2, 1);
                        }
                    }
                } else if (phase < 1.5) {
                    if (this instanceof SkadiCorruptedEntity) {
                        this.setAnimation("animation.skadi_corrupted.to_phase_3");
                    }
                    if ((Entity) this instanceof SkadiCorruptedEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_CONVERT_P, 99999);
                    if ((Entity) this instanceof LivingEntity entity)
                        entity.setHealth((Entity) this instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1);
                    if ((Entity) this instanceof SkadiCorruptedEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_PHASE, 2);
                    if ((Entity) this instanceof SkadiCorruptedEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_DEAL, 0);
                    if ((Entity) this instanceof SkadiCorruptedEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_DURATION, 80);
                    if (this.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE))
                        this.getAttribute(CAAttributes.MAGIC_RESISTANCE)
                                .setBaseValue(((this.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE)
                                        ? this.getAttribute(CAAttributes.MAGIC_RESISTANCE).getBaseValue()
                                        : 0) + 50));
                    if (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
                        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(
                                ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : 0) * 1.25));
                    if ((Entity) this instanceof SkadiCorruptedEntity datEntSetL)
                        datEntSetL.getEntityData().set(DATA_MAY_CORRUPT, true);
                    if (!this.level().isClientSide())
                        this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 80, 9, false, false));
                    if (!world.isClientSide()) {
                        if (world instanceof Level level) {
                            level.playSound(null, BlockPos.containing(x, y, z), CASounds.SILENCE4.get(), SoundSource.HOSTILE, 2, 1);
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
                        final Vec3 center = new Vec3(x, y, z);
                        List<LivingEntity> entfound = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(24), e -> true);
                        for (LivingEntity entityiterator : entfound) {
                            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring")))) {
                                if (!(entityiterator == enemy1)) {
                                    continue;
                                }
                            }
                            if (entityiterator == this) {
                                continue;
                            }
                            if (entityiterator instanceof Player player && (player.isCreative() || player.isSpectator())) {
                                continue;
                            }
                            if (distanceToSqr(entityiterator) <= 144) {
                                entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.SANITY_BREAK),
                                        (float) (ddd * 1.1));
                            }
                        }
                    }
                    dama = ((Entity) this instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * 0.01;
                    if (((Entity) this instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) > dama) {
                        if ((Entity) this instanceof LivingEntity entity)
                            entity.setHealth((float) (((Entity) this instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) - dama));
                    } else {
                        this.hurt(CADamageTypes.source(world, CADamageTypes.OCEANKILLER_DAMAGE), 99999);
                    }
                }
                double ang;
                double r;
                double t;
                t = tickCount % 90;
                for (int index0 = 0; index0 < 20; index0++) {
                    ang = Math.toRadians(index0 * 6 + t * 4);
                    r = 11.5 + Math.sin(index0 * 12);
                    if (world instanceof ServerLevel level)
                        level.sendParticles(CAParticles.CORRUPTED_FISH.get(), (x + r * Math.sin(ang)), (y + 0.15), (z + r * Math.cos(ang)), 1, 0, 0.25, 0, 0.2);
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
                        if ((Entity) this instanceof SkadiCorruptedEntity datEntSetI)
                            datEntSetI.getEntityData().set(DATA_DEAL, (int) (((Entity) this instanceof SkadiCorruptedEntity datEntI ? datEntI.getEntityData().get(DATA_DEAL) : 0) - ddd * healPerc * 3));
                    }
                }
                {
                    final Vec3 center = new Vec3(x, y, z);
                    List<LivingEntity> entfound = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(32), e -> true);
                    for (LivingEntity entityiterator : entfound) {
                        mayBonus = false;
                        isSeaborn = false;
                        if (distanceToSqr(entityiterator) <= 256) {
                            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring")))) {
                                mayBonus = true;
                                isSeaborn = true;
                            }
                            if (phase <= 1 && entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "is_humanside")))) {
                                mayBonus = true;
                                isSeaborn = false;
                            }
                            if (entityiterator == this) {
                                continue;
                            }
                            if (mayBonus) {
                                EntityUtils.heal(entityiterator, ddd * healPerc);
                                if (phase == 1 && !entityiterator.getPersistentData().getBoolean("corruptedBonus1")) {
                                    if (entityiterator.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
                                        entityiterator.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(
                                                (entityiterator.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? entityiterator.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : 0)
                                                        + ddd * 0.4);
                                    if (entityiterator.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE))
                                        entityiterator.getAttribute(CAAttributes.GENERAL_DEFENSE)
                                                .setBaseValue((entityiterator.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE)
                                                        ? entityiterator.getAttribute(CAAttributes.GENERAL_DEFENSE).getBaseValue()
                                                        : 0) + ddd * 0.4);
                                    entityiterator.getPersistentData().putBoolean("corruptedBonus1", true);
                                }
                                if (phase == 2 && !entityiterator.getPersistentData().getBoolean("corruptedBonus2")) {
                                    if (entityiterator.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
                                        entityiterator.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(
                                                (entityiterator.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? entityiterator.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : 0)
                                                        + ddd);
                                    if (entityiterator.getAttributes().hasAttribute(Attributes.MAX_HEALTH))
                                        entityiterator.getAttribute(Attributes.MAX_HEALTH).setBaseValue(
                                                (entityiterator.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? entityiterator.getAttribute(Attributes.MAX_HEALTH).getBaseValue() : 0) + ddd);
                                    entityiterator.getPersistentData().putBoolean("corruptedBonus2", true);
                                }
                                if (phase > 0.5 && isSeaborn) {
                                    if (!entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "bossoffspring")))
                                            && !entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanpet")))) {
                                        if (entityiterator instanceof Mob entity && enemy1 instanceof LivingEntity ent)
                                            entity.setTarget(ent);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (tickCount % gap == 99) {
                assert Boolean.TRUE; //#dbg:SkadiCorruptedSkills:corruptedSpawnCheck
                corruptedSpawnMobs(world, x, y, z, nn);
            }
            double phase1;
            double ang;
            double r;
            ang = Mth.nextDouble(RandomSource.create(), 0, 6.283);
            phase1 = (Entity) this instanceof SkadiCorruptedEntity datEntI ? datEntI.getEntityData().get(DATA_PHASE) : 0;
            for (int index0 = 0; index0 < (int) (phase1 + 1); index0++) {
                r = Mth.nextDouble(RandomSource.create(), 2, 3.5);
                if (world instanceof ServerLevel level)
                    level.sendParticles(CAParticles.CORRUPTED_FISH.get(), (x + r * Math.sin(ang)), (y + 0.25), (z + r * Math.cos(ang)), 1, 0, 0, 0, 0.2);
            }
            if (!(phase > 1.5)) {
                LivingEntity livEnt = this;
                if (deal >= livEnt.getMaxHealth() * 0.75) {
                    if ((Entity) this instanceof SkadiCorruptedEntity datEntSetL)
                        datEntSetL.getEntityData().set(DATA_MAY_CORRUPT, false);
                } else {
                    if ((Entity) this instanceof SkadiCorruptedEntity datEntSetL)
                        datEntSetL.getEntityData().set(DATA_MAY_CORRUPT, true);
                }
            }
        }
        if (entityData.get(DATA_MAY_CORRUPT) || getPhase() >= 2) bossInfo.setColor(ServerBossEvent.BossBarColor.RED);
        else bossInfo.setColor(ServerBossEvent.BossBarColor.BLUE);
        this.refreshDimensions();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player.getMainHandItem().getItem() == CAItems.CORRUPTED_HEART_SPAWNER.get()) {
            entityData.set(DATA_CONVERT_P, 1);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean canUsePortal(boolean allowVehicles) {
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
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata);
        double x = this.getX(), y = this.getY(), z = this.getZ();
        if (world instanceof Level level && !level.isClientSide()) {
            level.playSound(
                    null, BlockPos.containing(x, y, z),
                    CASounds.SILENCE2.get(),
                    SoundSource.NEUTRAL, 1, 1);
        }
        return retval;
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
        return this.isAlive() && this.getEntityData().get(DATA_DURATION) <= 0;
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
            this.dropExperience(this.getKillCredit());
            LevelAccessor world = this.level();
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();
            if ((Entity) this instanceof SkadiCorruptedEntity datEntL0 && datEntL0.getEntityData().get(DATA_MAY_CORRUPT)) {
                if (!world.isClientSide()) {
                    if (world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), CASounds.CORRUPTED_CORRUPT.get(), SoundSource.HOSTILE, 2, 1);
                    }
                }
                for (Entity entityiterator : new ArrayList<>(world.players())) {
                    if ((entityiterator != null ? distanceTo(entityiterator) : -1) < 32) {
                        if (entityiterator instanceof Player player && !player.level().isClientSide())
                            player.displayClientMessage(Component.literal((Component.translatable("entity.caerula_arbor.skadi_corrupted.corrupted").getString())), false);
                    }
                }
                if (world instanceof ServerLevel level) {
                    Entity entityToSpawn = CAEntities.ISHARMLA.get().spawn(level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                    if (entityToSpawn != null) {
                        entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                    }
                }
            } else {
                if (!world.isClientSide()) {
                    if (world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), CASounds.CORRUPTED_CONVERT.get(), SoundSource.HOSTILE, 2, 1);
                    }
                }
                for (Entity entityiterator : new ArrayList<>(world.players())) {
                    if ((entityiterator != null ? distanceTo(entityiterator) : -1) < 32) {
                        if (entityiterator instanceof Player player && !player.level().isClientSide())
                            player.displayClientMessage(Component.literal((Component.translatable("entity.caerula_arbor.skadi_corrupted.convert").getString())), false);
                    }
                }
                if (world instanceof ServerLevel level) {
                    ItemEntity entityToSpawn = new ItemEntity(level, x, y, z, new ItemStack(CAItems.INCANDESCENT_ANIMA.get()));
                    entityToSpawn.setPickUpDelay(10);
                    entityToSpawn.setUnlimitedLifetime();
                    level.addFreshEntity(entityToSpawn);
                }
                this.spawnHurtSkadi(world, x, y, z);
            }
            if (world instanceof ServerLevel level) {
                ItemEntity entityToSpawn = new ItemEntity(level, x, y, z, new ItemStack(CAItems.RECORD_UNDERTIDES.get()));
                entityToSpawn.setPickUpDelay(10);
                entityToSpawn.setUnlimitedLifetime();
                level.addFreshEntity(entityToSpawn);
            }
        }
    }

    @Override
    public void setHealth(float pHealth) {
        if (getPhase() < 2 && pHealth <= 0 && entityData.get(DATA_MAY_CORRUPT)) {
            entityData.set(DATA_CONVERT_P, 1);
            super.setHealth(this.getMaxHealth());
            return;
        }
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
        data.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
        data.add(new AnimationController<>(this, "attacking", 0, this::attackingPredicate));
        data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
    }

    public int getPhase() {
        return entityData.get(DATA_PHASE);
    }


    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}