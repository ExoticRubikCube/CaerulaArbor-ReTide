package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.api.event.SanityEvent;
import com.apocalypse.caerulaarbor.capability.sanity.SIHelper;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CAAttributes;
import com.apocalypse.caerulaarbor.init.CAEntities;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;

public class ScreamChestFishEntity extends SeaMonster {
    public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(ScreamChestFishEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(ScreamChestFishEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(ScreamChestFishEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Boolean> DATA_release = SynchedEntityData.defineId(ScreamChestFishEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> DATA_SCREAM_TICK = SynchedEntityData.defineId(ScreamChestFishEntity.class, EntityDataSerializers.INT);
    private boolean swinging;
    private boolean lastloop;
    private long lastSwing;
    public String animationprocedure = "empty";

    public ScreamChestFishEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(CAEntities.SCREAM_CHEST_FISH.get(), world);
    }

    public ScreamChestFishEntity(EntityType<ScreamChestFishEntity> type, Level world) {
        super(type, world);
        xpReward = 0;
        setNoAi(false);
        setMaxUpStep(0.6f);
        setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SHOOT, false);
        this.entityData.define(ANIMATION, "undefined");
        this.entityData.define(TEXTURE, "scream_chest_fish");
        this.entityData.define(DATA_release, false);
        this.entityData.define(DATA_SCREAM_TICK, 201);
    }

    public void setTexture(String texture) {
        this.entityData.set(TEXTURE, texture);
    }

    public String getTexture() {
        return this.entityData.get(TEXTURE);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return 3.24;
            }

            @Override
            public boolean canUse() {
                return super.canUse() && !isShiftKeyDown() && !ScreamChestFishEntity.this.isScreaming();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !isShiftKeyDown() && !ScreamChestFishEntity.this.isScreaming();
            }

        });
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1) {
            @Override
            public boolean canUse() {
                return super.canUse() && !isShiftKeyDown() && !ScreamChestFishEntity.this.isScreaming();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !isShiftKeyDown() && !ScreamChestFishEntity.this.isScreaming();
            }
        });
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                if (!super.canUse()) return false;
                return !isShiftKeyDown();
            }

            @Override
            public boolean canContinueToUse() {
                if (!super.canContinueToUse()) return false;
                return !isShiftKeyDown();
            }
        });
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.armor_stand.hit"));
    }

    @Override
    public SoundEvent getDeathSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.armor_stand.break"));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.FALL))
            return false;
        if (source.is(DamageTypes.DROWN))
            return false;
        if (isScreaming())
            return super.hurt(source, amount * 0.5f);
        boolean flag = super.hurt(source, amount);
        if (flag)
            startScreamChest(source.getEntity());
        return flag;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }

    private InteractionResult startScreamChest(@Nullable Entity sourceEntity) {
        if (sourceEntity == null) {
            return InteractionResult.PASS;
        }
        if (this.isShiftKeyDown()) {
            this.setAnimation("animation.scream_chest_fish.open");
            this.level().playSound(null, BlockPos.containing(this.getX(), this.getY(), this.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.chest.open")), SoundSource.HOSTILE, 1, 1);
            this.setShiftKeyDown(false);
            this.getEntityData().set(DATA_release, true);
            this.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            if (sourceEntity instanceof LivingEntity livingEntity) {
                this.setTarget(livingEntity);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }


    @Override
    public void die(DamageSource source) {
        super.die(source);
        Entity sourceentity = source.getEntity();
        if (sourceentity == null)
            return;
        if (((Entity) this instanceof ScreamChestFishEntity _datEntI ? _datEntI.getEntityData().get(DATA_SCREAM_TICK) : 0) > 0) {
            if (sourceentity instanceof ServerPlayer _player) {
                Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "i_scream"));
                AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
                if (!_ap.isDone()) {
                    for (String criteria : _ap.getRemainingCriteria())
                        _player.getAdvancements().award(_adv, criteria);
                }
            }
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
        SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
        {
            Entity _ent = this;
            _ent.setYRot((float) (90 * Mth.nextInt(RandomSource.create(), 0, 3)));
            _ent.setXRot(0);
            _ent.setYBodyRot(_ent.getYRot());
            _ent.setYHeadRot(_ent.getYRot());
            _ent.yRotO = _ent.getYRot();
            _ent.xRotO = _ent.getXRot();
            if (_ent instanceof LivingEntity _entity) {
                _entity.yBodyRotO = _entity.getYRot();
                _entity.yHeadRotO = _entity.getYRot();
            }
        }
        if (this.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get()))
            this.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).setBaseValue(25);
        return retval;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Texture", this.getTexture());
        compound.putBoolean("Datarelease", this.entityData.get(DATA_release));
        compound.putInt("DataSCREAM_TICK", this.entityData.get(DATA_SCREAM_TICK));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Texture"))
            this.setTexture(compound.getString("Texture"));
        if (compound.contains("Datarelease"))
            this.entityData.set(DATA_release, compound.getBoolean("Datarelease"));
        if (compound.contains("DataSCREAM_TICK"))
            this.entityData.set(DATA_SCREAM_TICK, compound.getInt("DataSCREAM_TICK"));
    }

    @Override
    public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
        super.mobInteract(sourceentity, hand);
        return startScreamChest(sourceentity);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        double scream;
        double d;
        double angle;
        double t;
        if (!((Entity) this instanceof ScreamChestFishEntity _datEntL0 && _datEntL0.getEntityData().get(DATA_release))) {
            setShiftKeyDown(true);
            if (!this.level().isClientSide())
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 8, false, false));
        } else {
            if (this.isAlive()) {
                setShiftKeyDown(false);
                scream = (Entity) this instanceof ScreamChestFishEntity _datEntI ? _datEntI.getEntityData().get(DATA_SCREAM_TICK) : 0;
                if (scream > 0) {
                    if ((Entity) this instanceof ScreamChestFishEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_SCREAM_TICK, (int) (scream - 1));
                    t = tickCount;
                    if (scream % 10 == 0) {
                        if (world instanceof Level _level) {
                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.sculk_shrieker.shriek")), SoundSource.HOSTILE, 1, 1);
                        }
                    }
                    for (int index0 = 0; index0 < 60; index0++) {
                        angle = Math.toRadians(index0 * 6 + t * 0.6);
                        d = (t * 0.5) % 5;
                        if (world instanceof ServerLevel _level)
                            _level.sendParticles(ParticleTypes.ELECTRIC_SPARK, (x + d * Math.sin(angle)), (y + 0.25), (z + d * Math.cos(angle)), 2, 0.1, 0.1, 0.1, 0.1);
                    }
                    for (Entity entityiterator : world.getEntities(this, new AABB((x - 5), (y - 2), (z - 5), (x + 5), (y + 3), (z + 5)))) {
                        if ((entityiterator != null ? distanceTo(entityiterator) : -1) <= 5) {
                            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                                if (!(entityiterator == ((Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null))) {
                                    continue;
                                }
                            }
                            if (!(entityiterator instanceof LivingEntity)) {
                                continue;
                            }
                            SIHelper.causeSanityInjury((LivingEntity) entityiterator, this, 5, SanityEvent.Hurt.Type.ENTITY);
                            if (!this.level().isClientSide())
                                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 1));
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


    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.2);
        builder = builder.add(Attributes.MAX_HEALTH, 40);
        builder = builder.add(Attributes.ARMOR, 10);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 5);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 1);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.isDeadOrDying()) {
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.scream_chest_fish.die"));
        }
        if (this.animationprocedure.equals("empty")) {
            if (this.isScreaming())
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.scream_chest_fish.scream"));
            if (this.isShiftKeyDown()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.scream_chest_fish.chest"));
            }
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.1F && event.getLimbSwingAmount() < 0.1F)) && this.entityData.get(DATA_release)) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.scream_chest_fish.move"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.scream_chest_fish.idle"));
        }
        return PlayState.STOP;
    }

    private PlayState attackingPredicate(AnimationState event) {
        double d1 = this.getX() - this.xOld;
        double d0 = this.getZ() - this.zOld;
        float velocity = (float) Math.sqrt(d1 * d1 + d0 * d0);
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
            this.swinging = true;
            this.lastSwing = level().getGameTime();
        }
        if (this.swinging && this.lastSwing + 12L <= level().getGameTime()) {
            this.swinging = false;
        }
        if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.scream_chest_fish.attack"));
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
        if (this.deathTime == 15) {
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
        data.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
        data.add(new AnimationController<>(this, "attacking", 0, this::attackingPredicate));
        data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
    }

    private boolean isScreaming() {
        if (!this.entityData.get(DATA_release)) return false;
        return this.entityData.get(DATA_SCREAM_TICK) > 0;
    }


    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}
