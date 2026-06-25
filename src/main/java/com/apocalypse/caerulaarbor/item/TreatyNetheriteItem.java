
package com.apocalypse.caerulaarbor.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;

import java.util.List;

public class TreatyNetheriteItem extends Item {
	public TreatyNetheriteItem() {
		super(new Item.Properties().stacksTo(64).fireResistant().rarity(Rarity.RARE));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.treaty_netherite.description_0"));
		list.add(Component.translatable("item.caerula_arbor.treaty_netherite.description_1"));
		list.add(Component.translatable("item.caerula_arbor.treaty_netherite.description_2"));
	}
}
