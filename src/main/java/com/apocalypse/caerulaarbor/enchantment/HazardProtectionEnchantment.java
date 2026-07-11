package com.apocalypse.caerulaarbor.enchantment;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CAEnchantments;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.Objects;

public class HazardProtectionEnchantment extends Enchantment {
	private static final EnchantmentCategory ENCHANTMENT_CATEGORY = EnchantmentCategory.create("caerula_arbor_hazard_protection",
			item -> Ingredient.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "enchantable/sanity_defend"))).test(new ItemStack(item)));

	public HazardProtectionEnchantment() {
		super(Enchantment.Rarity.UNCOMMON, ENCHANTMENT_CATEGORY, new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
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
		return 2;
	}

	@Override
	protected boolean checkCompatibility(Enchantment enchantment) {
		return super.checkCompatibility(enchantment) && !Objects.equals(CAEnchantments.FLEXIBILITY.get(), enchantment);
	}

	@Override
	public boolean isTreasureOnly() {
		return true;
	}
}
