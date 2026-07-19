package com.apocalypse.caerulaarbor.item;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class ChitinCookieRawItem extends Item {
	public ChitinCookieRawItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3f).alwaysEat().build()));
	}
}