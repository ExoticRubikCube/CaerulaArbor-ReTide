package com.apocalypse.caerulaarbor.procedures;

import com.apocalypse.caerulaarbor.utils.EntityUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;

public class SingleHealProcedure {
	public static void execute(LevelAccessor world, Entity obj, double num, double perc) {
		if (obj == null)
			return;
		double curH = 0;
		double maxH;
		if (obj.isAlive()) {
			curH = obj instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1;
			maxH = obj instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1;
			EntityUtils.heal(obj, maxH * perc + num);
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.CHERRY_LEAVES, (obj.getX()), (obj.getY() + 1), (obj.getZ()), 24, 1, 1, 1, 0.1);
		}
	}
}
