package com.susen36.caerulaarbor.entity.ai;

import com.susen36.caerulaarbor.entity.IzumikOffspringEntity;
import com.susen36.caerulaarbor.entity.MartusEntity;
import com.susen36.caerulaarbor.init.CAGameRules;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

public class SeabornAggressiveTargetGoal extends NearestAttackableTargetGoal<LivingEntity> {

    public SeabornAggressiveTargetGoal(Mob mob) {
        super(mob, LivingEntity.class, 5, true, false, candidate -> {
            if (mob.isInWater() ^ candidate.isInWater()) {
                return false;
            }
            if (candidate.getType().is(EntityUtils.SEA_BORN)) {
                return false;
            }
            if (candidate.getType().is(EntityUtils.SEA_FRIEND)) {
                return false;
            }
            if (candidate.getPersistentData().getBoolean("seabornForgive")) {
                return false;
            }
            return !EntityUtils.isSameTeam(mob, candidate);
        });
    }

    @Override
    public boolean canUse() {
        if (!this.mob.level().getLevelData().getGameRules().getBoolean(CAGameRules.AGGRESIVE_MODE)) {
            return false;
        }
        if (this.mob instanceof IzumikOffspringEntity || this.mob instanceof MartusEntity) {
            return false;
        }
        return super.canUse();
    }
}
