package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.SyncedAnimationEntity;
import com.apocalypse.caerulaarbor.init.CAEntities;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
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

import java.util.Comparator;
import java.util.List;

public class CorrectionalPhalanxyInfantryEntity extends Animal implements GeoEntity, SyncedAnimationEntity {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(CorrectionalPhalanxyInfantryEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(CorrectionalPhalanxyInfantryEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_SKILLP_1 = SynchedEntityData.defineId(CorrectionalPhalanxyInfantryEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_SKILLP_2 = SynchedEntityData.defineId(CorrectionalPhalanxyInfantryEntity.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";

    public CorrectionalPhalanxyInfantryEntity(Level world) {
        this(CAEntities.CORRECTIONAL_PHALANXY_INFANTRY.get(), world);
    }

    public CorrectionalPhalanxyInfantryEntity(EntityType<CorrectionalPhalanxyInfantryEntity> type, Level world) {
        super(type, world);
        xpReward = 8;
        setNoAi(false);
        setMaxUpStep(0.6f);
        setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SHOOT, false);
        this.entityData.define(DATA_ANIMATION, "undefined");
        this.entityData.define(DATA_SKILLP_1, 100);
        this.entityData.define(DATA_SKILLP_2, 200);
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
                return 6.25;
            }
        });
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Monster.class, true, false));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1));
        this.goalSelector.addGoal(5, new OpenDoorGoal(this, false));
        this.goalSelector.addGoal(6, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new FloatGoal(this));
    }

    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHitIn) {
        super.dropCustomDeathLoot(source, looting, recentlyHitIn);
        this.spawnAtLocation(new ItemStack(Items.PURPLE_DYE));
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
            CaerulaArborMod.queueServerWork(10, () -> {
                if (this.isAlive() && target.isAlive()) {
                    target.hurt(
                            new DamageSource(
                                    this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                                            .getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "generic_warrior_attack"))),
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
        Entity sourceentity = source.getEntity();
        if (sourceentity != null) {
            if (!(sourceentity instanceof Player)) {
                if (((Entity) this instanceof CorrectionalPhalanxyInfantryEntity _datEntI ? _datEntI.getEntityData().get(DATA_SKILLP_1) : 0) <= 0) {
                    if (distanceTo(sourceentity) <= 5 && this.isAlive()) {
                        if ((Entity) this instanceof CorrectionalPhalanxyInfantryEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_SKILLP_1, 100);
                        if (this instanceof CorrectionalPhalanxyInfantryEntity) {
                            this.setAnimation("animation.correctional_phalanx _infantry.heavyattack");
                        }
                        CaerulaArborMod.queueServerWork(20, () -> {
                            sourceentity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "generic_warrior_attack")))),
                                    (float) ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 2));
                            sourceentity.push((getLookAngle().x * 0.64), 0, (getLookAngle().z * 0.64));
                        });
                    }
                }
            }
        }
        return super.hurt(source, amount);
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
        double sklp1;
        double sklp2;
        Entity enemy;
        if (this.isAlive()) {
            sklp1 = (Entity) this instanceof CorrectionalPhalanxyInfantryEntity _datEntI ? _datEntI.getEntityData().get(DATA_SKILLP_1) : 0;
            sklp2 = (Entity) this instanceof CorrectionalPhalanxyInfantryEntity _datEntI ? _datEntI.getEntityData().get(DATA_SKILLP_2) : 0;
            if (sklp1 > 0) {
                if ((Entity) this instanceof CorrectionalPhalanxyInfantryEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_SKILLP_1, (int) (sklp1 - 1));
            }
            if (sklp2 > 0) {
                if ((Entity) this instanceof CorrectionalPhalanxyInfantryEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_SKILLP_2, (int) (sklp2 - 1));
            } else {
                enemy = this.getTarget();
                if (!(enemy == null)) {
                    if (distanceTo(enemy) <= 5 && enemy.isAlive()) {
                        if ((Entity) this instanceof CorrectionalPhalanxyInfantryEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_SKILLP_2, 200);
                        if (this instanceof CorrectionalPhalanxyInfantryEntity) {
                            this.setAnimation("animation.correctional_phalanx _infantry.swing");
                        }
                        ((Entity) this).lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((enemy.getX()), (enemy.getY()), (enemy.getZ())));
                        CaerulaArborMod.queueServerWork(16, () -> {
                            if (this.isAlive()) {
                                final Vec3 _center = new Vec3((this.getX() + 2 * getLookAngle().x), (this.getY() + 2 * getLookAngle().y), (this.getZ() + 2 * getLookAngle().z));
                                List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(6 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                                for (Entity entityiterator : _entfound) {
                                    if (!(entityiterator instanceof Mob) || entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "inquisition")))) {
                                        if (!(entityiterator == this.getTarget())) {
                                            continue;
                                        }
                                    }
                                    entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "generic_warrior_attack")))),
                                            (float) ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 1.5));
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

    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageable) {
        CorrectionalPhalanxyInfantryEntity retval = CAEntities.CORRECTIONAL_PHALANXY_INFANTRY.get().create(serverWorld);
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
        builder = builder.add(Attributes.MAX_HEALTH, 50);
        builder = builder.add(Attributes.ARMOR, 10);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 11);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
        return builder;
    }

    private PlayState movementPredicate(AnimationState<?> event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

            ) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.correctional_phalanx _infantry.move"));
            }
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.correctional_phalanx _infantry.death"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.correctional_phalanx _infantry.idle"));
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
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.correctional_phalanx _infantry.stab"));
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
        if (this.deathTime == 25) {
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
        data.add(new AnimationController<>(this, "movement", 1, this::movementPredicate));
        data.add(new AnimationController<>(this, "attacking", 1, this::attackingPredicate));
        data.add(new AnimationController<>(this, "procedure", 1, this::procedurePredicate));
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
