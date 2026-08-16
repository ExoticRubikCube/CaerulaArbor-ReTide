package com.susen36.caerulaarbor.event;

import com.susen36.babel.util.LifePointUtils;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.OceanizedEvokerEntity;
import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingUseTotemEvent;

@EventBusSubscriber
public class TotemEventHandler {
	@SubscribeEvent
	public static void whenEntityUsesTotem(LivingUseTotemEvent event) {
		if (event == null) {
			return;
		}

		if (event.getEntity() instanceof OceanizedEvokerEntity oceanizedEvoker) {
			CaerulaArbor.queueServerWork(2, () -> {
				if (oceanizedEvoker.isAlive() && !oceanizedEvoker.level().isClientSide()) {
					oceanizedEvoker.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 20, 0, false, false));
				}
			});
			CaerulaArbor.queueServerWork(5, () -> {
				if (oceanizedEvoker.isAlive()) {
					oceanizedEvoker.setHealth((float) (oceanizedEvoker.getMaxHealth() * 0.65));
				}
			});
			return;
		}

		if (event.getEntity() instanceof Player player) {
			LifePointUtils.setShieldPoint(player, LifePointUtils.getShieldPoint(player) + 1);
		}
	}
}