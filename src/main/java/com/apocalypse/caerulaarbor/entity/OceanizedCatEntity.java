package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;

import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;

public class OceanizedCatEntity extends SeaMonster {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(OceanizedCatEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(OceanizedCatEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(OceanizedCatEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_action_time = SynchedEntityData.defineId(OceanizedCatEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Boolean> DATA_stateSneaking = SynchedEntityData.defineId(OceanizedCatEntity.class, EntityDataSerializers.BOOLEAN);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public OceanizedCatEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CaerulaArborModEntities.OCEANIZED_CAT.get(), world);
	}

	public OceanizedCatEntity(EntityType<OceanizedCatEntity> type, Level world) {
		super(type, world);
		xpReward = 4;
		setNoAi(false);
		setMaxUpStep(0.6f);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "oceanized_cat");
		this.entityData.define(DATA_action_time, 0);
		this.entityData.define(DATA_stateSneaking, false);
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
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.6, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 3.24;
			}
		});
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Chicken.class, true, false));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, Rabbit.class, true, false));
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Cat.class, true, false));
		this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, IronGolem.class, true, false));
		this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, SnowGolem.class, true, false));
		this.targetSelector.addGoal(8, new NearestAttackableTargetGoal(this, Villager.class, true, false));
		this.targetSelector.addGoal(9, new NearestAttackableTargetGoal(this, Illusioner.class, true, false));
		this.targetSelector.addGoal(10, new NearestAttackableTargetGoal(this, Pillager.class, true, false));
		this.targetSelector.addGoal(11, new NearestAttackableTargetGoal(this, Vindicator.class, true, false));
		this.targetSelector.addGoal(12, new NearestAttackableTargetGoal(this, Witch.class, true, false));
		this.targetSelector.addGoal(13, new NearestAttackableTargetGoal(this, Piglin.class, true, false));
		this.targetSelector.addGoal(14, new NearestAttackableTargetGoal(this, PiglinBrute.class, true, false));
		this.targetSelector.addGoal(15, new NearestAttackableTargetGoal(this, ZombifiedPiglin.class, true, false));
		this.targetSelector.addGoal(16, new NearestAttackableTargetGoal(this, Player.class, true, false) {
			@Override
			public boolean canUse() {
				double x = OceanizedCatEntity.this.getX();
				double y = OceanizedCatEntity.this.getY();
				double z = OceanizedCatEntity.this.getZ();
				Entity entity = OceanizedCatEntity.this;
				Level world = OceanizedCatEntity.this.level();
				return super.canUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
			}

			@Override
			public boolean canContinueToUse() {
				double x = OceanizedCatEntity.this.getX();
				double y = OceanizedCatEntity.this.getY();
				double z = OceanizedCatEntity.this.getZ();
				Entity entity = OceanizedCatEntity.this;
				Level world = OceanizedCatEntity.this.level();
				return super.canContinueToUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
			}
		});
		this.targetSelector.addGoal(17, new NearestAttackableTargetGoal(this, Animal.class, true, false) {
			@Override
			public boolean canUse() {
				double x = OceanizedCatEntity.this.getX();
				double y = OceanizedCatEntity.this.getY();
				double z = OceanizedCatEntity.this.getZ();
				Entity entity = OceanizedCatEntity.this;
				Level world = OceanizedCatEntity.this.level();
				return super.canUse() && EntityUtils.canAttackAnimals();
			}

			@Override
			public boolean canContinueToUse() {
				double x = OceanizedCatEntity.this.getX();
				double y = OceanizedCatEntity.this.getY();
				double z = OceanizedCatEntity.this.getZ();
				Entity entity = OceanizedCatEntity.this;
				Level world = OceanizedCatEntity.this.level();
				return super.canContinueToUse() && EntityUtils.canAttackAnimals();
			}
		});
		this.goalSelector.addGoal(18, new RandomStrollGoal(this, 1));
		this.goalSelector.addGoal(19, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(20, new FloatGoal(this));
	}

	@Override
	public MobType getMobType() {
		return MobType.WATER;
	}

	@Override
	public SoundEvent getAmbientSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.cat.ambient"));
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.cat.hurt"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.cat.death"));
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		double targetX = target.getX();
		double targetY = target.getY();
		double targetZ = target.getZ();
		if (!this.level().isClientSide()) {
			this.level().playSound(null, BlockPos.containing(targetX, targetY, targetZ),
					ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.cat.hiss")), SoundSource.HOSTILE, 0.75F,
					(float) Mth.nextDouble(RandomSource.create(), 0.85, 1.15));
			CaerulaArborMod.queueServerWork(9, () -> {
				if (this.isAlive() && target.isAlive() && this.distanceTo(target) <= 2) {
					target.hurt(
							new DamageSource(
									this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
											.getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "general_seaborn_attack"))),
									this),
							(float) (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0));
				}
			});
			CaerulaArborMod.queueServerWork(14, () -> {
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
        if (this != null) {
            setShiftKeyDown(false);
            if ((Entity) this instanceof OceanizedCatEntity _datEntSetL)
                _datEntSetL.getEntityData().set(DATA_stateSneaking, false);
        }
        if (source.is(DamageTypes.FALL))
			return false;
		if (source.is(DamageTypes.DROWN))
			return false;
		return super.hurt(source, amount);
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
		EntityUtils.initCatSanity(this);
		return retval;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putInt("Dataaction_time", this.entityData.get(DATA_action_time));
		compound.putBoolean("DatastateSneaking", this.entityData.get(DATA_stateSneaking));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("Dataaction_time"))
			this.entityData.set(DATA_action_time, compound.getInt("Dataaction_time"));
		if (compound.contains("DatastateSneaking"))
			this.entityData.set(DATA_stateSneaking, compound.getBoolean("DatastateSneaking"));
	}

	@Override
	public void baseTick() {
		super.baseTick();
        if (this != null) {
            double time_stamp = 0;
            boolean sneak = false;
            if (this.isAlive() && tickCount % 10 == 0) {
                time_stamp = (Entity) this instanceof OceanizedCatEntity _datEntI ? _datEntI.getEntityData().get(DATA_action_time) : 0;
                sneak = (Entity) this instanceof OceanizedCatEntity _datEntL3 && _datEntL3.getEntityData().get(DATA_stateSneaking);
                if (time_stamp > 0) {
                    if ((Entity) this instanceof OceanizedCatEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_action_time, (int) (time_stamp - 1));
                } else if (Math.random() < 0.02) {
                    if (sneak) {
                        if ((Entity) this instanceof OceanizedCatEntity _datEntSetL)
                            _datEntSetL.getEntityData().set(DATA_stateSneaking, false);
                        if ((Entity) this instanceof OceanizedCatEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_action_time, 10);
                    } else if (!((Entity) this instanceof Mob _mobEnt7 && _mobEnt7.isAggressive())) {
                        if ((Entity) this instanceof OceanizedCatEntity _datEntSetL)
                            _datEntSetL.getEntityData().set(DATA_stateSneaking, true);
                        if ((Entity) this instanceof OceanizedCatEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_action_time, 10);
                    }
                }
                setShiftKeyDown(sneak);
            }
            if ((Entity) this instanceof Mob _mobEnt11 && _mobEnt11.isAggressive()) {
                setShiftKeyDown(false);
                if ((Entity) this instanceof OceanizedCatEntity _datEntSetL)
                    _datEntSetL.getEntityData().set(DATA_stateSneaking, false);
            }
        }
        this.refreshDimensions();
	}

	@Override
	public EntityDimensions getDimensions(Pose p_33597_) {
		return super.getDimensions(p_33597_).scale((float) 1);
	}

	public static void init() {
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.16);
		builder = builder.add(Attributes.MAX_HEALTH, 24);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 6);
		builder = builder.add(Attributes.FOLLOW_RANGE, 16);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.33);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

					&& !this.isAggressive()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_cat.walk"));
			}
			if (this.isShiftKeyDown()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_cat.sneak"));
			}
			if (this.isAggressive() && event.isMoving()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_cat.sprint"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_cat.idle"));
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
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_cat.attack"));
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
			this.remove(OceanizedCatEntity.RemovalReason.KILLED);
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
