package com.susen36.caerulaarbor.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.base.SyncedAnimationEntity;
import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class ReaperPetEntity extends TamableAnimal implements GeoEntity, SyncedAnimationEntity {
	public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(ReaperPetEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(ReaperPetEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_STATE = SynchedEntityData.defineId(ReaperPetEntity.class, EntityDataSerializers.INT);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private long lastSwing;
	public String animationprocedure = "empty";

	public ReaperPetEntity(Level world) {
		this(CAEntities.REAPER_PET.get(), world);
	}

	public ReaperPetEntity(EntityType<ReaperPetEntity> type, Level world) {
		super(type, world);
		xpReward = 8;
		setNoAi(false);
		this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1f);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_SHOOT, false);
		builder.define(DATA_ANIMATION, "undefined");
		builder.define(DATA_STATE, 0);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new OwnerHurtByTargetGoal(this) {
			@Override
			public boolean canUse() {
				return super.canUse() && ReaperPetEntity.this.isFollowable();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && ReaperPetEntity.this.isFollowable();
			}
		});
		this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this) {
			@Override
			public boolean canUse() {
				return super.canUse() && ReaperPetEntity.this.isFollowable();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && ReaperPetEntity.this.isFollowable();
			}
		});
		this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.5, (float) 4, (float) 16) {
			@Override
			public boolean canUse() {
				return super.canUse() && ReaperPetEntity.this.isFollowable();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && ReaperPetEntity.this.isFollowable();
			}
		});
		this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 2, false));
		this.goalSelector.addGoal(5, new OpenDoorGoal(this, false));
		this.goalSelector.addGoal(6, new OpenDoorGoal(this, true));
		this.goalSelector.addGoal(7, new TemptGoal(this, 0.4, Ingredient.of(CAItems.OCEAN_EYE.get()), false) {
			@Override
			public boolean canUse() {
				return super.canUse() && ReaperPetEntity.this.isMovable();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && ReaperPetEntity.this.isMovable();
			}
		});
		this.targetSelector.addGoal(8, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(9, new RandomStrollGoal(this, 1) {
			@Override
			public boolean canUse() {
				return super.canUse() && ReaperPetEntity.this.isMovable();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && ReaperPetEntity.this.isMovable();
			}
		});
		this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(11, new FloatGoal(this));
	}

	protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
		super.dropCustomDeathLoot(level, damageSource, recentlyHit);
		this.spawnAtLocation(new ItemStack(CAItems.BASE_EGG.get()));
	}

	@Override
	public SoundEvent getAmbientSound() {
		return SoundEvents.PUFFER_FISH_AMBIENT;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return SoundEvents.PUFFER_FISH_HURT;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.PUFFER_FISH_DEATH;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		this.getEntityData().set(DATA_STATE, 0);
		if (source.is(DamageTypes.DROWN))
			return false;
		return super.hurt(source, amount);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("State", this.entityData.get(DATA_STATE));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("State")) {
		    this.entityData.set(DATA_STATE, compound.getInt("State"));
		}
	}

	@Override
	public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
		ItemStack itemstack = sourceentity.getItemInHand(hand);
		this.level().isClientSide();
		InteractionResult retval;
		Item item = itemstack.getItem();
		if (itemstack.getItem() instanceof SpawnEggItem) {
			super.mobInteract(sourceentity, hand);
		} else if (this.level().isClientSide()) {
			if ((this.isTame() && this.isOwnedBy(sourceentity) || this.isFood(itemstack))) {
				this.level().isClientSide();
			}
		} else {
			if (this.isTame()) {
				if (this.isOwnedBy(sourceentity)) {
					if (itemstack.getComponents().has(DataComponents.FOOD) && this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
						this.usePlayerItem(sourceentity, hand, itemstack);
						this.heal((float) item.getFoodProperties(itemstack, this).nutrition());
					} else if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
						this.usePlayerItem(sourceentity, hand, itemstack);
						this.heal(4);
					} else {
						super.mobInteract(sourceentity, hand);
					}
				}
			} else if (this.isFood(itemstack)) {
				this.usePlayerItem(sourceentity, hand, itemstack);
				if (this.random.nextInt(3) == 0 && !EventHooks.onAnimalTame(this, sourceentity)) {
					this.tame(sourceentity);
					this.level().broadcastEntityEvent(this, (byte) 7);
				} else {
					this.level().broadcastEntityEvent(this, (byte) 6);
				}
				this.setPersistenceRequired();
			} else {
				retval = super.mobInteract(sourceentity, hand);
				if (retval == InteractionResult.SUCCESS || retval == InteractionResult.CONSUME)
					this.setPersistenceRequired();
			}
		}
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		Level world = this.level();
		if (((Entity) this instanceof TamableAnimal tamEnt ? (Entity) tamEnt.getOwner() : null) == sourceentity) {
			if (((Entity) sourceentity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Blocks.AIR.asItem()
					&& ((Entity) sourceentity instanceof LivingEntity livEnt ? livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == Blocks.AIR.asItem()) {
				if ((LevelAccessor) world instanceof ServerLevel level)
					level.sendParticles(ParticleTypes.HEART, x, y, z, 4, 0.8, 0.5, 0.8, 0.3);
				this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((sourceentity.getX()), (sourceentity.getY()), (sourceentity.getZ())));
                this.getNavigation().stop();
				if (this instanceof ReaperPetEntity) {
					this.setAnimation("animation.reaperpet.interact");
				}
				return InteractionResult.SUCCESS;
			} else if (((Entity) sourceentity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).is(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "fish_food")))) {
				if ((Entity) this instanceof LivingEntity entity)
					entity.setHealth((Entity) this instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1);
				((Entity) sourceentity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
				if ((LevelAccessor) world instanceof Level level) {
					level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.DOLPHIN_EAT, SoundSource.NEUTRAL, 1, 1);
				}
				return InteractionResult.SUCCESS;
			} else if (((Entity) sourceentity instanceof LivingEntity livEnt ? livEnt.getOffhandItem() : ItemStack.EMPTY).is(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "fish_food")))) {
				if ((Entity) this instanceof LivingEntity entity && !this.level().isClientSide())
					this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
				((Entity) sourceentity instanceof LivingEntity livEnt ? livEnt.getOffhandItem() : ItemStack.EMPTY).shrink(1);
				if ((LevelAccessor) world instanceof Level level) {
					level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.DOLPHIN_EAT, SoundSource.NEUTRAL, 1, 1);
				}
				return InteractionResult.SUCCESS;
			} else if (((Entity) sourceentity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Items.STICK) {
				if (sourceentity.isShiftKeyDown()) {
					if ((Entity) this instanceof ReaperPetEntity datEntSetI)
						datEntSetI.getEntityData().set(DATA_STATE, 2);
					if ((Entity) sourceentity instanceof Player player && !player.level().isClientSide())
						player.displayClientMessage(Component.literal((this.getDisplayName().getString() + Component.translatable("item.caerula_arbor.a_second_key.description_2").getString())), true);
				} else {
					if (((Entity) this instanceof ReaperPetEntity datEntI ? datEntI.getEntityData().get(DATA_STATE) : 0) == 0) {
						if ((Entity) this instanceof ReaperPetEntity datEntSetI)
							datEntSetI.getEntityData().set(DATA_STATE, 1);
						if ((Entity) sourceentity instanceof Player player && !player.level().isClientSide())
							player.displayClientMessage(Component.literal((this.getDisplayName().getString() + Component.translatable("item.caerula_arbor.a_second_key.description_1").getString())), true);
					} else {
						if ((Entity) this instanceof ReaperPetEntity datEntSetI)
							datEntSetI.getEntityData().set(DATA_STATE, 0);
						if ((Entity) sourceentity instanceof Player player && !player.level().isClientSide())
							player.displayClientMessage(Component.literal((this.getDisplayName().getString() + Component.translatable("item.caerula_arbor.a_second_key.description_0").getString())), true);
					}
				}
				return InteractionResult.SUCCESS;
			}
			return InteractionResult.PASS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public void baseTick() {
		super.baseTick();
		Entity owner;
		Entity target;
		if ((Entity) this instanceof Mob mobEnt0 && mobEnt0.isAggressive() && !((Entity) this instanceof LivingEntity livEnt1 && livEnt1.hasEffect(CAMobEffects.PET_REAP))) {
			if (!this.level().isClientSide())
				this.addEffect(new MobEffectInstance(CAMobEffects.PET_REAP, 100, 0, false, false));
		}
		if (((Entity) this instanceof ReaperPetEntity datEntI ? datEntI.getEntityData().get(DATA_STATE) : 0) == 2) {
			if ((Entity) this instanceof Mob entity)
				entity.setTarget(null);
		}
		owner = (Entity) this instanceof TamableAnimal tamEnt ? tamEnt.getOwner() : null;
		target = (Entity) this instanceof Mob mobEnt ? mobEnt.getTarget() : null;
		if (target == owner || (target instanceof TamableAnimal tamEnt ? (Entity) tamEnt.getOwner() : null) == owner) {
			if ((Entity) this instanceof Mob entity)
				entity.setTarget(null);
		}
		this.refreshDimensions();
	}

	

	@Override
	public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageable) {
		ReaperPetEntity retval = CAEntities.REAPER_PET.get().create(serverWorld);
		retval.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(retval.blockPosition()), MobSpawnType.BREEDING, null);;
		return retval;
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return List.of(Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.COOKED_COD, Items.COOKED_SALMON).contains(stack.getItem());
	}

	@Override
	public void aiStep() {
		super.aiStep();
		this.updateSwingTime();
	}

	

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.24);
		builder = builder.add(Attributes.MAX_HEALTH, 60);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 11);
		builder = builder.add(Attributes.FOLLOW_RANGE, 24);
		builder = builder.add(CAAttributes.MAGIC_RESISTANCE, 30);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

					&& !this.isAggressive()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.reaperpet.move"));
			}
			if (this.isAggressive() && event.isMoving()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.reaperpet.sprint"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.reaperpet.idle"));
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
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.reaperpet.attack"));
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

	public boolean isFollowable() {
		return this.entityData.get(DATA_STATE) == 0;
	}

	public boolean isMovable() {
		return this.entityData.get(DATA_STATE) != 2;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		data.add(new AnimationController<>(this, "movement", 3, this::movementPredicate));
		data.add(new AnimationController<>(this, "attacking", 3, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 3, this::procedurePredicate));
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