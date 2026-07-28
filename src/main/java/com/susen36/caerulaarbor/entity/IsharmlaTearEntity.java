package com.susen36.caerulaarbor.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.base.SyncedAnimationEntity;
import com.susen36.caerulaarbor.init.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

public class IsharmlaTearEntity extends PathfinderMob implements GeoEntity, SyncedAnimationEntity {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(IsharmlaTearEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(IsharmlaTearEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_FUNC_COOLDOWN = SynchedEntityData.defineId(IsharmlaTearEntity.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public String animationprocedure = "empty";
    String prevAnim = "empty";
    private boolean swinging;
    private long lastSwing;

    public IsharmlaTearEntity(Level world) {
        this(CAEntities.ISHARMLA_TEAR.get(), world);
    }

    public IsharmlaTearEntity(EntityType<IsharmlaTearEntity> type, Level world) {
        super(type, world);
        xpReward = 5;
        setNoAi(false);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(0.6f);
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder = builder.add(Attributes.MAX_HEALTH, 60);
        builder = builder.add(Attributes.ARMOR, 8);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 5);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
        builder = builder.add(CAAttributes.MAGIC_RESISTANCE, 60);
        builder = builder.add(CAAttributes.GENERAL_DEFENSE, 6);
        return builder;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHOOT, false);
        builder.define(DATA_ANIMATION, "undefined");
        builder.define(DATA_FUNC_COOLDOWN, 85);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    private boolean performHurtAttack() {
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();

        double damage = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;

        final Vec3 center = new Vec3(x, (y + 1), z);
        List<Entity> nearbyEntities = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(1.5), e -> true).stream()
                .sorted(Comparator.comparingDouble(ent -> ent.distanceToSqr(center)))
                .toList();

        for (Entity entityiterator : nearbyEntities) {
            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring")))) {
                continue;
            }
            if (!(entityiterator instanceof LivingEntity)) {
                continue;
            }
            if (isCreativePlayer(entityiterator)) {
                continue;
            }
            entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.ISHARMLA_ATTACK, this), (float) damage);
            return true;
        }
        return false;
    }

    private boolean isCreativePlayer(Entity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
        }
        if (entity.level().isClientSide() && entity instanceof Player player) {
            var connection = Minecraft.getInstance().getConnection();
            var playerInfo = connection == null ? null : connection.getPlayerInfo(player.getGameProfile().getId());
            return playerInfo != null && playerInfo.getGameMode() == GameType.CREATIVE;
        }
        return false;
    }

    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        this.spawnAtLocation(new ItemStack(CAItems.TEAR_ISHARMLA.get()));
    }

    @Override
    public SoundEvent getHurtSound(@NotNull DamageSource ds) {
        return SoundEvents.SCULK_SENSOR_HIT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.SCULK_SENSOR_BREAK;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.DROWN))
            return false;
        return super.hurt(source, amount);
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor world, @NotNull DifficultyInstance difficulty, @NotNull MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata);
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        if ((LevelAccessor) world instanceof Level level) {
            level.playSound(null, BlockPos.containing(x, y, z), CASounds.ISHARMLA_TEAR_PLACE.get(), SoundSource.HOSTILE, 2, 1);
        }
        if (this instanceof IsharmlaTearEntity) {
            this.setAnimation("animation.isharmla_tear.start");
        }
        return retval;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("FuncCooldown", this.entityData.get(DATA_FUNC_COOLDOWN));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("FuncCooldown")) {
            this.entityData.set(DATA_FUNC_COOLDOWN, compound.getInt("FuncCooldown"));
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        double dura;
        boolean isAttack;
        {
            LivingEntity ent = this;
            ent.setYRot(0);
            ent.setXRot(0);
            ent.setYBodyRot(ent.getYRot());
            ent.setYHeadRot(ent.getYRot());
            ent.yRotO = ent.getYRot();
            ent.xRotO = ent.getXRot();
            ent.yBodyRotO = ent.getYRot();
            ent.yHeadRotO = ent.getYRot();
        }
        setDeltaMovement(new Vec3(0, 0, 0));
        if (this.isAlive()) {
            dura = (Entity) this instanceof IsharmlaTearEntity datEntI ? datEntI.getEntityData().get(DATA_FUNC_COOLDOWN) : 0;
            if (dura > 0) {
                if ((Entity) this instanceof IsharmlaTearEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_FUNC_COOLDOWN, (int) (dura - 1));
            } else {
                isAttack = this.performHurtAttack();
                if (isAttack || this.tryConsumeIsharmlaSkillPoint()) {
                    if (this instanceof IsharmlaTearEntity) {
                        this.setAnimation("animation.isharmla_tear.attack");
                    }
                    if ((Entity) this instanceof IsharmlaTearEntity datEntSetI)
                        datEntSetI.getEntityData().set(DATA_FUNC_COOLDOWN, 60);
                    if (isAttack) {
                        if (world instanceof Level level) {
                            level.playSound(null, BlockPos.containing(x, y, z), CASounds.ISHARMLA_TEAR_HURT_1.get(), SoundSource.HOSTILE, 2, 1);
                        }
                    } else {
                        if (world instanceof Level level) {
                            level.playSound(null, BlockPos.containing(x, y, z), CASounds.ISHARMLA_TEAR_HURT_0.get(), SoundSource.HOSTILE, 2, 1);
                        }
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
    protected void doPush(@NotNull Entity entityIn) {
    }

    private boolean tryConsumeIsharmlaSkillPoint() {
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();

        Entity isharmla = world.getEntitiesOfClass(IsharmlaEntity.class, AABB.ofSize(new Vec3(x, y, z), 64, 64, 64), e -> true).stream()
                .min(Comparator.comparingDouble(ent -> ent.distanceToSqr(x, y, z)))
                .orElse(null);

        if (isharmla == null) {
            return false;
        }

        if (isharmla instanceof IsharmlaEntity datEntL2 && datEntL2.getEntityData().get(IsharmlaEntity.DATA_IS_MONSTER)) {
            return false;
        }

        double skillP = isharmla instanceof IsharmlaEntity datEntI ? datEntI.getEntityData().get(IsharmlaEntity.DATA_SKILLP_1) : 0;
        if (skillP > 0) {
            if (isharmla instanceof IsharmlaEntity datEntSetI)
                datEntSetI.getEntityData().set(IsharmlaEntity.DATA_SKILLP_1, (int) Math.max(skillP - 500, 0));
            return true;
        }
        return false;
    }

    @Override
    protected void pushEntities() {
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.updateSwingTime();
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.isharmla_tear.die"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.isharmla_tear.idle"));
        }
        return PlayState.STOP;
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

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 15) {
            this.remove(RemovalReason.KILLED);
            this.dropExperience(this.getKillCredit());
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