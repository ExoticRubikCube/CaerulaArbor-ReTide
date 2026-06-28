
package com.apocalypse.caerulaarbor.potion;

import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class RegenerationPerclyMobEffect extends MobEffect {
	public RegenerationPerclyMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -32383);
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity == null)
            return;
        EntityUtils.heal(entity, ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.0025 * ((double) amplifier + 1));
    }

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
