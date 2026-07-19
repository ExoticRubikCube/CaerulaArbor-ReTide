package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.init.CAMobEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

//TODO 直接注册就行
public class ChitinCookieDoneItem extends Item {
	public ChitinCookieDoneItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food(new FoodProperties.Builder().nutrition(4).saturationMod(0.25f).alwaysEat().build()));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.chitin_cookie_done.description_0"));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity living) {
		ItemStack retval = super.finishUsingItem(itemstack, world, living);
        if (!living.level().isClientSide()) {
            living.addEffect(new MobEffectInstance(CAMobEffects.ADD_DEF_TINY.get(), 600, 5, false, false));
        }
        return retval;
	}
}