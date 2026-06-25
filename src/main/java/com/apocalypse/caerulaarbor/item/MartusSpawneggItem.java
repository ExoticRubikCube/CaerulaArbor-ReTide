
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.ForgeSpawnEggItem;

public class MartusSpawneggItem extends ForgeSpawnEggItem {
	public MartusSpawneggItem() {
		super(CaerulaArborModEntities.MARTUS, -1, -1, new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
	}
}
