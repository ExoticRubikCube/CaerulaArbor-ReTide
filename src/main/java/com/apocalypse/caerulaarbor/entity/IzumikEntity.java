package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CaerulaArborModAttributes;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
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

public class IzumikEntity extends SeaMonster {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(IzumikEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(IzumikEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(IzumikEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_growth_p = SynchedEntityData.defineId(IzumikEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_skillp = SynchedEntityData.defineId(IzumikEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_phase = SynchedEntityData.defineId(IzumikEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_wave = SynchedEntityData.defineId(IzumikEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_skillp_1 = SynchedEntityData.defineId(IzumikEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_deal = SynchedEntityData.defineId(IzumikEntity.class, EntityDataSerializers.INT);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";
	private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.PINK, ServerBossEvent.BossBarOverlay.NOTCHED_12);
	
	public IzumikEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CaerulaArborModEntities.IZUMIK.get(), world);
	}

	public IzumikEntity(EntityType<IzumikEntity> type, Level world) {
		super(type, world);
		xpReward = 128;
		setNoAi(false);
		setMaxUpStep(2f);
		setPersistenceRequired();
		this.moveControl = new FlyingMoveControl(this, 10, true);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "izumik_base");
		this.entityData.define(DATA_growth_p, 0);
		this.entityData.define(DATA_skillp, 5);
		this.entityData.define(DATA_phase, 0);
		this.entityData.define(DATA_wave, 8);
		this.entityData.define(DATA_skillp_1, 100);
		this.entityData.define(DATA_deal, 0);
	}

	public void setTexture(String texture) {
		this.entityData.set(TEXTURE, texture);
	}

	public String getTexture() {
		return this.entityData.get(TEXTURE);
	}

	@Override
	public boolean canCollideWith(Entity entity) {
		return true;
	}

	@Override
	public boolean canBeCollidedWith() {
        Entity entity = this;
        return EntityUtils.isAlive(entity);
    }

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected PathNavigation createNavigation(Level world) {
		return new FlyingPathNavigation(this, world);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this) {
			@Override
			public boolean canUse() {
				double x = IzumikEntity.this.getX();
				double y = IzumikEntity.this.getY();
				double z = IzumikEntity.this.getZ();
				Entity entity = IzumikEntity.this;
				Level world = IzumikEntity.this.level();
				return super.canUse() && IzumikEntity.this.getEntityData().get(DATA_phase) > 0;
			}

			@Override
			public boolean canContinueToUse() {
				double x = IzumikEntity.this.getX();
				double y = IzumikEntity.this.getY();
				double z = IzumikEntity.this.getZ();
				Entity entity = IzumikEntity.this;
				Level world = IzumikEntity.this.level();
				return super.canContinueToUse() && IzumikEntity.this.getEntityData().get(DATA_phase) > 0;
			}
		});
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 156.25;
			}

			@Override
			public boolean canUse() {
				double x = IzumikEntity.this.getX();
				double y = IzumikEntity.this.getY();
				double z = IzumikEntity.this.getZ();
				Entity entity = IzumikEntity.this;
				Level world = IzumikEntity.this.level();
				return super.canUse() && IzumikEntity.this.getEntityData().get(DATA_phase) > 0;
			}

			@Override
			public boolean canContinueToUse() {
				double x = IzumikEntity.this.getX();
				double y = IzumikEntity.this.getY();
				double z = IzumikEntity.this.getZ();
				Entity entity = IzumikEntity.this;
				Level world = IzumikEntity.this.level();
				return super.canContinueToUse() && IzumikEntity.this.getEntityData().get(DATA_phase) > 0;
			}

		});
		this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1));
		this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(5, new FloatGoal(this));
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
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "izumik_ambient"));
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "izumik_hit"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "izumik_die"));
	}

	@Override
	public boolean causeFallDamage(float l, float d, DamageSource source) {
		return false;
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		double targetX = target.getX();
		double targetY = target.getY();
		double targetZ = target.getZ();
		if (!this.level().isClientSide()) {
			CaerulaArborMod.queueServerWork(7, () -> {
				this.level().playSound(null, BlockPos.containing(targetX, targetY, targetZ),
						ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "izumik_attack")), SoundSource.HOSTILE, 1,
						(float) Mth.nextDouble(RandomSource.create(), 0.85, 0.15));
				if (this.isAlive() && target.isAlive() && this.distanceTo(target) <= 13) {
					target.hurt(
							new DamageSource(
									this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
											.getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "izumik_normal_attack"))),
									this),
							(float) (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0));
					if (this.getEntityData().get(DATA_phase) >= 1) {
						float oceanMagicDamage = (float) (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0);
						if (CaerulaArborModVariables.MapVariables.get(this.level()).strategy_grow >= 4) {
							oceanMagicDamage *= 1.5F;
						}
						target.hurt(
								new DamageSource(
										this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
												.getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "ocean_magic"))),
										this),
								oceanMagicDamage);
						if (this.getEntityData().get(DATA_phase) >= 2 && Math.random() < 0.15 && target instanceof LivingEntity livingTarget
								&& livingTarget.getAttributes().hasAttribute(CaerulaArborModAttributes.NUMB.get())) {
							livingTarget.getAttribute(CaerulaArborModAttributes.NUMB.get())
									.setBaseValue(livingTarget.getAttribute(CaerulaArborModAttributes.NUMB.get()).getBaseValue() + 1);
							if (this.level() instanceof ServerLevel serverLevel) {
								serverLevel.sendParticles(ParticleTypes.FIREWORK, targetX, targetY + 0.75, targetZ, 16, 0.75, 0.75, 0.75, 0.1);
							}
						}
					}
				}
			});
		}
		return true;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		this.awardBoilingSeaAdvancement();
		if (source.is(DamageTypes.IN_FIRE))
			return false;
		if (source.getDirectEntity() instanceof ThrownPotion || source.getDirectEntity() instanceof AreaEffectCloud)
			return false;
		if (source.is(DamageTypes.CACTUS))
			return false;
		if (source.is(DamageTypes.DROWN))
			return false;
		if (source.is(DamageTypes.LIGHTNING_BOLT))
			return false;
		if (source.is(DamageTypes.EXPLOSION))
			return false;
		if (source.is(DamageTypes.FALLING_ANVIL))
			return false;
		if (source.is(DamageTypes.DRAGON_BREATH))
			return false;
		if (source.is(DamageTypes.WITHER))
			return false;
		if (source.is(DamageTypes.WITHER_SKULL))
			return false;
		boolean damaged = super.hurt(source, amount);
		if (damaged) {
			double accumulatedDamage = this.getEntityData().get(DATA_deal);
			if (accumulatedDamage >= this.getMaxHealth() * 0.3) {
				this.getEntityData().set(DATA_skillp, 0);
				this.getEntityData().set(DATA_deal, 0);
			} else {
				this.getEntityData().set(DATA_deal, (int) (accumulatedDamage + amount));
			}
		}
		return damaged;
	}

	@Override
	public void die(DamageSource source) {
		super.die(source);
		this.awardBoilingSeaAdvancement();
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
        if (this != null) {
            if (this instanceof IzumikEntity) {
                this.setAnimation("animation.izumik.start");
            }
            if (!this.level().isClientSide())
                this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 30, 1, false, false));
            if (this.getAttributes().hasAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get()))
                this.getAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get()).setBaseValue(10);
            if (this.getAttributes().hasAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()))
                this.getAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()).setBaseValue(50);
            if (this.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get()))
                this.getAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get()).setBaseValue(0.01);
        }
        return retval;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putInt("Datagrowth_p", this.entityData.get(DATA_growth_p));
		compound.putInt("Dataskillp", this.entityData.get(DATA_skillp));
		compound.putInt("Dataphase", this.entityData.get(DATA_phase));
		compound.putInt("Datawave", this.entityData.get(DATA_wave));
		compound.putInt("Dataskillp_1", this.entityData.get(DATA_skillp_1));
		compound.putInt("Datadeal", this.entityData.get(DATA_deal));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("Datagrowth_p"))
			this.entityData.set(DATA_growth_p, compound.getInt("Datagrowth_p"));
		if (compound.contains("Dataskillp"))
			this.entityData.set(DATA_skillp, compound.getInt("Dataskillp"));
		if (compound.contains("Dataphase"))
			this.entityData.set(DATA_phase, compound.getInt("Dataphase"));
		if (compound.contains("Datawave"))
			this.entityData.set(DATA_wave, compound.getInt("Datawave"));
		if (compound.contains("Dataskillp_1"))
			this.entityData.set(DATA_skillp_1, compound.getInt("Dataskillp_1"));
		if (compound.contains("Datadeal"))
			this.entityData.set(DATA_deal, compound.getInt("Datadeal"));
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
        if ((entity instanceof IzumikEntity _datEntI ? _datEntI.getEntityData().get(DATA_phase) : 0) == 0) {
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
            }.checkGamemode((Entity) sourceentity)) {
                if ((Entity) sourceentity instanceof Player _player && !_player.level().isClientSide())
                    _player.displayClientMessage(Component.literal((Component.translatable("izumik.fastlearn").getString())), false);
                if ((Entity) sourceentity instanceof Player _player && !_player.level().isClientSide())
                    _player.displayClientMessage(Component.literal((Component.translatable("izumik.saying").getString())), false);
                if (entity instanceof IzumikEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_growth_p, 20);
            }
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
        if (this != null) {
            Entity enemy = null;
            double sklp = 0;
            double grow = 0;
            double phase = 0;
            double sklp1 = 0;
            double waves = 0;
            double amplifi = 0;
            if (this.isAlive()) {
                this.removeEffect(CaerulaArborModMobEffects.DIZZY.get());
                this.removeEffect(CaerulaArborModMobEffects.FROZEN.get());
                enemy = this.getTarget();
                sklp = this.getEntityData().get(DATA_skillp);
                sklp1 = this.getEntityData().get(DATA_skillp_1);
                grow = this.getEntityData().get(DATA_growth_p);
                phase = this.getEntityData().get(DATA_phase);
                waves = this.getEntityData().get(DATA_wave);
                if (!this.hasEffect(CaerulaArborModMobEffects.IZUMIK_LEARN.get())) {
                    amplifi = Math.floor(grow / 5);
                    if (CaerulaArborModVariables.MapVariables.get(world).strategy_silence > 3) {
                        amplifi = amplifi * 4;
                    } else if (CaerulaArborModVariables.MapVariables.get(world).strategy_grow > 3) {
                        amplifi = amplifi * 3;
                    } else {
                        amplifi = amplifi * 2;
                    }
                    if (amplifi > 0) {
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.IZUMIK_LEARN.get(), 20, (int) (amplifi - 1), false, false));
                    }
                }
                if (phase == 0) {
                    this.removeEffect(MobEffects.REGENERATION);
                    if (!this.hasEffect(CaerulaArborModMobEffects.INVULNERABLE.get())) {
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 35, 2, false, false));
                    }
                    if (grow < 20) {
                        {
                            final Vec3 _center = new Vec3(x, y, z);
                            List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(14 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                            for (Entity entityiterator : _entfound) {
                                if (entityiterator instanceof IzumikOffspringEntity && (entityiterator != null ? distanceTo(entityiterator) : -1) <= 7) {
                                    if (!entityiterator.level().isClientSide())
                                        entityiterator.discard();
                                    this.getEntityData().set(DATA_growth_p, (int) (grow + 1));
                                    this.getEntityData().set(DATA_skillp, (int) (sklp - 1));
                                    if (world instanceof ServerLevel _level)
                                        _level.sendParticles(ParticleTypes.CLOUD, (entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ()), 32, 0.6, 0.6, 0.6, 0.1);
                                    CaerulaArborMod.LOGGER.info(("Izumik absorb offspr and grow to " + Math.round(grow + 1)));
                                    break;
                                }
                            }
                        }
                    } else {
                        if (this instanceof IzumikEntity) {
                            this.setAnimation("animation.izumik.revive");
                        }
                        this.getEntityData().set(DATA_phase, 1);
                        this.removeEffect(CaerulaArborModMobEffects.INVULNERABLE.get());
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 300, 1, false, false));
                        this.getEntityData().set(DATA_skillp, 300);
                        {
                            final Vec3 _center = new Vec3(x, y, z);
                            List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(72 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                            for (Entity entityiterator : _entfound) {
                                if (entityiterator instanceof Player _player && !_player.level().isClientSide())
                                    _player.displayClientMessage(Component.literal((Component.translatable("izumik.saying").getString())), false);
                            }
                        }
                    }
                    if (sklp <= 0) {
                        this.getEntityData().set(DATA_skillp, 5);
                        if (grow + 1 < 20) {
                            if (this instanceof IzumikEntity) {
                                this.setAnimation("animation.izumik.grow");
                            }
                        }
                        CaerulaArborMod.queueServerWork(25, () -> {
                            if (this == null)
                                return;
                            double rate = 0;
                            double range = 0;
                            if (world instanceof Level _level) {
                                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "izumik_learn")), SoundSource.HOSTILE, (float) 2.5, 1);
                            }
                            rate = 1;
                            range = 16;
                            if (CaerulaArborModVariables.MapVariables.get(world).strategy_grow >= 4) {
                                rate = 1.5;
                                range = 24;
                            }
                            for (int index0 = 0; index0 < 120; index0++) {
                                if (world instanceof ServerLevel _level)
                                    _level.sendParticles(ParticleTypes.END_ROD, (x + range * Math.sin(Math.toRadians(index0 * 3))), (y + 0.5), (z + range * Math.cos(Math.toRadians(index0 * 3))), 3, 0.15, 0.5, 0.15, 0.15);
                            }
                            {
                                final Vec3 _center = new Vec3(x, y, z);
                                List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate((2 * range) / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                                for (Entity entityiterator : _entfound) {
                                    if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                                        if (!(entityiterator == this.getTarget())) {
                                            continue;
                                        }
                                    }
                                    if (!(entityiterator instanceof Mob) && !(entityiterator instanceof Player)) {
                                        continue;
                                    }
                                    if (distanceTo(entityiterator) <= range) {
                                        entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "izumik_skill")))),
                                                (float) ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * rate));
                                    }
                                }
                            }
                        });
                    }
                    if (waves <= 0) {
                        if (this instanceof IzumikEntity) {
                            this.setAnimation("animation.izumik.revive");
                        }
                        this.getEntityData().set(DATA_phase, 1);
                        this.removeEffect(CaerulaArborModMobEffects.INVULNERABLE.get());
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 300, 1, false, false));
                        this.getEntityData().set(DATA_skillp, 300);
                        {
                            final Vec3 _center = new Vec3(x, y, z);
                            List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(72 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                            for (Entity entityiterator : _entfound) {
                                if (entityiterator instanceof Player _player && !_player.level().isClientSide())
                                    _player.displayClientMessage(Component.literal((Component.translatable("izumik.saying").getString())), false);
                            }
                        }
                    }
                    if (sklp1 <= 0 && waves > 0) {
                        for (int index0 = 0; index0 < 12; index0++) {
                            double range = 0;
                            double t = 0;
                            double tgtX = 0;
                            double tgtZ = 0;
                            double validY = 0;
                            validY = 114514;
                            for (int index1 = 0; index1 < 24; index1++) {
                                range = Mth.nextInt(RandomSource.create(), 28, 42);
                                t = Mth.nextDouble(RandomSource.create(), 0, 6.283);
                                tgtX = x + range * Math.sin(t);
                                tgtZ = z + range * Math.cos(t);
                                validY = findValidYOffspr(world, x, y, z, tgtX, y + 4, tgtZ);
                                if (validY < 114513) {
                                    break;
                                }
                            }
                            if (validY < 114513) {
                                if (world instanceof ServerLevel _level) {
                                    Entity entityToSpawn = CaerulaArborModEntities.IZUMIK_OFFSPRING.get().spawn(_level, BlockPos.containing(tgtX, validY, tgtZ), MobSpawnType.MOB_SUMMONED);
                                    if (entityToSpawn != null) {
                                        entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                                    }
                                }
                                if (world instanceof ServerLevel _level)
                                    _level.sendParticles(ParticleTypes.CLOUD, tgtX, validY, tgtZ, 32, 0.5, 0.5, 0.5, 0.15);
                            }
                        }
                        this.getEntityData().set(DATA_wave, (int) (waves - 1));
                        this.getEntityData().set(DATA_skillp_1, 600);
                    } else {
                        this.getEntityData().set(DATA_skillp_1, (int) (sklp1 - 1));
                    }
                } else {
                    if (sklp <= 0) {
                        if (!(this.getTarget() == null) && ((Entity) this.getTarget()).isAlive()) {
                            if ((this.getTarget() != null ? distanceTo(this.getTarget()) : -1) <= 24) {
                                if (phase >= 2) {
                                    this.getEntityData().set(DATA_skillp, 400);
                                } else {
                                    this.getEntityData().set(DATA_skillp, 600);
                                }
                                if (!this.level().isClientSide())
                                    this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 50, 0, false, false));
                                if (this instanceof IzumikEntity) {
                                    this.setAnimation("animation.izumik.skill");
                                }
                                CaerulaArborMod.queueServerWork(35, () -> {
                                    this.setHealth((float) ((this.getHealth()) + (this.getMaxHealth()) * 0.03));
                                    if (this.getAttributes().hasAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get()))
                                        this.getAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get())
                                                .setBaseValue(Math.min((this.getAttributes().hasAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get())
                                                        ? this.getAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get()).getBaseValue()
                                                        : 0) + 1, (this.getMaxHealth()) * 0.05));
                                    if (this == null)
                                        return;
                                    double range = 0;
                                    if (world instanceof Level _level) {
                                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "izumik_shock")), SoundSource.HOSTILE, (float) 3.5, 1);
                                    }
                                    range = 11;
                                    if (CaerulaArborModVariables.MapVariables.get(world).strategy_grow >= 4) {
                                        range = 14;
                                    }
                                    if ((this.getEntityData().get(DATA_phase)) >= 2) {
                                        range = range + 3;
                                    }
                                    new Object() {
                                        void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                                            IzumikEntity.this.performShockAttack((timedloopiterator + 1) * 2);
                                            final int tick2 = ticks;
                                            CaerulaArborMod.queueServerWork(tick2, () -> {
                                                if (timedlooptotal > timedloopiterator + 1) {
                                                    timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                                                }
                                            });
                                        }
                                    }.timedLoop(0, (int) range, 1);
                                });
                            }
                        }
                    } else {
                        if ((this.getHealth()) < (this.getMaxHealth()) * 0.33) {
                            this.getEntityData().set(DATA_skillp, (int) (sklp - 2));
                        } else {
                            this.getEntityData().set(DATA_skillp, (int) (sklp - 1));
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
	protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
	}

	@Override
	public void setNoGravity(boolean ignored) {
		super.setNoGravity(true);
	}

	public void aiStep() {
		super.aiStep();
		this.setNoGravity(true);
	}

	public static void init() {
	}

	private void awardBoilingSeaAdvancement() {
		for (Entity playerEntity : new ArrayList<>(this.level().players())) {
			if (this.level().dimension() == playerEntity.level().dimension() && playerEntity instanceof ServerPlayer serverPlayer) {
				Advancement advancement = serverPlayer.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "boiling_sea"));
				AdvancementProgress advancementProgress = serverPlayer.getAdvancements().getOrStartProgress(advancement);
				if (!advancementProgress.isDone()) {
					for (String criteria : advancementProgress.getRemainingCriteria()) {
						serverPlayer.getAdvancements().award(advancement, criteria);
					}
				}
			}
		}
	}

	private void performShockAttack(double r) {
		LevelAccessor world = this.level();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();

		double rate = 0.25;
		if (CaerulaArborModVariables.MapVariables.get(world).strategy_grow >= 4) {
			rate = 0.35;
		}

		for (int index0 = 0; index0 < 120; index0++) {
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.END_ROD, (x + r * Math.sin(Math.toRadians(index0 * 3))), (y + 0.5), (z + r * Math.cos(Math.toRadians(index0 * 3))), 3, 0.15, 0.5, 0.15, 0.15);
		}

		final Vec3 center = new Vec3(x, y, z);
		List<Entity> nearbyEntities = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(r), e -> true).stream()
				.sorted(Comparator.comparingDouble(ent -> ent.distanceToSqr(center)))
				.toList();

		for (Entity entityiterator : nearbyEntities) {
			if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
				if (!(entityiterator == this.getTarget())) {
					continue;
				}
			}
			if (!(entityiterator instanceof Mob) && !(entityiterator instanceof Player)) {
				continue;
			}
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
			if (this.distanceTo(entityiterator) <= r) {
				entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "izumik_skill"))), this),
						(float) ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * rate));

				if (!(entityiterator instanceof LivingEntity _livEnt11 && _livEnt11.hasEffect(CaerulaArborModMobEffects.IZUMIK_SHOCK.get()))) {
					if (entityiterator instanceof LivingEntity _entity && !this.level().isClientSide())
						this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.IZUMIK_SHOCK.get(), 160, 0, false, false));
				}
				if (!(entityiterator instanceof LivingEntity _livEnt13 && _livEnt13.hasEffect(CaerulaArborModMobEffects.DIZZY.get()))) {
					if (entityiterator instanceof LivingEntity _entity && !this.level().isClientSide())
						this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.DIZZY.get(), 160, 0, false, false));
				}
				if (this.getAttributes().hasAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get()))
					this.getAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get())
							.setBaseValue(Math.min((this.getAttributes().hasAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get())
									? this.getAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get()).getBaseValue()
									: 0) + 0.25, this.getMaxHealth() * 0.05));

				if ((this.getEntityData().get(DATA_phase) >= 2)) {
					if (Math.random() < 0.33) {
						if (CaerulaArborModVariables.MapVariables.get(world).strategy_grow >= 4) {
							if (entityiterator instanceof LivingEntity _livingEntity20 && _livingEntity20.getAttributes().hasAttribute(CaerulaArborModAttributes.NUMB.get()))
								_livingEntity20.getAttribute(CaerulaArborModAttributes.NUMB.get())
										.setBaseValue(((entityiterator instanceof LivingEntity _livingEntity19 && _livingEntity19.getAttributes().hasAttribute(CaerulaArborModAttributes.NUMB.get())
												? _livingEntity19.getAttribute(CaerulaArborModAttributes.NUMB.get()).getBaseValue()
												: 0) + 2));
						} else {
							if (entityiterator instanceof LivingEntity _livingEntity22 && _livingEntity22.getAttributes().hasAttribute(CaerulaArborModAttributes.NUMB.get()))
								_livingEntity22.getAttribute(CaerulaArborModAttributes.NUMB.get())
										.setBaseValue(((entityiterator instanceof LivingEntity _livingEntity21 && _livingEntity21.getAttributes().hasAttribute(CaerulaArborModAttributes.NUMB.get())
												? _livingEntity21.getAttribute(CaerulaArborModAttributes.NUMB.get()).getBaseValue()
												: 0) + 1));
						}
					}
					this.setHealth((float) ((this.getHealth()) + (this.getMaxHealth()) * 0.01));
				}
			}
		}
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.6);
		builder = builder.add(Attributes.MAX_HEALTH, 600);
		builder = builder.add(Attributes.ARMOR, 18);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 22);
		builder = builder.add(Attributes.FOLLOW_RANGE, 64);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
		builder = builder.add(Attributes.FLYING_SPEED, 0.6);
		return builder;
	}

	private double findValidYOffspr(LevelAccessor world, double x, double y, double z, double xx, double yy, double zz) {
		double y_found = 0;
		if (world instanceof Level _level) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.azalea.hit")), SoundSource.NEUTRAL, 0, 1);
		}
		y_found = yy;
		for (int index0 = 0; index0 < 12; index0++) {
			y_found = yy + index0;
			if (!(world.getBlockFloorHeight(BlockPos.containing(xx, y_found, zz)) > 0)) {
				return y_found;
			}
			y_found = yy - index0 - 1;
			if (!(world.getBlockFloorHeight(BlockPos.containing(xx, y_found, zz)) > 0)) {
				return y_found;
			}
		}
		return 114514;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.izumik.die1"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.izumik.idle"));
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
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.izumik.attack"));
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
		if (this.deathTime >= 30) {
			this.remove(IzumikEntity.RemovalReason.KILLED);
			this.dropExperience();
            LevelAccessor world = this.level();
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();
            if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                if (!world.isClientSide() && world.getServer() != null) {
                    BlockPos _bpLootTblWorld = BlockPos.containing(x, y, z);
                    for (ItemStack itemstackiterator : world.getServer().getLootData().getLootTable(new ResourceLocation(CaerulaArborMod.MODID, "gameplay/relic_izumik"))
                            .getRandomItems(new LootParams.Builder((ServerLevel) world).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(_bpLootTblWorld)).withParameter(LootContextParams.BLOCK_STATE, world.getBlockState(_bpLootTblWorld))
                                    .withOptionalParameter(LootContextParams.BLOCK_ENTITY, world.getBlockEntity(_bpLootTblWorld)).create(LootContextParamSets.EMPTY))) {
                        if (world instanceof ServerLevel _level) {
                            ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, itemstackiterator);
                            entityToSpawn.setPickUpDelay(10);
                            entityToSpawn.setUnlimitedLifetime();
                            _level.addFreshEntity(entityToSpawn);
                        }
                    }
                }
                for (int index0 = 0; index0 < 128; index0++) {
                    if (world instanceof ServerLevel _level)
                        _level.addFreshEntity(new ExperienceOrb(_level, (x + Mth.nextDouble(RandomSource.create(), -1, 1)), y, (z + Mth.nextDouble(RandomSource.create(), -1, 1)), Mth.nextInt(RandomSource.create(), 16, 64)));
                }
            }
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

	public ResourceKey<DamageType> oceankiller = ResourceKey.create(Registries.DAMAGE_TYPE, 
							new ResourceLocation(CaerulaArborMod.MODID, "oceankiller_damage"));

	@Override
	public void remove(RemovalReason pReason){
		if(this.level().getDifficulty() != Difficulty.PEACEFUL && pReason == RemovalReason.DISCARDED){
			this.hurt(
				new DamageSource(
					this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(oceankiller)
				),
				20
			);
			return;
		} else if (pReason == RemovalReason.KILLED && !this.isDeadOrDying()) return;	
		super.remove(pReason);
	}

	@Override
    public void setHealth(float pHealth){
    	float hlth = this.getHealth();
    	float mhlth = this.getMaxHealth();
        if(this.hasEffect(CaerulaArborModMobEffects.INVULNERABLE.get()) && pHealth < this.getHealth()) return;
        float reduction = hlth - pHealth;
        float finalV = reduction >= mhlth * 0.33f ? hlth - mhlth * 0.33f : hlth - reduction;
        super.setHealth(finalV);
    }
}
