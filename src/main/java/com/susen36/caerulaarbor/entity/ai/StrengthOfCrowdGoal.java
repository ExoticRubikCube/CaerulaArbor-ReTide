package com.susen36.caerulaarbor.entity.ai;

import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.manager.upgrade.MigrationUpgradeManager;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import java.util.EnumSet;
import java.util.List;

public class StrengthOfCrowdGoal extends Goal {
    private int timestamp;
    private final SeaMonster seaMonster;

    public StrengthOfCrowdGoal(SeaMonster mob) {
        this.seaMonster = mob;
        this.setFlags(EnumSet.noneOf(Goal.Flag.class));
    }

    @Override
    public boolean canUse() {
        int i = this.seaMonster.getLastHurtByMobTimestamp();
        LivingEntity livingentity = this.seaMonster.getLastHurtByMob();
        if (i != this.timestamp && livingentity != null) {
            if (livingentity == this.seaMonster) {
                return false;
            } else if (livingentity.getType().is(EntityUtils.SEA_BORN)) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public void start() {
        this.timestamp = this.seaMonster.getLastHurtByMobTimestamp();
        this.alertOthers();
    }

    protected void alertOthers() {
        Level level = this.seaMonster.level();
        double migrationLevel = MapVariables.get(level).strategy_migration;
        int[] range = MigrationUpgradeManager.getMigrationRange(migrationLevel);
        double rangeXZ = range[0];
        double rangeY = range[1];

        AABB aabb = AABB.unitCubeFromLowerCorner(this.seaMonster.position()).inflate(rangeXZ, rangeY, rangeXZ);
        List<SeaMonster> list = level.getEntitiesOfClass(SeaMonster.class, aabb, EntitySelector.NO_CREATIVE_OR_SPECTATOR);
        LivingEntity target = this.seaMonster.getLastHurtByMob();

        for (SeaMonster candidate : list) {
            if (this.seaMonster != candidate
                    && !candidate.getType().is(EntityUtils.SEA_BORN_PET)
                    && candidate.getTarget() == null
                    && !candidate.isAlliedTo(target)) {
                this.alertOther(candidate, target);
            }
        }
    }

    protected void alertOther(Mob mob, LivingEntity target) {
        if(mob.getTarget() == null) {
            mob.setTarget(target);
        }
    }
}