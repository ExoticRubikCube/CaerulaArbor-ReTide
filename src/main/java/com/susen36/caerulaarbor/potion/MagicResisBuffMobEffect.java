
package com.susen36.caerulaarbor.potion;

import com.susen36.babel.init.BabelAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class MagicResisBuffMobEffect extends MobEffect {
    public MagicResisBuffMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -3394561);
        this.addAttributeModifier(BabelAttributes.MAGIC_RESISTANCE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "magic_resis_buff_magic_resistance"), 4, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}