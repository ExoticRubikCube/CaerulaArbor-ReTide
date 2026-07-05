package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CAAttributes;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAItems;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;

public class OceanizedShulkerEntity extends SeaMonster {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(OceanizedShulkerEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(OceanizedShulkerEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_SHOOT_DELAY = SynchedEntityData.defineId(OceanizedShulkerEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<String> DATA_DIRECTION = SynchedEntityData.defineId(OceanizedShulkerEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Boolean> DATA_WALKING = SynchedEntityData.defineId(OceanizedShulkerEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> DATA_PEEK_TIME = SynchedEntityData.defineId(OceanizedShulkerEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(OceanizedShulkerEntity.class, EntityDataSerializers.INT);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";

    public OceanizedShulkerEntity(Level world) {
        this(CAEntities.OCEANIZED_SHULKER.get(), world);
    }

    public OceanizedShulkerEntity(EntityType<OceanizedShulkerEntity> type, Level world) {
        super(type, world);
        xpReward = 0;
        setNoAi(false);
        setMaxUpStep(1f);
        setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SHOOT, false);
        this.entityData.define(DATA_ANIMATION, "undefined");
        this.entityData.define(DATA_SHOOT_DELAY, 0);
        this.entityData.define(DATA_DIRECTION, "up");
        this.entityData.define(DATA_WALKING, false);
        this.entityData.define(DATA_PEEK_TIME, 0);
        this.entityData.define(DATA_VARIANT, 0);
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        Entity entity = this;
        return EntityUtils.isAlive(entity);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1, false) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return 4;
            }

            @Override
            public boolean canUse() {
                return super.canUse() && OceanizedShulkerEntity.this.isAlive() && OceanizedShulkerEntity.this.isWalking();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && OceanizedShulkerEntity.this.isAlive() && OceanizedShulkerEntity.this.isWalking();
            }

        });
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1) {
            @Override
            public boolean canUse() {
                return super.canUse() && OceanizedShulkerEntity.this.isAlive() && OceanizedShulkerEntity.this.isWalking();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && OceanizedShulkerEntity.this.isAlive() && OceanizedShulkerEntity.this.isWalking();
            }
        });
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && OceanizedShulkerEntity.this.isAlive() && OceanizedShulkerEntity.this.isWalking();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && OceanizedShulkerEntity.this.isAlive() && OceanizedShulkerEntity.this.isWalking();
            }
        });
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
        return SoundEvents.SHULKER_AMBIENT;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return peekTime() > 0 ? SoundEvents.SHULKER_HURT : SoundEvents.SHULKER_HURT_CLOSED;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.SHULKER_DEATH;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (peekTime() <= 0 && source.getDirectEntity() instanceof AbstractArrow)
            return false;
        if (source.is(DamageTypes.FALL))
            return false;
        if (source.is(DamageTypes.DROWN))
            return false;
        return super.hurt(source, amount);
    }

    @Override
    public void setHealth(float pHealth) {
        if (pHealth <= 0 && startWalking()) return;
        if (isBedrock() && peekTime() <= 0) return;
        super.setHealth(pHealth);
    }

    @Override
    public void die(DamageSource source) {
        if (!startWalking()) super.die(source);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
        if (((Entity) this instanceof OceanizedShulkerEntity datEntI ? datEntI.getEntityData().get(DATA_VARIANT) : 0) == 0) {
            if ((Entity) this instanceof OceanizedShulkerEntity datEntSetI)
                datEntSetI.getEntityData().set(DATA_VARIANT, Mth.nextInt(RandomSource.create(), 0, 1));
        }
        return super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        InteractionResult sup = super.mobInteract(player, hand);
        InteractionResult ths = InteractionResult.PASS;
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        if (((Entity) this instanceof OceanizedShulkerEntity datEntI ? datEntI.getEntityData().get(DATA_VARIANT) : 0) < 2) {
            if (player.getMainHandItem().getItem() == CAItems.COMPLEX_CHITIN.get()) {
                if ((Entity) this instanceof OceanizedShulkerEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_VARIANT, 2);
                if (this.getAttributes().hasAttribute(Attributes.MAX_HEALTH))
                    this.getAttribute(Attributes.MAX_HEALTH)
                            .setBaseValue(((this.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? this.getAttribute(Attributes.MAX_HEALTH).getBaseValue() : 0) * 2));
                if (this.getAttributes().hasAttribute(Attributes.ARMOR))
                    this.getAttribute(Attributes.ARMOR)
                            .setBaseValue(((this.getAttributes().hasAttribute(Attributes.ARMOR) ? this.getAttribute(Attributes.ARMOR).getBaseValue() : 0) * 1.5));
                if (this.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE.get()))
                    this.getAttribute(CAAttributes.GENERAL_DEFENSE.get())
                            .setBaseValue(((this.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE.get())
                                    ? this.getAttribute(CAAttributes.GENERAL_DEFENSE.get()).getBaseValue()
                                    : 0) + 5));
                if (this.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get()))
                    this.getAttribute(CAAttributes.MAGIC_RESISTANCE.get())
                            .setBaseValue(((this.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get())
                                    ? this.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).getBaseValue()
                                    : 0) + 35));
                if (this.getAttributes().hasAttribute(CAAttributes.SANITY_RESISTANCE.get()))
                    this.getAttribute(CAAttributes.SANITY_RESISTANCE.get())
                            .setBaseValue(((this.getAttributes().hasAttribute(CAAttributes.SANITY_RESISTANCE.get())
                                    ? this.getAttribute(CAAttributes.SANITY_RESISTANCE.get()).getBaseValue()
                                    : 0) + 35));
                player.getMainHandItem().shrink(1);
                if (world instanceof Level level) {
                    if (!level.isClientSide()) {
                        level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.SMITHING_TABLE_USE, SoundSource.HOSTILE, 1, 1);
                    } else {
                        level.playLocalSound(x, y, z, SoundEvents.SMITHING_TABLE_USE, SoundSource.HOSTILE, 1, 1, false);
                    }
                }
                this.setHealth(this.getMaxHealth());
                if ((Entity) player instanceof ServerPlayer serverPlayer) {
                    Advancement adv = serverPlayer.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "construction"));
                    AdvancementProgress ap = serverPlayer.getAdvancements().getOrStartProgress(adv);
                    if (!ap.isDone()) {
                        for (String criteria : ap.getRemainingCriteria())
                            serverPlayer.getAdvancements().award(adv, criteria);
                    }
                }
                ths = InteractionResult.SUCCESS;
            } else if ( player.getMainHandItem().getItem() == Blocks.BEDROCK.asItem()) {
                if ((Entity) this instanceof OceanizedShulkerEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_VARIANT, 3);
                if (this.getAttributes().hasAttribute(Attributes.MAX_HEALTH))
                    this.getAttribute(Attributes.MAX_HEALTH)
                            .setBaseValue(((this.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? this.getAttribute(Attributes.MAX_HEALTH).getBaseValue() : 0) * 18));
                if (this.getAttributes().hasAttribute(Attributes.ARMOR))
                    this.getAttribute(Attributes.ARMOR)
                            .setBaseValue(((this.getAttributes().hasAttribute(Attributes.ARMOR) ? this.getAttribute(Attributes.ARMOR).getBaseValue() : 0) * 10));
                if (this.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE.get()))
                    this.getAttribute(CAAttributes.GENERAL_DEFENSE.get())
                            .setBaseValue(((this.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE.get())
                                    ? this.getAttribute(CAAttributes.GENERAL_DEFENSE.get()).getBaseValue()
                                    : 0) + 32767));
                if (this.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get()))
                    this.getAttribute(CAAttributes.MAGIC_RESISTANCE.get())
                            .setBaseValue(((this.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get())
                                    ? this.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).getBaseValue()
                                    : 0) + 100));
                if (this.getAttributes().hasAttribute(CAAttributes.SANITY_RESISTANCE.get()))
                    this.getAttribute(CAAttributes.SANITY_RESISTANCE.get())
                            .setBaseValue(((this.getAttributes().hasAttribute(CAAttributes.SANITY_RESISTANCE.get())
                                    ? this.getAttribute(CAAttributes.SANITY_RESISTANCE.get()).getBaseValue()
                                    : 0) + 100));
                player.getMainHandItem().shrink(1);
                this.setHealth(this.getMaxHealth());
                if (world instanceof Level level) {
                    if (!level.isClientSide()) {
                        level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.SMITHING_TABLE_USE, SoundSource.HOSTILE, 1, 1);
                    } else {
                        level.playLocalSound(x, y, z, SoundEvents.SMITHING_TABLE_USE, SoundSource.HOSTILE, 1, 1, false);
                    }
                }
                ths = InteractionResult.SUCCESS;
            }
        }
        if (ths == InteractionResult.PASS) return sup;
        return ths;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("ShootDelay", this.entityData.get(DATA_SHOOT_DELAY));
        compound.putString("Direction", this.entityData.get(DATA_DIRECTION));
        compound.putBoolean("Walking", this.entityData.get(DATA_WALKING));
        compound.putInt("PeekTime", this.entityData.get(DATA_PEEK_TIME));
        compound.putInt("Variant", this.entityData.get(DATA_VARIANT));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("ShootDelay")) {
            this.entityData.set(DATA_SHOOT_DELAY, compound.getInt("ShootDelay"));
        }
        if (compound.contains("Direction")) {
            this.entityData.set(DATA_DIRECTION, compound.getString("Direction"));
        }
        if (compound.contains("Walking")) {
            this.entityData.set(DATA_WALKING, compound.getBoolean("Walking"));
        }
        if (compound.contains("PeekTime")) {
            this.entityData.set(DATA_PEEK_TIME, compound.getInt("PeekTime"));
        }
        if (compound.contains("Variant")) {
            this.entityData.set(DATA_VARIANT, compound.getInt("Variant"));
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Direction dire;
        Direction curDire = Direction.NORTH;
        String curDIreName;
        boolean isAttached;
        Entity enemy;
        double peekTime;
        double shootDelay;
        double variant;
        if (this.isAlive()) {
            if (tickCount <= 3) {
                {
                    LivingEntity ent = this;
                    ent.setYRot(0);
                    ent.setXRot(0);
                    ent.setYBodyRot(ent.getYRot());
                    ent.setYHeadRot(ent.getYRot());
                    ent.yRotO = ent.getYRot();
                    ent.xRotO = ent.getXRot();
                    ent.yBodyRotO = ent.getYRot();
                    ent.yHeadRotO = ent.getYRot();
                }
            }
            variant = (Entity) this instanceof OceanizedShulkerEntity datEntI ? datEntI.getEntityData().get(DATA_VARIANT) : 0;
            curDIreName = (Entity) this instanceof OceanizedShulkerEntity datEntS ? datEntS.getEntityData().get(DATA_DIRECTION) : "";
            Direction.byName(curDIreName);
            peekTime = (Entity) this instanceof OceanizedShulkerEntity datEntI ? datEntI.getEntityData().get(DATA_PEEK_TIME) : 0;
            shootDelay = (Entity) this instanceof OceanizedShulkerEntity datEntI ? datEntI.getEntityData().get(DATA_SHOOT_DELAY) : 0;
            isAttached = !((Entity) this instanceof OceanizedShulkerEntity datEntL11 && datEntL11.getEntityData().get(DATA_WALKING));
            if (isAttached) {
                if (!canStay(world, curDire)) {
                    dire = getShulkerDirection(world);
                    if (!(dire == null)) {
                        if ((Entity) this instanceof OceanizedShulkerEntity datEntSetS)
                            datEntSetS.getEntityData().set(DATA_DIRECTION, dire.toString());
                    } else {
                        double dx;
                        double dy;
                        double dz;
                        Direction tDIre;
                        for (int index0 = 0; index0 < 5; index0++) {
                            dx = Mth.nextInt(RandomSource.create(), -9, 9);
                            dy = Mth.nextInt(RandomSource.create(), -9, 9);
                            dz = Mth.nextInt(RandomSource.create(), -9, 9);
                            if (y + dy < -63) {
                                continue;
                            }
                            tDIre = getShulkerDirection(world, x + dx, y + dy, z + dz);
                            if (!(tDIre == null)) {
                                if (world instanceof Level level) {
                                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.SHULKER_TELEPORT, SoundSource.HOSTILE, 1, 1);
                                }
                                this.teleportTo((x + dx), (y + dy), (z + dz));
                                if ((Entity) this instanceof OceanizedShulkerEntity datEntSetS)
                                    datEntSetS.getEntityData().set(DATA_DIRECTION, tDIre.toString());
                                break;
                            }
                        }
                    }
                }
                setDeltaMovement(new Vec3(0, 0, 0));
                if (!world.isClientSide()) {
                    if (peekTime <= 0) {
                        if (Math.random() < 0.01) {
                            if ((Entity) this instanceof OceanizedShulkerEntity datEntSetI)
                                datEntSetI.getEntityData().set(DATA_PEEK_TIME, Mth.nextInt(RandomSource.create(), 60, 160));
                            if (Math.random() < 0.5) {
                                if (this instanceof OceanizedShulkerEntity) {
                                    this.setAnimation("animation.oceanized_shulker.open1");
                                }
                            } else {
                                if (this instanceof OceanizedShulkerEntity) {
                                    this.setAnimation("animation.oceanized_shulker.open2");
                                }
                            }
                            if (world instanceof Level level) {
                                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.SHULKER_OPEN, SoundSource.HOSTILE, 1, 1);
                            }
                        }
                    } else {
                        if (peekTime <= 1) {
                            if (Math.random() < 0.5) {
                                if (this instanceof OceanizedShulkerEntity) {
                                    this.setAnimation("animation.oceanized_shulker.close1");
                                }
                            } else {
                                if (this instanceof OceanizedShulkerEntity) {
                                    this.setAnimation("animation.oceanized_shulker.close2");
                                }
                            }
                            if (world instanceof Level level) {
                                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.SHULKER_CLOSE, SoundSource.HOSTILE, 1, 1);
                            }
                        }
                    }
                }
            } else {
                if ((Entity) this instanceof OceanizedShulkerEntity datEntSetS)
                    datEntSetS.getEntityData().set(DATA_DIRECTION, "up");
            }
            if (peekTime <= 0) {
                if (variant == 2) {
                    if (!this.level().isClientSide())
                        this.addEffect(new MobEffectInstance(CAMobEffects.SHULKER_BUFF.get(), 5, 2, false, false));
                } else if (variant == 3) {
                    if (!this.level().isClientSide())
                        this.addEffect(new MobEffectInstance(CAMobEffects.SHULKER_BUFF.get(), 5, 9, false, false));
                } else {
                    if (!this.level().isClientSide())
                        this.addEffect(new MobEffectInstance(CAMobEffects.SHULKER_BUFF.get(), 5, 0, false, false));
                }
            } else {
                if ((Entity) this instanceof OceanizedShulkerEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_PEEK_TIME, (int) (peekTime - 1));
                this.removeEffect(CAMobEffects.SHULKER_BUFF.get());
            }
            enemy = this.getTarget();
            if (shootDelay <= 0) {
                if (!(enemy == null) && enemy.isAlive()) {
                    if (distanceTo(enemy) <= 24) {
                        if ((Entity) this instanceof OceanizedShulkerEntity datEntSetI)
                            datEntSetI.getEntityData().set(DATA_SHOOT_DELAY, Mth.nextInt(RandomSource.create(), 40, 60));
                        if ((Entity) this instanceof OceanizedShulkerEntity datEntSetI)
                            datEntSetI.getEntityData().set(DATA_PEEK_TIME, 20);
                        if (isAttached) {
                            if (Math.random() < 0.5) {
                                if (this instanceof OceanizedShulkerEntity) {
                                    this.setAnimation("animation.oceanized_shulker.attack1");
                                }
                            } else {
                                if (this instanceof OceanizedShulkerEntity) {
                                    this.setAnimation("animation.oceanized_shulker.attack2");
                                }
                            }
                        } else {
                            if (this instanceof OceanizedShulkerEntity) {
                                this.setAnimation("animation.oceanized_shulker.attack_withfeet");
                            }
                            if (!this.level().isClientSide())
                                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 9, false, false));
                        }
                        shootShulkerBullet(enemy);
                        if (Math.random() < 0.5) {
                            shootShulkerBullet(enemy);
                        }
                    }
                }
            } else {
                if ((Entity) this instanceof OceanizedShulkerEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_SHOOT_DELAY, (int) (shootDelay - 1));
            }
        }
        this.refreshDimensions();
    }

    @Override
    public EntityDimensions getDimensions(Pose p_33597_) {
        return super.getDimensions(p_33597_).scale((float) 1);
    }

    public boolean startWalking() {
        if (isWalking())
            return false;
        if (this.getAttributes().hasAttribute(Attributes.MAX_HEALTH))
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getMaxHealth() * 0.6);
        if (this.getAttributes().hasAttribute(Attributes.ARMOR))
            this.getAttribute(Attributes.ARMOR).setBaseValue(this.getArmorValue() * 0.4);
        this.setHealth(this.getMaxHealth());
        if (!this.level().isClientSide())
            this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 20, 9, false, false));
        if (!this.level().isClientSide())
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 9, false, false));
        setAnimation("animation.oceanized_shulker.rise");
        setWalking(true);
        setPeekTime(0);
        return true;
    }

    public void shootShulkerBullet(Entity target) {
        if (target == null || !(this.level() instanceof ServerLevel level))
            return;
        Direction dire = this.getAttachDirection();
        Direction.Axis axis = dire != null ? dire.getAxis() : Direction.Axis.Y;
        ShulkerBullet sBullet = new ShulkerBullet(level, this, target, axis);
        sBullet.getPersistentData().putBoolean("oceanized", true);
        level.addFreshEntity(sBullet);
        if (!this.level().isClientSide()) {
            this.level().playSound(null, BlockPos.containing(this.getX(), this.getY(), this.getZ()), SoundEvents.SHULKER_SHOOT, SoundSource.HOSTILE, 1, 1);
        } else {
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.SHULKER_SHOOT, SoundSource.HOSTILE, 1, 1, false);
        }
    }


    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.2);
        builder = builder.add(Attributes.MAX_HEALTH, 55);
        builder = builder.add(Attributes.ARMOR, 5);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
        builder = builder.add(Attributes.FOLLOW_RANGE, 24);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 1);
        return builder;
    }

    private PlayState movementPredicate(AnimationState<?> event) {
        if (this.isDeadOrDying()) {
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_shulker.die"));
        }
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.1F && event.getLimbSwingAmount() < 0.1F))) {
                if (this.isWalking())
                    return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_shulker.move"));
                if (peekTime() <= 0)
                    event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_shulker.idle"));
                event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_shulker.idle_peeking"));
            }
            if (this.isWalking())
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_shulker.idel_withfeet"));
            else {
                if (this.peekTime() > 0)
                    return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_shulker.idle_peeking"));
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_shulker.idle"));
            }
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
        if (this.deathTime >= 20) {
            this.remove(RemovalReason.KILLED);
            LevelAccessor world = this.level();
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();
            double v;
            if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                v = (Entity) this instanceof OceanizedShulkerEntity datEntI ? datEntI.getEntityData().get(DATA_VARIANT) : 0;
                if (v == 2) {
                    if (world instanceof ServerLevel level) {
                        ItemEntity entityToSpawn = new ItemEntity(level, x, y, z, new ItemStack(CAItems.COMPLEX_CHITIN.get()));
                        entityToSpawn.setPickUpDelay(10);
                        entityToSpawn.setUnlimitedLifetime();
                        level.addFreshEntity(entityToSpawn);
                    }
                } else if (v == 3) {
                    if (world instanceof ServerLevel level) {
                        ItemEntity entityToSpawn = new ItemEntity(level, x, y, z, new ItemStack(Blocks.BEDROCK));
                        entityToSpawn.setPickUpDelay(5);
                        entityToSpawn.setUnlimitedLifetime();
                        level.addFreshEntity(entityToSpawn);
                    }
                }
            }
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
        data.add(new AnimationController<>(this, "movement", 2, this::movementPredicate));
        data.add(new AnimationController<>(this, "procedure", 2, this::procedurePredicate));
    }

    public boolean isWalking() {
        return this.entityData.get(DATA_WALKING);
    }

    public void setWalking(boolean walking) {
        this.entityData.set(DATA_WALKING, walking);
    }

    private int peekTime() {
        return this.entityData.get(DATA_PEEK_TIME);
    }

    private void setPeekTime(int peekTime) {
        this.entityData.set(DATA_PEEK_TIME, peekTime);
    }

    public Direction getAttachDirection() {
        return Direction.byName(this.entityData.get(DATA_DIRECTION));
    }

    private boolean isBedrock() {
        return this.entityData.get(DATA_VARIANT) == 3;
    }

    public boolean canStay(LevelAccessor world, Direction dire) {
        if (dire == null)
            return false;
        Direction opposite = dire.getOpposite();
        return world.getBlockState(BlockPos.containing(this.getX() + opposite.getStepX(), this.getY() + opposite.getStepY(), this.getZ() + opposite.getStepZ())).isFaceSturdy(world, BlockPos.containing(this.getX() + opposite.getStepX(), this.getY() + opposite.getStepY(), this.getZ() + opposite.getStepZ()), dire);
    }

    public Direction getShulkerDirection(LevelAccessor world) {
        if (world.getBlockFloorHeight(BlockPos.containing(this.getX(), this.getY(), this.getZ())) > 0) {
            return null;
        }
        for (Direction directioniterator : Direction.values()) {
            if (canStay(world, directioniterator)) {
                return directioniterator;
            }
        }
        return null;
    }

    public static Direction getShulkerDirection(LevelAccessor world, double x, double y, double z) {
        if (world.getBlockFloorHeight(BlockPos.containing(x, y, z)) > 0) {
            return null;
        }
        for (Direction directioniterator : Direction.values()) {
            if (canStayAt(world, x, y, z, directioniterator)) {
                return directioniterator;
            }
        }
        return null;
    }

    private static boolean canStayAt(LevelAccessor world, double x, double y, double z, Direction dire) {
        if (dire == null)
            return false;
        Direction opposite = dire.getOpposite();
        return world.getBlockState(BlockPos.containing(x + opposite.getStepX(), y + opposite.getStepY(), z + opposite.getStepZ())).isFaceSturdy(world, BlockPos.containing(x + opposite.getStepX(), y + opposite.getStepY(), z + opposite.getStepZ()), dire);
    }


    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}
