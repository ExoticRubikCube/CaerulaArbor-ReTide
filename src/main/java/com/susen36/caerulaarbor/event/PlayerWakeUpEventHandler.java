package com.susen36.caerulaarbor.event;

import com.susen36.babel.util.LifePointUtils;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;

@EventBusSubscriber
public class PlayerWakeUpEventHandler {
	@SubscribeEvent
	public static void onEntityEndSleep(PlayerWakeUpEvent event) {
		if (!event.updateLevel() && !event.wakeImmediately()) {
			Entity entity = event.getEntity();
			PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
			capability.player_light = capability.player_light + Mth.nextInt(RandomSource.create(), 1, 3);
			capability.syncPlayerVariables(entity);
			if (capability.player_light > 100) {
				capability.player_light = 100;
				capability.syncPlayerVariables(entity);
			}
			if (Math.random() < 0.2) {
				LifePointUtils.setShieldPoint(entity, LifePointUtils.getShieldPoint(entity) + 1);
			}
		}
	}
}