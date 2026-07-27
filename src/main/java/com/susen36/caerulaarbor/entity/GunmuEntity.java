package com.susen36.caerulaarbor.entity;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.sanity.SanityInjuryCapability;
import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CADamageTypes;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class GunmuEntity extends Monster {
    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.WHITE, ServerBossEvent.BossBarOverlay.PROGRESS);

    public GunmuEntity(Level world) {
        this(CAEntities.GUNMU.get(), world);
    }

    public GunmuEntity(EntityType<GunmuEntity> type, Level world) {
        super(type, world);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1f);
        xpReward = 0;
        setNoAi(false);
        setPersistenceRequired();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.5, false));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public double getMyRidingOffset() {
        return -0.35D;
    }

    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        this.spawnAtLocation(new ItemStack(CAItems.BANNED_ITEM.get()));
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
    public boolean hurt(DamageSource damagesource, float amount) {
        Level world = this.level();
        Entity sourceentity = damagesource.getEntity();
        Entity immediatesourceentity = damagesource.getDirectEntity();
        if (immediatesourceentity == null || sourceentity == null)
            return false;
        sourceentity.hurt(CADamageTypes.source(world, CADamageTypes.GUNMU_DAMAGE),
                sourceentity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1);
        if (!(sourceentity == immediatesourceentity)) {
            return false;
        }
        if (damagesource.is(DamageTypes.IN_FIRE))
            return false;
        if (damagesource.getDirectEntity() instanceof AbstractArrow)
            return false;
        if (damagesource.getDirectEntity() instanceof ThrownPotion || damagesource.getDirectEntity() instanceof AreaEffectCloud)
            return false;
        if (damagesource.is(DamageTypes.FALL))
            return false;
        if (damagesource.is(DamageTypes.CACTUS))
            return false;
        if (damagesource.is(DamageTypes.DROWN))
            return false;
        if (damagesource.is(DamageTypes.LIGHTNING_BOLT))
            return false;
        if (damagesource.is(DamageTypes.EXPLOSION) || damagesource.is(DamageTypes.PLAYER_EXPLOSION))
            return false;
        if (damagesource.is(DamageTypes.TRIDENT))
            return false;
        if (damagesource.is(DamageTypes.FALLING_ANVIL))
            return false;
        if (damagesource.is(DamageTypes.DRAGON_BREATH))
            return false;
        if (damagesource.is(DamageTypes.WITHER) || damagesource.is(DamageTypes.WITHER_SKULL))
            return false;
        return super.hurt(damagesource, amount);
    }

    @Override
    public boolean ignoreExplosion() {
        return true;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
        if (sourceentity.isHolding(CAItems.BANNED_ITEM.get())) {
            this.setRemoved(RemovalReason.CHANGED_DIMENSION);
            return InteractionResult.SUCCESS;
        } else if (sourceentity.isHolding(Items.STICK)) {
            this.setRemoved(RemovalReason.CHANGED_DIMENSION);
            Level level = this.level();
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();
            BlockPos pos = this.blockPosition();
            if (!level.isClientSide()) {
                level.playSound(this, pos, SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 2, 1);
                level.explode(this, x, y, z, 9, Level.ExplosionInteraction.MOB);
                if (level instanceof ServerLevel slvl) {
                    slvl.sendParticles(ParticleTypes.EXPLOSION, x, y, z, 4, 3, 3, 3, 1);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(sourceentity, hand);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (tickCount % 10 == 0) {
            clearFire();
            this.removeAllEffects();
            if (this.getAttributes().hasAttribute(CAAttributes.NUMB))
                this.getAttribute(CAAttributes.NUMB).setBaseValue(0);
            SanityInjuryCapability sanityInjury = ModCapabilities.getSanityInjury(this);
            sanityInjury.heal(sanityInjury.getMaxValue());
        }
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
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
    public void setHealth(float pHealth) {
        float hlth = this.getHealth();
        float reduction = hlth - pHealth;
        super.setHealth(reduction >= 1 ? hlth - 1 : pHealth);
    }

    public void remove(RemovalReason pReason) {
        if (this.level().getDifficulty() != Difficulty.PEACEFUL && this.isAlive() &&
                (pReason == RemovalReason.DISCARDED || pReason == RemovalReason.KILLED)) {
            return;
        }
        super.remove(pReason);
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.25);
        builder = builder.add(Attributes.MAX_HEALTH, 114);
        builder = builder.add(Attributes.ARMOR, 30);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 750);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
        builder = builder.add(CAAttributes.MAGIC_RESISTANCE, 100);
        builder = builder.add(CAAttributes.SANITY_MODIFIER, 0);
        return builder;
    }
}