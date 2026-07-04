package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.capability.map.MapVariables;
import com.apocalypse.caerulaarbor.entity.base.RavagerMountRider;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.entity.bullets.FleefishBulletEntity;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAGameRules;
import com.apocalypse.caerulaarbor.init.CAParticles;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import net.minecraft.sounds.SoundEvents;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class OceanizedEvokerEntity extends SeaMonster implements RangedAttackMob, RavagerMountRider {
    public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(OceanizedEvokerEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(OceanizedEvokerEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_skillp1 = SynchedEntityData.defineId(OceanizedEvokerEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_skillp2 = SynchedEntityData.defineId(OceanizedEvokerEntity.class, EntityDataSerializers.INT);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";

    public OceanizedEvokerEntity(Level world) {
        this(CAEntities.OCEANIZED_EVOKER.get(), world);
    }

    public OceanizedEvokerEntity(EntityType<OceanizedEvokerEntity> type, Level world) {
        super(type, world);
        xpReward = 8;
        setNoAi(false);
        setMaxUpStep(0.8f);
        setPersistenceRequired();
        this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.TOTEM_OF_UNDYING));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SHOOT, false);
        this.entityData.define(ANIMATION, "undefined");
        this.entityData.define(DATA_skillp1, 100);
        this.entityData.define(DATA_skillp2, 150);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, IronGolem.class, true, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, SnowGolem.class, true, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Villager.class, true, false));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Illusioner.class, true, false));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Evoker.class, true, false));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, Pillager.class, true, false));
        this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, Vindicator.class, true, false));
        this.targetSelector.addGoal(9, new NearestAttackableTargetGoal<>(this, Witch.class, true, false));
        this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, Piglin.class, true, false));
        this.targetSelector.addGoal(11, new NearestAttackableTargetGoal<>(this, PiglinBrute.class, true, false));
        this.targetSelector.addGoal(12, new NearestAttackableTargetGoal<>(this, ZombifiedPiglin.class, true, false));
        this.targetSelector.addGoal(13, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, target -> EntityUtils.isOceanizedPlayerNearby(this.level(), this.getX(), this.getY(), this.getZ())));
        this.goalSelector.addGoal(15, new RandomStrollGoal(this, 1));
        this.goalSelector.addGoal(16, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(17, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25, 60, 9f) {
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
            return this.canUse() || this.target.isAlive() && !this.mob.getNavigation().isDone();
        }

        public void stop() {
            this.target = null;
            this.seeTime = 0;
            this.attackTime = -1;
            ((OceanizedEvokerEntity) rangedAttackMob).entityData.set(SHOOT, false);
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            double d0 = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
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
                    ((OceanizedEvokerEntity) rangedAttackMob).entityData.set(SHOOT, false);
                    return;
                }
                ((OceanizedEvokerEntity) rangedAttackMob).entityData.set(SHOOT, true);
                float f = (float) Math.sqrt(d0) / this.attackRadius;
                float f1 = Mth.clamp(f, 0.1F, 1.0F);
                this.rangedAttackMob.performRangedAttack(this.target, f1);
                this.attackTime = Mth.floor(f * (float) (this.attackIntervalMax - this.attackIntervalMin) + (float) this.attackIntervalMin);
            } else if (this.attackTime < 0) {
                this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(d0) / (double) this.attackRadius, this.attackIntervalMin, this.attackIntervalMax));
            } else
                ((OceanizedEvokerEntity) rangedAttackMob).entityData.set(SHOOT, false);
        }
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public SoundEvent getAmbientSound() {
        return SoundEvents.EVOKER_AMBIENT;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return SoundEvents.EVOKER_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.EVOKER_DEATH;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.DROWN))
            return false;
        return super.hurt(source, amount);
    }

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Dataskillp1", this.entityData.get(DATA_skillp1));
        compound.putInt("Dataskillp2", this.entityData.get(DATA_skillp2));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Dataskillp1"))
            this.entityData.set(DATA_skillp1, compound.getInt("Dataskillp1"));
        if (compound.contains("Dataskillp2"))
            this.entityData.set(DATA_skillp2, compound.getInt("Dataskillp2"));
	}

    @Override
    public void baseTick() {
        super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity enemy;
        double sklp1;
        double sklp2;
        double dist;
        if (this.isAlive()) {
            enemy = this.getTarget();
            sklp1 = (Entity) this instanceof OceanizedEvokerEntity _datEntI ? _datEntI.getEntityData().get(DATA_skillp1) : 0;
            sklp2 = (Entity) this instanceof OceanizedEvokerEntity _datEntI ? _datEntI.getEntityData().get(DATA_skillp2) : 0;
            if (sklp1 <= 0) {
                if (!(enemy == null) && enemy.isAlive()) {
                    dist = Math.round(distanceTo(enemy));
                    if (dist <= 35) {
                        if (this instanceof OceanizedEvokerEntity) {
                            this.setAnimation("animation.oceanized_evoker.spell");
                        }
                        if ((Entity) this instanceof OceanizedEvokerEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_skillp1, 100);
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 9, false, false));
                        if (dist > 6) {
                            new Object() {
                                void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                                    if (((Entity) OceanizedEvokerEntity.this).isAlive()) {
                                        spawnLinearFangs(world, timedloopiterator + 1);
                                    }
                                    final int tick2 = ticks;
                                    CaerulaArborMod.queueServerWork(tick2, () -> {
                                        if (timedlooptotal > timedloopiterator + 1) {
                                            timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                                        }
                                    });
                                }
                            }.timedLoop(0, (int) dist, 1);
                        } else {
                            new Object() {
                                void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                                    if (((Entity) OceanizedEvokerEntity.this).isAlive()) {
                                        double dt;
                                        double r;
                                        double fy;
                                        double tx;
                                        double tz;
                                        dt = 360 / ((double) ((timedloopiterator + 1) * 3));
                                        r = 0.5 * (double) ((timedloopiterator + 1) * 3);
                                        for (int index0 = 0; index0 < (int) ((double) ((timedloopiterator + 1) * 3)); index0++) {
                                            tx = x + r * Math.sin(Math.toRadians(dt * index0));
                                            tz = z + r * Math.cos(Math.toRadians(dt * index0));
                                            fy = 114514;
                                            for (int dy = 0; dy <= 3; dy++) {
                                                BlockState target = world.getBlockState(BlockPos.containing(tx, y + dy, tz));
                                                if (target.canBeReplaced() && world.getBlockFloorHeight(BlockPos.containing(tx, y + dy - 1, tz)) > 0) {
                                                    fy = y + dy;
                                                    break;
                                                }
                                                target = world.getBlockState(BlockPos.containing(tx, y - dy, tz));
                                                if (target.canBeReplaced() && world.getBlockFloorHeight(BlockPos.containing(tx, y - dy - 1, tz)) > 0) {
                                                    fy = y - dy;
                                                    break;
                                                }
                                            }
                                            if (fy <= 114513) {
                                                if (world instanceof ServerLevel _level) {
                                                    Entity entityToSpawn = EntityType.EVOKER_FANGS.spawn(_level, BlockPos.containing(tx, fy, tz), MobSpawnType.MOB_SUMMONED);
                                                    if (entityToSpawn != null) {
                                                        entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
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
                            }.timedLoop(0, 5, 2);
                        }
                    }
                }
            } else {
                if ((Entity) this instanceof OceanizedEvokerEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_skillp1, (int) (sklp1 - 1));
                if (sklp1 == 20) {
                    if (world instanceof Level _level) {
                        _level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.EVOKER_PREPARE_ATTACK, SoundSource.NEUTRAL, 1, 1);
                    }
                }
            }
            if (sklp2 <= 0) {
                if (!(enemy == null) && enemy.isAlive()) {
                    if (this instanceof OceanizedEvokerEntity) {
                        this.setAnimation("animation.oceanized_evoker.spell");
                    }
                    if (world instanceof Level _level) {
                        _level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.EVOKER_CAST_SPELL, SoundSource.NEUTRAL, 1, 1);
                    }
                    if ((Entity) this instanceof OceanizedEvokerEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_skillp2, 300);
                    if (!this.level().isClientSide())
                        this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 9, false, false));
                    assert Boolean.TRUE; //#dbg:EvokerSkill:evo_skl_2
                    CaerulaArborMod.queueServerWork(10, () -> {
                        new Object() {
                            void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                                if (((Entity) OceanizedEvokerEntity.this).isAlive()) {
                                    double angl;
                                    double d;
                                    double tx;
                                    double tz;
                                    double rd;
                                    if (!(EntityUtils.getSeabornNum(world, x, y, z) >= (world.getLevelData().getGameRules().getInt(CAGameRules.CLONE_NUMBER_LIMIT)))) {
                                        angl = Mth.nextDouble(RandomSource.create(), 0, 6.283);
                                        d = Mth.nextDouble(RandomSource.create(), 1, 2);
                                        tx = x + d * Math.sin(angl);
                                        tz = z + d * Math.cos(angl);
                                        rd = Mth.nextDouble(RandomSource.create(), 0, 1);
                                        if (MapVariables.get(world).strategy_grow >= 4) {
                                            rd = Mth.nextDouble(RandomSource.create(), 0, 1.025);
                                        }
                                        if (rd < 0.5) {
                                            if (world instanceof ServerLevel _level) {
                                                Entity entityToSpawn = CAEntities.OCEANIZED_VEX.get().spawn(_level, BlockPos.containing(tx, y + 1.5, tz), MobSpawnType.MOB_SUMMONED);
                                                if (entityToSpawn != null) {
                                                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                                                }
                                            }
                                        } else if (rd < 0.65) {
                                            if (world instanceof ServerLevel _level) {
                                                Entity entityToSpawn = CAEntities.FLY_FISH.get().spawn(_level, BlockPos.containing(tx, y + 1.5, tz), MobSpawnType.MOB_SUMMONED);
                                                if (entityToSpawn != null) {
                                                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                                                }
                                            }
                                        } else if (rd < 0.8) {
                                            if (world instanceof ServerLevel _level) {
                                                Entity entityToSpawn = CAEntities.OCEANIZED_SPIDER.get().spawn(_level, BlockPos.containing(tx, y + 1.5, tz), MobSpawnType.MOB_SUMMONED);
                                                if (entityToSpawn != null) {
                                                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                                                }
                                            }
                                        } else if (rd < 0.95) {
                                            if (world instanceof ServerLevel _level) {
                                                Entity entityToSpawn = CAEntities.FLOATER_PROKARYOTE.get().spawn(_level, BlockPos.containing(tx, y + 1.5, tz), MobSpawnType.MOB_SUMMONED);
                                                if (entityToSpawn != null) {
                                                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                                                }
                                            }
                                        } else if (rd < 1) {
                                            if (world instanceof ServerLevel _level) {
                                                Entity entityToSpawn = CAEntities.FLEE_FISH.get().spawn(_level, BlockPos.containing(tx, y + 1.5, tz), MobSpawnType.MOB_SUMMONED);
                                                if (entityToSpawn != null) {
                                                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                                                }
                                            }
                                        } else {
                                            if (world instanceof ServerLevel _level) {
                                                Entity entityToSpawn = CAEntities.IZUMIK_OFFSPRING.get().spawn(_level, BlockPos.containing(tx, y + 1.5, tz), MobSpawnType.MOB_SUMMONED);
                                                if (entityToSpawn != null) {
                                                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                                                }
                                            }
                                        }
                                        if (world instanceof ServerLevel _level)
                                            _level.sendParticles(CAParticles.EDERMAN_PTC.get(), tx, (y + 2), tz, 16, 0.5, 0.5, 0.5, 0.15);
                                    }
                                }
                                final int tick2 = ticks;
                                CaerulaArborMod.queueServerWork(tick2, () -> {
                                    if (timedlooptotal > timedloopiterator + 1) {
                                        timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                                    }
                                });
                            }
                        }.timedLoop(0, 4, 5);
                    });
                }
            } else {
                if ((Entity) this instanceof OceanizedEvokerEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_skillp2, (int) (sklp2 - 1));
                if (sklp2 == 20) {
                    if (world instanceof Level _level) {
                        _level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.NEUTRAL, 1, 1);
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

    private void spawnLinearFangs(LevelAccessor world, double index) {
        double dist;
        double vx;
        double vy;
        double vz;
        double fy;
        Entity enemy = this.getTarget();
        if (enemy != null) {
            dist = Math.round(this.distanceTo(enemy));
            vx = enemy.getX() - this.getX();
            vy = enemy.getY() - this.getY();
            vz = enemy.getZ() - this.getZ();
            vx = this.getX() + vx * (index / dist);
            vy = this.getY() + vy * (index / dist);
            vz = this.getZ() + vz * (index / dist);
            fy = 114514;
            for (int dy = 0; dy <= 3; dy++) {
                BlockState target = world.getBlockState(BlockPos.containing(vx, vy + dy, vz));
                if (target.canBeReplaced() && world.getBlockFloorHeight(BlockPos.containing(vx, vy + dy - 1, vz)) > 0) {
                    fy = vy + dy;
                    break;
                }
                target = world.getBlockState(BlockPos.containing(vx, vy - dy, vz));
                if (target.canBeReplaced() && world.getBlockFloorHeight(BlockPos.containing(vx, vy - dy - 1, vz)) > 0) {
                    fy = vy - dy;
                    break;
                }
            }
            if (fy <= 114513) {
                if (world instanceof ServerLevel _level) {
                    Entity entityToSpawn = EntityType.EVOKER_FANGS.spawn(_level, BlockPos.containing(vx, fy, vz), MobSpawnType.MOB_SUMMONED);
                    if (entityToSpawn != null) {
                        entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                    }
                }
            }
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float flval) {
        FleefishBulletEntity.shoot(this, target, (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttributeValue(Attributes.ATTACK_DAMAGE) : 0) * (3.2 / 3.0));
    }


    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.16);
        builder = builder.add(Attributes.MAX_HEALTH, 50);
        builder = builder.add(Attributes.ARMOR, 3);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
        builder = builder.add(Attributes.FOLLOW_RANGE, 24);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

            ) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_evoker.move"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_evoker.idle"));
        }
        return PlayState.STOP;
    }

    private PlayState attackingPredicate(AnimationState event) {
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
            this.swinging = true;
            this.lastSwing = level().getGameTime();
        }
        if (this.swinging && this.lastSwing + 20L <= level().getGameTime()) {
            this.swinging = false;
        }
        if ((this.swinging || this.entityData.get(SHOOT)) && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_evoker.attack"));
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
