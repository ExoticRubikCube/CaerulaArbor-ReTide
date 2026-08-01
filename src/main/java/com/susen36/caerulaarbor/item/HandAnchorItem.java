
package com.susen36.caerulaarbor.item;

import com.susen36.babel.init.BabelMobEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.List;


public class HandAnchorItem extends PickaxeItem {
	private static final Tier TIER = new SimpleTier(
			BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
			1299,
			4f,
			9f,
			10,
			() -> Ingredient.of(new ItemStack(Items.IRON_INGOT), new ItemStack(Blocks.DEEPSLATE))
	);

	public HandAnchorItem() {
		super(TIER, new Item.Properties().fireResistant().attributes(PickaxeItem.createAttributes(TIER, 1, -3f)));
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity living, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, living, sourceentity);
        if (Math.random() < 0.15) {
            if (!living.level().isClientSide())
				living.addEffect(new MobEffectInstance(BabelMobEffects.DIZZY, 40, 0, false, false));
        }
        return retval;
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
		list.add(Component.translatable("item.caerula_arbor.hand_anchor.description_0"));
		list.add(Component.translatable("item.caerula_arbor.hand_anchor.description_1"));
	}
}