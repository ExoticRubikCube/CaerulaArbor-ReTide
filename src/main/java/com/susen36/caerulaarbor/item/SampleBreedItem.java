
package com.susen36.caerulaarbor.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;


public class SampleBreedItem extends Item {
	public SampleBreedItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(ItemStack itemstack) {
		return true;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.sample_breed.description_0"));
		list.add(Component.translatable("item.caerula_arbor.sample_breed.description_1"));
		list.add(Component.translatable("item.caerula_arbor.sample_breed.description_2"));
		list.add(Component.translatable("item.caerula_arbor.sample_breed.description_3"));
		list.add(Component.translatable("item.caerula_arbor.sample_breed.description_4"));
		list.add(Component.translatable("item.caerula_arbor.sample_breed.description_5"));
		list.add(Component.translatable("item.caerula_arbor.sample_breed.description_6"));
		list.add(Component.translatable("item.caerula_arbor.sample_breed.description_7"));
		list.add(Component.translatable("item.caerula_arbor.sample_breed.description_8"));
		list.add(Component.translatable("item.caerula_arbor.sample_breed.description_9"));
	}
}