package com.susen36.caerulaarbor.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.base.SyncedAnimationEntity;
import com.susen36.caerulaarbor.init.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
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
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.NeoForgeMod;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class TheLastKnightEntity extends Animal implements GeoEntity, SyncedAnimationEntity {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(TheLastKnightEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(TheLastKnightEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(TheLastKnightEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_SKILLP = SynchedEntityData.defineId(TheLastKnightEntity.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";
    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.WHITE, ServerBossEvent.BossBarOverlay.NOTCHED_6);

    public TheLastKnightEntity(Level world) {
        this(CAEntities.THE_LAST_KNIGHT.get(), world);
    }

    public TheLastKnightEntity(EntityType<TheLastKnightEntity> type, Level world) {
        super(type, world);
        xpReward = 64;
        setNoAi(false);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.25f);
        setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHOOT, false);
        builder.define(DATA_ANIMATION, "undefined");
        builder.define(DATA_DURATION, 0);
        builder.define(DATA_SKILLP, 200);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this,  1, false) {

            @Override
            public boolean canUse() {
                return super.canUse() && isLastKnightDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isLastKnightDurative();
            }

        });
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Monster.class, true, false) {
            @Override
            public boolean canUse() {
                return super.canUse() && isLastKnightDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isLastKnightDurative();
            }
        });
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1) {
            @Override
            public boolean canUse() {
                return super.canUse() && isLastKnightDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isLastKnightDurative();
            }
        });
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && isLastKnightDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isLastKnightDurative();
            }
        });
        this.goalSelector.addGoal(6, new FloatGoal(this));
    }

    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        this.spawnAtLocation(new ItemStack(CAItems.KNIGHT_CORPSE.get()));
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return CASounds.LAST_KNIGHT_HIT.get();
    }

    @Override
    public SoundEvent getDeathSound() {
        return CASounds.LAST_KNIGHT_HIT.get();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        this.applyLastKnightFreeze(source.getEntity());
        if (source.is(DamageTypes.IN_FIRE))
            return false;
        if (source.getDirectEntity() instanceof ThrownPotion || source.getDirectEntity() instanceof AreaEffectCloud)
            return false;
        if (source.is(DamageTypes.CACTUS))
            return false;
        if (source.is(DamageTypes.DROWN))
            return false;
        return super.hurt(source, amount);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        float attackDamage = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
        return target.hurt(this.damageSources().mobAttack(this),
                this.applyFrozenExecutionBonus(target, attackDamage));
    }

    public float applyFrozenExecutionBonus(Entity target, float baseDamage) {
        float damage = baseDamage;
        if (target.getTicksFrozen() >= 200) {
            damage *= 1.75F;
        }
        //TODO:涓轰粈涔堟病鏈塭lse?
        if (target.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring")))) {
            damage *= 1.5F;
        }
        return damage;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata);
        this.getEntityData().set(DATA_DURATION, 45);
        this.setAnimation("animation.last_knight.start");
        if (!this.level().isClientSide())
            this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 45, 9, false, false));
        return retval;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Duration", this.entityData.get(DATA_DURATION));
        compound.putInt("Skillp", this.entityData.get(DATA_SKILLP));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Duration")) {
            this.entityData.set(DATA_DURATION, compound.getInt("Duration"));
        }
        if (compound.contains("Skillp")) {
            this.entityData.set(DATA_SKILLP, compound.getInt("Skillp"));
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        Entity target;
        boolean shelled = false;
        double spawn = 0;
        double skillp;
        double duration;
        double idle = 0;
        if (this.isAlive()) {
            skillp = (Entity) this instanceof TheLastKnightEntity datEntI ? datEntI.getEntityData().get(DATA_SKILLP) : 0;
            duration = (Entity) this instanceof TheLastKnightEntity datEntI ? datEntI.getEntityData().get(DATA_DURATION) : 0;
            if (duration > 0) {
                if ((Entity) this instanceof TheLastKnightEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_DURATION, (int) (duration - 1));
            }
            setTicksFrozen(0);
            this.removeEffect(CAMobEffects.FROZEN);
            target = this.getTarget();
            if (skillp > 0) {
                if ((Entity) this instanceof TheLastKnightEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_SKILLP, (int) (skillp - 1));
            } else {
                if (!(target == null) && target.isAlive()) {
                    if (distanceTo(target) < 4) {
                        if ((Entity) this instanceof TheLastKnightEntity datEntSetI)
                            datEntSetI.getEntityData().set(DATA_DURATION, 90);
                        if ((Entity) this instanceof TheLastKnightEntity datEntSetI)
                            datEntSetI.getEntityData().set(DATA_SKILLP, 390);
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 30, 0, false, false));
                        if (this instanceof TheLastKnightEntity) {
                            this.setAnimation("animation.last_knight.skill");
                        }
                        CaerulaArborMod.queueServerWork(27, () -> {
                            if (this.isAlive()) {
                                this.performCrossAttack();
                            }
                        });
                        CaerulaArborMod.queueServerWork(45, () -> {
                            if (this.isAlive()) {
                                this.performCrossAttack();
                            }
                        });
                        CaerulaArborMod.queueServerWork(57, () -> {
                            if (this.isAlive()) {
                                this.performCrossAttack();
                            }
                        });
                    }
                }
            }
        }
        this.refreshDimensions();
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageable) {
        TheLastKnightEntity retval = CAEntities.THE_LAST_KNIGHT.get().create(serverWorld);
        retval.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(retval.blockPosition()), MobSpawnType.BREEDING, null);;
        return retval;
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


    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.2);
        builder = builder.add(NeoForgeMod.SWIM_SPEED, 8);
        builder = builder.add(CAAttributes.GENERAL_DEFENSE, 20);
        builder = builder.add(CAAttributes.MAGIC_RESISTANCE, 60);
        builder = builder.add(Attributes.MAX_HEALTH, 400);
        builder = builder.add(Attributes.ARMOR, 24);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 20);
        builder = builder.add(Attributes.FOLLOW_RANGE, 36);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

            ) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.last_knight.move"));
            }
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.last_knight.die"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.last_knight.idle"));
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
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.last_knight.attack"));
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
        if (this.deathTime == 35) {
            this.remove(RemovalReason.KILLED);
            this.dropExperience(this.getKillCredit());
            LevelAccessor world = this.level();
            if (world instanceof ServerLevel level) {
                Entity entityToSpawn = CAEntities.LAST_KNIGHT_AND_HORSE.get().spawn(level, BlockPos.containing(this.getX(), this.getY(), this.getZ()), MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.setYRot(getYRot());
                    entityToSpawn.setYBodyRot(getYRot());
                    entityToSpawn.setYHeadRot(getYRot());
                    entityToSpawn.setXRot(getXRot());
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

    public void applyLastKnightFreeze(Entity sourceEntity) {
        if (sourceEntity == null) {
            return;
        }
        int frozenDuration = this.getHealth() < this.getMaxHealth() * 0.5F ? 80 : 40;
        double frozenTicks = sourceEntity.getTicksFrozen();
        if (frozenTicks < 140) {
            sourceEntity.setTicksFrozen((int) Math.min(frozenTicks + frozenDuration, 200));
            return;
        }
        if (!(sourceEntity instanceof LivingEntity living) || !living.hasEffect(CAMobEffects.FROZEN)) {
            this.level().playSound(null, BlockPos.containing(sourceEntity.getX(), sourceEntity.getY(), sourceEntity.getZ()),
                    CASounds.LAST_JNIGHT_FREEZE.get(), SoundSource.HOSTILE,
                    4, (float) Mth.nextDouble(RandomSource.create(), 1, 1.15));
        }
        if (sourceEntity instanceof LivingEntity living && !living.level().isClientSide()) {
            living.addEffect(new MobEffectInstance(CAMobEffects.FROZEN, frozenDuration, 0, false, false));
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
        data.add(new AnimationController<>(this, "attacking", 0, this::attackingPredicate));
        data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    private void performCrossAttack() {
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();

        double damage = (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 2;
        Entity target = this.getTarget();

        for (int index0 = 0; index0 < 96; index0++) {
            if (world instanceof ServerLevel level)
                level.sendParticles(ParticleTypes.ENCHANTED_HIT, x - 12 + index0 * 0.25, y, (z + 2), 2, 0, 0.5, 0, 0.1);
            if (world instanceof ServerLevel level)
                level.sendParticles(ParticleTypes.ENCHANTED_HIT, x - 12 + index0 * 0.25, y, (z - 2), 2, 0, 0.5, 0, 0.1);
            if (world instanceof ServerLevel level)
                level.sendParticles(ParticleTypes.ENCHANTED_HIT, (x + 2), y, z - 12 + index0 * 0.25, 2, 0, 0.5, 0, 0.1);
            if (world instanceof ServerLevel level)
                level.sendParticles(ParticleTypes.ENCHANTED_HIT, (x - 2), y, z - 12 + index0 * 0.25, 2, 0, 0.5, 0, 0.1);
        }

        if (world instanceof Level level) {
            level.playSound(null, BlockPos.containing(x, y, z), CASounds.LAST_KNIGHT_ATTACK.get(), SoundSource.HOSTILE, 5, 1);
        }

        for (Entity entityiterator : world.getEntities(this, new AABB((x + 16), (y + 4), (z + 1.5), (x - 16), (y - 2), (z - 1.5)))) {
            if (entityiterator instanceof LivingEntity) {
                if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "is_humanside")))) {
                    if (!(entityiterator == target)) {
                        continue;
                    }
                }
                entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.LAST_KNIGHT_ATTACK, this), this.applyFrozenExecutionBonus(entityiterator, (float) damage));
            }
        }

        for (Entity entityiterator : world.getEntities(this, new AABB((x + 1.5), (y + 4), (z + 16), (x - 1.5), (y - 2), (z - 16)))) {
            if (entityiterator instanceof LivingEntity) {
                if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "is_humanside")))) {
                    if (!(entityiterator == target)) {
                        continue;
                    }
                }
                entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.LAST_KNIGHT_ATTACK),
                        this.applyFrozenExecutionBonus(entityiterator, (float) damage));
            }
        }
    }

    private boolean isLastKnightDurative() {
        return this.isAlive() && this.getEntityData().get(DATA_DURATION) <= 0;
    }

    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}