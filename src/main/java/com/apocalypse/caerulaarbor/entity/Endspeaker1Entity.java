package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CaerulaArborModAttributes;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import com.apocalypse.caerulaarbor.utils.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
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
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;

public class Endspeaker1Entity extends SeaMonster {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(Endspeaker1Entity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(Endspeaker1Entity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(Endspeaker1Entity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Boolean> DATA_isEvolving = SynchedEntityData.defineId(Endspeaker1Entity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Integer> DATA_EvolveTime = SynchedEntityData.defineId(Endspeaker1Entity.class, EntityDataSerializers.INT);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";
	private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.BLUE, ServerBossEvent.BossBarOverlay.PROGRESS);

	public Endspeaker1Entity(PlayMessages.SpawnEntity packet, Level world) {
		this(CaerulaArborModEntities.ENDSPEAKER_1.get(), world);
	}

	public Endspeaker1Entity(EntityType<Endspeaker1Entity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);
		setMaxUpStep(0.6f);
		setPersistenceRequired();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "endspeaker_1");
		this.entityData.define(DATA_isEvolving, false);
		this.entityData.define(DATA_EvolveTime, 0);
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
				Level world = Endspeaker1Entity.this.level();
				return super.canUse() && WorldUtils.canGrief(world);
			}

			@Override
			public boolean canContinueToUse() {
				Level world = Endspeaker1Entity.this.level();
				return super.canContinueToUse() && WorldUtils.canGrief(world);
			}
		});
		this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1, true) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 4;
			}

			@Override
			public boolean canUse() {
				return super.canUse() && isStarting();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && isStarting();
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
		this.targetSelector.addGoal(14, new NearestAttackableTargetGoal(this, Player.class, true, false) {
			@Override
			public boolean canUse() {
				double x = Endspeaker1Entity.this.getX();
				double y = Endspeaker1Entity.this.getY();
				double z = Endspeaker1Entity.this.getZ();
				Level world = Endspeaker1Entity.this.level();
				return super.canUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
			}

			@Override
			public boolean canContinueToUse() {
				double x = Endspeaker1Entity.this.getX();
				double y = Endspeaker1Entity.this.getY();
				double z = Endspeaker1Entity.this.getZ();
				Level world = Endspeaker1Entity.this.level();
				return super.canContinueToUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
			}
		});
		this.targetSelector.addGoal(15, new NearestAttackableTargetGoal(this, Animal.class, true, false) {
			@Override
			public boolean canUse() {
				return super.canUse() && EntityUtils.canAttackAnimals();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && EntityUtils.canAttackAnimals();
			}
		});
		this.goalSelector.addGoal(16, new RandomStrollGoal(this, 1) {
			@Override
			public boolean canUse() {
				return super.canUse() && isStarting();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && isStarting();
			}
		});
		this.goalSelector.addGoal(17, new RandomLookAroundGoal(this) {
			@Override
			public boolean canUse() {
				return super.canUse() && isStarting();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && isStarting();
			}
		});
		this.goalSelector.addGoal(18, new FloatGoal(this));
	}

	@Override
	public MobType getMobType() {
		return MobType.WATER;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
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
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "seaborn_death"));
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.DROWN))
			return false;
		return super.hurt(source, amount);
	}

	@Override
	public void setHealth(float pHealth){
		if (pHealth <= 0){
			super.setHealth(1);
			if (!isEvolving()) EntityUtils.endspeakerRevive(this);
		}
		else super.setHealth(pHealth);
	}

	@Override
	public void die(DamageSource source) {
		if(!isEvolving()) EntityUtils.endspeakerRevive(this);
	}

	private boolean isEvolving(){
		return this.entityData.get(DATA_EvolveTime) > 0;
	}

	private boolean isStarting(){
		return this.tickCount >= 50 && !this.entityData.get(DATA_isEvolving);
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
		if (this != null) {
			if (this instanceof Endspeaker1Entity) {
				this.setAnimation("animation.endspeaker_1.start");
			}
			if (this.getAttributes().hasAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()))
				this.getAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()).setBaseValue(30);
			if (this.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_RATE.get()))
				this.getAttribute(CaerulaArborModAttributes.SANITY_RATE.get()).setBaseValue(60);
			if (!this.level().isClientSide())
				this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 50, 9, false, false));
			EntityUtils.initEndspeakerAbilities(world, this);
			EntityUtils.getEndspeakerPrefixes(world, this);
		}
		return retval;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putBoolean("DataisEvolving", this.entityData.get(DATA_isEvolving));
		compound.putInt("DataEvolveTime", this.entityData.get(DATA_EvolveTime));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("DataisEvolving"))
			this.entityData.set(DATA_isEvolving, compound.getBoolean("DataisEvolving"));
		if (compound.contains("DataEvolveTime"))
			this.entityData.set(DATA_EvolveTime, compound.getInt("DataEvolveTime"));
	}

	@Override
	public void baseTick() {
		super.baseTick();
		executeSkills();
		this.refreshDimensions();
	}

	@Override
	public EntityDimensions getDimensions(Pose p_33597_) {
		return super.getDimensions(p_33597_).scale((float) 1);
	}

	@Override
	public boolean canChangeDimensions() {
		return false;
	}

	@Override
	public void startSeenByPlayer(ServerPlayer player) {
		super.startSeenByPlayer(player);
		this.bossInfo.addPlayer(player);
	}

	@Override
	public void stopSeenByPlayer(ServerPlayer player) {
		super.stopSeenByPlayer(player);
		this.bossInfo.removePlayer(player);
	}

	@Override
	public void customServerAiStep() {
		super.customServerAiStep();
		this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
	}

	public static void init() {
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.16);
		builder = builder.add(Attributes.MAX_HEALTH, 120);
		builder = builder.add(Attributes.ARMOR, 5);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 7);
		builder = builder.add(Attributes.FOLLOW_RANGE, 36);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
		return builder;
	}

	private void executeSkills() {
		if (!isAlive())
			return;
		if (tickCount < 75)
			return;
		double gap = 0;
		double evo = 0;
		evo = getEntityData().get(DATA_EvolveTime);
		if (evo > 0) {
			getEntityData().set(DATA_isEvolving, true);
			setHealth((float) Math.max(Math.round(getMaxHealth() * (300 - evo) * 0.0033333), 1));
			if (!hasEffect(CaerulaArborModMobEffects.INVULNERABLE.get())) {
				addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 300, 1, false, false));
			}
			if (evo <= 260) {
				if (!hasEffect(MobEffects.INVISIBILITY)) {
					addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 300, 0, false, false));
				}
			}
			if (evo == 1) {
				EntityUtils.endspeakerToPhase2(level(), getX(), getY(), getZ(), this, 2);
				if (!level().isClientSide)
					discard();
			}
			getEntityData().set(DATA_EvolveTime, (int) (evo - 1));
		} else {
			getEntityData().set(DATA_isEvolving, false);
		}
		gap = 600;
		if (getEntityData().get(DATA_isEvolving)) {
			gap = 100;
		}
		if (tickCount % gap == 50 && EntityUtils.getSeabornNum(level(), getX(), getY(), getZ()) < 32) {
			EntityUtils.spawnEndspeakerMobs(level(), getX(), getY(), getZ(), 0.085, 4);
		}
		EntityUtils.endspeakerTick(level(), this);
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

			) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.endspeaker_1.move"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.endspeaker_1.die"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.endspeaker_1.idle"));
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
		if (this.swinging && this.lastSwing + 7L <= level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.endspeaker_1.attack"));
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
		if (this.deathTime == 40) {
			this.remove(Endspeaker1Entity.RemovalReason.KILLED);
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
