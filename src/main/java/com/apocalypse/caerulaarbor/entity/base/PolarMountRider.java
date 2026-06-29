package com.apocalypse.caerulaarbor.entity.base;

import com.apocalypse.caerulaarbor.entity.OceanizedHorseEntity;
import com.apocalypse.caerulaarbor.entity.OceanizedPolarBearEntity;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

public interface PolarMountRider {
	default void tickMountBehavior() {
		if (!(((Entity) this).isAlive()) || ((Entity) this).isPassenger())
			return;
		Entity mount;
		if (((Entity) this).tickCount % 40 == 10 && Math.random() < 0.33) {
			mount = EntityUtils.findNearestRidable(((Entity) this).level(), ((Entity) this).getX(), ((Entity) this).getY(), ((Entity) this).getZ(), (Entity) this, 12, OceanizedPolarBearEntity.class);
			if (mount != null && mount.isAlive() && !mount.isVehicle()) {
				if (this instanceof Mob mob)
					mob.getNavigation().moveTo(mount.getX(), mount.getY(), mount.getZ(), 1);
			} else {
				mount = EntityUtils.findNearestRidable(((Entity) this).level(), ((Entity) this).getX(), ((Entity) this).getY(), ((Entity) this).getZ(), (Entity) this, 12, OceanizedHorseEntity.class);
				if (mount != null && mount.isAlive() && !mount.isVehicle()) {
					if (this instanceof Mob mob)
						mob.getNavigation().moveTo(mount.getX(), mount.getY(), mount.getZ(), 1);
				}
			}
		}
		if (((Entity) this).tickCount % 20 == 10) {
			mount = EntityUtils.findNearestRidable(((Entity) this).level(), ((Entity) this).getX(), ((Entity) this).getY(), ((Entity) this).getZ(), (Entity) this, 3, OceanizedPolarBearEntity.class);
			if (mount != null && mount.isAlive()) {
				if (mount.isVehicle())
					return;
				((Entity) this).startRiding(mount);
			} else {
				mount = EntityUtils.findNearestRidable(((Entity) this).level(), ((Entity) this).getX(), ((Entity) this).getY(), ((Entity) this).getZ(), (Entity) this, 3, OceanizedHorseEntity.class);
				if (mount != null && mount.isAlive() && !mount.isVehicle())
					((Entity) this).startRiding(mount);
			}
		}
	}
}
