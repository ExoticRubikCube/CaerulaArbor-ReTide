package com.susen36.caerulaarbor.event;

import com.susen36.babel.collectible.Collectibles;
import com.susen36.babel.util.EPUtils;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.init.CACollectible;
import com.susen36.caerulaarbor.init.CADamageTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;

@EventBusSubscriber
public class PEVOHealEventHandle {
	@SubscribeEvent
	public static void onEntityHealed(LivingHealEvent event) {
		Entity entity = event.getEntity();
		if (event.isCanceled()) {
			return;
		}

        boolean result;
        result = (ModCapabilities.getPlayerVariables(entity)).can_player_evo
                && (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.DISO) || (ModCapabilities.getPlayerVariables(entity)).player_oceanization >= 3);
        if (!entity.isShiftKeyDown() || !(entity instanceof Player livingEntity1) || !result) {
			return;
		}

		Level world = entity.level();
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
        double sanityDamage = (double) livingEntity1.getHealth() + event.getAmount() - (double) livingEntity1.getMaxHealth();
		if (sanityDamage <= 0) {
			return;
		}

		for (int particleIndex = 0; particleIndex < 120; particleIndex++) {
			double angle = Mth.nextDouble(RandomSource.create(), 0, 6.283);
			double radius = Mth.nextDouble(RandomSource.create(), 2.75, 3.25);
			if (world instanceof ServerLevel serverLevel) {
				serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, x + radius * Math.sin(angle), y + 0.4, z + radius * Math.cos(angle), 1, 0.1, 0.1, 0.1, 0.1);
			}
		}

        DamageSource wipeMagicDamage = CADamageTypes.source(world, CADamageTypes.WIPE_MAGIC, entity);
		// 生命溢出时对以自身为圆心半径 3 格内的敌对目标造成同等损伤并触发理智伤口
		for (LivingEntity target : world.getEntitiesOfClass(LivingEntity.class, new AABB(x - 3, y - 1, z - 3, x + 3, y + 3, z + 3), candidate -> candidate != entity && entity.distanceToSqr(candidate) <= 9.0)) {
			if (!(target instanceof Monster) && (!(target instanceof Mob mob) || mob.getTarget() != entity)) {
				continue;
			}
			target.hurt(wipeMagicDamage, (float) sanityDamage);
			EPUtils.causeSanityInjury(target, livingEntity1, sanityDamage / 20.0);
		}
	}
}