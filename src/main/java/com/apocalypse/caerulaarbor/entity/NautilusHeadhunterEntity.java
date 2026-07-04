package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.api.event.SanityEvent;
import com.apocalypse.caerulaarbor.capability.sanity.SIHelper;
import com.apocalypse.caerulaarbor.entity.base.SyncedAnimationEntity;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
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
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import net.minecraft.sounds.SoundEvents;

public class NautilusHeadhunterEntity extends Animal implements GeoEntity, SyncedAnimationEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(NautilusHeadhunterEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(NautilusHeadhunterEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_DRY_TICK = SynchedEntityData.defineId(NautilusHeadhunterEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_BONUS = SynchedEntityData.defineId(NautilusHeadhunterEntity.class, EntityDataSerializers.INT);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	public String animationprocedure = "empty";

	public NautilusHeadhunterEntity(Level world) {
		this(CAEntities.NAUTILUS_HEADHUNTER.get(), world);
	}

	public NautilusHeadhunterEntity(EntityType<NautilusHeadhunterEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);
		setMaxUpStep(0.6f);
		this.setPathfindingMalus(BlockPathTypes.WATER, 0);
		this.moveControl = new MoveControl(this) {
			@Override
			public void tick() {
				if (NautilusHeadhunterEntity.this.isInWater())
					NautilusHeadhunterEntity.this.setDeltaMovement(NautilusHeadhunterEntity.this.getDeltaMovement().add(0, 0.005, 0));
				if (this.operation == MoveControl.Operation.MOVE_TO && !NautilusHeadhunterEntity.this.getNavigation().isDone()) {
					double dx = this.wantedX - NautilusHeadhunterEntity.this.getX();
					double dy = this.wantedY - NautilusHeadhunterEntity.this.getY();
					double dz = this.wantedZ - NautilusHeadhunterEntity.this.getZ();
					float f = (float) (Mth.atan2(dz, dx) * (180 / Math.PI)) - 90;
					float f1 = (float) (this.speedModifier * NautilusHeadhunterEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
					NautilusHeadhunterEntity.this.setYRot(this.rotlerp(NautilusHeadhunterEntity.this.getYRot(), f, 10));
					NautilusHeadhunterEntity.this.yBodyRot = NautilusHeadhunterEntity.this.getYRot();
					NautilusHeadhunterEntity.this.yHeadRot = NautilusHeadhunterEntity.this.getYRot();
					if (NautilusHeadhunterEntity.this.isInWater()) {
						NautilusHeadhunterEntity.this.setSpeed((float) NautilusHeadhunterEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
						float f2 = -(float) (Mth.atan2(dy, (float) Math.sqrt(dx * dx + dz * dz)) * (180 / Math.PI));
						f2 = Mth.clamp(Mth.wrapDegrees(f2), -85, 85);
						NautilusHeadhunterEntity.this.setXRot(this.rotlerp(NautilusHeadhunterEntity.this.getXRot(), f2, 5));
						float f3 = Mth.cos(NautilusHeadhunterEntity.this.getXRot() * (float) (Math.PI / 180.0));
						NautilusHeadhunterEntity.this.setZza(f3 * f1);
						NautilusHeadhunterEntity.this.setYya((float) (f1 * dy));
					} else {
						NautilusHeadhunterEntity.this.setSpeed(f1 * 0.05F);
					}
				} else {
					NautilusHeadhunterEntity.this.setSpeed(0);
					NautilusHeadhunterEntity.this.setYya(0);
					NautilusHeadhunterEntity.this.setZza(0);
				}
			}
		};
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(DATA_DRY_TICK, 0);
		this.entityData.define(DATA_BONUS, 0);
	}


	@Override
	protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
		return 0.35F;
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
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth();
			}
		});
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Drowned.class, true, false));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, SnowGolem.class, true, false));
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Villager.class, true, false));
		this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Illusioner.class, true, false));
		this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, Pillager.class, true, false));
		this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, Vindicator.class, true, false));
		this.targetSelector.addGoal(9, new NearestAttackableTargetGoal<>(this, Witch.class, true, false));
		this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, Evoker.class, true, false));
		this.goalSelector.addGoal(11, new RandomSwimmingGoal(this, 1, 40));
		this.goalSelector.addGoal(12, new RandomLookAroundGoal(this));
	}

	@Override
	public MobType getMobType() {
		return MobType.WATER;
	}

	protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHitIn) {
		super.dropCustomDeathLoot(source, looting, recentlyHitIn);
		this.spawnAtLocation(new ItemStack(Items.NAUTILUS_SHELL));
	}

	@Override
	public SoundEvent getAmbientSound() {
		return SoundEvents.TURTLE_AMBIENT_LAND;
	}

	@Override
	public void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(SoundEvents.TURTLE_SWIM, 0.15f, 1);
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return SoundEvents.TURTLE_HURT_BABY;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.TURTLE_DEATH_BABY;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
        ((Entity) this).stopRiding();
        if (source.is(DamageTypes.DROWN))
			return false;
		return super.hurt(source, amount);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("DataDRY_TICK", this.entityData.get(DATA_DRY_TICK));
		compound.putInt("DataBONUS", this.entityData.get(DATA_BONUS));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("DataDRY_TICK"))
			this.entityData.set(DATA_DRY_TICK, compound.getInt("DataDRY_TICK"));
		if (compound.contains("DataBONUS"))
			this.entityData.set(DATA_BONUS, compound.getInt("DataBONUS"));
	}

	@Override
	public void awardKillScore(Entity entity, int score, DamageSource damageSource) {
		super.awardKillScore(entity, score, damageSource);
        double bonus;
        bonus = (Entity) this instanceof NautilusHeadhunterEntity _datEntI ? _datEntI.getEntityData().get(DATA_BONUS) : 0;
        if (bonus < 10) {
            if (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
                this.getAttribute(Attributes.ATTACK_DAMAGE)
                        .setBaseValue(((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : 0) + 1));
            if ((Entity) this instanceof NautilusHeadhunterEntity _datEntSetI)
                _datEntSetI.getEntityData().set(DATA_BONUS, (int) (bonus + 1));
        }
    }

	@Override
	public void baseTick() {
		super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        double dryTick;
        boolean isMounting;
        Entity enemy;
        Entity vehicle;
        if (this.isAlive()) {
            dryTick = (Entity) this instanceof NautilusHeadhunterEntity _datEntI ? _datEntI.getEntityData().get(DATA_DRY_TICK) : 0;
            enemy = this.getTarget();
            isMounting = isPassenger();
            if (isInWaterRainOrBubble() || isMounting) {
                if ((Entity) this instanceof NautilusHeadhunterEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_DRY_TICK, 0);
            } else {
                if ((Entity) this instanceof NautilusHeadhunterEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_DRY_TICK, (int) (dryTick + 1));
            }
            if (tickCount % 20 == 5) {
                if (dryTick > 300) {
                    this.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.DRY_OUT)), (float) (this.getMaxHealth() * 0.05));
                }
                if (isMounting) {
                    vehicle = getVehicle();
                    if (!(vehicle == null) && vehicle.isAlive()) {
                        if (!world.isClientSide()) {
                            if (world instanceof Level _level) {
                                    _level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.DOLPHIN_EAT, SoundSource.HOSTILE, 1, 1);
                            }
                        }
                        vehicle.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_WALL), this),
                                (float) (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0));
                        if (vehicle instanceof LivingEntity target) {
                            SIHelper.causeSanityInjury(target, this, 50, SanityEvent.Hurt.Type.ENTITY);
                        }
                    }
                } else {
                    if (!(enemy == null) && enemy.isAlive() && !(enemy instanceof Player)) {
                        if (distanceTo(enemy) <= 2 && !enemy.isVehicle()) {
                            if (!world.isClientSide()) {
                                if (world instanceof Level _level) {
                                        _level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.STRIDER_SADDLE, SoundSource.HOSTILE, 1, 1);
                                }
                            }
                            startRiding(enemy);
                        }
                    }
                }
            }
        }
        this.refreshDimensions();
	}

	

	@Override
	public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageable) {
		NautilusHeadhunterEntity retval = CAEntities.NAUTILUS_HEADHUNTER.get().create(serverWorld);
		retval.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(retval.blockPosition()), MobSpawnType.BREEDING, null, null);
		return retval;
	}

	@Override
	public boolean isFood(ItemStack stack) {
        stack.getItem();
        return false;
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

	@Override
	public void aiStep() {
		super.aiStep();
		this.updateSwingTime();
	}

	public static void registerSpawnPlacements() {
		SpawnPlacements.register(CAEntities.NAUTILUS_HEADHUNTER.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (entityType, world, reason, pos, random) -> {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			return WorldUtils.canSpawnMarineSeaborn(world, x, y, z);
		});
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 2);
		builder = builder.add(Attributes.MAX_HEALTH, 25);
		builder = builder.add(Attributes.ARMOR, 6);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
		builder = builder.add(Attributes.FOLLOW_RANGE, 16);
		builder = builder.add(Attributes.ATTACK_KNOCKBACK, 0.5);
		builder = builder.add(ForgeMod.SWIM_SPEED.get(), 2);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

					&& !this.isAggressive()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.nultilus_headhunter.swim"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.nultilus_headhunter.die"));
			}
			if (this.isAggressive() && event.isMoving()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.nultilus_headhunter.sprint"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.nultilus_headhunter.idle"));
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
		if (this.deathTime == 20) {
			this.remove(NautilusHeadhunterEntity.RemovalReason.KILLED);
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

