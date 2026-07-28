package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.init.CAParticles;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.LevelAccessor;

public class KingsBreathMobEffect extends MobEffect {
    public KingsBreathMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -3342337);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "kings_breath_attack_damage"), 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        LevelAccessor world = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        if ((ModCapabilities.getPlayerVariables(entity)).kingShowPtc) {
            if ((double) amplifier < 1) {
                world.addParticle(CAParticles.KING_SLAY.get(), (x + Mth.nextDouble(RandomSource.create(), -0.55, 0.55)), (y + Mth.nextDouble(RandomSource.create(), 0, entity.getBbHeight() * 0.6)),
                        (z + Mth.nextDouble(RandomSource.create(), -0.55, 0.55)), Math.sin(Mth.nextDouble(RandomSource.create(), 0, 6.283)), 0.05, Math.cos(Mth.nextDouble(RandomSource.create(), 0, 6.283)));
            } else {
                world.addParticle(CAParticles.KING_SLAY_RED.get(), (x + Mth.nextDouble(RandomSource.create(), -0.55, 0.55)), (y + Mth.nextDouble(RandomSource.create(), 0, entity.getBbHeight() * 0.6)),
                        (z + Mth.nextDouble(RandomSource.create(), -0.55, 0.55)), Math.sin(Mth.nextDouble(RandomSource.create(), 0, 6.283)), 0.05, Math.cos(Mth.nextDouble(RandomSource.create(), 0, 6.283)));
            }
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

}