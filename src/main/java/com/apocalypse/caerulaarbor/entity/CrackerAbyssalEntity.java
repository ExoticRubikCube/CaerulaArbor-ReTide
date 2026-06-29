package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.PolarMountRider;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.init.CABlocks;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import com.apocalypse.caerulaarbor.util.WorldUtils;
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
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

public class CrackerAbyssalEntity extends SeaMonster implements PolarMountRider {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(CrackerAbyssalEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(CrackerAbyssalEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(CrackerAbyssalEntity.class, EntityDataSerializers.STRING);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public CrackerAbyssalEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CAEntities.CRACKER_ABYSSAL.get(), world);
	}

	public CrackerAbyssalEntity(EntityType<CrackerAbyssalEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);
		setMaxUpStep(1.5f);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "reefbreaker");
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
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(2, new BreakDoorGoal(this, e -> true) {
			@Override
			public boolean canUse() {
				Level world = CrackerAbyssalEntity.this.level();
				return super.canUse() && WorldUtils.canGrief(world);
			}

			@Override
			public boolean canContinueToUse() {
				Level world = CrackerAbyssalEntity.this.level();
				return super.canContinueToUse() && WorldUtils.canGrief(world);
			}
		});
		this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1, true) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 6.25;
			}
		});
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolem.class, true, false));
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, SnowGolem.class, true, false));
		this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Villager.class, true, false));
		this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, Illusioner.class, true, false));
		this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, Pillager.class, true, false));
		this.targetSelector.addGoal(9, new NearestAttackableTargetGoal<>(this, Vindicator.class, true, false));
		this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, Witch.class, true, false));
		this.targetSelector.addGoal(11, new NearestAttackableTargetGoal<>(this, Piglin.class, true, false));
		this.targetSelector.addGoal(12, new NearestAttackableTargetGoal<>(this, PiglinBrute.class, true, false));
		this.targetSelector.addGoal(13, new NearestAttackableTargetGoal<>(this, ZombifiedPiglin.class, true, false));
		this.targetSelector.addGoal(14, new NearestAttackableTargetGoal<>(this, Player.class, true, false) {
			@Override
			public boolean canUse() {
				double x = CrackerAbyssalEntity.this.getX();
				double y = CrackerAbyssalEntity.this.getY();
				double z = CrackerAbyssalEntity.this.getZ();
				Level world = CrackerAbyssalEntity.this.level();
				return super.canUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
			}

			@Override
			public boolean canContinueToUse() {
				double x = CrackerAbyssalEntity.this.getX();
				double y = CrackerAbyssalEntity.this.getY();
				double z = CrackerAbyssalEntity.this.getZ();
				Entity entity = CrackerAbyssalEntity.this;
				Level world = CrackerAbyssalEntity.this.level();
				return super.canContinueToUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
			}
		});
		this.targetSelector.addGoal(15, new NearestAttackableTargetGoal<>(this, Animal.class, true, false) {
			@Override
			public boolean canUse() {
				return super.canUse() && EntityUtils.canAttackAnimals();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && EntityUtils.canAttackAnimals();
			}
		});
		this.goalSelector.addGoal(16, new RandomStrollGoal(this, 0.8));
		this.goalSelector.addGoal(17, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(18, new FloatGoal(this));
	}

    @Override
	public SoundEvent getAmbientSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.parrot.imitate.silverfish"));
	}

	@Override
	public void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.silverfish.step")), 0.15f, 1);
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "seaborn_generic_hit"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.phantom.death"));
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		double targetX = target.getX();
		double targetY = target.getY();
		double targetZ = target.getZ();
		if (!this.level().isClientSide()) {
			this.level().playSound(null, BlockPos.containing(targetX, targetY, targetZ),
					ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "reefbreaker_attack")), SoundSource.HOSTILE, 10,
					(float) Mth.nextDouble(RandomSource.create(), 0.85, 1.15));
			double amplifier = this.hasEffect(CAMobEffects.REEF_CRACKER.get()) ? this.getEffect(CAMobEffects.REEF_CRACKER.get()).getAmplifier() : -1;
			int nextAmplifier = amplifier < 0 ? 0 : Math.min((int)amplifier + 1, 14);
			this.addEffect(new MobEffectInstance(CAMobEffects.REEF_CRACKER.get(), 120, nextAmplifier, false, false));
			CaerulaArborMod.queueServerWork(12, () -> {
				if (this.isAlive() && target.isAlive() && this.distanceTo(target) <= 3) {
					target.hurt(
							new DamageSource(
									this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
											.getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "general_seaborn_attack"))),
									this),
							(float) (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0));
				}
			});
		}
		return true;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity sourceentity = source.getEntity();
        if (sourceentity != null) {
            double num;
            if (this.isAlive() && !this.hasEffect(CAMobEffects.COOLDOWN_SINAL.get())) {
				num = 0;
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(6 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (!(entityiterator == this) && (entityiterator instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) >= 10) {
							num = num + 1;
						}
					}
				}
				if (num >= 2 && distanceTo(sourceentity) <= 4) {
					if (this instanceof CrackerAbyssalEntity) {
						this.setAnimation("animation.nethersea_reefbreaker.spin");
					}
					if (!this.level().isClientSide())
						this.addEffect(new MobEffectInstance(CAMobEffects.COOLDOWN_SINAL.get(), 40, 0, false, false));
					CaerulaArborMod.queueServerWork(10, () -> {
						if (world instanceof Level _level) {
							_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.player.attack.sweep")), SoundSource.HOSTILE, 2, 1);
						}
						final Vec3 _center = new Vec3(x, y, z);
						List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(6 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
						for (Entity entityiterator : _entfound) {
							if ((!entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring"))) || ((Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) == entityiterator)
									&& (entityiterator instanceof Mob || entityiterator instanceof Player)) {
								if (distanceTo(entityiterator) <= 3) {
									entityiterator.hurt(new DamageSource(
													world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "general_seaborn_attack"))), this),
											(float) ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)
													? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue()
													: 0) * 1.5));
								}
							}
						}
					});
				}
			}
        }
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
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == CABlocks.SEA_TRAIL_STOP.get() || (world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == CABlocks.SEA_TRAIL_GROWN.get()) {
            if ((this.hasEffect(MobEffects.INVISIBILITY) ? this.getEffect(MobEffects.INVISIBILITY).getDuration() : 0) <= 5) {
                if (!this.level().isClientSide())
                    this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 20, 0));
            }
            if (world instanceof ServerLevel _level)
                _level.sendParticles(ParticleTypes.SMOKE, x, (y + 1), z, 4, 0.4, 2, 0.4, 0.01);
        }
        this.refreshDimensions();
	}

	@Override
	public EntityDimensions getDimensions(Pose p_33597_) {
		return super.getDimensions(p_33597_).scale((float) 1);
	}

	public static void init() {
		SpawnPlacements.register(CAEntities.CRACKER_ABYSSAL.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (entityType, world, reason, pos, random) -> {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			return WorldUtils.canDangerSeabornSpawn(world, x, y, z);
		});
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.27);
		builder = builder.add(Attributes.MAX_HEALTH, 85);
		builder = builder.add(Attributes.ARMOR, 10);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 13);
		builder = builder.add(Attributes.FOLLOW_RANGE, 32);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.85);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

			) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.nethersea_reefbreaker.move"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.nethersea_reefbreaker.die"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.nethersea_reefbreaker.idle"));
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
		if (this.swinging && this.lastSwing + 19L <= level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.nethersea_reefbreaker.attack"));
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
			this.remove(CrackerAbyssalEntity.RemovalReason.KILLED);
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
		data.add(new AnimationController<>(this, "attacking", 0, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
	}
}
