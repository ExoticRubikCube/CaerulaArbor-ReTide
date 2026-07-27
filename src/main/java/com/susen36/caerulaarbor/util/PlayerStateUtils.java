package com.susen36.caerulaarbor.util;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import net.minecraft.world.entity.Entity;

public class PlayerStateUtils {

	private PlayerStateUtils() {
		throw new UnsupportedOperationException("Utility class");
	}

	private static PlayerVariable getPlayerVariables(Entity entity) {
		if (entity == null)
			return new PlayerVariable();
		return ModCapabilities.getPlayerVariables(entity);
	}

	public static double getPlayerLight(Entity entity) {
		return getPlayerVariables(entity).player_light;
	}

	public static boolean isLightWaving(Entity entity) {
		double light = getPlayerLight(entity);
		return 50 <= light && light < 85;
	}

	public static boolean isLightBright(Entity entity) {
		return 85 <= getPlayerLight(entity);
	}

	public static boolean isLightCeased(Entity entity) {
		return getPlayerLight(entity) < 1;
	}

	public static boolean isLightDim(Entity entity) {
		double light = getPlayerLight(entity);
		return 1 <= light && light < 50;
	}

	public static boolean canPlayerEvo(Entity entity) {
		return getPlayerVariables(entity).can_player_evo;
	}

	public static double getSurvivor(Entity entity) {
		return getPlayerVariables(entity).relic_SURVIVOR;
	}

	public static boolean hasSurvivorCont(Entity entity) {
		return getSurvivor(entity) >= 0;
	}

	public static boolean hasAromatic(Entity entity) {
		return getPlayerVariables(entity).player_util_AROMATIC;
	}

	public static boolean isNexusRegSanitySelected(Entity entity) {
		return getPlayerVariables(entity).PEVO_NEXUS_reg_sanity;
	}

	public static boolean isNexusRegLightsSelected(Entity entity) {
		return getPlayerVariables(entity).PEVO_NEXUS_reg_lights;
	}

	public static boolean isNexusPercDamageSelected(Entity entity) {
		return getPlayerVariables(entity).PEVO_NEXUS_perc_damage;
	}

	public static boolean isNexusNoRejectionSelected(Entity entity) {
		return getPlayerVariables(entity).PEVO_NEXUS_no_rejection;
	}

	public static boolean isNexusExpoShieldSelected(Entity entity) {
		return getPlayerVariables(entity).PEVO_NEXUS_expo_shield;
	}

	public static void setEvoNode(Entity entity, String node) {
		if (entity == null)
			return;
		entity.getPersistentData().putString("showcasingEvoNode", node);
	}
}