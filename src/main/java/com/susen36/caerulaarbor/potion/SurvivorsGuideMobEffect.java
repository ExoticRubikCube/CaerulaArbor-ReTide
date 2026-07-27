
package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.init.CAAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class SurvivorsGuideMobEffect extends MobEffect {
    public SurvivorsGuideMobEffect() {
        super(MobEffectCategory.NEUTRAL, -3407872);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "survivors_guide_attack_damage"), 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        this.addAttributeModifier(Attributes.ARMOR, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "survivors_guide_armor"), 1, AttributeModifier.Operation.ADD_VALUE);
        this.addAttributeModifier(CAAttributes.GENERAL_DEFENSE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "survivors_guide_general_defense"), 0.25, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

}