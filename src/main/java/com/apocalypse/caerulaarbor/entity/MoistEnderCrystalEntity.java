package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.entity.base.SyncedAnimationEntity;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAItems;
import com.apocalypse.caerulaarbor.init.CAParticles;
import com.apocalypse.caerulaarbor.init.CASounds;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
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
import java.util.Comparator;
import java.util.List;

public class MoistEnderCrystalEntity extends PathfinderMob implements GeoEntity, SyncedAnimationEntity {
	public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(MoistEnderCrystalEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(MoistEnderCrystalEntity.class, EntityDataSerializers.STRING);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	public String animationprocedure = "empty";
	public boolean IS_STATIC = false;

	public MoistEnderCrystalEntity(Level world) {
		this(CAEntities.MOIST_ENDER_CRYSTAL.get(), world);
	}

	public MoistEnderCrystalEntity(EntityType<MoistEnderCrystalEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);
		setMaxUpStep(0.6f);
		setPersistenceRequired();
		this.moveControl = new FlyingMoveControl(this, 10, true);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_SHOOT, false);
		this.entityData.define(DATA_ANIMATION, "undefined");
	}


	@Override
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
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return SoundEvents.GLASS_HIT;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.GLASS_BREAK;
	}

	@Override
	public boolean causeFallDamage(float l, float d, DamageSource source) {
		return false;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("isStatic", this.IS_STATIC);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("isStatic"))
			this.IS_STATIC = compound.getBoolean("isStatic");
	}

	@Override
	public void baseTick() {
		super.baseTick();
		if (this.IS_STATIC) this.setDeltaMovement(Vec3.ZERO);
		if (!this.isAlive()) {
			setDeltaMovement(new Vec3(0, 0, 0));
		} else {
			if (tickCount < 5) {
				setDeltaMovement(new Vec3(0, 0, 0));
			}
			clearFire();
			setAirSupply(20);
		}
		this.refreshDimensions();
	}

	@Override
	public void tick() {
		this.noPhysics = true;
		super.tick();
		this.noPhysics = false;
		this.setNoGravity(true);
	}

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand pHand) {
		if (!this.IS_STATIC) return InteractionResult.PASS;
		Level level = this.level();
		if (level instanceof ServerLevel sLevel) {
			if (player.getMainHandItem().isEmpty()) {
				ItemEntity item = new ItemEntity(sLevel, this.getX(), this.getY(), this.getZ(),
						new ItemStack(CAItems.MOIST_CRYSTAL_ITEM.get()));
				item.setPickUpDelay(10);
				item.setUnlimitedLifetime();
				sLevel.addFreshEntity(item);
				this.discard();
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	public void move(MoverType pType, Vec3 pPos) {
		super.move(pType, pPos);
		this.checkInsideBlocks();
	}

	

	@Override
	protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
	}

	@Override
	public void setNoGravity(boolean ignored) {
		super.setNoGravity(true);
	}

	public void aiStep() {
		super.aiStep();
		this.setNoGravity(true);
	}


	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
		this.setDeltaMovement(Vec3.ZERO);
		return retval;
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.45);
		builder = builder.add(Attributes.MAX_HEALTH, 40);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 32);
		builder = builder.add(Attributes.FOLLOW_RANGE, 16);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
		builder = builder.add(Attributes.FLYING_SPEED, 0.45);
		return builder;
	}

	private PlayState movementPredicate(AnimationState<?> event) {
		if (this.animationprocedure.equals("empty")) {
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.moist_crystal.die"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.moist_crystal.idle"));
		}
		return PlayState.STOP;
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
			this.remove(RemovalReason.KILLED);
			this.dropExperience();
			LevelAccessor world = this.level();
			double x = this.getX();
			double y = this.getY();
			double z = this.getZ();
			double d;
			Entity enderina;
			if (world.isClientSide()) {
				return;
			}
			if (world instanceof Level _level) {
				_level.playSound(null, BlockPos.containing(x, y, z), CASounds.CASTER_CRYSTAL_EXPLODE.get(), SoundSource.NEUTRAL, 3, 1);
			}
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.EXPLOSION, x, (y + 0.5), z, 4, 0.5, 0.5, 0.5, 0.1);
			if (world instanceof ServerLevel _level)
				_level.sendParticles(CAParticles.EDERMAN_PTC.get(), x, (y + 0.5), z, 32, 1, 1, 1, 0.18);
			d = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
			if (d > 0) {
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(8 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (!(entityiterator instanceof LivingEntity)) {
							continue;
						}
						if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
							continue;
						}
						if (distanceTo(entityiterator) <= 4) {
							entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.EXPLOSION), this), (float) d);
						}
					}
				}
			}
			enderina = world.getEntitiesOfClass(OceanizedEnderinaEntity.class, AABB.ofSize(new Vec3(x, y, z), 64, 64, 64), e -> true).stream().min(new Object() {
				Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
					return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
				}
			}.compareDistOf(x, y, z)).orElse(null);
			if (enderina == null) {
				return;
			}
			if ((enderina instanceof OceanizedEnderinaEntity _datEntI ? _datEntI.getEntityData().get(OceanizedEnderinaEntity.DATA_REVIVE_TICK) : 0) > 0) {
				enderina.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hand_of_choker")))),
						(float) ((enderina instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) * 0.25));
			} else if (enderina instanceof LivingEntity livingEntity) {
				EntityUtils.heal(livingEntity, (livingEntity.getMaxHealth()) * 0.05);
				ModCapabilities.getSanityInjury(livingEntity).heal(1000);
			}
		}
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	protected void doPush(Entity entityIn) {
	}

	@Override
	protected void pushEntities() {
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
		data.add(new AnimationController<>(this, "procedure", 1, this::procedurePredicate));
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
