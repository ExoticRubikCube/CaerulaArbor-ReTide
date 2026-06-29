
package com.apocalypse.caerulaarbor.enchantment;

import com.apocalypse.caerulaarbor.init.CAItems;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;

public class ReflectionEnchantment extends Enchantment {
	private static final EnchantmentCategory ENCHANTMENT_CATEGORY = EnchantmentCategory.create("caerula_arbor_reflection", item -> Ingredient.of(new ItemStack(CAItems.PHLOEM_BOW.get())).test(new ItemStack(item)));

	public ReflectionEnchantment() {
		super(Enchantment.Rarity.COMMON, ENCHANTMENT_CATEGORY, EquipmentSlot.values());
	}

	@Override
	public int getMinCost(int level) {
		return 1 + level * 10;
	}

	@Override
	public int getMaxCost(int level) {
		return 6 + level * 10;
	}

	@Override
	public int getMaxLevel() {
		return 5;
	}

	@Override
	public boolean isTradeable() {
		return false;
	}
}
