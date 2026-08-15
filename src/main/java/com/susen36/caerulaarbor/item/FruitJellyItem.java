
package com.susen36.caerulaarbor.item;

import com.susen36.babel.elemental.base.AbstractEPCapability;
import com.susen36.babel.manager.EPManager;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;


public class FruitJellyItem extends Item {
	public FruitJellyItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(10).saturationModifier(0.4f).alwaysEdible().build()));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.fruit_jelly.description_0"));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = super.finishUsingItem(itemstack, world, entity);
		EPManager.getEP(entity).getEP(AbstractEPCapability.EPType.NERVOUS).heal(8);
		if (entity instanceof Player) {
			PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
			capability.player_light = Math.min(capability.player_light + 16, 100.0);
			capability.syncPlayerVariables(entity);
		}
		return retval;
	}
}