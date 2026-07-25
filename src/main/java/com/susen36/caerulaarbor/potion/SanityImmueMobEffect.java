
package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.init.CAAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SanityImmueMobEffect extends MobEffect {
    public SanityImmueMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -3342337);
        this.addAttributeModifier(CAAttributes.SANITY_RESISTANCE.get(), ResourceLocation.fromNamespaceAndPath("caerulaarbor", "sanity_immue_sanity_resistance"), 200, AttributeModifier.Operation.ADD_VALUE);
    }

    // TODO: 1.21.1 removed MobEffect.getCurativeItems(), curative logic needs migration to ConsumeEffect
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.isAlive()) {
            ModCapabilities.getSanityInjury(entity).heal(5);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}