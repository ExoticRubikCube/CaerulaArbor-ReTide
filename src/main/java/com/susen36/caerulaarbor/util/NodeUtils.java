package com.susen36.caerulaarbor.util;

import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import net.minecraft.world.entity.Entity;

public class NodeUtils {

	private NodeUtils() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static double getNodeAddDamage(Entity entity) {
		if (entity == null)
			return 0;
		return entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable()).PEVO_NODE_add_damage;
	}

	public static boolean isNodeAddDamageAtLeast(Entity entity, int value) {
		return getNodeAddDamage(entity) >= value;
	}

	public static double getNodeRealDamage(Entity entity) {
		if (entity == null)
			return 0;
		return entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable()).PEVO_NODE_real_damage;
	}

	public static boolean isNodeRealDamageAtLeast(Entity entity, int value) {
		return getNodeRealDamage(entity) >= value;
	}

	public static double getNodeAddSanity(Entity entity) {
		if (entity == null)
			return 0;
		return entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable()).PEVO_NODE_add_sanity;
	}

	public static boolean isNodeAddSanityAtLeast(Entity entity, int value) {
		return getNodeAddSanity(entity) >= value;
	}

	public static double getNodeLessDamage(Entity entity) {
		if (entity == null)
			return 0;
		return entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable()).PEVO_NODE_less_damage;
	}

	public static boolean isNodeLessDamageAtLeast(Entity entity, int value) {
		return getNodeLessDamage(entity) >= value;
	}

	public static double getNodeLessArmor(Entity entity) {
		if (entity == null)
			return 0;
		return entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable()).PEVO_NODE_less_armor;
	}

	public static boolean isNodeLessArmorAtLeast(Entity entity, int value) {
		return getNodeLessArmor(entity) >= value;
	}

	public static double getNodeHealDamage(Entity entity) {
		if (entity == null)
			return 0;
		return entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable()).PEVO_NODE_heal_damage;
	}

	public static boolean isNodeHealDamageAtLeast(Entity entity, int value) {
		return getNodeHealDamage(entity) >= value;
	}

	public static double getNodeWorseBreak(Entity entity) {
		if (entity == null)
			return 0;
		return entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable()).PEVO_NODE_worse_break;
	}

	public static boolean isNodeWorseBreakAtLeast(Entity entity, int value) {
		return getNodeWorseBreak(entity) >= value;
	}

	public static double getNodeAddSpeed(Entity entity) {
		if (entity == null)
			return 0;
		return entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable()).PEVO_NODE_add_speed;
	}

	public static boolean isNodeAddSpeedAtLeast(Entity entity, int value) {
		return getNodeAddSpeed(entity) >= value;
	}

	public static double getNodeAddResis(Entity entity) {
		if (entity == null)
			return 0;
		return entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable()).PEVO_NODE_add_resis;
	}

	public static boolean isNodeAddResisAtLeast(Entity entity, int value) {
		return getNodeAddResis(entity) >= value;
	}

	public static double getNodeAddMiss(Entity entity) {
		if (entity == null)
			return 0;
		return entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable()).PEVO_NODE_add_miss;
	}

	public static boolean isNodeAddMissAtLeast(Entity entity, int value) {
		return getNodeAddMiss(entity) >= value;
	}

	public static double getNodeAddDef(Entity entity) {
		if (entity == null)
			return 0;
		return entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable()).PEVO_NODE_add_def;
	}

	public static boolean isNodeAddDefAtLeast(Entity entity, int value) {
		return getNodeAddDef(entity) >= value;
	}

	public static double getNodeEunectes(Entity entity) {
		if (entity == null)
			return 0;
		return entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable()).PEVO_NODE_eunectes;
	}

	public static boolean isNodeEunectesAtLeast(Entity entity, int value) {
		return getNodeEunectes(entity) >= value;
	}

	public static double getNodeLivingBarrier(Entity entity) {
		if (entity == null)
			return 0;
		return entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable()).PEVO_NODE_living_barrier;
	}

	public static boolean isNodeLivingBarrierAtLeast(Entity entity, int value) {
		return getNodeLivingBarrier(entity) >= value;
	}

	public static boolean isNodeSet1Done(Entity entity) {
		if (entity == null)
			return false;
		return isNodeAddDefAtLeast(entity, 4) || isNodeAddResisAtLeast(entity, 4) || isNodeAddSpeedAtLeast(entity, 4) || isNodeAddSanityAtLeast(entity, 4);
	}

	public static boolean isNodeSet1Terminate(Entity entity) {
		if (entity == null)
			return false;
		return isNodeAddDefAtLeast(entity, 4) && isNodeAddResisAtLeast(entity, 4) && isNodeAddSpeedAtLeast(entity, 4) && isNodeAddSanityAtLeast(entity, 4);
	}

	public static boolean isNodeSet2Done(Entity entity) {
		if (entity == null)
			return false;
		return isNodeAddDamageAtLeast(entity, 4) || isNodeLessDamageAtLeast(entity, 4) || isNodeLivingBarrierAtLeast(entity, 4) || isNodeAddMissAtLeast(entity, 4);
	}

	public static boolean isNodeSet2Terminate(Entity entity) {
		if (entity == null)
			return false;
		return isNodeAddDamageAtLeast(entity, 4) && isNodeLessDamageAtLeast(entity, 4) && isNodeLivingBarrierAtLeast(entity, 4) && isNodeAddMissAtLeast(entity, 4);
	}

	public static boolean isNodeSet3Done(Entity entity) {
		if (entity == null)
			return false;
		return isNodeRealDamageAtLeast(entity, 4) || isNodeHealDamageAtLeast(entity, 4) || isNodeWorseBreakAtLeast(entity, 4);
	}

	public static boolean isNodeSet3Terminate(Entity entity) {
		if (entity == null)
			return false;
		return isNodeRealDamageAtLeast(entity, 4) && isNodeHealDamageAtLeast(entity, 4) && isNodeWorseBreakAtLeast(entity, 4);
	}

	public static boolean isNodeSet4Done(Entity entity) {
		if (entity == null)
			return false;
		return isNodeEunectesAtLeast(entity, 4) || isNodeLessArmorAtLeast(entity, 4);
	}
}
