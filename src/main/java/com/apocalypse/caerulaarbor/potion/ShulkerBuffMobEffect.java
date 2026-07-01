
package com.apocalypse.caerulaarbor.potion;

import com.apocalypse.caerulaarbor.init.CAAttributes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
//TODO:可以内联他和他的同类，记得处理isDurationEffectTick
public class ShulkerBuffMobEffect extends MobEffect {
	public ShulkerBuffMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -1);
		this.addAttributeModifier(Attributes.ARMOR, "1f6405f6-967e-367c-99d0-bea597cade17", 2, AttributeModifier.Operation.MULTIPLY_BASE);
		this.addAttributeModifier(Attributes.ARMOR_TOUGHNESS, "398662b7-34fe-3b72-9653-be5220077f42", 5, AttributeModifier.Operation.ADDITION);
		this.addAttributeModifier(CAAttributes.GENERAL_DEFENSE.get(), "0f155109-49e9-309d-98f1-ef37e28c5d97", 5, AttributeModifier.Operation.ADDITION);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
