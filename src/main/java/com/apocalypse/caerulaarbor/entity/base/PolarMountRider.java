package com.apocalypse.caerulaarbor.entity.base;

import com.apocalypse.caerulaarbor.entity.OceanizedHorseEntity;
import com.apocalypse.caerulaarbor.entity.OceanizedPolarBearEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;

public interface PolarMountRider {
	default void tickMountBehavior() {
		if (!(((Entity) this).isAlive()) || ((Entity) this).isPassenger())
			return;
		Entity mount;
		if (((Entity) this).tickCount % 40 == 10 && Math.random() < 0.33) {
			mount = findNearestRidable(((Entity) this).level(), ((Entity) this).getX(), ((Entity) this).getY(), ((Entity) this).getZ(), (Entity) this, 12, OceanizedPolarBearEntity.class);
			if (mount != null && mount.isAlive() && !mount.isVehicle()) {
				if (this instanceof Mob mob)
					mob.getNavigation().moveTo(mount.getX(), mount.getY(), mount.getZ(), 1);
			} else {
				mount = findNearestRidable(((Entity) this).level(), ((Entity) this).getX(), ((Entity) this).getY(), ((Entity) this).getZ(), (Entity) this, 12, OceanizedHorseEntity.class);
				if (mount != null && mount.isAlive() && !mount.isVehicle()) {
					if (this instanceof Mob mob)
						mob.getNavigation().moveTo(mount.getX(), mount.getY(), mount.getZ(), 1);
				}
			}
		}
		if (((Entity) this).tickCount % 20 == 10) {
			mount = findNearestRidable(((Entity) this).level(), ((Entity) this).getX(), ((Entity) this).getY(), ((Entity) this).getZ(), (Entity) this, 3, OceanizedPolarBearEntity.class);
			if (mount != null && mount.isAlive()) {
				if (mount.isVehicle())
					return;
				((Entity) this).startRiding(mount);
			} else {
				mount = findNearestRidable(((Entity) this).level(), ((Entity) this).getX(), ((Entity) this).getY(), ((Entity) this).getZ(), (Entity) this, 3, OceanizedHorseEntity.class);
				if (mount != null && mount.isAlive() && !mount.isVehicle())
					((Entity) this).startRiding(mount);
			}
		}
	}

	static Entity findNearestRidable(LevelAccessor world, double x, double y, double z, Entity entity, double distLimit, Class<? extends Entity> entityType) {
		if (entity == null)
			return null;
		Entity result = null;
		double minDist = 999;
		for (Entity entityiterator : world.getEntities(entity, new AABB((x + distLimit), (y + distLimit), (z + distLimit), (x - distLimit), (y - distLimit), (z - distLimit)))) {
			if (!entityType.isInstance(entityiterator)) {
				continue;
			}
			if (entityiterator.isVehicle()) {
				continue;
			}
			double d = entity.distanceTo(entityiterator);
			if (d < minDist && d < distLimit) {
				minDist = d;
				result = entityiterator;
			}
		}
		return result;
	}
}
