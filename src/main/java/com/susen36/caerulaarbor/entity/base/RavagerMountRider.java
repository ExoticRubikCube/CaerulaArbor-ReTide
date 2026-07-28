package com.susen36.caerulaarbor.entity.base;

import com.susen36.caerulaarbor.entity.OceanizedRavagerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

public interface RavagerMountRider extends PolarMountRider {
	@Override
	default void tickMountBehavior() {
		if (!(this instanceof Entity entity) || !entity.isAlive() || entity.isPassenger())
			return;
		Entity ravager;
		boolean canOrWillRide = false;
		if (entity.tickCount % 40 == 10 && Math.random() < 0.33) {
			ravager = PolarMountRider.findNearestRidable(entity.level(), entity.getX(), entity.getY(), entity.getZ(), (Entity) this, 12, OceanizedRavagerEntity.class);
			if (ravager != null && ravager.isAlive() && !ravager.isVehicle()) {
				if (this instanceof Mob mob)
					mob.getNavigation().moveTo(ravager.getX(), ravager.getY(), ravager.getZ(), 1);
				canOrWillRide = true;
			}
		}
		if (entity.tickCount % 20 == 10) {
			ravager = PolarMountRider.findNearestRidable(entity.level(), entity.getX(), entity.getY(), entity.getZ(), (Entity) this, 3, OceanizedRavagerEntity.class);
			if (ravager != null && ravager.isAlive()) {
				if (ravager.isVehicle())
					return;
				entity.startRiding(ravager);
				canOrWillRide = true;
			}
		}
		if (!canOrWillRide)
			PolarMountRider.super.tickMountBehavior();
	}
}