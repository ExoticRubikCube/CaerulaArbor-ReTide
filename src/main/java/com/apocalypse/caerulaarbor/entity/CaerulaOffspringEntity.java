package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.init.CAEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class CaerulaOffspringEntity extends Monster implements GeoEntity {
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(CaerulaOffspringEntity.class, EntityDataSerializers.STRING);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public CaerulaOffspringEntity(Level world) {
		this(CAEntities.CAERULA_OFFSPRING.get(), world);
	}

	public CaerulaOffspringEntity(EntityType<CaerulaOffspringEntity> type, Level world) {
		super(type, world);
		xpReward = 16;
		setNoAi(false);
		setMaxUpStep(0.6f);
		setPersistenceRequired();
		this.moveControl = new FlyingMoveControl(this, 10, true);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(TEXTURE, "caerula_offspring");
	}

	public void setTexture(String texture) {
		this.entityData.set(TEXTURE, texture);
	}

	public String getTexture() {
		return this.entityData.get(TEXTURE);
	}

	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected PathNavigation createNavigation(Level world) {
		return new FlyingPathNavigation(this, world);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, false) {
			protected double getAttackReachSqr(LivingEntity entity) {
				return 1.0;
			}
		});
		this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0, 20) {
			protected Vec3 getPosition() {
				RandomSource random = CaerulaOffspringEntity.this.getRandom();
				double dir_x = CaerulaOffspringEntity.this.getX() + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
				double dir_y = CaerulaOffspringEntity.this.getY() + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
				double dir_z = CaerulaOffspringEntity.this.getZ() + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
				return new Vec3(dir_x, dir_y, dir_z);
			}
		});
		this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(5, new FloatGoal(this));
	}

	public MobType getMobType() {
		return MobType.WATER;
	}

	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	public SoundEvent getAmbientSound() {
		return SoundEvents.SQUID_AMBIENT;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return SoundEvents.SQUID_HURT;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.SQUID_DEATH;
	}

	public boolean causeFallDamage(float l, float d, DamageSource source) {
		return false;
	}

	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		return super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
	}

	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
	}

	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture")) {
			this.setTexture(compound.getString("Texture"));
		}
	}

	public void baseTick() {
		super.baseTick();
		this.refreshDimensions();
	}

	public EntityDimensions getDimensions(Pose pPose) {
		return super.getDimensions(pPose).scale(1.0f);
	}

	protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
	}

	public void setNoGravity(boolean ignored) {
		super.setNoGravity(true);
	}

	@Override
	public void aiStep() {
		super.aiStep();
		this.setNoGravity(true);
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.4);
		builder = builder.add(Attributes.MAX_HEALTH, 40.0);
		builder = builder.add(Attributes.ARMOR, 6.0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 6.0);
		builder = builder.add(Attributes.FOLLOW_RANGE, 16.0);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
		builder = builder.add(Attributes.FLYING_SPEED, 0.4);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.isDeadOrDying()) {
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.izumik_offspring.die"));
		}
		return event.setAndContinue(RawAnimation.begin().thenLoop("animation.izumik_offspring.idle"));
	}

	protected void tickDeath() {
		++this.deathTime;
		if (this.deathTime == 20) {
			this.remove(Entity.RemovalReason.KILLED);
			this.dropExperience();
		}
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		data.add(new AnimationController(this, "movement", 0, this::movementPredicate));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
}
