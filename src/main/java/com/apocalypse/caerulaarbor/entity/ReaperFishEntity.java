package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.api.event.SanityEvent;
import com.apocalypse.caerulaarbor.capability.map.MapVariables;
import com.apocalypse.caerulaarbor.capability.sanity.SIHelper;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CAAttributes;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CASounds;
import com.apocalypse.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Comparator;
import java.util.List;

public class ReaperFishEntity extends SeaMonster {
	public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(ReaperFishEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(ReaperFishEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_CHARGE_TICK = SynchedEntityData.defineId(ReaperFishEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Boolean> DATA_IS_CHARGING = SynchedEntityData.defineId(ReaperFishEntity.class, EntityDataSerializers.BOOLEAN);
	private boolean swinging;
	private long lastSwing;
	public String animationprocedure = "empty";

	public ReaperFishEntity(Level world) {
		this(CAEntities.REAPER_FISH.get(), world);
	}

	public ReaperFishEntity(EntityType<ReaperFishEntity> type, Level world) {
		super(type, world);
		xpReward = 8;
		setNoAi(false);
		setMaxUpStep(1.5f);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_SHOOT, false);
		this.entityData.define(DATA_ANIMATION, "undefined");
		this.entityData.define(DATA_CHARGE_TICK, 0);
		this.entityData.define(DATA_IS_CHARGING, false);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 3.5, true) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 4;
			}
		});
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true, false));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, SnowGolem.class, true, false));
		this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1));
		this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(7, new FloatGoal(this));
	}

    @Override
	public SoundEvent getAmbientSound() {
		return SoundEvents.PHANTOM_AMBIENT;
	}

	@Override
	public void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(SoundEvents.SPIDER_STEP, 0.15f, 1);
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return CASounds.SEABORN_GENERIC_HIT.get();
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.PHANTOM_DEATH;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        double dx;
        double dy;
        double dz;
        double limithard;
        double hardness;
        boolean once;
        limithard = -1;
        if (MapVariables.get(world).strategy_migration >= 4) {
            limithard = 5;
        } else if (MapVariables.get(world).strategy_migration >= 2) {
            limithard = 3.5;
        }
        if (!world.isClientSide()) {
            if (WorldUtils.canGrief(world) && limithard > 0) {
                if (Math.random() < 0.5) {
                    once = false;
                    dx = -1;
                    for (int index0 = 0; index0 < 3; index0++) {
                        dz = -1;
                        for (int index1 = 0; index1 < 3; index1++) {
                            dy = 1;
                            for (int index2 = 0; index2 < 3; index2++) {
                                hardness = (world.getBlockState(BlockPos.containing(x + dx, y + dy, z + dz))).getDestroySpeed(world, BlockPos.containing(0, 0, 0));
                                if (hardness <= limithard && hardness >= 0 && world.getBlockFloorHeight(BlockPos.containing(x + dx, y + dy, z + dz)) > 0) {
                                    if (Math.random() < 0.75) {
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
        if (source.is(DamageTypes.DROWN))
			return false;
		return super.hurt(source, amount);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("ChargeTick", this.entityData.get(DATA_CHARGE_TICK));
		compound.putBoolean("IsCharging", this.entityData.get(DATA_IS_CHARGING));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("ChargeTick")) {
		    this.entityData.set(DATA_CHARGE_TICK, compound.getInt("ChargeTick"));
		}
		if (compound.contains("IsCharging")) {
		    this.entityData.set(DATA_IS_CHARGING, compound.getBoolean("IsCharging"));
		}
	}

	@Override
	public void baseTick() {
		super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        double angle;
        double cTick;
        boolean isCharging;
        if (this.getTarget() != null && tickCount % 20 == 0) {
            if (this.isAggressive() && this.isAlive()) {
                for (int index0 = 0; index0 < 120; index0++) {
                    angle = Mth.nextDouble(RandomSource.create(), 0, 6.283);
                    if (world instanceof ServerLevel level)
                        level.sendParticles(ParticleTypes.ELECTRIC_SPARK, (x + 5 * Math.sin(angle)), y, (z + 4 * Math.cos(angle)), 8, 0.1, 0.1, 0.1, 0.2);
                }
				final Vec3 center = new Vec3(x, y, z);
				List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(10 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
				for (Entity entityiterator : entfound) {
					if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring")))) {
						if (!(entityiterator == this.getTarget())) {
							continue;
						}
					}
					if (distanceTo(entityiterator) < 5) {
						if (!(entityiterator == this)) {
							if (entityiterator instanceof LivingEntity target) {
								SIHelper.causeSanityInjury(target,
										this,
										(this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 12,
										SanityEvent.Hurt.Type.ENTITY);
							}
						}
					}
				}
                ((Entity) this).hurt(this.damageSources().dryOut(),
                        (float) ((this.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? this.getAttribute(Attributes.MAX_HEALTH).getValue() : 0) * 0.01));
            }
        }
        isCharging = (Entity) this instanceof ReaperFishEntity datEntL17 && datEntL17.getEntityData().get(DATA_IS_CHARGING);
        cTick = (Entity) this instanceof ReaperFishEntity datEntI ? datEntI.getEntityData().get(DATA_CHARGE_TICK) : 0;
        if (cTick > 0) {
            if ((Entity) this instanceof ReaperFishEntity datEntSetI)
                datEntSetI.getEntityData().set(DATA_CHARGE_TICK, (int) (cTick - 1));
        }
        if (this.isAggressive()) {
            if (!isCharging && cTick <= 0) {
                if ((Entity) this instanceof ReaperFishEntity datEntSetI)
                    datEntSetI.getEntityData().set(DATA_CHARGE_TICK, 200);
                if ((Entity) this instanceof ReaperFishEntity datEntSetL)
                    datEntSetL.getEntityData().set(DATA_IS_CHARGING, true);
                if (!world.isClientSide()) {
                    if (world instanceof Level level) {
                            level.playSound(null, BlockPos.containing(x, y, z), CASounds.REAPER_ANGRY.get(), SoundSource.HOSTILE, (float) 1.5, 1);
                    }
                }
            }
        } else {
            if ((Entity) this instanceof ReaperFishEntity datEntSetL)
                datEntSetL.getEntityData().set(DATA_IS_CHARGING, false);
        }
        this.refreshDimensions();
	}

	public static void registerSpawnPlacements() {
		SpawnPlacements.register(CAEntities.REAPER_FISH.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (entityType, world, reason, pos, random) -> {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			return WorldUtils.canRareSeabornSpawn(world, x, y, z);
		});
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.25);
		builder = builder.add(Attributes.MAX_HEALTH, 80);
		builder = builder.add(Attributes.ARMOR, 10);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 7);
		builder = builder.add(Attributes.FOLLOW_RANGE, 32);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.75);
		builder = builder.add(CAAttributes.SANITY_RATE.get(), 6);
		builder = builder.add(CAAttributes.MAGIC_RESISTANCE.get(), 40);
		return builder;
	}

	private PlayState movementPredicate(AnimationState<?> event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

					&& !this.isAggressive() && !this.isSprinting()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.reaperfish.move"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.reaperfish.die"));
			}
			if (this.isSprinting()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.reaperfish.sprint"));
			}
			if (this.isAggressive() && event.isMoving()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.reaperfish.sprint"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.reaperfish.idle"));
		}
		return PlayState.STOP;
	}

	private PlayState attackingPredicate(AnimationState<?> event) {
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
			this.swinging = true;
			this.lastSwing = level().getGameTime();
		}
		if (this.swinging && this.lastSwing + 15L <= level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.reaperfish.attack"));
		}
		return PlayState.CONTINUE;
	}

	String prevAnim = "empty";

	private PlayState procedurePredicate(AnimationState<?> event) {
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
			this.remove(ReaperFishEntity.RemovalReason.KILLED);
			this.dropExperience();
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
		data.add(new AnimationController<>(this, "movement", 1, this::movementPredicate));
		data.add(new AnimationController<>(this, "attacking", 1, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 1, this::procedurePredicate));
	}


	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}
}

