
package com.apocalypse.caerulaarbor.potion;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

import java.util.List;
import java.util.ArrayList;

public class PathToUncoverMobEffect extends MobEffect {
    public PathToUncoverMobEffect() {
        super(MobEffectCategory.NEUTRAL, -16777012);
        this.addAttributeModifier(Attributes.MAX_HEALTH, "b6467a76-9bdf-3f36-bfa7-2b29587f19c0", 0.8, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, "2ba29047-8f0a-3ec9-9527-89dddcc110f4", 1.4, AttributeModifier.Operation.MULTIPLY_BASE);
        this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "d26612d3-df6b-353b-9caf-cf678460315d", 1, AttributeModifier.Operation.ADDITION);
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
