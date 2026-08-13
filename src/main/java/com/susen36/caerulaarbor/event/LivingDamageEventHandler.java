package com.susen36.caerulaarbor.event;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;


@EventBusSubscriber
public class LivingDamageEventHandler {

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        handleReduceLightsWithDamage(event);
    }

    //TODO 修改为消耗目标生命值才扣灯火
    private static void handleReduceLightsWithDamage(LivingDamageEvent.Pre event) {
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();
        double amount = event.getNewDamage();

        if (sourceentity == null || entity == sourceentity) return;

        if (entity instanceof Player) {
            double light_cost = Math.min(amount * 0.0025, 0.25);
            double setval = Math.max(ModCapabilities.getPlayerVariables(entity).player_light - light_cost, 0);
            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
            capability.player_light = setval;
            capability.syncPlayerVariables(entity);
        }
    }

}