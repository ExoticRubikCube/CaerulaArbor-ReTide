package com.susen36.caerulaarbor.entity;

import com.susen36.babel.init.BabelAttributes;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.init.*;
import com.susen36.caerulaarbor.util.EntityUtils;
import com.susen36.caerulaarbor.util.WorldUtils;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
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
import net.minecraft.sounds.SoundEvents;
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
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

public class FlamarineGolemEntity extends SeaMonster {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(FlamarineGolemEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(FlamarineGolemEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(FlamarineGolemEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_SKILL_P1 = SynchedEntityData.defineId(FlamarineGolemEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_SKILL_P2 = SynchedEntityData.defineId(FlamarineGolemEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_ADDITION = SynchedEntityData.defineId(FlamarineGolemEntity.class, EntityDataSerializers.INT);
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
        setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHOOT, false);
        builder.define(DATA_ANIMATION, "undefined");
        builder.define(DATA_DURATION, 0);
        builder.define(DATA_SKILL_P1, 6);
        builder.define(DATA_SKILL_P2, 100);
        builder.define(DATA_ADDITION, 40);
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
            public boolean canUse() {
                return super.canUse() && isFlamarineDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isFlamarineDurative();
            }

        });
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
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.IRON_GOLEM_STEP, 0.15f, 1);
    }

