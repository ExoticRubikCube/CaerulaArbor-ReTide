package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.*;
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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
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
import net.minecraft.world.level.block.state.BlockState;
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

public class ChitinGolemEntity extends IronGolem implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(ChitinGolemEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(ChitinGolemEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(ChitinGolemEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_rootX = SynchedEntityData.defineId(ChitinGolemEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_rootZ = SynchedEntityData.defineId(ChitinGolemEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Boolean> DATA_rooted = SynchedEntityData.defineId(ChitinGolemEntity.class, EntityDataSerializers.BOOLEAN);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public ChitinGolemEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CaerulaArborModEntities.CHITIN_GOLEM.get(), world);
	}

	public ChitinGolemEntity(EntityType<ChitinGolemEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);
		setMaxUpStep(1.5f);
		setPersistenceRequired();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "chitin_golem_0");
		this.entityData.define(DATA_rootX, 0);
		this.entityData.define(DATA_rootZ, 0);
		this.entityData.define(DATA_rooted, false);
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
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 12.25;
			}
		});
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Monster.class, true, false));
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
	public void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.iron_golem.step")), 0.15f, 1);
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
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity sourceentity = source.getEntity();
        if (this != null && sourceentity != null) {
            double num = 0;
            if (this.isAlive()) {
                if (!((Entity) this instanceof LivingEntity _livEnt1 && _livEnt1.hasEffect(CaerulaArborModMobEffects.COOLDOWN_SINAL.get()))) {
                    num = 0;
                    {
                        final Vec3 _center = new Vec3(x, y, z);
                        List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(8 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                        for (Entity entityiterator : _entfound) {
                            if (!(entityiterator == this) && (entityiterator instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) >= 10) {
                                num = num + 1;
                            }
                        }
                    }
                    if ((sourceentity != null ? distanceTo(sourceentity) : -1) <= 5) {
                        if (num >= 2) {
                            if (this instanceof ChitinGolemEntity) {
                                ((ChitinGolemEntity) this).setAnimation("animation.chitgolem.smash");
                            }
                            if (!this.level().isClientSide())
                                this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.COOLDOWN_SINAL.get(), 60, 0, false, false));
                            ((Entity) this).lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((sourceentity.getX()), (sourceentity.getY()), (sourceentity.getZ())));
                            CaerulaArborMod.queueServerWork(13, () -> {
                                if (world instanceof Level _level) {
                                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.iron_golem.damage")), SoundSource.HOSTILE, 2, 1);
                                }
                                {
                                    final Vec3 _center = new Vec3((x + 2 * getLookAngle().x), (y + 2), (z + 2 * getLookAngle().z));
                                    List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(4.5 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                                    for (Entity entityiterator : _entfound) {
                                        if (entityiterator instanceof Monster) {
                                            entityiterator.hurt(
                                                    new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "golem_attack"))), this),
                                                    (float) ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0)
                                                            * 3.5));
                                        } else if (entityiterator == ((Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null)) {
                                            entityiterator.hurt(
                                                    new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "golem_attack"))), this),
                                                    (float) ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0)
                                                            * 3.5));
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
        if (this != null) {
            if (this.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get()))
                this.getAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get()).setBaseValue(0.05);
            if (this.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_RATE.get()))
                this.getAttribute(CaerulaArborModAttributes.SANITY_RATE.get()).setBaseValue(10);
            if (this.getAttributes().hasAttribute(CaerulaArborModAttributes.MISSRATE.get()))
                this.getAttribute(CaerulaArborModAttributes.MISSRATE.get()).setBaseValue(33);
        }
        return retval;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putInt("DatarootX", this.entityData.get(DATA_rootX));
		compound.putInt("DatarootZ", this.entityData.get(DATA_rootZ));
		compound.putBoolean("Datarooted", this.entityData.get(DATA_rooted));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("DatarootX"))
			this.entityData.set(DATA_rootX, compound.getInt("DatarootX"));
		if (compound.contains("DatarootZ"))
			this.entityData.set(DATA_rootZ, compound.getInt("DatarootZ"));
		if (compound.contains("Datarooted"))
			this.entityData.set(DATA_rooted, compound.getBoolean("Datarooted"));
	}

	@Override
	public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
		ItemStack itemstack = sourceentity.getItemInHand(hand);
		InteractionResult retval = InteractionResult.sidedSuccess(this.level().isClientSide());
		super.mobInteract(sourceentity, hand);
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		Entity entity = this;
		Level world = this.level();
        if (entity == null || sourceentity == null)
            return InteractionResult.PASS;
        ItemStack mainHand = ItemStack.EMPTY;
        boolean isLowHealth = false;
        boolean isCreative = false;
        mainHand = ((Entity) sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).copy();
        isLowHealth = (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) < (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1);
        isCreative = new Object() {
            public boolean checkGamemode(Entity _ent) {
                if (_ent instanceof ServerPlayer _serverPlayer) {
                    return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                    return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                }
                return false;
            }
        }.checkGamemode((Entity) sourceentity);
        if (mainHand.getItem() == CaerulaArborModItems.OCEAN_CHITIN.get() && isLowHealth) {
            if (entity instanceof LivingEntity _entity)
                _entity.setHealth((float) ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) + (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.25));
            if ((LevelAccessor) world instanceof Level _level) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.iron_golem.repair")), SoundSource.PLAYERS, 1, 1);
            }
            if (!isCreative) {
                ((Entity) sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
            }
            return InteractionResult.SUCCESS;
        } else if (mainHand.getItem() == CaerulaArborModItems.CHITIN_INGOT.get() && isLowHealth) {
            if (entity instanceof LivingEntity _entity)
                _entity.setHealth((float) ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) + (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.5));
            if ((LevelAccessor) world instanceof Level _level) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.iron_golem.repair")), SoundSource.PLAYERS, 1, 1);
            }
            if (!isCreative) {
                ((Entity) sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
            }
            return InteractionResult.SUCCESS;
        } else if (mainHand.getItem() == CaerulaArborModBlocks.COMPLEX_CHITIN_BLOCK.get().asItem() && mainHand.getCount() >= 3) {
            if ((LevelAccessor) world instanceof Level _level) {
                if (!_level.isClientSide()) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.smithing_table.use")), SoundSource.PLAYERS, (float) 1.5, 1);
                } else {
                    _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.smithing_table.use")), SoundSource.PLAYERS, (float) 1.5, 1, false);
                }
            }
            if (!entity.level().isClientSide())
                entity.discard();
            if ((LevelAccessor) world instanceof ServerLevel _level) {
                Entity entityToSpawn = CaerulaArborModEntities.COMPLEX_CHITIN_GOLEM.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.setYRot(entity.getYRot());
                    entityToSpawn.setYBodyRot(entity.getYRot());
                    entityToSpawn.setYHeadRot(entity.getYRot());
                    entityToSpawn.setXRot(entity.getXRot());
                }
            }
            if (!isCreative) {
                ((Entity) sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(3);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

	@Override
	public void baseTick() {
		super.baseTick();
        double x = this.getX();
        double z = this.getZ();
        if (this != null) {
            boolean root = false;
            double rx = 0;
            double rz = 0;
            double dist = 0;
            double dist1 = 0;
            if (this != null) {
                double perc = 0;
                perc = ((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) / ((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1);
                if (perc < 0.25) {
                    if ((Entity) this instanceof ChitinGolemEntity animatable)
                        animatable.setTexture("chitin_golem_3");
                } else if (perc < 0.5) {
                    if ((Entity) this instanceof ChitinGolemEntity animatable)
                        animatable.setTexture("chitin_golem_2");
                } else if (perc < 0.75) {
                    if ((Entity) this instanceof ChitinGolemEntity animatable)
                        animatable.setTexture("chitin_golem_1");
                } else {
                    if ((Entity) this instanceof ChitinGolemEntity animatable)
                        animatable.setTexture("chitin_golem_0");
                }
            }
            if (this.isAlive()) {
                root = (Entity) this instanceof ChitinGolemEntity _datEntL1 && _datEntL1.getEntityData().get(DATA_rooted);
                if (!root) {
                    if (!(getDisplayName().getString()).equals(getType().getDescription().getString())) {
                        if ((Entity) this instanceof ChitinGolemEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_rootX, (int) Math.round(x));
                        if ((Entity) this instanceof ChitinGolemEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_rootZ, (int) Math.round(z));
                        if ((Entity) this instanceof ChitinGolemEntity _datEntSetL)
                            _datEntSetL.getEntityData().set(DATA_rooted, true);
                        CaerulaArborMod.LOGGER.info(("Chitin Golem " + getDisplayName().getString() + "has recognize x:" + Math.round(x) + " z:" + Math.round(z) + " as base"));
                    }
                } else if (Math.random() < 0.01 && !((Entity) this instanceof Mob _mobEnt9 && _mobEnt9.isAggressive())) {
                    rx = x - ((Entity) this instanceof ChitinGolemEntity _datEntI ? _datEntI.getEntityData().get(DATA_rootX) : 0);
                    rz = z - ((Entity) this instanceof ChitinGolemEntity _datEntI ? _datEntI.getEntityData().get(DATA_rootZ) : 0);
                    dist = new Vec3(0, 0, 0).distanceTo(new Vec3(rx, 0, rz));
                    if (dist >= 24) {
                        dist1 = Mth.nextDouble(RandomSource.create(), 4, 16);
                        if ((Entity) this instanceof Mob _entity)
                            _entity.getNavigation().moveTo(x - rx * dist1 / dist, this.getY(), z - rz * dist1 / dist, 1);
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
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.22);
		builder = builder.add(Attributes.MAX_HEALTH, 325);
		builder = builder.add(Attributes.ARMOR, 12);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 17);
		builder = builder.add(Attributes.FOLLOW_RANGE, 16);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

			) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.chitgolem.move"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.chitgolem.die"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.chitgolem.idle"));
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
		if (this.swinging && this.lastSwing + 15L <= level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.chitgolem.attack"));
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
		if (this.deathTime == 30) {
			this.remove(ChitinGolemEntity.RemovalReason.KILLED);
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
