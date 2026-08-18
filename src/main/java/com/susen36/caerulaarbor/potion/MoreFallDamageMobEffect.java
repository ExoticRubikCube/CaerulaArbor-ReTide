
package com.susen36.caerulaarbor.potion;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class MoreFallDamageMobEffect extends MobEffect {
	public MoreFallDamageMobEffect() {
		super(MobEffectCategory.HARMFUL, -13421773);
	}

	@Override
	public void onMobHurt(@NotNull LivingEntity livingEntity, int amplifier, @NotNull DamageSource damageSource, float amount) {
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}
}