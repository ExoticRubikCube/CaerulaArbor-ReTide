package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.*;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Comparator;
import java.util.List;

public class OceanizedBruteEntity extends SeaMonster {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(OceanizedBruteEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(OceanizedBruteEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_ABILITY = SynchedEntityData.defineId(OceanizedBruteEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_SKILLP = SynchedEntityData.defineId(OceanizedBruteEntity.class, EntityDataSerializers.INT);
    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.YELLOW, ServerBossEvent.BossBarOverlay.PROGRESS);
    public String animationprocedure = "empty";
    String prevAnim = "empty";
    private boolean swinging;
    private long lastSwing;

    public OceanizedBruteEntity(Level world) {
        this(CAEntities.OCEANIZED_BRUTE.get(), world);
    }

    public OceanizedBruteEntity(EntityType<OceanizedBruteEntity> type, Level world) {
        super(type, world);
        xpReward = 32;
        setNoAi(false);
        setMaxUpStep(1f);
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(CAAttributes.SANITY_RATE.get(), 7);
        builder = builder.add(CAAttributes.MAGIC_RESISTANCE.get(), 20);
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.25);
        builder = builder.add(Attributes.MAX_HEALTH, 115);
        builder = builder.add(Attributes.ARMOR, 5);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 16);
        builder = builder.add(Attributes.FOLLOW_RANGE, 24);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.45);
        return builder;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SHOOT, false);
        this.entityData.define(DATA_ANIMATION, "undefined");
        this.entityData.define(DATA_ABILITY, 0);
        this.entityData.define(DATA_SKILLP, 3);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1, true) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return 6.76;
            }
        });
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Hoglin.class, true, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, WitherSkeleton.class, true, false));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, WitherBoss.class, true, false));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, IronGolem.class, true, false));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, SnowGolem.class, true, false));
        this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, Villager.class, true, false));
        this.targetSelector.addGoal(9, new NearestAttackableTargetGoal<>(this, Illusioner.class, true, false));
        this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, Pillager.class, true, false));
        this.targetSelector.addGoal(11, new NearestAttackableTargetGoal<>(this, Vindicator.class, true, false));
        this.targetSelector.addGoal(12, new NearestAttackableTargetGoal<>(this, Witch.class, true, false));
        this.targetSelector.addGoal(13, new NearestAttackableTargetGoal<>(this, Piglin.class, true, false));
        this.targetSelector.addGoal(14, new NearestAttackableTargetGoal<>(this, PiglinBrute.class, true, false));
        this.targetSelector.addGoal(15, new NearestAttackableTargetGoal<>(this, ZombifiedPiglin.class, true, false));
        this.goalSelector.addGoal(17, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(18, new RandomStrollGoal(this, 1));
        this.goalSelector.addGoal(19, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(20, new FloatGoal(this));
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public SoundEvent getAmbientSound() {
        return CASounds.BRUTE_AMBIENT.get();
    }

    @Override
    public void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.PIGLIN_BRUTE_STEP, 0.15f, 1);
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return CASounds.BRUTE_HURT.get();
    }

    @Override
    public SoundEvent getDeathSound() {
        return CASounds.BRUTE_DIE.get();
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (!this.level().isClientSide()) {
            CaerulaArborMod.queueServerWork(10, () -> {
                if (this.isAlive() && target.isAlive() && this.distanceTo(target) <= 2.6) {
                    boolean damaged = target.hurt(
                            CADamageTypes.source(this.level(), CADamageTypes.GENERIC_SEABORN_ATTACK, this), (float) (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0));
                    if (damaged && target instanceof Player player) {
                        recordHurtPlayer(player);
                    }
                }
            });
        }
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity sourceentity = source.getEntity();
        if (sourceentity != null) {
            double sklp;
            if (this.isAlive()) {
                sklp = (Entity) this instanceof OceanizedBruteEntity datEntI ? datEntI.getEntityData().get(DATA_SKILLP) : 0;
                if (sklp >= 0) {
                    if ((Entity) this instanceof OceanizedBruteEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_SKILLP, (int) (sklp - 1));
                } else {
                    if (distanceTo(sourceentity) <= 5 && !this.hasEffect(CAMobEffects.COOLDOWN_SINAL.get())) {
                        if (this instanceof OceanizedBruteEntity) {
                            this.setAnimation("animation.oceanized_brute.skill");
                        }
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 40, 1, false, false));
                        this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((sourceentity.getX()), (sourceentity.getY()), (sourceentity.getZ())));
                        if ((Entity) this instanceof OceanizedBruteEntity datEntSetI)
                            datEntSetI.getEntityData().set(DATA_SKILLP, 5);
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CAMobEffects.COOLDOWN_SINAL.get(), 80, 0, false, false));
                        CaerulaArborMod.queueServerWork(20, () -> {
                            if (this.isAlive()) {
                                double sklp1;
                                sklp1 = Math.max(
                                        Math.min((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 4,
                                                ((Entity) this instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * 0.25),
                                        (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 1);
                                if (distanceTo(sourceentity) <= 3) {
                                    if (sourceentity.hurt(CADamageTypes.source(world, CADamageTypes.GENERIC_SEABORN_ATTACK, this), (float) sklp1) && sourceentity instanceof Player player) {
                                        recordHurtPlayer(player);
                                    }
                                    if (sourceentity instanceof LivingEntity entity1 && !entity1.level().isClientSide())
                                        entity1.addEffect(new MobEffectInstance(CAMobEffects.DIZZY.get(), 120, 0, false, false));
                                    if (sourceentity instanceof LivingEntity entity1 && !entity1.level().isClientSide())
                                        entity1.addEffect(new MobEffectInstance(CAMobEffects.ROCK_BREAK.get(), 120, 0, false, false));
                                }
                                if (world instanceof Level level) {
                                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.SHIELD_BREAK, SoundSource.HOSTILE, 2, 1);
                                }
                                final Vec3 center = new Vec3((x + 2 * getLookAngle().x), y, (z + 2 * getLookAngle().z));
                                List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(6 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                                for (Entity entityiterator : entfound) {
                                    if (!(entityiterator instanceof Mob)) {
                                        continue;
                                    }
                                    if (entityiterator == sourceentity) {
                                        continue;
                                    }
                                    if (entityiterator == this) {
                                        continue;
                                    }
                                    if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring")))) {
                                        if (!(((Entity) this instanceof Mob mobEnt ? (Entity) mobEnt.getTarget() : null) == entityiterator)) {
                                            continue;
                                        }
                                    }
                                    if (distanceTo(entityiterator) <= 3) {
                                        sklp1 = Math.max(
                                                Math.min((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 3,
                                                        ((Entity) this instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * 0.25),
                                                (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 1);
                                        entityiterator.hurt(
                                                CADamageTypes.source(world, CADamageTypes.GENERIC_SEABORN_ATTACK, this), (float) sklp1);
                                        if (entityiterator instanceof LivingEntity entity1 && !entity1.level().isClientSide())
                                            entity1.addEffect(new MobEffectInstance(CAMobEffects.DIZZY.get(), 120, 0, false, false));
                                        if (entityiterator instanceof LivingEntity entity1 && !entity1.level().isClientSide())
                                            entity1.addEffect(new MobEffectInstance(CAMobEffects.ROCK_BREAK.get(), 120, 0, false, false));
                                    }
                                }
                            }
                        });
                    }
                }
            }
            if (!(sourceentity instanceof OceanizedPiglinEntity)) {
                final Vec3 center = new Vec3(x, y, z);
                List<OceanizedPiglinEntity> entfound = world.getEntitiesOfClass(OceanizedPiglinEntity.class, new AABB(center, center).inflate(32 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                for (OceanizedPiglinEntity entityiterator : entfound) {
                    if (sourceentity instanceof LivingEntity ent) {
                        entityiterator.setTarget(ent);
                    }
                }
            }
        }
        if (source.is(DamageTypes.IN_FIRE))
            return false;
        if (source.is(DamageTypes.FALL))
            return false;
        if (source.is(DamageTypes.DROWN))
            return false;
        return super.hurt(source, amount);
    }

    private void recordHurtPlayer(Player player) {
        String hurtPlayerNames = getPersistentData().getString("hurtPlayer");
        String playerName = player.getDisplayName().getString();
        if (!hurtPlayerNames.contains(playerName)) {
            getPersistentData().putString("hurtPlayer", hurtPlayerNames + "," + playerName);
        }
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        LevelAccessor world = this.level();
        Entity sourceentity = source.getEntity();
        if (sourceentity == null)
            return;
        String str;
        String name;
        if (sourceentity instanceof Player && !(sourceentity instanceof ServerPlayer plr1 && plr1.level() instanceof ServerLevel
                && plr1.getAdvancements().getOrStartProgress(plr1.server.getAdvancements().getAdvancement(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "kill_brute"))).isDone())) {
            str = getPersistentData().getString("hurtPlayer");
            name = sourceentity.getDisplayName().getString();
            if (!str.contains(name)) {
                if (sourceentity instanceof ServerPlayer player) {
                    Advancement adv = player.server.getAdvancements().getAdvancement(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "kill_brute"));
                    AdvancementProgress ap = player.getAdvancements().getOrStartProgress(adv);
                    if (!ap.isDone()) {
                        for (String criteria : ap.getRemainingCriteria())
                            player.getAdvancements().award(adv, criteria);
                    }
                }
                if (world instanceof ServerLevel level) {
                    ItemEntity entityToSpawn = new ItemEntity(level, this.getX(), (this.getY() + 0.5), this.getZ(), new ItemStack(CAItems.CRIMSON_TREATY.get()));
                    entityToSpawn.setPickUpDelay(10);
                    entityToSpawn.setUnlimitedLifetime();
                    level.addFreshEntity(entityToSpawn);
                }
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Ability", this.entityData.get(DATA_ABILITY));
        compound.putInt("Skillp", this.entityData.get(DATA_SKILLP));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Ability")) {
            this.entityData.set(DATA_ABILITY, compound.getInt("Ability"));
        }
        if (compound.contains("Skillp")) {
            this.entityData.set(DATA_SKILLP, compound.getInt("Skillp"));
        }
    }

    @Override
    public void awardKillScore(Entity entity, int score, DamageSource damageSource) {
        super.awardKillScore(entity, score, damageSource);
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        double ablty;
        ablty = (Entity) this instanceof OceanizedBruteEntity datEntI ? datEntI.getEntityData().get(DATA_ABILITY) : 0;
        if (ablty < 7) {
            if (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
                this.getAttribute(Attributes.ATTACK_DAMAGE)
                        .setBaseValue(((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : 0) + 4));
            if ((Entity) this instanceof OceanizedBruteEntity datEntSetI)
                datEntSetI.getEntityData().set(DATA_ABILITY, (int) (ablty + 1));
            if (world instanceof ServerLevel level)
                level.sendParticles(ParticleTypes.LAVA, x, (y + 0.75), z, 32, 0.75, 0.75, 0.75, 0.1);
        }
        if (((Entity) this instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) < ((Entity) this instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1)) {
            this.setHealth((float) (this.getHealth() + this.getMaxHealth() * 0.1));
            if (world instanceof ServerLevel level)
                level.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, (y + 0.75), z, 32, 0.75, 0.75, 0.75, 0.1);
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        this.refreshDimensions();
    }

    @Override
    public EntityDimensions getDimensions(Pose p_33597_) {
        return super.getDimensions(p_33597_).scale((float) 1);
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

    private PlayState movementPredicate(AnimationState<?> event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

            ) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_brute.move"));
            }
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_brute.die"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_brute.idle"));
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
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_brute.attack"));
        }
        return PlayState.CONTINUE;
    }

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
        if (this.deathTime == 30) {
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
