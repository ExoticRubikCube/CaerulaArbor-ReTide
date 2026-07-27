
package com.susen36.caerulaarbor.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class UntameConfirmMobEffect extends MobEffect {
    public UntameConfirmMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -26215);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}