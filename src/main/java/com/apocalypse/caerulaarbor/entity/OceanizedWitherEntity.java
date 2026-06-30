package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CAAttributes;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAItems;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.util.EntityPredicateUtils;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import com.apocalypse.caerulaarbor.util.WorldUtils;
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
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
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
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
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
import java.util.EnumSet;
import java.util.List;

public class OceanizedWitherEntity extends SeaMonster implements RangedAttackMob {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(OceanizedWitherEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(OceanizedWitherEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(OceanizedWitherEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_skillp = SynchedEntityData.defineId(OceanizedWitherEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_duration = SynchedEntityData.defineId(OceanizedWitherEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_spawn = SynchedEntityData.defineId(OceanizedWitherEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Boolean> DATA_shelled = SynchedEntityData.defineId(OceanizedWitherEntity.class, EntityDataSerializers.BOOLEAN);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";
	private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.WHITE, ServerBossEvent.BossBarOverlay.NOTCHED_10);

	public OceanizedWitherEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CAEntities.OCEANIZED_WITHER.get(), world);
	}

	public OceanizedWitherEntity(EntityType<OceanizedWitherEntity> type, Level world) {
		super(type, world);
		xpReward = 512;
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
		this.entityData.define(TEXTURE, "oceanized_wither_inv");
		this.entityData.define(DATA_skillp, 200);
		this.entityData.define(DATA_duration, 100);
		this.entityData.define(DATA_spawn, 0);
		this.entityData.define(DATA_shelled, false);
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
	protected PathNavigation createNavigation(Level world) {
		return new FlyingPathNavigation(this, world);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this) {
			@Override
			public boolean canUse() {
				Entity entity = OceanizedWitherEntity.this;
				Level world = OceanizedWitherEntity.this.level();
				return super.canUse() && EntityPredicateUtils.isWitherDurative(entity);
			}

			@Override
			public boolean canContinueToUse() {
				Entity entity = OceanizedWitherEntity.this;
				Level world = OceanizedWitherEntity.this.level();
				return super.canContinueToUse() && EntityPredicateUtils.isWitherDurative(entity);
			}
		});
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, IronGolem.class, false, false));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Villager.class, false, false));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Animal.class, false, false));
		this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1, 20) {
			@Override
			protected Vec3 getPosition() {
				RandomSource random = OceanizedWitherEntity.this.getRandom();
				double dir_x = OceanizedWitherEntity.this.getX() + ((random.nextFloat() * 2 - 1) * 16);
				double dir_y = OceanizedWitherEntity.this.getY() + ((random.nextFloat() * 2 - 1) * 16);
				double dir_z = OceanizedWitherEntity.this.getZ() + ((random.nextFloat() * 2 - 1) * 16);
				return new Vec3(dir_x, dir_y, dir_z);
			}

			@Override
			public boolean canUse() {
				Entity entity = OceanizedWitherEntity.this;
				Level world = OceanizedWitherEntity.this.level();
				return super.canUse() && EntityPredicateUtils.isWitherDurative(entity);
			}

			@Override
			public boolean canContinueToUse() {
				double z = OceanizedWitherEntity.this.getZ();
				Entity entity = OceanizedWitherEntity.this;
				Level world = OceanizedWitherEntity.this.level();
				return super.canContinueToUse() && EntityPredicateUtils.isWitherDurative(entity);
			}

		});
		this.goalSelector.addGoal(6, new RandomLookAroundGoal(this) {
			@Override
			public boolean canUse() {
				Entity entity = OceanizedWitherEntity.this;
				Level world = OceanizedWitherEntity.this.level();
				return super.canUse() && EntityPredicateUtils.isWitherDurative(entity);
			}

			@Override
			public boolean canContinueToUse() {
				Entity entity = OceanizedWitherEntity.this;
				Level world = OceanizedWitherEntity.this.level();
				return super.canContinueToUse() && EntityPredicateUtils.isWitherDurative(entity);
			}
		});
		this.goalSelector.addGoal(1, new OceanizedWitherEntity.RangedAttackGoal(this, 1.25, 30, 12f) {
			@Override
			public boolean canContinueToUse() {
				return this.canUse();
			}
		});
	}

	public class RangedAttackGoal extends Goal {
		private final Mob mob;
		private final RangedAttackMob rangedAttackMob;
		@Nullable
		private LivingEntity target;
		private int attackTime = -1;
		private final double speedModifier;
		private int seeTime;
		private final int attackIntervalMin;
		private final int attackIntervalMax;
		private final float attackRadius;
		private final float attackRadiusSqr;

		public RangedAttackGoal(RangedAttackMob p_25768_, double p_25769_, int p_25770_, float p_25771_) {
			this(p_25768_, p_25769_, p_25770_, p_25770_, p_25771_);
		}

		public RangedAttackGoal(RangedAttackMob p_25773_, double p_25774_, int p_25775_, int p_25776_, float p_25777_) {
			if (!(p_25773_ instanceof LivingEntity)) {
				throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
			} else {
				this.rangedAttackMob = p_25773_;
				this.mob = (Mob) p_25773_;
				this.speedModifier = p_25774_;
				this.attackIntervalMin = p_25775_;
				this.attackIntervalMax = p_25776_;
				this.attackRadius = p_25777_;
				this.attackRadiusSqr = p_25777_ * p_25777_;
				this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
			}
		}

		public boolean canUse() {
			LivingEntity livingentity = this.mob.getTarget();
			if (livingentity != null && livingentity.isAlive()) {
				this.target = livingentity;
				return true;
			} else {
				return false;
			}
		}

		public boolean canContinueToUse() {
			return this.canUse() || this.target.isAlive() && !this.mob.getNavigation().isDone();
		}

		public void stop() {
			this.target = null;
			this.seeTime = 0;
			this.attackTime = -1;
			((OceanizedWitherEntity) rangedAttackMob).entityData.set(SHOOT, false);
		}

		public boolean requiresUpdateEveryTick() {
			return true;
		}

		public void tick() {
			double d0 = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
			boolean flag = this.mob.getSensing().hasLineOfSight(this.target);
			if (flag) {
				++this.seeTime;
			} else {
				this.seeTime = 0;
			}
			if (!(d0 > (double) this.attackRadiusSqr) && this.seeTime >= 5) {
				this.mob.getNavigation().stop();
			} else {
				this.mob.getNavigation().moveTo(this.target, this.speedModifier);
			}
			this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
			if (--this.attackTime == 0) {
				if (!flag) {
					((OceanizedWitherEntity) rangedAttackMob).entityData.set(SHOOT, false);
					return;
				}
				((OceanizedWitherEntity) rangedAttackMob).entityData.set(SHOOT, true);
				float f = (float) Math.sqrt(d0) / this.attackRadius;
				float f1 = Mth.clamp(f, 0.1F, 1.0F);
				this.rangedAttackMob.performRangedAttack(this.target, f1);
				this.attackTime = Mth.floor(f * (float) (this.attackIntervalMax - this.attackIntervalMin) + (float) this.attackIntervalMin);
			} else if (this.attackTime < 0) {
				this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(d0) / (double) this.attackRadius, this.attackIntervalMin, this.attackIntervalMax));
			} else
				((OceanizedWitherEntity) rangedAttackMob).entityData.set(SHOOT, false);
		}
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEAD;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	public SoundEvent getAmbientSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "ocean_wither_idle"));
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "ocean_wither_hurt"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "ocean_wither_die"));
	}

	@Override
	public boolean causeFallDamage(float l, float d, DamageSource source) {
		return false;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.IN_FIRE))
			return false;
		if (source.is(DamageTypes.FALL))
			return false;
		if (source.is(DamageTypes.CACTUS))
			return false;
		if (source.is(DamageTypes.DROWN))
			return false;
		if (source.is(DamageTypes.LIGHTNING_BOLT))
			return false;
		if (source.is(DamageTypes.EXPLOSION))
			return false;
		if (source.is(DamageTypes.DRAGON_BREATH))
			return false;
		if (source.is(DamageTypes.WITHER))
			return false;
		if (source.is(DamageTypes.WITHER_SKULL))
			return false;
		return super.hurt(source, amount);
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
        if (this != null) {
            if (this.getAttributes().hasAttribute(CAAttributes.SANITY_RATE.get()))
                this.getAttribute(CAAttributes.SANITY_RATE.get()).setBaseValue(10);
            if (this.getAttributes().hasAttribute(CAAttributes.SANITY_MODIFIER.get()))
                this.getAttribute(CAAttributes.SANITY_MODIFIER.get()).setBaseValue(0.01);
            if (this.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get()))
                this.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).setBaseValue(65);
            if (this.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE.get()))
                this.getAttribute(CAAttributes.GENERAL_DEFENSE.get()).setBaseValue(5);
            if (!this.level().isClientSide())
                this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 100, 9, false, false));
            if ((Entity) this instanceof LivingEntity _entity)
                _entity.setHealth(1);
            setDeltaMovement(new Vec3(0, (-0.75), 0));
            if ((Entity) this instanceof OceanizedWitherEntity _datEntSetI)
                _datEntSetI.getEntityData().set(DATA_duration, 100);
        }
        return retval;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putInt("Dataskillp", this.entityData.get(DATA_skillp));
		compound.putInt("Dataduration", this.entityData.get(DATA_duration));
		compound.putInt("Dataspawn", this.entityData.get(DATA_spawn));
		compound.putBoolean("Datashelled", this.entityData.get(DATA_shelled));
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
		if (compound.contains("Dataspawn"))
			this.entityData.set(DATA_spawn, compound.getInt("Dataspawn"));
		if (compound.contains("Datashelled"))
			this.entityData.set(DATA_shelled, compound.getBoolean("Datashelled"));
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
        if (entity == null)
            return InteractionResult.PASS;
        if (itemstack.getItem() == CAItems.OCEANIZED_WITHER_SPAWNEGG.get()) {
            if (entity instanceof OceanizedWitherEntity _datEntSetI)
                _datEntSetI.getEntityData().set(DATA_duration, 0);
            if (entity instanceof OceanizedWitherEntity _datEntSetI)
                _datEntSetI.getEntityData().set(DATA_spawn, 100);
            if (entity instanceof OceanizedWitherEntity animatable)
                animatable.setTexture("oceanized_wither");
            if (entity instanceof LivingEntity _entity)
                _entity.setHealth(entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1);
            if (entity instanceof LivingEntity _entity)
                _entity.removeEffect(CAMobEffects.INVULNERABLE.get());
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
            double spawn;
            double skillp;
            double duration;
            Entity enemy;
            boolean shelled;
            if (this.isAlive()) {
                spawn = (Entity) this instanceof OceanizedWitherEntity _datEntI ? _datEntI.getEntityData().get(DATA_spawn) : 0;
                skillp = (Entity) this instanceof OceanizedWitherEntity _datEntI ? _datEntI.getEntityData().get(DATA_skillp) : 0;
                duration = (Entity) this instanceof OceanizedWitherEntity _datEntI ? _datEntI.getEntityData().get(DATA_duration) : 0;
                if (spawn < 100) {
                    if ((Entity) this instanceof LivingEntity _entity)
                        _entity.setHealth((float) (((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * (spawn + 1) * 0.01));
                    if ((Entity) this instanceof OceanizedWitherEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_spawn, (int) (spawn + 1));
                    if (spawn == 19) {
                        if ((Entity) this instanceof OceanizedWitherEntity animatable)
                            animatable.setTexture("oceanized_wither_inv");
                    } else if (spawn == 39) {
                        if ((Entity) this instanceof OceanizedWitherEntity animatable)
                            animatable.setTexture("oceanized_wither");
                    } else if (spawn == 49) {
                        if ((Entity) this instanceof OceanizedWitherEntity animatable)
                            animatable.setTexture("oceanized_wither_inv");
                    } else if (spawn == 59) {
                        if ((Entity) this instanceof OceanizedWitherEntity animatable)
                            animatable.setTexture("oceanized_wither");
                    } else if (spawn == 69) {
                        if ((Entity) this instanceof OceanizedWitherEntity animatable)
                            animatable.setTexture("oceanized_wither_inv");
                    } else if (spawn == 79) {
                        if ((Entity) this instanceof OceanizedWitherEntity animatable)
                            animatable.setTexture("oceanized_wither");
                    } else if (spawn == 89) {
                        if ((Entity) this instanceof OceanizedWitherEntity animatable)
                            animatable.setTexture("oceanized_wither_inv");
                    } else if (spawn == 99) {
                        if ((Entity) this instanceof OceanizedWitherEntity animatable)
                            animatable.setTexture("oceanized_wither");
                        if (world instanceof Level _level && !_level.isClientSide())
                            _level.explode(null, x, y, z, 16, Level.ExplosionInteraction.MOB);
                        if (world instanceof Level _level) {
                                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "ocean_wither_spawn")), SoundSource.HOSTILE, 4, 1);
                        }
                        if (world instanceof ServerLevel _level)
                            _level.sendParticles(ParticleTypes.EXPLOSION, x, y, z, 4, 3, 3, 3, 1);
                    }
                }
                if (duration > 0) {
                    if ((Entity) this instanceof OceanizedWitherEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_duration, (int) (duration - 1));
                }
                enemy = (Entity) this instanceof Mob _mobEnt ? _mobEnt.getTarget() : null;
                if (skillp > 0) {
                    if ((Entity) this instanceof OceanizedWitherEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_skillp, (int) (skillp - 1));
                } else {
                    if (!(enemy == null) && enemy.isAlive()) {
                        if ((Entity) this instanceof OceanizedWitherEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_duration, 40);
                        if ((Entity) this instanceof OceanizedWitherEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(DATA_skillp, 300);
                        LivingEntity _entity = this;
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 20, 0, false, false));
                        if (this instanceof OceanizedWitherEntity) {
                            this.setAnimation("animation.oceanzied_wither.skill");
                        }
                        CaerulaArborMod.queueServerWork(4, () -> {
                            rimedWitherShoot(world);
                        });
                        CaerulaArborMod.queueServerWork(13, () -> {
                            this.gatlinWitherShoot(world, x, y, z);
                        });
                        CaerulaArborMod.queueServerWork(12, () -> {
                            this.gatlinWitherShoot(world, x, y, z);
                        });
                    }
                }
                if (tickCount % 10 == 0) {
                    boolean once;
                    double dx;
                    double dy;
                    double dz;
                    double hardness;
                    double lose = 0;
                    BlockState block;
                    if (WorldUtils.canGrief(world)) {
                        once = false;
                        dx = -1;
                        for (int index0 = 0; index0 < 3; index0++) {
                            dz = -1;
                            for (int index1 = 0; index1 < 3; index1++) {
                                dy = 0;
                                for (int index2 = 0; index2 < 4; index2++) {
                                    block = (world.getBlockState(BlockPos.containing(x + dx, y + dy, z + dz)));
                                    if (!block.is(BlockTags.create(new ResourceLocation("minecraft:wither_immnue")))) {
                                        hardness = block.getDestroySpeed(world, BlockPos.containing(0, 0, 0));
                                        if (hardness <= 7.5 && hardness >= 0 && world.getBlockFloorHeight(BlockPos.containing(x + dx, y + dy, z + dz)) > 0) {
                                            {
                                                BlockPos _pos = BlockPos.containing(x + dx, y + dy, z + dz);
                                                Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(x, y, z), null);
                                                world.destroyBlock(_pos, false);
                                            }
                                            if (world instanceof Level _level)
                                                _level.updateNeighborsAt(BlockPos.containing(x + dx, y + dy, z + dz), _level.getBlockState(BlockPos.containing(x + dx, y + dy, z + dz)).getBlock());
                                            once = true;
                                        }
                                    }
                                    dy = dy + 1;
                                }
                                dz = dz + 1;
                            }
                            dx = dx + 1;
                        }
                        if (once) {
                            if (world instanceof Level _level) {
                                if (!_level.isClientSide()) {
                                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.wither.break_block")), SoundSource.NEUTRAL, 1, 1);
                                } else {
                                    _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.wither.break_block")), SoundSource.NEUTRAL, 1, 1, false);
                                }
                            }
                        }
                    }
                }
                if (tickCount % 20 == 0) {
                    if ((Entity) this instanceof LivingEntity _entity)
                        _entity.removeEffect(MobEffects.WITHER);
                    if ((Entity) this instanceof LivingEntity _entity)
                        _entity.removeEffect(CAMobEffects.DIZZY.get());
                    {
                        final Vec3 _center = new Vec3(x, y, z);
                        List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(64 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                        for (Entity entityiterator : _entfound) {
                            if (entityiterator instanceof LivingEntity _livEnt34 && _livEnt34.hasEffect(MobEffects.WITHER) && entityiterator.isAlive()) {
                                entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "ocean_wither"))), this),
                                        (float) ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 0.75));
                                if (world instanceof ServerLevel _level)
                                    _level.sendParticles(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, (entityiterator.getX()), (entityiterator.getY() + 1), (entityiterator.getZ()), 16, 1, 1, 1, 0.1);
                            }
                        }
                    }
                }
                if (tickCount % 40 == 0) {
                    {
                        final Vec3 _center = new Vec3(x, y, z);
                        List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(72 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                        for (Entity entityiterator : _entfound) {
                            if (entityiterator instanceof WitherSkull && EntityUtils.getSpeed(entityiterator) < 0.15) {
                                if (!entityiterator.level().isClientSide())
                                    entityiterator.discard();
                            }
                        }
                    }
                }
                if (EntityUtils.getSpeed(this) > (this.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED) ? this.getAttribute(Attributes.MOVEMENT_SPEED).getValue() : 0)) {
                    setDeltaMovement(new Vec3(0, 0, 0));
                }
                shelled = (Entity) this instanceof OceanizedWitherEntity _datEntL50 && _datEntL50.getEntityData().get(DATA_shelled);
                if (((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) < ((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.5 && !shelled && spawn > 99) {
                    if (this.getAttributes().hasAttribute(Attributes.ARMOR))
                        this.getAttribute(Attributes.ARMOR)
                                .setBaseValue(((this.getAttributes().hasAttribute(Attributes.ARMOR) ? this.getAttribute(Attributes.ARMOR).getBaseValue() : 0) * 1.5));
                    if (this.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE.get()))
                        this.getAttribute(CAAttributes.GENERAL_DEFENSE.get())
                                .setBaseValue(((this.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE.get())
                                        ? this.getAttribute(CAAttributes.GENERAL_DEFENSE.get()).getBaseValue()
                                        : 0) * 1.5));
                    if (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
                        this.getAttribute(Attributes.ATTACK_DAMAGE)
                                .setBaseValue(((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : 0) * 1.5));
                    if ((Entity) this instanceof OceanizedWitherEntity animatable)
                        animatable.setTexture("oceanized_wither_anger");
                    if ((Entity) this instanceof OceanizedWitherEntity _datEntSetL)
                        _datEntSetL.getEntityData().set(DATA_shelled, true);
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
	public void performRangedAttack(LivingEntity target, float flval) {
		// TODO: Revisit this legacy system call when the pre-shot wither projectile path is cleaned up.
		WitherShootPreEntity.shoot(this, target);
	}

	private void rimedWitherShoot(LevelAccessor world) {
		InteractionResult start = InteractionResult.PASS;
		new Object() {
			void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                double angle = getYRot() + 30 * timedloopiterator;
                WorldUtils.shootWitherSkull(world, OceanizedWitherEntity.this, 0.1, Math.cos(Math.toRadians(angle)), Mth.nextDouble(RandomSource.create(), -0.25, 0.25), Math.sin(Math.toRadians(angle)), 5, 0.35, getX(),
                        getY() + Mth.nextDouble(RandomSource.create(), 0.25, 3), getZ());
                final int tick2 = ticks;
				CaerulaArborMod.queueServerWork(tick2, () -> {
					if (timedlooptotal > timedloopiterator + 1) {
						timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
					}
				});
			}
		}.timedLoop(0, 12, 1);
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

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.35);
		builder = builder.add(Attributes.MAX_HEALTH, 550);
		builder = builder.add(Attributes.ARMOR, 8);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 12);
		builder = builder.add(Attributes.FOLLOW_RANGE, 56);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
		builder = builder.add(Attributes.FLYING_SPEED, 0.35);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanzied_wither.die"));
			}
			if (this.isAggressive() && event.isMoving()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanzied_wither.aggresive"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanzied_wither.idle"));
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
		if (this.deathTime == 30) {
			this.remove(OceanizedWitherEntity.RemovalReason.KILLED);
			this.dropExperience();
			WorldUtils.dropMoistStar(this.level(), this.getX(), this.getY(), this.getZ(), this);
		}
	}

	@Override
	public void remove(RemovalReason pReason){
		if(this.level().getDifficulty() != Difficulty.PEACEFUL && pReason == RemovalReason.DISCARDED){
			this.hurt(
				new DamageSource(
					this.level().registryAccess().
					registryOrThrow(Registries.DAMAGE_TYPE).
					getHolderOrThrow(
						ResourceKey.create(
							Registries.DAMAGE_TYPE, 
							new ResourceLocation(CaerulaArborMod.MODID, "oceankiller_damage")
						)
					)
				),
				20
			);
			return;
		}
		super.remove(pReason);
	}

	@Override
    public void setHealth(float pHealth){
    	float hlth = this.getHealth();
    	float mhlth = this.getMaxHealth();
        if(this.hasEffect(CAMobEffects.INVULNERABLE.get()) && pHealth < hlth) return;
        float reduction = hlth - pHealth;
        super.setHealth(reduction >= mhlth * 0.35f ? hlth - mhlth * 0.35f : hlth - reduction);
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

	public void gatlinWitherShoot(LevelAccessor world, double x, double y, double z) {
		double vx = 0;
		double vy = 0;
		double vz = 0;
		new Object() {
			void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                double dist;
                double rng;
                dist = Mth.nextDouble(RandomSource.create(), 0, 16);
                rng = Mth.nextDouble(RandomSource.create(), 0, 6.283);
                WorldUtils.shootWitherSkull(world, OceanizedWitherEntity.this, 0.15, 0, -1, 0, 6, 0.35, x + dist * Math.cos(rng), y + Mth.nextDouble(RandomSource.create(), 4, 8), z + dist * Math.sin(rng));
                final int tick2 = ticks;
				CaerulaArborMod.queueServerWork(tick2, () -> {
					if (timedlooptotal > timedloopiterator + 1) {
						timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
					}
				});
			}
		}.timedLoop(0, 20, 1);
	}


	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}
}
