package com.susen36.caerulaarbor.entity.bullets;

import com.susen36.caerulaarbor.entity.base.BaseProjectile;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(value = Dist.CLIENT, _interface = ItemSupplier.class)
public class FakerggShootEntity extends BaseProjectile implements ItemSupplier {
	public static final ItemStack PROJECTILE_ITEM = new ItemStack(CAItems.FAKE_EGG.get());
	public FakerggShootEntity(Level world) {
		super(CAEntities.FAKERGG_SHOOT.get(), world);
	}

	public FakerggShootEntity(EntityType<? extends FakerggShootEntity> type, Level world) {
		super(type, world);
	}

	public FakerggShootEntity(EntityType<? extends FakerggShootEntity> type, double x, double y, double z, Level world) {
		super(type, x, y, z, world);
	}

	public FakerggShootEntity(EntityType<? extends FakerggShootEntity> type, LivingEntity entity, Level world) {
		super(type, entity, world);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemStack getItem() {
		return PROJECTILE_ITEM;
	}

	@Override
	public void onHitEntity(EntityHitResult entityHitResult) {
		super.onHitEntity(entityHitResult);
        LevelAccessor world = this.level();
        Entity entity = entityHitResult.getEntity();
        Entity sourceentity = this.getOwner();
        if (sourceentity == null)
            return;
        if (!(entity == sourceentity)) {
            if (world instanceof ServerLevel level) {
                Entity entityToSpawn = CAEntities.FAKE_OFFSPRING.get().spawn(level, BlockPos.containing(this.getX() + Mth.nextDouble(RandomSource.create(), -0.5, 0.5), this.getY(), this.getZ() + Mth.nextDouble(RandomSource.create(), -0.5, 0.5)),
                        MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                }
            }
            if (!level().isClientSide())
                discard();
        }
    }

	@Override
	public void onHitBlock(BlockHitResult blockHitResult) {
		super.onHitBlock(blockHitResult);
        LevelAccessor world = this.level();
        double x = blockHitResult.getBlockPos().getX();
        double y = blockHitResult.getBlockPos().getY();
        double z = blockHitResult.getBlockPos().getZ();
        if (world instanceof ServerLevel level) {
            Entity entityToSpawn = CAEntities.FAKE_OFFSPRING.get().spawn(level, BlockPos.containing(x + Mth.nextDouble(RandomSource.create(), 0, 1), y + 1, z + Mth.nextDouble(RandomSource.create(), 0, 1)), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn != null) {
                entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
            }
        }
        if (!level().isClientSide())
            discard();
    }

	@Override
	public void tick() {
		super.tick();
		if (this.inGround)
			this.discard();
	}

	public static FakerggShootEntity shoot(Level world, LivingEntity entity, RandomSource source) {
		return shoot(world, entity, source, 0.8f, 1.5, 0);
	}

	public static FakerggShootEntity shoot(Level world, LivingEntity entity, RandomSource source, float pullingPower) {
		return shoot(world, entity, source, pullingPower * 0.8f, 1.5, 0);
	}

	public static FakerggShootEntity shoot(Level world, LivingEntity entity, RandomSource random, float power, double damage, int knockback) {
		FakerggShootEntity entityarrow = new FakerggShootEntity(CAEntities.FAKERGG_SHOOT.get(), entity, world);
		entityarrow.shoot(entity.getViewVector(1).x, entity.getViewVector(1).y, entity.getViewVector(1).z, power * 2, 0);
		entityarrow.setSilent(true);
		entityarrow.setCritArrow(false);
		entityarrow.setBaseDamage(damage);
		entityarrow.setKnockback(knockback);
		world.addFreshEntity(entityarrow);
		world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PUFFER_FISH_BLOW_OUT, SoundSource.PLAYERS, 1, 1f / (random.nextFloat() * 0.5f + 1) + (power / 2));
		return entityarrow;
	}

	/**
	 * @deprecated 优先使用 {@link #shoot(LivingEntity, LivingEntity, double)}，以便调用方自行传入远程伤害倍率。
	 * 该回退重载会使用当前射手的平均倍率。
	 */
	@Deprecated
	public static FakerggShootEntity shoot(LivingEntity entity, LivingEntity target) {
		return shoot(entity, target, (entity.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? entity.getAttributeValue(Attributes.ATTACK_DAMAGE) : 0) * 0.5);
	}

	public static FakerggShootEntity shoot(LivingEntity entity, LivingEntity target, double damage) {
		FakerggShootEntity entityarrow = new FakerggShootEntity(CAEntities.FAKERGG_SHOOT.get(), entity, entity.level());
		double dx = target.getX() - entity.getX();
		double dy = target.getY() + target.getEyeHeight() - 1.1;
		double dz = target.getZ() - entity.getZ();
		entityarrow.shoot(dx, dy - entityarrow.getY() + Math.hypot(dx, dz) * 0.2F, dz, 0.8f * 2, 12.0F);
		entityarrow.setSilent(true);
		entityarrow.setBaseDamage(damage);
		entity.level().addFreshEntity(entityarrow);
		entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PUFFER_FISH_BLOW_OUT, SoundSource.PLAYERS, 1,
				1f / (RandomSource.create().nextFloat() * 0.5f + 1));
		return entityarrow;
	}
}