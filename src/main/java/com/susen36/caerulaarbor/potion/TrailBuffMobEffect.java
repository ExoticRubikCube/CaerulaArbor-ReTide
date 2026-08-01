
package com.susen36.caerulaarbor.potion;

import com.susen36.babel.api.entity.ElementalAttackModifier;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class TrailBuffMobEffect extends MobEffect implements ElementalAttackModifier {
    public TrailBuffMobEffect() {
        super(MobEffectCategory.NEUTRAL, -10053121);
    }

    @Override
    public double modifyElementalRate(LivingEntity entity, double rate, int amplifier) {
        return rate * 2;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        EntityUtils.applyNetherseaBuff(entity.level(), entity);
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

}