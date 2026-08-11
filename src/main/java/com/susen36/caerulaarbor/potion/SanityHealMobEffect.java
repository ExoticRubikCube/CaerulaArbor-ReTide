
package com.susen36.caerulaarbor.potion;

import com.susen36.babel.elemental.base.AbstractEPCapability;
import com.susen36.babel.manager.EPManager;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class SanityHealMobEffect extends MobEffect {
    public SanityHealMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -13057);
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }

    @Override
    public void applyInstantenousEffect(Entity source, Entity indirectSource, LivingEntity entity, int amplifier, double health) {
        EPManager.healElemental(entity, AbstractEPCapability.EPType.NERVOUS, Mth.floor(100.0 * ((double) amplifier + 1.0)));
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

}