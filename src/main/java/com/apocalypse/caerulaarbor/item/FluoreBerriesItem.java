
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.utils.EntityUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class FluoreBerriesItem extends Item {
	public FluoreBerriesItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(4).saturationMod(0.5f).alwaysEat().build()));
	}

	@Override
	public int getUseDuration(ItemStack itemstack) {
		return 40;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = super.finishUsingItem(itemstack, world, entity);
		EntityUtils.restorePlayerLights(entity, 8);
		if (!entity.level().isClientSide())
			entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0));
		entity.removeEffect(MobEffects.BLINDNESS);
		return retval;
	}
}
