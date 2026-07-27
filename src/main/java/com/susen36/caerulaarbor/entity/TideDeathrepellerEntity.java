package com.susen36.caerulaarbor.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CADamageTypes;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.util.EntityUtils;
import com.susen36.caerulaarbor.util.WorldUtils;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
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
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import java.util.Comparator;
import java.util.List;

public class TideDeathrepellerEntity extends SeaMonster {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(TideDeathrepellerEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(TideDeathrepellerEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_SKILLP = SynchedEntityData.defineId(TideDeathrepellerEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(TideDeathrepellerEntity.class, EntityDataSerializers.INT);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";
    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.BLUE, ServerBossEvent.BossBarOverlay.PROGRESS);

    public TideDeathrepellerEntity(Level world) {
        this(CAEntities.TIDE_DEATHREPELLER.get(), world);
    }

    public TideDeathrepellerEntity(EntityType<TideDeathrepellerEntity> type, Level world) {
        super(type, world);
        xpReward = 6;
        setNoAi(false);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.5f);
        setPersistenceRequired();
    }

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
                return super.canUse() && TideDeathrepellerEntity.this.isFaking();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && TideDeathrepellerEntity.this.isFaking();
            }
        });
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this,  0.8, true) {

            @Override
            public boolean canUse() {
                return super.canUse() && TideDeathrepellerEntity.this.isFaking();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && TideDeathrepellerEntity.this.isFaking();
            }

        });
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true, false) {
            @Override
            public boolean canUse() {
                return super.canUse() && TideDeathrepellerEntity.this.isFaking();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && TideDeathrepellerEntity.this.isFaking();
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
    public boolean hurt(DamageSource source, float amount) {
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity sourceentity = source.getEntity();
        if (sourceentity != null) {
            double num;
            if (this.isAlive() && !this.hasEffect(CAMobEffects.COOLDOWN_SINAL) && !((Entity) this instanceof LivingEntity livEnt2 && livEnt2.hasEffect(CAMobEffects.FAKE_DEATH))) {
                    if (distanceTo(sourceentity) <= 6) {
                        num = 0;
                        {
                            final Vec3 center = new Vec3(x, y, z);
                            List<LivingEntity> entfound = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(8), e -> true);
                            for (LivingEntity entityiterator : entfound) {
                                if (!(entityiterator == this) && entityiterator.getMaxHealth() >= 10) {
                                    num = num + 1;
                                }
                            }
                        }
                        if (num >= 2 || ((Entity) this instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) < ((Entity) this instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * 0.5) {
                            if (this instanceof TideDeathrepellerEntity) {
                                this.setAnimation("animation.deathrepeller.enchantattack");
                            }
                            if (!this.level().isClientSide())
                                this.addEffect(new MobEffectInstance(CAMobEffects.COOLDOWN_SINAL, 60, 0, false, false));
                            this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((sourceentity.getX()), (sourceentity.getY()), (sourceentity.getZ())));
                            CaerulaArborMod.queueServerWork(12, () -> {
                                if (world instanceof Level level) {
                                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 2, 1);
                                }
                                {
                                    final Vec3 center = new Vec3((x + 1.8 * getLookAngle().x), (y + 1.5), (z + 1.8 * getLookAngle().z));
                                    List<LivingEntity> entfound = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(5), e -> true);
                                    for (LivingEntity entityiterator : entfound) {
                                        if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring"))) && ((Entity) this instanceof Mob mobEnt ? (Entity) mobEnt.getTarget() : null) == entityiterator
                                                || !entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring"))) && (entityiterator instanceof Mob || entityiterator instanceof Player)) {
                                            entityiterator.hurt(
                                                    CADamageTypes.source(world, CADamageTypes.REPELLER_ATTACK, this), (float) ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0)
                                                            * 2.5));
                                            for (int index0 = 0; index0 < 2; index0++) {
                                                EntityUtils.giveLessArmor(entityiterator, 11);
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
            }
        }
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
            Entity bishop = this.level().getEntitiesOfClass(TideBishopEntity.class, AABB.ofSize(new Vec3(x, y, z), 128, 128, 128), candidate -> true).stream()
                    .min(Comparator.comparingDouble(candidate -> candidate.distanceToSqr(x, y, z))).orElse(null);
            boolean keepup = bishop != null;
            if (bishop instanceof LivingEntity bishopLiving && bishopLiving.hasEffect(CAMobEffects.FAKE_DEATH)) {
                keepup = false;
            }
            if (keepup) {
                super.setHealth(Math.max(this.getHealth(), 1.0F));
                this.setShiftKeyDown(true);
                if (!this.level().isClientSide()) {
                    this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 200, 0, false, false));
                    this.addEffect(new MobEffectInstance(CAMobEffects.FAKE_DEATH, 200, 1, false, false));
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
            nearest = this.level().getEntitiesOfClass(TideBishopEntity.class, AABB.ofSize(new Vec3(x, y, z), 128, 128, 128), candidate -> true).stream()
                    .min(Comparator.comparingDouble(candidate -> candidate.distanceToSqr(x, y, z))).orElse(null);
            boolean keepup = nearest != null;
            if (nearest instanceof LivingEntity nearestLiving && nearestLiving.hasEffect(CAMobEffects.FAKE_DEATH)) {
                keepup = false;
            }
            if (!keepup) {
                this.setAnimation("animation.deathrepeller.die");
                this.removeAllEffects();
                this.hurt(this.level().damageSources().fellOutOfWorld(), 114514);
            }
            return;
        }
        double skillCooldown = this.getEntityData().get(DATA_SKILLP);
        double skillDuration = this.getEntityData().get(DATA_DURATION);
        if (skillDuration > 0) {
            this.getEntityData().set(DATA_DURATION, (int) (skillDuration - 1));
        }
        if (skillCooldown <= 0) {
            double nearbyCount = 0;
            Vec3 center = new Vec3(x, y, z);
            List<LivingEntity> nearbyEntities = this.level().getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(8), candidate -> true);
            for (LivingEntity nearbyEntity : nearbyEntities) {
                if (nearbyEntity != this && nearbyEntity.getMaxHealth() >= 10) {
                    nearbyCount++;
                }
            }
            if (nearbyCount >= 2 || this.getHealth() < this.getMaxHealth() * 0.5) {
                Entity target = this.getTarget();
                if (target != null && this.distanceTo(target) <= 4) {
                    this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(target.getX(), target.getY(), target.getZ()));
                    this.setAnimation("empty");
                    if (!this.level().isClientSide()) {
                        this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 50, 0, false, false));
                    }
                    this.setAnimation("animation.deathrepeller.combo");
                    CaerulaArborMod.queueServerWork(17, () -> {
                        if (this.isAlive()) {
                            EntityUtils.repellerChop(this.level(), x, y, z, this, 2);
                        }
                    });
                    CaerulaArborMod.queueServerWork(23, () -> {
                        if (this.isAlive()) {
                            EntityUtils.repellerChop(this.level(), x, y, z, this, 2);
                        }
                    });
                    CaerulaArborMod.queueServerWork(35, () -> {
                        if (this.isAlive()) {
                            EntityUtils.repellerChop(this.level(), x, y, z, this, 2);
                        }
                    });
                    CaerulaArborMod.queueServerWork(42, () -> {
                        if (this.isAlive()) {
                            EntityUtils.repellerChop(this.level(), x, y, z, this, 3.5);
                        }
                    });
                    this.getEntityData().set(DATA_DURATION, 53);
                    this.getEntityData().set(DATA_SKILLP, 300);
                }
            }
        } else {
            this.getEntityData().set(DATA_SKILLP, (int) (skillCooldown - 1));
            if (MapVariables.get(this.level()).strategy_grow >= 3) {
                this.getEntityData().set(DATA_SKILLP, (int) (skillCooldown - 2));
            }
        }
        nearest = this.level().getEntitiesOfClass(TideBishopEntity.class, AABB.ofSize(new Vec3(x, y, z), 128, 128, 128), candidate -> true).stream()
                .min(Comparator.comparingDouble(candidate -> candidate.distanceToSqr(x, y, z))).orElse(null);
        if (nearest instanceof LivingEntity nearestLiving && nearestLiving.hasEffect(CAMobEffects.FAKE_DEATH)) {
            EntityUtils.spawnLinkParticles(this.level(), this, nearest);
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


    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder = builder.add(Attributes.MAX_HEALTH, 75);
        builder = builder.add(Attributes.ARMOR, 10);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 8);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.65);
        builder = builder.add(CAAttributes.MAX_SANITY, 2000);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

            ) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.deathrepeller.move"));
            }
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.deathrepeller.die"));
            }
            if (this.isShiftKeyDown()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.deathrepeller.die_loop"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.deathrepeller.idle"));
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
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.deathrepeller.attack"));
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
        if (this.getEntityData().get(TideDeathrepellerEntity.DATA_DURATION) > 0) {
            return false;
        }
        return !this.hasEffect(CAMobEffects.FAKE_DEATH);
    }

    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}