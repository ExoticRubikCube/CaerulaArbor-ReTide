package com.susen36.caerulaarbor.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.base.SyncedAnimationEntity;
import com.susen36.caerulaarbor.entity.bullets.CarmenBulletEntity;
import com.susen36.caerulaarbor.init.*;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SaintCarmenEntity extends Animal implements GeoEntity, SyncedAnimationEntity {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(SaintCarmenEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(SaintCarmenEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_SKILL_P1 = SynchedEntityData.defineId(SaintCarmenEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_SKILL_P2 = SynchedEntityData.defineId(SaintCarmenEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_SHOOT_P = SynchedEntityData.defineId(SaintCarmenEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_BULLET = SynchedEntityData.defineId(SaintCarmenEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(SaintCarmenEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_RELOAD_P = SynchedEntityData.defineId(SaintCarmenEntity.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";

    public SaintCarmenEntity(Level world) {
        this(CAEntities.SAINT_CARMEN.get(), world);
    }

    public SaintCarmenEntity(EntityType<SaintCarmenEntity> type, Level world) {
        super(type, world);
        xpReward = 0;
        setNoAi(false);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1f);
        setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHOOT, false);
        builder.define(DATA_ANIMATION, "undefined");
        builder.define(DATA_SKILL_P1, 200);
        builder.define(DATA_SKILL_P2, 100);
        builder.define(DATA_SHOOT_P, 80);
        builder.define(DATA_BULLET, 3);
        builder.define(DATA_DURATION, 0);
        builder.define(DATA_RELOAD_P, 500);
    }


    @Override
    public EntityDimensions getDefaultDimensions(Pose pose) {
        return super.getDefaultDimensions(pose);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this,  1.3, false) {

            @Override
            public boolean canUse() {
                return super.canUse() && isCarmenDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isCarmenDurative();
            }

        });
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Monster.class, true, false));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1) {
            @Override
            public boolean canUse() {
                return super.canUse() && isCarmenDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isCarmenDurative();
            }
        });
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && isCarmenDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isCarmenDurative();
            }
        });
        this.goalSelector.addGoal(6, new FloatGoal(this));
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
    public boolean doHurtTarget(Entity target) {
        double targetX = target.getX();
        double targetY = target.getY();
        double targetZ = target.getZ();
        if (!this.level().isClientSide()) {
            CaerulaArbor.queueServerWork(9, () -> {
                if (this.isAlive() && target.isAlive() && this.distanceTo(target) <= 3) {
                    this.level().playSound(null, BlockPos.containing(targetX, targetY, targetZ),
                            CASounds.CARMEN_MELEE.get(), SoundSource.NEUTRAL, 2.33F,
                            (float) Mth.nextDouble(RandomSource.create(), 0.9, 1.1));
                    this.applyMuteOnHit(target);
                    target.hurt(
                            CADamageTypes.source(this.level(), CADamageTypes.GENERIC_WARRIOR_ATTACK, this), (float) (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0));
                }
            });
        }
        return true;
    }

    private void applyMuteOnHit(Entity target) {
        if (target instanceof LivingEntity livingTarget && !livingTarget.level().isClientSide()) {
            livingTarget.addEffect(new MobEffectInstance(CAMobEffects.MUTE, 100, 0));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("SkillP1", this.entityData.get(DATA_SKILL_P1));
        compound.putInt("SkillP2", this.entityData.get(DATA_SKILL_P2));
        compound.putInt("ShootP", this.entityData.get(DATA_SHOOT_P));
        compound.putInt("Bullet", this.entityData.get(DATA_BULLET));
        compound.putInt("Duration", this.entityData.get(DATA_DURATION));
        compound.putInt("ReloadP", this.entityData.get(DATA_RELOAD_P));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("SkillP1")) {
            this.entityData.set(DATA_SKILL_P1, compound.getInt("SkillP1"));
        }
        if (compound.contains("SkillP2")) {
            this.entityData.set(DATA_SKILL_P2, compound.getInt("SkillP2"));
        }
        if (compound.contains("ShootP")) {
            this.entityData.set(DATA_SHOOT_P, compound.getInt("ShootP"));
        }
        if (compound.contains("Bullet")) {
            this.entityData.set(DATA_BULLET, compound.getInt("Bullet"));
        }
        if (compound.contains("Duration")) {
            this.entityData.set(DATA_DURATION, compound.getInt("Duration"));
        }
        if (compound.contains("ReloadP")) {
            this.entityData.set(DATA_RELOAD_P, compound.getInt("ReloadP"));
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
        double sklp1;
        double dura;
        double sklp2;
        double shootCooldown;
        double bullet;
        double reloadP;
        boolean canShoot;
        if (this.isAlive()) {
            if (tickCount % 40 == 20) {
                IreneEntity.burnBrandAround(world, x, y, z);
            }
            sklp1 = (Entity) this instanceof SaintCarmenEntity datEntI ? datEntI.getEntityData().get(DATA_SKILL_P1) : 0;
            sklp2 = (Entity) this instanceof SaintCarmenEntity datEntI ? datEntI.getEntityData().get(DATA_SKILL_P2) : 0;
            shootCooldown = (Entity) this instanceof SaintCarmenEntity datEntI ? datEntI.getEntityData().get(DATA_SHOOT_P) : 0;
            bullet = (Entity) this instanceof SaintCarmenEntity datEntI ? datEntI.getEntityData().get(DATA_BULLET) : 0;
            reloadP = (Entity) this instanceof SaintCarmenEntity datEntI ? datEntI.getEntityData().get(DATA_RELOAD_P) : 0;
            dura = (Entity) this instanceof SaintCarmenEntity datEntI ? datEntI.getEntityData().get(DATA_DURATION) : 0;
            target = this.getTarget();
            if (dura > 0) {
                if ((Entity) this instanceof SaintCarmenEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_DURATION, (int) (dura - 1));
            }
            if (shootCooldown > 0) {
                if ((Entity) this instanceof SaintCarmenEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_SHOOT_P, (int) (shootCooldown - 1));
            }
            if (sklp1 > 0) {
                if ((Entity) this instanceof SaintCarmenEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_SKILL_P1, (int) (sklp1 - 1));
            } else if (dura <= 0) {
                if (!(target == null) && target.isAlive()) {
                    if (distanceTo(target) <= 24) {
                        if (this instanceof SaintCarmenEntity) {
                            this.setAnimation("animation.saint_carmen.melee_skill");
                        }
                        if ((Entity) this instanceof SaintCarmenEntity datEntSetI)
                            datEntSetI.getEntityData().set(DATA_SKILL_P1, 240);
                        if ((Entity) this instanceof SaintCarmenEntity datEntSetI)
                            datEntSetI.getEntityData().set(DATA_DURATION, 33);
                        dura = 33;
                        CaerulaArbor.queueServerWork(10, () -> {
                            if (this.isAlive()) {
                                carmenTeleport(world, x, y, z);
                            }
                        });
                        CaerulaArbor.queueServerWork(21, () -> {
                            if (this.isAlive()) {
                                shoot(world, x, y, z, 2);
                            }
                        });
                    }
                }
            }
            canShoot = bullet > 0 && shootCooldown <= 0;
            if (sklp2 > 0) {
                if ((Entity) this instanceof SaintCarmenEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_SKILL_P2, (int) (sklp2 - 1));
            } else if (dura <= 0 && canShoot) {
                if (!(target == null) && target.isAlive()) {
                    if (distanceTo(target) <= 24) {
                        if (this instanceof SaintCarmenEntity) {
                            this.setAnimation("animation.saint_carmen.gun_skill");
                        }
                        if ((Entity) this instanceof SaintCarmenEntity datEntSetI)
                            datEntSetI.getEntityData().set(DATA_SKILL_P2, 480);
                        if ((Entity) this instanceof SaintCarmenEntity datEntSetI)
                            datEntSetI.getEntityData().set(DATA_DURATION, 50);
                        if ((Entity) this instanceof SaintCarmenEntity datEntSetI)
                            datEntSetI.getEntityData().set(DATA_SHOOT_P, 50);
                        dura = 50;
                        push((getLookAngle().x * (-1.5)), 0, (getLookAngle().z * (-1.5)));
                        this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((target.getX()), (getY() + 1.8), (target.getZ())));
                        CaerulaArbor.queueServerWork(12, () -> {
                            if (this.isAlive()) {
                                shootAbundant(world, x, y, z, 1);
                            }
                        });
                        CaerulaArbor.queueServerWork(23, () -> {
                            if (this.isAlive()) {
                                shootAbundant(world, x, y, z, 2);
                            }
                        });
                        CaerulaArbor.queueServerWork(35, () -> {
                            if (this.isAlive()) {
                                shootAbundant(world, x, y, z, 3);
                            }
                        });
                    }
                }
            }
            if (canShoot) {
                if (dura <= 0) {
                    if (!(target == null) && target.isAlive()) {
                        if (distanceTo(target) <= 6) {
                            if (this instanceof SaintCarmenEntity) {
                                this.setAnimation("animation.saint_carmen.gun");
                            }
                            if ((Entity) this instanceof SaintCarmenEntity datEntSetI)
                                datEntSetI.getEntityData().set(DATA_DURATION, 20);
                            if ((Entity) this instanceof SaintCarmenEntity datEntSetI)
                                datEntSetI.getEntityData().set(DATA_SHOOT_P, 80);
                            if ((Entity) this instanceof SaintCarmenEntity datEntSetI)
                                datEntSetI.getEntityData().set(DATA_BULLET, (int) (bullet - 1));
                            this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((target.getX()), (getY() + 1.8), (target.getZ())));
                            CaerulaArbor.queueServerWork(9, () -> {
                                if (this.isAlive()) {
                                    shoot(world, x, y, z, 2);
                                }
                            });
                        }
                    }
                }
            } else {
                if (reloadP > 0) {
                    if ((Entity) this instanceof SaintCarmenEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_RELOAD_P, (int) (reloadP - 1));
                } else if (dura <= 0) {
                    if (this instanceof SaintCarmenEntity) {
                        this.setAnimation("animation.saint_carmen.reload");
                    }
                    if ((Entity) this instanceof SaintCarmenEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_BULLET, 3);
                    if ((Entity) this instanceof SaintCarmenEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_DURATION, 20);
                    if ((Entity) this instanceof SaintCarmenEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_RELOAD_P, 600);
                    if ((Entity) this instanceof SaintCarmenEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_SHOOT_P, 20);
                    if (world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), CASounds.IRENE_RELOAD.get(), SoundSource.NEUTRAL, 2, 1);
                    }
                }
            }
            showBullets(bullet);
        }
        this.refreshDimensions();
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageable) {
        SaintCarmenEntity retval = CAEntities.SAINT_CARMEN.get().create(serverWorld);
        retval.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(retval.blockPosition()), MobSpawnType.BREEDING, null);;
        return retval;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        stack.getItem();
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.updateSwingTime();
    }


    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.16);
        builder = builder.add(Attributes.MAX_HEALTH, 280);
        builder = builder.add(Attributes.ARMOR, 8);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 22);
        builder = builder.add(Attributes.FOLLOW_RANGE, 32);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 1);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

                    && !this.isAggressive()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.saint_carmen.move"));
            }
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.saint_carmen.die"));
            }
            if (this.isAggressive() && event.isMoving()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.saint_carmen.sprint"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.saint_carmen.idle"));
        }
        return PlayState.STOP;
    }

    private PlayState attackingPredicate(AnimationState event) {
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
            this.swinging = true;
            this.lastSwing = level().getGameTime();
        }
        if (this.swinging && this.lastSwing + 16L <= level().getGameTime()) {
            this.swinging = false;
        }
        if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.saint_carmen.melee"));
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
        data.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
        data.add(new AnimationController<>(this, "attacking", 0, this::attackingPredicate));
        data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    private void shootAbundant(LevelAccessor world, double x, double y, double z, double t) {
        double dama;
        double xx;
        double yy;
        double zz;
        Entity target;
        if (world instanceof Level level) {
            level.playSound(null, BlockPos.containing(x, y, z), CASounds.CARMEN_BIG_SHOOT.get(), SoundSource.NEUTRAL, 3, 1);
        }
        dama = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
        target = this.getTarget();
        if (!(target == null) && target.isAlive()) {
            xx = target.getX();
            yy = target.getY();
            zz = target.getZ();
            this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(xx, (yy + 1.8), zz));
        }
        for (int index0 = 0; index0 < (int) t; index0++) {
            for (int index1 = 0; index1 < 3; index1++) {
                {
                    Entity shootFrom = this;
                    Level projectileLevel = shootFrom.level();
                    if (!projectileLevel.isClientSide()) {
                        CarmenBulletEntity entityToSpawn = new CarmenBulletEntity(CAEntities.CARMEN_BULLET.get(), projectileLevel);
                        entityToSpawn.setOwner(this);
                        entityToSpawn.setBaseDamage((float) dama);
                        entityToSpawn.setSilent(true);
                        entityToSpawn.setPos(shootFrom.getX(), shootFrom.getEyeY() - 0.1, shootFrom.getZ());
                        entityToSpawn.shoot(shootFrom.getLookAngle().x, shootFrom.getLookAngle().y, shootFrom.getLookAngle().z, (float) 1.75, 15);
                        projectileLevel.addFreshEntity(entityToSpawn);
                    }
                }
            }
        }
    }

    private void shoot(LevelAccessor world, double x, double y, double z, double rate) {
        Entity target;
        double xx;
        double yy;
        double zz;
        double dama;
        target = this.getTarget();
        if (target == null) {
            return;
        }
        if (!target.isAlive()) {
            return;
        }
        if (this.distanceTo(target) > 8) {
            return;
        }
        xx = target.getX();
        yy = target.getY();
        zz = target.getZ();
        this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(xx, (yy + 1.8), zz));
        dama = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
        this.applyMuteOnHit(target);
        target.hurt(CADamageTypes.source(world, CADamageTypes.GENERIC_WARRIOR_ATTACK, this), (float) (dama * rate));
        if (world instanceof Level level) {
            level.playSound(null, BlockPos.containing(x, y, z), CASounds.CARMEN_SHOOT.get(), SoundSource.NEUTRAL, 3, 1);
        }
        if (world instanceof ServerLevel level)
            level.sendParticles(ParticleTypes.END_ROD, xx, (yy + 0.75), zz, 32, 0.75, 0.75, 0.75, 0.1);
    }

    private void showBullets(double bulletCount) {
        if (this.tickCount % 2 == 0) {
            for (int index = 0; index < (int) bulletCount; index++) {
                this.level().addParticle(CAParticles.BULLETS.get(), (this.getX() + 1), (this.getY() + 1.5 + index * 0.25), (this.getZ() + 1), 0, 0, 0);
            }
        }
    }

    private void carmenTeleport(LevelAccessor world, double x, double y, double z) {
        Entity target;
        double xx;
        double yy;
        double zz;
        double dama;
        target = this.getTarget();
        if (target == null) {
            return;
        }
        if (!target.isAlive()) {
            return;
        }
        if (this.distanceTo(target) > 24) {
            return;
        }
        xx = target.getX();
        yy = target.getY();
        zz = target.getZ();
        dama = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
        this.teleportTo((xx + Mth.nextDouble(RandomSource.create(), -0.5, 0.5)), yy, (zz + Mth.nextDouble(RandomSource.create(), -0.5, 0.5)));
        if (world instanceof Level level) {
            level.playSound(null, BlockPos.containing(x, y, z), CASounds.CARMEN_MELEE.get(), SoundSource.NEUTRAL, 3, 1);
        }
        this.applyMuteOnHit(target);
        target.hurt(CADamageTypes.source(world, CADamageTypes.GENERIC_WARRIOR_ATTACK, this), (float) (dama * 2));
    }


    private boolean isCarmenDurative() {
        return this.isAlive() && this.getEntityData().get(DATA_DURATION) <= 0;
    }

    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}