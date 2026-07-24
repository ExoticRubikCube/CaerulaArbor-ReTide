package com.susen36.caerulaarbor.init.animfactory;

import com.susen36.caerulaarbor.entity.base.SyncedAnimationEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class EntityAnimationFactory {
	@SubscribeEvent
	public static void onEntityTick(LivingTickEvent event) {
		if (event.getEntity() instanceof SyncedAnimationEntity syncable) {
			syncable.syncClientAnimation();
		}
	}
}
