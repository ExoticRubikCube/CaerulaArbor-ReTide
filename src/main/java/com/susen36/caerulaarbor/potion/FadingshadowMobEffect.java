
package com.susen36.caerulaarbor.potion;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class FadingshadowMobEffect extends MobEffect {
    public FadingshadowMobEffect() {
        super(MobEffectCategory.NEUTRAL, -13382401);
        this.addAttributeModifier(Attributes.ATTACK_SPEED, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "fadingshadow_attack_speed"), -0.25, AttributeModifier.Operation.ADD_VALUE);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "fadingshadow_movement_speed"), -0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        // TODO: NeoForge 1.21.1 removed NeoForgeMod.ENTITY_GRAVITY, reimplement when replacement is known
        // this.addAttributeModifier(NeoForgeMod.ENTITY_GRAVITY, "e9e734e4-9030-3c73-9958-539443bf5286", -0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    // TODO: 1.21.1 removed MobEffect.getCurativeItems(), curative logic needs migration to ConsumeEffect
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}