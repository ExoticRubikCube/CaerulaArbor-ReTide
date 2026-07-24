
package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.init.CAAttributes;
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
        this.addAttributeModifier(CAAttributes.SANITY_RESISTANCE.get(), "25ed2265-a53a-32f6-8c94-a83714a7ad23", 200, AttributeModifier.Operation.ADDITION);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.isAlive()) {
            ModCapabilities.getSanityInjury(entity).heal(5);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
