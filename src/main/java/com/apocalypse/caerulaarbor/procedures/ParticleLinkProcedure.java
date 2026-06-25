package com.apocalypse.caerulaarbor.procedures;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public class ParticleLinkProcedure {
	public static void execute(LevelAccessor world, Entity from, Entity to) {
		if (from == null || to == null)
			return;
		double vx = 0;
		double vy = 0;
		double vz = 0;
		double size = 0;
		if (from.isAlive()) {
			vx = to.getX() - from.getX();
			vy = (to.getY() + to.getBbHeight() * 0.5) - (from.getY() + from.getBbHeight() * 0.5);
			vz = to.getZ() - from.getZ();
			size = Math.max(Math.min(Math.round(Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2) + Math.pow(vz, 2))), 32), 1);
			size = size * 3;
			for (int index0 = 0; index0 < (int) size; index0++) {
				if (Math.random() > 0.5) {
					if (world instanceof ServerLevel _level)
						_level.sendParticles(ParticleTypes.END_ROD, (from.getX() + (vx / size) * index0), (from.getY() + (vy / size) * index0 + from.getBbHeight() * 0.5), (from.getZ() + (vz / size) * index0), 1, 0.08, 0.08, 0.08, 0);
				} else {
					if (world instanceof ServerLevel _level)
						_level.sendParticles(ParticleTypes.FIREWORK, (from.getX() + (vx / size) * index0), (from.getY() + (vy / size) * index0 + from.getBbHeight() * 0.5), (from.getZ() + (vz / size) * index0), 1, 0.08, 0.08, 0.08, 0);
				}
			}
		}
	}
}

// TODO: 调用次数 = 8，副作用密集（粒子效果），保持原样不重构
