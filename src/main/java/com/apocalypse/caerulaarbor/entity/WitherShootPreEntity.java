package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import com.apocalypse.caerulaarbor.utils.WorldUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
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
import net.minecraftforge.network.PlayMessages;

@OnlyIn(value = Dist.CLIENT, _interface = ItemSupplier.class)
public class WitherShootPreEntity extends AbstractArrow implements ItemSupplier {
	public static final ItemStack PROJECTILE_ITEM = new ItemStack(Blocks.WITHER_SKELETON_SKULL);

	public WitherShootPreEntity(PlayMessages.SpawnEntity packet, Level world) {
		super(CaerulaArborModEntities.WITHER_SHOOT_PRE.get(), world);
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
        if (entity == null || this == null || sourceentity == null)
            return;
        Entity enemy = null;
        Entity otherOne = null;
        Entity otherTwo = null;
        if (entity instanceof OceanizedWitherEntity) {
            return;
        }
        if ((sourceentity instanceof OceanizedWitherEntity _datEntI ? _datEntI.getEntityData().get(OceanizedWitherEntity.DATA_duration) : 0) > 0) {
            if (!level().isClientSide())
                discard();
            return;
        }
        enemy = sourceentity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null;
        if (enemy == null || !enemy.isAlive()) {
            if (!level().isClientSide())
                discard();
            return;
        }
        WorldUtils.shootWitherTo(world, sourceentity, enemy);
        otherOne = EntityUtils.getNearestEnemy(world, x, y, z, enemy, null, sourceentity);
        if (otherOne == null || !otherOne.isAlive()) {
            otherOne = enemy;
        }
        WorldUtils.shootWitherTo(world, sourceentity, otherOne);
        otherTwo = EntityUtils.getNearestEnemy(world, x, y, z, enemy, otherOne, sourceentity);
        if (otherTwo == null || !otherTwo.isAlive()) {
            otherTwo = enemy;
        }
        WorldUtils.shootWitherTo(world, sourceentity, otherTwo);
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
            Entity enemy = null;
            Entity otherOne = null;
            Entity otherTwo = null;
            if ((entity instanceof OceanizedWitherEntity _datEntI ? _datEntI.getEntityData().get(OceanizedWitherEntity.DATA_duration) : 0) > 0) {
                if (!level().isClientSide())
                    discard();
            } else {
                enemy = entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null;
                if (enemy == null || !enemy.isAlive()) {
                    if (!level().isClientSide())
                        discard();
                } else {
                    WorldUtils.shootWitherTo(world, entity, enemy);
                    otherOne = EntityUtils.getNearestEnemy(world, x, y, z, enemy, enemy, entity);
                    if (otherOne == null || !otherOne.isAlive()) {
                        otherOne = enemy;
                    }
                    WorldUtils.shootWitherTo(world, entity, otherOne);
                    otherTwo = EntityUtils.getNearestEnemy(world, x, y, z, enemy, otherOne, entity);
                    if (otherTwo == null || !otherTwo.isAlive()) {
                        otherTwo = enemy;
                    }
                    WorldUtils.shootWitherTo(world, entity, otherTwo);
                    if (!level().isClientSide())
                        discard();
                }
            }
        }
        if (this.inGround)
			this.discard();
	}

	public static WitherShootPreEntity shoot(Level world, LivingEntity entity, RandomSource source) {
		return shoot(world, entity, source, 0.1f, 0, 0);
	}

	public static WitherShootPreEntity shoot(Level world, LivingEntity entity, RandomSource source, float pullingPower) {
		return shoot(world, entity, source, pullingPower * 0.1f, 0, 0);
	}

	public static WitherShootPreEntity shoot(Level world, LivingEntity entity, RandomSource random, float power, double damage, int knockback) {
		WitherShootPreEntity entityarrow = new WitherShootPreEntity(CaerulaArborModEntities.WITHER_SHOOT_PRE.get(), entity, world);
		entityarrow.shoot(entity.getViewVector(1).x, entity.getViewVector(1).y, entity.getViewVector(1).z, power * 2, 0);
		entityarrow.setSilent(true);
		entityarrow.setCritArrow(false);
		entityarrow.setBaseDamage(damage);
		entityarrow.setKnockback(knockback);
		world.addFreshEntity(entityarrow);
		return entityarrow;
	}

	// TODO: Revisit this legacy two-arg helper when the pre-shot wither projectile API is refactored.
	public static WitherShootPreEntity shoot(LivingEntity entity, LivingEntity target) {
		WitherShootPreEntity entityarrow = new WitherShootPreEntity(CaerulaArborModEntities.WITHER_SHOOT_PRE.get(), entity, entity.level());
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
