package com.susen36.caerulaarbor.entity.bullets;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.base.BaseProjectile;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CASounds;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Comparator;
import java.util.List;

@OnlyIn(value = Dist.CLIENT, _interface = ItemSupplier.class)
public class HealBullletEntity extends BaseProjectile implements ItemSupplier {
	public static final ItemStack PROJECTILE_ITEM = new ItemStack(Items.AMETHYST_SHARD);
	public HealBullletEntity(Level world) {
		super(CAEntities.HEAL_BULLLET.get(), world);
	}

	public HealBullletEntity(EntityType<? extends HealBullletEntity> type, Level world) {
		super(type, world);
	}

	public HealBullletEntity(EntityType<? extends HealBullletEntity> type, double x, double y, double z, Level world) {
		super(type, world);
		moveTo(x, y, z);
	}

	public HealBullletEntity(EntityType<? extends HealBullletEntity> type, LivingEntity entity, Level world) {
		super(type, entity, world);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemStack getItem() {
		return PROJECTILE_ITEM;
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
            if (!level().isClientSide())
                discard();
            CaerulaArbor.queueServerWork(16, () -> {
                if (entity.isAlive()) {
                    double atk;
                    double count = 0;
                    atk = entity instanceof LivingEntity livingEntity0 && livingEntity0.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity0.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
                    {
                        final Vec3 center = new Vec3(x, y, z);
                        List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(12 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                        for (Entity entityiterator : entfound) {
                            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "homo_sapiens")))) {
                                if ((entityiterator instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) < (entityiterator instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1)) {
                                    if (entityiterator instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                                        livingEntity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 1));
                                    EntityUtils.healWithParticles(world, entityiterator, atk, 0);
                                    if (!(entityiterator == entity)) {
                                        count = count + 1;
                                        if (count >= 3) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (world instanceof Level level) {
                            level.playSound(null, BlockPos.containing(x, y, z), CASounds.MEDIC_NORMAL.get(), SoundSource.HOSTILE, (float) 1.8,
                                    (float) Mth.nextDouble(RandomSource.create(), 0.8, 1.2));
                    }
                }
            });
        }
        if (this.inGround)
			this.discard();
	}

	public static HealBullletEntity shoot(Level world, LivingEntity entity, RandomSource source) {
		return shoot(world, entity, source, 0.1f, 0, 0);
	}

	public static HealBullletEntity shoot(Level world, LivingEntity entity, RandomSource source, float pullingPower) {
		return shoot(world, entity, source, pullingPower * 0.1f, 0, 0);
	}

	public static HealBullletEntity shoot(Level world, LivingEntity entity, RandomSource random, float power, double damage, int knockback) {
		HealBullletEntity entityarrow = new HealBullletEntity(CAEntities.HEAL_BULLLET.get(), entity, world);
		entityarrow.shoot(entity.getViewVector(1).x, entity.getViewVector(1).y, entity.getViewVector(1).z, power * 2, 0);
		entityarrow.setSilent(true);
		entityarrow.setBaseDamage(damage);
		world.addFreshEntity(entityarrow);
		return entityarrow;
	}

       // TODO：当治疗弹 API 重构时，重新审视这个遗留的双参数接口。
	public static HealBullletEntity shoot(LivingEntity entity, LivingEntity target) {
		HealBullletEntity entityarrow = new HealBullletEntity(CAEntities.HEAL_BULLLET.get(), entity, entity.level());
		double dx = target.getX() - entity.getX();
		double dy = target.getY() + target.getEyeHeight() - 1.1;
		double dz = target.getZ() - entity.getZ();
		entityarrow.shoot(dx, dy - entityarrow.getY() + Math.hypot(dx, dz) * 0.2F, dz, 0.1f * 2, 12.0F);
		entityarrow.setSilent(true);
		entityarrow.setBaseDamage(0);
		entity.level().addFreshEntity(entityarrow);
		return entityarrow;
	}
}
