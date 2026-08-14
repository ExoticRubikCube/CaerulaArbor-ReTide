package com.susen36.caerulaarbor.entity;

import com.susen36.babel.init.BabelAttributes;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.init.CADamageTypes;
import com.susen36.caerulaarbor.init.CAEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animation.*;

public class OceanizedCatEntity extends SeaMonster {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(OceanizedCatEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(OceanizedCatEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_ACTION_TIME = SynchedEntityData.defineId(OceanizedCatEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> DATA_STATE_SNEAKING = SynchedEntityData.defineId(OceanizedCatEntity.class, EntityDataSerializers.BOOLEAN);
    public String animationprocedure = "empty";
    String prevAnim = "empty";
    private boolean swinging;
    private long lastSwing;

    public OceanizedCatEntity(Level world) {
        this(CAEntities.OCEANIZED_CAT.get(), world);
    }

    public OceanizedCatEntity(EntityType<OceanizedCatEntity> type, Level world) {
        super(type, world);
        xpReward = 4;
        setNoAi(false);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHOOT, false);
        builder.define(DATA_ANIMATION, "undefined");
        builder.define(DATA_ACTION_TIME, 0);
        builder.define(DATA_STATE_SNEAKING, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.6, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Chicken.class, true, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Rabbit.class, true, false));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Cat.class, true, false));
        this.goalSelector.addGoal(18, new RandomStrollGoal(this, 1));
        this.goalSelector.addGoal(19, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(20, new FloatGoal(this));
    }

    @Override
    public SoundEvent getAmbientSound() {
        return SoundEvents.CAT_AMBIENT;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.CAT_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.CAT_DEATH;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        double targetX = target.getX();
        double targetY = target.getY();
        double targetZ = target.getZ();
        if (!this.level().isClientSide()) {
            this.level().playSound(null, BlockPos.containing(targetX, targetY, targetZ),
                    SoundEvents.CAT_HISS, SoundSource.HOSTILE, 0.75F,
                    (float) Mth.nextDouble(RandomSource.create(), 0.85, 1.15));
            CaerulaArbor.queueServerWork(9, () -> {
                if (this.isAlive() && target.isAlive() && this.distanceTo(target) <= 2) {
                    target.hurt(
                            CADamageTypes.source(this.level(), CADamageTypes.GENERIC_SEABORN_ATTACK, this), (float) (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0));
                }
            });
            CaerulaArbor.queueServerWork(14, () -> {
                if (this.isAlive() && target.isAlive() && this.distanceTo(target) <= 3) {
                    target.hurt(
                            CADamageTypes.source(this.level(), CADamageTypes.GENERIC_SEABORN_ATTACK, this), (float) (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0));
                }
            });
        }
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        setShiftKeyDown(false);
        if ((Entity) this instanceof OceanizedCatEntity datEntSetL)
            datEntSetL.getEntityData().set(DATA_STATE_SNEAKING, false);
        if (source.is(DamageTypes.FALL))
            return false;
        if (source.is(DamageTypes.DROWN))
            return false;
        return super.hurt(source, amount);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("ActionTime", this.entityData.get(DATA_ACTION_TIME));
        compound.putBoolean("StateSneaking", this.entityData.get(DATA_STATE_SNEAKING));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("ActionTime")) {
            this.entityData.set(DATA_ACTION_TIME, compound.getInt("ActionTime"));
        }
        if (compound.contains("StateSneaking")) {
            this.entityData.set(DATA_STATE_SNEAKING, compound.getBoolean("StateSneaking"));
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        double time_stamp;
        boolean sneak;
        if (this.isAlive() && tickCount % 10 == 0) {
            time_stamp = (Entity) this instanceof OceanizedCatEntity datEntI ? datEntI.getEntityData().get(DATA_ACTION_TIME) : 0;
            sneak = (Entity) this instanceof OceanizedCatEntity datEntL3 && datEntL3.getEntityData().get(DATA_STATE_SNEAKING);
            if (time_stamp > 0) {
                if ((Entity) this instanceof OceanizedCatEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_ACTION_TIME, (int) (time_stamp - 1));
            } else if (Math.random() < 0.02) {
                if (sneak) {
                    if ((Entity) this instanceof OceanizedCatEntity datEntSetL)
                        datEntSetL.getEntityData().set(DATA_STATE_SNEAKING, false);
                    if ((Entity) this instanceof OceanizedCatEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_ACTION_TIME, 10);
                } else if (!this.isAggressive()) {
                    if ((Entity) this instanceof OceanizedCatEntity datEntSetL)
                        datEntSetL.getEntityData().set(DATA_STATE_SNEAKING, true);
                    if ((Entity) this instanceof OceanizedCatEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_ACTION_TIME, 10);
                }
            }
            setShiftKeyDown(sneak);
        }
        if (this.isAggressive()) {
            setShiftKeyDown(false);
            if ((Entity) this instanceof OceanizedCatEntity datEntSetL)
                datEntSetL.getEntityData().set(DATA_STATE_SNEAKING, false);
        }
        this.refreshDimensions();
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

                    && !this.isAggressive()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_cat.walk"));
            }
            if (this.isShiftKeyDown()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_cat.sneak"));
            }
            if (this.isAggressive() && event.isMoving()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_cat.sprint"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_cat.idle"));
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
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_cat.attack"));
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

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(BabelAttributes.MAGIC_RESISTANCE, 35);
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.16);
        builder = builder.add(Attributes.MAX_HEALTH, 24);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 6);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.33);
        builder = builder.add(Attributes.STEP_HEIGHT, 0.6f);
        return builder;
    }
    
    public String getSyncedAnimation() {
        return this.entityData.get(DATA_ANIMATION);
    }

    public void setAnimation(String animation) {
        this.entityData.set(DATA_ANIMATION, animation);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "movement", 1, this::movementPredicate));
        data.add(new AnimationController<>(this, "attacking", 1, this::attackingPredicate));
        data.add(new AnimationController<>(this, "procedure", 1, this::procedurePredicate));
    }

    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}