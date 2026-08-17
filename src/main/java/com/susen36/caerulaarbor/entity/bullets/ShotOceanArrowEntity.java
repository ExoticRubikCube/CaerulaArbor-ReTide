package com.susen36.caerulaarbor.entity.bullets;

import com.susen36.babel.effect.LessArmorMobEffect;
import com.susen36.babel.util.EPUtils;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.init.CAParticles;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

import java.util.List;

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
		if (!this.level().isClientSide()) {
			// 下放自 LivingHurtEventHandler.handleOnArrowHit：ComplexChitin 分裂 / TrailriteLink 连锁
			float f = (float) this.getDeltaMovement().length();
			double d0 = this.getBaseDamage();
			DamageSource damagesource = this.damageSources().arrow(this, sourceentity != null ? sourceentity : this);
			if (this.getWeaponItem() != null && this.level() instanceof ServerLevel serverlevel) {
				d0 = EnchantmentHelper.modifyDamage(serverlevel, this.getWeaponItem(), entity, damagesource, (float) d0);
			}
			int j = Mth.ceil(Mth.clamp(f * d0, 0.0, 2.147483647E9));
			if (this.isCritArrow()) {
				long k = this.random.nextInt(j / 2 + 2);
				j = (int) Math.min(k + j, 2147483647L);
			}
			double amount = j;
			double x = entity.getX();
			double y = entity.getY();
			double z = entity.getZ();

			if (this.getPersistentData().getBoolean("ComplexChitin")) {
				if (entity instanceof LivingEntity target) {
					EPUtils.causeSanityInjury(target, amount * 0.2);
				}
				for (int index0 = 0; index0 < 3; index0++) {
					double yaw = Mth.nextInt(RandomSource.create(), -30, 30);
					float sine = Mth.sin((float) Math.toRadians(yaw));
					float cosine = Mth.cos((float) Math.toRadians(yaw));
					double vx = this.getDeltaMovement().x();
					double vz = this.getDeltaMovement().z();
					if (this.level() instanceof ServerLevel projectileLevel) {
						AbstractArrow entityToSpawn = new Arrow(EntityType.ARROW, projectileLevel);
						entityToSpawn.setOwner(sourceentity);
						entityToSpawn.setBaseDamage((float) (amount * 0.64));
						entityToSpawn.setCritArrow(true);
						entityToSpawn.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
						entityToSpawn.setPos(x, this.getY(), z);
						entityToSpawn.shoot((1.5 * (vx * cosine + vz * sine)), (1.5 + this.getDeltaMovement().y()), (1.5 + vz * cosine - vx * sine), (float) 1.5, (float) 0.05);
						projectileLevel.addFreshEntity(entityToSpawn);
					}
				}
			}
			double lll = this.getPersistentData().getDouble("TrailriteLink");
			if (lll > 0) {
				if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
					livingEntity.addEffect(new MobEffectInstance(CAMobEffects.COOLDOWN_SINAL, 20, 0, false, false));
				double y1 = this.getY();
				Entity entity1 = sourceentity;
				if (entity1 != null) {
					final Vec3 center = new Vec3(x, y1, z);
					List<LivingEntity> entfound = this.level().getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(24 / 2d),
							e -> e != entity1 && !e.hasEffect(CAMobEffects.COOLDOWN_SINAL));
					LivingEntity nextTarget = null;
					double minDist = -1.0D;
					Entity recentVictim = (entity1 instanceof LivingEntity livingEntity) ? livingEntity.getLastHurtMob() : null;
					Entity recentAttacker = (entity1 instanceof LivingEntity livingEntity) ? livingEntity.getLastHurtByMob() : null;
					for (LivingEntity entityiterator : entfound) {
						boolean isValid;
						if (entityiterator instanceof Monster) {
							isValid = true;
						} else {
							isValid = (entityiterator instanceof Mob mobEnt ? (Entity) mobEnt.getTarget() : null) == entity1
									|| entityiterator == recentVictim
									|| entityiterator == recentAttacker;
						}
						if (isValid) {
							double d = entityiterator.distanceToSqr(x, y1, z);
							if (minDist == -1.0D || d < minDist) {
								minDist = d;
								nextTarget = entityiterator;
							}
						}
					}
					if (nextTarget != null && this.level() instanceof ServerLevel projectileLevel) {
						AbstractArrow entityToSpawn = new Arrow(EntityType.ARROW, projectileLevel);
						entityToSpawn.setOwner(entity1);
						entityToSpawn.setBaseDamage((float) amount);
						entityToSpawn.setCritArrow(true);
						entityToSpawn.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
						entityToSpawn.setPos(x, y1, z);
						entityToSpawn.getPersistentData().putDouble("TrailriteLink", lll - 1);
						entityToSpawn.shoot((nextTarget.getX() - x), ((nextTarget.getY() + nextTarget.getBbHeight() * 0.9) - y1), (nextTarget.getZ() - z), (float) 1.75, 0);
						projectileLevel.addFreshEntity(entityToSpawn);
					}
				}
				if (lll > 4) {
					if (this.level() instanceof ServerLevel level)
						level.sendParticles(CAParticles.MOIST_BOOM.get(), x, (y + 0.5), z, 2, 0.1, 0.1, 0.1, 0.1);
					if (entity instanceof LivingEntity target) {
						if (entity1 instanceof LivingEntity attacker) {
							EPUtils.causeSanityInjury(target, attacker, amount * 0.25);
						} else {
							EPUtils.causeSanityInjury(target, amount * 0.25);
						}
					}
					if (entity instanceof LivingEntity living)
						LessArmorMobEffect.apply(living);
				}
			}
		}
		CaerulaArbor.queueServerWork(10, () -> {
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