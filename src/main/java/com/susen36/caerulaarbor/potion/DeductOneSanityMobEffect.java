
package com.susen36.caerulaarbor.potion;import com.susen36.babel.util.EPUtils;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class DeductOneSanityMobEffect extends MobEffect {
	public DeductOneSanityMobEffect() {
		super(MobEffectCategory.HARMFUL, -6684724);
	}

	// TODO: 1.21.1 removed MobEffect.getCurativeItems(), curative logic needs migration to ConsumeEffect
	public List<ItemStack> getCurativeItems() {
		ArrayList<ItemStack> cures = new ArrayList<>();
		cures.add(new ItemStack(Items.MILK_BUCKET));
		cures.add(new ItemStack(Items.TOTEM_OF_UNDYING));
		cures.add(new ItemStack(Items.HONEY_BOTTLE));
		return cures;
	}

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        EPUtils.causeSanityInjury(entity, (double) amplifier + 1);
	    return true;
    }

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}

}