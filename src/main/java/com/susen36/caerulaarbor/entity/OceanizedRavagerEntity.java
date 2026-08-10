package com.susen36.caerulaarbor.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.init.CADamageTypes;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animation.*;

public class OceanizedRavagerEntity extends SeaMonster {
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(OceanizedRavagerEntity.class, EntityDataSerializers.STRING);
    public String animationprocedure = "empty";
    String prevAnim = "empty";
    private boolean swinging;
    private long lastSwing;

    public OceanizedRavagerEntity(Level world) {
        this(CAEntities.OCEANIZED_RAVAGER.get(), world);
    }

    public OceanizedRavagerEntity(EntityType<OceanizedRavagerEntity> type, Level world) {
        super(type, world);
        xpReward = 24;
        setNoAi(false);
        setPersistenceRequired();
    }

    public static void summonFellows(LevelAccessor world, double x, double y, double z, int count) {
        if (!(world instanceof ServerLevel level)) {
            return;
        }
        BlockPos spawnPos = BlockPos.containing(x, y, z);
        RandomSource random = world.getRandom();
        for (int index = 0; index < count; index++) {
            Entity entityToSpawn = switch (random.nextInt(5)) {
                case 0 -> CAEntities.OCEANIZED_PILLAGER.get().spawn(level, spawnPos, MobSpawnType.MOB_SUMMONED);
                case 1 -> CAEntities.OCEANIZED_VINDICATOR.get().spawn(level, spawnPos, MobSpawnType.MOB_SUMMONED);
                case 2 -> CAEntities.OCEANIZED_VILLAGER.get().spawn(level, spawnPos, MobSpawnType.MOB_SUMMONED);
                case 3 -> CAEntities.OCEANIZED_WITCH.get().spawn(level, spawnPos, MobSpawnType.MOB_SUMMONED);
                default -> CAEntities.OCEANIZED_EVOKER.get().spawn(level, spawnPos, MobSpawnType.MOB_SUMMONED);
            };
            if (entityToSpawn != null) {
                entityToSpawn.setDeltaMovement(0, 0.15, 0);
            }
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ANIMATION, "undefined");
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.75, false));
        this.goalSelector.addGoal(15, new RandomStrollGoal(this, 1));
        this.goalSelector.addGoal(16, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public SoundEvent getAmbientSound() {
        return SoundEvents.RAVAGER_AMBIENT;
    }

    @Override
    public void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.RAVAGER_STEP, 0.15f, 1);
    }

    @Override
    public SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.RAVAGER_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.RAVAGER_DEATH;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (!this.level().isClientSide()) {
            CaerulaArbor.queueServerWork(11, () -> {
                if (this.isAlive() && target.isAlive() && this.distanceTo(target) <= 3.8) {
                    target.hurt(
                            CADamageTypes.source(this.level(), CADamageTypes.GENERIC_SEABORN_ATTACK, this), (float) (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0));
                }
            });
        }
        return true;
    }

    @Override
    public void baseTick() {
        super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        BlockState target;
        boolean breaked = false;
        if (this.deathTime == 22) {
            summonFellows(world, x, y, z, 3);
        }
        if (this.isAggressive()) {
            if (WorldUtils.canGrief(world)) {
                if (Math.random() < 0.05) {
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = 1; dy <= 3; dy++) {
                            for (int dz = -1; dz <= 1; dz++) {
                                target = (world.getBlockState(BlockPos.containing(x + dx, y + dy, z + dz)));
                                if (world.getBlockFloorHeight(BlockPos.containing(x + dx, y + dy, z + dz)) > 0 && target.getDestroySpeed(world, BlockPos.containing(0, 0, 0)) > 0 && target.getDestroySpeed(world, BlockPos.containing(0, 0, 0)) <= 5) {
                                    world.destroyBlock(BlockPos.containing(x + dx, y + dy, z + dz), false);
                                    breaked = true;
                                }
                            }
                        }
                    }
                    if (breaked) {
                        if (world instanceof Level level) {
                            if (!level.isClientSide()) {
                                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.WITHER_BREAK_BLOCK, SoundSource.HOSTILE, 1, 1);
                            } else {
                                level.playLocalSound(x, y, z, SoundEvents.WITHER_BREAK_BLOCK, SoundSource.HOSTILE, 1, 1, false);
                            }
                        }
                    }
                }
            }
        }
        this.refreshDimensions();
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

                    && !this.isAggressive()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_ravager.walk"));
            }
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_ravager.die"));
            }
            if (this.isAggressive() && event.isMoving()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_ravager.sprint"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_ravager.idle"));
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
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_ravager.attack"));
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
        if (this.deathTime == 30) {
            this.remove(RemovalReason.KILLED);
            this.dropExperience(this.getKillCredit());
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.15);
        builder = builder.add(Attributes.MAX_HEALTH, 215);
        builder = builder.add(Attributes.ARMOR, 6);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 17);
        builder = builder.add(Attributes.FOLLOW_RANGE, 27);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 1);
        builder = builder.add(Attributes.ATTACK_KNOCKBACK, 1);
        builder = builder.add(Attributes.STEP_HEIGHT, 1.5f);
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
        data.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
        data.add(new AnimationController<>(this, "attacking", 0, this::attackingPredicate));
        data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
    }


    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}