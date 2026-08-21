
package com.susen36.caerulaarbor.potion;

import com.susen36.babel.util.EPUtils;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class DeductOneSanityMobEffect extends MobEffect {
	public DeductOneSanityMobEffect() {
		super(MobEffectCategory.HARMFUL, -6684724);
	}

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        EPUtils.causeSanityInjury(entity, ((double) amplifier + 1) / 20.0);
	    return true;
    }

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}

}