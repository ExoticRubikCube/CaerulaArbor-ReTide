package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import com.apocalypse.caerulaarbor.init.CaerulaArborModItems;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
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
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;

public class AccumulatorCloneEntity extends SeaMonster {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(AccumulatorCloneEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(AccumulatorCloneEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(AccumulatorCloneEntity.class, EntityDataSerializers.STRING);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public AccumulatorCloneEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CaerulaArborModEntities.ACCUMULATOR_CLONE.get(), world);
	}

	public AccumulatorCloneEntity(EntityType<AccumulatorCloneEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);
		setMaxUpStep(0.6f);
		this.setPathfindingMalus(BlockPathTypes.WATER, 0);
		this.moveControl = new MoveControl(this) {
			@Override
			public void tick() {
				if (AccumulatorCloneEntity.this.isInWater())
					AccumulatorCloneEntity.this.setDeltaMovement(AccumulatorCloneEntity.this.getDeltaMovement().add(0, 0.005, 0));
				if (this.operation == MoveControl.Operation.MOVE_TO && !AccumulatorCloneEntity.this.getNavigation().isDone()) {
					double dx = this.wantedX - AccumulatorCloneEntity.this.getX();
					double dy = this.wantedY - AccumulatorCloneEntity.this.getY();
					double dz = this.wantedZ - AccumulatorCloneEntity.this.getZ();
					float f = (float) (Mth.atan2(dz, dx) * (180 / Math.PI)) - 90;
					float f1 = (float) (this.speedModifier * AccumulatorCloneEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
					AccumulatorCloneEntity.this.setYRot(this.rotlerp(AccumulatorCloneEntity.this.getYRot(), f, 10));
					AccumulatorCloneEntity.this.yBodyRot = AccumulatorCloneEntity.this.getYRot();
					AccumulatorCloneEntity.this.yHeadRot = AccumulatorCloneEntity.this.getYRot();
					if (AccumulatorCloneEntity.this.isInWater()) {
						AccumulatorCloneEntity.this.setSpeed((float) AccumulatorCloneEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
						float f2 = -(float) (Mth.atan2(dy, (float) Math.sqrt(dx * dx + dz * dz)) * (180 / Math.PI));
						f2 = Mth.clamp(Mth.wrapDegrees(f2), -85, 85);
						AccumulatorCloneEntity.this.setXRot(this.rotlerp(AccumulatorCloneEntity.this.getXRot(), f2, 5));
						float f3 = Mth.cos(AccumulatorCloneEntity.this.getXRot() * (float) (Math.PI / 180.0));
						AccumulatorCloneEntity.this.setZza(f3 * f1);
						AccumulatorCloneEntity.this.setYya((float) (f1 * dy));
					} else {
						AccumulatorCloneEntity.this.setSpeed(f1 * 0.05F);
					}
				} else {
					AccumulatorCloneEntity.this.setSpeed(0);
					AccumulatorCloneEntity.this.setYya(0);
					AccumulatorCloneEntity.this.setZza(0);
				}
			}
		};
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "accumulator");
	}

	public void setTexture(String texture) {
		this.entityData.set(TEXTURE, texture);
	}

