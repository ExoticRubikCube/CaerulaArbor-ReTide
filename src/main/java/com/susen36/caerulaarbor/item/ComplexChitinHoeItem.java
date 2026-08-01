
package com.susen36.caerulaarbor.item;

import com.susen36.babel.api.entity.ElementalAttacker;
import com.susen36.babel.elemental.base.AbstractEPCapability;
import com.susen36.caerulaarbor.init.CABlocks;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.List;


public class ComplexChitinHoeItem extends HoeItem implements ElementalAttacker {
	private static final Tier TIER = new SimpleTier(
			BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
			3374,
			15f,
			0.5f,
			18,
			() -> Ingredient.of(new ItemStack(CAItems.COMPLEX_CHITIN.get()))
	);

	public ComplexChitinHoeItem() {
		super(TIER, new Item.Properties().fireResistant().attributes(createAttributes()));
	}

	private static ItemAttributeModifiers createAttributes() {
		ItemAttributeModifiers base = HoeItem.createAttributes(TIER, 0, 0f);
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
		return 80.0D;
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
		list.add(Component.translatable("item.caerula_arbor.complex_chitin_hoe.description_0"));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
		if (context.getPlayer() == null) {
			return InteractionResult.PASS;
		}
		// TODO：评估是否为 ComplexChitinHoeItem 与 TrailriteHoeItem 制作共同基类，并将这段共享交互逻辑收口到那里。
		BlockState clickedState = context.getLevel().getBlockState(context.getClickedPos());
		if (context.getPlayer().isShiftKeyDown() && clickedState.getBlock() == Blocks.FARMLAND) {
			BlockState oceanFarmlandState = CABlocks.OCEAN_FARMLAND.get().withPropertiesOf(clickedState);
			context.getLevel().setBlock(context.getClickedPos(), oceanFarmlandState, 3);
		}
		return InteractionResult.SUCCESS;
	}
}