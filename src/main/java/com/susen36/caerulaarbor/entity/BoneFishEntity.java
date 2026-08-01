package com.susen36.caerulaarbor.entity;


import com.susen36.babel.elemental.base.AbstractEPCapability;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.util.EntityUtils;
import com.susen36.caerulaarbor.util.WorldUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import software.bernie.geckolib.animation.*;

public class BoneFishEntity extends SeaMonster implements Bucketable {
	public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(BoneFishEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(BoneFishEntity.class, EntityDataSerializers.STRING);
	private boolean swinging;
	private long lastSwing;
	private boolean fromBucket;
	public String animationprocedure = "empty";

	public BoneFishEntity(Level world) {
		this(CAEntities.BONE_FISH.get(), world);
	}

	public BoneFishEntity(EntityType<BoneFishEntity> type, Level world) {
		super(type, world);
		xpReward = 3;
		setNoAi(false);
		this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(0.6f);
		this.setPathfindingMalus(PathType.WATER, 0);
		this.moveControl = new MoveControl(this) {
			@Override
			public void tick() {
				if (BoneFishEntity.this.isInWater())
					BoneFishEntity.this.setDeltaMovement(BoneFishEntity.this.getDeltaMovement().add(0, 0.005, 0));
				if (this.operation == MoveControl.Operation.MOVE_TO && !BoneFishEntity.this.getNavigation().isDone()) {
					double dx = this.wantedX - BoneFishEntity.this.getX();
					double dy = this.wantedY - BoneFishEntity.this.getY();
					double dz = this.wantedZ - BoneFishEntity.this.getZ();
					float f = (float) (Mth.atan2(dz, dx) * (180 / Math.PI)) - 90;
					float f1 = (float) (this.speedModifier * BoneFishEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
					BoneFishEntity.this.setYRot(this.rotlerp(BoneFishEntity.this.getYRot(), f, 10));
					BoneFishEntity.this.yBodyRot = BoneFishEntity.this.getYRot();
					BoneFishEntity.this.yHeadRot = BoneFishEntity.this.getYRot();
					if (BoneFishEntity.this.isInWater()) {
						BoneFishEntity.this.setSpeed((float) BoneFishEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
						float f2 = -(float) (Mth.atan2(dy, (float) Math.sqrt(dx * dx + dz * dz)) * (180 / Math.PI));
						f2 = Mth.clamp(Mth.wrapDegrees(f2), -85, 85);
						BoneFishEntity.this.setXRot(this.rotlerp(BoneFishEntity.this.getXRot(), f2, 5));
						float f3 = Mth.cos(BoneFishEntity.this.getXRot() * (float) (Math.PI / 180.0));
						BoneFishEntity.this.setZza(f3 * f1);
						BoneFishEntity.this.setYya((float) (f1 * dy));
					} else {
						BoneFishEntity.this.setSpeed(f1 * 0.05F);
					}
				} else {
					BoneFishEntity.this.setSpeed(0);
					BoneFishEntity.this.setYya(0);
					BoneFishEntity.this.setZza(0);
				}
			}
		};
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_SHOOT, false);
		builder.define(DATA_ANIMATION, "undefined");
	}

    @Override
	protected PathNavigation createNavigation(Level world) {
		return new WaterBoundPathNavigation(this, world);
	}

	@Override
	public AbstractEPCapability.EPType getElementalType() {
		return AbstractEPCapability.EPType.CORROSION;
	}

	@Override
	public double getElementalRate() {
		return 0.45D;
	}

	@Override
	public double getElementalInjuryDamage() {
		return 15;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1, false));
		this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, target -> EntityUtils.isOceanizedPlayerNearby(this.level(), this.getX(), this.getY(), this.getZ())));
		this.goalSelector.addGoal(8, new RandomSwimmingGoal(this, 1, 40));
		this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
	}

    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
		super.dropCustomDeathLoot(level, damageSource, recentlyHit);
		this.spawnAtLocation(new ItemStack(CAItems.BONE_SHARD.get()));
	}

	@Override
	public SoundEvent getAmbientSound() {
		return SoundEvents.SALMON_AMBIENT;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return SoundEvents.SALMON_HURT;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.SALMON_DEATH;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.DROWN))
			return false;
		return super.hurt(source, amount);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("FromBucket", this.fromBucket());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.setFromBucket(compound.getBoolean("FromBucket"));
	}

	@Override
	public boolean fromBucket() {
		return this.fromBucket;
	}

	@Override
	public void setFromBucket(boolean fromBucket) {
		this.fromBucket = fromBucket;
	}

	@Override
	public void saveToBucketTag(ItemStack bucketStack) {
		Bucketable.saveDefaultDataToBucketTag(this, bucketStack);
	}

	@Override
	public void loadFromBucketTag(CompoundTag bucketTag) {
		Bucketable.loadDefaultDataFromBucketTag(this, bucketTag);
		this.setFromBucket(true);
	}

	@Override
	public ItemStack getBucketItemStack() {
		return new ItemStack(CAItems.BUCKET_BONEFISH.get());
	}

	@Override
	public SoundEvent getPickupSound() {
		return SoundEvents.BUCKET_FILL_FISH;
	}

	@Override
	public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
		return Bucketable.bucketMobPickup(sourceentity, hand, this).orElse(super.mobInteract(sourceentity, hand));
	}

	@Override
	public boolean requiresCustomPersistence() {
		return super.requiresCustomPersistence() || this.fromBucket();
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return !this.fromBucket() && !this.hasCustomName();
	}

	@Override
	public void baseTick() {
		super.baseTick();
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

	public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
		event.register(CAEntities.BONE_FISH.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (entityType, world, reason, pos, random) -> {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			return WorldUtils.canSpawnUnderwaterSeaborn(world, x, y, z);
		}, RegisterSpawnPlacementsEvent.Operation.REPLACE);
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 2);
		builder = builder.add(Attributes.MAX_HEALTH, 9);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 4);
		builder = builder.add(Attributes.FOLLOW_RANGE, 12);
		builder = builder.add(NeoForgeMod.SWIM_SPEED, 2);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.bonefish.idle"));
		}
		return PlayState.STOP;
	}

	private PlayState attackingPredicate(AnimationState event) {
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
			this.swinging = true;
			this.lastSwing = level().getGameTime();
		}
		if (this.swinging && this.lastSwing + 7L <= level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.bonefish.attack"));
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
			this.remove(BoneFishEntity.RemovalReason.KILLED);
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
		data.add(new AnimationController<>(this, "movement", 3, this::movementPredicate));
		data.add(new AnimationController<>(this, "attacking", 3, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 3, this::procedurePredicate));
	}

	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}
}