
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.List;


public class AegirSwordItem extends SwordItem {
	private static final Tier TIER = new SimpleTier(
			BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
			1299,
			7f,
			7f,
			9,
            Ingredient::of
	);

	public AegirSwordItem() {
		super(TIER, new Item.Properties().attributes(SwordItem.createAttributes(TIER, 3, -2.4f)));
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
		list.add(Component.translatable("item.caerula_arbor.aegir_sword.description_0"));
		list.add(Component.translatable("item.caerula_arbor.aegir_sword.description_1"));
	}

	private static final ResourceLocation AEGIR_ATTACK_ID = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "aegir_sword_attack");

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		if (entity instanceof LivingEntity livingEntity && !entity.level().isClientSide()) {
			AttributeInstance attackAttr = livingEntity.getAttribute(Attributes.ATTACK_DAMAGE);
			if (selected && EntityUtils.getHealthPerc(entity) >= 0.5) {
				if (attackAttr.getModifier(AEGIR_ATTACK_ID) == null) {
					attackAttr.addTransientModifier(new AttributeModifier(AEGIR_ATTACK_ID, 0.25D * 2.0D, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
				}
			} else if (attackAttr.getModifier(AEGIR_ATTACK_ID) != null) {
				attackAttr.removeModifier(AEGIR_ATTACK_ID);
			}
		}
	}
}