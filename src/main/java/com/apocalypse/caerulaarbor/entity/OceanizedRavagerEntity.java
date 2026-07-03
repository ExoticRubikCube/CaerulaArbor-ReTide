package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import com.apocalypse.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
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
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class OceanizedRavagerEntity extends SeaMonster {
    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(OceanizedRavagerEntity.class, EntityDataSerializers.STRING);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";

    public OceanizedRavagerEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(CAEntities.OCEANIZED_RAVAGER.get(), world);
    }

    public OceanizedRavagerEntity(EntityType<OceanizedRavagerEntity> type, Level world) {
        super(type, world);
        xpReward = 24;
        setNoAi(false);
        setMaxUpStep(1.5f);
        setPersistenceRequired();
    }

    public static void summonFellows(LevelAccessor world, double x, double y, double z, int count) {
        if (!(world instanceof ServerLevel level)) {
            return;
        }
        BlockPos spawnPos = BlockPos.containing(x, y, z);
        RandomSource random = world.getRandom();
        for (int index = 0; index < count; index++) {
            Entity entityToSpawn = switch (random.nextInt(5)) {
                case 0 -> CAEntities.OCEANIZED_PILLAGER.get().spawn(level, spawnPos, MobSpawnType.MOB_SUMMONED);
                case 1 -> CAEntities.OCEANIZED_VINDICATOR.get().spawn(level, spawnPos, MobSpawnType.MOB_SUMMONED);
                case 2 -> CAEntities.OCEANIZED_VILLAGER.get().spawn(level, spawnPos, MobSpawnType.MOB_SUMMONED);
                case 3 -> CAEntities.OCEANIZED_WITCH.get().spawn(level, spawnPos, MobSpawnType.MOB_SUMMONED);
                default -> CAEntities.OCEANIZED_EVOKER.get().spawn(level, spawnPos, MobSpawnType.MOB_SUMMONED);
            };
            if (entityToSpawn != null) {
                entityToSpawn.setDeltaMovement(0, 0.15, 0);
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIMATION, "undefined");
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.75, false) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return 12.25;
            }
        });
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, SnowGolem.class, true, false));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Villager.class, true, false));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Illusioner.class, true, false));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, Pillager.class, true, false));
        this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, Vindicator.class, true, false));
        this.targetSelector.addGoal(9, new NearestAttackableTargetGoal<>(this, Witch.class, true, false));
        this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, Piglin.class, true, false));
        this.targetSelector.addGoal(11, new NearestAttackableTargetGoal<>(this, PiglinBrute.class, true, false));
        this.targetSelector.addGoal(12, new NearestAttackableTargetGoal<>(this, ZombifiedPiglin.class, true, false));
        this.targetSelector.addGoal(13, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, target -> EntityUtils.isOceanizedPlayerNearby(this.level(), this.getX(), this.getY(), this.getZ())));
        this.goalSelector.addGoal(15, new RandomStrollGoal(this, 1));
        this.goalSelector.addGoal(16, new RandomLookAroundGoal(this));
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
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.ravager.ambient"));
    }

    @Override
    public void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.ravager.step")), 0.15f, 1);
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.ravager.hurt"));
    }

    @Override
    public SoundEvent getDeathSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.ravager.death"));
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (!this.level().isClientSide()) {
            CaerulaArborMod.queueServerWork(11, () -> {
                if (this.isAlive() && target.isAlive() && this.distanceTo(target) <= 3.8) {
                    target.hurt(
                            new DamageSource(
                                    this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                                            .getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "general_seaborn_attack"))),
                                    this),
                            (float) (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0));
                }
            });
        }
        return true;
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
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        BlockState target;
        boolean breaked = false;
        if (this.deathTime == 22) {
            summonFellows(world, x, y, z, 3);
        }
        Mob _mobEnt1 = this;
        if (_mobEnt1.isAggressive()) {
            if (WorldUtils.canGrief(world)) {
                if (Math.random() < 0.05) {
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = 1; dy <= 3; dy++) {
                            for (int dz = -1; dz <= 1; dz++) {
                                target = (world.getBlockState(BlockPos.containing(x + dx, y + dy, z + dz)));
                                if (world.getBlockFloorHeight(BlockPos.containing(x + dx, y + dy, z + dz)) > 0 && target.getDestroySpeed(world, BlockPos.containing(0, 0, 0)) > 0 && target.getDestroySpeed(world, BlockPos.containing(0, 0, 0)) <= 5) {
                                    world.destroyBlock(BlockPos.containing(x + dx, y + dy, z + dz), false);
                                    breaked = true;
                                }
                            }
                        }
                    }
                    if (breaked) {
                        if (world instanceof Level _level) {
                            if (!_level.isClientSide()) {
                                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.wither.break_block")), SoundSource.HOSTILE, 1, 1);
                            } else {
                                _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.wither.break_block")), SoundSource.HOSTILE, 1, 1, false);
                            }
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
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.15);
        builder = builder.add(Attributes.MAX_HEALTH, 215);
        builder = builder.add(Attributes.ARMOR, 6);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 17);
        builder = builder.add(Attributes.FOLLOW_RANGE, 27);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 1);
        builder = builder.add(Attributes.ATTACK_KNOCKBACK, 1);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

                    && !this.isAggressive()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_ravager.walk"));
            }
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_ravager.die"));
            }
            if (this.isAggressive() && event.isMoving()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_ravager.sprint"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_ravager.idle"));
        }
        return PlayState.STOP;
    }

    private PlayState attackingPredicate(AnimationState event) {
        double d1 = this.getX() - this.xOld;
        double d0 = this.getZ() - this.zOld;
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
            this.swinging = true;
            this.lastSwing = level().getGameTime();
        }
        if (this.swinging && this.lastSwing + 19L <= level().getGameTime()) {
            this.swinging = false;
        }
        if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_ravager.attack"));
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


    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}
