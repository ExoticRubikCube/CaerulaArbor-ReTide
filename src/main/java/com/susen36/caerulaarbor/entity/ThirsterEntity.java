package com.susen36.caerulaarbor.entity;

import com.susen36.babel.init.BabelAttributes;
import com.susen36.babel.init.BabelMobEffects;
import com.susen36.babel.util.EPUtils;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.base.SeaMonsterBoss;
import com.susen36.caerulaarbor.init.*;
import com.susen36.caerulaarbor.util.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ThirsterEntity extends SeaMonsterBoss {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(ThirsterEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(ThirsterEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(ThirsterEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_SKILL_P = SynchedEntityData.defineId(ThirsterEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_INTEGRATION = SynchedEntityData.defineId(ThirsterEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_DIZZY_NUM = SynchedEntityData.defineId(ThirsterEntity.class, EntityDataSerializers.INT);
    public String animationprocedure = "empty";
    public SoundEvent HURT = CASounds.SEABORN_GENERIC_HIT.get();
    public SoundEvent DIE = CASounds.SEABORN_DEATH.get();
    String prevAnim = "empty";
    private boolean swinging;
    private long lastSwing;

    public ThirsterEntity(Level world) {
        this(CAEntities.THIRSTER.get(), world);
    }

    public ThirsterEntity(EntityType<ThirsterEntity> type, Level world) {
        super(type, world);
        this.bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.BLUE, ServerBossEvent.BossBarOverlay.PROGRESS);
        xpReward = 24;
        setNoAi(false);
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.2);
        builder = builder.add(Attributes.MAX_HEALTH, 340);
        builder = builder.add(Attributes.ARMOR, 15);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 5);
        builder = builder.add(Attributes.FOLLOW_RANGE, 36);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 1);
        builder = builder.add(CAAttributes.GENERAL_DEFENSE, 10);
        builder = builder.add(BabelAttributes.MAGIC_RESISTANCE, 95);
        builder = builder.add(BabelAttributes.MAX_ELEMENTAL_VALUE, 2000);
        builder = builder.add(Attributes.STEP_HEIGHT, 1.25f);
        return builder;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHOOT, false);
        builder.define(DATA_ANIMATION, "undefined");
        builder.define(DATA_DURATION, 0);
        builder.define(DATA_SKILL_P, 0);
        builder.define(DATA_INTEGRATION, 0);
        builder.define(DATA_DIZZY_NUM, 2);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.1, false));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1) {
            @Override
            public boolean canUse() {
                return super.canUse() && isThirsterDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isThirsterDurative();
            }
        });
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && isThirsterDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isThirsterDurative();
            }
        });
        this.goalSelector.addGoal(5, new FloatGoal(this));
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public @NotNull SoundEvent getHurtSound(@NotNull DamageSource ds) {
        return HURT;
    }

    @Override
    public @NotNull SoundEvent getDeathSound() {
        return DIE;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.DROWN))
            return false;
        double healthBeforeDamage = this.getHealth();
        boolean damaged = super.hurt(source, amount);
        if (damaged) {
            LevelAccessor world = this.level();
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();
            double duration = this.getEntityData().get(DATA_DURATION);
            double integration = this.getEntityData().get(DATA_INTEGRATION) + Math.max(1, amount);
            double maxHealth = this.getMaxHealth();
            this.getEntityData().set(DATA_INTEGRATION, (int) integration);
            if (integration >= maxHealth * 0.15 && duration <= 0) {
                double dizzyTargetCount = this.getEntityData().get(DATA_DIZZY_NUM);
                Entity currentTarget = this.getTarget();
                new Object() {
                    void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                        double d = timedloopiterator * 4;
                        for (int index0 = 0; index0 < 120; index0++) {
                            double angle = index0 * 3;
                            if (world instanceof ServerLevel level)
                                level.sendParticles(ParticleTypes.CLOUD, (x + d * Math.sin(angle)), (y + 0.5), (z + d * Math.cos(angle)), 2, 0.1, 0.1, 0.1, 0.1);
                        }
                        final int tick2 = ticks;
                        CaerulaArbor.queueServerWork(tick2, () -> {
                            if (timedlooptotal > timedloopiterator + 1) {
                                timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                            }
                        });
                    }
                }.timedLoop(0, 5, 1);
                if (world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), CASounds.BISHOPFISH_ATTACK.get(), SoundSource.HOSTILE,
                            (float) 2.5, 1);
                }
                final Vec3 center = new Vec3(x, y, z);
                TagKey<EntityType<?>> oceanOffspringTag = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "seaborn"));
                List<LivingEntity> entfound = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(20),
                        e -> e.isAlive()
                                && !(e.getType().is(oceanOffspringTag) && e != currentTarget)
                                && !(e instanceof Player player && (player.isCreative() || player.isSpectator())))
                        .stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                for (LivingEntity entityiterator : entfound) {
                    if (this.distanceToSqr(entityiterator) < 400) {
                        if (!entityiterator.level().isClientSide())
                            entityiterator.addEffect(new MobEffectInstance(BabelMobEffects.STUN, 160, 0, false, false));
                        dizzyTargetCount = dizzyTargetCount - 1;
                        if (dizzyTargetCount <= 1) {
                            break;
                        }
                    }
                }
                this.getEntityData().set(DATA_INTEGRATION, 0);
                this.getEntityData().set(DATA_DURATION, 400);
                if (this.getAttributes().hasAttribute(CAAttributes.LIVING_BARRIER))
                    Objects.requireNonNull(this.getAttribute(CAAttributes.LIVING_BARRIER)).setBaseValue((maxHealth - healthBeforeDamage));
            }
        }
        return damaged;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Duration", this.entityData.get(DATA_DURATION));
        compound.putInt("SkillP", this.entityData.get(DATA_SKILL_P));
        compound.putInt("Integration", this.entityData.get(DATA_INTEGRATION));
        compound.putInt("DizzyNum", this.entityData.get(DATA_DIZZY_NUM));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Duration")) {
            this.entityData.set(DATA_DURATION, compound.getInt("Duration"));
        }
        if (compound.contains("SkillP")) {
            this.entityData.set(DATA_SKILL_P, compound.getInt("SkillP"));
        }
        if (compound.contains("Integration")) {
            this.entityData.set(DATA_INTEGRATION, compound.getInt("Integration"));
        }
        if (compound.contains("DizzyNum")) {
            this.entityData.set(DATA_DIZZY_NUM, compound.getInt("DizzyNum"));
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity target;
        double barr;
        double perc;
        double sklp1;
        double d;
        double maxH;
        double angle;
        double dura;
        if (!world.isClientSide()) {
            if (this.isAlive()) {
                sklp1 = (Entity) this instanceof ThirsterEntity datEntI ? datEntI.getEntityData().get(DATA_SKILL_P) : 0;
                dura = (Entity) this instanceof ThirsterEntity datEntI ? datEntI.getEntityData().get(DATA_DURATION) : 0;
                if ((Entity) this instanceof ThirsterEntity datEntI) {
                    datEntI.getEntityData().get(DATA_INTEGRATION);
                }
                target = this.getTarget();
                barr = this.getAttributes().hasAttribute(CAAttributes.LIVING_BARRIER)
                        ? Objects.requireNonNull(this.getAttribute(CAAttributes.LIVING_BARRIER)).getBaseValue()
                        : 0;
                if (dura > 0) {
                    if ((Entity) this instanceof ThirsterEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_DURATION, (int) (dura - 1));
                    if (barr <= 0) {
                        if ((Entity) this instanceof ThirsterEntity datEntSetI)
                            datEntSetI.getEntityData().set(DATA_DURATION, 0);
                        if ((Entity) this instanceof ThirsterEntity datEntSetI)
                            datEntSetI.getEntityData().set(DATA_DIZZY_NUM, 2);
                    }
                } else {
                    if (barr > 0) {
                        if (this.getAttributes().hasAttribute(CAAttributes.LIVING_BARRIER))
                            Objects.requireNonNull(this.getAttribute(CAAttributes.LIVING_BARRIER)).setBaseValue(0);
                        this.performSanityAttack();
                    }
                }
                if (sklp1 > 0) {
                    if ((Entity) this instanceof ThirsterEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_SKILL_P, (int) (sklp1 - 1));
                } else {
                    if (!(target == null) && target.isAlive()) {
                        if ((Entity) this instanceof ThirsterEntity datEntSetI)
                            datEntSetI.getEntityData().set(DATA_SKILL_P, 600);
                        if (this instanceof ThirsterEntity) {
                            this.setAnimation("animation.thirster.skill");
                        }
                        CaerulaArbor.queueServerWork(5, () -> {
                            Entity enemy1;
                            double num;
                            double tX = 0;
                            double tZ = 0;
                            double tY;
                            num = 2;
                            enemy1 = this.getTarget();
                            {
                                final Vec3 center = new Vec3(x, y, z);
                                List<LivingEntity> entfound = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(20), e -> true);
                                for (LivingEntity entityiterator : entfound) {
                                    if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "seaborn")))) {
                                        if (!(entityiterator == enemy1)) {
                                            continue;
                                        }
                                    }
                                    if (entityiterator instanceof Player) {
                                        if (new Object() {
                                            public boolean checkGamemode(Entity ent) {
                                                if (ent instanceof ServerPlayer serverPlayer) {
                                                    return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                                                } else if (ent.level().isClientSide() && ent instanceof Player player) {
                                                    return Objects.requireNonNull(Minecraft.getInstance().getConnection()).getPlayerInfo(player.getGameProfile().getId()) != null
                                                            && Objects.requireNonNull(Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId())).getGameMode() == GameType.CREATIVE;
                                                }
                                                return false;
                                            }
                                        }.checkGamemode(entityiterator) || new Object() {
                                            public boolean checkGamemode(Entity ent) {
                                                if (ent instanceof ServerPlayer serverPlayer) {
                                                    return serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
                                                } else if (ent.level().isClientSide() && ent instanceof Player player) {
                                                    return Objects.requireNonNull(Minecraft.getInstance().getConnection()).getPlayerInfo(player.getGameProfile().getId()) != null
                                                            && Objects.requireNonNull(Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId())).getGameMode() == GameType.SPECTATOR;
                                                }
                                                return false;
                                            }
                                        }.checkGamemode(entityiterator)) {
                                            continue;
                                        }
                                    }
                                    if (distanceTo(entityiterator) < 20) {
                                        num = num - 1;
                                        tX = tX + entityiterator.getX();
                                        tZ = tZ + entityiterator.getZ();
                                        if (world instanceof ServerLevel level)
                                            level.sendParticles(CAParticles.MOIST_BOOM.get(), (entityiterator.getX()), (entityiterator.getY() + 0.75), (entityiterator.getZ()), 8, 0.75, 0.75, 0.75, 0.1);
                                        CaerulaArbor.queueServerWork(15, () -> {
                                            entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.OCEAN_MAGIC, this), (float) (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? Objects.requireNonNull(this.getAttribute(Attributes.ATTACK_DAMAGE)).getValue() : 0));
                                        });
                                        if (num <= 0) {
                                            break;
                                        }
                                    }
                                }
                            }
                            if (num < 2) {
                                tX = tX / (2 - num);
                                tZ = tZ / (2 - num);
                                tY = WorldUtils.findFirstEmptyYAbove(world, tX, y, tZ);
                                if (!Double.isNaN(tY)) {
                                    if (world instanceof ServerLevel level) {
                                        Entity entityToSpawn = CAEntities.ABSORBER_LIMB.get().spawn(level, BlockPos.containing(tX, tY, tZ), MobSpawnType.MOB_SUMMONED);
                                        if (entityToSpawn != null) {
                                            entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
                maxH = this.getMaxHealth();
                if (maxH > 0) {
                    perc = barr / maxH;
                    if (perc > 0) {
                        for (int index0 = 0; index0 < 5; index0++) {
                            if (Math.random() < perc) {
                                angle = Mth.nextDouble(RandomSource.create(), 0, 6.283);
                                d = Mth.nextDouble(RandomSource.create(), 2, 2.5);
                                if (world instanceof ServerLevel level)
                                    level.sendParticles(ParticleTypes.ENCHANTED_HIT, (x + d * Math.sin(angle)), (y + 1), (z + d * Math.cos(angle)), 2, 0.1, 0.1, 0.1, 0.1);
                            }
                        }
                    }
                }
                if (tickCount % 20 == 10) {
                    double num;
                    Entity enemy1;
                    double result;
                    final Vec3 center1 = new Vec3(x, y, z);
                    List<AbsorberLimbEntity> entfound1 = world.getEntitiesOfClass(AbsorberLimbEntity.class,
                            new AABB(center1, center1).inflate(48 / 2d), AbsorberLimbEntity::isAlive);
                    result = entfound1.size();
                    num = result;
                    enemy1 = this.getTarget();
                    if (num > 0) {
                        {
                            final Vec3 center = new Vec3(x, y, z);
                            List<LivingEntity> entfound = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(20), e -> true);
                            for (LivingEntity entityiterator : entfound) {
                                if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "seaborn")))) {
                                    if (!(entityiterator == enemy1)) {
                                        continue;
                                    }
                                }
                                if (entityiterator instanceof Player) {
                                    if (new Object() {
                                        public boolean checkGamemode(Entity ent) {
                                            if (ent instanceof ServerPlayer serverPlayer) {
                                                return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                                            } else if (ent.level().isClientSide() && ent instanceof Player player) {
                                                return Objects.requireNonNull(Minecraft.getInstance().getConnection()).getPlayerInfo(player.getGameProfile().getId()) != null
                                                        && Objects.requireNonNull(Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId())).getGameMode() == GameType.CREATIVE;
                                            }
                                            return false;
                                        }
                                    }.checkGamemode(entityiterator) || new Object() {
                                        public boolean checkGamemode(Entity ent) {
                                            if (ent instanceof ServerPlayer serverPlayer) {
                                                return serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
                                            } else if (ent.level().isClientSide() && ent instanceof Player player) {
                                                return Objects.requireNonNull(Minecraft.getInstance().getConnection()).getPlayerInfo(player.getGameProfile().getId()) != null
                                                        && Objects.requireNonNull(Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId())).getGameMode() == GameType.SPECTATOR;
                                            }
                                            return false;
                                        }
                                    }.checkGamemode(entityiterator)) {
                                        continue;
                                    }
                                }
                                if (distanceToSqr(entityiterator) < 400) {
                                    entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.TRAIL_DAMAGE),
                                            (float) num);
                                    EPUtils.causeSanityInjury(entityiterator, this, num * 25);
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
    public void tick() {
        super.tick();
        float p = (float) this.entityData.get(DATA_DURATION) / 400f;
        if (p > 0 && !this.isDeadOrDying()) {
            this.bossInfo.setColor(ServerBossEvent.BossBarColor.WHITE);
            this.bossInfo.setProgress(p);
        } else {
            this.bossInfo.setColor(ServerBossEvent.BossBarColor.BLUE);
            float m = this.getMaxHealth();
            if (m > 0) this.bossInfo.setProgress(this.getHealth() / m);
        }
    }

    @Override
    public @NotNull EntityDimensions getDefaultDimensions(@NotNull Pose pose) {
        return super.getDefaultDimensions(pose).scale((float) 1.2);
    }

    @Override
    public boolean canUsePortal(boolean allowVehicles) {
        return false;
    }

    private void performSanityAttack() {
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = getZ(world, x, y);

        if (world instanceof Level level) {
            level.playSound(null, BlockPos.containing(x, y, z), CASounds.POCKET_SEA_CREEPER_EXPLODE.get(), SoundSource.HOSTILE, 3, 1);
        }

        this.getEntityData().set(DATA_DIZZY_NUM, this.getEntityData().get(DATA_DIZZY_NUM) + 1);

        final Vec3 center = new Vec3(x, y, z);
        List<LivingEntity> nearbyEntities = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(20),
                e -> !e.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "seaborn"))));

        for (LivingEntity entityiterator : nearbyEntities) {
            if (this.distanceToSqr(entityiterator) < 400) {
                EPUtils.causeSanityInjury(entityiterator, this, 1000);
            }
        }
    }

    private double getZ(LevelAccessor world, double x, double y) {
        double z = this.getZ();

        new Object() {
            void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                double d = timedloopiterator * 4;
                for (int index0 = 0; index0 < 120; index0++) {
                    double angle = index0 * 3;
                    if (world instanceof ServerLevel level)
                        level.sendParticles(ParticleTypes.ELECTRIC_SPARK, (x + d * Math.sin(angle)), (y + 0.5), (z + d * Math.cos(angle)), 2, 0.1, 0.1, 0.1, 0.1);
                }
                final int tick2 = ticks;
                CaerulaArbor.queueServerWork(tick2, () -> {
                    if (timedlooptotal > timedloopiterator + 1) {
                        timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                    }
                });
            }
        }.timedLoop(0, 5, 1);
        return z;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

            ) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.thirster.move"));
            }
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.thirster.die"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.thirster.idle"));
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
        if (this.swinging && this.lastSwing + 13L <= level().getGameTime()) {
            this.swinging = false;
        }
        if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.thirster.attack"));
        }
        return PlayState.CONTINUE;
    }

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

    private boolean isThirsterDurative() {
        return this.isAlive() && this.getEntityData().get(DATA_DURATION) <= 0;
    }

    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}