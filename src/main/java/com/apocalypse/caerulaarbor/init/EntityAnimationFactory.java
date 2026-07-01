package com.apocalypse.caerulaarbor.init;

import com.apocalypse.caerulaarbor.entity.base.SyncedAnimationEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EntityAnimationFactory {
	@SubscribeEvent
	public static void onEntityTick(LivingEvent.LivingTickEvent event) {
		if (event.getEntity() instanceof SyncedAnimationEntity syncable) {
			syncable.syncClientAnimation();
		}
	}
}
