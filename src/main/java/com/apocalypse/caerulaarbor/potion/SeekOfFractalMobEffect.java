
package com.apocalypse.caerulaarbor.potion;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

import java.util.List;
import java.util.ArrayList;

public class SeekOfFractalMobEffect extends MobEffect {
    public SeekOfFractalMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -16777165);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, "8ea2c1b5-880f-3689-b67f-f4a85af1a386", 0.5, AttributeModifier.Operation.MULTIPLY_TOTAL);
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
