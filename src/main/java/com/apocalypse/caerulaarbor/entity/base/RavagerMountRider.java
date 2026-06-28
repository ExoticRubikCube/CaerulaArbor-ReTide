package com.apocalypse.caerulaarbor.entity.base;

import com.apocalypse.caerulaarbor.entity.OceanizedRavagerEntity;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

public interface RavagerMountRider extends PolarMountRider {
	@Override
	default void tickMountBehavior() {
		if (!(((Entity) this).isAlive()) || ((Entity) this).isPassenger())
			return;
		Entity ravager;
		boolean canOrWillRide = false;
		if (((Entity) this).tickCount % 40 == 10 && Math.random() < 0.33) {
			ravager = EntityUtils.findNearestRidable(((Entity) this).level(), ((Entity) this).getX(), ((Entity) this).getY(), ((Entity) this).getZ(), (Entity) this, 12, OceanizedRavagerEntity.class);
			if (ravager != null && ravager.isAlive() && !ravager.isVehicle()) {
				if (this instanceof Mob mob)
					mob.getNavigation().moveTo(ravager.getX(), ravager.getY(), ravager.getZ(), 1);
				canOrWillRide = true;
			}
		}
		if (((Entity) this).tickCount % 20 == 10) {
			ravager = EntityUtils.findNearestRidable(((Entity) this).level(), ((Entity) this).getX(), ((Entity) this).getY(), ((Entity) this).getZ(), (Entity) this, 3, OceanizedRavagerEntity.class);
			if (ravager != null && ravager.isAlive()) {
				if (ravager.isVehicle())
					return;
				((Entity) this).startRiding(ravager);
				canOrWillRide = true;
			}
		}
		if (!canOrWillRide)
			PolarMountRider.super.tickMountBehavior();
	}
}
