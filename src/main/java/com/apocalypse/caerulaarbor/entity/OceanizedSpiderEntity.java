package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CABlocks;
import com.apocalypse.caerulaarbor.init.CADamageTypes;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import com.apocalypse.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.EnumSet;

public class OceanizedSpiderEntity extends SeaMonster {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(OceanizedSpiderEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(OceanizedSpiderEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_MUTE_TIME = SynchedEntityData.defineId(OceanizedSpiderEntity.class, EntityDataSerializers.INT);
    public String animationprocedure = "empty";
    String prevAnim = "empty";
    private boolean swinging;
    private long lastSwing;

    public OceanizedSpiderEntity(Level world) {
        this(CAEntities.OCEANIZED_SPIDER.get(), world);
    }

    public OceanizedSpiderEntity(EntityType<OceanizedSpiderEntity> type, Level world) {
        super(type, world);
        xpReward = 5;
        setNoAi(false);
        setMaxUpStep(0.6f);
        this.moveControl = new FlyingMoveControl(this, 10, true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.4);
        builder = builder.add(Attributes.MAX_HEALTH, 32);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 7);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.85);
        builder = builder.add(Attributes.FLYING_SPEED, 0.4);
        return builder;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SHOOT, false);
        this.entityData.define(DATA_ANIMATION, "undefined");
        this.entityData.define(DATA_MUTE_TIME, 0);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (!this.level().isClientSide()) {
            CaerulaArborMod.queueServerWork(10, () -> {
                if (this.isAlive() && target.isAlive() && this.distanceTo(target) <= 2.6) {
                    target.hurt(
                            CADamageTypes.source(this.level(), CADamageTypes.GENERIC_SEABORN_ATTACK, this), (float) (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0));
                }
            });
        }
        return true;
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        return new FlyingPathNavigation(this, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.5, true) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return 3.24;
            }
        });
        this.goalSelector.addGoal(3, new Goal() {
            {
                this.setFlags(EnumSet.of(Flag.MOVE));
            }

            public boolean canUse() {
                return OceanizedSpiderEntity.this.getTarget() != null && !OceanizedSpiderEntity.this.getMoveControl().hasWanted();
            }

            @Override
            public boolean canContinueToUse() {
                return OceanizedSpiderEntity.this.getMoveControl().hasWanted() && OceanizedSpiderEntity.this.getTarget() != null && OceanizedSpiderEntity.this.getTarget().isAlive();
            }

            @Override
            public void start() {
                LivingEntity livingentity = OceanizedSpiderEntity.this.getTarget();
                Vec3 vec3d;
                if (livingentity != null) {
                    vec3d = livingentity.getEyePosition(1);
                    OceanizedSpiderEntity.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 1.25);
                }
            }

            @Override
            public void tick() {
                LivingEntity livingentity = OceanizedSpiderEntity.this.getTarget();
                if (OceanizedSpiderEntity.this.getBoundingBox().intersects(livingentity.getBoundingBox())) {
                    OceanizedSpiderEntity.this.doHurtTarget(livingentity);
                } else {
                    double d0 = OceanizedSpiderEntity.this.distanceToSqr(livingentity);
                    if (d0 < 16) {
                        Vec3 vec3d = livingentity.getEyePosition(1);
                        OceanizedSpiderEntity.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 1.25);
                    }
                }
            }
        });
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolem.class, true, false));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, SnowGolem.class, true, false));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Villager.class, true, false));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, Illusioner.class, true, false));
        this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, Pillager.class, true, false));
        this.targetSelector.addGoal(9, new NearestAttackableTargetGoal<>(this, Vindicator.class, true, false));
        this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, Witch.class, true, false));
        this.targetSelector.addGoal(11, new NearestAttackableTargetGoal<>(this, Piglin.class, true, false));
        this.targetSelector.addGoal(12, new NearestAttackableTargetGoal<>(this, PiglinBrute.class, true, false));
        this.targetSelector.addGoal(13, new NearestAttackableTargetGoal<>(this, ZombifiedPiglin.class, true, false));
        this.targetSelector.addGoal(14, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, target -> EntityUtils.isOceanizedPlayerNearby(this.level(), this.getX(), this.getY(), this.getZ())));
        this.goalSelector.addGoal(16, new RandomStrollGoal(this, 1, 20) {
            @Override
            protected Vec3 getPosition() {
                RandomSource random = OceanizedSpiderEntity.this.getRandom();
                double dir_x = OceanizedSpiderEntity.this.getX() + ((random.nextFloat() * 2 - 1) * 16);
                double dir_y = OceanizedSpiderEntity.this.getY() + ((random.nextFloat() * 2 - 1) * 16);
                double dir_z = OceanizedSpiderEntity.this.getZ() + ((random.nextFloat() * 2 - 1) * 16);
                return new Vec3(dir_x, dir_y, dir_z);
            }
        });
        this.goalSelector.addGoal(17, new RandomLookAroundGoal(this));
    }

    @Override
    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    @Override
    public SoundEvent getAmbientSound() {
        return SoundEvents.SPIDER_AMBIENT;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return SoundEvents.SPIDER_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.SPIDER_DEATH;
    }

    @Override
    public boolean causeFallDamage(float l, float d, DamageSource source) {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.FALL))
            return false;
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
        this.refreshDimensions();
    }

    @Override
    public EntityDimensions getDimensions(Pose p_33597_) {
        return super.getDimensions(p_33597_).scale((float) 1);
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    public void setNoGravity(boolean ignored) {
        super.setNoGravity(true);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.setNoGravity(true);
    }

    private PlayState movementPredicate(AnimationState<?> event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) && this.onGround()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_spider.walk"));
            }
            if (!this.onGround()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_spider.fly"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_spider.idle"));
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
        if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_spider.attack"));
        }
        return PlayState.CONTINUE;
    }

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
            LevelAccessor world = this.level();
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();
            if (WorldUtils.canGrief(world)) {
                if ((world.getBlockState(BlockPos.containing(x, y, z))).canBeReplaced()) {
                    if (((Entity) this instanceof OceanizedSpiderEntity datEntI ? datEntI.getEntityData().get(DATA_MUTE_TIME) : 0) <= 0) {
                        if (Math.random() < 0.25) {
                            world.levelEvent(2001, BlockPos.containing(x, y, z), Block.getId(CABlocks.RED_OVARY.get().defaultBlockState()));
                            if (world instanceof Level level) {
                                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.SCULK_VEIN_PLACE, SoundSource.HOSTILE, 1, (float) 0.8);
                            }
                            if (world instanceof ServerLevel level)
                                FallingBlockEntity.fall(level, BlockPos.containing(x, y, z), CABlocks.RED_OVARY.get().defaultBlockState());
                        } else {
                            world.levelEvent(2001, BlockPos.containing(x, y, z), Block.getId(CABlocks.OCEAN_OVARY.get().defaultBlockState()));
                            if (world instanceof Level level) {
                                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.SCULK_VEIN_PLACE, SoundSource.HOSTILE, 1, 1);
                            }
                            if (world instanceof ServerLevel level)
                                FallingBlockEntity.fall(level, BlockPos.containing(x, y, z), CABlocks.OCEAN_OVARY.get().defaultBlockState());
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
        data.add(new AnimationController<>(this, "movement", 2, this::movementPredicate));
        data.add(new AnimationController<>(this, "attacking", 2, this::attackingPredicate));
        data.add(new AnimationController<>(this, "procedure", 2, this::procedurePredicate));
    }


    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}
