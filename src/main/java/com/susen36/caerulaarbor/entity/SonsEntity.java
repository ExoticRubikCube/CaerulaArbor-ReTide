package com.susen36.caerulaarbor.entity;

import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animation.*;

import java.util.Comparator;

public class SonsEntity extends SeaMonster {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(SonsEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(SonsEntity.class, EntityDataSerializers.STRING);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";

    public SonsEntity(Level world) {
        this(CAEntities.SONS.get(), world);
    }

    public SonsEntity(EntityType<SonsEntity> type, Level world) {
        super(type, world);
        xpReward = 0;
        setNoAi(false);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHOOT, false);
        builder.define(DATA_ANIMATION, "undefined");
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 0, false));
    }

    @Override
    public SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.TROPICAL_FISH_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.TROPICAL_FISH_DEATH;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (Math.random() == 0.15) {
            if (!this.level().isClientSide())
                this.addEffect(new MobEffectInstance(CAMobEffects.COOLDOWN_SINAL, 40, 0, false, false));
        }
        if (source.is(DamageTypes.FALL))
            return false;
        if (source.is(DamageTypes.DROWN))
            return false;
        return super.hurt(source, amount);
    }

    @Override
    public void awardKillScore(Entity entity, int score, DamageSource damageSource) {
        super.awardKillScore(entity, score, damageSource);
        if (this instanceof SonsEntity) {
            this.setAnimation("animation.bishopson.end");
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity owner;
        Entity tgt;
        owner = world.getEntitiesOfClass(BishopFishEntity.class, AABB.ofSize(new Vec3(x, y, z), 96, 96, 96), e -> true).stream().min(new Object() {
            Comparator<Entity> compareDistOf(double x, double y, double z) {
                return Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(x, y, z));
            }
        }.compareDistOf(x, y, z)).orElse(null);
        if (!(owner == null)) {
            this.getNavigation().moveTo((owner.getX()), (owner.getY()), (owner.getZ()), 0.33);
        } else {
            this.getNavigation().stop();
        }
        if (!((Entity) this instanceof LivingEntity livEnt7 && livEnt7.hasEffect(CAMobEffects.COOLDOWN_SINAL))) {
            tgt = (Entity) this instanceof Mob mobEnt ? mobEnt.getTarget() : null;
            if (!(null == tgt)) {
                if (distanceTo(tgt) <= 3.5) {
                    if (tgt instanceof LivingEntity && !this.level().isClientSide())
                        this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 3, false, false));
                    {
                        tgt.teleportTo((getX() + getLookAngle().x * 0.33), (getY()), (getZ() + getLookAngle().z * 0.33));
                        if (tgt instanceof ServerPlayer serverPlayer)
                            serverPlayer.connection.teleport((getX() + getLookAngle().x * 0.33), (getY()), (getZ() + getLookAngle().z * 0.33), tgt.getYRot(), tgt.getXRot());
                    }
                    tgt.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(x, y, z));
                    if (this instanceof SonsEntity) {
                        this.setAnimation("animation.bishopson.loop");
                    }
                }
            }
        }
        this.refreshDimensions();
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


    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder = builder.add(Attributes.MAX_HEALTH, 12);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
        builder = builder.add(Attributes.FOLLOW_RANGE, 18);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
        builder = builder.add(Attributes.STEP_HEIGHT, 0.6f);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.bishopson.idle"));
        }
        return PlayState.STOP;
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
        data.add(new AnimationController<>(this, "movement", 6, this::movementPredicate));
        data.add(new AnimationController<>(this, "procedure", 6, this::procedurePredicate));
    }


    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}