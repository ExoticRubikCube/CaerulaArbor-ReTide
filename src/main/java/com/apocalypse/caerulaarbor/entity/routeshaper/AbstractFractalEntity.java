package com.apocalypse.caerulaarbor.entity.routeshaper;

import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CAGameRules;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
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
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractFractalEntity extends SeaMonster {
	protected static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(AbstractFractalEntity.class, EntityDataSerializers.STRING);
	protected static final EntityDataAccessor<Integer> DATA_ATTACK_SKILLP = SynchedEntityData.defineId(AbstractFractalEntity.class, EntityDataSerializers.INT);
	protected static final EntityDataAccessor<String> DATA_OWNER = SynchedEntityData.defineId(AbstractFractalEntity.class, EntityDataSerializers.STRING);
	public String animationprocedure = "empty";
	protected String prevAnim = "empty";
	protected boolean swinging;
	protected long lastSwing;

	protected AbstractFractalEntity(EntityType<? extends AbstractFractalEntity> entityType, Level level) {
		super(entityType, level);
		setNoAi(false);
		setMaxUpStep(1f);
		setPersistenceRequired();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(DATA_ATTACK_SKILLP, 0);
		this.entityData.define(DATA_OWNER, "null");
	}

	protected abstract EntityType<?> getSummonedFractalType();

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.5, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 6.25;
			}
		});
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false, false));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, SnowGolem.class, false, false));
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Villager.class, false, false));
		this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Illusioner.class, false, false));
		this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, Pillager.class, false, false));
		this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, Vindicator.class, false, false));
		this.targetSelector.addGoal(9, new NearestAttackableTargetGoal<>(this, Witch.class, false, false));
		this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, Piglin.class, false, false));
		this.targetSelector.addGoal(11, new NearestAttackableTargetGoal<>(this, PiglinBrute.class, false, false));
		this.targetSelector.addGoal(12, new NearestAttackableTargetGoal<>(this, ZombifiedPiglin.class, false, false));
		this.targetSelector.addGoal(13, new NearestAttackableTargetGoal<>(this, Player.class, false, false) {
			@Override
			public boolean canUse() {
				double x = AbstractFractalEntity.this.getX();
				double y = AbstractFractalEntity.this.getY();
				double z = AbstractFractalEntity.this.getZ();
				Level world = AbstractFractalEntity.this.level();
				return super.canUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
			}

			@Override
			public boolean canContinueToUse() {
				double x = AbstractFractalEntity.this.getX();
				double y = AbstractFractalEntity.this.getY();
				double z = AbstractFractalEntity.this.getZ();
				Level world = AbstractFractalEntity.this.level();
				return super.canContinueToUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
			}
		});
		this.goalSelector.addGoal(14, new RandomStrollGoal(this, 0.5));
		this.goalSelector.addGoal(15, new RandomLookAroundGoal(this));
	}

	protected int getAttackSkillp() {
		return this.entityData.get(DATA_ATTACK_SKILLP);
	}

	protected void setAttackSkillp(int attackSkillp) {
		this.entityData.set(DATA_ATTACK_SKILLP, attackSkillp);
	}

	protected String getOwner() {
		return this.entityData.get(DATA_OWNER);
	}

	protected void setOwner(String owner) {
		this.entityData.set(DATA_OWNER, owner);
	}

	protected void handleSuccessfulAttack() {
		if (this.getAttackSkillp() >= 2) {
			this.summonFractal();
			this.setAttackSkillp(0);
		} else {
			this.setAttackSkillp(this.getAttackSkillp() + 1);
		}
	}

	protected void summonFractal() {
		if (EntityUtils.getSeabornNum(this.level(), this.getX(), this.getY(), this.getZ()) > this.level().getLevelData().getGameRules().getInt(CAGameRules.CLONE_NUMBER_LIMIT)) {
			return;
		}
		double nearbyCount = 0;
		Vec3 center = new Vec3(this.getX(), this.getY(), this.getZ());
		List<Entity> nearbyEntities = this.level().getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(64 / 2d), entity -> true).stream()
			.sorted(Comparator.comparingDouble(entity -> entity.distanceToSqr(center)))
			.toList();
		for (Entity nearbyEntity : nearbyEntities) {
			if (nearbyEntity instanceof RouteFractalEntity || nearbyEntity instanceof LineringPathshaperEntity) {
				nearbyCount = nearbyCount + 1;
			}
		}
		if (nearbyCount >= 18) {
			return;
		}
		double offsetX = Mth.nextDouble(RandomSource.create(), -0.5, 0.5);
		double offsetZ = Mth.nextDouble(RandomSource.create(), -0.5, 0.5);
		if (this.level() instanceof ServerLevel serverLevel) {
			Entity entityToSpawn = this.getSummonedFractalType().spawn(serverLevel, BlockPos.containing(this.getX() + offsetX, this.getY(), this.getZ() + offsetZ), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
				entityToSpawn.setYRot(this.level().getRandom().nextFloat() * 360F);
			}
			serverLevel.sendParticles(ParticleTypes.CLOUD, this.getX() + offsetX, this.getY(), this.getZ() + offsetZ, 48, 0.5, 1, 0.5, 0.1);
		}
	}

	protected PlayState procedurePredicate(AnimationState<?> event) {
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

	/**
	 * 处理塑路分形系实体的移动待机动画。
	 *
	 * @param event GeckoLib 动画状态
	 * @return 对应控制器的播放状态
	 */
	protected PlayState movementPredicate(AnimationState<?> event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) && !this.isVehicle() && !this.isAggressive() && !this.isSprinting()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.routeshaper.move"));
			}
			if (this.isSprinting()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.routeshaper.move"));
			}
			if (this.isVehicle() && event.isMoving()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.routeshaper.move"));
			}
			if (this.isAggressive() && event.isMoving() && !this.isVehicle()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.routeshaper.move"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.routeshaper.idle"));
		}
		return PlayState.STOP;
	}

	/**
	 * 处理塑路分形系实体的普攻挥击动画。
	 *
	 * @param event GeckoLib 动画状态
	 * @return 对应控制器的播放状态
	 */
	protected PlayState attackingPredicate(AnimationState<?> event) {
		if (this.getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
			this.swinging = true;
			this.lastSwing = this.level().getGameTime();
		}
		if (this.swinging && this.lastSwing + 20L <= this.level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.routeshaper.attack"));
		}
		return PlayState.CONTINUE;
	}

	public String getSyncedAnimation() {
		return this.entityData.get(ANIMATION);
	}

	public void setAnimation(String animation) {
		this.entityData.set(ANIMATION, animation);
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("caerulaarbor", "seaborn_generic_hit"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("caerulaarbor", "seaborn_death"));
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.DROWN))
			return false;
		return super.hurt(source, amount);
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		boolean flag = super.doHurtTarget(target);
		if (flag) {
			this.handleSuccessfulAttack();
		}
		return flag;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("AttackCount", this.getAttackSkillp());
		compound.putString("Dataowner", this.getOwner());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("AttackCount"))
			this.setAttackSkillp(compound.getInt("AttackCount"));
		if (compound.contains("Dataowner"))
			this.setOwner(compound.getString("Dataowner"));
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
		world.playSound(null, BlockPos.containing(this.getX(), this.getY(), this.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.axolotl.splash")), SoundSource.HOSTILE, 0.75F, 1);
		return retval;
	}

	@Override
	protected void tickDeath() {
		++this.deathTime;
		if (this.deathTime == 20) {
			this.remove(RemovalReason.KILLED);
			this.dropExperience();
		}
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		data.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
		data.add(new AnimationController<>(this, "attacking", 0, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
	}

	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}
}
