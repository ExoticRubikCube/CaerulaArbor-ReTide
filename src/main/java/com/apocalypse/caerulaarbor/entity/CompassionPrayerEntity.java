package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.api.event.SanityEvent;
import com.apocalypse.caerulaarbor.capability.sanity.SIHelper;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.entity.bullets.PrayerSplashEntity;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.init.CASounds;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class CompassionPrayerEntity extends SeaMonster implements RangedAttackMob {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(CompassionPrayerEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(CompassionPrayerEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_PHASE = SynchedEntityData.defineId(CompassionPrayerEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_REVIVE_TICK = SynchedEntityData.defineId(CompassionPrayerEntity.class, EntityDataSerializers.INT);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";

    public CompassionPrayerEntity(Level world) {
        this(CAEntities.COMPASSION_PRAYER.get(), world);
    }

    public CompassionPrayerEntity(EntityType<CompassionPrayerEntity> type, Level world) {
        super(type, world);
        xpReward = 16;
        setNoAi(false);
        setMaxUpStep(0.8f);
        setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SHOOT, false);
        this.entityData.define(DATA_ANIMATION, "undefined");
        this.entityData.define(DATA_PHASE, 0);
        this.entityData.define(DATA_REVIVE_TICK, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this) {
            @Override
            public boolean canUse() {
                //TODO 需要清理mcr残留
                if (!super.canUse()) return false;
                return hasEffect(CAMobEffects.FAKE_DEATH.get());
            }

            @Override
            public boolean canContinueToUse() {
                if (!super.canContinueToUse()) return false;
                return hasEffect(CAMobEffects.FAKE_DEATH.get());
            }
        });
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, IronGolem.class, true, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, SnowGolem.class, true, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Villager.class, true, false));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Illusioner.class, true, false));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Pillager.class, true, false));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, Vindicator.class, true, false));
        this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, Witch.class, true, false));
        this.targetSelector.addGoal(9, new NearestAttackableTargetGoal<>(this, Piglin.class, true, false));
        this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, PiglinBrute.class, true, false));
        this.targetSelector.addGoal(11, new NearestAttackableTargetGoal<>(this, ZombifiedPiglin.class, true, false));
        this.targetSelector.addGoal(12, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, target -> EntityUtils.isOceanizedPlayerNearby(this.level(), this.getX(), this.getY(), this.getZ())));
        this.goalSelector.addGoal(13, new RandomStrollGoal(this, 0.8) {
            @Override
            public boolean canUse() {
                if (!super.canUse()) return false;
                return hasEffect(CAMobEffects.FAKE_DEATH.get());
            }

            @Override
            public boolean canContinueToUse() {
                if (!super.canContinueToUse()) return false;
                return hasEffect(CAMobEffects.FAKE_DEATH.get());
            }
        });
        this.goalSelector.addGoal(14, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                if (!super.canUse()) return false;
                return hasEffect(CAMobEffects.FAKE_DEATH.get());
            }

            @Override
            public boolean canContinueToUse() {
                if (!super.canContinueToUse()) return false;
                return hasEffect(CAMobEffects.FAKE_DEATH.get());
            }
        });
        this.goalSelector.addGoal(15, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25, 80, 5f) {
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
                this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
            }
        }

        public boolean canUse() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                this.target = livingentity;
                LivingEntity entity = CompassionPrayerEntity.this;
                return entity.hasEffect(CAMobEffects.FAKE_DEATH.get());
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
            ((CompassionPrayerEntity) rangedAttackMob).entityData.set(DATA_SHOOT, false);
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
                    ((CompassionPrayerEntity) rangedAttackMob).entityData.set(DATA_SHOOT, false);
                    return;
                }
                ((CompassionPrayerEntity) rangedAttackMob).entityData.set(DATA_SHOOT, true);
                float f = (float) Math.sqrt(d0) / this.attackRadius;
                float f1 = Mth.clamp(f, 0.1F, 1.0F);
                this.rangedAttackMob.performRangedAttack(this.target, f1);
                this.attackTime = Mth.floor(f * (float) (this.attackIntervalMax - this.attackIntervalMin) + (float) this.attackIntervalMin);
            } else if (this.attackTime < 0) {
                this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(d0) / (double) this.attackRadius, this.attackIntervalMin, this.attackIntervalMax));
            } else
                ((CompassionPrayerEntity) rangedAttackMob).entityData.set(DATA_SHOOT, false);
        }
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return CASounds.SEABORN_GENERIC_HIT.get();
    }

    @Override
    public SoundEvent getDeathSound() {
        return CASounds.SEABORN_DEATH.get();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.DROWN))
            return false;
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource source) {
        if (this.getEntityData().get(DATA_PHASE) == 0 && this.getEntityData().get(DATA_REVIVE_TICK) <= 0) {
            Vec3 center = new Vec3(this.getX(), this.getY(), this.getZ());
            List<Entity> nearbyEntities = this.level().getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(32 / 2d), entity -> true).stream()
                    .sorted(Comparator.comparingDouble(candidate -> candidate.distanceToSqr(center)))
                    .toList();
            for (Entity nearbyEntity : nearbyEntities) {
                if (nearbyEntity != this && nearbyEntity.isAlive()
                        && nearbyEntity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                    if (nearbyEntity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide()) {
                        livingEntity.addEffect(new MobEffectInstance(CAMobEffects.IMMORTAL.get(), 200, 0, false, false));
                    }
                }
            }
            this.getEntityData().set(DATA_REVIVE_TICK, 200);
            if (!this.level().isClientSide()) {
                this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 200, 1, false, false));
                this.addEffect(new MobEffectInstance(CAMobEffects.FAKE_DEATH.get(), 200, 1, false, false));
            }
            return;
        }
        super.die(source);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Phase", this.entityData.get(DATA_PHASE));
        compound.putInt("ReviveTick", this.entityData.get(DATA_REVIVE_TICK));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Phase")) {
            this.entityData.set(DATA_PHASE, compound.getInt("Phase"));
        }
        if (compound.contains("ReviveTick")) {
            this.entityData.set(DATA_REVIVE_TICK, compound.getInt("ReviveTick"));
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity enemy;
        double dura;
        double P;
        double perc;
        double d;
        if (this.isAlive()) {
            dura = (Entity) this instanceof CompassionPrayerEntity _datEntI ? _datEntI.getEntityData().get(DATA_REVIVE_TICK) : 0;
            if (dura > 0) {
                if (dura <= 100) {
                    if ((Entity) this instanceof CompassionPrayerEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_PHASE, 1);
                }
                if ((Entity) this instanceof CompassionPrayerEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_REVIVE_TICK, (int) (dura - 1));
                setShiftKeyDown(true);
            } else {
                setShiftKeyDown(false);
            }
            P = (Entity) this instanceof CompassionPrayerEntity _datEntI ? _datEntI.getEntityData().get(DATA_PHASE) : 0;
            if (P == 0) {
                if (tickCount % 10 == 0) {
                    {
                        final Vec3 _center = new Vec3(x, y, z);
                        List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(32 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                        for (Entity entityiterator : _entfound) {
                            if (!entityiterator.isAlive()) {
                                continue;
                            }
                            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "bossoffspring")))) {
                                continue;
                            }
                            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                                if (!(entityiterator instanceof LivingEntity _livEnt12 && _livEnt12.hasEffect(CAMobEffects.ADD_HEALTH_PERCLY.get()))) {
                                    perc = EntityUtils.getHealthPerc(entityiterator);
                                    if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
                                        _entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_HEALTH_PERCLY.get(), 32768, 1, false, false));
                                    if (entityiterator instanceof LivingEntity _entity)
                                        _entity.setHealth((float) ((entityiterator instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * perc));
                                }
                            }
                        }
                    }
                }
            } else {
                Mob _mobEnt = this;
                enemy = _mobEnt.getTarget();
                LivingEntity _livingEntity19 = this;
                d = _livingEntity19.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity19.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
                if (tickCount % 20 == 0) {
                    {
                        final Vec3 _center = new Vec3(x, y, z);
                        List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(10 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                        for (Entity entityiterator : _entfound) {
                            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                                if (!(entityiterator == enemy)) {
                                    continue;
                                }
                            }
                            if (!(entityiterator instanceof LivingEntity)) {
                                continue;
                            }
                            if (distanceTo(entityiterator) <= 5) {
                                entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "ocean_magic")))),
                                        (float) (d * 0.2));
                                if (entityiterator instanceof LivingEntity target) {
                                    SIHelper.causeSanityInjury(target, d * 30, SanityEvent.Hurt.Type.ENTITY);
                                }
                            }
                        }
                    }
                }
                double angle;
                double d1;
                d1 = 5;
                for (int index0 = 0; index0 < 6; index0++) {
                    angle = Mth.nextDouble(RandomSource.create(), 0, 6.283);
                    if (world instanceof ServerLevel _level)
                        _level.sendParticles(ParticleTypes.ELECTRIC_SPARK, (x + d1 * Math.sin(angle)), (y + 0.4), (z + d1 * Math.cos(angle)), 2, 0.1, 0.1, 0.1, 0.1);
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
    public void performRangedAttack(LivingEntity target, float flval) {
        PrayerSplashEntity.shoot(this, target, (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttributeValue(Attributes.ATTACK_DAMAGE) : 0) * (2.5 / 7.0));
    }


    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.18);
        builder = builder.add(Attributes.MAX_HEALTH, 136);
        builder = builder.add(Attributes.ARMOR, 2);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 7);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
        return builder;
    }

    private PlayState movementPredicate(AnimationState<?> event) {
        if (this.animationprocedure.equals("empty")) {
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.compassion_prayer.die"));
            }
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.1F && event.getLimbSwingAmount() < 0.1F))) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.compassion_prayer.move"));
            }
            if (this.isShiftKeyDown()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.compassion_prayer.revive"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.compassion_prayer.idle"));
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
        if ((this.swinging || this.entityData.get(DATA_SHOOT)) && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.compassion_prayer.attack"));
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
        data.add(new AnimationController<>(this, "movement", 2, this::movementPredicate));
        data.add(new AnimationController<>(this, "attacking", 2, this::attackingPredicate));
        data.add(new AnimationController<>(this, "procedure", 2, this::procedurePredicate));
    }


    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}
