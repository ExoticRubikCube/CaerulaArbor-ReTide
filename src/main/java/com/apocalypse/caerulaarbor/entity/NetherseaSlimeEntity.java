package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAItems;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.DungeonHooks;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class NetherseaSlimeEntity extends SeaMonster {
	public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(NetherseaSlimeEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(NetherseaSlimeEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_SIZE = SynchedEntityData.defineId(NetherseaSlimeEntity.class, EntityDataSerializers.INT);
	public String animationprocedure = "empty";

	public NetherseaSlimeEntity(Level world) {
		this(CAEntities.NETHERSEA_SLIME.get(), world);
	}

	public NetherseaSlimeEntity(EntityType<NetherseaSlimeEntity> type, Level world) {
		super(type, world);
		xpReward = 2;
		setNoAi(false);
		setMaxUpStep(1f);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_SHOOT, false);
		this.entityData.define(DATA_ANIMATION, "undefined");
		this.entityData.define(DATA_SIZE, 4);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				NetherseaSlimeEntity slime = NetherseaSlimeEntity.this;
				int size = Math.max(slime.getEntityData().get(NetherseaSlimeEntity.DATA_SIZE), 1);
				return 0.5625 * size * size;
			}
		});
		this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1));
		this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(5, new FloatGoal(this));
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEFINED;
	}

	@Override
	public void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(SoundEvents.SLIME_JUMP_SMALL, 0.15f, 1);
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return SoundEvents.SLIME_HURT;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.SLIME_DEATH;
	}

	@Override
	public boolean causeFallDamage(float l, float d, DamageSource source) {
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        if (world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.SLIME_SQUISH, SoundSource.HOSTILE, 1, 1);
        }
        return super.causeFallDamage(l, d, source);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.FALL))
			return false;
		if (source.is(DamageTypes.DROWN))
			return false;
		return super.hurt(source, amount);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("Size", this.entityData.get(DATA_SIZE));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Size")) {
		    this.entityData.set(DATA_SIZE, compound.getInt("Size"));
		}
	}

	@Override
	public void baseTick() {
		super.baseTick();
		CompoundTag tag = this.getPersistentData();
		if(!tag.getBoolean("Resized")){
            double size;
            size = (Entity) this instanceof NetherseaSlimeEntity datEntI ? datEntI.getEntityData().get(DATA_SIZE) : 0;
            if (size > 1) {
                if (this.getAttributes().hasAttribute(Attributes.MAX_HEALTH))
                    this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(
                            ((this.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? this.getAttribute(Attributes.MAX_HEALTH).getBaseValue() : 0) * Math.pow(size, 2)));
                if (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
                    this.getAttribute(Attributes.ATTACK_DAMAGE)
                            .setBaseValue(((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : 0) * size));
                if (this.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED))
                    this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue((0.1 + 0.025 * Math.max(size, 4)));
                if (this.getAttributes().hasAttribute(Attributes.KNOCKBACK_RESISTANCE))
                    this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue((size * 0.2));
                this.setHealth(this.getMaxHealth());
            }
            tag.putBoolean("Resized", true);
		}
		this.refreshDimensions();
	}

	@Override
	public EntityDimensions getDimensions(Pose p_33597_) {
		Entity entity = this;
		return super.getDimensions(p_33597_).scale((float) EntityUtils.getSlimeSize(entity));
	}

	public static void registerDungeonMob() {
		DungeonHooks.addDungeonMob(CAEntities.NETHERSEA_SLIME.get(), 180);
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.075);
		builder = builder.add(Attributes.MAX_HEALTH, 2);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 2);
		builder = builder.add(Attributes.FOLLOW_RANGE, 24);
		return builder;
	}

	private PlayState movementPredicate(AnimationState<?> event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.05F && event.getLimbSwingAmount() < 0.05F))) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.nethersea_slime.move"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.nethersea_slime.die"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.nethersea_slime.idle"));
		}
		return PlayState.STOP;
	}

	String prevAnim = "empty";

	private PlayState procedurePredicate(AnimationState<?> event) {
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
		if (this.deathTime >= 14) {
			this.remove(NetherseaSlimeEntity.RemovalReason.KILLED);
			this.dropExperience();
            LevelAccessor world = this.level();
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();
            int size = (Entity) this instanceof NetherseaSlimeEntity datEntI ? datEntI.getEntityData().get(DATA_SIZE) : 0;
            if (size > 1) {
                size = (int) (size * 0.5);
                Vec3 pos = new Vec3(x, y, z);
                if (world instanceof ServerLevel level) {
                    RandomSource levelRandom = level.getRandom();
                    int t = Mth.nextInt(levelRandom, 2, 4);
                    for (int index0 = 0; index0 < t; index0++) {
                        Vec3 offset = new Vec3(Mth.nextDouble(levelRandom, -1, 1), 0, Mth.nextDouble(levelRandom, -1, 1));
                        Entity entityToSpawn = CAEntities.NETHERSEA_SLIME.get().create(level);
                        if (entityToSpawn instanceof NetherseaSlimeEntity slime){
                            slime.setPos(pos.add(offset));
                            slime.getEntityData().set(DATA_SIZE, size);
                            //SlimeAttrModifyProcedure.execute(slime);
                            slime.setYRot(world.getRandom().nextFloat() * 360F);
                            level.addFreshEntity(slime);
                        }
                    }
                }
            } else if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                if (world instanceof ServerLevel level) {
                    ItemEntity entityToSpawn = new ItemEntity(level, x, y, z, new ItemStack(CAItems.TRAIL_CREAM.get()));
                    entityToSpawn.setPickUpDelay(10);
                    level.addFreshEntity(entityToSpawn);
                }
            }
        }
	}

	@Override
	public void push(Entity pEntity){
		super.push(pEntity);
		if (pEntity instanceof NetherseaSlimeEntity) return;
		if (pEntity instanceof LivingEntity entity && !entity.level().isClientSide())
			entity.addEffect(new MobEffectInstance(CAMobEffects.DEDUCT_ONE_SANITY.get(), 70, 0));
	}

	public String getSyncedAnimation() {
		return this.entityData.get(DATA_ANIMATION);
	}

	public void setAnimation(String animation) {
		this.entityData.set(DATA_ANIMATION, animation);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		data.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
		data.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
	}

	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}
}

