package com.susen36.caerulaarbor.entity.ai;

import com.susen36.caerulaarbor.init.CAGameRules;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;

public class SeabornCounterTargetGoal extends NearestAttackableTargetGoal<Monster> {

    public SeabornCounterTargetGoal(Mob mob) {
        super(mob, Monster.class, 10, true, false, candidate -> {
            if (candidate.getType().is(EntityUtils.SEA_BORN)) {
                return false;
            }
            return !candidate.getType().is(EntityUtils.SEA_BORN_PET);
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
