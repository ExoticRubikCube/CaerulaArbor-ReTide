package com.apocalypse.caerulaarbor.events;

import com.apocalypse.caerulaarbor.init.CaerulaArborModAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PlayerRebornEventHandle {
	@SubscribeEvent
	public static void onPlayerRespawned(PlayerEvent.PlayerRespawnEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof LivingEntity livingEntity && livingEntity.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY.get()))
			livingEntity.getAttribute(CaerulaArborModAttributes.SANITY.get()).setBaseValue(1000);
	}
}
