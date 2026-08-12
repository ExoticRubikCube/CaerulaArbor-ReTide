package com.susen36.caerulaarbor.entity;

import com.susen36.babel.init.BabelAttributes;
import com.susen36.babel.init.BabelMobEffects;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.api.anim.SyncedAnimationEntity;
import com.susen36.caerulaarbor.init.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Comparator;
import java.util.List;

public class ComplexChitinGolemEntity extends IronGolem implements GeoEntity, SyncedAnimationEntity {

    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(ComplexChitinGolemEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(ComplexChitinGolemEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_SKILLP = SynchedEntityData.defineId(ComplexChitinGolemEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(ComplexChitinGolemEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> DATA_ROOTED = SynchedEntityData.defineId(ComplexChitinGolemEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> DATA_ROOT_X = SynchedEntityData.defineId(ComplexChitinGolemEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_ROOT_Z = SynchedEntityData.defineId(ComplexChitinGolemEntity.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";

    public ComplexChitinGolemEntity(Level world) {
        this(CAEntities.COMPLEX_CHITIN_GOLEM.get(), world);
    }

    public ComplexChitinGolemEntity(EntityType<ComplexChitinGolemEntity> type, Level world) {
        super(type, world);
        xpReward = 0;
        setNoAi(false);
        setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHOOT, false);
        builder.define(DATA_ANIMATION, "undefined");
        builder.define(DATA_SKILLP, 200);
        builder.define(DATA_DURATION, 0);
        builder.define(DATA_ROOTED, false);
        builder.define(DATA_ROOT_X, 0);
        builder.define(DATA_ROOT_Z, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 2, false) {

            @Override
            public boolean canUse() {
                return super.canUse() && isDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isDurative();
            }

        });
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Monster.class, true, false));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hurt = super.doHurtTarget(target);
        if (hurt && target.getBbWidth() * target.getBbHeight() <= 6) {
            target.push(0, 0.5, 0);
        }
        return hurt;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.IN_FIRE))
            return false;
        if (source.is(DamageTypes.FALL))
            return false;
        if (source.is(DamageTypes.DROWN))
            return false;
        if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
                && !source.is(CADamageTags.BYPASS_PROTECTION)) {
            amount = Math.min(amount, this.getMaxHealth() * 0.1F);
        }
        return super.hurt(source, amount);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Skillp", this.entityData.get(DATA_SKILLP));
        compound.putInt("Duration", this.entityData.get(DATA_DURATION));
        compound.putBoolean("Rooted", this.entityData.get(DATA_ROOTED));
        compound.putInt("RootX", this.entityData.get(DATA_ROOT_X));
        compound.putInt("RootZ", this.entityData.get(DATA_ROOT_Z));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Skillp")) {
            this.entityData.set(DATA_SKILLP, compound.getInt("Skillp"));
        }
        if (compound.contains("Duration")) {
            this.entityData.set(DATA_DURATION, compound.getInt("Duration"));
        }
        if (compound.contains("Rooted")) {
            this.entityData.set(DATA_ROOTED, compound.getBoolean("Rooted"));
        }
        if (compound.contains("RootX")) {
            this.entityData.set(DATA_ROOT_X, compound.getInt("RootX"));
        }
        if (compound.contains("RootZ")) {
            this.entityData.set(DATA_ROOT_Z, compound.getInt("RootZ"));
        }
    }

    @Override
    public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
        super.mobInteract(sourceentity, hand);
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Level world = this.level();
        double scale = 0;
        if (this.getHealth() >= this.getMaxHealth()) {
            return InteractionResult.PASS;
        }
        if (sourceentity.getMainHandItem().getItem() == CAItems.OCEAN_CHITIN.get()) {
            scale = 0.15;
            if (!(new Object() {
                public boolean checkGamemode(Entity ent) {
                    if (ent instanceof ServerPlayer serverPlayer) {
                        return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                    } else if (ent.level().isClientSide() && ent instanceof Player player) {
                        return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                    }
                    return false;
                }
            }.checkGamemode((Entity) sourceentity))) {
                if ((Entity) sourceentity instanceof Player player) {
                    ItemStack stktoremove = new ItemStack(CAItems.OCEAN_CHITIN.get());
                    player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                }
            }
        } else {
            if (sourceentity.getMainHandItem().getItem() == CAItems.COMPLEX_CHITIN.get()) {
                scale = 0.25;
                if (!(new Object() {
                    public boolean checkGamemode(Entity ent) {
                        if (ent instanceof ServerPlayer serverPlayer) {
                            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (ent.level().isClientSide() && ent instanceof Player player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) sourceentity))) {
                    if ((Entity) sourceentity instanceof Player player) {
                        ItemStack stktoremove = new ItemStack(CAItems.COMPLEX_CHITIN.get());
                        player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                    }
                }
            }
        }
        if (scale > 0) {
            this.setHealth((float) (this.getHealth() + this.getMaxHealth() * scale));
            if (world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.PLAYERS, 1, 1);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void baseTick() {
        super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity target;
        double sklp1;
        double dura;
        if (this.isAlive()) {
            this.removeEffect(BabelMobEffects.STUN);
            boolean root;
            double rx;
            double rz;
            double dist;
            double dist1;
            root = (Entity) this instanceof ComplexChitinGolemEntity datEntL0 && datEntL0.getEntityData().get(DATA_ROOTED);
            if (!root) {
                if (!(getDisplayName().getString()).equals(getType().getDescription().getString())) {
                    if ((Entity) this instanceof ComplexChitinGolemEntity datEntSetI1)
                        datEntSetI1.getEntityData().set(DATA_ROOT_X, (int) Math.round(x));
                    if ((Entity) this instanceof ComplexChitinGolemEntity datEntSetI1)
                        datEntSetI1.getEntityData().set(DATA_ROOT_Z, (int) Math.round(z));
                    if ((Entity) this instanceof ComplexChitinGolemEntity datEntSetL)
                        datEntSetL.getEntityData().set(DATA_ROOTED, true);
                    CaerulaArbor.LOGGER.info(("Complex Chitin Golem " + getDisplayName().getString() + "has recognize x:" + Math.round(x) + " z:" + Math.round(z) + " as base"));
                }
            } else if (Math.random() < 0.01) {
                Mob mobEnt8 = this;
                if (!mobEnt8.isAggressive()) {
                    rx = x - ((Entity) this instanceof ComplexChitinGolemEntity datEntI1 ? datEntI1.getEntityData().get(DATA_ROOT_X) : 0);
                    rz = z - ((Entity) this instanceof ComplexChitinGolemEntity datEntI1 ? datEntI1.getEntityData().get(DATA_ROOT_Z) : 0);
                    dist = new Vec3(0, 0, 0).distanceTo(new Vec3(rx, 0, rz));
                    if (dist >= 24) {
                        dist1 = Mth.nextDouble(RandomSource.create(), 4, 16);
                        this.getNavigation().moveTo(x - rx * dist1 / dist, y, z - rz * dist1 / dist, 1);
                    }
                }
            }
            sklp1 = (Entity) this instanceof ComplexChitinGolemEntity datEntI ? datEntI.getEntityData().get(DATA_SKILLP) : 0;
            dura = (Entity) this instanceof ComplexChitinGolemEntity datEntI ? datEntI.getEntityData().get(DATA_DURATION) : 0;
            target = this.getTarget();
            if (dura > 0) {
                if ((Entity) this instanceof ComplexChitinGolemEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_DURATION, (int) (dura - 1));
            }
            if (sklp1 > 0) {
                if ((Entity) this instanceof ComplexChitinGolemEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_SKILLP, (int) (sklp1 - 1));
            } else {
                if (!(target == null) && target.isAlive()) {
                    if (distanceTo(target) <= 5 && dura < 1) {
                        if ((Entity) this instanceof ComplexChitinGolemEntity datEntSetI)
                            datEntSetI.getEntityData().set(DATA_DURATION, 110);
                        if (this instanceof ComplexChitinGolemEntity) {
                            this.setAnimation("animation.complex_chitin_golem.spin");
                        }
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 110, 9, false, false));
                        if ((Entity) this instanceof ComplexChitinGolemEntity datEntSetI)
                            datEntSetI.getEntityData().set(DATA_SKILLP, 400);
                        CaerulaArbor.queueServerWork(6, () -> {
                            if (this.isAlive()) {
                                if (world instanceof Level level) {
                                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.PISTON_EXTEND, SoundSource.NEUTRAL, 2, 1);
                                }
                            }
                        });
                        CaerulaArbor.queueServerWork(13, () -> {
                            if (this.isAlive()) {
                                if (world instanceof Level level) {
                                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.PISTON_CONTRACT, SoundSource.NEUTRAL, 2, 1);
                                }
                            }
                        });
                        CaerulaArbor.queueServerWork(20, () -> {
                            if (this.isAlive()) {
                                if (!this.level().isClientSide())
                                    this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 60, 0, false, false));
                            }
                        });
                        for (int index0 = 0; index0 < 16; index0++) {
                            CaerulaArbor.queueServerWork(index0 * 3 + 26, () -> {
                                if (this.isAlive()) {
                                    double damage;
                                    final Vec3 center = new Vec3((getX()), (getY()), (getZ()));
                                    List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(10 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                                    for (Entity entityiterator : entfound) {
                                        if (!(entityiterator instanceof Monster)) {
                                            if (!(entityiterator == this.getTarget())) {
                                                continue;
                                            }
                                        }
                                        if (entityiterator == this) {
                                            continue;
                                        }
                                        if (entityiterator != null && distanceTo(entityiterator) <= 5) {
                                            damage = (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 0.75;
                                            entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.GOLEM_ATTACK, this), (float) damage);
                                        }
                                    }
                                }
                            });
                        }
                        CaerulaArbor.queueServerWork(90, () -> {
                            if (this.isAlive()) {
                                if (world instanceof Level level) {
                                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.PISTON_CONTRACT, SoundSource.NEUTRAL, 2, 1);
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
    public void aiStep() {
        super.aiStep();
        this.updateSwingTime();
    }


    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.17);
        builder = builder.add(BabelAttributes.ELEMENTAL_MODIFIER, 0.05);
        builder = builder.add(CAAttributes.MAGIC_RESISTANCE, 50);
        builder = builder.add(CAAttributes.GENERAL_DEFENSE, 12);
        builder = builder.add(Attributes.MAX_HEALTH, 675);
        builder = builder.add(Attributes.ARMOR, 16);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 28);
        builder = builder.add(Attributes.FOLLOW_RANGE, 20);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
        builder = builder.add(Attributes.STEP_HEIGHT, 1.6f);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

                    && !this.isAggressive()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.complex_chitin_golem.move"));
            }
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.complex_chitin_golem.die"));
            }
            if (this.isAggressive() && event.isMoving()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.complex_chitin_golem.run"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.complex_chitin_golem.idle"));
        }
        return PlayState.STOP;
    }

    private PlayState attackingPredicate(AnimationState event) {
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
            this.swinging = true;
            this.lastSwing = level().getGameTime();
        }
        if (this.swinging && this.lastSwing + 10L <= level().getGameTime()) {
            this.swinging = false;
        }
        if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.complex_chitin_golem.attack"));
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
        if (this.deathTime == 40) {
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
        data.add(new AnimationController<>(this, "movement", 1, this::movementPredicate));
        data.add(new AnimationController<>(this, "attacking", 1, this::attackingPredicate));
        data.add(new AnimationController<>(this, "procedure", 1, this::procedurePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    private boolean isDurative() {
        return this.isAlive() && this.getEntityData().get(DATA_DURATION) <= 0;
    }

    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}