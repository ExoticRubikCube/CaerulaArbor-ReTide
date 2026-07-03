package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.RavagerMountRider;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.entity.bullets.ThrowablePotionEntity;
import com.apocalypse.caerulaarbor.init.CAAttributes;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.init.CAPotions;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
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
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class OceanziedWitchEntity extends SeaMonster implements RangedAttackMob, RavagerMountRider {
    public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(OceanziedWitchEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(OceanziedWitchEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_skillp = SynchedEntityData.defineId(OceanziedWitchEntity.class, EntityDataSerializers.INT);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";

    public OceanziedWitchEntity(Level world) {
        this(CAEntities.OCEANIZED_WITCH.get(), world);
    }

    public OceanziedWitchEntity(EntityType<OceanziedWitchEntity> type, Level world) {
        super(type, world);
        xpReward = 6;
        setNoAi(false);
        setMaxUpStep(1f);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SHOOT, false);
        this.entityData.define(ANIMATION, "undefined");
        this.entityData.define(DATA_skillp, 200);
    }


    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, IronGolem.class, true, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, SnowGolem.class, true, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Villager.class, true, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Illusioner.class, true, true));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Pillager.class, true, true));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, Vindicator.class, true, true));
        this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, Witch.class, true, true));
        this.targetSelector.addGoal(9, new NearestAttackableTargetGoal<>(this, Piglin.class, true, true));
        this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, PiglinBrute.class, true, true));
        this.targetSelector.addGoal(11, new NearestAttackableTargetGoal<>(this, ZombifiedPiglin.class, true, true));
        this.targetSelector.addGoal(12, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, true, target -> EntityUtils.isOceanizedPlayerNearby(this.level(), this.getX(), this.getY(), this.getZ())));
        this.goalSelector.addGoal(14, new RandomStrollGoal(this, 1));
        this.goalSelector.addGoal(15, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(16, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25, 50, 4f) {
            @Override
            public boolean canContinueToUse() {
                return this.canUse();
            }
        });
    }

    public class RangedAttackGoal extends Goal {
        private final Mob mob;
        private final RangedAttackMob rangedAttackMob;
        @Nullable
        private LivingEntity target;
        private int attackTime = -1;
        private final double speedModifier;
        private int seeTime;
        private final int attackIntervalMin;
        private final int attackIntervalMax;
        private final float attackRadius;
        private final float attackRadiusSqr;

        public RangedAttackGoal(RangedAttackMob p_25768_, double p_25769_, int p_25770_, float p_25771_) {
            this(p_25768_, p_25769_, p_25770_, p_25770_, p_25771_);
        }

        public RangedAttackGoal(RangedAttackMob p_25773_, double p_25774_, int p_25775_, int p_25776_, float p_25777_) {
            if (!(p_25773_ instanceof LivingEntity)) {
                throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
            } else {
                this.rangedAttackMob = p_25773_;
                this.mob = (Mob) p_25773_;
                this.speedModifier = p_25774_;
                this.attackIntervalMin = p_25775_;
                this.attackIntervalMax = p_25776_;
                this.attackRadius = p_25777_;
                this.attackRadiusSqr = p_25777_ * p_25777_;
                this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
            }
        }

        public boolean canUse() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                this.target = livingentity;
                return true;
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return this.canUse() || this.target.isAlive() && !this.mob.getNavigation().isDone();
        }

        public void stop() {
            this.target = null;
            this.seeTime = 0;
            this.attackTime = -1;
            ((OceanziedWitchEntity) rangedAttackMob).entityData.set(SHOOT, false);
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            double d0 = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
            boolean flag = this.mob.getSensing().hasLineOfSight(this.target);
            if (flag) {
                ++this.seeTime;
            } else {
                this.seeTime = 0;
            }
            if (!(d0 > (double) this.attackRadiusSqr) && this.seeTime >= 5) {
                this.mob.getNavigation().stop();
            } else {
                this.mob.getNavigation().moveTo(this.target, this.speedModifier);
            }
            this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
            if (--this.attackTime == 0) {
                if (!flag) {
                    ((OceanziedWitchEntity) rangedAttackMob).entityData.set(SHOOT, false);
                    return;
                }
                ((OceanziedWitchEntity) rangedAttackMob).entityData.set(SHOOT, true);
                float f = (float) Math.sqrt(d0) / this.attackRadius;
                float f1 = Mth.clamp(f, 0.1F, 1.0F);
                this.rangedAttackMob.performRangedAttack(this.target, f1);
                this.attackTime = Mth.floor(f * (float) (this.attackIntervalMax - this.attackIntervalMin) + (float) this.attackIntervalMin);
            } else if (this.attackTime < 0) {
                this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(d0) / (double) this.attackRadius, this.attackIntervalMin, this.attackIntervalMax));
            } else
                ((OceanziedWitchEntity) rangedAttackMob).entityData.set(SHOOT, false);
        }
    }

    @Override
    public SoundEvent getAmbientSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "witch_ambient"));
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "witch_hurt"));
    }

    @Override
    public SoundEvent getDeathSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "witch_die"));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        LevelAccessor world = this.level();
        if (!this.hasEffect(CAMobEffects.COOLDOWN_SINAL.get())) {
            {
                final Vec3 _center = new Vec3(this.getX(), this.getY(), this.getZ());
                List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(16 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                for (Entity entityiterator : _entfound) {
                    if (Math.random() < 0.33 && entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                        double xx = entityiterator.getX();
                        double yy = entityiterator.getY() + entityiterator.getBbHeight();
                        double zz = entityiterator.getZ();
                        double potion;
                        if (world instanceof Level _level) {
                            _level.playSound(null, BlockPos.containing(xx, yy, zz), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.witch.throw")), SoundSource.HOSTILE, 1, 1);
                        }
                        potion = Mth.nextInt(RandomSource.create(), 0, 4);
                        if (potion == 0) {
                            if (entityiterator instanceof LivingEntity _entity1 && !_entity1.level().isClientSide())
                                _entity1.addEffect(new MobEffectInstance(CAMobEffects.SANITY_HEAL.get(), 1, 2));
                            if (world instanceof ServerLevel projectileLevel) {
                                Projectile _entityToSpawn = new Object() {
                                    public Projectile getPotion(Level level, Entity shooter) {
                                        ThrownPotion entityToSpawn = new ThrownPotion(EntityType.POTION, level);
                                        entityToSpawn.setItem(PotionUtils.setPotion(Items.SPLASH_POTION.getDefaultInstance(), CAPotions.SANITY_CURE.get()));
                                        entityToSpawn.setOwner(shooter);
                                        return entityToSpawn;
                                    }
                                }.getPotion(projectileLevel, (Entity) this);
                                _entityToSpawn.setPos(xx, yy, zz);
                                _entityToSpawn.shoot(0, (-1), 0, 1, 0);
                                projectileLevel.addFreshEntity(_entityToSpawn);
                            }
                        } else if (potion == 1) {
                            if (entityiterator instanceof LivingEntity _entity1 && !_entity1.level().isClientSide())
                                _entity1.addEffect(new MobEffectInstance(MobEffects.HEAL, 1, 2));
                            if (world instanceof ServerLevel projectileLevel) {
                                Projectile _entityToSpawn = new Object() {
                                    public Projectile getPotion(Level level, Entity shooter) {
                                        ThrownPotion entityToSpawn = new ThrownPotion(EntityType.POTION, level);
                                        entityToSpawn.setItem(PotionUtils.setPotion(Items.SPLASH_POTION.getDefaultInstance(), Potions.HEALING));
                                        entityToSpawn.setOwner(shooter);
                                        return entityToSpawn;
                                    }
                                }.getPotion(projectileLevel, (Entity) this);
                                _entityToSpawn.setPos(xx, yy, zz);
                                _entityToSpawn.shoot(0, (-1), 0, 1, 0);
                                projectileLevel.addFreshEntity(_entityToSpawn);
                            }
                        } else if (potion == 2) {
                            if (entityiterator instanceof LivingEntity _entity1 && !_entity1.level().isClientSide())
                                _entity1.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 2));
                            if (world instanceof ServerLevel projectileLevel) {
                                Projectile _entityToSpawn = new Object() {
                                    public Projectile getPotion(Level level, Entity shooter) {
                                        ThrownPotion entityToSpawn = new ThrownPotion(EntityType.POTION, level);
                                        entityToSpawn.setItem(PotionUtils.setPotion(Items.LINGERING_POTION.getDefaultInstance(), Potions.REGENERATION));
                                        entityToSpawn.setOwner(shooter);
                                        return entityToSpawn;
                                    }
                                }.getPotion(projectileLevel, (Entity) this);
                                _entityToSpawn.setPos(xx, yy, zz);
                                _entityToSpawn.shoot(0, (-1), 0, 1, 0);
                                projectileLevel.addFreshEntity(_entityToSpawn);
                            }
                        } else if (potion == 3) {
                            if (entityiterator instanceof LivingEntity _entity1 && !_entity1.level().isClientSide())
                                _entity1.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 0));
                            if (world instanceof ServerLevel projectileLevel) {
                                Projectile _entityToSpawn = new Object() {
                                    public Projectile getPotion(Level level, Entity shooter) {
                                        ThrownPotion entityToSpawn = new ThrownPotion(EntityType.POTION, level);
                                        entityToSpawn.setItem(PotionUtils.setPotion(Items.SPLASH_POTION.getDefaultInstance(), Potions.FIRE_RESISTANCE));
                                        entityToSpawn.setOwner(shooter);
                                        return entityToSpawn;
                                    }
                                }.getPotion(projectileLevel, (Entity) this);
                                _entityToSpawn.setPos(xx, yy, zz);
                                _entityToSpawn.shoot(0, (-1), 0, 1, 0);
                                projectileLevel.addFreshEntity(_entityToSpawn);
                            }
                        } else if (potion == 4) {
                            if (entityiterator instanceof LivingEntity _entity1 && !_entity1.level().isClientSide())
                                _entity1.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600, 1));
                            if (world instanceof ServerLevel projectileLevel) {
                                Projectile _entityToSpawn = new Object() {
                                    public Projectile getPotion(Level level, Entity shooter) {
                                        ThrownPotion entityToSpawn = new ThrownPotion(EntityType.POTION, level);
                                        entityToSpawn.setItem(PotionUtils.setPotion(Items.SPLASH_POTION.getDefaultInstance(), Potions.STRENGTH));
                                        entityToSpawn.setOwner(shooter);
                                        return entityToSpawn;
                                    }
                                }.getPotion(projectileLevel, (Entity) this);
                                _entityToSpawn.setPos(xx, yy, zz);
                                _entityToSpawn.shoot(0, (-1), 0, 1, 0);
                                projectileLevel.addFreshEntity(_entityToSpawn);
                            }
                        }
                        if (entityiterator instanceof LivingEntity _entity)
                            _entity.removeEffect(MobEffects.POISON);
                        if (entityiterator instanceof LivingEntity _entity)
                            _entity.removeEffect(MobEffects.WEAKNESS);
                        if (entityiterator instanceof LivingEntity _entity)
                            _entity.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
                    }
                }
            }
            if (!this.level().isClientSide())
                this.addEffect(new MobEffectInstance(CAMobEffects.COOLDOWN_SINAL.get(), 100, 0, false, false));
        }
        if (source.is(DamageTypes.DROWN))
            return false;
        return super.hurt(source, amount);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
        SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
        if (this.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get()))
            this.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).setBaseValue(90);
        return retval;
    }

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Dataskillp", this.entityData.get(DATA_skillp));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Dataskillp"))
            this.entityData.set(DATA_skillp, compound.getInt("Dataskillp"));
	}

    @Override
    public void baseTick() {
        super.baseTick();
        double sklp;
        Entity enemy;
        if (this.isAlive()) {
            sklp = (Entity) this instanceof OceanziedWitchEntity _datEntI ? _datEntI.getEntityData().get(DATA_skillp) : 0;
            if (sklp <= 0) {
                enemy = (Entity) this instanceof Mob _mobEnt ? _mobEnt.getTarget() : null;
                if (!(enemy == null) && enemy.isAlive() && (enemy != null ? distanceTo(enemy) : -1) <= 9) {
                    if (this instanceof OceanziedWitchEntity) {
                        this.setAnimation("animation.oceanized_witch.throw");
                    }
                    if ((Entity) this instanceof OceanziedWitchEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_skillp, 250);
                    if (!this.level().isClientSide())
                        this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 60, 0, false, false));
                    CaerulaArborMod.queueServerWork(14, this::shootRandomPotion);
                    CaerulaArborMod.queueServerWork(19, this::shootRandomPotion);
                    CaerulaArborMod.queueServerWork(23, this::shootRandomPotion);
                    CaerulaArborMod.queueServerWork(28, this::shootRandomPotion);
                    CaerulaArborMod.queueServerWork(29, this::shootRandomPotion);
                    CaerulaArborMod.queueServerWork(34, this::shootRandomPotion);
                    CaerulaArborMod.queueServerWork(36, this::shootRandomPotion);
                    CaerulaArborMod.queueServerWork(41, this::shootRandomPotion);
                }
            } else {
                if ((Entity) this instanceof OceanziedWitchEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_skillp, (int) (sklp - 1));
            }
            this.removeEffect(MobEffects.POISON);
            this.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            this.removeEffect(MobEffects.WEAKNESS);
        }
        this.refreshDimensions();
    }

    @Override
    public EntityDimensions getDimensions(Pose p_33597_) {
        return super.getDimensions(p_33597_).scale((float) 1);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float flval) {
        ThrowablePotionEntity.shoot(this, target, (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttributeValue(Attributes.ATTACK_DAMAGE) : 0) * (2.3 / 4.0));
    }

    public void shootRandomPotion() {
        if (!this.isAlive()) {
            return;
        }

        LivingEntity target = this.getTarget();
        if (target != null) {
            this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(target.getX(), target.getY() + target.getBbHeight() * 0.9, target.getZ()));
        }

        int potionIndex = Mth.nextInt(this.getRandom(), 0, 4);
        if (potionIndex == 0) {
            this.throwSplashPotion(Potions.HARMING);
        } else if (potionIndex == 1) {
            this.throwSplashPotion(CAPotions.INST_SANITY.get());
        } else if (potionIndex == 2) {
            this.throwSplashPotion(Potions.POISON);
        } else if (potionIndex == 3) {
            this.throwSplashPotion(Potions.LONG_WEAKNESS);
        } else {
            this.throwSplashPotion(Potions.SLOWNESS);
        }

        if (this.getRandom().nextBoolean()) {
            this.throwSplashPotion(Potions.HARMING);
        } else {
            this.throwSplashPotion(CAPotions.INST_SANITY.get());
        }
    }

    private void throwSplashPotion(Potion potion) {
        Level projectileLevel = this.level();
        if (projectileLevel.isClientSide()) {
            return;
        }

        ThrownPotion thrownPotion = new ThrownPotion(EntityType.POTION, projectileLevel);
        thrownPotion.setItem(PotionUtils.setPotion(Items.SPLASH_POTION.getDefaultInstance(), potion));
        thrownPotion.setOwner(this);
        thrownPotion.setPos(this.getX(), this.getEyeY() - 0.1, this.getZ());
        thrownPotion.shoot(this.getLookAngle().x, this.getLookAngle().y, this.getLookAngle().z, 1, 2);
        projectileLevel.addFreshEntity(thrownPotion);
    }


    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.16);
        builder = builder.add(Attributes.MAX_HEALTH, 60);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 4);
        builder = builder.add(Attributes.FOLLOW_RANGE, 13);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

            ) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_witch.move"));
            }
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_witch.die"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_witch.idle"));
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
        if (this.swinging && this.lastSwing + 25L <= level().getGameTime()) {
            this.swinging = false;
        }
        if ((this.swinging || this.entityData.get(SHOOT)) && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_witch.attack"));
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
        data.add(new AnimationController<>(this, "movement", 1, this::movementPredicate));
        data.add(new AnimationController<>(this, "attacking", 1, this::attackingPredicate));
        data.add(new AnimationController<>(this, "procedure", 1, this::procedurePredicate));
    }


    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}
