
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.api.event.SanityEvent;
import com.susen36.caerulaarbor.capability.sanity.SIHelper;
import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class NourishedApplePieItem extends Item {
	public NourishedApplePieItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON).food((new FoodProperties.Builder()).nutrition(8).saturationMod(0.8f).alwaysEat().build()));
	}

	@Override
	public int getUseDuration(ItemStack itemstack) {
		return 30;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.nourished_apple_pie.description_0"));
		list.add(Component.translatable("item.caerula_arbor.nourished_apple_pie.description_1"));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = super.finishUsingItem(itemstack, world, entity);
		//TODO 修改为使用.effect()
		if (!entity.level().isClientSide()) {
			entity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 30, 0));
			entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 800, 2));
			entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 2));
			entity.addEffect(new MobEffectInstance(CAMobEffects.ESSENCE_RESISTANCE.get(), 3600, 0));
		}
		entity.setHealth((float) (entity.getHealth() + entity.getMaxHealth() * 0.15));
		SIHelper.causeSanityInjury(entity, 75, SanityEvent.Hurt.Type.FOOD);
		return retval;
	}
}
