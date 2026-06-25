
package com.apocalypse.caerulaarbor.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class OperationKitGladiiaItem extends Item {
	public OperationKitGladiiaItem() {
		super(new Item.Properties().stacksTo(8).rarity(Rarity.UNCOMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.operation_kit_gladiia.description_0"));
		list.add(Component.translatable("item.caerula_arbor.operation_kit_gladiia.description_1"));
	}
}
