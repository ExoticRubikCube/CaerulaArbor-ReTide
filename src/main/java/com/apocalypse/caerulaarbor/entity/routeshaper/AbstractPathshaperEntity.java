package com.apocalypse.caerulaarbor.entity.routeshaper;

import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CAGameRules;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Comparator;
import java.util.List;

public abstract class AbstractPathshaperEntity extends SeaMonster {
	protected static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(AbstractPathshaperEntity.class, EntityDataSerializers.STRING);
	public String animationprocedure = "empty";
	protected String prevAnim = "empty";

	protected AbstractPathshaperEntity(EntityType<? extends AbstractPathshaperEntity> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(ANIMATION, "undefined");
	}

	protected abstract EntityDataAccessor<Integer> getHurtSkillpAccessor();

	protected abstract int getHurtSummonThreshold();

	protected abstract EntityDataAccessor<Integer> getAttackSkillpAccessor();

	protected abstract EntityType<?> getSummonedFractalType();

	protected int getHurtSkillp() {
		return this.entityData.get(this.getHurtSkillpAccessor());
	}

	protected void setHurtSkillp(int hurtSkillp) {
		this.entityData.set(this.getHurtSkillpAccessor(), hurtSkillp);
	}

	protected int getAttackSkillp() {
		return this.entityData.get(this.getAttackSkillpAccessor());
	}

	protected void setAttackSkillp(int attackSkillp) {
		this.entityData.set(this.getAttackSkillpAccessor(), attackSkillp);
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

	public String getSyncedAnimation() {
		return this.entityData.get(ANIMATION);
	}

	public void setAnimation(String animation) {
		this.entityData.set(ANIMATION, animation);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.FALL))
			return false;
		if (source.is(DamageTypes.DROWN))
			return false;
		boolean flag = super.hurt(source, amount);
		if (flag) {
			Entity sourceEntity = source.getEntity();
			int nextHurtSkillp = this.getHurtSkillp() + 1;
			if (nextHurtSkillp >= this.getHurtSummonThreshold()) {
				this.summonFractal();
				if (Math.random() < 0.33) {
					this.summonFractal();
				}
				this.setHurtSkillp(0);
			} else {
				this.setHurtSkillp(nextHurtSkillp);
			}

			if (sourceEntity instanceof LivingEntity attacker) {
				Vec3 center = new Vec3(this.getX(), this.getY(), this.getZ());
				List<Entity> nearbyEntities = this.level().getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(64 / 2d), entity -> true).stream()
					.sorted(Comparator.comparingDouble(entity -> entity.distanceToSqr(center)))
					.toList();
				for (Entity nearbyEntity : nearbyEntities) {
					if (nearbyEntity instanceof RouteFractalEntity || nearbyEntity instanceof LingeringFractalEntity) {
						if (nearbyEntity instanceof Mob mob) {
							mob.setTarget(attacker);
						}
					}
				}
			}
		}
		return flag;
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		boolean flag = super.doHurtTarget(target);
		if (flag) {
			if (this.getAttackSkillp() >= 2) {
				this.summonFractal();
				this.setAttackSkillp(0);
			} else {
				this.setAttackSkillp(this.getAttackSkillp() + 1);
			}
		}
		return flag;
	}

	@Override
	public void baseTick() {
		super.baseTick();
		if (this.tickCount % 20 == 7) {
			if (this instanceof LineringPathshaperEntity || this instanceof RouteShaperEntity routeShaper && routeShaper.getEntityData().get(RouteShaperEntity.DATA_phase) == 1) {
				Vec3 center = new Vec3(this.getX(), this.getY(), this.getZ());
				List<Entity> nearbyEntities = this.level().getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(64 / 2d), entity -> true).stream()
					.sorted(Comparator.comparingDouble(entity -> entity.distanceToSqr(center)))
					.toList();
				for (Entity nearbyEntity : nearbyEntities) {
					if (nearbyEntity instanceof RouteFractalEntity routeFractal && !routeFractal.hasEffect(CAMobEffects.SEEK_OF_FRACTAL.get())) {
						if (!routeFractal.level().isClientSide()) {
							routeFractal.addEffect(new MobEffectInstance(CAMobEffects.SEEK_OF_FRACTAL.get(), 999, 0));
						}
					}
				}
			}
		}
		this.refreshDimensions();
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		// TODO: Other entities that still use MCreator-style flattened NBT keys should be migrated to semantic split keys too.
		compound.putInt("AttackCount", this.getAttackSkillp());
		compound.putInt("HurtCount", this.getHurtSkillp());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("AttackCount"))
			this.setAttackSkillp(compound.getInt("AttackCount"));
		if (compound.contains("HurtCount"))
			this.setHurtSkillp(compound.getInt("HurtCount"));
	}
}
