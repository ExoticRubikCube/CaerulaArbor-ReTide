
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.List;

public class BowlSeagrassItem extends Item {
	public BowlSeagrassItem() {
		super(new Item.Properties().stacksTo(16).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(3).saturationMod(0.4f).alwaysEat().build()));
	}

	@Override
	public int getUseDuration(ItemStack itemstack) {
		return 30;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.bowl_seagrass.description_0"));
		list.add(Component.translatable("item.caerula_arbor.bowl_seagrass.description_1"));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = new ItemStack(Items.BOWL);
		super.finishUsingItem(itemstack, world, entity);
		if (!entity.level().isClientSide())
			entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 200, 0));
		{
			boolean setval = true;
			entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
				capability.relic_util_SEAGRASS = setval;
				capability.syncPlayerVariables(entity);
			});
		}
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
