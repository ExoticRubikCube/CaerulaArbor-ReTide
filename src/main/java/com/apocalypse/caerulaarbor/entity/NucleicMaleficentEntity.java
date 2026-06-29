package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.api.event.SanityEvent;
import com.apocalypse.caerulaarbor.capability.sanity.SIHelper;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.*;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
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
import java.util.List;

public class NucleicMaleficentEntity extends SeaMonster {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(NucleicMaleficentEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(NucleicMaleficentEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(NucleicMaleficentEntity.class, EntityDataSerializers.STRING);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public NucleicMaleficentEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CAEntities.NUCLEIC_MALEFICENT.get(), world);
	}

	public NucleicMaleficentEntity(EntityType<NucleicMaleficentEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);
		setMaxUpStep(0.6f);
		this.setPathfindingMalus(BlockPathTypes.WATER, 0);
		this.moveControl = new MoveControl(this) {
			@Override
			public void tick() {
				if (NucleicMaleficentEntity.this.isInWater())
					NucleicMaleficentEntity.this.setDeltaMovement(NucleicMaleficentEntity.this.getDeltaMovement().add(0, 0.005, 0));
				if (this.operation == MoveControl.Operation.MOVE_TO && !NucleicMaleficentEntity.this.getNavigation().isDone()) {
					double dx = this.wantedX - NucleicMaleficentEntity.this.getX();
					double dy = this.wantedY - NucleicMaleficentEntity.this.getY();
					double dz = this.wantedZ - NucleicMaleficentEntity.this.getZ();
					float f = (float) (Mth.atan2(dz, dx) * (180 / Math.PI)) - 90;
					float f1 = (float) (this.speedModifier * NucleicMaleficentEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
					NucleicMaleficentEntity.this.setYRot(this.rotlerp(NucleicMaleficentEntity.this.getYRot(), f, 10));
					NucleicMaleficentEntity.this.yBodyRot = NucleicMaleficentEntity.this.getYRot();
					NucleicMaleficentEntity.this.yHeadRot = NucleicMaleficentEntity.this.getYRot();
					if (NucleicMaleficentEntity.this.isInWater()) {
						NucleicMaleficentEntity.this.setSpeed((float) NucleicMaleficentEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
						float f2 = -(float) (Mth.atan2(dy, (float) Math.sqrt(dx * dx + dz * dz)) * (180 / Math.PI));
						f2 = Mth.clamp(Mth.wrapDegrees(f2), -85, 85);
						NucleicMaleficentEntity.this.setXRot(this.rotlerp(NucleicMaleficentEntity.this.getXRot(), f2, 5));
						float f3 = Mth.cos(NucleicMaleficentEntity.this.getXRot() * (float) (Math.PI / 180.0));
						NucleicMaleficentEntity.this.setZza(f3 * f1);
						NucleicMaleficentEntity.this.setYya((float) (f1 * dy));
					} else {
						NucleicMaleficentEntity.this.setSpeed(f1 * 0.05F);
					}
				} else {
					NucleicMaleficentEntity.this.setSpeed(0);
					NucleicMaleficentEntity.this.setYya(0);
					NucleicMaleficentEntity.this.setZza(0);
				}
			}
		};
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "nucleic_maleficent");
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
		return new WaterBoundPathNavigation(this, world);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 2, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 4.84;
			}
		});
		this.goalSelector.addGoal(3, new RandomSwimmingGoal(this, 1, 40));
		this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
	}

    @Override
	public SoundEvent getAmbientSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.parrot.imitate.phantom"));
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "seaborn_generic_hit"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.phantom.death"));
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		if (!this.level().isClientSide()) {
			CaerulaArborMod.queueServerWork(12, () -> {
				if (this.isAlive() && target.isAlive() && this.distanceTo(target) <= 2.5) {
					target.hurt(
							new DamageSource(
									this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
											.getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "general_seaborn_attack"))),
									this),
							(float) (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0));
				}
			});
		}
		return true;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.DROWN))
			return false;
		return super.hurt(source, amount);
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
        if (this.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get()))
            this.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).setBaseValue(45);
        if (this.getAttributes().hasAttribute(CAAttributes.SANITY_RATE.get()))
            this.getAttribute(CAAttributes.SANITY_RATE.get()).setBaseValue(6);
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
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        double angle;
        if (this.getTarget() != null && ((Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null).isAlive()) {
            if ((Entity) this instanceof Mob _mobEnt4 && _mobEnt4.isAggressive() && this.isAlive() && tickCount % 20 == 0) {
                for (int index0 = 0; index0 < 120; index0++) {
                    angle = Mth.nextDouble(RandomSource.create(), 0, 6.283);
                    if (world instanceof ServerLevel _level)
                        _level.sendParticles(ParticleTypes.ELECTRIC_SPARK, (x + 5 * Math.sin(angle)), (y + 0.33), (z + 5 * Math.cos(angle)), 3, 0.1, 0.1, 0.1, 0.2);
                }
                {
                    final Vec3 _center = new Vec3(x, y, z);
                    List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(10 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                    for (Entity entityiterator : _entfound) {
                        if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring"))) && !(entityiterator == ((Entity) this instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null))) {
                            continue;
                        }
                        if ((entityiterator != null ? distanceTo(entityiterator) : -1) < 5) {
                            if (!(entityiterator == this)) {
                                if (entityiterator instanceof LivingEntity target) {
                                    SIHelper.causeSanityInjury(target,
                                            this,
                                            (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 6,
                                            SanityEvent.Hurt.Type.ENTITY);
                                }
                            }
                        }
                    }
                }
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
                            for (int index2 = 0; index2 < 2; index2++) {
                                block = (world.getBlockState(BlockPos.containing(x + dx, y + dy, z + dz)));
                                hardness = block.getDestroySpeed(world, BlockPos.containing(0, 0, 0));
                                if (hardness <= 5 && hardness >= 0 && world.getBlockFloorHeight(BlockPos.containing(x + dx, y + dy, z + dz)) > 0 || block.getBlock() == CABlocks.WHITE_CHITIN_BLOCK.get()) {
                                    {
                                        BlockPos _pos = BlockPos.containing(x + dx, y + dy, z + dz);
                                        Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(x, y, z), null);
                                        world.destroyBlock(_pos, false);
                                    }
                                    if (world instanceof Level _level)
                                        _level.updateNeighborsAt(BlockPos.containing(x + dx, y + dy, z + dz), _level.getBlockState(BlockPos.containing(x + dx, y + dy, z + dz)).getBlock());
                                    once = true;
                                    if (block.getBlock() == CABlocks.WHITE_CHITIN_BLOCK.get()) {
                                        lose = lose + 0.1;
                                    } else {
                                        if (hardness < 1) {
                                            lose = lose + 0.01;
                                        } else if (hardness < 2.5) {
                                            lose = lose + 0.025;
                                        } else {
                                            lose = lose + 0.05;
                                        }
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
                        ((Entity) this).hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "sanity_break")))),
                                (float) (((Entity) this instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * lose));
                    }
                }
                if (!_mobEnt4.hasEffect(CAMobEffects.FAST_SWIM.get())) {
                    if (!this.level().isClientSide())
                        this.addEffect(new MobEffectInstance(CAMobEffects.FAST_SWIM.get(), 20, 2, false, false));
                }
            }
        }
        this.refreshDimensions();
	}

	@Override
	public EntityDimensions getDimensions(Pose p_33597_) {
		return super.getDimensions(p_33597_).scale((float) 1.25);
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	public boolean checkSpawnObstruction(LevelReader world) {
		return world.isUnobstructed(this);
	}

	@Override
	public boolean isPushedByFluid() {
		return false;
	}

	public static void init() {
		SpawnPlacements.register(CAEntities.NUCLEIC_MALEFICENT.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (entityType, world, reason, pos, random) -> {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
            if (!world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation(CaerulaArborMod.MODID, "deepmarine_spawn_biome")))) {
                return false;
            }
            if (Math.random() * 100 < (world.getLevelData().getGameRules().getInt(CAGameRules.SEABORN_SPAWN_RATE))) {
                return world.getDifficulty() != Difficulty.PEACEFUL;
            }
            return false;
        });
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 1.25);
		builder = builder.add(Attributes.MAX_HEALTH, 120);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 9);
		builder = builder.add(Attributes.FOLLOW_RANGE, 16);
		builder = builder.add(ForgeMod.SWIM_SPEED.get(), 1.25);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.nucleic_maleficent.die"));
			}
			if (this.isAggressive() && event.isMoving()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.nucleic_maleficent.swim"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.nucleic_maleficent.idle"));
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
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.nucleic_maleficent.attack"));
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
			this.remove(NucleicMaleficentEntity.RemovalReason.KILLED);
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
