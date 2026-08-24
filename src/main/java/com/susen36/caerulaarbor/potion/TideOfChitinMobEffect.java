package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.init.CAParticles;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class TideOfChitinMobEffect extends MobEffect {
    public TideOfChitinMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -13382401);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        Level world = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        world.addParticle(CAParticles.KNIFEPTC.get(), (x + Mth.nextDouble(RandomSource.create(), -0.45, 0.45)), (y + Mth.nextDouble(RandomSource.create(), 0, entity.getBbHeight() * 0.8)),
                (z + Mth.nextDouble(RandomSource.create(), -0.45, 0.45)), Math.sin(Mth.nextDouble(RandomSource.create(), 0, 6.283)), 0.1, Math.cos(Mth.nextDouble(RandomSource.create(), 0, 6.283)));
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

}
