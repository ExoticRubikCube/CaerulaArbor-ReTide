
package com.susen36.caerulaarbor.potion;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class FakeDeathMobEffect extends MobEffect {
    public FakeDeathMobEffect() {
        super(MobEffectCategory.NEUTRAL, -13596966);
        this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "fake_death_knockback_resistance"), 10, AttributeModifier.Operation.ADD_VALUE);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "fake_death_movement_speed"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "fake_death_attack_damage"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "fake_death_entity_reach"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @Override
    public void addAttributeModifiers(AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(attributeMap, amplifier);
    }

    @Override
    public void onEffectAdded(LivingEntity livingEntity, int amplifier) {
        super.onEffectAdded(livingEntity, amplifier);
            livingEntity.setHealth(1);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        MobEffectInstance effect = livingEntity.getActiveEffects().stream()
                .filter(inst -> inst.getEffect().value() == this)
                .findFirst()
                .orElse(null);
        if (effect == null) {
            return true;
        }
        int duration = effect.getDuration();
        float maxHealth = livingEntity.getMaxHealth();
        float currentHealth = livingEntity.getHealth();
        float deficit = maxHealth - currentHealth;
        if (deficit <= 0.0F) {
            return true;
        }
        float healAmount;
        if (duration <= 1) {
            healAmount = deficit;
        } else {
            healAmount = deficit / duration;
        }
        livingEntity.setHealth(currentHealth + healAmount);
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

}