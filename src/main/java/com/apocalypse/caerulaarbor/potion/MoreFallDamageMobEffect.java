
package com.apocalypse.caerulaarbor.potion;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class MoreFallDamageMobEffect extends MobEffect {
	public MoreFallDamageMobEffect() {
		super(MobEffectCategory.HARMFUL, -13421773);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
