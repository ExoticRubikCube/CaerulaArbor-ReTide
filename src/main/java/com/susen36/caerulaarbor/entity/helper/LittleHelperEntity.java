package com.susen36.caerulaarbor.entity.helper;

import com.susen36.caerulaarbor.entity.base.SyncedAnimationEntity;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CASounds;
import com.susen36.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class LittleHelperEntity extends PathfinderMob implements GeoEntity, SyncedAnimationEntity {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(LittleHelperEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(LittleHelperEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_DURABILITY = SynchedEntityData.defineId(LittleHelperEntity.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";

    public LittleHelperEntity(Level world) {
        this(CAEntities.LITTLE_HELPER.get(), world);
    }

    public LittleHelperEntity(EntityType<? extends LittleHelperEntity> type, Level world) {
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
        builder.define(DATA_DURABILITY, 4);
    }

    @Nullable
    protected SoundEvent getCustomDeathSound() {
        return SoundEvents.ARMOR_STAND_BREAK;
    }

    protected InteractionResult handleApocalypseInteract(Player sourceentity) {
        return InteractionResult.PASS;
    }

    protected ItemStack getRecycleItemStack() {
        return new ItemStack(CAItems.ITEM_HELPER.get());
    }

    protected void playBreakSound(ServerLevel serverLevel) {
    }

    public void handlePassengerLeftClick(Player passenger) {
        this.playPassengerLeftClickSound(passenger);
        WorldUtils.clearNetherseaAround(this.level(), this.getX(), this.getY() - 1, this.getZ(), this);
    }

    protected void playPassengerLeftClickSound(Player passenger) {
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(CASounds.CLEAN_BOT_MOVE.get(), 0.15f, 1);
    }

    @Override
    public SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ARMOR_STAND_HIT;
    }

    @Override
    public SoundEvent getDeathSound() {
        SoundEvent deathSound = this.getCustomDeathSound();
        return deathSound == null ? super.getDeathSound() : deathSound;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.IN_FIRE))
            return false;
        if (source.getDirectEntity() instanceof ThrownPotion || source.getDirectEntity() instanceof AreaEffectCloud)
            return false;
        if (source.is(DamageTypes.FALL))
            return false;
        if (source.is(DamageTypes.CACTUS))
            return false;
        if (source.is(DamageTypes.DROWN))
            return false;
        if (source.is(DamageTypes.LIGHTNING_BOLT))
            return false;
        if (source.is(DamageTypes.EXPLOSION))
            return false;
        if (source.is(DamageTypes.TRIDENT))
            return false;
        if (source.is(DamageTypes.FALLING_ANVIL))
            return false;
        if (source.is(DamageTypes.DRAGON_BREATH))
            return false;
        if (source.is(DamageTypes.WITHER))
            return false;
        if (source.is(DamageTypes.WITHER_SKULL))
            return false;
        int durability = this.entityData.get(DATA_DURABILITY);
        if (durability > 0) {
            this.entityData.set(DATA_DURABILITY, durability - 1);
            return false;
        }
        if (durability >= 0 && this.isAlive()) {
            if (!this.level().isClientSide()) {
                this.discard();
            }
            if (this.level() instanceof ServerLevel serverLevel) {
                ItemEntity itemEntity = new ItemEntity(serverLevel, this.getX(), this.getY(), this.getZ(), this.getRecycleItemStack());
                itemEntity.setPickUpDelay(10);
                itemEntity.setUnlimitedLifetime();
                serverLevel.addFreshEntity(itemEntity);
                this.playBreakSound(serverLevel);
            }
        }
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Durability", this.entityData.get(DATA_DURABILITY));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Durability")) {
            this.entityData.set(DATA_DURABILITY, compound.getInt("Durability"));
        }
    }

    @Override
    public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
        super.mobInteract(sourceentity, hand);
        sourceentity.startRiding(this);
        InteractionResult apocalypseResult = this.handleApocalypseInteract(sourceentity);
        if (apocalypseResult != InteractionResult.PASS) {
            return apocalypseResult;
        }
        if (sourceentity.isShiftKeyDown() && sourceentity.getMainHandItem().getItem() == Blocks.AIR.asItem() && sourceentity.getOffhandItem().getItem() == Blocks.AIR.asItem()) {
            if (!this.level().isClientSide()) {
                this.discard();
            }
            if (this.level() instanceof ServerLevel serverLevel) {
                ItemEntity itemEntity = new ItemEntity(serverLevel, this.getX(), this.getY(), this.getZ(), this.getRecycleItemStack());
                itemEntity.setPickUpDelay(5);
                itemEntity.setUnlimitedLifetime();
                serverLevel.addFreshEntity(itemEntity);
            }
            return InteractionResult.FAIL;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (this.tickCount % 30 == 5 && this.entityData.get(DATA_DURABILITY) < 4) {
            this.entityData.set(DATA_DURABILITY, this.entityData.get(DATA_DURABILITY) + 1);
        }
        WorldUtils.clearNetherseaAround(this.level(), this.getX(), this.getY(), this.getZ(), this);
        this.refreshDimensions();
    }

    @Override
    public void travel(Vec3 dir) {
        Entity entity = this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
        if (this.isVehicle() && entity != null) {
            this.setYRot(entity.getYRot());
            this.yRotO = this.getYRot();
            this.setXRot(entity.getXRot() * 0.5F);
            this.setRot(this.getYRot(), this.getXRot());
            this.yBodyRot = entity.getYRot();
            this.yHeadRot = entity.getYRot();
            if (entity instanceof LivingEntity passenger) {
                this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
                float forward = passenger.zza;
                float strafe = passenger.xxa;
                super.travel(new Vec3(strafe, 0, forward));
            }
            double d1 = this.getX() - this.xo;
            double d0 = this.getZ() - this.zo;
            float f1 = (float) Math.sqrt(d1 * d1 + d0 * d0) * 4;
            if (f1 > 1.0F)
                f1 = 1.0F;
            this.walkAnimation.setSpeed(this.walkAnimation.speed() + (f1 - this.walkAnimation.speed()) * 0.4F);
            this.walkAnimation.position(this.walkAnimation.position() + this.walkAnimation.speed());
            this.calculateEntityAnimation(true);
            return;
        }
        super.travel(dir);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.updateSwingTime();
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.25);
        builder = builder.add(NeoForgeMod.SWIM_SPEED, 2.5);
        builder = builder.add(Attributes.MAX_HEALTH, 799);
        builder = builder.add(Attributes.ARMOR, 5);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 256);
        builder = builder.add(Attributes.FOLLOW_RANGE, 32);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if (this.isVehicle() && event.isMoving()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.little_helper.move"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.little_helper.idle"));
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

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 5) {
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
        data.add(new AnimationController<>(this, "movement", 3, this::movementPredicate));
        data.add(new AnimationController<>(this, "procedure", 3, this::procedurePredicate));
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