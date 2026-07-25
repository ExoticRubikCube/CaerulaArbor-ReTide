
package com.susen36.caerulaarbor.potion;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.common.NeoForgeMod;

public class FastSwimMobEffect extends MobEffect {
	public FastSwimMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -16750849);
		this.addAttributeModifier(NeoForgeMod.SWIM_SPEED, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "fast_swim_swim_speed"), 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}
}