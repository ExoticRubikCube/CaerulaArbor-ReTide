package com.apocalypse.caerulaarbor.procedures;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CaerulaArborModParticleTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

public class IsharmlaDroppedAttackProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity obj, double R, double rate) {
		if (obj == null || !(world instanceof ServerLevel))
			return;
		ServerLevel level = (ServerLevel) world;
		for (int i = 0; i < 20; i++) {
			final int tick = i;
			final double fi = i;
			CaerulaArborMod.queueServerWork(tick, () -> {
				level.sendParticles(CaerulaArborModParticleTypes.MOIST_BOOM.get(), x, y + 10 - fi * 0.5, z, 1, 0, 0, 0, 0);
				double angle = Math.toRadians(fi * 9);
				level.sendParticles(ParticleTypes.END_ROD, x + R * Math.cos(angle), y, z + R * Math.sin(angle), 1, 0, 0, 0, 0);
				double angle180 = Math.toRadians(fi * 9 + 180);
				level.sendParticles(ParticleTypes.END_ROD, x + R * Math.cos(angle180), y + 0.125, z + R * Math.sin(angle180), 1, 0, 0, 0, 0);
			});
		}
		final ServerLevel finalLevel = level;
		final Entity finalObj = obj;
		CaerulaArborMod.queueServerWork(20, () -> {
			RangedIsharmlaAttackProcedure.execute(finalLevel, x, y, z, finalObj, R, rate);
		});
	}
}