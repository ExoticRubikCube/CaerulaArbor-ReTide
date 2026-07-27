
package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.init.CAAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class EngravedTriumphMobEffect extends MobEffect {
    public EngravedTriumphMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -16751002);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "engraved_triumph_attack_damage"), 0.01, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        this.addAttributeModifier(Attributes.MAX_HEALTH, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "engraved_triumph_max_health"), 0.01, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        this.addAttributeModifier(Attributes.ARMOR_TOUGHNESS, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "engraved_triumph_armor_toughness"), 0.01, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        this.addAttributeModifier(CAAttributes.MAGIC_RESISTANCE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "engraved_triumph_magic_resistance"), 0.25, AttributeModifier.Operation.ADD_VALUE);
        this.addAttributeModifier(CAAttributes.GENERAL_DEFENSE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "engraved_triumph_general_defense"), 0.01, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}