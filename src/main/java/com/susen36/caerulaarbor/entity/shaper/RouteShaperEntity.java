package com.susen36.caerulaarbor.entity.shaper;

import com.susen36.babel.init.BabelAttributes;
import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

public class RouteShaperEntity extends AbstractPathshaperEntity {
	public RouteShaperEntity(Level world) {
		this(CAEntities.ROUTE_SHAPER.get(), world);
	}

	public RouteShaperEntity(EntityType<RouteShaperEntity> type, Level world) {
		super(type, world);
		bossInfo.setColor(ServerBossEvent.BossBarColor.BLUE);
		xpReward = 32;
	}

	@Override
	protected EntityType<?> getSummonedFractalType() {
		return CAEntities.ROUTE_FRACTAL.get();
	}

	@Override
	protected int getHurtSummonThreshold() {
		return 10;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1, false) {

			@Override
			public boolean canUse() {
				return super.canUse() && !hasEffect(CAMobEffects.FAKE_DEATH);
			}

			@Override
			public boolean canContinueToUse() {
				return super.canUse() && !hasEffect(CAMobEffects.FAKE_DEATH);
			}

		});
	}

	@Override
	public void die(DamageSource source) {
		super.die(source);
		if (this.isDeadOrDying()) {
			LevelAccessor world = this.level();
			final Vec3 center = new Vec3(this.getX(), this.getY(), this.getZ());
			List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(64 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
			for (Entity entityiterator : entfound) {
				if (entityiterator instanceof RouteFractalEntity fractal && fractal.hasOwner(this.getUUID())) {
					entityiterator.hurt(entityiterator.level().damageSources().fellOutOfWorld(), 999999);
				}
			}
		}
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = AbstractPathshaperEntity.createAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.2);
		builder = builder.add(Attributes.MAX_HEALTH, 140);
		builder = builder.add(Attributes.ARMOR, 8);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 9);
		builder = builder.add(Attributes.FOLLOW_RANGE, 48);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 1);
		builder = builder.add(CAAttributes.MAGIC_RESISTANCE, 24);
		builder = builder.add(BabelAttributes.MAX_ELEMENTAL_VALUE, 2000);
		return builder;
	}
}
