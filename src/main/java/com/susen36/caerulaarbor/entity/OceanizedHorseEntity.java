package com.susen36.caerulaarbor.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.init.CABlocks;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.util.PlayerStateUtils;
import com.susen36.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import software.bernie.geckolib.animation.*;

public class OceanizedHorseEntity extends SeaMonster {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(OceanizedHorseEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(OceanizedHorseEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_LAY_LIMIT = SynchedEntityData.defineId(OceanizedHorseEntity.class, EntityDataSerializers.INT);
    private static final ResourceLocation OCEANIZED_HORSE_GUIDE_ARMOR_ID = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "oceanized_horse_guide_armor");
    private static final ResourceLocation OCEANIZED_HORSE_GUIDE_ARMOR_TOUGHNESS_ID = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "oceanized_horse_guide_armor_toughness");
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";

    public OceanizedHorseEntity(Level world) {
        this(CAEntities.OCEANIZED_HORSE.get(), world);
    }

    public OceanizedHorseEntity(EntityType<OceanizedHorseEntity> type, Level world) {
        super(type, world);
        xpReward = 6;
        setNoAi(false);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHOOT, false);
        builder.define(DATA_ANIMATION, "undefined");
        builder.define(DATA_LAY_LIMIT, 36);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1, true));
        this.goalSelector.addGoal(15, new RandomStrollGoal(this, 1));
        this.goalSelector.addGoal(16, new FloatGoal(this));
        this.goalSelector.addGoal(17, new RandomLookAroundGoal(this));
    }

    @Override
    public SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_HORSE_AMBIENT;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ZOMBIE_HORSE_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_HORSE_DEATH;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("LayLimit", this.entityData.get(DATA_LAY_LIMIT));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("LayLimit")) {
            this.entityData.set(DATA_LAY_LIMIT, compound.getInt("LayLimit"));
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        Level world = this.level();
        Entity rider;
        // 低血量且未被沉默、且允许篡改地形时：附加护甲并不断生成溟痕（耗尽 LAY_LIMIT 即停）
        boolean active = WorldUtils.canGrief(world) && this.getHealth() < this.getMaxHealth() * 0.5F && !this.hasEffect(CAMobEffects.MUTE);
        if (!this.level().isClientSide()) {
            AttributeInstance armorAttr = this.getAttribute(Attributes.ARMOR);
            if (active) {
                if (armorAttr.getModifier(OCEANIZED_HORSE_GUIDE_ARMOR_ID) == null) {
                    armorAttr.addTransientModifier(new AttributeModifier(OCEANIZED_HORSE_GUIDE_ARMOR_ID, 12, AttributeModifier.Operation.ADD_VALUE));
                }
                if (armorAttr.getModifier(OCEANIZED_HORSE_GUIDE_ARMOR_TOUGHNESS_ID) == null) {
                    armorAttr.addTransientModifier(new AttributeModifier(OCEANIZED_HORSE_GUIDE_ARMOR_TOUGHNESS_ID, 9, AttributeModifier.Operation.ADD_VALUE));
                }
                int layLimit = this.entityData.get(DATA_LAY_LIMIT);
                if (layLimit > 0) {
                    double x = this.getX();
                    double y = this.getY();
                    double z = this.getZ();
                    BlockPos pos = BlockPos.containing(x, y, z);
                    if (CABlocks.SEA_TRAIL_INIT.get().defaultBlockState().canSurvive(world, pos) && world.getBlockFloorHeight(pos) <= 0) {
                        PlayerStateUtils.replaceTrail(world, CABlocks.SEA_TRAIL_INIT.get().defaultBlockState(),
                                (world.getFluidState(pos).createLegacyBlock()).getBlock() == Blocks.WATER, x, y, z);
                        this.entityData.set(DATA_LAY_LIMIT, layLimit - 1);
                    }
                }
            } else {
                if (armorAttr.getModifier(OCEANIZED_HORSE_GUIDE_ARMOR_ID) != null) {
                    armorAttr.removeModifier(OCEANIZED_HORSE_GUIDE_ARMOR_ID);
                }
                if (armorAttr.getModifier(OCEANIZED_HORSE_GUIDE_ARMOR_TOUGHNESS_ID) != null) {
                    armorAttr.removeModifier(OCEANIZED_HORSE_GUIDE_ARMOR_TOUGHNESS_ID);
                }
            }
        }
        if ((tickCount - ((Entity) this instanceof LivingEntity livEnt ? livEnt.getLastHurtMobTimestamp() : 0)) % 10 == 0) {
            rider = getFirstPassenger();
            if (!(rider == null) && rider.isAlive()) {
                if (rider instanceof LivingEntity entity && !this.level().isClientSide())
                    this.addEffect(new MobEffectInstance(CAMobEffects.ADD_ATTACK_PERCLY, 40, 1, false, true));
            }
        }
        this.refreshDimensions();
    }


    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder = builder.add(Attributes.MAX_HEALTH, 62);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 8);
        builder = builder.add(Attributes.FOLLOW_RANGE, 24);
        builder = builder.add(Attributes.ATTACK_KNOCKBACK, 0.4);
        builder = builder.add(Attributes.STEP_HEIGHT, 1.3f);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) && !this.isVehicle() && !this.isAggressive() && !this.isSprinting()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanzied_horse.move"));
            }
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanzied_horse.die"));
            }
            if (this.isSprinting()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanzied_horse.sprint"));
            }
            if (this.isVehicle() && event.isMoving()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanzied_horse.sprint"));
            }
            if (this.isAggressive() && event.isMoving() && !this.isVehicle()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanzied_horse.sprint"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanzied_horse.idle"));
        }
        return PlayState.STOP;
    }

    private PlayState attackingPredicate(AnimationState event) {
        double d1 = this.getX() - this.xOld;
        double d0 = this.getZ() - this.zOld;
        float velocity = (float) Math.sqrt(d1 * d1 + d0 * d0);
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
            this.swinging = true;
            this.lastSwing = level().getGameTime();
        }
        if (this.swinging && this.lastSwing + 20L <= level().getGameTime()) {
            this.swinging = false;
        }
        if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanzied_horse.attack"));
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
        data.add(new AnimationController<>(this, "movement", 1, this::movementPredicate));
        data.add(new AnimationController<>(this, "attacking", 1, this::attackingPredicate));
        data.add(new AnimationController<>(this, "procedure", 1, this::procedurePredicate));
    }


    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}