package com.apocalypse.caerulaarbor.event;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PEVOHealEventHandle {
	@SubscribeEvent
	public static void onEntityHealed(LivingHealEvent event) {
		Entity entity = event.getEntity();
		if (event.isCanceled() || entity == null) {
			return;
		}

		if (!entity.isShiftKeyDown() || !(entity instanceof Player) || !EntityUtils.canPlayerEvo(entity)) {
			return;
		}

		LevelAccessor world = entity.level();
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		double currentHealth = entity instanceof LivingEntity livingEntity ? livingEntity.getHealth() : -1;
		double maxHealth = entity instanceof LivingEntity livingEntity ? livingEntity.getMaxHealth() : -1;
		double overflowHealing = currentHealth + event.getAmount() - maxHealth;
		if (overflowHealing <= 0) {
			return;
		}

		double healDamageNodeLevel = EntityUtils.getNodeHealDamage(entity);
		double damageRate = 0;
		double sanityDamageRate = 0;
		if (healDamageNodeLevel >= 4) {
			damageRate = 1;
			sanityDamageRate = 20;
		} else if (healDamageNodeLevel >= 3) {
			damageRate = 0.6;
			sanityDamageRate = 10;
		} else if (healDamageNodeLevel >= 2) {
			damageRate = 0.3;
		} else if (healDamageNodeLevel >= 1) {
			damageRate = 0.1;
		}
		if (damageRate <= 0) {
			return;
		}

		for (int particleIndex = 0; particleIndex < 120; particleIndex++) {
			double angle = Mth.nextDouble(RandomSource.create(), 0, 6.283);
			double radius = Mth.nextDouble(RandomSource.create(), 2.75, 3.25);
			if (world instanceof ServerLevel serverLevel) {
				serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, x + radius * Math.sin(angle), y + 0.4, z + radius * Math.cos(angle), 1, 0.1, 0.1, 0.1, 0.1);
			}
		}

		double damage = overflowHealing * damageRate;
		double sanityDamage = overflowHealing * sanityDamageRate;
		DamageSource wipeMagicDamage = new DamageSource(
				world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
						.getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "wipe_magic"))),
				entity);
		for (Entity nearbyEntity : world.getEntities(entity, new AABB(x - 3, y - 1, z - 3, x + 3, y + 3, z + 3))) {
			if (entity.distanceTo(nearbyEntity) > 3 || !(nearbyEntity instanceof LivingEntity)) {
				continue;
			}
			if (!(nearbyEntity instanceof Monster) && (!(nearbyEntity instanceof Mob mob) || mob.getTarget() != entity)) {
				continue;
			}
			nearbyEntity.hurt(wipeMagicDamage, (float) damage);
			if (sanityDamage > 0) {
				EntityUtils.deductSanity(nearbyEntity, sanityDamage);
			}
		}
	}
}
