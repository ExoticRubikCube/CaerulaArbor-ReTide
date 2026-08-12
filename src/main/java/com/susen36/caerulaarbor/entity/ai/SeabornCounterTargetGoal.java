package com.susen36.caerulaarbor.entity.ai;

import com.susen36.caerulaarbor.init.CAGameRules;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;

public class SeabornCounterTargetGoal extends NearestAttackableTargetGoal<Monster> {

    public SeabornCounterTargetGoal(Mob mob) {
        super(mob, Monster.class, 10, true, false, candidate -> {
            if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(candidate)) {
                return false;
            }
            if (candidate.getPersistentData().getBoolean("seabornForgive")) {
                return false;
            }
            if (!EntityUtils.isSameTeam(mob, candidate)) {
                return (candidate.getType().is(EntityUtils.SEABORN) ||
                        candidate.getType().is(EntityUtils.SEABORN_BOSS) ||
                        candidate.getType().is(EntityUtils.SEABORN_MINION)) &&
                        !candidate.getType().is(EntityUtils.SEABORN_PET);
            }
            return false;
        });
    }

    @Override
    public boolean canUse() {
        if (!this.mob.level().getLevelData().getGameRules().getBoolean(CAGameRules.DEFENSIVE_MODE)) {
            return false;
        }
        return super.canUse();
    }
}
