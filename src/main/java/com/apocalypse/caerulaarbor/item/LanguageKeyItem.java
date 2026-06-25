
package com.apocalypse.caerulaarbor.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class LanguageKeyItem extends Item {
	public LanguageKeyItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.language_key.description_0"));
		list.add(Component.translatable("item.caerula_arbor.language_key.description_1"));
		list.add(Component.translatable("item.caerula_arbor.language_key.description_2"));
		list.add(Component.translatable("item.caerula_arbor.language_key.description_3"));
		list.add(Component.translatable("item.caerula_arbor.language_key.description_4"));
		list.add(Component.translatable("item.caerula_arbor.language_key.description_5"));
		list.add(Component.translatable("item.caerula_arbor.language_key.description_6"));
		list.add(Component.translatable("item.caerula_arbor.language_key.description_7"));
		list.add(Component.translatable("item.caerula_arbor.language_key.description_8"));
		list.add(Component.translatable("item.caerula_arbor.language_key.description_9"));
		list.add(Component.translatable("item.caerula_arbor.language_key.description_10"));
		list.add(Component.translatable("item.caerula_arbor.language_key.description_11"));
		list.add(Component.translatable("item.caerula_arbor.language_key.description_12"));
		list.add(Component.translatable("item.caerula_arbor.language_key.description_13"));
		list.add(Component.translatable("item.caerula_arbor.language_key.description_14"));
		list.add(Component.translatable("item.caerula_arbor.language_key.description_15"));
		list.add(Component.translatable("item.caerula_arbor.language_key.description_16"));
	}
}
