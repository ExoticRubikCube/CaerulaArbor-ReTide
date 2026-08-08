package com.susen36.caerulaarbor.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import javax.annotation.Nullable;

public class OceanizeRabbitEntity extends SeaMonster {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(OceanizeRabbitEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(OceanizeRabbitEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(OceanizeRabbitEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_SWALLOW_P = SynchedEntityData.defineId(OceanizeRabbitEntity.class, EntityDataSerializers.INT);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";

    public OceanizeRabbitEntity(Level world) {
        this(CAEntities.OCEANIZE_RABBIT.get(), world);
    }

    public OceanizeRabbitEntity(EntityType<OceanizeRabbitEntity> type, Level world) {
        super(type, world);
        xpReward = 1;
        setNoAi(false);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.4f);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHOOT, false);
        builder.define(DATA_ANIMATION, "undefined");
        builder.define(DATA_VARIANT, 0);
        builder.define(DATA_SWALLOW_P, 40);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.5, false));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(3, new TemptGoal(this, 1, Ingredient.of(Items.CARROT), false));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(6, new FloatGoal(this));
    }

    @Override
    public SoundEvent getAmbientSound() {
        return SoundEvents.RABBIT_AMBIENT;
    }

    @Override
    public void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.RABBIT_JUMP, 0.15f, 1);
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return SoundEvents.RABBIT_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.RABBIT_DEATH;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.FALL))
            return false;
        return super.hurt(source, amount);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
        double variant;
        double rrr;
        double curVar;
        curVar = this.getEntityData().get(DATA_VARIANT);
        if (curVar == 0) {
            rrr = Math.random();
            if (rrr < 0.35) {
                variant = 0;
            } else if (rrr < 0.7) {
                variant = 2;
            } else if (rrr < 0.82) {
                variant = 1;
            } else if (rrr < 0.94) {
                variant = 4;
            } else {
                variant = 3;
            }
            if (variant != 0) {
                if ((Entity) this instanceof OceanizeRabbitEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_VARIANT, (int) variant);
                if (variant != 2) {
                    if (this.getAttributes().hasAttribute(Attributes.MAX_HEALTH))
                        this.getAttribute(Attributes.MAX_HEALTH)
                                .setBaseValue(((this.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? this.getAttribute(Attributes.MAX_HEALTH).getBaseValue() : 0) * 1.25));
                    this.setHealth(this.getMaxHealth());
                }
                if (variant == 3) {
                    if (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
                        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(
                                ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : 0) * 1.25));
                }
            }
        }
        return super.finalizeSpawn(world, difficulty, reason, livingdata);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Variant", this.entityData.get(DATA_VARIANT));
        compound.putInt("SwallowP", this.entityData.get(DATA_SWALLOW_P));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Variant")) {
            this.entityData.set(DATA_VARIANT, compound.getInt("Variant"));
        }
        if (compound.contains("SwallowP")) {
            this.entityData.set(DATA_SWALLOW_P, compound.getInt("SwallowP"));
        }
    }

    @Override
    public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
        super.mobInteract(sourceentity, hand);
        Entity entity = this;
        if ((entity instanceof OceanizeRabbitEntity datEntI ? datEntI.getEntityData().get(DATA_VARIANT) : 0) > 4.5) {
            return InteractionResult.PASS;
        }
        if (sourceentity.isHolding(CAItems.APOCALYPSE.get())) {
            if (entity instanceof OceanizeRabbitEntity datEntSetI)
                datEntSetI.getEntityData().set(DATA_VARIANT, 5);
            LivingEntity livingEntity = (LivingEntity) entity;
            if (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((livingEntity.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : 0) * 10);
            if (livingEntity.getAttributes().hasAttribute(Attributes.MAX_HEALTH))
                livingEntity.getAttribute(Attributes.MAX_HEALTH).setBaseValue((this.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? this.getAttribute(Attributes.MAX_HEALTH).getBaseValue() : 0) * 10);
            if (livingEntity.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE))
                livingEntity.getAttribute(CAAttributes.GENERAL_DEFENSE).setBaseValue(32.5);
            if (livingEntity.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE))
                livingEntity.getAttribute(CAAttributes.MAGIC_RESISTANCE).setBaseValue(79.9);
            if (livingEntity.getAttributes().hasAttribute(Attributes.KNOCKBACK_RESISTANCE))
                livingEntity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
            if (!this.level().isClientSide()) {
                //TODO 99999应该怎么替代
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 99999, 2));
                this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 99999, 2));
                this.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 99999, 0));
                this.addEffect(new MobEffectInstance(CAMobEffects.SANITY_IMMUE, 99999, 0));
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 99999, 1));
            }
            livingEntity.setHealth(livingEntity.getMaxHealth());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void baseTick() {
        super.baseTick();
        Entity target;
        double sklp1;
        if (!(((Entity) this instanceof OceanizeRabbitEntity datEntI ? datEntI.getEntityData().get(DATA_VARIANT) : 0) < 4.5)) {
            if (this.isAlive()) {
                sklp1 = (Entity) this instanceof OceanizeRabbitEntity datEntI ? datEntI.getEntityData().get(DATA_SWALLOW_P) : 0;
                target = this.getTarget();
                if (sklp1 > 0) {
                    if ((Entity) this instanceof OceanizeRabbitEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_SWALLOW_P, (int) (sklp1 - 1));
                } else {
                    if (!(target == null) && target.isAlive() && distanceTo(target) <= 5) {
                        if (this instanceof OceanizeRabbitEntity) {
                            this.setAnimation("animation.oceanized_rabbit.swallow");
                        }
                        if ((Entity) this instanceof OceanizeRabbitEntity datEntSetI)
                            datEntSetI.getEntityData().set(DATA_SWALLOW_P, 200);
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 15, 9, false, false));
                        CaerulaArbor.queueServerWork(8, () -> {
                            if (this.isAlive()) {
                                if (this.getTarget() != null) {
                                    if (this.getTarget().isAlive()) {
                                        this.getTarget().hurt(this.damageSources().outOfBorder(),
                                                (float) ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 7.99));
                                    }
                                }
                            }
                        });
                    }
                }
            }
        }
        this.refreshDimensions();
    }


    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.25);
        builder = builder.add(Attributes.MAX_HEALTH, 8);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 5);
        builder = builder.add(Attributes.FOLLOW_RANGE, 18);
        builder = builder.add(Attributes.ATTACK_KNOCKBACK, 0.1);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

            ) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_rabbit.move"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_rabbit.idle"));
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
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_rabbit.attack"));
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
            LevelAccessor world = this.level();
            double vvv;
            ItemStack coral = ItemStack.EMPTY;
            if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                vvv = (Entity) this instanceof OceanizeRabbitEntity datEntI ? datEntI.getEntityData().get(DATA_VARIANT) : 0;
                if (vvv == 0) {
                    coral = new ItemStack(Blocks.BRAIN_CORAL_FAN).copy();
                } else if (vvv == 1) {
                    coral = new ItemStack(Blocks.BUBBLE_CORAL_FAN).copy();
                } else if (vvv == 2) {
                    coral = new ItemStack(Blocks.HORN_CORAL_FAN).copy();
                } else if (vvv == 3) {
                    coral = new ItemStack(Blocks.FIRE_CORAL_FAN).copy();
                } else if (vvv == 4) {
                    coral = new ItemStack(Blocks.TUBE_CORAL_FAN).copy();
                } else if (vvv == 5) {
                    coral = new ItemStack(CAItems.BLOODY_RECORD.get()).copy();
                }
                if (world instanceof ServerLevel level) {
                    ItemEntity entityToSpawn = new ItemEntity(level, this.getX(), this.getY(), this.getZ(), coral);
                    entityToSpawn.setPickUpDelay(10);
                    level.addFreshEntity(entityToSpawn);
                }
            }
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


    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}