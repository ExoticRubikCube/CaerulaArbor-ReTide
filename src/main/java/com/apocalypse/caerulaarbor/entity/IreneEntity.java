package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import com.apocalypse.caerulaarbor.init.CaerulaArborModItems;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.utils.EntityPredicateUtils;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import com.apocalypse.caerulaarbor.utils.WorldUtils;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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

import java.util.Comparator;
import java.util.List;

public class IreneEntity extends Animal implements GeoEntity {

	private boolean isIreneDurative() {
		return EntityPredicateUtils.isIreneDurative(this);
	}
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(IreneEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(IreneEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(IreneEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_skillp1 = SynchedEntityData.defineId(IreneEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_skillp2 = SynchedEntityData.defineId(IreneEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_duration = SynchedEntityData.defineId(IreneEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_tapTick = SynchedEntityData.defineId(IreneEntity.class, EntityDataSerializers.INT);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public IreneEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CaerulaArborModEntities.IRENE.get(), world);
	}

	public IreneEntity(EntityType<IreneEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);
		setMaxUpStep(1f);
		setPersistenceRequired();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "irene");
		this.entityData.define(DATA_skillp1, 0);
		this.entityData.define(DATA_skillp2, 12);
		this.entityData.define(DATA_duration, 0);
		this.entityData.define(DATA_tapTick, 0);
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
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.25, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 7.5625;
			}

			@Override
			public boolean canUse() {
				double x = IreneEntity.this.getX();
				double y = IreneEntity.this.getY();
				double z = IreneEntity.this.getZ();
				Entity entity = IreneEntity.this;
				Level world = IreneEntity.this.level();
				return super.canUse() && isIreneDurative();
			}

			@Override
			public boolean canContinueToUse() {
				double x = IreneEntity.this.getX();
				double y = IreneEntity.this.getY();
				double z = IreneEntity.this.getZ();
				Entity entity = IreneEntity.this;
				Level world = IreneEntity.this.level();
				return super.canContinueToUse() && isIreneDurative();
			}

		});
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Monster.class, true, false));
		this.goalSelector.addGoal(4, new OpenDoorGoal(this, false));
		this.goalSelector.addGoal(5, new OpenDoorGoal(this, true));
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, (float) 6) {
			@Override
			public boolean canUse() {
				double x = IreneEntity.this.getX();
				double y = IreneEntity.this.getY();
				double z = IreneEntity.this.getZ();
				Entity entity = IreneEntity.this;
				Level world = IreneEntity.this.level();
				return super.canUse() && isIreneDurative();
			}

			@Override
			public boolean canContinueToUse() {
				double x = IreneEntity.this.getX();
				double y = IreneEntity.this.getY();
				double z = IreneEntity.this.getZ();
				Entity entity = IreneEntity.this;
				Level world = IreneEntity.this.level();
				return super.canContinueToUse() && isIreneDurative();
			}
		});
		this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1) {
			@Override
			public boolean canUse() {
				double x = IreneEntity.this.getX();
				double y = IreneEntity.this.getY();
				double z = IreneEntity.this.getZ();
				Entity entity = IreneEntity.this;
				Level world = IreneEntity.this.level();
				return super.canUse() && isIreneDurative();
			}

			@Override
			public boolean canContinueToUse() {
				double x = IreneEntity.this.getX();
				double y = IreneEntity.this.getY();
				double z = IreneEntity.this.getZ();
				Entity entity = IreneEntity.this;
				Level world = IreneEntity.this.level();
				return super.canContinueToUse() && isIreneDurative();
			}
		});
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this) {
			@Override
			public boolean canUse() {
				double x = IreneEntity.this.getX();
				double y = IreneEntity.this.getY();
				double z = IreneEntity.this.getZ();
				Entity entity = IreneEntity.this;
				Level world = IreneEntity.this.level();
				return super.canUse() && isIreneDurative();
			}

			@Override
			public boolean canContinueToUse() {
				double x = IreneEntity.this.getX();
				double y = IreneEntity.this.getY();
				double z = IreneEntity.this.getZ();
				Entity entity = IreneEntity.this;
				Level world = IreneEntity.this.level();
				return super.canContinueToUse() && isIreneDurative();
			}
		});
		this.goalSelector.addGoal(9, new FloatGoal(this));
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEFINED;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHitIn) {
		super.dropCustomDeathLoot(source, looting, recentlyHitIn);
		this.spawnAtLocation(new ItemStack(CaerulaArborModItems.TRAIL_POWDER.get()));
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "irene_hit"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "irene_die"));
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity sourceentity = source.getEntity();
        if (sourceentity != null) {
            Entity specter = null;
            if (!(sourceentity instanceof Player) && !(sourceentity instanceof SpecterEntity)) {
                specter = (Entity) world.getEntitiesOfClass(SpecterEntity.class, AABB.ofSize(new Vec3(x, y, z), 32, 32, 32), e -> true).stream().sorted(new Object() {
                    Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                        return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
                    }
                }.compareDistOf(x, y, z)).findFirst().orElse(null);
                if (!(specter == null)) {
                    if (specter instanceof Mob _entity)
                        _entity.getNavigation().moveTo(x, y, z, 1);
                    if (specter instanceof Mob _entity && sourceentity instanceof LivingEntity _ent)
                        _entity.setTarget(_ent);
                }
            }
        }
        return super.hurt(source, amount);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putInt("Dataskillp1", this.entityData.get(DATA_skillp1));
		compound.putInt("Dataskillp2", this.entityData.get(DATA_skillp2));
		compound.putInt("Dataduration", this.entityData.get(DATA_duration));
		compound.putInt("DatatapTick", this.entityData.get(DATA_tapTick));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("Dataskillp1"))
			this.entityData.set(DATA_skillp1, compound.getInt("Dataskillp1"));
		if (compound.contains("Dataskillp2"))
			this.entityData.set(DATA_skillp2, compound.getInt("Dataskillp2"));
		if (compound.contains("Dataduration"))
			this.entityData.set(DATA_duration, compound.getInt("Dataduration"));
		if (compound.contains("DatatapTick"))
			this.entityData.set(DATA_tapTick, compound.getInt("DatatapTick"));
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
        double tap = 0;
        Entity enemy = null;
        if (!entity.isAlive()) {
            return InteractionResult.PASS;
        }
        if (((Entity) sourceentity instanceof LivingEntity _entity) ? _entity.isHolding(CaerulaArborModItems.PERSONNEL_TRANSPORTER.get()) : false) {
            return InteractionResult.PASS;
        }
        tap = entity instanceof IreneEntity _datEntI ? _datEntI.getEntityData().get(DATA_tapTick) : 0;
        if (tap <= 0) {
            enemy = entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null;
            if (!(enemy == null) && enemy.isAlive()) {
                return InteractionResult.PASS;
            }
            if (!((LevelAccessor) world).isClientSide()) {
                if ((LevelAccessor) world instanceof Level _level) {
                    if (!_level.isClientSide()) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "irene_interact")), SoundSource.NEUTRAL, 3, 1);
                    } else {
                        _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "irene_interact")), SoundSource.NEUTRAL, 3, 1, false);
                    }
                }
            }
            if (entity instanceof IreneEntity) {
                ((IreneEntity) entity).setAnimation("animation.irene.interact");
            }
            if (entity instanceof IreneEntity _datEntSetI)
                _datEntSetI.getEntityData().set(DATA_tapTick, 35);
            if (entity instanceof IreneEntity _datEntSetI)
                _datEntSetI.getEntityData().set(DATA_duration, 35);
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
        if (this != null) {
            Entity enemy = null;
            double sklp1 = 0;
            double dura = 0;
            double skillp2 = 0;
            double tap = 0;
            double less = 0;
            if (this.isAlive()) {
                if (tickCount % 40 == 15) {
                    WorldUtils.ireneBurnBrandAround(world, x, y, z);
                }
                sklp1 = (Entity) this instanceof IreneEntity _datEntI ? _datEntI.getEntityData().get(DATA_skillp1) : 0;
                skillp2 = (Entity) this instanceof IreneEntity _datEntI ? _datEntI.getEntityData().get(DATA_skillp2) : 0;
                dura = (Entity) this instanceof IreneEntity _datEntI ? _datEntI.getEntityData().get(DATA_duration) : 0;
                tap = (Entity) this instanceof IreneEntity _datEntI ? _datEntI.getEntityData().get(DATA_tapTick) : 0;
                enemy = (Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null;
                if (dura > 0) {
                    if ((Entity) this instanceof IreneEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_duration, (int) (dura - 1));
                }
                if (tap > 0) {
                    if ((Entity) this instanceof IreneEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_tapTick, (int) (tap - 1));
                }
                if (sklp1 >= 4 && dura <= 0) {
                    if (!(enemy == null) && enemy.isAlive()) {
                        if (distanceTo(enemy) <= 4) {
                            if (this instanceof IreneEntity) {
                                this.setAnimation("animation.irene.skill_1");
                            }
                            if ((Entity) this instanceof IreneEntity _datEntSetI)
                                _datEntSetI.getEntityData().set(DATA_skillp1, 0);
                            if ((Entity) this instanceof IreneEntity _datEntSetI)
                                _datEntSetI.getEntityData().set(DATA_duration, 27);
                            CaerulaArborMod.queueServerWork(10, () -> {
                                if (this.isAlive()) {
                                    if (world instanceof Level _level) {
                                        if (!_level.isClientSide()) {
                                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "irene_fly")), SoundSource.NEUTRAL, 3, 1);
                                        } else {
                                            _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "irene_fly")), SoundSource.NEUTRAL, 3, 1, false);
                                        }
                                    }
									Entity enemy1 = (Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null;
									if (enemy1 == null || this == null)
										return;
									enemy1.push(0, 0.4, 0);
									if (world instanceof ServerLevel _level)
										_level.sendParticles(ParticleTypes.FIREWORK, (enemy1.getX()), (enemy1.getY() + 1), (enemy1.getZ()), 48, 0.15, 1, 0.15, 0.15);
									if (enemy1 instanceof LivingEntity _entity && !_entity.level().isClientSide())
										_entity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, (int) (double) 30, 0));
									enemy1.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hunter_attack"))), this),
											(float) (((Entity) this instanceof LivingEntity _livingEntity6 && _livingEntity6.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity6.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * (double) 3));
									CaerulaArborMod.queueServerWork(6, () -> {
										if (world instanceof Level _level) {
											if (!_level.isClientSide()) {
												_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "irene_gun")), SoundSource.NEUTRAL, 3, 1);
											} else {
												_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "irene_gun")), SoundSource.NEUTRAL, 3, 1, false);
											}
										}
										if (world instanceof ServerLevel _level)
											_level.sendParticles(ParticleTypes.END_ROD, (enemy1.getX()), (enemy1.getY() + 0.75), (enemy1.getZ()), 32, 0.75, 0.75, 0.75, 0.15);
										enemy1.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hunter_attack"))), this),
												(float) (((Entity) this instanceof LivingEntity _livingEntity14 && _livingEntity14.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity14.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 2));
									});
								}
                            });
                        }
                    }
                } else if (skillp2 >= 16 && dura <= 0) {
                    if (!(enemy == null) && enemy.isAlive()) {
                        if ((enemy != null ? distanceTo(enemy) : -1) <= 6) {
                            if (this instanceof IreneEntity) {
                                ((IreneEntity) this).setAnimation("animation.irene.skill_2");
                            }
                            if (!this.level().isClientSide())
                                this.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 60, 9, false, false));
                            if (world instanceof Level _level) {
                                if (!_level.isClientSide()) {
                                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "irene_skill")), SoundSource.NEUTRAL, 3, 1);
                                } else {
                                    _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "irene_skill")), SoundSource.NEUTRAL, 3, 1, false);
                                }
                            }
                            if ((Entity) this instanceof IreneEntity _datEntSetI)
                                _datEntSetI.getEntityData().set(DATA_skillp2, 0);
                            if ((Entity) this instanceof IreneEntity _datEntSetI)
                                _datEntSetI.getEntityData().set(DATA_duration, 70);
                            CaerulaArborMod.queueServerWork(9, () -> {
                                if (this.isAlive()) {
									if (this == null)
										return;
									double damage = 0;
									damage = (Entity) this instanceof LivingEntity _livingEntity0 && _livingEntity0.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity0.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
									if (world instanceof Level _level) {
										if (!_level.isClientSide()) {
											_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "irene_skill_fly")), SoundSource.NEUTRAL, 3, 1);
										} else {
											_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "irene_skill_fly")), SoundSource.NEUTRAL, 3, 1, false);
										}
									}
									{
										final Vec3 _center = new Vec3(x, y, z);
										List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(14 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
										for (Entity entityiterator : _entfound) {
											if (EntityPredicateUtils.isValidEnemyForIrene(entityiterator, this) && (entityiterator != null ? distanceTo(entityiterator) : -1) <= 7) {
												entityiterator.push(0, 0.5, 0);
												if (world instanceof ServerLevel _level)
													_level.sendParticles(ParticleTypes.FIREWORK, (entityiterator.getX()), (entityiterator.getY() + 0.75), (entityiterator.getZ()), 48, 0.15, 1, 0.15, 0.15);
												if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
													_entity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 80, 0));
												entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hunter_attack"))), this),
														(float) (damage * 3));
											}
										}
									}
								}
                            });
                            CaerulaArborMod.queueServerWork(16, () -> {
                                if (this.isAlive()) {
                                    if (world instanceof Level _level) {
                                        if (!_level.isClientSide()) {
                                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "irene_skill_loop")), SoundSource.NEUTRAL, 2, 1);
                                        } else {
                                            _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "irene_skill_loop")), SoundSource.NEUTRAL, 2, 1, false);
                                        }
                                    }
                                }
                            });
                            for (int index0 = 0; index0 < 10; index0++) {
                                CaerulaArborMod.queueServerWork((int) Math.toIntExact(Math.round(20 + index0 * 3.778)), () -> {
                                    if (this.isAlive()) {
                                        Entity selected = null;
										double damage = 0;
										double tx = 0;
										double ty = 0;
										double tz = 0;
										damage = (Entity) this instanceof LivingEntity _livingEntity0 && _livingEntity0.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity0.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
										Entity result = null;
										Vec3 pos = position();
										AABB area = new AABB(pos.add(-7,-7,-7),pos.add(7,7,7));
										List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, area, e1 ->{
											return EntityPredicateUtils.isValidEnemyForIrene(e1, this)
											&& e1.position().vectorTo(pos).horizontalDistanceSqr()<= 49;
										});
										if(!entities.isEmpty()) {
											int index = Mth.nextInt(world.getRandom(), 0, entities.size() - 1);
											result = entities.get(index);
										}
										selected = result;
										if (selected == null) {
											return;
										}
										tx = selected.getX();
										ty = selected.getY();
										tz = selected.getZ();
										{
											final Vec3 _center = new Vec3(tx, ty, tz);
											List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(6 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
											for (Entity entityiterator : _entfound) {
												if (EntityPredicateUtils.isValidEnemyForIrene(entityiterator, this) && (entityiterator != null ? selected.distanceTo(entityiterator) : -1) <= 3) {
													entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hunter_attack"))), this),
															(float) (damage * 2.5));
													if (world instanceof Level _level) {
														if (!_level.isClientSide()) {
															_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "irene_skill_gun")), SoundSource.NEUTRAL, 3, 1);
														} else {
															_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "irene_skill_gun")), SoundSource.NEUTRAL, 3, 1, false);
														}
													}
													if (world instanceof ServerLevel _level)
														_level.sendParticles(ParticleTypes.END_ROD, (entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ()), 72, 2.5, 2.5, 2.5, 0.1);
												}
											}
										}
									}
                                });
                            }
                            CaerulaArborMod.queueServerWork(59, () -> {
                                if (this.isAlive()) {
                                    if (world instanceof Level _level) {
                                        if (!_level.isClientSide()) {
                                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "irene_reload")), SoundSource.NEUTRAL, 3, 1);
                                        } else {
                                            _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "irene_reload")), SoundSource.NEUTRAL, 3, 1, false);
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
                EntityUtils.vanguardBuff(world, x, y, z, this);
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
		IreneEntity retval = CaerulaArborModEntities.IRENE.get().create(serverWorld);
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

	public static void init() {
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.19);
		builder = builder.add(Attributes.MAX_HEALTH, 85);
		builder = builder.add(Attributes.ARMOR, 6);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 9);
		builder = builder.add(Attributes.FOLLOW_RANGE, 32);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.25);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

					&& !this.isAggressive()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.irene.move"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.irene.die"));
			}
			if (this.isAggressive() && event.isMoving()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.irene.run"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.irene.idle"));
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
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.irene.attack"));
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
			this.remove(IreneEntity.RemovalReason.KILLED);
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
