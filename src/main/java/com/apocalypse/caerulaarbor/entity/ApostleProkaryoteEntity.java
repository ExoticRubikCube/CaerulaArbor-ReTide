package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.capability.map.MapVariables;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CaerulaArborModBlocks;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import com.apocalypse.caerulaarbor.util.WorldUtils;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
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

public class ApostleProkaryoteEntity extends SeaMonster {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(ApostleProkaryoteEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(ApostleProkaryoteEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(ApostleProkaryoteEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Boolean> DATA_shelled = SynchedEntityData.defineId(ApostleProkaryoteEntity.class, EntityDataSerializers.BOOLEAN);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public ApostleProkaryoteEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CaerulaArborModEntities.APOSTLE_PROKARYOTE.get(), world);
	}

	public ApostleProkaryoteEntity(EntityType<ApostleProkaryoteEntity> type, Level world) {
		super(type, world);
		xpReward = 8;
		setNoAi(false);
		setMaxUpStep(1.1f);
		this.setPathfindingMalus(BlockPathTypes.WATER, 0);
		this.moveControl = new MoveControl(this) {
			@Override
			public void tick() {
				if (ApostleProkaryoteEntity.this.isInWater())
					ApostleProkaryoteEntity.this.setDeltaMovement(ApostleProkaryoteEntity.this.getDeltaMovement().add(0, 0.005, 0));
				if (this.operation == MoveControl.Operation.MOVE_TO && !ApostleProkaryoteEntity.this.getNavigation().isDone()) {
					double dx = this.wantedX - ApostleProkaryoteEntity.this.getX();
					double dy = this.wantedY - ApostleProkaryoteEntity.this.getY();
					double dz = this.wantedZ - ApostleProkaryoteEntity.this.getZ();
					float f = (float) (Mth.atan2(dz, dx) * (180 / Math.PI)) - 90;
					float f1 = (float) (this.speedModifier * ApostleProkaryoteEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
					ApostleProkaryoteEntity.this.setYRot(this.rotlerp(ApostleProkaryoteEntity.this.getYRot(), f, 10));
					ApostleProkaryoteEntity.this.yBodyRot = ApostleProkaryoteEntity.this.getYRot();
					ApostleProkaryoteEntity.this.yHeadRot = ApostleProkaryoteEntity.this.getYRot();
					if (ApostleProkaryoteEntity.this.isInWater()) {
						ApostleProkaryoteEntity.this.setSpeed((float) ApostleProkaryoteEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
						float f2 = -(float) (Mth.atan2(dy, (float) Math.sqrt(dx * dx + dz * dz)) * (180 / Math.PI));
						f2 = Mth.clamp(Mth.wrapDegrees(f2), -85, 85);
						ApostleProkaryoteEntity.this.setXRot(this.rotlerp(ApostleProkaryoteEntity.this.getXRot(), f2, 5));
						float f3 = Mth.cos(ApostleProkaryoteEntity.this.getXRot() * (float) (Math.PI / 180.0));
						ApostleProkaryoteEntity.this.setZza(f3 * f1);
						ApostleProkaryoteEntity.this.setYya((float) (f1 * dy));
					} else {
						ApostleProkaryoteEntity.this.setSpeed(f1 * 0.05F);
					}
				} else {
					ApostleProkaryoteEntity.this.setSpeed(0);
					ApostleProkaryoteEntity.this.setYya(0);
					ApostleProkaryoteEntity.this.setZza(0);
				}
			}
		};
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "apostle_unshelled");
		this.entityData.define(DATA_shelled, false);
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
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 6.25;
			}
		});
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Pufferfish.class, true, false) {
			@Override
			public boolean canUse() {
				return super.canUse() && EntityUtils.canAttackAnimals();
			}

			@Override
			public boolean canContinueToUse() {
				double x = ApostleProkaryoteEntity.this.getX();
				double y = ApostleProkaryoteEntity.this.getY();
				double z = ApostleProkaryoteEntity.this.getZ();
				Entity entity = ApostleProkaryoteEntity.this;
				Level world = ApostleProkaryoteEntity.this.level();
				return super.canContinueToUse() && EntityUtils.canAttackAnimals();
			}
		});
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, GlowSquid.class, true, false) {
			@Override
			public boolean canUse() {
				double x = ApostleProkaryoteEntity.this.getX();
				double y = ApostleProkaryoteEntity.this.getY();
				double z = ApostleProkaryoteEntity.this.getZ();
				Entity entity = ApostleProkaryoteEntity.this;
				Level world = ApostleProkaryoteEntity.this.level();
				return super.canUse() && EntityUtils.canAttackAnimals();
			}

			@Override
			public boolean canContinueToUse() {
				double x = ApostleProkaryoteEntity.this.getX();
				double y = ApostleProkaryoteEntity.this.getY();
				double z = ApostleProkaryoteEntity.this.getZ();
				Entity entity = ApostleProkaryoteEntity.this;
				Level world = ApostleProkaryoteEntity.this.level();
				return super.canContinueToUse() && EntityUtils.canAttackAnimals();
			}
		});
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Squid.class, true, false) {
			@Override
			public boolean canUse() {
				double x = ApostleProkaryoteEntity.this.getX();
				double y = ApostleProkaryoteEntity.this.getY();
				double z = ApostleProkaryoteEntity.this.getZ();
				Entity entity = ApostleProkaryoteEntity.this;
				Level world = ApostleProkaryoteEntity.this.level();
				return super.canUse() && EntityUtils.canAttackAnimals();
			}

			@Override
			public boolean canContinueToUse() {
				double x = ApostleProkaryoteEntity.this.getX();
				double y = ApostleProkaryoteEntity.this.getY();
				double z = ApostleProkaryoteEntity.this.getZ();
				Entity entity = ApostleProkaryoteEntity.this;
				Level world = ApostleProkaryoteEntity.this.level();
				return super.canContinueToUse() && EntityUtils.canAttackAnimals();
			}
		});
		this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, TropicalFish.class, true, false) {
			@Override
			public boolean canUse() {
				double x = ApostleProkaryoteEntity.this.getX();
				double y = ApostleProkaryoteEntity.this.getY();
				double z = ApostleProkaryoteEntity.this.getZ();
				Entity entity = ApostleProkaryoteEntity.this;
				Level world = ApostleProkaryoteEntity.this.level();
				return super.canUse() && EntityUtils.canAttackAnimals();
			}

			@Override
			public boolean canContinueToUse() {
				double x = ApostleProkaryoteEntity.this.getX();
				double y = ApostleProkaryoteEntity.this.getY();
				double z = ApostleProkaryoteEntity.this.getZ();
				Entity entity = ApostleProkaryoteEntity.this;
				Level world = ApostleProkaryoteEntity.this.level();
				return super.canContinueToUse() && EntityUtils.canAttackAnimals();
			}
		});
		this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, Salmon.class, true, false) {
			@Override
			public boolean canUse() {
				double x = ApostleProkaryoteEntity.this.getX();
				double y = ApostleProkaryoteEntity.this.getY();
				double z = ApostleProkaryoteEntity.this.getZ();
				Entity entity = ApostleProkaryoteEntity.this;
				Level world = ApostleProkaryoteEntity.this.level();
				return super.canUse() && EntityUtils.canAttackAnimals();
			}

			@Override
			public boolean canContinueToUse() {
				double x = ApostleProkaryoteEntity.this.getX();
				double y = ApostleProkaryoteEntity.this.getY();
				double z = ApostleProkaryoteEntity.this.getZ();
				Entity entity = ApostleProkaryoteEntity.this;
				Level world = ApostleProkaryoteEntity.this.level();
				return super.canContinueToUse() && EntityUtils.canAttackAnimals();
			}
		});
		this.targetSelector.addGoal(8, new NearestAttackableTargetGoal(this, Player.class, true, false) {
			@Override
			public boolean canUse() {
				double x = ApostleProkaryoteEntity.this.getX();
				double y = ApostleProkaryoteEntity.this.getY();
				double z = ApostleProkaryoteEntity.this.getZ();
				Entity entity = ApostleProkaryoteEntity.this;
				Level world = ApostleProkaryoteEntity.this.level();
				return super.canUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
			}

			@Override
			public boolean canContinueToUse() {
				double x = ApostleProkaryoteEntity.this.getX();
				double y = ApostleProkaryoteEntity.this.getY();
				double z = ApostleProkaryoteEntity.this.getZ();
				Entity entity = ApostleProkaryoteEntity.this;
				Level world = ApostleProkaryoteEntity.this.level();
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

	@Override
	public void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.dolphin.swim")), 0.15f, 1);
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
		EntityUtils.initBplusMagic(this);
		return retval;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putBoolean("Datashelled", this.entityData.get(DATA_shelled));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("Datashelled"))
			this.entityData.set(DATA_shelled, compound.getBoolean("Datashelled"));
	}

	@Override
	public void baseTick() {
		super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        if (this != null) {
            boolean found = false;
            double perc = 0;
            if (this.isAlive()) {
                if (!((Entity) this instanceof ApostleProkaryoteEntity _datEntL1 && _datEntL1.getEntityData().get(DATA_shelled))) {
                    for (int dx = -2; dx <= 2; dx++) {
                        for (int dy = -2; dy <= 3; dy++) {
                            for (int dz = -2; dz <= 2; dz++) {
                                if ((world.getBlockState(BlockPos.containing(x + dx, y + dy, z + dz))).getBlock() == CaerulaArborModBlocks.WHITE_CHITIN_BLOCK.get()) {
                                    ((Entity) this).lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((x + dx), (y + dy), (z + dz)));
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
                        perc = ((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) / ((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1);
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
                        if ((Entity) this instanceof LivingEntity _entity)
                            _entity.setHealth((float) (((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * perc));
                        if ((Entity) this instanceof ApostleProkaryoteEntity _datEntSetL)
                            _datEntSetL.getEntityData().set(DATA_shelled, true);
                        if (this instanceof ApostleProkaryoteEntity) {
                            this.setAnimation("animation.apostle.skill");
                        }
                        CaerulaArborMod.queueServerWork(17, () -> {
                            if (world instanceof Level _level) {
                                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.armor.equip_leather")), SoundSource.HOSTILE, 1, 1);
                            }
                        });
                    }
                } else {
                    if ((Entity) this instanceof ApostleProkaryoteEntity animatable)
                        animatable.setTexture("apostle");
                }
            }
        }
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
		SpawnPlacements.register(CaerulaArborModEntities.APOSTLE_PROKARYOTE.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (entityType, world, reason, pos, random) -> {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			return WorldUtils.canSpawnMarineSeaborn(world, x, y, z);
		});
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 1.5);
		builder = builder.add(Attributes.MAX_HEALTH, 50);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 9);
		builder = builder.add(Attributes.FOLLOW_RANGE, 24);
		builder = builder.add(ForgeMod.SWIM_SPEED.get(), 1.5);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

			) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.apostle.move"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.apostle.die"));
			}
			if (this.isInWaterOrBubble()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.apostle.move"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.apostle.idle"));
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
		if (this.swinging && this.lastSwing + 20L <= level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.apostle.attack"));
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
			this.remove(ApostleProkaryoteEntity.RemovalReason.KILLED);
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
}
