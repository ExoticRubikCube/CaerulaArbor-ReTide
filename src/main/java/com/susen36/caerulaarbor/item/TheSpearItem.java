
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.SimpleTier;

public class TheSpearItem extends SwordItem {
	private static final Tier TIER = new SimpleTier(
			BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
			2299,
			8f,
			8f,
			9,
            Ingredient::of
	);

	public TheSpearItem() {
		super(TIER, new Item.Properties().fireResistant().attributes(SwordItem.createAttributes(TIER, 3, -2.5f)));
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		if (selected)
			EntityUtils.giveSpearFight(entity);
	}
}