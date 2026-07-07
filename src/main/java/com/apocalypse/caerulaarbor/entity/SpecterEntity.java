package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.SyncedAnimationEntity;
import com.apocalypse.caerulaarbor.init.*;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

public class SpecterEntity extends Animal implements GeoEntity, SyncedAnimationEntity {

    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(SpecterEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(SpecterEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_SKILLP_1 = SynchedEntityData.defineId(SpecterEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_SKILLP_2 = SynchedEntityData.defineId(SpecterEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(SpecterEntity.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";


    public SpecterEntity(Level world) {
        this(CAEntities.SPECTER.get(), world);
    }

    public SpecterEntity(EntityType<SpecterEntity> type, Level world) {
        super(type, world);
        xpReward = 0;
        setNoAi(false);
        setMaxUpStep(1f);
        setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SHOOT, false);
        this.entityData.define(DATA_ANIMATION, "undefined");
        this.entityData.define(DATA_SKILLP_1, 2);
        this.entityData.define(DATA_SKILLP_2, 240);
        this.entityData.define(DATA_DURATION, 0);
    }


    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.3, false) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return 12.25;
            }

            @Override
            public boolean canUse() {
                return super.canUse() && isSpecterDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isSpecterDurative();
            }

        });
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Monster.class, true, false));
        this.goalSelector.addGoal(4, new OpenDoorGoal(this, false));
        this.goalSelector.addGoal(5, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, IreneEntity.class, (float) 11) {
            @Override
            public boolean canUse() {
                return super.canUse() && isSpecterDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isSpecterDurative();
            }
        });
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1) {
            @Override
            public boolean canUse() {
                return super.canUse() && isSpecterDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isSpecterDurative();
            }
        });
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && isSpecterDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isSpecterDurative();
            }
        });
        this.goalSelector.addGoal(9, new FloatGoal(this));
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return CASounds.SPECTER_HIT.get();
    }

    @Override
    public SoundEvent getDeathSound() {
        return CASounds.SPECTER_DIE.get();
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        double targetX = target.getX();
        double targetY = target.getY();
        double targetZ = target.getZ();
        if (!this.level().isClientSide()) {
            this.getEntityData().set(DATA_DURATION, this.getEntityData().get(DATA_DURATION) + 30);
            this.getEntityData().set(DATA_SKILLP_1, this.getEntityData().get(DATA_SKILLP_1) + 1);
            this.level().playSound(null, BlockPos.containing(targetX, targetY, targetZ),
                    CASounds.SPECTER_ATTACK.get(), SoundSource.HOSTILE, 2.5F, 1);
            CaerulaArborMod.queueServerWork(12, () -> {
                if (this.isAlive() && target.isAlive() && this.distanceTo(target) <= 3.5) {
                    target.hurt(
                            CADamageTypes.source(this.level(), CADamageTypes.SAW_CUT, this), (float) ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 0.45));
                }
            });
            CaerulaArborMod.queueServerWork(15, () -> {
                if (this.isAlive() && target.isAlive() && this.distanceTo(target) <= 3.5) {
                    target.hurt(
                            CADamageTypes.source(this.level(), CADamageTypes.SAW_CUT, this), (float) ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 0.5));
                }
            });
            CaerulaArborMod.queueServerWork(18, () -> {
                if (this.isAlive() && target.isAlive() && this.distanceTo(target) <= 3.5) {
                    target.hurt(
                            CADamageTypes.source(this.level(), CADamageTypes.SAW_CUT, this), (float) ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 0.45));
                }
            });
        }
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.DROWN))
            return false;
        return super.hurt(source, amount);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        if ((LevelAccessor) world instanceof Level level) {
            level.playSound(null, BlockPos.containing(x, y, z), CASounds.SAW_SPECT_SKILL.get(), SoundSource.NEUTRAL, (float) 2.5, 1);
        }
        this.setAnimation("animation.specter.start");
        return super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
    }

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Skillp1", this.entityData.get(DATA_SKILLP_1));
        compound.putInt("Skillp2", this.entityData.get(DATA_SKILLP_2));
        compound.putInt("Duration", this.entityData.get(DATA_DURATION));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Skillp1")) {
            this.entityData.set(DATA_SKILLP_1, compound.getInt("Skillp1"));
        }
        if (compound.contains("Skillp2")) {
            this.entityData.set(DATA_SKILLP_2, compound.getInt("Skillp2"));
        }
        if (compound.contains("Duration")) {
            this.entityData.set(DATA_DURATION, compound.getInt("Duration"));
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
        double sklp1;
        double dura;
        double skillp2;
        if (this.isAlive()) {
            sklp1 = this.getEntityData().get(DATA_SKILLP_1);
            skillp2 = this.getEntityData().get(DATA_SKILLP_2);
            dura = this.getEntityData().get(DATA_DURATION);
            target = this.getTarget();
            if (dura > 0) {
                this.getEntityData().set(DATA_DURATION, (int) (dura - 1));
            }
            if (sklp1 >= 5) {
                if (!(target == null) && target.isAlive()) {
                    if (distanceTo(target) <= 4) {
                        this.setAnimation("animation.specter.skill");
                        this.getEntityData().set(DATA_SKILLP_1, 0);
                        this.getEntityData().set(DATA_DURATION, (int) (dura + 45));
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 35, 0, false, false));
                        CaerulaArborMod.queueServerWork(8, () -> {
                            if (world instanceof Level level) {
                                level.playSound(null, BlockPos.containing(x, y, z), CASounds.SAW_HEAVY.get(), SoundSource.NEUTRAL, 3, 1);
                            }
                        });
                        CaerulaArborMod.queueServerWork(15, () -> {
                            if (this.isAlive()) {
                                this.performRangedAttack(2);
                            }
                        });
                        CaerulaArborMod.queueServerWork(25, () -> {
                            if (world instanceof Level level) {
                                level.playSound(null, BlockPos.containing(x, y, z), CASounds.SAW_CUT_SPECT.get(), SoundSource.NEUTRAL, 3, 1);
                            }
                        });
                        CaerulaArborMod.queueServerWork(31, () -> {
                            if (this.isAlive()) {
                                this.performRangedAttack(1.5);
                            }
                        });
                    }
                }
            }
            if (skillp2 > 0) {
                if (EntityUtils.getHealthPerc(this) <= 0.5 && skillp2 < 600) {
                    this.getEntityData().set(DATA_SKILLP_2, (int) (skillp2 - 2));
                } else {
                    this.getEntityData().set(DATA_SKILLP_2, (int) (skillp2 - 1));
                }
            } else {
                if (!(target == null) && target.isAlive()) {
                    if (EntityUtils.getHealthPerc(this) <= 0.5) {
                        if (world instanceof Level level) {
                            level.playSound(null, BlockPos.containing(x, y, z), CASounds.SPECTER_SKILL_ON.get(), SoundSource.NEUTRAL, 3, 1);
                        }
                        if (world instanceof Level level) {
                            level.playSound(null, BlockPos.containing(x, y, z), CASounds.SPECTER_SKILL.get(), SoundSource.NEUTRAL, 3, 1);
                        }
                        this.getEntityData().set(DATA_SKILLP_2, 1000);
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CAMobEffects.IMMORTAL.get(), 400, 0, false, false));
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CAMobEffects.ADD_ATTACK_PERCLY.get(), 400, 5, false, false));
                    }
                }
            }
            GladiiaEntity.healFromGladiia(world, x, y, z, this);
            double perc;
            if (tickCount % 10 == 0) {
                {
                    final Vec3 center = new Vec3(x, y, z);
                    List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(48 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                    for (Entity entityiterator : entfound) {
                        if (!entityiterator.isAlive()) {
                            continue;
                        }
                        if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hunters")))) {
                            if (!(entityiterator instanceof LivingEntity livEnt3 && livEnt3.hasEffect(CAMobEffects.ADD_HEALTH_PERCLY.get()))) {
                                perc = EntityUtils.getHealthPerc(entityiterator);
                                if (entityiterator instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                                    livingEntity.addEffect(new MobEffectInstance(CAMobEffects.ADD_HEALTH_PERCLY.get(), 32768, 0, false, false));
                                if (entityiterator instanceof LivingEntity livingEntity)
                                    livingEntity.setHealth((float) (livingEntity.getMaxHealth() * perc));
                            }
                        }
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
        SpecterEntity retval = CAEntities.SPECTER.get().create(serverWorld);
        retval.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(retval.blockPosition()), MobSpawnType.BREEDING, null, null);
        return retval;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        stack.getItem();
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.updateSwingTime();
    }


    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.18);
        builder = builder.add(ForgeMod.SWIM_SPEED.get(), 8);
        builder = builder.add(CAAttributes.SANITY_MODIFIER.get(), 0.33);
        builder = builder.add(Attributes.MAX_HEALTH, 218);
        builder = builder.add(Attributes.ARMOR, 4);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 34);
        builder = builder.add(Attributes.FOLLOW_RANGE, 32);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.85);
        return builder;
    }

    private PlayState movementPredicate(AnimationState<?> event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

                    && !this.isAggressive()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.specter.move"));
            }
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.specter.die"));
            }
            if (this.isAggressive() && event.isMoving()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.specter.run"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.specter.idle"));
        }
        return PlayState.STOP;
    }

    private PlayState attackingPredicate(AnimationState<?> event) {
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
            this.swinging = true;
            this.lastSwing = level().getGameTime();
        }
        if (this.swinging && this.lastSwing + 30L <= level().getGameTime()) {
            this.swinging = false;
        }
        if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.specter.attack"));
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
        if (this.deathTime == 20) {
            this.remove(RemovalReason.KILLED);
            this.dropExperience();
            LevelAccessor world = this.level();
            if (world instanceof ServerLevel level) {
                Entity entityToSpawn = CAEntities.SPECTER_DOLL.get().spawn(level, BlockPos.containing(this.getX(), this.getY(), this.getZ()), MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
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
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    private void performRangedAttack(double rate) {
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();

        Entity target = this.getTarget();
        double damage = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
        double range = 4.5;

        final Vec3 center = new Vec3(x, y, z);
        List<Entity> nearbyEntities = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(4.5), e -> true).stream()
                .sorted(Comparator.comparingDouble(ent -> ent.distanceToSqr(center)))
                .toList();

        for (Entity entityiterator : nearbyEntities) {
            if (!(entityiterator instanceof LivingEntity)) {
                continue;
            }
            if (!entityiterator.isAlive()) {
                continue;
            }
            if (entityiterator instanceof Player || (entityiterator instanceof TamableAnimal tamEnt && tamEnt.isTame())) {
                if (!(entityiterator == target)) {
                    continue;
                }
            }
            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "is_humanside")))) {
                if (!(entityiterator == target)) {
                    continue;
                }
            }
            if (entityiterator == this) {
                continue;
            }
            if (this.distanceTo(entityiterator) <= range) {
                entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.SAW_CUT, this), (float) (damage * rate));
            }
        }
    }

    public static boolean isSpecterAround(LevelAccessor world, double x, double y, double z) {
        return !world.getEntitiesOfClass(SpecterEntity.class, AABB.ofSize(new Vec3(x, y, z), 64, 64, 64), Entity::isAlive).isEmpty();
    }

    private boolean isSpecterDurative() {
        return this.isAlive() && this.tickCount > 15 && this.getEntityData().get(DATA_DURATION) <= 0;
    }

    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}
