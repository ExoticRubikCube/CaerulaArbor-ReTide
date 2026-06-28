package com.apocalypse.caerulaarbor.event;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.OceanizedEvokerEntity;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingUseTotemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class TotemEventHandler {
	@SubscribeEvent
	public static void whenEntityUsesTotem(LivingUseTotemEvent event) {
		if (event == null || event.getEntity() == null) {
			return;
		}

		if (event.getEntity() instanceof OceanizedEvokerEntity oceanizedEvoker) {
			CaerulaArborMod.queueServerWork(2, () -> {
				if (oceanizedEvoker.isAlive() && !oceanizedEvoker.level().isClientSide()) {
					oceanizedEvoker.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 20, 0, false, false));
				}
			});
			CaerulaArborMod.queueServerWork(5, () -> {
				if (oceanizedEvoker.isAlive()) {
					oceanizedEvoker.setHealth((float) (oceanizedEvoker.getMaxHealth() * 0.65));
				}
			});
			return;
		}

		if (event.getEntity() instanceof Player player) {
			double nextShield = player.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null)
					.orElse(new CaerulaArborModVariables.PlayerVariables()).player_shield + 1;
			player.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
				capability.player_shield = nextShield;
				capability.syncPlayerVariables(player);
			});
		}
	}
}
