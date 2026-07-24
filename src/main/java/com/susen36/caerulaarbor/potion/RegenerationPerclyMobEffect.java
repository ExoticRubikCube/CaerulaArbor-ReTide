
package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class RegenerationPerclyMobEffect extends MobEffect {
	public RegenerationPerclyMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -32383);
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
        EntityUtils.heal(entity, entity.getMaxHealth() * 0.0025 * ((double) amplifier + 1));
    }

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
