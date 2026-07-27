
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

public class  ChitinPickaxeItem extends PickaxeItem {
	private static final Tier TIER = new SimpleTier(
			BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
			1220,
			6f,
			2f,
			11,
			() -> Ingredient.of(new ItemStack(CAItems.OCEAN_CHITIN.get()))
	);

	public ChitinPickaxeItem() {
		super(TIER, new Item.Properties().attributes(PickaxeItem.createAttributes(TIER, 1, -2.8f)));
	}

	@Override
	public boolean hasCraftingRemainingItem(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getCraftingRemainingItem(ItemStack itemstack) {
		ItemStack retval = new ItemStack(this);
		retval.setDamageValue(itemstack.getDamageValue() + 1);
		if (retval.getDamageValue() >= retval.getMaxDamage()) {
			return ItemStack.EMPTY;
		}
		return retval;
	}

	@Override
	public boolean isRepairable(ItemStack itemstack) {
		return false;

	}}
