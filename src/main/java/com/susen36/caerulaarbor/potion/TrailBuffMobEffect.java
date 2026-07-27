
package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TrailBuffMobEffect extends MobEffect {
    public TrailBuffMobEffect() {
        super(MobEffectCategory.NEUTRAL, -10053121);
        this.addAttributeModifier(CAAttributes.SANITY_RATE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "trail_buff_sanity_rate"), 1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
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