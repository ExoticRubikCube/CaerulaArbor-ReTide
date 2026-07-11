package com.apocalypse.caerulaarbor.entity.wither;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.*;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import com.apocalypse.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

public class OceannizedWitheriaEntity extends AbstractOceanizedWitherEntity {
    public static final EntityDataAccessor<Integer> DATA_IDLE_TIME = SynchedEntityData.defineId(OceannizedWitheriaEntity.class, EntityDataSerializers.INT);

    public OceannizedWitheriaEntity(Level world) {
        this(CAEntities.OCEANIZED_WITHERIA.get(), world);
    }

    public OceannizedWitheriaEntity(EntityType<OceannizedWitheriaEntity> type, Level world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IDLE_TIME, 0);
    }

    @Override
    protected int getInitialSkillp() {
        return 100;
    }

    @Override
    protected int getInitialDuration() {
        return 95;
    }

    @Override
    protected int getDeathDuration() {
        return 60;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.25, false) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return 12.25;
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
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, SnowGolem.class, false, false));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
        SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
        if (!this.level().isClientSide())
            this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 95, 9, false, false));
        if (this instanceof OceannizedWitheriaEntity) {
            this.setAnimation("animation.oceanzied_witheria.start");
        }
        return retval;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("IdleTime", this.entityData.get(DATA_IDLE_TIME));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("IdleTime")) {
            this.entityData.set(DATA_IDLE_TIME, compound.getInt("IdleTime"));
        }
    }

    @Override
    protected void tickSubclassBaseTick(LevelAccessor world, double x, double y, double z) {
        Entity target = null;
        double skillp;
        double idle = 0;
        skillp = this.entityData.get(DATA_SKILLP);
        if ((this.getDisplayName().getString()).equals(this.getType().getDescription().getString())) {
            target = this.getTarget();
            idle = this.entityData.get(DATA_IDLE_TIME);
            if (target == null || !target.isAlive()) {
                this.entityData.set(DATA_IDLE_TIME, (int) (idle + 1));
            } else {
                this.entityData.set(DATA_IDLE_TIME, 0);
            }
        }
        if (skillp > 0) {
            this.entityData.set(DATA_SKILLP, (int) (skillp - 1));
        } else if (target != null && target.isAlive()) {
            this.entityData.set(DATA_DURATION, 65);
            this.entityData.set(DATA_SKILLP, 400);
            if (!this.level().isClientSide()) {
                this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 65, 0, false, false));
            }
            this.setAnimation("animation.oceanzied_witheria.skill");
            CaerulaArborMod.queueServerWork(20, () -> {
                if (this.isAlive()) {
                    timedLoop(0, 15, 1);
                }
            });
            CaerulaArborMod.queueServerWork(27, () -> {
                if (this.isAlive()) {
                    this.purchaseEnemy();
                    this.witheriaSweep(-2, 0.75);
                }
            });
            CaerulaArborMod.queueServerWork(30, () -> {
                if (this.isAlive()) {
                    this.purchaseEnemy();
                    if (this.entityData.get(DATA_DURATION) <= 0) {
                        Entity enemy1 = this.getTarget();
                        if (enemy1 != null && enemy1.isAlive()) {
                            this.shootWitheriaTo(enemy1);
                            Entity otherOne = EntityUtils.getNearestEnemy(world, x, y, z, enemy1, enemy1, this);
                            if (otherOne == null || !otherOne.isAlive()) {
                                otherOne = enemy1;
                            }
                            this.shootWitheriaTo(otherOne);
                            Entity otherTwo = EntityUtils.getNearestEnemy(world, x, y, z, enemy1, otherOne, this);
                            if (otherTwo == null || !otherTwo.isAlive()) {
                                otherTwo = enemy1;
                            }
                            this.shootWitheriaTo(otherTwo);
                        }
                    }
                    this.witheriaSweep(-2, 0.75);
                }
            });
            CaerulaArborMod.queueServerWork(48, () -> {
                if (this.isAlive()) {
                    this.purchaseEnemy();
                    this.witheriaSweep(0, 1.5);
                }
            });
        }
        if (this.tickCount % 10 == 0) {
            this.witheriaDestroyBlocks();
        }
        if (idle > 1800) {
            this.entityData.set(DATA_SKILLP, 1800);
            this.entityData.set(DATA_DURATION, 1800);
            if (!this.level().isClientSide()) {
                this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 999, 9, false, false));
            }
            this.setAnimation("animation.oceanzied_witheria.byebye");
            CaerulaArborMod.queueServerWork(100, () -> {
                if (!this.level().isClientSide()) {
                    this.discard();
                }
            });
        }
    }

    private void witheriaDestroyBlocks() {
        LevelAccessor world = this.level();
        if (!WorldUtils.canGrief(world)) {
            return;
        }
        boolean once = false;
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        BlockPos originPos = this.blockPosition();
        double dx = -1;
        for (int index0 = 0; index0 < 3; index0++) {
            double dz = -1;
            for (int index1 = 0; index1 < 3; index1++) {
                double dy = 0;
                for (int index2 = 0; index2 < 2; index2++) {
                    BlockPos blockPos = BlockPos.containing(x + dx, y + dy, z + dz);
                    BlockState block = world.getBlockState(blockPos);
                    if (!block.is(BlockTags.create(ResourceLocation.parse("minecraft:wither_immnue")))) {
                        double hardness = block.getDestroySpeed(world, BlockPos.containing(0, 0, 0));
                        if (hardness <= 7.5 && hardness >= 0 && world.getBlockFloorHeight(blockPos) > 0) {
                            Block.dropResources(world.getBlockState(blockPos), world, originPos, null);
                            world.destroyBlock(blockPos, false);
                            if (world instanceof Level level) {
                                level.updateNeighborsAt(blockPos, level.getBlockState(blockPos).getBlock());
                            }
                            once = true;
                        }
                    }
                    dy = dy + 1;
                }
                dz = dz + 1;
            }
            dx = dx + 1;
        }
        if (once && world instanceof Level level) {
            if (!level.isClientSide()) {
                level.playSound(null, originPos, SoundEvents.WITHER_BREAK_BLOCK, SoundSource.NEUTRAL, 1, 1);
            } else {
                level.playLocalSound(x, y, z, SoundEvents.WITHER_BREAK_BLOCK, SoundSource.NEUTRAL, 1, 1, false);
            }
        }
    }

    @Override
    protected boolean shouldEnterShelledState() {
        return this.getHealth() < this.getMaxHealth() * 0.5;
    }

    private void purchaseEnemy() {
        Entity target = this.getTarget();
        if (target != null && target.isAlive() && this.distanceTo(target) > 4) {
            this.teleportTo(target.getX(), target.getY(), target.getZ());
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.45);
        builder = builder.add(Attributes.MAX_HEALTH, 600);
        builder = builder.add(Attributes.ARMOR, 15);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 24);
        builder = builder.add(Attributes.FOLLOW_RANGE, 48);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
        builder = builder.add(Attributes.FLYING_SPEED, 0.45);
        builder = builder.add(CAAttributes.SANITY_RATE.get(), 10);
        builder = builder.add(CAAttributes.SANITY_MODIFIER.get(), 0.01);
        builder = builder.add(CAAttributes.MAGIC_RESISTANCE.get(), 65);
        builder = builder.add(CAAttributes.GENERAL_DEFENSE.get(), 5);
        return builder;
    }

    private PlayState movementPredicate(AnimationState<?> event) {
        if (this.animationprocedure.equals("empty")) {
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanzied_witheria.die"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanzied_witheria.idle"));
        }
        return PlayState.STOP;
    }

    private PlayState attackingPredicate(AnimationState<?> event) {
        double d1 = this.getX() - this.xOld;
        double d0 = this.getZ() - this.zOld;
        float velocity = (float) Math.sqrt(d1 * d1 + d0 * d0);
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
            this.swinging = true;
            this.lastSwing = level().getGameTime();
        }
        if (this.swinging && this.lastSwing + 19L <= level().getGameTime()) {
            this.swinging = false;
        }
        if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanzied_witheria.attack"));
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
    public void remove(RemovalReason pReason) {
        if (this.getEntityData().get(DATA_DURATION) > 999) super.remove(pReason);
        if (this.level().getDifficulty() != Difficulty.PEACEFUL && pReason == RemovalReason.DISCARDED) {
            this.hurt(
                    CADamageTypes.source(this.level(), CADamageTypes.OCEANKILLER_DAMAGE),
                    20
            );
            return;
        }
        super.remove(pReason);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
        data.add(new AnimationController<>(this, "attacking", 0, this::attackingPredicate));
        data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
    }

    private void witheriaSweep(double cosine, double rate) {
        Level world = this.level();
        double damage;
        Entity target = this.getTarget();
        final Vec3 center = new Vec3(this.getX(), this.getY(), this.getZ());
        List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(8 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
        for (Entity entityiterator : entfound) {
            if (!(entityiterator instanceof LivingEntity)) {
                continue;
            }
            if (entityiterator == this) {
                continue;
            }
            if (entityiterator.getType().is(EntityUtils.OCEAN_OFFSPRING)) {
                if (!(entityiterator == target)) {
                    continue;
                }
            }
            if (this.distanceTo(entityiterator) <= 4 && (EntityUtils.getEntityCosine(this, entityiterator) >= cosine || entityiterator == target)) {
                damage = (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * rate;
                if (entityiterator instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.WITHER, 80, 0));
                this.dealOceanWitherAttack((LivingEntity) entityiterator, (float) damage);
            }
        }
        if (!world.isClientSide()) {
            world.playSound(null, BlockPos.containing(this.getX(), this.getY(), this.getZ()), CASounds.WITHER_SCYTHE_PRE.get(), SoundSource.HOSTILE, 12, 1);
        } else {
            world.playLocalSound(this.getX(), this.getY(), this.getZ(), CASounds.WITHER_SCYTHE_PRE.get(), SoundSource.HOSTILE, 12, 1, false);
        }
    }

    private void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
        for (int j = 1; j <= 4; j++) {
            double angleOffset = 12 * j * timedloopiterator;
            angledShot(this.getYRot() + angleOffset);
        }
        final int nextDelay = ticks;
        final int nextIterator = timedloopiterator + 1;
        CaerulaArborMod.queueServerWork(nextDelay, () -> {
            if (this.isAlive() && timedlooptotal > nextIterator) {
                timedLoop(nextIterator, timedlooptotal, nextDelay);
            }
        });
    }

    private void angledShot(double angle) {
        LevelAccessor world = this.level();
        double dist = Mth.nextDouble(RandomSource.create(), 1.25, 5);
        shootWitherSkull(world, this, 0.1, Math.cos(Math.toRadians(angle + 90)), Mth.nextDouble(RandomSource.create(), -0.5, 0.5), Math.sin(Math.toRadians(angle + 90)), 5, 0.35,
                this.getX() + dist * Math.cos(Math.toRadians(angle)), this.getY() + Mth.nextDouble(RandomSource.create(), 1.25, 4.25), this.getZ() + dist * Math.sin(Math.toRadians(angle)));
    }

    private void shootWitheriaTo(Entity tgt) {
        if (tgt == null)
            return;
        double vx = tgt.getX() - this.getX();
        double vy = (tgt.getY() + tgt.getBbHeight() * 0.5) - (this.getY() + 1.5);
        double vz = tgt.getZ() - this.getZ();
        shootWitherSkull(this.level(), this, 0.1, vx, vy, vz, 1, Mth.nextDouble(RandomSource.create(), 0.42, 0.56), this.getX(), this.getY() + 1.5, this.getZ());
    }

    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}
