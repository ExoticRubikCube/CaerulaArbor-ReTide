package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.api.event.SanityEvent;
import com.apocalypse.caerulaarbor.capability.sanity.SIHelper;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.*;
import com.apocalypse.caerulaarbor.util.WorldUtils;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

public class OceanizedEndermanEntity extends SeaMonster {
    private static final TagKey<DamageType> BYPASSES_ENDERMAN = TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "bypasses_enderman"));
    public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(OceanizedEndermanEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(OceanizedEndermanEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_skillp = SynchedEntityData.defineId(OceanizedEndermanEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_cooldown = SynchedEntityData.defineId(OceanizedEndermanEntity.class, EntityDataSerializers.INT);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";

    public OceanizedEndermanEntity(Level world) {
        this(CAEntities.OCEANIZED_ENDERMAN.get(), world);
    }

    public OceanizedEndermanEntity(EntityType<OceanizedEndermanEntity> type, Level world) {
        super(type, world);
        xpReward = 8;
        setNoAi(false);
        setMaxUpStep(1f);
        setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SHOOT, false);
        this.entityData.define(ANIMATION, "undefined");
        this.entityData.define(DATA_skillp, 150);
        this.entityData.define(DATA_cooldown, 200);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.33, false) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return 6.25;
            }
        });
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Endermite.class, true, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolem.class, true, false));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, SnowGolem.class, true, false));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Villager.class, true, false));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, Illusioner.class, true, false));
        this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, Pillager.class, true, false));
        this.targetSelector.addGoal(9, new NearestAttackableTargetGoal<>(this, Vindicator.class, true, false));
        this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, Witch.class, true, false));
        this.targetSelector.addGoal(11, new NearestAttackableTargetGoal<>(this, Piglin.class, true, false));
        this.targetSelector.addGoal(12, new NearestAttackableTargetGoal<>(this, PiglinBrute.class, true, false));
        this.targetSelector.addGoal(13, new NearestAttackableTargetGoal<>(this, ZombifiedPiglin.class, true, false));
        this.goalSelector.addGoal(14, new RandomStrollGoal(this, 1));
        this.goalSelector.addGoal(15, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(16, new FloatGoal(this));
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEFINED;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public SoundEvent getAmbientSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.enderman.ambient"));
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.enderman.hurt"));
    }

    @Override
    public SoundEvent getDeathSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.enderman.death"));
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (!this.level().isClientSide()) {
            CaerulaArborMod.queueServerWork(9, () -> {
                if (this.isAlive() && target.isAlive() && this.distanceTo(target) <= 2.5) {
                    target.hurt(
                            new DamageSource(
                                    this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                                            .getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "general_seaborn_attack"))),
                                    this),
                            (float) (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0));
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
        RandomSource random = this.getRandom();
        double ratio;
        double angl;
        double dist;
        double validY;
        double tX;
        double tZ;
        if (!source.is(BYPASSES_ENDERMAN) && !this.hasEffect(CAMobEffects.DIZZY.get()) && !this.hasEffect(CAMobEffects.MUTE.get())) {
            Entity directEntity = source.getDirectEntity();
            if (directEntity != sourceentity || !(sourceentity instanceof LivingEntity)) {
                if (sourceentity instanceof LivingEntity target) {
                    this.setTarget(target);
                }
                if (this.isAlive()) {
                    for (int index0 = 0; index0 < 64; index0++) {
                        angl = Mth.nextDouble(random, 0, 6.283);
                        dist = Mth.nextDouble(random, 3, 6);
                        tX = x + dist * Math.sin(angl);
                        tZ = z + dist * Math.cos(angl);
                        validY = findValidTeleportY(world, tX, y, tZ);
                        if (!Double.isNaN(validY)) {
                            this.teleportTo(x, y, z, tX, validY, tZ);
                            break;
                        }
                    }
                }
                return true;
            }
        }
        if (this.isAlive()) {
            ratio = 0.2;
            if (((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) <= ((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.5) {
                ratio = 0.45;
            }
            if (random.nextDouble() < ratio) {
                for (int index0 = 0; index0 < 64; index0++) {
                    angl = Mth.nextDouble(random, 0, 6.283);
                    dist = Mth.nextDouble(random, 3, 6);
                    tX = x + dist * Math.sin(angl);
                    tZ = z + dist * Math.cos(angl);
                    validY = findValidTeleportY(world, tX, y, tZ);
                    if (!Double.isNaN(validY)) {
                        this.teleportTo(x, y, z, tX, validY, tZ);
                        if ((Entity) this instanceof LivingEntity _entity)
                            _entity.setHealth((float) (((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) + ((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.075));
                        break;
                    }
                }
            }
        }
        if (sourceentity != null && this.distanceTo(sourceentity) >= 6) {
            double sx = sourceentity.getX();
            double sy = sourceentity.getY();
            double sz = sourceentity.getZ();
            if (isValidTeleportPlace(world, sx, sy, sz)) {
                if (!sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring"))) || this.getTarget() == sourceentity) {
                    this.teleportTo(x, y, z, sx, sy, sz);
                    if (sourceentity instanceof LivingEntity target) {
                        SIHelper.causeSanityInjury(target,
                                this,
                                (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 15,
                                SanityEvent.Hurt.Type.ENTITY);
                        this.setTarget(target);
                    }
                    sourceentity.hurt(
                            new DamageSource(this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "ocean_magic"))), this),
                            (float) ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 0.75));
                }
            }
        }
        if (source.is(DamageTypes.FALL))
            return false;
        if (source.is(DamageTypes.DROWN))
            return false;
        return super.hurt(source, amount);
    }

    private static double findValidTeleportY(LevelAccessor world, double x, double y, double z) {
        double validY;
        for (int index0 = 0; index0 < 12; index0++) {
            validY = y + index0;
            if (isValidTeleportPlace(world, x, validY, z)) {
                return validY;
            }
            validY = y - index0 - 1;
            if (isValidTeleportPlace(world, x, validY, z)) {
                return validY;
            }
        }
        return Double.NaN;
    }

    private static boolean isValidTeleportPlace(LevelAccessor world, double x, double y, double z) {
        if (world instanceof Level level && level.isClientSide()) {
            level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.enderman.ambient")), SoundSource.HOSTILE, 0, 1, false);
        }
        for (int dy = 0; dy <= 3; dy++) {
            if (world.getBlockFloorHeight(BlockPos.containing(x, y + dy, z)) > 0) {
                return false;
            }
        }
        return true;
    }

    private boolean teleportTo(double fromX, double fromY, double fromZ, double toX, double toY, double toZ) {
        if (!this.isAlive()) {
            return false;
        }
        double vx = toX - fromX;
        double vy = toY - fromY;
        double vz = toZ - fromZ;
        int particleSteps = (int) Math.max(Math.min(Math.round(Math.sqrt(vx * vx + vy * vy + vz * vz)), 32), 1);
        Vec3 previousPosition = new Vec3(fromX, fromY, fromZ);
        boolean teleported = this.randomTeleport(toX, toY, toZ, true);
        if (!teleported) {
            return false;
        }
        this.level().gameEvent(GameEvent.TELEPORT, previousPosition, GameEvent.Context.of(this));
        if (!this.isSilent()) {
            this.level().playSound(null, fromX, fromY, fromZ, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
            this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
        }
        for (int index0 = 0; index0 < particleSteps; index0++) {
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(CAParticles.EDERMAN_PTC.get(), fromX + (vx / particleSteps) * index0, fromY + (vy / particleSteps) * index0 + 0.65, fromZ + (vz / particleSteps) * index0, 32, 0.65, 0.65,
                        0.65, 0.05);
            }
        }
        this.clearFire();
        return true;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
        SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
        if (this.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get())) {
            this.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).setBaseValue(40);
        }
        return retval;
    }

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Dataskillp", this.entityData.get(DATA_skillp));
        compound.putInt("Datacooldown", this.entityData.get(DATA_cooldown));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Dataskillp"))
            this.entityData.set(DATA_skillp, compound.getInt("Dataskillp"));
        if (compound.contains("Datacooldown"))
            this.entityData.set(DATA_cooldown, compound.getInt("Datacooldown"));
	}

    @Override
    public void baseTick() {
        super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity enemy;
        double sklp;
        double cool;
        if (this.isAlive()) {
            sklp = (Entity) this instanceof OceanizedEndermanEntity _datEntI ? _datEntI.getEntityData().get(DATA_skillp) : 0;
            cool = (Entity) this instanceof OceanizedEndermanEntity _datEntI ? _datEntI.getEntityData().get(DATA_cooldown) : 0;
            enemy = (Entity) this instanceof Mob _mobEnt ? _mobEnt.getTarget() : null;
            if (!(enemy == null)) {
                if (sklp <= 0) {
                    if ((Entity) this instanceof OceanizedEndermanEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_skillp, 300);
                    if (this instanceof OceanizedEndermanEntity) {
                        this.setAnimation("animation.oceanzied_enderman.skill");
                    }
                    if (!this.level().isClientSide())
                        this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 35, 0, false, false));
                    if (world instanceof Level _level) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.enderman.stare")), SoundSource.HOSTILE, 1, 1);
                    }
                    CaerulaArborMod.queueServerWork(13, () -> {
                        if (this == null)
                            return;
                        double sklp1 = 0;
                        Entity enemy1;
                        enemy1 = (Entity) this instanceof Mob _mobEnt ? _mobEnt.getTarget() : null;
                        if (!(enemy1 == null)) {
                            ((Entity) this).lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((enemy1.getX()), (enemy1.getY()), (enemy1.getZ())));
                        }
                        new Object() {
                            void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                                double tgtX;
                                double tgtY;
                                double tgtZ;
                                double ctX;
                                double ctZ;
                                ctX = x + getLookAngle().x * (double) (2 * (timedloopiterator + 1));
                                ctZ = z + getLookAngle().z * (double) (2 * (timedloopiterator + 1));
                                {
                                    final Vec3 _center = new Vec3(ctX, y, ctZ);
                                    List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(6 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                                    for (Entity entityiterator : _entfound) {
                                        tgtX = entityiterator.getX();
                                        tgtY = entityiterator.getY();
                                        tgtZ = entityiterator.getZ();
                                        if (new Vec3(ctX, y, ctZ).distanceTo(new Vec3(tgtX, tgtY, tgtZ)) <= 3) {
                                            if (!(entityiterator instanceof Mob)) {
                                                continue;
                                            }
                                            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                                                if (!(((Entity) OceanizedEndermanEntity.this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) == entityiterator)) {
                                                    continue;
                                                }
                                            }
                                            entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "ocean_magic"))), OceanizedEndermanEntity.this),
                                                    (float) (((Entity) OceanizedEndermanEntity.this instanceof LivingEntity _livingEntity10 && _livingEntity10.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity10.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 0.9));
                                            if (WorldUtils.canGrief(world)) {
                                                if ((world.getBlockState(BlockPos.containing(tgtX, tgtY, tgtZ))).canBeReplaced() && CABlocks.SEA_TRAIL_INIT.get().defaultBlockState().canSurvive(world, BlockPos.containing(tgtX, tgtY, tgtZ))) {
                                                    world.setBlock(BlockPos.containing(tgtX, tgtY, tgtZ), CABlocks.SEA_TRAIL_INIT.get().defaultBlockState(), 3);
                                                }
                                            }
                                        }
                                    }
                                }
                                final int tick2 = ticks;
                                CaerulaArborMod.queueServerWork(tick2, () -> {
                                    if (timedlooptotal > timedloopiterator + 1) {
                                        timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                                    }
                                });
                            }
                        }.timedLoop(0, 12, 1);
                    });
                } else {
                    if ((Entity) this instanceof OceanizedEndermanEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_skillp, (int) (sklp - 1));
                }
                if (cool <= 0 && enemy.isAlive()) {
                    if ((enemy != null ? distanceTo(enemy) : -1) >= 8 && !((Entity) this instanceof LivingEntity _livEnt13 && _livEnt13.hasEffect(CAMobEffects.COOLDOWN_SINAL.get()))) {
                        if ((Entity) this instanceof OceanizedEndermanEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_cooldown, 100);
                        this.teleportTo(x, y, z, enemy.getX(), enemy.getY(), enemy.getZ());
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CAMobEffects.COOLDOWN_SINAL.get(), 100, 0, false, false));
                    }
                } else {
                    if ((Entity) this instanceof OceanizedEndermanEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_cooldown, (int) (cool - 1));
                }
            }
        }
        this.refreshDimensions();
    }

    @Override
    public EntityDimensions getDimensions(Pose p_33597_) {
        return super.getDimensions(p_33597_).scale((float) 1);
    }


    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder = builder.add(Attributes.MAX_HEALTH, 95);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 14);
        builder = builder.add(Attributes.FOLLOW_RANGE, 32);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.45);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

                    && !this.isAggressive()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanzied_enderman.walk"));
            }
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanzied_enderman.die"));
            }
            if (this.isAggressive() && event.isMoving()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanzied_enderman.sprint"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanzied_enderman.idle"));
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
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanzied_enderman.attack"));
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
        if (this.deathTime == 20) {
            this.remove(RemovalReason.KILLED);
            this.dropExperience();
            LevelAccessor world = this.level();
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();
            if (world instanceof ServerLevel _level)
                _level.sendParticles(ParticleTypes.CLOUD, x, (y + 1.8), z, 32, 1, 1, 1, 0.1);
            if (world instanceof ServerLevel _level) {
                Entity entityToSpawn = CAEntities.SLIDER_FISH.get().spawn(_level, BlockPos.containing(x, y + 1.8, z), MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                }
            }
        }
    }

    public String getSyncedAnimation() {
        return this.entityData.get(ANIMATION);
    }

    public void setAnimation(String animation) {
        this.entityData.set(ANIMATION, animation);
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
