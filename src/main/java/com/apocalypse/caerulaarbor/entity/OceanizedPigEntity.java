package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CAAttributes;
import com.apocalypse.caerulaarbor.init.CABlocks;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.util.CaerulaUtil;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import com.apocalypse.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
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
import net.minecraft.world.level.block.Blocks;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class OceanizedPigEntity extends SeaMonster {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(OceanizedPigEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(OceanizedPigEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_MUTE_TIME = SynchedEntityData.defineId(OceanizedPigEntity.class, EntityDataSerializers.INT);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";

    public OceanizedPigEntity(Level world) {
        this(CAEntities.OCEANIZED_PIG.get(), world);
    }

    public OceanizedPigEntity(EntityType<OceanizedPigEntity> type, Level world) {
        super(type, world);
        xpReward = 2;
        setNoAi(false);
        setMaxUpStep(0.6f);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SHOOT, false);
        this.entityData.define(DATA_ANIMATION, "undefined");
        this.entityData.define(DATA_MUTE_TIME, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.4, false) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return 3.0625;
            }
        });
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true, false));
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
        this.goalSelector.addGoal(15, new RandomStrollGoal(this, 0.4));
        this.goalSelector.addGoal(16, new FloatGoal(this));
        this.goalSelector.addGoal(17, new RandomLookAroundGoal(this));
    }

    @Override
    public SoundEvent getAmbientSound() {
        return SoundEvents.PIG_AMBIENT;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return SoundEvents.PIG_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.PIG_DEATH;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.DROWN))
            return false;
        return super.hurt(source, amount);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("MuteTime", this.entityData.get(DATA_MUTE_TIME));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("MuteTime")) {
            this.entityData.set(DATA_MUTE_TIME, compound.getInt("MuteTime"));
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (this.isAlive()) {
            if (this.hasEffect(CAMobEffects.MUTE.get())) {
                if ((Entity) this instanceof OceanizedPigEntity datEntSetI) {
                    datEntSetI.getEntityData().set(DATA_MUTE_TIME,
                            datEntSetI.hasEffect(CAMobEffects.MUTE.get()) ? datEntSetI.getEffect(CAMobEffects.MUTE.get()).getDuration() : 0);
                }
            } else if (((Entity) this instanceof OceanizedPigEntity datEntI ? datEntI.getEntityData().get(DATA_MUTE_TIME) : 0) == 1) {
                if ((Entity) this instanceof OceanizedPigEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_MUTE_TIME, 0);
            }
        }
        this.refreshDimensions();
    }

    @Override
    public EntityDimensions getDimensions(Pose p_33597_) {
        return super.getDimensions(p_33597_).scale((float) 1);
    }


    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(CAAttributes.SANITY_RATE.get(), 6);
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.4);
        builder = builder.add(Attributes.MAX_HEALTH, 11);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16);
        return builder;
    }

    private PlayState movementPredicate(AnimationState<?> event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

            ) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_pig.move"));
            }
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_pig.die"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_pig.idle"));
        }
        return PlayState.STOP;
    }

    private PlayState attackingPredicate(AnimationState<?> event) {
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
            this.swinging = true;
            this.lastSwing = level().getGameTime();
        }
        if (this.swinging && this.lastSwing + 18L <= level().getGameTime()) {
            this.swinging = false;
        }
        if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_pig.attack"));
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
        if (this.deathTime == 10) {
            this.remove(RemovalReason.KILLED);
            this.dropExperience();
            LevelAccessor world = this.level();
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();
            if (WorldUtils.canGrief(world)) {
                if (((Entity) this instanceof OceanizedPigEntity datEntI ? datEntI.getEntityData().get(DATA_MUTE_TIME) : 0) <= 0) {
                    if (CABlocks.SEA_TRAIL_INIT.get().defaultBlockState().canSurvive(world, BlockPos.containing(x, y, z)) && !(world.getBlockFloorHeight(BlockPos.containing(x, y, z)) > 0)) {
                        CaerulaUtil.replaceTrail(world, CABlocks.SEA_TRAIL_INIT.get().defaultBlockState(), (world.getFluidState(BlockPos.containing(x, y, z)).createLegacyBlock()).getBlock() == Blocks.WATER, x, y, z);
                    }
                    for (Direction directioniterator : Direction.Plane.HORIZONTAL) {
                        if (CABlocks.SEA_TRAIL_INIT.get().defaultBlockState().canSurvive(world, BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ()))
                                && !(world.getBlockFloorHeight(BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ())) > 0)) {
                            if (Math.random() < 0.33) {
                                CaerulaUtil.replaceTrail(world, CABlocks.SEA_TRAIL_INIT.get().defaultBlockState(),
                                        (world.getFluidState(BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ())).createLegacyBlock()).getBlock() == Blocks.WATER, x + directioniterator.getStepX(), y,
                                        z + directioniterator.getStepZ());
                            }
                        }
                    }
                }
            }
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
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}
