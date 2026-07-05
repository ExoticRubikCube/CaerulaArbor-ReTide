package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.RangedSanityAttacker;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAItems;
import com.apocalypse.caerulaarbor.init.CASounds;
import com.apocalypse.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveBackToVillageGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class CreeperFishEntity extends SeaMonster implements RangedSanityAttacker {
	public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(CreeperFishEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(CreeperFishEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_DEAL = SynchedEntityData.defineId(CreeperFishEntity.class, EntityDataSerializers.INT);
	private boolean swinging;
	private long lastSwing;
	public String animationprocedure = "empty";

	public CreeperFishEntity(Level world) {
		this(CAEntities.CREEPER_FISH.get(), world);
	}

	public CreeperFishEntity(EntityType<CreeperFishEntity> type, Level world) {
		super(type, world);
		xpReward = 8;
		setNoAi(false);
		setMaxUpStep(1.2f);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_SHOOT, false);
		this.entityData.define(DATA_ANIMATION, "undefined");
		this.entityData.define(DATA_DEAL, 0);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1, true) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 0;
			}
		});
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Cat.class, false, false));
		this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
		this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1));
		this.goalSelector.addGoal(5, new MoveBackToVillageGoal(this, 0.6, false));
		this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
	}

	@Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHitIn) {
		super.dropCustomDeathLoot(source, looting, recentlyHitIn);
		this.spawnAtLocation(new ItemStack(CAItems.OCEAN_CRYSTAL.get()));
	}

	@Override
	public SoundEvent getAmbientSound() {
		return SoundEvents.TROPICAL_FISH_AMBIENT;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return SoundEvents.CREEPER_HURT;
	}

	@Override
	public SoundEvent getDeathSound() {
		return CASounds.SEABORN_DEATH.get();
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.DROWN))
			return false;
		float healthBeforeDamage = this.getHealth();
		boolean damaged = super.hurt(source, amount);
		if (damaged && amount <= healthBeforeDamage) {
			double accumulatedDamage = this.getEntityData().get(DATA_DEAL) + amount;
			this.getEntityData().set(DATA_DEAL, (int) accumulatedDamage);
			if (accumulatedDamage >= this.getMaxHealth() * 0.15) {
				this.performRangedSanityAttack();
				this.getEntityData().set(DATA_DEAL, 0);
			}
		}
		return damaged;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("Deal", this.entityData.get(DATA_DEAL));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Deal")) {
		    this.entityData.set(DATA_DEAL, compound.getInt("Deal"));
		}
	}

	@Override
	public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
		ItemStack itemstack = sourceentity.getItemInHand(hand);
		super.mobInteract(sourceentity, hand);
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		CreeperFishEntity entity = this;
		Level world = this.level();
        if (itemstack.getItem() == Items.FLINT_AND_STEEL) {
            if ((LevelAccessor) world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.CREEPER_PRIMED, SoundSource.HOSTILE, 2, 1);
            }
            if (entity instanceof CreeperFishEntity) {
                entity.setAnimation("animation.explosivefish.jump");
            }
            new Object() {
                void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                    CreeperFishEntity.this.performRangedSanityAttack();
                    final int tick2 = ticks;
                    CaerulaArborMod.queueServerWork(tick2, () -> {
                        if (timedlooptotal > timedloopiterator + 1) {
                            timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                        }
                    });
                }
            }.timedLoop(0, 5, 4);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

	@Override
	public void baseTick() {
		super.baseTick();
		this.refreshDimensions();
	}

	

	public static void registerSpawnPlacements() {
		SpawnPlacements.register(CAEntities.CREEPER_FISH.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (entityType, world, reason, pos, random) -> {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			return WorldUtils.canRareSeabornSpawn(world, x, y, z);
		});
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.25);
		builder = builder.add(Attributes.MAX_HEALTH, 85);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 2);
		builder = builder.add(Attributes.FOLLOW_RANGE, 24);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.85);
		return builder;
	}

	private PlayState movementPredicate(AnimationState<?> event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) && !this.isVehicle() && !this.isAggressive() && !this.isSprinting()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.explosivefish.move"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.explosivefish.die"));
			}
			if (this.isShiftKeyDown()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.explosivefish.move"));
			}
			if (this.isSprinting()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.explosivefish.move"));
			}
			if (this.isVehicle() && event.isMoving()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.explosivefish.move"));
			}
			if (this.isAggressive() && event.isMoving() && !this.isVehicle()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.explosivefish.move"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.explosivefish.idle"));
		}
		return PlayState.STOP;
	}

	private PlayState attackingPredicate(AnimationState<?> event) {
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
			this.swinging = true;
			this.lastSwing = level().getGameTime();
		}
		if (this.swinging && this.lastSwing + 19L <= level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.explosivefish.attack"));
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
			this.remove(CreeperFishEntity.RemovalReason.KILLED);
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
		data.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
		data.add(new AnimationController<>(this, "attacking", 0, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
	}


	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}
}

