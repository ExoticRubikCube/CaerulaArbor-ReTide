
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.List;

public class AegirSwordItem extends SwordItem {
	public AegirSwordItem() {
		super(new Tier() {
			public int getUses() {
				return 1299;
			}

			public float getSpeed() {
				return 7f;
			}

			public float getAttackDamageBonus() {
				return 7f;
			}

			public int getLevel() {
				return 3;
			}

			public int getEnchantmentValue() {
				return 9;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of();
			}
		}, 3, -2.4f, new Item.Properties());
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
		list.add(Component.translatable("item.caerula_arbor.aegir_sword.description_0"));
		list.add(Component.translatable("item.caerula_arbor.aegir_sword.description_1"));
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		if (selected && EntityUtils.getHealthPerc(entity) >= 0.5) {
			if (!(entity instanceof LivingEntity livEnt0 && livEnt0.hasEffect(CAMobEffects.BOOST_OF_SILENCE.get()))) {
				if (entity instanceof LivingEntity living && !entity.level().isClientSide())
					living.addEffect(new MobEffectInstance(CAMobEffects.BOOST_OF_SILENCE.get(), 10, 1, false, false));
			}
		}
	}
}
