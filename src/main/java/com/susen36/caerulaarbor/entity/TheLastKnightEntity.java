package com.susen36.caerulaarbor.entity;

import com.susen36.babel.init.BabelAttributes;
import com.susen36.babel.init.BabelMobEffects;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.api.anim.SyncedAnimationEntity;
import com.susen36.caerulaarbor.init.*;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class TheLastKnightEntity extends PathfinderMob implements GeoEntity, SyncedAnimationEntity {
    protected static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(TheLastKnightEntity.class, EntityDataSerializers.STRING);
    protected static final EntityDataAccessor<Integer> DATA_PHASE = SynchedEntityData.defineId(TheLastKnightEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> DATA_IS_EVOLVING = SynchedEntityData.defineId(TheLastKnightEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Integer> DATA_EVOLVE_TIME = SynchedEntityData.defineId(TheLastKnightEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(TheLastKnightEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> DATA_SKILL_COOLDOWN = SynchedEntityData.defineId(TheLastKnightEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> DATA_ADDITION = SynchedEntityData.defineId(TheLastKnightEntity.class, EntityDataSerializers.INT);
    protected static final TagKey<EntityType<?>> IS_HUMANSIDE = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "is_humanside"));

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected boolean swinging;
    protected long lastSwing;
    public String animationprocedure = "empty";
    protected String prevAnim = "empty";
    protected ServerBossEvent bossInfo;

    public TheLastKnightEntity(Level world) {
        this(CAEntities.THE_LAST_KNIGHT.get(), world);
    }

    public TheLastKnightEntity(EntityType<? extends TheLastKnightEntity> entityType, Level level) {
        super(entityType, level);
        this.bossInfo = new ServerBossEvent(this.getTypeName(), ServerBossEvent.BossBarColor.WHITE, ServerBossEvent.BossBarOverlay.PROGRESS);
        this.setNoAi(false);
        this.setPersistenceRequired();
        this.xpReward = 64;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ANIMATION, "undefined");
        builder.define(DATA_PHASE, 0);
        builder.define(DATA_IS_EVOLVING, false);
        builder.define(DATA_EVOLVE_TIME, 0);
        builder.define(DATA_DURATION, 0);
        builder.define(DATA_SKILL_COOLDOWN, 200);
        builder.define(DATA_ADDITION, 0);
    }

    @Override
    protected Component getTypeName() {
        if (this.getPhase() == 1) {
            return Component.translatable("entity.caerula_arbor.last_knight_and_horse");
        }
        return Component.translatable("entity.caerula_arbor.the_last_knight");
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && (TheLastKnightEntity.this.isPhaseZeroStarting() || TheLastKnightEntity.this.isPhaseOneStarting());
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && (TheLastKnightEntity.this.isPhaseZeroStarting() || TheLastKnightEntity.this.isPhaseOneStarting());
            }
        });
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false) {
            @Override
            public boolean canUse() {
                return super.canUse() && TheLastKnightEntity.this.isPhaseZeroStarting();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && TheLastKnightEntity.this.isPhaseZeroStarting();
            }
        });
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.33D, false) {
            @Override
            public boolean canUse() {
                return super.canUse() && TheLastKnightEntity.this.isPhaseOneStarting();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && TheLastKnightEntity.this.isPhaseOneStarting();
            }
        });
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Monster.class, true, false) {
            @Override
            public boolean canUse() {
                return super.canUse() && (TheLastKnightEntity.this.isPhaseZeroStarting() || TheLastKnightEntity.this.isPhaseOneStarting());
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && (TheLastKnightEntity.this.isPhaseZeroStarting() || TheLastKnightEntity.this.isPhaseOneStarting());
            }
        });
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0D) {
            @Override
            public boolean canUse() {
                return super.canUse() && (TheLastKnightEntity.this.isPhaseZeroStarting() || TheLastKnightEntity.this.isPhaseOneStarting());
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && (TheLastKnightEntity.this.isPhaseZeroStarting() || TheLastKnightEntity.this.isPhaseOneStarting());
            }
        });
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && (TheLastKnightEntity.this.isPhaseZeroStarting() || TheLastKnightEntity.this.isPhaseOneStarting());
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && (TheLastKnightEntity.this.isPhaseZeroStarting() || TheLastKnightEntity.this.isPhaseOneStarting());
            }
        });
        this.goalSelector.addGoal(6, new FloatGoal(this));
    }

    @Override
    public SoundEvent getHurtSound(DamageSource source) {
        return CASounds.LAST_KNIGHT_HIT.get();
    }

    @Override
    public SoundEvent getDeathSound() {
        return CASounds.LAST_KNIGHT_HIT.get();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Entity sourceEntity = source.getEntity();
        if (sourceEntity instanceof LivingEntity sourceliving) {
            int frozenDuration = this.getHealth() < this.getMaxHealth() * 0.5F ? 80 : 40;
            double frozenTicks = sourceEntity.getTicksFrozen();
            if (frozenTicks < 140) {
                sourceEntity.setTicksFrozen((int) Math.min(frozenTicks + frozenDuration, 200));
            } else {
                if (!sourceliving.hasEffect(CAMobEffects.FROZEN)) {
                    this.level().playSound(null, BlockPos.containing(sourceEntity.getX(), sourceEntity.getY(), sourceEntity.getZ()),
                            CASounds.LAST_JNIGHT_FREEZE.get(), SoundSource.HOSTILE,
                            4, (float) Mth.nextDouble(RandomSource.create(), 1, 1.15));
                }
                if (!sourceliving.level().isClientSide()) {
                    sourceliving.addEffect(new MobEffectInstance(CAMobEffects.FROZEN, frozenDuration, 0, false, false));
                }
            }
        }
        if (source.is(DamageTypes.IN_FIRE)) {
            return false;
        }
        if (source.getDirectEntity() instanceof ThrownPotion || source.getDirectEntity() instanceof AreaEffectCloud) {
            return false;
        }
        if (source.is(DamageTypes.CACTUS)) {
            return false;
        }
        if (source.is(DamageTypes.DROWN)) {
            return false;
        }
        if (this.getPhase() == 1 && source.is(DamageTypes.FALL)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        float attackDamage = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0.0F;
        if (this.getPhase() == 1) {
            int addition = this.getAddition();
            float multiplier = 1.0F + addition * 0.03F;
            float finalDamage = this.applyFrozenExecutionBonus(target, attackDamage) * multiplier;
            boolean hurt = target.hurt(this.damageSources().mobAttack(this), finalDamage);
            if (hurt) {
                this.setAddition(0);
            }
            return hurt;
        }
        return target.hurt(this.damageSources().mobAttack(this), this.applyFrozenExecutionBonus(target, attackDamage));
    }

    protected float applyFrozenExecutionBonus(Entity target, float baseDamage) {
        float damage = baseDamage;
        if (target.getTicksFrozen() >= 200) {
            damage *= 1.75F;
        }
        if (target.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "seaborn")))) {
            damage *= 1.5F;
        }
        return damage;
    }

    @Override
    public void heal(float healAmount) {
        if(this.getPhase() != 1){
            super.heal(healAmount);
        }
    }

    @Override
    public void setHealth(float pHealth) {
        if (pHealth <= 0.0F && this.canTransitionToNextPhase()) {
            super.setHealth(1.0F);
            if (this.bossInfo != null) {
                float maxHealth = this.getMaxHealth();
                this.bossInfo.setProgress(maxHealth <= 0.0F ? 0.0F : Mth.clamp(this.getHealth() / maxHealth, 0.0F, 1.0F));
            }
            this.startNextPhaseTransition();
            return;
        }
        super.setHealth(pHealth);
        if (this.bossInfo != null) {
            float maxHealth = this.getMaxHealth();
            this.bossInfo.setProgress(maxHealth <= 0.0F ? 0.0F : Mth.clamp(this.getHealth() / maxHealth, 0.0F, 1.0F));
        }
    }

    @Override
    public void die(DamageSource source) {
        if (this.canTransitionToNextPhase()) {
            this.startNextPhaseTransition();
            return;
        }
        super.die(source);
        if (this.getPhase() == 1) {
            Entity sourceentity = source.getEntity();
            if (sourceentity instanceof ServerPlayer player) {
                AdvancementHolder adv = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "kill_knight_and_horse"));
                if (adv != null) {
                    AdvancementProgress ap = player.getAdvancements().getOrStartProgress(adv);
                    if (!ap.isDone()) {
                        for (String criteria : ap.getRemainingCriteria()) {
                            player.getAdvancements().award(adv, criteria);
                        }
                    }
                }
            }
        }
    }

    public int getPhase() {
        return this.entityData.get(DATA_PHASE);
    }

    public void setPhase(int phase) {
        this.entityData.set(DATA_PHASE, Mth.clamp(phase, 0, 1));
        this.updatePhaseRuntimeProperties();
        this.refreshDimensions();
    }

    public int getEvolveTime() {
        return this.entityData.get(DATA_EVOLVE_TIME);
    }

    public void setEvolveTime(int evolveTime) {
        this.entityData.set(DATA_EVOLVE_TIME, evolveTime);
    }

    protected boolean isEvolving() {
        return this.getEvolveTime() > 0;
    }

    protected boolean canTransitionToNextPhase() {
        return this.getPhase() == 0 && !this.isEvolving();
    }

    public int getDuration() {
        return this.entityData.get(DATA_DURATION);
    }

    public void setDuration(int duration) {
        this.entityData.set(DATA_DURATION, duration);
    }

    public int getSkillCooldown() {
        return this.entityData.get(DATA_SKILL_COOLDOWN);
    }

    public void setSkillCooldown(int skillCooldown) {
        this.entityData.set(DATA_SKILL_COOLDOWN, skillCooldown);
    }

    public int getAddition() {
        return this.entityData.get(DATA_ADDITION);
    }

    public void setAddition(int addition) {
        this.entityData.set(DATA_ADDITION, addition);
    }

    protected boolean isPhaseZeroStarting() {
        return this.tickCount >= 45 && !this.isEvolving() && this.getDuration() <= 0;
    }

    protected boolean isPhaseOneStarting() {
        return this.tickCount >= 40 && !this.isEvolving() && this.getDuration() <= 0;
    }

    protected ServerBossEvent.BossBarOverlay getPhaseBossBarOverlay() {
        return this.getPhase() == 1 ? ServerBossEvent.BossBarOverlay.NOTCHED_10 : ServerBossEvent.BossBarOverlay.PROGRESS;
    }

    protected void updatePhaseRuntimeProperties() {
        this.updatePhaseAttributes();
        if (this.bossInfo != null) {
            this.bossInfo.setColor(this.getPhase() == 1 ? ServerBossEvent.BossBarColor.BLUE : ServerBossEvent.BossBarColor.WHITE);
            this.bossInfo.setOverlay(this.getPhaseBossBarOverlay());
            this.bossInfo.setName(this.getTypeName());
            float maxHealth = this.getMaxHealth();
            float progress = maxHealth <= 0.0F ? 0.0F : Mth.clamp(this.getHealth() / maxHealth, 0.0F, 1.0F);
            this.bossInfo.setProgress(progress);
        }
    }

    protected void updatePhaseAttributes() {
        if (this.getPhase() == 0) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.20D);
            this.getAttribute(NeoForgeMod.SWIM_SPEED).setBaseValue(8.0D);
        } else if (this.getPhase() == 1) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25D);
            this.getAttribute(NeoForgeMod.SWIM_SPEED).setBaseValue(12.0D);
            this.getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(0.5D);
        }
    }

    protected void startNextPhaseTransition() {
        if (this.getPhase() == 0) {
            this.setAnimation("animation.last_knight.die");
            this.setEvolveTime(100);
        }
    }

    @Override
    public void awardKillScore(Entity entity, int score, DamageSource damageSource) {
        super.awardKillScore(entity, score, damageSource);
        if (this.getPhase() == 1) {
            LevelAccessor world = this.level();
            if (this.getHealth() < this.getMaxHealth()) {
                this.setHealth((float) (this.getHealth() + this.getMaxHealth() * 0.03));
                if (world instanceof ServerLevel level) {
                    level.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getX(), this.getY() + 0.75, this.getZ(), 32, 1, 2, 1, 0.1);
                }
            }
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        if (this.getPhase() == 1 || !(this.getPhase() == 0)) {
            this.spawnAtLocation(new ItemStack(CAItems.KNIGHT_CORPSE.get()));
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata);
        this.setPhase(0);
        this.setDuration(45);
        this.setAnimation("animation.last_knight.start");
        if (!this.level().isClientSide()) {
            this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 45, 9, false, false));
        }
        return retval;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Phase", this.getPhase());
        compound.putBoolean("IsEvolving", this.entityData.get(DATA_IS_EVOLVING));
        compound.putInt("EvolveTime", this.getEvolveTime());
        compound.putInt("Duration", this.getDuration());
        compound.putInt("SkillCooldown", this.getSkillCooldown());
        compound.putInt("Addition", this.getAddition());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Phase")) {
            this.setPhase(compound.getInt("Phase"));
        }
        if (compound.contains("IsEvolving")) {
            this.entityData.set(DATA_IS_EVOLVING, compound.getBoolean("IsEvolving"));
        }
        if (compound.contains("EvolveTime")) {
            this.setEvolveTime(compound.getInt("EvolveTime"));
        }
        if (compound.contains("Duration")) {
            this.setDuration(compound.getInt("Duration"));
        }
        if (compound.contains("SkillCooldown")) {
            this.setSkillCooldown(compound.getInt("SkillCooldown"));
        } else if (compound.contains("Skillp")) {
            this.setSkillCooldown(compound.getInt("Skillp"));
        }
        if (compound.contains("Addition")) {
            this.setAddition(compound.getInt("Addition"));
        }
    }

    private void performCrossAttack() {
        Level level = this.level();
        Entity target = this.getTarget();
        float damage = (float) (this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.0);
        DamageSource source = CADamageTypes.source(level, CADamageTypes.LAST_KNIGHT_ATTACK, this);

        for (int i = 0; i < 96; ++i) {
            double d = -12.0 + i * 0.25;
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.ENCHANTED_HIT, this.getX() + d, this.getY(), this.getZ() + 2.0, 2, 0.0, 0.5, 0.0, 0.1);
                serverLevel.sendParticles(ParticleTypes.ENCHANTED_HIT, this.getX() + d, this.getY(), this.getZ() - 2.0, 2, 0.0, 0.5, 0.0, 0.1);
                serverLevel.sendParticles(ParticleTypes.ENCHANTED_HIT, this.getX() + 2.0, this.getY(), this.getZ() + d, 2, 0.0, 0.5, 0.0, 0.1);
                serverLevel.sendParticles(ParticleTypes.ENCHANTED_HIT, this.getX() - 2.0, this.getY(), this.getZ() + d, 2, 0.0, 0.5, 0.0, 0.1);
            }
        }
        level.playSound(null, this.blockPosition(), CASounds.LAST_KNIGHT_ATTACK.get(), SoundSource.HOSTILE, 5.0F, 1.0F);

        double cx = this.getX();
        double cy = this.getY();
        double cz = this.getZ();
        AABB axisX = new AABB(cx - 16.0, cy - 2.0, cz - 1.5, cx + 16.0, cy + 4.0, cz + 1.5);
        AABB axisZ = new AABB(cx - 1.5, cy - 2.0, cz - 16.0, cx + 1.5, cy + 4.0, cz + 16.0);
        Predicate<LivingEntity> filter = living -> !living.getType().is(IS_HUMANSIDE) || living == target;

        for (LivingEntity living : level.getEntitiesOfClass(LivingEntity.class, axisX, filter)) {
            living.hurt(source, this.applyFrozenExecutionBonus(living, damage));
        }
        for (LivingEntity living : level.getEntitiesOfClass(LivingEntity.class, axisZ, filter)) {
            living.hurt(source, this.applyFrozenExecutionBonus(living, damage));
        }
    }

    @Override
    public boolean isPushable() {
        if (this.getPhase() == 1) {
            return false;
        }
        return super.isPushable();
    }

    @Override
    protected void doPush(Entity entityIn) {
        if (this.getPhase() != 1) {
            super.doPush(entityIn);
        }
    }

    @Override
    protected void pushEntities() {
        if (this.getPhase() != 1) {
            super.pushEntities();
        }
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose pose) {
        EntityDimensions bb = super.getDefaultDimensions(pose);
        return this.getPhase() == 1
            ? EntityDimensions.scalable(bb.width() * 1.25F, bb.height() * 1.25F)
            : bb;
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
    public void aiStep() {
        super.aiStep();
        this.updateSwingTime();
    }

    protected PlayState phaseZeroMovementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if (event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.last_knight.move"));
            }
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.last_knight.die"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.last_knight.idle"));
        }
        return PlayState.STOP;
    }

    protected PlayState phaseZeroAttackingPredicate(AnimationState event) {
        if (this.getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
            this.swinging = true;
            this.lastSwing = this.level().getGameTime();
        }
        if (this.swinging && this.lastSwing + 19L <= this.level().getGameTime()) {
            this.swinging = false;
        }
        if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.last_knight.attack"));
        }
        return PlayState.CONTINUE;
    }

    protected PlayState phaseOneMovementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if (event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.last_knight_horse.move"));
            }
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.last_knight_horse.die"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.last_knight_horse.idle"));
        }
        return PlayState.STOP;
    }

    protected PlayState phaseOneAttackingPredicate(AnimationState event) {
        if (this.getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
            this.swinging = true;
            this.lastSwing = this.level().getGameTime();
        }
        if (this.swinging && this.lastSwing + 19L <= this.level().getGameTime()) {
            this.swinging = false;
        }
        if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.last_knight_horse.atack"));
        }
        return PlayState.CONTINUE;
    }

    protected PlayState procedurePredicate(AnimationState event) {
        if (!this.animationprocedure.equals("empty") && event.getController().getAnimationState() == AnimationController.State.STOPPED
                || (!this.animationprocedure.equals(this.prevAnim) && !this.animationprocedure.equals("empty"))) {
            if (!this.animationprocedure.equals(this.prevAnim)) {
                event.getController().forceAnimationReset();
            }
            event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
            if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
                this.animationprocedure = "empty";
                event.getController().forceAnimationReset();
            }
        } else if (this.animationprocedure.equals("empty")) {
            this.prevAnim = "empty";
            return PlayState.STOP;
        }
        this.prevAnim = this.animationprocedure;
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "movement", 0, event -> {
            if (this.getPhase() == 0) {
                return this.phaseZeroMovementPredicate(event);
            }
            return this.phaseOneMovementPredicate(event);
        }));
        data.add(new AnimationController<>(this, "attacking", 0, event -> {
            if (this.getPhase() == 0) {
                return this.phaseZeroAttackingPredicate(event);
            }
            return this.phaseOneAttackingPredicate(event);
        }));
        data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
    }

    public String getSyncedAnimation() {
        return this.entityData.get(DATA_ANIMATION);
    }

    public void setAnimation(String animation) {
        this.entityData.set(DATA_ANIMATION, animation);
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.20)
                .add(NeoForgeMod.SWIM_SPEED, 8)
                .add(CAAttributes.GENERAL_DEFENSE, 10)
                .add(CAAttributes.MAGIC_RESISTANCE, 60)
                .add(Attributes.MAX_HEALTH, 400.0)
                .add(Attributes.ARMOR, 24)
                .add(Attributes.ATTACK_DAMAGE, 12)
                .add(Attributes.FOLLOW_RANGE, 36)
                .add(Attributes.KNOCKBACK_RESISTANCE, 10)
                .add(BabelAttributes.MAX_ELEMENTAL_VALUE, 2000.0);
        builder = builder.add(Attributes.STEP_HEIGHT, 1.25F);
        return builder;
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (this.getPhase() == 0) {
            if (this.isAlive()) {
                double evolveTime = this.getEvolveTime();
                if (evolveTime > 0) {
                    this.entityData.set(DATA_IS_EVOLVING, true);
                    if (!this.hasEffect(CAMobEffects.INVULNERABLE)) {
                        this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 120, 1, false, false));
                    }
                    if (!this.level().isClientSide() && evolveTime == 1) {
                        this.setPhase(1);
                        this.setHealth(this.getMaxHealth() * 0.25F);
                        this.setAnimation("animation.last_knight_horse.start");
                        this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 40, 9, false, false));
                    }
                    this.setEvolveTime((int) (evolveTime - 1));
                } else {
                    this.entityData.set(DATA_IS_EVOLVING, false);
                }
                if (!this.isEvolving()) {
                    double duration = this.getDuration();
                    if (duration > 0) {
                        this.setDuration((int) (duration - 1));
                    }
                    Entity target = this.getTarget();
                    double skillp = this.getSkillCooldown();
                    if (skillp > 0) {
                        this.setSkillCooldown((int) (skillp - 1));
                    } else {
                        if (target != null && target.isAlive() && this.distanceTo(target) < 4) {
                            this.setDuration(90);
                            this.setSkillCooldown(390);
                            if (!this.level().isClientSide()) {
                                this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 30, 0, false, false));
                            }
                            this.setAnimation("animation.last_knight.skill");
                            CaerulaArbor.queueServerWork(27, () -> {
                                if (this.isAlive()) {
                                    this.performCrossAttack();
                                }
                            });
                            CaerulaArbor.queueServerWork(45, () -> {
                                if (this.isAlive()) {
                                    this.performCrossAttack();
                                }
                            });
                            CaerulaArbor.queueServerWork(57, () -> {
                                if (this.isAlive()) {
                                    this.performCrossAttack();
                                }
                            });
                        }
                    }
                }
            }
        } else if (this.getPhase() == 1) {
            if (this.isAlive()) {
                LevelAccessor world = this.level();
                double x = this.getX();
                double y = this.getY();
                double z = this.getZ();
                if (this.tickCount % 10 == 0) {
                    if (!this.level().isClientSide()) {
                        this.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20, 0, false, false));
                    }
                    if (this.getHealth() > this.getMaxHealth() * 0.5) {
                        final Vec3 center = new Vec3(x, y, z);
                        List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(24), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                        for (Entity entityiterator : entfound) {
                            if (entityiterator.getTicksFrozen() >= 125 && entityiterator.isAlive()) {
                                entityiterator.setTicksFrozen(200);
                                if (entityiterator instanceof LivingEntity living && !this.level().isClientSide()) {
                                    living.addEffect(new MobEffectInstance(CAMobEffects.FROZEN, 20, 0, false, false));
                                }
                            }
                        }
                    }
                    Entity target = this.getTarget();
                    if (target != null && target.isAlive()) {
                        int add = this.getAddition();
                        if (add < 20) {
                            this.setAddition(add + 1);
                        }
                    }
                }
                if (EntityUtils.getSpeed(this) > this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 1.25) {
                    this.setDeltaMovement(Vec3.ZERO);
                }
                this.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
                this.removeEffect(BabelMobEffects.STUN);
                if (!this.isEvolving()) {
                    double duration = this.getDuration();
                    if (duration > 0) {
                        this.setDuration((int) (duration - 1));
                    }
                    Entity target = this.getTarget();
                    double skillp = this.getSkillCooldown();
                    if (skillp > 0) {
                        this.setSkillCooldown((int) (skillp - 1));
                    } else {
                        if (target != null && target.isAlive() && this.distanceTo(target) < 4) {
                            this.setDuration(40);
                            this.setSkillCooldown(240);
                            if (!this.level().isClientSide()) {
                                this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 32, 0, false, false));
                            }
                            this.setAnimation("animation.last_knight_horse.skill");
                            CaerulaArbor.queueServerWork(13, () -> {
                                double damage = this.getAttributeValue(Attributes.ATTACK_DAMAGE);
                                Entity enemy1 = this.getTarget();
                                final Vec3 center = new Vec3(x + 2 * this.getLookAngle().x, y, z + 2 * this.getLookAngle().z);
                                List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(4), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                                for (Entity entityiterator : entfound) {
                                    if (!(entityiterator instanceof LivingEntity)) {
                                        continue;
                                    }
                                    if (entityiterator == this) {
                                        continue;
                                    }
                                    if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "is_humanside"))) && !(entityiterator == enemy1)) {
                                        continue;
                                    }
                                    if (this.distanceTo(entityiterator) <= 4) {
                                        entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.LAST_KNIGHT_ATTACK, this), (float) (damage * 1.5));
                                        entityiterator.push(0, 0.64, 0);
                                        ItemStack useItem1 = entityiterator instanceof LivingEntity entUseItem12 ? entUseItem12.getUseItem() : ItemStack.EMPTY;
                                        if (useItem1.getItem() instanceof ShieldItem) {
                                            if (entityiterator instanceof Player player) {
                                                player.getCooldowns().addCooldown(useItem1.getItem(), 100);
                                            }
                                            if (world instanceof Level level) {
                                                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.SHIELD_BREAK, SoundSource.HOSTILE, 1, 1);
                                            }
                                        }
                                        CaerulaArbor.queueServerWork(7, () -> {
                                            entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.LAST_KNIGHT_ATTACK, this), (float) (this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2));
                                            if (entityiterator instanceof LivingEntity livingVictim && !this.level().isClientSide()) {
                                                livingVictim.addEffect(new MobEffectInstance(BabelMobEffects.LESS_ARMOR, 150, 0, false, false));
                                            }
                                            entityiterator.push(0, -1, 0);
                                        });
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }
        setTicksFrozen(0);
        this.removeEffect(CAMobEffects.FROZEN);
        this.updatePhaseRuntimeProperties();
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        int threshold = this.getPhase() == 1 ? 40 : 35;
        if (!this.level().isClientSide() && this.deathTime == threshold && !this.isRemoved()) {
            this.remove(RemovalReason.KILLED);
            this.dropExperience(this.getKillCredit());
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}