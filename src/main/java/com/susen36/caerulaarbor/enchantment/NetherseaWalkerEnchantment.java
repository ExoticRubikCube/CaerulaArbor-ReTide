package com.susen36.caerulaarbor.enchantment;

import com.susen36.caerulaarbor.CaerulaArborMod;

import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public class NetherseaWalkerEnchantment extends Enchantment {
	private static final EnchantmentCategory ENCHANTMENT_CATEGORY = EnchantmentCategory.create("caerula_arbor_nethersea_walker",
			item -> Ingredient.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "enchantable/nethersea_walker"))).test(new ItemStack(item)));

	public NetherseaWalkerEnchantment() {
		super(Enchantment.Rarity.COMMON, ENCHANTMENT_CATEGORY, new EquipmentSlot[]{EquipmentSlot.FEET});
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
		return 3;
	}

	@Override
	protected boolean checkCompatibility(Enchantment enchantment) {
		return super.checkCompatibility(enchantment) && !Objects.equals(Enchantments.FROST_WALKER, enchantment);
	}
}
