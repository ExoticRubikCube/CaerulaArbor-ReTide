package com.apocalypse.caerulaarbor.event;

import com.apocalypse.caerulaarbor.api.event.SanityEvent;
import com.apocalypse.caerulaarbor.capability.sanity.SIHelper;
import com.apocalypse.caerulaarbor.init.CADamageTypes;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import com.apocalypse.caerulaarbor.util.NodeUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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

		if (!entity.isShiftKeyDown() || !(entity instanceof Player livingEntity1) || !EntityUtils.canPlayerEvo(entity)) {
			return;
		}

		LevelAccessor world = entity.level();
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
        double overflowHealing = (double) livingEntity1.getHealth() + event.getAmount() - (double) livingEntity1.getMaxHealth();
		if (overflowHealing <= 0) {
			return;
		}

		double healDamageNodeLevel = NodeUtils.getNodeHealDamage(entity);
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
		var wipeMagicDamage = CADamageTypes.source(world, CADamageTypes.WIPE_MAGIC, entity);
		for (Entity nearbyEntity : world.getEntities(entity, new AABB(x - 3, y - 1, z - 3, x + 3, y + 3, z + 3))) {
			if (entity.distanceTo(nearbyEntity) > 3 || !(nearbyEntity instanceof LivingEntity)) {
				continue;
			}
			if (!(nearbyEntity instanceof Monster) && (!(nearbyEntity instanceof Mob mob) || mob.getTarget() != entity)) {
				continue;
			}
			nearbyEntity.hurt(wipeMagicDamage, (float) damage);
			if (sanityDamage > 0 && entity instanceof LivingEntity attacker && nearbyEntity instanceof LivingEntity target) {
				SIHelper.causeSanityInjury(target, attacker, sanityDamage, SanityEvent.Hurt.Type.ENTITY);
			}
		}
	}
}
