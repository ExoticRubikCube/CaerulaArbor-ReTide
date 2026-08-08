package com.susen36.caerulaarbor.entity.crawler;

import com.susen36.babel.api.entity.ElementalAttacker;
import com.susen36.babel.elemental.base.AbstractEPCapability;
import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.init.CADamageTypes;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CASounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MoveBackToVillageGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import java.util.Collection;
import java.util.List;

public abstract class AbstractPocketSeaCrawlerEntity extends SeaMonster implements ElementalAttacker {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(AbstractPocketSeaCrawlerEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(AbstractPocketSeaCrawlerEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Float> DATA_DEAL = SynchedEntityData.defineId(AbstractPocketSeaCrawlerEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Boolean> DATA_CHARGED = SynchedEntityData.defineId(AbstractPocketSeaCrawlerEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> DATA_SWELL_DIR = SynchedEntityData.defineId(AbstractPocketSeaCrawlerEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_SWELL = SynchedEntityData.defineId(AbstractPocketSeaCrawlerEntity.class, EntityDataSerializers.INT);
    public static int maxSwell = 30;
    public String animationprocedure = "empty";

    protected AbstractPocketSeaCrawlerEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        xpReward = 8;
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.2f);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHOOT, false);
        builder.define(DATA_ANIMATION, "undefined");
        builder.define(DATA_DEAL, 0.0F);
        builder.define(DATA_CHARGED, false);
        builder.define(DATA_SWELL_DIR, -1);
        builder.define(DATA_SWELL, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Cat.class, false, false));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1));
        this.goalSelector.addGoal(5, new MoveBackToVillageGoal(this, 0.6, false));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    public boolean charged() {
        return this.entityData.get(DATA_CHARGED);
    }

    public void setCharged() {
        this.entityData.set(DATA_CHARGED, true);
    }

    public int getSwellDir() {
        return this.entityData.get(DATA_SWELL_DIR);
    }

    public void setSwellDir(int dir) {
        this.entityData.set(DATA_SWELL_DIR, dir);
    }

    public float getSwelling(float partialTick) {
        return this.entityData.get(DATA_SWELL) / (float) maxSwell;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity()== this || (source.is(DamageTypes.EXPLOSION) || source.is(CADamageTypes.OCEAN_MAGIC)))
            return false;
        if (this.charged() && (source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE)))
            return false;
        return super.hurt(source, amount);
    }

    protected void explode(boolean damagesSelf) {
        if (!this.level().isClientSide) {

            ServerLevel serverLevel = (ServerLevel) this.level();
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();

            serverLevel.sendParticles(ParticleTypes.EXPLOSION, x, y, z, 1, 0, 0, 0, 0.5);
            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 1, 0, 0, 0, 0.5);
            serverLevel.playSound(null, BlockPos.containing(this.position()), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 3.0F, 1.0F);

            float attackDamage = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)
                    ? (float) (this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 3.5F * (this.charged() ? 2.0D : 1.0D))
                    : 0.0F;

            Vec3 centerPos = new Vec3(x, y, z);
            List<LivingEntity> nearbyEntities = serverLevel.getEntitiesOfClass(
                    LivingEntity.class,
                    new AABB(centerPos, centerPos).inflate(3.0D),
                    entity -> entity != this && !entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring")))
            );

            for (LivingEntity entity : nearbyEntities) {
                entity.hurt(CADamageTypes.source(serverLevel, CADamageTypes.OCEAN_MAGIC, this), attackDamage);
            }

            this.spawnLingeringCloud();

            if (damagesSelf) {
                float selfDamage = this.getMaxHealth() * 0.3F;
                if (this.getHealth() <= selfDamage) {
                    this.dead = true;
                    this.triggerOnDeathMobEffects(Entity.RemovalReason.KILLED);
                    this.discard();
                } else {
                    this.setHealth(this.getHealth() - selfDamage);
                }
            }
        }
    }

    private void spawnLingeringCloud() {
        Collection<MobEffectInstance> collection = this.getActiveEffects();
        if (!collection.isEmpty()) {
            AreaEffectCloud areaeffectcloud = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
            areaeffectcloud.setRadius(2.0F);
            areaeffectcloud.setRadiusOnUse(-0.5F);
            areaeffectcloud.setWaitTime(10);
            areaeffectcloud.setDuration(areaeffectcloud.getDuration() / 2);
            areaeffectcloud.setRadiusPerTick(-areaeffectcloud.getRadius() / (float)areaeffectcloud.getDuration());

            for(MobEffectInstance mobeffectinstance : collection) {
                areaeffectcloud.addEffect(new MobEffectInstance(mobeffectinstance));
            }

            this.level().addFreshEntity(areaeffectcloud);
        }

    }

    @Override
    public AbstractEPCapability.EPType getElementalType() {
        return AbstractEPCapability.EPType.NERVOUS;
    }

    @Override
    public double getElementalRate() {
        return 1.25D;
    }

    @Override
    public double getElementalInjuryDamage() {
        return 25;
    }

    @Override
    public SoundEvent getAmbientSound() {
        return SoundEvents.TROPICAL_FISH_AMBIENT;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return SoundEvents.CREEPER_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return CASounds.SEABORN_DEATH.get();
    }


    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("Deal", this.entityData.get(DATA_DEAL));
        compound.putBoolean("Charged", this.entityData.get(DATA_CHARGED));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Deal")) {
            this.entityData.set(DATA_DEAL, compound.getFloat("Deal"));
        }
        if (compound.contains("Charged")) {
            this.entityData.set(DATA_CHARGED, compound.getBoolean("Charged"));
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        this.spawnAtLocation(new ItemStack(CAItems.OCEAN_CRYSTAL.get()));
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) && !this.isVehicle() && !this.isAggressive() && !this.isSprinting()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.pocket_sea_creeper.move"));
            }
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.pocket_sea_creeper.die"));
            }
            if (this.isShiftKeyDown()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.pocket_sea_creeper.move"));
            }
            if (this.isSprinting()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.pocket_sea_creeper.move"));
            }
            if (this.isVehicle() && event.isMoving()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.pocket_sea_creeper.move"));
            }
            if (this.isAggressive() && event.isMoving() && !this.isVehicle()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.pocket_sea_creeper.move"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.pocket_sea_creeper.idle"));
        }
        return PlayState.STOP;
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

    public void thunderHit(ServerLevel serverWorld, LightningBolt lightningBolt) {
        super.thunderHit(serverWorld, lightningBolt);
        this.setCharged();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
        data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
    }

    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}
