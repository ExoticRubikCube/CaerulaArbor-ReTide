
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class FloureIcecreamItem extends Item {
	public FloureIcecreamItem() {
		super(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON).food((new FoodProperties.Builder()).nutrition(6).saturationMod(0.75f).alwaysEat().build()));
	}

	@Override
	public int getUseDuration(ItemStack itemstack) {
		return 24;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.fluore_icecream.description_0"));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = new ItemStack(CAItems.SHELL_OF_STONECUTTER.get());
		super.finishUsingItem(itemstack, world, entity);
		if (!entity.level().isClientSide()) {
			entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0));
			entity.addEffect(new MobEffectInstance(CAMobEffects.ESSENCE_RESISTANCE.get(), 1200, 0));
		}
		entity.removeEffect(CAMobEffects.FROZEN.get());
		entity.setTicksFrozen(0);
		EntityUtils.restorePlayerLights(entity, 12);
		if (itemstack.isEmpty()) {
			return retval;
		} else {
			if (entity instanceof Player player && !player.getAbilities().instabuild) {
				if (!player.getInventory().add(retval))
					player.drop(retval, false);
			}
			return itemstack;
		}
	}
}
