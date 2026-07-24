
package com.susen36.caerulaarbor.enchantment;

import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;

public class SynesthesiaEnchantment extends Enchantment {
	private static final EnchantmentCategory ENCHANTMENT_CATEGORY = EnchantmentCategory.create("caerula_arbor_synesthesia",
			item -> Ingredient.of(new ItemStack(CAItems.LEGENDARY_SPEAR.get()), new ItemStack(CAItems.HIGHMORE_SCYTHE.get()), new ItemStack(CAItems.DRAGON_WAND.get())).test(new ItemStack(item)));

	public SynesthesiaEnchantment() {
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
}
