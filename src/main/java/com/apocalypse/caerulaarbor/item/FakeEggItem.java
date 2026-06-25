
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.procedures.EatTrialMorProcedure;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class FakeEggItem extends Item {
	public FakeEggItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(2).saturationMod(0.25f).build()));
	}

	@Override
	public int getUseDuration(ItemStack itemstack) {
		return 35;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = super.finishUsingItem(itemstack, world, entity);
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		EatTrialMorProcedure.execute(world, x, y, z, entity);
		return retval;
	}
}
