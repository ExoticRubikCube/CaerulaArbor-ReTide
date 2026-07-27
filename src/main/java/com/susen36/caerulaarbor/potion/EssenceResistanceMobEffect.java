
package com.susen36.caerulaarbor.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class EssenceResistanceMobEffect extends MobEffect {
    public EssenceResistanceMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -3041537);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}