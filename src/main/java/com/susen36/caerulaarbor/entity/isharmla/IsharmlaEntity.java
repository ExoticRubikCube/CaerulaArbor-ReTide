package com.susen36.caerulaarbor.entity.isharmla;

import com.susen36.babel.init.BabelAttributes;
import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.api.ServerGeoAnimator;
import com.susen36.caerulaarbor.client.model.entity.IsharmlaModel;
import com.susen36.caerulaarbor.entity.GladiiaWhirlEntity;
import com.susen36.caerulaarbor.entity.SkadiCorruptedEntity;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.init.*;
import com.susen36.caerulaarbor.util.EntityUtils;
import com.susen36.caerulaarbor.util.WorldUtils;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import javax.annotation.Nullable;
import java.util.*;

public class IsharmlaEntity extends SeaMonster {
	public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(IsharmlaEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(IsharmlaEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Boolean> DATA_IS_MONSTER = SynchedEntityData.defineId(IsharmlaEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Integer> DATA_SKILLP_1 = SynchedEntityData.defineId(IsharmlaEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_SKILLP_2 = SynchedEntityData.defineId(IsharmlaEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(IsharmlaEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_RECORDED_HEALTH = SynchedEntityData.defineId(IsharmlaEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_ABSORPTION = SynchedEntityData.defineId(IsharmlaEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_HEAL_P = SynchedEntityData.defineId(IsharmlaEntity.class, EntityDataSerializers.INT);
	private boolean IS_ANGERED;
	private boolean swinging;
	private long lastSwing;
	public String animationprocedure = "empty";
	private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.YELLOW, ServerBossEvent.BossBarOverlay.NOTCHED_6);

	public static final SoundEvent SKADI_HIT = CASounds.SKADI_HIT.get();

	public final IsharmlaPart head;
	private final IsharmlaPart body;
	private final IsharmlaPart front;
	private final IsharmlaPart medium;
	private final IsharmlaPart back;
	private final IsharmlaPart backTail;
	private final IsharmlaPart tail;
	private final IsharmlaPart[] subEntities;
	// 服务端骨骼姿态计算器，驱动 subEntities 跟随 GeckoLib 动画
	private final ServerGeoAnimator<IsharmlaEntity> serverGeoAnimator;

	public IsharmlaEntity(Level world) {
		this(CAEntities.ISHARMLA.get(), world);
	}

	public IsharmlaEntity(EntityType<IsharmlaEntity> type, Level world) {
		super(type, world);
		this.serverGeoAnimator = new ServerGeoAnimator<>(this, new IsharmlaModel());
		this.head = new IsharmlaPart(this, "upjaw", 4.0F, 4.0F);
		this.body = new IsharmlaPart(this, "isharmla_body", 0.0F, 0.0F);
		this.front = new IsharmlaPart(this, "front", 8.0F, 6.0F);
		this.medium = new IsharmlaPart(this, "medium", 8.0F, 6.0F);
		this.back = new IsharmlaPart(this, "back", 8.0F, 6.0F);
		this.backTail = new IsharmlaPart(this, "backTail", 6.0F, 4.0F);
		this.tail = new IsharmlaPart(this, "tail", 6.0F, 4.0F);
		this.subEntities = new IsharmlaPart[]{this.head, this.body, this.front, this.medium, this.back, this.backTail, this.tail};
		xpReward = 64;
		setNoAi(false);
		this.noCulling = true;
		this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.5f);
		setPersistenceRequired();
		IS_ANGERED = false;
		this.setId(ENTITY_COUNTER.getAndAdd(8) + 1);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_SHOOT, false);
		builder.define(DATA_ANIMATION, "undefined");
		builder.define(DATA_IS_MONSTER, false);
		builder.define(DATA_SKILLP_1, 400);
		builder.define(DATA_SKILLP_2, 240);
		builder.define(DATA_DURATION, 60);
		builder.define(DATA_RECORDED_HEALTH, 1000);
		builder.define(DATA_ABSORPTION, 60);
		builder.define(DATA_HEAL_P, 60);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this , 1, false) {

			@Override
			public boolean canUse() {
				return super.canUse() && IsharmlaEntity.this.isMonster();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && IsharmlaEntity.this.isMonster();
			}

		});
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1) {
			@Override
			public boolean canUse() {
				return super.canUse() && IsharmlaEntity.this.isDurative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && IsharmlaEntity.this.isDurative();
			}
		});
		this.goalSelector.addGoal(4, new RandomLookAroundGoal(this) {
			@Override
			public boolean canUse() {
				return !isMonster() && super.canUse() && IsharmlaEntity.this.isDurative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && IsharmlaEntity.this.isDurative();
			}
		});
		this.goalSelector.addGoal(5, new FloatGoal(this));
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		if (isMonster()) return SoundEvents.GUARDIAN_HURT;
		return SKADI_HIT;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.GUARDIAN_DEATH;
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		double targetX = target.getX();
		double targetY = target.getY();
		double targetZ = target.getZ();
		if (!this.level().isClientSide()) {
			this.level().playSound(null, BlockPos.containing(this.getX(), this.getY(), this.getZ()),
					CASounds.ISHARMLA_ATTACK_PRE.get(), SoundSource.HOSTILE, 2.5F, 1);
			CaerulaArborMod.queueServerWork(13, () -> {
				if (this.isAlive() && this.level() instanceof ServerLevel serverLevel) {
					double sourceX = this.getX();
					double sourceY = this.getY();
					double sourceZ = this.getZ();
					for (int index = 0; index < 12; index++) {
						serverLevel.sendParticles(CAParticles.MOIST_BOOM.get(), sourceX, sourceY + 10 + index, sourceZ, 6, index * 0.1, index * 0.1, index * 0.1, 0);
					}
				}
			});
			CaerulaArborMod.queueServerWork(15, () -> {
				if (this.isAlive() && target.isAlive() && this.distanceTo(target) <= 32) {
					this.level().playSound(null, BlockPos.containing(targetX, targetY, targetZ),
							CASounds.ISHARMLA_ATTACK_LAUNCH.get(), SoundSource.HOSTILE, 2.5F, 1);
					isharmlaDroppedAttack(this.level(), targetX, targetY, targetZ, Mth.nextDouble(RandomSource.create(), 1.5, 3), 1);
					int count = 0;
					for (int index = 0; index < 5; index++) {
						if (count > 5) {
							break;
						}
						final Vec3 center = new Vec3(this.getX(), this.getY(), this.getZ());
						TagKey<EntityType<?>> oceanOffspringTag = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring"));
						List<LivingEntity> foundEntities = this.level().getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(24),
								entity -> entity.isAlive()
										&& entity != this
										&& !(entity instanceof ServerPlayer serverPlayer
										&& (serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE
										|| serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR))
										&& !(entity.getType().is(oceanOffspringTag) && entity != target))
								.stream().sorted(Comparator.comparingDouble(candidate -> candidate.distanceToSqr(center))).toList();
						for (LivingEntity entityIterator : foundEntities) {
							if (count > 5) {
								break;
							}
							if (this.distanceToSqr(entityIterator) <= 576) {
								count++;
								int delayTicks = 2 * count;
								CaerulaArborMod.queueServerWork(delayTicks, () -> {
									isharmlaDroppedAttack(this.level(), entityIterator.getX(), entityIterator.getY(), entityIterator.getZ(), Mth.nextDouble(RandomSource.create(), 1.5, 3), 1);
								});
							}
						}
					}
				}
			});
			this.getEntityData().set(DATA_DURATION, 60);
		}
		return true;
	}

