package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.util.EntityPredicateUtils;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
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
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
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

public class LastKnightAndHorseEntity extends Animal implements GeoEntity {

	private boolean isLastKnightStarting() {
		return EntityPredicateUtils.isLastKnightStarting(this);
	}
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(LastKnightAndHorseEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(LastKnightAndHorseEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(LastKnightAndHorseEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_addiiton = SynchedEntityData.defineId(LastKnightAndHorseEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_skillp = SynchedEntityData.defineId(LastKnightAndHorseEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_duration = SynchedEntityData.defineId(LastKnightAndHorseEntity.class, EntityDataSerializers.INT);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";
	private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.WHITE, ServerBossEvent.BossBarOverlay.NOTCHED_6);

	public LastKnightAndHorseEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CaerulaArborModEntities.LAST_KNIGHT_AND_HORSE.get(), world);
	}

	public LastKnightAndHorseEntity(EntityType<LastKnightAndHorseEntity> type, Level world) {
		super(type, world);
		xpReward = 64;
		setNoAi(false);
		setMaxUpStep(1.25f);
		setPersistenceRequired();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "knight_amd_horse");
		this.entityData.define(DATA_addiiton, 0);
		this.entityData.define(DATA_skillp, 140);
		this.entityData.define(DATA_duration, 0);
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
				return super.canUse() && isLastKnightStarting();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && isLastKnightStarting();
			}
		});
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.33, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 36;
			}

			@Override
			public boolean canUse() {
				return super.canUse() && isLastKnightStarting();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && isLastKnightStarting();
			}

		});
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Monster.class, true, false) {
			@Override
			public boolean canUse() {
				return super.canUse() && isLastKnightStarting();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && isLastKnightStarting();
			}
		});
		this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1) {
			@Override
			public boolean canUse() {
				return super.canUse() && isLastKnightStarting();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && isLastKnightStarting();
			}
		});
		this.goalSelector.addGoal(5, new RandomLookAroundGoal(this) {
			@Override
			public boolean canUse() {
				return super.canUse() && isLastKnightStarting();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && isLastKnightStarting();
			}
		});
		this.goalSelector.addGoal(6, new FloatGoal(this));
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEFINED;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "last_knight_hit"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "last_knight_hit"));
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		EntityUtils.applyLastKnightFreeze(this.level(), this, source.getEntity());
		if (source.is(DamageTypes.IN_FIRE))
			return false;
		if (source.getDirectEntity() instanceof ThrownPotion || source.getDirectEntity() instanceof AreaEffectCloud)
			return false;
		if (source.is(DamageTypes.FALL))
			return false;
		if (source.is(DamageTypes.CACTUS))
			return false;
		if (source.is(DamageTypes.DROWN))
			return false;
		return super.hurt(source, amount);
	}

	@Override
	public void die(DamageSource source) {
		super.die(source);
        Entity sourceentity = source.getEntity();
        if (sourceentity == null)
            return;
        if (sourceentity instanceof ServerPlayer _player) {
            Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "kill_knight_and_horse"));
            AdvancementProgress _ap = null;
            if (_adv != null) {
				_ap = _player.getAdvancements().getOrStartProgress(_adv);
				if (!_ap.isDone()) {
					for (String criteria : _ap.getRemainingCriteria())
						_player.getAdvancements().award(_adv, criteria);
				}
			}
        }
    }

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
		EntityUtils.initLastKnightAttributes(this);
		this.setAnimation("animation.last_knight_horse.start");
		if (this.getAttributes().hasAttribute(ForgeMod.SWIM_SPEED.get()))
			this.getAttribute(ForgeMod.SWIM_SPEED.get())
					.setBaseValue((this.getAttributes().hasAttribute(ForgeMod.SWIM_SPEED.get()) ? this.getAttribute(ForgeMod.SWIM_SPEED.get()).getBaseValue() : 0) * 12);
		new Object() {
			void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
				if (isAlive()) {
					LastKnightAndHorseEntity.this.setHealth((float) (LastKnightAndHorseEntity.this.getMaxHealth() * (1 - (timedloopiterator + 1) * 0.0125)));
				}
				final int tick2 = ticks;
				CaerulaArborMod.queueServerWork(tick2, () -> {
					if (timedlooptotal > timedloopiterator + 1) {
						timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
					}
				});
			}
		}.timedLoop(0, 40, 1);
		return retval;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putInt("Dataaddiiton", this.entityData.get(DATA_addiiton));
		compound.putInt("Dataskillp", this.entityData.get(DATA_skillp));
		compound.putInt("Dataduration", this.entityData.get(DATA_duration));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("Dataaddiiton"))
			this.entityData.set(DATA_addiiton, compound.getInt("Dataaddiiton"));
		if (compound.contains("Dataskillp"))
			this.entityData.set(DATA_skillp, compound.getInt("Dataskillp"));
		if (compound.contains("Dataduration"))
			this.entityData.set(DATA_duration, compound.getInt("Dataduration"));
	}

	@Override
	public void awardKillScore(Entity entity, int score, DamageSource damageSource) {
		super.awardKillScore(entity, score, damageSource);
        LevelAccessor world = this.level();
        if ((this.getHealth()) < (this.getMaxHealth())) {
            this.setHealth((float) ((this.getHealth()) + (this.getMaxHealth()) * 0.03));
            if (world instanceof ServerLevel _level)
                _level.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getX(), (this.getY() + 0.75), this.getZ(), 32, 1, 2, 1, 0.1);
        }
    }

	@Override
	public void baseTick() {
		super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity enemy = null;
        double add = 0;
        double duration = 0;
        double skillp = 0;
        if (this.isAlive()) {
            if (tickCount % 10 == 0) {
                this.removeEffect(CaerulaArborModMobEffects.DIZZY.get());
                this.removeEffect(CaerulaArborModMobEffects.FROZEN.get());
                this.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
                if (!this.level().isClientSide())
                    this.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20, 0, false, false));
                if ((this.getHealth()) > (this.getMaxHealth()) * 0.5) {
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(48 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (entityiterator.getTicksFrozen() >= 125 && entityiterator.isAlive()) {
							entityiterator.setTicksFrozen(200);
							if (entityiterator instanceof LivingEntity _entity && !this.level().isClientSide())
								this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.FROZEN.get(), 20, 0, false, false));
						}
					}
                }
                enemy = this.getTarget();
                if (!(enemy == null) && enemy.isAlive()) {
                    add = this.getEntityData().get(DATA_addiiton);
                    if (add < 20) {
                        this.getEntityData().set(DATA_addiiton, (int) (add + 1));
                    }
                }
            }
            if (EntityUtils.getSpeed(this) > (this.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED) ? this.getAttribute(Attributes.MOVEMENT_SPEED).getValue() : 0)
                    * 1.25) {
                setDeltaMovement(new Vec3(0, 0, 0));
            }
            setTicksFrozen(0);
            this.removeEffect(CaerulaArborModMobEffects.FROZEN.get());
            this.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            this.removeEffect(CaerulaArborModMobEffects.DIZZY.get());
            skillp = this.getEntityData().get(DATA_skillp);
            duration = this.getEntityData().get(DATA_duration);
            if (duration > 0) {
                this.getEntityData().set(DATA_duration, (int) (duration - 1));
            }
            enemy = this.getTarget();
            if (skillp > 0) {
                this.getEntityData().set(DATA_skillp, (int) (skillp - 1));
            } else {
                if (!(enemy == null) && enemy.isAlive()) {
                    if (distanceTo(enemy) < 4) {
                        this.getEntityData().set(DATA_duration, 40);
                        this.getEntityData().set(DATA_skillp, 240);
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 32, 0, false, false));
                        this.setAnimation("animation.last_knight_horse.skill");
                        CaerulaArborMod.queueServerWork(13, () -> {
                            Entity enemy1 = null;
                            double damage = 0;
                            damage = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
                            enemy1 = this.getTarget();
                            {
                                final Vec3 _center = new Vec3((x + 2 * getLookAngle().x), y, (z + 2 * getLookAngle().z));
                                List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(8 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                                for (Entity entityiterator : _entfound) {
                                    if (!(entityiterator instanceof LivingEntity)) {
                                        continue;
                                    }
                                    if (entityiterator == this) {
                                        continue;
                                    }
                                    if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "is_humanside"))) && !(entityiterator == enemy1)) {
                                        continue;
                                    }
                                    if (distanceTo(entityiterator) <= 4) {
                                        entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "last_knight_attack"))), this),
                                                (float) (damage * 1.5));
                                        entityiterator.push(0, 0.64, 0);
                                        if ((entityiterator instanceof LivingEntity _entUseItem12 ? _entUseItem12.getUseItem() : ItemStack.EMPTY).getItem() instanceof ShieldItem) {
                                            if (entityiterator instanceof Player _player) {
                                                _player.getCooldowns().addCooldown(_player.getUseItem().getItem(), 100);
                                            }
                                            if (world instanceof Level _level) {
                                                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.shield.break")), SoundSource.HOSTILE, 1, 1);
                                            }
                                        }
                                        CaerulaArborMod.queueServerWork(7, () -> {
                                            entityiterator.hurt(
                                                    new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "last_knight_attack"))), this),
                                                    (float) ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 2));
                                            if (entityiterator instanceof LivingEntity _entity && !this.level().isClientSide())
                                                this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ROCK_BREAK.get(), 150, 0, false, false));
                                            entityiterator.push(0, (-1), 0);
                                        });
                                    }
                                }
                            }
                        });
                    }
                }
            }
        }
        this.refreshDimensions();
	}

	@Override
	public EntityDimensions getDimensions(Pose p_33597_) {
		return super.getDimensions(p_33597_).scale((float) 1);
	}

	@Override
	public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageable) {
		LastKnightAndHorseEntity retval = CaerulaArborModEntities.LAST_KNIGHT_AND_HORSE.get().create(serverWorld);
		retval.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(retval.blockPosition()), MobSpawnType.BREEDING, null, null);
		return retval;
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return List.of().contains(stack.getItem());
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

	@Override
	public void aiStep() {
		super.aiStep();
		this.updateSwingTime();
	}

	public static void init() {
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.25);
		builder = builder.add(Attributes.MAX_HEALTH, 400);
		builder = builder.add(Attributes.ARMOR, 24);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 20);
		builder = builder.add(Attributes.FOLLOW_RANGE, 36);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
		builder = builder.add(Attributes.ATTACK_KNOCKBACK, 0.5);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

			) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.last_knight_horse.move"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.last_knight_horse.die"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.last_knight_horse.idle"));
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
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.last_knight_horse.atack"));
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
			this.remove(LastKnightAndHorseEntity.RemovalReason.KILLED);
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

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
}
