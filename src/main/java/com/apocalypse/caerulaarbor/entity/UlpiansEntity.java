package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.capability.sanity.SanityInjuryCapability;
import com.apocalypse.caerulaarbor.entity.base.SyncedAnimationEntity;
import com.apocalypse.caerulaarbor.init.*;
import com.apocalypse.caerulaarbor.util.EntityUtils;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Comparator;
import java.util.List;

public class UlpiansEntity extends Animal implements GeoEntity, SyncedAnimationEntity {
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(UlpiansEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(UlpiansEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_SKILLP_1 = SynchedEntityData.defineId(UlpiansEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_SKILLP_2 = SynchedEntityData.defineId(UlpiansEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_BONUS = SynchedEntityData.defineId(UlpiansEntity.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";

    public UlpiansEntity(Level world) {
        this(CAEntities.ULPIANS.get(), world);
    }

    public UlpiansEntity(EntityType<UlpiansEntity> type, Level world) {
        super(type, world);
        xpReward = 0;
        setNoAi(false);
        setMaxUpStep(1f);
        setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ANIMATION, "undefined");
        this.entityData.define(DATA_DURATION, 0);
        this.entityData.define(DATA_SKILLP_1, 80);
        this.entityData.define(DATA_SKILLP_2, 160);
        this.entityData.define(DATA_BONUS, 0);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.1, false) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return 9;
            }

            @Override
            public boolean canUse() {
                return super.canUse() && isUlpuansDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isUlpuansDurative();
            }

        });
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Monster.class, true, false));
        this.goalSelector.addGoal(4, new OpenDoorGoal(this, false));
        this.goalSelector.addGoal(5, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(6, new RandomStrollGoal(this, 1) {
            @Override
            public boolean canUse() {
                return super.canUse() && isUlpuansDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isUlpuansDurative();
            }
        });
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && isUlpuansDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isUlpuansDurative();
            }
        });
        this.goalSelector.addGoal(8, new FloatGoal(this));
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return CASounds.ULPIANS_HIT.get();
    }

    @Override
    public SoundEvent getDeathSound() {
        return CASounds.ULPIANS_DIE.get();
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        double targetX = target.getX();
        double targetY = target.getY();
        double targetZ = target.getZ();
        if (!this.level().isClientSide()) {
            this.getEntityData().set(DATA_DURATION, this.getEntityData().get(DATA_DURATION) + 30);
            this.level().playSound(null, BlockPos.containing(targetX, targetY, targetZ),
                    CASounds.ANCHOR_PRE.get(), SoundSource.HOSTILE, 2.2F, 1);
            CaerulaArborMod.queueServerWork(14, () -> {
                if (this.isAlive()) {
                    Entity currentTarget = this.getTarget();
                    double damage = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
                    this.level().playSound(null, BlockPos.containing(targetX, targetY, targetZ),
                            CASounds.ANCHOR_ATTACK.get(), SoundSource.HOSTILE, 2.75F, 1);
                    final Vec3 center = new Vec3(this.getX(), this.getY(), this.getZ());
                    TagKey<EntityType<?>> oceanOffspringTag = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring"));
                    List<LivingEntity> foundEntities = this.level().getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(24),
                            entity -> entity.isAlive()
                                    && entity != this
                                    && !(entity instanceof ServerPlayer serverPlayer
                                            && (serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE
                                            || serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR))
                                    && !(entity.getType().is(oceanOffspringTag) && entity != currentTarget));
                    for (LivingEntity entityIterator : foundEntities) {
                        if (this.distanceToSqr(entityIterator) <= 576) {
                            entityIterator.hurt(CADamageTypes.source(this.level(), CADamageTypes.HUNTER_ATTACK, this), (float) damage);
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
                }
            });
        }
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        double healAmoun;
        if (this.isAlive()) {
            if (invulnerableTime <= 15) {
                healAmoun = 8;
                if (this.getHealth() < this.getMaxHealth() * 0.6) {
                    healAmoun = 12;
                }
                this.setHealth((float) Math.min(this.getHealth() + healAmoun, this.getMaxHealth()));
            }
        }
        if (source.is(DamageTypes.FALL))
            return false;
        if (source.is(DamageTypes.DROWN))
            return false;
        return super.hurt(source, amount);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Duration", this.entityData.get(DATA_DURATION));
        compound.putInt("Skillp1", this.entityData.get(DATA_SKILLP_1));
        compound.putInt("Skillp2", this.entityData.get(DATA_SKILLP_2));
        compound.putInt("Bonus", this.entityData.get(DATA_BONUS));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Duration")) {
            this.entityData.set(DATA_DURATION, compound.getInt("Duration"));
        }
        if (compound.contains("Skillp1")) {
            this.entityData.set(DATA_SKILLP_1, compound.getInt("Skillp1"));
        }
        if (compound.contains("Skillp2")) {
            this.entityData.set(DATA_SKILLP_2, compound.getInt("Skillp2"));
        }
        if (compound.contains("Bonus")) {
            this.entityData.set(DATA_BONUS, compound.getInt("Bonus"));
        }
    }

    @Override
    public void awardKillScore(Entity entity, int score, DamageSource damageSource) {
        super.awardKillScore(entity, score, damageSource);
        LevelAccessor world = this.level();
        double bns;
        double perc;
        bns = (Entity) this instanceof UlpiansEntity datEntI ? datEntI.getEntityData().get(DATA_BONUS) : 0;
        if (bns < 10) {
            if ((Entity) this instanceof UlpiansEntity datEntSetI)
                datEntSetI.getEntityData().set(DATA_BONUS, (int) (bns + 1));
            {
                final Vec3 center = new Vec3(this.getX(), this.getY(), this.getZ());
                TagKey<EntityType<?>> huntersTag = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "hunters"));
                List<LivingEntity> entfound = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(24),
                        e -> e.isAlive() && e.getType().is(huntersTag));
                for (LivingEntity entityiterator : entfound) {
                    perc = entityiterator.getHealth() / entityiterator.getMaxHealth();
                    if (entityiterator.getAttributes().hasAttribute(Attributes.MAX_HEALTH))
                        entityiterator.getAttribute(Attributes.MAX_HEALTH).setBaseValue(
                                (entityiterator.getAttribute(Attributes.MAX_HEALTH).getBaseValue() + 10));
                    entityiterator.setHealth((float) (entityiterator.getMaxHealth() * perc));
                    if (entityiterator.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
                        entityiterator.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(
                                (entityiterator.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() + 2));
                }
            }
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
        double skillp2;
        if (this.isAlive()) {
            sklp1 = (Entity) this instanceof UlpiansEntity datEntI ? datEntI.getEntityData().get(DATA_SKILLP_1) : 0;
            skillp2 = (Entity) this instanceof UlpiansEntity datEntI ? datEntI.getEntityData().get(DATA_SKILLP_2) : 0;
            dura = (Entity) this instanceof UlpiansEntity datEntI ? datEntI.getEntityData().get(DATA_DURATION) : 0;
            target = this.getTarget();
            if (dura > 0) {
                if ((Entity) this instanceof UlpiansEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_DURATION, (int) (dura - 1));
            }
            if (sklp1 > 0) {
                if ((Entity) this instanceof UlpiansEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_SKILLP_1, (int) (sklp1 - 1));
            } else {
                if (!(target == null) && target.isAlive()) {
                    if (distanceTo(target) <= 3.5) {
                        if (this instanceof UlpiansEntity) {
                            this.setAnimation("animation.ulpians.pull");
                        }
                        if ((Entity) this instanceof UlpiansEntity datEntSetI)
                            datEntSetI.getEntityData().set(DATA_SKILLP_1, 120);
                        if ((Entity) this instanceof UlpiansEntity datEntSetI)
                            datEntSetI.getEntityData().set(DATA_DURATION, (int) (dura + 40));
                        if (world instanceof Level level) {
                            level.playSound(null, BlockPos.containing(x, y, z), CASounds.ULPIANS_PUL_PRE.get(), SoundSource.NEUTRAL, (float) 2.2, 1);
                        }
                        this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((target.getX()), (target.getY() + 1.6), (target.getZ())));
                        CaerulaArborMod.queueServerWork(13, () -> {
                            if (this.isAlive()) {
                                if (world instanceof Level level) {
                                    level.playSound(null, BlockPos.containing(x, y, z), CASounds.ULPIANS_PULL_THROW.get(), SoundSource.NEUTRAL, 3, 1);
                                }
                            }
                        });
                        CaerulaArborMod.queueServerWork(20, () -> {
                            if (this.isAlive()) {
                                Entity enemy1;
                                double damage;
                                double r;
                                double d;
                                enemy1 = (Entity) this instanceof Mob mobEnt ? mobEnt.getTarget() : null;
                                r = 6;
                                damage = (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 2.7;
                                if (world instanceof Level level) {
                                    level.playSound(null, BlockPos.containing(getX(), getY(), getZ()), CASounds.ULPIANS_PULL_HIT.get(), SoundSource.NEUTRAL, 3, 1);
                                }
                                {
                                    final Vec3 center = new Vec3((getX()), (getY()), (getZ()));
                                    TagKey<EntityType<?>> humanSideTag = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "is_humanside"));
                                    List<LivingEntity> entfound = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(6),
                                            e -> e != this
                                                    && !((e instanceof Player || (e instanceof TamableAnimal tamEnt && tamEnt.isTame())) && e != enemy1)
                                                    && !(e.getType().is(humanSideTag) && e != enemy1));
                                    for (LivingEntity entityiterator : entfound) {
                                        d = distanceTo(entityiterator);
                                        if (d <= r && (EntityUtils.getEntityCosine(this, entityiterator) > 0.5 || d <= 3)) {
                                            if (!this.level().isClientSide())
                                                this.addEffect(new MobEffectInstance(CAMobEffects.DIZZY.get(), 40, 0, false, false));
                                            entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.ANCHOR_SMASH, this), (float) damage);
                                        }
                                    }
                                }
                            }
                        });
                        CaerulaArborMod.queueServerWork(24, () -> {
                            if (this.isAlive()) {
                                Entity enemy1 = this.getTarget();
                                double r = 4.5;
                                {
                                    final Vec3 center = new Vec3(x, y, z);
                                    TagKey<EntityType<?>> humanSideTag = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "is_humanside"));
                                    List<LivingEntity> entfound = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(4.5),
                                            e -> e.isAlive()
                                                    && e != this
                                                    && !((e instanceof Player || (e instanceof TamableAnimal tamEnt && tamEnt.isTame())) && e != enemy1)
                                                    && !(e.getType().is(humanSideTag) && e != enemy1));
                                    for (LivingEntity entityiterator : entfound) {
                                        if (distanceToSqr(entityiterator) <= r * r && EntityUtils.getEntityCosine(this, entityiterator) > 0.6) {
                                            Vec3 offset = position().add(entityiterator.position().reverse());
                                            if (offset.lengthSqr() <= 0.01) continue;
                                            offset = offset.normalize();
                                            entityiterator.push(offset.x, offset.y, offset.z);
                                        }
                                    }
                                }
                            }
                        });
                        CaerulaArborMod.queueServerWork(26, () -> {
                            if (this.isAlive()) {
                                if (world instanceof Level level) {
                                    level.playSound(null, BlockPos.containing(x, y, z), CASounds.ULPIANS_PULL_PULL.get(), SoundSource.NEUTRAL, (float) 2.5, 1);
                                }
                            }
                        });
                    }
                }
            }
            if (skillp2 > 0) {
                if ((Entity) this instanceof UlpiansEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_SKILLP_2, (int) (skillp2 - 1));
            } else {
                if (!(target == null) && target.isAlive()) {
                    if (distanceTo(target) <= 24) {
                        if (this instanceof UlpiansEntity) {
                            this.setAnimation("animation.ulpians.skill");
                        }
                        if (SpecterEntity.isSpecterAround(world, x, y, z)) {
                            if ((Entity) this instanceof UlpiansEntity datEntSetI)
                                datEntSetI.getEntityData().set(DATA_SKILLP_2, 820);
                        } else {
                            if ((Entity) this instanceof UlpiansEntity datEntSetI)
                                datEntSetI.getEntityData().set(DATA_SKILLP_2, 900);
                        }
                        if ((Entity) this instanceof UlpiansEntity datEntSetI)
                            datEntSetI.getEntityData().set(DATA_DURATION, (int) (dura + 40));
                        if (world instanceof Level level) {
                            level.playSound(null, BlockPos.containing(x, y, z), CASounds.ULPIANS_PUL_PRE.get(), SoundSource.NEUTRAL, (float) 2.2, 1);
                        }
                        ((Entity) this).lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((target.getX()), (target.getY() + 1.6), (target.getZ())));
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 25, 9, false, false));
                        if (world instanceof Level level) {
                            level.playSound(null, BlockPos.containing(x, y, z), CASounds.ULPIANS_SKILL.get(), SoundSource.NEUTRAL, (float) 2.5, 1);
                        }
                        CaerulaArborMod.queueServerWork(16, () -> {
                            if (world instanceof Level level) {
                                level.playSound(null, BlockPos.containing(x, y, z), CASounds.ANCHOR_THROW.get(), SoundSource.NEUTRAL, (float) 2.2, 1);
                            }
                        });
                        CaerulaArborMod.queueServerWork(22, () -> {
                            if (this.isAlive()) {
                                Entity enemy1;
                                double perc;
                                double damage;
                                double noeX;
                                double nowY;
                                double nowZ;
                                perc = ((Entity) this instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) / ((Entity) this instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1);
                                if (!this.level().isClientSide())
                                    this.addEffect(new MobEffectInstance(CAMobEffects.PATH_TO_UNCOVER.get(), 500, 0, false, true));
                                this.setHealth((float) (this.getMaxHealth() * perc));
                                enemy1 = (Entity) this instanceof Mob mobEnt ? mobEnt.getTarget() : null;
                                damage = (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 1.5;
                                if (!(enemy1 == null)) {
                                    Entity ent = this;
                                    ent.teleportTo((enemy1.getX()), (enemy1.getY()), (enemy1.getZ()));
                                    if (enemy1 instanceof LivingEntity && !this.level().isClientSide())
                                        this.addEffect(new MobEffectInstance(CAMobEffects.DIZZY.get(), 120, 0, false, false));
                                    enemy1.hurt(CADamageTypes.source(world, CADamageTypes.ANCHOR_SMASH, this), (float) damage);
                                }
                                noeX = getX();
                                nowY = getY();
                                nowZ = getZ();
                                {
                                    final Vec3 center = new Vec3(noeX, nowY, nowZ);
                                    TagKey<EntityType<?>> humanSideTag = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "is_humanside"));
                                    List<LivingEntity> entfound = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(6),
                                            e -> e != this
                                                    && e != enemy1
                                                    && !(e instanceof Player)
                                                    && !(e instanceof TamableAnimal tamEnt && tamEnt.isTame())
                                                    && !e.getType().is(humanSideTag));
                                    for (LivingEntity entityiterator : entfound) {
                                        if (distanceToSqr(entityiterator) <= 36) {
                                            if (!this.level().isClientSide())
                                                this.addEffect(new MobEffectInstance(CAMobEffects.DIZZY.get(), 120, 0, false, false));
                                            entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.ANCHOR_SMASH, this), (float) damage);
                                        }
                                    }
                                }
                                this.removeEffect(CAMobEffects.DIZZY.get());
                                this.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
                                this.removeEffect(MobEffects.DIG_SLOWDOWN);
                                SanityInjuryCapability sanityInjury = ModCapabilities.getSanityInjury(this);
                                sanityInjury.heal(sanityInjury.getMaxValue());
                                if (world instanceof ServerLevel level)
                                    level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, noeX, nowY, nowZ, 72, 3, 3, 3, 0.5);
                                if (world instanceof Level level) {
                                    level.playSound(null, BlockPos.containing(noeX, nowY, nowZ), CASounds.ANCHOR_SKILL.get(), SoundSource.PLAYERS, 3, 1);
                                }
                            }
                        });
                    }
                }
            }
            GladiiaEntity.healFromGladiia(world, x, y, z, this);
        }
        this.refreshDimensions();
    }

    @Override
    public EntityDimensions getDimensions(Pose p_33597_) {
        return super.getDimensions(p_33597_).scale((float) 1);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageable) {
        UlpiansEntity retval = CAEntities.ULPIANS.get().create(serverWorld);
        retval.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(retval.blockPosition()), MobSpawnType.BREEDING, null, null);
        return retval;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.updateSwingTime();
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.18);
        builder = builder.add(ForgeMod.SWIM_SPEED.get(), 8);
        builder = builder.add(CAAttributes.SANITY_MODIFIER.get(), 0.33);
        builder = builder.add(Attributes.MAX_HEALTH, 430);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 55);
        builder = builder.add(Attributes.FOLLOW_RANGE, 32);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 5);
        return builder;
    }

    private boolean isUlpuansDurative() {
        return this.isAlive() && this.getEntityData().get(DATA_DURATION) <= 0;
    }
    private PlayState movementPredicate(AnimationState<?> event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

            ) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.ulpians.move"));
            }
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.ulpians.die"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.ulpians.idle"));
        }
        return PlayState.STOP;
    }

    private PlayState attackingPredicate(AnimationState<?> event) {
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
            this.swinging = true;
            this.lastSwing = level().getGameTime();
        }
        if (this.swinging && this.lastSwing + 30L <= level().getGameTime()) {
            this.swinging = false;
        }
        if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.ulpians.attack"));
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
        if (this.deathTime == 20) {
            this.remove(RemovalReason.KILLED);
            this.dropExperience();
        }
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

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}
