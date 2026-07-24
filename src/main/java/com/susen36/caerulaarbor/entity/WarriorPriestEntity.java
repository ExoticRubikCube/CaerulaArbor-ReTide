package com.susen36.caerulaarbor.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.base.SyncedAnimationEntity;
import com.susen36.caerulaarbor.init.CADamageTypes;
import com.susen36.caerulaarbor.init.CAEntities;
import net.minecraft.commands.arguments.EntityAnchorArgument;
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
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
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

public class WarriorPriestEntity extends Animal implements GeoEntity, SyncedAnimationEntity {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(WarriorPriestEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(WarriorPriestEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_SKILL_P = SynchedEntityData.defineId(WarriorPriestEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_SKILL_P1 = SynchedEntityData.defineId(WarriorPriestEntity.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";

    public WarriorPriestEntity(Level world) {
        this(CAEntities.WARRIOR_PRIEST.get(), world);
    }

    public WarriorPriestEntity(EntityType<WarriorPriestEntity> type, Level world) {
        super(type, world);
        xpReward = 11;
        setNoAi(false);
        setMaxUpStep(0.8f);
        setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SHOOT, false);
        this.entityData.define(DATA_ANIMATION, "undefined");
        this.entityData.define(DATA_SKILL_P, 100);
        this.entityData.define(DATA_SKILL_P1, 200);
    }


    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1, false) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return 4.84;
            }
        });
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Monster.class, true, false));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1));
        this.goalSelector.addGoal(5, new OpenDoorGoal(this, false));
        this.goalSelector.addGoal(6, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new FloatGoal(this));
    }

    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHitIn) {
        super.dropCustomDeathLoot(source, looting, recentlyHitIn);
        this.spawnAtLocation(new ItemStack(Blocks.WHITE_TULIP));
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
    public boolean doHurtTarget(Entity target) {
        if (!this.level().isClientSide()) {
            CaerulaArborMod.queueServerWork(8, () -> {
                if (this.isAlive() && target.isAlive()) {
                    target.hurt(CADamageTypes.source(this.level(), CADamageTypes.GENERIC_WARRIOR_ATTACK, this), (float) (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0));
                    if (target instanceof LivingEntity livingTarget && !livingTarget.level().isClientSide()) {
                        livingTarget.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 1, false, true));
                    }
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
        if (sourceentity != null) {
            if (this.isAlive() && sourceentity.isAlive()) {
                if (distanceTo(sourceentity) <= 2.5 && ((Entity) this instanceof WarriorPriestEntity datEntI ? datEntI.getEntityData().get(DATA_SKILL_P) : 0) <= 0) {
                    if (this instanceof WarriorPriestEntity) {
                        this.setAnimation("animation.warriorpriest.shieldattack");
                    }
                    if ((Entity) this instanceof WarriorPriestEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_SKILL_P, 150);
                    CaerulaArborMod.queueServerWork(15, () -> {
                        if (sourceentity.isAlive()) {
                            this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((sourceentity.getX()), (sourceentity.getY()), (sourceentity.getZ())));
                            if (world instanceof Level level) {
                                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.EMPTY, SoundSource.HOSTILE, (float) 2.5, 1);
                            }
                            sourceentity.hurt(
                                    CADamageTypes.source(world, CADamageTypes.GENERIC_WARRIOR_ATTACK, this), (float) ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 2));
                            sourceentity.push(0, 0.5, 0);
                        }
                    });
                }
            }
        }
        return super.hurt(source, amount);
    }

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("SkillP", this.entityData.get(DATA_SKILL_P));
        compound.putInt("SkillP1", this.entityData.get(DATA_SKILL_P1));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("SkillP")) {
            this.entityData.set(DATA_SKILL_P, compound.getInt("SkillP"));
        }
        if (compound.contains("SkillP1")) {
            this.entityData.set(DATA_SKILL_P1, compound.getInt("SkillP1"));
        }
	}

    @Override
    public void baseTick() {
        super.baseTick();
        LevelAccessor world = this.level();
        double sklp1;
        double skillp;
        if (this.isAlive()) {
            skillp = (Entity) this instanceof WarriorPriestEntity datEntI ? datEntI.getEntityData().get(DATA_SKILL_P) : 0;
            sklp1 = (Entity) this instanceof WarriorPriestEntity datEntI ? datEntI.getEntityData().get(DATA_SKILL_P1) : 0;
            if (skillp > 0) {
                if ((Entity) this instanceof WarriorPriestEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_SKILL_P, (int) (skillp - 1));
            }
            if (sklp1 > 0) {
                if ((Entity) this instanceof WarriorPriestEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_SKILL_P1, (int) (sklp1 - 1));
            } else {
                if (this instanceof WarriorPriestEntity) {
                    this.setAnimation("animation.warriorpriest.givebuff");
                }
                if ((Entity) this instanceof WarriorPriestEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_SKILL_P1, 400);
                {
                    final Vec3 center = new Vec3(this.getX(), this.getY(), this.getZ());
                    List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(16 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                    for (Entity entityiterator : entfound) {
                        if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "homo_sapiens")))) {
                            if (entityiterator instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                                livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 400, 0));
                            if (entityiterator instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                                livingEntity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400, 0));
                        }
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

    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageable) {
        WarriorPriestEntity retval = CAEntities.WARRIOR_PRIEST.get().create(serverWorld);
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
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.25);
        builder = builder.add(Attributes.MAX_HEALTH, 40);
        builder = builder.add(Attributes.ARMOR, 7);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 11);
        builder = builder.add(Attributes.FOLLOW_RANGE, 24);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
        return builder;
    }

    private PlayState movementPredicate(AnimationState<?> event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

            ) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.warriorpriest.move"));
            }
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.warriorpriest.dead"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.warriorpriest.idle"));
        }
        return PlayState.STOP;
    }

    private PlayState attackingPredicate(AnimationState<?> event) {
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
            this.swinging = true;
            this.lastSwing = level().getGameTime();
        }
        if (this.swinging && this.lastSwing + 19L <= level().getGameTime()) {
            this.swinging = false;
        }
        if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.warriorpriest.attack"));
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
