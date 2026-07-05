package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.SyncedAnimationEntity;
import com.apocalypse.caerulaarbor.entity.bullets.HealBullletEntity;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
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
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class TribunalHealerEntity extends Animal implements RangedAttackMob, GeoEntity, SyncedAnimationEntity {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(TribunalHealerEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(TribunalHealerEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_SKILLP_1 = SynchedEntityData.defineId(TribunalHealerEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_SKILLP_2 = SynchedEntityData.defineId(TribunalHealerEntity.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";

    public TribunalHealerEntity(Level world) {
        this(CAEntities.TRIBUNAL_HEALER.get(), world);
    }

    public TribunalHealerEntity(EntityType<TribunalHealerEntity> type, Level world) {
        super(type, world);
        xpReward = 8;
        setNoAi(false);
        setMaxUpStep(0.8f);
        setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SHOOT, false);
        this.entityData.define(DATA_ANIMATION, "undefined");
        this.entityData.define(DATA_SKILLP_1, 100);
        this.entityData.define(DATA_SKILLP_2, 90);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Monster.class, true, false));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Monster.class, (float) 6));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1));
        this.goalSelector.addGoal(5, new OpenDoorGoal(this, false));
        this.goalSelector.addGoal(6, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25, 60, 14f) {
            @Override
            public boolean canContinueToUse() {
                return this.canUse();
            }
        });
    }

    public class RangedAttackGoal extends Goal {
        private final Mob mob;
        private final RangedAttackMob rangedAttackMob;
        @Nullable
        private LivingEntity target;
        private int attackTime = -1;
        private final double speedModifier;
        private int seeTime;
        private final int attackIntervalMin;
        private final int attackIntervalMax;
        private final float attackRadius;
        private final float attackRadiusSqr;

        public RangedAttackGoal(RangedAttackMob p_25768_, double p_25769_, int p_25770_, float p_25771_) {
            this(p_25768_, p_25769_, p_25770_, p_25770_, p_25771_);
        }

        public RangedAttackGoal(RangedAttackMob p_25773_, double p_25774_, int p_25775_, int p_25776_, float p_25777_) {
            if (!(p_25773_ instanceof LivingEntity)) {
                throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
            } else {
                this.rangedAttackMob = p_25773_;
                this.mob = (Mob) p_25773_;
                this.speedModifier = p_25774_;
                this.attackIntervalMin = p_25775_;
                this.attackIntervalMax = p_25776_;
                this.attackRadius = p_25777_;
                this.attackRadiusSqr = p_25777_ * p_25777_;
                this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
            }
        }

        public boolean canUse() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                this.target = livingentity;
                return true;
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return this.canUse() || this.target != null && this.target.isAlive() && !this.mob.getNavigation().isDone();
        }

        public void stop() {
            this.target = null;
            this.seeTime = 0;
            this.attackTime = -1;
            ((TribunalHealerEntity) rangedAttackMob).entityData.set(DATA_SHOOT, false);
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            double d0 = 0;
            if (this.target != null) {
                d0 = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
            }
            boolean flag = this.mob.getSensing().hasLineOfSight(this.target);
            if (flag) {
                ++this.seeTime;
            } else {
                this.seeTime = 0;
            }
            if (!(d0 > (double) this.attackRadiusSqr) && this.seeTime >= 5) {
                this.mob.getNavigation().stop();
            } else {
                this.mob.getNavigation().moveTo(this.target, this.speedModifier);
            }
            this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
            if (--this.attackTime == 0) {
                if (!flag) {
                    ((TribunalHealerEntity) rangedAttackMob).entityData.set(DATA_SHOOT, false);
                    return;
                }
                ((TribunalHealerEntity) rangedAttackMob).entityData.set(DATA_SHOOT, true);
                float f = (float) Math.sqrt(d0) / this.attackRadius;
                float f1 = Mth.clamp(f, 0.1F, 1.0F);
                this.rangedAttackMob.performRangedAttack(this.target, f1);
                this.attackTime = Mth.floor(f * (float) (this.attackIntervalMax - this.attackIntervalMin) + (float) this.attackIntervalMin);
            } else if (this.attackTime < 0) {
                this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(d0) / (double) this.attackRadius, this.attackIntervalMin, this.attackIntervalMax));
            } else
                ((TribunalHealerEntity) rangedAttackMob).entityData.set(DATA_SHOOT, false);
        }
    }

    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHitIn) {
        super.dropCustomDeathLoot(source, looting, recentlyHitIn);
        this.spawnAtLocation(new ItemStack(Items.AMETHYST_SHARD));
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
	public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Skillp1", this.entityData.get(DATA_SKILLP_1));
        compound.putInt("Skillp2", this.entityData.get(DATA_SKILLP_2));
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
	}

    @Override
    public void baseTick() {
        super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        double sklp1;
        double sklp2;
        if (this.isAlive()) {
            sklp1 = (Entity) this instanceof TribunalHealerEntity datEntI ? datEntI.getEntityData().get(DATA_SKILLP_1) : 0;
            sklp2 = (Entity) this instanceof TribunalHealerEntity datEntI ? datEntI.getEntityData().get(DATA_SKILLP_2) : 0;
            if (sklp1 > 0) {
                if ((Entity) this instanceof TribunalHealerEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_SKILLP_1, (int) (sklp1 - 1));
            } else {
                if (tickCount % 5 == 0) {
                    if (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) {
                        this.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
                    }
                    {
                        final Vec3 center = new Vec3(x, y, z);
                        List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(16 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                        for (Entity entityiterator : entfound) {
                            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "homo_sapiens")))) {
                                if (!(this == entityiterator)) {
                                    if ((Entity) this instanceof TribunalHealerEntity datEntSetI)
                                        datEntSetI.getEntityData().set(DATA_SKILLP_1, 100);
                                    if (this instanceof TribunalHealerEntity) {
                                        this.setAnimation("animation.tribunal_healer.concentratedheal");
                                    }
                                    CaerulaArborMod.queueServerWork(20, () -> {
                                        if (this.isAlive()) {
                                            double atk1;
                                            double count1 = 0;
                                            atk1 = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
                                            if (world instanceof Level level) {
                                                level.playSound(null, BlockPos.containing(x, y, z), CASounds.MEDIC_STRONG.get(), SoundSource.NEUTRAL, 2, 1);
                                            }
                                            final Vec3 center1 = new Vec3(x, y, z);
                                            List<Entity> entfound1 = world.getEntitiesOfClass(Entity.class, new AABB(center1, center1).inflate(18 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center1))).toList();
                                            for (Entity entityiterator1 : entfound1) {
                                                if (entityiterator1.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "inquisition")))) {
                                                    if ((entityiterator1 instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) < (entityiterator1 instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1)) {
                                                        if (entityiterator1 instanceof LivingEntity entity && !entity.level().isClientSide())
                                                            entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 1));
                                                        if ((entityiterator1 instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) < (entityiterator1 instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * 0.5) {
                                                            if ((Entity) this instanceof LivingEntity entity && !entity.level().isClientSide())
                                                                entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
                                                            if ((Entity) this instanceof LivingEntity entity && !entity.level().isClientSide())
                                                                entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
                                                            EntityUtils.healWithParticles(world, entityiterator1, atk1 * 2, 20);
                                                        } else {
                                                            EntityUtils.healWithParticles(world, entityiterator1, atk1 * 2, 0);
                                                        }
                                                        if (!(this == entityiterator1)) {
                                                            count1 = count1 + 1;
                                                            if (count1 >= 7) {
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    });
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (sklp2 > 0) {
                if ((Entity) this instanceof TribunalHealerEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_SKILLP_2, (int) (sklp2 - 1));
            } else {
                if (tickCount % 5 == 0 && hasAggresiveMobAround(world, x, y, z)) {
                    if ((Entity) this instanceof TribunalHealerEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_SKILLP_2, 240);
                    if (this instanceof TribunalHealerEntity) {
                        this.setAnimation("animation.tribunal_healer.shockwave");
                    }
                    CaerulaArborMod.queueServerWork(40, () -> {
                        if (this.isAlive()) {
                            double vx;
                            double vz;
                            double dist;
                            if (world instanceof Level level) {
                                level.playSound(null, BlockPos.containing(x, y, z), CASounds.MEDIC_BLAST.get(), SoundSource.PLAYERS, 2, 1);
                            }
                            {
                                final Vec3 center = new Vec3((getX()), (getY()), (getZ()));
                                List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(12 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                                for (Entity entityiterator : entfound) {
                                    if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "is_humanside")))) {
                                        continue;
                                    }
                                    if (entityiterator instanceof TamableAnimal tamEnt && tamEnt.isTame()) {
                                        continue;
                                    }
                                    if (!(entityiterator instanceof Mob)) {
                                        continue;
                                    }
                                    if (distanceTo(entityiterator) <= 6) {
                                        vx = entityiterator.getX() - getX();
                                        vz = entityiterator.getZ() - getZ();
                                        if (vx == 0) {
                                            vx = 1;
                                        }
                                        if (vz == 0) {
                                            vz = 1;
                                        }
                                        dist = Math.sqrt(vx * vx + vz * vz);
                                        entityiterator.push((0.85 / Math.max(vx, vx / dist)), 0.25, (0.85 / Math.max(vz, vz / dist)));
                                        if (entityiterator instanceof LivingEntity entity && !entity.level().isClientSide())
                                            entity.addEffect(new MobEffectInstance(CAMobEffects.MUTE.get(), 60, 0, false, false));
                                        CaerulaArborMod.queueServerWork(8, () -> {
                                            if (this.isAlive()) {
                                                entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.INDIRECT_MAGIC), this),
                                                        (float) ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 2));
                                            }
                                        });
                                    }
                                }
                            }
                            CaerulaArborMod.queueServerWork(7, () -> {
                                if (this.isAlive()) {
                                    if (world instanceof Level level) {
                                        level.playSound(null, BlockPos.containing(x, y, z), CASounds.MEDIC_ATTACK.get(), SoundSource.PLAYERS, (float) 1.25, 1);
                                    }
                                    new Object() {
                                        void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                                            for (int index0 = 0; index0 < 60; index0++) {
                                                if (world instanceof ServerLevel level)
                                                    level.sendParticles(CAParticles.PURPLE_FLAME.get(), (getX() + 1 * (timedloopiterator + 1) * Math.sin(Math.toRadians(index0 * 6))), (getY()),
                                                            (getZ() + 1 * (timedloopiterator + 1) * Math.cos(Math.toRadians(index0 * 6))), 2, 0.1, 0.15, 0.1, 0.1);
                                            }
                                            final int tick2 = ticks;
                                            CaerulaArborMod.queueServerWork(tick2, () -> {
                                                if (timedlooptotal > timedloopiterator + 1) {
                                                    timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                                                }
                                            });
                                        }
                                    }.timedLoop(0, 6, 1);
                                }
                            });
                        }
                    });
                }
            }
        }
        this.refreshDimensions();
    }

    private boolean hasAggresiveMobAround(LevelAccessor world, double x, double y, double z) {
        Vec3 center = new Vec3(x, y, z);
        List<Entity> entities = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(8 / 2d), entity -> true).stream()
                .sorted(Comparator.comparingDouble(candidate -> candidate.distanceToSqr(center))).toList();
        for (Entity entityIterator : entities) {
            if (this.distanceTo(entityIterator) <= 4
                    && ((entityIterator instanceof Mob mob ? mob.getTarget() : null) == this || entityIterator == this.getLastHurtByMob())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public EntityDimensions getDimensions(Pose p_33597_) {
        return super.getDimensions(p_33597_).scale((float) 1);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float flval) {
        // TODO：当治疗弹路径清理完成后，重新审视这个遗留的系统调用。
        HealBullletEntity.shoot(this, target);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageable) {
        TribunalHealerEntity retval = CAEntities.TRIBUNAL_HEALER.get().create(serverWorld);
        retval.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(retval.blockPosition()), MobSpawnType.BREEDING, null, null);
        return retval;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.updateSwingTime();
    }


    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.2);
        builder = builder.add(Attributes.MAX_HEALTH, 70);
        builder = builder.add(Attributes.ARMOR, 5);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 10);
        builder = builder.add(Attributes.FOLLOW_RANGE, 18);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.25);
        builder = builder.add(CAAttributes.MAGIC_RESISTANCE.get(), 50);
        return builder;
    }

    private PlayState movementPredicate(AnimationState<?> event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

            ) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.tribunal_healer.move"));
            }
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.tribunal_healer.death"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.tribunal_healer.idle"));
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
        if (this.swinging && this.lastSwing + 35L <= level().getGameTime()) {
            this.swinging = false;
        }
        if ((this.swinging || this.entityData.get(DATA_SHOOT)) && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.tribunal_healer.heal"));
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
        if (this.deathTime == 24) {
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
        data.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
        data.add(new AnimationController<>(this, "attacking", 4, this::attackingPredicate));
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
