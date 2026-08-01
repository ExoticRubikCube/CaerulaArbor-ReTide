
package com.susen36.caerulaarbor.item;

import com.susen36.babel.effect.LessArmorMobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class CollectorMeatItem extends Item {
	public CollectorMeatItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(3).saturationModifier(0.2f).build()));
	}

	@Override
	public ItemStack finishUsingItem(@NotNull ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = super.finishUsingItem(itemstack, world, entity);
		LessArmorMobEffect.apply(entity);
        return retval;
	}
}