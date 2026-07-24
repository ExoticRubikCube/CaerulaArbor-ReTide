package com.susen36.caerulaarbor.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.api.event.SanityEvent;
import com.susen36.caerulaarbor.capability.sanity.SIHelper;
import com.susen36.caerulaarbor.entity.base.SyncedAnimationEntity;
import com.susen36.caerulaarbor.entity.enderdragon.OceanizedEnderinaEntity;
import com.susen36.caerulaarbor.init.CADamageTypes;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAParticles;
import com.susen36.caerulaarbor.init.CASounds;
import net.minecraft.commands.arguments.EntityAnchorArgument;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class MoistDragonBreathEntity extends PathfinderMob implements GeoEntity, SyncedAnimationEntity {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(MoistDragonBreathEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(MoistDragonBreathEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> DATA_TARGET = SynchedEntityData.defineId(MoistDragonBreathEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> DATA_OWNER = SynchedEntityData.defineId(MoistDragonBreathEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_TYPE = SynchedEntityData.defineId(MoistDragonBreathEntity.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public String animationprocedure = "empty";

    public MoistDragonBreathEntity(Level world) {
        this(CAEntities.MOIST_DRAGON_BREATH.get(), world);
    }

    public MoistDragonBreathEntity(EntityType<MoistDragonBreathEntity> type, Level world) {
        super(type, world);
        xpReward = 0;
        setNoAi(false);
        setMaxUpStep(0.6f);
        this.moveControl = new FlyingMoveControl(this, 10, true);
    }

    public static void spawn(LevelAccessor world, double x, double y, double z, Entity owner, Entity target, double type) {
        if (owner == null || !(world instanceof ServerLevel level)) {
            return;
        }
        Entity entityToSpawn = CAEntities.MOIST_DRAGON_BREATH.get().spawn(level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
        if (!(entityToSpawn instanceof MoistDragonBreathEntity dragonBreath)) {
            return;
        }
        RandomSource random = world.getRandom();
        entityToSpawn.setDeltaMovement(owner.getLookAngle().scale(0.25).add(
                Mth.nextDouble(random, -0.15, 0.15),
                Mth.nextDouble(random, -0.15, 0.15),
                Mth.nextDouble(random, -0.15, 0.15)));
        SoundEvent shootSound = CASounds.CASTER_CAST.get();
        level.playSound(owner, BlockPos.containing(x, y, z), shootSound, SoundSource.HOSTILE, 2, Mth.nextFloat(owner.level().getRandom(), 0.9f, 1.1f));
        SynchedEntityData data = dragonBreath.getEntityData();
        data.set(DATA_OWNER, owner.getStringUUID());
        if (target != null) {
            data.set(DATA_TARGET, target.getStringUUID());
        }
        data.set(DATA_TYPE, (int) type);
        if (type > 0.5) {
            AttributeInstance maxHealthAttribute = dragonBreath.getAttribute(Attributes.MAX_HEALTH);
            if (maxHealthAttribute != null) {
                maxHealthAttribute.setBaseValue(maxHealthAttribute.getBaseValue() * 2);
            }
            dragonBreath.setHealth(dragonBreath.getMaxHealth());
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SHOOT, false);
        this.entityData.define(DATA_ANIMATION, "undefined");
        this.entityData.define(DATA_TARGET, "");
        this.entityData.define(DATA_OWNER, "");
        this.entityData.define(DATA_TYPE, 0);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        return new FlyingPathNavigation(this, world);
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return SoundEvents.GENERIC_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
    }

    @Override
    public boolean causeFallDamage(float l, float d, DamageSource source) {
        return false;
    }

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Target", this.entityData.get(DATA_TARGET));
        compound.putString("Owner", this.entityData.get(DATA_OWNER));
        compound.putInt("Type", this.entityData.get(DATA_TYPE));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Target")) {
            this.entityData.set(DATA_TARGET, compound.getString("Target"));
        }
        if (compound.contains("Owner")) {
            this.entityData.set(DATA_OWNER, compound.getString("Owner"));
        }
        if (compound.contains("Type")) {
            this.entityData.set(DATA_TYPE, compound.getInt("Type"));
        }
	}

    @Override
    public void baseTick() {
        super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        if (tickCount >= 1200 && !level().isClientSide()) {
            discard();
        }
        if (this.isAlive()) {
            clearFire();
            setAirSupply(20);
            SynchedEntityData data = this.getEntityData();
            Entity target = null;
            if (world instanceof ServerLevel level) {
                String targetUuid = data.get(DATA_TARGET);
                if (targetUuid != null && !targetUuid.isEmpty()) {
                    target = level.getEntity(UUID.fromString(targetUuid));
                }
                level.sendParticles(CAParticles.EDERMAN_PTC.get(), x, y + 0.25, z, 3, 0.1, 0.1, 0.1, 0.1);
            }
            if (target != null && target.isAlive()) {
                this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(target.getX(), target.getY() + 0.5, target.getZ()));
                if (tickCount > 20) {
                    if (distanceTo(target) > 0.5) {
                        Vec3 offset = position()
                                .vectorTo(target.position().add(0, 0.25, 0));
                        if (offset.lengthSqr() <= 0.05) {
                            setDeltaMovement(Vec3.ZERO);
                        } else {
                            offset = offset.normalize().scale(0.4);
                            setDeltaMovement(offset);
                        }
                    } else {
                        dragonBreathExplode(world, x, y, z, this);
                    }
                }
            } else {
                target = this.getTarget();
                if (target != null && target.isAlive()) {
                    data.set(DATA_TARGET, target.getStringUUID());
                } else {
                    dragonBreathExplode(world, x, y, z, this);
                }
            }
        }
        this.refreshDimensions();
    }

    @Override
    public void tick() {
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);
    }

    @Override
    public void move(MoverType pType, Vec3 pPos) {
        super.move(pType, pPos);
        this.checkInsideBlocks();
    }

    @Override
    public EntityDimensions getDimensions(Pose p_33597_) {
        return super.getDimensions(p_33597_).scale((float) 1);
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
        this.updateSwingTime();
        this.setNoGravity(true);
    }


    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder = builder.add(Attributes.MAX_HEALTH, 24);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 14);
        builder = builder.add(Attributes.FOLLOW_RANGE, 36);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
        builder = builder.add(Attributes.FLYING_SPEED, 0.3);
        return builder;
    }

    private PlayState movementPredicate(AnimationState<?> event) {
        if (this.animationprocedure.equals("empty")) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.moist_dragon_breath.idle"));
        }
        return PlayState.STOP;
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
        if (this.deathTime == 5) {
            this.remove(RemovalReason.KILLED);
            this.dropExperience();
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() == null) return false;
        return super.hurt(source, amount);
    }

    @Override
    public void setHealth(float pHealth) {
        if (this.entityData.get(DATA_TYPE) == 1) {
            float hlth = this.getHealth();
            float reduction = hlth - pHealth;
            super.setHealth(reduction >= 1 ? hlth - 1 : pHealth);
            return;
        }
        super.setHealth(pHealth);
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

    public static void dragonBreathRain(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null)
            return;
        double r;
        double d;
        double tx;
        double tz;
        r = Mth.nextDouble(RandomSource.create(), 0, 6.283);
        d = Mth.nextDouble(RandomSource.create(), 3, 8);
        tx = x + d * Math.cos(r);
        tz = z + d * Math.sin(r);
        if (world instanceof ServerLevel projectileLevel) {
            DragonFireball fireball = new DragonFireball(EntityType.DRAGON_FIREBALL, projectileLevel);
            fireball.setOwner(entity);
            fireball.xPower = 0;
            fireball.yPower = -0.1;
            fireball.zPower = 0;
            fireball.setPos(tx, (y + Mth.nextInt(RandomSource.create(), 6, 9)), tz);
            fireball.shoot(0, 1, 0, (float) (-0.5), 0);
            projectileLevel.addFreshEntity(fireball);
        }
    }

    private void dragonBreathExplode(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null)
            return;
        Entity owner;
        Entity target;
        String uuid;
        String uuid1;
        double d;
        double T;
        if (world.isClientSide()) {
            return;
        }
        if (entity instanceof LivingEntity livingEntity1 && livingEntity1.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) {
            livingEntity1.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
        }
        uuid = entity instanceof MoistDragonBreathEntity datEntS ? datEntS.getEntityData().get(MoistDragonBreathEntity.DATA_TARGET) : "";
        uuid1 = entity instanceof MoistDragonBreathEntity datEntS ? datEntS.getEntityData().get(MoistDragonBreathEntity.DATA_OWNER) : "";
        target = world instanceof ServerLevel server && uuid != null && !uuid.isEmpty() ? server.getEntity(UUID.fromString(uuid)) : null;
        owner = world instanceof ServerLevel server && uuid1 != null && !uuid1.isEmpty() ? server.getEntity(UUID.fromString(uuid1)) : null;
        if (!(owner == null) && owner.isAlive()) {
            d = owner instanceof LivingEntity livingEntity8 && livingEntity8.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity8.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
        } else {
            d = entity instanceof LivingEntity livingEntity9 && livingEntity9.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity9.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
        }
        if (d > 0) {
            T = entity instanceof MoistDragonBreathEntity datEntI ? datEntI.getEntityData().get(MoistDragonBreathEntity.DATA_TYPE) : 0;
            if (T > 0.5) {
                d = d * 1.35;
            }
            final Vec3 center = new Vec3(x, y, z);
            List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(5 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
            for (Entity entityiterator : entfound) {
                if (!(entityiterator instanceof LivingEntity)) {
                    continue;
                }
                if (entityiterator == owner) {
                    continue;
                }
                boolean result = true;
                if (target == null || owner == null) {
                    result = false;
                } else {
                    Entity recentVictim;
                    Entity recentAttacker;
                    if (owner instanceof OceanizedEnderinaEntity) {
                        if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring")))) {
                            if (!(entityiterator == target)) {
                                result = false;
                            }
                        }
                    } else if (!(entityiterator instanceof Monster)) {
                        if (entityiterator != target) {
                            recentVictim = (owner instanceof LivingEntity livingOwner) ? livingOwner.getLastHurtMob() : null;
                            recentAttacker = (owner instanceof LivingEntity livingOwner) ? livingOwner.getLastHurtByMob() : null;
                            if (entityiterator != recentVictim) {
                                if (entityiterator != recentAttacker) {
                                    if ((entityiterator instanceof Mob mobEnt ? (Entity) mobEnt.getTarget() : null) != owner) {
                                        result = false;
                                    }
                                }
                            }
                        }
                    }
                }
                if (result) {
                    if (entity.distanceTo(entityiterator) <= 2.5) {
                        entityiterator.hurt(
                                CADamageTypes.source(world, CADamageTypes.OCEAN_MAGIC, entity, owner),
                                (float) d);
                        if (entityiterator instanceof LivingEntity livingTarget) {
                            if (owner instanceof LivingEntity attacker) {
                                SIHelper.causeSanityInjury(livingTarget, attacker, d * 20, SanityEvent.Hurt.Type.ENTITY);
                            } else {
                                SIHelper.causeSanityInjury(livingTarget, d * 20, SanityEvent.Hurt.Type.ENTITY);
                            }
                        }
                    }
                }
            }
            if (world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), CASounds.CASTER_EXPLODE.get(), SoundSource.HOSTILE, 3, (float) Mth.nextDouble(RandomSource.create(), 0.9, 1.1));
                if (world instanceof ServerLevel serverLevel)
                    serverLevel.sendParticles(ParticleTypes.DRAGON_BREATH, x, (y + 0.25), z, 32, 2, 2, 2, 0.18);
            }
        }
        if (!entity.level().isClientSide())
            entity.discard();
    }


    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}
