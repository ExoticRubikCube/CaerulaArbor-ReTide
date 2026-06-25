
package com.apocalypse.caerulaarbor.potion;

import com.apocalypse.caerulaarbor.utils.EntityUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

import com.apocalypse.caerulaarbor.init.CaerulaArborModAttributes;

import java.util.List;
import java.util.ArrayList;

public class SanityImmueMobEffect extends MobEffect {
    public SanityImmueMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -3342337);
        this.addAttributeModifier(CaerulaArborModAttributes.SANITY_RESISTANCE.get(), "25ed2265-a53a-32f6-8c94-a83714a7ad23", 200, AttributeModifier.Operation.ADDITION);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity == null)
            return;
        if (((Entity) entity).isAlive()) {
            EntityUtils.restoreSanity(entity, 5);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
