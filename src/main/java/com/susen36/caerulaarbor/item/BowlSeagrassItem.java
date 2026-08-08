
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.Relic;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
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
		super(new Item.Properties().stacksTo(16).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(3).saturationModifier(0.4f).alwaysEdible().build()));
	}

	@Override
	public int getUseDuration(ItemStack itemstack, LivingEntity user) {
		return 30;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.bowl_seagrass.description_0"));
		list.add(Component.translatable("item.caerula_arbor.bowl_seagrass.description_1"));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = new ItemStack(Items.BOWL);
		super.finishUsingItem(itemstack, world, entity);
		if (!entity.level().isClientSide())
			entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 200, 0));
		boolean setval = true;
		PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
		Relic.SEAWEED_SALAD.set(capability, setval ? 1 : 0);
		capability.syncPlayerVariables(entity);
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