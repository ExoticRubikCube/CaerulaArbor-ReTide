package com.apocalypse.caerulaarbor.entity.bullets;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAItems;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

@OnlyIn(value = Dist.CLIENT, _interface = ItemSupplier.class)
public class ShotOceanArrowEntity extends AbstractArrow implements ItemSupplier {
	public static final ItemStack PROJECTILE_ITEM = new ItemStack(CAItems.OCEAN_ARROW.get());

	public ShotOceanArrowEntity(Level world) {
		super(CAEntities.SHOT_OCEAN_ARROW.get(), world);
	}

	public ShotOceanArrowEntity(EntityType<? extends ShotOceanArrowEntity> type, Level world) {
		super(type, world);
	}

	public ShotOceanArrowEntity(EntityType<? extends ShotOceanArrowEntity> type, double x, double y, double z, Level world) {
		super(type, x, y, z, world);
	}

	public ShotOceanArrowEntity(EntityType<? extends ShotOceanArrowEntity> type, LivingEntity entity, Level world) {
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
        Entity entity = entityHitResult.getEntity();
        Entity sourceentity = this.getOwner();
        if (sourceentity == null)
            return;
        if (!(entity == sourceentity)) {
            entity.invulnerableTime = 0;
        }
        CaerulaArborMod.queueServerWork(10, () -> {
            if (!level().isClientSide())
                discard();
        });
    }

	@Override
	public void tick() {
		super.tick();
        Entity entity = this.getOwner();
        if (entity != null) {
            Entity enemy;
            enemy = entity instanceof Mob mobEnt ? mobEnt.getTarget() : null;
            if (!(enemy == null)) {
                lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((enemy.getX()), (enemy.getY() + enemy.getBbHeight() * 0.75), (enemy.getZ())));
            }
        }
        if (this.inGround)
			this.discard();
	}

	public static ShotOceanArrowEntity shoot(Level world, LivingEntity entity, RandomSource source) {
		return shoot(world, entity, source, 2.5f, 2.5, 0);
	}

	public static ShotOceanArrowEntity shoot(Level world, LivingEntity entity, RandomSource source, float pullingPower) {
		return shoot(world, entity, source, pullingPower * 2.5f, 2.5, 0);
	}

	public static ShotOceanArrowEntity shoot(Level world, LivingEntity entity, RandomSource random, float power, double damage, int knockback) {
		ShotOceanArrowEntity entityarrow = new ShotOceanArrowEntity(CAEntities.SHOT_OCEAN_ARROW.get(), entity, world);
		entityarrow.shoot(entity.getViewVector(1).x, entity.getViewVector(1).y, entity.getViewVector(1).z, power * 2, 0);
		entityarrow.setSilent(true);
		entityarrow.setCritArrow(true);
		entityarrow.setBaseDamage(damage);
		entityarrow.setKnockback(knockback);
		world.addFreshEntity(entityarrow);
		world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1, 1f / (random.nextFloat() * 0.5f + 1) + (power / 2));
		return entityarrow;
	}

	/**
	 * @deprecated 优先使用 {@link #shoot(LivingEntity, LivingEntity, double)}，以便调用方自行传入远程伤害倍率。
	 * 该回退重载会使用当前射手的平均倍率。
	 */
	@Deprecated
	public static ShotOceanArrowEntity shoot(LivingEntity entity, LivingEntity target) {
		return shoot(entity, target, (entity.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? entity.getAttributeValue(Attributes.ATTACK_DAMAGE) : 0) * (25.0 / 72.0));
	}

	public static ShotOceanArrowEntity shoot(LivingEntity entity, LivingEntity target, double damage) {
		ShotOceanArrowEntity entityarrow = new ShotOceanArrowEntity(CAEntities.SHOT_OCEAN_ARROW.get(), entity, entity.level());
		double dx = target.getX() - entity.getX();
		double dy = target.getY() + target.getEyeHeight() - 1.1;
		double dz = target.getZ() - entity.getZ();
		entityarrow.shoot(dx, dy - entityarrow.getY() + Math.hypot(dx, dz) * 0.2F, dz, 2.5f * 2, 12.0F);
		entityarrow.setSilent(true);
		entityarrow.setBaseDamage(damage);
		entityarrow.setKnockback(0);
		entityarrow.setCritArrow(true);
		entity.level().addFreshEntity(entityarrow);
		entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1, 1f / (RandomSource.create().nextFloat() * 0.5f + 1));
		return entityarrow;
	}
}
