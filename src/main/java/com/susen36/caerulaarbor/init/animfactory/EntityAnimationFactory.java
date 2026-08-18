package com.susen36.caerulaarbor.init.animfactory;

import com.susen36.caerulaarbor.api.anim.SyncedAnimationEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;


@EventBusSubscriber
public class EntityAnimationFactory {
	@SubscribeEvent
	public static void onEntityTick(EntityTickEvent.Pre event) {
		if (event.getEntity() instanceof SyncedAnimationEntity syncable) {
			syncable.syncClientAnimation();
		}
	}
}