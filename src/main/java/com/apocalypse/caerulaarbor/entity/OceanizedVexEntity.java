package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.api.event.SanityEvent;
import com.apocalypse.caerulaarbor.capability.sanity.SIHelper;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import com.apocalypse.caerulaarbor.util.WorldUtils;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class OceanizedVexEntity extends SeaMonster {
    public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(OceanizedVexEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(OceanizedVexEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(OceanizedVexEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_leftSurvivalTick = SynchedEntityData.defineId(OceanizedVexEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<String> DATA_SAYER = SynchedEntityData.defineId(OceanizedVexEntity.class, EntityDataSerializers.STRING);
    private boolean swinging;
    private boolean lastloop;
    private long lastSwing;
    public String animationprocedure = "empty";

    public OceanizedVexEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(CAEntities.OCEANIZED_VEX.get(), world);
    }

    public OceanizedVexEntity(EntityType<OceanizedVexEntity> type, Level world) {
        super(type, world);
        xpReward = 4;
        setNoAi(false);
        setMaxUpStep(0.6f);
        this.moveControl = new FlyingMoveControl(this, 10, true);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SHOOT, false);
        this.entityData.define(ANIMATION, "undefined");
        this.entityData.define(TEXTURE, "oceanized_vex");
        this.entityData.define(DATA_leftSurvivalTick, 600);
        this.entityData.define(DATA_SAYER, "");
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
    protected PathNavigation createNavigation(Level world) {
        return new FlyingPathNavigation(this, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.5, false) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return 2.25;
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
        this.targetSelector.addGoal(13, new NearestAttackableTargetGoal(this, Player.class, true, false) {
            @Override
            public boolean canUse() {
                double x = OceanizedVexEntity.this.getX();
                double y = OceanizedVexEntity.this.getY();
                double z = OceanizedVexEntity.this.getZ();
                Level world = OceanizedVexEntity.this.level();
                return super.canUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
            }

            @Override
            public boolean canContinueToUse() {
                double x = OceanizedVexEntity.this.getX();
                double y = OceanizedVexEntity.this.getY();
                double z = OceanizedVexEntity.this.getZ();
                Level world = OceanizedVexEntity.this.level();
                return super.canContinueToUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
            }
        });
        this.goalSelector.addGoal(14, new RandomStrollGoal(this, 1, 20) {
            @Override
            protected Vec3 getPosition() {
                RandomSource random = OceanizedVexEntity.this.getRandom();
                double dir_x = OceanizedVexEntity.this.getX() + ((random.nextFloat() * 2 - 1) * 16);
                double dir_y = OceanizedVexEntity.this.getY() + ((random.nextFloat() * 2 - 1) * 16);
                double dir_z = OceanizedVexEntity.this.getZ() + ((random.nextFloat() * 2 - 1) * 16);
                return new Vec3(dir_x, dir_y, dir_z);
            }
        });
        this.goalSelector.addGoal(15, new RandomLookAroundGoal(this));
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEFINED;
    }

    @Override
    public SoundEvent getAmbientSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.vex.ambient"));
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.vex.hurt"));
    }

    @Override
    public SoundEvent getDeathSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.vex.death"));
    }

    @Override
    public boolean causeFallDamage(float l, float d, DamageSource source) {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.DROWN))
            return false;
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        Entity sourceentity = source.getEntity();
        if (sourceentity == null)
            return;
        setDeltaMovement(new Vec3(0, 0, 0));
        if ((Entity) this instanceof OceanizedVexEntity _datEntSetS)
            _datEntSetS.getEntityData().set(DATA_SAYER, (sourceentity.getStringUUID()));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
        SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
        if ((Entity) this instanceof OceanizedVexEntity _datEntSetI)
            _datEntSetI.getEntityData().set(DATA_leftSurvivalTick, 600 + Mth.nextInt(RandomSource.create(), 0, 1800));
        return retval;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Texture", this.getTexture());
        compound.putInt("DataleftSurvivalTick", this.entityData.get(DATA_leftSurvivalTick));
        compound.putString("DataSAYER", this.entityData.get(DATA_SAYER));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Texture"))
            this.setTexture(compound.getString("Texture"));
        if (compound.contains("DataleftSurvivalTick"))
            this.entityData.set(DATA_leftSurvivalTick, compound.getInt("DataleftSurvivalTick"));
        if (compound.contains("DataSAYER"))
            this.entityData.set(DATA_SAYER, compound.getString("DataSAYER"));
    }

    @Override
    public void baseTick() {
        super.baseTick();
        Level world = this.level();
        Entity enemy;
        double sklp1;
        String uuid1;
        if (this.isAlive()) {
            if ((Entity) this instanceof Mob _mobEnt) {
                _mobEnt.getTarget();
            }
            sklp1 = (Entity) this instanceof OceanizedVexEntity _datEntI ? _datEntI.getEntityData().get(DATA_leftSurvivalTick) : 0;
            if (sklp1 <= 0) {
                ((Entity) this).hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.STARVE)), (float) Math.max(0.075 * ((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1), 1));
            } else {
                if ((Entity) this instanceof OceanizedVexEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_leftSurvivalTick, (int) (sklp1 - 1));
            }
            if ((Entity) this instanceof Mob _mobEnt7 && _mobEnt7.isAggressive()) {
                if ((Entity) this instanceof OceanizedVexEntity animatable)
                    animatable.setTexture("oceanized_vex_charging");
            } else {
                if ((Entity) this instanceof OceanizedVexEntity animatable)
                    animatable.setTexture("oceanized_vex");
            }
        } else {
            uuid1 = (Entity) this instanceof OceanizedVexEntity _datEntS ? _datEntS.getEntityData().get(DATA_SAYER) : "";
            enemy = new Object() {
                Entity entityFromStringUUID(String uuid2, Level world) {
                    Entity _uuidentity = null;
                    if (world instanceof ServerLevel _server) {
                        try {
                            _uuidentity = _server.getEntity(UUID.fromString(uuid2));
                        } catch (Exception e) {
                        }
                    }
                    return _uuidentity;
                }
            }.entityFromStringUUID(uuid1, world);
            if (!(enemy == null) && enemy.isAlive()) {
                ((Entity) this).lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((enemy.getX()), (enemy.getY()), (enemy.getZ())));
                Vec3 offset = enemy.position().add(0, 1, 0).add(position().reverse());
                if (!(offset.lengthSqr() <= 9)) {
                    offset = offset.normalize().scale(0.5);
                    setDeltaMovement(offset);
                }
            }
        }
        if (tickCount % 40 == 15) {
            if (getY() <= -64) {
                push(0, 0.64, 0);
            } else if (WorldUtils.isDistFromGround(world, this.getX(), this.getY(), this.getZ())) {
                push(0, (-0.64), 0);
            }
        }
        this.refreshDimensions();
    }

    @Override
    public void tick() {
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);
    }

    @Override
    public void move(MoverType pType, Vec3 pPos) {
        super.move(pType, pPos);
        this.checkInsideBlocks();
    }

    @Override
    public EntityDimensions getDimensions(Pose p_33597_) {
        return super.getDimensions(p_33597_).scale((float) 1);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entityIn) {
    }

    @Override
    protected void pushEntities() {
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
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.6);
        builder = builder.add(Attributes.MAX_HEALTH, 24);
        builder = builder.add(Attributes.ARMOR, 1);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 11);
        builder = builder.add(Attributes.FOLLOW_RANGE, 24);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 1);
        builder = builder.add(Attributes.FLYING_SPEED, 0.6);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

                    && !this.isAggressive()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_vex.fly"));
            }
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_vex.die"));
            }
            if (this.isAggressive() && event.isMoving()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_vex.charge"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_vex.idle"));
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
        if (this.swinging && this.lastSwing + 10L <= level().getGameTime()) {
            this.swinging = false;
        }
        if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_vex.attack"));
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
        if (this.deathTime >= 80) {
            this.remove(RemovalReason.KILLED);
            this.dropExperience();
            LevelAccessor world = this.level();
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();
            double sanity;
            if (world instanceof ServerLevel _level)
                _level.sendParticles(ParticleTypes.EXPLOSION, x, (y + 0.4), z, 4, 1, 1, 1, 0.1);
            if (world instanceof Level _level) {
                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.vex.charge")), SoundSource.NEUTRAL, 3, 1);
            }
            sanity = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
            {
                final Vec3 _center = new Vec3(x, y, z);
                List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(8 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                for (Entity entityiterator : _entfound) {
                    if (!(entityiterator instanceof LivingEntity)) {
                        continue;
                    }
                    if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                        continue;
                    }
                    if ((entityiterator != null ? distanceTo(entityiterator) : -1) <= 4) {
                        entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "ocean_magic"))), this),
                                (float) (sanity * 3));
                        if (entityiterator instanceof LivingEntity target) {
                            SIHelper.causeSanityInjury(target, this, sanity * 20, SanityEvent.Hurt.Type.ENTITY);
                        }
                    }
                }
            }
        } else if (this.deathTime > 10 && this.deathTime < 70) {
            String uuid = this.getEntityData().get(DATA_SAYER);
            if (uuid.isEmpty()) {
                this.deathTime = 79;
                return;
            }
            try {
                UUID uuidTry = UUID.fromString(uuid);
                if (this.level() instanceof ServerLevel s) {
                    Entity slayer = s.getEntity(uuidTry);
                    if (slayer != null && this.distanceToSqr(slayer) <= 2.25) this.deathTime = 79;
                }
            } catch (Exception e) {
                CaerulaArborMod.LOGGER.debug("Invalid UUID for oceanized vex: {}", uuid);
            }
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
