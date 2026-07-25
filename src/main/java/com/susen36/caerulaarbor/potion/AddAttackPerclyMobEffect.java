
package com.susen36.caerulaarbor.potion;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class AddAttackPerclyMobEffect extends MobEffect {
	public AddAttackPerclyMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -3407770);
		this.addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "add_attack_percly_attack_damage"), 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}
}