package com.susen36.caerulaarbor.entity;


import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CABlocks;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CASounds;
import com.susen36.caerulaarbor.util.WorldUtils;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

public class ApostleProkaryoteEntity extends SeaMonster {
	public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(ApostleProkaryoteEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(ApostleProkaryoteEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Boolean> DATA_SHELLED = SynchedEntityData.defineId(ApostleProkaryoteEntity.class, EntityDataSerializers.BOOLEAN);
	private boolean swinging;
	private long lastSwing;
	public String animationprocedure = "empty";
	protected final WaterBoundPathNavigation waterNavigation;
	protected final GroundPathNavigation groundNavigation;
	private final MoveControl landControl;
	private final SeabornSwimControl swimControl;
	String prevAnim = "empty";

	public ApostleProkaryoteEntity(Level world) {
		this(CAEntities.APOSTLE_PROKARYOTE.get(), world);
	}

	public ApostleProkaryoteEntity(EntityType<ApostleProkaryoteEntity> type, Level world) {
		super(type, world);
		xpReward = 8;
		setNoAi(false);
		this.setPathfindingMalus(PathType.WATER, 0);
		this.landControl = new MoveControl(this);
		this.swimControl = new SeabornSwimControl(this);
		this.moveControl = this.swimControl;
		this.waterNavigation = new WaterBoundPathNavigation(this, world);
		this.groundNavigation = new GroundPathNavigation(this, world);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_SHOOT, false);
		builder.define(DATA_ANIMATION, "undefined");
		builder.define(DATA_SHELLED, false);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1, false));
		this.goalSelector.addGoal(9, new RandomSwimmingGoal(this, 1, 40));
		this.goalSelector.addGoal(10, new RandomStrollGoal(this, 1.0));
		this.goalSelector.addGoal(12, new RandomLookAroundGoal(this));
	}

	@Override
	public void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(SoundEvents.DOLPHIN_SWIM, 0.15f, 1);
	}

	@Override
	public SoundEvent getHurtSound(DamageSource damageSource) {
		return CASounds.SEABORN_GENERIC_HIT.get();
	}

	@Override
	public SoundEvent getDeathSound() {
		return CASounds.SEABORN_DEATH.get();
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("Shelled", this.entityData.get(DATA_SHELLED));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Shelled")) {
			this.entityData.set(DATA_SHELLED, compound.getBoolean("Shelled"));
		}
	}

	@Override
	public void baseTick() {
		super.baseTick();
		LevelAccessor world = this.level();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		boolean found = false;
		double perc;
		if (this.isAlive()) {
			if (!((Entity) this instanceof ApostleProkaryoteEntity datEntL1 && datEntL1.getEntityData().get(DATA_SHELLED))) {
				for (int dx = -2; dx <= 2; dx++) {
					for (int dy = -2; dy <= 3; dy++) {
						for (int dz = -2; dz <= 2; dz++) {
							if ((world.getBlockState(BlockPos.containing(x + dx, y + dy, z + dz))).getBlock() == CABlocks.WHITE_CHITIN_BLOCK.get()) {
								this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((x + dx), (y + dy), (z + dz)));
								world.destroyBlock(BlockPos.containing(x + dx, y + dy, z + dz), false);
								found = true;
								break;
							}
						}
						if (found) {
							break;
						}
					}
					if (found) {
						break;
					}
				}
				if (found) {
					perc = this.getHealth() / this.getMaxHealth();
					if (MapVariables.get(world).strategy_subsisting >= 4) {
						if (this.getAttributes().hasAttribute(Attributes.MAX_HEALTH))
							this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(
									((this.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? this.getAttribute(Attributes.MAX_HEALTH).getBaseValue() : 0) * 2.2));
						if (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
							this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(
									((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : 0) * 1.8));
					} else {
						if (this.getAttributes().hasAttribute(Attributes.MAX_HEALTH))
							this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(
									((this.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? this.getAttribute(Attributes.MAX_HEALTH).getBaseValue() : 0) * 1.6));
						if (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
							this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(
									((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : 0) * 1.4));
					}
					this.setHealth((float) (this.getMaxHealth() * perc));
					if ((Entity) this instanceof ApostleProkaryoteEntity datEntSetL)
						datEntSetL.getEntityData().set(DATA_SHELLED, true);
					if (this instanceof ApostleProkaryoteEntity) {
						this.setAnimation("animation.apostle.skill");
					}
					CaerulaArbor.queueServerWork(17, () -> {
						if (world instanceof Level level) {
							level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.HOSTILE, 1, 1);
						}
					});
				}
			}
		}
		this.refreshDimensions();
	}

	@Override
	public boolean checkSpawnObstruction(LevelReader world) {
		return world.isUnobstructed(this);
	}

	@Override
	public boolean isPushedByFluid() {
		return false;
	}

	@Override
	public void updateSwimming() {
		if (!this.level().isClientSide()) {
			if (this.isEffectiveAi() && this.isInWater()) {
				this.navigation = this.waterNavigation;
				this.moveControl = this.swimControl;
				this.setSwimming(true);
			} else {
				this.navigation = this.groundNavigation;
				this.moveControl = this.landControl;
				this.setSwimming(false);
			}
		}
	}

	public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
		event.register(CAEntities.APOSTLE_PROKARYOTE.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (entityType, world, reason, pos, random) -> {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			return WorldUtils.canSpawnMarineSeaborn(world, x, y, z);
		}, RegisterSpawnPlacementsEvent.Operation.REPLACE);
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 1.5);
		builder = builder.add(Attributes.MAX_HEALTH, 50);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 9);
		builder = builder.add(Attributes.FOLLOW_RANGE, 24);
		builder = builder.add(NeoForgeMod.SWIM_SPEED, 1.5);
		builder = builder.add(CAAttributes.MAGIC_RESISTANCE, 18);
		builder = builder.add(Attributes.STEP_HEIGHT, 1.1f);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		boolean inWater = this.isInWaterOrBubble();
		if (this.isDeadOrDying()) {
			if (inWater) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.apostle.die"));
			}
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.apostle.die_lamd"));
		}
		if (this.animationprocedure.equals("empty")) {
			if (event.isMoving()) {
				if (inWater) {
					return event.setAndContinue(RawAnimation.begin().thenLoop("animation.apostle.move"));
				}
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.apostle.move_land"));
			}
			if (inWater) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.apostle.idle"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.apostle.idle_land"));
		}
		return PlayState.STOP;
	}

	private PlayState attackingPredicate(AnimationState event) {
		if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
			this.swinging = true;
			this.lastSwing = level().getGameTime();
		}
		if (this.swinging && this.lastSwing + 20L <= level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.apostle.attack"));
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

	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}

	public static class SeabornSwimControl extends MoveControl {
		public SeabornSwimControl(Mob mob) {
			super(mob);
		}

		@Override
		public void tick() {
			Mob entity = this.mob;
			if (entity.isInWater()) {
				entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, 0.005, 0.0));
			}
			if (this.operation == Operation.MOVE_TO && !entity.getNavigation().isDone()) {
				double dx = this.wantedX - entity.getX();
				double dy = this.wantedY - entity.getY();
				double dz = this.wantedZ - entity.getZ();
				float f = (float) (Mth.atan2(dz, dx) * (180D / Math.PI)) - 90.0f;
				float f1 = (float) (this.speedModifier * entity.getAttribute(NeoForgeMod.SWIM_SPEED).getValue());
				entity.setYRot(this.rotlerp(entity.getYRot(), f, 10.0f));
				entity.yBodyRot = entity.getYRot();
				entity.yHeadRot = entity.getYRot();
				if (entity.isInWater()) {
					LivingEntity target;
					entity.setSpeed((float) entity.getAttribute(NeoForgeMod.SWIM_SPEED).getValue());
					float f2 = -(float) (Mth.atan2(dy, (float) Math.sqrt(dx * dx + dz * dz)) * (180D / Math.PI));
					f2 = Mth.clamp(Mth.wrapDegrees(f2), -85.0f, 85.0f);
					entity.setXRot(this.rotlerp(entity.getXRot(), f2, 5.0f));
					float f3 = Mth.cos(entity.getXRot() * (float) (Math.PI / 180.0));
					entity.setZza(f3 * f1);
					entity.setYya((float) (f1 * dy));
					if (entity.tickCount % 20 == 0 && !entity.level().isClientSide() && (target = entity.getTarget()) != null && target.isAlive() && !target.isInWater() && target.getY() > entity.getY()) {
						Vec3 jump = entity.position().vectorTo(target.position()).normalize().scale(f1 * 0.35);
						entity.push(jump.x, jump.y + 0.33, jump.z);
					}
				} else {
					super.tick();
				}
			} else {
				entity.setSpeed(0.0f);
				entity.setYya(0.0f);
				entity.setZza(0.0f);
			}
		}
	}
}