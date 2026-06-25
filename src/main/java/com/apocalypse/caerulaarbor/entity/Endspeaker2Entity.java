package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.entity.base.SeaMonster;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CaerulaArborModAttributes;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
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

public class Endspeaker2Entity extends SeaMonster {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(Endspeaker2Entity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(Endspeaker2Entity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(Endspeaker2Entity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Boolean> DATA_isEvolving = SynchedEntityData.defineId(Endspeaker2Entity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Integer> DATA_duration = SynchedEntityData.defineId(Endspeaker2Entity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_skillp = SynchedEntityData.defineId(Endspeaker2Entity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_EvolveTime = SynchedEntityData.defineId(Endspeaker2Entity.class, EntityDataSerializers.INT);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";
	private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.WHITE, ServerBossEvent.BossBarOverlay.NOTCHED_6);

	public Endspeaker2Entity(PlayMessages.SpawnEntity packet, Level world) {
		this(CaerulaArborModEntities.ENDSPEAKER_2.get(), world);
	}

	public Endspeaker2Entity(EntityType<Endspeaker2Entity> type, Level world) {
		super(type, world);
		xpReward = 48;
		setNoAi(false);
		setMaxUpStep(1f);
		setPersistenceRequired();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "endspeaker_2");
		this.entityData.define(DATA_isEvolving, false);
		this.entityData.define(DATA_duration, 0);
		this.entityData.define(DATA_skillp, 100);
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
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this) {
			@Override
			public boolean canUse() {
				double x = Endspeaker2Entity.this.getX();
				double y = Endspeaker2Entity.this.getY();
				double z = Endspeaker2Entity.this.getZ();
				Entity entity = Endspeaker2Entity.this;
				Level world = Endspeaker2Entity.this.level();
				return super.canUse() && Endspeaker2Entity.this.isDurative();
			}

			@Override
			public boolean canContinueToUse() {
				double x = Endspeaker2Entity.this.getX();
				double y = Endspeaker2Entity.this.getY();
				double z = Endspeaker2Entity.this.getZ();
				Entity entity = Endspeaker2Entity.this;
				Level world = Endspeaker2Entity.this.level();
				return super.canContinueToUse() && Endspeaker2Entity.this.isDurative();
			}
		});
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.25, true) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 20.25;
			}

			@Override
			public boolean canUse() {
				double x = Endspeaker2Entity.this.getX();
				double y = Endspeaker2Entity.this.getY();
				double z = Endspeaker2Entity.this.getZ();
				Entity entity = Endspeaker2Entity.this;
				Level world = Endspeaker2Entity.this.level();
				return super.canUse() && Endspeaker2Entity.this.isDurative();
			}

			@Override
			public boolean canContinueToUse() {
				double x = Endspeaker2Entity.this.getX();
				double y = Endspeaker2Entity.this.getY();
				double z = Endspeaker2Entity.this.getZ();
				Entity entity = Endspeaker2Entity.this;
				Level world = Endspeaker2Entity.this.level();
				return super.canContinueToUse() && Endspeaker2Entity.this.isDurative();
			}

		});
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, true, false));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, SnowGolem.class, true, false));
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Villager.class, true, false));
		this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, Illusioner.class, true, false));
		this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, Pillager.class, true, false));
		this.targetSelector.addGoal(8, new NearestAttackableTargetGoal(this, Vindicator.class, true, false));
		this.targetSelector.addGoal(9, new NearestAttackableTargetGoal(this, Witch.class, true, false));
		this.targetSelector.addGoal(10, new NearestAttackableTargetGoal(this, Piglin.class, true, false));
		this.targetSelector.addGoal(11, new NearestAttackableTargetGoal(this, PiglinBrute.class, true, false));
		this.targetSelector.addGoal(12, new NearestAttackableTargetGoal(this, ZombifiedPiglin.class, true, false));
		this.targetSelector.addGoal(13, new NearestAttackableTargetGoal(this, Player.class, true, false) {
			@Override
			public boolean canUse() {
				double x = Endspeaker2Entity.this.getX();
				double y = Endspeaker2Entity.this.getY();
				double z = Endspeaker2Entity.this.getZ();
				Entity entity = Endspeaker2Entity.this;
				Level world = Endspeaker2Entity.this.level();
				return super.canUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
			}

			@Override
			public boolean canContinueToUse() {
				double x = Endspeaker2Entity.this.getX();
				double y = Endspeaker2Entity.this.getY();
				double z = Endspeaker2Entity.this.getZ();
				Entity entity = Endspeaker2Entity.this;
				Level world = Endspeaker2Entity.this.level();
				return super.canContinueToUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
			}
		});
		this.goalSelector.addGoal(14, new RandomStrollGoal(this, 1) {
			@Override
			public boolean canUse() {
				double x = Endspeaker2Entity.this.getX();
				double y = Endspeaker2Entity.this.getY();
				double z = Endspeaker2Entity.this.getZ();
				Entity entity = Endspeaker2Entity.this;
				Level world = Endspeaker2Entity.this.level();
				return super.canUse() && Endspeaker2Entity.this.isDurative();
			}

			@Override
			public boolean canContinueToUse() {
				double x = Endspeaker2Entity.this.getX();
				double y = Endspeaker2Entity.this.getY();
				double z = Endspeaker2Entity.this.getZ();
				Entity entity = Endspeaker2Entity.this;
				Level world = Endspeaker2Entity.this.level();
				return super.canContinueToUse() && Endspeaker2Entity.this.isDurative();
			}
		});
		this.goalSelector.addGoal(15, new RandomLookAroundGoal(this) {
			@Override
			public boolean canUse() {
				double x = Endspeaker2Entity.this.getX();
				double y = Endspeaker2Entity.this.getY();
				double z = Endspeaker2Entity.this.getZ();
				Entity entity = Endspeaker2Entity.this;
				Level world = Endspeaker2Entity.this.level();
				return super.canUse() && Endspeaker2Entity.this.isDurative();
			}

			@Override
			public boolean canContinueToUse() {
				double x = Endspeaker2Entity.this.getX();
				double y = Endspeaker2Entity.this.getY();
				double z = Endspeaker2Entity.this.getZ();
				Entity entity = Endspeaker2Entity.this;
				Level world = Endspeaker2Entity.this.level();
				return super.canContinueToUse() && Endspeaker2Entity.this.isDurative();
			}
		});
		this.goalSelector.addGoal(16, new FloatGoal(this));
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
	public SoundEvent getAmbientSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.glow_squid.ambient"));
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
		if (source.is(DamageTypes.FALL))
			return false;
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

	private boolean isDurative() {
		if (isEvolving()) {
			return false;
		}
		return getEntityData().get(DATA_duration) <= 0 && tickCount >= 68;
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
        if (this != null) {
            if (this instanceof Endspeaker2Entity) {
                ((Endspeaker2Entity) this).setAnimation("animation.endspeaker_2.start");
            }
            if (this.getAttributes().hasAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()))
                this.getAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()).setBaseValue(30);
            if (!this.level().isClientSide())
                this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 68, 9, false, false));
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
		compound.putInt("Dataduration", this.entityData.get(DATA_duration));
		compound.putInt("Dataskillp", this.entityData.get(DATA_skillp));
		compound.putInt("DataEvolveTime", this.entityData.get(DATA_EvolveTime));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("DataisEvolving"))
			this.entityData.set(DATA_isEvolving, compound.getBoolean("DataisEvolving"));
		if (compound.contains("Dataduration"))
			this.entityData.set(DATA_duration, compound.getInt("Dataduration"));
		if (compound.contains("Dataskillp"))
			this.entityData.set(DATA_skillp, compound.getInt("Dataskillp"));
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
	public boolean isPushable() {
		return false;
	}

	@Override
	protected void doPush(Entity entityIn) {
	}

	@Override
	protected void pushEntities() {
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
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.18);
		builder = builder.add(Attributes.MAX_HEALTH, 140);
		builder = builder.add(Attributes.ARMOR, 6);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 9);
		builder = builder.add(Attributes.FOLLOW_RANGE, 32);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
		return builder;
	}

	private void executeSkills() {
		if (!isAlive())
			return;
		Entity enemy = null;
		boolean iEvol = false;
		double gap = 0;
		double sklp1 = 0;
		double dura = 0;
		double evo = 0;
		if (tickCount < 35)
			return;
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
				EntityUtils.endspeakerToPhase2(level(), getX(), getY(), getZ(), this, 3);
				if (!level().isClientSide)
					discard();
			}
			getEntityData().set(DATA_EvolveTime, (int) (evo - 1));
		} else {
			getEntityData().set(DATA_isEvolving, false);
		}
		iEvol = getEntityData().get(DATA_isEvolving);
		gap = 400;
		if (iEvol) {
			gap = 80;
		}
		if (tickCount % gap == 33 && EntityUtils.getSeabornNum(level(), getX(), getY(), getZ()) < 32) {
			EntityUtils.spawnEndspeakerMobs(level(), getX(), getY(), getZ(), 0.1, 5);
		}
		EntityUtils.endspeakerTick(level(), this);
		sklp1 = getEntityData().get(DATA_skillp);
		dura = getEntityData().get(DATA_duration);
		enemy = getTarget();
		if (dura > 0) {
			getEntityData().set(DATA_duration, (int) (dura - 1));
		}
		if (iEvol) {
			getEntityData().set(DATA_skillp, 10000);
			return;
		}
		if (sklp1 > 0) {
			getEntityData().set(DATA_skillp, (int) (sklp1 - 1));
		} else {
			if (!(enemy == null) && enemy.isAlive()) {
				if (distanceTo(enemy) <= 4) {
					setAnimation("animation.endspeaker_2.skill");
					getEntityData().set(DATA_duration, 36);
					getEntityData().set(DATA_skillp, 170);
					CaerulaArborMod.queueServerWork(9, () -> {
						if (isAlive()) {
							Entity enemy1 = null;
							double count = 0;
							double atk = 0;
							enemy1 = getTarget();
							atk = getAttribute(Attributes.ATTACK_DAMAGE).getValue();
							count = 4;
							if (!(enemy1 == null) && enemy1.isAlive()) {
								enemy1.push(0, 0.33, 0);
								enemy1.hurt(new DamageSource(level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "endspeaker_attack"))), this),
										(float) (atk * 1.75));
								count = 3;
							}
							{
								final Vec3 _center = new Vec3(getX(), getY(), getZ());
								List<Entity> _entfound = level().getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(8 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
								for (Entity entityiterator : _entfound) {
									if (!(entityiterator instanceof LivingEntity)) {
										continue;
									}
									if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
										if (!(entityiterator == enemy1)) {
											continue;
										}
									}
									if (entityiterator == this) {
										continue;
									}
									if (distanceTo(entityiterator) <= 4) {
										entityiterator.push(0, 0.33, 0);
										entityiterator.hurt(new DamageSource(level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "endspeaker_attack"))), this),
												(float) (atk * 1.75));
										count = count - 1;
									}
									if (count <= 0) {
										break;
									}
								}
							}
						}
					});
					CaerulaArborMod.queueServerWork(19, () -> {
						if (isAlive()) {
							Entity enemy1 = null;
							double count = 0;
							double atk = 0;
							atk = getAttribute(Attributes.ATTACK_DAMAGE).getValue();
							count = 9;
							enemy1 = getTarget();
							{
								final Vec3 _center = new Vec3(getX(), getY(), getZ());
								List<Entity> _entfound = level().getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(10 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
								for (Entity entityiterator : _entfound) {
									if (!(entityiterator instanceof LivingEntity)) {
										continue;
									}
									if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
										if (!(entityiterator == enemy1)) {
											continue;
										}
									}
									if (entityiterator == this) {
										continue;
									}
									if (distanceTo(entityiterator) <= 5) {
										entityiterator.hurt(new DamageSource(level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "endspeaker_attack"))), this),
												(float) (atk * 2.25));
										count = count - 1;
									}
									if (count <= 0) {
										break;
									}
								}
							}
						}
					});
				}
			}
		}
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.endspeaker_2.die"));
			}
			if (this.isAggressive() && event.isMoving()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.endspeaker_2.sprint"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.endspeaker_2.idle"));
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
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.endspeaker_2.attack"));
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
		if (this.deathTime == 45) {
			this.remove(Endspeaker2Entity.RemovalReason.KILLED);
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
