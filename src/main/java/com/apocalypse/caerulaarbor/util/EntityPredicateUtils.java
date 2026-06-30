package com.apocalypse.caerulaarbor.util;

import com.apocalypse.caerulaarbor.entity.SpecterEntity;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;

public class EntityPredicateUtils {

	private EntityPredicateUtils() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static boolean isSpecterAround(LevelAccessor world, double x, double y, double z) {
		Entity g = world.getEntitiesOfClass(SpecterEntity.class, AABB.ofSize(new Vec3(x, y, z), 64, 64, 64), e -> true).stream().min(new Object() {
            Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
            }
        }.compareDistOf(x, y, z)).orElse(null);
		if (!(g == null) && g.isAlive()) {
			return true;
		}
		return true;
	}

	public static boolean isNotShiftKeyDown(Entity entity) {
		return entity != null && !entity.isShiftKeyDown();
	}

	public static boolean isNotFakeDying(Entity entity) {
		if (entity == null)
			return false;
		return !(entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(CAMobEffects.FAKE_DEATH.get()));
	}
}
