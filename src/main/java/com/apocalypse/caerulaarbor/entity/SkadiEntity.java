package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.utils.EntityPredicateUtils;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.EntityAnchorArgument;
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
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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

public class SkadiEntity extends Animal implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(SkadiEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(SkadiEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(SkadiEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_relax_cooldown = SynchedEntityData.defineId(SkadiEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_skillp = SynchedEntityData.defineId(SkadiEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_phase = SynchedEntityData.defineId(SkadiEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_skillp2 = SynchedEntityData.defineId(SkadiEntity.class, EntityDataSerializers.INT);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public SkadiEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CaerulaArborModEntities.SKADI.get(), world);
	}

	public SkadiEntity(EntityType<SkadiEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);
		setMaxUpStep(1.2f);
		setPersistenceRequired();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "hunter_skadi");
		this.entityData.define(DATA_relax_cooldown, 200);
		this.entityData.define(DATA_skillp, 120);
		this.entityData.define(DATA_phase, 0);
		this.entityData.define(DATA_skillp2, 0);
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
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.15, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 9;
			}

			@Override
			public boolean canUse() {
				double x = SkadiEntity.this.getX();
				double y = SkadiEntity.this.getY();
				double z = SkadiEntity.this.getZ();
				Entity entity = SkadiEntity.this;
				Level world = SkadiEntity.this.level();
                if (!super.canUse()) return false;
                return EntityPredicateUtils.isNotFakeDying(entity);
			}

			@Override
			public boolean canContinueToUse() {
				double x = SkadiEntity.this.getX();
				double y = SkadiEntity.this.getY();
				double z = SkadiEntity.this.getZ();
				Entity entity = SkadiEntity.this;
				Level world = SkadiEntity.this.level();
                if (!super.canContinueToUse()) return false;
                return EntityPredicateUtils.isNotFakeDying(entity);
			}

		});
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Monster.class, true, false));
		this.goalSelector.addGoal(4, new OpenDoorGoal(this, false));
		this.goalSelector.addGoal(5, new OpenDoorGoal(this, true));
		this.goalSelector.addGoal(6, new RandomStrollGoal(this, 1));
		this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(8, new FloatGoal(this));
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
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "skadi_hit"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "skadi_died"));
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity sourceentity = source.getEntity();
        if (this != null && sourceentity != null) {
            double sklp = 0;
            if (!new Object() {
                public boolean checkGamemode(Entity _ent) {
                    if (_ent instanceof ServerPlayer _serverPlayer) {
                        return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                    } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                        return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                    }
                    return false;
                }
            }.checkGamemode(sourceentity)) {
                boolean result;
                result = EntityPredicateUtils.isNotFakeDying(this);
                if (result) {
                    sklp = (Entity) this instanceof SkadiEntity _datEntI ? _datEntI.getEntityData().get(DATA_skillp2) : 0;
                    if (sklp <= 0 && this.isAlive()) {
                        if ((sourceentity != null ? distanceTo(sourceentity) : -1) <= 5) {
                            if ((Entity) this instanceof SkadiEntity _datEntSetI)
                                _datEntSetI.getEntityData().set(DATA_skillp2, 120);
                            if (this instanceof SkadiEntity) {
                                ((SkadiEntity) this).setAnimation("animation.skadi.skill");
                            }
                            ((Entity) this).lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((sourceentity.getX()), (sourceentity.getY()), (sourceentity.getZ())));
                            if (!this.level().isClientSide())
                                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 9, false, false));
                            CaerulaArborMod.queueServerWork(16, () -> {
                                if (this.isAlive()) {
                                    if ((sourceentity != null ? distanceTo(sourceentity) : -1) <= 5) {
                                        sourceentity.hurt(
                                                new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hunter_attack"))), this),
                                                (float) ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0)
                                                        * 2.5));
                                        if (sourceentity instanceof LivingEntity _entity && !this.level().isClientSide())
                                            this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.DIZZY.get(), 100, 0, false, false));
                                        sourceentity.push((getLookAngle().x + 0.33), 0, (getLookAngle().z + 0.33));
                                    }
                                    if (world instanceof Level _level) {
                                        if (!_level.isClientSide()) {
                                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.dragon_fireball.explode")), SoundSource.HOSTILE, 2, 1);
                                        } else {
                                            _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.dragon_fireball.explode")), SoundSource.HOSTILE, 2, 1, false);
                                        }
                                    }
                                    if (this == null || sourceentity == null)
                                        return;
                                    double sklp1 = 0;
                                    double ddd = 0;
                                    ddd = (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 2.25;
                                    {
                                        final Vec3 _center = new Vec3((x + 2 * getLookAngle().x), y, (z + 2 * getLookAngle().z));
                                        List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(5 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                                        for (Entity entityiterator : _entfound) {
                                            if (!(entityiterator instanceof LivingEntity)) {
                                                continue;
                                            }
                                            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "is_humanside")))) {
                                                continue;
                                            }
                                            if (!(entityiterator == sourceentity)) {
                                                continue;
                                            }
                                            if ((entityiterator != null ? distanceTo(entityiterator) : -1) <= 3) {
                                                entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hunter_attack"))), this),
                                                        (float) ddd);
                                                if (entityiterator instanceof LivingEntity _entity && !this.level().isClientSide())
                                                    this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.DIZZY.get(), 100, 0, false, false));
                                                entityiterator.push((getLookAngle().x + 0.33), 0, (getLookAngle().z + 0.33));
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
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
		EntityUtils.initHunter(this);
		return retval;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putInt("Datarelax_cooldown", this.entityData.get(DATA_relax_cooldown));
		compound.putInt("Dataskillp", this.entityData.get(DATA_skillp));
		compound.putInt("Dataphase", this.entityData.get(DATA_phase));
		compound.putInt("Dataskillp2", this.entityData.get(DATA_skillp2));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("Datarelax_cooldown"))
			this.entityData.set(DATA_relax_cooldown, compound.getInt("Datarelax_cooldown"));
		if (compound.contains("Dataskillp"))
			this.entityData.set(DATA_skillp, compound.getInt("Dataskillp"));
		if (compound.contains("Dataphase"))
			this.entityData.set(DATA_phase, compound.getInt("Dataphase"));
		if (compound.contains("Dataskillp2"))
			this.entityData.set(DATA_skillp2, compound.getInt("Dataskillp2"));
	}

	@Override
	public void baseTick() {
		super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        if (this != null) {
            double rlx = 0;
            double sklp = 0;
            double sklp2 = 0;
            if (this.isAlive()) {
                if (Math.random() < 0.001) {
                    rlx = (Entity) this instanceof SkadiEntity _datEntI ? _datEntI.getEntityData().get(DATA_relax_cooldown) : 0;
                    if (rlx <= 0) {
                        if (!((Entity) this instanceof Mob _mobEnt2 && _mobEnt2.isAggressive())) {
                            if ((Entity) this instanceof Mob _entity)
                                _entity.getNavigation().stop();
                            if (this instanceof SkadiEntity) {
                                ((SkadiEntity) this).setAnimation("animation.skadi.relax");
                            }
                            rlx = 320;
                        }
                    }
                }
                if (rlx > 0) {
                    rlx = rlx - 1;
                }
                sklp = (Entity) this instanceof SkadiEntity _datEntI ? _datEntI.getEntityData().get(DATA_skillp) : 0;
                sklp2 = (Entity) this instanceof SkadiEntity _datEntI ? _datEntI.getEntityData().get(DATA_skillp2) : 0;
                if (sklp <= 0) {
                    if (!(((Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) == null)) {
                        if ((((Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) != null ? distanceTo(((Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null)) : -1) < 3) {
                            if (this instanceof SkadiEntity) {
                                ((SkadiEntity) this).setAnimation("animation.skadi.spin");
                            }
                            if (!this.level().isClientSide())
                                this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 40, 0, false, false));
                            CaerulaArborMod.queueServerWork(10, () -> {
                                spinAttack(1.5);
                            });
                            CaerulaArborMod.queueServerWork(14, () -> {
                                spinAttack(2);
                            });
                            CaerulaArborMod.queueServerWork(20, () -> {
                                spinAttack(1.5);
                            });
                            if (EntityPredicateUtils.isSpecterAround(world, x, y, z)) {
                                sklp = 170;
                            } else {
                                sklp = 200;
                            }
                        }
                    }
                } else {
                    sklp = sklp - 1;
                }
                if (sklp2 > 0) {
                    if ((Entity) this instanceof SkadiEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_skillp2, (int) (sklp2 - 1));
                }
                if ((Entity) this instanceof SkadiEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_relax_cooldown, (int) rlx);
                if ((Entity) this instanceof SkadiEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_skillp, (int) sklp);
                EntityUtils.healFromGladiia(world, x, y, z, this);
                if (this != null) {
                    if (tickCount % 10 == 0) {
                        {
                            final Vec3 _center = new Vec3(x, y, z);
                            List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(48 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                            for (Entity entityiterator : _entfound) {
                                if (!entityiterator.isAlive()) {
                                    continue;
                                }
                                if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hunters")))) {
                                    if (!(entityiterator instanceof LivingEntity _livEnt3 && _livEnt3.hasEffect(CaerulaArborModMobEffects.ADD_ATTACK_PERCLY.get()))) {
                                        if (entityiterator instanceof LivingEntity _entity && !this.level().isClientSide())
                                            this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ADD_ATTACK_PERCLY.get(), 32768, 0, false, false));
                                    }
                                }
                            }
                        }
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
		SkadiEntity retval = CaerulaArborModEntities.SKADI.get().create(serverWorld);
		retval.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(retval.blockPosition()), MobSpawnType.BREEDING, null, null);
		return retval;
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return List.of().contains(stack.getItem());
	}

	@Override
	public void aiStep() {
		super.aiStep();
		this.updateSwingTime();
	}

	public void spinAttack(double damageMultiplier) {
		Entity target = this.getTarget();
		double damage = (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttributeValue(Attributes.ATTACK_DAMAGE) : 0) * damageMultiplier;
		Vec3 center = new Vec3(this.getX(), this.getY(), this.getZ());
		List<Entity> entities = this.level().getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(6 / 2d), e -> true).stream()
				.sorted(Comparator.comparingDouble(e -> e.distanceToSqr(center))).toList();
		for (Entity entity : entities) {
			if (!(entity instanceof Monster)) {
				if (!(entity == target || (entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) == this)) {
					continue;
				}
			}
			if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "is_humanside")))) {
				if (!(entity == target || (entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) == this)) {
					continue;
				}
			}
			if (this.distanceTo(entity) < 3.5) {
				entity.hurt(new DamageSource(this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
						.getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hunter_attack"))), this), (float) damage);
			}
		}
	}

	public static void init() {
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.18);
		builder = builder.add(Attributes.MAX_HEALTH, 270);
		builder = builder.add(Attributes.ARMOR, 5);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 38);
		builder = builder.add(Attributes.FOLLOW_RANGE, 32);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

					&& !this.isAggressive()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.skadi.move"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.skadi.die"));
			}
			if (this.isAggressive() && event.isMoving()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.skadi.sprint"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.skadi.idle"));
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
		if (this.swinging && this.lastSwing + 20L <= level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.skadi.attack"));
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
			this.remove(SkadiEntity.RemovalReason.KILLED);
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
