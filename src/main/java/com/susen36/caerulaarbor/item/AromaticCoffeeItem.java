
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
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

public class AromaticCoffeeItem extends Item {
	public AromaticCoffeeItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON).food((new FoodProperties.Builder()).nutrition(5).saturationMod(3f).alwaysEat().build()));
	}

	@Override
	public int getUseDuration(ItemStack itemstack) {
		return 40;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.aromatic_coffee.description_0"));
		list.add(Component.translatable("item.caerula_arbor.aromatic_coffee.description_1"));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = super.finishUsingItem(itemstack, world, entity);
		if (!entity.level().isClientSide())
			entity.addEffect(new MobEffectInstance(MobEffects.JUMP, 240, 0));
		double setval = (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_shield + 1;
		entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
			capability.player_shield = setval;
			capability.syncPlayerVariables(entity);
		});
		entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
			capability.player_util_AROMATIC = true;
			capability.syncPlayerVariables(entity);
		});
		return retval;
	}
}
