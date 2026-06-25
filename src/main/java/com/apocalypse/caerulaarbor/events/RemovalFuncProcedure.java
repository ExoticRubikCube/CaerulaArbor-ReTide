package com.apocalypse.caerulaarbor.events;

import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class RemovalFuncProcedure {
	@SubscribeEvent
	public static void onEntityLeave(EntityLeaveLevelEvent event) {
		if (event.getEntity() != null) {
			String removalreason = "null";
			if (event.getEntity().getRemovalReason() != null) {
				removalreason = event.getEntity().getRemovalReason().toString().toLowerCase();
			}
			execute(event);
		}
	}

    private static void execute(@Nullable Event event) {
	}
}
