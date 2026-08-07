package com.susen36.caerulaarbor.entity.tidelinked;

import com.susen36.babel.api.entity.ElementalAttacker;
import com.susen36.babel.elemental.base.AbstractEPCapability;
import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.util.EntityUtils;
import com.susen36.caerulaarbor.util.WorldUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
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
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class AbstractTidelinkedEntity extends SeaMonster implements ElementalAttacker {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(AbstractTidelinkedEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(AbstractTidelinkedEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_SKILLP = SynchedEntityData.defineId(AbstractTidelinkedEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(AbstractTidelinkedEntity.class, EntityDataSerializers.INT);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";
    protected final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.BLUE, ServerBossEvent.BossBarOverlay.PROGRESS);

    public AbstractTidelinkedEntity(EntityType<? extends AbstractTidelinkedEntity> type, Level world) {
        super(type, world);
        xpReward = 6;
        setNoAi(false);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.5f);
        setPersistenceRequired();
    }

    protected abstract String getAnimationPrefix();

    protected abstract int getRevivalDuration();

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHOOT, false);
        builder.define(DATA_ANIMATION, "undefined");
        builder.define(DATA_SKILLP, 100);
        builder.define(DATA_DURATION, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && !AbstractTidelinkedEntity.this.isFaking();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !AbstractTidelinkedEntity.this.isFaking();
            }
        });
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true, false) {
            @Override
            public boolean canUse() {
                return super.canUse() && !AbstractTidelinkedEntity.this.isFaking();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !AbstractTidelinkedEntity.this.isFaking();
            }
        });
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, SnowGolem.class, true, false));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Villager.class, true, false));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Illusioner.class, true, false));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, Pillager.class, true, false));
        this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, Vindicator.class, true, false));
        this.targetSelector.addGoal(9, new NearestAttackableTargetGoal<>(this, Witch.class, true, false));
        this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, Piglin.class, true, false));
        this.targetSelector.addGoal(11, new NearestAttackableTargetGoal<>(this, PiglinBrute.class, true, false));
        this.targetSelector.addGoal(12, new NearestAttackableTargetGoal<>(this, ZombifiedPiglin.class, true, false));
        this.targetSelector.addGoal(13, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, target -> EntityUtils.isOceanizedPlayerNearby(this.level(), this.getX(), this.getY(), this.getZ())));
        this.goalSelector.addGoal(15, new RandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(16, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(17, new FloatGoal(this));
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
    protected void playAttackSound() {
        this.level().playSound(null, this.blockPosition(), SoundEvents.GUARDIAN_ATTACK, SoundSource.HOSTILE, 2, 1);
    }

    @Nullable
    private UUID ownerUUID;

    public void setOwner(@Nullable LivingEntity entity) {
        this.ownerUUID = entity != null ? entity.getUUID() : null;
    }

    @Nullable
    protected TidelinkedBishopEntity getLinkedBishop() {
        if (this.ownerUUID == null || !(this.level() instanceof ServerLevel serverLevel))
            return null;
        Entity entity = serverLevel.getEntity(this.ownerUUID);
        return entity instanceof TidelinkedBishopEntity bishop ? bishop : null;
    }

    @Override
    public AbstractEPCapability.EPType getElementalType() {
        return AbstractEPCapability.EPType.CORROSION;
    }

    @Override
    public double getElementalRate() {
        return 0.5D;
    }

    @Override
    public double getElementalInjuryDamage() {
        return 0.0D;
    }

    @Override
    public void setHealth(float pHealth) {
        if (pHealth <= 0) {
            TidelinkedBishopEntity bishop = this.getLinkedBishop();
            boolean keepup = bishop != null && this.distanceToSqr(bishop) < 1024.0;
            if (bishop != null && bishop.hasEffect(CAMobEffects.FAKE_DEATH)) {
                keepup = false;
            }
            if (keepup) {
                super.setHealth(Math.max(this.getHealth(), 1.0F));
                this.setShiftKeyDown(true);
                if (!this.level().isClientSide()) {
                    this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, this.getRevivalDuration(), 0, false, false));
                    this.addEffect(new MobEffectInstance(CAMobEffects.FAKE_DEATH, this.getRevivalDuration(), 1, false, false));
                }
                return;
            }
        }
        super.setHealth(pHealth);
    }

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Skillp", this.entityData.get(DATA_SKILLP));
        compound.putInt("Duration", this.entityData.get(DATA_DURATION));
        if (this.ownerUUID != null) {
            compound.putUUID("Owner", this.ownerUUID);
        }
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Skillp")) {
            this.entityData.set(DATA_SKILLP, compound.getInt("Skillp"));
        }
        if (compound.contains("Duration")) {
            this.entityData.set(DATA_DURATION, compound.getInt("Duration"));
        }
        if (compound.hasUUID("Owner")) {
            this.ownerUUID = compound.getUUID("Owner");
        }
	}

    @Override
    public void baseTick() {
        super.baseTick();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        if (this.hasEffect(CAMobEffects.FAKE_DEATH)) {
            TidelinkedBishopEntity bishop = this.getLinkedBishop();
            boolean keepup = bishop != null && this.distanceToSqr(bishop) < 1024.0;
            if (bishop != null && bishop.hasEffect(CAMobEffects.FAKE_DEATH)) {
                keepup = false;
            }
            if (!keepup) {
                this.setAnimation(this.getAnimationPrefix() + ".die");
                this.removeAllEffects();
                this.hurt(this.level().damageSources().fellOutOfWorld(), 114514);
            }
        } else {
            double skillCooldown = this.getEntityData().get(DATA_SKILLP);
            double skillDuration = this.getEntityData().get(DATA_DURATION);
            if (skillDuration > 0) {
                this.getEntityData().set(DATA_DURATION, (int) (skillDuration - 1));
            }
            if (skillCooldown > 0) {
                this.getEntityData().set(DATA_SKILLP, (int) (skillCooldown - 1));
                if (MapVariables.get(this.level()).strategy_grow >= 3) {
                    this.getEntityData().set(DATA_SKILLP, (int) (skillCooldown - 2));
                }
            }
            TidelinkedBishopEntity bishop = this.getLinkedBishop();
            if (bishop instanceof LivingEntity nearestLiving && nearestLiving.hasEffect(CAMobEffects.FAKE_DEATH)) {
                EntityUtils.spawnLinkParticles(this.level(), this, bishop);
                if (MapVariables.get(this.level()).strategy_silence >= 3) {
                    if (this.getAttributes().hasAttribute(CAAttributes.MISSRATE)) {
                        this.getAttribute(CAAttributes.MISSRATE).setBaseValue(40);
                    }
                } else if (MapVariables.get(this.level()).strategy_subsisting >= 4) {
                    if (this.getAttributes().hasAttribute(CAAttributes.MISSRATE)) {
                        this.getAttribute(CAAttributes.MISSRATE).setBaseValue(20);
                    }
                }
            }
        }
        this.refreshDimensions();
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose p_33597_) {
        return super.getDefaultDimensions(p_33597_).scale((float) 1.2);
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

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

            ) {
                return event.setAndContinue(RawAnimation.begin().thenLoop(this.getAnimationPrefix() + ".move"));
            }
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay(this.getAnimationPrefix() + ".die"));
            }
            if (this.isShiftKeyDown()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop(this.getAnimationPrefix() + ".die_loop"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop(this.getAnimationPrefix() + ".idle"));
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
            return event.setAndContinue(RawAnimation.begin().thenPlay(this.getAnimationPrefix() + ".attack"));
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
        if (this.deathTime == 22) {
            this.remove(RemovalReason.KILLED);
            this.dropExperience(this.getKillCredit());
            WorldUtils.dropRelicTidebi(this.level(), this.getX(), this.getY(), this.getZ());
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

    public boolean isFaking() {
        return this.getEntityData().get(AbstractTidelinkedEntity.DATA_DURATION) > 0 || this.hasEffect(CAMobEffects.FAKE_DEATH);
    }

    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}