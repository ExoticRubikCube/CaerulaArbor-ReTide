
package com.susen36.caerulaarbor.potion;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

import java.util.List;
import java.util.ArrayList;

public class ButchersPowerMobEffect extends MobEffect {
    public ButchersPowerMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -26317);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, "15dbbcf6-9a2d-3af6-b4be-1243b20d3d48", 0.2, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
