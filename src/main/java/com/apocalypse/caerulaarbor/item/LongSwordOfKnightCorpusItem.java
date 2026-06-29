
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.init.CAItems;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.List;

public class LongSwordOfKnightCorpusItem extends SwordItem {
	public LongSwordOfKnightCorpusItem() {
		super(new Tier() {
			public int getUses() {
				return 4000;
			}

			public float getSpeed() {
				return 9f;
			}

			public float getAttackDamageBonus() {
				return 10f;
			}

			public int getLevel() {
				return 4;
			}

			public int getEnchantmentValue() {
				return 16;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(CAItems.KNIGHT_CORPSE.get()));
			}
		}, 3, -2.9f, new Item.Properties().fireResistant());
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
		list.add(Component.translatable("item.caerula_arbor.long_sword_of_knight_corpus.description_0"));
		list.add(Component.translatable("item.caerula_arbor.long_sword_of_knight_corpus.description_1"));
		list.add(Component.translatable("item.caerula_arbor.long_sword_of_knight_corpus.description_2"));
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		if (selected) {
            if (entity == null)
                return;
            if (!(entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(CAMobEffects.ADD_REACH.get()))) {
                if (entity instanceof LivingEntity living && !living.level().isClientSide())
                    living.addEffect(new MobEffectInstance(CAMobEffects.ADD_REACH.get(), 20, 2, false, false));
            }
        }
	}
}
