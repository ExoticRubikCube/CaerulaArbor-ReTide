package com.apocalypse.caerulaarbor.util;

import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class EffectUtils {

	private EffectUtils() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static void addReachEffect(Entity entity, int duration, int amplifier) {
		if (entity == null)
			return;
		if (!(entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(CaerulaArborModMobEffects.ADD_REACH.get()))) {
			if (entity instanceof LivingEntity living && !living.level().isClientSide())
				living.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ADD_REACH.get(), duration, amplifier, false, false));
		}
	}
}
