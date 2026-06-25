package com.apocalypse.caerulaarbor.procedures;

import com.apocalypse.caerulaarbor.entity.OceanizedHorseEntity;
import com.apocalypse.caerulaarbor.entity.OceanizedPolarBearEntity;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LevelAccessor;

public class SeabornRidePolarProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		Entity ravager = null;
		if (!entity.isAlive()) {
			return;
		}
		if (entity.isPassenger()) {
			return;
		}
		if (entity.tickCount % 40 == 10 && Math.random() < 0.33) {
			ravager = EntityUtils.findNearestRidable(world, x, y, z, entity, 12, OceanizedPolarBearEntity.class);
			if (!(ravager == null) && ravager.isAlive()) {
				if (!ravager.isVehicle()) {
					if (entity instanceof Mob _entity)
						_entity.getNavigation().moveTo((ravager.getX()), (ravager.getY()), (ravager.getZ()), 1);
				}
			} else {
				ravager = EntityUtils.findNearestRidable(world, x, y, z, entity, 12, OceanizedHorseEntity.class);
				if (!(ravager == null) && ravager.isAlive()) {
					if (!ravager.isVehicle()) {
						if (entity instanceof Mob _entity)
							_entity.getNavigation().moveTo((ravager.getX()), (ravager.getY()), (ravager.getZ()), 1);
					}
				}
			}
		}
		if (entity.tickCount % 20 == 10) {
			ravager = EntityUtils.findNearestRidable(world, x, y, z, entity, 3, OceanizedPolarBearEntity.class);
			if (!(ravager == null) && ravager.isAlive()) {
				if (ravager.isVehicle()) {
					return;
				}
				entity.startRiding(ravager);
			} else {
				ravager = EntityUtils.findNearestRidable(world, x, y, z, entity, 3, OceanizedHorseEntity.class);
				if (!(ravager == null) && ravager.isAlive()) {
					if (!ravager.isVehicle()) {
						entity.startRiding(ravager);
					}
				}
			}
		}
	}
}

// TODO: 调用次数 = 18，副作用密集（骑乘），保持原样不重构
