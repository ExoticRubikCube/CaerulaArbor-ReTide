package com.susen36.caerulaarbor.entity.crawler;


import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.base.RangedSanityAttacker;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CASounds;
import com.susen36.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementTypes;
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
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import software.bernie.geckolib.animation.*;

public class PocketSeaCrawlerEntity extends SeaMonster implements RangedSanityAttacker {
	public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(PocketSeaCrawlerEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(PocketSeaCrawlerEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_DEAL = SynchedEntityData.defineId(PocketSeaCrawlerEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Boolean> DATA_CHARGED = SynchedEntityData.defineId(PocketSeaCrawlerEntity.class, EntityDataSerializers.BOOLEAN);
	private boolean swinging;
	private long lastSwing;
	public String animationprocedure = "empty";

	//public PocketSeaCrawlerEntity(Level world) {
	//	this(CAEntities.POCKET_SEA_CREEPER.get(), world);
	//}

	public PocketSeaCrawlerEntity(EntityType<PocketSeaCrawlerEntity> type, Level world) {
		super(type, world);
		xpReward = 8;
		setNoAi(false);
		this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.2f);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_SHOOT, false);
		builder.define(DATA_ANIMATION, "undefined");
		builder.define(DATA_DEAL, 0);
		builder.define(DATA_CHARGED, false);
	}

	public boolean charged() {
		return this.entityData.get(DATA_CHARGED);
	}

	public void setCharged() {
		this.entityData.set(DATA_CHARGED, true);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1, true));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Cat.class, false, false));
		this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
		this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1));
		this.goalSelector.addGoal(5, new MoveBackToVillageGoal(this, 0.6, false));
		this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
	}

	@Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
		super.dropCustomDeathLoot(level, damageSource, recentlyHit);
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
		if (this.charged() && (source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE)))
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
		compound.putBoolean("Charged", this.entityData.get(DATA_CHARGED));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Deal")) {
		    this.entityData.set(DATA_DEAL, compound.getInt("Deal"));
		}
		if (compound.contains("Charged")) {
			this.entityData.set(DATA_CHARGED, compound.getBoolean("Charged"));
		}
	}

	@Override
	public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
		ItemStack itemstack = sourceentity.getItemInHand(hand);
		super.mobInteract(sourceentity, hand);
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		PocketSeaCrawlerEntity entity = this;
		Level world = this.level();
        if (itemstack.getItem() == Items.FLINT_AND_STEEL) {
            if ((LevelAccessor) world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.CREEPER_PRIMED, SoundSource.HOSTILE, 2, 1);
            }
            if (entity instanceof PocketSeaCrawlerEntity) {
                entity.setAnimation("animation.pocket_sea_creeper.jump");
            }
            new Object() {
                void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                    PocketSeaCrawlerEntity.this.performRangedSanityAttack();
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



	public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
		event.register(CAEntities.POCKET_SEA_CREEPER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (entityType, world, reason, pos, random) -> {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			return WorldUtils.canRareSeabornSpawn(world, x, y, z);
		}, RegisterSpawnPlacementsEvent.Operation.REPLACE);
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

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) && !this.isVehicle() && !this.isAggressive() && !this.isSprinting()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.pocket_sea_creeper.move"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.pocket_sea_creeper.die"));
			}
			if (this.isShiftKeyDown()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.pocket_sea_creeper.move"));
			}
			if (this.isSprinting()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.pocket_sea_creeper.move"));
			}
			if (this.isVehicle() && event.isMoving()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.pocket_sea_creeper.move"));
			}
			if (this.isAggressive() && event.isMoving() && !this.isVehicle()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.pocket_sea_creeper.move"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.pocket_sea_creeper.idle"));
		}
		return PlayState.STOP;
	}

	private PlayState attackingPredicate(AnimationState event) {
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
			this.swinging = true;
			this.lastSwing = level().getGameTime();
		}
		if (this.swinging && this.lastSwing + 19L <= level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.pocket_sea_creeper.attack"));
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
			this.remove(RemovalReason.KILLED);
			this.dropExperience(this.getKillCredit());
		}
	}

	public String getSyncedAnimation() {
		return this.entityData.get(DATA_ANIMATION);
	}

	public void setAnimation(String animation) {
		this.entityData.set(DATA_ANIMATION, animation);
	}

	public void thunderHit(ServerLevel serverWorld, LightningBolt lightningBolt) {
		super.thunderHit(serverWorld, lightningBolt);
		this.setCharged();
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