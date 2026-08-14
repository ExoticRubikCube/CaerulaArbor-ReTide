
package com.susen36.caerulaarbor.potion;

import com.susen36.babel.api.entity.ElementalAttackModifier;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.init.CAEntityTypeTags;
import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;

public class TrailBuffMobEffect extends MobEffect implements ElementalAttackModifier {
    public TrailBuffMobEffect() {
        super(MobEffectCategory.NEUTRAL, -10053121);
    }

    @Override
    public double modifyElementalRate(LivingEntity entity, double rate, int amplifier) {
        return rate * 2;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        LevelAccessor world = entity.level();
        if (entity.isAlive() && (Entity) entity instanceof LivingEntity living) {
            MapVariables mapVars = MapVariables.get(world);
            boolean isSeabornUnit = (living.getType().is(CAEntityTypeTags.SEABORN)
                    || living.getType().is(CAEntityTypeTags.SEABORN_BOSS)
                    || living.getType().is(CAEntityTypeTags.SEABORN_MINION))
                    && !living.getType().is(CAEntityTypeTags.SEABORN_PET);

            if (living.tickCount % 20 == 0) {
                if (isSeabornUnit) {
                    if (mapVars.strategy_silence >= 2) {
                        living.heal((float) (living.getMaxHealth() * 0.05));
                    } else if (mapVars.strategy_subsisting >= 3) {
                        living.heal((float) (living.getMaxHealth() * 0.02));
                    }
                } else if (living instanceof Player player
                        && ModCapabilities.getPlayerVariables(player).player_oceanization >= 3) {
                    if (mapVars.strategy_silence >= 2) {
                        living.heal((float) (living.getMaxHealth() * 0.05));
                    } else if (mapVars.strategy_subsisting >= 3) {
                        living.heal((float) (living.getMaxHealth() * 0.02));
                    }
                }
            }

            if (!living.level().isClientSide()) {
                if (isSeabornUnit) {
                    living.addEffect(new MobEffectInstance(CAMobEffects.RUNNING_ON_TRAIL, 5, 0, false, false));
                } else if (living instanceof Player player
                        && ModCapabilities.getPlayerVariables(player).player_oceanization >= 3) {
                    living.addEffect(new MobEffectInstance(CAMobEffects.RUNNING_ON_TRAIL, 5, 0, false, false));
                    living.addEffect(new MobEffectInstance(MobEffects.JUMP, 5, 0, false, false));
                }
            }
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

}