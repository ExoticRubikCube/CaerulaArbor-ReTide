
package com.susen36.caerulaarbor.potion;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class AddHealthPerclyMobEffect extends MobEffect {
	public AddHealthPerclyMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -65536);
		this.addAttributeModifier(Attributes.MAX_HEALTH, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "add_health_percly_max_health"), 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}
}