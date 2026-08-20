package com.susen36.caerulaarbor.event;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CAConfigs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

@EventBusSubscriber
public class PlayerEatEventHandler {
	@SubscribeEvent
	public static void onUseItemFinish(LivingEntityUseItemEvent.Finish event) {
		Entity entity = event.getEntity();
		ItemStack itemStack = event.getItem();
        String itemId = BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString();
		double minimumLightGain;
		double maximumLightGain;
		double separatorIndex;
		if (itemId.equals("alexscaves:biome_treat")) {
			return;
		}
		for (String configuredLightFood : CAConfigs.LIGHTS_FOOD.get()) {
			separatorIndex = configuredLightFood.indexOf(", ");
			if (separatorIndex < 0) {
				CaerulaArbor.LOGGER.warn("Wrong lights food config for item" + configuredLightFood);
				return;
			}
			String configuredItemId = configuredLightFood.substring(0, (int) separatorIndex);
			if (itemId.equals(configuredItemId)) {
				String lightRange = configuredLightFood.substring((int) (separatorIndex + 2));
				separatorIndex = lightRange.indexOf("/");
				if (separatorIndex < 0) {
					CaerulaArbor.LOGGER.warn("Wrong lights food config for item" + configuredLightFood);
					return;
				}
				try {
					minimumLightGain = Double.parseDouble(lightRange.substring(0, (int) separatorIndex).trim());
				} catch (Exception exception) {
					minimumLightGain = 0;
				}
				try {
					maximumLightGain = Double.parseDouble(lightRange.substring((int) (separatorIndex + 1)).trim());
				} catch (Exception exception) {
					maximumLightGain = 0;
				}
				double setval = Math.min(100,
						ModCapabilities.getPlayerVariables(entity).player_light
								+ Mth.nextInt(RandomSource.create(), (int) minimumLightGain, (int) maximumLightGain));
				PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
				capability.player_light = setval;
				capability.syncPlayerVariables(entity);
			}
		}
	}
}