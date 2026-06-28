package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

@OnlyIn(value = Dist.CLIENT, _interface = ItemSupplier.class)
public class ThrowablePotionEntity extends AbstractArrow implements ItemSupplier {
	public static final ItemStack PROJECTILE_ITEM = new ItemStack(Items.SPLASH_POTION);

	public ThrowablePotionEntity(PlayMessages.SpawnEntity packet, Level world) {
		super(CaerulaArborModEntities.THROWABLE_POTION.get(), world);
	}

	public ThrowablePotionEntity(EntityType<? extends ThrowablePotionEntity> type, Level world) {
		super(type, world);
	}

	public ThrowablePotionEntity(EntityType<? extends ThrowablePotionEntity> type, double x, double y, double z, Level world) {
		super(type, x, y, z, world);
	}

	public ThrowablePotionEntity(EntityType<? extends ThrowablePotionEntity> type, LivingEntity entity, Level world) {
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
	public void tick() {
		super.tick();
        Entity owner = this.getOwner();
        if (owner != null) {
            if (owner instanceof OceanziedWitchEntity oceanziedWitch) {
                oceanziedWitch.shootRandomPotion();
            }
            if (!this.level().isClientSide()) {
                this.discard();
            }
        }
        if (this.inGround)
			this.discard();
	}

	public static ThrowablePotionEntity shoot(Level world, LivingEntity entity, RandomSource source) {
		return shoot(world, entity, source, 0.9f, 2.3, 0);
	}

	public static ThrowablePotionEntity shoot(Level world, LivingEntity entity, RandomSource source, float pullingPower) {
		return shoot(world, entity, source, pullingPower * 0.9f, 2.3, 0);
	}

	public static ThrowablePotionEntity shoot(Level world, LivingEntity entity, RandomSource random, float power, double damage, int knockback) {
		ThrowablePotionEntity entityarrow = new ThrowablePotionEntity(CaerulaArborModEntities.THROWABLE_POTION.get(), entity, world);
		entityarrow.shoot(entity.getViewVector(1).x, entity.getViewVector(1).y, entity.getViewVector(1).z, power * 2, 0);
		entityarrow.setSilent(true);
		entityarrow.setCritArrow(false);
		entityarrow.setBaseDamage(damage);
		entityarrow.setKnockback(knockback);
		world.addFreshEntity(entityarrow);
		return entityarrow;
	}

	/**
	 * @deprecated Prefer {@link #shoot(LivingEntity, LivingEntity, double)} so callers can pass their own ranged damage scaling.
	 * This fallback uses the average scaling ratio of current shooters.
	 */
	@Deprecated
	public static ThrowablePotionEntity shoot(LivingEntity entity, LivingEntity target) {
		return shoot(entity, target, (entity.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? entity.getAttributeValue(Attributes.ATTACK_DAMAGE) : 0) * (2.3 / 4.0));
	}

	public static ThrowablePotionEntity shoot(LivingEntity entity, LivingEntity target, double damage) {
		ThrowablePotionEntity entityarrow = new ThrowablePotionEntity(CaerulaArborModEntities.THROWABLE_POTION.get(), entity, entity.level());
		double dx = target.getX() - entity.getX();
		double dy = target.getY() + target.getEyeHeight() - 1.1;
		double dz = target.getZ() - entity.getZ();
		entityarrow.shoot(dx, dy - entityarrow.getY() + Math.hypot(dx, dz) * 0.2F, dz, 0.9f * 2, 12.0F);
		entityarrow.setSilent(true);
		entityarrow.setBaseDamage(damage);
		entityarrow.setKnockback(0);
		entityarrow.setCritArrow(false);
		entity.level().addFreshEntity(entityarrow);
		return entityarrow;
	}
}
