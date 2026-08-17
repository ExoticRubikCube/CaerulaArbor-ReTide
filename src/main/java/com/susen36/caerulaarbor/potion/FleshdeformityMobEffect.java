
package com.susen36.caerulaarbor.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class FleshdeformityMobEffect extends MobEffect {
    public FleshdeformityMobEffect() {
        super(MobEffectCategory.NEUTRAL, -26215);
        // 所有属性修正（弱化 -25%/-50%，深度补偿攻速/移速/游泳）均由 DisconcentrationEventHandler 以动态修正单独施加。
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}