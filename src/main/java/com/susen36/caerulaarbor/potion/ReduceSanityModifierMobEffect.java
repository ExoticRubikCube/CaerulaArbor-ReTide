package com.susen36.caerulaarbor.potion;

import com.susen36.babel.api.entity.ElementalDefenseModifier;
import com.susen36.babel.elemental.base.AbstractEPCapability;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class ReduceSanityModifierMobEffect extends MobEffect implements ElementalDefenseModifier {
    public ReduceSanityModifierMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -1);
    }

    @Override
    public AbstractEPCapability.EPType getElementalDefenseType() {
        return AbstractEPCapability.EPType.NERVOUS;
    }

    @Override
    public double modifyElementalDefenseTotal(LivingEntity entity, double modifier, int amplifier) {
        return modifier * (1 - 0.05 * (amplifier + 1));
    }

    @Override
    public double modifyElementalDefenseBase(LivingEntity entity, double modifier, int amplifier) {
        return modifier;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
