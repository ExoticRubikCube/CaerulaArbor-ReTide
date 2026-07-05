package com.apocalypse.caerulaarbor.entity.bullets;

import com.apocalypse.caerulaarbor.entity.OceanIllusionEntity;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CASounds;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

@OnlyIn(value = Dist.CLIENT, _interface = ItemSupplier.class)
public class FishSplashEntity extends AbstractArrow implements ItemSupplier {
	public static final ItemStack PROJECTILE_ITEM = new ItemStack(Items.GHAST_TEAR);

	public FishSplashEntity(Level world) {
		super(CAEntities.FISH_SPLASH.get(), world);
	}

	public FishSplashEntity(EntityType<? extends FishSplashEntity> type, Level world) {
		super(type, world);
	}

	public FishSplashEntity(EntityType<? extends FishSplashEntity> type, double x, double y, double z, Level world) {
		super(type, x, y, z, world);
	}

	public FishSplashEntity(EntityType<? extends FishSplashEntity> type, LivingEntity entity, Level world) {
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
		if (this.getOwner() instanceof OceanIllusionEntity) {
			entityHitResult.getEntity().invulnerableTime = 0;
		}
		EntityUtils.killSelf(this.level(), entityHitResult.getEntity(), this);
	}

	@Override
	public void tick() {
		super.tick();
        LevelAccessor world = this.level();
        world.addParticle(ParticleTypes.DOLPHIN, this.getX(), this.getY(), this.getZ(), ((-0.05) * getDeltaMovement().x()), ((-0.05) * getDeltaMovement().y()), ((-0.05) * getDeltaMovement().z()));
        if (tickCount >= 200) {
            if (!level().isClientSide())
                discard();
        }
        if (this.inGround)
			this.discard();
	}

	public static FishSplashEntity shoot(Level world, LivingEntity entity, RandomSource source) {
		return shoot(world, entity, source, 1.5f, 2.5, 0);
	}

	public static FishSplashEntity shoot(Level world, LivingEntity entity, RandomSource source, float pullingPower) {
		return shoot(world, entity, source, pullingPower * 1.5f, 2.5, 0);
	}

	public static FishSplashEntity shoot(Level world, LivingEntity entity, RandomSource random, float power, double damage, int knockback) {
		FishSplashEntity entityarrow = new FishSplashEntity(CAEntities.FISH_SPLASH.get(), entity, world);
		entityarrow.shoot(entity.getViewVector(1).x, entity.getViewVector(1).y, entity.getViewVector(1).z, power * 2, 0);
		entityarrow.setSilent(true);
		entityarrow.setCritArrow(false);
		entityarrow.setBaseDamage(damage);
		entityarrow.setKnockback(knockback);
		world.addFreshEntity(entityarrow);
		world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), CASounds.SPLASHER_ATTACK.get(), SoundSource.PLAYERS, 1, 1f / (random.nextFloat() * 0.5f + 1) + (power / 2));
		return entityarrow;
	}

	/**
	 * @deprecated 优先使用 {@link #shoot(LivingEntity, LivingEntity, double)}，以便调用方自行传入远程伤害倍率。
	 * 该回退重载会使用当前射手的平均倍率。
	 */
	@Deprecated
	public static FishSplashEntity shoot(LivingEntity entity, LivingEntity target) {
		return shoot(entity, target, (entity.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? entity.getAttributeValue(Attributes.ATTACK_DAMAGE) : 0) * (5.0 / 9.0));
	}

	public static FishSplashEntity shoot(LivingEntity entity, LivingEntity target, double damage) {
		FishSplashEntity entityarrow = new FishSplashEntity(CAEntities.FISH_SPLASH.get(), entity, entity.level());
		double dx = target.getX() - entity.getX();
		double dy = target.getY() + target.getEyeHeight() - 1.1;
		double dz = target.getZ() - entity.getZ();
		entityarrow.shoot(dx, dy - entityarrow.getY() + Math.hypot(dx, dz) * 0.2F, dz, 1.5f * 2, 12.0F);
		entityarrow.setSilent(true);
		entityarrow.setBaseDamage(damage);
		entityarrow.setKnockback(0);
		entityarrow.setCritArrow(false);
		entity.level().addFreshEntity(entityarrow);
		entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), CASounds.SPLASHER_ATTACK.get(), SoundSource.PLAYERS, 1,
				1f / (RandomSource.create().nextFloat() * 0.5f + 1));
		return entityarrow;
	}
}
