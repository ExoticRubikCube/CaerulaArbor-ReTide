
package com.susen36.caerulaarbor.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class GuidedEvoMobEffect extends MobEffect {
    public GuidedEvoMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -5532226);
    }

    

    @Override
    public void addAttributeModifiers(AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(attributeMap, amplifier);
    }

    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        super.onEffectAdded(entity, amplifier);
        if ((Entity) entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
            livingEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING, -1, 0, false, false));
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

}