
package com.susen36.caerulaarbor.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class UnderBreakMobEffect extends MobEffect {
    public UnderBreakMobEffect() {
        super(MobEffectCategory.NEUTRAL, -1);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

}