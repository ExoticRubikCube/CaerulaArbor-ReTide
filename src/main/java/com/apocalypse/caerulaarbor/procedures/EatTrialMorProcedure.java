package com.apocalypse.caerulaarbor.procedures;

import com.apocalypse.caerulaarbor.api.event.SanityEvent;
import com.apocalypse.caerulaarbor.capability.sanity.SIHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;

public class EatTrialMorProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity livingEntity) {
			SIHelper.causeSanityInjury(livingEntity, 160, SanityEvent.Hurt.Type.FOOD);
		}
		if (world instanceof ServerLevel serverLevel)
			serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, (y + 0.8), z, 48, 0.5, 1, 0.5, 0.1);
	}
}

// TODO: 是公共方法
