package com.susen36.caerulaarbor.entity.bullets;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.commands.arguments.EntityAnchorArgument;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(value = Dist.CLIENT, _interface = ItemSupplier.class)
public class ShotOceanArrowEntity extends AbstractArrow implements ItemSupplier {
	public static final ItemStack PROJECTILE_ITEM = new ItemStack(CAItems.OCEAN_ARROW.get());

	public ShotOceanArrowEntity(EntityType<? extends ShotOceanArrowEntity> entityType, Level level) {
		super(entityType, level);
	}

	public ShotOceanArrowEntity(EntityType<? extends ShotOceanArrowEntity> type, LivingEntity entity, Level world) {
		super(type, world);
		setOwner(entity);
		setPos(entity.getX(), entity.getY() - 0.1, entity.getZ());
	}

	public ShotOceanArrowEntity(Level level, double x, double y, double z, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
		super(CAEntities.SHOT_OCEAN_ARROW.get(), x, y, z, level, pickupItemStack, firedFromWeapon);
	}

	public ShotOceanArrowEntity(Level level, LivingEntity owner, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
		super(CAEntities.SHOT_OCEAN_ARROW.get(), owner, level, pickupItemStack, firedFromWeapon);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemStack getItem() {
		return PROJECTILE_ITEM;
	}

	@Override
	protected ItemStack getDefaultPickupItem() {
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
            Entity target;
            target = entity instanceof Mob mobEnt ? mobEnt.getTarget() : null;
            if (!(target == null)) {
                lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((target.getX()), (target.getY() + target.getBbHeight() * 0.75), (target.getZ())));
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
		entityarrow.setCritArrow(true);
		entity.level().addFreshEntity(entityarrow);
		entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1, 1f / (RandomSource.create().nextFloat() * 0.5f + 1));
		return entityarrow;
	}
}