    @Override
    public SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        double targetX = target.getX();
        double targetY = target.getY();
        double targetZ = target.getZ();
        if (!this.level().isClientSide()) {
            this.getEntityData().set(DATA_DURATION, 35);
            CaerulaArbor.queueServerWork(20, () -> {
                if (this.isAlive() && target.isAlive() && this.distanceTo(target) <= 5.75) {
                    target.hurt(
                            CADamageTypes.source(this.level(), CADamageTypes.GOLEM_ATTACK, this), (float) (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0));
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
                sklp = (Entity) this instanceof FlamarineGolemEntity datEntI ? datEntI.getEntityData().get(DATA_SKILL_P1) : 0;
                if (sklp > 0) {
                    if ((Entity) this instanceof FlamarineGolemEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_SKILL_P1, (int) (sklp - 1));
                }
            }
            if (!(sourceentity instanceof FlamarineGolemEntity || sourceentity instanceof FlamarineStatueEntity)) {
                final Vec3 center = new Vec3(this.getX(), this.getY(), this.getZ());
                List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(32 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                for (Entity entityiterator : entfound) {
                    if (entityiterator instanceof FlamarineStatueEntity entity && sourceentity instanceof LivingEntity ent && ent.canBeSeenAsEnemy())
                        entity.setTarget(ent);
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
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata);
        if (this instanceof FlamarineGolemEntity) {
            this.setAnimation("animation.flamarine_golem.start");
        }
        if ((Entity) this instanceof FlamarineGolemEntity datEntSetI)
            datEntSetI.getEntityData().set(DATA_DURATION, 60);
        return retval;
    }

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Duration", this.entityData.get(DATA_DURATION));
        compound.putInt("SkillP1", this.entityData.get(DATA_SKILL_P1));
        compound.putInt("SkillP2", this.entityData.get(DATA_SKILL_P2));
        compound.putInt("Addition", this.entityData.get(DATA_ADDITION));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Duration")) {
            this.entityData.set(DATA_DURATION, compound.getInt("Duration"));
        }
        if (compound.contains("SkillP1")) {
            this.entityData.set(DATA_SKILL_P1, compound.getInt("SkillP1"));
        }
        if (compound.contains("SkillP2")) {
            this.entityData.set(DATA_SKILL_P2, compound.getInt("SkillP2"));
        }
        if (compound.contains("Addition")) {
            this.entityData.set(DATA_ADDITION, compound.getInt("Addition"));
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
        double sklp1;
        double sklp2;
        double dura;
        if (this.deathTime == 46) {
            if (!world.isClientSide()) {
                if (world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.TRIDENT_HIT_GROUND, SoundSource.HOSTILE, 2, 1);
                }
            }
        }
        if (this.isAlive()) {
            sklp1 = (Entity) this instanceof FlamarineGolemEntity datEntI ? datEntI.getEntityData().get(DATA_SKILL_P1) : 0;
            sklp2 = (Entity) this instanceof FlamarineGolemEntity datEntI ? datEntI.getEntityData().get(DATA_SKILL_P2) : 0;
            dura = (Entity) this instanceof FlamarineGolemEntity datEntI ? datEntI.getEntityData().get(DATA_DURATION) : 0;
            target = this.getTarget();
            if (dura > 0) {
                if ((Entity) this instanceof FlamarineGolemEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_DURATION, (int) (dura - 1));
            }
            if (sklp1 <= 0 && dura <= 0) {
                if (!(target == null) && target.isAlive()) {
                    if (distanceTo(target) <= 6) {
                        if (this instanceof FlamarineGolemEntity) {
                            this.setAnimation("animation.flamarine_golem.heavy");
                        }
                        if ((Entity) this instanceof FlamarineGolemEntity datEntSetI)
                            datEntSetI.getEntityData().set(DATA_SKILL_P1, 11);
                        if ((Entity) this instanceof FlamarineGolemEntity datEntSetI)
                            datEntSetI.getEntityData().set(DATA_DURATION, 40);
                        dura = 40;
                        CaerulaArbor.queueServerWork(20, () -> {
                            if (this.isAlive()) {
                                Entity enemy1;
                                double damage;
                                double r;
                                double h;
                                double d;
                                enemy1 = this.getTarget();
                                r = 6;
                                damage = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
                                if (world instanceof Level level) {
                                    if (!level.isClientSide()) {
                                        level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.ANVIL_LAND, SoundSource.HOSTILE, 1, 1);
                                    } else {
                                        level.playLocalSound(x, y, z, SoundEvents.ANVIL_LAND, SoundSource.HOSTILE, 1, 1, false);
                                    }
                                }
                                final Vec3 center = new Vec3(x, y, z);
                                List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(12 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                                for (Entity entityiterator : entfound) {
                                    if (!(entityiterator instanceof LivingEntity)) {
                                        continue;
                                    }
                                    if (!entityiterator.isAlive()) {
                                        continue;
                                    }
                                    if (entityiterator instanceof Player || (entityiterator instanceof TamableAnimal tamEnt && tamEnt.isTame())) {
                                        if (!(entityiterator == enemy1)) {
                                            continue;
                                        }
                                    }
                                    if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "is_humanside")))) {
                                        if (!(entityiterator == enemy1)) {
                                            continue;
                                        }
                                    }
                                    if (entityiterator == this) {
                                        continue;
                                    }
                                    if (distanceTo(entityiterator) <= r) {
                                        h = entityiterator instanceof LivingEntity livEnt ? livEnt.getHealth() : -1;
                                        d = Math.min(damage * 4.5, Math.max(h * 0.25, damage * 1.5));
                                        entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.GOLEM_ATTACK, this), (float) d);
                                        breakShield(world, x, y, z, entityiterator, 120);
                                    }
                                }
                            }
                        });
                    }
                }
            }
            if (sklp2 > 0) {
                if ((Entity) this instanceof FlamarineGolemEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_SKILL_P2, (int) (sklp2 - 1));
            } else if (dura <= 0) {
                if (!(target == null) && target.isAlive()) {
                    if (distanceTo(target) <= 5) {
                        if (this instanceof FlamarineGolemEntity) {
                            this.setAnimation("animation.flamarine_golem.combo");
                        }
                        if ((Entity) this instanceof FlamarineGolemEntity datEntSetI)
                            datEntSetI.getEntityData().set(DATA_SKILL_P2, 380);
                        if ((Entity) this instanceof FlamarineGolemEntity datEntSetI)
                            datEntSetI.getEntityData().set(DATA_DURATION, 60);
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 30, 9, false, false));
                        CaerulaArbor.queueServerWork(22, () -> {
                            if (this.isAlive()) {
                                this.combo(world, x, y, z, 5.75, 2);
                            }
                        });
                        CaerulaArbor.queueServerWork(35, () -> {
                            if (this.isAlive()) {
                                this.combo(world, x, y, z, 6.5, 2.5);
                            }
                        });
                    }
                }
            }
            if (tickCount % 20 == 10) {
                if (!(target == null) && target.isAlive()) {
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
                                    if (block.is(BlockTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "flamarine_destroyable")))) {
                                        hardness = block.getDestroySpeed(world, BlockPos.containing(0, 0, 0));
                                        if (hardness <= 2.5 && hardness >= 0 && world.getBlockFloorHeight(BlockPos.containing(x + dx, y + dy, z + dz)) > 0) {
                                            {
                                                BlockPos pos = BlockPos.containing(x + dx, y + dy, z + dz);
                                                Block.dropResources(world.getBlockState(pos), world, BlockPos.containing(x, y, z), null);
                                                world.destroyBlock(pos, false);
                                            }
                                            if (world instanceof Level level)
                                                level.updateNeighborsAt(BlockPos.containing(x + dx, y + dy, z + dz), level.getBlockState(BlockPos.containing(x + dx, y + dy, z + dz)).getBlock());
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
                            if (world instanceof Level level) {
                                if (!level.isClientSide()) {
                                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.WITHER_BREAK_BLOCK, SoundSource.NEUTRAL, 1, 1);
                                } else {
                                    level.playLocalSound(x, y, z, SoundEvents.WITHER_BREAK_BLOCK, SoundSource.NEUTRAL, 1, 1, false);
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
    public EntityDimensions getDefaultDimensions(Pose p_33597_) {
        return super.getDefaultDimensions(p_33597_).scale((float) 1.25);
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


    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.18);
        builder = builder.add(CAAttributes.GENERAL_DEFENSE, 5);
        builder = builder.add(BabelAttributes.MAGIC_RESISTANCE, 15);
        builder = builder.add(CAAttributes.SANITY_RESISTANCE, 60);
        builder = builder.add(Attributes.MAX_HEALTH, 270);
        builder = builder.add(Attributes.ARMOR, 23);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 19);
        builder = builder.add(Attributes.FOLLOW_RANGE, 32);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
        builder = builder.add(Attributes.STEP_HEIGHT, 1.5f);
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
            this.dropExperience(this.getKillCredit());
            LevelAccessor world = this.level();
            if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                if (world instanceof ServerLevel level) {
                    ItemEntity entityToSpawn = new ItemEntity(level, this.getX(), (this.getY() + 1), this.getZ(), new ItemStack(CAItems.FLAMARINE_UPGRADE_TEMPLATE.get()));
                    entityToSpawn.setPickUpDelay(5);
                    level.addFreshEntity(entityToSpawn);
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

    private void combo(LevelAccessor world, double x, double y, double z, double dist, double rate) {
        Entity target;
        double damage;
        target = this.getTarget();
        damage = (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * rate;
        if (world instanceof Level level) {
            level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.TRIDENT_HIT, SoundSource.HOSTILE, 1, 1);
        }
        if (!(target == null)) {
            this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((target.getX()), (target.getY()), (target.getZ())));
            if (this.distanceTo(target) <= dist) {
                target.hurt(CADamageTypes.source(world, CADamageTypes.GOLEM_ATTACK, this), (float) damage);
                breakShield(world, x, y, z, target, 120);
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
                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.SHIELD_BREAK, SoundSource.PLAYERS, 1, 1);
            }
        }
    }

    private boolean isFlamarineDurative() {
        return this.isAlive() && this.getEntityData().get(DATA_DURATION) <= 0;
    }

    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}