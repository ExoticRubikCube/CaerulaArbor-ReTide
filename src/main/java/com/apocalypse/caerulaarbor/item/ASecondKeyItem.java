
package com.apocalypse.caerulaarbor.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class ASecondKeyItem extends Item {
	public ASecondKeyItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.a_second_key.description_0"));
		list.add(Component.translatable("item.caerula_arbor.a_second_key.description_1"));
		list.add(Component.translatable("item.caerula_arbor.a_second_key.description_2"));
		list.add(Component.translatable("item.caerula_arbor.a_second_key.description_3"));
	}
}
