package com.susen36.caerulaarbor.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class OceanizedChickenEntity extends SeaMonster {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(OceanizedChickenEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(OceanizedChickenEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_GROW_TIME = SynchedEntityData.defineId(OceanizedChickenEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_LAY_COOLDOWN = SynchedEntityData.defineId(OceanizedChickenEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> DATA_IS_CHILD = SynchedEntityData.defineId(OceanizedChickenEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> DATA_EGG_OFFSET = SynchedEntityData.defineId(OceanizedChickenEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_EGG_RATE = SynchedEntityData.defineId(OceanizedChickenEntity.class, EntityDataSerializers.INT);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";

    public OceanizedChickenEntity(Level world) {
        this(CAEntities.OCEANIZED_CHICKEN.get(), world);
    }

    public OceanizedChickenEntity(EntityType<OceanizedChickenEntity> type, Level world) {
        super(type, world);
        xpReward = 3;
        setNoAi(false);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(0.6f);
        this.moveControl = new FlyingMoveControl(this, 10, true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHOOT, false);
        builder.define(DATA_ANIMATION, "undefined");
        builder.define(DATA_GROW_TIME, 10000);
        builder.define(DATA_LAY_COOLDOWN, 1200);
        builder.define(DATA_IS_CHILD, false);
        builder.define(DATA_EGG_OFFSET, 4);
        builder.define(DATA_EGG_RATE, 1000);
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        return new FlyingPathNavigation(this, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) {

            @Override
            public boolean canUse() {
                return super.canUse() && OceanizedChickenEntity.this.isRipe();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && OceanizedChickenEntity.this.isRipe();
            }

        });
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(3, new Goal() {
            {
                this.setFlags(EnumSet.of(Flag.MOVE));
            }

            public boolean canUse() {
                if (OceanizedChickenEntity.this.getTarget() != null && !OceanizedChickenEntity.this.getMoveControl().hasWanted()) {
                    return OceanizedChickenEntity.this.isRipe();
                } else {
                    return false;
                }
            }

            @Override
            public boolean canContinueToUse() {
                return OceanizedChickenEntity.this.isRipe() && OceanizedChickenEntity.this.getMoveControl().hasWanted() && OceanizedChickenEntity.this.getTarget() != null && OceanizedChickenEntity.this.getTarget().isAlive();
            }

            @Override
            public void start() {
                LivingEntity livingentity = OceanizedChickenEntity.this.getTarget();
                Vec3 vec3d = livingentity.getEyePosition(1);
                OceanizedChickenEntity.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 1.25);
            }

            @Override
            public void tick() {
                LivingEntity livingentity = OceanizedChickenEntity.this.getTarget();
                if (OceanizedChickenEntity.this.getBoundingBox().intersects(livingentity.getBoundingBox())) {
                    OceanizedChickenEntity.this.doHurtTarget(livingentity);
                } else {
                    double d0 = OceanizedChickenEntity.this.distanceToSqr(livingentity);
                    if (d0 < 16) {
                        Vec3 vec3d = livingentity.getEyePosition(1);
                        OceanizedChickenEntity.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 1.25);
                    }
                }
            }
        });
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1, 20) {
            @Override
            protected Vec3 getPosition() {
                RandomSource random = OceanizedChickenEntity.this.getRandom();
                double dir_x = OceanizedChickenEntity.this.getX() + ((random.nextFloat() * 2 - 1) * 16);
                double dir_y = OceanizedChickenEntity.this.getY() + ((random.nextFloat() * 2 - 1) * 16);
                double dir_z = OceanizedChickenEntity.this.getZ() + ((random.nextFloat() * 2 - 1) * 16);
                return new Vec3(dir_x, dir_y, dir_z);
            }
        });
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return SoundEvents.CHICKEN_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.CHICKEN_DEATH;
    }

    @Override
    public boolean causeFallDamage(float l, float d, DamageSource source) {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.FALL))
            return false;
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        push(0, (-0.64), 0);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata);
        if ((Entity) this instanceof OceanizedChickenEntity datEntSetI)
            datEntSetI.getEntityData().set(DATA_GROW_TIME, 10000 - Mth.nextInt(RandomSource.create(), 0, 6000));
        if ((Entity) this instanceof OceanizedChickenEntity datEntSetI)
            datEntSetI.getEntityData().set(DATA_LAY_COOLDOWN, 1200 + Mth.nextInt(RandomSource.create(), -100, 100));
        return retval;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("GrowTime", this.entityData.get(DATA_GROW_TIME));
        compound.putInt("LayCooldown", this.entityData.get(DATA_LAY_COOLDOWN));
        compound.putBoolean("IsChild", this.entityData.get(DATA_IS_CHILD));
        compound.putInt("EggOffset", this.entityData.get(DATA_EGG_OFFSET));
        compound.putInt("EggRate", this.entityData.get(DATA_EGG_RATE));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("GrowTime")) {
            this.entityData.set(DATA_GROW_TIME, compound.getInt("GrowTime"));
        }
        if (compound.contains("LayCooldown")) {
            this.entityData.set(DATA_LAY_COOLDOWN, compound.getInt("LayCooldown"));
        }
        if (compound.contains("IsChild")) {
            this.entityData.set(DATA_IS_CHILD, compound.getBoolean("IsChild"));
        }
        if (compound.contains("EggOffset")) {
            this.entityData.set(DATA_EGG_OFFSET, compound.getInt("EggOffset"));
        }
        if (compound.contains("EggRate")) {
            this.entityData.set(DATA_EGG_RATE, compound.getInt("EggRate"));
        }
    }

    @Override
    public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
        super.mobInteract(sourceentity, hand);
        Entity entity = this;
        if (new Object() {
            public boolean checkGamemode(Entity ent) {
                if (ent instanceof ServerPlayer serverPlayer) {
                    return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                } else if (ent.level().isClientSide() && ent instanceof Player player) {
                    return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                }
                return false;
            }
        }.checkGamemode((Entity) sourceentity)) {
            if (sourceentity.isHolding(CAItems.NETHERSEA_CHICKEN_EGG.get())) {
                if (entity instanceof OceanizedChickenEntity datEntL2 && datEntL2.getEntityData().get(DATA_IS_CHILD)) {
                    if (entity instanceof OceanizedChickenEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_GROW_TIME, 1);
                } else {
                    if (entity instanceof OceanizedChickenEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_LAY_COOLDOWN, 1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void baseTick() {
        super.baseTick();
        LevelAccessor world = this.level();
        double lay;
        double grow;
        boolean is_child;
        is_child = (Entity) this instanceof OceanizedChickenEntity datEntL0 && datEntL0.getEntityData().get(DATA_IS_CHILD);
        this.getAttribute(Attributes.SCALE).setBaseValue(is_child ? 0.5 : 1.0);
        if (is_child) {
            grow = (Entity) this instanceof OceanizedChickenEntity datEntI ? datEntI.getEntityData().get(DATA_GROW_TIME) : 0;
            if (grow > 0) {
                if ((Entity) this instanceof OceanizedChickenEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_GROW_TIME, (int) (grow - 1));
            } else {
                if ((Entity) this instanceof OceanizedChickenEntity datEntSetL)
                    datEntSetL.getEntityData().set(DATA_IS_CHILD, false);
            }
        } else {
            lay = (Entity) this instanceof OceanizedChickenEntity datEntI ? datEntI.getEntityData().get(DATA_LAY_COOLDOWN) : 0;
            if (lay > 0) {
                if ((Entity) this instanceof OceanizedChickenEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_LAY_COOLDOWN, (int) (lay - 1));
            } else {
                if ((Entity) this instanceof OceanizedChickenEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_LAY_COOLDOWN, Mth.nextInt(RandomSource.create(), 2400, 4800));
                if (this instanceof OceanizedChickenEntity) {
                    this.setAnimation("animation.oceanized_chicken.lay");
                }
                CaerulaArborMod.queueServerWork(5, () -> {
                    if (world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(getX(), getY(), getZ()), SoundEvents.CHICKEN_EGG, SoundSource.NEUTRAL, 1, 1);
                    }
                    if (world instanceof ServerLevel level) {
                        ItemStack result;
                        ItemStack egg;
                        double rrr;
                        double ooo;
                        double c;
                        egg = new ItemStack(CAItems.NETHERSEA_CHICKEN_EGG.get()).copy();
                        rrr = (Entity) this instanceof OceanizedChickenEntity datEntI ? datEntI.getEntityData().get(DATA_EGG_RATE) : 0;
                        ooo = (Entity) this instanceof OceanizedChickenEntity datEntI ? datEntI.getEntityData().get(DATA_EGG_OFFSET) : 0;
                        c = Mth.nextInt(RandomSource.create(), 1, 16);
                        if (c > 9) {
                            if (c <= 12) {
                                rrr = rrr + 0.05;
                            } else if (c <= 15) {
                                ooo = ooo + 1;
                            } else {
                                rrr = rrr + 0.05;
                                ooo = ooo + 1;
                            }
                        }
                        CustomData.update(DataComponents.CUSTOM_DATA, egg, tag -> {
                            tag.putDouble("rate", rrr);
                            tag.putDouble("offset", ooo);
                        });
                        result = egg;
                        ItemEntity entityToSpawn = new ItemEntity(level, (getX()), (getY()), (getZ()), result);
                        entityToSpawn.setPickUpDelay(10);
                        level.addFreshEntity(entityToSpawn);
                    }
                });
            }
        }
        this.refreshDimensions();
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    public void setNoGravity(boolean ignored) {
        super.setNoGravity(true);
    }

    public void aiStep() {
        super.aiStep();
        this.setNoGravity(true);
    }


    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder = builder.add(Attributes.MAX_HEALTH, 10);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 2);
        builder = builder.add(Attributes.FOLLOW_RANGE, 22);
        builder = builder.add(Attributes.FLYING_SPEED, 0.3);
        builder = builder.add(Attributes.SCALE, 1.0);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_chicken.idle"));
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
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_chicken.attack"));
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

    public boolean isRipe() {
        return !this.getEntityData().get(DATA_IS_CHILD);
    }


    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}