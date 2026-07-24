
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public class OceanthornShovelItem extends ShovelItem {
	public OceanthornShovelItem() {
		super(new Tier() {
			public int getUses() {
				return 325;
			}

			public float getSpeed() {
				return 5.5f;
			}

			public float getAttackDamageBonus() {
				return 5.5f;
			}

			public int getLevel() {
				return 2;
			}

			public int getEnchantmentValue() {
				return 18;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(CAItems.OCEAN_CRYSTAL.get()));
			}
		}, 1, -3f, new Item.Properties());
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
	}
}
