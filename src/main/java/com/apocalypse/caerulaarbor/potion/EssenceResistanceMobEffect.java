
package com.apocalypse.caerulaarbor.potion;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

import java.util.List;
import java.util.ArrayList;

public class EssenceResistanceMobEffect extends MobEffect {
    public EssenceResistanceMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -3041537);
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
