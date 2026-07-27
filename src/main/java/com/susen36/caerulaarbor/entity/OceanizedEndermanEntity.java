package com.susen36.caerulaarbor.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.api.event.SanityEvent;
import com.susen36.caerulaarbor.capability.sanity.SIHelper;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.init.*;
import com.susen36.caerulaarbor.util.WorldUtils;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;

import java.util.Comparator;
import java.util.List;

public class OceanizedEndermanEntity extends SeaMonster {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(OceanizedEndermanEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(OceanizedEndermanEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_SKILLP = SynchedEntityData.defineId(OceanizedEndermanEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_COOLDOWN = SynchedEntityData.defineId(OceanizedEndermanEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> DATA_HOLDING_CREEPER = SynchedEntityData.defineId(OceanizedEndermanEntity.class, EntityDataSerializers.BOOLEAN);
    private static final TagKey<DamageType> BYPASSES_ENDERMAN = CADamageTags.BYPASSES_ENDERMAN;
    public String animationprocedure = "empty";
    String prevAnim = "empty";
    private boolean swinging;
    private long lastSwing;
    private boolean creeperCharged;

    public OceanizedEndermanEntity(Level world) {
        this(CAEntities.OCEANIZED_ENDERMAN.get(), world);
    }

    public OceanizedEndermanEntity(EntityType<OceanizedEndermanEntity> type, Level world) {
        super(type, world);
        xpReward = 8;
        setNoAi(false);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1f);
        setPersistenceRequired();
        this.creeperCharged = false;
    }

    public boolean getCreeperCharged() {
        return this.creeperCharged;
    }

    public void setCreeperCharged() {
        this.creeperCharged = true;
    }

    public boolean isHolding() {
        return this.entityData.get(DATA_HOLDING_CREEPER);
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
            level.playLocalSound(x, y, z, SoundEvents.ENDERMAN_AMBIENT, SoundSource.HOSTILE, 0, 1, false);
        }
        for (int dy = 0; dy <= 3; dy++) {
            if (world.getBlockFloorHeight(BlockPos.containing(x, y + dy, z)) > 0) {
                return false;
            }
        }
        return true;
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(CAAttributes.MAGIC_RESISTANCE, 40);
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder = builder.add(Attributes.MAX_HEALTH, 95);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 14);
        builder = builder.add(Attributes.FOLLOW_RANGE, 32);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.45);
        return builder;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHOOT, false);
        builder.define(DATA_ANIMATION, "undefined");
        builder.define(DATA_SKILLP, 150);
        builder.define(DATA_COOLDOWN, 200);
        builder.define(DATA_HOLDING_CREEPER, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.33, false));
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
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public SoundEvent getAmbientSound() {
        return SoundEvents.ENDERMAN_AMBIENT;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return SoundEvents.ENDERMAN_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.ENDERMAN_DEATH;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (!this.level().isClientSide()) {
            CaerulaArborMod.queueServerWork(9, () -> {
                if (this.isAlive() && target.isAlive() && this.distanceTo(target) <= 2.5) {
                    target.hurt(
                            CADamageTypes.source(this.level(), CADamageTypes.GENERIC_SEABORN_ATTACK, this), (float) (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0));
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
        if (!source.is(BYPASSES_ENDERMAN) && !this.hasEffect(CAMobEffects.DIZZY) && !this.hasEffect(CAMobEffects.MUTE)) {
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
            if (( this instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) <= ((Entity) this instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * 0.5) {
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
                        this.setHealth((float) (((Entity) this instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) + ((Entity) this instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * 0.075));
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
                if (!sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring"))) || this.getTarget() == sourceentity) {
                    this.teleportTo(x, y, z, sx, sy, sz);
                    if (sourceentity instanceof LivingEntity target) {
                        SIHelper.causeSanityInjury(target,
                                this,
                                (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 15,
                                SanityEvent.Hurt.Type.ENTITY);
                        this.setTarget(target);
                    }
                    sourceentity.hurt(
                            CADamageTypes.source(this.level(), CADamageTypes.OCEAN_MAGIC, this), (float) ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 0.75));
                }
            }
        }
        if (source.is(DamageTypes.FALL))
            return false;
        if (source.is(DamageTypes.DROWN))
            return false;
        float finalAmount = this.isHolding() && !source.is(CADamageTags.BYPASS_PROTECTION) ? amount * 0.8f : amount;
        return super.hurt(source, finalAmount);
    }

    private void teleportTo(double fromX, double fromY, double fromZ, double toX, double toY, double toZ) {
        if (!this.isAlive()) {
            return;
        }
        double vx = toX - fromX;
        double vy = toY - fromY;
        double vz = toZ - fromZ;
        int particleSteps = (int) Math.max(Math.min(Math.round(Math.sqrt(vx * vx + vy * vy + vz * vz)), 32), 1);
        Vec3 previousPosition = new Vec3(fromX, fromY, fromZ);
        boolean teleported = this.randomTeleport(toX, toY, toZ, true);
        if (!teleported) {
            return;
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
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Skillp", this.entityData.get(DATA_SKILLP));
        compound.putInt("Cooldown", this.entityData.get(DATA_COOLDOWN));
        compound.putBoolean("HoldingCreeper", this.entityData.get(DATA_HOLDING_CREEPER));
        compound.putBoolean("CreeperCharged", this.getCreeperCharged());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Skillp")) {
            this.entityData.set(DATA_SKILLP, compound.getInt("Skillp"));
        }
        if (compound.contains("Cooldown")) {
            this.entityData.set(DATA_COOLDOWN, compound.getInt("Cooldown"));
        }
        if (compound.contains("HoldingCreeper")) {
            this.entityData.set(DATA_HOLDING_CREEPER, compound.getBoolean("HoldingCreeper"));
        }
        if (compound.contains("CreeperCharged") && compound.getBoolean("CreeperCharged")) {
            this.setCreeperCharged();
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
        double sklp;
        double cool;
        if (this.isAlive()) {
            sklp = (Entity) this instanceof OceanizedEndermanEntity datEntI ? datEntI.getEntityData().get(DATA_SKILLP) : 0;
            cool = (Entity) this instanceof OceanizedEndermanEntity datEntI ? datEntI.getEntityData().get(DATA_COOLDOWN) : 0;
            target = (Entity) this instanceof Mob mobEnt ? mobEnt.getTarget() : null;
            if (!(target == null)) {
                if (sklp <= 0) {
                    if ((Entity) this instanceof OceanizedEndermanEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_SKILLP, 300);
                    if (this instanceof OceanizedEndermanEntity) {
                        this.setAnimation("animation.oceanzied_enderman.skill");
                    }
                    if (!this.level().isClientSide())
                        this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 35, 0, false, false));
                    if (world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.ENDERMAN_STARE, SoundSource.HOSTILE, 1, 1);
                    }
                    CaerulaArborMod.queueServerWork(13, () -> {
                        Entity enemy1;
                        enemy1 = (Entity) this instanceof Mob mobEnt ? mobEnt.getTarget() : null;
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
                                    final Vec3 center = new Vec3(ctX, y, ctZ);
                                    List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(6 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                                    for (Entity entityiterator : entfound) {
                                        tgtX = entityiterator.getX();
                                        tgtY = entityiterator.getY();
                                        tgtZ = entityiterator.getZ();
                                        if (new Vec3(ctX, y, ctZ).distanceTo(new Vec3(tgtX, tgtY, tgtZ)) <= 3) {
                                            if (!(entityiterator instanceof Mob)) {
                                                continue;
                                            }
                                            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring")))) {
                                                if (!(((Entity) OceanizedEndermanEntity.this instanceof Mob mobEnt ? (Entity) mobEnt.getTarget() : null) == entityiterator)) {
                                                    continue;
                                                }
                                            }
                                            entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.OCEAN_MAGIC, OceanizedEndermanEntity.this), (float) (((Entity) OceanizedEndermanEntity.this instanceof LivingEntity livingEntity10 && livingEntity10.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity10.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 0.9));
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
                    if ((Entity) this instanceof OceanizedEndermanEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_SKILLP, (int) (sklp - 1));
                }
                if (cool <= 0 && target.isAlive()) {
                    if (distanceTo(target) >= 8 && !((Entity) this instanceof LivingEntity livEnt13 && livEnt13.hasEffect(CAMobEffects.COOLDOWN_SINAL))) {
                        if ((Entity) this instanceof OceanizedEndermanEntity datEntSetI)
                            datEntSetI.getEntityData().set(DATA_COOLDOWN, 100);
                        this.teleportTo(x, y, z, target.getX(), target.getY(), target.getZ());
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CAMobEffects.COOLDOWN_SINAL, 100, 0, false, false));
                    }
                } else {
                    if ((Entity) this instanceof OceanizedEndermanEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_COOLDOWN, (int) (cool - 1));
                }
            }
        }
        if (!this.isHolding() && this.isAlive()) {
            PocketSeaCreeperEntity targetCreeper;
            if (this.tickCount % 40 == 10 && Math.random() < 0.67) {
                targetCreeper = world.getEntitiesOfClass(PocketSeaCreeperEntity.class, AABB.ofSize(new Vec3(x, y, z), 48.0, 48.0, 48.0),
                                e -> e.getDisplayName().getString().equals(e.getType().getDescription().getString()) && checkSameTeam(this, e))
                        .stream().sorted(Comparator.comparingDouble(ent -> ent.distanceToSqr(x, y, z))).findFirst().orElse(null);
                if (targetCreeper != null && targetCreeper.isAlive()) {
                    this.getNavigation().moveTo(targetCreeper.getX(), targetCreeper.getY(), targetCreeper.getZ(), 1.0);
                }
            }
            if (this.tickCount % 20 == 10) {
                targetCreeper = world.getEntitiesOfClass(PocketSeaCreeperEntity.class, AABB.ofSize(new Vec3(x, y, z), 5.0, 5.0, 5.0),
                                e -> e.getDisplayName().getString().equals(e.getType().getDescription().getString()) && checkSameTeam(this, e))
                        .stream().sorted(Comparator.comparingDouble(ent -> ent.distanceToSqr(x, y, z))).findFirst().orElse(null);
                if (targetCreeper != null && targetCreeper.isAlive()) {
                    if (!this.level().isClientSide()) {
                        this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 9, false, false));
                    }
                    if (!targetCreeper.level().isClientSide()) {
                        targetCreeper.discard();
                    }
                    if (world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.HOSTILE, 2.0f, 1.0f);
                    }
                    this.entityData.set(DATA_HOLDING_CREEPER, true);
                    if (targetCreeper.charged()) {
                        this.setCreeperCharged();
                    }
                }
            }
        }
        this.refreshDimensions();
    }

    private boolean checkSameTeam(Entity a, Entity b) {
        if (a == null || b == null) {
            return false;
        }
        var at = a.getTeam();
        var bt = b.getTeam();
        if (at == null) {
            return bt == null;
        }
        return at.isAlliedTo(bt);
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.isDeadOrDying()) {
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanzied_enderman.die"));
        }
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) && !this.isAggressive()) {
                if (this.isHolding()) {
                    return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanzied_enderman.walk_hold"));
                }
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanzied_enderman.walk"));
            }
            if (this.isAggressive() && event.isMoving()) {
                if (this.isHolding()) {
                    return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanzied_enderman.sprint_hold"));
                }
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanzied_enderman.sprint"));
            }
            if (this.isHolding()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanzied_enderman.idle_hold"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanzied_enderman.idle"));
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
            if (this.isHolding()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanzied_enderman.attack_hold"));
            }
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanzied_enderman.attack"));
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
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            this.remove(RemovalReason.KILLED);
            this.dropExperience(this.getKillCredit());
            LevelAccessor world = this.level();
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();
            if (world instanceof ServerLevel level)
                level.sendParticles(ParticleTypes.CLOUD, x, (y + 1.8), z, 32, 1, 1, 1, 0.1);
            if (world instanceof ServerLevel level) {
                Entity entityToSpawn = CAEntities.SLIDER_FISH.get().spawn(level, BlockPos.containing(x, y + 1.8, z), MobSpawnType.MOB_SUMMONED);
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
        if (animation.equals("animation.oceanzied_enderman.skill") && this.isHolding()) {
            animation = animation + "_hold";
        }
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