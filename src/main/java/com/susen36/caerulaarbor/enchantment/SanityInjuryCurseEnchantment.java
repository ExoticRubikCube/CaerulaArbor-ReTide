package com.susen36.caerulaarbor.enchantment;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.init.CAEnchantments;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.Objects;

public class SanityInjuryCurseEnchantment extends Enchantment {
	private static final EnchantmentCategory ENCHANTMENT_CATEGORY = EnchantmentCategory.create("caerula_arbor_sanity_injury_curse",
			item -> Ingredient.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "enchantable/sanity_defend"))).test(new ItemStack(item)));

	public SanityInjuryCurseEnchantment() {
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
	protected boolean checkCompatibility(Enchantment enchantment) {
		return super.checkCompatibility(enchantment) && !Objects.equals(CAEnchantments.SANITY_DEFEND.get(), enchantment);
	}

	@Override
	public boolean isCurse() {
		return true;
	}

	@Override
	public boolean isTradeable() {
		return false;
	}
}
