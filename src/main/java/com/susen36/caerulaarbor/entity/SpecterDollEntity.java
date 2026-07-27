package com.susen36.caerulaarbor.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.base.SyncedAnimationEntity;
import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAParticles;
import com.susen36.caerulaarbor.init.CASounds;
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
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

public class SpecterDollEntity extends Animal implements GeoEntity, SyncedAnimationEntity {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(SpecterDollEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(SpecterDollEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";

    public SpecterDollEntity(Level world) {
        this(CAEntities.SPECTER_DOLL.get(), world);
    }

    public SpecterDollEntity(EntityType<SpecterDollEntity> type, Level world) {
        super(type, world);
        xpReward = 0;
        setNoAi(false);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(0.6f);
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
        this.goalSelector.addGoal(1, new FloatGoal(this));
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return CASounds.SPECTER_HIT.get();
    }

    @Override
    public SoundEvent getDeathSound() {
        return CASounds.SPECTER_DOLL_DIE.get();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.FALL))
            return false;
        if (source.is(DamageTypes.DROWN))
            return false;
        return super.hurt(source, amount);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata);
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        if ((LevelAccessor) world instanceof Level level) {
            level.playSound(null, BlockPos.containing(x, y, z), CASounds.SPECTER_DOLL_AMBIENT.get(), SoundSource.NEUTRAL, 3, 1);
        }
        if ((LevelAccessor) world instanceof ServerLevel level)
            level.sendParticles(CAParticles.SPECTER_GLITTER.get(), x, (y + 0.75), z, 64, 0.75, 0.75, 0.75, 0.1);
        return retval;
    }


    @Override
    public void baseTick() {
        super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        double tickCount1;
        if (this.isAlive()) {
            GladiiaEntity.healFromGladiia(world, x, y, z, this);
            setDeltaMovement(new Vec3(0, (getDeltaMovement().y()), 0));
            tickCount1 = tickCount;
            this.setHealth((float) (this.getHealth() + this.getMaxHealth() * 0.0005));
            if (tickCount1 > 20 && tickCount1 < 200) {
                if (tickCount1 % 20 == 0) {
                    Entity target;
                    double damage;
                    double r;
                    target = this.getTarget();
                    r = 6;
                    damage = (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 0.8;
                    {
                        final Vec3 center = new Vec3(x, y, z);
                        List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(12 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                        for (Entity entityiterator : entfound) {
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
                            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "is_humanside")))) {
                                if (!(entityiterator == target)) {
                                    continue;
                                }
                            }
                            if (entityiterator == this) {
                                continue;
                            }
                            double result;
                            result = Math.abs(getX() - entityiterator.getX()) + Math.abs(getZ() - entityiterator.getZ());
                            if (result <= r) {
                                invulnerableTime = 0;
                                entityiterator.hurt(this.damageSources().magic(), (float) damage);
                                if (!this.level().isClientSide())
                                    this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 2));
                            }
                        }
                    }
                }
                this.spawnDiamondParticle(tickCount1 % 20 + 1);
            }
            if (tickCount1 >= 220) {
                if (!level().isClientSide())
                    discard();
                if (world instanceof ServerLevel level)
                    level.sendParticles(CAParticles.SPECTER_GLITTER.get(), x, (y + 0.75), z, 64, 0.75, 0.75, 0.75, 0.1);
                if (world instanceof ServerLevel level) {
                    Entity entityToSpawn = CAEntities.SPECTER.get().spawn(level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                    if (entityToSpawn != null) {
                        entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                    }
                }
            }
        }
        this.refreshDimensions();
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageable) {
        SpecterDollEntity retval = CAEntities.SPECTER_DOLL.get().create(serverWorld);
        retval.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(retval.blockPosition()), MobSpawnType.BREEDING, null);;
        return retval;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.updateSwingTime();
    }


    private void spawnDiamondParticle(double t) {
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();

        double R = t * 0.9;
        for (int index0 = 0; index0 < (int) (R + 1); index0++) {
            double dx = index0 * 0.5;
            double dz = (R - index0) * 0.5;
            world.addParticle(CAParticles.SPECTER_GLITTER.get(), (x + dx), (y + 0.1), (z + dz), 0, 0.1, 0);
            world.addParticle(CAParticles.SPECTER_GLITTER.get(), (x - dx), (y + 0.1), (z + dz), 0, 0.1, 0);
            world.addParticle(CAParticles.SPECTER_GLITTER.get(), (x + dx), (y + 0.1), (z - dz), 0, 0.1, 0);
            world.addParticle(CAParticles.SPECTER_GLITTER.get(), (x - dx), (y + 0.1), (z - dz), 0, 0.1, 0);
        }
        if (Math.random() < 0.15) {
            R = Mth.nextDouble(RandomSource.create(), 1, 8);
            double tt = Mth.nextDouble(RandomSource.create(), 0, 6.283);
            world.addParticle(CAParticles.SPECTER_CHARS.get(), (x + R * Math.cos(tt)), (y + 0.2), (z + R * Math.sin(tt)), 0, 0.25, 0);
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0);
        builder = builder.add(Attributes.MAX_HEALTH, 218);
        builder = builder.add(Attributes.ARMOR, 7);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 34);
        builder = builder.add(Attributes.FOLLOW_RANGE, 32);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
        builder = builder.add(CAAttributes.MAGIC_RESISTANCE, 50);
        builder = builder.add(CAAttributes.SANITY_MODIFIER, 0.33);
        builder = builder.add(CAAttributes.MISSRATE, 18);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.specter_doll.die"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.specter_doll.idle"));
        }
        return PlayState.STOP;
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
        data.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
        data.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}