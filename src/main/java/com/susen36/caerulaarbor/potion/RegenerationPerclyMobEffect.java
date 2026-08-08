
package com.susen36.caerulaarbor.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class RegenerationPerclyMobEffect extends MobEffect {
	public RegenerationPerclyMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -32383);
	}

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        entity.heal((float) (entity.getMaxHealth() * 0.0025 * ((double) amplifier + 1)));
        return true;
    }

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}
}