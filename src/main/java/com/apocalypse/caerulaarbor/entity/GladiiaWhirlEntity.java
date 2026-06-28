package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
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
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
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

public class GladiiaWhirlEntity extends PathfinderMob implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(GladiiaWhirlEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(GladiiaWhirlEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(GladiiaWhirlEntity.class, EntityDataSerializers.STRING);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public GladiiaWhirlEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CaerulaArborModEntities.GLADIIA_WHIRL.get(), world);
	}

	public GladiiaWhirlEntity(EntityType<GladiiaWhirlEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(true);
		setMaxUpStep(0f);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "gladiia_whirl");
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
	public MobType getMobType() {
		return MobType.UNDEFINED;
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
		this.setNoGravity(true);
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        if (this != null) {
            Entity gladiia = null;
            gladiia = (Entity) world.getEntitiesOfClass(GladiiaEntity.class, AABB.ofSize(new Vec3(x, y, z), 48, 48, 48), e -> true).stream().sorted(new Object() {
                Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                    return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
                }
            }.compareDistOf(x, y, z)).findFirst().orElse(null);
            if (!(gladiia == null)) {
                if (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
                    this.getAttribute(Attributes.ATTACK_DAMAGE)
                            .setBaseValue((gladiia instanceof LivingEntity _livingEntity2 && this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0));
            }
            if (this.getAttributes().hasAttribute(ForgeMod.ENTITY_GRAVITY.get()))
                this.getAttribute(ForgeMod.ENTITY_GRAVITY.get()).setBaseValue(0);
        }
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
		AttributeInstance maxHealth = this.getAttribute(Attributes.MAX_HEALTH);
        if(maxHealth != null) maxHealth.setBaseValue(10);
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        if (this != null) {
            Entity enemy = null;
            double t = 0;
            double damage = 0;
            double d = 0;
            t = tickCount;
            if (t >= 120) {
                if (!level().isClientSide())
                    discard();
            }
            if (!(t >= 111)) {
                damage = (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 0.9;
                {
                    final Vec3 _center = new Vec3(x, y, z);
                    List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(18 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                    for (Entity entityiterator : _entfound) {
                        d = entityiterator != null ? distanceTo(entityiterator) : -1;
                        if (entityiterator instanceof GladiiaEntity) {
                            continue;
                        }
                        if (entityiterator instanceof GladiiaWhirlEntity) {
                            continue;
                        }
                        if (!(entityiterator instanceof LivingEntity)) {
                            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "no_join_whirl")))) {
                                continue;
                            }
                        }
                        enemy = entityiterator instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null;
                        if (entityiterator instanceof Player || (entityiterator instanceof TamableAnimal _tamEnt ? _tamEnt.isTame() : false)) {
                            if (new Object() {
                                public boolean checkGamemode(Entity _ent) {
                                    if (_ent instanceof ServerPlayer _serverPlayer) {
                                        return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                                    } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                                        return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                                && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                                    }
                                    return false;
                                }
                            }.checkGamemode(entityiterator)) {
                                continue;
                            }
                            if (new Object() {
                                public boolean checkGamemode(Entity _ent) {
                                    if (_ent instanceof ServerPlayer _serverPlayer) {
                                        return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
                                    } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                                        return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                                && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.SPECTATOR;
                                    }
                                    return false;
                                }
                            }.checkGamemode(entityiterator)) {
                                continue;
                            }
                            if (!(enemy instanceof GladiiaEntity)) {
                                continue;
                            }
                        }
                        if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "is_humanside")))) {
                            if (!(enemy instanceof GladiiaEntity)) {
                                continue;
                            }
                        }
                        if (d <= 9) {
                            EntityUtils.turnRounds(entityiterator, this);
                        }
                    }
                }
                if (t % 20 == 11) {
                    if (!world.isClientSide()) {
                        if (world instanceof Level _level) {
                                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "gladiia_skill_rim")), SoundSource.NEUTRAL, 3, 1);
                        }
                    }
                    {
                        final Vec3 _center = new Vec3(x, y, z);
                        List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(24 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                        for (Entity entityiterator : _entfound) {
                            d = entityiterator != null ? distanceTo(entityiterator) : -1;
                            if (!(entityiterator instanceof LivingEntity)) {
                                if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "no_join_whirl")))) {
                                    continue;
                                }
                            }
                            if (entityiterator instanceof GladiiaEntity) {
                                continue;
                            }
                            if (entityiterator instanceof GladiiaWhirlEntity) {
                                continue;
                            }
                            enemy = entityiterator instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null;
                            if (entityiterator instanceof Player || (entityiterator instanceof TamableAnimal _tamEnt ? _tamEnt.isTame() : false)) {
                                if (new Object() {
                                    public boolean checkGamemode(Entity _ent) {
                                        if (_ent instanceof ServerPlayer _serverPlayer) {
                                            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                                        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                                            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                                        }
                                        return false;
                                    }
                                }.checkGamemode(entityiterator)) {
                                    continue;
                                }
                                if (new Object() {
                                    public boolean checkGamemode(Entity _ent) {
                                        if (_ent instanceof ServerPlayer _serverPlayer) {
                                            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
                                        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                                            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.SPECTATOR;
                                        }
                                        return false;
                                    }
                                }.checkGamemode(entityiterator)) {
                                    continue;
                                }
                                if (!(enemy instanceof GladiiaEntity)) {
                                    continue;
                                }
                            }
                            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "is_humanside")))) {
                                if (!(enemy instanceof GladiiaEntity)) {
                                    continue;
                                }
                            }
                            if (d <= 12) {
                                if (entityiterator instanceof LivingEntity) {
                                    entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "gladiia_magic")))),
                                            (float) damage);
                                    if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
                                        _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 1, false, false));
                                }
                                if (d >= 3) {
                                    EntityUtils.pullToGladiia(entityiterator, this);
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
    public boolean hurt(@NotNull DamageSource pSource, float pAmount){
        return false;
    }

    @Override
    protected void actuallyHurt(@NotNull DamageSource pDamageSource, float pDamageAmount) {
        return;
    }

    @Override
    protected void markHurt() {
        this.hurtMarked = false;
    }

    @Override
    public boolean isInvulnerableTo(@NotNull DamageSource pSource) {
        return true;
    }

    @Override
    public void setHealth(float pHealth) {
        if(pHealth < this.getHealth()) return;
        super.setHealth(pHealth);
    }

    @Override
    public void setDeltaMovement(@NotNull Vec3 pDeltaMovement) {
        super.setDeltaMovement(Vec3.ZERO);
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
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0);
		builder = builder.add(Attributes.MAX_HEALTH, 10);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 27);
		builder = builder.add(Attributes.FOLLOW_RANGE, 1);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 99);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.gladiia_whirl.idle"));
		}
		return PlayState.STOP;
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
		if (this.deathTime == 1) {
			this.remove(GladiiaWhirlEntity.RemovalReason.KILLED);
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
		data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
}
