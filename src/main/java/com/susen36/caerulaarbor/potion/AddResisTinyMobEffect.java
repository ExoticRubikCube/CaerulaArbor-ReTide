
package com.susen36.caerulaarbor.potion;

import com.susen36.babel.init.BabelAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class AddResisTinyMobEffect extends MobEffect {
	public AddResisTinyMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -1);
		this.addAttributeModifier(BabelAttributes.MAGIC_RESISTANCE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "add_resis_tiny_magic_resistance"), 3, AttributeModifier.Operation.ADD_VALUE);
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}

}