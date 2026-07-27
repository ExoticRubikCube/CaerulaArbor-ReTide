
package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.init.CAAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class FlexibilityBuffMobEffect extends MobEffect {
    public FlexibilityBuffMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -6697729);
        this.addAttributeModifier(CAAttributes.MISSRATE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "flexibility_buff_missrate"), 3, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}