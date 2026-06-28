package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CaerulaArborModAttributes;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import com.apocalypse.caerulaarbor.init.CaerulaArborModItems;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.util.EntityPredicateUtils;
import net.minecraft.client.Minecraft;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
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

public class ComplexChitinGolemEntity extends IronGolem implements GeoEntity {

	private boolean isDurative() {
		return EntityPredicateUtils.isDurative(this);
	}
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(ComplexChitinGolemEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(ComplexChitinGolemEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(ComplexChitinGolemEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_skillp = SynchedEntityData.defineId(ComplexChitinGolemEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_duration = SynchedEntityData.defineId(ComplexChitinGolemEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Boolean> DATA_rooted = SynchedEntityData.defineId(ComplexChitinGolemEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Integer> DATA_rootX = SynchedEntityData.defineId(ComplexChitinGolemEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_rootZ = SynchedEntityData.defineId(ComplexChitinGolemEntity.class, EntityDataSerializers.INT);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public ComplexChitinGolemEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CaerulaArborModEntities.COMPLEX_CHITIN_GOLEM.get(), world);
	}

	public ComplexChitinGolemEntity(EntityType<ComplexChitinGolemEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);
		setMaxUpStep(1.6f);
		setPersistenceRequired();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "complex_chitin_golem");
		this.entityData.define(DATA_skillp, 200);
		this.entityData.define(DATA_duration, 0);
		this.entityData.define(DATA_rooted, false);
		this.entityData.define(DATA_rootX, 0);
		this.entityData.define(DATA_rootZ, 0);
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
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 2, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 12.25;
			}

			@Override
			public boolean canUse() {
				return super.canUse() && isDurative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && isDurative();
			}

		});
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Monster.class, true, false));
		this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1));
		this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
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
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.iron_golem.hurt"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.iron_golem.death"));
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.IN_FIRE))
			return false;
		if (source.is(DamageTypes.FALL))
			return false;
		if (source.is(DamageTypes.DROWN))
			return false;
		return super.hurt(source, amount);
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
        if (this.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_RATE.get()))
            this.getAttribute(CaerulaArborModAttributes.SANITY_RATE.get()).setBaseValue(8);
        if (this.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get()))
            this.getAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get()).setBaseValue(0.05);
        if (this.getAttributes().hasAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()))
            this.getAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()).setBaseValue(50);
        if (this.getAttributes().hasAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get()))
            this.getAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get()).setBaseValue(12);
        return retval;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putInt("Dataskillp", this.entityData.get(DATA_skillp));
		compound.putInt("Dataduration", this.entityData.get(DATA_duration));
		compound.putBoolean("Datarooted", this.entityData.get(DATA_rooted));
		compound.putInt("DatarootX", this.entityData.get(DATA_rootX));
		compound.putInt("DatarootZ", this.entityData.get(DATA_rootZ));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("Dataskillp"))
			this.entityData.set(DATA_skillp, compound.getInt("Dataskillp"));
		if (compound.contains("Dataduration"))
			this.entityData.set(DATA_duration, compound.getInt("Dataduration"));
		if (compound.contains("Datarooted"))
			this.entityData.set(DATA_rooted, compound.getBoolean("Datarooted"));
		if (compound.contains("DatarootX"))
			this.entityData.set(DATA_rootX, compound.getInt("DatarootX"));
		if (compound.contains("DatarootZ"))
			this.entityData.set(DATA_rootZ, compound.getInt("DatarootZ"));
	}

	@Override
	public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
		ItemStack itemstack = sourceentity.getItemInHand(hand);
		InteractionResult retval = InteractionResult.sidedSuccess(this.level().isClientSide());
		super.mobInteract(sourceentity, hand);
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
        Level world = this.level();
        double scale = 0;
        if (this.getHealth() >= this.getMaxHealth()) {
            return InteractionResult.PASS;
        }
        if (sourceentity.getMainHandItem().getItem() == CaerulaArborModItems.OCEAN_CHITIN.get()) {
            scale = 0.15;
            if (!(new Object() {
                public boolean checkGamemode(Entity _ent) {
                    if (_ent instanceof ServerPlayer _serverPlayer) {
                        return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                    } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                        return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                    }
                    return false;
                }
            }.checkGamemode((Entity) sourceentity))) {
                if ((Entity) sourceentity instanceof Player _player) {
                    ItemStack _stktoremove = new ItemStack(CaerulaArborModItems.OCEAN_CHITIN.get());
                    _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                }
            }
        } else {
            if (sourceentity.getMainHandItem().getItem() == CaerulaArborModItems.COMPLEX_CHITIN.get()) {
                scale = 0.25;
                if (!(new Object() {
                    public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) sourceentity))) {
                    if ((Entity) sourceentity instanceof Player _player) {
                        ItemStack _stktoremove = new ItemStack(CaerulaArborModItems.COMPLEX_CHITIN.get());
                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                    }
                }
            }
        }
        if (scale > 0) {
            this.setHealth((float) (this.getHealth() + this.getMaxHealth() * scale));
            if ((LevelAccessor) world instanceof Level _level) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.iron_golem.repair")), SoundSource.PLAYERS, 1, 1);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

	@Override
	public void baseTick() {
		super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity enemy = null;
        double sklp1 = 0;
        double dura = 0;
        updateTexture();
        if (this.isAlive()) {
            LivingEntity _entity = (LivingEntity) (Entity) this;
            _entity.removeEffect(CaerulaArborModMobEffects.DIZZY.get());
            boolean root = false;
            double rx = 0;
            double rz = 0;
            double dist = 0;
            double dist1 = 0;
            root = (Entity) this instanceof ComplexChitinGolemEntity _datEntL0 && _datEntL0.getEntityData().get(DATA_rooted);
            if (!root) {
                if (!(getDisplayName().getString()).equals(getType().getDescription().getString())) {
                    if ((Entity) this instanceof ComplexChitinGolemEntity _datEntSetI1)
                        _datEntSetI1.getEntityData().set(DATA_rootX, (int) Math.round(x));
                    if ((Entity) this instanceof ComplexChitinGolemEntity _datEntSetI1)
                        _datEntSetI1.getEntityData().set(DATA_rootZ, (int) Math.round(z));
                    if ((Entity) this instanceof ComplexChitinGolemEntity _datEntSetL)
                        _datEntSetL.getEntityData().set(DATA_rooted, true);
                    CaerulaArborMod.LOGGER.info(("Complex Chitin Golem " + getDisplayName().getString() + "has recognize x:" + Math.round(x) + " z:" + Math.round(z) + " as base"));
                }
            } else if (Math.random() < 0.01) {
				Mob _mobEnt8 = (Mob) (Entity) this;
				if (!_mobEnt8.isAggressive()) {
					rx = x - ((Entity) this instanceof ComplexChitinGolemEntity _datEntI1 ? _datEntI1.getEntityData().get(DATA_rootX) : 0);
					rz = z - ((Entity) this instanceof ComplexChitinGolemEntity _datEntI1 ? _datEntI1.getEntityData().get(DATA_rootZ) : 0);
					dist = new Vec3(0, 0, 0).distanceTo(new Vec3(rx, 0, rz));
					if (dist >= 24) {
						dist1 = Mth.nextDouble(RandomSource.create(), 4, 16);
						Mob _entity1 = (Mob) (Entity) this;
						_entity1.getNavigation().moveTo(x - rx * dist1 / dist, y, z - rz * dist1 / dist, 1);
					}
				}
			}
            sklp1 = (Entity) this instanceof ComplexChitinGolemEntity _datEntI ? _datEntI.getEntityData().get(DATA_skillp) : 0;
            dura = (Entity) this instanceof ComplexChitinGolemEntity _datEntI ? _datEntI.getEntityData().get(DATA_duration) : 0;
            enemy = (Entity) this instanceof Mob _mobEnt ? _mobEnt.getTarget() : null;
            if (dura > 0) {
                if ((Entity) this instanceof ComplexChitinGolemEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_duration, (int) (dura - 1));
            }
            if (sklp1 > 0) {
                if ((Entity) this instanceof ComplexChitinGolemEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_skillp, (int) (sklp1 - 1));
            } else {
                if (!(enemy == null) && enemy.isAlive()) {
                    if (distanceTo(enemy) <= 5 && dura < 1) {
                        if ((Entity) this instanceof ComplexChitinGolemEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_duration, 110);
                        if (this instanceof ComplexChitinGolemEntity) {
                            this.setAnimation("animation.complex_chitin_golem.spin");
                        }
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 110, 9, false, false));
                        if ((Entity) this instanceof ComplexChitinGolemEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_skillp, 400);
                        CaerulaArborMod.queueServerWork(6, () -> {
                            if (this.isAlive()) {
                                if (world instanceof Level _level) {
                                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.piston.extend")), SoundSource.NEUTRAL, 2, 1);
                                }
                            }
                        });
                        CaerulaArborMod.queueServerWork(13, () -> {
                            if (this.isAlive()) {
                                if (world instanceof Level _level) {
                                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.piston.contract")), SoundSource.NEUTRAL, 2, 1);
                                }
                            }
                        });
                        CaerulaArborMod.queueServerWork(20, () -> {
                            if (this.isAlive()) {
                                if (!this.level().isClientSide())
                                    this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 60, 0, false, false));
                            }
                        });
                        for (int index0 = 0; index0 < 16; index0++) {
                            CaerulaArborMod.queueServerWork(index0 * 3 + 26, () -> {
                                if (this.isAlive()) {
                                    Entity enemy1 = null;
                                    double damage = 0;
                                    double r = 0;
                                    {
                                        final Vec3 _center = new Vec3((getX()), (getY()), (getZ()));
                                        List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(10 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                                        for (Entity entityiterator : _entfound) {
                                            if (!(entityiterator instanceof Monster)) {
                                                if (!(entityiterator == this.getTarget())) {
                                                    continue;
                                                }
                                            }
                                            if (entityiterator == this) {
                                                continue;
                                            }
                                            if (entityiterator != null && distanceTo(entityiterator) <= 5) {
												damage = (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 0.75;
												entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "golem_attack"))), this),
														(float) damage);
											}
                                        }
                                    }
                                }
                            });
                        }
                        CaerulaArborMod.queueServerWork(90, () -> {
                            if (this.isAlive()) {
                                if (world instanceof Level _level) {
                                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.piston.contract")), SoundSource.NEUTRAL, 2, 1);
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
	public void aiStep() {
		super.aiStep();
		this.updateSwingTime();
	}

	public static void init() {
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.17);
		builder = builder.add(Attributes.MAX_HEALTH, 675);
		builder = builder.add(Attributes.ARMOR, 16);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 28);
		builder = builder.add(Attributes.FOLLOW_RANGE, 20);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

					&& !this.isAggressive()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.complex_chitin_golem.move"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.complex_chitin_golem.die"));
			}
			if (this.isAggressive() && event.isMoving()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.complex_chitin_golem.run"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.complex_chitin_golem.idle"));
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
		if (this.swinging && this.lastSwing + 10L <= level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.complex_chitin_golem.attack"));
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
			this.remove(ComplexChitinGolemEntity.RemovalReason.KILLED);
			this.dropExperience();
		}
	}

	public String getSyncedAnimation() {
		return this.entityData.get(ANIMATION);
	}

	public void setAnimation(String animation) {
		this.entityData.set(ANIMATION, animation);
	}

	private void updateTexture() {
		double perc = this.getHealth() / this.getMaxHealth();
		if (perc < 0.25) {
			this.setTexture("complex_chitin_golem_3");
		} else if (perc < 0.5) {
			this.setTexture("complex_chitin_golem_2");
		} else if (perc < 0.75) {
			this.setTexture("complex_chitin_golem_1");
		} else {
			this.setTexture("complex_chitin_golem");
		}
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		data.add(new AnimationController<>(this, "movement", 1, this::movementPredicate));
		data.add(new AnimationController<>(this, "attacking", 1, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 1, this::procedurePredicate));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
}
