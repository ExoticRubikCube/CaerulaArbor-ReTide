package com.apocalypse.caerulaarbor.entity.bullets;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.CompassionPrayerEntity;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAParticles;
import com.apocalypse.caerulaarbor.init.CASounds;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

import java.util.Comparator;
import java.util.List;

@OnlyIn(value = Dist.CLIENT, _interface = ItemSupplier.class)
public class PrayerSplashEntity extends AbstractArrow implements ItemSupplier {
	public static final ItemStack PROJECTILE_ITEM = new ItemStack(Items.GHAST_TEAR);

	public PrayerSplashEntity(Level world) {
		super(CAEntities.PRAYER_SPLASH.get(), world);
	}

	public PrayerSplashEntity(EntityType<? extends PrayerSplashEntity> type, Level world) {
		super(type, world);
	}

	public PrayerSplashEntity(EntityType<? extends PrayerSplashEntity> type, double x, double y, double z, Level world) {
		super(type, x, y, z, world);
	}

	public PrayerSplashEntity(EntityType<? extends PrayerSplashEntity> type, LivingEntity entity, Level world) {
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
		EntityUtils.killSelf(this.level(), entityHitResult.getEntity(), this);
	}

	@Override
	public void tick() {
		super.tick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity entity = this.getOwner();
        if (entity != null) {
            double count = 0;
            double atk;
            world.addParticle(ParticleTypes.DOLPHIN, x, y, z, ((-0.05) * getDeltaMovement().x()), ((-0.05) * getDeltaMovement().y()), ((-0.05) * getDeltaMovement().z()));
            if (tickCount >= 200) {
                if (!level().isClientSide())
                    discard();
            }
            if ((entity instanceof CompassionPrayerEntity datEntI ? datEntI.getEntityData().get(CompassionPrayerEntity.DATA_PHASE) : 0) <= 0) {
                atk = entity instanceof LivingEntity livingEntity7 && livingEntity7.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity7.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
                {
                    final Vec3 center = new Vec3(x, y, z);
                    List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(12 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                    for (Entity entityiterator : entfound) {
                        if (entity == entityiterator) {
                            continue;
                        }
						if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring")))) {
							if (entityiterator instanceof LivingEntity livingEntity && livingEntity.getHealth() < livingEntity.getMaxHealth()) {
								if (entityiterator.isAlive()) {
									EntityUtils.heal(livingEntity, atk);
                                    if (world instanceof ServerLevel level)
                                        level.sendParticles(CAParticles.SEA_SPLASH.get(), (entityiterator.getX()), (entityiterator.getY() + 1), (entityiterator.getZ()), 24, 1, 1, 1, 0.1);
                                    count = count + 1;
                                    if (count >= 3) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                if (count > 0) {
                    if (!level().isClientSide())
                        discard();
                }
            }
        }
        if (this.inGround)
			this.discard();
	}

	public static PrayerSplashEntity shoot(Level world, LivingEntity entity, RandomSource source) {
		return shoot(world, entity, source, 1.5f, 2.5, 0);
	}

	public static PrayerSplashEntity shoot(Level world, LivingEntity entity, RandomSource source, float pullingPower) {
		return shoot(world, entity, source, pullingPower * 1.5f, 2.5, 0);
	}

	public static PrayerSplashEntity shoot(Level world, LivingEntity entity, RandomSource random, float power, double damage, int knockback) {
		PrayerSplashEntity entityarrow = new PrayerSplashEntity(CAEntities.PRAYER_SPLASH.get(), entity, world);
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
	public static PrayerSplashEntity shoot(LivingEntity entity, LivingEntity target) {
		return shoot(entity, target, (entity.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? entity.getAttributeValue(Attributes.ATTACK_DAMAGE) : 0) * (5.0 / 14.0));
	}

	public static PrayerSplashEntity shoot(LivingEntity entity, LivingEntity target, double damage) {
		PrayerSplashEntity entityarrow = new PrayerSplashEntity(CAEntities.PRAYER_SPLASH.get(), entity, entity.level());
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
