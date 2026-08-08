package com.susen36.caerulaarbor.entity;

import com.susen36.babel.init.BabelAttributes;
import com.susen36.babel.init.BabelMobEffects;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.init.*;
import com.susen36.caerulaarbor.util.EntityUtils;
import com.susen36.caerulaarbor.util.WorldUtils;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
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
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class IzumikEntity extends SeaMonster {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(IzumikEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(IzumikEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_GROWTH_P = SynchedEntityData.defineId(IzumikEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_SKILLP = SynchedEntityData.defineId(IzumikEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_PHASE = SynchedEntityData.defineId(IzumikEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_WAVE = SynchedEntityData.defineId(IzumikEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_SKILLP_1 = SynchedEntityData.defineId(IzumikEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_DEAL = SynchedEntityData.defineId(IzumikEntity.class, EntityDataSerializers.INT);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";
    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.PINK, ServerBossEvent.BossBarOverlay.NOTCHED_12);

    public IzumikEntity(Level world) {
        this(CAEntities.IZUMIK.get(), world);
    }

    public IzumikEntity(EntityType<IzumikEntity> type, Level world) {
        super(type, world);
        xpReward = 128;
        setNoAi(false);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(2f);
        setPersistenceRequired();
        setNoGravity(true);
        this.moveControl = new FlyingMoveControl(this, 10, true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHOOT, false);
        builder.define(DATA_ANIMATION, "undefined");
        builder.define(DATA_GROWTH_P, 0);
        builder.define(DATA_SKILLP, 5);
        builder.define(DATA_PHASE, 0);
        builder.define(DATA_WAVE, 8);
        builder.define(DATA_SKILLP_1, 100);
        builder.define(DATA_DEAL, 0);
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
    protected PathNavigation createNavigation(Level world) {
        return new FlyingPathNavigation(this, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && IzumikEntity.this.getEntityData().get(DATA_PHASE) > 0;
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && IzumikEntity.this.getEntityData().get(DATA_PHASE) > 0;
            }
        });
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this,  1.15, false) {

            @Override
            public boolean canUse() {
                return super.canUse() && IzumikEntity.this.getEntityData().get(DATA_PHASE) > 0;
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && IzumikEntity.this.getEntityData().get(DATA_PHASE) > 0;
            }

        });
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new FloatGoal(this));
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public SoundEvent getAmbientSound() {
        return CASounds.IZUMIK_AMBIENT.get();
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return CASounds.IZUMIK_HIT.get();
    }

    @Override
    public SoundEvent getDeathSound() {
        return CASounds.IZUMIK_DIE.get();
    }

    @Override
    public boolean causeFallDamage(float l, float d, DamageSource source) {
        return false;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        double targetX = target.getX();
        double targetY = target.getY();
        double targetZ = target.getZ();
        if (!this.level().isClientSide()) {
            CaerulaArbor.queueServerWork(7, () -> {
                this.level().playSound(null, BlockPos.containing(targetX, targetY, targetZ),
                        CASounds.IZUMIK_ATTACK.get(), SoundSource.HOSTILE, 1,
                        (float) Mth.nextDouble(RandomSource.create(), 0.85, 0.15));
                if (this.isAlive() && target.isAlive() && this.distanceTo(target) <= 13) {
                    target.hurt(
                            CADamageTypes.source(this.level(), CADamageTypes.IZUMIK_NORMAL_ATTACK, this), (float) (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0));
                    if (this.getEntityData().get(DATA_PHASE) >= 1) {
                        float oceanMagicDamage = (float) (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0);
                        if (MapVariables.get(this.level()).strategy_grow >= 4) {
                            oceanMagicDamage *= 1.5F;
                        }
                        target.hurt(
                                CADamageTypes.source(this.level(), CADamageTypes.OCEAN_MAGIC, this), oceanMagicDamage);
                        if (this.getEntityData().get(DATA_PHASE) >= 2 && Math.random() < 0.15 && target instanceof LivingEntity livingTarget) {
                            int currentNumb = livingTarget.hasEffect(BabelMobEffects.NUMB) ? livingTarget.getEffect(BabelMobEffects.NUMB).getAmplifier() + 1 : 0;
                            livingTarget.removeEffect(BabelMobEffects.NUMB);
                            livingTarget.addEffect(new MobEffectInstance(BabelMobEffects.NUMB, Integer.MAX_VALUE, currentNumb, false, false, true));
                            if (this.level() instanceof ServerLevel serverLevel) {
                                serverLevel.sendParticles(ParticleTypes.FIREWORK, targetX, targetY + 0.75, targetZ, 16, 0.75, 0.75, 0.75, 0.1);
                            }
                        }
                    }
                }
            });
        }
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        this.awardBoilingSeaAdvancement();
        if (source.is(DamageTypes.IN_FIRE))
            return false;
        if (source.getDirectEntity() instanceof ThrownPotion || source.getDirectEntity() instanceof AreaEffectCloud)
            return false;
        if (source.is(DamageTypes.CACTUS))
            return false;
        if (source.is(DamageTypes.DROWN))
            return false;
        if (source.is(DamageTypes.LIGHTNING_BOLT))
            return false;
        if (source.is(DamageTypes.EXPLOSION))
            return false;
        if (source.is(DamageTypes.FALLING_ANVIL))
            return false;
        if (source.is(DamageTypes.DRAGON_BREATH))
            return false;
        if (source.is(DamageTypes.WITHER))
            return false;
        if (source.is(DamageTypes.WITHER_SKULL))
            return false;
        boolean damaged = super.hurt(source, amount);
        if (damaged) {
            double accumulatedDamage = this.getEntityData().get(DATA_DEAL);
            if (accumulatedDamage >= this.getMaxHealth() * 0.3) {
                this.getEntityData().set(DATA_SKILLP, 0);
                this.getEntityData().set(DATA_DEAL, 0);
            } else {
                this.getEntityData().set(DATA_DEAL, (int) (accumulatedDamage + amount));
            }
        }
        return damaged;
    }

    @Override
    public void die(DamageSource source) {
        if (this.getEntityData().get(DATA_PHASE) == 1 && MapVariables.get(this.level()).strategy_silence >= 3) {
            if (!this.level().isClientSide()) {
                this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 200, 1, false, false));
                this.addEffect(new MobEffectInstance(CAMobEffects.FAKE_DEATH, 200, 1, false, false));
            }
            this.getEntityData().set(DATA_PHASE, 2);
            if (this.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE)) {
                this.getAttribute(CAAttributes.GENERAL_DEFENSE).setBaseValue(this.getAttribute(CAAttributes.GENERAL_DEFENSE).getBaseValue() + 2);
            }
            return;
        }
        super.die(source);
        this.awardBoilingSeaAdvancement();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata);
        if (this instanceof IzumikEntity) {
            this.setAnimation("animation.izumik.start");
        }
        if (!this.level().isClientSide())
            this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 30, 1, false, false));
        return retval;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("GrowthP", this.entityData.get(DATA_GROWTH_P));
        compound.putInt("Skillp", this.entityData.get(DATA_SKILLP));
        compound.putInt("Phase", this.entityData.get(DATA_PHASE));
        compound.putInt("Wave", this.entityData.get(DATA_WAVE));
        compound.putInt("Skillp1", this.entityData.get(DATA_SKILLP_1));
        compound.putInt("Deal", this.entityData.get(DATA_DEAL));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("GrowthP")) {
            this.entityData.set(DATA_GROWTH_P, compound.getInt("GrowthP"));
        }
        if (compound.contains("Skillp")) {
            this.entityData.set(DATA_SKILLP, compound.getInt("Skillp"));
        }
        if (compound.contains("Phase")) {
            this.entityData.set(DATA_PHASE, compound.getInt("Phase"));
        }
        if (compound.contains("Wave")) {
            this.entityData.set(DATA_WAVE, compound.getInt("Wave"));
        }
        if (compound.contains("Skillp1")) {
            this.entityData.set(DATA_SKILLP_1, compound.getInt("Skillp1"));
        }
        if (compound.contains("Deal")) {
            this.entityData.set(DATA_DEAL, compound.getInt("Deal"));
        }
    }

    @Override
    public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
        super.mobInteract(sourceentity, hand);
        Entity entity = this;
        if ((entity instanceof IzumikEntity datEntI ? datEntI.getEntityData().get(DATA_PHASE) : 0) == 0) {
            if (new Object() {
                public boolean checkGamemode(Entity ent) {
                    if (ent instanceof ServerPlayer serverPlayer) {
                        return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                    } else if (ent.level().isClientSide() && ent instanceof Player player) {
                        return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                    }
                    return false;
                }
            }.checkGamemode((Entity) sourceentity)) {
                if ((Entity) sourceentity instanceof Player player && !player.level().isClientSide())
                    player.displayClientMessage(Component.literal((Component.translatable("izumik.fastlearn").getString())), false);
                if ((Entity) sourceentity instanceof Player player && !player.level().isClientSide())
                    player.displayClientMessage(Component.literal((Component.translatable("izumik.saying").getString())), false);
                if (entity instanceof IzumikEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_GROWTH_P, 20);
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void baseTick() {
        super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        double sklp;
        double grow;
        double phase;
        double sklp1;
        double waves;
        double amplifi;
        if (this.isAlive()) {
            this.removeEffect(BabelMobEffects.DIZZY);
            this.removeEffect(CAMobEffects.FROZEN);
            sklp = this.getEntityData().get(DATA_SKILLP);
            sklp1 = this.getEntityData().get(DATA_SKILLP_1);
            grow = this.getEntityData().get(DATA_GROWTH_P);
            phase = this.getEntityData().get(DATA_PHASE);
            waves = this.getEntityData().get(DATA_WAVE);
            if (!this.hasEffect(CAMobEffects.IZUMIK_LEARN)) {
                amplifi = Math.floor(grow / 5);
                if (MapVariables.get(world).strategy_silence > 3) {
                    amplifi = amplifi * 4;
                } else if (MapVariables.get(world).strategy_grow > 3) {
                    amplifi = amplifi * 3;
                } else {
                    amplifi = amplifi * 2;
                }
                if (amplifi > 0) {
                    if (!this.level().isClientSide())
                        this.addEffect(new MobEffectInstance(CAMobEffects.IZUMIK_LEARN, 20, (int) (amplifi - 1), false, false));
                }
            }
            if (phase == 0) {
                this.removeEffect(MobEffects.REGENERATION);
                if (!this.hasEffect(CAMobEffects.INVULNERABLE)) {
                    if (!this.level().isClientSide())
                        this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 35, 2, false, false));
                }
                if (grow < 20) {
                    {
                        final Vec3 center = new Vec3(x, y, z);
                        Optional<IzumikOffspringEntity> nearestOffspring = world.getEntitiesOfClass(IzumikOffspringEntity.class, new AABB(center, center).inflate(7), e -> true)
                                .stream().min(Comparator.comparingDouble(e -> e.distanceToSqr(center)));
                        if (nearestOffspring.isPresent()) {
                            IzumikOffspringEntity entityiterator = nearestOffspring.get();
                            if (!entityiterator.level().isClientSide())
                                entityiterator.discard();
                            this.getEntityData().set(DATA_GROWTH_P, (int) (grow + 1));
                            this.getEntityData().set(DATA_SKILLP, (int) (sklp - 1));
                            if (world instanceof ServerLevel level)
                                level.sendParticles(ParticleTypes.CLOUD, (entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ()), 32, 0.6, 0.6, 0.6, 0.1);
                            CaerulaArbor.LOGGER.info("Izumik absorb offspr and grow to " + Math.round(grow + 1));
                        }
                    }
                } else {
                    if (this instanceof IzumikEntity) {
                        this.setAnimation("animation.izumik.revive");
                    }
                    this.getEntityData().set(DATA_PHASE, 1);
                    this.removeEffect(CAMobEffects.INVULNERABLE);
                    if (!this.level().isClientSide())
                        this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 300, 1, false, false));
                    this.getEntityData().set(DATA_SKILLP, 300);
                    {
                        List<Player> players = world.getEntitiesOfClass(Player.class, new AABB(new Vec3(x, y, z), new Vec3(x, y, z)).inflate(36), e -> true);
                        for (Player player : players) {
                            if (!player.level().isClientSide())
                                player.displayClientMessage(Component.literal((Component.translatable("izumik.saying").getString())), false);
                        }
                    }
                }
                if (sklp <= 0) {
                    this.getEntityData().set(DATA_SKILLP, 5);
                    if (grow + 1 < 20) {
                        if (this instanceof IzumikEntity) {
                            this.setAnimation("animation.izumik.grow");
                        }
                    }
                    CaerulaArbor.queueServerWork(25, () -> {
                        double rate;
                        double range;
                        if (world instanceof Level level) {
                            level.playSound(null, BlockPos.containing(x, y, z), CASounds.IZUMIK_LEARN.get(), SoundSource.HOSTILE, (float) 2.5, 1);
                        }
                        rate = 1;
                        range = 16;
                        if (MapVariables.get(world).strategy_grow >= 4) {
                            rate = 1.5;
                            range = 24;
                        }
                        for (int index0 = 0; index0 < 120; index0++) {
                            if (world instanceof ServerLevel level)
                                level.sendParticles(ParticleTypes.END_ROD, (x + range * Math.sin(Math.toRadians(index0 * 3))), (y + 0.5), (z + range * Math.cos(Math.toRadians(index0 * 3))), 3, 0.15, 0.5, 0.15, 0.15);
                        }
                        {
                            final Vec3 center = new Vec3(x, y, z);
                            TagKey<EntityType<?>> oceanOffspringTag = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "sea_born"));
                            List<LivingEntity> nearbyEntities = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(range),
                                    e -> (e instanceof Mob || e instanceof Player)
                                            && !(e.getType().is(oceanOffspringTag) && e != this.getTarget()));
                            for (LivingEntity entityiterator : nearbyEntities) {
                                if (distanceToSqr(entityiterator) <= range * range) {
                                    entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.IZUMIK_SKILL),
                                            (float) ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * rate));
                                }
                            }
                        }
                    });
                }
                if (waves <= 0) {
                    if (this instanceof IzumikEntity) {
                        this.setAnimation("animation.izumik.revive");
                    }
                    this.getEntityData().set(DATA_PHASE, 1);
                    this.removeEffect(CAMobEffects.INVULNERABLE);
                    if (!this.level().isClientSide())
                        this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 300, 1, false, false));
                    this.getEntityData().set(DATA_SKILLP, 300);
                    {
                        List<Player> players = world.getEntitiesOfClass(Player.class, new AABB(new Vec3(x, y, z), new Vec3(x, y, z)).inflate(36), e -> true);
                        for (Player player : players) {
                            if (!player.level().isClientSide())
                                player.displayClientMessage(Component.literal((Component.translatable("izumik.saying").getString())), false);
                        }
                    }
                }
                if (sklp1 <= 0 && waves > 0) {
                    for (int index0 = 0; index0 < 12; index0++) {
                        double range;
                        double t;
                        double tgtX = 0;
                        double tgtZ = 0;
                        double validY;
                        validY = Double.NaN;
                        for (int index1 = 0; index1 < 24; index1++) {
                            range = Mth.nextInt(RandomSource.create(), 28, 42);
                            t = Mth.nextDouble(RandomSource.create(), 0, 6.283);
                            tgtX = x + range * Math.sin(t);
                            tgtZ = z + range * Math.cos(t);
                            validY = WorldUtils.findValidSpawnY(world, x, y, z, tgtX, y + 4, tgtZ);
                            if (!Double.isNaN(validY)) {
                                break;
                            }
                        }
                        if (!Double.isNaN(validY)) {
                            if (world instanceof ServerLevel level) {
                                Entity entityToSpawn = CAEntities.IZUMIK_OFFSPRING.get().spawn(level, BlockPos.containing(tgtX, validY, tgtZ), MobSpawnType.MOB_SUMMONED);
                                if (entityToSpawn != null) {
                                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                                }
                            }
                            if (world instanceof ServerLevel level)
                                level.sendParticles(ParticleTypes.CLOUD, tgtX, validY, tgtZ, 32, 0.5, 0.5, 0.5, 0.15);
                        }
                    }
                    this.getEntityData().set(DATA_WAVE, (int) (waves - 1));
                    this.getEntityData().set(DATA_SKILLP_1, 600);
                } else {
                    this.getEntityData().set(DATA_SKILLP_1, (int) (sklp1 - 1));
                }
            } else {
                if (sklp <= 0) {
                    if (!(this.getTarget() == null) && this.getTarget().isAlive()) {
                        if ((this.getTarget() != null ? distanceTo(this.getTarget()) : -1) <= 24) {
                            if (phase >= 2) {
                                this.getEntityData().set(DATA_SKILLP, 400);
                            } else {
                                this.getEntityData().set(DATA_SKILLP, 600);
                            }
                            if (!this.level().isClientSide())
                                this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 50, 0, false, false));
                            if (this instanceof IzumikEntity) {
                                this.setAnimation("animation.izumik.skill");
                            }
                            CaerulaArbor.queueServerWork(35, () -> {
                                this.setHealth((float) ((this.getHealth()) + (this.getMaxHealth()) * 0.03));
                                if (this.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE))
                                    this.getAttribute(CAAttributes.GENERAL_DEFENSE)
                                            .setBaseValue(Math.min((this.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE)
                                                    ? this.getAttribute(CAAttributes.GENERAL_DEFENSE).getBaseValue()
                                                    : 0) + 1, (this.getMaxHealth()) * 0.05));
                                double range;
                                if (world instanceof Level level) {
                                    level.playSound(null, BlockPos.containing(x, y, z), CASounds.IZUMIK_SHOCK.get(), SoundSource.HOSTILE, (float) 3.5, 1);
                                }
                                range = 11;
                                if (MapVariables.get(world).strategy_grow >= 4) {
                                    range = 14;
                                }
                                if ((this.getEntityData().get(DATA_PHASE)) >= 2) {
                                    range = range + 3;
                                }
                                new Object() {
                                    void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                                        IzumikEntity.this.performShockAttack((timedloopiterator + 1) * 2);
                                        final int tick2 = ticks;
                                        CaerulaArbor.queueServerWork(tick2, () -> {
                                            if (timedlooptotal > timedloopiterator + 1) {
                                                timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                                            }
                                        });
                                    }
                                }.timedLoop(0, (int) range, 1);
                            });
                        }
                    }
                } else {
                    if ((this.getHealth()) < (this.getMaxHealth()) * 0.33) {
                        this.getEntityData().set(DATA_SKILLP, (int) (sklp - 2));
                    } else {
                        this.getEntityData().set(DATA_SKILLP, (int) (sklp - 1));
                    }
                }
            }
        }
        this.refreshDimensions();
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
    public boolean canUsePortal(boolean allowVehicles) {
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

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    public void setNoGravity(boolean ignored) {
        super.setNoGravity(true);
    }


    private void awardBoilingSeaAdvancement() {
        for (Entity playerEntity : new ArrayList<>(this.level().players())) {
            if (this.level().dimension() == playerEntity.level().dimension() && playerEntity instanceof ServerPlayer serverPlayer) {
                AdvancementHolder advancement = serverPlayer.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "boiling_sea"));
                AdvancementProgress advancementProgress = serverPlayer.getAdvancements().getOrStartProgress(advancement);
                if (!advancementProgress.isDone()) {
                    for (String criteria : advancementProgress.getRemainingCriteria()) {
                        serverPlayer.getAdvancements().award(advancement, criteria);
                    }
                }
            }
        }
    }

    private void performShockAttack(double r) {
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();

        double rate = 0.25;
        if (MapVariables.get(world).strategy_grow >= 4) {
            rate = 0.35;
        }

        for (int index0 = 0; index0 < 120; index0++) {
            if (world instanceof ServerLevel level)
                level.sendParticles(ParticleTypes.END_ROD, (x + r * Math.sin(Math.toRadians(index0 * 3))), (y + 0.5), (z + r * Math.cos(Math.toRadians(index0 * 3))), 3, 0.15, 0.5, 0.15, 0.15);
        }

        final Vec3 center = new Vec3(x, y, z);
        TagKey<EntityType<?>> oceanOffspringTag = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "sea_born"));
        List<LivingEntity> nearbyEntities = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(r),
                e -> (e instanceof Mob || e instanceof Player)
                        && !(e.getType().is(oceanOffspringTag) && e != this.getTarget()));

        for (LivingEntity entityiterator : nearbyEntities) {
            if (new Object() {
                public boolean checkGamemode(Entity ent) {
                    if (ent instanceof ServerPlayer serverPlayer) {
                        return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                    } else if (ent.level().isClientSide() && ent instanceof Player player) {
                        return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                    }
                    return false;
                }
            }.checkGamemode(entityiterator)) {
                continue;
            }
            if (this.distanceToSqr(entityiterator) <= r * r) {
                entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.IZUMIK_SKILL, this), (float) ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * rate));
                if (!entityiterator.hasEffect(CAMobEffects.IZUMIK_SHOCK)) {
                    if (!this.level().isClientSide())
                        this.addEffect(new MobEffectInstance(CAMobEffects.IZUMIK_SHOCK, 160, 0, false, false));
                }
                if (!entityiterator.hasEffect(BabelMobEffects.DIZZY)) {
                    if (!this.level().isClientSide())
                        this.addEffect(new MobEffectInstance(BabelMobEffects.DIZZY, 160, 0, false, false));
                }
                if (this.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE))
                    this.getAttribute(CAAttributes.GENERAL_DEFENSE)
                            .setBaseValue(Math.min((this.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE)
                                    ? this.getAttribute(CAAttributes.GENERAL_DEFENSE).getBaseValue()
                                    : 0) + 0.25, this.getMaxHealth() * 0.05));

                if ((this.getEntityData().get(DATA_PHASE) >= 2)) {
                    if (Math.random() < 0.33) {
                        if (MapVariables.get(world).strategy_grow >= 4) {
                            int currentNumb = entityiterator.hasEffect(BabelMobEffects.NUMB) ? entityiterator.getEffect(BabelMobEffects.NUMB).getAmplifier() + 1 : 0;
                            entityiterator.removeEffect(BabelMobEffects.NUMB);
                            entityiterator.addEffect(new MobEffectInstance(BabelMobEffects.NUMB, Integer.MAX_VALUE, currentNumb + 1, false, false, true));
                        } else {
                            int currentNumb = entityiterator.hasEffect(BabelMobEffects.NUMB) ? entityiterator.getEffect(BabelMobEffects.NUMB).getAmplifier() + 1 : 0;
                            entityiterator.removeEffect(BabelMobEffects.NUMB);
                            entityiterator.addEffect(new MobEffectInstance(BabelMobEffects.NUMB, Integer.MAX_VALUE, currentNumb, false, false, true));
                        }
                    }
                    this.setHealth((float) ((this.getHealth()) + (this.getMaxHealth()) * 0.01));
                }
            }
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.6);
        builder = builder.add(Attributes.MAX_HEALTH, 600);
        builder = builder.add(Attributes.ARMOR, 18);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 22);
        builder = builder.add(Attributes.FOLLOW_RANGE, 64);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
        builder = builder.add(Attributes.FLYING_SPEED, 0.6);
        builder = builder.add(CAAttributes.GENERAL_DEFENSE, 10);
        builder = builder.add(CAAttributes.MAGIC_RESISTANCE, 50);
        builder = builder.add(BabelAttributes.ELEMENTAL_MODIFIER, 0.01);
        builder = builder.add(BabelAttributes.MAX_ELEMENTAL_VALUE, 2000);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.izumik.die1"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.izumik.idle"));
        }
        return PlayState.STOP;
    }

    private PlayState attackingPredicate(AnimationState event) {
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
            this.swinging = true;
            this.lastSwing = level().getGameTime();
        }
        if (this.swinging && this.lastSwing + 19L <= level().getGameTime()) {
            this.swinging = false;
        }
        if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.izumik.attack"));
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
        if (this.deathTime >= 30) {
            this.remove(RemovalReason.KILLED);
            this.dropExperience(this.getKillCredit());
            LevelAccessor world = this.level();
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();
            if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                if (!world.isClientSide() && world.getServer() != null) {
                    BlockPos bpLootTblWorld = BlockPos.containing(x, y, z);
                    for (ItemStack itemstackiterator : world.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "gameplay/relic_izumik")))
                            .getRandomItems(new LootParams.Builder((ServerLevel) world).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(bpLootTblWorld)).withParameter(LootContextParams.BLOCK_STATE, world.getBlockState(bpLootTblWorld))
                                    .withOptionalParameter(LootContextParams.BLOCK_ENTITY, world.getBlockEntity(bpLootTblWorld)).create(LootContextParamSets.EMPTY))) {
                        if (world instanceof ServerLevel level) {
                            ItemEntity entityToSpawn = new ItemEntity(level, x, y, z, itemstackiterator);
                            entityToSpawn.setPickUpDelay(10);
                            entityToSpawn.setUnlimitedLifetime();
                            level.addFreshEntity(entityToSpawn);
                        }
                    }
                }
                for (int index0 = 0; index0 < 128; index0++) {
                    if (world instanceof ServerLevel level)
                        level.addFreshEntity(new ExperienceOrb(level, (x + Mth.nextDouble(RandomSource.create(), -1, 1)), y, (z + Mth.nextDouble(RandomSource.create(), -1, 1)), Mth.nextInt(RandomSource.create(), 16, 64)));
                }
            }
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
    public void remove(RemovalReason pReason) {
        if (this.level().getDifficulty() != Difficulty.PEACEFUL && pReason == RemovalReason.DISCARDED) {
            this.hurt(
                    CADamageTypes.source(this.level(), CADamageTypes.OCEANKILLER_DAMAGE),
                    20
            );
            return;
        } else if (pReason == RemovalReason.KILLED && !this.isDeadOrDying()) return;
        super.remove(pReason);
    }

    @Override
    public void setHealth(float pHealth) {
        if (pHealth <= 0 && this.getEntityData().get(DATA_PHASE) == 0) {
            super.setHealth(this.getMaxHealth() * 0.6f);
            if (!this.level().isClientSide()) {
                this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 300, 1, false, false));
            }
            this.getEntityData().set(DATA_PHASE, 1);
            if (this.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE)) {
                this.getAttribute(CAAttributes.GENERAL_DEFENSE).setBaseValue(this.getAttribute(CAAttributes.GENERAL_DEFENSE).getBaseValue() + 2);
            }
            return;
        }
        if (this.hasEffect(CAMobEffects.INVULNERABLE) && pHealth < this.getHealth()) return;
        float reduction = this.getHealth() - pHealth;
        float finalV = reduction >= this.getMaxHealth() * 0.33f ? this.getHealth() - this.getMaxHealth() * 0.33f : this.getHealth() - reduction;
        super.setHealth(finalV);
    }

    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}