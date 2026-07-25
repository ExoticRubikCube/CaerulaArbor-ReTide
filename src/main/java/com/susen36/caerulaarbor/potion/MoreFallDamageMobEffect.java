
package com.susen36.caerulaarbor.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class MoreFallDamageMobEffect extends MobEffect {
	public MoreFallDamageMobEffect() {
		super(MobEffectCategory.HARMFUL, -13421773);
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}
}