
package com.susen36.caerulaarbor.item;

import com.susen36.babel.api.entity.ElementalAttacker;
import com.susen36.babel.elemental.base.AbstractEPCapability;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.List;


public class ComplexChitinPickaxeItem extends PickaxeItem implements ElementalAttacker {
	private static final Tier TIER = new SimpleTier(
			BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
			3374,
			15f,
			6.5f,
			18,
			() -> Ingredient.of(new ItemStack(CAItems.COMPLEX_CHITIN.get()))
	);

	public ComplexChitinPickaxeItem() {
		super(TIER, new Item.Properties().fireResistant().attributes(createAttributes()));
	}

	private static ItemAttributeModifiers createAttributes() {
		ItemAttributeModifiers base = PickaxeItem.createAttributes(TIER, 1, -2.8f);
		ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
		for (ItemAttributeModifiers.Entry entry : base.modifiers()) {
			builder.add(entry.attribute(), entry.modifier(), entry.slot());
		}
		return builder.build();
	}

	@Override
	public AbstractEPCapability.EPType getElementalType() {
		return AbstractEPCapability.EPType.NERVOUS;
	}

	@Override
	public double getElementalRate() {
		return 0.0D;
	}

	@Override
	public double getElementalInjuryDamage() {
		return 120.0D;
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

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.complex_chitin_pickaxe.description_0"));
	}
}