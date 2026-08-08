package com.susen36.caerulaarbor.event;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CAConfigs;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
		String messageText;
		double minimumLightGain;
		double maximumLightGain;
		double separatorIndex;
		double lifeGain;
		double maxLives;
		double currentLives;
		if (itemId.equals("alexscaves:biome_treat")) {
			return;
		}
		if (entity instanceof LivingEntity livingEntity && itemStack.getComponents().has(DataComponents.FOOD) && livingEntity.getHealth() >= livingEntity.getMaxHealth() * 0.6) {
			if (entity instanceof Player player && player.getFoodData().getFoodLevel() < 20 && player.getFoodData().getSaturationLevel() < 20) {
				double reviveChance = itemStack.getItem().getFoodProperties(itemStack, livingEntity).nutrition() * 0.01;
				if (player.getFoodData().getFoodLevel() > 16) {
					reviveChance = reviveChance * 1.5;
				}
				if (itemStack.getItem().getFoodProperties(itemStack, livingEntity).saturation() > 0.5) {
					reviveChance = reviveChance * 1.25;
				}
				if (Math.random() < reviveChance) {
					double maxReviveAmount = 1;
					messageText = Component.translatable("gameplay.life_point.revive.2").getString();
					if (itemStack.getItem().getFoodProperties(itemStack, livingEntity).saturation() > 0.1 && Math.random() < 0.33) {
						maxReviveAmount = 2;
						messageText = Component.translatable("gameplay.life_point.revive.1").getString();
					}
					if (player.getFoodData().getFoodLevel() > 16 && Math.random() < 0.33) {
						messageText = Component.translatable("gameplay.life_point.revive.0").getString();
					}
					lifeGain = Mth.nextInt(RandomSource.create(), 1, (int) maxReviveAmount);
					maxLives = ModCapabilities.getPlayerVariables(entity).player_maxlive;
					currentLives = ModCapabilities.getPlayerVariables(entity).player_lives;
					if (currentLives < maxLives) {
						{
							double setval = Math.min(currentLives + lifeGain, maxLives);
							PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
							capability.player_lives = setval;
							capability.syncPlayerVariables(entity);
						}
						if (!player.level().isClientSide())
							player.displayClientMessage(Component.literal("§a" + messageText.replace("{num}", "" + Math.round(lifeGain))), true);
					}
				}
			}
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
				{
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
}