package com.susen36.caerulaarbor.entity;

import com.susen36.babel.init.BabelAttributes;
import com.susen36.babel.util.EPUtils;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.init.CAEntities;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import javax.annotation.Nullable;

public class ScreamChestFishEntity extends SeaMonster {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(ScreamChestFishEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(ScreamChestFishEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Boolean> DATA_RELEASE = SynchedEntityData.defineId(ScreamChestFishEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> DATA_SCREAM_TICK = SynchedEntityData.defineId(ScreamChestFishEntity.class, EntityDataSerializers.INT);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";

    public ScreamChestFishEntity(Level world) {
        this(CAEntities.SCREAM_CHEST_FISH.get(), world);
    }

    public ScreamChestFishEntity(EntityType<ScreamChestFishEntity> type, Level world) {
        super(type, world);
        xpReward = 0;
        setNoAi(false);
        setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHOOT, false);
        builder.define(DATA_ANIMATION, "undefined");
        builder.define(DATA_RELEASE, false);
        builder.define(DATA_SCREAM_TICK, 201);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this,  1.2, true) {

            @Override
            public boolean canUse() {
                return super.canUse() && !isShiftKeyDown() && !ScreamChestFishEntity.this.isScreaming();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !isShiftKeyDown() && !ScreamChestFishEntity.this.isScreaming();
            }

        });
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1) {
            @Override
            public boolean canUse() {
                return super.canUse() && !isShiftKeyDown() && !ScreamChestFishEntity.this.isScreaming();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !isShiftKeyDown() && !ScreamChestFishEntity.this.isScreaming();
            }
        });
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                if (!super.canUse()) return false;
                return !isShiftKeyDown();
            }

            @Override
            public boolean canContinueToUse() {
                if (!super.canContinueToUse()) return false;
                return !isShiftKeyDown();
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
        if (source.is(DamageTypes.FALL))
            return false;
        if (source.is(DamageTypes.DROWN))
            return false;
        if (isScreaming())
            return super.hurt(source, amount * 0.5f);
        boolean flag = super.hurt(source, amount);
        if (flag)
            startScreamChest(source.getEntity());
        return flag;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }

    private InteractionResult startScreamChest(@Nullable Entity sourceEntity) {
        if (sourceEntity == null) {
            return InteractionResult.PASS;
        }
        if (this.isShiftKeyDown()) {
            this.setAnimation("animation.scream_chest_fish.open");
            this.level().playSound(null, BlockPos.containing(this.getX(), this.getY(), this.getZ()), SoundEvents.CHEST_OPEN, SoundSource.HOSTILE, 1, 1);
            this.setShiftKeyDown(false);
            this.getEntityData().set(DATA_RELEASE, true);
            this.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            if (sourceEntity instanceof LivingEntity livingEntity) {
                this.setTarget(livingEntity);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }


    @Override
    public void die(DamageSource source) {
        super.die(source);
        Entity sourceentity = source.getEntity();
        if (sourceentity == null)
            return;
        if (((Entity) this instanceof ScreamChestFishEntity datEntI ? datEntI.getEntityData().get(DATA_SCREAM_TICK) : 0) > 0) {
            if (sourceentity instanceof ServerPlayer player) {
                AdvancementHolder adv = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "i_scream"));
                AdvancementProgress ap = player.getAdvancements().getOrStartProgress(adv);
                if (!ap.isDone()) {
                    for (String criteria : ap.getRemainingCriteria())
                        player.getAdvancements().award(adv, criteria);
                }
            }
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
        this.setYRot((float) (90 * Mth.nextInt(RandomSource.create(), 0, 3)));
        this.setXRot(0);
        this.setYBodyRot(this.getYRot());
        this.setYHeadRot(this.getYRot());
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
        this.yBodyRotO = this.getYRot();
        this.yHeadRotO = this.getYRot();
        return super.finalizeSpawn(world, difficulty, reason, livingdata);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Release", this.entityData.get(DATA_RELEASE));
        compound.putInt("ScreamTick", this.entityData.get(DATA_SCREAM_TICK));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Release")) {
            this.entityData.set(DATA_RELEASE, compound.getBoolean("Release"));
        }
        if (compound.contains("ScreamTick")) {
            this.entityData.set(DATA_SCREAM_TICK, compound.getInt("ScreamTick"));
        }
    }

    @Override
    public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
        super.mobInteract(sourceentity, hand);
        return startScreamChest(sourceentity);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        double scream;
        double d;
        double angle;
        double t;
        if (!((Entity) this instanceof ScreamChestFishEntity datEntL0 && datEntL0.getEntityData().get(DATA_RELEASE))) {
            setShiftKeyDown(true);
            if (!this.level().isClientSide())
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 8, false, false));
        } else {
            if (this.isAlive()) {
                setShiftKeyDown(false);
                scream = (Entity) this instanceof ScreamChestFishEntity datEntI ? datEntI.getEntityData().get(DATA_SCREAM_TICK) : 0;
                if (scream > 0) {
                    if ((Entity) this instanceof ScreamChestFishEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_SCREAM_TICK, (int) (scream - 1));
                    t = tickCount;
                    if (scream % 10 == 0) {
                        if (world instanceof Level level) {
                            level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.HOSTILE, 1, 1);
                        }
                    }
                    for (int index0 = 0; index0 < 60; index0++) {
                        angle = Math.toRadians(index0 * 6 + t * 0.6);
                        d = (t * 0.5) % 5;
                        if (world instanceof ServerLevel level)
                            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, (x + d * Math.sin(angle)), (y + 0.25), (z + d * Math.cos(angle)), 2, 0.1, 0.1, 0.1, 0.1);
                    }
                    for (Entity entityiterator : world.getEntities(this, new AABB((x - 5), (y - 2), (z - 5), (x + 5), (y + 3), (z + 5)))) {
                        if ((entityiterator != null ? distanceTo(entityiterator) : -1) <= 5) {
                            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "seaborn")))) {
                                if (!(entityiterator == this.getTarget())) {
                                    continue;
                                }
                            }
                            if (!(entityiterator instanceof LivingEntity)) {
                                continue;
                            }
                            EPUtils.causeSanityInjury((LivingEntity) entityiterator, this, 0.25);
                            if (!this.level().isClientSide())
                                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 1));
                        }
                    }
                }
            }
        }
        this.refreshDimensions();
    }


    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.2);
        builder = builder.add(BabelAttributes.MAGIC_RESISTANCE, 25);
        builder = builder.add(Attributes.MAX_HEALTH, 40);
        builder = builder.add(Attributes.ARMOR, 10);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 5);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 1);
        builder = builder.add(Attributes.STEP_HEIGHT, 0.6f);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.isDeadOrDying()) {
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.scream_chest_fish.die"));
        }
        if (this.animationprocedure.equals("empty")) {
            if (this.isScreaming())
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.scream_chest_fish.scream"));
            if (this.isShiftKeyDown()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.scream_chest_fish.chest"));
            }
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.1F && event.getLimbSwingAmount() < 0.1F)) && this.entityData.get(DATA_RELEASE)) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.scream_chest_fish.move"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.scream_chest_fish.idle"));
        }
        return PlayState.STOP;
    }

    private PlayState attackingPredicate(AnimationState event) {
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
            this.swinging = true;
            this.lastSwing = level().getGameTime();
        }
        if (this.swinging && this.lastSwing + 12L <= level().getGameTime()) {
            this.swinging = false;
        }
        if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.scream_chest_fish.attack"));
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
        if (this.deathTime == 15) {
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
        data.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
        data.add(new AnimationController<>(this, "attacking", 0, this::attackingPredicate));
        data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
    }

    private boolean isScreaming() {
        if (!this.entityData.get(DATA_RELEASE)) return false;
        return this.entityData.get(DATA_SCREAM_TICK) > 0;
    }

    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}