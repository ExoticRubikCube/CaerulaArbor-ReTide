package com.susen36.caerulaarbor.entity.bullets;

import com.susen36.caerulaarbor.entity.base.BaseProjectile;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAParticles;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class HighmoreShootEntity extends BaseProjectile {

	public HighmoreShootEntity(Level world) {
		super(CAEntities.HIGHMORE_SHOOT.get(), world);
	}

	public HighmoreShootEntity(EntityType<? extends HighmoreShootEntity> type, Level world) {
		super(type, world);
	}

	public HighmoreShootEntity(EntityType<? extends HighmoreShootEntity> type, double x, double y, double z, Level world) {
		super(type, world);
		setPos(x, y, z);
	}

	public HighmoreShootEntity(EntityType<? extends HighmoreShootEntity> type, LivingEntity entity, Level world) {
		super(type, world);
		setOwner(entity);
		setPos(entity.getX(), entity.getY() - 0.1, entity.getZ());
	}


	@Override
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		if (!this.level().isClientSide()) {
			this.discard();
		}
	}

	@Override
	protected void onHitBlock(BlockHitResult blockHitResult) {
		super.onHitBlock(blockHitResult);
		if (!this.level().isClientSide()) {
			this.discard();
		}
	}

	@Override
	public void tick() {
		super.tick();
		Level world = this.level();
		setNoGravity(true);
		if (tickCount > 160) {
			if (!level().isClientSide())
				discard();
		}
		world.addParticle(CAParticles.SEA_SPLASH.get(), this.getX(), this.getY(), this.getZ(), 0, 0, 0);
		if (this.inGround)
			this.discard();
	}

	public static HighmoreShootEntity shoot(Level world, LivingEntity entity, RandomSource source) {
		return shoot(world, entity, source, 1.5f, 3.5, 0);
	}

	public static HighmoreShootEntity shoot(Level world, LivingEntity entity, RandomSource source, float pullingPower) {
		return shoot(world, entity, source, pullingPower * 1.5f, 3.5, 0);
	}

	public static HighmoreShootEntity shoot(Level world, LivingEntity entity, RandomSource random, float power, double damage, int knockback) {
		HighmoreShootEntity entityarrow = new HighmoreShootEntity(CAEntities.HIGHMORE_SHOOT.get(), entity, world);
		entityarrow.shoot(entity.getViewVector(1).x, entity.getViewVector(1).y, entity.getViewVector(1).z, power * 2, 0);
		entityarrow.setSilent(true);
		entityarrow.setBaseDamage(damage);
		entityarrow.setKnockback(knockback);
		world.addFreshEntity(entityarrow);
		world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.SQUID_SQUIRT, SoundSource.PLAYERS, 1, 1f / (random.nextFloat() * 0.5f + 1) + (power / 2));
		return entityarrow;
	}

	/**
	 * @deprecated 优先使用 {@link #shoot(LivingEntity, LivingEntity, double)}，以便调用方自行传入远程伤害倍率。
	 * 该回退重载会使用当前射手的平均倍率。
	 */
	@Deprecated
	public static HighmoreShootEntity shoot(LivingEntity entity, LivingEntity target) {
		return shoot(entity, target, (entity.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? entity.getAttributeValue(Attributes.ATTACK_DAMAGE) : 0) * (7.0 / 12.0));
	}

	public static HighmoreShootEntity shoot(LivingEntity entity, LivingEntity target, double damage) {
		HighmoreShootEntity entityarrow = new HighmoreShootEntity(CAEntities.HIGHMORE_SHOOT.get(), entity, entity.level());
		double dx = target.getX() - entity.getX();
		double dy = target.getY() + target.getEyeHeight() - 1.1;
		double dz = target.getZ() - entity.getZ();
		entityarrow.shoot(dx, dy - entityarrow.getY() + Math.hypot(dx, dz) * 0.2F, dz, 1.5f * 2, 12.0F);
		entityarrow.setSilent(true);
		entityarrow.setBaseDamage(damage);
		entity.level().addFreshEntity(entityarrow);
		entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.SQUID_SQUIRT, SoundSource.PLAYERS, 1, 1f / (RandomSource.create().nextFloat() * 0.5f + 1));
		return entityarrow;
	}
}