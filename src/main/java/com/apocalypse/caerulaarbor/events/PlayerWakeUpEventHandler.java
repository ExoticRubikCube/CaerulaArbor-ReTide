package com.apocalypse.caerulaarbor.events;

import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PlayerWakeUpEventHandler {
	@SubscribeEvent
	public static void onEntityEndSleep(PlayerWakeUpEvent event) {
		if (!event.updateLevel() && !event.wakeImmediately()) {
			Entity entity = event.getEntity();
			if (entity == null)
				return;
			entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
				capability.player_light = (entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).player_light + Mth.nextInt(RandomSource.create(), 1, 3);
				capability.syncPlayerVariables(entity);
			});
			if ((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).player_light > 100) {
				entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.player_light = 100;
					capability.syncPlayerVariables(entity);
				});
			}
			if (Math.random() < 0.2) {
				entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.player_shield = (entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).player_shield + 1;
					capability.syncPlayerVariables(entity);
				});
			}
		}
	}
}
