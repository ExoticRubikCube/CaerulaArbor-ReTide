
package com.susen36.caerulaarbor.potion;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

import com.susen36.caerulaarbor.init.CAAttributes;

import java.util.List;
import java.util.ArrayList;
import net.minecraft.resources.ResourceLocation;

public class MagicResisBuffMobEffect extends MobEffect {
    public MagicResisBuffMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -3394561);
        this.addAttributeModifier(CAAttributes.MAGIC_RESISTANCE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "magic_resis_buff_magic_resistance"), 4, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}