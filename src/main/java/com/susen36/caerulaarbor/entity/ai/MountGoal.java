package com.susen36.caerulaarbor.entity.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;

public class MountGoal extends Goal {
    private final Mob rider;
    private final Class<? extends Entity>[] mountTypes;

    @SafeVarargs
    public MountGoal(Mob rider, Class<? extends Entity>... mountTypes) {
        this.rider = rider;
        this.mountTypes = mountTypes;
    }

    @Override
    public boolean canUse() {
        return this.rider.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        return this.rider.isAlive();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (!this.rider.isPassenger()) {
            if (this.rider.tickCount % 40 == 10 && this.rider.getRandom().nextFloat() < 0.33F) {
                Entity mount = this.findMount(12.0D);
                if (mount != null) {
                    this.rider.getNavigation().moveTo(mount.getX(), mount.getY(), mount.getZ(), 1.0D);
                }
            }
            if (this.rider.tickCount % 20 == 10) {
                Entity mount = this.findMount(3.0D);
                if (mount != null && mount.isAlive() && !mount.isVehicle()) {
                    this.rider.startRiding(mount);
                }
            }
        }
    }

    private Entity findMount(double distanceLimit) {
        Entity result = null;
        AABB searchBox = this.rider.getBoundingBox().inflate(distanceLimit);
        for (Class<? extends Entity> mountType : this.mountTypes) {
            double nearestDistanceSqr = Mth.square(distanceLimit);
            for (Entity entity : this.rider.level().getEntities(this.rider, searchBox)) {
                if (mountType.isInstance(entity) && entity.isAlive() && !entity.isVehicle()) {
                    double distanceSqr = this.rider.distanceToSqr(entity);
                    if (distanceSqr < nearestDistanceSqr) {
                        nearestDistanceSqr = distanceSqr;
                        result = entity;
                    }
                }
            }
            if (result != null) {
                break;
            }
        }
        return result;
    }
}
