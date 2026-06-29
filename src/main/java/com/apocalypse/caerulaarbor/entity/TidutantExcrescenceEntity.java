package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CABlocks;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.procedures.TrailReplaceProcedure;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import com.apocalypse.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
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
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class TidutantExcrescenceEntity extends SeaMonster {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(TidutantExcrescenceEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(TidutantExcrescenceEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(TidutantExcrescenceEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_mute_time = SynchedEntityData.defineId(TidutantExcrescenceEntity.class, EntityDataSerializers.INT);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public TidutantExcrescenceEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CAEntities.TIDUTANT_EXCRESCENCE.get(), world);
	}

	public TidutantExcrescenceEntity(EntityType<TidutantExcrescenceEntity> type, Level world) {
		super(type, world);
		xpReward = 8;
		setNoAi(false);
		setMaxUpStep(0.6f);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "tidutant_excrescence");
		this.entityData.define(DATA_mute_time, 0);
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
				return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth();
			}
		});
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true, false));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, SnowGolem.class, true, false));
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Villager.class, true, false));
		this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Illusioner.class, true, false));
		this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, Pillager.class, true, false));
		this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, Vindicator.class, true, false));
		this.targetSelector.addGoal(9, new NearestAttackableTargetGoal<>(this, Witch.class, true, false));
		this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, Piglin.class, true, false));
		this.targetSelector.addGoal(11, new NearestAttackableTargetGoal<>(this, PiglinBrute.class, true, false));
		this.targetSelector.addGoal(12, new NearestAttackableTargetGoal<>(this, ZombifiedPiglin.class, true, false));
		this.targetSelector.addGoal(13, new NearestAttackableTargetGoal<>(this, Player.class, true, false) {
			@Override
			public boolean canUse() {
				double x = TidutantExcrescenceEntity.this.getX();
				double y = TidutantExcrescenceEntity.this.getY();
				double z = TidutantExcrescenceEntity.this.getZ();
				Level world = TidutantExcrescenceEntity.this.level();
				return super.canUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
			}

			@Override
			public boolean canContinueToUse() {
				double x = TidutantExcrescenceEntity.this.getX();
				double y = TidutantExcrescenceEntity.this.getY();
				double z = TidutantExcrescenceEntity.this.getZ();
				Level world = TidutantExcrescenceEntity.this.level();
				return super.canContinueToUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
			}
		});
		this.goalSelector.addGoal(14, new RandomStrollGoal(this, 0.8));
		this.goalSelector.addGoal(15, new RandomLookAroundGoal(this));
	}

    @Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "seaborn_generic_hit"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "seaborn_death"));
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
        Entity sourceentity = source.getEntity();
        if (sourceentity != null) {
            ResourceLocation EffectRes = new ResourceLocation("bobsoriginiumdream", "distortion");
            MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(EffectRes);
            if (effect != null) {
                MobEffectInstance effectInst = new MobEffectInstance(effect, 200, 1);
                if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                    _entity.addEffect(effectInst);
            }
        }
        return super.hurt(source, amount);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putInt("Datamute_time", this.entityData.get(DATA_mute_time));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("Datamute_time"))
			this.entityData.set(DATA_mute_time, compound.getInt("Datamute_time"));
	}

	@Override
	public void baseTick() {
		super.baseTick();
        if (this.isAlive()) {
            if ((Entity) this instanceof LivingEntity _livEnt1 && _livEnt1.hasEffect(CAMobEffects.MUTE.get())) {
                if ((Entity) this instanceof TidutantExcrescenceEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_mute_time,
                            (Entity) this instanceof LivingEntity _livEnt && _livEnt.hasEffect(CAMobEffects.MUTE.get()) ? _livEnt.getEffect(CAMobEffects.MUTE.get()).getDuration() : 0);
            } else if (((Entity) this instanceof TidutantExcrescenceEntity _datEntI ? _datEntI.getEntityData().get(DATA_mute_time) : 0) == 1) {
                if ((Entity) this instanceof TidutantExcrescenceEntity _datEntSetI)
                    _datEntSetI.getEntityData().set(DATA_mute_time, 0);
            }
        }
        this.refreshDimensions();
	}

	@Override
	public EntityDimensions getDimensions(Pose p_33597_) {
		return super.getDimensions(p_33597_).scale((float) 1);
	}

	public static void init() {
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.2);
		builder = builder.add(Attributes.MAX_HEALTH, 20);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 7);
		builder = builder.add(Attributes.FOLLOW_RANGE, 19);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

			) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.tidutant_excrescence.move"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.tidutant_excrescence.idle"));
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
		if (this.deathTime == 20) {
			this.remove(TidutantExcrescenceEntity.RemovalReason.KILLED);
			this.dropExperience();
            LevelAccessor world = this.level();
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();
            if (WorldUtils.canGrief(world)) {
                if (((Entity) this instanceof TidutantExcrescenceEntity _datEntI ? _datEntI.getEntityData().get(DATA_mute_time) : 0) <= 0) {
                    if (CABlocks.SEA_TRAIL_INIT.get().defaultBlockState().canSurvive(world, BlockPos.containing(x, y, z)) && !(world.getBlockFloorHeight(BlockPos.containing(x, y, z)) > 0)) {
                        TrailReplaceProcedure.execute(world, CABlocks.SEA_TRAIL_INIT.get().defaultBlockState(), (world.getFluidState(BlockPos.containing(x, y, z)).createLegacyBlock()).getBlock() == Blocks.WATER, x, y, z);
                    }
                    for (Direction directioniterator : Direction.Plane.HORIZONTAL) {
                        if (CABlocks.SEA_TRAIL_INIT.get().defaultBlockState().canSurvive(world, BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ()))
                                && !(world.getBlockFloorHeight(BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ())) > 0)) {
                            if (Math.random() < 0.25) {
                                TrailReplaceProcedure.execute(world, CABlocks.SEA_TRAIL_INIT.get().defaultBlockState(),
                                        (world.getFluidState(BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ())).createLegacyBlock()).getBlock() == Blocks.WATER, x + directioniterator.getStepX(), y,
                                        z + directioniterator.getStepZ());
                            }
                        }
                    }
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
		data.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
		data.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
	}
}
