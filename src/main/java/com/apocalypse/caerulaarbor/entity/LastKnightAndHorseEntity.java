package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.SyncedAnimationEntity;
import com.apocalypse.caerulaarbor.init.*;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
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
import net.minecraft.world.entity.animal.Animal;
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
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

public class LastKnightAndHorseEntity extends Animal implements GeoEntity, SyncedAnimationEntity {

    public static final EntityDataAccessor<Boolean> DATA_IS_SHOOTING = SynchedEntityData.defineId(LastKnightAndHorseEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(LastKnightAndHorseEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_ADDITION = SynchedEntityData.defineId(LastKnightAndHorseEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_SKILL_COOLDOWN = SynchedEntityData.defineId(LastKnightAndHorseEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_SKILL_DURATION = SynchedEntityData.defineId(LastKnightAndHorseEntity.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";
    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.WHITE, ServerBossEvent.BossBarOverlay.NOTCHED_6);

    public LastKnightAndHorseEntity(Level world) {
        this(CAEntities.LAST_KNIGHT_AND_HORSE.get(), world);
    }

    public LastKnightAndHorseEntity(EntityType<LastKnightAndHorseEntity> type, Level world) {
        super(type, world);
        xpReward = 64;
        setNoAi(false);
        setMaxUpStep(1.25f);
        setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_SHOOTING, false);
        this.entityData.define(DATA_ANIMATION, "undefined");
        this.entityData.define(DATA_ADDITION, 0);
        this.entityData.define(DATA_SKILL_COOLDOWN, 140);
        this.entityData.define(DATA_SKILL_DURATION, 0);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && isLastKnightStarting();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isLastKnightStarting();
            }
        });
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.33, false) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return 36;
            }

            @Override
            public boolean canUse() {
                return super.canUse() && isLastKnightStarting();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isLastKnightStarting();
            }

        });
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Monster.class, true, false) {
            @Override
            public boolean canUse() {
                return super.canUse() && isLastKnightStarting();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isLastKnightStarting();
            }
        });
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1) {
            @Override
            public boolean canUse() {
                return super.canUse() && isLastKnightStarting();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isLastKnightStarting();
            }
        });
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && isLastKnightStarting();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isLastKnightStarting();
            }
        });
        this.goalSelector.addGoal(6, new FloatGoal(this));
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
        if (source.is(DamageTypes.FALL))
            return false;
        if (source.is(DamageTypes.CACTUS))
            return false;
        if (source.is(DamageTypes.DROWN))
            return false;
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        Entity sourceentity = source.getEntity();
        if (sourceentity == null)
            return;
        if (sourceentity instanceof ServerPlayer player) {
            Advancement adv = player.server.getAdvancements().getAdvancement(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "kill_knight_and_horse"));
            AdvancementProgress ap;
            if (adv != null) {
                ap = player.getAdvancements().getOrStartProgress(adv);
                if (!ap.isDone()) {
                    for (String criteria : ap.getRemainingCriteria())
                        player.getAdvancements().award(adv, criteria);
                }
            }
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
        SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
        this.setAnimation("animation.last_knight_horse.start");
        new Object() {
            void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                if (isAlive()) {
                    LastKnightAndHorseEntity.this.setHealth((float) (LastKnightAndHorseEntity.this.getMaxHealth() * (1 - (timedloopiterator + 1) * 0.0125)));
                }
                final int tick2 = ticks;
                CaerulaArborMod.queueServerWork(tick2, () -> {
                    if (timedlooptotal > timedloopiterator + 1) {
                        timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                    }
                });
            }
        }.timedLoop(0, 40, 1);
        return retval;
    }

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Addition", this.entityData.get(DATA_ADDITION));
        compound.putInt("SkillCooldown", this.entityData.get(DATA_SKILL_COOLDOWN));
        compound.putInt("SkillDuration", this.entityData.get(DATA_SKILL_DURATION));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Addition"))
            this.entityData.set(DATA_ADDITION, compound.getInt("Addition"));
        if (compound.contains("SkillCooldown"))
            this.entityData.set(DATA_SKILL_COOLDOWN, compound.getInt("SkillCooldown"));
        if (compound.contains("SkillDuration"))
            this.entityData.set(DATA_SKILL_DURATION, compound.getInt("SkillDuration"));
	}

    @Override
    public void awardKillScore(Entity entity, int score, DamageSource damageSource) {
        super.awardKillScore(entity, score, damageSource);
        LevelAccessor world = this.level();
        if ((this.getHealth()) < (this.getMaxHealth())) {
            this.setHealth((float) ((this.getHealth()) + (this.getMaxHealth()) * 0.03));
            if (world instanceof ServerLevel level)
                level.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getX(), (this.getY() + 0.75), this.getZ(), 32, 1, 2, 1, 0.1);
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity target;
        double add;
        double duration;
        double skillp;
        if (this.isAlive()) {
            if (tickCount % 10 == 0) {
                this.removeEffect(CAMobEffects.DIZZY.get());
                this.removeEffect(CAMobEffects.FROZEN.get());
                this.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
                if (!this.level().isClientSide())
                    this.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20, 0, false, false));
                if ((this.getHealth()) > (this.getMaxHealth()) * 0.5) {
                    final Vec3 center = new Vec3(x, y, z);
                    List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(48 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                    for (Entity entityiterator : entfound) {
                        if (entityiterator.getTicksFrozen() >= 125 && entityiterator.isAlive()) {
                            entityiterator.setTicksFrozen(200);
                            if (entityiterator instanceof LivingEntity && !this.level().isClientSide())
                                this.addEffect(new MobEffectInstance(CAMobEffects.FROZEN.get(), 20, 0, false, false));
                        }
                    }
                }
                target = this.getTarget();
                if (!(target == null) && target.isAlive()) {
                    add = this.getEntityData().get(DATA_ADDITION);
                    if (add < 20) {
                        this.getEntityData().set(DATA_ADDITION, (int) (add + 1));
                    }
                }
            }
            if (EntityUtils.getSpeed(this) > (this.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED) ? this.getAttribute(Attributes.MOVEMENT_SPEED).getValue() : 0)
                    * 1.25) {
                setDeltaMovement(new Vec3(0, 0, 0));
            }
            setTicksFrozen(0);
            this.removeEffect(CAMobEffects.FROZEN.get());
            this.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            this.removeEffect(CAMobEffects.DIZZY.get());
            skillp = this.getEntityData().get(DATA_SKILL_COOLDOWN);
            duration = this.getEntityData().get(DATA_SKILL_DURATION);
            if (duration > 0) {
                this.getEntityData().set(DATA_SKILL_DURATION, (int) (duration - 1));
            }
            target = this.getTarget();
            if (skillp > 0) {
                this.getEntityData().set(DATA_SKILL_COOLDOWN, (int) (skillp - 1));
            } else {
                if (!(target == null) && target.isAlive()) {
                    if (distanceTo(target) < 4) {
                        this.getEntityData().set(DATA_SKILL_DURATION, 40);
                        this.getEntityData().set(DATA_SKILL_COOLDOWN, 240);
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 32, 0, false, false));
                        this.setAnimation("animation.last_knight_horse.skill");
                        CaerulaArborMod.queueServerWork(13, () -> {
                            Entity enemy1;
                            double damage;
                            damage = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
                            enemy1 = this.getTarget();
                            {
                                final Vec3 center = new Vec3((x + 2 * getLookAngle().x), y, (z + 2 * getLookAngle().z));
                                List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(8 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                                for (Entity entityiterator : entfound) {
                                    if (!(entityiterator instanceof LivingEntity)) {
                                        continue;
                                    }
                                    if (entityiterator == this) {
                                        continue;
                                    }
                                    if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "is_humanside"))) && !(entityiterator == enemy1)) {
                                        continue;
                                    }
                                    if (distanceTo(entityiterator) <= 4) {
                                        entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.LAST_KNIGHT_ATTACK, this), (float) (damage * 1.5));
                                        entityiterator.push(0, 0.64, 0);
                                        if ((entityiterator instanceof LivingEntity entUseItem12 ? entUseItem12.getUseItem() : ItemStack.EMPTY).getItem() instanceof ShieldItem) {
                                            if (entityiterator instanceof Player player) {
                                                player.getCooldowns().addCooldown(player.getUseItem().getItem(), 100);
                                            }
                                            if (world instanceof Level level) {
                                                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.SHIELD_BREAK, SoundSource.HOSTILE, 1, 1);
                                            }
                                        }
                                        CaerulaArborMod.queueServerWork(7, () -> {
                                            entityiterator.hurt(
                                                    CADamageTypes.source(world, CADamageTypes.LAST_KNIGHT_ATTACK, this), (float) ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 2));
                                            if (entityiterator instanceof LivingEntity && !this.level().isClientSide())
                                                this.addEffect(new MobEffectInstance(CAMobEffects.ROCK_BREAK.get(), 150, 0, false, false));
                                            entityiterator.push(0, (-1), 0);
                                        });
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

    @Override
    public EntityDimensions getDimensions(Pose p_33597_) {
        return super.getDimensions(p_33597_).scale((float) 1);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageable) {
        LastKnightAndHorseEntity retval = CAEntities.LAST_KNIGHT_AND_HORSE.get().create(serverWorld);
        retval.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(retval.blockPosition()), MobSpawnType.BREEDING, null, null);
        return retval;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entityIn) {
    }

    @Override
    protected void pushEntities() {
    }

    @Override
    public boolean canChangeDimensions() {
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
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.25);
        builder = builder.add(ForgeMod.SWIM_SPEED.get(), 12);
        builder = builder.add(CAAttributes.GENERAL_DEFENSE.get(), 20);
        builder = builder.add(CAAttributes.MAGIC_RESISTANCE.get(), 60);
        builder = builder.add(Attributes.MAX_HEALTH, 400);
        builder = builder.add(Attributes.ARMOR, 24);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 20);
        builder = builder.add(Attributes.FOLLOW_RANGE, 36);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
        builder = builder.add(Attributes.ATTACK_KNOCKBACK, 0.5);
        builder = builder.add(CAAttributes.MAX_SANITY.get(), 2000);
        return builder;
    }

    private PlayState movementPredicate(AnimationState<?> event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

            ) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.last_knight_horse.move"));
            }
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.last_knight_horse.die"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.last_knight_horse.idle"));
        }
        return PlayState.STOP;
    }

    private PlayState attackingPredicate(AnimationState<?> event) {
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
            this.swinging = true;
            this.lastSwing = level().getGameTime();
        }
        if (this.swinging && this.lastSwing + 19L <= level().getGameTime()) {
            this.swinging = false;
        }
        if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.last_knight_horse.atack"));
        }
        return PlayState.CONTINUE;
    }

    String prevAnim = "empty";

    private PlayState procedurePredicate(AnimationState<?> event) {
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
        if (this.deathTime == 40) {
            this.remove(RemovalReason.KILLED);
            this.dropExperience();
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
        if (!(sourceEntity instanceof LivingEntity living) || !living.hasEffect(CAMobEffects.FROZEN.get())) {
            this.level().playSound(null, BlockPos.containing(sourceEntity.getX(), sourceEntity.getY(), sourceEntity.getZ()),
                    CASounds.LAST_JNIGHT_FREEZE.get(), SoundSource.HOSTILE,
                    4, (float) Mth.nextDouble(RandomSource.create(), 1, 1.15));
        }
        if (sourceEntity instanceof LivingEntity living && !living.level().isClientSide()) {
            living.addEffect(new MobEffectInstance(CAMobEffects.FROZEN.get(), frozenDuration, 0, false, false));
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

    private boolean isLastKnightStarting() {
        return this.tickCount >= 40 && this.getEntityData().get(DATA_SKILL_DURATION) <= 0;
    }

    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}
