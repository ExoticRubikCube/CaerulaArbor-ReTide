
package com.susen36.caerulaarbor.potion;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class AddHealthPerclyMobEffect extends MobEffect {
	public AddHealthPerclyMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -65536);
		this.addAttributeModifier(Attributes.MAX_HEALTH, "22dd66e8-dc75-3998-a13a-f53918dc0bed", 0.2, AttributeModifier.Operation.MULTIPLY_BASE);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
