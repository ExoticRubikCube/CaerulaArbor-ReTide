package com.susen36.caerulaarbor.entity.ai;

import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.manager.upgrade.MigrationUpgradeManager;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import java.util.EnumSet;
import java.util.List;

public class StrengthOfCrowdGoal extends TargetGoal {
    private int timestamp;
    private final SeaMonster seaMonster;

    public StrengthOfCrowdGoal(SeaMonster mob) {
        super(mob, true);
        this.seaMonster = mob;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        int i = this.mob.getLastHurtByMobTimestamp();
        LivingEntity livingentity = this.mob.getLastHurtByMob();
        if (i != this.timestamp && livingentity != null) {
            if (livingentity == this.mob) {
                return false;
            } else {
                return !livingentity.getType().is(EntityUtils.SEA_BORN);
            }
        } else {
            return false;
        }
    }

    @Override
    public void start() {
        this.timestamp = this.mob.getLastHurtByMobTimestamp();
        this.alertOthers();
        super.start();
    }

    protected void alertOthers() {
        Level level = this.seaMonster.level();
        double migrationLevel = MapVariables.get(level).strategy_migration;
        int[] range = MigrationUpgradeManager.getMigrationRange(migrationLevel);
        double rangeXZ = range[0];
        double rangeY = range[1];

        AABB aabb = AABB.unitCubeFromLowerCorner(this.mob.position()).inflate(rangeXZ, rangeY, rangeXZ);
        List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, aabb, EntitySelector.NO_SPECTATORS);
        LivingEntity target = this.mob.getLastHurtByMob();

        for (LivingEntity candidate : list) {
            if (this.mob != candidate
                    && candidate instanceof Mob candidateMob
                    && candidate.getType().is(EntityUtils.SEA_BORN)
                    && !candidate.getType().is(EntityUtils.SEA_BORN_PET)
                    && candidateMob.getTarget() == null
                    && !candidateMob.isAlliedTo(target)) {
                this.alertOther(candidateMob, target);
            }
        }
    }

    protected void alertOther(Mob mob, LivingEntity target) {
        mob.setTarget(target);
    }
}
