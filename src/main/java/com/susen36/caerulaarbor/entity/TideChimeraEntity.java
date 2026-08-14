package com.susen36.caerulaarbor.entity;

import com.susen36.babel.elemental.base.AbstractEPCapability;
import com.susen36.babel.init.BabelAttributes;
import com.susen36.babel.manager.EPManager;
import com.susen36.babel.util.EPUtils;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.base.SeaMonsterBoss;
import com.susen36.caerulaarbor.entity.bullets.*;
import com.susen36.caerulaarbor.init.*;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

public class TideChimeraEntity extends SeaMonsterBoss {

    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(TideChimeraEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(TideChimeraEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(TideChimeraEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_SUMMON_P = SynchedEntityData.defineId(TideChimeraEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_SKILL_P = SynchedEntityData.defineId(TideChimeraEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_DEAL = SynchedEntityData.defineId(TideChimeraEntity.class, EntityDataSerializers.INT);
    public String animationprocedure = "empty";
    String prevAnim = "empty";
    private boolean swinging;
    private long lastSwing;

    public TideChimeraEntity(Level world) {
        this(CAEntities.TIDE_CHIMERA.get(), world);
    }

    public TideChimeraEntity(EntityType<TideChimeraEntity> type, Level world) {
        super(type, world);
        this.bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.BLUE, ServerBossEvent.BossBarOverlay.NOTCHED_12);
        xpReward = 99;
        setNoAi(false);
        setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHOOT, false);
        builder.define(DATA_ANIMATION, "undefined");
        builder.define(DATA_DURATION, 0);
        builder.define(DATA_SUMMON_P, 3);
        builder.define(DATA_SKILL_P, 200);
        builder.define(DATA_DEAL, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this,  1, false) {

            @Override
            public boolean canUse() {
                return super.canUse() && isChimeraDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isChimeraDurative();
            }

        });
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1) {
            @Override
            public boolean canUse() {
                return super.canUse() && isChimeraDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isChimeraDurative();
            }
        });
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && isChimeraDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isChimeraDurative();
            }
        });
        this.goalSelector.addGoal(7, new FloatGoal(this));
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource source) {
        return CASounds.SEABORN_GENERIC_HIT.get();
    }

    @Override
    public SoundEvent getDeathSound() {
        return CASounds.APOCATA_DIE.get();
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        double targetX = target.getX();
        double targetY = target.getY();
        double targetZ = target.getZ();
        if (!this.level().isClientSide()) {
            CaerulaArbor.queueServerWork(8, () -> {
                if (this.isAlive() && target.isAlive() && this.distanceTo(target) <= 4) {
                    if (target instanceof LivingEntity livingTarget) {
                        EPManager.hurtElemental(
                                livingTarget,
                                AbstractEPCapability.EPType.CORROSION,
                                this,
                                Mth.floor(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.35D + 4.0D)
                        );
                    }
                    this.level().playSound(null, BlockPos.containing(targetX, targetY, targetZ),
                            CASounds.PUNCTUREFISH_ATTACK.get(), SoundSource.HOSTILE, 3,
                            (float) Mth.nextDouble(RandomSource.create(), 0.9, 1.1));
                    if (target.hurt(
                            CADamageTypes.source(this.level(), CADamageTypes.GENERIC_SEABORN_ATTACK, this), (float) (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0))) {
                        int amplifier = this.hasEffect(CAMobEffects.REEF_CRACKER) ? this.getEffect(CAMobEffects.REEF_CRACKER).getAmplifier() : -1;
                        int nextAmplifier = Mth.clamp(amplifier + 1, 0, 31);
                        this.addEffect(new MobEffectInstance(CAMobEffects.REEF_CRACKER, 100, nextAmplifier, false, false));
                    }
                }
            });
        }
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        LevelAccessor world = this.level();
        Entity sourceentity = source.getEntity();
        if (sourceentity != null) {
            if (!(sourceentity instanceof Player) && !(sourceentity instanceof ApocataEntity)) {
                if (!sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "seaborn")))) {
                    {
                        final Vec3 center = new Vec3(this.getX(), this.getY(), this.getZ());
                        List<LivingEntity> entfound = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(16), e -> true);
                        for (LivingEntity entityiterator : entfound) {
                            if (entityiterator == this) {
                                continue;
                            }
                            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "seaborn")))) {
                                if (entityiterator instanceof Mob entity && sourceentity instanceof LivingEntity ent)
                                    entity.setTarget(ent);
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
            double accumulatedDamage = this.getEntityData().get(DATA_DEAL) + amount;
            this.getEntityData().set(DATA_DEAL, (int) accumulatedDamage);
            if (accumulatedDamage >= this.getMaxHealth() * 0.25) {
                this.performRangedSanityAttack();
                this.getEntityData().set(DATA_DEAL, 0);
            }
        }
        return damaged;
    }

    protected void performRangedSanityAttack() {
        Level level = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        double attackDamage = (Entity) this instanceof LivingEntity livingEntity && livingEntity.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;

        float selfDamage = this.getMaxHealth() * 0.3f;
        this.setHealth(this.getHealth() - selfDamage);

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.EXPLOSION, x, y + 1, z, 1, 0, 0, 0, 0.5);
            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y + 1, z, 1, 0, 0, 0, 0.5);
        }

        level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 3.0F, 1.0F);

        Vec3 centerPos = new Vec3(x, y + 1, z);
        double radius = 3.0D;
        List<Entity> nearbyEntities = level.getEntitiesOfClass(Entity.class, new AABB(centerPos, centerPos).inflate(radius), entity -> entity != this).stream().sorted(Comparator.comparingDouble(entity -> entity.distanceToSqr(centerPos))).toList();
        for (Entity entity : nearbyEntities) {
            if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "seaborn")))) {
                continue;
            }
            if (!(entity instanceof LivingEntity)) {
                continue;
            }
            entity.hurt(CADamageTypes.source(level, CADamageTypes.OCEAN_MAGIC, this),
                    (float) attackDamage);
            if ((Entity) this instanceof LivingEntity attacker && entity instanceof LivingEntity target) {
                EPUtils.causeSanityInjury(target, attacker, attackDamage * 150);
            }
        }
    }
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        if (!this.level().isClientSide())
            this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 100, 9, false, false));
        if (this instanceof TideChimeraEntity) {
            this.setAnimation("animation.super_apocata.start");
        }
        CaerulaArbor.queueServerWork(36, () -> {
            if (this.isAlive()) {
                if (world instanceof Level level) {
                    if (!level.isClientSide()) {
                        level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.ANVIL_LAND, SoundSource.HOSTILE, (float) 2.5, 1);
                    } else {
                        level.playLocalSound(x, y, z, SoundEvents.ANVIL_LAND, SoundSource.HOSTILE, (float) 2.5, 1, false);
                    }
                }
            }
        });
        CaerulaArbor.queueServerWork(60, () -> {
            if (this.isAlive()) {
                if (world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.SLIME_JUMP, SoundSource.HOSTILE, (float) 2.5, 1);
                }
            }
        });
        CaerulaArbor.queueServerWork(66, () -> {
            if (this.isAlive()) {
                if (world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.SLIME_JUMP, SoundSource.HOSTILE, (float) 2.5, 1);
                }
            }
        });
        CaerulaArbor.queueServerWork(83, () -> {
            if (this.isAlive()) {
                if (world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.SLIME_BLOCK_PLACE, SoundSource.HOSTILE, (float) 2.5, 1);
                }
            }
        });
        return super.finalizeSpawn(world, difficulty, reason, livingdata);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Duration", this.entityData.get(DATA_DURATION));
        compound.putInt("SummonP", this.entityData.get(DATA_SUMMON_P));
        compound.putInt("SkillP", this.entityData.get(DATA_SKILL_P));
        compound.putInt("Deal", this.entityData.get(DATA_DEAL));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Duration")) {
            this.entityData.set(DATA_DURATION, compound.getInt("Duration"));
        }
        if (compound.contains("SummonP")) {
            this.entityData.set(DATA_SUMMON_P, compound.getInt("SummonP"));
        }
        if (compound.contains("SkillP")) {
            this.entityData.set(DATA_SKILL_P, compound.getInt("SkillP"));
        }
        if (compound.contains("Deal")) {
            this.entityData.set(DATA_DEAL, compound.getInt("Deal"));
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity target;
        double sklp1;
        double dura;
        double tap;
        double perc;
        boolean maySummon = false;
        LivingEntity livEnt = this;
        if (livEnt.deathTime == 10) {
            if (world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.HOSTILE, 2, 1);
            }
            Entity entityToSpawn;
            BlockPos pos = BlockPos.containing(x, y, z);
            if (world instanceof ServerLevel level) {
                entityToSpawn = CAEntities.CRACKER_ABYSSAL.get().spawn(level, pos, MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                }
                entityToSpawn = CAEntities.GUIDE_ABYSSAL.get().spawn(level, pos, MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                }
                entityToSpawn = CAEntities.UMBRELLA_ABYSSAL.get().spawn(level, pos, MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                }
                entityToSpawn = CAEntities.POCKET_SEA_CREEPER.get().spawn(level, pos, MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                }
                entityToSpawn = CAEntities.PREGNANT_FISH.get().spawn(level, pos, MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                }
                entityToSpawn = CAEntities.BASELAYER_ABYSSAL.get().spawn(level, pos, MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                }
                entityToSpawn = CAEntities.PREDATOR_ABYSSAL.get().spawn(level, pos, MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                }
                entityToSpawn = CAEntities.SPLASHER_ABYSSAL.get().spawn(level, pos, MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                }
                entityToSpawn = CAEntities.APOSTLE_PROKARYOTE.get().spawn(level, pos, MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                }
                entityToSpawn = CAEntities.ACCUMULATOR_PROKARYOTE.get().spawn(level, pos, MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn instanceof AccumulatorProkaryoteEntity accumulatorProkaryote) {
                    if (!level.isClientSide()) {
                        accumulatorProkaryote.refreshVariantAttributes();
                    }
                }
                if (entityToSpawn != null) {
                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                }
                entityToSpawn = CAEntities.PUNCTURE_FISH.get().spawn(level, pos, MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                }
                entityToSpawn = CAEntities.NUCLEIC_MALEFICENT.get().spawn(level, pos, MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                }
            }
        }
        if (this.isAlive()) {
            sklp1 = (Entity) this instanceof TideChimeraEntity datEntI ? datEntI.getEntityData().get(DATA_SKILL_P) : 0;
            dura = (Entity) this instanceof TideChimeraEntity datEntI ? datEntI.getEntityData().get(DATA_DURATION) : 0;
            tap = (Entity) this instanceof TideChimeraEntity datEntI ? datEntI.getEntityData().get(DATA_SUMMON_P) : 0;
            perc = EntityUtils.getHealthPerc(this);
            target = this.getTarget();
            if (dura > 0) {
                if ((Entity) this instanceof TideChimeraEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_DURATION, (int) (dura - 1));
            }
            if (dura <= 0) {
                if (perc <= 0.25 && tap >= 1) {
                    maySummon = true;
                    if (!this.level().isClientSide())
                        this.addEffect(new MobEffectInstance(CAMobEffects.IMMORTAL, 600, 0, false, false));
                    CaerulaArbor.queueServerWork(10, () -> {
                        summonRandomChimera(world, x, y, z);
                    });
                } else if (perc <= 0.5 && tap >= 2) {
                    maySummon = true;
                    CaerulaArbor.queueServerWork(10, () -> {
                        summonRandomChimera(world, x, y, z);
                    });
                } else if (perc <= 0.75 && tap >= 3) {
                    maySummon = true;
                    CaerulaArbor.queueServerWork(10, () -> {
                        summonRandomChimera(world, x, y, z);
                    });
                }
                if (maySummon) {
                    if (!this.level().isClientSide())
                        this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 20, 0, false, false));
                    if ((Entity) this instanceof TideChimeraEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_SUMMON_P, (int) (tap - 1));
                    if ((Entity) this instanceof TideChimeraEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_DURATION, 18);
                    dura = 18;
                    if (this instanceof TideChimeraEntity) {
                        this.setAnimation("animation.super_apocata.throw");
                    }
                }
            }
            if (sklp1 > 0) {
                if ((Entity) this instanceof TideChimeraEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_SKILL_P, (int) (sklp1 - 1));
            } else if (dura <= 0) {
                if (!(target == null) && target.isAlive()) {
                    if (distanceTo(target) <= 8) {
                        if (this instanceof TideChimeraEntity) {
                            this.setAnimation("animation.super_apocata.ranged");
                        }
                        if ((Entity) this instanceof TideChimeraEntity datEntSetI)
                            datEntSetI.getEntityData().set(DATA_SKILL_P, 300);
                        if ((Entity) this instanceof TideChimeraEntity datEntSetI)
                            datEntSetI.getEntityData().set(DATA_DURATION, 28);
                        CaerulaArbor.queueServerWork(12, () -> {
                            if (this.isAlive()) {
                                if (world instanceof ServerLevel level)
                                    level.sendParticles(ParticleTypes.EXPLOSION, x, (y + 4), z, 3, 0, 0, 0, 0.1);
                                if (world instanceof Level level) {
                                    level.playSound(null, BlockPos.containing(x, y, z), CASounds.FIRSTTELLER_ATTACK.get(), SoundSource.HOSTILE, (float) 1.5, 1);
                                }
                                this.distributeBullets(world, x, y, z);
                            }
                        });
                        CaerulaArbor.queueServerWork(13, () -> {
                            if (this.isAlive()) {
                                if (world instanceof ServerLevel level)
                                    level.sendParticles(ParticleTypes.EXPLOSION, x, (y + 4), z, 3, 0, 0, 0, 0.1);
                                if (world instanceof Level level) {
                                    level.playSound(null, BlockPos.containing(x, y, z), CASounds.FIRSTTELLER_ATTACK.get(), SoundSource.HOSTILE, (float) 1.5, 1);
                                }
                                this.distributeBullets(world, x, y, z);
                            }
                        });
                        CaerulaArbor.queueServerWork(14, () -> {
                            if (this.isAlive()) {
                                if (world instanceof ServerLevel level)
                                    level.sendParticles(ParticleTypes.EXPLOSION, x, (y + 4), z, 3, 0, 0, 0, 0.1);
                                if (world instanceof Level level) {
                                    level.playSound(null, BlockPos.containing(x, y, z), CASounds.FIRSTTELLER_ATTACK.get(), SoundSource.HOSTILE, (float) 1.5, 1);
                                }
                                this.distributeBullets(world, x, y, z);
                            }
                        });
                    }
                }
            }
            EntityUtils.giveGuideLay(this);
            double angle;
            double d;
            double daam = 0;
            this.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            for (int index0 = 0; index0 < 8; index0++) {
                angle = Mth.nextDouble(RandomSource.create(), 0, 6.283);
                d = Mth.nextDouble(RandomSource.create(), 6.5, 6.75);
                if (world instanceof ServerLevel level)
                    level.sendParticles(ParticleTypes.BUBBLE_COLUMN_UP, (x + d * Math.sin(angle)), (y + 0.4), (z + d * Math.cos(angle)), 2, 0.1, 0.1, 0.1, 0.1);
                angle = Mth.nextDouble(RandomSource.create(), 0, 6.283);
                d = Mth.nextDouble(RandomSource.create(), 6.5, 6.75);
                if (world instanceof ServerLevel level)
                    level.sendParticles(ParticleTypes.ELECTRIC_SPARK, (x + d * Math.sin(angle)), (y + 0.4), (z + d * Math.cos(angle)), 2, 0.1, 0.1, 0.1, 0.1);
            }
            if (tickCount % 20 == 0) {
                if (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) {
                    this.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
                }
                for (Entity entityiterator : world.getEntities(this, new AABB((x - 6.5), (y - 2), (z - 6.5), (x + 6.5), (y + 5), (z + 6.5)))) {
                    if ((entityiterator != null ? distanceTo(entityiterator) : -1) <= 6.5) {
                        if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "seaborn")))) {
                            if (!(entityiterator == this.getTarget())) {
                                continue;
                            }
                        }
                        if (!(entityiterator instanceof LivingEntity)) {
                            continue;
                        }
                        EPUtils.causeSanityInjury((LivingEntity) entityiterator, this, daam * 4);
                        entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.OCEAN_MAGIC),
                                (float) (daam * 0.5));
                    }
                }
            }
        }
        this.refreshDimensions();
    }

    private void summonRandomChimera(LevelAccessor world, double x, double y, double z) {
        double randomValue = Math.random();
        if (world instanceof Level level) {
            level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.LINGERING_POTION_THROW, SoundSource.HOSTILE, 3, 1);
        }
        Entity entityToSpawn = null;
        BlockPos pos = BlockPos.containing(x, y + 3, z);
        if (world instanceof ServerLevel serverLevel) {
            if (randomValue < 0.01) {
                entityToSpawn = CAEntities.IZUMIK.get().spawn(serverLevel, pos, MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn instanceof IzumikEntity izumik) {
                    izumik.getEntityData().set(IzumikEntity.DATA_GROWTH_P, 20);
                }
            } else if (randomValue < 0.02) {
                entityToSpawn = CAEntities.TIDE_CHIMERA.get().spawn(serverLevel, pos, MobSpawnType.MOB_SUMMONED);
            } else if (randomValue < 0.21) {
                entityToSpawn = CAEntities.TIDELINKED_IMMORTAL.get().spawn(serverLevel, pos, MobSpawnType.MOB_SUMMONED);
            } else if (randomValue < 0.4) {
                entityToSpawn = CAEntities.LINGERING_PATHSHAPER.get().spawn(serverLevel, pos, MobSpawnType.MOB_SUMMONED);
            } else if (randomValue < 0.6) {
                entityToSpawn = EndspeakerEntity.spawnForPhase(serverLevel, pos, MobSpawnType.MOB_SUMMONED, 3);
            } else if (randomValue < 0.8) {
                entityToSpawn = CAEntities.FIRST_TO_TALK.get().spawn(serverLevel, pos, MobSpawnType.MOB_SUMMONED);
            } else {
                entityToSpawn = CAEntities.MEGA_CHEST.get().spawn(serverLevel, pos, MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn instanceof MegaChestEntity megaChest) {
                    megaChest.getEntityData().set(MegaChestEntity.DATA_RELEASED, true);
                }
            }
        }
        if (entityToSpawn != null) {
            entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
            entityToSpawn.push(this.getLookAngle().x, 0.25, this.getLookAngle().z);
        }
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
    public boolean canUsePortal(boolean allowVehicles) {
        return false;
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
            this.remove(RemovalReason.KILLED);
            this.dropExperience(this.getKillCredit());
        }
    }

    @Override
    public void setHealth(float pHealth) {
        float hlth = this.getHealth();
        float mhlth = this.getMaxHealth();
        if (this.hasEffect(CAMobEffects.INVULNERABLE) && pHealth < this.getHealth()) return;
        float reduction = hlth - pHealth;
        float finalV = reduction >= mhlth * 0.26f ? hlth - mhlth * 0.26f : hlth - reduction;
        super.setHealth(finalV);
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

    public void distributeBullets(LevelAccessor world, double x, double y, double z) {
        double r;
        double d;
        double tx;
        double tz;
        double ty;
        double dama;
        dama = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
        for (int index0 = 0; index0 < 3; index0++) {
            r = Mth.nextDouble(RandomSource.create(), 0, 6.283);
            d = Mth.nextDouble(RandomSource.create(), 2, 9);
            tx = x + d * Math.cos(r);
            ty = y + Mth.nextDouble(RandomSource.create(), 8, 11);
            tz = z + d * Math.sin(r);
            if (world instanceof ServerLevel projectileLevel) {
                FishShootEntity entityToSpawn = new FishShootEntity(CAEntities.FISH_SHOOT.get(), projectileLevel);
                entityToSpawn.setOwner(this);
                entityToSpawn.setBaseDamage((float) dama);
                entityToSpawn.setSilent(true);
                entityToSpawn.setPos(tx, ty, tz);
                entityToSpawn.shoot(0, (-1), 0, 1, (float) 0.1);
                projectileLevel.addFreshEntity(entityToSpawn);
            }
        }
        for (int index1 = 0; index1 < 3; index1++) {
            r = Mth.nextDouble(RandomSource.create(), 0, 6.283);
            d = Mth.nextDouble(RandomSource.create(), 2, 9);
            tx = x + d * Math.cos(r);
            ty = y + Mth.nextDouble(RandomSource.create(), 8, 11);
            tz = z + d * Math.sin(r);
            if (world instanceof ServerLevel projectileLevel) {
                FishSplashEntity entityToSpawn = new FishSplashEntity(CAEntities.FISH_SPLASH.get(), projectileLevel);
                entityToSpawn.setOwner(this);
                entityToSpawn.setBaseDamage((float) dama);
                entityToSpawn.setSilent(true);
                entityToSpawn.setPos(tx, ty, tz);
                entityToSpawn.shoot(0, (-1), 0, 1, (float) 0.1);
                projectileLevel.addFreshEntity(entityToSpawn);
            }
        }
        for (int index2 = 0; index2 < 2; index2++) {
            r = Mth.nextDouble(RandomSource.create(), 0, 6.283);
            d = Mth.nextDouble(RandomSource.create(), 2, 9);
            tx = x + d * Math.cos(r);
            ty = y + Mth.nextDouble(RandomSource.create(), 8, 11);
            tz = z + d * Math.sin(r);
            if (world instanceof ServerLevel projectileLevel) {
                FleefishBulletEntity entityToSpawn = new FleefishBulletEntity(CAEntities.FLEEFISH_BULLET.get(), projectileLevel);
                entityToSpawn.setOwner(this);
                entityToSpawn.setBaseDamage((float) dama);
                entityToSpawn.setSilent(true);
                entityToSpawn.setPos(tx, ty, tz);
                entityToSpawn.shoot(0, (-1), 0, 1, (float) 0.1);
                projectileLevel.addFreshEntity(entityToSpawn);
            }
        }
        for (int index3 = 0; index3 < 2; index3++) {
            r = Mth.nextDouble(RandomSource.create(), 0, 6.283);
            d = Mth.nextDouble(RandomSource.create(), 2, 9);
            tx = x + d * Math.cos(r);
            ty = y + Mth.nextDouble(RandomSource.create(), 8, 11);
            tz = z + d * Math.sin(r);
            if (world instanceof ServerLevel projectileLevel) {
                TellerShotEntity entityToSpawn = new TellerShotEntity(CAEntities.TELLER_SHOT.get(), projectileLevel);
                entityToSpawn.setOwner(this);
                entityToSpawn.setBaseDamage((float) dama);
                entityToSpawn.setSilent(true);
                entityToSpawn.setPos(tx, ty, tz);
                entityToSpawn.shoot(0, (-1), 0, 1, (float) 0.1);
                projectileLevel.addFreshEntity(entityToSpawn);
            }
        }
        for (int index4 = 0; index4 < 2; index4++) {
            r = Mth.nextDouble(RandomSource.create(), 0, 6.283);
            d = Mth.nextDouble(RandomSource.create(), 2, 9);
            tx = x + d * Math.cos(r);
            ty = y + Mth.nextDouble(RandomSource.create(), 8, 11);
            tz = z + d * Math.sin(r);
            if (world instanceof ServerLevel projectileLevel) {
                AbandonedShootEntity entityToSpawn = new AbandonedShootEntity(CAEntities.ABANDONED_SHOOT.get(), projectileLevel);
                entityToSpawn.setOwner(this);
                entityToSpawn.setBaseDamage((float) dama);
                entityToSpawn.setSilent(true);
                entityToSpawn.setPos(tx, ty, tz);
                entityToSpawn.shoot(0, (-1), 0, 1, (float) 0.1);
                projectileLevel.addFreshEntity(entityToSpawn);
            }
        }
        for (int index5 = 0; index5 < 3; index5++) {
            r = Mth.nextDouble(RandomSource.create(), 0, 6.283);
            d = Mth.nextDouble(RandomSource.create(), 2, 9);
            tx = x + d * Math.cos(r);
            ty = y + Mth.nextDouble(RandomSource.create(), 8, 11);
            tz = z + d * Math.sin(r);
            if (world instanceof ServerLevel projectileLevel) {
                FakerggShootEntity entityToSpawn = new FakerggShootEntity(CAEntities.FAKERGG_SHOOT.get(), projectileLevel);
                entityToSpawn.setOwner(this);
                entityToSpawn.setBaseDamage((float) dama);
                entityToSpawn.setSilent(true);
                entityToSpawn.setPos(tx, ty, tz);
                entityToSpawn.shoot(0, (-1), 0, 1, (float) 0.1);
                projectileLevel.addFreshEntity(entityToSpawn);
            }
        }
    }

    private boolean isChimeraDurative() {
        return this.isAlive() && this.tickCount > 100 && this.getEntityData().get(DATA_DURATION) <= 0;
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.18);
        builder = builder.add(Attributes.MAX_HEALTH, 300);
        builder = builder.add(Attributes.ARMOR, 8);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 19);
        builder = builder.add(Attributes.FOLLOW_RANGE, 48);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
        builder = builder.add(CAAttributes.MISSRATE, 50);
        builder = builder.add(BabelAttributes.MAGIC_RESISTANCE, 45);
        builder = builder.add(Attributes.STEP_HEIGHT, 1.5f);
        return builder;
    }

    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}