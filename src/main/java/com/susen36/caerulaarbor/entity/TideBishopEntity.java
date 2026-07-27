package com.susen36.caerulaarbor.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.entity.bullets.TellerShotEntity;
import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.util.EntityUtils;
import com.susen36.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
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
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Objects;

public class TideBishopEntity extends SeaMonster implements RangedAttackMob {
    public static final EntityDataAccessor<Boolean> DATA_IS_SHOOTING = SynchedEntityData.defineId(TideBishopEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(TideBishopEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_SKILL_COOLDOWN = SynchedEntityData.defineId(TideBishopEntity.class, EntityDataSerializers.INT);
    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.GREEN, ServerBossEvent.BossBarOverlay.NOTCHED_6);
    public String animationprocedure = "empty";
    String prevAnim = "empty";
    private boolean swinging;
    private long lastSwing;

    public TideBishopEntity(Level world) {
        this(CAEntities.TIDE_BISHOP.get(), world);
    }

    public TideBishopEntity(EntityType<TideBishopEntity> type, Level world) {
        super(type, world);
        xpReward = 32;
        setNoAi(false);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(0.6f);
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder = builder.add(Attributes.MAX_HEALTH, 160);
        builder = builder.add(Attributes.ARMOR, 6);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 9);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
        builder = builder.add(CAAttributes.MAX_SANITY, 2000);
        return builder;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_IS_SHOOTING, false);
        builder.define(DATA_ANIMATION, "undefined");
        builder.define(DATA_SKILL_COOLDOWN, 160);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, IronGolem.class, true, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, SnowGolem.class, true, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Villager.class, true, false));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Illusioner.class, true, false));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Pillager.class, true, false));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, Vindicator.class, true, false));
        this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, Witch.class, true, false));
        this.targetSelector.addGoal(9, new NearestAttackableTargetGoal<>(this, Piglin.class, true, false));
        this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, PiglinBrute.class, true, false));
        this.targetSelector.addGoal(11, new NearestAttackableTargetGoal<>(this, ZombifiedPiglin.class, true, false));
        this.targetSelector.addGoal(12, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, target -> EntityUtils.isOceanizedPlayerNearby(this.level(), this.getX(), this.getY(), this.getZ())));
        this.goalSelector.addGoal(14, new RandomStrollGoal(this, 0.4));
        this.goalSelector.addGoal(15, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(16, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25, 60, 9f) {
            @Override
            public boolean canContinueToUse() {
                return this.canUse();
            }
        });
    }

    @Override
    public SoundEvent getAmbientSound() {
        return SoundEvents.EVOKER_AMBIENT;
    }

    @Override
    public @NotNull SoundEvent getHurtSound(@NotNull DamageSource ds) {
        return SoundEvents.EVOKER_HURT;
    }

    @Override
    public @NotNull SoundEvent getDeathSound() {
        return SoundEvents.EVOKER_DEATH;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        world.getEntitiesOfClass(TideDeathrepellerEntity.class, AABB.ofSize(new Vec3(x, y, z), 96, 96, 96), e -> true).stream().min(new Object() {
            Comparator<Entity> compareDistOf(double x, double y, double z) {
                return Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(x, y, z));
            }
        }.compareDistOf(x, y, z)).ifPresent(call -> call.getNavigation().moveTo(x, y, z, 0.8));
        if (source.is(DamageTypes.DROWN))
            return false;
        return super.hurt(source, amount);
    }

    @Override
    public void setHealth(float pHealth) {
        if (pHealth <= 0) {
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();
            Entity repeller = this.level().getEntitiesOfClass(TideDeathrepellerEntity.class, AABB.ofSize(new Vec3(x, y, z), 128, 128, 128), candidate -> true).stream()
                    .min(Comparator.comparingDouble(candidate -> candidate.distanceToSqr(x, y, z))).orElse(null);
            boolean keepup = repeller != null;
            if (repeller instanceof LivingEntity repellerLiving && repellerLiving.hasEffect(CAMobEffects.FAKE_DEATH)) {
                keepup = false;
            }
            if (keepup) {
                super.setHealth(Math.max(this.getHealth(), 1.0F));
                this.setShiftKeyDown(true);
                if (!this.level().isClientSide()) {
                    this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 400, 0, false, false));
                    this.addEffect(new MobEffectInstance(CAMobEffects.FAKE_DEATH, 400, 0, false, false));
                }
                return;
            }
        }
        super.setHealth(pHealth);
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor world, @NotNull DifficultyInstance difficulty, @NotNull MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
        SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata);
        if (world instanceof ServerLevel level) {
            Entity entityToSpawn = CAEntities.TIDE_DEATHREPELLER.get().spawn(level, BlockPos.containing(this.getX() + Mth.nextDouble(RandomSource.create(), -3, 3), this.getY(), this.getZ() + Mth.nextDouble(RandomSource.create(), -3, 3)), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn != null) {
                entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
            }
        }
        return retval;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("SkillCooldown", this.entityData.get(DATA_SKILL_COOLDOWN));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("SkillCooldown"))
            this.entityData.set(DATA_SKILL_COOLDOWN, compound.getInt("SkillCooldown"));
    }

    @Override
    public void baseTick() {
        super.baseTick();
        this.tickLinkedBehavior();
        this.refreshDimensions();
    }

    private void tickLinkedBehavior() {
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity nearest;
        if (this.hasEffect(CAMobEffects.FAKE_DEATH)) {
            nearest = this.level().getEntitiesOfClass(TideDeathrepellerEntity.class, AABB.ofSize(new Vec3(x, y, z), 128, 128, 128), candidate -> true).stream()
                    .min(Comparator.comparingDouble(candidate -> candidate.distanceToSqr(x, y, z))).orElse(null);
            boolean keepup = nearest != null;
            if (nearest instanceof LivingEntity nearestLiving && nearestLiving.hasEffect(CAMobEffects.FAKE_DEATH)) {
                keepup = false;
            }
            if (!keepup) {
                this.setAnimation("animation.tidebishop.die");
                this.removeAllEffects();
                this.hurt(this.level().damageSources().fellOutOfWorld(), 114514);
            }
            return;
        }
        double skillCooldown = this.getEntityData().get(DATA_SKILL_COOLDOWN);
        if (skillCooldown <= 0) {
            if (this.getTarget() != null) {
                this.setAnimation("animation.tidebishop.cast");
                if (!this.level().isClientSide()) {
                    this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 50, 0, false, false));
                }
                CaerulaArborMod.queueServerWork(33, () -> {
                    Entity repeller;
                    if (this.isAlive() && this.getHealth() < this.getMaxHealth()) {
                        Level projectileLevel = this.level();
                        if (!projectileLevel.isClientSide()) {
                            Projectile projectile = new Object() {
                                public Projectile getArrow(Level level, Entity shooter, float damage, int knockback, byte piercing) {
                                    AbstractArrow entityToSpawn = new TellerShotEntity(CAEntities.TELLER_SHOT.get(), level);
                                    entityToSpawn.setOwner(shooter);
                                    entityToSpawn.setBaseDamage(damage);
                                    entityToSpawn.setKnockback(knockback);
                                    entityToSpawn.setSilent(true);
                                    entityToSpawn.setPierceLevel(piercing);
                                    entityToSpawn.setCritArrow(true);
                                    return entityToSpawn;
                                }
                            }.getArrow(projectileLevel, this, (float) (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? Objects.requireNonNull(this.getAttribute(Attributes.ATTACK_DAMAGE)).getValue() : 0), 0, (byte) 1);
                            projectile.setPos(this.getX(), this.getEyeY() - 0.1, this.getZ());
                            projectile.shoot(this.getLookAngle().x, this.getLookAngle().y, this.getLookAngle().z, 1.5F, 0);
                            projectileLevel.addFreshEntity(projectile);
                        }
                        this.setHealth((float) (this.getHealth() + this.getMaxHealth() * 0.1));
                        if (this.level() instanceof ServerLevel level) {
                            level.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, y + 1.5, z, 64, 1.5, 1.5, 1.5, 0.2);
                        }
                    }
                    repeller = this.level().getEntitiesOfClass(TideDeathrepellerEntity.class, AABB.ofSize(new Vec3(x, y, z), 96, 96, 96), candidate -> true).stream()
                            .min(Comparator.comparingDouble(candidate -> candidate.distanceToSqr(x, y, z))).orElse(null);
                    if (repeller instanceof LivingEntity repellerLiving && repellerLiving.isAlive() && repellerLiving.getHealth() < repellerLiving.getMaxHealth()) {
                        repellerLiving.setHealth((float) (repellerLiving.getHealth() + repellerLiving.getMaxHealth() * 0.1));
                        if (this.level() instanceof ServerLevel level) {
                            level.sendParticles(ParticleTypes.HAPPY_VILLAGER, repellerLiving.getX(), repellerLiving.getY() + 1.5, repellerLiving.getZ(), 64, 1.5, 1.5, 1.5, 0.2);
                        }
                    }
                });
                this.getEntityData().set(DATA_SKILL_COOLDOWN, 200);
            }
        } else {
            this.getEntityData().set(DATA_SKILL_COOLDOWN, (int) (skillCooldown - 1));
        }
        nearest = this.level().getEntitiesOfClass(TideDeathrepellerEntity.class, AABB.ofSize(new Vec3(x, y, z), 128, 128, 128), candidate -> true).stream()
                .min(Comparator.comparingDouble(candidate -> candidate.distanceToSqr(x, y, z))).orElse(null);
        if (nearest instanceof LivingEntity nearestLiving && nearestLiving.hasEffect(CAMobEffects.FAKE_DEATH)) {
            EntityUtils.spawnLinkParticles(this.level(), this, nearest);
            if (MapVariables.get(this.level()).strategy_silence >= 3) {
                if (this.getAttributes().hasAttribute(CAAttributes.MISSRATE)) {
                    Objects.requireNonNull(this.getAttribute(CAAttributes.MISSRATE)).setBaseValue(30);
                }
            } else if (MapVariables.get(this.level()).strategy_subsisting >= 4) {
                if (this.getAttributes().hasAttribute(CAAttributes.MISSRATE)) {
                    Objects.requireNonNull(this.getAttribute(CAAttributes.MISSRATE)).setBaseValue(15);
                }
            }
        } else if (this.getAttributes().hasAttribute(CAAttributes.MISSRATE)) {
            Objects.requireNonNull(this.getAttribute(CAAttributes.MISSRATE)).setBaseValue(0);
        }
    }

    @Override
    public @NotNull EntityDimensions getDefaultDimensions(@NotNull Pose p_33597_) {
        return super.getDefaultDimensions(p_33597_).scale((float) 1.5);
    }

    @Override
    public void performRangedAttack(@NotNull LivingEntity target, float flval) {
        TellerShotEntity.shoot(this, target, (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttributeValue(Attributes.ATTACK_DAMAGE) : 0) * (4.0 / 9.0));
    }

    @Override
    public boolean canUsePortal(boolean allowVehicles) {
        return false;
    }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer player) {
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

                    && !this.isSprinting()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.tidebishop.move"));
            }
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.tidebishop.die"));
            }
            if (this.isShiftKeyDown()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.tidebishop.die_loop"));
            }
            if (this.isSprinting()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.tidebishop.die_move"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.tidebishop.idle"));
        }
        return PlayState.STOP;
    }

    private PlayState attackingPredicate(AnimationState event) {
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
            this.swinging = true;
            this.lastSwing = level().getGameTime();
        }
        if (this.swinging && this.lastSwing + 25L <= level().getGameTime()) {
            this.swinging = false;
        }
        if ((this.swinging || this.entityData.get(DATA_IS_SHOOTING)) && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.tidebishop.attack"));
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
        if (this.deathTime == 20) {
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
        data.add(new AnimationController<>(this, "movement", 2, this::movementPredicate));
        data.add(new AnimationController<>(this, "attacking", 2, this::attackingPredicate));
        data.add(new AnimationController<>(this, "procedure", 2, this::procedurePredicate));
    }

    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }

    public class RangedAttackGoal extends Goal {
        private final Mob mob;
        private final RangedAttackMob rangedAttackMob;
        private final double speedModifier;
        private final int attackIntervalMin;
        private final int attackIntervalMax;
        private final float attackRadius;
        private final float attackRadiusSqr;
        @Nullable
        private LivingEntity target;
        private int attackTime = -1;
        private int seeTime;

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
                return true;
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return this.canUse() || Objects.requireNonNull(this.target).isAlive() && !this.mob.getNavigation().isDone();
        }

        public void stop() {
            this.target = null;
            this.seeTime = 0;
            this.attackTime = -1;
            ((TideBishopEntity) rangedAttackMob).entityData.set(DATA_IS_SHOOTING, false);
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            double d0 = 0;
            if (this.target != null) {
                d0 = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
            }
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
                    ((TideBishopEntity) rangedAttackMob).entityData.set(DATA_IS_SHOOTING, false);
                    return;
                }
                ((TideBishopEntity) rangedAttackMob).entityData.set(DATA_IS_SHOOTING, true);
                float f = (float) Math.sqrt(d0) / this.attackRadius;
                float f1 = Mth.clamp(f, 0.1F, 1.0F);
                this.rangedAttackMob.performRangedAttack(this.target, f1);
                this.attackTime = Mth.floor(f * (float) (this.attackIntervalMax - this.attackIntervalMin) + (float) this.attackIntervalMin);
            } else if (this.attackTime < 0) {
                this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(d0) / (double) this.attackRadius, this.attackIntervalMin, this.attackIntervalMax));
            } else
                ((TideBishopEntity) rangedAttackMob).entityData.set(DATA_IS_SHOOTING, false);
        }
    }
}