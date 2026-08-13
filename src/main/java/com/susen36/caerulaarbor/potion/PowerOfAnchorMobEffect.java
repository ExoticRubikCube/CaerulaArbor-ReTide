
package com.susen36.caerulaarbor.potion;

import com.susen36.babel.api.entity.ElementalDefenseModifier;
import com.susen36.babel.elemental.base.AbstractEPCapability;
import com.susen36.babel.util.EPUtils;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.util.MathUtils;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class PowerOfAnchorMobEffect extends MobEffect implements ElementalDefenseModifier {
    public PowerOfAnchorMobEffect() {
        super(MobEffectCategory.NEUTRAL, -6684724);
    }

    @Override
    public AbstractEPCapability.EPType getElementalDefenseType() {
        return AbstractEPCapability.EPType.NERVOUS;
    }

    @Override
    public double modifyElementalDefenseTotal(LivingEntity entity, double modifier, int amplifier) {
        return modifier * (1 - 0.4 * (amplifier + 1));
    }

    @Override
    public double modifyElementalDefenseBase(LivingEntity entity, double modifier, int amplifier) {
        return modifier;
    }


    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Player) {
            EPUtils.healElemental(entity, AbstractEPCapability.EPType.NERVOUS, 10);
            ModCapabilities.getPlayerVariables(entity).player_light = Math.min(ModCapabilities.getPlayerVariables(entity).player_light + 0.125, 100.0);
            ModCapabilities.getPlayerVariables(entity).syncPlayerVariables(entity);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return MathUtils.isMultipleOf(duration, 10);
    }

}