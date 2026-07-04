package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CAAttributes;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAItems;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import com.apocalypse.caerulaarbor.util.WorldUtils;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

public class FlamarineGolemEntity extends SeaMonster {
    public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(FlamarineGolemEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(FlamarineGolemEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_duration = SynchedEntityData.defineId(FlamarineGolemEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_skillP1 = SynchedEntityData.defineId(FlamarineGolemEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_skillP2 = SynchedEntityData.defineId(FlamarineGolemEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_addition = SynchedEntityData.defineId(FlamarineGolemEntity.class, EntityDataSerializers.INT);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";
    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.BLUE, ServerBossEvent.BossBarOverlay.PROGRESS);

    public FlamarineGolemEntity(Level world) {
        this(CAEntities.FLAMARINE_GOLEM.get(), world);
    }

    public FlamarineGolemEntity(EntityType<FlamarineGolemEntity> type, Level world) {
        super(type, world);
        xpReward = 48;
        setNoAi(false);
        setMaxUpStep(1.5f);
        setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SHOOT, false);
        this.entityData.define(ANIMATION, "undefined");
        this.entityData.define(DATA_duration, 0);
        this.entityData.define(DATA_skillP1, 6);
        this.entityData.define(DATA_skillP2, 100);
        this.entityData.define(DATA_addition, 40);
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
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 2.25, false) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return 20.25;
            }

            @Override
            public boolean canUse() {
                return super.canUse() && isFlamarineDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isFlamarineDurative();
            }

        });
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1) {
            @Override
            public boolean canUse() {
                return super.canUse() && isFlamarineDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isFlamarineDurative();
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
    public void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.iron_golem.step")), 0.15f, 1);
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.iron_golem.hurt"));
    }

    @Override
    public SoundEvent getDeathSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.iron_golem.death"));
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        double targetX = target.getX();
        double targetY = target.getY();
        double targetZ = target.getZ();
        if (!this.level().isClientSide()) {
            this.getEntityData().set(DATA_duration, 35);
            CaerulaArborMod.queueServerWork(20, () -> {
                if (this.isAlive() && target.isAlive() && this.distanceTo(target) <= 5.75) {
                    target.hurt(
                            new DamageSource(
                                    this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                                            .getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "golem_attack"))),
                                    this),
                            (float) (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0));
                    breakShield(this.level(), targetX, targetY, targetZ, target, 120);
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
            double sklp;
            if (this.isAlive()) {
                sklp = (Entity) this instanceof FlamarineGolemEntity _datEntI ? _datEntI.getEntityData().get(DATA_skillP1) : 0;
                if (sklp > 0) {
                    if ((Entity) this instanceof FlamarineGolemEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_skillP1, (int) (sklp - 1));
                }
            }
            if (!(sourceentity instanceof FlamarineGolemEntity || sourceentity instanceof FlamarineStatueEntity)) {
                final Vec3 _center = new Vec3(this.getX(), this.getY(), this.getZ());
                List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(32 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                for (Entity entityiterator : _entfound) {
                    if (entityiterator instanceof FlamarineStatueEntity entity && sourceentity instanceof LivingEntity _ent && _ent.canBeSeenAsEnemy())
                        entity.setTarget(_ent);
                }
            }
        }
        if (source.is(DamageTypes.IN_FIRE))
            return false;
        if (source.getDirectEntity() instanceof ThrownPotion || source.getDirectEntity() instanceof AreaEffectCloud)
            return false;
        if (source.is(DamageTypes.FALL))
            return false;
        if (source.is(DamageTypes.CACTUS))
            return false;
        if (source.is(DamageTypes.DROWN))
            return false;
        if (source.is(DamageTypes.EXPLOSION))
            return false;
        if (source.is(DamageTypes.TRIDENT))
            return false;
        if (source.is(DamageTypes.FALLING_ANVIL))
            return false;
        if (source.is(DamageTypes.DRAGON_BREATH))
            return false;
        if (source.is(DamageTypes.WITHER))
            return false;
        if (source.is(DamageTypes.WITHER_SKULL))
            return false;
        return super.hurt(source, amount);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
        SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
        if (this.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE.get()))
            this.getAttribute(CAAttributes.GENERAL_DEFENSE.get()).setBaseValue(5);
        if (this.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get()))
            this.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).setBaseValue(15);
        if (this.getAttributes().hasAttribute(CAAttributes.SANITY_RESISTANCE.get()))
            this.getAttribute(CAAttributes.SANITY_RESISTANCE.get()).setBaseValue(60);
        if (this instanceof FlamarineGolemEntity) {
            this.setAnimation("animation.flamarine_golem.start");
        }
        if ((Entity) this instanceof FlamarineGolemEntity _datEntSetI)
            _datEntSetI.getEntityData().set(DATA_duration, 60);
        return retval;
    }

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Dataduration", this.entityData.get(DATA_duration));
        compound.putInt("DataskillP1", this.entityData.get(DATA_skillP1));
        compound.putInt("DataskillP2", this.entityData.get(DATA_skillP2));
        compound.putInt("Dataaddition", this.entityData.get(DATA_addition));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Dataduration"))
            this.entityData.set(DATA_duration, compound.getInt("Dataduration"));
        if (compound.contains("DataskillP1"))
            this.entityData.set(DATA_skillP1, compound.getInt("DataskillP1"));
        if (compound.contains("DataskillP2"))
            this.entityData.set(DATA_skillP2, compound.getInt("DataskillP2"));
        if (compound.contains("Dataaddition"))
            this.entityData.set(DATA_addition, compound.getInt("Dataaddition"));
	}

    @Override
    public void baseTick() {
        super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity enemy;
        double sklp1;
        double sklp2;
        double dura;
        if (this.deathTime == 46) {
            if (!world.isClientSide()) {
                if (world instanceof Level _level) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.trident.hit_ground")), SoundSource.HOSTILE, 2, 1);
                }
            }
        }
        if (this.isAlive()) {
            sklp1 = (Entity) this instanceof FlamarineGolemEntity _datEntI ? _datEntI.getEntityData().get(DATA_skillP1) : 0;
            sklp2 = (Entity) this instanceof FlamarineGolemEntity _datEntI ? _datEntI.getEntityData().get(DATA_skillP2) : 0;
            dura = (Entity) this instanceof FlamarineGolemEntity _datEntI ? _datEntI.getEntityData().get(DATA_duration) : 0;
            enemy = this.getTarget();
            if (dura > 0) {
                if ((Entity) this instanceof FlamarineGolemEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_duration, (int) (dura - 1));
            }
            if (sklp1 <= 0 && dura <= 0) {
                if (!(enemy == null) && enemy.isAlive()) {
                    if (distanceTo(enemy) <= 6) {
                        if (this instanceof FlamarineGolemEntity) {
                            this.setAnimation("animation.flamarine_golem.heavy");
                        }
                        if ((Entity) this instanceof FlamarineGolemEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_skillP1, 11);
                        if ((Entity) this instanceof FlamarineGolemEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_duration, 40);
                        dura = 40;
                        CaerulaArborMod.queueServerWork(20, () -> {
                            if (this.isAlive()) {
                                Entity enemy1;
                                double damage;
                                double r;
                                double h;
                                double d;
                                enemy1 = this.getTarget();
                                r = 6;
                                damage = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
                                if (world instanceof Level _level) {
                                    if (!_level.isClientSide()) {
                                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.anvil.land")), SoundSource.HOSTILE, 1, 1);
                                    } else {
                                        _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.anvil.land")), SoundSource.HOSTILE, 1, 1, false);
                                    }
                                }
                                final Vec3 _center = new Vec3(x, y, z);
                                List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(12 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                                for (Entity entityiterator : _entfound) {
                                    if (!(entityiterator instanceof LivingEntity)) {
                                        continue;
                                    }
                                    if (!entityiterator.isAlive()) {
                                        continue;
                                    }
                                    if (entityiterator instanceof Player || (entityiterator instanceof TamableAnimal _tamEnt && _tamEnt.isTame())) {
                                        if (!(entityiterator == enemy1)) {
                                            continue;
                                        }
                                    }
                                    if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "is_humanside")))) {
                                        if (!(entityiterator == enemy1)) {
                                            continue;
                                        }
                                    }
                                    if (entityiterator == this) {
                                        continue;
                                    }
                                    if (distanceTo(entityiterator) <= r) {
                                        h = entityiterator instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1;
                                        d = Math.min(damage * 4.5, Math.max(h * 0.25, damage * 1.5));
                                        entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "golem_attack"))), this),
                                                (float) d);
                                        breakShield(world, x, y, z, entityiterator, 120);
                                    }
                                }
                            }
                        });
                    }
                }
            }
            if (sklp2 > 0) {
                if ((Entity) this instanceof FlamarineGolemEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_skillP2, (int) (sklp2 - 1));
            } else if (dura <= 0) {
                if (!(enemy == null) && enemy.isAlive()) {
                    if (distanceTo(enemy) <= 5) {
                        if (this instanceof FlamarineGolemEntity) {
                            this.setAnimation("animation.flamarine_golem.combo");
                        }
                        if ((Entity) this instanceof FlamarineGolemEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_skillP2, 380);
                        if ((Entity) this instanceof FlamarineGolemEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_duration, 60);
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 30, 9, false, false));
                        CaerulaArborMod.queueServerWork(22, () -> {
                            if (this.isAlive()) {
                                this.combo(world, x, y, z, 5.75, 2);
                            }
                        });
                        CaerulaArborMod.queueServerWork(35, () -> {
                            if (this.isAlive()) {
                                this.combo(world, x, y, z, 6.5, 2.5);
                            }
                        });
                    }
                }
            }
            if (tickCount % 20 == 10) {
                if (!(enemy == null) && enemy.isAlive()) {
                    boolean once = false;
                    double dx;
                    double dy;
                    double dz;
                    double hardness;
                    BlockState block;
                    if (WorldUtils.canGrief(world)) {
                        dx = -1;
                        for (int index0 = 0; index0 < 3; index0++) {
                            dz = -1;
                            for (int index1 = 0; index1 < 3; index1++) {
                                dy = 0;
                                for (int index2 = 0; index2 < 4; index2++) {
                                    block = (world.getBlockState(BlockPos.containing(x + dx, y + dy, z + dz)));
                                    if (block.is(BlockTags.create(new ResourceLocation(CaerulaArborMod.MODID, "flamarine_destroyable")))) {
                                        hardness = block.getDestroySpeed(world, BlockPos.containing(0, 0, 0));
                                        if (hardness <= 2.5 && hardness >= 0 && world.getBlockFloorHeight(BlockPos.containing(x + dx, y + dy, z + dz)) > 0) {
                                            {
                                                BlockPos _pos = BlockPos.containing(x + dx, y + dy, z + dz);
                                                Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(x, y, z), null);
                                                world.destroyBlock(_pos, false);
                                            }
                                            if (world instanceof Level _level)
                                                _level.updateNeighborsAt(BlockPos.containing(x + dx, y + dy, z + dz), _level.getBlockState(BlockPos.containing(x + dx, y + dy, z + dz)).getBlock());
                                            once = true;
                                        }
                                    }
                                    dy = dy + 1;
                                }
                                dz = dz + 1;
                            }
                            dx = dx + 1;
                        }
                        if (once) {
                            if (world instanceof Level _level) {
                                if (!_level.isClientSide()) {
                                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.wither.break_block")), SoundSource.NEUTRAL, 1, 1);
                                } else {
                                    _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.wither.break_block")), SoundSource.NEUTRAL, 1, 1, false);
                                }
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
        return super.getDimensions(p_33597_).scale((float) 1.25);
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    public void customServerAiStep() {
        super.customServerAiStep();
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
    }


    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.18);
        builder = builder.add(Attributes.MAX_HEALTH, 270);
        builder = builder.add(Attributes.ARMOR, 23);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 19);
        builder = builder.add(Attributes.FOLLOW_RANGE, 32);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.isDeadOrDying()) {
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.flamarine_golem.die"));
        }

        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

                    && !this.isAggressive() && !this.isSprinting()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.flamarine_golem.move"));
            }
            if (this.isSprinting()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.flamarine_golem.sprint"));
            }
            if (this.isAggressive() && event.isMoving()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.flamarine_golem.sprint"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.flamarine_golem.idle"));
        }
        return PlayState.STOP;
    }

    private PlayState attackingPredicate(AnimationState event) {
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
            this.swinging = true;
            this.lastSwing = level().getGameTime();
        }
        if (this.swinging && this.lastSwing + 35L <= level().getGameTime()) {
            this.swinging = false;
        }
        if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.flamarine_golem.attack"));
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
        if (this.deathTime == 60) {
            this.remove(RemovalReason.KILLED);
            this.dropExperience();
            LevelAccessor world = this.level();
            if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                if (world instanceof ServerLevel _level) {
                    ItemEntity entityToSpawn = new ItemEntity(_level, this.getX(), (this.getY() + 1), this.getZ(), new ItemStack(CAItems.FLAMARINE_UPGRADE_TEMPLATE.get()));
                    entityToSpawn.setPickUpDelay(5);
                    _level.addFreshEntity(entityToSpawn);
                }
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
        data.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
        data.add(new AnimationController<>(this, "attacking", 0, this::attackingPredicate));
        data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
    }

    private void combo(LevelAccessor world, double x, double y, double z, double dist, double rate) {
        Entity enemy;
        double damage;
        enemy = this.getTarget();
        damage = (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * rate;
        if (world instanceof Level _level) {
            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.trident.hit")), SoundSource.HOSTILE, 1, 1);
        }
        if (!(enemy == null)) {
            this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((enemy.getX()), (enemy.getY()), (enemy.getZ())));
            if (this.distanceTo(enemy) <= dist) {
                enemy.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "golem_attack"))), this), (float) damage);
                breakShield(world, x, y, z, enemy, 120);
            }
        }
    }

    private void breakShield(LevelAccessor world, double x, double y, double z, Entity entity, int time) {
        if ((entity instanceof LivingEntity livingEntity ? livingEntity.getUseItem() : ItemStack.EMPTY).getItem() instanceof ShieldItem) {
            if (entity instanceof Player player) {
                player.getCooldowns().addCooldown(player.getUseItem().getItem(), time);
                player.stopUsingItem();
                player.level().broadcastEntityEvent(player, (byte) 30);
            }
            if (world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.shield.break")), SoundSource.PLAYERS, 1, 1);
            }
        }
    }

    private boolean isFlamarineDurative() {
        return this.isAlive() && this.getEntityData().get(DATA_duration) <= 0;
    }

    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}
