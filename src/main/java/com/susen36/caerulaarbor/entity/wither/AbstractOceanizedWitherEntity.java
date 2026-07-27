package com.susen36.caerulaarbor.entity.wither;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.init.*;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;


public abstract class AbstractOceanizedWitherEntity extends SeaMonster {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(AbstractOceanizedWitherEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(AbstractOceanizedWitherEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_SKILLP = SynchedEntityData.defineId(AbstractOceanizedWitherEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(AbstractOceanizedWitherEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> DATA_SHELLED = SynchedEntityData.defineId(AbstractOceanizedWitherEntity.class, EntityDataSerializers.BOOLEAN);

    protected boolean swinging;
    protected long lastSwing;
    protected String prevAnim = "empty";
    public String animationprocedure = "empty";
    protected final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.WHITE, ServerBossEvent.BossBarOverlay.NOTCHED_10);

    protected AbstractOceanizedWitherEntity(EntityType<? extends AbstractOceanizedWitherEntity> type, Level world) {
        super(type, world);
        this.xpReward = 512;
        this.setNoAi(false);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(2F);
        this.setPersistenceRequired();
        this.moveControl = new FlyingMoveControl(this, 10, true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHOOT, false);
        builder.define(DATA_ANIMATION, "undefined");
        builder.define(DATA_SKILLP, this.getInitialSkillp());
        builder.define(DATA_DURATION, this.getInitialDuration());
        builder.define(DATA_SHELLED, false);
    }

    protected abstract int getInitialSkillp();

    protected abstract int getInitialDuration();

    protected abstract int getDeathDuration();

    public String getSyncedAnimation() {
        return this.entityData.get(DATA_ANIMATION);
    }

    public void setAnimation(String animation) {
        this.entityData.set(DATA_ANIMATION, animation);
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        return new FlyingPathNavigation(this, world);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public SoundEvent getAmbientSound() {
        return CASounds.OCEAN_WITHER_IDLE.get();
    }

    @Override
    public SoundEvent getHurtSound(DamageSource source) {
        return CASounds.OCEAN_WITHER_HURT.get();
    }

    @Override
    public SoundEvent getDeathSound() {
        return CASounds.OCEAN_WITHER_DIE.get();
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.IN_FIRE)) {
            return false;
        }
        if (source.is(DamageTypes.FALL)) {
            return false;
        }
        if (source.is(DamageTypes.CACTUS)) {
            return false;
        }
        if (source.is(DamageTypes.DROWN)) {
            return false;
        }
        if (source.is(DamageTypes.LIGHTNING_BOLT)) {
            return false;
        }
        if (source.is(DamageTypes.EXPLOSION)) {
            return false;
        }
        if (source.is(DamageTypes.DRAGON_BREATH)) {
            return false;
        }
        if (source.is(DamageTypes.WITHER)) {
            return false;
        }
        if (source.is(DamageTypes.WITHER_SKULL)) {
            return false;
        }
        if (this.entityData.get(DATA_SHELLED) && source.is(DamageTypeTags.IS_PROJECTILE)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    public void applyOceanMagicFollowup(Entity target, Entity directSource) {
        if (!(target instanceof LivingEntity livingTarget) || directSource == null || target == this) {
            return;
        }
        livingTarget.hurt(
                CADamageTypes.source(this.level(), CADamageTypes.OCEAN_MAGIC, directSource, this),
                this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) : 0
        );
        livingTarget.invulnerableTime = 0;
    }

    protected void dealOceanWitherAttack(LivingEntity target, float damage) {
        if (target == null || target == this) {
            return;
        }
        this.applyOceanMagicFollowup(target, this);
        target.hurt(
                CADamageTypes.source(this.level(), CADamageTypes.OCEAN_WITHER, this),
                damage
        );
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && isWitherDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isWitherDurative();
            }
        });
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, IronGolem.class, false, false));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1, 20) {
            @Override
            protected Vec3 getPosition() {
                RandomSource random = AbstractOceanizedWitherEntity.this.getRandom();
                double dirX = AbstractOceanizedWitherEntity.this.getX() + ((random.nextFloat() * 2 - 1) * 16);
                double dirY = AbstractOceanizedWitherEntity.this.getY() + ((random.nextFloat() * 2 - 1) * 16);
                double dirZ = AbstractOceanizedWitherEntity.this.getZ() + ((random.nextFloat() * 2 - 1) * 16);
                return new Vec3(dirX, dirY, dirZ);
            }

            @Override
            public boolean canUse() {
                return super.canUse() && isWitherDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isWitherDurative();
            }
        });
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && isWitherDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isWitherDurative();
            }
        });
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Skillp", this.entityData.get(DATA_SKILLP));
        compound.putInt("Duration", this.entityData.get(DATA_DURATION));
        compound.putBoolean("Shelled", this.entityData.get(DATA_SHELLED));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Skillp")) {
            this.entityData.set(DATA_SKILLP, compound.getInt("Skillp"));
        }
        if (compound.contains("Duration")) {
            this.entityData.set(DATA_DURATION, compound.getInt("Duration"));
        }
        if (compound.contains("Shelled")) {
            this.entityData.set(DATA_SHELLED, compound.getBoolean("Shelled"));
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return super.getDimensions(pose).scale(1F);
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
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    public void setNoGravity(boolean ignored) {
        super.setNoGravity(true);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.setNoGravity(true);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        if (this.isAlive()) {
            int duration = this.entityData.get(DATA_DURATION);
            if (duration > 0) {
                this.entityData.set(DATA_DURATION, duration - 1);
            }
            this.tickSubclassBaseTick(world, x, y, z);
            if (this.tickCount % 20 == 0) {
                this.removeEffect(MobEffects.WITHER);
                this.removeEffect(CAMobEffects.DIZZY);

                Vec3 center = new Vec3(x, y, z);
                List<LivingEntity> nearbyEntities = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(64 / 2D), entity -> true).stream().sorted(Comparator.comparingDouble(candidate -> candidate.distanceToSqr(center)))
                        .toList();
                for (LivingEntity living : nearbyEntities) {
                    if (living.isAlive() && living.hasEffect(MobEffects.WITHER)) {
                        this.dealOceanWitherAttack(
                                living,
                                (float) ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 0.75)
                        );
                        if (world instanceof ServerLevel level) {
                            level.sendParticles(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, living.getX(), living.getY() + 1, living.getZ(), 16, 1, 1, 1, 0.1);
                        }
                    }
                }
            }
            if (this.tickCount % 40 == 0) {
                Vec3 center = new Vec3(x, y, z);
                List<WitherSkull> nearbyEntities = world.getEntitiesOfClass(WitherSkull.class, new AABB(center, center).inflate(72 / 2D), entity -> true).stream().sorted(Comparator.comparingDouble(candidate -> candidate.distanceToSqr(center)))
                        .toList();
                for (WitherSkull skull : nearbyEntities) {
                    if (!skull.level().isClientSide() && EntityUtils.getSpeed(skull) < 0.15) {
                        skull.discard();
                    }
                }
            }
            if (EntityUtils.getSpeed(this) > (this.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED) ? this.getAttribute(Attributes.MOVEMENT_SPEED).getValue() : 0)) {
                this.setDeltaMovement(new Vec3(0, 0, 0));
            }
            if (!this.entityData.get(DATA_SHELLED) && this.shouldEnterShelledState()) {
                if (this.getAttributes().hasAttribute(Attributes.ARMOR)) {
                    this.getAttribute(Attributes.ARMOR).setBaseValue((this.getAttributes().hasAttribute(Attributes.ARMOR) ? this.getAttribute(Attributes.ARMOR).getBaseValue() : 0) * 1.5);
                }
                if (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) {
                    this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : 0) * 1.5);
                }
                if (this.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE)) {
                    this.getAttribute(CAAttributes.GENERAL_DEFENSE).setBaseValue(
                            (this.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE) ? this.getAttribute(CAAttributes.GENERAL_DEFENSE).getBaseValue() : 0) * 1.5
                    );
                }
                this.entityData.set(DATA_SHELLED, true);
            }
        }
        this.refreshDimensions();
    }

    protected abstract void tickSubclassBaseTick(LevelAccessor world, double x, double y, double z);

    protected abstract boolean shouldEnterShelledState();

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == this.getDeathDuration()) {
            this.remove(RemovalReason.KILLED);
            this.dropExperience(this.getKillCredit());
            LevelAccessor world = this.level();
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();
            if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                if (world instanceof ServerLevel level) {
                    ItemEntity entityToSpawn = new ItemEntity(level, x, y, z, new ItemStack(CAItems.MOIST_STAR.get()));
                    entityToSpawn.setPickUpDelay(10);
                    entityToSpawn.setUnlimitedLifetime();
                    level.addFreshEntity(entityToSpawn);
                }
                for (int index0 = 0; index0 < 64; index0++) {
                    if (world instanceof ServerLevel level)
                        level.addFreshEntity(new ExperienceOrb(level, (x + Mth.nextDouble(RandomSource.create(), -1, 1)), y, (z + Mth.nextDouble(RandomSource.create(), -1, 1)), Mth.nextInt(RandomSource.create(), 32, 48)));
                }
            }
            if (this instanceof OceanizedWitherEntity) {
                if (world instanceof ServerLevel level) {
                    Entity entityToSpawn = CAEntities.OCEANIZED_WITHERIA.get().spawn(level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                    if (entityToSpawn != null) {
                        entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                    }
                }
            }
        }
    }

    @Override
    public void setHealth(float health) {
        float currentHealth = this.getHealth();
        float maxHealth = this.getMaxHealth();
        if (this.hasEffect(CAMobEffects.INVULNERABLE) && health < currentHealth) {
            return;
        }
        float reduction = currentHealth - health;
        super.setHealth(reduction >= maxHealth * 0.35F ? currentHealth - maxHealth * 0.35F : currentHealth - reduction);
    }

    public boolean isWitherDurative() {
        return this.isAlive() && this.entityData.get(DATA_DURATION) <= 0;
    }

    public static void shootWitherSkull(LevelAccessor world, Entity from, double acceleration, double dx, double dy, double dz, double inaccuracy, double speed, double x, double y, double z) {
        if (from == null) {
            return;
        }

        double adjustedDx = 0;
        double adjustedDy = 0;
        double adjustedDz = 0;
        double module = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (module > 0) {
            adjustedDx = dx / module * acceleration;
            adjustedDy = dy / module * acceleration;
            adjustedDz = dz / module * acceleration;
        }

        CaerulaArborMod.queueServerWork(Mth.nextInt(RandomSource.create(), 0, 4), () -> {
            if (world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.WITHER_SHOOT, SoundSource.HOSTILE, 0.85F, 1);
            }
        });

        if (world instanceof ServerLevel projectileLevel) {
            Projectile projectile = new WitherSkull(EntityType.WITHER_SKULL, projectileLevel) {
                @Override
                protected void onHitEntity(EntityHitResult result) {
                    Entity target = result.getEntity();
                    Entity owner = this.getOwner();
                    if (owner instanceof AbstractOceanizedWitherEntity oceanizedWither && target instanceof LivingEntity livingTarget && target != owner) {
                        oceanizedWither.applyOceanMagicFollowup(livingTarget, this);
                    }
                    super.onHitEntity(result);
                }
            };
            projectile.setOwner(from);
            ((WitherSkull) projectile).xPower = adjustedDx;
            ((WitherSkull) projectile).yPower = adjustedDy;
            ((WitherSkull) projectile).zPower = adjustedDz;
            projectile.setPos(x, y, z);
            projectile.shoot(dx, dy, dz, (float) speed, (float) inaccuracy);
            projectileLevel.addFreshEntity(projectile);
        }
    }
}