package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CAAttributes;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAItems;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
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
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

public class OceanizedBruteEntity extends SeaMonster {
    public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(OceanizedBruteEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(OceanizedBruteEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_ability = SynchedEntityData.defineId(OceanizedBruteEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_skillp = SynchedEntityData.defineId(OceanizedBruteEntity.class, EntityDataSerializers.INT);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";
    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.YELLOW, ServerBossEvent.BossBarOverlay.PROGRESS);

    public OceanizedBruteEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(CAEntities.OCEANIZED_BRUTE.get(), world);
    }

    public OceanizedBruteEntity(EntityType<OceanizedBruteEntity> type, Level world) {
        super(type, world);
        xpReward = 32;
        setNoAi(false);
        setMaxUpStep(1f);
        setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SHOOT, false);
        this.entityData.define(ANIMATION, "undefined");
        this.entityData.define(DATA_ability, 0);
        this.entityData.define(DATA_skillp, 3);
    }


    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1, true) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return 6.76;
            }
        });
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Hoglin.class, true, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, WitherSkeleton.class, true, false));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, WitherBoss.class, true, false));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, IronGolem.class, true, false));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, SnowGolem.class, true, false));
        this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, Villager.class, true, false));
        this.targetSelector.addGoal(9, new NearestAttackableTargetGoal<>(this, Illusioner.class, true, false));
        this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, Pillager.class, true, false));
        this.targetSelector.addGoal(11, new NearestAttackableTargetGoal<>(this, Vindicator.class, true, false));
        this.targetSelector.addGoal(12, new NearestAttackableTargetGoal<>(this, Witch.class, true, false));
        this.targetSelector.addGoal(13, new NearestAttackableTargetGoal<>(this, Piglin.class, true, false));
        this.targetSelector.addGoal(14, new NearestAttackableTargetGoal<>(this, PiglinBrute.class, true, false));
        this.targetSelector.addGoal(15, new NearestAttackableTargetGoal<>(this, ZombifiedPiglin.class, true, false));
        this.goalSelector.addGoal(17, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(18, new RandomStrollGoal(this, 1));
        this.goalSelector.addGoal(19, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(20, new FloatGoal(this));
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public SoundEvent getAmbientSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "brute_ambient"));
    }

    @Override
    public void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.piglin_brute.step")), 0.15f, 1);
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "brute_hurt"));
    }

    @Override
    public SoundEvent getDeathSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "brute_die"));
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (!this.level().isClientSide()) {
            CaerulaArborMod.queueServerWork(10, () -> {
                if (this.isAlive() && target.isAlive() && this.distanceTo(target) <= 2.6) {
                    boolean damaged = target.hurt(
                            new DamageSource(
                                    this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                                            .getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "general_seaborn_attack"))),
                                    this),
                            (float) (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0));
                    if (damaged && target instanceof Player player) {
                        recordHurtPlayer(player);
                    }
                }
            });
        }
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity sourceentity = source.getEntity();
        if (sourceentity != null) {
            double sklp;
            if (this.isAlive()) {
                sklp = (Entity) this instanceof OceanizedBruteEntity _datEntI ? _datEntI.getEntityData().get(DATA_skillp) : 0;
                if (sklp >= 0) {
                    if ((Entity) this instanceof OceanizedBruteEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_skillp, (int) (sklp - 1));
                } else {
                    if (distanceTo(sourceentity) <= 5 && !this.hasEffect(CAMobEffects.COOLDOWN_SINAL.get())) {
                        if (this instanceof OceanizedBruteEntity) {
                            this.setAnimation("animation.oceanized_brute.skill");
                        }
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 40, 1, false, false));
                        ((Entity) this).lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((sourceentity.getX()), (sourceentity.getY()), (sourceentity.getZ())));
                        if ((Entity) this instanceof OceanizedBruteEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_skillp, 5);
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CAMobEffects.COOLDOWN_SINAL.get(), 80, 0, false, false));
                        CaerulaArborMod.queueServerWork(20, () -> {
                            if (this.isAlive()) {
                                double sklp1;
                                sklp1 = Math.max(
                                        Math.min((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 4,
                                                ((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.25),
                                        (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 1);
                                if (distanceTo(sourceentity) <= 3) {
                                    if (sourceentity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "general_seaborn_attack"))), this),
                                            (float) sklp1) && sourceentity instanceof Player player) {
                                        recordHurtPlayer(player);
                                    }
                                    if (sourceentity instanceof LivingEntity _entity1 && !_entity1.level().isClientSide())
                                        _entity1.addEffect(new MobEffectInstance(CAMobEffects.DIZZY.get(), 120, 0, false, false));
                                    if (sourceentity instanceof LivingEntity _entity1 && !_entity1.level().isClientSide())
                                        _entity1.addEffect(new MobEffectInstance(CAMobEffects.ROCK_BREAK.get(), 120, 0, false, false));
                                }
                                if (world instanceof Level _level) {
                                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.shield.break")), SoundSource.HOSTILE, 2, 1);
                                }
                                final Vec3 _center = new Vec3((x + 2 * getLookAngle().x), y, (z + 2 * getLookAngle().z));
                                List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(6 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                                for (Entity entityiterator : _entfound) {
                                    if (!(entityiterator instanceof Mob)) {
                                        continue;
                                    }
                                    if (entityiterator == sourceentity) {
                                        continue;
                                    }
                                    if (entityiterator == this) {
                                        continue;
                                    }
                                    if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                                        if (!(((Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) == entityiterator)) {
                                            continue;
                                        }
                                    }
                                    if ((entityiterator != null ? distanceTo(entityiterator) : -1) <= 3) {
                                        sklp1 = Math.max(
                                                Math.min((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 3,
                                                        ((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.25),
                                                (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 1);
                                        entityiterator.hurt(
                                                new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "general_seaborn_attack"))), this),
                                                (float) sklp1);
                                        if (entityiterator instanceof LivingEntity _entity1 && !_entity1.level().isClientSide())
                                            _entity1.addEffect(new MobEffectInstance(CAMobEffects.DIZZY.get(), 120, 0, false, false));
                                        if (entityiterator instanceof LivingEntity _entity1 && !_entity1.level().isClientSide())
                                            _entity1.addEffect(new MobEffectInstance(CAMobEffects.ROCK_BREAK.get(), 120, 0, false, false));
                                    }
                                }
                            }
                        });
                    }
                }
            }
            if (!(sourceentity instanceof OceanizedPiglinEntity)) {
                {
                    final Vec3 _center = new Vec3(x, y, z);
                    List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(32 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                    for (Entity entityiterator : _entfound) {
                        if (entityiterator instanceof OceanizedPiglinEntity) {
                            if (entityiterator instanceof Mob _entity && sourceentity instanceof LivingEntity _ent)
                                _entity.setTarget(_ent);
                        }
                    }
                }
            }
        }
        if (source.is(DamageTypes.IN_FIRE))
            return false;
        if (source.is(DamageTypes.FALL))
            return false;
        if (source.is(DamageTypes.DROWN))
            return false;
        return super.hurt(source, amount);
    }

    private void recordHurtPlayer(Player player) {
        String hurtPlayerNames = getPersistentData().getString("hurtPlayer");
        String playerName = player.getDisplayName().getString();
        if (!hurtPlayerNames.contains(playerName)) {
            getPersistentData().putString("hurtPlayer", hurtPlayerNames + "," + playerName);
        }
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        LevelAccessor world = this.level();
        Entity sourceentity = source.getEntity();
        if (sourceentity == null)
            return;
        String str;
        String name;
        if (sourceentity instanceof Player && !(sourceentity instanceof ServerPlayer _plr1 && _plr1.level() instanceof ServerLevel
                && _plr1.getAdvancements().getOrStartProgress(_plr1.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "kill_brute"))).isDone())) {
            str = getPersistentData().getString("hurtPlayer");
            name = sourceentity.getDisplayName().getString();
            if (!str.contains(name)) {
                if (sourceentity instanceof ServerPlayer _player) {
                    Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "kill_brute"));
                    AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
                    if (!_ap.isDone()) {
                        for (String criteria : _ap.getRemainingCriteria())
                            _player.getAdvancements().award(_adv, criteria);
                    }
                }
                if (world instanceof ServerLevel _level) {
                    ItemEntity entityToSpawn = new ItemEntity(_level, this.getX(), (this.getY() + 0.5), this.getZ(), new ItemStack(CAItems.CRIMSON_TREATY.get()));
                    entityToSpawn.setPickUpDelay(10);
                    entityToSpawn.setUnlimitedLifetime();
                    _level.addFreshEntity(entityToSpawn);
                }
            }
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
        SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
        if (this.getAttributes().hasAttribute(CAAttributes.SANITY_RATE.get()))
            this.getAttribute(CAAttributes.SANITY_RATE.get()).setBaseValue(7);
        if (this.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get()))
            this.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).setBaseValue(20);
        return retval;
    }

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Dataability", this.entityData.get(DATA_ability));
        compound.putInt("Dataskillp", this.entityData.get(DATA_skillp));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Dataability"))
            this.entityData.set(DATA_ability, compound.getInt("Dataability"));
        if (compound.contains("Dataskillp"))
            this.entityData.set(DATA_skillp, compound.getInt("Dataskillp"));
	}

    @Override
    public void awardKillScore(Entity entity, int score, DamageSource damageSource) {
        super.awardKillScore(entity, score, damageSource);
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        double ablty;
        ablty = (Entity) this instanceof OceanizedBruteEntity _datEntI ? _datEntI.getEntityData().get(DATA_ability) : 0;
        if (ablty < 7) {
            if (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
                this.getAttribute(Attributes.ATTACK_DAMAGE)
                        .setBaseValue(((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : 0) + 4));
            if ((Entity) this instanceof OceanizedBruteEntity _datEntSetI)
                _datEntSetI.getEntityData().set(DATA_ability, (int) (ablty + 1));
            if (world instanceof ServerLevel _level)
                _level.sendParticles(ParticleTypes.LAVA, x, (y + 0.75), z, 32, 0.75, 0.75, 0.75, 0.1);
        }
        if (((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) < ((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1)) {
            if ((Entity) this instanceof LivingEntity _entity)
                _entity.setHealth((float) (((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) + ((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.1));
            if (world instanceof ServerLevel _level)
                _level.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, (y + 0.75), z, 32, 0.75, 0.75, 0.75, 0.1);
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        this.refreshDimensions();
    }

    @Override
    public EntityDimensions getDimensions(Pose p_33597_) {
        return super.getDimensions(p_33597_).scale((float) 1);
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
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.25);
        builder = builder.add(Attributes.MAX_HEALTH, 115);
        builder = builder.add(Attributes.ARMOR, 5);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 16);
        builder = builder.add(Attributes.FOLLOW_RANGE, 24);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.45);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

            ) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_brute.move"));
            }
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_brute.die"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_brute.idle"));
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
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_brute.attack"));
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
        data.add(new AnimationController<>(this, "movement", 1, this::movementPredicate));
        data.add(new AnimationController<>(this, "attacking", 1, this::attackingPredicate));
        data.add(new AnimationController<>(this, "procedure", 1, this::procedurePredicate));
    }


    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}
