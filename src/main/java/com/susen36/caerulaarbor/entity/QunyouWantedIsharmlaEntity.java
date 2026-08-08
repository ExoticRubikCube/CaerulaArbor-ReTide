package com.susen36.caerulaarbor.entity;

import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CASounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import javax.annotation.Nullable;

public class QunyouWantedIsharmlaEntity extends SeaMonster {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(QunyouWantedIsharmlaEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(QunyouWantedIsharmlaEntity.class, EntityDataSerializers.STRING);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";
    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.YELLOW, ServerBossEvent.BossBarOverlay.NOTCHED_12);

    public QunyouWantedIsharmlaEntity(Level world) {
        this(CAEntities.QUNYOU_WANTED_ISHARMLA.get(), world);
    }

    public QunyouWantedIsharmlaEntity(EntityType<QunyouWantedIsharmlaEntity> type, Level world) {
        super(type, world);
        xpReward = 256;
        setNoAi(false);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.5f);
        setPersistenceRequired();
        this.moveControl = new FlyingMoveControl(this, 10, true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHOOT, false);
        builder.define(DATA_ANIMATION, "undefined");
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        return new FlyingPathNavigation(this, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.5, false));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1, 20) {
            @Override
            protected Vec3 getPosition() {
                RandomSource random = QunyouWantedIsharmlaEntity.this.getRandom();
                double dir_x = QunyouWantedIsharmlaEntity.this.getX() + ((random.nextFloat() * 2 - 1) * 16);
                double dir_y = QunyouWantedIsharmlaEntity.this.getY() + ((random.nextFloat() * 2 - 1) * 16);
                double dir_z = QunyouWantedIsharmlaEntity.this.getZ() + ((random.nextFloat() * 2 - 1) * 16);
                return new Vec3(dir_x, dir_y, dir_z);
            }
        });
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
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
        if (source.is(DamageTypes.FALL))
            return false;
        if (source.is(DamageTypes.CACTUS))
            return false;
        if (source.is(DamageTypes.DROWN))
            return false;
        return super.hurt(source, amount);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata);
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        if (this instanceof QunyouWantedIsharmlaEntity) {
            this.setAnimation("animation.isharmla.to_monster");
        }
        if (world instanceof Level level) {
            level.playSound(null, BlockPos.containing(x, y, z), CASounds.ISHARMLA_TO_MONSTERR.get(), SoundSource.HOSTILE, 2, 1);
        }
        return retval;
    }


    @Override
    public void baseTick() {
        super.baseTick();
        this.refreshDimensions();
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose p_33597_) {
        return super.getDefaultDimensions(p_33597_).scale((float) 0.35);
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
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.6);
        builder = builder.add(Attributes.MAX_HEALTH, 625554);
        builder = builder.add(Attributes.ARMOR, 24);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 6593);
        builder = builder.add(Attributes.FOLLOW_RANGE, 36);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
        builder = builder.add(Attributes.FLYING_SPEED, 0.6);
        builder = builder.add(CAAttributes.GENERAL_DEFENSE, 1656);
        builder = builder.add(CAAttributes.MAGIC_RESISTANCE, 90);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.isharmla.idle_monster"));
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

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            this.remove(RemovalReason.KILLED);
            this.dropExperience(this.getKillCredit());
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
        data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
    }


    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}