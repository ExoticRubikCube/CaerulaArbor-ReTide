package com.susen36.caerulaarbor.entity;

import com.susen36.babel.init.BabelAttributes;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.base.SyncedAnimationEntity;
import com.susen36.caerulaarbor.init.*;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Comparator;
import java.util.List;

public class ChitinGolemEntity extends IronGolem implements GeoEntity, SyncedAnimationEntity {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(ChitinGolemEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(ChitinGolemEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_ROOT_X = SynchedEntityData.defineId(ChitinGolemEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_ROOT_Z = SynchedEntityData.defineId(ChitinGolemEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> DATA_ROOTED = SynchedEntityData.defineId(ChitinGolemEntity.class, EntityDataSerializers.BOOLEAN);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";

    public ChitinGolemEntity(Level world) {
        this(CAEntities.CHITIN_GOLEM.get(), world);
    }

    public ChitinGolemEntity(EntityType<ChitinGolemEntity> type, Level world) {
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
        builder.define(DATA_ROOT_X, 0);
        builder.define(DATA_ROOT_Z, 0);
        builder.define(DATA_ROOTED, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Monster.class, true, false));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    @Override
    public void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.IRON_GOLEM_STEP, 0.15f, 1);
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
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity sourceentity = source.getEntity();
        if (sourceentity != null) {
            double num;
            if (this.isAlive()) {
                if (!this.hasEffect(CAMobEffects.COOLDOWN_SINAL)) {
                    num = 0;
                    {
                        final Vec3 center = new Vec3(x, y, z);
                        List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(8 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                        for (Entity entityiterator : entfound) {
                            if (!(entityiterator == this) && (entityiterator instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) >= 10) {
                                num = num + 1;
                            }
                        }
                    }
                    if (distanceTo(sourceentity) <= 5) {
                        if (num >= 2) {
                            if (this instanceof ChitinGolemEntity) {
                                this.setAnimation("animation.chitgolem.smash");
                            }
                            if (!this.level().isClientSide())
                                this.addEffect(new MobEffectInstance(CAMobEffects.COOLDOWN_SINAL, 60, 0, false, false));
                            this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((sourceentity.getX()), (sourceentity.getY()), (sourceentity.getZ())));
                            CaerulaArbor.queueServerWork(13, () -> {
                                if (world instanceof Level level) {
                                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.IRON_GOLEM_DAMAGE, SoundSource.HOSTILE, 2, 1);
                                }
                                {
                                    final Vec3 center = new Vec3((x + 2 * getLookAngle().x), (y + 2), (z + 2 * getLookAngle().z));
                                    List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(4.5 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                                    for (Entity entityiterator : entfound) {
                                        if (entityiterator instanceof Monster) {
                                            entityiterator.hurt(
                                                    CADamageTypes.source(world, CADamageTypes.GOLEM_ATTACK, this), (float) ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0)
                                                            * 3.5));
                                        } else if (entityiterator == this.getTarget()) {
                                            entityiterator.hurt(
                                                    CADamageTypes.source(world, CADamageTypes.GOLEM_ATTACK, this), (float) ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0)
                                                            * 3.5));
                                        }
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
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("RootX", this.entityData.get(DATA_ROOT_X));
        compound.putInt("RootZ", this.entityData.get(DATA_ROOT_Z));
        compound.putBoolean("Rooted", this.entityData.get(DATA_ROOTED));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("RootX")) {
            this.entityData.set(DATA_ROOT_X, compound.getInt("RootX"));
        }
        if (compound.contains("RootZ")) {
            this.entityData.set(DATA_ROOT_Z, compound.getInt("RootZ"));
        }
        if (compound.contains("Rooted")) {
            this.entityData.set(DATA_ROOTED, compound.getBoolean("Rooted"));
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        super.mobInteract(player, hand);
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity entity = this;
        Level world = this.level();
        ItemStack mainHand;
        boolean isLowHealth;
        boolean isCreative;
        mainHand = player.getMainHandItem().copy();
        isLowHealth = (entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) < (entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1);
        isCreative = new Object() {
            public boolean checkGamemode(Entity ent) {
                if (ent instanceof ServerPlayer serverPlayer) {
                    return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                } else if (ent.level().isClientSide() && ent instanceof Player player) {
                    return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                }
                return false;
            }
        }.checkGamemode((Entity) player);
        if (mainHand.getItem() == CAItems.OCEAN_CHITIN.get() && isLowHealth) {
            LivingEntity living = (LivingEntity) entity;
            living.setHealth((float) (living.getHealth() + living.getMaxHealth() * 0.25));
            if (world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.PLAYERS, 1, 1);
            }
            if (!isCreative) {
                player.getMainHandItem().shrink(1);
            }
            return InteractionResult.SUCCESS;
        } else if (mainHand.getItem() == CAItems.CHITIN_INGOT.get() && isLowHealth) {
            if (entity instanceof LivingEntity livingEntity)
                livingEntity.setHealth((float) (livingEntity.getHealth() + livingEntity.getMaxHealth() * 0.5));
            if (world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.PLAYERS, 1, 1);
            }
            if (!isCreative) {
                player.getMainHandItem().shrink(1);
            }
            return InteractionResult.SUCCESS;
        } else if (mainHand.getItem() == CABlocks.COMPLEX_CHITIN_BLOCK.get().asItem() && mainHand.getCount() >= 3) {
            if (world instanceof Level level) {
                if (!level.isClientSide()) {
                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.SMITHING_TABLE_USE, SoundSource.PLAYERS, (float) 1.5, 1);
                } else {
                    level.playLocalSound(x, y, z, SoundEvents.SMITHING_TABLE_USE, SoundSource.PLAYERS, (float) 1.5, 1, false);
                }
            }
            if (!entity.level().isClientSide())
                entity.discard();
            if (world instanceof ServerLevel level) {
                Entity entityToSpawn = CAEntities.COMPLEX_CHITIN_GOLEM.get().spawn(level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.setYRot(entity.getYRot());
                    entityToSpawn.setYBodyRot(entity.getYRot());
                    entityToSpawn.setYHeadRot(entity.getYRot());
                    entityToSpawn.setXRot(entity.getXRot());
                }
            }
            if (!isCreative) {
                player.getMainHandItem().shrink(3);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void baseTick() {
        super.baseTick();
        double x = this.getX();
        double z = this.getZ();
        boolean root;
        double rx;
        double rz;
        double dist;
        double dist1;
        if (this.isAlive()) {
            root = (Entity) this instanceof ChitinGolemEntity datEntL1 && datEntL1.getEntityData().get(DATA_ROOTED);
            if (!root) {
                if (!(getDisplayName().getString()).equals(getType().getDescription().getString())) {
                    if ((Entity) this instanceof ChitinGolemEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_ROOT_X, (int) Math.round(x));
                    if ((Entity) this instanceof ChitinGolemEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_ROOT_Z, (int) Math.round(z));
                    if ((Entity) this instanceof ChitinGolemEntity datEntSetL)
                        datEntSetL.getEntityData().set(DATA_ROOTED, true);
                    CaerulaArbor.LOGGER.info(("Chitin Golem " + getDisplayName().getString() + "has recognize x:" + Math.round(x) + " z:" + Math.round(z) + " as base"));
                }
            } else if (Math.random() < 0.01 && !((Entity) this instanceof Mob mobEnt9 && mobEnt9.isAggressive())) {
                rx = x - ((Entity) this instanceof ChitinGolemEntity datEntI ? datEntI.getEntityData().get(DATA_ROOT_X) : 0);
                rz = z - ((Entity) this instanceof ChitinGolemEntity datEntI ? datEntI.getEntityData().get(DATA_ROOT_Z) : 0);
                dist = new Vec3(0, 0, 0).distanceTo(new Vec3(rx, 0, rz));
                if (dist >= 24) {
                    dist1 = Mth.nextDouble(RandomSource.create(), 4, 16);
                    this.getNavigation().moveTo(x - rx * dist1 / dist, this.getY(), z - rz * dist1 / dist, 1);
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
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.22);
        builder = builder.add(Attributes.MAX_HEALTH, 325);
        builder = builder.add(Attributes.ARMOR, 12);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 17);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
        builder = builder.add(BabelAttributes.ELEMENTAL_MODIFIER, 0.05);
        builder = builder.add(CAAttributes.MISSRATE, 33);
        builder = builder.add(Attributes.STEP_HEIGHT, 1.5f);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.chitgolem.move"));
            }
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.chitgolem.die"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.chitgolem.idle"));
        }
        return PlayState.STOP;
    }

    private PlayState attackingPredicate(AnimationState event) {
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
            this.swinging = true;
            this.lastSwing = level().getGameTime();
        }
        if (this.swinging && this.lastSwing + 15L <= level().getGameTime()) {
            this.swinging = false;
        }
        if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.chitgolem.attack"));
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
        if (this.deathTime == 30) {
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