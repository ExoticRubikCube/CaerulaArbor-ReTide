package com.susen36.caerulaarbor.event;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;

@EventBusSubscriber
public class PlayerWakeUpEventHandler {
	@net.neoforged.bus.api.SubscribeEvent
	public static void onEntityEndSleep(PlayerWakeUpEvent event) {
		if (!event.updateLevel() && !event.wakeImmediately()) {
			Entity entity = event.getEntity();
            entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
				capability.player_light = (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_light + Mth.nextInt(RandomSource.create(), 1, 3);
				capability.syncPlayerVariables(entity);
			});
			if ((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_light > 100) {
				entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
					capability.player_light = 100;
					capability.syncPlayerVariables(entity);
				});
			}
			if (Math.random() < 0.2) {
				entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
					capability.player_shield = (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_shield + 1;
					capability.syncPlayerVariables(entity);
				});
			}
		}
	}
}
