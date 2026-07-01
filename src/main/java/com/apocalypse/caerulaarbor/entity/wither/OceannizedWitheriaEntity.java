package com.apocalypse.caerulaarbor.entity.wither;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CAAttributes;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import com.apocalypse.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

public class OceannizedWitheriaEntity extends AbstractOceanizedWitherEntity {
    public static final EntityDataAccessor<Integer> DATA_idle_time = SynchedEntityData.defineId(OceannizedWitheriaEntity.class, EntityDataSerializers.INT);

    public OceannizedWitheriaEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(CAEntities.OCEANIZED_WITHERIA.get(), world);
    }

    public OceannizedWitheriaEntity(EntityType<OceannizedWitheriaEntity> type, Level world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_idle_time, 0);
    }

    @Override
    protected String getDefaultTexture() {
        return "oceanized_witheria";
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
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, SnowGolem.class, false, false));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1, 20) {
            @Override
            protected Vec3 getPosition() {
                RandomSource random = OceannizedWitheriaEntity.this.getRandom();
                double dir_x = OceannizedWitheriaEntity.this.getX() + ((random.nextFloat() * 2 - 1) * 16);
                double dir_y = OceannizedWitheriaEntity.this.getY() + ((random.nextFloat() * 2 - 1) * 16);
                double dir_z = OceannizedWitheriaEntity.this.getZ() + ((random.nextFloat() * 2 - 1) * 16);
                return new Vec3(dir_x, dir_y, dir_z);
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
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
        SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
        if (this.getAttributes().hasAttribute(CAAttributes.SANITY_RATE.get()))
            this.getAttribute(CAAttributes.SANITY_RATE.get()).setBaseValue(10);
        if (this.getAttributes().hasAttribute(CAAttributes.SANITY_MODIFIER.get()))
            this.getAttribute(CAAttributes.SANITY_MODIFIER.get()).setBaseValue(0.01);
        if (this.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get()))
            this.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).setBaseValue(65);
        if (this.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE.get()))
            this.getAttribute(CAAttributes.GENERAL_DEFENSE.get()).setBaseValue(5);
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
        compound.putInt("Dataidle_time", this.entityData.get(DATA_idle_time));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Dataidle_time"))
            this.entityData.set(DATA_idle_time, compound.getInt("Dataidle_time"));
    }

    @Override
    protected void tickSubclassBaseTick(LevelAccessor world, double x, double y, double z) {
        Entity enemy = null;
        double skillp;
        double idle = 0;
        skillp = this.entityData.get(DATA_skillp);
        if ((this.getDisplayName().getString()).equals(this.getType().getDescription().getString())) {
            enemy = this.getTarget();
            idle = this.entityData.get(DATA_idle_time);
            if (enemy == null || !enemy.isAlive()) {
                this.entityData.set(DATA_idle_time, (int) (idle + 1));
            } else {
                this.entityData.set(DATA_idle_time, 0);
            }
        }
        if (skillp > 0) {
            this.entityData.set(DATA_skillp, (int) (skillp - 1));
        } else if (enemy != null && enemy.isAlive()) {
            this.entityData.set(DATA_duration, 65);
            this.entityData.set(DATA_skillp, 400);
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
                    if (this.entityData.get(DATA_duration) <= 0) {
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
            WorldUtils.witheriaDestroyBlocks(world, x, y, z);
        }
        if (idle > 1800) {
            this.entityData.set(DATA_skillp, 1800);
            this.entityData.set(DATA_duration, 1800);
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

    @Override
    protected boolean shouldEnterShelledState() {
        return this.getHealth() < this.getMaxHealth() * 0.5;
    }

    @Override
    protected String getShelledTexture() {
        return "oceanized_witheria_anger";
    }

    private void purchaseEnemy() {
        Entity enemy = this.getTarget();
        if (enemy != null && enemy.isAlive() && this.distanceTo(enemy) > 4) {
            this.teleportTo(enemy.getX(), enemy.getY(), enemy.getZ());
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
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanzied_witheria.die"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanzied_witheria.idle"));
        }
        return PlayState.STOP;
    }

    private PlayState attackingPredicate(AnimationState event) {
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
    public void remove(RemovalReason pReason) {
        if (this.getEntityData().get(DATA_duration) > 999) super.remove(pReason);
        if (this.level().getDifficulty() != Difficulty.PEACEFUL && pReason == RemovalReason.DISCARDED) {
            this.hurt(
                    new DamageSource(
                            this.level().registryAccess().
                                    registryOrThrow(Registries.DAMAGE_TYPE).
                                    getHolderOrThrow(
                                            ResourceKey.create(
                                                    Registries.DAMAGE_TYPE,
                                                    new ResourceLocation(CaerulaArborMod.MODID, "oceankiller_damage")
                                            )
                                    )
                    ),
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
        final Vec3 _center = new Vec3(this.getX(), this.getY(), this.getZ());
        List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(8 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
        for (Entity entityiterator : _entfound) {
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
                if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
                    _entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 80, 0));
                entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "ocean_wither"))), this),
                        (float) damage);
            }
        }
        if (!world.isClientSide()) {
            world.playSound(null, BlockPos.containing(this.getX(), this.getY(), this.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "wither_scythe_pre")), SoundSource.HOSTILE, 12, 1);
        } else {
            world.playLocalSound(this.getX(), this.getY(), this.getZ(), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "wither_scythe_pre")), SoundSource.HOSTILE, 12, 1, false);
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
