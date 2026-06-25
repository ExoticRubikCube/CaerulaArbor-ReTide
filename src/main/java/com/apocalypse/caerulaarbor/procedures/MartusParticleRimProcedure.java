package com.apocalypse.caerulaarbor.procedures;

import com.apocalypse.caerulaarbor.init.CaerulaArborModParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;

public class MartusParticleRimProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		double r;
		double t;
		double tx;
		double yz;
		double randint;
		randint = Mth.nextInt(RandomSource.create(), 0, 59);
		for (int index0 = 0; index0 < 60; index0++) {
			t = randint + index0 * 6;
			r = 2.5 + 0.5 * Math.sin(Math.toRadians(index0 * 24));
			tx = x + r * Math.sin(Math.toRadians(t));
			yz = z + r * Math.cos(Math.toRadians(t));
			world.addParticle(CaerulaArborModParticleTypes.MARTUS_CHARS.get(), tx, (y + 1), yz, 0, 0.15, 0);
			world.addParticle(CaerulaArborModParticleTypes.MARTUS_CHARS.get(), tx, (y + 0.8), yz, 0, (-0.08), 0);
		}
	}
}

// TODO: 调用次数 = 4，副作用密集（粒子效果），保持原样不重构
