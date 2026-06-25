
package com.apocalypse.caerulaarbor.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
//TODO:届时会批量标记这种可以直接使用ide内联的物品类
public class TrailriteNuggetItem extends Item {
	public TrailriteNuggetItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
	}
}
