package com.susen36.caerulaarbor.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.api.anim.SyncedAnimationEntity;
import com.susen36.caerulaarbor.init.CADamageTypes;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CASounds;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Comparator;
import java.util.List;

public class GladiiaWhirlEntity extends PathfinderMob implements GeoEntity, SyncedAnimationEntity {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(GladiiaWhirlEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(GladiiaWhirlEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";

    public GladiiaWhirlEntity(Level world) {
        this(CAEntities.GLADIIA_WHIRL.get(), world);
    }

    public GladiiaWhirlEntity(EntityType<GladiiaWhirlEntity> type, Level world) {
        super(type, world);
        xpReward = 0;
        setNoAi(true);
        setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHOOT, false);
        builder.define(DATA_ANIMATION, "undefined");
    }

    @Override
    public void baseTick() {
        super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity target;
        double t;
        double damage;
        double d;
        t = tickCount;
        if (t >= 120) {
            if (!level().isClientSide())
                discard();
        }
        if (!(t >= 111)) {
            damage = (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 0.9;
            {
                final Vec3 center = new Vec3(x, y, z);
                List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(18 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                for (Entity entityiterator : entfound) {
                    d = entityiterator != null ? distanceTo(entityiterator) : -1;
                    if (entityiterator instanceof GladiiaEntity) {
                        continue;
                    }
                    if (entityiterator instanceof GladiiaWhirlEntity) {
                        continue;
                    }
                    if (!(entityiterator instanceof LivingEntity)) {
                        if (entityiterator != null && entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "no_join_whirl")))) {
                            continue;
                        }
                    }
                    target = entityiterator instanceof Mob mobEnt ? mobEnt.getTarget() : null;
                    if (entityiterator instanceof Player || (entityiterator instanceof TamableAnimal tamEnt && tamEnt.isTame())) {
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
                        if (new Object() {
                            public boolean checkGamemode(Entity ent) {
                                if (ent instanceof ServerPlayer serverPlayer) {
                                    return serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
                                } else if (ent.level().isClientSide() && ent instanceof Player player) {
                                    return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                            && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.SPECTATOR;
                                }
                                return false;
                            }
                        }.checkGamemode(entityiterator)) {
                            continue;
                        }
                        if (!(target instanceof GladiiaEntity)) {
                            continue;
                        }
                    }
                    if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "is_humanside")))) {
                        if (!(target instanceof GladiiaEntity)) {
                            continue;
                        }
                    }
                    if (d <= 9) {
                        EntityUtils.applyOrbitMotion(entityiterator, this);
                    }
                }
            }
            if (t % 20 == 11) {
                if (!world.isClientSide()) {
                    if (world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), CASounds.GLADIIA_SKILL_RIM.get(), SoundSource.NEUTRAL, 3, 1);
                    }
                }
                {
                    final Vec3 center = new Vec3(x, y, z);
                    List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(24 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                    for (Entity entityiterator : entfound) {
                        d = entityiterator != null ? distanceTo(entityiterator) : -1;
                        if (!(entityiterator instanceof LivingEntity)) {
                            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "no_join_whirl")))) {
                                continue;
                            }
                        }
                        if (entityiterator instanceof GladiiaEntity) {
                            continue;
                        }
                        if (entityiterator instanceof GladiiaWhirlEntity) {
                            continue;
                        }
                        target = entityiterator instanceof Mob mobEnt ? mobEnt.getTarget() : null;
                        if (entityiterator instanceof Player || (entityiterator instanceof TamableAnimal tamEnt && tamEnt.isTame())) {
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
                            if (new Object() {
                                public boolean checkGamemode(Entity ent) {
                                    if (ent instanceof ServerPlayer serverPlayer) {
                                        return serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
                                    } else if (ent.level().isClientSide() && ent instanceof Player player) {
                                        return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                                && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.SPECTATOR;
                                    }
                                    return false;
                                }
                            }.checkGamemode(entityiterator)) {
                                continue;
                            }
                            if (!(target instanceof GladiiaEntity)) {
                                continue;
                            }
                        }
                        if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "is_humanside")))) {
                            if (!(target instanceof GladiiaEntity)) {
                                continue;
                            }
                        }
                        if (d <= 12) {
                            if (entityiterator instanceof LivingEntity) {
                                entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.GLADIIA_MAGIC),
                                        (float) damage);
                                if (entityiterator instanceof LivingEntity entity && !entity.level().isClientSide())
                                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 1, false, false));
                            }
                            if (d >= 3) {
                                EntityUtils.pullToward(entityiterator, this);
                            }
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
    protected void doPush(Entity entityIn) {
    }

    @Override
    protected void pushEntities() {
    }

    @Override
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        return false;
    }

    @Override
    protected void actuallyHurt(@NotNull DamageSource pDamageSource, float pDamageAmount) {
    }

    @Override
    protected void markHurt() {
        this.hurtMarked = false;
    }

    @Override
    public boolean isInvulnerableTo(@NotNull DamageSource pSource) {
        return true;
    }

    @Override
    public void setHealth(float pHealth) {
        if (pHealth < this.getHealth()) return;
        super.setHealth(pHealth);
    }

    @Override
    public void setDeltaMovement(@NotNull Vec3 pDeltaMovement) {
        super.setDeltaMovement(Vec3.ZERO);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.updateSwingTime();
    }


    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0);
        builder = builder.add(Attributes.MAX_HEALTH, 10);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 27);
        builder = builder.add(Attributes.FOLLOW_RANGE, 1);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 99);
        builder = builder.add(Attributes.GRAVITY, 0);
        builder = builder.add(Attributes.STEP_HEIGHT, 0f);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.gladiia_whirl.idle"));
        }
        return PlayState.STOP;
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
        if (this.deathTime == 1) {
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