package com.susen36.caerulaarbor.event;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;

@EventBusSubscriber
public class LivingExpDropEventHandler {
	@SubscribeEvent
	public static void onLivingDropXp(LivingExperienceDropEvent event) {
		if (event == null || event.getEntity() == null) return;

		Player sourceentity = event.getAttackingPlayer();
		if (sourceentity == null)
			return;
		if ((sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_king_EXTENSION) {
			if ((sourceentity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_lives <= 1) {
				event.setDroppedExperience((int) (event.getDroppedExperience() * 1.5));
			}
		}
	}
}
