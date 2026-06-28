package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.*;
import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
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
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
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
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Endspeaker3Entity extends SeaMonster {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(Endspeaker3Entity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(Endspeaker3Entity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(Endspeaker3Entity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_duration = SynchedEntityData.defineId(Endspeaker3Entity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_skillp = SynchedEntityData.defineId(Endspeaker3Entity.class, EntityDataSerializers.INT);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";
	private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.WHITE, ServerBossEvent.BossBarOverlay.NOTCHED_10);

	private boolean isDurative() {
		if (!isAlive()) {
			return false;
		}
		return getEntityData().get(DATA_duration) <= 0 && tickCount >= 50;
	}

	public Endspeaker3Entity(PlayMessages.SpawnEntity packet, Level world) {
		this(CaerulaArborModEntities.ENDSPEAKER_3.get(), world);
	}

	public Endspeaker3Entity(EntityType<Endspeaker3Entity> type, Level world) {
		super(type, world);
		xpReward = 64;
		setNoAi(false);
		setMaxUpStep(1.5f);
		setPersistenceRequired();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "endspeaker_3");
		this.entityData.define(DATA_duration, 0);
		this.entityData.define(DATA_skillp, 200);
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
				double x = Endspeaker3Entity.this.getX();
				double y = Endspeaker3Entity.this.getY();
				double z = Endspeaker3Entity.this.getZ();
				Entity entity = Endspeaker3Entity.this;
				Level world = Endspeaker3Entity.this.level();
				return super.canUse() && Endspeaker3Entity.this.isDurative();
			}

			@Override
			public boolean canContinueToUse() {
				double x = Endspeaker3Entity.this.getX();
				double y = Endspeaker3Entity.this.getY();
				double z = Endspeaker3Entity.this.getZ();
				Entity entity = Endspeaker3Entity.this;
				Level world = Endspeaker3Entity.this.level();
				return super.canContinueToUse() && Endspeaker3Entity.this.isDurative();
			}
		});
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1, true) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 25;
			}

			@Override
			public boolean canUse() {
				double x = Endspeaker3Entity.this.getX();
				double y = Endspeaker3Entity.this.getY();
				double z = Endspeaker3Entity.this.getZ();
				Entity entity = Endspeaker3Entity.this;
				Level world = Endspeaker3Entity.this.level();
				return super.canUse() && Endspeaker3Entity.this.isDurative();
			}

			@Override
			public boolean canContinueToUse() {
				double x = Endspeaker3Entity.this.getX();
				double y = Endspeaker3Entity.this.getY();
				double z = Endspeaker3Entity.this.getZ();
				Entity entity = Endspeaker3Entity.this;
				Level world = Endspeaker3Entity.this.level();
				return super.canContinueToUse() && Endspeaker3Entity.this.isDurative();
			}

		});
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true, false));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, SnowGolem.class, true, false));
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Villager.class, true, false));
		this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Illusioner.class, true, false));
		this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, Pillager.class, true, false));
		this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, Vindicator.class, true, false));
		this.targetSelector.addGoal(9, new NearestAttackableTargetGoal<>(this, Witch.class, true, false));
		this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, Piglin.class, true, false));
		this.targetSelector.addGoal(11, new NearestAttackableTargetGoal<>(this, PiglinBrute.class, true, false));
		this.targetSelector.addGoal(12, new NearestAttackableTargetGoal<>(this, ZombifiedPiglin.class, true, false));
		this.targetSelector.addGoal(13, new NearestAttackableTargetGoal(this, Player.class, true, false) {
			@Override
			public boolean canUse() {
				double x = Endspeaker3Entity.this.getX();
				double y = Endspeaker3Entity.this.getY();
				double z = Endspeaker3Entity.this.getZ();
				Entity entity = Endspeaker3Entity.this;
				Level world = Endspeaker3Entity.this.level();
				return super.canUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
			}

			@Override
			public boolean canContinueToUse() {
				double x = Endspeaker3Entity.this.getX();
				double y = Endspeaker3Entity.this.getY();
				double z = Endspeaker3Entity.this.getZ();
				Entity entity = Endspeaker3Entity.this;
				Level world = Endspeaker3Entity.this.level();
				return super.canContinueToUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
			}
		});
		this.targetSelector.addGoal(14, new NearestAttackableTargetGoal(this, Animal.class, true, false) {
			@Override
			public boolean canUse() {
				double x = Endspeaker3Entity.this.getX();
				double y = Endspeaker3Entity.this.getY();
				double z = Endspeaker3Entity.this.getZ();
				Entity entity = Endspeaker3Entity.this;
				Level world = Endspeaker3Entity.this.level();
				return super.canUse() && EntityUtils.canAttackAnimals();
			}

			@Override
			public boolean canContinueToUse() {
				double x = Endspeaker3Entity.this.getX();
				double y = Endspeaker3Entity.this.getY();
				double z = Endspeaker3Entity.this.getZ();
				Entity entity = Endspeaker3Entity.this;
				Level world = Endspeaker3Entity.this.level();
				return super.canContinueToUse() && EntityUtils.canAttackAnimals();
			}
		});
		this.goalSelector.addGoal(15, new RandomStrollGoal(this, 1) {
			@Override
			public boolean canUse() {
				double x = Endspeaker3Entity.this.getX();
				double y = Endspeaker3Entity.this.getY();
				double z = Endspeaker3Entity.this.getZ();
				Entity entity = Endspeaker3Entity.this;
				Level world = Endspeaker3Entity.this.level();
				return super.canUse() && Endspeaker3Entity.this.isDurative();
			}

			@Override
			public boolean canContinueToUse() {
				double x = Endspeaker3Entity.this.getX();
				double y = Endspeaker3Entity.this.getY();
				double z = Endspeaker3Entity.this.getZ();
				Entity entity = Endspeaker3Entity.this;
				Level world = Endspeaker3Entity.this.level();
				return super.canContinueToUse() && Endspeaker3Entity.this.isDurative();
			}
		});
		this.goalSelector.addGoal(16, new RandomLookAroundGoal(this) {
			@Override
			public boolean canUse() {
				double x = Endspeaker3Entity.this.getX();
				double y = Endspeaker3Entity.this.getY();
				double z = Endspeaker3Entity.this.getZ();
				Entity entity = Endspeaker3Entity.this;
				Level world = Endspeaker3Entity.this.level();
				return super.canUse() && Endspeaker3Entity.this.isDurative();
			}

			@Override
			public boolean canContinueToUse() {
				double x = Endspeaker3Entity.this.getX();
				double y = Endspeaker3Entity.this.getY();
				double z = Endspeaker3Entity.this.getZ();
				Entity entity = Endspeaker3Entity.this;
				Level world = Endspeaker3Entity.this.level();
				return super.canContinueToUse() && Endspeaker3Entity.this.isDurative();
			}
		});
		this.goalSelector.addGoal(17, new FloatGoal(this));
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
	public void die(DamageSource source) {
		super.die(source);
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        if (this == null)
            return;
        CaerulaArborModVariables.MapVariables.get(world).endspeaker_abolities = 0;
        CaerulaArborModVariables.MapVariables.get(world).syncData(world);
        if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            if ((world.getBlockState(BlockPos.containing(x, y, z))).canBeReplaced()) {
                world.setBlock(BlockPos.containing(x, y, z), CaerulaArborModBlocks.ENDSPEAKER_NEST.get().defaultBlockState(), 3);
            } else if ((world.getBlockState(BlockPos.containing(x, y + 1, z))).canBeReplaced()) {
                world.setBlock(BlockPos.containing(x, y + 1, z), CaerulaArborModBlocks.ENDSPEAKER_NEST.get().defaultBlockState(), 3);
            }
        }
        for (Entity entityiterator : new ArrayList<>(world.players())) {
            if ((level().dimension()) == (entityiterator.level().dimension()) && (entityiterator != null ? distanceTo(entityiterator) : -1) < 64) {
                if (entityiterator instanceof ServerPlayer _player) {
                    Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "silent_interruption"));
                    AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
                    if (!_ap.isDone()) {
                        for (String criteria : _ap.getRemainingCriteria())
                            _player.getAdvancements().award(_adv, criteria);
                    }
                }
            }
        }
    }

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
		if (this != null) {
			if (this instanceof Endspeaker3Entity) {
				this.setAnimation("animation.endspeaker_3.start");
			}
			if (this.getAttributes().hasAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()))
				this.getAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()).setBaseValue(30);
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
		compound.putInt("Dataduration", this.entityData.get(DATA_duration));
		compound.putInt("Dataskillp", this.entityData.get(DATA_skillp));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("Dataduration"))
			this.entityData.set(DATA_duration, compound.getInt("Dataduration"));
		if (compound.contains("Dataskillp"))
			this.entityData.set(DATA_skillp, compound.getInt("Dataskillp"));
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
		builder = builder.add(Attributes.MAX_HEALTH, 224);
		builder = builder.add(Attributes.ARMOR, 8);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 11);
		builder = builder.add(Attributes.FOLLOW_RANGE, 36);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
		return builder;
	}

	private void executeSkills() {
		if (!isAlive())
			return;
		Entity enemy = null;
		double gap = 0;
		double sklp1 = 0;
		double dura = 0;
		if (tickCount < 35)
			return;
		gap = 400;
		if (tickCount % gap == 33 && EntityUtils.getSeabornNum(level(), getX(), getY(), getZ()) < 32) {
			EntityUtils.spawnEndspeakerMobs(level(), getX(), getY(), getZ(), 0.15, 5);
		}
		EntityUtils.endspeakerTick(level(), this);
		sklp1 = getEntityData().get(DATA_skillp);
		dura = getEntityData().get(DATA_duration);
		enemy = getTarget();
		if (dura > 0) {
			getEntityData().set(DATA_duration, (int) (dura - 1));
		}
		if (sklp1 > 0) {
			getEntityData().set(DATA_skillp, (int) (sklp1 - 1));
		} else {
			if (!(enemy == null) && enemy.isAlive()) {
				if (distanceTo(enemy) <= 24) {
					setAnimation("animation.endspeaker_3.skill");
					addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 50, 9, false, false));
					getEntityData().set(DATA_skillp, 280);
					getEntityData().set(DATA_duration, 60);
					CaerulaArborMod.queueServerWork(16, () -> {
						teleportToEnemy();
					});
					CaerulaArborMod.queueServerWork(19, () -> {
						chop(getX(), getY(), getZ(), false);
					});
					CaerulaArborMod.queueServerWork(21, () -> {
						teleportToEnemy();
					});
					CaerulaArborMod.queueServerWork(24, () -> {
						chop(getX(), getY(), getZ(), false);
					});
					CaerulaArborMod.queueServerWork(26, () -> {
						teleportToEnemy();
					});
					CaerulaArborMod.queueServerWork(30, () -> {
						chop(getX(), getY(), getZ(), true);
					});
					CaerulaArborMod.queueServerWork(34, () -> {
						teleportToEnemy();
					});
					CaerulaArborMod.queueServerWork(37, () -> {
						chop(getX(), getY(), getZ(), true);
					});
					CaerulaArborMod.queueServerWork(41, () -> {
						teleportTo(getX(), getY(), getZ());
					});
					CaerulaArborMod.queueServerWork(44, () -> {
						chop(getX(), getY(), getZ(), true);
					});
				}
			}
		}
		if (getHealth() < getMaxHealth() * 0.4) {
			setTexture("endspeaker_3_broken");
		} else {
			setTexture("endspeaker_3");
		}
	}

	private void teleportToEnemy() {
		Entity enemy = getTarget();
		boolean canAttack = !(enemy == null) && enemy.isAlive();
		if (canAttack) {
			teleportTo(enemy.getX(), enemy.getY(), enemy.getZ());
		}
	}

	private void chop(double x, double y, double z, boolean ranged) {
		Entity enemy = getTarget();
		double atk = getAttribute(Attributes.ATTACK_DAMAGE).getValue();
		boolean canAttack = !(enemy == null) && enemy.isAlive();
		if (!this.level().isClientSide()) {
			this.level().playSound(null, BlockPos.containing(getX(), getY(), getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "endspeaker_attack_hit")), SoundSource.HOSTILE, (float) 3.5, 1);
		} else {
			this.level().playLocalSound(getX(), getY(), getZ(), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "endspeaker_attack_hit")), SoundSource.HOSTILE, (float) 3.5, 1, false);
		}
		if (ranged) {
			double atk1 = getAttribute(Attributes.ATTACK_DAMAGE).getValue();
			Entity enemy1 = getTarget();
			{
				final Vec3 _center = new Vec3(x, y, z);
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
						LivingEntity livingTarget = (LivingEntity) entityiterator;
						float healthBeforeHit = livingTarget.getHealth();
						float absorptionBeforeHit = livingTarget.getAbsorptionAmount();
						if (livingTarget.hurt(new DamageSource(level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "endspeaker_attack"))),
								this), (float) (atk1 * 0.9))) {
							handleAttackHit(livingTarget, healthBeforeHit, absorptionBeforeHit);
						}
					}
				}
			}
		} else if (canAttack) {
			LivingEntity livingTarget = (LivingEntity) enemy;
			float healthBeforeHit = livingTarget.getHealth();
			float absorptionBeforeHit = livingTarget.getAbsorptionAmount();
			if (livingTarget.hurt(new DamageSource(level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "endspeaker_attack"))),
					this), (float) (atk * 1.5))) {
				handleAttackHit(livingTarget, healthBeforeHit, absorptionBeforeHit);
			}
		}
	}

	private void handleAttackHit(LivingEntity target, float healthBeforeHit, float absorptionBeforeHit) {
		float dealtDamage = Math.max(0.0F, healthBeforeHit + absorptionBeforeHit - target.getHealth() - target.getAbsorptionAmount());
		if (dealtDamage <= 0.0F) {
			return;
		}
		if (level() instanceof ServerLevel serverLevel) {
			serverLevel.sendParticles(CaerulaArborModParticleTypes.ENDSPEAKER_PARTICLE.get(), target.getX(), target.getY() + 0.75, target.getZ(), 8, 0.75, 0.75, 0.75, 0.1);
		}
		if (getHealth() >= getMaxHealth() * 0.4F) {
			setHealth(getHealth() + dealtDamage * 1.4F);
		} else {
			setHealth(getHealth() + dealtDamage * 1.8F);
		}
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

			) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.endspeaker_3.move"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.endspeaker_3.die"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.endspeaker_3.idle"));
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
		if (this.swinging && this.lastSwing + 30L <= level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.endspeaker_3.attack"));
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
		if (this.deathTime == 50) {
			this.remove(Endspeaker3Entity.RemovalReason.KILLED);
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
