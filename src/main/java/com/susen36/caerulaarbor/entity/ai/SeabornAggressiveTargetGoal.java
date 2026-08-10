package com.susen36.caerulaarbor.entity.ai;

import com.susen36.caerulaarbor.init.CAGameRules;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

public class SeabornAggressiveTargetGoal extends NearestAttackableTargetGoal<LivingEntity> {

    public SeabornAggressiveTargetGoal(Mob mob) {
        super(mob, LivingEntity.class, 5, true, false, candidate -> {
            /*
             * Java小知识：^（异或运算符）
             * 在布尔运算中，A ^ B 表示"同为假，异为真"。
             * 也就是说，只有当 mob 和 candidate 一个在水里、另一个不在水里（状态不一致）时，
             * 整个表达式才会返回 true。
             */
            if (mob.isInWater() ^ candidate.isInWater()) {
                return false;
            }
            if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(candidate)) {
                return false;
            }
            if (candidate.getType().is(EntityUtils.SEA_BORN)) {
                return false;
            }
            if (candidate.getType().is(EntityUtils.SEA_BORN_PET)) {
                return false;
            }
            if (candidate.getType().is(EntityUtils.SEA_BORN_BOSS)) {
                return false;
            }
            if (candidate.getType().is(EntityUtils.SEA_BORN_MINION)) {
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
        return super.canUse();
    }
}
