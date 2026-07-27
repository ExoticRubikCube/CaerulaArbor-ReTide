
package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.init.CAAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class SanidyDefenderMobEffect extends MobEffect {
    public SanidyDefenderMobEffect() {
        super(MobEffectCategory.NEUTRAL, -6697729);
        this.addAttributeModifier(CAAttributes.SANITY_MODIFIER, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "sanidy_defender_sanity_modifier"), -0.06, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}