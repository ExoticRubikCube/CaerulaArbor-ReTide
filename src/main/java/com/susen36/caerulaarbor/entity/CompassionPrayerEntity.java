package com.susen36.caerulaarbor.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.api.event.SanityEvent;
import com.susen36.caerulaarbor.capability.sanity.SIHelper;
import com.susen36.caerulaarbor.entity.base.SeaMonsterBoss;
import com.susen36.caerulaarbor.entity.bullets.PrayerSplashEntity;
import com.susen36.caerulaarbor.init.CADamageTypes;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.init.CASounds;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animation.*;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class CompassionPrayerEntity extends SeaMonsterBoss implements RangedAttackMob {
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
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(0.8f);
        setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHOOT, false);
        builder.define(DATA_ANIMATION, "undefined");
        builder.define(DATA_PHASE, 0);
        builder.define(DATA_REVIVE_TICK, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // TODO: 子类专属匿名 override 版 HurtByTargetGoal（需 FAKE_DEATH 效果激活），覆盖基类同优先级目标
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this) {
            @Override
            public boolean canUse() {
                //TODO 需要清理mcr残留 :更改为 return super.canUse() && hasEffect(CAMobEffects.FAKE_DEATH);
                if (!super.canUse()) return false;
                return hasEffect(CAMobEffects.FAKE_DEATH);
            }

            @Override
            public boolean canContinueToUse() {
                if (!super.canContinueToUse()) return false;
                return hasEffect(CAMobEffects.FAKE_DEATH);
            }
        });
        // TODO: 子类专属 goalSelector 行为（假死状态下随机游荡），与基类 targetSelector 统一目标不冲突
        this.goalSelector.addGoal(13, new RandomStrollGoal(this, 0.8) {
            @Override
            public boolean canUse() {
                if (!super.canUse()) return false;
                return hasEffect(CAMobEffects.FAKE_DEATH);
            }

            @Override
            public boolean canContinueToUse() {
                if (!super.canContinueToUse()) return false;
                return hasEffect(CAMobEffects.FAKE_DEATH);
            }
        });
        // TODO: 子类专属 goalSelector 行为（假死状态下随机环顾），与基类 targetSelector 统一目标不冲突
        this.goalSelector.addGoal(14, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                if (!super.canUse()) return false;
                return hasEffect(CAMobEffects.FAKE_DEATH);
            }

            @Override
            public boolean canContinueToUse() {
                if (!super.canContinueToUse()) return false;
                return hasEffect(CAMobEffects.FAKE_DEATH);
            }
        });

        this.goalSelector.addGoal(15, new FloatGoal(this));
        // TODO: 子类专属 goalSelector 行为（远程攻击祈祷弹），与基类 targetSelector 统一目标不冲突
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
                return entity.hasEffect(CAMobEffects.FAKE_DEATH);
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
    public SoundEvent getHurtSound(DamageSource source) {
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
                        && nearbyEntity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "sea_born")))) {
                    if (nearbyEntity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide()) {
                        livingEntity.addEffect(new MobEffectInstance(CAMobEffects.IMMORTAL, 200, 0, false, false));
                    }
                }
            }
            this.getEntityData().set(DATA_REVIVE_TICK, 200);
            if (!this.level().isClientSide()) {
                this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 200, 1, false, false));
                this.addEffect(new MobEffectInstance(CAMobEffects.FAKE_DEATH, 200, 1, false, false));
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
        Entity target;
        double dura;
        double P;
        double perc;
        double d;
        if (this.isAlive()) {
            dura = (Entity) this instanceof CompassionPrayerEntity datEntI ? datEntI.getEntityData().get(DATA_REVIVE_TICK) : 0;
            if (dura > 0) {
                if (dura <= 100) {
                    if ((Entity) this instanceof CompassionPrayerEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_PHASE, 1);
                }
                if ((Entity) this instanceof CompassionPrayerEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_REVIVE_TICK, (int) (dura - 1));
                setShiftKeyDown(true);
            } else {
                setShiftKeyDown(false);
            }
            P = (Entity) this instanceof CompassionPrayerEntity datEntI ? datEntI.getEntityData().get(DATA_PHASE) : 0;
            if (P == 0) {
                if (tickCount % 10 == 0) {
                    {
                        final Vec3 center = new Vec3(x, y, z);
                        List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(32 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                        for (Entity entityiterator : entfound) {
                            if (!entityiterator.isAlive()) {
                                continue;
                            }
                            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "sea_born_boss")))) {
                                continue;
                            }
                            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "sea_born")))) {
                                if (!(entityiterator instanceof LivingEntity livEnt12 && livEnt12.hasEffect(CAMobEffects.ADD_HEALTH_PERCLY))) {
                                    perc = EntityUtils.getHealthPerc(entityiterator);
                                    if (entityiterator instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                                        livingEntity.addEffect(new MobEffectInstance(CAMobEffects.ADD_HEALTH_PERCLY, 32768, 1, false, false));
                                    if (entityiterator instanceof LivingEntity livingEntity)
                                        livingEntity.setHealth((float) (livingEntity.getMaxHealth() * perc));
                                }
                            }
                        }
                    }
                }
            } else {
                Mob mobEnt = this;
                target = mobEnt.getTarget();
                LivingEntity livingEntity19 = this;
                d = livingEntity19.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity19.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
                if (tickCount % 20 == 0) {
                    {
                        final Vec3 center = new Vec3(x, y, z);
                        List<LivingEntity> entfound = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(10 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                        for (LivingEntity entityiterator : entfound) {
                            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "sea_born")))) {
                                if (!(entityiterator == target)) {
                                    continue;
                                }
                            }
                            if (distanceTo(entityiterator) <= 5) {
                                entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.OCEAN_MAGIC),
                                        (float) (d * 0.2));
                                SIHelper.causeSanityInjury(entityiterator, d * 30, SanityEvent.Hurt.Type.ENTITY);
                            }
                        }
                    }
                }
                double angle;
                double d1;
                d1 = 5;
                for (int index0 = 0; index0 < 6; index0++) {
                    angle = Mth.nextDouble(RandomSource.create(), 0, 6.283);
                    if (world instanceof ServerLevel level)
                        level.sendParticles(ParticleTypes.ELECTRIC_SPARK, (x + d1 * Math.sin(angle)), (y + 0.4), (z + d1 * Math.cos(angle)), 2, 0.1, 0.1, 0.1, 0.1);
                }
            }
        }
        this.refreshDimensions();
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

    private PlayState movementPredicate(AnimationState event) {
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

    private PlayState attackingPredicate(AnimationState event) {
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