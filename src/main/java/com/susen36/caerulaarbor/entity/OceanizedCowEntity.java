package com.susen36.caerulaarbor.entity;

import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.init.CABlocks;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.util.EntityUtils;
import com.susen36.caerulaarbor.util.WorldUtils;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import software.bernie.geckolib.animation.*;

public class OceanizedCowEntity extends SeaMonster {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(OceanizedCowEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(OceanizedCowEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Boolean> DATA_SKILL = SynchedEntityData.defineId(OceanizedCowEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> DATA_MILK_COOLDOWN = SynchedEntityData.defineId(OceanizedCowEntity.class, EntityDataSerializers.INT);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";

    public OceanizedCowEntity(Level world) {
        this(CAEntities.OCEANIZED_COW.get(), world);
    }

    public OceanizedCowEntity(EntityType<OceanizedCowEntity> type, Level world) {
        super(type, world);
        xpReward = 3;
        setNoAi(false);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1f);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHOOT, false);
        builder.define(DATA_ANIMATION, "undefined");
        builder.define(DATA_SKILL, true);
        builder.define(DATA_MILK_COOLDOWN, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true, false));
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
        this.goalSelector.addGoal(15, new RandomStrollGoal(this, 1));
        this.goalSelector.addGoal(16, new FloatGoal(this));
        this.goalSelector.addGoal(17, new RandomLookAroundGoal(this));
    }

    @Override
    public SoundEvent getAmbientSound() {
        return SoundEvents.COW_AMBIENT;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return SoundEvents.COW_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.COW_DEATH;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Skill", this.entityData.get(DATA_SKILL));
        compound.putInt("MilkCooldown", this.entityData.get(DATA_MILK_COOLDOWN));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Skill")) {
            this.entityData.set(DATA_SKILL, compound.getBoolean("Skill"));
        }
        if (compound.contains("MilkCooldown")) {
            this.entityData.set(DATA_MILK_COOLDOWN, compound.getInt("MilkCooldown"));
        }
    }

    @Override
    public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
        super.mobInteract(sourceentity, hand);
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity entity = this;
        Level world = this.level();
        if (sourceentity.isHolding(Items.SHEARS) && entity instanceof OceanizedCowEntity datEntL1 && datEntL1.getEntityData().get(DATA_SKILL)) {
            if (entity instanceof OceanizedCowEntity datEntSetL)
                datEntSetL.getEntityData().set(DATA_SKILL, false);
            if (world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.MOOSHROOM_SHEAR, SoundSource.PLAYERS, 1, 1);
            }
            for (int index0 = 0; index0 < Mth.nextInt(RandomSource.create(), 3, 5); index0++) {
                if (world instanceof ServerLevel level) {
                    ItemEntity entityToSpawn = new ItemEntity(level, x, (y + 0.65), z, new ItemStack(CABlocks.TRAIL_MUSHROOM.get()));
                    entityToSpawn.setPickUpDelay(10);
                    level.addFreshEntity(entityToSpawn);
                }
            }
            return InteractionResult.SUCCESS;
        }
        if (sourceentity.getMainHandItem().getItem() == Items.BUCKET) {
            int cooldown = this.entityData.get(DATA_MILK_COOLDOWN);
            if (cooldown <= 0) {
                ItemStack bucket = sourceentity.getMainHandItem();
                bucket.shrink(1);
                ItemStack milk = new ItemStack(CAItems.NETHERSEA_MILK.get());
                if (sourceentity.getInventory().add(milk)) {
                    // success
                } else {
                    sourceentity.drop(milk, false);
                }
                this.entityData.set(DATA_MILK_COOLDOWN, Mth.nextInt(RandomSource.create(), 1200, 2400));
                world.playSound(null, BlockPos.containing(x, y, z), SoundEvents.COW_MILK, SoundSource.PLAYERS, 1, 1);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void baseTick() {
        super.baseTick();
        LevelAccessor world = this.level();
        if (!this.hasEffect(CAMobEffects.MUTE) && (Entity) this instanceof OceanizedCowEntity datEntL1 && datEntL1.getEntityData().get(DATA_SKILL) && WorldUtils.canGrief(world)) {
            if (!this.level().isClientSide() && !datEntL1.hasEffect(CAMobEffects.COW_BUFF)) {
                this.addEffect(new MobEffectInstance(CAMobEffects.COW_BUFF, 20, 0, false, false));
            }
        }
        int milkCooldown = this.entityData.get(DATA_MILK_COOLDOWN);
        if (milkCooldown > 0) {
            this.entityData.set(DATA_MILK_COOLDOWN, milkCooldown - 1);
        }
        this.refreshDimensions();
    }


    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.18);
        builder = builder.add(Attributes.MAX_HEALTH, 19);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 4);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.2);
        builder = builder.add(Attributes.ATTACK_KNOCKBACK, 0.5);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_cow.move"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_cow.idle"));
        }
        return PlayState.STOP;
    }

    private PlayState attackingPredicate(AnimationState event) {
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
            this.swinging = true;
            this.lastSwing = level().getGameTime();
        }
        if (this.swinging && this.lastSwing + 15L <= level().getGameTime()) {
            this.swinging = false;
        }
        if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_cow.attack"));
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
        if (this.deathTime == 20) {
            this.remove(RemovalReason.KILLED);
            this.dropExperience(this.getKillCredit());
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


    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}