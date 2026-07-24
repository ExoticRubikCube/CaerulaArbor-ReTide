
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class HandAnchorItem extends PickaxeItem {
	public HandAnchorItem() {
		super(new Tier() {
			public int getUses() {
				return 1299;
			}

			public float getSpeed() {
				return 4f;
			}

			public float getAttackDamageBonus() {
				return 9f;
			}

			public int getLevel() {
				return 3;
			}

			public int getEnchantmentValue() {
				return 10;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(Items.IRON_INGOT), new ItemStack(Blocks.DEEPSLATE));
			}
		}, 1, -3f, new Item.Properties().fireResistant());
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity living, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, living, sourceentity);
        if (Math.random() < 0.15) {
            if (!living.level().isClientSide())
				living.addEffect(new MobEffectInstance(CAMobEffects.DIZZY.get(), 40, 0, false, false));
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
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.hand_anchor.description_0"));
		list.add(Component.translatable("item.caerula_arbor.hand_anchor.description_1"));
	}
}
