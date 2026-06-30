package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.init.CAAttributes;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.util.EntityPredicateUtils;
import com.apocalypse.caerulaarbor.util.EntityUtils;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

public class UlpiansEntity extends Animal implements GeoEntity, SyncedAnimationEntity {

    private boolean isUlpuansDurative() {
        return EntityPredicateUtils.isUlpuansDurative(this);
    }

    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(UlpiansEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_duration = SynchedEntityData.defineId(UlpiansEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_skillp1 = SynchedEntityData.defineId(UlpiansEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_skillp2 = SynchedEntityData.defineId(UlpiansEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_bonus = SynchedEntityData.defineId(UlpiansEntity.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean swinging;
    private boolean lastloop;
    private long lastSwing;
    public String animationprocedure = "empty";

    public UlpiansEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(CAEntities.ULPIANS.get(), world);
    }

    public UlpiansEntity(EntityType<UlpiansEntity> type, Level world) {
        super(type, world);
        xpReward = 0;
        setNoAi(false);
        setMaxUpStep(1f);
        setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIMATION, "undefined");
        this.entityData.define(DATA_duration, 0);
        this.entityData.define(DATA_skillp1, 80);
        this.entityData.define(DATA_skillp2, 160);
        this.entityData.define(DATA_bonus, 0);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.1, false) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return 9;
            }

            @Override
            public boolean canUse() {
                return super.canUse() && isUlpuansDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isUlpuansDurative();
            }

        });
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Monster.class, true, false));
        this.goalSelector.addGoal(4, new OpenDoorGoal(this, false));
        this.goalSelector.addGoal(5, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(6, new RandomStrollGoal(this, 1) {
            @Override
            public boolean canUse() {
                return super.canUse() && isUlpuansDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isUlpuansDurative();
            }
        });
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && isUlpuansDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isUlpuansDurative();
            }
        });
        this.goalSelector.addGoal(8, new FloatGoal(this));
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
    public SoundEvent getHurtSound(DamageSource ds) {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "ulpians_hit"));
    }

    @Override
    public SoundEvent getDeathSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "ulpians_die"));
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        double targetX = target.getX();
        double targetY = target.getY();
        double targetZ = target.getZ();
        if (!this.level().isClientSide()) {
            this.getEntityData().set(DATA_duration, this.getEntityData().get(DATA_duration) + 30);
            this.level().playSound(null, BlockPos.containing(targetX, targetY, targetZ),
                    ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "anchor_pre")), SoundSource.HOSTILE, 2.2F, 1);
            CaerulaArborMod.queueServerWork(14, () -> {
                if (this.isAlive()) {
                    Entity enemy = this.getTarget();
                    double damage = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
                    this.level().playSound(null, BlockPos.containing(targetX, targetY, targetZ),
                            ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "anchor_hit")), SoundSource.HOSTILE, 2.75F, 1);
                    final Vec3 center = new Vec3(this.getX(), this.getY(), this.getZ());
                    List<Entity> foundEntities = this.level().getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(48 / 2d), entity -> true).stream()
                            .sorted(Comparator.comparingDouble(candidate -> candidate.distanceToSqr(center))).toList();
                    for (Entity entityIterator : foundEntities) {
                        if (!(entityIterator instanceof LivingEntity)) {
                            continue;
                        }
                        if (!entityIterator.isAlive()) {
                            continue;
                        }
                        if (entityIterator instanceof ServerPlayer serverPlayer) {
                            if (serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE || serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR) {
                                continue;
                            }
                        }
                        if (entityIterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring"))) && entityIterator != enemy) {
                            continue;
                        }
                        if (entityIterator == this) {
                            continue;
                        }
                        if (this.distanceTo(entityIterator) <= 24) {
                            entityIterator.hurt(
                                    new DamageSource(
                                            this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                                                    .getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hunter_attack"))),
                                            this),
                                    (float) damage);
                            Vec3 pushVec = this.position().vectorTo(entityIterator.position());
                            if (pushVec.lengthSqr() < 0.0001) {
                                pushVec = new Vec3(0, 0, 1);
                            } else {
                                pushVec = pushVec.normalize();
                            }
                            pushVec = pushVec.scale(1.25);
                            entityIterator.push(pushVec.x, pushVec.y, pushVec.z);
                        }
                    }
                }
            });
        }
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        double healAmoun;
        if (this.isAlive()) {
            if (invulnerableTime <= 15) {
                healAmoun = 8;
                if (((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) < ((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.6) {
                    healAmoun = 12;
                }
                if ((Entity) this instanceof LivingEntity _entity)
                    _entity.setHealth((float) Math.min(((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) + healAmoun, (Entity) this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1));
            }
        }
        if (source.is(DamageTypes.FALL))
            return false;
        if (source.is(DamageTypes.DROWN))
            return false;
        return super.hurt(source, amount);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
        SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
        if (this != null) {
            if ((Entity) this instanceof LivingEntity _livingEntity1 && _livingEntity1.getAttributes().hasAttribute(ForgeMod.SWIM_SPEED.get()))
                _livingEntity1.getAttribute(ForgeMod.SWIM_SPEED.get())
                        .setBaseValue((((Entity) this instanceof LivingEntity _livingEntity0 && _livingEntity0.getAttributes().hasAttribute(ForgeMod.SWIM_SPEED.get()) ? _livingEntity0.getAttribute(ForgeMod.SWIM_SPEED.get()).getBaseValue() : 0) * 8));
            if ((Entity) this instanceof LivingEntity _livingEntity2 && _livingEntity2.getAttributes().hasAttribute(CAAttributes.SANITY_MODIFIER.get()))
                _livingEntity2.getAttribute(CAAttributes.SANITY_MODIFIER.get()).setBaseValue(0.33);
        }
        return retval;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Duration", this.entityData.get(DATA_duration));
        compound.putInt("PrimarySkillCooldown", this.entityData.get(DATA_skillp1));
        compound.putInt("SecondarySkillCooldown", this.entityData.get(DATA_skillp2));
        compound.putInt("BonusStacks", this.entityData.get(DATA_bonus));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Duration")) {
            this.entityData.set(DATA_duration, compound.getInt("Duration"));
        } else if (compound.contains("Dataduration")) {
            this.entityData.set(DATA_duration, compound.getInt("Dataduration"));
        }
        if (compound.contains("PrimarySkillCooldown")) {
            this.entityData.set(DATA_skillp1, compound.getInt("PrimarySkillCooldown"));
        } else if (compound.contains("Dataskillp1")) {
            this.entityData.set(DATA_skillp1, compound.getInt("Dataskillp1"));
        }
        if (compound.contains("SecondarySkillCooldown")) {
            this.entityData.set(DATA_skillp2, compound.getInt("SecondarySkillCooldown"));
        } else if (compound.contains("Dataskillp2")) {
            this.entityData.set(DATA_skillp2, compound.getInt("Dataskillp2"));
        }
        if (compound.contains("BonusStacks")) {
            this.entityData.set(DATA_bonus, compound.getInt("BonusStacks"));
        } else if (compound.contains("Databonus")) {
            this.entityData.set(DATA_bonus, compound.getInt("Databonus"));
        }
    }

    @Override
    public void awardKillScore(Entity entity, int score, DamageSource damageSource) {
        super.awardKillScore(entity, score, damageSource);
        LevelAccessor world = this.level();
        double bns;
        double perc;
        bns = (Entity) this instanceof UlpiansEntity _datEntI ? _datEntI.getEntityData().get(DATA_bonus) : 0;
        if (bns < 10) {
            if ((Entity) this instanceof UlpiansEntity _datEntSetI)
                _datEntSetI.getEntityData().set(DATA_bonus, (int) (bns + 1));
            {
                final Vec3 _center = new Vec3(this.getX(), this.getY(), this.getZ());
                List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(48 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                for (Entity entityiterator : _entfound) {
                    if (!(entityiterator instanceof LivingEntity)) {
                        continue;
                    }
                    if (entityiterator.isAlive()) {
                        if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hunters")))) {
                            perc = (entityiterator instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) / (entityiterator instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1);
                            if (entityiterator instanceof LivingEntity _livingEntity8 && _livingEntity8.getAttributes().hasAttribute(Attributes.MAX_HEALTH))
                                _livingEntity8.getAttribute(Attributes.MAX_HEALTH).setBaseValue(
                                        ((entityiterator instanceof LivingEntity _livingEntity7 && _livingEntity7.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? _livingEntity7.getAttribute(Attributes.MAX_HEALTH).getBaseValue() : 0) + 10));
                            if (entityiterator instanceof LivingEntity _entity)
                                _entity.setHealth((float) ((entityiterator instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * perc));
                            if (entityiterator instanceof LivingEntity _livingEntity12 && _livingEntity12.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
                                _livingEntity12.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(
                                        ((entityiterator instanceof LivingEntity _livingEntity11 && _livingEntity11.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity11.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : 0)
                                                + 2));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity enemy;
        double gap = 0;
        double sklp1;
        double dura;
        double skillp2;
        if (this.isAlive()) {
            sklp1 = (Entity) this instanceof UlpiansEntity _datEntI ? _datEntI.getEntityData().get(DATA_skillp1) : 0;
            skillp2 = (Entity) this instanceof UlpiansEntity _datEntI ? _datEntI.getEntityData().get(DATA_skillp2) : 0;
            dura = (Entity) this instanceof UlpiansEntity _datEntI ? _datEntI.getEntityData().get(DATA_duration) : 0;
            enemy = (Entity) this instanceof Mob _mobEnt ? _mobEnt.getTarget() : null;
            if (dura > 0) {
                if ((Entity) this instanceof UlpiansEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_duration, (int) (dura - 1));
            }
            if (sklp1 > 0) {
                if ((Entity) this instanceof UlpiansEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_skillp1, (int) (sklp1 - 1));
            } else {
                if (!(enemy == null) && enemy.isAlive()) {
                    if ((enemy != null ? distanceTo(enemy) : -1) <= 3.5) {
                        if (this instanceof UlpiansEntity) {
                            this.setAnimation("animation.ulpians.pull");
                        }
                        if ((Entity) this instanceof UlpiansEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_skillp1, 120);
                        if ((Entity) this instanceof UlpiansEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_duration, (int) (dura + 40));
                        if (world instanceof Level _level) {
                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "ulpians_pul_pre")), SoundSource.NEUTRAL, (float) 2.2, 1);
                        }
                        ((Entity) this).lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((enemy.getX()), (enemy.getY() + 1.6), (enemy.getZ())));
                        CaerulaArborMod.queueServerWork(13, () -> {
                            if (this.isAlive()) {
                                if (world instanceof Level _level) {
                                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "ulpians_pull_throw")), SoundSource.NEUTRAL, 3, 1);
                                }
                            }
                        });
                        CaerulaArborMod.queueServerWork(20, () -> {
                            if (this.isAlive()) {
                                Entity enemy1;
                                double damage;
                                double r;
                                double d;
                                enemy1 = (Entity) this instanceof Mob _mobEnt ? _mobEnt.getTarget() : null;
                                r = 6;
                                damage = (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 2.7;
                                if (world instanceof Level _level) {
                                    _level.playSound(null, BlockPos.containing(getX(), getY(), getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "ulpians_pull_hit")), SoundSource.NEUTRAL, 3, 1);
                                }
                                {
                                    final Vec3 _center = new Vec3((getX()), (getY()), (getZ()));
                                    List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(12 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                                    for (Entity entityiterator : _entfound) {
                                        if (!(entityiterator instanceof LivingEntity)) {
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
                                        d = entityiterator != null ? distanceTo(entityiterator) : -1;
                                        if (d <= r && (EntityUtils.getEntityCosine(this, entityiterator) > 0.5 || d <= 3)) {
                                            if (entityiterator instanceof LivingEntity _entity && !this.level().isClientSide())
                                                this.addEffect(new MobEffectInstance(CAMobEffects.DIZZY.get(), 40, 0, false, false));
                                            entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "anchor_smash"))), this),
                                                    (float) damage);
                                        }
                                    }
                                }
                            }
                        });
                        CaerulaArborMod.queueServerWork(24, () -> {
                            if (this.isAlive()) {
                                Entity enemy1;
                                double damage = 0;
                                double r;
                                enemy1 = (Entity) this instanceof Mob _mobEnt ? _mobEnt.getTarget() : null;
                                r = 4.5;
                                {
                                    final Vec3 _center = new Vec3(x, y, z);
                                    List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(9 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
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
                                        if ((entityiterator != null ? distanceTo(entityiterator) : -1) <= r && EntityUtils.getEntityCosine(this, entityiterator) > 0.6) {
                                            if (entityiterator == null || this == null)
                                                continue;
                                            Vec3 offset = position().add(entityiterator.position().reverse());
                                            if (offset.lengthSqr() <= 0.01) continue;
                                            offset = offset.normalize();
                                            entityiterator.push(offset.x, offset.y, offset.z);
                                        }
                                    }
                                }
                            }
                        });
                        CaerulaArborMod.queueServerWork(26, () -> {
                            if (this.isAlive()) {
                                if (world instanceof Level _level) {
                                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "ulpians_pull_pull")), SoundSource.NEUTRAL, (float) 2.5, 1);
                                }
                            }
                        });
                    }
                }
            }
            if (skillp2 > 0) {
                if ((Entity) this instanceof UlpiansEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_skillp2, (int) (skillp2 - 1));
            } else {
                if (!(enemy == null) && enemy.isAlive()) {
                    if ((enemy != null ? distanceTo(enemy) : -1) <= 24) {
                        if (this instanceof UlpiansEntity) {
                            this.setAnimation("animation.ulpians.skill");
                        }
                        if (EntityPredicateUtils.isSpecterAround(world, x, y, z)) {
                            if ((Entity) this instanceof UlpiansEntity _datEntSetI)
                                _datEntSetI.getEntityData().set(DATA_skillp2, 820);
                        } else {
                            if ((Entity) this instanceof UlpiansEntity _datEntSetI)
                                _datEntSetI.getEntityData().set(DATA_skillp2, 900);
                        }
                        if ((Entity) this instanceof UlpiansEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_duration, (int) (dura + 40));
                        if (world instanceof Level _level) {
                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "ulpians_pul_pre")), SoundSource.NEUTRAL, (float) 2.2, 1);
                        }
                        ((Entity) this).lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((enemy.getX()), (enemy.getY() + 1.6), (enemy.getZ())));
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 25, 9, false, false));
                        if (world instanceof Level _level) {
                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "ulpians_skill")), SoundSource.NEUTRAL, (float) 2.5, 1);
                        }
                        CaerulaArborMod.queueServerWork(16, () -> {
                            if (world instanceof Level _level) {
                                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "anchor_throw")), SoundSource.NEUTRAL, (float) 2.2, 1);
                            }
                        });
                        CaerulaArborMod.queueServerWork(22, () -> {
                            if (this.isAlive()) {
                                Entity enemy1;
                                double perc;
                                double damage;
                                double noeX;
                                double nowY;
                                double nowZ;
                                perc = ((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) / ((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1);
                                if (!this.level().isClientSide())
                                    this.addEffect(new MobEffectInstance(CAMobEffects.PATH_TO_UNCOVER.get(), 500, 0, false, true));
                                if ((Entity) this instanceof LivingEntity _entity)
                                    _entity.setHealth((float) (((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * perc));
                                enemy1 = (Entity) this instanceof Mob _mobEnt ? _mobEnt.getTarget() : null;
                                damage = (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 1.5;
                                if (!(enemy1 == null)) {
                                    {
                                        Entity _ent = this;
                                        _ent.teleportTo((enemy1.getX()), (enemy1.getY()), (enemy1.getZ()));
                                        if (_ent instanceof ServerPlayer _serverPlayer)
                                            _serverPlayer.connection.teleport((enemy1.getX()), (enemy1.getY()), (enemy1.getZ()), _ent.getYRot(), _ent.getXRot());
                                    }
                                    if (enemy1 instanceof LivingEntity _entity && !this.level().isClientSide())
                                        this.addEffect(new MobEffectInstance(CAMobEffects.DIZZY.get(), 120, 0, false, false));
                                    enemy1.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "anchor_smash"))), this), (float) damage);
                                }
                                noeX = getX();
                                nowY = getY();
                                nowZ = getZ();
                                {
                                    final Vec3 _center = new Vec3(noeX, nowY, nowZ);
                                    List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(12 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                                    for (Entity entityiterator : _entfound) {
                                        if (!(entityiterator instanceof LivingEntity)) {
                                            continue;
                                        }
                                        if (entityiterator instanceof Player || (entityiterator instanceof TamableAnimal _tamEnt && _tamEnt.isTame())) {
                                            continue;
                                        }
                                        if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "is_humanside")))) {
                                            continue;
                                        }
                                        if (entityiterator == this) {
                                            continue;
                                        }
                                        if (entityiterator == enemy1) {
                                            continue;
                                        }
                                        if ((entityiterator != null ? distanceTo(entityiterator) : -1) <= 6) {
                                            if (entityiterator instanceof LivingEntity && !this.level().isClientSide())
                                                this.addEffect(new MobEffectInstance(CAMobEffects.DIZZY.get(), 120, 0, false, false));
                                            entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "anchor_smash"))), this),
                                                    (float) damage);
                                        }
                                    }
                                }
                                if ((Entity) this instanceof LivingEntity _entity)
                                    _entity.removeEffect(CAMobEffects.DIZZY.get());
                                if ((Entity) this instanceof LivingEntity _entity)
                                    _entity.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
                                if ((Entity) this instanceof LivingEntity _entity)
                                    _entity.removeEffect(MobEffects.DIG_SLOWDOWN);
                                ModCapabilities.getSanityInjury(this).heal(1000);
                                if (world instanceof ServerLevel _level)
                                    _level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, noeX, nowY, nowZ, 72, 3, 3, 3, 0.5);
                                if (world instanceof Level _level) {
                                    _level.playSound(null, BlockPos.containing(noeX, nowY, nowZ), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "anchor_skill")), SoundSource.PLAYERS, 3, 1);
                                }
                            }
                        });
                    }
                }
            }
            GladiiaEntity.healFromGladiia(world, x, y, z, this);
        }
        this.refreshDimensions();
    }

    @Override
    public EntityDimensions getDimensions(Pose p_33597_) {
        return super.getDimensions(p_33597_).scale((float) 1);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageable) {
        UlpiansEntity retval = CAEntities.ULPIANS.get().create(serverWorld);
        retval.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(retval.blockPosition()), MobSpawnType.BREEDING, null, null);
        return retval;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return List.of().contains(stack.getItem());
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.updateSwingTime();
    }


    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.18);
        builder = builder.add(Attributes.MAX_HEALTH, 430);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 55);
        builder = builder.add(Attributes.FOLLOW_RANGE, 32);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 5);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

            ) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.ulpians.move"));
            }
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.ulpians.die"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.ulpians.idle"));
        }
        return PlayState.STOP;
    }

    private PlayState attackingPredicate(AnimationState event) {
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
            this.swinging = true;
            this.lastSwing = level().getGameTime();
        }
        if (this.swinging && this.lastSwing + 30L <= level().getGameTime()) {
            this.swinging = false;
        }
        if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.ulpians.attack"));
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
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }


    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}
