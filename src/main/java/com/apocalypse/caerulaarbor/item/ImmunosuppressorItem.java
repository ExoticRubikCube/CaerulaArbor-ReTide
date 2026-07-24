
package com.apocalypse.caerulaarbor.item;

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

public class ImmunosuppressorItem extends Item {
	public ImmunosuppressorItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(1).saturationMod(0f).alwaysEat().build()));
	}

	@Override
	public int getUseDuration(ItemStack itemstack) {
		return 30;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.immunosuppressor.description_0"));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
        if (!entity.level().isClientSide()) {
			entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 800, 3));
			entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 800, 1));
			entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0));
			entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 400, 2));
			entity.addEffect(new MobEffectInstance(MobEffects.POISON, 400, 1));
		}
		return super.finishUsingItem(itemstack, world, entity);
	}
}
