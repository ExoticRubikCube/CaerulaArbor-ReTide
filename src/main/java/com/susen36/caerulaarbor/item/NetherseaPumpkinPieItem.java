
package com.susen36.caerulaarbor.item;

import com.susen36.babel.util.EPUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class NetherseaPumpkinPieItem extends Item {
	public NetherseaPumpkinPieItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(9).saturationModifier(0.5f).build()));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = super.finishUsingItem(itemstack, world, entity);
		EPUtils.causeSanityInjury(entity, 50);
		return retval;
	}
}