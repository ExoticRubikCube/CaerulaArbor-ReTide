package com.susen36.caerulaarbor.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.base.SyncedAnimationEntity;
import com.susen36.caerulaarbor.init.*;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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
import java.util.List;

public class SkadiEntity extends Animal implements GeoEntity, SyncedAnimationEntity {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(SkadiEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(SkadiEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_RELAX_COOLDOWN = SynchedEntityData.defineId(SkadiEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_SKILLP = SynchedEntityData.defineId(SkadiEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_PHASE = SynchedEntityData.defineId(SkadiEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_SKILLP_2 = SynchedEntityData.defineId(SkadiEntity.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";
    @Nullable
    private DamageSource lastDamageSource;

    public SkadiEntity(Level world) {
        this(CAEntities.SKADI.get(), world);
    }

    public SkadiEntity(EntityType<SkadiEntity> type, Level world) {
        super(type, world);
        xpReward = 0;
        setNoAi(false);
        setMaxUpStep(1.2f);
        setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SHOOT, false);
        this.entityData.define(DATA_ANIMATION, "undefined");
        this.entityData.define(DATA_RELAX_COOLDOWN, 200);
        this.entityData.define(DATA_SKILLP, 120);
        this.entityData.define(DATA_PHASE, 0);
        this.entityData.define(DATA_SKILLP_2, 0);
    }


    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.15, false) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return 9;
            }

            @Override
            public boolean canUse() {
                if (!super.canUse()) return false;
                return hasEffect(CAMobEffects.FAKE_DEATH.get());
            }

            @Override
            public boolean canContinueToUse() {
                if (!super.canContinueToUse()) return false;
                return hasEffect(CAMobEffects.FAKE_DEATH.get());
            }

        });
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Monster.class, true, false));
        this.goalSelector.addGoal(4, new OpenDoorGoal(this, false));
        this.goalSelector.addGoal(5, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(6, new RandomStrollGoal(this, 1));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new FloatGoal(this));
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return CASounds.SKADI_HIT.get();
    }

    @Override
    public SoundEvent getDeathSound() {
        return CASounds.SKADI_DIED.get();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        this.lastDamageSource = source;
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity sourceentity = source.getEntity();
        if (sourceentity != null) {
            double sklp;
            if (!new Object() {
                public boolean checkGamemode(Entity ent) {
                    if (ent instanceof ServerPlayer serverPlayer) {
                        return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                    } else if (ent.level().isClientSide() && ent instanceof Player player) {
                        return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                    }
                    return false;
                }
            }.checkGamemode(sourceentity)) {
                boolean result;
                result = hasEffect(CAMobEffects.FAKE_DEATH.get());
                if (result) {
                    sklp = (Entity) this instanceof SkadiEntity datEntI ? datEntI.getEntityData().get(DATA_SKILLP_2) : 0;
                    if (sklp <= 0 && this.isAlive()) {
                        if (distanceTo(sourceentity) <= 5) {
                            if ((Entity) this instanceof SkadiEntity datEntSetI)
                                datEntSetI.getEntityData().set(DATA_SKILLP_2, 120);
                            if (this instanceof SkadiEntity) {
                                this.setAnimation("animation.skadi.skill");
                            }
                            ((Entity) this).lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((sourceentity.getX()), (sourceentity.getY()), (sourceentity.getZ())));
                            if (!this.level().isClientSide())
                                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 9, false, false));
                            CaerulaArborMod.queueServerWork(16, () -> {
                                if (this.isAlive()) {
                                    if (distanceTo(sourceentity) <= 5) {
                                        sourceentity.hurt(
                                                CADamageTypes.source(world, CADamageTypes.HUNTER_ATTACK, this), (float) ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0)
                                                        * 2.5));
                                        if (sourceentity instanceof LivingEntity && !this.level().isClientSide())
                                            this.addEffect(new MobEffectInstance(CAMobEffects.DIZZY.get(), 100, 0, false, false));
                                        sourceentity.push((getLookAngle().x + 0.33), 0, (getLookAngle().z + 0.33));
                                    }
                                    if (world instanceof Level level) {
                                        level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.HOSTILE, 2, 1);
                                    }
                                    double ddd;
                                    ddd = (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 2.25;
                                    if (sourceentity instanceof LivingEntity && !sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "is_humanside")))
                                            && distanceTo(sourceentity) <= 3) {
                                        sourceentity.hurt(CADamageTypes.source(world, CADamageTypes.HUNTER_ATTACK, this), (float) ddd);
                                        if (!this.level().isClientSide())
                                            this.addEffect(new MobEffectInstance(CAMobEffects.DIZZY.get(), 100, 0, false, false));
                                        sourceentity.push((getLookAngle().x + 0.33), 0, (getLookAngle().z + 0.33));
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }
        if (source.is(DamageTypes.DROWN))
            return false;
        return super.hurt(source, amount);
    }

    @Override
    public void setHealth(float pHealth) {
        float currentHealth = this.getHealth();
        if (pHealth <= 0) {
            ResourceKey<net.minecraft.world.damagesource.DamageType> cursedDamage = CADamageTypes.ISHARMLA_CURSED;
            if (this.lastDamageSource == null || !this.lastDamageSource.is(cursedDamage)) {
                int phase = this.getEntityData().get(DATA_PHASE);
                if (phase == 0 || phase == 1) {
                    super.setHealth(Math.max(currentHealth, 1.0F));
                    this.getEntityData().set(DATA_PHASE, phase + 1);
                    this.level().playSound(null, BlockPos.containing(this.getX(), this.getY(), this.getZ()), CASounds.SKADI_TALK.get(), SoundSource.HOSTILE, 2, 1);

                    if (!this.level().isClientSide()) {
                        this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 100, 1, false, false));
                        this.addEffect(new MobEffectInstance(CAMobEffects.FAKE_DEATH.get(), 100, 3, false, false));
                        if (phase == 0) {
                            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 131071, 1, false, true));
                        }
                    }
                    if (this.getAttributes().hasAttribute(Attributes.MAX_HEALTH)) {
                        double scaledMaxHealth = this.getAttribute(Attributes.MAX_HEALTH).getBaseValue() * (phase == 0 ? 0.75 : 0.8);
                        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(scaledMaxHealth);
                    }
                    if (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) {
                        double scaledAttackDamage = this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() * (phase == 0 ? 1.5 : 1.25);
                        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(scaledAttackDamage);
                    }
                    if (this.getAttributes().hasAttribute(Attributes.ARMOR)) {
                        this.getAttribute(Attributes.ARMOR).setBaseValue(this.getAttribute(Attributes.ARMOR).getBaseValue() * 2);
                    }
                    super.setHealth(Math.min(currentHealth, this.getMaxHealth()));
                    return;
                }
            }
        }
        super.setHealth(pHealth);
    }

    @Override
    public void die(DamageSource source) {
        ResourceKey<net.minecraft.world.damagesource.DamageType> cursedDamage = CADamageTypes.ISHARMLA_CURSED;
        if (source.is(cursedDamage)) {
            for (Entity nearbyPlayer : this.level().players()) {
                if (this.distanceTo(nearbyPlayer) < 32 && nearbyPlayer instanceof Player player && !player.level().isClientSide()) {
                    player.displayClientMessage(Component.literal(Component.translatable("entity.caerula_arbor.skadi_corrupted.start").getString()), false);
                }
            }
            if (!this.level().isClientSide()) {
                this.discard();
            }
            if (this.level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY() + 1, this.getZ(), 5, 0, 0, 0, 0.1);
                Entity corruptedSkadi = CAEntities.SKADI_CORRUPTED.get().spawn(level, BlockPos.containing(this.getX(), this.getY(), this.getZ()), MobSpawnType.MOB_SUMMONED);
                if (corruptedSkadi != null) {
                    corruptedSkadi.setYRot(this.level().getRandom().nextFloat() * 360F);
                }
            }
            return;
        }
        super.die(source);
    }

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("RelaxCooldown", this.entityData.get(DATA_RELAX_COOLDOWN));
        compound.putInt("Skillp", this.entityData.get(DATA_SKILLP));
        compound.putInt("Phase", this.entityData.get(DATA_PHASE));
        compound.putInt("Skillp2", this.entityData.get(DATA_SKILLP_2));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("RelaxCooldown")) {
            this.entityData.set(DATA_RELAX_COOLDOWN, compound.getInt("RelaxCooldown"));
        }
        if (compound.contains("Skillp")) {
            this.entityData.set(DATA_SKILLP, compound.getInt("Skillp"));
        }
        if (compound.contains("Phase")) {
            this.entityData.set(DATA_PHASE, compound.getInt("Phase"));
        }
        if (compound.contains("Skillp2")) {
            this.entityData.set(DATA_SKILLP_2, compound.getInt("Skillp2"));
        }
	}

    @Override
    public void baseTick() {
        super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        double rlx = 0;
        double sklp;
        double sklp2;
        if (this.isAlive()) {
            if (Math.random() < 0.001) {
                rlx = (Entity) this instanceof SkadiEntity datEntI ? datEntI.getEntityData().get(DATA_RELAX_COOLDOWN) : 0;
                if (rlx <= 0) {
                    if (!((Entity) this instanceof Mob mobEnt2 && mobEnt2.isAggressive())) {
                        if ((Entity) this instanceof Mob entity)
                            entity.getNavigation().stop();
                        if (this instanceof SkadiEntity) {
                            this.setAnimation("animation.skadi.relax");
                        }
                        rlx = 320;
                    }
                }
            }
            if (rlx > 0) {
                rlx = rlx - 1;
            }
            sklp = (Entity) this instanceof SkadiEntity datEntI ? datEntI.getEntityData().get(DATA_SKILLP) : 0;
            sklp2 = (Entity) this instanceof SkadiEntity datEntI ? datEntI.getEntityData().get(DATA_SKILLP_2) : 0;
            if (sklp <= 0) {
                if (!(((Entity) this instanceof Mob mobEnt ? (Entity) mobEnt.getTarget() : null) == null)) {
                    if ((((Entity) this instanceof Mob mobEnt ? (Entity) mobEnt.getTarget() : null) != null ? distanceTo(((Entity) this instanceof Mob mobEnt ? mobEnt.getTarget() : null)) : -1) < 3) {
                        if (this instanceof SkadiEntity) {
                            this.setAnimation("animation.skadi.spin");
                        }
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 40, 0, false, false));
                        CaerulaArborMod.queueServerWork(10, () -> {
                            spinAttack(1.5);
                        });
                        CaerulaArborMod.queueServerWork(14, () -> {
                            spinAttack(2);
                        });
                        CaerulaArborMod.queueServerWork(20, () -> {
                            spinAttack(1.5);
                        });
                        if (SpecterEntity.isSpecterAround(world, x, y, z)) {
                            sklp = 170;
                        } else {
                            sklp = 200;
                        }
                    }
                }
            } else {
                sklp = sklp - 1;
            }
            if (sklp2 > 0) {
                if ((Entity) this instanceof SkadiEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_SKILLP_2, (int) (sklp2 - 1));
            }
            if ((Entity) this instanceof SkadiEntity datEntSetI)
                datEntSetI.getEntityData().set(DATA_RELAX_COOLDOWN, (int) rlx);
            if ((Entity) this instanceof SkadiEntity datEntSetI)
                datEntSetI.getEntityData().set(DATA_SKILLP, (int) sklp);
            GladiiaEntity.healFromGladiia(world, x, y, z, this);
            if (tickCount % 10 == 0) {
                final Vec3 center = new Vec3(x, y, z);
                    TagKey<EntityType<?>> huntersTag = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "hunters"));
                    List<LivingEntity> nearbyHunters = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(24),
                            e -> e.isAlive() && e.getType().is(huntersTag));
                    for (LivingEntity entityiterator : nearbyHunters) {
                        if (!(entityiterator.hasEffect(CAMobEffects.ADD_ATTACK_PERCLY.get()))) {
                            if (!this.level().isClientSide())
                                this.addEffect(new MobEffectInstance(CAMobEffects.ADD_ATTACK_PERCLY.get(), -1, 0, false, false));
                            break;
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
        SkadiEntity retval = CAEntities.SKADI.get().create(serverWorld);
        retval.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(retval.blockPosition()), MobSpawnType.BREEDING, null, null);
        return retval;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.updateSwingTime();
    }

    public void spinAttack(double damageMultiplier) {
        Entity target = this.getTarget();
        double damage = (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttributeValue(Attributes.ATTACK_DAMAGE) : 0) * damageMultiplier;
        Vec3 center = new Vec3(this.getX(), this.getY(), this.getZ());
        TagKey<EntityType<?>> humanSideTag = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "is_humanside"));
        List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(3),
                entity -> entity.isAlive()
                        && ((entity == target || (entity instanceof Mob mobEnt && mobEnt.getTarget() == this))
                        || (entity instanceof Monster && !entity.getType().is(humanSideTag))));
        for (LivingEntity entity : entities) {
            if (this.distanceToSqr(entity) < 12.25) {
                entity.hurt(CADamageTypes.source(this.level(), CADamageTypes.HUNTER_ATTACK, this), (float) damage);
            }
        }
    }


    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.18);
        builder = builder.add(ForgeMod.SWIM_SPEED.get(), 8);
        builder = builder.add(CAAttributes.SANITY_MODIFIER.get(), 0.33);
        builder = builder.add(Attributes.MAX_HEALTH, 270);
        builder = builder.add(Attributes.ARMOR, 5);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 38);
        builder = builder.add(Attributes.FOLLOW_RANGE, 32);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
        return builder;
    }

    private PlayState movementPredicate(AnimationState<?> event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

                    && !this.isAggressive()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.skadi.move"));
            }
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.skadi.die"));
            }
            if (this.isAggressive() && event.isMoving()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.skadi.sprint"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.skadi.idle"));
        }
        return PlayState.STOP;
    }

    private PlayState attackingPredicate(AnimationState<?> event) {
        double d1 = this.getX() - this.xOld;
        double d0 = this.getZ() - this.zOld;
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
            this.swinging = true;
            this.lastSwing = level().getGameTime();
        }
        if (this.swinging && this.lastSwing + 20L <= level().getGameTime()) {
            this.swinging = false;
        }
        if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.skadi.attack"));
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


    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}
