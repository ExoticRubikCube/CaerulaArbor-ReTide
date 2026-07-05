package com.apocalypse.caerulaarbor.entity.bullets;

import com.apocalypse.caerulaarbor.entity.wither.AbstractOceanizedWitherEntity;
import com.apocalypse.caerulaarbor.entity.wither.OceanizedWitherEntity;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

@OnlyIn(value = Dist.CLIENT, _interface = ItemSupplier.class)
public class WitherShootPreEntity extends AbstractArrow implements ItemSupplier {
	public static final ItemStack PROJECTILE_ITEM = new ItemStack(Blocks.WITHER_SKELETON_SKULL);

	public WitherShootPreEntity(Level world) {
		super(CAEntities.WITHER_SHOOT_PRE.get(), world);
	}

	public WitherShootPreEntity(EntityType<? extends WitherShootPreEntity> type, Level world) {
		super(type, world);
	}

	public WitherShootPreEntity(EntityType<? extends WitherShootPreEntity> type, double x, double y, double z, Level world) {
		super(type, x, y, z, world);
	}

	public WitherShootPreEntity(EntityType<? extends WitherShootPreEntity> type, LivingEntity entity, Level world) {
		super(type, entity, world);
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemStack getItem() {
		return PROJECTILE_ITEM;
	}

	@Override
	protected ItemStack getPickupItem() {
		return PROJECTILE_ITEM;
	}

	@Override
	protected void doPostHurtEffects(LivingEntity entity) {
		super.doPostHurtEffects(entity);
		entity.setArrowCount(entity.getArrowCount() - 1);
	}

	@Override
	public void onHitEntity(EntityHitResult entityHitResult) {
		super.onHitEntity(entityHitResult);
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity entity = entityHitResult.getEntity();
        Entity sourceentity = this.getOwner();
        if (sourceentity == null)
            return;
        Entity target;
        Entity otherOne;
        Entity otherTwo;
        if (entity instanceof OceanizedWitherEntity) {
            return;
        }
        if ((sourceentity instanceof OceanizedWitherEntity datEntI ? datEntI.getEntityData().get(OceanizedWitherEntity.DATA_DURATION) : 0) > 0) {
            if (!level().isClientSide())
                discard();
            return;
        }
        target = sourceentity instanceof Mob mobEnt ? mobEnt.getTarget() : null;
        if (target == null || !target.isAlive()) {
            if (!level().isClientSide())
                discard();
            return;
        }
        this.shootWitherToTarget(world, sourceentity, target);
        otherOne = EntityUtils.getNearestEnemy(world, x, y, z, target, null, sourceentity);
        if (otherOne == null || !otherOne.isAlive()) {
            otherOne = target;
        }
        this.shootWitherToTarget(world, sourceentity, otherOne);
        otherTwo = EntityUtils.getNearestEnemy(world, x, y, z, target, otherOne, sourceentity);
        if (otherTwo == null || !otherTwo.isAlive()) {
            otherTwo = target;
        }
        this.shootWitherToTarget(world, sourceentity, otherTwo);
        if (!level().isClientSide())
            discard();
    }

	@Override
	public void tick() {
		super.tick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity entity = this.getOwner();
        if (entity != null && this != null) {
            Entity target;
            Entity otherOne;
            Entity otherTwo;
            if ((entity instanceof OceanizedWitherEntity datEntI ? datEntI.getEntityData().get(OceanizedWitherEntity.DATA_DURATION) : 0) > 0) {
                if (!level().isClientSide())
                    discard();
            } else {
                target = entity instanceof Mob mobEnt ? mobEnt.getTarget() : null;
                if (target == null || !target.isAlive()) {
                    if (!level().isClientSide())
                        discard();
                } else {
                    this.shootWitherToTarget(world, entity, target);
                    otherOne = EntityUtils.getNearestEnemy(world, x, y, z, target, target, entity);
                    if (otherOne == null || !otherOne.isAlive()) {
                        otherOne = target;
                    }
                    this.shootWitherToTarget(world, entity, otherOne);
                    otherTwo = EntityUtils.getNearestEnemy(world, x, y, z, target, otherOne, entity);
                    if (otherTwo == null || !otherTwo.isAlive()) {
                        otherTwo = target;
                    }
                    this.shootWitherToTarget(world, entity, otherTwo);
                    if (!level().isClientSide())
                        discard();
                }
            }
        }
        if (this.inGround)
			this.discard();
	}

	private void shootWitherToTarget(LevelAccessor world, Entity from, Entity target) {
		if (from == null || target == null) {
			return;
		}
		double vx = target.getX() - from.getX();
		double vy = target.getY() + target.getBbHeight() * 0.5 - (from.getY() + 2.7);
		double vz = target.getZ() - from.getZ();
		AbstractOceanizedWitherEntity.shootWitherSkull(world, from, 0.1, vx, vy, vz, 1, Mth.nextDouble(RandomSource.create(), 0.42, 0.56), from.getX(), from.getY() + 2.7, from.getZ());
	}

	public static WitherShootPreEntity shoot(Level world, LivingEntity entity, RandomSource source) {
		return shoot(world, entity, source, 0.1f, 0, 0);
	}

	public static WitherShootPreEntity shoot(Level world, LivingEntity entity, RandomSource source, float pullingPower) {
		return shoot(world, entity, source, pullingPower * 0.1f, 0, 0);
	}

	public static WitherShootPreEntity shoot(Level world, LivingEntity entity, RandomSource random, float power, double damage, int knockback) {
		WitherShootPreEntity entityarrow = new WitherShootPreEntity(CAEntities.WITHER_SHOOT_PRE.get(), entity, world);
		entityarrow.shoot(entity.getViewVector(1).x, entity.getViewVector(1).y, entity.getViewVector(1).z, power * 2, 0);
		entityarrow.setSilent(true);
		entityarrow.setCritArrow(false);
		entityarrow.setBaseDamage(damage);
		entityarrow.setKnockback(knockback);
		world.addFreshEntity(entityarrow);
		return entityarrow;
	}

       // TODO：当前置凋灵弹 API 重构时，重新审视这个遗留的双参数接口。
	public static WitherShootPreEntity shoot(LivingEntity entity, LivingEntity target) {
		WitherShootPreEntity entityarrow = new WitherShootPreEntity(CAEntities.WITHER_SHOOT_PRE.get(), entity, entity.level());
		double dx = target.getX() - entity.getX();
		double dy = target.getY() + target.getEyeHeight() - 1.1;
		double dz = target.getZ() - entity.getZ();
		entityarrow.shoot(dx, dy - entityarrow.getY() + Math.hypot(dx, dz) * 0.2F, dz, 0.1f * 2, 12.0F);
		entityarrow.setSilent(true);
		entityarrow.setBaseDamage(0);
		entityarrow.setKnockback(0);
		entityarrow.setCritArrow(false);
		entity.level().addFreshEntity(entityarrow);
		return entityarrow;
	}
}
