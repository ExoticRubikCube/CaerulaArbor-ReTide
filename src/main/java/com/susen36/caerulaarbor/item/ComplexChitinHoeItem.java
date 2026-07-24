
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CABlocks;
import com.susen36.caerulaarbor.init.CAItems;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.UUID;

public class ComplexChitinHoeItem extends HoeItem {
	public ComplexChitinHoeItem() {
		super(new Tier() {
			public int getUses() {
				return 3374;
			}

			public float getSpeed() {
				return 15f;
			}

			public float getAttackDamageBonus() {
				return 0.5f;
			}

			public int getLevel() {
				return 3;
			}

			public int getEnchantmentValue() {
				return 18;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(CAItems.COMPLEX_CHITIN.get()));
			}
		}, 0, 0f, new Item.Properties().fireResistant());
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
		Multimap<Attribute, AttributeModifier> map = super.getDefaultAttributeModifiers(equipmentSlot);
		if (equipmentSlot == EquipmentSlot.MAINHAND) {
			map = HashMultimap.create(map);
			map.put(CAAttributes.SANITY_INJURY_DAMAGE.get(),
					new AttributeModifier(new UUID(equipmentSlot.toString().hashCode(), 0), "caerula_arbor_attribute_modifier", 80, AttributeModifier.Operation.ADDITION));
		}
		return map;
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
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
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
