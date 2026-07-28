package com.susen36.caerulaarbor.capability.sanity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.api.event.SanityEvent;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

public class SIHelper {
    private SIHelper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void causeSanityInjury(LivingEntity target, double value) {
        causeSanityInjury(target, null, value, SanityEvent.Hurt.Type.BLOCK);
    }

    public static void causeSanityInjury(LivingEntity target, double value, SanityEvent.Hurt.Type type) {
        causeSanityInjury(target, null, value, type);
    }

    public static void causeSanityInjury(LivingEntity target, LivingEntity attacker, double value) {
        causeSanityInjury(target, attacker, value, SanityEvent.Hurt.Type.ENTITY);
    }

    public static void causeSanityInjury(LivingEntity target, @Nullable LivingEntity attacker, double value, SanityEvent.Hurt.Type type) {
        SanityEvent.Hurt event = new SanityEvent.Hurt(attacker, target, value, type);
        if (!NeoForge.EVENT_BUS.post(event).isCanceled()) {
            ModCapabilities.getSanityInjury(target).hurt(event.getAmount());
        }
    }

    public static void causeSanityInjuryWithParticles(LivingEntity target, double value) {
        causeSanityInjuryWithParticles(target, null, value, SanityEvent.Hurt.Type.DEFAULT);
    }

    public static void causeSanityInjuryWithParticles(LivingEntity target, double value, SanityEvent.Hurt.Type type) {
        causeSanityInjuryWithParticles(target, null, value, type);
    }

    public static void causeSanityInjuryWithParticles(LivingEntity target, @Nullable LivingEntity attacker, double value, SanityEvent.Hurt.Type type) {
        causeSanityInjury(target, attacker, value, type);
        for (int i = 1; i < 4; i++) {
            CaerulaArborMod.queueServerWork(i * 3, () -> {
                if (target.level() instanceof ServerLevel server) {
                    server.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                            target.getX(), target.getY() + 0.5 * target.getBbHeight(), target.getZ(),
                            24, 0.86, 1.2, 0.86, 0.1);
                }
            });
        }
    }
}