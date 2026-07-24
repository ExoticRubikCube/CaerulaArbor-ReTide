
package com.susen36.caerulaarbor.potion;

import net.minecraftforge.common.ForgeMod;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class FastSwimMobEffect extends MobEffect {
	public FastSwimMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -16750849);
		this.addAttributeModifier(ForgeMod.SWIM_SPEED.get(), "e955f470-b81c-339d-b42d-3f4d3cf8a64c", 0.5, AttributeModifier.Operation.MULTIPLY_BASE);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