	private void isharmlaDroppedAttack(LevelAccessor world, double x, double y, double z, double radius, double rate) {
		if (!(world instanceof ServerLevel level)) {
			return;
		}
		for (int index = 0; index < 20; index++) {
			final double particleIndex = index;
			CaerulaArborMod.queueServerWork(index, () -> {
				level.sendParticles(CAParticles.MOIST_BOOM.get(), x, y + 10 - particleIndex * 0.5, z, 1, 0, 0, 0, 0);
				double angle = Math.toRadians(particleIndex * 9);
				level.sendParticles(ParticleTypes.END_ROD, x + radius * Math.cos(angle), y, z + radius * Math.sin(angle), 1, 0, 0, 0, 0);
				double oppositeAngle = Math.toRadians(particleIndex * 9 + 180);
				level.sendParticles(ParticleTypes.END_ROD, x + radius * Math.cos(oppositeAngle), y + 0.125, z + radius * Math.sin(oppositeAngle), 1, 0, 0, 0, 0);
			});
		}
		CaerulaArborMod.queueServerWork(20, () -> {
			Entity target = this.getTarget();
			double damage = (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * rate;
			level.playSound(null, BlockPos.containing(x, y, z), CASounds.ISHARMLA_ATTACK_HIT.get(), SoundSource.HOSTILE, 2,
					(float) Mth.nextDouble(RandomSource.create(), 0.85, 1.1));
			Vec3 center = new Vec3(x, y, z);
			List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(radius),
					entity -> !(entity.getType().is(EntityUtils.OCEAN_OFFSPRING) && entity != target));
			for (LivingEntity entityIterator : entities) {
				if (center.distanceToSqr(entityIterator.position()) <= radius * radius) {
					entityIterator.hurt(
							CADamageTypes.source(world, CADamageTypes.ISHARMLA_ATTACK, this), (float) damage);
				}
			}
		});
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.DROWN) || source.is(DamageTypes.IN_WALL))
			return false;
		return super.hurt(source, amount);
	}

	@Override
	public void die(DamageSource source) {
		if (this.transformToHuman()) return;
		super.die(source);
	}

	@Override
	public void setHealth(float pHealth) {
		float deletion = Math.min(this.getHealth() - pHealth, this.getMaxHealth() * 0.51f);
		float newHealth = this.getHealth() - deletion;
		if (newHealth <= 0 && this.transformToHuman()) return;
		super.setHealth(newHealth);
	}

	@Override
	public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
		ItemStack item = pPlayer.getMainHandItem();
		if (item.is(CAItems.ISHARMLA_SPAWNER.get())) {
			this.transformToMonster();
			return InteractionResult.SUCCESS;
		}
		return super.mobInteract(pPlayer, pHand);
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata);
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		if (this instanceof IsharmlaEntity) {
			this.setAnimation("animation.isharmla.start");
		}
		if (!this.level().isClientSide())
			this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 60, 9, false, false));
		if ((LevelAccessor) world instanceof Level level) {
			level.playSound(null, BlockPos.containing(x, y, z), CASounds.ISHARMLA_TO_HUMAN.get(), SoundSource.HOSTILE, 2, 1);
		}
		for (Entity entityiterator : new ArrayList<>(world.players())) {
			if ((level().dimension()) == (entityiterator.level().dimension())) {
				if (entityiterator instanceof ServerPlayer player) {
					AdvancementHolder adv = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "we_many_orienting"));
					AdvancementProgress ap = player.getAdvancements().getOrStartProgress(adv);
					if (!ap.isDone()) {
						for (String criteria : ap.getRemainingCriteria())
							player.getAdvancements().award(adv, criteria);
					}
				}
			}
		}
		return retval;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("IsMonster", this.entityData.get(DATA_IS_MONSTER));
		compound.putInt("Skillp1", this.entityData.get(DATA_SKILLP_1));
		compound.putInt("Skillp2", this.entityData.get(DATA_SKILLP_2));
		compound.putInt("Duration", this.entityData.get(DATA_DURATION));
		compound.putInt("RecordedHealth", this.entityData.get(DATA_RECORDED_HEALTH));
		compound.putInt("Absorption", this.entityData.get(DATA_ABSORPTION));
		compound.putInt("HealP", this.entityData.get(DATA_HEAL_P));
		compound.putBoolean("IsAngered", IS_ANGERED);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("IsMonster")) {
		    this.entityData.set(DATA_IS_MONSTER, compound.getBoolean("IsMonster"));
		}
		if (compound.contains("Skillp1")) {
		    this.entityData.set(DATA_SKILLP_1, compound.getInt("Skillp1"));
		}
		if (compound.contains("Skillp2")) {
		    this.entityData.set(DATA_SKILLP_2, compound.getInt("Skillp2"));
		}
		if (compound.contains("Duration")) {
		    this.entityData.set(DATA_DURATION, compound.getInt("Duration"));
		}
		if (compound.contains("RecordedHealth")) {
		    this.entityData.set(DATA_RECORDED_HEALTH, compound.getInt("RecordedHealth"));
		}
		if (compound.contains("Absorption")) {
		    this.entityData.set(DATA_ABSORPTION, compound.getInt("Absorption"));
		}
		if (compound.contains("HealP")) {
		    this.entityData.set(DATA_HEAL_P, compound.getInt("HealP"));
		}
		if (compound.contains("IsAngered"))
			this.IS_ANGERED = compound.getBoolean("IS_ANGERED");

	}

	@Override
	public void baseTick() {
		super.baseTick();
		if (isMonster()) bossInfo.setColor(ServerBossEvent.BossBarColor.WHITE);
		else {
			bossInfo.setColor(ServerBossEvent.BossBarColor.YELLOW);
			if (this.getHealth() < this.getMaxHealth() * 0.5 && !IS_ANGERED) {
				AttributeInstance inst = this.getAttribute(Attributes.ATTACK_DAMAGE);
				if (inst != null) inst.setBaseValue(inst.getBaseValue() * 1.5);
				this.entityData.set(DATA_SKILLP_2, 1);
				this.distributeIsharmlaTear(this.level(), this.getX(), this.getY(), this.getZ());
				this.IS_ANGERED = true;
			}
		}
		LevelAccessor world = this.level();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		Entity target;
		double sklp1;
		double sklp2;
		double dura;
		double healP;
		double absP;
		boolean isMonster;
		boolean canAttack;
		if (this.isAlive()) {
			sklp1 = (Entity) this instanceof IsharmlaEntity datEntI ? datEntI.getEntityData().get(DATA_SKILLP_1) : 0;
			sklp2 = (Entity) this instanceof IsharmlaEntity datEntI ? datEntI.getEntityData().get(DATA_SKILLP_2) : 0;
			dura = (Entity) this instanceof IsharmlaEntity datEntI ? datEntI.getEntityData().get(DATA_DURATION) : 0;
			absP = (Entity) this instanceof IsharmlaEntity datEntI ? datEntI.getEntityData().get(DATA_ABSORPTION) : 0;
			isMonster = (Entity) this instanceof IsharmlaEntity datEntL5 && datEntL5.getEntityData().get(DATA_IS_MONSTER);
            target = this.getTarget();
			if (dura > 0) {
				if ((Entity) this instanceof IsharmlaEntity datEntSetI)
					datEntSetI.getEntityData().set(DATA_DURATION, (int) (dura - 1));
			}
			if (absP > 0) {
                double damage;
				double d;
				double healthBonus = 0;
				double attackBonus = 0;
				double itrHealth;
				double itrAttack;
				double beforeHealth;
				double beforeAttack;
                damage = (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 0.15;
				{
					final Vec3 center = new Vec3(x, y, z);
					TagKey<EntityType<?>> oceanOffspringTag = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring"));
					TagKey<EntityType<?>> bossOffspringTag = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "bossoffspring"));
					List<LivingEntity> entfound = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(32),
							e -> e.isAlive()
									&& e != this
									&& !(e instanceof GladiiaWhirlEntity)
									&& !(e instanceof Player)
									&& !(e instanceof IsharmlaTearEntity));
					for (LivingEntity entityiterator : entfound) {
						d = distanceTo(entityiterator);
						if (d <= 32) {
							EntityUtils.applyOrbitMotion(entityiterator, this);
							if (entityiterator.getType().is(oceanOffspringTag)
									&& !entityiterator.getType().is(bossOffspringTag)) {
								itrHealth = entityiterator.getMaxHealth() * 0.01;
								itrAttack = (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : 0) * 0.01;
								if (Math.random() < 0.25) {
									sendLinkParticlesToEntity(world, x - 0.5, y, z - 0.5, entityiterator);
									if (d >= 3) {
										EntityUtils.pullToward(entityiterator, this);
									}
								}
								entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.OCEANKILLER_DAMAGE),
										(float) damage);
								healthBonus = healthBonus + itrHealth;
								attackBonus = attackBonus + itrAttack;
							}
						}
					}
				}
				beforeHealth = this.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? this.getAttribute(Attributes.MAX_HEALTH).getBaseValue() : 0;
				beforeAttack = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : 0;
				if (this.getAttributes().hasAttribute(Attributes.MAX_HEALTH))
					this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((beforeHealth + Math.min(beforeHealth * 0.025, healthBonus)));
				if (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
					this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((beforeAttack + Math.min(beforeAttack * 0.025, attackBonus)));
				this.setHealth(this.getMaxHealth());
				if ((Entity) this instanceof IsharmlaEntity datEntSetI)
					datEntSetI.getEntityData().set(DATA_ABSORPTION, (int) (absP - 1));
			}
			canAttack = !(target == null) && target.isAlive();
			if (isMonster) {
                double r;
				double t;
				double ang;
				t = tickCount % 90;
				for (int index0 = 0; index0 < 20; index0++) {
					ang = Math.toRadians(index0 * 6 + t * 4);
					r = 12 + Math.sin(index0 * 12);
					if (Math.random() < 0.33) {
						if (world instanceof ServerLevel level1)
							level1.sendParticles(ParticleTypes.END_ROD, (x + r * Math.sin(ang)), (y + 0.125), (z + r * Math.cos(ang)), 1, 0, 0.25, 0, 0.2);
					}
					if (world instanceof ServerLevel level1)
						level1.sendParticles(CAParticles.EDERMAN_PTC.get(), (x + r * Math.sin(ang)), (y + 0.15), (z + r * Math.cos(ang)), 1, 0, 0.25, 0, 0.2);
					r = 23 + Math.sin(index0 * 12);
					if (Math.random() < 0.33) {
						if (world instanceof ServerLevel level1)
							level1.sendParticles(ParticleTypes.END_ROD, (x + r * Math.sin(ang)), (y + 0.125), (z + r * Math.cos(ang)), 1, 0, 0.25, 0, 0.2);
					}
					if (world instanceof ServerLevel level1)
						level1.sendParticles(CAParticles.EDERMAN_PTC.get(), (x + r * Math.sin(ang)), (y + 0.15), (z + r * Math.cos(ang)), 1, 0, 0.25, 0, 0.2);
				}
				if (sklp1 > 0) {
					if ((Entity) this instanceof IsharmlaEntity datEntSetI)
						datEntSetI.getEntityData().set(DATA_SKILLP_1, (int) (sklp1 - 1));
				} else if (dura <= 0) {
					if (canAttack) {
						if (distanceTo(target) <= 32) {
							if (this instanceof IsharmlaEntity) {
								this.setAnimation("animation.isharmla.tail_monster");
							}
							if ((Entity) this instanceof IsharmlaEntity datEntSetI)
								datEntSetI.getEntityData().set(DATA_SKILLP_1, 300);
							if ((Entity) this instanceof IsharmlaEntity datEntSetI)
								datEntSetI.getEntityData().set(DATA_DURATION, 26);
							dura = 26;
							CaerulaArborMod.queueServerWork(10, () -> {
								this.performRangedAttack(32, 2.5);
							});
						}
					}
				}
				if (sklp2 > 0) {
					if ((Entity) this instanceof IsharmlaEntity datEntSetI)
						datEntSetI.getEntityData().set(DATA_SKILLP_2, (int) (sklp2 - 1));
				} else if (dura <= 0) {
					if (canAttack) {
						if (distanceTo(target) <= 32) {
							if (this instanceof IsharmlaEntity) {
								this.setAnimation("animation.isharmla.bite_monster");
							}
							if ((Entity) this instanceof IsharmlaEntity datEntSetI)
								datEntSetI.getEntityData().set(DATA_SKILLP_2, 200);
							if ((Entity) this instanceof IsharmlaEntity datEntSetI)
								datEntSetI.getEntityData().set(DATA_DURATION, 40);
							if (!this.level().isClientSide())
								this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 30, 9, false, false));
							CaerulaArborMod.queueServerWork(15, () -> {
								Entity enemy1;
								double d;
								enemy1 =  this.getTarget();
								d = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
								if (world instanceof Level level) {
									level.playSound(null, BlockPos.containing(x, y, z), CASounds.ISHARMLA_TEAR_HURT_1.get(), SoundSource.HOSTILE, 3, 1);
								}
								if (!(enemy1 == null)) {
									if ((enemy1 instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) <= d) {
                                                                                enemy1.hurt(CADamageTypes.source(world, CADamageTypes.ISHARMLA_ATTACK, this), (float) (d * 16));
									} else {
										enemy1.hurt(CADamageTypes.source(world, CADamageTypes.ISHARMLA_ATTACK, this), (float) (d * 3.5));
									}
								}
							});
						}
					}
				}
			} else {
				healP =  this instanceof IsharmlaEntity datEntI ? datEntI.getEntityData().get(DATA_HEAL_P) : 0;
				if (healP > 0) {
					if ((Entity) this instanceof IsharmlaEntity datEntSetI)
						datEntSetI.getEntityData().set(DATA_HEAL_P, (int) (healP - 1));
				} else if (dura <= 0) {
					if (this instanceof IsharmlaEntity) {
						this.setAnimation("animation.isharmla.heal_human");
					}
					if (this instanceof IsharmlaEntity datEntSetI)
						datEntSetI.getEntityData().set(DATA_HEAL_P, 120);
					if (this instanceof IsharmlaEntity datEntSetI)
						datEntSetI.getEntityData().set(DATA_DURATION, 30);
					dura = 30;
					CaerulaArborMod.queueServerWork(15, () -> {
						double atk;
						double count = 0;
						atk = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
						if (world instanceof Level level) {
							level.playSound(null, BlockPos.containing(x, y, z), CASounds.ISHARMLA_HEAL.get(), SoundSource.HOSTILE, 3, 1);
						}
						{
							final Vec3 center = new Vec3(x, y, z);
							TagKey<EntityType<?>> oceanOffspringTag = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring"));
							List<LivingEntity> entfound = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(8),
									e -> e.getType().is(oceanOffspringTag) && e.getHealth() < e.getMaxHealth())
									.stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
							for (LivingEntity entityiterator : entfound) {
								if (entityiterator.getHealth() < entityiterator.getMaxHealth()) {
                                    entityiterator.heal((float) atk);
                                    if (world instanceof ServerLevel level)
										level.sendParticles(ParticleTypes.HAPPY_VILLAGER, (entityiterator.getX()), (entityiterator.getY() + 0.75), (entityiterator.getZ()), 24, 0.75, 0.75, 0.75, 0.1);
									if (!(this == entityiterator)) {
										count = count + 1;
										if (count >= 6) {
											break;
										}
									}
								}
							}
						}
					});
				}
				if (sklp1 > 0) {
					if ((Entity) this instanceof IsharmlaEntity datEntSetI)
						datEntSetI.getEntityData().set(DATA_SKILLP_1, (int) (sklp1 - 1));
				} else if (dura <= 0) {
					this.transformToMonster();
					dura = 40;
				}
				if (sklp2 > 0) {
					if ((Entity) this instanceof IsharmlaEntity datEntSetI)
						datEntSetI.getEntityData().set(DATA_SKILLP_2, (int) (sklp2 - 1));
				} else if (dura <= 0) {
					if (this instanceof IsharmlaEntity) {
						this.setAnimation("animation.isharmla.heal_human");
					}
					if ((Entity) this instanceof IsharmlaEntity datEntSetI)
						datEntSetI.getEntityData().set(DATA_SKILLP_2, 600);
					if ((Entity) this instanceof IsharmlaEntity datEntSetI)
						datEntSetI.getEntityData().set(DATA_DURATION, 30);
					if (!this.level().isClientSide())
						this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 30, 9, false, false));
					this.distributeIsharmlaTear(world, x, y, z);
				}
				if (tickCount % 400 == 80) {
					SkadiCorruptedEntity.corruptedSpawnMobs(world, x, y, z, 3);
				}
			}
		}
		this.refreshDimensions();
	}

	@Override
	public EntityDimensions getDefaultDimensions(Pose p_33597_) {
		if (isMonsterForm()) return super.getDefaultDimensions(p_33597_).scale(10, 4f);
		return super.getDefaultDimensions(p_33597_);
	}

	@Override
	public void setId(int id) {
		super.setId(id);
		for (int index = 0; index < this.subEntities.length; index++) {
			this.subEntities[index].setId(id + index + 1);
		}
	}

	@Override
	public boolean isMultipartEntity() {
		return true;
	}

	@Override
	public PartEntity<?>[] getParts() {
		return this.subEntities;
	}

	@Override
	public boolean canUsePortal(boolean allowVehicles) {
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

	@Override
	public void aiStep() {
		super.aiStep();
		for (IsharmlaPart part : this.subEntities) {
			part.refreshDimensions();
		}
		if (this.isAlive() && this.isMonsterForm()) {
			Vec3[] oldPositions = new Vec3[this.subEntities.length];
			for (int index = 0; index < this.subEntities.length; index++) {
				oldPositions[index] = this.subEntities[index].position();
			}
			Map<String, Vec3> allBonePos = this.serverGeoAnimator.tickAndGetCurrentPose(this.tickCount, this.getYRot(), true);
			Map<String, Vec3> currentPose = new HashMap<>();
			for (IsharmlaPart part : this.subEntities) {
				Vec3 v = allBonePos.get(part.name);
				if (v != null) currentPose.put(part.name, v);
			}
			for (IsharmlaPart part : this.subEntities) {
				Vec3 entityOffset = currentPose.get(part.name);
				if (entityOffset == null) continue;
				this.tickPart(part, entityOffset.x, entityOffset.y, entityOffset.z);
			}
			for (int index = 0; index < this.subEntities.length; index++) {
				IsharmlaPart part = this.subEntities[index];
				Vec3 oldPosition = oldPositions[index];
				part.xo = oldPosition.x;
				part.yo = oldPosition.y;
				part.zo = oldPosition.z;
				part.xOld = oldPosition.x;
				part.yOld = oldPosition.y;
				part.zOld = oldPosition.z;
			}
		} else if (this.isAlive()) {
			for (IsharmlaPart part : this.subEntities) {
				part.setPos(this.getX(), this.getEyeY(), this.getZ());
			}
		}
	}

	@Override
	public boolean isPushable() {
		return !isMonster();
	}

	@Override
	protected void doPush(Entity entityIn) {
		if (isMonster()) return;
		super.doPush(entityIn);
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.18);
		builder = builder.add(Attributes.MAX_HEALTH, 320);
		builder = builder.add(Attributes.ARMOR, 12);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 16);
		builder = builder.add(Attributes.FOLLOW_RANGE, 64);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 1);
		builder = builder.add(CAAttributes.GENERAL_DEFENSE, 4);
		builder = builder.add(CAAttributes.MAGIC_RESISTANCE, 50);
		builder = builder.add(BabelAttributes.ELEMENTAL_MODIFIER, 0.01);
		builder = builder.add(BabelAttributes.MAX_ELEMENTAL_VALUE, 2000);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		boolean isM3 = isMonster();
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.05F && event.getLimbSwingAmount() < 0.05F))) {
				if (isM3) return event.setAndContinue(RawAnimation.begin().thenLoop("animation.isharmla.idle_monster"));
				else return event.setAndContinue(RawAnimation.begin().thenLoop("animation.isharmla.move_human"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.isharmla.die"));
			}
			if (isM3) return event.setAndContinue(RawAnimation.begin().thenLoop("animation.isharmla.idle_monster"));
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.isharmla.idle_human"));
		}
		return PlayState.STOP;
	}

	private PlayState attackingPredicate(AnimationState event) {
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
			this.swinging = true;
			this.lastSwing = level().getGameTime();
		}
		if (this.swinging && this.lastSwing + 42L <= level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.isharmla.attack_monster"));
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
		if (this.deathTime >= 20) {
			LevelAccessor world = this.level();
			double x = this.getX();
			double y = this.getY();
			double z = this.getZ();
			if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
				if (!world.isClientSide() && world.getServer() != null) {
					BlockPos bpLootTblWorld = BlockPos.containing(x, y, z);
					for (ItemStack itemstackiterator : world.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "gameplay/relic_isharmla")))
							.getRandomItems(new LootParams.Builder((ServerLevel) world).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(bpLootTblWorld)).withParameter(LootContextParams.BLOCK_STATE, world.getBlockState(bpLootTblWorld))
									.withOptionalParameter(LootContextParams.BLOCK_ENTITY, world.getBlockEntity(bpLootTblWorld)).create(LootContextParamSets.EMPTY))) {
						if (world instanceof ServerLevel level) {
							ItemEntity entityToSpawn = new ItemEntity(level, x, y, z, itemstackiterator);
							entityToSpawn.setPickUpDelay(10);
							entityToSpawn.setUnlimitedLifetime();
							level.addFreshEntity(entityToSpawn);
						}
					}
				}
				for (int index0 = 0; index0 < 96; index0++) {
					if (world instanceof ServerLevel level)
						level.addFreshEntity(new ExperienceOrb(level, (x + Mth.nextDouble(RandomSource.create(), -1, 1)), y, (z + Mth.nextDouble(RandomSource.create(), -1, 1)), Mth.nextInt(RandomSource.create(), 16, 48)));
				}
			}
			this.remove(RemovalReason.KILLED);
			this.dropExperience(this.getKillCredit());
		}
	}

	public String getSyncedAnimation() {
		return this.entityData.get(DATA_ANIMATION);
	}

	public void setAnimation(String animation) {
		this.entityData.set(DATA_ANIMATION, animation);
		this.setAnimationProcedure(animation);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		data.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
		data.add(new AnimationController<>(this, "attacking", 0, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
	}

	private void distributeIsharmlaTear(LevelAccessor world, double x, double y, double z) {
		double r;
		double d;
		double tx;
		double tz;
		double ty;
		for (int index0 = 0; index0 < 8; index0++) {
			r = Mth.nextDouble(RandomSource.create(), 0, 6.283);
			d = Mth.nextDouble(RandomSource.create(), 4, 18);
			tx = x + d * Math.cos(r);
			tz = z + d * Math.sin(r);
			ty = WorldUtils.findFirstEmptyYAbove(world, tx, y, tz);
			if (!Double.isNaN(ty)) {
				if (world instanceof ServerLevel level) {
					Entity entityToSpawn = CAEntities.ISHARMLA_TEAR.get().spawn(level, BlockPos.containing(tx, ty, tz), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
					}
				}
				break;
			}
		}
	}

	public static void sendLinkParticlesToEntity(LevelAccessor world, double x, double y, double z, Entity target) {
		if (target == null) {
			return;
		}
		double vx = target.getX() - (x + 0.5);
		double vy = target.getY() - (y + 0.5);
		double vz = target.getZ() - (z + 0.5);
		double size = Math.clamp(Math.round(Math.sqrt(vx * vx + vy * vy + vz * vz)), 1, 32);
		for (int index = 0; index < (int) size; index++) {
			if (world instanceof ServerLevel level) {
				level.sendParticles(CAParticles.ISHARMLA_CURSE_PARTICLE.get(), x + 0.5 + vx / size * index, y + 0.5 + vy / size * index + 0.5, z + 0.5 + vz / size * index, 5, 0.32, 0.5, 0.32, 0.05);
			}
		}
	}

	private boolean isDurative() {
		if (!isAlive()) {
			return false;
		}
		return getEntityData().get(DATA_DURATION) <= 0;
	}

	public boolean isMonsterForm() {
		return getEntityData().get(DATA_IS_MONSTER);
	}

	public boolean isMonster() {
		return isDurative() && isMonsterForm();
	}

	private void tickPart(IsharmlaPart part, double x, double y, double z) {
		part.setPos(this.getX() + x, this.getY() + y, this.getZ() + z);
	}

	public boolean hurt(IsharmlaPart part, DamageSource source, float amount) {
		return this.hurt(source, amount);
	}

	private void performRangedAttack(double radius, double damageRate) {
		LevelAccessor world = this.level();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();

		Entity target = this.getTarget();
		double damage = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * damageRate : 0;

		if (world instanceof Level level) {
			level.playSound(null, BlockPos.containing(x, y, z), CASounds.ISHARMLA_ATTACK_HIT.get(), SoundSource.HOSTILE, 2, (float) Mth.nextDouble(RandomSource.create(), 0.85, 1.1));
		}

		final Vec3 center = new Vec3(x, y, z);
		TagKey<EntityType<?>> oceanOffspringTag = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring"));
		List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(radius),
				e -> !(e.getType().is(oceanOffspringTag) && e != target));

		for (LivingEntity entityiterator : entities) {
			if (center.distanceToSqr(entityiterator.position()) <= radius * radius) {
				entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.ISHARMLA_ATTACK, this), (float) damage);
			}
		}
	}

	private void transformToMonster() {
		LevelAccessor world = this.level();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();

		if (this.getEntityData().get(DATA_IS_MONSTER)) {
			return;
		}

		if (world instanceof Level level) {
			level.playSound(null, BlockPos.containing(x, y, z), CASounds.ISHARMLA_TO_MONSTERR.get(), SoundSource.HOSTILE, 3, 1);
		}

		transformParticleLoop(world, x, y, z, 0, 10, 2, true);

		double currentHealth = this.getHealth();
		double maxHealth = this.getMaxHealth();

		this.setAnimation("animation.isharmla.to_monster");
		this.getEntityData().set(DATA_DURATION, 40);
		this.getEntityData().set(DATA_SKILLP_1, 200);
		this.getEntityData().set(DATA_SKILLP_2, 100);
		this.getEntityData().set(DATA_RECORDED_HEALTH, (int) ((currentHealth / maxHealth) * 1000));
		this.getEntityData().set(DATA_IS_MONSTER, true);

		this.setHealth((float) maxHealth);
		if (!this.level().isClientSide())
			this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 40, 9, false, false));
		if (!this.level().isClientSide())
			this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 99999, 2, false, false));

		final Vec3 center = new Vec3(x, y, z);
		List<IsharmlaTearEntity> nearbyEntities = world.getEntitiesOfClass(IsharmlaTearEntity.class, new AABB(center, center).inflate(32), e -> true);

		for (IsharmlaTearEntity entityiterator : nearbyEntities) {
			entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.OCEANKILLER_DAMAGE),
					114514);//TODO 好臭的伤害
		}
	}

	private boolean transformToHuman() {
		LevelAccessor world = this.level();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();

		if (!this.getEntityData().get(DATA_IS_MONSTER)) {
			return false;
		}

		transformParticleLoop(world, x, y, z, 0, 10, 2, false);

		if (world instanceof Level level) {
			level.playSound(null, BlockPos.containing(x, y, z), CASounds.ISHARMLA_TO_HUMAN.get(), SoundSource.HOSTILE, 3, 1);
		}

		double recordedHealth = this.getEntityData().get(DATA_RECORDED_HEALTH);
		double maxHealth = this.getMaxHealth();

		this.setAnimation("animation.isharmla.to_human");

		this.removeEffect(MobEffects.MOVEMENT_SPEED);
		this.getEntityData().set(DATA_DURATION, 40);
		this.getEntityData().set(DATA_SKILLP_1, 2400);
		this.getEntityData().set(DATA_SKILLP_2, 280);
		this.getEntityData().set(DATA_IS_MONSTER, false);

		this.removeEffect(CAMobEffects.INVULNERABLE);
		this.setHealth((float) Math.max(maxHealth * Math.min(recordedHealth * 0.001 + 0.03, 1), 1));
		if (!this.level().isClientSide())
			this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 40, 9, false, false));

		return true;
	}

	private void transformParticleLoop(LevelAccessor world, double x, double y, double z, int startIter, int totalIter, int ticks, boolean expanding) {
		final int currentIter = startIter;
		CaerulaArborMod.queueServerWork(ticks, () -> {
			if (totalIter > currentIter + 1) {
				for (int index0 = 0; index0 < 120; index0++) {
					int radius = expanding ? currentIter : (10 - currentIter);
					if (world instanceof ServerLevel level)
						level.sendParticles(CAParticles.EDERMAN_PTC.get(), (x + radius * Math.sin(Math.toRadians(3 * index0))), (y + 0.125),
								(z + radius * Math.cos(Math.toRadians(3 * index0))), 2, 0.05, 0.05, 0.05, 0.1);
					if (world instanceof ServerLevel level)
						level.sendParticles(ParticleTypes.END_ROD, (x + radius * Math.sin(Math.toRadians(3 * index0))), (y + 0.125), (z + radius * Math.cos(Math.toRadians(3 * index0))), 1, 0.05, 0.05, 0.05, 0.1);
					if (world instanceof ServerLevel level)
						level.sendParticles(CAParticles.EDERMAN_PTC.get(), (x + 2 * Math.sin(Math.toRadians(3 * index0))), (y + currentIter * 0.5), (z + 2 * Math.cos(Math.toRadians(3 * index0))), 2,
								0.05, 0.05, 0.05, 0.1);
					if (world instanceof ServerLevel level)
						level.sendParticles(ParticleTypes.END_ROD, (x + 2 * Math.sin(Math.toRadians(3 * index0))), (y + currentIter * 0.5), (z + 2 * Math.cos(Math.toRadians(3 * index0))), 1, 0.05, 0.05, 0.05, 0.1);
				}
				transformParticleLoop(world, x, y, z, currentIter + 1, totalIter, ticks, expanding);
			}
		});
	}


	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}
}