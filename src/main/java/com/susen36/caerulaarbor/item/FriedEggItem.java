
package com.susen36.caerulaarbor.item;

import com.susen36.babel.elemental.base.AbstractEPCapability;
import com.susen36.babel.manager.EPManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class FriedEggItem extends Item {
	public FriedEggItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(6).saturationModifier(0.6f).alwaysEdible().build()));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = super.finishUsingItem(itemstack, world, entity);
		EPManager.getEP(entity).getEP(AbstractEPCapability.EPType.NERVOUS).heal(2);
		if (!entity.level().isClientSide())
			entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 1));
		return retval;
	}
}