package com.susen36.caerulaarbor.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.base.SeaMonsterBoss;
import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CADamageTypes;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAGameRules;
import com.susen36.caerulaarbor.util.EntityUtils;
import com.susen36.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import java.util.Comparator;
import java.util.List;

public class SuperBigCatEntity extends SeaMonsterBoss {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(SuperBigCatEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(SuperBigCatEntity.class, EntityDataSerializers.STRING);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";

    public SuperBigCatEntity(Level world) {
        this(CAEntities.SUPER_BIG_CAT.get(), world);
    }

    public SuperBigCatEntity(EntityType<SuperBigCatEntity> type, Level world) {
        super(type, world);
        this.bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.GREEN, ServerBossEvent.BossBarOverlay.PROGRESS);
        xpReward = 64;
        setNoAi(false);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.8f);
        setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHOOT, false);
        builder.define(DATA_ANIMATION, "undefined");
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Chicken.class, true, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Rabbit.class, true, false));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Cat.class, true, false));
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
        return SoundEvents.OCELOT_AMBIENT;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.OCELOT_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.OCELOT_DEATH;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        double targetX = target.getX();
        double targetY = target.getY();
        double targetZ = target.getZ();
        if (!this.level().isClientSide()) {
            this.level().playSound(null, BlockPos.containing(targetX, targetY, targetZ),
                    SoundEvents.CAT_HISS, SoundSource.HOSTILE, 1,
                    (float) Mth.nextDouble(RandomSource.create(), 0.85, 1.15));
            CaerulaArbor.queueServerWork(9, () -> {
                if (this.isAlive() && target.isAlive() && this.distanceTo(target) <= 10) {
                    superCatRanged(targetX, targetY, targetZ);
                }
            });
            CaerulaArbor.queueServerWork(14, () -> {
                if (this.isAlive() && target.isAlive() && this.distanceTo(target) <= 15) {
                    superCatRanged(targetX, targetY, targetZ);
                }
            });
        }
        return true;
    }

    private void superCatRanged(double x, double y, double z) {
        Entity target = this.getTarget();
        double damage;
        Vec3 center = new Vec3(x, y, z);
        List<Entity> entities = this.level().getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(10 / 2d), entity -> true).stream()
                .sorted(Comparator.comparingDouble(candidate -> candidate.distanceToSqr(center))).toList();
        for (Entity entityIterator : entities) {
            if (!(entityIterator instanceof Mob) && !(entityIterator instanceof Player)) {
                continue;
            }
            if (entityIterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "sea_born"))) && entityIterator != target) {
                continue;
            }
            if (entityIterator == this) {
                continue;
            }
            if (center.distanceTo(new Vec3(entityIterator.getX(), entityIterator.getY(), entityIterator.getZ())) <= 5) {
                damage = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
                entityIterator.hurt(
                        CADamageTypes.source(this.level(), CADamageTypes.SUPER_CAT_ATTACK, this), (float) damage);
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        LevelAccessor world = this.level();
        Entity sourceentity = source.getEntity();
        if (sourceentity != null) {
            if (!(sourceentity instanceof OceanizedCatEntity)) {
                {
                    final Vec3 center = new Vec3(this.getX(), this.getY(), this.getZ());
                    List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(32 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                    for (Entity entityiterator : entfound) {
                        if (entityiterator instanceof OceanizedCatEntity entity && sourceentity instanceof LivingEntity ent)
                            entity.setTarget(ent);
                    }
                }
            }
        }
        if (source.is(DamageTypes.FALL))
            return false;
        if (source.is(DamageTypes.DROWN))
            return false;
        return super.hurt(source, amount);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        Level world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        double t;
        double angl;
        if (tickCount % 400 == 40) {
            if (EntityUtils.getSeabornAround(world, x, y, z, this) < (world.getLevelData().getGameRules().getInt(CAGameRules.CLONE_NUMBER_LIMIT))) {
                t = Mth.nextInt(RandomSource.create(), 2, 4);
                for (int index0 = 0; index0 < (int) t; index0++) {
                    angl = Mth.nextDouble(RandomSource.create(), 0, 6.283);
                    if (world instanceof ServerLevel level) {
                        Entity entityToSpawn = CAEntities.OCEANIZED_CAT.get().spawn(level, BlockPos.containing(x + 4 * Math.sin(angl), y + 1, z + 4 * Math.cos(angl)), MobSpawnType.MOB_SUMMONED);
                        if (entityToSpawn != null) {
                            entityToSpawn.setYRot(getYRot());
                            entityToSpawn.setYBodyRot(getYRot());
                            entityToSpawn.setYHeadRot(getYRot());
                        }
                    }
                }
            }
        }
        this.refreshDimensions();
    }

    @Override
    public boolean canUsePortal(boolean allowVehicles) {
        return false;
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(CAAttributes.MAGIC_RESISTANCE, 35);
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder = builder.add(Attributes.MAX_HEALTH, 500);
        builder = builder.add(Attributes.ARMOR, 10);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 16);
        builder = builder.add(Attributes.FOLLOW_RANGE, 24);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.isDeadOrDying()) {
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_cat.die"));
        }
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

            ) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_cat.walk"));
            }
            if (this.isShiftKeyDown()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_cat.sneak"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_cat.idle"));
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
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_cat.attack"));
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
        if (this.deathTime == 60) {
            this.remove(RemovalReason.KILLED);
            this.dropExperience(this.getKillCredit());
            LevelAccessor world = this.level();
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();
            double t;
            double angl;
            double yyy;
            double xxx;
            double zzz;
            for (int index0 = 0; index0 < 6; index0++) {
                angl = Mth.nextDouble(RandomSource.create(), 0, 6.283);
                t = Mth.nextDouble(RandomSource.create(), 2, 6);
                xxx = x + t * Math.sin(angl);
                zzz = z + t * Math.cos(angl);
                yyy = WorldUtils.findValidSpawnY(world, x, y, z, xxx, y + 1, zzz);
                if (!Double.isNaN(yyy)) {
                    if (world instanceof ServerLevel level) {
                        Entity entityToSpawn = CAEntities.OCEANIZED_CAT.get().spawn(level, BlockPos.containing(xxx, yyy, zzz), MobSpawnType.MOB_SUMMONED);
                        if (entityToSpawn != null) {
                            entityToSpawn.setYRot(getYRot());
                            entityToSpawn.setYBodyRot(getYRot());
                            entityToSpawn.setYHeadRot(getYRot());
                        }
                    }
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