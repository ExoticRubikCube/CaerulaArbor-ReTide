package com.apocalypse.caerulaarbor.entity.wither;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CAAttributes;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAItems;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class OceanizedWitherEntity extends AbstractOceanizedWitherEntity implements RangedAttackMob {
    public static final EntityDataAccessor<Integer> DATA_spawn = SynchedEntityData.defineId(OceanizedWitherEntity.class, EntityDataSerializers.INT);

    public OceanizedWitherEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(CAEntities.OCEANIZED_WITHER.get(), world);
    }

    public OceanizedWitherEntity(EntityType<OceanizedWitherEntity> type, Level world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_spawn, 0);
    }

    @Override
    protected String getDefaultTexture() {
        return "oceanized_wither_inv";
    }

    @Override
    protected int getInitialSkillp() {
        return 200;
    }

    @Override
    protected int getInitialDuration() {
        return 100;
    }

    @Override
    protected int getDeathDuration() {
        return 30;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && isWitherDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isWitherDurative();
            }
        });
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, IronGolem.class, false, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Villager.class, false, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Animal.class, false, false));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1, 20) {
            @Override
            protected Vec3 getPosition() {
                RandomSource random = OceanizedWitherEntity.this.getRandom();
                double dir_x = OceanizedWitherEntity.this.getX() + ((random.nextFloat() * 2 - 1) * 16);
                double dir_y = OceanizedWitherEntity.this.getY() + ((random.nextFloat() * 2 - 1) * 16);
                double dir_z = OceanizedWitherEntity.this.getZ() + ((random.nextFloat() * 2 - 1) * 16);
                return new Vec3(dir_x, dir_y, dir_z);
            }

            @Override
            public boolean canUse() {
                return super.canUse() && isWitherDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isWitherDurative();
            }

        });
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && isWitherDurative();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isWitherDurative();
            }
        });
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25, 30, 12f) {
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
            ((OceanizedWitherEntity) rangedAttackMob).entityData.set(SHOOT, false);
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
                    ((OceanizedWitherEntity) rangedAttackMob).entityData.set(SHOOT, false);
                    return;
                }
                ((OceanizedWitherEntity) rangedAttackMob).entityData.set(SHOOT, true);
                float f = (float) Math.sqrt(d0) / this.attackRadius;
                float f1 = Mth.clamp(f, 0.1F, 1.0F);
                this.rangedAttackMob.performRangedAttack(this.target, f1);
                this.attackTime = Mth.floor(f * (float) (this.attackIntervalMax - this.attackIntervalMin) + (float) this.attackIntervalMin);
            } else if (this.attackTime < 0) {
                this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(d0) / (double) this.attackRadius, this.attackIntervalMin, this.attackIntervalMax));
            } else
                ((OceanizedWitherEntity) rangedAttackMob).entityData.set(SHOOT, false);
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
        SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
        if (this.getAttributes().hasAttribute(CAAttributes.SANITY_RATE.get()))
            this.getAttribute(CAAttributes.SANITY_RATE.get()).setBaseValue(10);
        if (this.getAttributes().hasAttribute(CAAttributes.SANITY_MODIFIER.get()))
            this.getAttribute(CAAttributes.SANITY_MODIFIER.get()).setBaseValue(0.01);
        if (this.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get()))
            this.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).setBaseValue(65);
        if (this.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE.get()))
            this.getAttribute(CAAttributes.GENERAL_DEFENSE.get()).setBaseValue(5);
        if (!this.level().isClientSide())
            this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 100, 9, false, false));
        this.setHealth(1);
        setDeltaMovement(new Vec3(0, (-0.75), 0));
        if ((Entity) this instanceof OceanizedWitherEntity _datEntSetI)
            _datEntSetI.getEntityData().set(DATA_duration, 100);
        return retval;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Dataspawn", this.entityData.get(DATA_spawn));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Dataspawn"))
            this.entityData.set(DATA_spawn, compound.getInt("Dataspawn"));
    }

    @Override
    public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
        ItemStack itemStack = sourceentity.getItemInHand(hand);
        super.mobInteract(sourceentity, hand);
        if (itemStack.getItem() != CAItems.OCEANIZED_WITHER_SPAWNEGG.get()) {
            return InteractionResult.PASS;
        }

        this.entityData.set(DATA_duration, 0);
        this.entityData.set(DATA_spawn, 100);
        this.setTexture("oceanized_wither");
        this.setHealth(this.getMaxHealth());
        this.removeEffect(CAMobEffects.INVULNERABLE.get());
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void tickSubclassBaseTick(LevelAccessor world, double x, double y, double z) {
        double spawn = this.entityData.get(DATA_spawn);
        double skillp = this.entityData.get(DATA_skillp);

        if (spawn < 100) {
            this.setHealth((float) (this.getMaxHealth() * (spawn + 1) * 0.01));
            this.entityData.set(DATA_spawn, (int) (spawn + 1));
            if (spawn == 19) {
                this.setTexture("oceanized_wither_inv");
            } else if (spawn == 39) {
                this.setTexture("oceanized_wither");
            } else if (spawn == 49) {
                this.setTexture("oceanized_wither_inv");
            } else if (spawn == 59) {
                this.setTexture("oceanized_wither");
            } else if (spawn == 69) {
                this.setTexture("oceanized_wither_inv");
            } else if (spawn == 79) {
                this.setTexture("oceanized_wither");
            } else if (spawn == 89) {
                this.setTexture("oceanized_wither_inv");
            } else if (spawn == 99) {
                this.setTexture("oceanized_wither");
                if (world instanceof Level level && !level.isClientSide()) {
                    level.explode(null, x, y, z, 16, Level.ExplosionInteraction.MOB);
                }
                if (world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "ocean_wither_spawn")), SoundSource.HOSTILE, 4, 1);
                }
                if (world instanceof ServerLevel level) {
                    level.sendParticles(ParticleTypes.EXPLOSION, x, y, z, 4, 3, 3, 3, 1);
                }
            }
        }

        Entity enemy = this.getTarget();
        if (skillp > 0) {
            this.entityData.set(DATA_skillp, (int) (skillp - 1));
        } else if (enemy != null && enemy.isAlive()) {
            this.entityData.set(DATA_duration, 40);
            this.entityData.set(DATA_skillp, 300);
            if (!this.level().isClientSide()) {
                this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 20, 0, false, false));
            }
            this.setAnimation("animation.oceanzied_wither.skill");
            CaerulaArborMod.queueServerWork(4, () -> this.rimedWitherShoot(world));
            CaerulaArborMod.queueServerWork(13, () -> this.gatlinWitherShoot(world, x, y, z));
            CaerulaArborMod.queueServerWork(12, () -> this.gatlinWitherShoot(world, x, y, z));
        }

        if (this.tickCount % 10 == 0 && WorldUtils.canGrief(world)) {
            boolean brokeAnyBlock = false;
            double dx = -1;
            for (int xIndex = 0; xIndex < 3; xIndex++) {
                double dz = -1;
                for (int zIndex = 0; zIndex < 3; zIndex++) {
                    double dy = 0;
                    for (int yIndex = 0; yIndex < 4; yIndex++) {
                        BlockState block = world.getBlockState(BlockPos.containing(x + dx, y + dy, z + dz));
                        if (!block.is(BlockTags.create(new ResourceLocation("minecraft:wither_immnue")))) {
                            double hardness = block.getDestroySpeed(world, BlockPos.containing(0, 0, 0));
                            if (hardness <= 7.5 && hardness >= 0 && world.getBlockFloorHeight(BlockPos.containing(x + dx, y + dy, z + dz)) > 0) {
                                BlockPos blockPos = BlockPos.containing(x + dx, y + dy, z + dz);
                                Block.dropResources(world.getBlockState(blockPos), world, BlockPos.containing(x, y, z), null);
                                world.destroyBlock(blockPos, false);
                                if (world instanceof Level level) {
                                    level.updateNeighborsAt(blockPos, level.getBlockState(blockPos).getBlock());
                                }
                                brokeAnyBlock = true;
                            }
                        }
                        dy = dy + 1;
                    }
                    dz = dz + 1;
                }
                dx = dx + 1;
            }
            if (brokeAnyBlock && world instanceof Level level) {
                if (!level.isClientSide()) {
                    level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.wither.break_block")), SoundSource.NEUTRAL, 1, 1);
                } else {
                    level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.wither.break_block")), SoundSource.NEUTRAL, 1, 1, false);
                }
            }
        }
    }

    @Override
    protected boolean shouldEnterShelledState() {
        return this.getHealth() < this.getMaxHealth() * 0.5 && this.entityData.get(DATA_spawn) > 99;
    }

    @Override
    protected String getShelledTexture() {
        return "oceanized_wither_anger";
    }

    @Override
    public void performRangedAttack(LivingEntity target, float flval) {
        // TODO: Revisit this legacy system call when the pre-shot wither projectile path is cleaned up.
        WitherShootPreEntity.shoot(this, target);
    }

    private void rimedWitherShoot(LevelAccessor world) {
        InteractionResult start = InteractionResult.PASS;
        new Object() {
            void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                double angle = getYRot() + 30 * timedloopiterator;
                shootWitherSkull(world, OceanizedWitherEntity.this, 0.1, Math.cos(Math.toRadians(angle)), Mth.nextDouble(RandomSource.create(), -0.25, 0.25), Math.sin(Math.toRadians(angle)), 5, 0.35, getX(),
                        getY() + Mth.nextDouble(RandomSource.create(), 0.25, 3), getZ());
                final int tick2 = ticks;
                CaerulaArborMod.queueServerWork(tick2, () -> {
                    if (timedlooptotal > timedloopiterator + 1) {
                        timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                    }
                });
            }
        }.timedLoop(0, 12, 1);
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.35);
        builder = builder.add(Attributes.MAX_HEALTH, 550);
        builder = builder.add(Attributes.ARMOR, 8);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 12);
        builder = builder.add(Attributes.FOLLOW_RANGE, 56);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
        builder = builder.add(Attributes.FLYING_SPEED, 0.35);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanzied_wither.die"));
            }
            if (this.isAggressive() && event.isMoving()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanzied_wither.aggresive"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanzied_wither.idle"));
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
    public void remove(RemovalReason pReason) {
        if (this.level().getDifficulty() != Difficulty.PEACEFUL && pReason == RemovalReason.DISCARDED) {
            this.hurt(
                    new DamageSource(
                            this.level().registryAccess().
                                    registryOrThrow(Registries.DAMAGE_TYPE).
                                    getHolderOrThrow(
                                            ResourceKey.create(
                                                    Registries.DAMAGE_TYPE,
                                                    new ResourceLocation(CaerulaArborMod.MODID, "oceankiller_damage")
                                            )
                                    )
                    ),
                    20
            );
            return;
        }
        super.remove(pReason);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
        data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
    }

    public void gatlinWitherShoot(LevelAccessor world, double x, double y, double z) {
        double vx = 0;
        double vy = 0;
        double vz = 0;
        new Object() {
            void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                double dist;
                double rng;
                dist = Mth.nextDouble(RandomSource.create(), 0, 16);
                rng = Mth.nextDouble(RandomSource.create(), 0, 6.283);
                shootWitherSkull(world, OceanizedWitherEntity.this, 0.15, 0, -1, 0, 6, 0.35, x + dist * Math.cos(rng), y + Mth.nextDouble(RandomSource.create(), 4, 8), z + dist * Math.sin(rng));
                final int tick2 = ticks;
                CaerulaArborMod.queueServerWork(tick2, () -> {
                    if (timedlooptotal > timedloopiterator + 1) {
                        timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                    }
                });
            }
        }.timedLoop(0, 20, 1);
    }


    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }
}
