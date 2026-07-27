
package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.util.MathUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PowerOfAnchorMobEffect extends MobEffect {
    public PowerOfAnchorMobEffect() {
        super(MobEffectCategory.NEUTRAL, -6684724);
        this.addAttributeModifier(CAAttributes.SANITY_MODIFIER, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "power_of_anchor_sanity_modifier"), -0.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Player) {
            ModCapabilities.getSanityInjury(entity).heal(10);
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