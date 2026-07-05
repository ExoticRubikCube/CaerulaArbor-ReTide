package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CAAttributes;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAItems;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Comparator;
import java.util.List;

public class OceanizedFoxEntity extends SeaMonster {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(OceanizedFoxEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(OceanizedFoxEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_SKILLP = SynchedEntityData.defineId(OceanizedFoxEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> DATA_SLEEPING = SynchedEntityData.defineId(OceanizedFoxEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> DATA_ACTION_TIME = SynchedEntityData.defineId(OceanizedFoxEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(OceanizedFoxEntity.class, EntityDataSerializers.INT);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";

    public OceanizedFoxEntity(Level world) {
        this(CAEntities.OCEANIZED_FOX.get(), world);
    }

    public OceanizedFoxEntity(EntityType<OceanizedFoxEntity> type, Level world) {
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
        this.entityData.define(DATA_SKILLP, 10);
        this.entityData.define(DATA_SLEEPING, false);
        this.entityData.define(DATA_ACTION_TIME, 0);
        this.entityData.define(DATA_DURATION, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 2, false) {
            @Override
            protected double getAttackReachSqr(@NotNull LivingEntity entity) {
                return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth();
            }

            @Override
            public boolean canUse() {
                return super.canUse() && OceanizedFoxEntity.this.notSleeping();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && OceanizedFoxEntity.this.notSleeping();
            }

        });
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Chicken.class, true, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Rabbit.class, true, false));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Turtle.class, true, false));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Salmon.class, true, true));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, TropicalFish.class, true, true));
        this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, Pufferfish.class, true, true));
        this.goalSelector.addGoal(9, new TemptGoal(this, 1, Ingredient.of(CAItems.CANNED_CHERRY.get()), false));
        this.goalSelector.addGoal(10, new RemoveBlockGoal(Blocks.SWEET_BERRY_BUSH, this, 1, 3));
        this.goalSelector.addGoal(11, new RandomStrollGoal(this, 1) {
            @Override
            public boolean canUse() {
                return super.canUse() && OceanizedFoxEntity.this.notSleeping();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && OceanizedFoxEntity.this.notSleeping();
            }
        });
        this.goalSelector.addGoal(12, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && OceanizedFoxEntity.this.notSleeping();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && OceanizedFoxEntity.this.notSleeping();
            }
        });
        this.goalSelector.addGoal(13, new FloatGoal(this));
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public SoundEvent getAmbientSound() {
        return SoundEvents.FOX_AMBIENT;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return SoundEvents.FOX_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.FOX_DEATH;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        setShiftKeyDown(false);
        if ((Entity) this instanceof OceanizedFoxEntity _datEntSetL)
            _datEntSetL.getEntityData().set(DATA_SLEEPING, false);
        if (source.is(DamageTypes.CACTUS))
            return false;
        if (source.is(DamageTypes.SWEET_BERRY_BUSH))
            return false;
        if (source.is(DamageTypes.DROWN))
            return false;
        return super.hurt(source, amount);
    }

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Skillp", this.entityData.get(DATA_SKILLP));
        compound.putBoolean("Sleeping", this.entityData.get(DATA_SLEEPING));
        compound.putInt("ActionTime", this.entityData.get(DATA_ACTION_TIME));
        compound.putInt("Duration", this.entityData.get(DATA_DURATION));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Skillp")) {
            this.entityData.set(DATA_SKILLP, compound.getInt("Skillp"));
        }
        if (compound.contains("Sleeping")) {
            this.entityData.set(DATA_SLEEPING, compound.getBoolean("Sleeping"));
        }
        if (compound.contains("ActionTime")) {
            this.entityData.set(DATA_ACTION_TIME, compound.getInt("ActionTime"));
        }
        if (compound.contains("Duration")) {
            this.entityData.set(DATA_DURATION, compound.getInt("Duration"));
        }
	}

    @Override
    public void awardKillScore(Entity entity, int score, DamageSource damageSource) {
        super.awardKillScore(entity, score, damageSource);
        this.heal(2);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        boolean sneak;
        double time_stamp;
        double skillp;
        double dura;
        Entity enemy;
        if (this.isAlive()) {
            if (tickCount % 10 == 0) {
                time_stamp = (Entity) this instanceof OceanizedFoxEntity _datEntI ? _datEntI.getEntityData().get(DATA_ACTION_TIME) : 0;
                sneak = (Entity) this instanceof OceanizedFoxEntity _datEntL3 && _datEntL3.getEntityData().get(DATA_SLEEPING);
                if (time_stamp > 0) {
                    if ((Entity) this instanceof OceanizedFoxEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_ACTION_TIME, (int) (time_stamp - 1));
                } else if (Math.random() < 0.02) {
                    if (sneak) {
                        if ((Entity) this instanceof OceanizedFoxEntity _datEntSetL)
                            _datEntSetL.getEntityData().set(DATA_SLEEPING, false);
                        if ((Entity) this instanceof OceanizedFoxEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_ACTION_TIME, 200);
                    } else if (!this.isAggressive()) {
                        if ((Entity) this instanceof OceanizedFoxEntity _datEntSetL)
                            _datEntSetL.getEntityData().set(DATA_SLEEPING, true);
                        if ((Entity) this instanceof OceanizedFoxEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_ACTION_TIME, 200);
                    }
                }
                setShiftKeyDown(sneak);
            }
            enemy = this.getTarget();
            if (!(enemy == null) && enemy.isAlive()) {
                setShiftKeyDown(false);
                if ((Entity) this instanceof OceanizedFoxEntity _datEntSetL)
                    _datEntSetL.getEntityData().set(DATA_SLEEPING, false);
            }
            skillp = (Entity) this instanceof OceanizedFoxEntity _datEntI ? _datEntI.getEntityData().get(DATA_SKILLP) : 0;
            dura = (Entity) this instanceof OceanizedFoxEntity _datEntI ? _datEntI.getEntityData().get(DATA_DURATION) : 0;
            if (dura > 0) {
                if ((Entity) this instanceof OceanizedFoxEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_DURATION, (int) (dura - 1));
            }
            if (skillp > 0) {
                if ((Entity) this instanceof OceanizedFoxEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_SKILLP, (int) (skillp - 1));
            } else {
                if (!(enemy == null) && enemy.isAlive()) {
                    if (distanceTo(enemy) <= 4) {
                        if ((Entity) this instanceof OceanizedFoxEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_DURATION, 45);
                        if ((Entity) this instanceof OceanizedFoxEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_SKILLP, 200);
                        if (this instanceof OceanizedFoxEntity) {
                            this.setAnimation("animation.oceanized_fox.jump");
                        }
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 25, 9, false, false));
                        push((getLookAngle().x * 0.25), 0.25, (getLookAngle().z * 0.25));
                        CaerulaArborMod.queueServerWork(20, () -> {
                            if (this.isAlive() && !(((Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) == null)) {
                                if (world instanceof Level _level) {
                                    _level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.FOX_TELEPORT, SoundSource.HOSTILE, 1, 1);
                                }
                                {
                                    Entity _ent = this;
                                    _ent.teleportTo((((Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null).getX()), (((Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null).getY() + 0.25),
                                            (((Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null).getZ()));
                                    if (_ent instanceof ServerPlayer _serverPlayer)
                                        _serverPlayer.connection.teleport((((Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null).getX()), (((Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null).getY() + 0.25),
                                                (((Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null).getZ()), _ent.getYRot(), _ent.getXRot());
                                }
                            }
                        });
                        CaerulaArborMod.queueServerWork(30, () -> {
                            if (this.isAlive()) {
                                Entity enemy1;
                                double damage;
                                if (world instanceof Level _level) {
                                    _level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.FOX_AGGRO, SoundSource.HOSTILE, 2, 1);
                                }
                                damage = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
                                enemy1 = (Entity) this instanceof Mob _mobEnt ? _mobEnt.getTarget() : null;
                                {
                                    final Vec3 _center = new Vec3((getX()), (getY()), (getZ()));
                                    List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(6 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                                    for (Entity entityiterator : _entfound) {
                                        if (!(entityiterator instanceof LivingEntity)) {
                                            continue;
                                        }
                                        if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                                            if (!(entityiterator == enemy1)) {
                                                continue;
                                            }
                                        }
                                        if (entityiterator == this) {
                                            continue;
                                        }
                                        if (distanceTo(entityiterator) <= 3) {
                                            entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.MOB_ATTACK), this), (float) (damage * 1.5));
                                            if ((Entity) this instanceof LivingEntity _entity)
                                                _entity.setHealth(((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) + 1);
                                        }
                                    }
                                }
                            }
                        });
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


    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.16);
        builder = builder.add(CAAttributes.MAGIC_RESISTANCE.get(), 30);
        builder = builder.add(Attributes.MAX_HEALTH, 37);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 7);
        builder = builder.add(Attributes.FOLLOW_RANGE, 24);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.35);
        return builder;
    }

    private PlayState movementPredicate(AnimationState<?> event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

                    && !this.isAggressive()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_fox.move"));
            }
            if (this.isShiftKeyDown()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_fox.sleep"));
            }
            if (this.isAggressive() && event.isMoving()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_fox.sprint"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_fox.idle"));
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
        data.add(new AnimationController<>(this, "movement", 3, this::movementPredicate));
        data.add(new AnimationController<>(this, "procedure", 3, this::procedurePredicate));
    }

    public boolean notSleeping() {
        return !this.getEntityData().get(OceanizedFoxEntity.DATA_SLEEPING)
                && this.getEntityData().get(OceanizedFoxEntity.DATA_DURATION) <= 0;
    }


    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}
