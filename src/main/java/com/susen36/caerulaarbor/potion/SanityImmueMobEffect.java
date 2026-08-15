
package com.susen36.caerulaarbor.potion;

import com.susen36.babel.elemental.base.AbstractEPCapability;
import com.susen36.babel.util.EPUtils;
import com.susen36.caerulaarbor.init.CAAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class SanityImmueMobEffect extends MobEffect {
    public SanityImmueMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -3342337);
        this.addAttributeModifier(CAAttributes.SANITY_RESISTANCE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "sanity_immue_sanity_resistance"), 200, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.isAlive()) {
            EPUtils.healElemental(entity, AbstractEPCapability.EPType.NERVOUS, 1);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}