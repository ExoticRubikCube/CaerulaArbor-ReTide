package com.apocalypse.caerulaarbor.events;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.*;
import com.apocalypse.caerulaarbor.init.CaerulaArborModAttributes;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Comparator;
import java.util.List;

@Mod.EventBusSubscriber
public class LivingDamageEventHandler {

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event == null || event.getEntity() == null) return;

        handleReduceLightsWithDamage(event);
        handleSanityRateFunctions(event);
        handlePlayerEvolutionDamage(event);
    }

    private static void handleReduceLightsWithDamage(LivingDamageEvent event) {
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();
        double amount = event.getAmount();

        if (entity == null || sourceentity == null) return;
        if (event.isCanceled()) return;
        if (entity == sourceentity) return;

        if (entity instanceof Player) {
            double light_cost = Math.min(amount * 0.0025, 0.25);
            double _setval = Math.max((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).player_light - light_cost, 0);
            entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
                capability.player_light = _setval;
                capability.syncPlayerVariables(entity);
            });
        }
    }

    private static void handleSanityRateFunctions(LivingDamageEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();
        double amount = event.getAmount();

        if (entity == null || sourceentity == null) return;
        if (event.isCanceled()) return;

        if ((sourceentity instanceof LivingEntity _livingEntity1 && _livingEntity1.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_RATE.get())
                ? _livingEntity1.getAttribute(CaerulaArborModAttributes.SANITY_RATE.get()).getValue()
                : 0) > 0) {
            EntityUtils.deductSanity(entity, amount * (sourceentity instanceof LivingEntity _livingEntity2 && _livingEntity2.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_RATE.get())
                    ? _livingEntity2.getAttribute(CaerulaArborModAttributes.SANITY_RATE.get()).getValue()
                    : 0));
            new Object() {
                void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                    if (world instanceof ServerLevel _level)
                        _level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, (y + entity.getBbHeight() * 0.5), z,
                                (int) Math.min(1 * (sourceentity instanceof LivingEntity _livingEntity4 && _livingEntity4.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_RATE.get())
                                        ? _livingEntity4.getAttribute(CaerulaArborModAttributes.SANITY_RATE.get()).getValue()
                                        : 0), 16),
                                1.2, 1.5, 1.2, 0.1);
                    final int tick2 = ticks;
                    CaerulaArborMod.queueServerWork(tick2, () -> {
                        if (timedlooptotal > timedloopiterator + 1) {
                            timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                        }
                    });
                }
            }.timedLoop(0, 5, 1);
        }
    }

    private static void handlePlayerEvolutionDamage(LivingDamageEvent event) {
        DamageSource damagesource = event.getSource();
        Entity entity = event.getEntity();
        Entity sourceentity = event.getSource().getEntity();

        if (damagesource == null || entity == null || sourceentity == null) return;

        if (!(sourceentity instanceof Player attacker) || !EntityUtils.canPlayerEvo(attacker)) return;

        double barrier = attacker.getAttributes().hasAttribute(CaerulaArborModAttributes.LIVING_BARRIER.get())
                ? attacker.getAttribute(CaerulaArborModAttributes.LIVING_BARRIER.get()).getBaseValue()
                : 0;

        double rate;
        double max;
        double lvl = EntityUtils.getNodeLivingBarrier(attacker);

        if (lvl >= 4) { rate = 0.3; max = 10; }
        else if (lvl >= 3) { rate = 0.18; max = 10; }
        else if (lvl >= 2) { rate = 0.09; max = 5; }
        else if (lvl >= 1) { rate = 0.03; max = 5; }
        else { rate = 0; max = 0; }

        max = max * attacker.getMaxHealth();

        if (barrier < max && rate > 0) {
            if (attacker.getAttributes().hasAttribute(CaerulaArborModAttributes.LIVING_BARRIER.get()))
                attacker.getAttribute(CaerulaArborModAttributes.LIVING_BARRIER.get()).setBaseValue(Math.min(barrier + event.getAmount() * rate, max));
        }

        lvl = EntityUtils.getNodeRealDamage(attacker);

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
            double amount = event.getAmount();

            double h = (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) - amount;
            double d = Math.min((attacker.getAttributes().hasAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE) ? attacker.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue() : 0) * rate, h - 1);

            if (d > 0) {
                entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hand_of_choker"))), attacker), (float) d);
                if (world instanceof ServerLevel _level)
                    _level.sendParticles(ParticleTypes.GLOW_SQUID_INK, x, (y + 0.75), z, 3, 0.75, 0.75, 0.75, 0.1);
            }
        }
    }

}
