
package com.susen36.caerulaarbor.item;

import com.susen36.babel.api.BabelAPI;
import com.susen36.babel.elemental.base.AbstractEPCapability;
import com.susen36.babel.init.BabelMobEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;


public class NervousRegenerationItem extends Item {
	public NervousRegenerationItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(2).saturationModifier(0.5f).alwaysEdible().build()));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.nervous_regeneration.description_0"));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = super.finishUsingItem(itemstack, world, entity);
		AbstractEPCapability sanityInjury = BabelAPI.getEP(entity).getEP(AbstractEPCapability.EPType.NERVOUS);
		sanityInjury.heal(sanityInjury.getMaxValue());
		entity.removeEffect(BabelMobEffects.DIZZY);
		entity.removeEffect(MobEffects.BLINDNESS);
		entity.removeEffect(MobEffects.DARKNESS);
		return retval;
	}
}