	public String getTexture() {
		return this.entityData.get(TEXTURE);
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected PathNavigation createNavigation(Level world) {
		return new WaterBoundPathNavigation(this, world);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.25, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 9;
			}
		});
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Pufferfish.class, true, false) {
			@Override
			public boolean canUse() {
				double x = AccumulatorCloneEntity.this.getX();
				double y = AccumulatorCloneEntity.this.getY();
				double z = AccumulatorCloneEntity.this.getZ();
				Entity entity = AccumulatorCloneEntity.this;
				Level world = AccumulatorCloneEntity.this.level();
				return super.canUse() && EntityUtils.canAttackAnimals();
			}

			@Override
			public boolean canContinueToUse() {
				double x = AccumulatorCloneEntity.this.getX();
				double y = AccumulatorCloneEntity.this.getY();
				double z = AccumulatorCloneEntity.this.getZ();
				Entity entity = AccumulatorCloneEntity.this;
				Level world = AccumulatorCloneEntity.this.level();
				return super.canContinueToUse() && EntityUtils.canAttackAnimals();
			}
		});
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, GlowSquid.class, true, false) {
			@Override
			public boolean canUse() {
				double x = AccumulatorCloneEntity.this.getX();
				double y = AccumulatorCloneEntity.this.getY();
				double z = AccumulatorCloneEntity.this.getZ();
				Entity entity = AccumulatorCloneEntity.this;
				Level world = AccumulatorCloneEntity.this.level();
				return super.canUse() && EntityUtils.canAttackAnimals();
			}

			@Override
			public boolean canContinueToUse() {
				double x = AccumulatorCloneEntity.this.getX();
				double y = AccumulatorCloneEntity.this.getY();
				double z = AccumulatorCloneEntity.this.getZ();
				Entity entity = AccumulatorCloneEntity.this;
				Level world = AccumulatorCloneEntity.this.level();
				return super.canContinueToUse() && EntityUtils.canAttackAnimals();
			}
		});
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Squid.class, true, false) {
			@Override
			public boolean canUse() {
				double x = AccumulatorCloneEntity.this.getX();
				double y = AccumulatorCloneEntity.this.getY();
				double z = AccumulatorCloneEntity.this.getZ();
				Entity entity = AccumulatorCloneEntity.this;
				Level world = AccumulatorCloneEntity.this.level();
				return super.canUse() && EntityUtils.canAttackAnimals();
			}

			@Override
			public boolean canContinueToUse() {
				double x = AccumulatorCloneEntity.this.getX();
				double y = AccumulatorCloneEntity.this.getY();
				double z = AccumulatorCloneEntity.this.getZ();
				Entity entity = AccumulatorCloneEntity.this;
				Level world = AccumulatorCloneEntity.this.level();
				return super.canContinueToUse() && EntityUtils.canAttackAnimals();
			}
		});
		this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, TropicalFish.class, true, false) {
			@Override
			public boolean canUse() {
				double x = AccumulatorCloneEntity.this.getX();
				double y = AccumulatorCloneEntity.this.getY();
				double z = AccumulatorCloneEntity.this.getZ();
				Entity entity = AccumulatorCloneEntity.this;
				Level world = AccumulatorCloneEntity.this.level();
				return super.canUse() && EntityUtils.canAttackAnimals();
			}

			@Override
			public boolean canContinueToUse() {
				double x = AccumulatorCloneEntity.this.getX();
				double y = AccumulatorCloneEntity.this.getY();
				double z = AccumulatorCloneEntity.this.getZ();
				Entity entity = AccumulatorCloneEntity.this;
				Level world = AccumulatorCloneEntity.this.level();
				return super.canContinueToUse() && EntityUtils.canAttackAnimals();
			}
		});
		this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, Salmon.class, true, false) {
			@Override
			public boolean canUse() {
				double x = AccumulatorCloneEntity.this.getX();
				double y = AccumulatorCloneEntity.this.getY();
				double z = AccumulatorCloneEntity.this.getZ();
				Entity entity = AccumulatorCloneEntity.this;
				Level world = AccumulatorCloneEntity.this.level();
				return super.canUse() && EntityUtils.canAttackAnimals();
			}

			@Override
			public boolean canContinueToUse() {
				double x = AccumulatorCloneEntity.this.getX();
				double y = AccumulatorCloneEntity.this.getY();
				double z = AccumulatorCloneEntity.this.getZ();
				Entity entity = AccumulatorCloneEntity.this;
				Level world = AccumulatorCloneEntity.this.level();
				return super.canContinueToUse() && EntityUtils.canAttackAnimals();
			}
		});
		this.targetSelector.addGoal(8, new NearestAttackableTargetGoal(this, Player.class, true, false) {
			@Override
			public boolean canUse() {
				double x = AccumulatorCloneEntity.this.getX();
				double y = AccumulatorCloneEntity.this.getY();
				double z = AccumulatorCloneEntity.this.getZ();
				Entity entity = AccumulatorCloneEntity.this;
				Level world = AccumulatorCloneEntity.this.level();
				return super.canUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
			}

			@Override
			public boolean canContinueToUse() {
				double x = AccumulatorCloneEntity.this.getX();
				double y = AccumulatorCloneEntity.this.getY();
				double z = AccumulatorCloneEntity.this.getZ();
				Entity entity = AccumulatorCloneEntity.this;
				Level world = AccumulatorCloneEntity.this.level();
				return super.canContinueToUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
			}
		});
		this.goalSelector.addGoal(9, new RandomSwimmingGoal(this, 1, 40));
		this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
	}

	@Override
	public MobType getMobType() {
		return MobType.WATER;
	}

	protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHitIn) {
		super.dropCustomDeathLoot(source, looting, recentlyHitIn);
		this.spawnAtLocation(new ItemStack(CaerulaArborModItems.OCEAN_CELL.get()));
	}

	@Override
	public void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.puffer_fish.flop")), 0.15f, 1);
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "seaborn_generic_hit"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "seaborn_death"));
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.DROWN))
			return false;
		return super.hurt(source, amount);
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
        if (this != null) {
            if ((Entity) this instanceof LivingEntity _entity)
                _entity.setHealth((float) (((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.5));
        }
        return retval;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
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
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	public boolean checkSpawnObstruction(LevelReader world) {
		return world.isUnobstructed(this);
	}

	@Override
	public boolean isPushedByFluid() {
		return false;
	}

	public static void init() {
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 1.75);
		builder = builder.add(Attributes.MAX_HEALTH, 32);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
		builder = builder.add(Attributes.FOLLOW_RANGE, 16);
		builder = builder.add(ForgeMod.SWIM_SPEED.get(), 1.75);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

			) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.accumulator.move"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.accumulator.idle"));
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
		if (this.swinging && this.lastSwing + 10L <= level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.accumulator.attack"));
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
			this.remove(AccumulatorCloneEntity.RemovalReason.KILLED);
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
		data.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
		data.add(new AnimationController<>(this, "attacking", 4, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
	}
}
