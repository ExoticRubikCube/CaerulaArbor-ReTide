package com.susen36.caerulaarbor.entity;

import com.susen36.babel.init.BabelAttributes;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.base.SeaMonsterBoss;
import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CABlocks;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

public class MegaChestEntity extends SeaMonsterBoss {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(MegaChestEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(MegaChestEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Boolean> DATA_RELEASED = SynchedEntityData.defineId(MegaChestEntity.class, EntityDataSerializers.BOOLEAN);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";

    public MegaChestEntity(Level world) {
        this(CAEntities.MEGA_CHEST.get(), world);
    }

    public MegaChestEntity(EntityType<MegaChestEntity> type, Level world) {
        super(type, world);
        this.bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.WHITE, ServerBossEvent.BossBarOverlay.NOTCHED_10);
        xpReward = 32;
        setNoAi(false);
        setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHOOT, false);
        builder.define(DATA_ANIMATION, "undefined");
        builder.define(DATA_RELEASED, false);
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        Entity entity = this;
        return EntityUtils.isAlive(entity);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 0.6, true));

        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.4) {
            @Override
            public boolean canUse() {
                return super.canContinueToUse() && !isShiftKeyDown();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !isShiftKeyDown();
            }
        });
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return super.canContinueToUse() && !isShiftKeyDown();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !isShiftKeyDown();
            }
        });
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ARMOR_STAND_HIT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.ARMOR_STAND_BREAK;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.DROWN))
            return false;
        boolean flag = super.hurt(source, amount);
        if (flag) this.handleChestStart(source.getEntity());
        return flag;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (source.getEntity() instanceof ServerPlayer player) {
            AdvancementHolder advancement = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "costly_treasures"));
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
            if (!progress.isDone()) {
                for (String criteria : progress.getRemainingCriteria())
                    player.getAdvancements().award(advancement, criteria);
            }
        }
    }

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Released", this.entityData.get(DATA_RELEASED));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Released")) {
            this.entityData.set(DATA_RELEASED, compound.getBoolean("Released"));
        }
	}

    @Override
    public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
        super.mobInteract(sourceentity, hand);
        return this.handleChestStart(sourceentity);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity target;
        if (!((Entity) this instanceof MegaChestEntity datEntL0 && datEntL0.getEntityData().get(DATA_RELEASED))) {
            setShiftKeyDown(true);
            if (!this.level().isClientSide())
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 8, false, false));
        }
        target = this.getTarget();
        if (target == null || !target.isAlive()) {
            if (!world.isClientSide() && this.getNoActionTime() >= 1200) {
                if (this.isAlive()) {
                    if ((world.getBlockState(BlockPos.containing(x, y, z))).canBeReplaced()) {
                        if (!level().isClientSide())
                            discard();
                        {
                            BlockPos bp = BlockPos.containing(x, y, z);
                            BlockState bs = CABlocks.CHESTMEGA_SPAWNER.get().withPropertiesOf(world.getBlockState(bp));
                            if (bs.getBlock().getStateDefinition().getProperty("facing") instanceof DirectionProperty directionProperty)
                                bs = bs.setValue(directionProperty, getDirection());
                            world.setBlock(bp, bs, 3);
                        }
                        if (world instanceof Level level) {
                            level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.ENDER_CHEST_CLOSE, SoundSource.BLOCKS, 1, 1);
                        }
                    }
                }
            }
        }
        this.refreshDimensions();
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose p_33597_) {
        return super.getDefaultDimensions(p_33597_).scale((float) 1.5);
    }

    @Override
    public boolean canUsePortal(boolean allowVehicles) {
        return false;
    }

    private InteractionResult handleChestStart(Entity sourceentity) {
        if (sourceentity == null)
            return InteractionResult.PASS;

        if (this.isShiftKeyDown()) {
            this.setAnimation("animation.chestmega.start");

            LevelAccessor world = this.level();
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();

            if (world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.ENDER_CHEST_OPEN, SoundSource.HOSTILE, 1, 1);
            }

            this.setShiftKeyDown(false);
            this.getEntityData().set(DATA_RELEASED, true);

            this.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);

            if (sourceentity instanceof LivingEntity ent)
                this.setTarget(ent);

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(CAAttributes.MAGIC_RESISTANCE, 40);
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.7);
        builder = builder.add(Attributes.MAX_HEALTH, 240);
        builder = builder.add(Attributes.ARMOR, 17);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 23);
        builder = builder.add(Attributes.FOLLOW_RANGE, 18);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 1);
        builder = builder.add(BabelAttributes.MAX_ELEMENTAL_VALUE, 2000);
        builder = builder.add(Attributes.STEP_HEIGHT, 1.2f);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.isDeadOrDying()) {
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.chestmega.die"));
        }
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

                    && this.entityData.get(DATA_RELEASED)) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.chestmega.move"));
            }
            if (this.isShiftKeyDown()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.chestmega.stay"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.chestmega.idle"));
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
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.chestmega.attack"));
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
        data.add(new AnimationController<>(this, "movement", 3, this::movementPredicate));
        data.add(new AnimationController<>(this, "attacking", 3, this::attackingPredicate));
        data.add(new AnimationController<>(this, "procedure", 3, this::procedurePredicate));
    }

    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}