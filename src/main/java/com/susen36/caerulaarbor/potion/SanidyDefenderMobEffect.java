package com.susen36.caerulaarbor.potion;

import com.susen36.babel.api.entity.ElementalDefenseModifier;
import com.susen36.babel.elemental.base.AbstractEPCapability;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class SanidyDefenderMobEffect extends MobEffect implements ElementalDefenseModifier {
    public SanidyDefenderMobEffect() {
        super(MobEffectCategory.NEUTRAL, -6697729);
    }

    @Override
    public AbstractEPCapability.EPType getElementalDefenseType() {
        return AbstractEPCapability.EPType.NERVOUS;
    }

    @Override
    public double modifyElementalDefenseBase(LivingEntity entity, double modifier, int amplifier) {
        return modifier * (1 - 0.06 * (amplifier + 1));
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
