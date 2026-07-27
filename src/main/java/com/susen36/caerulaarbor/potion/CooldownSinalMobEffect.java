
package com.susen36.caerulaarbor.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class CooldownSinalMobEffect extends MobEffect {
    public CooldownSinalMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -3342337);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}