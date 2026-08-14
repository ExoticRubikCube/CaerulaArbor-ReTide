package com.susen36.caerulaarbor.entity;


import com.susen36.babel.init.BabelAttributes;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.entity.warden.OceanizedWardenisEntity;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.util.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class IzumikOffspringEntity extends SeaMonster {
	public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(IzumikOffspringEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(IzumikOffspringEntity.class, EntityDataSerializers.STRING);
	private static final TagKey<EntityType<?>> ENTITY_TAG = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "izumik_discovers"));
	public String animationprocedure = "empty";

	public IzumikOffspringEntity(Level world) {
		this(CAEntities.IZUMIK_OFFSPRING.get(), world);
	}

	public IzumikOffspringEntity(EntityType<IzumikOffspringEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);
		this.moveControl = new FlyingMoveControl(this, 10, true);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_SHOOT, false);
		builder.define(DATA_ANIMATION, "undefined");
	}


	public boolean spawnRandomEntityFromTag(ServerLevel serverLevel, double x, double y, double z) {
		if (serverLevel == null) {
			return false;
		}
		return spawnEntity(serverLevel, x, y, z);
	}

	public static Optional<EntityType<?>> randomEntityTypeInTag(Level level, TagKey<EntityType<?>> tag) {
		Registry<EntityType<?>> registry = level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE);
		Optional<HolderSet.Named<EntityType<?>>> optional = registry.getTag(tag);
		if (optional.isEmpty()) {
			return Optional.empty();
		}
		List<Holder<EntityType<?>>> entitiesInTag = optional.get().stream().toList();
		if (entitiesInTag.isEmpty()) {
			return Optional.empty();
		}
		RandomSource random = level.getRandom();
		EntityType<?> selected = entitiesInTag.get(random.nextInt(entitiesInTag.size())).value();
		return Optional.of(selected);
	}

	private boolean spawnEntity(ServerLevel level, double x, double y, double z) {
		Optional<EntityType<?>> optionalEntityType = randomEntityTypeInTag(level, ENTITY_TAG);
		if (optionalEntityType.isEmpty()) {
			return false;
		}
		Entity entity = optionalEntityType.get().spawn(level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
		if (entity != null) {
			entity.setYRot(level.getRandom().nextFloat() * 360.0F);
			if ((Entity) this instanceof Mob before && entity instanceof Mob after) {
				Team team = before.getTeam();
				MinecraftServer server = after.getServer();
				if (server != null && team instanceof PlayerTeam playerTeam) {
					server.getScoreboard().addPlayerToTeam(after.getScoreboardName(), playerTeam);
				}
			}
			if (entity instanceof OceanizedWardenisEntity warden) {
				var mh = warden.getAttribute(Attributes.MAX_HEALTH);
				if (mh != null) {
					mh.setBaseValue(mh.getBaseValue() * 0.35);
				}
				warden.setHealth(warden.getMaxHealth());
			}
		}
		return entity != null;
	}

	@Override
	protected PathNavigation createNavigation(Level world) {
		return new FlyingPathNavigation(this, world);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, false));this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1, 20) {
			@Override
			protected Vec3 getPosition() {
				RandomSource random = IzumikOffspringEntity.this.getRandom();
				double dir_x = IzumikOffspringEntity.this.getX() + ((random.nextFloat() * 2 - 1) * 16);
				double dir_y = IzumikOffspringEntity.this.getY() + ((random.nextFloat() * 2 - 1) * 16);
				double dir_z = IzumikOffspringEntity.this.getZ() + ((random.nextFloat() * 2 - 1) * 16);
				return new Vec3(dir_x, dir_y, dir_z);
			}
		});
		this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(5, new FloatGoal(this));
	}

    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
		super.dropCustomDeathLoot(level, damageSource, recentlyHit);
		this.spawnAtLocation(new ItemStack(CAItems.COLOURFULL_JELLY.get()));
	}

	@Override
	public SoundEvent getAmbientSound() {
		return SoundEvents.SQUID_AMBIENT;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.SQUID_HURT;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.SQUID_DEATH;
	}

	@Override
	public boolean causeFallDamage(float l, float d, DamageSource source) {
		return false;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity sourceentity = source.getEntity();
        if (sourceentity != null) {
            if (!isRemoved()) {
                if (this.isAlive()) {
                    boolean success = false;
                    if (distanceTo(sourceentity) <= 3.5) {
                        if (!world.isClientSide()) {
                        if (world instanceof ServerLevel serverLevel) {
                            success = spawnRandomEntityFromTag(serverLevel, x, y, z);
                        }
                    }
                        if (success) {
                            CaerulaArbor.LOGGER.info(("offspring at " + x + " " + y + " " + z + " changes"));
                            if (world instanceof ServerLevel level)
                                level.sendParticles(ParticleTypes.CLOUD, x, (y + 0.75), z, 32, 0.75, 0.75, 0.75, 0.1);
                            if (!level().isClientSide())
                                discard();
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
	public void baseTick() {
		super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        boolean success = false;
        Entity owner;
        if (tickCount % 5 == 0) {
            owner = world.getEntitiesOfClass(IzumikEntity.class, AABB.ofSize(new Vec3(x, y, z), 85, 32, 85), e -> true).stream().min(new Object() {
                Comparator<Entity> compareDistOf(double x, double y, double z) {
                    return Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(x, y, z));
                }
            }.compareDistOf(x, y, z)).orElse(null);
            if (!(owner == null)) {
                if (owner.getEntityData().get(IzumikEntity.DATA_PHASE) == 0)
					this.getNavigation().moveTo((owner.getX()), (owner.getY() + 4), (owner.getZ()), 0.75);
            } else {
                this.getNavigation().stop();
            }
			final Vec3 center = new Vec3(x, y, z);
			List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(4 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
			for (Entity entityiterator : entfound) {
				if (entityiterator == this) {
					continue;
				}
				if (!(entityiterator instanceof Mob)) {
					if (!(entityiterator instanceof Player)) {
						continue;
					} else {
						if (new Object() {
							public boolean checkGamemode(Entity ent) {
								if (ent instanceof ServerPlayer serverPlayer) {
									return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
								} else if (ent.level().isClientSide() && ent instanceof Player player) {
									return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
											&& Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
								}
								return false;
							}
						}.checkGamemode(entityiterator)) {
							continue;
						}
					}
				}
				if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "seaborn")))) {
					continue;
				}
				if (distanceTo(entityiterator) <= 2) {
					if (!world.isClientSide()) {
						if (world instanceof ServerLevel serverLevel) {
							success = spawnRandomEntityFromTag(serverLevel, x, y, z);
						}
					}
					if (success) {
						CaerulaArbor.LOGGER.info(("offspring at " + x + " " + y + " " + z + " changes"));
						if (world instanceof ServerLevel level)
							level.sendParticles(ParticleTypes.CLOUD, x, (y + 0.75), z, 32, 0.75, 0.75, 0.75, 0.1);
						if (!level().isClientSide())
							discard();
						break;
					}
				}
			}
        }

        this.refreshDimensions();
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

	public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
		event.register(CAEntities.IZUMIK_OFFSPRING.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (entityType, world, reason, pos, random) -> {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			return WorldUtils.canCommonSeabornSpawn(world, x, y, z);
		}, RegisterSpawnPlacementsEvent.Operation.REPLACE);
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.4);
		builder = builder.add(Attributes.MAX_HEALTH, 80);
		builder = builder.add(Attributes.ARMOR, 15);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 8);
		builder = builder.add(Attributes.FOLLOW_RANGE, 16);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.35);
		builder = builder.add(Attributes.FLYING_SPEED, 0.4);
		builder = builder.add(BabelAttributes.MAGIC_RESISTANCE, 40);
		builder = builder.add(Attributes.STEP_HEIGHT, 0.6f);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.izumik_offspring.die"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.izumik_offspring.idle"));
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

	public String getSyncedAnimation() {
		return this.entityData.get(DATA_ANIMATION);
	}

	public void setAnimation(String animation) {
		this.entityData.set(DATA_ANIMATION, animation);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		data.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
		data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
	}


	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}
}