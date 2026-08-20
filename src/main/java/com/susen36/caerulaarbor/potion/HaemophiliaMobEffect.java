
package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.init.CAParticles;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;

public class HaemophiliaMobEffect extends MobEffect {
    public HaemophiliaMobEffect() {
        super(MobEffectCategory.NEUTRAL, -3381505);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        LevelAccessor world = entity.level();
        if (!entity.isInvulnerable()) {
            double health_cur;
            if (Math.round((Entity) entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) < Math.round((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1)) {
                health_cur = Math.max(((Entity) entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) * (0.975 - 0.025 * (double) amplifier), 0.5);
                if (((Entity) entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) > 0.5 && entity.isAlive()) {
                    entity.hurt(entity.damageSources().starve(), (float) (((Entity) entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) - health_cur));
                    for (int index0 = 0; index0 < 24; index0++) {
                        world.addParticle(CAParticles.BLOODOOZE.get(), entity.getX(), (entity.getY() + 1.33), entity.getZ(), (Mth.nextDouble(RandomSource.create(), -1.25, 1.25)), (Mth.nextDouble(RandomSource.create(), -0.05, 0.05)),
                                (Mth.nextDouble(RandomSource.create(), -1.25, 1.25)));
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        // 随机 tick 触发扣血，概率 0.025 保持约每 40 tick 一次的均值（与固定节拍强度相近）；是否扣取决于 applyEffectTick 里的非满血判断
        return Math.random() < 0.025;
    }

}