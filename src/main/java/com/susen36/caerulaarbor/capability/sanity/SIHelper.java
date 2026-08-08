package com.susen36.caerulaarbor.capability.sanity;

import com.susen36.babel.api.BabelAPI;
import com.susen36.babel.api.event.ElementEvent;
import com.susen36.babel.elemental.base.AbstractEPCapability;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.api.event.SanityEvent;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class SIHelper {
    private SIHelper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void causeSanityInjury(LivingEntity target, double value) {
        BabelAPI.hurtElemental(target, AbstractEPCapability.EPType.NERVOUS, Mth.floor(value));
    }

    public static void causeSanityInjury(LivingEntity target, double value, SanityEvent.Hurt.Type type) {
        BabelAPI.hurtElemental(target, AbstractEPCapability.EPType.NERVOUS, null, Mth.floor(value), ElementEvent.HurtType.valueOf(type.name()));
    }

    public static void causeSanityInjury(LivingEntity target, LivingEntity attacker, double value) {
        BabelAPI.hurtElemental(target, AbstractEPCapability.EPType.NERVOUS, attacker, Mth.floor(value));
    }

    public static void causeSanityInjury(LivingEntity target, @Nullable LivingEntity attacker, double value, SanityEvent.Hurt.Type type) {
        BabelAPI.hurtElemental(target, AbstractEPCapability.EPType.NERVOUS, attacker, Mth.floor(value), ElementEvent.HurtType.valueOf(type.name()));
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
            CaerulaArbor.queueServerWork(i * 3, () -> {
                if (target.level() instanceof ServerLevel server) {
                    server.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                            target.getX(), target.getY() + 0.5 * target.getBbHeight(), target.getZ(),
                            24, 0.86, 1.2, 0.86, 0.1);
                }
            });
        }
    }
}