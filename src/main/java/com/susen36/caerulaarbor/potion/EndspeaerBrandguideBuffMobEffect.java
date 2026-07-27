
package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.init.CAAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class EndspeaerBrandguideBuffMobEffect extends MobEffect {
    public EndspeaerBrandguideBuffMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -16763956);
        this.addAttributeModifier(Attributes.ARMOR, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "endspeaer_brandguide_buff_armor"), 4, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        this.addAttributeModifier(Attributes.ARMOR_TOUGHNESS, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "endspeaer_brandguide_buff_armor_toughness"), 2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        this.addAttributeModifier(CAAttributes.GENERAL_DEFENSE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "endspeaer_brandguide_buff_general_defense"), 4, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}