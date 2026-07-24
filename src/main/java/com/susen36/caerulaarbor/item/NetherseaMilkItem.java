package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class NetherseaMilkItem extends MilkBucketItem {
	public NetherseaMilkItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
		ItemStack ret = super.finishUsingItem(stack, level, living);
		if (!level.isClientSide()) {
			living.addEffect(new MobEffectInstance(CAMobEffects.DEDUCT_ONE_SANITY.get(), 200, 0, false, false));
		}
		return ret;
	}

	@Override
	public boolean hasCraftingRemainingItem() {
		return true;
	}

	@Override
	public ItemStack getCraftingRemainingItem(ItemStack itemstack) {
		return new ItemStack((ItemLike) Items.BUCKET);
	}
}