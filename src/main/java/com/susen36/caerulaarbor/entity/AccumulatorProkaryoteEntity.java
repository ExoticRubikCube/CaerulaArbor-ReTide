package com.susen36.caerulaarbor.entity;


import com.susen36.babel.init.BabelAttributes;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.init.CASounds;
import com.susen36.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import javax.annotation.Nullable;

public class AccumulatorProkaryoteEntity extends SeaMonster {
	public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(AccumulatorProkaryoteEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(AccumulatorProkaryoteEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Boolean> DATA_SPLIT = SynchedEntityData.defineId(AccumulatorProkaryoteEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(AccumulatorProkaryoteEntity.class, EntityDataSerializers.INT);
	public static final int VARIANT_ACCUMULATOR = 0;
	public static final int VARIANT_DIVICELLULAR = 1;
	private boolean swinging;
	private long lastSwing;
	public String animationprocedure = "empty";
	protected WaterBoundPathNavigation waterNavigation;
	protected GroundPathNavigation groundNavigation;
	private MoveControl landControl;
	private ApostleProkaryoteEntity.SeabornSwimControl swimControl;
	String prevAnim = "empty";

	public AccumulatorProkaryoteEntity(Level world) {
		this(CAEntities.ACCUMULATOR_PROKARYOTE.get(), world);
	}

	public AccumulatorProkaryoteEntity(EntityType<AccumulatorProkaryoteEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);
		this.setPathfindingMalus(PathType.WATER, 0);
		this.landControl = new MoveControl(this);
		this.swimControl = new ApostleProkaryoteEntity.SeabornSwimControl(this);
		this.moveControl = this.landControl;
		this.waterNavigation = new WaterBoundPathNavigation(this, world);
		this.groundNavigation = new GroundPathNavigation(this, world);
	}

	public boolean isDivicellularVariant() {
		return this.entityData.get(DATA_VARIANT) == VARIANT_DIVICELLULAR;
	}

	public void refreshVariantAttributes() {
		if (this.isDivicellularVariant()) {
			if (this.getAttributes().hasAttribute(Attributes.ARMOR)) {
				this.getAttribute(Attributes.ARMOR).setBaseValue(5.0D);
			}
			if (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) {
				this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4.0D);
			}
			if (this.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
				this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25D);
			}
			if (this.getAttributes().hasAttribute(Attributes.KNOCKBACK_RESISTANCE)) {
				this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5D);
			}
		} else {
			if (this.getAttributes().hasAttribute(Attributes.ARMOR)) {
				this.getAttribute(Attributes.ARMOR).setBaseValue(0.0D);
			}
			if (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) {
				this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(3.0D);
			}
			if (this.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
				this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2D);
			}
			if (this.getAttributes().hasAttribute(Attributes.KNOCKBACK_RESISTANCE)) {
				this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
			}
		}
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_SHOOT, false);
		builder.define(DATA_ANIMATION, "undefined");
		builder.define(DATA_SPLIT, true);
		builder.define(DATA_VARIANT, VARIANT_ACCUMULATOR);
	}

	@Override
	protected PathNavigation createNavigation(Level world) {
		return new GroundPathNavigation(this, world);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		if (this.isDivicellularVariant()) {
			this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15, false));
			this.goalSelector.addGoal(8, new RandomStrollGoal(this, 1));
			this.goalSelector.addGoal(9, new RandomSwimmingGoal(this, 4, 40));
			this.goalSelector.addGoal(10, new FloatGoal(this));
			this.goalSelector.addGoal(11, new RandomLookAroundGoal(this));
		} else {
			this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.25, false));
			this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0));
			this.goalSelector.addGoal(5, new RandomSwimmingGoal(this, 1, 40));
			this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
		}
	}

	@Override
	protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
		super.dropCustomDeathLoot(level, damageSource, recentlyHit);
		if (this.isDivicellularVariant()) {
			this.spawnAtLocation(new ItemStack(Blocks.GLASS));
		}
	}

	@Override
	public void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(SoundEvents.PUFFER_FISH_FLOP, 0.15f, 1);
	}

	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		if (this.isDivicellularVariant()) {
			return SoundEvents.PUFFER_FISH_HURT;
		}
		return CASounds.SEABORN_GENERIC_HIT.get();
	}

	@Override
	public SoundEvent getDeathSound() {
		if (this.isDivicellularVariant()) {
			return SoundEvents.PUFFER_FISH_DEATH;
		}
		return CASounds.SEABORN_DEATH.get();
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("Split", this.entityData.get(DATA_SPLIT));
		compound.putInt("Variant", this.entityData.get(DATA_VARIANT));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Split")) {
			this.entityData.set(DATA_SPLIT, compound.getBoolean("Split"));
		}
		if (compound.contains("Variant")) {
			this.entityData.set(DATA_VARIANT, compound.getInt("Variant"));
		}
		if (!this.level().isClientSide()) {
			this.refreshVariantAttributes();
		}
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
		SpawnGroupData data = super.finalizeSpawn(world, difficulty, reason, livingdata);
		if (!this.level().isClientSide()) {
			this.refreshVariantAttributes();
		}
		return data;
	}

	@Override
	public void baseTick() {
		super.baseTick();
		LevelAccessor world = this.level();
		if (!this.isDivicellularVariant() && this.isAlive() && tickCount % 10 == 0) {
			if (this.getHealth() <= this.getMaxHealth() * 0.5F && this.entityData.get(DATA_SPLIT)) {
				this.setAnimation("animation.accumulator.split");
				this.setHealth(this.getMaxHealth() * 0.5F);
				this.entityData.set(DATA_SPLIT, false);
				if (!this.hasEffect(CAMobEffects.MUTE)) {
					CaerulaArbor.queueServerWork(10, () -> {
						ServerLevel serverLevel = world instanceof ServerLevel ? (ServerLevel) world : null;
						if (serverLevel != null) {
							int targetVariant = this.isInWater() ? VARIANT_ACCUMULATOR : VARIANT_DIVICELLULAR;
							Entity entityToSpawn = CAEntities.ACCUMULATOR_PROKARYOTE.get().spawn(serverLevel, BlockPos.containing(getX() + Mth.nextDouble(RandomSource.create(), -1, 1), getY() + 0.5, getZ() + Mth.nextDouble(RandomSource.create(), -1, 1)), MobSpawnType.MOB_SUMMONED);
							if (entityToSpawn instanceof AccumulatorProkaryoteEntity cloneProkaryote) {
								cloneProkaryote.entityData.set(DATA_VARIANT, targetVariant);
								if (!serverLevel.isClientSide()) {
									cloneProkaryote.refreshVariantAttributes();
								}
								if (targetVariant == VARIANT_ACCUMULATOR) {
									cloneProkaryote.setHealth(cloneProkaryote.getMaxHealth() * 0.5F);
								}
								cloneProkaryote.entityData.set(DATA_SPLIT, false);
							}
							if (entityToSpawn != null) {
								entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
								if (this instanceof Mob parentMob && entityToSpawn instanceof Mob childMob) {
									Team team = parentMob.getTeam();
									MinecraftServer server = childMob.getServer();
									if (server != null && team instanceof PlayerTeam playerTeam) {
										server.getScoreboard().addPlayerToTeam(childMob.getScoreboardName(), playerTeam);
									}
								}
							}
						}
						if (world instanceof Level level) {
							level.playSound(null, BlockPos.containing(getX(), getY(), getZ()), SoundEvents.PUFFER_FISH_BLOW_OUT, SoundSource.HOSTILE, 2, 1);
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
		event.register(CAEntities.ACCUMULATOR_PROKARYOTE.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (entityType, world, reason, pos, random) -> {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			return WorldUtils.canSpawnUnderwaterSeaborn(world, x, y, z);
		}, RegisterSpawnPlacementsEvent.Operation.REPLACE);
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.2);
		builder = builder.add(Attributes.MAX_HEALTH, 32);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
		builder = builder.add(Attributes.FOLLOW_RANGE, 16);
		builder = builder.add(NeoForgeMod.SWIM_SPEED, 0.9D);
		builder = builder.add(BabelAttributes.MAGIC_RESISTANCE, 18);
		builder = builder.add(Attributes.STEP_HEIGHT, 0.6f);
		return builder;
	}


	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if (this.isDivicellularVariant()) {
				if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))) {
					return event.setAndContinue(RawAnimation.begin().thenLoop("animation.accumulator.move"));
				}
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.accumulator.idle"));
			}
			boolean inWater = this.isInWaterOrBubble();
			if (event.isMoving()) {
				if (inWater) {
					return event.setAndContinue(RawAnimation.begin().thenLoop("animation.accumulator.move"));
				}
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.accumulator.move_land"));
			}
			if (inWater) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.accumulator.idle"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.accumulator.idle_land"));
		}
		return PlayState.STOP;
	}

	private PlayState attackingPredicate(AnimationState event) {
		if (this.isDivicellularVariant()) {
			if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
				this.swinging = true;
				this.lastSwing = level().getGameTime();
			}
			if (this.swinging && this.lastSwing + 10L <= level().getGameTime()) {
				this.swinging = false;
			}
			if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
				event.getController().forceAnimationReset();
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.accumulator.attack"));
			}
		} else {
			double d1 = this.getX() - this.xOld;
			double d0 = this.getZ() - this.zOld;
			if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
				this.swinging = true;
				this.lastSwing = level().getGameTime();
			}
			if (this.swinging && this.lastSwing + 10L <= level().getGameTime()) {
				this.swinging = false;
			}
			if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
				event.getController().forceAnimationReset();
				if (this.isInWaterOrBubble()) {
					return event.setAndContinue(RawAnimation.begin().thenPlay("animation.accumulator.attack"));
				}
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.accumulator.attack_land"));
			}
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
		data.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
		data.add(new AnimationController<>(this, "attacking", 4, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
	}


	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}
}
