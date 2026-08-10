package com.susen36.caerulaarbor.entity;

import com.susen36.babel.api.entity.ElementalAttacker;
import com.susen36.babel.elemental.base.AbstractEPCapability;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CASounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import java.util.Comparator;
import java.util.List;

public class TideutantRockSpiderEntity extends SeaMonster implements ElementalAttacker {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(TideutantRockSpiderEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(TideutantRockSpiderEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(TideutantRockSpiderEntity.class, EntityDataSerializers.INT);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";

    public TideutantRockSpiderEntity(Level world) {
        this(CAEntities.TIDUTANT_ROCK_SPIDER.get(), world);
    }

    public TideutantRockSpiderEntity(EntityType<TideutantRockSpiderEntity> type, Level world) {
        super(type, world);
        xpReward = 16;
        setNoAi(false);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(0.6f);
        setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHOOT, false);
        builder.define(DATA_ANIMATION, "undefined");
        builder.define(DATA_DURATION, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this,  1, true) {

            @Override
            public boolean canUse() {
                return super.canUse() && isRockSpiderDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isRockSpiderDurative();
            }

        });
        this.goalSelector.addGoal(15, new RandomStrollGoal(this, 1) {
            @Override
            public boolean canUse() {
                return super.canUse() && isRockSpiderDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isRockSpiderDurative();
            }
        });
        this.goalSelector.addGoal(16, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(17, new FloatGoal(this));
    }

    @Override
    public AbstractEPCapability.EPType getElementalType() {
        return AbstractEPCapability.EPType.CORROSION;
    }

    @Override
    public double getElementalRate() {
        return 0.35D;
    }

    @Override
    public double getElementalInjuryDamage() {
        return 4.0D;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.SPIDER_AMBIENT, 0.15f, 1);
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
        LevelAccessor world = this.level();
        Entity sourceentity = source.getEntity();
        if (sourceentity != null) {
            if (!(sourceentity instanceof TideutantRockSpiderEntity) && !(sourceentity instanceof TidutantExcrescenceEntity)) {
                final Vec3 center = new Vec3(this.getX(), this.getY(), this.getZ());
                List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(32 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                for (Entity entityiterator : entfound) {
                    if (entityiterator instanceof TidutantExcrescenceEntity entity && sourceentity instanceof LivingEntity ent)
                        entity.setTarget(ent);
                }
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
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Duration")) {
            this.entityData.set(DATA_DURATION, compound.getInt("Duration"));
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        double dura;
        if (this.isAlive()) {
            dura = (Entity) this instanceof TideutantRockSpiderEntity datEntI ? datEntI.getEntityData().get(DATA_DURATION) : 0;
            if (dura > 0) {
                if ((Entity) this instanceof TideutantRockSpiderEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_DURATION, (int) (dura - 1));
            }
            double count = 0;
            {
                final Vec3 center = new Vec3(x, y, z);
                List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(48 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                for (Entity entityiterator : entfound) {
                    if (entityiterator instanceof TidutantExcrescenceEntity) {
                        count = count + 1;
                    }
                }
            }
            if (!(count > 32)) {
                if (tickCount % 120 == 75) {
                    if (this instanceof TideutantRockSpiderEntity) {
                        this.setAnimation("animation.tidutant_rock_spider.skill");
                    }
                    if ((Entity) this instanceof TideutantRockSpiderEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_DURATION, 20);
                    CaerulaArbor.queueServerWork(11, () -> {
                        if (this.isAlive()) {
                            if (world instanceof Level level) {
                                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.ZOMBIE_DESTROY_EGG, SoundSource.HOSTILE, 1, 1);
                            }
                            if (world instanceof ServerLevel level) {
                                Entity entityToSpawn = CAEntities.TIDUTANT_EXCRESCENCE.get().spawn(level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                                if (entityToSpawn != null) {
                                    entityToSpawn.setYRot((float) Mth.nextDouble(RandomSource.create(), 0, 360));
                                    entityToSpawn.setYBodyRot((float) Mth.nextDouble(RandomSource.create(), 0, 360));
                                    entityToSpawn.setYHeadRot((float) Mth.nextDouble(RandomSource.create(), 0, 360));
                                    entityToSpawn.setDeltaMovement((Mth.nextDouble(RandomSource.create(), -0.2, 0.2)), 0, (Mth.nextDouble(RandomSource.create(), -0.2, 0.2)));
                                }
                            }
                        }
                    });
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
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.18);
        builder = builder.add(Attributes.MAX_HEALTH, 85);
        builder = builder.add(Attributes.ARMOR, 3);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 7);
        builder = builder.add(Attributes.FOLLOW_RANGE, 24);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.75);
        builder = builder.add(Attributes.ATTACK_KNOCKBACK, 0.15);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.tidutant_rock_spider.move"));
            }
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.tidutant_rock_spider.die"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.tidutant_rock_spider.idle"));
        }
        return PlayState.STOP;
    }

    private PlayState attackingPredicate(AnimationState event) {
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
            this.swinging = true;
            this.lastSwing = level().getGameTime();
        }
        if (this.swinging && this.lastSwing + 17L <= level().getGameTime()) {
            this.swinging = false;
        }
        if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.tidutant_rock_spider.attack"));
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
        if (this.deathTime == 15) {
            this.remove(RemovalReason.KILLED);
            this.dropExperience(this.getKillCredit());
            LevelAccessor world = this.level();
            for (int index0 = 0; index0 < 4; index0++) {
                if (world instanceof ServerLevel level) {
                    Entity entityToSpawn = CAEntities.TIDUTANT_EXCRESCENCE.get().spawn(level, BlockPos.containing(this.getX(), this.getY(), this.getZ()), MobSpawnType.MOB_SUMMONED);
                    if (entityToSpawn != null) {
                        entityToSpawn.setYRot((float) Mth.nextDouble(RandomSource.create(), 0, 360));
                        entityToSpawn.setYBodyRot((float) Mth.nextDouble(RandomSource.create(), 0, 360));
                        entityToSpawn.setYHeadRot((float) Mth.nextDouble(RandomSource.create(), 0, 360));
                        entityToSpawn.setDeltaMovement((Mth.nextDouble(RandomSource.create(), -0.2, 0.2)), 0, (Mth.nextDouble(RandomSource.create(), -0.2, 0.2)));
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
        data.add(new AnimationController<>(this, "movement", 1, this::movementPredicate));
        data.add(new AnimationController<>(this, "attacking", 1, this::attackingPredicate));
        data.add(new AnimationController<>(this, "procedure", 1, this::procedurePredicate));
    }

    private boolean isRockSpiderDurative() {
        return this.isAlive() && this.getEntityData().get(DATA_DURATION) <= 0;
    }

    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}