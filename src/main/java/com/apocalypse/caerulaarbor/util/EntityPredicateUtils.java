package com.apocalypse.caerulaarbor.util;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.*;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;

public class EntityPredicateUtils {

	private EntityPredicateUtils() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static boolean isAlive(Entity entity) {
		return entity != null && entity.isAlive();
	}

	public static boolean isThirsterDurative(Entity entity) {
		if (!isAlive(entity)) {
			return false;
		}
		return (entity instanceof ThirsterEntity _datEntI ? _datEntI.getEntityData().get(ThirsterEntity.DATA_DURATION) : 0) <= 0;
	}

	public static boolean isLastKnightDurative(Entity entity) {
		if (!isAlive(entity)) {
			return false;
		}
		return (entity instanceof TheLastKnightEntity _datEntI ? _datEntI.getEntityData().get(TheLastKnightEntity.DATA_duration) : 0) <= 0;
	}

	public static boolean isDurative(Entity entity) {
		if (!isAlive(entity)) {
			return false;
		}
		return (entity instanceof ComplexChitinGolemEntity _datEntI ? _datEntI.getEntityData().get(ComplexChitinGolemEntity.DATA_duration) : 0) <= 0;
	}

	public static boolean isBishopStarted(Entity entity) {
		if (!isAlive(entity)) {
			return false;
		}
		if (entity.tickCount < 80) return false;
		return (entity instanceof BishopFishEntity _datEntI ? _datEntI.getEntityData().get(BishopFishEntity.DATA_duration) : 0) <= 0;
	}

	public static boolean isCorruptedDurative(Entity entity) {
		if (!isAlive(entity)) {
			return false;
		}
		return (entity instanceof SkadiCorruptedEntity _datEntI ? _datEntI.getEntityData().get(SkadiCorruptedEntity.DATA_duration) : 0) <= 0;
	}

	public static boolean isIllusionerDurative(Entity entity) {
		if (!isAlive(entity)) {
			return false;
		}
		return (entity instanceof OceanizedIllusionerEntity _datEntI ? _datEntI.getEntityData().get(OceanizedIllusionerEntity.DATA_duration) : 0) <= 0;
	}

	public static boolean isFlamarineDurative(Entity entity) {
		if (!isAlive(entity)) {
			return false;
		}
		return (entity instanceof FlamarineGolemEntity _datEntI ? _datEntI.getEntityData().get(FlamarineGolemEntity.DATA_duration) : 0) <= 0;
	}

	public static boolean isRockSpiderDurative(Entity entity) {
		if (!isAlive(entity)) {
			return false;
		}
		return (entity instanceof TideutantRockSpiderEntity _datEntI ? _datEntI.getEntityData().get(TideutantRockSpiderEntity.DATA_duration) : 0) <= 0;
	}

	public static boolean isLastKnightStarting(Entity entity) {
		if (entity == null)
			return false;
		return entity.tickCount >= 40 && (entity instanceof LastKnightAndHorseEntity _datEntI ? _datEntI.getEntityData().get(LastKnightAndHorseEntity.DATA_SKILL_DURATION) : 0) <= 0;
	}

	public static boolean isWitherDurative(Entity entity) {
		if (!isAlive(entity)) {
			return false;
		}
		return (entity instanceof OceanizedWitherEntity _datEntI ? _datEntI.getEntityData().get(OceanizedWitherEntity.DATA_duration) : 0) <= 0
				&& (entity instanceof OceannizedWitheriaEntity _datEntI ? _datEntI.getEntityData().get(OceannizedWitheriaEntity.DATA_duration) : 0) <= 0;
	}

	public static boolean isApocataDurative(Entity entity) {
		if (!isAlive(entity)) {
			return false;
		}
		return (entity instanceof ApocataEntity _datEntI ? _datEntI.getEntityData().get(ApocataEntity.DATA_duration) : 0) <= 0;
	}

	public static boolean isXantisTapative(Entity entity) {
		if (!isAlive(entity)) {
			return false;
		}
		return (entity instanceof XantisEntity _datEntI ? _datEntI.getEntityData().get(XantisEntity.DATA_TAP_TICK) : 0) <= 0;
	}

	public static boolean isChimeraDurative(Entity entity) {
		if (!isAlive(entity)) {
			return false;
		}
		if (entity.tickCount <= 100) {
			return false;
		}
		return (entity instanceof TideChimeraEntity _datEntI ? _datEntI.getEntityData().get(TideChimeraEntity.DATA_duration) : 0) <= 0;
	}

	public static boolean isEnderinaDurative(Entity entity) {
		if (!isAlive(entity)) {
			return false;
		}
		if (entity instanceof OceanizedEnderinaEntity enderina) {
			return enderina.getEntityData().get(OceanizedEnderinaEntity.DATA_DURATION) <= 0
				&& enderina.getEntityData().get(OceanizedEnderinaEntity.DATA_REVIVE_TICK) <= 0;
		}
		return false;
	}

	public static boolean isGladiiaDurative(Entity entity) {
		if (!isAlive(entity)) {
			return false;
		}
		return (entity instanceof GladiiaEntity _datEntI ? _datEntI.getEntityData().get(GladiiaEntity.DATA_duration) : 0) <= 0;
	}

	public static boolean isCarmenDurative(Entity entity) {
		if (!isAlive(entity)) {
			return false;
		}
		return (entity instanceof SaintCarmenEntity _datEntI ? _datEntI.getEntityData().get(SaintCarmenEntity.DATA_duration) : 0) <= 0;
	}

	public static boolean isUlpuansDurative(Entity entity) {
		if (!isAlive(entity)) {
			return false;
		}
		return (entity instanceof UlpiansEntity _datEntI ? _datEntI.getEntityData().get(UlpiansEntity.DATA_duration) : 0) <= 0;
	}

	public static boolean isSpecterDurative(Entity entity) {
		if (!isAlive(entity)) {
			return false;
		}
		if (entity.tickCount <= 15) {
			return false;
		}
		return (entity instanceof SpecterEntity _datEntI ? _datEntI.getEntityData().get(SpecterEntity.DATA_duration) : 0) <= 0;
	}

	public static boolean isIreneDurative(Entity entity) {
		if (!isAlive(entity)) {
			return false;
		}
		return (entity instanceof IreneEntity _datEntI ? _datEntI.getEntityData().get(IreneEntity.DATA_duration) : 0) <= 0;
	}

	public static boolean isEndspeaker(Entity entity) {
		if (entity == null)
			return false;
		return entity instanceof EndspeakerEntity;
	}

	public static boolean isSpecterAround(LevelAccessor world, double x, double y, double z) {
		Entity g = world.getEntitiesOfClass(SpecterEntity.class, AABB.ofSize(new Vec3(x, y, z), 64, 64, 64), e -> true).stream().sorted(new Object() {
			Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
				return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
			}
		}.compareDistOf(x, y, z)).findFirst().orElse(null);
		if (!(g == null) && g.isAlive()) {
			return true;
		}
		return true;
	}

	public static boolean isValidEnemyForIrene(Entity ene, Entity entity) {
		if (ene == null || entity == null)
			return false;
		Entity enemy = entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null;
		if (!(ene instanceof LivingEntity)) {
			return false;
		}
		if (!ene.isAlive()) {
			return false;
		}
		if (ene instanceof Player || (ene instanceof TamableAnimal _tamEnt && _tamEnt.isTame())) {
			if (!(ene == enemy)) {
				return false;
			}
		}
		if (ene.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "is_humanside")))) {
			if (!(ene == enemy)) {
				return false;
			}
		}
        return ene != entity;
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
