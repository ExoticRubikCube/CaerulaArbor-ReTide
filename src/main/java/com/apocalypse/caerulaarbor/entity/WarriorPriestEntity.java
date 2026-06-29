package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CAEntities;
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
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
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

public class WarriorPriestEntity extends Animal implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(WarriorPriestEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(WarriorPriestEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(WarriorPriestEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_skill_p = SynchedEntityData.defineId(WarriorPriestEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_skill_p1 = SynchedEntityData.defineId(WarriorPriestEntity.class, EntityDataSerializers.INT);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public WarriorPriestEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CAEntities.WARRIOR_PRIEST.get(), world);
	}

	public WarriorPriestEntity(EntityType<WarriorPriestEntity> type, Level world) {
		super(type, world);
		xpReward = 11;
		setNoAi(false);
		setMaxUpStep(0.8f);
		setPersistenceRequired();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "warriorpriest_texture");
		this.entityData.define(DATA_skill_p, 100);
		this.entityData.define(DATA_skill_p1, 200);
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
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 4.84;
			}
		});
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Monster.class, true, false));
		this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1));
		this.goalSelector.addGoal(5, new OpenDoorGoal(this, false));
		this.goalSelector.addGoal(6, new OpenDoorGoal(this, true));
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

	protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHitIn) {
		super.dropCustomDeathLoot(source, looting, recentlyHitIn);
		this.spawnAtLocation(new ItemStack(Blocks.WHITE_TULIP));
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.hurt"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.death"));
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		if (!this.level().isClientSide()) {
			CaerulaArborMod.queueServerWork(8, () -> {
				if (this.isAlive() && target.isAlive()) {
					target.hurt(
							new DamageSource(
									this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
											.getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "generic_warrior_attack"))),
									this),
							(float) (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0));
					if (target instanceof LivingEntity livingTarget && !livingTarget.level().isClientSide()) {
						livingTarget.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 1, false, true));
					}
				}
			});
		}
		return true;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity sourceentity = source.getEntity();
        if (sourceentity != null) {
            double dist = 0;
            if (this.isAlive() && sourceentity.isAlive()) {
                if (distanceTo(sourceentity) <= 2.5 && ((Entity) this instanceof WarriorPriestEntity _datEntI ? _datEntI.getEntityData().get(DATA_skill_p) : 0) <= 0) {
                    if (this instanceof WarriorPriestEntity) {
                        this.setAnimation("animation.warriorpriest.shieldattack");
                    }
                    if ((Entity) this instanceof WarriorPriestEntity _datEntSetI)
                        _datEntSetI.getEntityData().set(DATA_skill_p, 150);
                    CaerulaArborMod.queueServerWork(15, () -> {
                        if (sourceentity.isAlive()) {
                            ((Entity) this).lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((sourceentity.getX()), (sourceentity.getY()), (sourceentity.getZ())));
                            if (world instanceof Level _level) {
                                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("intentionally_empty")), SoundSource.HOSTILE, (float) 2.5, 1);
                            }
                            sourceentity.hurt(
                                    new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "generic_warrior_attack"))), this),
                                    (float) ((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 2));
                            sourceentity.push(0, 0.5, 0);
                        }
                    });
                }
            }
        }
        return super.hurt(source, amount);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putInt("Dataskill_p", this.entityData.get(DATA_skill_p));
		compound.putInt("Dataskill_p1", this.entityData.get(DATA_skill_p1));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("Dataskill_p"))
			this.entityData.set(DATA_skill_p, compound.getInt("Dataskill_p"));
		if (compound.contains("Dataskill_p1"))
			this.entityData.set(DATA_skill_p1, compound.getInt("Dataskill_p1"));
	}

	@Override
	public void baseTick() {
		super.baseTick();
        LevelAccessor world = this.level();
        double sklp1;
        double skillp;
        if (this.isAlive()) {
            skillp = (Entity) this instanceof WarriorPriestEntity _datEntI ? _datEntI.getEntityData().get(DATA_skill_p) : 0;
            sklp1 = (Entity) this instanceof WarriorPriestEntity _datEntI ? _datEntI.getEntityData().get(DATA_skill_p1) : 0;
            if (skillp > 0) {
                if ((Entity) this instanceof WarriorPriestEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_skill_p, (int) (skillp - 1));
            }
            if (sklp1 > 0) {
                if ((Entity) this instanceof WarriorPriestEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_skill_p1, (int) (sklp1 - 1));
            } else {
                if (this instanceof WarriorPriestEntity) {
                    this.setAnimation("animation.warriorpriest.givebuff");
                }
                if ((Entity) this instanceof WarriorPriestEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_skill_p1, 400);
                {
                    final Vec3 _center = new Vec3(this.getX(), this.getY(), this.getZ());
                    List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(16 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                    for (Entity entityiterator : _entfound) {
                        if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "homo_sapiens")))) {
                            if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
                                _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 400, 0));
                            if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
                                _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400, 0));
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
		WarriorPriestEntity retval = CAEntities.WARRIOR_PRIEST.get().create(serverWorld);
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
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.25);
		builder = builder.add(Attributes.MAX_HEALTH, 40);
		builder = builder.add(Attributes.ARMOR, 7);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 11);
		builder = builder.add(Attributes.FOLLOW_RANGE, 24);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

			) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.warriorpriest.move"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.warriorpriest.dead"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.warriorpriest.idle"));
		}
		return PlayState.STOP;
	}

	private PlayState attackingPredicate(AnimationState event) {
		if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
			this.swinging = true;
			this.lastSwing = level().getGameTime();
		}
		if (this.swinging && this.lastSwing + 19L <= level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.warriorpriest.attack"));
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
			this.remove(WarriorPriestEntity.RemovalReason.KILLED);
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
