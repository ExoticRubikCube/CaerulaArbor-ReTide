package com.susen36.caerulaarbor.event;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CADamageTypes;
import com.susen36.caerulaarbor.util.EntityUtils;
import com.susen36.caerulaarbor.util.NodeUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;


@EventBusSubscriber
public class LivingDamageEventHandler {

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (event == null) return;

        handleReduceLightsWithDamage(event);
        handlePlayerEvolutionDamage(event);
    }

    private static void handleReduceLightsWithDamage(LivingDamageEvent.Pre event) {
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();
        double amount = event.getNewDamage();

        if (sourceentity == null) return;
        if (entity == sourceentity) return;

        if (entity instanceof Player) {
            double light_cost = Math.min(amount * 0.0025, 0.25);
            double setval = Math.max(ModCapabilities.getPlayerVariables(entity).player_light - light_cost, 0);
            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
            capability.player_light = setval;
            capability.syncPlayerVariables(entity);
        }
    }

    private static void handlePlayerEvolutionDamage(LivingDamageEvent.Pre event) {
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();

        if (sourceentity == null) return;

        if (!(sourceentity instanceof Player attacker) || !EntityUtils.canPlayerEvo(attacker)) return;

        double barrier = attacker.getAttributes().hasAttribute(CAAttributes.LIVING_BARRIER)
                ? attacker.getAttribute(CAAttributes.LIVING_BARRIER).getBaseValue()
                : 0;

        double rate;
        double max;
        double lvl = NodeUtils.getNodeLivingBarrier(attacker);

        if (lvl >= 4) { rate = 0.3; max = 10; }
        else if (lvl >= 3) { rate = 0.18; max = 10; }
        else if (lvl >= 2) { rate = 0.09; max = 5; }
        else if (lvl >= 1) { rate = 0.03; max = 5; }
        else { rate = 0; max = 0; }

        max = max * attacker.getMaxHealth();

        if (barrier < max && rate > 0) {
            if (attacker.getAttributes().hasAttribute(CAAttributes.LIVING_BARRIER))
                attacker.getAttribute(CAAttributes.LIVING_BARRIER).setBaseValue(Math.min(barrier + event.getNewDamage() * rate, max));
        }

        lvl = NodeUtils.getNodeRealDamage(attacker);

        if (lvl >= 4) rate = 0.3;
        else if (lvl >= 3) rate = 0.18;
        else if (lvl >= 2) rate = 0.09;
        else if (lvl >= 1) rate = 0.03;
        else rate = 0;

        if (rate > 0) {
            if (attacker.isShiftKeyDown()) return;

            LevelAccessor world = entity.level();
            double x = entity.getX();
            double y = entity.getY();
            double z = entity.getZ();
            double amount = event.getNewDamage();

            double h = (entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) - amount;
            double d = Math.min((attacker.getAttributes().hasAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE) ? attacker.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue() : 0) * rate, h - 1);

            if (d > 0) {
                entity.hurt(CADamageTypes.source(world, CADamageTypes.HAND_OF_CHOKER, attacker), (float) d);
                if (world instanceof ServerLevel level)
                    level.sendParticles(ParticleTypes.GLOW_SQUID_INK, x, (y + 0.75), z, 3, 0.75, 0.75, 0.75, 0.1);
            }
        }
    }

}